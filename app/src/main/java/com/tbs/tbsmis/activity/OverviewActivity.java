package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class OverviewActivity extends Activity implements View.OnTouchListener,
		View.OnClickListener {
	private WebView webview;
	private RelativeLayout loadingIV;
	private ImageView iv;

	private AnimationDrawable loadingAnima;
	private boolean loadingDialogState;
	private String tempUrl;
	private ImageView backBtn;
	private ImageView finishBtn;
	private String ResName;
	private TextView title;
	private IniFile m_iniFileIO;
	private String userIni;
    private String appNewsFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.overview_activity);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}
	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@SuppressWarnings("deprecation")
	public void init() {
        this.backBtn = (ImageView) this.findViewById(R.id.basic_back_btn);
        this.finishBtn = (ImageView) this.findViewById(R.id.basic_down_btn);
        this.title = (TextView) this.findViewById(R.id.title_tvv);
        this.webview = (WebView) this.findViewById(R.id.webview3);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.backBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
            this.tempUrl = intent.getStringExtra("tempUrl");
            this.ResName = intent.getStringExtra("ResName");
		}
		if (this.tempUrl == null || this.tempUrl.equals("")) {
            this.finish();
			Toast.makeText(this, "请检查参数设置", Toast.LENGTH_SHORT).show();
		}
		System.out.println("tempUrl = "+tempUrl);
        this.title.setText(this.ResName);
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
        appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
		String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"currentPort", constants.DefaultLocalPort, (byte) 0);
		String baseUrl = "http://" + ipUrl + ":" + portUrl;
        this.tempUrl = StringUtils.isUrl(this.tempUrl, baseUrl, UIHelper
				.getShareperference(this, constants.SAVE_LOCALMSGNUM,
						"resname", "yqxx"));
        //System.out.println(tempUrl);
		/**
		 * webview��������
		 */
        this.webview.getSettings().setSavePassword(false);
        this.webview.getSettings().setSaveFormData(false);
        this.webview.getSettings().setJavaScriptEnabled(true);
        this.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// 设置是否可以支持缩放
        this.webview.getSettings().setSupportZoom(true);
		// 缩放工具
        this.webview.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
		}
        this.webview.getSettings().setBuiltInZoomControls(true);
		// 支持满屏
        this.webview.getSettings().setLoadWithOverviewMode(true);
        this.webview.getSettings().setSupportMultipleWindows(true);
        this.webview.getSettings().setDomStorageEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webview.getSettings().setAppCacheEnabled(true);
        this.webview.clearCache(true);
        this.webview.setClickable(true);
        this.webview.setLongClickable(true);
        this.webview.setOnTouchListener(this);
        // 修改ua使得web端正确判断 @ 2015-11-07 11:13:28
        String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		UIHelper.addJavascript(this, this.webview);
//        this.I = new Overview_GestureDetector(this, this.webview);
//        this.mGestureDetector = new GestureDetector(this, I);
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
                OverviewActivity.this.loadingDialogState = progress < 100;
				if (OverviewActivity.this.loadingDialogState) {
                    OverviewActivity.this.startAnimation();
				} else {
                    OverviewActivity.this.stopAnimation();
				}
			}
		});
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        this.webview.loadUrl(this.tempUrl);
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
				"load", 0);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        this.webview.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appNewsFile,
						"BASIC_SETUP", "BackColorValue", "#f6f6f6", (byte) 0)));
		// 3.0版本才有setDisplayZoomControls的功能
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
                this.webview.getSettings().setTextZoom(Integer.parseInt(this.m_iniFileIO.getIniString(
                        this.appNewsFile, "BASIC_SETUP", "TextZoom", "100", (byte) 0)));
            }
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (this.webview != null) {
            this.webview.setVisibility(View.GONE);
		}
        MyActivity.getInstance().finishActivity(this);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		//return this.mGestureDetector.onTouchEvent(arg1);
        return false;
	}

	public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
	}

	public void stopAnimation() {
        this.loadingIV.setVisibility(View.INVISIBLE);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.basic_back_btn:
            this.finish();
			break;
		case R.id.basic_down_btn:
            this.finish();
			break;
		}
	}

	private boolean ViewState() {
		if (this.webview.canGoBack()) {
            this.webview.goBack();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
			if (this.ViewState())
				return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
