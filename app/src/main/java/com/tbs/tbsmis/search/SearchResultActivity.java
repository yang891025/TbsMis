package com.tbs.tbsmis.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

@SuppressLint("SetJavaScriptEnabled")
public class SearchResultActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "SearchResultActivity";
    private ImageView back_btn;
    private EditText news_search_edit;
    private WebView search_result_webview;
    private WebSettings Settings;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private IniFile m_iniFileIO;
    private String baseUrl;
    public String tempUrl;
    private String userIni;
    private int CurrentSearch;
    private String searchWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_search_result);
        Log.i(SearchResultActivity.TAG, "onCreate");
        MyActivity.getInstance().addActivity(this);
        if (this.getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            CurrentSearch = intent.getIntExtra("currentSearch", 0);
            searchWord = intent.getStringExtra("searchWord");
            this.init();
        }
    }

    private void init() {
        // TODO Auto-generated method stub
        this.back_btn = (ImageView) this.findViewById(R.id.tv_back);
        this.news_search_edit = (EditText) this.findViewById(R.id.et_search_keyword);
        this.search_result_webview = (WebView) this.findViewById(R.id.search_result_webview);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.back_btn.setOnClickListener(this);
        news_search_edit.setOnClickListener(this);
        news_search_edit.setText(searchWord);
        this.initWebview();
        this.initPath();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.loadingIV.setVisibility(View.INVISIBLE);
        String search_Name = this.m_iniFileIO.getIniString(this.userIni, "search",
                "search" + CurrentSearch, "", (byte) 0);
        String SearchURL = m_iniFileIO.getIniString(userIni,
                search_Name, "SearchURL", "", (byte) 0);
        if(!StringUtils.isEmpty(SearchURL)) {
            String tempUrl = StringUtils.isUrl(this.m_iniFileIO.getIniString(this.userIni,
                    search_Name, "SearchURL", "", (byte) 0), this.baseUrl, UIHelper
                    .getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));

            search_result_webview.loadUrl(tempUrl + "?tword=" + searchWord);
        }else{
            search_result_webview.loadData("<div style=\"padding-top:20px;text-align:center\">\n" +
                    "\t<div style=\"background:url(img/Warning.png) no-repeat center bottom;height:72px;" +
                    "margin-bottom:10px\"></div>\n" +
                    "\t<h6 style=\"font-size: 18px;line-height: 30px;\">您没有配置检索功能</h6>\n" +
                    "</div>","text/html; charset=UTF-8", null);
        }

    }

    private void initPath() {
        // TODO Auto-generated method stub
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
        userIni = webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        //userIni = userIni;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String ipUrl = this.m_iniFileIO.getIniString(userIni, "search",
                "searchAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(userIni, "search",
                "searchPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
    }

    @SuppressWarnings("deprecation")
    private void initWebview() {
        // TODO Auto-generated method stub
        // 检索结果webview初始化
        this.Settings = this.search_result_webview.getSettings();
        this.Settings.setSupportZoom(true);
        this.Settings.setBuiltInZoomControls(false);
        this.Settings.setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.Settings.setDisplayZoomControls(false);
        }
        //Settings.setPluginsEnabled(true);
        this.Settings.setPluginState(WebSettings.PluginState.ON);
        this.Settings.setSupportMultipleWindows(true);
        this.Settings.setJavaScriptCanOpenWindowsAutomatically(true);
        this.Settings.setDomStorageEnabled(true);
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = search_result_webview.getSettings().getUserAgentString();
        search_result_webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        UIHelper.addJavascript(this, this.search_result_webview);
        this.search_result_webview.setWebViewClient(UIHelper.getWebViewClient());
        this.search_result_webview.setWebChromeClient(new WebChromeClient()
        {
            private boolean loadingDialogState;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                this.loadingDialogState = progress < 100;

                if (this.loadingDialogState) {
                    SearchResultActivity.this.startAnimation();
                } else {
                    SearchResultActivity.this.stopAnimation();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        Log.i(SearchResultActivity.TAG, "onStart");
        super.onStart();
    }

    public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        // loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onDestroy() {
        Log.i(SearchResultActivity.TAG, "onDestroy");
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.tv_back:
                this.finish();
                break;
            case R.id.et_search_keyword:
                this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (this.search_result_webview.canGoBackOrForward(-2)) {
                this.search_result_webview.goBack();
            } else {
                this.finish();
            }
        }
        return false;
    }
}