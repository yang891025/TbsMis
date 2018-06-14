package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class MyResourceActivity extends Activity implements View.OnTouchListener,
        View.OnClickListener, GestureDetector.OnGestureListener
{
    private WebView webview;
    private RelativeLayout loadingIV;
    private ImageView iv;

    private AnimationDrawable loadingAnima;
    private boolean loadingDialogState;
    private String tempUrl;
    private ImageView backBtn;
    private TextView webtitle;
    private IniFile m_iniFileIO;
    private String appNewsFile;
    private ImageView menuBtn;
    private PopupWindow moreWindow2;
    protected boolean isOpenPop;
    private RelativeLayout home;
    private RelativeLayout go;
    private RelativeLayout goBack;
    private RelativeLayout refresh;
    private RelativeLayout favorite;
    private RelativeLayout share;
    private RelativeLayout open;
    private GestureDetector mGestureDetector;
    private RelativeLayout setup;
    private RelativeLayout cloudTitle;
    private String userIni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mycloud_activity);
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @SuppressWarnings("deprecation")
    public void init() {
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.webtitle = (TextView) this.findViewById(R.id.title_tv);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.webview = (WebView) this.findViewById(R.id.webview3);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.mGestureDetector = new GestureDetector(this);
        this.backBtn.setOnClickListener(this);
        this.menuBtn.setOnClickListener(this);
        this.webtitle.setText("我的资源");
        this.m_iniFileIO = new IniFile();
        this.iniPath();

        /**
         * webview��������
         */
        this.webview.getSettings().setSavePassword(false);
        this.webview.getSettings().setSaveFormData(false);
        this.webview.getSettings().setDefaultTextEncodingName("gb2312");
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
        this.webview.getSettings().setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
        }

        this.webview.getSettings().setSupportMultipleWindows(true);
        this.webview.getSettings().setDomStorageEnabled(true);
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.getSettings().setLoadWithOverviewMode(true);
        // flash支持
        //webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        // webview.getSettings().setAppCacheEnabled(true);
        // webview.getSettings().setAppCacheMaxSize(10240);
        this.webview.clearCache(true);
        this.webview.setClickable(true);
        this.webview.setLongClickable(true);
        this.webview.setOnTouchListener(this);

        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");

        // ȡ�������
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        UIHelper.addJavascript(this, this.webview);
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
        this.webview.setDownloadListener(UIHelper.MyWebViewDownLoadListener(this));
        this.webview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!title.contains("://"))
                    MyResourceActivity.this.webtitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                MyResourceActivity.this.loadingDialogState = progress < 100;
                if (MyResourceActivity.this.loadingDialogState) {
                    MyResourceActivity.this.startAnimation();
                } else {
                    MyResourceActivity.this.stopAnimation();
                }
            }
        });
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        this.webview.loadUrl(this.tempUrl);
        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "load", 0);
    }

    private void iniPath() {
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
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appNewsFile = webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appNewsFile;
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
        if (this.getIntent().getExtras() != null) {
            Intent Intent = this.getIntent();
            int rights = Intent.getIntExtra("rights", 1);
            if (rights == 2) {
                this.tempUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                        "PersonalPath", "/ebs_user/MyHome.cbs", (byte) 0);
            } else if (rights == 3) {
                this.tempUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                        "SystemPath", "/ebs/home.cbs", (byte) 0);
            } else if (rights == 4) {
                String appName = this.appNewsFile.substring(0,
                        this.appNewsFile.lastIndexOf("/"));
                appName = appName.substring(appName.lastIndexOf("/") + 1);
                this.tempUrl = "/download/SelectDir.cbs?appName=" + appName;
            } else if (rights == 5) {
                String appName = this.appNewsFile.substring(0,
                        this.appNewsFile.lastIndexOf("/"));
                appName = appName.substring(appName.lastIndexOf("/") + 1);
                this.tempUrl = "/Store/AppInfoInStore.cbs?appName=" + appName;
            } else {
                this.tempUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                        "ResPath", "/ebs_user/MyResource.cbs", (byte) 0);
            }
        }

        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlineAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlinePort", constants.DefaultServerPort, (byte) 0);
        String baseUrl = "http://" + ipUrl + ":" + portUrl;
        this.tempUrl = StringUtils.isUrl(baseUrl + this.tempUrl, baseUrl, null);
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 1) {
            if (this.tempUrl.indexOf("?") != -1) {
                this.tempUrl = this.tempUrl
                        + "&loginId="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "LoginId", "", (byte) 0)
                        + "&UserName="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "Account", "", (byte) 0);
            } else {
                this.tempUrl = this.tempUrl
                        + "?loginId="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "LoginId", "", (byte) 0)
                        + "&UserName="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "Account", "", (byte) 0);
            }
        } else {
            UIHelper.showLoginDialog(this, 0);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
        if (this.webview != null) {
            this.webview.clearCache(true);
            this.webview.setVisibility(View.GONE);
            this.webview.destroy();
            this.webview = null;
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        // return mGestureDetector.onTouchEvent(arg1);
        return this.mGestureDetector.onTouchEvent(arg1);
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

    public void changMorePopState2(View v) {
        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow2(v);
        } else {
            if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow2(View parent) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.mycloud_menu, null);
        this.home = (RelativeLayout) view.findViewById(R.id.home);
        this.go = (RelativeLayout) view.findViewById(R.id.go);
        this.goBack = (RelativeLayout) view.findViewById(R.id.goBack);
        this.refresh = (RelativeLayout) view.findViewById(R.id.refresh);
        // font = (RelativeLayout) view.findViewById(R.R.id.font);
        // background = (RelativeLayout) view.findViewById(R.R.id.background);
        this.favorite = (RelativeLayout) view.findViewById(R.id.favorite);
        this.share = (RelativeLayout) view.findViewById(R.id.share);
        this.setup = (RelativeLayout) view.findViewById(R.id.setup);
        this.open = (RelativeLayout) view.findViewById(R.id.open);
        if (this.webview.canGoBack()) {
            this.goBack.setEnabled(true);
        } else {
            this.goBack.setEnabled(false);
        }
        if (this.webview.canGoForward()) {
            this.go.setEnabled(true);
        } else {
            this.go.setEnabled(false);
        }
        this.home.setOnClickListener(this);
        this.go.setOnClickListener(this);
        this.goBack.setOnClickListener(this);
        this.refresh.setOnClickListener(this);
        this.favorite.setOnClickListener(this);
        this.share.setOnClickListener(this);
        this.setup.setOnClickListener(this);
        this.open.setOnClickListener(this);
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "APPSHOW",
                "open_in_browse", "1", (byte) 0)) == 0){
            open.setVisibility(View.GONE);
        }
        this.moreWindow2 = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                MyResourceActivity.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn2:
                this.finish();
                // overridePendingTransition(R.anim.push_down, R.anim.push_up);
                break;
            case R.id.search_btn2:
                this.changMorePopState2(v);
                break;
            case R.id.home:
                this.iniPath();
                this.webview.loadUrl(this.tempUrl);
                this.moreWindow2.dismiss();
                break;
            case R.id.go:
                this.webview.goForward();
                this.moreWindow2.dismiss();
                break;
            case R.id.goBack:
                this.webview.goBack();
                this.moreWindow2.dismiss();
                break;
            case R.id.refresh:
                this.webview.reload();
                this.moreWindow2.dismiss();
                break;
            case R.id.share:
                JsoupExam.getSearchEngine(2, webview.getUrl(), this);
                this.moreWindow2.dismiss();
                break;
            case R.id.favorite:
                JsoupExam.getSearchEngine(1, webview.getUrl(), this);
                this.moreWindow2.dismiss();
                break;
            case R.id.setup:
                Intent intent = new Intent();
                intent = new Intent();
                intent.setClass(this, SetUpActivity.class);
                this.startActivity(intent);
                this.moreWindow2.dismiss();
                break;
            case R.id.open:
                UIHelper.showBrowsere(this, webview.getUrl());
                this.moreWindow2.dismiss();
                break;
        }
    }

    @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this.webview.canGoBack()) {
                this.webview.goBack();
            } else {
                this.onBackPressed();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
            }

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            this.menuBtn.callOnClick();
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float diffX = e1.getX() - e2.getX();
        float diffY = e1.getY() - e2.getY();
        // TODO Auto-generated method stub
        if (diffY > this.webview.getHeight() / 5 && Math.abs(velocityX) > 30) {
            this.webview.loadUrl("javascript:jump()");
        } else if (diffY < -(this.webview.getHeight() / 5)
                && Math.abs(velocityX) > 30) {
            this.webview.loadUrl("javascript:OnUpdate()");
        } else if (diffX > this.webview.getWidth() / 5 && Math.abs(diffX) > Math.abs(diffY)
                && Math.abs(velocityX) > 20) {
            this.webview.loadUrl("javascript:tonext()");
            this.webview.loadUrl("javascript:jumppage(3)");
        } else if (diffX < -(this.webview.getWidth() / 5) && Math.abs(diffX) > Math.abs(diffY)
                && Math.abs(velocityX) > 20) {
            this.webview.loadUrl("javascript:toprev()");
            this.webview.loadUrl("javascript:jumppage(2)");
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

}
