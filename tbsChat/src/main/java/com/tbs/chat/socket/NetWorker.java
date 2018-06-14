package com.tbs.chat.socket;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.database.table.FRIENDS_TABLE;
import com.tbs.chat.database.table.USER_TABLE;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.entity.UserEntity;
import com.tbs.chat.entity.ebs.UserEbsEntity;
import com.tbs.chat.ui.myinterface.ReceiveInfoListener;
import com.tbs.chat.util.FileUtil;
import com.tbs.chat.util.LogUtil;
import com.tbs.chat.util.Util;
import com.tbs.ini.IniFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

public class NetWorker extends Thread {

	private final String TAG = "NetWorker";

	private final int BUFFER_SIZE = 1024;
	private final byte connect = 1;
	private final byte running = 2;

	private byte state = connect; // 状态（默认为连接状态）
	private boolean onWork = true;
	private boolean onSocket = false;

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private Context mContext;
	private String IP = null;
	private String PORT = null;
	private DBUtil dao = null;

	Vector<ReceiveInfoListener> listeners = new Vector<ReceiveInfoListener>();

	public NetWorker() {

	}

	public NetWorker(Context context) {
		mContext = context;
		dao = DBUtil.getInstance(context);
	}

	@Override
	public void run() {
		try {
			if (socket != null) {
				socket.close();
			}
			if (dis != null) {
				dis.close();
			}
			if (dos != null) {
				dos.close();
			}
			onWork = true;
			onSocket = false;
			state = connect;

		} catch (IOException e) {
			e.printStackTrace();
		}
		while (onWork) {
			switch (state) {
			case connect:// 链接状态
				connect();
				break;

			case running:// 链接状态
				receiveMsg();
				break;
			}
		}

	}

