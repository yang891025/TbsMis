package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.webkit.ValueCallback;
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
import com.tbs.tbsmis.check.LoginDialogResult;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class MySkyDriveActivity extends Activity implements View.OnTouchListener,
        View.OnClickListener, GestureDetector.OnGestureListener
{
    private GestureDetector mGestureDetector;// ������
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
    private RelativeLayout home;
    private RelativeLayout go;
    private RelativeLayout goBack;
    private RelativeLayout refresh;
    // private RelativeLayout font;
    // private RelativeLayout background;
    private RelativeLayout favorite;
    private RelativeLayout share;
    private RelativeLayout open;
    private RelativeLayout setup;
    private boolean isOpenPop;
    private PopupWindow moreWindow2;
    private RelativeLayout skydriveTitle;
    protected ValueCallback<Uri> mUploadMessage;
    private BroadcastReceiver MyBroadCastReceiver;
    protected static final int FILECHOOSER_RESULTCODE = 0;
    private String userIni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mycloud_activity);
        MyActivity.getInstance().addActivity(this);
        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("freshView" + getString(R.string.about_title));
        this.MyBroadCastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (("freshView" + getString(R.string.about_title)).equals(intent.getAction())) {
                    MySkyDriveActivity.this.webview.loadUrl("javascript:delzip()");
                    MySkyDriveActivity.this.webview.reload();
                }
            }
        };
        this.registerReceiver(this.MyBroadCastReceiver, filter);
        this.init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @SuppressWarnings("deprecation")
    public void init() {
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.webtitle = (TextView) this.findViewById(R.id.title_tv);
        this.skydriveTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.webview = (WebView) this.findViewById(R.id.webview3);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.mGestureDetector = new GestureDetector(this);
        this.backBtn.setOnClickListener(this);
        this.menuBtn.setOnClickListener(this);
        this.webtitle.setText("我的云盘");
        this.m_iniFileIO = new IniFile();
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
        this.webview.getSettings().setAppCacheEnabled(true);
        this.webview.getSettings().setAppCacheMaxSize(10240);
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
        this.webview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!title.contains("://"))
                    MySkyDriveActivity.this.webtitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                MySkyDriveActivity.this.loadingDialogState = progress < 100;
                if (MySkyDriveActivity.this.loadingDialogState) {
                    MySkyDriveActivity.this.startAnimation();
                } else {
                    MySkyDriveActivity.this.stopAnimation();
                }
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType) {
                if (MySkyDriveActivity.this.mUploadMessage != null)
                    return;
                MySkyDriveActivity.this.mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MySkyDriveActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
                        SlidingActivity.FILECHOOSER_RESULTCODE);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }
        });
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        this.iniPath();
        this.webview.loadUrl(this.tempUrl);
        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "load", 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == MySkyDriveActivity.FILECHOOSER_RESULTCODE) {
            if (null == this.mUploadMessage)
                return;
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                    : intent.getData();
            this.mUploadMessage.onReceiveValue(result);
            this.mUploadMessage = null;
        } else if (requestCode == 1 && resultCode == 1) {
            this.iniPath();
            this.webview.loadUrl(this.tempUrl);
        }
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
        this.tempUrl = this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "skydrivePath", "/SkyDrive/MySkyDrive.cbs", (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(userIni, "Skydrive",
                "skydrivePort", constants.DefaultServerPort, (byte) 0);
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "skydriveAddress", constants.DefaultServerIp, (byte) 0);
        String baseUrl = "http://" + ipUrl + ":" + portUrl;
        this.tempUrl = StringUtils.isUrl(baseUrl + this.tempUrl, baseUrl, null);
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 1) {
            if (this.tempUrl.indexOf("?") != -1) {
                this.tempUrl = this.tempUrl
                        + "&LoginId="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "LoginId", "", (byte) 0)
                        + "&UserName="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "Account", "", (byte) 0);
            } else {
                this.tempUrl = this.tempUrl
                        + "?LoginId="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "LoginId", "", (byte) 0)
                        + "&UserName="
                        + this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "Account", "", (byte) 0);
            }
        } else {
            this.startActivityForResult(new Intent(this, LoginDialogResult.class), 1);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
        this.unregisterReceiver(this.MyBroadCastReceiver);
        if (this.webview != null) {
            this.webview.clearCache(true);
            this.webview.setVisibility(View.GONE);
            this.webview.destroy();
            this.webview = null;
        }
    }

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
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                this.menuBtn.callOnClick();
            }
        }
        return true;
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
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
        // font = (RelativeLayout) view.findViewById(R.id.font);
        // background = (RelativeLayout) view.findViewById(R.id.background);
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
                MySkyDriveActivity.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.skydriveTitle.getHeight() * 3 / 2);
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
