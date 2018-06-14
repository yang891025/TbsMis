package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.util.DES;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private Button dynamicBtn;
	private Button findPwdBtn;
	private RelativeLayout loginBtn;
	private LinearLayout dynamicTag;
	private TextView dynamicTxt;
	private EditText userEt;
	private EditText passwordEt;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	public String retStr, retLogid;
	private String appTestFile;
	private IniFile IniFile;
	private TextView titleTxt;
	private Button activateBtn;
	private CheckBox rememberCheckBox;
	private CheckBox autoCheckBox;
	private RelativeLayout auto_login;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_login);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.title_btn_left);
        this.rightBtn = (Button) this.findViewById(R.id.title_btn_right);
        this.titleTxt = (TextView) this.findViewById(R.id.title);

        this.findPwdBtn = (Button) this.findViewById(R.id.account_forget_password);
        this.dynamicBtn = (Button) this.findViewById(R.id.account_dynamic_login);
        this.activateBtn = (Button) this.findViewById(R.id.account_activate);
        this.loginBtn = (RelativeLayout) this.findViewById(R.id.account_login);
        this.auto_login = (RelativeLayout) this.findViewById(R.id.auto_login);
        this.dynamicTag = (LinearLayout) this.findViewById(R.id.account_tip);
        this.dynamicTxt = (TextView) this.findViewById(R.id.account_error_text);
        this.userEt = (EditText) this.findViewById(R.id.account);
        this.passwordEt = (EditText) this.findViewById(R.id.account_password);
        this.iv = (ImageView) this.findViewById(R.id.account_loading);
        this.rememberCheckBox = (CheckBox) this.findViewById(R.id.account_remember_checkBox);
        this.autoCheckBox = (CheckBox) this.findViewById(R.id.account_auto_checkBox);

        this.dynamicTag.setVisibility(View.GONE);
        this.iv.setVisibility(View.INVISIBLE);
        this.titleTxt.setText("账号登录");
        this.loginBtn.setEnabled(false);
        this.rightBtn.setText(R.string.sapi_regist);

        this.rememberCheckBox.setOnClickListener(this);
        this.autoCheckBox.setOnClickListener(this);
        this.loginBtn.setOnClickListener(this);
        this.findPwdBtn.setOnClickListener(this);
        this.dynamicBtn.setOnClickListener(this);
        this.activateBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);

        this.userEt.addTextChangedListener(new TextWatcher() {
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
				if (StringUtils.isEmpty(LoginActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(LoginActivity.this.userEt.getText().toString())) {
                    LoginActivity.this.loginBtn.setEnabled(false);
				} else {
                    LoginActivity.this.loginBtn.setEnabled(true);
				}
			}
		});

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
				if (StringUtils.isEmpty(LoginActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(LoginActivity.this.userEt.getText().toString())) {
                    LoginActivity.this.loginBtn.setEnabled(false);
				} else {
                    LoginActivity.this.loginBtn.setEnabled(true);
				}
			}
		});
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
        this.IniFile = new IniFile();
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appTestFile = webRoot
				+ this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appTestFile;
        if(Integer.parseInt(this.IniFile.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
        this.userEt.setText(this.IniFile.getIniString(this.userIni, "Login", "UserName",
				"", (byte) 0));
        this.userEt.setSelection(this.userEt.getText().toString().length());// 光标位置
		if (Integer.parseInt(this.IniFile.getIniString(this.userIni, "Login",
				"rememberPwd", "0", (byte) 0)) == 1) {
            this.rememberCheckBox.setChecked(true);
            this.passwordEt.setText(this.IniFile.getIniString(this.userIni, "Login",
					"PassWord", "", (byte) 0));
		}
		if (Integer.parseInt(this.IniFile.getIniString(this.userIni, "Login",
				"autoLogin", "0", (byte) 0)) == 1) {
            this.autoCheckBox.setChecked(true);
		}
		if (Integer.parseInt(this.IniFile.getIniString(userIni, "Login",
				"autoLoginShow", "1", (byte) 0)) == 0) {
            this.auto_login.setVisibility(View.GONE);
		}
	}

	// Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onRestart() {
		super.onRestart();
		String userTxt = UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "username", "");
		String passwordTxt = UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "password", "");
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "mobile",
				"");
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "email",
				"");
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
				"username", "");
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
				"password", "");
		if (!StringUtils.isEmpty(userTxt) && !StringUtils.isEmpty(passwordTxt)) {
            this.userEt.setText(userTxt);
            this.passwordEt.setText(passwordTxt);
            this.connect();
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn_left:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.account_remember_checkBox:
			if (this.rememberCheckBox.isChecked()) {
                this.IniFile.writeIniString(this.userIni, "Login", "rememberPwd", "1");
				// rememberCheckBox.setChecked(false);
			} else {
                this.IniFile.writeIniString(this.userIni, "Login", "rememberPwd", "0");
				// rememberCheckBox.setChecked(true);
			}
			break;
		case R.id.account_auto_checkBox:
			if (this.autoCheckBox.isChecked()) {
                this.IniFile.writeIniString(this.userIni, "Login", "autoLogin", "1");
				// autoCheckBox.setChecked(false);
			} else {
                this.IniFile.writeIniString(this.userIni, "Login", "autoLogin", "0");
				// autoCheckBox.setChecked(true);
			}
			break;
		case R.id.account_dynamic_login:
			Intent intent = new Intent();
			intent.setClass(this, DynamicLoginActivity.class);
            this.startActivity(intent);
			break;
		case R.id.account_forget_password:
			Intent intent2 = new Intent();
			intent2.setClass(this, FindPasswordActivity.class);
            this.startActivity(intent2);
			break;
		case R.id.account_activate:
			Intent intent3 = new Intent();
			intent3.setClass(this, ActivateAccountActivity.class);
            this.startActivity(intent3);
			break;
		case R.id.title_btn_right:
			Intent intent1 = new Intent();
			intent1.setClass(this, RegisterActivity.class);
            this.startActivity(intent1);
			break;
		case R.id.account_login:
			if (!StringUtils.checkUserName(this.userEt.getText().toString())) {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_username_format_error);
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
        this.loadingAnima.stop();
        this.iv.setVisibility(View.INVISIBLE);
	}

	public Map<String, String> login() {
		String pmt = DES.encrypt("password=" + this.passwordEt.getText());
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "login");
		params.put("account", this.userEt.getText().toString());
		params.put("clientId", UIHelper.DeviceMD5ID(this));
		params.put("password", this.passwordEt.getText().toString());
		params.put("pmt", pmt);
		return params;
	}

	private void connect() {
		LoginActivity.MyAsyncTask task = new LoginActivity.MyAsyncTask(this);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		Context context;
		int count;
		private String user;

		public MyAsyncTask(Context c) {
            context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
            LoginActivity.this.startAnimation();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(String... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ LoginActivity.this.IniFile.getIniString(LoginActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ LoginActivity.this.IniFile.getIniString(LoginActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0)
					+ LoginActivity.this.IniFile.getIniString(LoginActivity.this.userIni, "Login", "ebsPath",
							"/EBS/UserServlet", (byte) 0);
			//System.out.println(constants.verifyURL + ":" + login().toString());
			return connection.asyncConnect(constants.verifyURL, LoginActivity.this.login(),
					HttpConnectionUtil.HttpMethod.POST, LoginActivity.this);
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            LoginActivity.this.stopAnimation();
			System.out.println("result=" + result);
			if (result != null) {
				JSONObject json;
				try {
					json = new JSONObject(result);
                    LoginActivity.this.retStr = json.getString("msg");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
                    LoginActivity.this.retStr = result;
				}
				try {
					json = new JSONObject(result);
                    LoginActivity.this.retLogid = json.getString("loginId");
                    this.user = json.getString("user");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
                    LoginActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    LoginActivity.this.dynamicTxt
                            .setText(this.context.getString(R.string.sapi_login_error_msg));
				}
				if (LoginActivity.this.retStr != null && !StringUtils.isEmpty(LoginActivity.this.retLogid)) {
					if (this.user != null) {
						try {
							if (LoginActivity.this.retStr.equals("登录成功")) {
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"UserName", LoginActivity.this.userEt.getText().toString());
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"PassWord", LoginActivity.this.passwordEt.getText()
												.toString());
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"LoginId", LoginActivity.this.retLogid);
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"LoginFlag", "1");
								json = new JSONObject(this.user);
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"NickName", json.getString("userName"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"newEMail", json.getString("newEMail"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Mobile", json.getString("mobile"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Contact", json.getString("email"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Account", json.getString("account"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"UserCode", json.getString("userCode"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Signature",
										json.getString("idiograph"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Sex", json.getString("sex"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Address", json.getString("myURL"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Location_province",
										json.getString("province"));
                                LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
										"Location_city", json.getString("city"));
								if (StringUtils.isEmpty(json
										.getString("userCodeModifyNum"))) {
                                    LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
											"AccountFlag", "0");
								} else {
                                    LoginActivity.this.IniFile.writeIniString(LoginActivity.this.userIni, "Login",
											"AccountFlag", "1");
								}
								if (UIHelper
										.getShareperference(
                                                this.context,
												Constants.SHARED_PREFERENCE_NAME,
												Constants.SETTINGS_NOTIFICATION_ENABLED,
												true)) {
									ServiceManager serviceManager = new ServiceManager(
											LoginActivity.this);
									serviceManager
											.setNotificationIcon(R.drawable.notification);
									serviceManager.setUserInfo(LoginActivity.this.IniFile
											.getIniString(LoginActivity.this.userIni, "Login",
													"Account", "", (byte) 0),
											DES.encrypt(LoginActivity.this.IniFile.getIniString(
                                                    LoginActivity.this.userIni, "Login",
													"PassWord", "", (byte) 0)),
                                            LoginActivity.this.IniFile.getIniString(LoginActivity.this.userIni,
													"Login", "LoginId", "",
													(byte) 0));
									serviceManager.restartService();
								}
								if (Integer.parseInt(LoginActivity.this.IniFile.getIniString(
                                        userIni, "Login", "UserUpate",
										"1", (byte) 0)) == 1) {
//                                    LoginActivity.this.startService(new Intent(
//                                            getString(string.ServerName1)));
                                    Intent mIntent = new Intent();
                                    mIntent.setAction(context
                                            .getString(R.string.ServerName1));//你定义的service的action
                                    mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                    context.startService(mIntent);
								}
                                LoginActivity.this.startActivity(new Intent(LoginActivity.this,
                                        SetUpActivity.class));
                                Intent intent = new Intent();
                                intent.setAction("login"
                                        + this.context.getString(R.string.about_title));
                                LoginActivity.this.sendBroadcast(intent);
                                LoginActivity.this.finish();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					//System.out.println("dynamicTag");
                    LoginActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    LoginActivity.this.dynamicTxt.setText(LoginActivity.this.retStr);
				}
			} else {
                LoginActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                LoginActivity.this.dynamicTxt
						.setText(this.context.getString(R.string.sapi_login_error));
			}
			//
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}
}