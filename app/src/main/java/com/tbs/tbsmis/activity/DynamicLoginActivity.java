package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

@SuppressLint("ShowToast")
public class DynamicLoginActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private Button sendBtn;
	private EditText verifyNum;
	private EditText phoneNum;
	private AnimationDrawable loadingAnima;
	private ImageView iv;
	private RelativeLayout loginBtn;
	private TextView dynamicTxt;
	protected int i;
	private LinearLayout dynamicTag;
	private IniFile IniFile;
	private String appTestFile;
	private TextView titleTxt;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_dynamic_login);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.title_btn_left);
        this.rightBtn = (Button) this.findViewById(R.id.title_btn_right);
        this.titleTxt = (TextView) this.findViewById(R.id.title);

        this.dynamicTag = (LinearLayout) this.findViewById(R.id.sapi_dynamic_login_ll_error_tips);
        this.dynamicTxt = (TextView) this.findViewById(R.id.sapi_dynamic_login_tv_error_text);
        this.loginBtn = (RelativeLayout) this.findViewById(R.id.sapi_dynamic_login_rl_login);
        this.sendBtn = (Button) this.findViewById(R.id.sapi_dynamic_login_btn_sendsms);
        this.verifyNum = (EditText) this.findViewById(R.id.sapi_dynamic_login_et_smscode);
        this.phoneNum = (EditText) this.findViewById(R.id.sapi_dynamic_login_et_phone);
        this.iv = (ImageView) this.findViewById(R.id.sapi_dynamic_login_iv_login_loading);

        this.iv.setVisibility(View.INVISIBLE);
        this.dynamicTag.setVisibility(View.GONE);
        this.titleTxt.setText("动态登录");
        this.loginBtn.setEnabled(false);
        this.rightBtn.setText(R.string.sapi_regist);

        this.sendBtn.setOnClickListener(this);
        this.loginBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);

        this.verifyNum.addTextChangedListener(new TextWatcher() {
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
				if (StringUtils.isEmpty(DynamicLoginActivity.this.verifyNum.getText().toString())) {
                    DynamicLoginActivity.this.loginBtn.setEnabled(false);
				} else {
                    DynamicLoginActivity.this.loginBtn.setEnabled(true);
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
		if (StringUtils.checkPhone(this.IniFile.getIniString(this.userIni, "Login",
				"Mobile", "", (byte) 0))) {
            this.phoneNum.setText(this.IniFile.getIniString(this.userIni, "Login",
					"Mobile", "", (byte) 0));
		}
	}

	public Map<String, String> sendverify() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "sendSMSCheckCode");
		params.put("mobile", this.phoneNum.getText().toString());
		params.put("SMSCHECKCODE_KEY", "bbb");
		return params;
	}

	public Map<String, String> checkverify() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "loginByDyn");
		params.put("mobile", this.phoneNum.getText().toString());
		params.put("smsCheckCode", this.verifyNum.getText().toString());
		params.put("SMSCHECKCODE_KEY", "bbb");
		return params;
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
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
            this.startActivity(intent);
			break;
		case R.id.sapi_dynamic_login_btn_sendsms:
			if (StringUtils.checkPhone(this.phoneNum.getText().toString())) {
                this.connect(1);
                this.sendBtn.setEnabled(false);
			} else {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_phone_format_error);
			}
			break;
		case R.id.sapi_dynamic_login_rl_login:
			if (StringUtils.checkVerify(this.verifyNum.getText().toString())) {
                this.connect(2);
			} else {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_verifycode_input_error);
			}
			break;
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
                DynamicLoginActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                DynamicLoginActivity.this.dynamicTxt.setText("在" + DynamicLoginActivity.this.i + "s秒后可重发验证码");
				break;
			case 2:
                DynamicLoginActivity.this.sendBtn.setEnabled(true);
                DynamicLoginActivity.this.dynamicTag.setVisibility(View.GONE);
				break;
			}
		}
	};

	public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.iv.setVisibility(View.VISIBLE);
	}

	public void stopAnimation() {
		// loadingAnima.stop();
        this.iv.setVisibility(View.INVISIBLE);
	}

	private void connect(int count) {
		DynamicLoginActivity.MyAsyncTask task = new DynamicLoginActivity.MyAsyncTask(this, count);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		Context context;
		int count;
		private String retStr;
		private String retLogid;

		public MyAsyncTask(Context c, int count) {
            context = c;
			this.count = count;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
            DynamicLoginActivity.this.startAnimation();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(String... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ DynamicLoginActivity.this.IniFile.getIniString(DynamicLoginActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ DynamicLoginActivity.this.IniFile.getIniString(DynamicLoginActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0)
					+ DynamicLoginActivity.this.IniFile.getIniString(DynamicLoginActivity.this.userIni, "Login", "ebsPath",
							"/EBS/UserServlet", (byte) 0);
			if (this.count == 1) {
				return connection.asyncConnect(constants.verifyURL, DynamicLoginActivity.this.sendverify(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			} else if (this.count == 2) {
				return connection.asyncConnect(constants.verifyURL, DynamicLoginActivity.this.checkverify(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			}
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            DynamicLoginActivity.this.stopAnimation();
            DynamicLoginActivity.this.i = 1;
			if (result != null) {
				JSONObject json;
				try {
					json = new JSONObject(result);
                    this.retStr = json.getString("msg");
                    this.retLogid = json.getString("loginId");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (this.retStr != null && !StringUtils.isEmpty(this.retLogid)) {
					if (this.retStr.equals("登录成功")) {
                        IniFile.deleteIniString(userIni,"Login","UserName");
                        IniFile.deleteIniString(userIni,"Login","PassWord");
                        IniFile.deleteIniString(userIni,"Login","LoginId");
                        IniFile.deleteIniString(userIni,"Login","NickName");
                        IniFile.deleteIniString(userIni,"Login","newEMail");
                        IniFile.deleteIniString(userIni,"Login","Mobile");
                        IniFile.deleteIniString(userIni,"Login","Account");
                        IniFile.deleteIniString(userIni,"Login","UserCode");
                        IniFile.deleteIniString(userIni,"Login","Sex");
                        IniFile.deleteIniString(userIni,"Login","AccountFlag");
                        DynamicLoginActivity.this.IniFile.writeIniString(DynamicLoginActivity.this.userIni, "Login", "UserName",
                                DynamicLoginActivity.this.phoneNum.getText().toString());
                        DynamicLoginActivity.this.IniFile.writeIniString(DynamicLoginActivity.this.userIni, "Login", "Account",
                                DynamicLoginActivity.this.phoneNum.getText().toString());
                        DynamicLoginActivity.this.IniFile.writeIniString(DynamicLoginActivity.this.userIni, "Login", "Mobile",
                                DynamicLoginActivity.this.phoneNum.getText().toString());
                        DynamicLoginActivity.this.IniFile.writeIniString(DynamicLoginActivity.this.userIni, "Login", "LoginId",
                                this.retLogid);
                        DynamicLoginActivity.this.IniFile.writeIniString(DynamicLoginActivity.this.userIni, "Login",
								"LoginFlag", "1");
                        DynamicLoginActivity.this.startActivity(new Intent(this.context, SetUpActivity.class));
                        DynamicLoginActivity.this.finish();
					}
				} else if (result.equals("发送成功")) {
					new Thread() {
						@Override
						public void run() {
                            DynamicLoginActivity.this.i = 70;
							while (DynamicLoginActivity.this.i > 1) {
								try {
									Message msg = new Message();
									msg.what = 1;
                                    DynamicLoginActivity.this.mHandler.sendMessage(msg);
									Thread.sleep(1000);
                                    DynamicLoginActivity.this.i--;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (DynamicLoginActivity.this.i != 0) {
								Message msg = new Message();
								msg.what = 2;
                                DynamicLoginActivity.this.mHandler.sendMessage(msg);
							}
						}
					}.start();
				} else {
                    DynamicLoginActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    DynamicLoginActivity.this.dynamicTxt.setText(result);
				}
			} else {
                DynamicLoginActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                DynamicLoginActivity.this.dynamicTxt.setText(this.context.getString(R.string.sapi_login_error));
			}
			//
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