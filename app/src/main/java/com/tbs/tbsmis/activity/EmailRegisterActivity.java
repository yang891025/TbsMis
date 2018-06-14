package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EmailRegisterActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private LinearLayout dynamicTag;
	private TextView dynamicTxt;
	private EditText phoneNum;
	private EditText passwordEt;
	private ImageView iv;
	private RelativeLayout registBtn;
	private AnimationDrawable loadingAnima;
	private TextView titleTxt;
	private IniFile m_iniFileIO;
	private String webRoot;
	private String appFile;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_email_regist);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.title_btn_left);
        this.rightBtn = (Button) this.findViewById(R.id.title_btn_right);
        this.titleTxt = (TextView) this.findViewById(R.id.title);

        this.dynamicTag = (LinearLayout) this.findViewById(R.id.normal_tip);
        this.dynamicTxt = (TextView) this.findViewById(R.id.error_text);
        this.phoneNum = (EditText) this.findViewById(R.id.email);
        this.passwordEt = (EditText) this.findViewById(R.id.password);
        this.iv = (ImageView) this.findViewById(R.id.loading);
        this.registBtn = (RelativeLayout) this.findViewById(R.id.regist);

        this.titleTxt.setText("邮箱用户注册");
        this.registBtn.setEnabled(false);
        this.rightBtn.setText(R.string.sapi_login);
        this.dynamicTag.setVisibility(View.GONE);
        this.rightBtn.setVisibility(View.GONE);
        this.iv.setVisibility(View.INVISIBLE);

        this.registBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);

        this.passwordEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtils.isEmpty(EmailRegisterActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(EmailRegisterActivity.this.phoneNum.getText().toString())) {
                    EmailRegisterActivity.this.registBtn.setEnabled(false);
				} else {
                    EmailRegisterActivity.this.registBtn.setEnabled(true);
				}
			}
		});

        this.phoneNum.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (StringUtils.isEmpty(EmailRegisterActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(EmailRegisterActivity.this.phoneNum.getText().toString())) {
                    EmailRegisterActivity.this.registBtn.setEnabled(false);
				} else {
                    EmailRegisterActivity.this.registBtn.setEnabled(true);
				}
			}
		});
	}

	private void initPath() {
        this.m_iniFileIO = new IniFile();
        this.webRoot = UIHelper.getSoftPath(this);
		if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
		}
        this.webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        this.webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", this.webRoot);
		if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
		}
		String WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appFile = this.webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appFile;
        if(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn_left:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.title_btn_right:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.regist:
			if (!StringUtils.checkEmail(this.phoneNum.getText().toString())) {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_email_format_error);
				return;
			} else if (!StringUtils.checkPassWord(this.passwordEt.getText()
					.toString())) {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_password_format_error);
				return;
			}
            this.connect();
			break;
		}
	}

	public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.iv.setVisibility(View.VISIBLE);
	}

	public void stopAnimation() {
		// loadingAnima.stop();
        this.iv.setVisibility(View.INVISIBLE);
	}

	public Map<String, String> login() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "registerByEMail");
		params.put("password", this.passwordEt.getText().toString());
		params.put("email", this.phoneNum.getText().toString());
		return params;
	}

	private void connect() {
		EmailRegisterActivity.MyAsyncTask task = new EmailRegisterActivity.MyAsyncTask(this);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

		Context context;
		int count;
		private String retStr;
		private String retEmail;

		public MyAsyncTask(Context c) {
            context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
            EmailRegisterActivity.this.startAnimation();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(Integer... params) {
            EmailRegisterActivity.this.initPath();
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ EmailRegisterActivity.this.m_iniFileIO.getIniString(EmailRegisterActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ EmailRegisterActivity.this.m_iniFileIO.getIniString(EmailRegisterActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0)
					+ EmailRegisterActivity.this.m_iniFileIO.getIniString(EmailRegisterActivity.this.userIni, "Login", "ebsPath",
							"/EBS/UserServlet", (byte) 0);
			return connection.asyncConnect(constants.verifyURL, EmailRegisterActivity.this.login(), HttpConnectionUtil.HttpMethod.POST,
					EmailRegisterActivity.this);
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            EmailRegisterActivity.this.stopAnimation();
			if (result != null) {
				JSONObject json;
				try {
					json = new JSONObject(result);
                    this.retStr = json.getString("regMsg");
                    this.retEmail = json.getString("sendMsg");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (this.retStr != null && this.retEmail != null) {
					if (this.retStr.equals("注册成功") && this.retEmail.equals("邮件发送成功")) {
						UIHelper.setSharePerference(this.context, constants.SAVE_LOCALMSGNUM,
								"username", EmailRegisterActivity.this.phoneNum.getText().toString());
						UIHelper.setSharePerference(this.context, constants.SAVE_LOCALMSGNUM,
								"password", EmailRegisterActivity.this.passwordEt.getText().toString());
						Intent intent = new Intent();
						intent.putExtra("phoneNum", EmailRegisterActivity.this.phoneNum.getText()
								.toString());
						intent.putExtra("action", "点击邮件中的确认链接来完成注册");
						intent.putExtra("title", "激活");
						intent.setClass(this.context, EmailCodeActivity.class);
                        EmailRegisterActivity.this.startActivity(intent);
					}
				} else {
                    EmailRegisterActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    EmailRegisterActivity.this.dynamicTxt.setText(result);
				}
			} else {
                EmailRegisterActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                EmailRegisterActivity.this.dynamicTxt.setText(this.context.getString(R.string.sapi_login_error));
			}
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}
}