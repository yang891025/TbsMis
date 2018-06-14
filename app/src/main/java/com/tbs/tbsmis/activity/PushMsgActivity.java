package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 信息推送
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class PushMsgActivity extends Activity implements View.OnClickListener {
	private LinearLayout push_user;
	private EditText push_username, push_title, push_content, push_link;
	private Button push_btn, cancle_btn;
	private RadioGroup push_radio_group;
	private IniFile m_iniFileIO;
	private String userIni;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_push);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.initView();
	}

	// 初始化视图控件
	private void initView() {
        this.push_username = (EditText) this.findViewById(R.id.push_username);
        this.push_title = (EditText) this.findViewById(R.id.push_title);
        this.push_content = (EditText) this.findViewById(R.id.push_content);
        this.push_link = (EditText) this.findViewById(R.id.push_link);
        this.push_btn = (Button) this.findViewById(R.id.push_btn);
        this.cancle_btn = (Button) this.findViewById(R.id.cancle_btn);
        this.push_user = (LinearLayout) this.findViewById(R.id.push_user);
        this.push_radio_group = (RadioGroup) this.findViewById(R.id.push_radio_group);
		UIHelper.setSharePerference(this, constants.SAVE_INFORMATION,
				"checkedId", 0);
        this.push_radio_group
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// 根据ID判断选择的按钮
						if (checkedId == R.id.push_broadcast) {
                            PushMsgActivity.this.push_user.setVisibility(View.GONE);
							UIHelper.setSharePerference(PushMsgActivity.this,
									constants.SAVE_INFORMATION, "checkedId", 0);
						} else if (checkedId == R.id.push_signle) {
                            PushMsgActivity.this.push_user.setVisibility(View.VISIBLE);
							UIHelper.setSharePerference(PushMsgActivity.this,
									constants.SAVE_INFORMATION, "checkedId", 1);
						} else if (checkedId == R.id.push_group) {
                            PushMsgActivity.this.push_user.setVisibility(View.VISIBLE);
							UIHelper.setSharePerference(PushMsgActivity.this,
									constants.SAVE_INFORMATION, "checkedId", 2);
						} else if (checkedId == R.id.push_anonymous) {
                            PushMsgActivity.this.push_user.setVisibility(View.GONE);
							UIHelper.setSharePerference(PushMsgActivity.this,
									constants.SAVE_INFORMATION, "checkedId", 3);
						}
					}
				});
        this.push_btn.setOnClickListener(this);
        this.cancle_btn.setOnClickListener(this);
	}

	private void initPath() {
        this.m_iniFileIO = new IniFile();
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
        String appFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appFile;
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		MyActivity.getInstance().finishActivity(this);
	}

	public Map<String, String> sendverify() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "send");
		int checkedId = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "checkedId", 0);
		if (checkedId == 0) {
			params.put("broadcast", "Y");
		} else if (checkedId == 1) {
			params.put("broadcast", "N");
		} else if (checkedId == 2) {
			params.put("broadcast", "G");
		} else if (checkedId == 3) {
			params.put("broadcast", "U");
		}
		String message = "tbs:default:【标题】:" + this.push_title.getText()
				+ "\r\n" + "【来源】:\r\n" + "【日期】:" + StringUtils.getDate()
				+ "\r\n" + "【日志】:\r\n" + "【正文】:"
				+ this.push_content.getText();
		params.put("username", this.push_username.getText().toString());
		params.put("title", this.push_title.getText().toString());
		params.put("message", message);
		params.put("uri", this.push_link.getText().toString());
		return params;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
		}
		return true;
	}

	class HttpAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;

		public HttpAsyncTask(Context context) {
			this.context = context;
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 * 
		 * @return
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
            PushMsgActivity.this.initPath();
			constants.verifyURL = "http://"
					+ PushMsgActivity.this.m_iniFileIO.getIniString(userIni, "Push",
							"sendAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ PushMsgActivity.this.m_iniFileIO.getIniString(userIni, "Push", "sendPort",
							"8089", (byte) 0) + "/notification.do";
			return connection.asyncConnect(constants.verifyURL, PushMsgActivity.this.sendverify(),
					HttpConnectionUtil.HttpMethod.POST, this.context);
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (StringUtils.isEmpty(result)) {
				Toast.makeText(this.context, "错误(E0023)：无法连接到推送服务!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this.context, "推送已提交", Toast.LENGTH_SHORT).show();
                PushMsgActivity.this.finish();
			}

		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {

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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cancle_btn:
            this.finish();
			break;
		case R.id.push_btn:
			int checkedId = UIHelper.getShareperference(this,
					constants.SAVE_INFORMATION, "checkedId", 0);
			if (StringUtils.isEmpty(this.push_content.getText().toString())) {
				Toast.makeText(this, "推送信息不可为空",
						Toast.LENGTH_SHORT).show();
			} else {
				if (checkedId == 1 || checkedId == 2) {
					if (StringUtils.isEmpty(this.push_username.getText().toString())) {
						Toast.makeText(this, "用户名不可为空",
								Toast.LENGTH_SHORT).show();
					} else {
						PushMsgActivity.HttpAsyncTask task = new PushMsgActivity.HttpAsyncTask(this);
						task.execute();
					}
				} else {
					PushMsgActivity.HttpAsyncTask task = new PushMsgActivity.HttpAsyncTask(this);
					task.execute();
				}
			}
			break;
		}
	}
}
