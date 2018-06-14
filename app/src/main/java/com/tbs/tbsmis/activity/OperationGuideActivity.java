package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

@SuppressLint("SetJavaScriptEnabled")
public class OperationGuideActivity extends Activity {
	private WebView webview;
	private TextView title;
	private ImageView downBtn;
	private ImageView finishBtn;
	private IniFile m_iniFileIO;
	private String appTestFile;
    private String userIni;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.new_help);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	@SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
	private void init() {
        this.m_iniFileIO = new IniFile();
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.webview = (WebView) this.findViewById(R.id.help_webview);
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
        this.appTestFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appTestFile;
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        this.webview.getSettings().setDefaultTextEncodingName("gb2312");
		// webview.getSettings().setSavePassword(false);
		// webview.getSettings().setSaveFormData(false);
        this.webview.getSettings().setJavaScriptEnabled(true);
		// webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		// 设置可以支持缩放
        this.webview.getSettings().setSupportZoom(true);
		// 设置默认缩放方式
		// webview.setScrollBarStyle(View.SCROLLBAR_POSITION_DEFAULT);
        this.webview.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
		}
		// 设置出现缩放工具
        this.webview.getSettings().setBuiltInZoomControls(true);
        this.webview.getSettings().setLoadWithOverviewMode(true);
        this.webview.getSettings().setUseWideViewPort(true);
		// 支持flash
		//webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
		// ʵ�����ƶ���
        this.webview.clearCache(true);
        this.webview.setClickable(true);
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        this.webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;

			}
			// 出错处理
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				String strVal = "file:///android_asset/error.html";
				view.loadUrl(strVal);
			}
		});
		String operationUrl = this.m_iniFileIO.getIniString(this.userIni, "Software",
				"operationGuidePath", "/tbsapp/help/help.html", (byte) 0);
		String Url;
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Software",
                "softwareAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "Software",
                "softwarePort", constants.DefaultServerPort, (byte) 0);
		Url = "http://" + ipUrl + ":" + portUrl;
		String tempUrl =  StringUtils.isUrl(Url+operationUrl,Url,null);
        this.webview.loadUrl(tempUrl);
        this.title.setText(R.string.onlinehelp);
        this.finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                OperationGuideActivity.this.finish();
                OperationGuideActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
        this.downBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                OperationGuideActivity.this.finish();
                OperationGuideActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
		}
		return true;
	}
}