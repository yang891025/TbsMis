package com.tbs.chat.socket;

import android.content.Context;

/**
 * Communication类是用户和实际发送、接收消息线程之间的桥梁，用户向Communication发送命令，
 * Communication调用线程执行发送请求
 */
public class Communication {

	
	private static NetWorker netWorker; // 这是一个线程类
	private static Communication instance; // 一个静态的Communication实例
	private Context mcontext;// 消息摘要

	//启动连接服务器
	private Communication() {
		netWorker = new NetWorker();//实例化线程类
		netWorker.start();// 线程开始执行
	}
	
	private Communication(Context context) {
		this.mcontext = context;
		netWorker = new NetWorker(context);//实例化线程类
		netWorker.start();// 线程开始执行
	}
	
	public static Communication newInstance() {
		if (instance == null){
			instance = new Communication();
		}
		return instance;
	}

	public static Communication newInstance(Context context) {
		if (instance == null){
			instance = new Communication(context);
		}
		return instance;
	}

	public void setInstanceNull() {
		instance = null;
	}

	public NetWorker getTransportWorker() {
		return netWorker;
	}
	
	/*
	 * 只负责发送数据包，返回包根据内容再通知是否成功
	 * 返回false则通信失败,true通信成功
	 */
	public void verifyPhone(String phone) {
		// 将账号和密码发送到了服务器端，本方法就返回true
		netWorker.verifyPhone(phone);
	}
	
	/*
	 * 只负责发送数据包，返回包根据内容再通知是否成功
	 * 返回false则通信失败,true通信成功
	 */
	public void verifyNumber(String number,String phone) {
		// 将账号和密码发送到了服务器端，本方法就返回true
		netWorker.verifyNumber(number,phone);
	}
	
	/*
	 * 只负责发送数据包，返回包根据内容再通知是否成功
	 * 返回false则通信失败,true通信成功
	 * String userName,
	 */
	public boolean register(String psw,String nick,String phone,String countryCode,String headPath) {
		// 将账号和密码发送到了服务器端，本方法就返回true
		return netWorker.regist(psw, nick, phone, countryCode, headPath);
	}

	//登录
	public boolean login(String userId, String pwd) {
		return netWorker.login(userId, pwd);
	}
	
	// 手机号登录
	public boolean loginPhone(String phone) {
		return netWorker.loginPhone(phone);
	}

	//获取好友列表
	public void getFriends(String userId) {
		netWorker.getFriends(mcontext,userId);
	}

	// 根据UserId查找用户信息
	public void searchUser(String value) {
		netWorker.searchUser(value);
	}

	// 添加好友
	public boolean addFriend(String userID, String friendID) {
		return netWorker.addFriend(userID, friendID);
	}

	// 更新头像
	public void updateHeadImg(String headPath) {
		netWorker.updateHeadImg(headPath);
	}
	
	// 更新昵称
	public boolean updateName(String nick) {
		boolean flag = netWorker.updateName(nick);
		return flag;
	}
	
	// 更
	public boolean updatePassword(String password) {
		boolean flag = netWorker.updatePassword(password);
		return flag;
	}
	
	// 设置tbsims号
	public boolean settingUserNum(String userNum) {
		boolean flag = netWorker.updatePassword(userNum);
		return flag;
	}
	
	// 更新行呗
	public boolean updateSex(String sex) {
		boolean flag = netWorker.updateSex(sex);
		return flag;
	}
	
	// 更新地区
	public boolean updateDie(String dir) {
		boolean flag = netWorker.updateDir(dir);
		return flag;
	}

	public boolean sendImg(String self, String friend, String time, String content) {
		return netWorker.sendImg(self, friend, time, content);
	}

	public boolean sendText(String self, String friend, String time, String content) {
		return netWorker.sendText(self, friend, time, content);
	}

	public boolean sendAudio(String self, String friend, String time, String content, String audioTime) {
		return netWorker.sendAudio(self, friend, time, content, audioTime);
	}
	
	//接收暂存在服务器端的离线消息
	public void getOfflineMessage(String userID) {
		netWorker.getOfflineMessage(userID);
	}
	
	/**
	 * 发送意见建议
	 */
	public boolean suggest(String suggestStr) {
		return netWorker.suggest(suggestStr);
	}
	
	/**
	 * 发送意见建议
	 */
	public boolean getVersion() {
		return netWorker.getVersion();
	}
	
	/**
	 * 发送下载apk请求
	 */
	public boolean getDownloadApk() {
		return netWorker.getDownloadApk();
	}

	//清理退出
	public void sendExitQuest() {
		netWorker.sendExitQuest();
	}
	
	// 退出登录用户
	public void sendExitUser() {
		netWorker.sendExitUser();
	}
	
	// 挤下在线用户
	// public void sendPushUser() {
	// netWorker.sendPushUser();
	// }
	
	//创建sessionid
	public String newSessionID() {
		return String.valueOf(System.currentTimeMillis());
	}

	//重新连接
	public void reconnect() {
		netWorker.notify();
	}
	
	//程序退出时的清理工作
	public void stopWork() {
		netWorker.setOnWork(false);
	}
	
	//判断socket是否还在连接着
	public boolean isConnection(){
		return netWorker.isConnection();
	}
	
	// 获取指定账号的用户信息
	public void getFriendsInfo(String friendID) {
		netWorker.getFriendsInfo(mcontext, friendID);
	}
}
