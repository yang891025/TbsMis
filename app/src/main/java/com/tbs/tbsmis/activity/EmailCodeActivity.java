package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EmailCodeActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private TextView titleTxt;
	private TextView emailTxt;
	private EditText emailExt;
	private Button resend;
	private Button modify;
	private IniFile m_iniFileIO;
	private String webRoot;
	private String appFile;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.email_code_layout);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.title_btn_left);
        this.rightBtn = (Button) this.findViewById(R.id.title_btn_right);
        this.titleTxt = (TextView) this.findViewById(R.id.title);
        this.resend = (Button) this.findViewById(R.id.email_resend);
        this.modify = (Button) this.findViewById(R.id.email_send);
        this.emailTxt = (TextView) this.findViewById(R.id.email_text);
        this.emailExt = (EditText) this.findViewById(R.id.emailcode);
		if (this.getIntent().getExtras() != null) {
			Intent Intent = this.getIntent();
            this.emailTxt.setText(Intent.getStringExtra("phoneNum"));
            this.titleTxt.setText(Intent.getStringExtra("title"));
		}
        this.rightBtn.setText(R.string.sapi_login);
        this.rightBtn.setVisibility(View.GONE);
        this.modify.setEnabled(false);
        this.backBtn.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);
        this.resend.setOnClickListener(this);
        this.modify.setOnClickListener(this);
        this.emailExt.addTextChangedListener(new TextWatcher() {
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
				if (StringUtils.isEmpty(EmailCodeActivity.this.emailExt.getText().toString())) {
                    EmailCodeActivity.this.modify.setEnabled(false);
				} else {
                    EmailCodeActivity.this.modify.setEnabled(true);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn_left:
            this.startActivity(new Intent(this,
                    SetUpActivity.class));
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.title_btn_right:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.email_resend:
            this.initPath();
            this.connect(1);
			break;
		case R.id.email_send:
			if (StringUtils.checkVerify(this.emailExt.getText().toString())) {
				// 登陆
                this.initPath();
                this.connect(2);
			} else {
				Toast.makeText(this, R.string.sapi_smscode_tip,
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
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

	public Map<String, String> ModifyEmail() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "modifyEMail");
		params.put("account", this.m_iniFileIO.getIniString(this.userIni, "Login",
				"Account", "", (byte) 0));
		params.put("newEMail", this.emailTxt.getText().toString());
		params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
				"LoginId", "", (byte) 0));
		return params;
	}

	public Map<String, String> checkEmail() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "activateByEMailCode");
		params.put("email", this.emailTxt.getText().toString());
		params.put("code", this.emailExt.getText().toString());
		return params;
	}

	private void connect(int count) {
		EmailCodeActivity.MyAsyncTask task = new EmailCodeActivity.MyAsyncTask(this, count);
		task.execute();
	}

	//
	@SuppressLint("ShowToast")
	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		// private static final String TAG = "MyAsyncTask";
		private final Context context;
		private final int action;
		private String retStr;
		private String retEmail;

		public MyAsyncTask(Context c, int action) {
            context = c;
			this.action = action;
		}

		//
		@Override
		protected void onPreExecute() {

		}

		//
		@Override
		protected String doInBackground(String... params) {
            EmailCodeActivity.this.initPath();
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ EmailCodeActivity.this.m_iniFileIO.getIniString(EmailCodeActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ EmailCodeActivity.this.m_iniFileIO.getIniString(EmailCodeActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0)
					+ EmailCodeActivity.this.m_iniFileIO.getIniString(EmailCodeActivity.this.userIni, "Login", "ebsPath",
							"/EBS/UserServlet", (byte) 0);
			switch (this.action) {
			case 1:
				return connection.asyncConnect(constants.verifyURL,
                        EmailCodeActivity.this.ModifyEmail(), HttpConnectionUtil.HttpMethod.POST, this.context);
			case 2:
				return connection.asyncConnect(constants.verifyURL,
                        EmailCodeActivity.this.checkEmail(), HttpConnectionUtil.HttpMethod.POST, this.context);
			}
			return null;
		}

		//
		@Override
		protected void onPostExecute(String result) {
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
					if (this.retStr.equals("修改邮箱成功") && this.retEmail.equals("邮件发送成功")) {
						Toast.makeText(EmailCodeActivity.this, this.retEmail,
								Toast.LENGTH_SHORT).show();
					}
				} else if (result.equals("激活成功") || result.equals("修改成功")) {
					UIHelper.setSharePerference(this.context,
							constants.SAVE_LOCALMSGNUM, "email", EmailCodeActivity.this.emailTxt
									.getText().toString());
					if (EmailCodeActivity.this.titleTxt.getText().toString().equals("激活")) {
                        EmailCodeActivity.this.startActivity(new Intent(EmailCodeActivity.this,
                                SetUpActivity.class));
					} else if (EmailCodeActivity.this.titleTxt.getText().toString().equals("找回密码")) {
                        EmailCodeActivity.this.startActivity(new Intent(EmailCodeActivity.this,
								LoginActivity.class));
					} else if (EmailCodeActivity.this.titleTxt.getText().toString().equals("账户激活")) {
                        EmailCodeActivity.this.startActivity(new Intent(EmailCodeActivity.this,
								LoginActivity.class));
					} else if (EmailCodeActivity.this.titleTxt.getText().toString().equals("修改邮箱")) {
                        EmailCodeActivity.this.startActivity(new Intent(EmailCodeActivity.this,
                                SetUpActivity.class));
					}
                    EmailCodeActivity.this.finish();
				} else {
					Toast.makeText(EmailCodeActivity.this, result,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.titleTxt.getText().toString().equals("激活")) {
                this.startActivity(new Intent(this,
                        SetUpActivity.class));
			} else if (this.titleTxt.getText().toString().equals("找回密码")) {
                this.startActivity(new Intent(this,
						LoginActivity.class));
			}else if (this.titleTxt.getText().toString().equals("账户激活")) {
                this.startActivity(new Intent(this,
						LoginActivity.class));
			} else if (this.titleTxt.getText().toString().equals("修改邮箱")) {
                this.startActivity(new Intent(this,
                        SetUpActivity.class));
			}
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}
}