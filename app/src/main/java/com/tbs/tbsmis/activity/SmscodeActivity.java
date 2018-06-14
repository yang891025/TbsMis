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

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ShowToast")
public class SmscodeActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private ImageView iv;
	private EditText verifyNum;
	private Button resentBtn;
	private RelativeLayout loginBtn;
	private String phoneNum;
	private TextView dynamicTxt;
	private LinearLayout dynamicTag;
	private TextView phoneTxt;
	protected int i;
	private TextView titleTxt;
	private IniFile m_iniFileIO;
	private String webRoot;
	private String appFile;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_smscode);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.backBtn = (ImageView) this.findViewById(R.id.title_btn_left);
        this.rightBtn = (Button) this.findViewById(R.id.title_btn_right);
        this.titleTxt = (TextView) this.findViewById(R.id.title);

        this.dynamicTag = (LinearLayout) this.findViewById(R.id.error_tip);
        this.dynamicTxt = (TextView) this.findViewById(R.id.error_text);
        this.phoneTxt = (TextView) this.findViewById(R.id.smscode_tip);
        this.loginBtn = (RelativeLayout) this.findViewById(R.id.done);
        this.resentBtn = (Button) this.findViewById(R.id.resent);
        this.verifyNum = (EditText) this.findViewById(R.id.smscode);
        this.iv = (ImageView) this.findViewById(R.id.loading);

        this.rightBtn.setText(R.string.sapi_regist_btn_text);
        this.iv.setVisibility(View.INVISIBLE);
        this.dynamicTag.setVisibility(View.GONE);
        this.loginBtn.setEnabled(false);
        this.resentBtn.setEnabled(false);

        this.loginBtn.setOnClickListener(this);
        this.resentBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.rightBtn.setVisibility(View.GONE);

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
				if (StringUtils.isEmpty(SmscodeActivity.this.verifyNum.getText().toString())) {
                    SmscodeActivity.this.loginBtn.setEnabled(false);
				} else {
                    SmscodeActivity.this.loginBtn.setEnabled(true);
				}
			}
		});

		if (this.getIntent().getExtras() != null) {
			Intent Intent = this.getIntent();
            this.phoneNum = Intent.getStringExtra("phoneNum");
            this.titleTxt.setText(Intent.getStringExtra("title"));
            this.phoneTxt.setText(this.phoneNum);
		}
        this.connect(1);
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
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		@SuppressLint("NewApi")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
                SmscodeActivity.this.resentBtn.setText("在" + SmscodeActivity.this.i + "s秒后可重发激活码");
				break;
			case 2:
                SmscodeActivity.this.resentBtn.setText(R.string.sapi_smscode_resent);
                SmscodeActivity.this.resentBtn.setEnabled(true);
				break;
			}
		}
	};
	private AnimationDrawable loadingAnima;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn_left:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.resent:
            this.connect(1);
			break;
		case R.id.done:
			if (StringUtils.checkVerify(this.verifyNum.getText().toString())) {
				// 登陆
				if (this.titleTxt.getText().equals("找回密码")) {
                    this.connect(3);
				} else if (this.titleTxt.getText().equals("绑定手机")) {
                    this.connect(4);
				}else if (this.titleTxt.getText().equals("账户激活")) {
                    this.connect(5);
				} else {
                    this.connect(2);
				}
			} else {
                this.dynamicTag.setVisibility(View.VISIBLE);
                this.dynamicTxt.setText(R.string.sapi_smscode_tip);
			}
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

	public Map<String, String> sendverify() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "sendSMSCheckCode");
		params.put("mobile", this.phoneTxt.getText().toString());
		params.put("SMSCHECKCODE_KEY", "aaa");
		return params;
	}

	public Map<String, String> checkverify() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "activateAccount");
		params.put("account", UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "username", ""));
		params.put("smsCheckCode", this.verifyNum.getText().toString());
		params.put("SMSCHECKCODE_KEY", "aaa");
		return params;
	}
	public Map<String, String> activateAccount() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "activateAccount");
		params.put("account", this.phoneTxt.getText().toString());
		params.put("smsCheckCode", this.verifyNum.getText().toString());
		params.put("SMSCHECKCODE_KEY", "aaa");
		return params;
	}
	public Map<String, String> PhonePwd() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "recoverPwdByMobile");
		params.put("mobile", this.phoneTxt.getText().toString());
		params.put("smsCheckCode", this.verifyNum.getText().toString());
		params.put("SMSCHECKCODE_KEY", "aaa");
		return params;
	}

	public Map<String, String> ModifyMobile() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "modifyMobile");
		params.put("account", UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "username", ""));
		params.put("newMobile", this.phoneTxt.getText().toString());
		params.put("smsCheckCode", this.verifyNum.getText().toString());
		params.put("loginId", UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "loginId", ""));
		params.put("SMSCHECKCODE_KEY", "aaa");
		return params;
	}

	private void connect(int count) {
		SmscodeActivity.MyAsyncTask task = new SmscodeActivity.MyAsyncTask(this, count);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		Context context;
		int count;

		public MyAsyncTask(Context c, int count) {
            context = c;
			this.count = count;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
            SmscodeActivity.this.startAnimation();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(String... params) {
            SmscodeActivity.this.initPath();
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ SmscodeActivity.this.m_iniFileIO.getIniString(SmscodeActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ SmscodeActivity.this.m_iniFileIO.getIniString(SmscodeActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0)
					+ SmscodeActivity.this.m_iniFileIO.getIniString(SmscodeActivity.this.userIni, "Login", "ebsPath",
							"/EBS/UserServlet", (byte) 0);
			if (this.count == 1) {
				return connection.asyncConnect(constants.verifyURL, SmscodeActivity.this.sendverify(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			} else if (this.count == 2) {
				return connection.asyncConnect(constants.verifyURL, SmscodeActivity.this.checkverify(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			} else if (this.count == 3) {
				return connection.asyncConnect(constants.verifyURL, SmscodeActivity.this.PhonePwd(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			} else if (this.count == 4) {
				return connection.asyncConnect(constants.verifyURL, SmscodeActivity.this.ModifyMobile(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			}else if (this.count == 5) {
				return connection.asyncConnect(constants.verifyURL, SmscodeActivity.this.activateAccount(), HttpConnectionUtil.HttpMethod.POST,
                        this.context);
			}
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            SmscodeActivity.this.stopAnimation();

			if (result != null) {
				if (result.equals("发送成功")) {
                    SmscodeActivity.this.i = 1;
					new Thread() {
						@Override
						public void run() {
                            SmscodeActivity.this.i = 70;
							while (SmscodeActivity.this.i > 1) {
								try {
									Message msg = new Message();
									msg.what = 1;
                                    SmscodeActivity.this.mHandler.sendMessage(msg);
									Thread.sleep(1000);
                                    SmscodeActivity.this.i--;
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (SmscodeActivity.this.i != 0) {
								Message msg = new Message();
								msg.what = 2;
                                SmscodeActivity.this.mHandler.sendMessage(msg);
							}
						}
					}.start();
				} else if (result.equals("激活成功")) {
                    SmscodeActivity.this.startActivity(new Intent(this.context, LoginActivity.class));
                    SmscodeActivity.this.finish();
				} else if (result.equals("修改手机成功")) {
					UIHelper.setSharePerference(this.context,
							constants.SAVE_LOCALMSGNUM, "mobile", SmscodeActivity.this.phoneTxt
									.getText().toString());
                    SmscodeActivity.this.startActivity(new Intent(this.context,
							MySetupActivity.class));

                    SmscodeActivity.this.finish();
				} else {
                    SmscodeActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    SmscodeActivity.this.dynamicTxt.setText(result);
				}
			} else {
                SmscodeActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                SmscodeActivity.this.dynamicTxt.setText(this.context.getString(R.string.sapi_login_error));
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