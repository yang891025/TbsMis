package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class BrowserActivity extends Activity implements View.OnClickListener,
		View.OnTouchListener {
	private LinearLayout GoBtn, BackBtn;
	private ImageView imageGo, imageBack;
	private WebView wv;
	private String tempUrl;
	private ProgressBar pb;
	private View.OnClickListener listener;
	private LinearLayout CloseBtn;
	private LinearLayout RefreshBtn;
	private IniFile m_iniFileIO;
	private String userIni;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_online_layout);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void init() {

        this.CloseBtn = (LinearLayout) this.findViewById(R.id.news_detail_homeBtn);
        this.GoBtn = (LinearLayout) this.findViewById(R.id.news_detail_goBtn);
        this.BackBtn = (LinearLayout) this.findViewById(R.id.news_detail_backBtn);
        this.RefreshBtn = (LinearLayout) this.findViewById(R.id.news_detail_settingBtn);

        this.imageGo = (ImageView) this.findViewById(R.id.news_detail_goIV);
        this.imageBack = (ImageView) this.findViewById(R.id.news_detail_backIV);

        this.CloseBtn.setOnClickListener(this);
        this.GoBtn.setOnClickListener(this);
        this.BackBtn.setOnClickListener(this);
        this.RefreshBtn.setOnClickListener(this);
        this.wv = (WebView) this.findViewById(R.id.webview_online);
        this.pb = (ProgressBar) this.findViewById(R.id.progressbar_online);
        this.pb.setMax(100);
		if (this.getIntent().getExtras() != null) {
			Intent intent;
			intent = this.getIntent();
            this.tempUrl = intent.getStringExtra("tempUrl");
		}
		if (this.tempUrl == null || this.tempUrl.equals("")) {
            this.finish();
			Toast.makeText(this, "请检查更新设置", Toast.LENGTH_SHORT).show();
		}
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
        String appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
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
		/**
		 * webview��������
		 */
        this.wv.getSettings().setSupportZoom(true);
        this.wv.getSettings().setBuiltInZoomControls(true);
        this.wv.getSettings().setJavaScriptEnabled(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.wv.getSettings().setDisplayZoomControls(true);
		}
        this.wv.getSettings().setUseWideViewPort(true);
        this.wv.getSettings().setLoadWithOverviewMode(true);
        this.wv.getSettings().setSupportMultipleWindows(true);
        this.wv.getSettings().setDomStorageEnabled(true);
		//wv.getSettings().setPluginsEnabled(true);
        this.wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.wv.setOnTouchListener(this);
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.wv.getSettings().getUserAgentString();
        this.wv.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        this.wv.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// 实现下载的跳转
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                BrowserActivity.this.startActivity(intent);
			}
		});
        this.wv.setWebViewClient(UIHelper.getWebViewClient());
        this.wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				// mProgressStatus = progress;
                BrowserActivity.this.pb.setProgress(progress);
				if (progress > 20) {
					if (BrowserActivity.this.wv.canGoBack()) {
                        BrowserActivity.this.imageBack
								.setImageResource(R.drawable.bottombar_btn_unback);
                        BrowserActivity.this.BackBtn.setOnClickListener(BrowserActivity.this.listener);
					} else {
                        BrowserActivity.this.imageBack
								.setImageResource(R.drawable.bottombar_btn_back);
                        BrowserActivity.this.BackBtn.setOnClickListener(null);
					}
					if (BrowserActivity.this.wv.canGoForward()) {
                        BrowserActivity.this.imageGo.setImageResource(R.drawable.bottombar_btn_ungo);
                        BrowserActivity.this.GoBtn.setOnClickListener(BrowserActivity.this.listener);
					} else {
                        BrowserActivity.this.imageGo.setImageResource(R.drawable.bottombar_btn_go);
                        BrowserActivity.this.GoBtn.setOnClickListener(null);
					}
				}
			}
		});
        this.wv.loadUrl(this.tempUrl);
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
				"load", 0);
        this.listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.news_detail_backBtn:
                    BrowserActivity.this.wv.goBack();
					break;
				case R.id.news_detail_goBtn:
                    BrowserActivity.this.wv.goForward();
					break;
				}
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.news_detail_settingBtn:
            this.wv.reload();
			break;
		case R.id.news_detail_homeBtn:
            this.wv.stopLoading();
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		default:
			break;
		}
	}
    @Override
    public void finish() {
//        ViewGroup view = (ViewGroup) getWindow().getDecorView();
//        view.removeAllViews();
        super.finish();
    }
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		if (this.wv != null) {
            this.wv.clearCache(true);
            this.wv.setVisibility(View.GONE);
//            this.wv.destroy();
//            this.wv = null;
		}
        MyActivity.getInstance().finishActivity(this);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.wv.stopLoading();
            this.finish();
			//onBackPressed();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
		}
		return true;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
