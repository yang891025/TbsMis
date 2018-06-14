package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.ChatMsgViewAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.database.dao.DBUtil;
import com.tbs.tbsmis.database.table.WX_MESSAGE_TABLE;
import com.tbs.tbsmis.entity.WXTextEntity;
import com.tbs.tbsmis.entity.WXUserEntity;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.TxtImport;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.httpRequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

//import com.tbs.chat.constants.Constants;

public class WeixinChatActivity extends Activity implements View.OnClickListener {

	private String openId;
	private ArrayList<WXTextEntity> Msglist;
	private IniFile IniFile;
	private ChatMsgViewAdapter MyMsgAdapter;
	private ImageView backBtn;
	private TextView chat_title;
	private ListView MyListView;
	private Button chat_sendBtn;
	private EditText chat_contentEdit;
	private String userIni;
	private DBUtil dao;
	private String dbName;
	private WXUserEntity userInfo;
	private int Selection;
	private boolean isOpenPop;
	private PopupWindow moreWindow2;
	private RelativeLayout cloudTitle;
	private ImageView menuBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weixin_chat_layout);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.Msglist = new ArrayList<WXTextEntity>();
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.rl_layout);
        this.chat_title = (TextView) this.findViewById(R.id.chat_title);
        this.chat_contentEdit = (EditText) this.findViewById(R.id.chat_contentEdit);
        this.backBtn = (ImageView) this.findViewById(R.id.chat_backBtn);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.MyListView = (ListView) this.findViewById(R.id.chat_listview);
        this.chat_sendBtn = (Button) this.findViewById(R.id.chat_sendBtn);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinChatActivity.this.finish();
			}
		});
        this.menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinChatActivity.this.changMorePopState2(v);
			}
		});
        this.chat_sendBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StringUtils.isEmpty(WeixinChatActivity.this.chat_contentEdit.getText().toString())) {
					Toast.makeText(WeixinChatActivity.this, "信息不可为空",
							Toast.LENGTH_SHORT).show();
				} else {
					WXTextEntity msg = new WXTextEntity();
					msg.setToUserName(WeixinChatActivity.this.dbName);
					msg.setContent(WeixinChatActivity.this.chat_contentEdit.getText().toString());
					msg.setCreateTime(StringUtils.getNowTimeStamp());
					msg.setDirection(1);
					msg.setFromUserName(WeixinChatActivity.this.openId);
					msg.setMsgId((WeixinChatActivity.this.openId + new Date()).hashCode());
					msg.setMsgType("text");
					msg.setSendState(1);
                    WeixinChatActivity.this.Msglist.add(msg);
                    WeixinChatActivity.this.MyMsgAdapter.notifyDataSetChanged();
                    WeixinChatActivity.this.MyListView.setSelection(WeixinChatActivity.this.MyListView.getCount() - 1);
                    WeixinChatActivity.this.connect(0, msg, WeixinChatActivity.this.Msglist.size() - 1);
                    WeixinChatActivity.this.chat_contentEdit.setText("");
				}
			}
		});
        this.init();
	}

	private void init() {
        this.IniFile = new IniFile();
        String webRoot = UIHelper.getSoftPath(this);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                "Path", webRoot);
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
        this.dbName = this.IniFile.getIniString(userIni, "WeiXin", "iniPath",
				"wechat", (byte) 0);
		// TODO Auto-generated method stub
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
			int search = intent.getIntExtra("search", 0);
            this.openId = intent.getStringExtra("openId");
            this.Msglist = this.dao.getMessage(this, this.openId);
            this.userInfo = this.dao.queryName(this.openId, this.dbName);
            this.Selection = this.Msglist.size();
			if (search == 1) {
				long msgid = intent.getLongExtra("msgid", 0);
				for (int i = 0; i < this.Selection; i++) {
					if (msgid == this.Msglist.get(i).getMsgId()) {
                        this.Selection = i;
						break;
					}
				}
			}
		}
        this.MyMsgAdapter = new ChatMsgViewAdapter(this, this.Msglist, this.userInfo);
        this.MyListView.setAdapter(this.MyMsgAdapter);
        this.MyListView.setSelection(this.Selection);
        this.chat_title.setText(this.userInfo.getNickname());

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
						"TBS软件", (byte) 0) + "/msg/" + this.userInfo.getNickname()
				+ "_msg.txt";
		// 导入数据库的数量
		int tempStr = importTest.export2Txt(savePath, this.dbName + "' and "
				+ WX_MESSAGE_TABLE.FROMUSERNAME + "='" + this.openId, true);
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
		RelativeLayout menu_clear = (RelativeLayout) view
				.findViewById(R.id.menu_clear);
		menu_find.setBackgroundResource(R.drawable.more_down);
		menu_find.setVisibility(View.VISIBLE);
		menu_clear.setVisibility(View.VISIBLE);
		menu_refresh.setVisibility(View.VISIBLE);
		menu_refresh.setOnClickListener(this);
		menu_clear.setOnClickListener(this);
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
                WeixinChatActivity.this.isOpenPop = false;
			}
		});
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
	}

	private void connect(int count, String token, WXTextEntity msg, int listNum) {
		WeixinChatActivity.GetAsyncTask task = new WeixinChatActivity.GetAsyncTask(count, this, token, msg, listNum);
		task.execute();
	}

	private void connect(int count, WXTextEntity msg, int listNum) {
		WeixinChatActivity.GetAsyncTask task = new WeixinChatActivity.GetAsyncTask(count, this, msg, listNum);
		task.execute();
	}

	/**
	 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
	 * 
	 */
	class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;
		private final int count;
		private String Token;
		private final int listNum;
		private final WXTextEntity msg;

		public GetAsyncTask(int count, Context context, WXTextEntity msg,
				int listNum) {
            this.context = context;
			this.count = count;
			this.listNum = listNum;
			this.msg = msg;
		}

		public GetAsyncTask(int count, Context context, String Token,
				WXTextEntity msg, int listNum) {
            this.context = context;
			this.count = count;
			this.Token = Token;
			this.listNum = listNum;
			this.msg = msg;
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {

		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			if (this.count == 0) {
				String verifyURL = WeixinChatActivity.this.IniFile.getIniString(userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getToken";
				return connection.asyncConnect(verifyURL, null, HttpConnectionUtil.HttpMethod.GET,
                        this.context);
			} else {
				String verifyURL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="
						+ this.Token;
				String menuUrl = "{\"touser\":\"" + WeixinChatActivity.this.openId
						+ "\",\"msgtype\":\"text\",\"text\":{\"content\":\""
						+ this.msg.getContent() + "\"}}";
				JSONObject jsonObject = httpRequestUtil.httpsRequest(verifyURL,
						"POST", menuUrl);
				if (null != jsonObject) {
					try {
						if (jsonObject.getInt("errcode") == 0) {
							return "2";
						} else {
							return "3";
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return "3";
			}
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
                WeixinChatActivity.this.MyListView.setSelection(0);
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else {
				if (this.count == 0) {
                    WeixinChatActivity.this.connect(1, result, this.msg, this.listNum);
				} else if (this.count == 1) {
                    WeixinChatActivity.this.Msglist.get(this.listNum).setSendState(Integer.parseInt(result));
                    WeixinChatActivity.this.MyMsgAdapter.notifyDataSetChanged();
                    WeixinChatActivity.this.MyListView.setSelection(WeixinChatActivity.this.MyListView.getCount() - 1);
                    WeixinChatActivity.this.dao.addOrUpdateMessage(this.msg, this.msg.getToUserName());
				}
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
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
            this.Msglist = this.dao.getMessage(this, this.openId);
            this.MyMsgAdapter = new ChatMsgViewAdapter(this, this.Msglist, this.userInfo);
            this.MyListView.setAdapter(this.MyMsgAdapter);
            this.moreWindow2.dismiss();
			break;
		case R.id.menu_clear:
            this.dao.deleteMessages(this.openId);
            this.Msglist.clear();
            this.MyMsgAdapter.notifyDataSetChanged();
            this.moreWindow2.dismiss();
			Intent intent1 = new Intent(constants.REFRESH_MESSAGE);
            this.sendBroadcast(intent1);
			break;
		case R.id.menu_find:
			Intent intent = new Intent();
			intent.putExtra("openId", this.openId);
			intent.setClass(this, WeixinSearchActivity.class);
            this.startActivity(intent);
            this.moreWindow2.dismiss();
			break;
		}
	}
}