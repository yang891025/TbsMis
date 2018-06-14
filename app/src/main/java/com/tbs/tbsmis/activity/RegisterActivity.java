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

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ShowToast")
public class RegisterActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private Button phoneBtn;
	private AnimationDrawable loadingAnima;
	private LinearLayout dynamicTag;
	private TextView dynamicTxt;
	private EditText phoneNum;
	private ImageView iv;
	private RelativeLayout registBtn;
	private TextView passwordEt;
	private TextView userEt;
	private Button emailBtn;
	private TextView titleTxt;
	private IniFile IniFile;
	private String appTestFile;
	private Button smsBtn;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_regist);
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
        this.phoneNum = (EditText) this.findViewById(R.id.phone);
        this.userEt = (EditText) this.findViewById(R.id.username);
        this.passwordEt = (EditText) this.findViewById(R.id.password);
        this.iv = (ImageView) this.findViewById(R.id.loading);
        this.phoneBtn = (Button) this.findViewById(R.id.account_phone_regist);
        this.smsBtn = (Button) this.findViewById(R.id.account_sms_regist);
        this.emailBtn = (Button) this.findViewById(R.id.account_email_regist);
        this.registBtn = (RelativeLayout) this.findViewById(R.id.regist);

        this.titleTxt.setText("普通用户注册");
        this.dynamicTag.setVisibility(View.GONE);
        this.iv.setVisibility(View.INVISIBLE);
        this.registBtn.setEnabled(false);
        this.rightBtn.setText(R.string.sapi_login);

        this.emailBtn.setOnClickListener(this);
        this.smsBtn.setOnClickListener(this);
        this.registBtn.setOnClickListener(this);
        this.phoneBtn.setOnClickListener(this);
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
				if (StringUtils.isEmpty(RegisterActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(RegisterActivity.this.userEt.getText().toString())
						|| StringUtils.isEmpty(RegisterActivity.this.phoneNum.getText().toString())) {
                    RegisterActivity.this.registBtn.setEnabled(false);
				} else {
                    RegisterActivity.this.registBtn.setEnabled(true);
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
				if (StringUtils.isEmpty(RegisterActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(RegisterActivity.this.userEt.getText().toString())
						|| StringUtils.isEmpty(RegisterActivity.this.phoneNum.getText().toString())) {
                    RegisterActivity.this.registBtn.setEnabled(false);
				} else {
                    RegisterActivity.this.registBtn.setEnabled(true);
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
				if (StringUtils.isEmpty(RegisterActivity.this.passwordEt.getText().toString())
						|| StringUtils.isEmpty(RegisterActivity.this.userEt.getText().toString())
						|| StringUtils.isEmpty(RegisterActivity.this.phoneNum.getText().toString())) {
                    RegisterActivity.this.registBtn.setEnabled(false);
				} else {
                    RegisterActivity.this.registBtn.setEnabled(true);
				}
			}
		});
	}

	private void initPath() {
		// TODO Auto-generated method stub
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
			if (!StringUtils.checkUserName(this.userEt.getText().toString())) {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_username_format_error);
				return;
			} else if (!StringUtils.checkPassWord(this.passwordEt.getText()
					.toString())) {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_password_format_error);
				return;
			} else if (!StringUtils.checkPhone(this.phoneNum.getText().toString())) {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_phone_format_error);
				return;
			}
            this.connect();
			break;
		case R.id.account_phone_regist:
			Intent intent1 = new Intent();
			intent1.setClass(this, PhoneRegisterActivity.class);
            this.startActivity(intent1);
			break;
		case R.id.account_email_regist:
			Intent intent2 = new Intent();
			intent2.setClass(this, EmailRegisterActivity.class);
            this.startActivity(intent2);
			break;
		case R.id.account_sms_regist:
			Intent intent3 = new Intent();
			intent3.setClass(this, SmsRegisterActivity.class);
            this.startActivity(intent3);
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
		params.put("act", "register");
		params.put("account", this.userEt.getText().toString());
		params.put("password", this.passwordEt.getText().toString());
		params.put("mobile", this.phoneNum.getText().toString());
		return params;
	}

	private void connect() {
		RegisterActivity.MyAsyncTask task = new RegisterActivity.MyAsyncTask(this);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

		Context context;
		int count;

		public MyAsyncTask(Context c) {
            context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
            RegisterActivity.this.startAnimation();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(Integer... params) {
            RegisterActivity.this.initPath();
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ RegisterActivity.this.IniFile.getIniString(RegisterActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ RegisterActivity.this.IniFile.getIniString(RegisterActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0)
					+ RegisterActivity.this.IniFile.getIniString(RegisterActivity.this.userIni, "Login", "ebsPath",
							"/EBS/UserServlet", (byte) 0);
            System.out.println("constants.verifyURL = "+ constants.verifyURL);
			return connection.asyncConnect(constants.verifyURL, RegisterActivity.this.login(),
					HttpConnectionUtil.HttpMethod.POST, RegisterActivity.this);

		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            RegisterActivity.this.stopAnimation();
			if (result != null) {
				if (result.equals("注册成功")) {
					UIHelper.setSharePerference(this.context,
							constants.SAVE_LOCALMSGNUM, "username", RegisterActivity.this.userEt
									.getText().toString());
					UIHelper.setSharePerference(this.context,
							constants.SAVE_LOCALMSGNUM, "password", RegisterActivity.this.passwordEt
									.getText().toString());
					UIHelper.setSharePerference(this.context,
							constants.SAVE_LOCALMSGNUM, "mobile", RegisterActivity.this.phoneNum
									.getText().toString());
					Intent intent = new Intent();
					intent.putExtra("phoneNum", RegisterActivity.this.phoneNum.getText().toString());
					intent.putExtra("title", "短信验证");
					intent.setClass(this.context, SmscodeActivity.class);
                    RegisterActivity.this.startActivity(intent);
				} else {
                    RegisterActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    RegisterActivity.this.dynamicTxt.setText(result);
				}
			} else {
                RegisterActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                RegisterActivity.this.dynamicTxt
						.setText(this.context.getString(R.string.sapi_login_error));
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