	/**
	 * 链接服务器端
	 */
	private synchronized void connect() {
		Log.d(TAG, "****************** start connect *****************");
		boolean flag = false;
		Intent intent = new Intent(Constants.SOCKET_CONNECTION);
		PORT = "9999";
		IP = getPropertyItem(Constants.URL);
		try {
			socket = new Socket();
			SocketAddress socAddress = new InetSocketAddress(IP,
					Integer.valueOf(PORT));
			socket.connect(socAddress, 9000);
			if (socket.isConnected()) {
				dis = new DataInputStream(new BufferedInputStream(
						socket.getInputStream()));
				dos = new DataOutputStream(new BufferedOutputStream(
						socket.getOutputStream()));
				state = running;// 将状态重置为运行状态
				onSocket = true;// socket状态为链接
				flag = true;
			} else {
				flag = false;
			}
			Log.d(TAG, "******************connect is "+ flag +"*****************");

			intent.putExtra("isConnect", flag);
			mContext.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "connect is error");
			Intent START_TIMER = new Intent(Constants.START_TIMER);
			mContext.sendBroadcast(START_TIMER);
			onWork = false;
		}
	}

	/**
	 * 接收消息
	 */
	public synchronized void receiveMsg() {
		try {
			int type = dis.readInt();
            Log.e(TAG, "receiveMsg type="+ type);
			switch (type) {
			case Config.RESULT_LOGIN:// 接收到登陆验证结果
				handLogin();
				break;

			case Config.RESULT_REPEAT_LOGIN:// 接收到重复登陆结果
				handRepeatLogin();
				break;

			case Config.RESULT_VERIFY_PHONE:// 接收手机验证码
				handVerifyPhone();
				break;

			case Config.RESULT_REGIST:// 接收到注册结果
				handRegist();
				break;

			case Config.RECEIVE_TEXT:// 接收文本信息
				handReceiveText();
				break;

			case Config.RECEIVE_IMG:// 接收图片信息
				handReceiveData(Config.MESSAGE_TYPE_IMG);
				break;

			case Config.RECEIVE_AUDIO:// 接收音频信息
				handReceiveData(Config.MESSAGE_TYPE_AUDIO);
				break;

			case Config.RESULT_SEARCH_USER:// 接收查询用户请求
                Log.e(TAG, "searchUser result");
                handSearchUser();
				break;

			case Config.ADD_FRIEND:// 添加好友请求
				handAddFriend();
				break;

			case Config.RESULT_GET_OFFLINE_MSG:// 接收离线信息请求
				handGetOfflineMsg();
				break;

			case Config.RESULT_GET_HEAD:// 接收获得头像请求
				handGetHead();
				break;

			case Config.RESULT_GET_FRIENDS:// 接收获得好友列表请求
				System.out.println(Config.RESULT_GET_FRIENDS);
				handGetFriends();
				break;

			case Config.RESULT_EXIT_USER:// 退出用户
				handExitUser();
				break;

			case Config.RESULT_GET_VERSION:// 判断版本
				handGetVersion();
				break;

			case Config.RESULT_GET_DOWNLOAD:// 下载更新
				handGetDownloadApk();
				break;

			case Config.RESULT_GET_FRIENDS_INFO:// 获得好友信息
				handGetDownloadApk();
				break;

			case Config.RESULT_SETTING_USERNUM:// 设计ims账号
				handSettingUserNum();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();

			Log.d(TAG, "receiveMsg is error");

			Intent START_TIMER = new Intent(Constants.START_TIMER);
			START_TIMER.addCategory(Constants.SEARCH_FRIEND_RESOULT);
			mContext.sendBroadcast(START_TIMER);

			onWork = false;
		}
	}

	/**
	 * 设置工作状态
	 * 
	 * @param onWork
	 */
	public void setOnWork(boolean onWork) {
		this.onWork = onWork;
	}

	public boolean isOnSocket() {
		return onSocket;
	}

	/**
	 * 重新连接
	 */
	public void reconnect() {
		notify();
	}

	/**
	 *  现阶段只有ChatActivity注册了ReceiveInfoListener
	 *  所以listeners里只有一个ReceiveInfoListener
	 */
	public boolean receive(MessageEntity message) {
		ReceiveInfoListener listener = listeners.get(0);
		boolean result = listener.receive(message);
		return result;
	}

	/**
	 * 以下方法是客户端向服务器发送登录请求的方法
	 */
	public boolean login(String userId, String pwd) {
		boolean flag = true;
		LogUtil.record("-----------------------------------------------------------");
		LogUtil.record("****************** start login *****************");
		try {
			dos.writeInt(Config.REQUEST_LOGIN);
			LogUtil.record("****************** login REQUEST_LOGIN:"
					+ Config.REQUEST_LOGIN + " *****************");
			dos.writeInt(0);// 账号密码登录
			dos.writeUTF(userId);
			LogUtil.record("****************** login userId:" + userId
					+ " *****************");
			dos.writeUTF(pwd);
			LogUtil.record("****************** login pwd:" + pwd
					+ " *****************");
			dos.flush();
			LogUtil.record("****************** login finish *****************");
		} catch (Exception e) {
			System.out.println(e);
			flag = false;
		} finally {
			return flag;
		}
	}

	/**
	 * 以下方法是客户端向服务器发送登录请求的方法
	 */
	public boolean loginPhone(String phone) {
		boolean flag = true;
		Log.d(TAG, "--------------------------------------------------------");
		LogUtil.record("****************** start loginPhone *****************");
		try {
			dos.writeInt(Config.REQUEST_LOGIN);
			LogUtil.record("****************** loginPhone REQUEST_LOGIN:"
					+ Config.REQUEST_LOGIN + " *****************");
			dos.writeInt(1);// 手机号登录
			Log.d(TAG, "phone:" + phone);
			dos.writeUTF(phone);
			LogUtil.record("****************** loginPhone phone:" + phone
					+ " *****************");
			dos.flush();
			LogUtil.record("****************** loginPhone finish *****************");
			Log.d(TAG,
					"--------------------------------------------------------");
		} catch (Exception e) {
			flag = false;
		} finally {
			return flag;
		}
	}

	/**
	 * 以下方法是客户端接收服务器响应的方法 
	 * 如果发送内容成功返回true
	 * 如果发送内容失败返回false
	 * 如果报错也返回登录失败
	 */
	public void handLogin() {
		Intent intent = null;
		boolean isLogin = false;
		int loginResoult = 0;
		try {
			LogUtil.record("-----------------------------------------------------------");
			isLogin = dis.readBoolean();
			if (isLogin) { // 登录成功
				LogUtil.record("****************** Login is success *****************");
				String loginID = dis.readUTF();
				String userId = dis.readUTF();
				String country_code = dis.readUTF();
				String nick_Name = dis.readUTF();
				String password = dis.readUTF();
				String phone = dis.readUTF();
				String province = dis.readUTF();
                String city = dis.readUTF();
				String userNum = dis.readUTF();
				int isExistImg = dis.readInt();
				//System.out.println("创建用户的文件夹");
				// 创建用户的文件夹
				FileUtil.createFolder(userId);

				LogUtil.record("****************** isExistImg :" + isExistImg
						+ " *****************");
				intent = new Intent(Constants.LOGIN_RESOULT);
				intent.putExtra("self_id", userId);
				intent.putExtra("country_code", country_code);
				intent.putExtra("nick_Name", nick_Name);
				intent.putExtra("phone", phone);
				intent.putExtra("province", province);
                intent.putExtra("city", city);
                intent.putExtra("userNum", userNum);
				intent.putExtra("loginID", loginID);
				if (isExistImg == Config.USER_HAS_IMG) {
					// 接收传过来的时间戳
					String lastModifyTime = dis.readUTF();
					// 查询用户信息
					UserEntity user = dao.queryUser(mContext,
							USER_TABLE.TABLE_NAME, userId);
					if (user != null) { // 本地数据库中有这个记录�
						String modifyTime = user.getModifyTime();
						if (lastModifyTime.equals(modifyTime)) {
							// 两个时间戳相同，表明头像没有被更新过,使用本地头像
							intent.putExtra("head", user.getHead());
							intent.putExtra("modify_time",
									String.valueOf(lastModifyTime));
							//向服务器发出获取好友信息列表请求
							getFriends(mContext, userId);
						} else {
							// 两个时间戳不同，表明头像已被更新,使用服务器端的头像,发送请求，更新头像图片
							getUserHead(userId);
						}
					} else {
						getUserHead(userId);
					}
				} else if (isExistImg == Config.USER_NOT_IMG) {
					intent.putExtra("head", "");
					// 向服务器发出获取好友信息列表请求
					Log.d(TAG, "handGetHead userId:" + userId);
					getFriends(mContext, userId);
				}
				// 设置登录结果为成功
				loginResoult = Config.RESULT_LOGIN_SUCCESS;
			} else {
				// 设置登录结果为失败
				loginResoult = Config.RESULT_LOGIN_UNSUCCESS;
				LogUtil.record("****************** Login is unsuccess *****************");

			}
		} catch (Exception e) {
			isLogin = false;
			loginResoult = Config.RESULT_LOGIN_ERRO;
			LogUtil.record("****************** Login erro:" + e.getMessage()
					+ " *****************");
		} finally {
			if (isLogin) {
				sendBCast("isLogin", loginResoult, intent);
			} else {
				sendBCast("isLogin", loginResoult, Constants.LOGIN_RESOULT);
			}
			Constants.isLogin = isLogin;
			LogUtil.record("-----------------------------------------------------------");
		}
	}

	/**
	 * 以下方法是客户端接收服务器响应的方法 
	 * 如果发送内容成功返回true
	 * 如果发送内容失败返回false
	 * 如果报错也返回登录失败
	 */
	public void handRepeatLogin() {
		LogUtil.record("****************** Login is success *****************");
		try {
			String content = dis.readUTF();
			Log.d(TAG, "handRepeatLogin content:" + content);
			Intent intent = new Intent(Constants.LOGIN_REPEAT_RESOULT);
			intent.putExtra("content", content);
			mContext.sendBroadcast(intent);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void verifyPhone(String phone) {
		LogUtil.record("****************** start verifyPhone *****************");
		try {
			dos.writeInt(Config.REQUEST_VERIFY_PHONE);
			LogUtil.record("****************** regist REQUEST_REGIST:"
					+ Config.REQUEST_VERIFY_PHONE + " *****************");
			dos.writeUTF(phone);
			LogUtil.record("****************** regist phone:" + phone
					+ " *****************");
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void verifyNumber(String number, String phone) {
		LogUtil.record("****************** start verifyPhone *****************");
		try {
			dos.writeInt(Config.REQUEST_VERIFY_NUMBER);
			LogUtil.record("****************** regist verifyNumber:"
					+ Config.REQUEST_VERIFY_NUMBER + " *****************");
			dos.writeUTF(number);
			dos.writeUTF(phone);
			LogUtil.record("****************** regist number:" + number
					+ " *****************");
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获得验证手机号
	private void handVerifyPhone() {
		Intent intent = null;
		Log.d(TAG, "--------------------------------------------------------");
		try {
			int verifyCode = dis.readInt();
			Log.d(TAG, "--------------------------verifyCode:" + verifyCode
					+ "------------------------------");
			intent = new Intent(Constants.VERIFY_PHONE);
			intent.putExtra("verifyCode", verifyCode);
			mContext.sendBroadcast(intent);
			Log.d(TAG,
					"--------------------------------------------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 在注册的时候，并不发送头像图片的时间戳，而是有服务器端存储图片后，再把存储的图片的时间戳发回给客户端
	 * String userName,
	 */
	public boolean regist(String psw, String nick, String phone,
			String countryCode, String headPath) {
		LogUtil.record("****************** start regist *****************");
		try {
			dos.writeInt(Config.REQUEST_REGIST);
			LogUtil.record("****************** regist REQUEST_REGIST:"
					+ Config.REQUEST_REGIST + " *****************");
			// dos.writeUTF(userName);
			// LogUtil.record("****************** regist userName:"+userName+" *****************");
			dos.writeUTF(psw);
			LogUtil.record("****************** regist psw:" + psw
					+ " *****************");
			dos.writeUTF(nick);
			LogUtil.record("****************** regist nick:" + nick
					+ " *****************");
			dos.writeUTF(phone);
			LogUtil.record("****************** regist phone:" + phone
					+ " *****************");
			dos.writeUTF(countryCode);
			LogUtil.record("****************** regist countryCode:"
					+ countryCode + " *****************");
			if (headPath != null && headPath.length() > 0) {
				LogUtil.record("****************** regist headPath:true *****************");
				dos.writeBoolean(true); // 有头像
				LogUtil.record("****************** regist filePath:" + headPath
						+ " *****************");
				readFileSendData(headPath);
			} else {
				dos.writeBoolean(false); // 无头像
			}
			dos.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// 获得服务器的注册结果
	private void handRegist() {
		Intent intent = null;
		Log.d(TAG, "--------------------------------------------------------");
		try {
			boolean registResult = dis.readBoolean();
			if (registResult) {
				String userId = dis.readUTF();
				String lastModifyTime = dis.readUTF();
				FileUtil.createFolder(userId);// 创建用户的文件夹
				intent = new Intent(Constants.REG_RESOULT);// 创建intent
				intent.putExtra("userId", userId);
				intent.putExtra("lastModifyTime", lastModifyTime);
				intent.putExtra("state", Config.RIGEST_SUCCESS);
			} else {
				intent = new Intent(Constants.REG_RESOULT);
				intent.putExtra("state", Config.RIGEST_FAILED);
			}
			mContext.sendBroadcast(intent);
			Log.d(TAG,
					"--------------------------------------------------------");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取好友列表
	 */
	public void getUserHead(String userId) {
		try {
			// 两个时间戳不同，表明头像已被更新,使用服务器端的头像,发送请求，更新头像图片
			dos.writeInt(Config.REQUEST_GET_HEAD);
			dos.writeUTF(userId);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取好友列表
	 */
	public void getFriends(Context context, String selfId) {
        Log.d(TAG,"****************** start getFriends *****************");
		LogUtil.record("****************** start request *****************");
		try {
			// 获取本地数据库中的好友列表
			ArrayList<FriendEntity> list = Util.queryFriends(context, selfId);
			// 将好友的头像时间戳发到服务器
			dos.writeInt(Config.REQUEST_GET_FRIENDS);
			LogUtil.record("****************** request REQUEST_GET_FRIENDS:"
					+ Config.REQUEST_GET_FRIENDS + " *****************");
			dos.writeUTF(selfId);
			LogUtil.record("****************** request selfId:" + selfId
					+ " *****************");
			int size = list.size();
			// 先把好友的数量发送到服务器
			// System.out.println("size=" + size);
			dos.writeInt(size);
			LogUtil.record("****************** request size:" + size
					+ " *****************");
			// dos.flush();
			for (int i = 0; i < size; i++) {
				String friendID = String.valueOf(list.get(i).getFriendID());
				String modifyTime = list.get(i).getModifyTime();
				dos.writeUTF(friendID);
				LogUtil.record("****************** request Friends friendID:"
						+ friendID + " ");
				dos.writeUTF(modifyTime);
				LogUtil.record("****************** request modifyTime:"
						+ modifyTime + " *****************");
			}
			dos.flush();
			LogUtil.record("****************** request finish *****************");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 先把好友的数量发送到服务器
	 */
	public void getFriendsInfo(Context context, String friendID) {
		LogUtil.record("****************** start request *****************");
		try {
			// 将好友的头像时间戳发到服务器
			dos.writeInt(Config.REQUEST_GET_FRIENDS_INFO);
			LogUtil.record("****************** request REQUEST_GET_FRIENDS:"
					+ Config.REQUEST_GET_FRIENDS + " *****************");
			dos.writeUTF(friendID);
			LogUtil.record("****************** request selfId:" + friendID
					+ " *****************");
			dos.flush();
			LogUtil.record("****************** request finish *****************");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取软件版和更新内容响应
	 */
	public void handGetFriendInfo() {
		LogUtil.record("****************** start request *****************");
		try {
			String friendNike = dis.readUTF();
			LogUtil.record("****************** request friendNike:"
					+ friendNike + " *****************");
			Intent intent = new Intent(Constants.UPDATE_FRIENDNIKE);
			intent.putExtra("friendNike", friendNike);
			mContext.sendBroadcast(intent);
			LogUtil.record("****************** request finish *****************");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取好友列表
	 */
	public void handGetFriends() {
        Log.d(TAG,"****************** start handGetFriends *****************");
		LogUtil.record("****************** start handGetFriends *****************");
		try {
			int size = dis.readInt();
			LogUtil.record("****************** handGetFriends size:" + size
					+ " *****************");
			// 使用从服务器发送来的好友信息更新本地数据库中取出好友信息
			for (int i = 0; i < size; i++) {
				int cmd = dis.readInt();
				//System.out.println("cmd=" + cmd);
				// 用户设置了头像,需要更新头像为最新的和个人信息
				if (cmd == Config.IMG_NEED_UPDATE) {

					String userId = dis.readUTF();
					LogUtil.record("****************** handGetFriends userId:"
							+ userId + " *****************");
					String friendId = dis.readUTF();
					LogUtil.record("****************** handGetFriends friendId:"
							+ friendId + " *****************");
					String nickName = dis.readUTF();
					LogUtil.record("****************** handGetFriends nickName:"
							+ nickName + " *****************");
					String sex = dis.readUTF();
					LogUtil.record("****************** handGetFriends sex:"
							+ sex + " *****************");
					String phone = dis.readUTF();
					LogUtil.record("****************** handGetFriends phone:"
							+ phone + " *****************");
					String province = dis.readUTF();
                    String city = dis.readUTF();
					LogUtil.record("****************** handGetFriends dir:"
							+ province + " *****************");
					String countryCode = dis.readUTF();
					LogUtil.record("****************** handGetFriends countryCode:"
							+ countryCode + " *****************");
					String modifyTime = dis.readUTF();
					LogUtil.record("****************** handGetFriends modifyTime:"
							+ modifyTime + " *****************");
					// 使用好友id创建头像名称
					File file = FileUtil.createHeadFile(friendId);
					LogUtil.record("****************** handGetFriends file absolutepath:"
							+ file.getAbsolutePath() + " *****************");
					// 将文件写入到sd卡内
					// receiveDataWriteFile(file.getAbsolutePath());
					// 设置文件的左后修改时间
					//file.setLastModified(Long.parseLong(modifyTime));

					FriendEntity friend = new FriendEntity();
					friend.setUserID(userId);
					friend.setFriendID(friendId);
					friend.setNickName(nickName);
					friend.setSex(sex);
					friend.setPhone(phone);
					friend.setData2(province);
					friend.setCountryCode(countryCode);
					friend.setHead("");
					friend.setModifyTime(modifyTime);

					// 更新数据库信息
					dao.updateFirendInfo(friend);

					// 发送广播更新好友列表
					Intent intent = new Intent(Constants.REFRESH_ADDRESS);
					mContext.sendBroadcast(intent);

				} else if (cmd == Config.IMG_NO_UPDATE) { // 头像已是最新的了,不需要更新头像

					String userId = dis.readUTF();
					String friendId = dis.readUTF();
					String nickName = dis.readUTF();
					String sex = dis.readUTF();
					String phone = dis.readUTF();
					String province = dis.readUTF();
                    String city = dis.readUTF();
					String countryCode = dis.readUTF();
					FriendEntity friend = new FriendEntity();
					friend.setUserID(userId);
					friend.setFriendID(friendId);
					friend.setNickName(nickName);
					friend.setSex(sex);
					friend.setPhone(phone);
					friend.setData2(province);
					friend.setCountryCode(countryCode);

					// 更新数据库信息
					dao.updateFirendInfo(friend);

					// 发送广播更新好友列表
					Intent intent = new Intent(Constants.REFRESH_ADDRESS);
					mContext.sendBroadcast(intent);

				} else if (cmd == Config.ADD_FRIEND) {
					handAddFriend();// 添加好友
				}
				LogUtil.record("****************** handGetFriends finish *****************");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//发送请求离线消息广播
		Intent intent1 = new Intent(Constants.REQUEST_OFF_MESSAGE);
		mContext.sendBroadcast(intent1);

	}

	/**
	 * 服务器向客户端发来建立好友对的命令
	 */
	public void handAddFriend() {
		LogUtil.record("--------------------- handAddFriend start ----------------------");
		try {

			String userId = dis.readUTF();
			String friendId = dis.readUTF();
			String nickName = dis.readUTF();
			String sex = dis.readUTF();
			String phone = dis.readUTF();
			String province = dis.readUTF();
            String city = dis.readUTF();
			String countryCode = dis.readUTF();
			int type = dis.readInt();
			LogUtil.record("--------------------- handAddFriend type:" + type
					+ " ----------------------");
			String modifyTime = dis.readUTF();
			String headPath = "";
			if (type == Config.USER_HAS_IMG) { // 有头像,读取头像数据在本地建立头像文件
				File file = FileUtil.createHeadFile(friendId);
				headPath = file.getAbsolutePath();
				receiveDataWriteFile(headPath);
				file.setLastModified(Long.parseLong(modifyTime));
			}
			// 向数据库中插入好友关系.
			ContentValues values = new ContentValues();
			values.put(FRIENDS_TABLE.SELF_ID, userId);
			values.put(FRIENDS_TABLE.FRIEND_ID, friendId);
			values.put(FRIENDS_TABLE.NICK, nickName);
			values.put(FRIENDS_TABLE.SEX, sex);
			values.put(FRIENDS_TABLE.HEAD, headPath);
			values.put(FRIENDS_TABLE.COUNTRY_CODE, countryCode);
			values.put(FRIENDS_TABLE.PHONE, phone);
			values.put(FRIENDS_TABLE.MODIFY_TIME, modifyTime);
			values.put(FRIENDS_TABLE.DATA2, province);
			dao.insertFriend(mContext, values);
			LogUtil.record("--------------------- handAddFriend insertFriend finish ----------------------");
			// 发送广播更新好友列表
			Intent intent = new Intent(Constants.REFRESH_ADDRESS);
			mContext.sendBroadcast(intent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		LogUtil.record("--------------------- handAddFriend finish ----------------------");
	}

	/**
	 *  获取暂存在服务器端的离线消息
	 */
	public void getOfflineMessage(String userID) {
		try {
			dos.writeInt(Config.REQUEST_GET_OFFLINE_MSG);
			dos.writeUTF(String.valueOf(userID));
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

/**
	 * 获得离线信息
	 * 获得离线后发送广播
	 * 广播段处理信息入库和显示
	 */
	public void handGetOfflineMsg() {
        Log.d(TAG,"****************** start handGetOfflineMsg *****************");
		try {
			LogUtil.record("****************** start handGetOfflineMsg *****************");
			int listSize = dis.readInt();
			for (int i = 0; i < listSize; i++) {
				String audioTime = null;
				String content = null;
				String friendId = dis.readUTF();
				LogUtil.record("****************** handGetOfflineMsg friendId:"
						+ friendId + " *****************");
				String selfId = dis.readUTF();
				LogUtil.record("****************** handGetOfflineMsg selfId:"
						+ selfId + " *****************");
				int type = dis.readInt();
				LogUtil.record("****************** handGetOfflineMsg type:"
						+ type + " *****************");
				String time = dis.readUTF();
				LogUtil.record("****************** handGetOfflineMsg time:"
						+ time + " *****************");
				if (type == Config.MESSAGE_TYPE_TXT) {
					content = dis.readUTF();
				} else if (type == Config.MESSAGE_TYPE_ADD_FRIEND) {// 向服务器发出查询详细信息的请求
					dos.writeInt(Config.REQUEST_GET_USER);
					dos.writeUTF(selfId);
					dos.flush();
				} else {
					if (type == Config.MESSAGE_TYPE_AUDIO) {
						audioTime = dis.readUTF();
					}
					// 创建文件
					File file = FileUtil.createFile(selfId, type);
					// 获取文件绝对路径
					content = file.getAbsolutePath();
					// 写入文件
					receiveDataWriteFile(content);
				}

				// 向数据库插入数据
				ContentValues values = new ContentValues();
				values.put("self_id", selfId);
				values.put("friend_id", friendId);
				values.put("direction", Config.MESSAGE_FROM);
				values.put("type", type);
				values.put("content", content);
				values.put("time", time);
				values.put("read_type", 0);
				if (audioTime != null && !audioTime.equals("")) {
					values.put("data5", audioTime);
				}

				dao.insertMessage(mContext, values);

				// 发送刷新界面的广播
				Intent intent = new Intent(Constants.REFRESH_MESSAGE);
				intent.putExtra("friendID", friendId);
				intent.putExtra("direction", Config.MESSAGE_FROM);
				mContext.sendBroadcast(intent);
			}
			LogUtil.record("****************** handGetOfflineMsg finish *****************");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理文本消息
	 */
	private void handReceiveText() {
		try {
			// 消息的发送者，在这里强调他是消息的另一方
			String friend = dis.readUTF();
			// 别人发给自己的消息，自己是该消息的接收者
			String self = dis.readUTF();
			String time = dis.readUTF();
			String content = dis.readUTF();
			int direction = Config.MESSAGE_FROM;
			int type = Config.MESSAGE_TYPE_TXT;
			int read_type = 0;
			// 实例化实体类
			MessageEntity chatMessage = new MessageEntity(self, friend,
					direction, type, time, content, read_type, "", "", "", "",
					"");
			// 向数据库插入数据
			ContentValues values = new ContentValues();
			values.put("self_id", self);
			values.put("friend_id", friend);
			values.put("direction", direction);
			values.put("type", type);
			values.put("content", content);
			values.put("time", time);
			values.put("read_type", read_type);
			dao.insertMessage(mContext, values);
			// 发送广播，传输参数,刷新信息
			Bundle bundle = new Bundle();
			bundle.putSerializable("chatMessage", chatMessage);

			Intent message = new Intent(Constants.REFRESH_MESSAGE);
			message.putExtra("friendID", friend);
			message.putExtra("direction", Config.MESSAGE_FROM);
			message.putExtras(bundle);
			mContext.sendBroadcast(message);

			// 刷新消息细览
			Intent message_detail = new Intent(Constants.REFRESH_MESSAGE_DETAIL);
			message_detail.putExtras(bundle);
			mContext.sendBroadcast(message_detail);
		} catch (Exception e) {
			Log.e(TAG, "handleTxt exception:" + e.getMessage());
		}
	}

	/**
	 * 处理语音、图片消息
	 */
	private void handReceiveData(int type) {
		LogUtil.record("****************** start request *****************");
		LogUtil.record("****************** handReceiveData type:" + type
				+ " *****************");
		if (type == Config.MESSAGE_TYPE_IMG) {
			Log.d(TAG, "------开始接收图片消息------");
		} else if (type == Config.MESSAGE_TYPE_AUDIO) {
			Log.d(TAG, "------开始接收语音消息------");
		}
		File file = null;
		try {
			// 好友ID
			String friend = dis.readUTF();
			// 自己的ID
			String self = dis.readUTF();
			// 发送的时间
			String time = dis.readUTF();
			// 发送类型
			int direction = Config.MESSAGE_FROM;
			// 是否读取过
			int read_type = 0;
			// 音频持续时间
			String audioTime = null;
			if (type == Config.MESSAGE_TYPE_AUDIO) {
				audioTime = dis.readUTF();
				Log.d(TAG, "handReceiveData audioTime:" + audioTime);
			}
			// 创建图片
			file = FileUtil.createFile(self, type);
			// 获得绝对路径
			String filePath = file.getAbsolutePath();
			LogUtil.record("****************** handReceiveData filePath:"
					+ filePath + " *****************");
			// 读取流，写入文件
			receiveDataWriteFile(filePath);
			// 向数据库插入数据
			Log.d(TAG, "向数据库插入数据");
			ContentValues values = new ContentValues();
			values.put("self_id", self);
			values.put("friend_id", friend);
			values.put("direction", direction);
			values.put("type", type);
			values.put("content", filePath);
			values.put("time", time);
			values.put("read_type", read_type);
			if (audioTime != null && !audioTime.equals("")) {
				values.put("data5", audioTime);
			}
			long id = dao.insertMessage(mContext, values);
			LogUtil.record("****************** handReceiveData insertMessage id:"
					+ id + " *****************");
			// 创建实体类，向实体内添加数据
			Log.d(TAG, "创建实体类");
			MessageEntity chatMessage = new MessageEntity(self, friend,
					Config.MESSAGE_FROM, type, time, filePath, 0, "", "", "",
					"", audioTime);
			Bundle bundle = new Bundle();
			bundle.putSerializable("chatMessage", chatMessage);
			// 发送广播,刷新短信概览界面
			Log.d(TAG, "发送广播");
			Intent message = new Intent(Constants.REFRESH_MESSAGE);
			message.putExtra("friendID", friend);
			message.putExtra("direction", Config.MESSAGE_FROM);
			message.putExtras(bundle);
			mContext.sendBroadcast(message);
			// 发送广播,刷新短信细览界面
			Intent message_detail = new Intent(Constants.REFRESH_MESSAGE_DETAIL);
			message_detail.putExtras(bundle);
			mContext.sendBroadcast(message_detail);
		} catch (Exception e) {
			Log.e(TAG, "handReceiveData exception:" + e.getMessage());
			LogUtil.record("****************** handReceiveData exception:"
					+ e.getMessage() + " *****************");
		} finally {
			if (file != null) {
				file = null;
			}
			LogUtil.record("****************** end request *****************");
		}
	}

	// 发送文本消息
	public boolean sendText(String self, String receiver, String time,
			String content) {
		boolean result = true;
		try {
			Log.d(TAG, "sendText 1");
			dos.writeInt(Config.REQUEST_SEND_TXT);
			Log.d(TAG, "sendText 2");
			dos.writeUTF(self);
			Log.d(TAG, "sendText 3");
			dos.writeUTF(receiver);
			Log.d(TAG, "sendText 4");
			dos.writeUTF(time);
			Log.d(TAG, "sendText 5");
			dos.writeUTF(content);
			Log.d(TAG, "sendText 6");
			dos.flush();
			Log.d(TAG, "sendText 7");
			result = true;
		} catch (Exception e) {
			Log.e(TAG, "sendText exception:" + e.getMessage());
			result = false;
		}
		return result;
	}

	/**
	 * 发送语音消息
	 * 
	 * @param self
	 * @param receiver
	 * @param time
	 * @param filePath
	 * @param audioTime
	 * @return
	 */
	public boolean sendAudio(String self, String receiver, String time,
			String filePath, String audioTime) {
		boolean result = false;
		try {
			dos.writeInt(Config.REQUEST_SEND_AUDIO);
			dos.writeUTF(self);
			dos.writeUTF(receiver);
			dos.writeUTF(time);
			dos.writeUTF(audioTime);
			// 根据content指定的位置，获取语音文件的字节数组
			readFileSendData(filePath);
			result = true;
		} catch (IOException e) {
			Log.e(TAG, "sendAudio exception:" + e.getMessage());
			result = false;
		}
		return result;
	}

	/**
	 * 发送图片消息
	 * 
	 * @param self
	 * @param receiver
	 * @param time
	 * @param filePath
	 * @return
	 */
	public boolean sendImg(String self, String receiver, String time,
			String filePath) {
		boolean result = true;
		try {
			dos.writeInt(Config.REQUEST_SEND_IMG);
			dos.writeUTF(self);
			dos.writeUTF(receiver);
			dos.writeUTF(time);
			dos.flush();
			readFileSendData(filePath);
			result = true;
		} catch (IOException e) {
			Log.e(TAG, "sendImg exception:" + e.getMessage());
			result = false;
		}
		return result;
	}

	/**
	 * 查找好友
	 */
	public void searchUser(String value) {
        Log.e(TAG, "start  searchUser");
		try {

			dos.writeInt(Config.REQUEST_SEARCH_USER);
			dos.writeUTF(value);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
            Log.e(TAG, "searchUser error:"+e.getMessage());
		}
        Log.e(TAG, "searchUser stop");
	}

	/**
	 * 查找好友
	 * 线程中循环执行方法接收内容
	 */
	public void handSearchUser() {
		LogUtil.record("---------------------handSearchUser start----------------------");
		Bundle bundle = null;
		Intent intent = null;
		try {
			int result = dis.readInt();
			intent = new Intent(Constants.SEARCH_FRIEND_RESOULT);
            LogUtil.record("---------------------handSearchUser result="+result+"----------------------");
			if (result == Config.SEARCH_USER_SUCCESS) {
				FriendEntity friend = new FriendEntity();// 实例化好友实体
				String friendId = dis.readUTF();// 获取好友ID
				String nickName = dis.readUTF();// 获取好友昵称
				String sex = dis.readUTF();// 获取好友性别
				String phone = dis.readUTF();// 获取好友电话
				String province = dis.readUTF();
                String city = dis.readUTF();
				String countryCode = dis.readUTF();// 获取好友国家代码
				String modifyTime = dis.readUTF();// 获取好友时间戳

				friend.setUserID(Constants.userEbs.getUserId());// 存储查询人ID
				friend.setFriendID(friendId);// 存储好友ID
				friend.setNickName(nickName);// 存储好友昵称
				friend.setSex(sex);// 存储好友性别
				friend.setPhone(phone);// 存储好友电话
				friend.setData2(province);// 存储好友地区
				friend.setCountryCode(countryCode);// 存储好友国家代码
				friend.setModifyTime(modifyTime);// 存储好友时间戳

				int type = dis.readInt();// 获得类型
				LogUtil.record("---------------------handSearchUser type:"
						+ type + "----------------------");
				if (type == Config.USER_HAS_IMG) { // 有头像
					File file = FileUtil.createHeadFile(""
							+ friend.getFriendID());// 创建文件
					String headPath = file.getAbsolutePath();// 获得头像的绝对路径
					LogUtil.record("---------------------handSearchUser headPath:"
							+ headPath + "----------------------");
					// receiveDataWriteFile(headPath);// 写入头像
					friend.setHead("");// 传送头像路径
				} else {
					friend.setHead(""); //  无头像，是设为null还是设为“”
				}

				bundle = new Bundle();// 存储好友内容到bundle
				bundle.putInt("state", Config.SEARCH_USER_SUCCESS);
				bundle.putSerializable("friend", friend);
			} else {

				bundle = new Bundle();// 存储好友内容到bundle
				bundle.putInt("state", Config.SEARCH_USER_FALSE);
			}
			intent.putExtras(bundle);
			mContext.sendBroadcast(intent);
		} catch (IOException e) {
			e.printStackTrace();
            intent = new Intent(Constants.SEARCH_FRIEND_RESOULT);
            LogUtil.record("---------------------handSearchUser err0r:"
                    + e.getMessage() + "----------------------");
            bundle = new Bundle();// 存储好友内容到bundle
            bundle.putInt("state", Config.SEARCH_USER_FALSE);
            intent.putExtras(bundle);
            mContext.sendBroadcast(intent);
		}
	}

	/**
	 * 添加好友
	 */
	public boolean addFriend(String userID, String friendID) {
		boolean result = true;
		try {
			dos.writeInt(Config.REQUEST_ADD_FRIEND);
			dos.writeUTF(userID);
			dos.writeUTF(friendID);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	/**
	 * 获取指定用户的头像
	 */
	private void handGetHead() {
		try {
       //接收用户ID
			String userId = dis.readUTF();
			//接收时间戳
			String modifyTime = dis.readUTF();
			//创建头像文件
			//File file = FileUtil.createHeadFile(userId);
			File file = FileUtil.createHeadFile(userId);
			//获得文件路径
			String headPath = file.getAbsolutePath();
			//保存头像文件
			receiveDataWriteFile(headPath);
			//设置文件时间戳
			file.setLastModified(Long.parseLong(modifyTime));
			//更新本地数据库
			dao.updateUserHead(mContext, userId, userId,headPath, modifyTime);
			//更新当前用户的头像文件路径
			Constants.userEbs.setHead(headPath);
			//向服务器发出获取好友信息列表请求
			getFriends(mContext, userId);
			//此处可能需要发送广播更新用户资料
			Intent intent = new Intent(Constants.FLUSH_HEAD);
			mContext.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 锟斤拷锟斤拷锟叫讹拷锟矫伙拷锟斤拷前锟角凤拷锟斤拷头锟斤拷锟矫伙拷选锟斤拷投锟斤拷锟斤拷直锟接革拷锟斤拷头锟今即匡拷 锟斤拷锟铰猴拷锟斤拷刷锟斤拷头锟斤拷悴�
	 */
	public void updateHeadImg(String headPath) {
		File file = new File(headPath);
		if (!file.exists()) {
			return;
		}
		String lastModifyTime = String.valueOf(file.lastModified());// 锟斤拷锟斤拷锟斤拷锟睫革拷时锟斤拷
		UserEbsEntity user = Constants.userEbs;
		String userID = "" + user.getUserCode();// 锟斤拷锟斤拷撕锟�
		// String psw = user.getPassword();//锟斤拷锟斤拷锟斤拷锟�
		int resoult = dao.updateUserHead(mContext, userID, userID, headPath, ""
				+ lastModifyTime);
		if (resoult > -1) {
			// 锟斤拷锟斤拷Constants.user锟叫碉拷头锟斤拷
			Constants.userEbs.setHead(headPath);
			try {
				dos.writeInt(Config.REQUEST_UPDATE_HEAD);
				dos.writeUTF(userID);
				dos.writeUTF(lastModifyTime);
				// 锟斤拷锟斤拷头锟今到凤拷锟斤拷锟斤拷
				readFileSendData(headPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 锟斤拷锟斤拷刷锟铰革拷锟斤拷锟斤拷息锟斤拷头锟斤拷墓悴�
			Intent flushHead = new Intent(Constants.FLUSH_HEAD);
			flushHead.putExtra("headPath", headPath);
			mContext.sendBroadcast(flushHead);
		}
	}

	/**
	 *更新名称
	 *更新后发送刷新头像广播
	 */
	public boolean updateName(String nick) {
		boolean flag = false;
		UserEbsEntity user = Constants.userEbs;
		// 获得账号
		String userID = String.valueOf(user.getUserId());
		Log.d(TAG, "updateName userID:" + userID);
		long resoult = dao.updateUserInfo(userID, "nick_Name", nick);
		Log.d(TAG, "updateName resoult:" + resoult);
		if (resoult > -1) {
			try {
				dos.writeInt(Config.REQUEST_UPDATE_NICK);
				Log.d(TAG, "updateName request_update_nick:"
						+ Config.REQUEST_UPDATE_NICK);
				dos.writeUTF(userID);
				Log.d(TAG, "updateName userID:" + userID);
				dos.writeUTF(nick);
				Log.d(TAG, "updateName nick:" + nick);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 更新Constants.user中的昵称
			Constants.userEbs.setUserName(nick);

			// 发送刷新个人信息中头像的广播
			Intent flushNike = new Intent(Constants.FLUSH_NICK);
			flushNike.putExtra("nick", nick);
			mContext.sendBroadcast(flushNike);

			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 *更新名称
	 *更新后发送刷新头像广播
	 */
	public boolean settingUserNum(String userNum) {
		boolean flag = false;
		String userID = Constants.userEbs.getUserId();
		long resoult = dao.updateUserInfo(userID, "userNum", userNum);
		if (resoult > -1) {
			try {
				dos.writeInt(Config.REQUEST_UPDATE_PASSWORD);
				dos.writeUTF(userID);
				dos.writeUTF(userNum);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
			// 发送刷新个人信息中头像的广播

			//更新Constants.user中的昵称
			Constants.userEbs.setUserNum(userNum);
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 更新tbsims号
	 */
	public boolean updatePassword(String password) {
		boolean flag = false;
		UserEbsEntity user = Constants.userEbs;
		String userID = String.valueOf(user.getUserId());// 获得账号
		long resoult = dao.updateUserInfo(userID, "psw", password);
		if (resoult > -1) {
			try {
				dos.writeInt(Config.REQUEST_UPDATE_PASSWORD);
				dos.writeUTF(userID);
				dos.writeUTF(password);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
			// 发送刷新个人信息中头像的广播

			// 更新Constants.user中的昵称
			Constants.userEbs.setPassword(password);
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 更新性别
	 */
	public boolean updateSex(String sex) {
		boolean flag = false;

		UserEbsEntity user = Constants.userEbs;

		String userID = String.valueOf(user.getUserId());// 获得账号
		long resoult = dao.updateUserInfo(userID, "sex", sex);
		if (resoult > -1) {
			try {
				dos.writeInt(Config.REQUEST_UPDATE_GENDER);
				dos.writeUTF(userID);
				dos.writeUTF(sex);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 更新Constants.user中的昵称
			Constants.userEbs.setSex(sex);

			// 发送刷新个人信息中性别
			Intent flushSex = new Intent(Constants.FLUSH_SEX);
			flushSex.putExtra("sex", sex);
			mContext.sendBroadcast(flushSex);

			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 更新地址
	 */
	public boolean updateDir(String dir) {
		boolean flag = false;

		UserEbsEntity user = Constants.userEbs;
		String[] userDir = dir.split(" ");

		String userID = String.valueOf(user.getUserId());// 获得账号
		long resoult = dao.updateUserInfo(userID, "data2", dir);
		if (resoult > -1) {
			try {
				dos.writeInt(Config.REQUEST_UPDATE_DIR);
				dos.writeUTF(userID);
				for (int i = 0; i < userDir.length; i++) {
					dos.writeUTF(userDir[i]);
				}
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// 更新Constants.user中的昵称
			Constants.userEbs.setData2(dir);

			// 发送刷新个人信息中性别
			Intent flushSex = new Intent(Constants.FLUSH_DIR);
			flushSex.putExtra("tree", dir);
			mContext.sendBroadcast(flushSex);

			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 设置ims账号
	 */
	public void handSettingUserNum() {

		try {
			int setState = dis.readInt();
			Intent setResult = new Intent(Constants.SETTING_USERNUM_STATE);
			setResult.putExtra("state", setState);
			mContext.sendBroadcast(setResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退出程序请求
	 */
	public void sendExitQuest() {
		LogUtil.record("-----------------------------------------------------------");
		LogUtil.record("****************** start sendExitQuest *****************");
		try {
			String userID = String.valueOf(Constants.userEbs.getUserId());
			dos.writeInt(Config.REQUEST_EXIT);
			LogUtil.record("****************** sendExitQuest REQUEST_LOGIN:"
					+ Config.REQUEST_LOGIN + " *****************");
			dos.writeUTF(userID);
			LogUtil.record("****************** sendExitQuest userID:" + userID
					+ " *****************");
			dos.flush();
			LogUtil.record("****************** sendExitQuest finish *****************");
		} catch (IOException e) {
			LogUtil.record("****************** sendExitQuest IOException:"
					+ e.getMessage() + " *****************");
			Log.e(TAG, "sendExitQuest error：" + e.getMessage());
		}
	}

	/**
	 * 退出用户请求
	 */
	public void sendExitUser() {
		LogUtil.record("-----------------------------------------------------------");
		LogUtil.record("****************** start sendExitUser *****************");
		try {
			String userID = String.valueOf(Constants.userEbs.getUserId());
			dos.writeInt(Config.REQUEST_EXIT_USER);
			LogUtil.record("****************** sendExitUser REQUEST_LOGIN:"
					+ Config.REQUEST_LOGIN + " *****************");
			dos.writeUTF(userID);
			LogUtil.record("****************** sendExitUser userID:" + userID
					+ " *****************");
			dos.flush();
			LogUtil.record("****************** sendExitUser finish *****************");
		} catch (IOException e) {
			LogUtil.record("****************** sendExitUser IOException:"
					+ e.getMessage() + " *****************");
			Log.e(TAG, "sendExitQuest error:" + e.getMessage());
		}
	}

	/**
	 * 锟剿筹拷锟矫伙拷锟斤拷锟斤拷
	 */
	// public void sendPushUser() {
	// LogUtil.record("****************** start sendExitUser *****************");
	// try {
	// String userID = String.valueOf(Constants.user.getSelf_id());
	// dos.writeInt(Config.REQUEST_PUSH_USER);
	// LogUtil.record("****************** sendPushUser REQUEST_LOGIN:" +
	// Config.REQUEST_PUSH_USER + " *****************");
	// dos.writeUTF(userID);
	// LogUtil.record("****************** sendPushUser userID:" + userID +
	// " *****************");
	// dos.flush();
	// LogUtil.record("****************** sendPushUser finish *****************");
	// } catch (IOException e) {
	// LogUtil.record("****************** sendPushUser IOException:" +
	// e.getMessage() + " *****************");
	// Log.e(TAG, "sendExitQuest error锟斤拷" + e.getMessage());
	// }
	// }

	/**
	 * 退出好友
	 */
	public void handExitUser() {
		Log.d(TAG, "---------------------------------------------------------");
		LogUtil.record("-----------------------------------------------------------");
		LogUtil.record("****************** start handExitUser *****************");
		try {
			int resoult = dis.readInt();
			Log.d(TAG, "handExitUser resoult:" + resoult);
			LogUtil.record("****************** handExitUser resoult:" + resoult
					+ " *****************");
			// 使用从服务器发送来的好友信息更新本地数据库中取出好友信息
			if (resoult == Config.EXIT_USER_SUCCESS) {
				// 发送广播，退出用户
				Intent intent = new Intent(Constants.EXIT_USER);
				mContext.sendBroadcast(intent);
			} else if (resoult == Config.EXIT_USER_UNSUCCESS) {

			}
			LogUtil.record("****************** handExitUser finish *****************");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 	 * 在注册的时候，并不发送头像图片的时间戳，而是有服务器端存储图片后，再把存储的图片的时间戳发回给客户端
	 * String userName,
	 */
	public boolean suggest(String suggestStr) {
		boolean flag = false;
		try {
			String userID = String.valueOf(Constants.userEbs.getUserId());
			dos.writeInt(Config.REQUEST_SEND_SUGGEST);
			dos.writeUTF(userID);
			dos.writeUTF(suggestStr);
			dos.flush();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取软件版和更新内容请求
	 */
	public boolean getVersion() {
		boolean flag = false;
		LogUtil.record("****************** start getVersion *****************");
		try {
			dos.writeInt(Config.REQUEST_GET_VERSION);
			LogUtil.record("****************** regist getVersion:"
					+ Config.REQUEST_GET_VERSION + " *****************");
			dos.flush();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取软件版和更新内容响应
	 */
	public void handGetVersion() {
		LogUtil.record("****************** start handGetVersion *****************");
		try {
			float version = dis.readFloat();
			LogUtil.record("****************** handGetVersion resoult:"
					+ version + " *****************");
			String updateInfo = dis.readUTF();
			LogUtil.record("****************** handGetVersion updateInfo:"
					+ updateInfo + " *****************");
			Intent intent = new Intent(Constants.UPDATE_APK);
			intent.putExtra("version", version);
			intent.putExtra("updateInfo", updateInfo);
			mContext.sendBroadcast(intent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取软件版和更新内容请求
	 */
	public boolean getDownloadApk() {
		boolean flag = false;
		LogUtil.record("****************** start getVersion *****************");
		try {
			dos.writeInt(Config.REQUEST_GET_DOWNLOAD);
			LogUtil.record("****************** regist getVersion:"
					+ Config.REQUEST_GET_VERSION + " *****************");
			dos.flush();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取软件版和更新内容响应
	 */
	public void handGetDownloadApk() {
		LogUtil.record("****************** start handGetVersion *****************");
		try {

			// 使用好友id创建头像名称
			File file = FileUtil.creatApk();
			// 文件路径
			String path = file.getAbsolutePath();
			// 将文件写入到sd卡内
			receiveDataWriteFile(path);
			// 保存文件路径
			setSharePerference(Constants.LAUNCHER_PREFERENCE, path);

			Intent intent = new Intent(Constants.INSTALL_APK);
			mContext.sendBroadcast(intent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接收文件保存到sdcard
	 */
	private void receiveDataWriteFile(String filePath)
			throws IOException {
		LogUtil.record("-----------------------------------------------------------");
		LogUtil.record("******************start receiveDataWriteFile *****************");
		// 创建文件
		File file = new File(filePath);
		// 输出文件
		FileOutputStream fos = new FileOutputStream(file);
		// 包装输出文件
		DataOutputStream dos = new DataOutputStream(fos);
		int length = 0;
		int totalNum = dis.readInt();
		int total = totalNum;
		byte[] buffer = new byte[BUFFER_SIZE];
		LogUtil.record("****************** readFileSendData file size:" + total
				+ "*****************");
		while (total > 0) {
			if (total < buffer.length) {
				length = dis.read(buffer, 0, total);
			} else {
				length = dis.read(buffer);
			}
			if (length > 0) {
				dos.write(buffer, 0, length);
				dos.flush();
			}
			total -= length;
			int number = totalNum - total;
			number = number / 1024;

			Intent intent = new Intent(Constants.FLUSH_DOWNLOAD_APK);
			intent.putExtra("downloadNum", number);
			mContext.sendBroadcast(intent);
		}
		LogUtil.record("******************readFileSendData length finish *****************");
		// 判断关闭dis和fis
		if (fos != null) {
			fos.close();
			fos = null;
		}
		if (dos != null) {
			dos.close();
			dos = null;
		}
	}

	/**
	 * 读取文件(图片、语音)，发送数据
	 */
	private void readFileSendData(String filePath)
			throws IOException {
		LogUtil.record("-----------------------------------------------------------");
		LogUtil.record("******************start readFileSendData *****************");
		// 创建文件
		File file = new File(filePath);
		LogUtil.record("****************** readFileSendData file size:"
				+ file.length() + "*****************");
		// 读取文件
		FileInputStream fis = new FileInputStream(file);
		// 包装文件输入流
		DataInputStream dis = new DataInputStream(fis);
		int length = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int totalNum = (int) file.length();
		dos.writeInt(totalNum);
		while (totalNum > 0) {
			length = dis.read(buffer);
			dos.write(buffer, 0, length);
			dos.flush();
			totalNum -= length;
//			LogUtil.record("******************readFileSendData length:"
//					+ totalNum + "*****************");
		}
		LogUtil.record("******************readFileSendData length finish *****************");
		// 判断关闭dis和fis
		if (fis != null) {
			fis.close();
			fis = null;
		}
		if (dis != null) {
			dis.close();
			dis = null;
		}
	}

	/**
	 * 判断是否连接中
	 * 
	 * @return
	 */
	public boolean isConnection() {
		return socket.isConnected();
	}

	/**
	 * 获得配置property
	 * 
	 * @param name
	 * @return
	 */
	public String getPropertyItem(String name) {
		String webRoot = Util.getShareperference(mContext,
				Constants.SAVE_INFORMATION, "Path", Constants.CONFIG_INI_URL);
		String appTestFile = webRoot;
		IniFile IniFile = new IniFile();
		if (!webRoot.endsWith(".ini")) {
			if (webRoot.endsWith("/") == false) {
				webRoot += "/";
			}
			String WebIniFile = webRoot + Constants.WEB_CONFIG_FILE_NAME;
			appTestFile = webRoot
					+ IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
							Constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
		}
		IniFile m_iniFileIO = new IniFile();
		// 获取ini
		String item = m_iniFileIO.getIniString(appTestFile,
				Constants.EBSSERVER, name, "e.tbs.com.cn", (byte) 0);
		return item;
	}

	/*
	 *  保存内容到shareperence 设置第一次登录属性为true
	 */
	public void setSharePerference(String perferenceName, String value) {
		SharedPreferences setting = mContext.getSharedPreferences(
				perferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = setting.edit();
		editor.putString("download_apk_path", value);
		editor.commit();
	}

	/**
	 * 获得serverIP
	 * 
	 * @return
	 */
	public InetAddress getServerIP() {
		InetAddress myServer = null;
		try {
			String IP = getPropertyItem(Constants.URL);
			myServer = InetAddress.getByName(IP);
		} catch (UnknownHostException e) {

		}
		return myServer;
	}

		/**
	 * 登陆成功状态
	 * 成功返回true
	 * 失败返回false
	 * 参数是传入的返回值
	 */
	public void sendBCast(String stateKey, int stateValue, String filter) {
		Intent intent = new Intent(filter);
		intent.putExtra(stateKey, stateValue);
		mContext.sendBroadcast(intent);
	}

	public void sendBCast(String stateKey, int stateValue, Intent intent) {
		intent.putExtra(stateKey, stateValue);
		mContext.sendBroadcast(intent);
	}
}
