package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.WeChatMessageAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.database.dao.DBUtil;
import com.tbs.tbsmis.database.table.WX_MESSAGE_TABLE;
import com.tbs.tbsmis.entity.WXGroupEntity;
import com.tbs.tbsmis.entity.WXTextEntity;
import com.tbs.tbsmis.entity.WXUserEntity;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.TxtImport;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.widget.PullToRefreshListView;
import com.tbs.tbsmis.widget.PullToRefreshListView.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

//import com.tbs.chat.constants.Constants;

public class WeixinNewMessage extends Activity implements View.OnClickListener {
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private PullToRefreshListView lvNews;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	private String userIni;
	private IniFile IniFile;
	private WeChatMessageAdapter lvUserAdapter;
	private ImageView backBtn;
	private TextView title_tv;
	private DBUtil dao;
	private ArrayList<WXTextEntity> message;
	private String dbName;
	private BroadcastReceiver MyBroadCastReceiver;
	private boolean isOpenPop;
	private PopupWindow moreWindow2;
	private RelativeLayout cloudTitle;
	private ImageView menuBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_user_list);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.title_tv.setText("消息管理");
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.menuBtn.setVisibility(View.VISIBLE);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinNewMessage.this.finish();
			}
		});
        this.menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinNewMessage.this.changMorePopState2(v);
			}
		});
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(constants.REFRESH_MESSAGE);
		filter.addCategory("default");
        this.MyBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (constants.REFRESH_MESSAGE.equals(intent.getAction())) {
                    WeixinNewMessage.this.message = WeixinNewMessage.this.dao.getMessage1(WeixinNewMessage.this, WeixinNewMessage.this.dbName);
                    WeixinNewMessage.this.lvUserAdapter = new WeChatMessageAdapter(WeixinNewMessage.this.message,
							WeixinNewMessage.this, WeixinNewMessage.this.dao);
                    WeixinNewMessage.this.lvNews.setAdapter(WeixinNewMessage.this.lvUserAdapter);
					// lvNews.onRefreshComplete();
				}
			}
		};
        this.registerReceiver(this.MyBroadCastReceiver, filter);
        this.IniFile = new IniFile();
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        this.dao = DBUtil.getInstance(this);
        this.lvNews_footer = this.getLayoutInflater().inflate(R.layout.listview_footer,
				null);
        this.lvNews_foot_more = (TextView) this.lvNews_footer
				.findViewById(R.id.listview_foot_more);
        this.lvNews_foot_progress = (ProgressBar) this.lvNews_footer
				.findViewById(R.id.listview_foot_progress);
        this.lvNews = (PullToRefreshListView) this.findViewById(R.id.frame_listview_users);
        this.lvNews.addFooterView(this.lvNews_footer);// 添加底部视图 必须在setAdapter前
        this.lvNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || position == WeixinNewMessage.this.message.size() + 1)
					return;
				WeChatMessageAdapter.ViewHolder holder = (WeChatMessageAdapter.ViewHolder) view.getTag();
				String apptext = holder.openId;
				// 跳转到回复详情
				Intent intent = new Intent();
				intent.putExtra("openId", apptext);
				intent.setClass(WeixinNewMessage.this, WeixinChatActivity.class);
                WeixinNewMessage.this.startActivity(intent);
			}
		});
        this.lvNews.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				WeChatMessageAdapter.ViewHolder holder = (WeChatMessageAdapter.ViewHolder) view.getTag();
				final String openId = holder.openId;
				String apptext = holder.tv.getText().toString();
				new Builder(WeixinNewMessage.this)
						.setCancelable(false)
						.setMessage("删除与“" + apptext + "”的对话？")
						// 提示框标题
						.setPositiveButton(
								"确定",// 提示框的两个按钮
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
                                        WeixinNewMessage.this.dao.deleteMessages(openId);
                                        WeixinNewMessage.this.message.remove(position - 1);
                                        WeixinNewMessage.this.lvUserAdapter.notifyDataSetChanged();
									}
								}).setNegativeButton("取消", null).create()
						.show();
				return true;
			}
		});
        this.lvNews.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                WeixinNewMessage.this.lvNews.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (WeixinNewMessage.this.message.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(WeixinNewMessage.this.lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(WeixinNewMessage.this.lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    WeixinNewMessage.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    WeixinNewMessage.this.lvNews_foot_more
							.setText(R.string.pull_to_refresh_refreshing_label);
                    WeixinNewMessage.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					// int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    WeixinNewMessage.this.initUserData(2);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
                WeixinNewMessage.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
        this.lvNews.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
                WeixinNewMessage.this.connect(1);
			}
		});
        this.dbName = this.IniFile.getIniString(this.userIni, "WeiXin", "iniPath",
				"wechat", (byte) 0);
        this.message = this.dao.getMessage1(this, this.dbName);
        this.lvUserAdapter = new WeChatMessageAdapter(this.message, this, this.dao);
        this.lvNews.setAdapter(this.lvUserAdapter);
        this.lvNews.onRefreshComplete();
		if (this.message.size() <= 0)
            this.lvNews_foot_more.setText("无最新消息");
		else
            this.lvNews_foot_more.setText(R.string.load_full);
        this.lvNews_foot_progress.setVisibility(View.GONE);
		ArrayList<WXUserEntity> Userlist = this.dao.getUsersInfo(this, this.dbName);
		if (Userlist.size() > 0) {
            this.connect(1);
		} else {
            this.connect(2);
		}

	}

	public void initUserData(int count) {
		if (count == 1) {
            this.lvNews.onRefreshComplete(this.getString(R.string.pull_to_refresh_update)
					+ new Date().toLocaleString());
			// NewDataToast.makeText(this, "已是最新信息", false).show();
		} else
            this.lvNews.onRefreshComplete();
        this.lvUserAdapter.notifyDataSetChanged();
        this.lvNews.setTag(UIHelper.LISTVIEW_DATA_FULL);
		if (this.message.size() <= 0)
            this.lvNews_foot_more.setText("无最新消息");
		else
            this.lvNews_foot_more.setText(R.string.load_full);
        this.lvNews_foot_progress.setVisibility(View.GONE);
	}

	public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
	}

	public void stopAnimation() {
		// loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
	}

	public int impot2Text() {
		// String m_fieldNameSep = "";// 获得xml中的内容
		// String m_recordSep = "";// 获得xml中的内容
		// int m_recordSepCount = 3;// 获得xml中的内容
		// String m_lineSplitter = "";// 获得xml中的内容
		TxtImport importTest = new TxtImport(this);// 实例化TxtImport类

		// 此处需要做入库判断，所还需要想思路
		// importTest.setScanParams(m_fieldNameSep, m_recordSep,
		// m_recordSepCount,
		// m_lineSplitter);
		// 设定导入导入的数据库表
		importTest.changeTable(WX_MESSAGE_TABLE.TABLE_NAME);
		// 根据需要设定表中各字段的别名
		importTest.setColumnAlias("ToUserName", "账户id");
		importTest.setColumnAlias("FromUserName", "关注用户id");
		importTest.setColumnAlias("CreateTime", "时间");
		importTest.setColumnAlias("MsgType", "消息类型");
		importTest.setColumnAlias("Content", "消息内容");
		importTest.setColumnAlias("MsgId", "消息id");
		// 保存路径
		String webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String savePath = webRoot
				+ "WeiXin/"
				+ this.IniFile.getIniString(this.userIni, "WeiXin", "Account",
						"TBS软件", (byte) 0) + "/msg/allmsg.txt";
		// 导入数据库的数量
		int tempStr = importTest.export2Txt(savePath, this.dbName, true);
		return tempStr;
	}

	public void changMorePopState2(View v) {
        this.isOpenPop = !this.isOpenPop;
		if (this.isOpenPop) {
            this.popWindow2(v);
		} else {
			if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void popWindow2(View parent) {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = lay.inflate(R.layout.weixin_save_menu, null);
		RelativeLayout send_text = (RelativeLayout) view
				.findViewById(R.id.menu_save);
		RelativeLayout menu_refresh = (RelativeLayout) view
				.findViewById(R.id.menu_refresh);
		RelativeLayout menu_find = (RelativeLayout) view
				.findViewById(R.id.menu_find);
		menu_find.setBackgroundResource(R.drawable.more_down);
		menu_find.setVisibility(View.VISIBLE);
		menu_refresh.setVisibility(View.VISIBLE);
		menu_refresh.setOnClickListener(this);
		menu_find.setOnClickListener(this);
		send_text.setOnClickListener(this);
        this.moreWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
                WeixinNewMessage.this.isOpenPop = false;
			}
		});
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        this.unregisterReceiver(this.MyBroadCastReceiver);
		MyActivity.getInstance().finishActivity(this);
	}

	public void initJson2db(String json, String dbName) {
		try {
			JSONObject jsonresult = new JSONObject(json);
			JSONArray jsonUsers = jsonresult.getJSONArray("users");
			JSONArray jsonGroups = jsonresult.getJSONArray("group");
			ArrayList<WXGroupEntity> groups = new ArrayList<WXGroupEntity>();
			for (int i = 0; i < jsonGroups.length(); i++) {
				JSONObject ob = jsonGroups.getJSONObject(i);
				WXGroupEntity group = new WXGroupEntity();
				group.setId(ob.getInt("groupid"));
				group.setName(ob.getString("groupname"));
				group.setCount(ob.getInt("groupcount"));
				groups.add(group);
			}
            this.dao.addOrUpdateGruop(groups);
			ArrayList<WXUserEntity> users = new ArrayList<WXUserEntity>();
			for (int i = 0; i < jsonUsers.length(); i++) {
				JSONObject ob = jsonUsers.getJSONObject(i);
				WXUserEntity user = new WXUserEntity();
				user.setOpenid(ob.getString("openid"));
				user.setNickname(ob.getString("nickname"));
				user.setCity(ob.getString("city"));
				user.setProvince(ob.getString("province"));
				user.setCountry(ob.getString("country"));
				user.setSex(ob.getInt("sex"));
				user.setLanguage(ob.getString("language"));
				user.setHeadimgurl(ob.getString("headimgurl"));
				user.setSubscribe_time(ob.getInt("subscribe_time"));
				user.setRemark(ob.getString("remark"));
				user.setGroupid(ob.getInt("groupid"));
				user.setSubscribe(ob.getInt("subscribe"));
				users.add(user);
			}
            this.dao.addOrUpdateUser(users, dbName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void connect(int count) {
		WeixinNewMessage.GetAsyncTask task = new WeixinNewMessage.GetAsyncTask(this, count);
		task.execute();
	}

	/**
	 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
	 * 
	 */
	class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;
		private final int count;

		public GetAsyncTask(Context context, int count) {
            this.context = context;
			this.count = count;
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
            WeixinNewMessage.this.startAnimation();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			String username = "";
			if (this.count == 1) {
				String verifyURL = WeixinNewMessage.this.IniFile.getIniString(WeixinNewMessage.this.userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getNewText&username="
						+ username
						+ "&deviceid=" + UIHelper.DeviceMD5ID(this.context);
				String result = connection.asyncConnect(verifyURL, null,
						HttpConnectionUtil.HttpMethod.GET, this.context);
				if (!StringUtils.isEmpty(result)) {
					try {
						JSONArray jsonUsers = new JSONObject(result)
								.getJSONArray("msg");
						ArrayList<WXTextEntity> messages = new ArrayList<WXTextEntity>();
						for (int i = 0; i < jsonUsers.length(); i++) {
							JSONObject ob = jsonUsers.getJSONObject(i);
							WXTextEntity msg = new WXTextEntity();
							msg.setToUserName(ob.getString("ToUserName"));
							msg.setContent(ob.getString("Content"));
							msg.setCreateTime(ob.getInt("CreateTime"));
							msg.setDirection(0);
							msg.setFromUserName(ob.getString("FromUserName"));
							msg.setMsgId(ob.getLong("MsgId"));
							msg.setMsgType(ob.getString("MsgType"));
							msg.setSendState(0);
							messages.add(msg);
						}
						if (messages.size() > 0) {
                            WeixinNewMessage.this.dao.addOrUpdateMessage(messages, WeixinNewMessage.this.dbName);
							return "0";
						} else {
							return "1";
						}
					} catch (JSONException e) {
						return "3";
					}
				}
			} else if (this.count == 2) {
				String verifyURL = WeixinNewMessage.this.IniFile.getIniString(WeixinNewMessage.this.userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getUsersInfo&username="
						+ username
						+ "&deviceid=" + UIHelper.DeviceMD5ID(this.context);
				String verify = connection.asyncConnect(verifyURL, null,
						HttpConnectionUtil.HttpMethod.GET, this.context);
				if (!StringUtils.isEmpty(verify)) {
                    WeixinNewMessage.this.initJson2db(verify, WeixinNewMessage.this.dbName);
					return "true";
				}

			}
			return null;
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
                WeixinNewMessage.this.stopAnimation();
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else if (this.count == 1) {
                WeixinNewMessage.this.initUserData(Integer.parseInt(result));
                WeixinNewMessage.this.stopAnimation();
			} else if (this.count == 2) {
                WeixinNewMessage.this.connect(1);
			}
		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu_save:
			int count = this.impot2Text();
			Toast.makeText(this, "保存了" + count + "条信息", Toast.LENGTH_SHORT)
					.show();
            this.moreWindow2.dismiss();
			break;
		case R.id.menu_refresh:
            this.connect(1);
            this.moreWindow2.dismiss();
			break;
		case R.id.menu_find:
			Intent intent = new Intent();
			intent.setClass(this, WeixinSearchActivity.class);
            this.startActivity(intent);
            this.moreWindow2.dismiss();
			break;
		}
	}

}
