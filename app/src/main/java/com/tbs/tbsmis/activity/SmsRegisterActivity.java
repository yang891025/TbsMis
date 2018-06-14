package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ShowToast")
public class SmsRegisterActivity extends Activity implements View.OnClickListener {
	private ImageView backBtn;
	private Button rightBtn;
	private ImageView iv;
	private Button resentBtn;
	private RelativeLayout loginBtn;
	private TextView dynamicTxt;
	private LinearLayout dynamicTag;
	private TextView phoneTxt;
	protected int i;
	private TextView titleTxt;
	private IniFile m_iniFileIO;
	private String webRoot;
	private String appFile;
	private TextView done_text;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_sms_register);
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
        this.done_text = (TextView) this.findViewById(R.id.done_text);
        this.loginBtn = (RelativeLayout) this.findViewById(R.id.done);
        this.resentBtn = (Button) this.findViewById(R.id.resent);
        this.iv = (ImageView) this.findViewById(R.id.loading);

        this.rightBtn.setText(R.string.sapi_regist_btn_text);
        this.iv.setVisibility(View.INVISIBLE);
        this.phoneTxt.setVisibility(View.INVISIBLE);
        this.dynamicTag.setVisibility(View.GONE);
        this.titleTxt.setText(R.string.sapi_sms_regist);
        this.loginBtn.setEnabled(false);
        this.resentBtn.setEnabled(false);

        this.loginBtn.setOnClickListener(this);
        this.resentBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.rightBtn.setOnClickListener(this);
        this.rightBtn.setVisibility(View.GONE);
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

	private AnimationDrawable loadingAnima;
	public String mobile;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title_btn_left:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.resent:
            this.dynamicTag.setVisibility(View.GONE);
            this.connect(1);
			break;
		case R.id.done:
			if (this.isTabletDevice()) {
				Intent intent = new Intent();
				intent.setClass(this, LoginActivity.class);
                this.startActivity(intent);
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			} else {
				Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
				sendIntent.setData(Uri.parse("smsto:" + this.mobile));
				sendIntent.putExtra("sms_body", "zc:");
                this.startActivity(sendIntent);
			}
			break;
		case R.id.title_btn_right:
			Intent intent = new Intent();
			intent.setClass(this, LoginActivity.class);
            this.startActivity(intent);
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
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
		params.put("act", "getSMSMobile");
		return params;
	}

	private void connect(int count) {
		SmsRegisterActivity.MyAsyncTask task = new SmsRegisterActivity.MyAsyncTask(this, count);
		task.execute();
	}

	class MyAsyncTask extends AsyncTask<String, Integer, String> {

		Context context;
		int count;
		private String retStr;

		public MyAsyncTask(Context c, int count) {
            context = c;
			this.count = count;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
            SmsRegisterActivity.this.startAnimation();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected String doInBackground(String... params) {
            SmsRegisterActivity.this.initPath();
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ SmsRegisterActivity.this.m_iniFileIO.getIniString(SmsRegisterActivity.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ SmsRegisterActivity.this.m_iniFileIO.getIniString(SmsRegisterActivity.this.userIni, "Login", "ebsPort",
							"8083", (byte) 0) + "/EBS/UserSMSServlet";
			if (this.count == 1) {
				return connection.asyncConnect(constants.verifyURL,
                        SmsRegisterActivity.this.sendverify(), HttpConnectionUtil.HttpMethod.POST, this.context);
			}
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(String result) {
            SmsRegisterActivity.this.stopAnimation();
			if (result != null) {
				JSONObject json;
				try {
					json = new JSONObject(result);
                    this.retStr = json.getString("msg");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
                    this.retStr = result;
				}
				try {
					json = new JSONObject(result);
                    SmsRegisterActivity.this.mobile = json.getString("mobile");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (this.retStr.equals("成功")) {
                    SmsRegisterActivity.this.loginBtn.setEnabled(true);
                    SmsRegisterActivity.this.phoneTxt.setVisibility(View.VISIBLE);
					if (SmsRegisterActivity.this.isTabletDevice()) {
						String birth = SmsRegisterActivity.this.getResources().getString(
                                R.string.sapi_sms_sent_error);
                        SmsRegisterActivity.this.phoneTxt.setText(String.format(birth, "“zc:+用户名”，到"
								+ SmsRegisterActivity.this.mobile));
					} else {
                        SmsRegisterActivity.this.done_text.setText(R.string.send_label);
                        SmsRegisterActivity.this.rightBtn.setVisibility(View.VISIBLE);
						String birth = SmsRegisterActivity.this.getResources().getString(
                                R.string.sapi_sms_sent_to);
                        SmsRegisterActivity.this.phoneTxt.setText(String.format(birth, "“zc:+用户名”，到"
								+ SmsRegisterActivity.this.mobile));
					}

				} else {
                    SmsRegisterActivity.this.resentBtn.setEnabled(true);
                    SmsRegisterActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                    SmsRegisterActivity.this.dynamicTxt.setText(this.retStr);
				}
			} else {
                SmsRegisterActivity.this.dynamicTag.setVisibility(View.VISIBLE);
                SmsRegisterActivity.this.dynamicTxt
						.setText(this.context.getString(R.string.sapi_login_error));
			}
			//
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	public boolean isTabletDevice() {
		TelephonyManager telephony = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		int type = telephony.getPhoneType();
        return type == TelephonyManager.PHONE_TYPE_NONE;
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}
}