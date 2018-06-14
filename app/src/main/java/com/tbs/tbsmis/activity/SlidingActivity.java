package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.Exam.ExamMainActivity;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.MenuListAdapter;
import com.tbs.tbsmis.check.PopMenuAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.lib.BaseSlidingFragmentActivity;
import com.tbs.tbsmis.lib.SlidingMenu;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.search.SearchActivityNew;
import com.tbs.tbsmis.source.FileSourceActivity;
import com.tbs.tbsmis.util.CRequest;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.weixin.WeixinNewMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.tbs.chat.ui.conversation.MainTab;

public class SlidingActivity extends BaseSlidingFragmentActivity implements
        View.OnClickListener, View.OnTouchListener, GestureDetector.OnGestureListener
{
    protected static final int FILECHOOSER_RESULTCODE = 0;
    private boolean isOpenPop;
    private PopupWindow SetWindow;
    private CheckBox toolcheck, msgcheck, right_box, left_box;
    protected static SlidingMenu mSlidingMenu;
    private TextView title;
    private RelativeLayout loadingIV;
    private RelativeLayout navigatoin;
    private ImageView iv;
    private WebView webview;
    private boolean loadingDialogState;
    private AnimationDrawable loadingAnima;
    private String tempUrl;
    private int pagenum;
    private int ShowLeftBar, ShowRightBar, ShowMenu, ShowMore;
    private GestureDetector mGestureDetector;
    public int pagerIndex;
    public ArrayList<View> menuViews;
    private ViewGroup main;
    public ViewPager viewPager;
    private ImageView imagePrevious;
    private ImageView imageNext;
    private SlidingActivity.MyBroadcastReciver MyBroadcastReciver;
    private int n, screenWidth;
    private IniFile m_iniFileIO;
    @SuppressWarnings("unused")
    private ProgressBar pb;
    private ImageView searchBtn;
    private ImageView menuBtn;
    private RelativeLayout top_navigate;
    private String baseUrl;
    private String Botscan;
    private String resname, NowResname;
    private SlideMenuLayout menuBar;
    private int ShowMainTitle;
    private String appTestFile;
    // private String appUserFile;
    private String appNewsFile;
    private String webRoot;
    private CompoundButton titlecheck;
    private WebSettings Settings;
    private CheckBox bottom_window_box;
    private int screenHight;
    private View baseView;
    protected ValueCallback<Uri> mUploadMessage;
    private String userIni;
    private View myView = null;
    private ViewGroup frameLayout = null;
    private WebChromeClient.CustomViewCallback myCallBack = null;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private WebView web;
    private long mExitTime;
    private WebChromeClient.FileChooserParams mFileChooserParams;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        pagerIndex = 0;
        LayoutInflater inflater = getLayoutInflater();
        mGestureDetector = new GestureDetector(this);
        main = (ViewGroup) inflater.inflate(R.layout.browse_activity, null);
        setContentView(main);
        // 接受广播注册（Tbsnews）
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Action_main"
                + getString(R.string.about_title));
        intentFilter.addAction("login"
                + getString(R.string.about_title));
        intentFilter.addAction("pay"
                + getString(R.string.about_title));
        intentFilter.addAction("Action_fresh" + getString(R.string.about_title));
        MyBroadcastReciver = new SlidingActivity.MyBroadcastReciver();
        registerReceiver(MyBroadcastReciver, intentFilter);
        MyActivity.getInstance().addActivity(this);
        initdata();
        initView1(savedInstanceState);
        initSlidingMenu();
        if (getIntent().getExtras() != null) {
            Intent notificationIntent = getIntent();
            int flag = notificationIntent.getIntExtra("flag", 0);
            Intent intent = new Intent();
            if (flag == 0) {
                intent.setClass(this, DetailActivity.class);
                String notificationId = notificationIntent
                        .getStringExtra(Constants.NOTIFICATION_ID);
                String notificationTitle = notificationIntent
                        .getStringExtra(Constants.NOTIFICATION_TITLE);
                String notificationUri = notificationIntent
                        .getStringExtra(Constants.NOTIFICATION_URI);
                intent.putExtra("flag", 2);
                intent.putExtra("winStrState", constants.STATEFORSEARCH);
                intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
                intent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
                intent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
            } else if (flag == 1) {
                intent.setClass(this, OfflineActivity.class);
            } else if (flag == 2) {
                intent.setClass(this, WeixinNewMessage.class);
            }
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        iniWidth();
        webview.setBackgroundColor(Color
                .parseColor(m_iniFileIO.getIniString(appNewsFile,
                        "BASIC_SETUP", "BackColorValue", "#f6f6f6", (byte) 0)));
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            Settings.setTextZoom(Integer.parseInt(m_iniFileIO.getIniString(
                    appNewsFile, "BASIC_SETUP", "TextZoom", "100", (byte) 0)));
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(MyBroadcastReciver);
        if (webview != null) {
            webview.clearCache(true);
            webview.clearHistory();
            webview.setVisibility(View.GONE);
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }

    private void initdata() {
        m_iniFileIO = new IniFile();
        iniPath();
        ShowLeftBar = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "ShowLeftBar", "1", (byte) 0));
        ShowRightBar = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "ShowRightBar", "1", (byte) 0));
        ShowMenu = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "ShowMenu", "0", (byte) 0));
        ShowMore = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "ShowMore", "0", (byte) 0));
        ShowMainTitle = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "ShowMainTitle", "1", (byte) 0));
    }

    private void iniPath() {
        // TODO Auto-generated method stub
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHight = dm.heightPixels;
        String configPath = getApplicationContext().getFilesDir()
                .getParentFile().getAbsolutePath();
        if (configPath.endsWith("/") == false) {
            configPath = configPath + "/";
        }
        appTestFile = configPath + constants.APP_CONFIG_FILE_NAME;
        // appUserFile = configPath + constants.USER_CONFIG_FILE_NAME;
        webRoot = UIHelper.getSoftPath(this);
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
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
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
        resname = UIHelper.getShareperference(this, constants.SAVE_LOCALMSGNUM,
                "resname", "yqxx");
        NowResname = resname;
        String ipUrl = m_iniFileIO.getIniString(userIni, "SERVICE",
                "currentAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = m_iniFileIO.getIniString(userIni, "SERVICE",
                "currentPort", constants.DefaultLocalPort, (byte) 0);
        baseUrl = "http://" + ipUrl + ":" + portUrl;
        Botscan = m_iniFileIO.getIniString(userIni, "MSGURL", "NavPage",
                "", (byte) 0);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initView1(Bundle savedInstanceState) {
        pb = (ProgressBar) findViewById(R.id.progressbar);
        searchBtn = (ImageView) findViewById(R.id.NavigateHome);
        menuBtn = (ImageView) findViewById(R.id.NavigateBack);
        webview = (WebView) findViewById(R.id.webview);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        /**
         * webview��������
         */
        Settings = webview.getSettings();
        Settings.setSupportZoom(true);
        Settings.setBuiltInZoomControls(true);
        Settings.setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            Settings.setDisplayZoomControls(false);
        }
        Settings.setUseWideViewPort(true);
        Settings.setLoadWithOverviewMode(true);
        Settings.setPluginState(WebSettings.PluginState.ON);
        Settings.setSupportMultipleWindows(true);
        Settings.setJavaScriptCanOpenWindowsAutomatically(true);
        Settings.setDomStorageEnabled(true);
        Settings.setAllowFileAccess(true);
        webview.setOnTouchListener(this);

        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");

        UIHelper.addJavascript(this, webview);
        title = (TextView) findViewById(R.id.tvHeaderTitle);
        imagePrevious = (ImageView) findViewById(R.id.ivPreviousButton);
        imageNext = (ImageView) findViewById(R.id.ivNextButton);
        navigatoin = (RelativeLayout) findViewById(R.id.linearLayout02);
        top_navigate = (RelativeLayout) findViewById(R.id.item_header);
        loadingIV = (RelativeLayout) findViewById(R.id.loading_dialog);
        iv = (ImageView) findViewById(R.id.gifview);
        viewPager = (ViewPager) main.findViewById(R.id.slideMenu);
        menuBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        imagePrevious.setOnClickListener(new ImagePreviousOnclickListener());
        imageNext.setOnClickListener(new ImageNextOnclickListener());
        if (ShowLeftBar == 0) {
            menuBtn.setVisibility(View.GONE);
        }
        if (ShowRightBar == 0) {
            searchBtn.setVisibility(View.GONE);
        }
        menuBtn.setBackgroundResource(constants.TopButtonIcoId[Integer
                .parseInt(m_iniFileIO.getIniString(userIni, "BUTTON",
                        "MainLeftImageId", "5", (byte) 0))]);
        searchBtn.setBackgroundResource(constants.TopButtonIcoId[Integer
                .parseInt(m_iniFileIO.getIniString(userIni, "BUTTON",
                        "MainRightImageId", "4", (byte) 0))]);
//        String Title = m_iniFileIO.getIniString(appNewsFile, "TBSAPP",
//                "AppName", "移动信息服务", (byte) 0);
//        title.setText(Title);
        if (ShowMainTitle == 0) {
            top_navigate.setVisibility(View.GONE);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        webview.setWebViewClient(UIHelper.getWebViewClient());
        webview.setDownloadListener(UIHelper.MyWebViewDownLoadListener(this));
        webview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onReceivedTitle(WebView view, String Title) {
                super.onReceivedTitle(view, Title);
                if (!Title.contains("://"))
                    SlidingActivity.this.title.setText(Title);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                SlidingActivity.this.loadingDialogState = progress < 100;
                if (SlidingActivity.this.loadingDialogState) {
                    SlidingActivity.this.startAnimation();
                } else {
                    SlidingActivity.this.stopAnimation();
                }
            }

            @Override
            public final boolean onJsBeforeUnload(WebView paramWebView,
                                                  String paramString1, String paramString2,
                                                  JsResult paramJsResult) {
                paramJsResult.confirm();
                return true;
            }

//            @Override
//            public void onCloseWindow(WebView view) {
//                //main.removeView(baseView);
//                super.onCloseWindow(view);
//                if (baseView != null)
//                    if (baseView.isShown()) {
//                        main.removeView(baseView);
//                        web.clearHistory();
//                        web.destroy();
//                    }
//            }

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog,
                                          boolean userGesture, Message resultMsg) {
                //if (userGesture) {
                // create_window.setVisibility(view.VISIBLE);
                WebView web = SlidingActivity.this.getCurrentWebView();
                SlidingActivity.this.main.addView(SlidingActivity.this.baseView);
                ((WebViewTransport) resultMsg.obj).setWebView(web);
                resultMsg.sendToTarget();
                //}
                return true;
            }


            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mUploadMessageForAndroid5 = filePathCallback;
                mFileChooserParams = fileChooserParams;
                openFileChooserImplForAndroid5();
                return true;
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                thisopenFileChooser(uploadMsg);
            }

            // For Android > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                thisopenFileChooser(uploadMsg);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (myView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                frameLayout.setVisibility(View.VISIBLE);
                webview.setVisibility(View.INVISIBLE);
                frameLayout.addView(view);
                myView = view;
                myCallBack = callback;
                if (ShowMainTitle == 1) {
                    top_navigate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onHideCustomView() {
                if (myView == null) {
                    return;
                }
                frameLayout.removeView(myView);
                myView = null;
                frameLayout.setVisibility(View.GONE);
                webview.setVisibility(View.VISIBLE);
                myCallBack.onCustomViewHidden();
                if (ShowMainTitle == 1) {
                    top_navigate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                // TODO Auto-generated method stub
                return super.onConsoleMessage(consoleMessage);
            }

        });
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == SlidingActivity.FILECHOOSER_RESULTCODE) {
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                    : intent.getData();
            if (null != mUploadMessage) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            } else if (mUploadMessageForAndroid5 != null) {
                mUploadMessageForAndroid5.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,
                        intent));
                mUploadMessageForAndroid5 = null;

            }
        }
    }

    public void thisopenFileChooser(ValueCallback<Uri> uploadMsg) {
        if (SlidingActivity.this.mUploadMessage != null)
            return;
        SlidingActivity.this.mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        SlidingActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"),
                FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5() {
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private WebView getCurrentWebView() {
        baseView = LayoutInflater.from(this).inflate(R.layout.my_relative_view,
                null);
        ImageView backBtn = (ImageView) baseView.findViewById(R.id.more_btn2);
        ImageView finishBtn = (ImageView) baseView
                .findViewById(R.id.search_btn2);
        finishBtn.setVisibility(View.GONE);
        final TextView webtitle = (TextView) baseView
                .findViewById(R.id.title_tv);
        web = (WebView) baseView.findViewById(R.id.open_webview);
        webtitle.setText("弹出窗口");
        WebSettings setting = web.getSettings();
        // 以下两行为实现 overview mode
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            setting.setDisplayZoomControls(false);
        }
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setting.setPluginState(WebSettings.PluginState.ON);
        setting.setSupportMultipleWindows(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setDomStorageEnabled(true);
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = web.getSettings().getUserAgentString();
        web.getSettings().setUserAgentString(ua + "; tbsmis/2015");

        UIHelper.addJavascript(this, web);
        web.setWebViewClient(UIHelper.getWebViewClient());

        web.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onCloseWindow(WebView view) {
                SlidingActivity.this.main.removeView(SlidingActivity.this.baseView);
                view.destroy();
            }
//
//            @Override
//            public boolean onCreateWindow(WebView view, boolean dialog,
//                                          boolean userGesture, Message resultMsg) {
//                //if (userGesture) {
//                // create_window.setVisibility(view.VISIBLE);
//                WebView web = SlidingActivity.this.getCurrentWebView();
//                SlidingActivity.this.main.addView(SlidingActivity.this.baseView);
//                ((WebViewTransport) resultMsg.obj).setWebView(web);
//                resultMsg.sendToTarget();
//                //}
//                return true;
//            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                SlidingActivity.this.loadingDialogState = progress < 100;
                if (SlidingActivity.this.loadingDialogState) {
                    SlidingActivity.this.startAnimation();
                } else {
                    SlidingActivity.this.stopAnimation();
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!title.contains("://"))
                    webtitle.setText(title);
            }
        });
        web.requestFocus();
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        return web;
    }

    private void initSlidingMenu() {
        // int SplitRatio =
        // Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
        // "APPSHOW", "LeftWidth", "50", (byte) 0));
        // int SplitRatioRight = Integer.parseInt(m_iniFileIO.getIniString(
        // appNewsFile, "APPSHOW", "RightWidth", "50", (byte) 0));
        int TouchMode = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "TouchMode", "0", (byte) 0));
        SlidingActivity.mSlidingMenu = getSlidingMenu();
        if (TouchMode == 0) {
            SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        } else if (TouchMode == 1) {
            SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }
        if (ShowMenu == 1 && ShowMore == 1) {
            SlidingActivity.mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);// 设置滑动方向为左右
            setBehindContentView(R.layout.left_frame);// 设置左菜单
            SlidingActivity.mSlidingMenu.setSecondaryMenu(R.layout.right_frame);// 设置右菜单
            FragmentTransaction mFragementTransaction1 = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag1 = new RightFragment();
            mFragementTransaction1.replace(R.id.right_frament, mFrag1);
            mFragementTransaction1.commit();
            FragmentTransaction mFragementTransaction = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag = new LeftFragment();
            mFragementTransaction.replace(R.id.left_frame, mFrag);
            mFragementTransaction.commit();
        } else if (ShowMore == 0 && ShowMenu == 1) {
            SlidingActivity.mSlidingMenu.setMode(SlidingMenu.LEFT);// 设置滑动方式为左
            SlidingActivity.mSlidingMenu.setSecondaryMenu(R.layout.right_frame);// 设置右菜单
            FragmentTransaction mFragementTransaction1 = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag1 = new RightFragment();
            mFragementTransaction1.replace(R.id.right_frament, mFrag1);
            mFragementTransaction1.commit();
            setBehindContentView(R.layout.left_frame);// 设置左菜单
            FragmentTransaction mFragementTransaction = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag = new LeftFragment();
            mFragementTransaction.replace(R.id.left_frame, mFrag);
            mFragementTransaction.commit();
        } else if (ShowMenu == 0 && ShowMore == 1) {
            SlidingActivity.mSlidingMenu.setMode(SlidingMenu.RIGHT);// 设置滑动方式为右
            setBehindContentView(R.layout.left_frame);// 设置左菜单
            FragmentTransaction mFragementTransaction = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag = new LeftFragment();
            mFragementTransaction.replace(R.id.left_frame, mFrag);
            mFragementTransaction.commit();
            SlidingActivity.mSlidingMenu.setSecondaryMenu(R.layout.right_frame);// 设置右菜单
            FragmentTransaction mFragementTransaction1 = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag1 = new RightFragment();
            mFragementTransaction1.replace(R.id.right_frament, mFrag1);
            mFragementTransaction1.commit();
        } else if (ShowMenu == 0 && ShowMore == 0) {
            SlidingActivity.mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);// 设置滑动方式为右
            SlidingActivity.mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
            setBehindContentView(R.layout.left_frame);// 设置左菜单
            SlidingActivity.mSlidingMenu.setSecondaryMenu(R.layout.right_frame);// 设置右菜单
            FragmentTransaction mFragementTransaction1 = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag1 = new RightFragment();
            mFragementTransaction1.replace(R.id.right_frament, mFrag1);
            mFragementTransaction1.commit();
            FragmentTransaction mFragementTransaction = getSupportFragmentManager()
                    .beginTransaction();
            Fragment mFrag = new LeftFragment();
            mFragementTransaction.replace(R.id.left_frame, mFrag);
            mFragementTransaction.commit();
        }
        SlidingActivity.mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);// ������˵���ӰͼƬ
        SlidingActivity.mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);// �����Ҳ˵���ӰͼƬ
        SlidingActivity.mSlidingMenu.setShadowWidth(screenWidth / 110);// 设置阴影宽度
        SlidingActivity.mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
        SlidingActivity.mSlidingMenu.setFadeEnabled(false);// 设置滑动时菜单的是否淡入淡出
        SlidingActivity.mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        iniPath();
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.NavigateBack:
                String leftBtnAction = m_iniFileIO.getIniString(userIni,
                        "BUTTONURL", "MainLeft", "", (byte) 0);
                if (StringUtils.isEmpty(leftBtnAction)) {
                    if (Integer.parseInt(m_iniFileIO.getIniString(userIni,
                            "APPSHOW", "ShowMenu", "0", (byte) 0)) == 1) {
                        leftBtnAction = "tbs:left";
                    } else {
                        leftBtnAction = m_iniFileIO.getIniString(userIni,
                                "TBSAPP", "defaultUrl", "", (byte) 0);
                    }
                }
                if (UIHelper.TbsMotion(this,
                        StringUtils.isUrl(leftBtnAction, baseUrl, resname))) {
                    if (!UIHelper.MenuMotion(this,
                            StringUtils.isUrl(leftBtnAction, baseUrl, resname))) {
                        if (m_iniFileIO.getIniString(userIni, StringUtils.isUrl(leftBtnAction, baseUrl, resname),
                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                            complete_custom(v, StringUtils.isUrl(leftBtnAction, baseUrl, resname));
                        } else {
                            not_complete_menu(v,
                                    StringUtils.isUrl(leftBtnAction, baseUrl, resname));
                        }
                    } else {
                        webview.loadUrl(StringUtils.isUrl(leftBtnAction, baseUrl,
                                resname));
                    }
                }
                break;
            case R.id.NavigateHome:
                String BtnAction = m_iniFileIO.getIniString(userIni,
                        "BUTTONURL", "MainRight", "tbs:custom_menu", (byte) 0);
                if (StringUtils.isEmpty(BtnAction)) {
                    BtnAction = "tbs:custom_menu";
                }
                if (UIHelper.TbsMotion(this,
                        StringUtils.isUrl(BtnAction, baseUrl, resname))) {
                    if (!UIHelper.MenuMotion(this,
                            StringUtils.isUrl(BtnAction, baseUrl, resname))) {
                        if (m_iniFileIO.getIniString(userIni, StringUtils.isUrl(BtnAction, baseUrl, resname),
                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                            complete_custom(v, StringUtils.isUrl(BtnAction, baseUrl, resname));
                        } else {
                            not_complete_menu(v,
                                    StringUtils.isUrl(BtnAction, baseUrl, resname));
                        }
                    } else {
                        webview.loadUrl(StringUtils.isUrl(BtnAction, baseUrl,
                                resname));
                    }
                }
                break;
//            case R.id.menultool:
//                int toolset = Integer.parseInt(m_iniFileIO.getIniString(
//                        userIni, "APPSHOW", "ToolSet", "1", (byte) 0));
//                if (toolset == 1) {
//                    m_iniFileIO.writeIniString(userIni, "APPSHOW", "ToolSet",
//                            "0");
//                    toolcheck.setChecked(false);
//                    intent.setAction("loadView"
//                            + getString(R.string.about_title));
//                    intent.putExtra("flag", 3);
//                    intent.putExtra("author", 1);
//                    sendBroadcast(intent);
//                } else {
//                    m_iniFileIO.writeIniString(userIni, "APPSHOW", "ToolSet",
//                            "1");
//                    toolcheck.setChecked(true);
//                    intent.setAction("loadView"
//                            + getString(R.string.about_title));
//                    intent.putExtra("flag", 3);
//                    intent.putExtra("author", 2);
//                    sendBroadcast(intent);
//                }
//                break;
//            case R.id.menultitle:
//                if (Integer.parseInt(m_iniFileIO.getIniString(userIni,
//                        "APPSHOW", "ShowMainTitle", "1", (byte) 0)) == 1) {
//                    m_iniFileIO.writeIniString(userIni, "APPSHOW",
//                            "ShowMainTitle", "0");
//                    titlecheck.setChecked(false);
//                    top_navigate.setVisibility(View.GONE);
//
//                } else {
//                    m_iniFileIO.writeIniString(userIni, "APPSHOW",
//                            "ShowMainTitle", "1");
//                    titlecheck.setChecked(true);
//                    top_navigate.setVisibility(View.VISIBLE);
//                }
//                break;
//            case R.id.msgtool:
//                int toolmsg = Integer.parseInt(m_iniFileIO.getIniString(
//                        userIni, "APPSHOW", "StatusLine", "1", (byte) 0));
//                if (toolmsg == 1) {
//                    m_iniFileIO.writeIniString(userIni, "APPSHOW",
//                            "StatusLine", "0");
//                    msgcheck.setChecked(false);
//                    intent.setAction("loadView"
//                            + getString(R.string.about_title));
//                    intent.putExtra("flag", 4);
//                    intent.putExtra("author", 1);
//                    sendBroadcast(intent);
//                } else {
//                    m_iniFileIO.writeIniString(userIni, "APPSHOW",
//                            "StatusLine", "1");
//                    msgcheck.setChecked(true);
//                    intent.setAction("loadView"
//                            + getString(R.string.about_title));
//                    intent.putExtra("flag", 4);
//                    intent.putExtra("author", 2);
//                    sendBroadcast(intent);
//                }
//                break;
//            case R.id.move_left_tool:
//                int left_open = UIHelper.getShareperference(this,
//                        constants.SAVE_LOCALMSGNUM, "left_open", 0);
//                if (left_open == 1) {
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
//                            "left_open", 0);
//                    SlidingActivity.mSlidingMenu.showContent();
//                    left_box.setChecked(false);
//                } else {
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
//                            "left_open", 1);
//                    SlidingActivity.mSlidingMenu.showMenu();
//                    left_box.setChecked(true);
//                }
//                break;
//            case R.id.move_right_tool:
//                int right_open = UIHelper.getShareperference(this,
//                        constants.SAVE_LOCALMSGNUM, "right_open", 1);
//                if (right_open == 1) {
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
//                            "right_open", 0);
//                    SlidingActivity.mSlidingMenu.showContent();
//                    right_box.setChecked(false);
//                } else {
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
//                            "right_open", 1);
//                    SlidingActivity.mSlidingMenu.showSecondaryMenu(true);
//                    right_box.setChecked(true);
//                }
//                break;
//            case R.id.bottom_window:
//                if (UIHelper.getShareperference(this, constants.SAVE_LOCALMSGNUM,
//                        "bottom_window", 1) == 1) {
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
//                            "bottom_window", 0);
//                    bottom_window_box.setChecked(false);
//                    intent.setAction("loadView"
//                            + getString(R.string.about_title));
//                    intent.putExtra("flag", 4);
//                    intent.putExtra("author", 4);
//                    sendBroadcast(intent);
//                } else {
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
//                            "bottom_window", 1);
//                    bottom_window_box.setChecked(true);
//                    intent.setAction("loadView"
//                            + getString(R.string.about_title));
//                    intent.putExtra("flag", 4);
//                    intent.putExtra("author", 3);
//                    sendBroadcast(intent);
//                }
//                break;
            case R.id.menu_moreBtn:
                SetWindow.dismiss();
                if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                        "LoginFlag", "0", (byte) 0)) == 1) {
                    showWindow_bottom(v);
                } else {
                    intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.more_setBtn:
                SetWindow.dismiss();
                intent = new Intent();
                intent.setClass(this, SetUpActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_deviceBtn:
                SetWindow.dismiss();
                intent = new Intent();
                intent.setClass(this, MyCloudActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_sourceBtn:
                SetWindow.dismiss();
                intent = new Intent();
                intent.setClass(this, FileSourceActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_emailBtn:
                intent = new Intent();
                intent.setClass(this, ResourceStoreActivity.class);
                startActivity(intent);
                SetWindow.dismiss();
                break;
//            case R.id.menu_chatBtn:
////                intent = new Intent();
////                intent.setClass(this, MainTab.class);
////                 startActivity(intent);
////                   intent = new Intent();
////                intent.setClass(this, AcsAudioAcitvity.class);
////                startActivity(intent);
////                 SetWindow.dismiss();
//                intent = new Intent();
//                intent.setClass(this, LocalMusicActivity.class);
//                startActivity(intent);
//                SetWindow.dismiss();
//                break;
//            case R.id.menu_mcsBtn:
//                PackageManager packageManager = getPackageManager();
//                intent = new Intent();
//                intent = packageManager.getLaunchIntentForPackage("com.tbs.tbsmcs");
//                if (intent == null) {
//                    Toast.makeText(this, "应用未安装", Toast.LENGTH_SHORT).show();
//                } else {
//                    startActivity(intent);
//                }
//                SetWindow.dismiss();
//                break;
            case R.id.menu_skydriveBtn:
                intent = new Intent();
                intent.setClass(this, MySkyDriveActivity.class);
                startActivity(intent);
                SetWindow.dismiss();
                break;
            case R.id.menu_goBtn:
                SetWindow.dismiss();
//                Intent intent6 = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("sourceId", "8aac50fa5a4607ce015a463a5c7b0011");
//                bundle.putString("type", "Online");
//                intent6.putExtras(bundle);
//                intent6.setClass(this, VideoPlayer.class);
//                startActivity(intent6);
                webview.goForward();
                break;
            case R.id.menu_backBtn:
                SetWindow.dismiss();
//                Intent intent5 = new Intent();
//                Bundle bundle5 = new Bundle();
//                bundle5.putString("sourceId", "13");
//                bundle5.putString("type", "Online");
//                intent5.putExtras(bundle5);
//                intent5.setClass(this, FilmActivity.class);
//                startActivity(intent5);
                webview.goBack();
                break;
            case R.id.menu_refreshBtn:
                SetWindow.dismiss();
//                Intent intent4 = new Intent();
//                intent4.setClass(this, LivePlayer.class);
//                Bundle bundle4 = new Bundle();
//                bundle4.putString("liveId", "2820efbb5e6e711e015e6e7607870002");
//                intent4.putExtras(bundle4);
//                startActivity(intent4);
                webview.reload();
                break;
            case R.id.menu_scanBtn:
                SetWindow.dismiss();
                intent = new Intent();
                intent.setClass(this, ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_shareBtn:
                SetWindow.dismiss();
//                Intent intent3 = new Intent();
//                Bundle bundle3 = new Bundle();
//                bundle3.putString("sourceId", "2820efb85726d10a015726d10a430000");
//                bundle3.putString("type", "Online");
//                intent3.putExtras(bundle3);
//                intent3.setClass(this, ShortVideoPlayer.class);
//                startActivity(intent3);
                UIHelper.showShareMore(this, title.getText()
                        .toString());
//                JsoupExam.getSearchEngine(2, webview.getUrl(), this);
                break;
            case R.id.menu_favouriteBtn:
//                UIHelper.Collect(this, "", title.getText()
//                        .toString(), "", "", "");
                JsoupExam.getSearchEngine(1, webview.getUrl(), this);
                SetWindow.dismiss();
//                webview.loadUrl("javascript:gettitle()");
                break;
            case R.id.menu_appBtn:
                SetWindow.dismiss();
                intent = new Intent();
                intent.setClass(this, MyAppsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_searchBtn:
                SetWindow.dismiss();
                intent = new Intent();
                intent.setClass(this, SearchActivityNew.class);
                startActivity(intent);
                break;
            case R.id.menu_quitBtn:
                SetWindow.dismiss();
                UIHelper.showAllQuitDialog(this);
                break;
            case R.id.menu_homeBtn:
                SetWindow.dismiss();
                Intent intent2 = new Intent();
                Bundle bundle2 = new Bundle();
                String examInfo = "{\"examId\":\"8aac50fa5a600c11015a602a747b0062\"," +
                        "\"paperId\":\"176\"," +
                        "\"e_name\":\"路局固定试卷\"," +
                        "\"e_time\":\"3\"," +
                        "\"e_passscore\":\"10\"," +
                        "\"e_totalscore\":\"12\"}";
                bundle2.putString("examInfo", examInfo);
                intent2.putExtras(bundle2);
                intent2.setClass(this, ExamMainActivity.class);
                startActivity(intent2);
                String Botsearch = m_iniFileIO.getIniString(userIni, "TBSAPP",
                        "defaultUrl", "", (byte) 0);
//                if (StringUtils.isEmpty(Botsearch)) {
//                    Botsearch = m_iniFileIO.getIniString(userIni, "BUTTONURL",
//                            "BOT3", "", (byte) 0);
//                }
                tempUrl = StringUtils.isUrl(Botsearch, baseUrl, resname);
                if (UIHelper.TbsMotion(this, tempUrl)) {
                    webview.loadUrl(tempUrl);
                    navigatoin.setVisibility(View.GONE);
                }
                break;
            case R.id.userinfo:
                SetWindow.dismiss();
                if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                        "LoginFlag", "0", (byte) 0)) == 1) {
                    intent = new Intent();
                    intent.setClass(this, MyPageActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.more_btn2:
                main.removeView(baseView);
                web.clearHistory();
                web.destroy();
                break;
            case R.id.search_btn2:
                main.removeView(baseView);
                web.clearHistory();
                web.destroy();
                break;
        }
    }

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SlidingActivity.this.iniPath();
            if (action.equals("Action_main"
                    + getString(R.string.about_title))) {
                int SelBtn = intent.getIntExtra("flag", 1);
                int navigatoinflag = 0;
                String Title = null;
                switch (SelBtn) {
                    case 1:
                        SlidingActivity.this.menuViews = new ArrayList<View>();
                        SlidingActivity.this.menuBar = new SlideMenuLayout(SlidingActivity.this);
                        SlidingActivity.this.pagenum = intent.getIntExtra("pagenum", 0);
                        navigatoinflag = intent.getIntExtra("navigatoin", 1);
                        if (navigatoinflag == 1) {
                            SlidingActivity.this.n = (constants.menus.length - 1) / 4;
                            for (int i = 0; i <= SlidingActivity.this.n; i++) {
                                SlidingActivity.this.menuViews.add(SlidingActivity.this.menuBar.getSlideMenuLinerLayout(
                                        SlidingActivity.this.screenWidth, i));
                            }
                            SlidingActivity.this.viewPager.setAdapter(new SlideMenuAdapter(SlidingActivity.this
                                    .menuViews));
                            SlidingActivity.this.viewPager
                                    .setOnPageChangeListener(new SlideMenuChangeListener());
                            if (SlidingActivity.this.menuViews.size() > 1) {
                                SlidingActivity.this.imageNext.setVisibility(View.VISIBLE);
                            } else {
                                SlidingActivity.this.imageNext.setVisibility(View.INVISIBLE);
                            }
                            SlidingActivity.this.imagePrevious.setVisibility(View.INVISIBLE);
                            SlidingActivity.this.pagerIndex = 0;
                            SlidingActivity.this.viewPager.setCurrentItem(SlidingActivity.this.pagerIndex);
                            SlidingActivity.this.tempUrl = StringUtils.isUrl(SlidingActivity.this.Botscan,
                                    SlidingActivity.this.baseUrl, SlidingActivity.this.resname)
                                    + "&Mycondition=" + constants.menus[0];

                            if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                            }
                            SlidingActivity.this.navigatoin.setVisibility(View.VISIBLE);
                        } else {
                            SlidingActivity.this.pagenum = 1;
                            SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                            SlidingActivity.this.tempUrl = StringUtils.isUrl(SlidingActivity.this.Botscan,
                                    SlidingActivity.this.baseUrl, SlidingActivity.this.resname);
                            if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                            }
                        }
                        // System.out.println(tempUrl);
                        SlidingActivity.mSlidingMenu.showContent();
                        break;
                    case 2:
                        navigatoinflag = intent.getIntExtra("navigatoin", 1);
                        if (navigatoinflag == 1) {
                            String BOT1 = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "BUTTONURL", "BOT1", "", (byte) 0);
                            if (!StringUtils.isEmpty(BOT1)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(
                                        BOT1,
                                        SlidingActivity.this.baseUrl, SlidingActivity.this.resname);
                                if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                    if (!UIHelper.MenuMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        if (m_iniFileIO.getIniString(userIni, tempUrl,
                                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                                            complete_custom(main, tempUrl);
                                        } else {
                                            not_complete_menu(main,
                                                    tempUrl);
                                        }
                                    } else {
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }
                        } else if (navigatoinflag == 2) {
                            String BOT2 = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "BUTTONURL", "BOT2", "", (byte) 0);
                            if (!StringUtils.isEmpty(BOT2)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(
                                        BOT2,
                                        SlidingActivity.this.baseUrl, SlidingActivity.this.resname);
                                if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                    if (!UIHelper.MenuMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        if (m_iniFileIO.getIniString(userIni, tempUrl,
                                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                                            complete_custom(main, tempUrl);
                                        } else {
                                            not_complete_menu(main,
                                                    tempUrl);
                                        }
                                    } else {
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }
                        } else if (navigatoinflag == 3) {
                            String BOT3 = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "BUTTONURL", "BOT3", "", (byte) 0);
                            if (!StringUtils.isEmpty(BOT3)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(
                                        BOT3,
                                        SlidingActivity.this.baseUrl, SlidingActivity.this.resname);
                                if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                    if (!UIHelper.MenuMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        if (m_iniFileIO.getIniString(userIni, tempUrl,
                                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                                            complete_custom(main, tempUrl);
                                        } else {
                                            not_complete_menu(main,
                                                    tempUrl);
                                        }
                                    } else {
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }
                        } else if (navigatoinflag == 4) {
                            String BOT4 = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "BUTTONURL", "BOT4", "", (byte) 0);
                            if (!StringUtils.isEmpty(BOT4)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(BOT4, SlidingActivity.this.baseUrl,
                                        SlidingActivity.this.resname);

                                if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {

                                    if (!UIHelper.MenuMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        if (m_iniFileIO.getIniString(userIni, tempUrl,
                                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                                            complete_custom(main, tempUrl);
                                        } else {
                                            not_complete_menu(main,
                                                    tempUrl);
                                        }
                                    } else {
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }

                        } else if (navigatoinflag == 5) {
                            String BOT5 = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "BUTTONURL", "BOT5", "", (byte) 0);
                            if (!StringUtils.isEmpty(BOT5)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(BOT5, SlidingActivity.this.baseUrl,
                                        SlidingActivity.this.resname);

                                if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                    if (!UIHelper.MenuMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        if (m_iniFileIO.getIniString(userIni, tempUrl,
                                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                                            complete_custom(main, tempUrl);
                                        } else {
                                            not_complete_menu(main,
                                                    tempUrl);
                                        }
                                    } else {
                                        // System.out.println(tempUrl);
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }
                        } else if (navigatoinflag == 6) {
                            String leftUrl = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "TBSAPP", "leftUrl", "", (byte) 0);
                            if (!StringUtils.isEmpty(leftUrl)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(
                                        leftUrl, SlidingActivity.this.baseUrl,
                                        SlidingActivity.this.resname);
                                if (!StringUtils.isEmpty(SlidingActivity.this.tempUrl)) {
                                    if (UIHelper.TbsMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }
                        } else if (navigatoinflag == 7) {
                            String rightUrl = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this
                                            .userIni,
                                    "TBSAPP", "rightUrl", "", (byte) 0);
                            if (!StringUtils.isEmpty(rightUrl)) {
                                SlidingActivity.this.tempUrl = StringUtils.isUrl(rightUrl, SlidingActivity.this.baseUrl,
                                        SlidingActivity.this.resname);
                                if (!StringUtils.isEmpty(SlidingActivity.this.tempUrl)) {
                                    if (UIHelper.TbsMotion(SlidingActivity.this,
                                            SlidingActivity.this.tempUrl)) {
                                        SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                        SlidingActivity.this.pagenum = 1;
                                        SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                        SlidingActivity.mSlidingMenu.showContent();
                                    }
                                }
                            }
                        } else if (navigatoinflag == 8) {
                            SlidingActivity.this.tempUrl = StringUtils.isUrl(
                                    intent.getStringExtra("url"), SlidingActivity.this.baseUrl, SlidingActivity.this
                                            .resname);
                            if (!StringUtils.isEmpty(SlidingActivity.this.tempUrl)) {
                                if (UIHelper.TbsMotion(SlidingActivity.this,
                                        SlidingActivity.this.tempUrl)) {
                                    SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                    SlidingActivity.this.pagenum = 1;
                                    SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                    SlidingActivity.mSlidingMenu.showContent();
                                }
                            }
                        } else if (navigatoinflag == 9) {
                            SlidingActivity.this.tempUrl = StringUtils.isUrl(SlidingActivity.this.m_iniFileIO
                                    .getIniString(
                                            SlidingActivity.this.userIni, "MSGURL", "PromptMsg", "",
                                            (byte) 0), SlidingActivity.this.baseUrl, SlidingActivity.this.resname);
                            if (!StringUtils.isEmpty(SlidingActivity.this.tempUrl)) {
                                if (UIHelper.TbsMotion(SlidingActivity.this,
                                        SlidingActivity.this.tempUrl)) {
                                    SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                    SlidingActivity.this.pagenum = 1;
                                    SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                                    SlidingActivity.mSlidingMenu.showContent();
                                }
                            }
                        }
                        break;
                    case 3:
                        SlidingActivity.this.menuBtn.setBackgroundResource(constants.TopButtonIcoId[Integer.parseInt
                                (SlidingActivity.this
                                        .m_iniFileIO
                                        .getIniString(SlidingActivity.this.userIni, "BUTTON",
                                                "MainLeftImageId", "5", (byte) 0))]);
                        SlidingActivity.this.searchBtn
                                .setBackgroundResource(constants.TopButtonIcoId[Integer
                                        .parseInt(SlidingActivity.this.m_iniFileIO.getIniString(
                                                SlidingActivity.this.userIni, "BUTTON",
                                                "MainRightImageId", "4", (byte) 0))]);
                        break;
                    case 4:
                        int menuNum = intent.getIntExtra("menuNum", 0);
                        SlidingActivity.this.tempUrl = StringUtils.isUrl(SlidingActivity.this.Botscan,
                                SlidingActivity.this.baseUrl, SlidingActivity.this.resname)
                                + "&Mycondition=" + constants.menus[menuNum];
                        if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                            SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                        }
                        break;
                    case 5:
                        SlidingActivity.this.pagenum = intent.getIntExtra("pagenum", 0);
                        SlidingActivity.this.tempUrl = intent.getStringExtra("url");
                        Title = intent.getStringExtra("name");
                        SlidingActivity.this.title.setText(Title);
                        Map<String, String> mapRequest = CRequest
                                .URLRequest(SlidingActivity.this.tempUrl);
                        SlidingActivity.this.resname = mapRequest.get("resname");
                        if (StringUtils.isEmpty(SlidingActivity.this.resname)) {
                            SlidingActivity.this.resname = SlidingActivity.this.NowResname;
                        }
                        UIHelper.setSharePerference(SlidingActivity.this,
                                constants.SAVE_LOCALMSGNUM, "resname", SlidingActivity.this.resname);
                        navigatoinflag = intent.getIntExtra("navigatoin", 1);
                        if (navigatoinflag == 0) {
                            SlidingActivity.this.tempUrl = StringUtils.isUrl(SlidingActivity.this.tempUrl,
                                    SlidingActivity.this.baseUrl, null);
                            if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                            }
                        }
                        if (UIHelper.getShareperference(SlidingActivity.this,
                                constants.SAVE_LOCALMSGNUM, "left_open", 0) == 0) {
                            SlidingActivity.mSlidingMenu.showContent();
                        }

                        break;
                    case 6:
                        SlidingActivity.this.pagenum = intent.getIntExtra("pagenum", 0);
                        SlidingActivity.this.tempUrl = intent.getStringExtra("url");
                        Map<String, String> mapRequest1 = CRequest
                                .URLRequest(SlidingActivity.this.tempUrl);
                        SlidingActivity.this.resname = mapRequest1.get("resname");
                        if (StringUtils.isEmpty(SlidingActivity.this.resname)) {
                            SlidingActivity.this.resname = SlidingActivity.this.NowResname;
                        }
                        UIHelper.setSharePerference(SlidingActivity.this,
                                constants.SAVE_LOCALMSGNUM, "resname", SlidingActivity.this.resname);
                        Title = intent.getStringExtra("name");
                        navigatoinflag = intent.getIntExtra("navigatoin", 1);
                        if (navigatoinflag == 0) {
                            SlidingActivity.this.tempUrl = StringUtils.isUrl(SlidingActivity.this.tempUrl,
                                    SlidingActivity.this.baseUrl, null);
                            if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                            }
                        }
                        if (UIHelper.getShareperference(SlidingActivity.this,
                                constants.SAVE_LOCALMSGNUM, "left_open", 0) == 0) {
                            SlidingActivity.mSlidingMenu.showContent();
                        }

                        SlidingActivity.this.title.setText(Title);
                        break;
                    case 7:
                        SlidingActivity.this.webview.reload();
                        break;
                    case 8:
                        navigatoinflag = intent.getIntExtra("navigatoin", 0);
                        SlidingActivity.mSlidingMenu.showSecondaryMenu(true);
                        if (navigatoinflag == 1) {
                            Intent rightintent = new Intent();
                            rightintent.setAction("loadRight"
                                    + getString(R.string.about_title));
                            rightintent.putExtra("flag", 1);
                            SlidingActivity.this.sendBroadcast(rightintent);
                        } else if (navigatoinflag == 2) {
                            SlidingActivity.this.tempUrl = intent.getStringExtra("url");
                            Intent leftintent = new Intent();
                            leftintent.setAction("loadRight"
                                    + getString(R.string.about_title));
                            leftintent.putExtra("flag", 2);
                            leftintent.putExtra("tempUrl", SlidingActivity.this.tempUrl);
                            leftintent.putExtra("ResName", SlidingActivity.this.resname);
                            SlidingActivity.this.sendBroadcast(leftintent);
                        }
                        break;
                    case 9:
                        navigatoinflag = intent.getIntExtra("navigatoin", 0);
                        SlidingActivity.mSlidingMenu.showMenu(true);
                        if (navigatoinflag == 1) {
                            Intent leftintent = new Intent();
                            leftintent.setAction("loadLeft"
                                    + getString(R.string.about_title));
                            leftintent.putExtra("flag", 0);
                            SlidingActivity.this.sendBroadcast(leftintent);
                        } else if (navigatoinflag == 2) {
                            SlidingActivity.this.tempUrl = intent.getStringExtra("url");
                            Intent leftintent = new Intent();
                            leftintent.setAction("loadLeft"
                                    + getString(R.string.about_title));
                            leftintent.putExtra("flag", 1);
                            leftintent.putExtra("sourceUrl", SlidingActivity.this.tempUrl);
                            SlidingActivity.this.sendBroadcast(leftintent);
                        }
                        break;
                    case 10:
                        Title = intent.getStringExtra("name");
                        SlidingActivity.this.title.setText(Title);
                        break;
                    case 11:
                        String notificationId = intent
                                .getStringExtra(Constants.NOTIFICATION_ID);
                        String notificationTitle = intent
                                .getStringExtra(Constants.NOTIFICATION_TITLE);
                        String notificationUri = intent
                                .getStringExtra(Constants.NOTIFICATION_URI);
                        Intent detailintent = new Intent();
                        detailintent.putExtra("flag", 2);
                        detailintent.putExtra("winStrState",
                                constants.STATEFORSEARCH);
                        detailintent.putExtra(Constants.NOTIFICATION_ID,
                                notificationId);
                        detailintent.putExtra(Constants.NOTIFICATION_TITLE,
                                notificationTitle);
                        detailintent.putExtra(Constants.NOTIFICATION_URI,
                                notificationUri);
                        SlidingActivity.this.startActivity(detailintent);
                        break;
                    case 12:
                        Title = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                "TBSAPP",
                                "AppName", "", (byte) 0);
                        SlidingActivity.this.title.setText(Title);
                        UIHelper.setSharePerference(SlidingActivity.this,
                                constants.SAVE_LOCALMSGNUM, "resname", SlidingActivity.this.m_iniFileIO
                                        .getIniString(SlidingActivity.this.userIni, "TBSAPP",
                                                "resname", "", (byte) 0));
                        break;
                    case 13:
                        SlidingActivity.this.ShowMainTitle = Integer.parseInt(SlidingActivity.this.m_iniFileIO
                                .getIniString(
                                        SlidingActivity.this.userIni, "APPSHOW", "ShowMainTitle", "1",
                                        (byte) 0));
                        if (SlidingActivity.this.ShowMainTitle == 0) {
                            SlidingActivity.this.top_navigate.setVisibility(View.GONE);
                        } else {
                            SlidingActivity.this.top_navigate.setVisibility(View.VISIBLE);
                        }
                        SlidingActivity.this.ShowLeftBar = Integer.parseInt(SlidingActivity.this.m_iniFileIO
                                .getIniString(
                                        SlidingActivity.this.userIni, "APPSHOW", "ShowLeftBar", "1",
                                        (byte) 0));
                        SlidingActivity.this.ShowRightBar = Integer.parseInt(SlidingActivity.this.m_iniFileIO
                                .getIniString(
                                        SlidingActivity.this.userIni, "APPSHOW", "ShowRightBar", "1",
                                        (byte) 0));
                        if (SlidingActivity.this.ShowLeftBar == 0) {
                            SlidingActivity.this.menuBtn.setVisibility(View.GONE);
                        } else {
                            SlidingActivity.this.menuBtn.setVisibility(View.VISIBLE);
                        }
                        if (SlidingActivity.this.ShowRightBar == 0) {
                            SlidingActivity.this.searchBtn.setVisibility(View.GONE);
                        } else {
                            SlidingActivity.this.searchBtn.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 15:
                        SlidingActivity.this.pagenum = intent.getIntExtra("pagenum", 0);
                        navigatoinflag = intent.getIntExtra("navigatoin", 1);
                        if (navigatoinflag == 0) {
                            String Botsearch = SlidingActivity.this.m_iniFileIO.getIniString(
                                    SlidingActivity.this.userIni, "TBSAPP", "defaultUrl", "",
                                    (byte) 0);
                            if (StringUtils.isEmpty(Botsearch)) {
                                Botsearch = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this
                                                .userIni,
                                        "BUTTONURL", "BOT3", "", (byte) 0);
                            }
                            SlidingActivity.this.tempUrl = StringUtils
                                    .isUrl(Botsearch, SlidingActivity.this.baseUrl, SlidingActivity.this.resname);
                            if (UIHelper.TbsMotion(SlidingActivity.this, SlidingActivity.this.tempUrl)) {
                                SlidingActivity.this.webview.loadUrl(SlidingActivity.this.tempUrl);
                                SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case 16:
                        if (SlidingActivity.this.top_navigate.getHeight() > UIHelper.getShareperference(
                                SlidingActivity.this, constants.SAVE_LOCALMSGNUM,
                                "poplocation", SlidingActivity.this.top_navigate.getHeight())) {
                            UIHelper.setSharePerference(SlidingActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "poplocation",
                                    SlidingActivity.this.top_navigate.getHeight());
                        }
                        SlidingActivity.this.default_menu(SlidingActivity.this.main);
                        break;
                    case 17:
                        if (SlidingActivity.this.top_navigate.getHeight() > UIHelper.getShareperference(
                                SlidingActivity.this, constants.SAVE_LOCALMSGNUM,
                                "poplocation", SlidingActivity.this.top_navigate.getHeight())) {
                            UIHelper.setSharePerference(SlidingActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "poplocation",
                                    SlidingActivity.this.top_navigate.getHeight());
                        }
                        SlidingActivity.this.default_left_menu(SlidingActivity.this.main);
                        break;
                    case 21:
                        String tempUrl = intent.getStringExtra("tempUrl");
                        if (m_iniFileIO.getIniString(userIni, tempUrl,
                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                            complete_custom(main, tempUrl);
                        } else {
                            not_complete_menu(main,
                                    tempUrl);
                        }
                        //SlidingActivity.this.not_complete_menu(SlidingActivity.this.main, tempUrl);
                        break;
                    case 22:
                        showWindow_bottom(main);
                        break;
                    case 18:
                        if (SlidingActivity.this.webview.canGoBack()) {
                            SlidingActivity.this.webview.goBack();
                        }
                        break;
                    case 19:
                        if (SlidingActivity.this.webview.canGoForward()) {
                            SlidingActivity.this.webview.goForward();
                        }
                        break;
                    case 20:
                        String Botsearch = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this
                                        .userIni,
                                "TBSAPP", "defaultUrl", "", (byte) 0);
                        if (StringUtils.isEmpty(Botsearch)) {
                            Botsearch = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni,
                                    "BUTTONURL", "BOT3", "", (byte) 0);
                        }
                        tempUrl = StringUtils.isUrl(Botsearch, SlidingActivity.this.baseUrl, SlidingActivity.this
                                .resname);
                        if (UIHelper.TbsMotion(SlidingActivity.this, tempUrl)) {
                            SlidingActivity.this.webview.loadUrl(tempUrl);
                            SlidingActivity.this.navigatoin.setVisibility(View.GONE);
                        }
                        break;
                }
            } else if (action.equals("Action_fresh"
                    + getString(R.string.about_title))) {
                SlidingActivity.this.webview.loadUrl("javascript:delzip()");
                SlidingActivity.this.webview.reload();
            } else if (action.equals("login"
                    + getString(R.string.about_title))) {
                webview.reload();
//                String leftBtnAction = m_iniFileIO.getIniString(appNewsFile,
//                        "BUTTONURL", "MainLeft", "", (byte) 0);
//                if (StringUtils.isEmpty(leftBtnAction)) {
//                    if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
//                            "APPSHOW", "ShowMenu", "0", (byte) 0)) == 1) {
//                        leftBtnAction = "tbs:left";
//                    } else {
//                        leftBtnAction = m_iniFileIO.getIniString(appNewsFile,
//                                "TBSAPP", "defaultUrl", "", (byte) 0);
//                    }
//                }
//                if (UIHelper.TbsMotion(SlidingActivity.this,
//                        StringUtils.isUrl(leftBtnAction, baseUrl, resname))) {
//                        webview.loadUrl(StringUtils.isUrl(leftBtnAction, baseUrl,
//                                resname));
//                    }
            } else if (("pay" + getString(R.string.about_title)).equals(action)) {
                int SelBtn = intent.getIntExtra("flag", 1);

                IniFile IniFile = new IniFile();
                String webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appTestFile = webRoot
                        + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String userIni = appTestFile;
                if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1) {
                    String dataPath = context.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                String loginId = IniFile.getIniString(userIni, "Login",
                        "LoginId", "", (byte) 0);
                String orderId = UIHelper.getShareperference(context, constants.SAVE_INFORMATION, "orderid",
                        "");
                String ipUrl = IniFile.getIniString(userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp, (byte) 0);
                String portUrl = IniFile.getIniString(userIni, "Login",
                        "ebsPort", "8083", (byte) 0);
                String baseUrl = "http://" + ipUrl + ":" + portUrl;

                if (SelBtn == 0) {
                    String url = baseUrl + "/TBS-PAY/pay/PayServlet?act=fcallbackPay&orderId=" +
                            orderId +
                            "&loginId=" + loginId + "&payStatus=1";
                    SlidingActivity.this.webview.loadUrl(url);
                } else if (SelBtn == 1) {
                    String url = baseUrl + "/TBS-PAY/pay/PayServlet?act=fcallbackPay&orderId=" +
                            orderId +
                            "&loginId=" + loginId + "&payStatus=0";
                    SlidingActivity.this.webview.loadUrl(url);
                    //webview.goBackOrForward(-3);
                }

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (baseView != null && baseView.isShown()) {
                main.removeView(baseView);
                web.clearHistory();
                web.destroy();
                return true;
            } else if (Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "APPSHOW", "ToolSet", "0", (byte) 0)) == 0) {
                if ((System.currentTimeMillis() - mExitTime) > 800) {
                    Toast.makeText(SlidingActivity.this, "连按两次退出应用程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                    if (webview.canGoBack()) {
                        webview.goBack();
                    }
                } else {
                    UIHelper.showQuitDialog(this);
                }
                return true;
            } else if (SlidingActivity.mSlidingMenu.getmViewAbove().getCurrentItem() == 1) {
                if ((System.currentTimeMillis() - mExitTime) > 800) {
                    Toast.makeText(SlidingActivity.this, "连按两次退出应用程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                    if (UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "bottom_window", 0) == 1) {
                        Intent intent = new Intent();
                        intent.setAction("loadView"
                                + getString(R.string.about_title));
                        intent.putExtra("flag", 2);
                        intent.putExtra("author", 2);
                        sendBroadcast(intent);
                    }
                } else {
                    UIHelper.showQuitDialog(this);
                }
                return true;
            } else {
                SlidingActivity.mSlidingMenu.showContent();
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (top_navigate.getHeight() > UIHelper.getShareperference(
                    this, constants.SAVE_LOCALMSGNUM,
                    "poplocation", top_navigate.getHeight())) {
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "poplocation",
                        top_navigate.getHeight());
            }
            tempUrl = StringUtils.isUrl(m_iniFileIO.getIniString(userIni,
                    "TBSAPP", "defaultMenu", "", (byte) 0), baseUrl, resname);
            if (UIHelper.TbsMotion(this, tempUrl)) {
                if (!UIHelper.MenuMotion(this, tempUrl)) {
                    if (m_iniFileIO.getIniString(userIni, tempUrl,
                            "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                        complete_custom(main, tempUrl);
                    } else {
                        not_complete_menu(main,
                                tempUrl);
                    }
                    return true;
                }
            }
            default_menu(main);
        }
        return true;
    }

    public void default_menu(View v) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            popWindow_default_menu(v);
        } else {
            if (SetWindow != null) {
                SetWindow.dismiss();
            }
        }
    }

    public void default_left_menu(View v) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            popWindow_default_left_menu(v);
        } else {
            if (SetWindow != null) {
                SetWindow.dismiss();
            }
        }
    }

    public void complete_custom(View v, String action) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            popWindow_complete_custom(v, action);
        } else {
            if (SetWindow != null) {
                SetWindow.dismiss();
            }
        }
    }

    public void showWindow_bottom(View v) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            popWindow_bottom(v);
        } else {
            if (SetWindow != null) {
                SetWindow.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow_complete_custom(View parent, final String action) {
        // iniPath();
        ArrayList<Map<String, String>> MenuList = new ArrayList<Map<String, String>>();
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.main_menu_prefrences, null);
        ListView menu_list = (ListView) view.findViewById(R.id.listMenuItems);
        int groupresnum = Integer.parseInt(m_iniFileIO.getIniString(
                userIni, action, "Count", "0", (byte) 0));

        for (int i = 1; i <= groupresnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            group.put("Title", m_iniFileIO.getIniString(userIni, action,
                    "Title" + i, "", (byte) 0));
            group.put("Icon", m_iniFileIO.getIniString(userIni, action,
                    "Icon" + i, "0", (byte) 0));
            MenuList.add(group);
        }
        PopMenuAdapter MenuListAdapter = new PopMenuAdapter(MenuList,
                this);
        menu_list.setAdapter(MenuListAdapter);
        // ��listView�ļ�����
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SlidingActivity.this.iniPath();
                String url = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni, action,
                        "Url" + (arg2 + 1), "", (byte) 0);
                if (UIHelper.TbsMotion(SlidingActivity.this,
                        StringUtils.isUrl(url, SlidingActivity.this.baseUrl, SlidingActivity.this.resname))) {
                    if (UIHelper.MenuMotion(SlidingActivity.this,
                            StringUtils.isUrl(url, baseUrl, resname))) {
                        SlidingActivity.this.webview.loadUrl(StringUtils.isUrl(url, SlidingActivity.this.baseUrl,
                                SlidingActivity.this.resname));
                    } else {
                        SlidingActivity.this.SetWindow.dismiss();
                        url = url.substring(url.lastIndexOf(":") + 1);
                        if (m_iniFileIO.getIniString(userIni, url,
                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                            complete_custom(main, url);
                        } else {
                            not_complete_menu(main,
                                    url);
                        }
                        return;
                    }
                }
                SlidingActivity.this.SetWindow.dismiss();
            }
        });
        SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        SetWindow.setFocusable(true);
        SetWindow.setOutsideTouchable(false);
        SetWindow.setBackgroundDrawable(new BitmapDrawable());
        SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss() {
                SlidingActivity.this.isOpenPop = false;
            }
        });
        String location = m_iniFileIO.getIniString(userIni, action,
                "location", "right_top", (byte) 0);
        int x = screenWidth
                * Integer.parseInt(m_iniFileIO.getIniString(userIni,
                action, "location_x", "1", (byte) 0)) / 100;
        int y = screenHight
                * Integer.parseInt(m_iniFileIO.getIniString(userIni,
                action, "location_y", "0", (byte) 0)) / 100;
        if (location.equalsIgnoreCase("left_bot")) {
            SetWindow.showAtLocation(
                    parent,
                    Gravity.LEFT | Gravity.BOTTOM,
                    x,
                    y
                            + UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "poplocation",
                            top_navigate.getHeight()));
        } else {
            SetWindow.showAtLocation(
                    parent,
                    Gravity.RIGHT | Gravity.TOP,
                    x,
                    y
                            + UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "poplocation",
                            top_navigate.getHeight()) * 3 / 2);
        }
        SetWindow.update();
    }

    @SuppressWarnings("deprecation")
    private void popWindow_bottom(View parent) {
        // iniPath();
        final ArrayList<Map<String, String>> MenuList = new ArrayList<Map<String, String>>();
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.bottom_menu_layout, null);
        final ListView menu_list = (ListView) view.findViewById(R.id.listMenuItems);
        RelativeLayout bottom_cancle = (RelativeLayout) view.findViewById(R.id.bottom_cancle);

        bottom_cancle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SetWindow.dismiss();
            }
        });
        String configPath = this.getApplicationContext().getFilesDir()
                .getParentFile().getAbsolutePath();
        if (configPath.endsWith("/") == false) {
            configPath = configPath + "/";
        }
        String appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
        int itemnum = Integer.parseInt(m_iniFileIO.getIniString(appIniFile,
                "manage_menu", "Count", "0", (byte) 0));
        for (int j = 1; j <= itemnum; j++) {
            String title = m_iniFileIO.getIniString(appIniFile,
                    "manage_menu", "Title" + j, "", (byte) 0);
            String path = m_iniFileIO.getIniString(appIniFile, "manage_menu",
                    "Url" + j, "", (byte) 0);
            String pic = m_iniFileIO.getIniString(appIniFile, "manage_menu",
                    "Icon" + j, "0", (byte) 0);
            String Section = m_iniFileIO.getIniString(appIniFile, "manage_menu",
                    "Section" + j, "0", (byte) 0);
            String Key = m_iniFileIO.getIniString(appIniFile, "manage_menu",
                    "Key" + j, "", (byte) 0);
            String defaultV = m_iniFileIO.getIniString(appIniFile, "manage_menu",
                    "default" + j, "0", (byte) 0);
            int show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    Section, Key, defaultV, (byte) 0));
            if (show == 1) {
//                if (path.contains("com.")) {
//                    //包管理操作管理类
//                    PackageManager pm = this.getPackageManager();
//                    //获取到应用信息
//                    Intent it = pm.getLaunchIntentForPackage(path);
//                    if (it != null) {
//                        Map<String, String> group = new HashMap<String, String>();
//                        group.put("Title", title);
//                        group.put("Icon", pic);
//                        group.put("Url", path);
//                        MenuList.add(group);
//                    }
//                } else {
                Map<String, String> group = new HashMap<String, String>();
                group.put("Title", title);
                group.put("Icon", pic);
                group.put("Url", path);
                MenuList.add(group);
//                }
            }
        }
        PopMenuAdapter MenuListAdapter = new PopMenuAdapter(MenuList,
                this);
        menu_list.setAdapter(MenuListAdapter);
        // ��listView�ļ�����
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SlidingActivity.this.iniPath();
                if (MenuList.get(arg2).get("Url").contains("com.tbs.tbsvps")) {
                    //包管理操作管理类
                    PackageManager pm = SlidingActivity.this.getPackageManager();
                    //获取到应用信息
                    Intent it = pm.getLaunchIntentForPackage(MenuList.get(arg2).get("Url"));
                    if (it != null) {
                        it = new Intent(Intent.ACTION_MAIN);
                        it.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName("com.tbs.tbsvps", "com.tbs.tbsvps.SplashActivity");
                        it.setComponent(cn);
                        it.setFlags(101);
                        it.putExtra("iniPath", userIni);
                        startActivity(it);
                    }
                } else if (MenuList.get(arg2).get("Url").contains("com.")) {
                    //包管理操作管理类
                    PackageManager pm = SlidingActivity.this.getPackageManager();
                    //获取到应用信息
                    Intent it = pm.getLaunchIntentForPackage(MenuList.get(arg2).get("Url"));
                    if (it != null)
                        SlidingActivity.this.startActivity(it);
                } else {
                    if (UIHelper.TbsMotion(SlidingActivity.this,
                            StringUtils.isUrl(MenuList.get(arg2).get("Url"), SlidingActivity.this.baseUrl,
                                    SlidingActivity.this.resname))) {
                        if (UIHelper.MenuMotion(SlidingActivity.this,
                                StringUtils.isUrl(MenuList.get(arg2).get("Url"), baseUrl, resname))) {
                            SlidingActivity.this.webview.loadUrl(StringUtils.isUrl(MenuList.get(arg2).get("Url"),
                                    SlidingActivity.this.baseUrl,
                                    SlidingActivity.this.resname));
                        } else {
                            SlidingActivity.this.SetWindow.dismiss();
                            String url = MenuList.get(arg2).get("Url").substring(MenuList.get(arg2).get("Url")
                                    .lastIndexOf(":") + 1);
                            if (m_iniFileIO.getIniString(userIni, url,
                                    "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                                complete_custom(main, url);
                            } else {
                                not_complete_menu(main, url);
                            }
                            return;
                        }
                    }
                }
                SlidingActivity.this.SetWindow.dismiss();
            }
        });
        SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        SetWindow.setFocusable(true);
        SetWindow.setOutsideTouchable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        SetWindow.setBackgroundDrawable(dw);
        SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss() {
                SlidingActivity.this.isOpenPop = false;
            }
        });
        SetWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        SetWindow.update();
    }

    @SuppressWarnings("deprecation")
    private void popWindow_default_menu(View parent) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.main_overflow_prefrences, null);
        iniPath();
        ImageView avatar_iv = (ImageView) view.findViewById(R.id.userimage);
        TextView nickname_tv = (TextView) view.findViewById(R.id.usernickname);
        TextView district = (TextView) view.findViewById(R.id.useraccount);
        LinearLayout menu_homeBtn = (LinearLayout) view
                .findViewById(R.id.menu_homeBtn);
        LinearLayout menu_searchBtn = (LinearLayout) view
                .findViewById(R.id.menu_searchBtn);
        LinearLayout menu_goBtn = (LinearLayout) view
                .findViewById(R.id.menu_goBtn);
        LinearLayout menu_backBtn = (LinearLayout) view
                .findViewById(R.id.menu_backBtn);
        LinearLayout menu_refreshBtn = (LinearLayout) view
                .findViewById(R.id.menu_refreshBtn);
        LinearLayout menu_shareBtn = (LinearLayout) view
                .findViewById(R.id.menu_shareBtn);
        LinearLayout menu_scanBtn = (LinearLayout) view
                .findViewById(R.id.menu_scanBtn);
        LinearLayout menu_favouriteBtn = (LinearLayout) view
                .findViewById(R.id.menu_favouriteBtn);
        LinearLayout menu_appBtn = (LinearLayout) view
                .findViewById(R.id.menu_appBtn);
        LinearLayout menu_quitBtn = (LinearLayout) view
                .findViewById(R.id.menu_quitBtn);
        LinearLayout userinfo = (LinearLayout) view
                .findViewById(R.id.userinfo);
        LinearLayout menu_moreBtn = (LinearLayout) view
                .findViewById(R.id.menu_moreBtn);
        LinearLayout more_setBtn = (LinearLayout) view
                .findViewById(R.id.more_setBtn);

        more_setBtn.setOnClickListener(this);
        menu_moreBtn.setOnClickListener(this);
        menu_appBtn.setOnClickListener(this);
        menu_quitBtn.setOnClickListener(this);
        menu_homeBtn.setOnClickListener(this);
        menu_searchBtn.setOnClickListener(this);
        menu_goBtn.setOnClickListener(this);
        menu_backBtn.setOnClickListener(this);
        menu_refreshBtn.setOnClickListener(this);
        menu_shareBtn.setOnClickListener(this);
        menu_scanBtn.setOnClickListener(this);
        menu_favouriteBtn.setOnClickListener(this);
        userinfo.setOnClickListener(this);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "custom_menu",
                "login", "1", (byte) 0)) == 0) {
            userinfo.setVisibility(View.GONE);
            menu_appBtn.setBackgroundResource(R.drawable.more_up);
        } else {
            int setup = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "Login", "LoginFlag", "0", (byte) 0));
            if (setup == 1) {
                String nickName = m_iniFileIO.getIniString(userIni, "Login",
                        "NickName", "匿名", (byte) 0);
                if (StringUtils.isEmpty(nickName)) {
                    nickname_tv.setText("匿名");
                } else {
                    nickname_tv.setText(nickName);
                }
                if (StringUtils.isEmpty(m_iniFileIO.getIniString(userIni,
                        "Login", "UserCode", "", (byte) 0))) {
                    district.setText("账号:"
                            + m_iniFileIO.getIniString(userIni, "Login",
                            "Account", "", (byte) 0));
                } else {
                    district.setText("账号:"
                            + m_iniFileIO.getIniString(userIni, "Login",
                            "UserCode", "", (byte) 0));
                }
                String headPath = "http://"
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp, (byte) 0)
                        + ":"
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsPort", "8083", (byte) 0)
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsPath", "/EBS/UserServlet", (byte) 0)
                        + "?act=downloadHead&account="
                        + m_iniFileIO.getIniString(userIni, "Login", "Account",
                        "", (byte) 0);
                ImageLoader imageLoader = new ImageLoader(this, R.drawable.default_avatar);
                imageLoader.DisplayImage(headPath, avatar_iv);
            } else {
                nickname_tv.setText(R.string.sapi_click_login);
                district.setVisibility(View.GONE);
            }
        }

        int show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "myapplication", "1", (byte) 0));
        if (show == 0) {
            menu_appBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "search", "0", (byte) 0));
        if (show == 0) {
            menu_searchBtn.setVisibility(View.GONE);
        } else {
            show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "search", "search_show_in_menu", "0", (byte) 0));
            if (show == 0) {
                menu_searchBtn.setVisibility(View.GONE);
            } else {
                menu_searchBtn.setVisibility(View.VISIBLE);
            }
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "scan", "1", (byte) 0));
        if (show == 0) {
            menu_scanBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "home", "1", (byte) 0));
        if (show == 0) {
            menu_homeBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "backward", "1", (byte) 0));
        if (show == 0) {
            menu_backBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "foreword", "1", (byte) 0));
        if (show == 0) {
            menu_goBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "refresh", "1", (byte) 0));
        if (show == 0) {
            menu_refreshBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "share", "1", (byte) 0));
        if (show == 0) {
            menu_shareBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "set", "1", (byte) 0));
        if (show == 0) {
            more_setBtn.setVisibility(View.GONE);
            menu_shareBtn.setBackgroundResource(R.drawable.more_down);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "collection", "0", (byte) 0));
        if (show == 0) {
            menu_favouriteBtn.setVisibility(View.GONE);
        } else {
            show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "Collect", "collect_show_in_menu", "0", (byte) 0));
            if (show == 0) {
                menu_favouriteBtn.setVisibility(View.GONE);
            } else {
                menu_favouriteBtn.setVisibility(View.VISIBLE);
            }
        }

        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "manager", "1", (byte) 0));
        if (show == 0) {
            menu_moreBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "quit", "1", (byte) 0));
        if (show == 0) {
            menu_quitBtn.setVisibility(View.GONE);
        }
        SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        SetWindow.setFocusable(true);
        SetWindow.setOutsideTouchable(false);
        SetWindow.setBackgroundDrawable(new BitmapDrawable());
        SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                SlidingActivity.this.isOpenPop = false;
            }
        });
        SetWindow.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                UIHelper.getShareperference(this, constants.SAVE_LOCALMSGNUM,
                        "poplocation", top_navigate.getHeight()) * 3 / 2);
        SetWindow.update();
    }

    @SuppressWarnings("deprecation")
    private void popWindow_default_left_menu(View parent) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.main_overflow_prefrences, null);
        iniPath();
        ImageView avatar_iv = (ImageView) view.findViewById(R.id.userimage);
        TextView nickname_tv = (TextView) view.findViewById(R.id.usernickname);
        TextView district = (TextView) view.findViewById(R.id.useraccount);
        LinearLayout menu_homeBtn = (LinearLayout) view
                .findViewById(R.id.menu_homeBtn);
        LinearLayout menu_searchBtn = (LinearLayout) view
                .findViewById(R.id.menu_searchBtn);
        LinearLayout menu_goBtn = (LinearLayout) view
                .findViewById(R.id.menu_goBtn);
        LinearLayout menu_backBtn = (LinearLayout) view
                .findViewById(R.id.menu_backBtn);
        LinearLayout menu_refreshBtn = (LinearLayout) view
                .findViewById(R.id.menu_refreshBtn);
        LinearLayout menu_shareBtn = (LinearLayout) view
                .findViewById(R.id.menu_shareBtn);
        LinearLayout menu_scanBtn = (LinearLayout) view
                .findViewById(R.id.menu_scanBtn);
        LinearLayout menu_favouriteBtn = (LinearLayout) view
                .findViewById(R.id.menu_favouriteBtn);
        LinearLayout menu_appBtn = (LinearLayout) view
                .findViewById(R.id.menu_appBtn);
        LinearLayout userinfo = (LinearLayout) view
                .findViewById(R.id.userinfo);
        LinearLayout menu_setBtn = (LinearLayout) view
                .findViewById(R.id.more_setBtn);
        LinearLayout menu_moreBtn = (LinearLayout) view
                .findViewById(R.id.menu_moreBtn);
        LinearLayout menu_quitBtn = (LinearLayout) view
                .findViewById(R.id.menu_quitBtn);
        userinfo.setOnClickListener(this);
        menu_setBtn.setOnClickListener(this);
        menu_appBtn.setOnClickListener(this);
        menu_quitBtn.setOnClickListener(this);
        menu_homeBtn.setOnClickListener(this);
        menu_searchBtn.setOnClickListener(this);
        menu_goBtn.setOnClickListener(this);
        menu_backBtn.setOnClickListener(this);
        menu_refreshBtn.setOnClickListener(this);
        menu_shareBtn.setOnClickListener(this);
        menu_scanBtn.setOnClickListener(this);
        menu_favouriteBtn.setOnClickListener(this);
        menu_moreBtn.setOnClickListener(this);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "custom_menu",
                "left_login", "1", (byte) 0)) == 0) {
            userinfo.setVisibility(View.GONE);
            menu_appBtn.setBackgroundResource(R.drawable.more_up);
        } else {
            int setup = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "Login", "LoginFlag", "0", (byte) 0));
            if (setup == 1) {
                String nickName = m_iniFileIO.getIniString(userIni, "Login",
                        "NickName", "匿名", (byte) 0);
                if (StringUtils.isEmpty(nickName)) {
                    nickname_tv.setText("匿名");
                } else {
                    nickname_tv.setText(nickName);
                }
                if (StringUtils.isEmpty(m_iniFileIO.getIniString(userIni,
                        "Login", "UserCode", "", (byte) 0))) {
                    district.setText("账号:"
                            + m_iniFileIO.getIniString(userIni, "Login",
                            "Account", "", (byte) 0));
                } else {
                    district.setText("账号:"
                            + m_iniFileIO.getIniString(userIni, "Login",
                            "UserCode", "", (byte) 0));
                }
                String headPath = "http://"
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp, (byte) 0)
                        + ":"
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsPort", "8083", (byte) 0)
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsPath", "/EBS/UserServlet", (byte) 0)
                        + "?act=downloadHead&account="
                        + m_iniFileIO.getIniString(userIni, "Login", "Account",
                        "", (byte) 0);
                ImageLoader imageLoader = new ImageLoader(this, R.drawable.default_avatar);
                imageLoader.DisplayImage(headPath, avatar_iv);
            } else {
                nickname_tv.setText(R.string.sapi_click_login);
                district.setVisibility(View.GONE);
            }
        }
        int show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_myapplication", "1", (byte) 0));
        if (show == 0) {
            menu_appBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_manager", "1", (byte) 0));
        if (show == 0) {
            menu_moreBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_search", "0", (byte) 0));
        if (show == 0) {
            menu_searchBtn.setVisibility(View.GONE);
        } else {
            show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "search", "search_show_in_menu", "0", (byte) 0));
            if (show == 0) {
                menu_searchBtn.setVisibility(View.GONE);
            } else {
                menu_searchBtn.setVisibility(View.VISIBLE);
            }
        }

        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_scan", "1", (byte) 0));
        if (show == 0) {
            menu_scanBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_home", "1", (byte) 0));
        if (show == 0) {
            menu_homeBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_backward", "1", (byte) 0));
        if (show == 0) {
            menu_backBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_foreword", "1", (byte) 0));
        if (show == 0) {
            menu_goBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_refresh", "1", (byte) 0));
        if (show == 0) {
            menu_refreshBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_share", "1", (byte) 0));
        if (show == 0) {
            menu_shareBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_set", "1", (byte) 0));
        if (show == 0) {
            menu_setBtn.setVisibility(View.GONE);
            menu_shareBtn.setBackgroundResource(R.drawable.more_down);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_quit", "1", (byte) 0));
        if (show == 0) {
            menu_quitBtn.setVisibility(View.GONE);
        }
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "custom_menu", "left_collection", "0", (byte) 0));
        if (show == 0) {
            menu_favouriteBtn.setVisibility(View.GONE);
        } else {
            show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "Collect", "collect_show_in_menu", "0", (byte) 0));
            if (show == 0) {
                menu_favouriteBtn.setVisibility(View.GONE);
            }
        }
        SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        SetWindow.setFocusable(true);
        SetWindow.setOutsideTouchable(false);
        SetWindow.setBackgroundDrawable(new BitmapDrawable());
        SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                SlidingActivity.this.isOpenPop = false;
            }
        });
        SetWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, 10,
                UIHelper.getShareperference(this, constants.SAVE_LOCALMSGNUM,
                        "poplocation", top_navigate.getHeight()) * 3 / 2);
        SetWindow.update();
    }

    public void not_complete_menu(View v, String action) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            popWindow_not_complete_menu(v, action);
        } else {
            if (SetWindow != null) {
                SetWindow.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow_not_complete_menu(View parent, final String action) {
        // iniPath();
        ArrayList<Map<String, String>> MenuList = new ArrayList<Map<String, String>>();
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.main_menu_prefrences, null);
        // iniPath();
        ImageView avatar_iv = (ImageView) view.findViewById(R.id.userimage);
        TextView nickname_tv = (TextView) view.findViewById(R.id.usernickname);
        TextView district = (TextView) view.findViewById(R.id.useraccount);
        LinearLayout userinfo = (LinearLayout) view.findViewById(R.id.userinfo);
        ListView menu_list = (ListView) view.findViewById(R.id.listMenuItems);
        userinfo.setOnClickListener(this);

        LinearLayout menu_appBtn = (LinearLayout) view
                .findViewById(R.id.menu_appBtn);
        int show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "myapplication_show_in_menu", "Store", "1", (byte) 0));
        if (show == 1) {
            menu_appBtn.setVisibility(View.VISIBLE);
        }
        LinearLayout menu_emailBtn = (LinearLayout) view
                .findViewById(R.id.menu_emailBtn);
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                "sourceStore_show_in_menu", "0", (byte) 0));
        if (show == 1) {
            menu_emailBtn.setVisibility(View.VISIBLE);
        }
        LinearLayout menu_skydriveBtn = (LinearLayout) view
                .findViewById(R.id.menu_skydriveBtn);
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "Skydrive", "myDrive_show_in_menu", "0", (byte) 0));
        if (show == 1) {
            menu_skydriveBtn.setVisibility(View.VISIBLE);
        }
        LinearLayout menu_sourceBtn = (LinearLayout) view
                .findViewById(R.id.menu_sourceBtn);
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "Store", "mysource_show_in_menu", "0", (byte) 0));
        if (show == 1)
            menu_sourceBtn.setVisibility(View.VISIBLE);
        LinearLayout menu_deviceBtn = (LinearLayout) view
                .findViewById(R.id.menu_deviceBtn);
        show = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                "store_show_in_menu", "1", (byte) 0));
        if (show == 1) {
            menu_deviceBtn.setVisibility(View.VISIBLE);
        }
        menu_deviceBtn.setOnClickListener(this);
        menu_skydriveBtn.setOnClickListener(this);
        menu_emailBtn.setOnClickListener(this);
        menu_sourceBtn.setOnClickListener(this);
        menu_appBtn.setOnClickListener(this);
        int groupresnum = Integer.parseInt(m_iniFileIO.getIniString(
                userIni, action, "Count", "0", (byte) 0));

        for (int i = 1; i <= groupresnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            group.put("Title", m_iniFileIO.getIniString(userIni, action,
                    "Title" + i, "", (byte) 0));
            group.put("Icon", m_iniFileIO.getIniString(userIni, action,
                    "Icon" + i, "0", (byte) 0));
            MenuList.add(group);
        }
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "menuShowUser", "1", (byte) 0)) == 1) {
            int setup = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                    "Login", "LoginFlag", "0", (byte) 0));
            if (setup == 1) {
                String nickName = m_iniFileIO.getIniString(userIni, "Login",
                        "NickName", "匿名", (byte) 0);
                if (StringUtils.isEmpty(nickName)) {
                    nickname_tv.setText("匿名");
                } else {
                    nickname_tv.setText(nickName);
                }
                if (StringUtils.isEmpty(m_iniFileIO.getIniString(userIni,
                        "Login", "UserCode", "", (byte) 0))) {
                    district.setText("账号:"
                            + m_iniFileIO.getIniString(userIni, "Login",
                            "Account", "", (byte) 0));
                } else {
                    district.setText("账号:"
                            + m_iniFileIO.getIniString(userIni, "Login",
                            "UserCode", "", (byte) 0));
                }
                String headPath = "http://"
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp, (byte) 0)
                        + ":"
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsPort", "8083", (byte) 0)
                        + m_iniFileIO.getIniString(userIni, "Login",
                        "ebsPath", "/EBS/UserServlet", (byte) 0)
                        + "?act=downloadHead&account="
                        + m_iniFileIO.getIniString(userIni, "Login", "Account",
                        "", (byte) 0);
                ImageLoader imageLoader = new ImageLoader(this, R.drawable.default_avatar);
                imageLoader.DisplayImage(headPath, avatar_iv);
            } else {
                nickname_tv.setText(R.string.sapi_click_login);
                district.setVisibility(View.GONE);
            }
            userinfo.setVisibility(View.VISIBLE);
            MenuListAdapter MenuListAdapter = new MenuListAdapter(MenuList,
                    this);
            menu_list.setAdapter(MenuListAdapter);
        } else {
            userinfo.setVisibility(View.GONE);
            PopMenuAdapter MenuListAdapter = new PopMenuAdapter(MenuList,
                    this);
            menu_list.setAdapter(MenuListAdapter);
        }

        // ��listView�ļ�����
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SlidingActivity.this.iniPath();
                String url = SlidingActivity.this.m_iniFileIO.getIniString(SlidingActivity.this.userIni, action,
                        "Url" + (arg2 + 1), "", (byte) 0);
                if (UIHelper.TbsMotion(SlidingActivity.this,
                        StringUtils.isUrl(url, SlidingActivity.this.baseUrl, SlidingActivity.this.resname))) {
                    if (UIHelper.MenuMotion(SlidingActivity.this,
                            StringUtils.isUrl(url, baseUrl, resname))) {
                        SlidingActivity.this.webview.loadUrl(StringUtils.isUrl(url, SlidingActivity.this.baseUrl,
                                SlidingActivity.this.resname));
                    } else {
                        SlidingActivity.this.SetWindow.dismiss();
                        url = url.substring(url.lastIndexOf(":") + 1);
                        if (m_iniFileIO.getIniString(userIni, url,
                                "defaults", "false", (byte) 0).equalsIgnoreCase("false")) {
                            complete_custom(main, url);
                        } else {
                            not_complete_menu(main,
                                    url);
                        }
                        return;
                    }
                }
                SlidingActivity.this.SetWindow.dismiss();
            }
        });
        SetWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        SetWindow.setFocusable(true);
        SetWindow.setOutsideTouchable(false);
        SetWindow.setBackgroundDrawable(new BitmapDrawable());
        SetWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss() {
                SlidingActivity.this.isOpenPop = false;
            }
        });
        String location = m_iniFileIO.getIniString(userIni, action,
                "location", "right_top", (byte) 0);
        int x = screenWidth
                * Integer.parseInt(m_iniFileIO.getIniString(userIni,
                action, "location_x", "1", (byte) 0)) / 100;
        int y = screenHight
                * Integer.parseInt(m_iniFileIO.getIniString(userIni,
                action, "location_y", "0", (byte) 0)) / 100;
        if (location.equalsIgnoreCase("left_bot")) {
            SetWindow.showAtLocation(
                    parent,
                    Gravity.LEFT | Gravity.BOTTOM,
                    x,
                    y
                            + UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "poplocation",
                            top_navigate.getHeight()));
        } else {
            SetWindow.showAtLocation(
                    parent,
                    Gravity.RIGHT | Gravity.TOP,
                    x,
                    y
                            + UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "poplocation",
                            top_navigate.getHeight()) * 3 / 2);
        }
        SetWindow.update();
    }

    // �����˵�����¼�������
    class SlideMenuChangeListener implements ViewPager.OnPageChangeListener
    {
        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageSelected(int arg0) {
            int pageCount = SlidingActivity.this.menuViews.size() - 1;
            SlidingActivity.this.pagerIndex = arg0;
            // ��ʾ�ұߵ���ͼƬ
            if (arg0 >= 0 && arg0 < pageCount) {
                SlidingActivity.this.imageNext.setVisibility(View.VISIBLE);
            } else {
                SlidingActivity.this.imageNext.setVisibility(View.INVISIBLE);
            }
            // ��ʾ��ߵ���ͼƬ
            if (arg0 > 0 && arg0 <= pageCount) {
                SlidingActivity.this.imagePrevious.setVisibility(View.VISIBLE);
            } else {
                SlidingActivity.this.imagePrevious.setVisibility(View.INVISIBLE);
            }
        }
    }

    // �����˵����������
    class SlideMenuAdapter extends PagerAdapter
    {
        public List<View> mListViews;

        public SlideMenuAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            //System.out.println("instant = " + arg1);
            return mListViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
        }
    }

    // �ҵ���ͼƬ��ť�¼�
    class ImageNextOnclickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            SlidingActivity.this.pagerIndex++;
            SlidingActivity.this.viewPager.setCurrentItem(SlidingActivity.this.pagerIndex);
        }
    }

    // �󵼺�ͼƬ��ť�¼�
    class ImagePreviousOnclickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            SlidingActivity.this.pagerIndex--;
            SlidingActivity.this.viewPager.setCurrentItem(SlidingActivity.this.pagerIndex);
        }
    }

    public void startAnimation() {
        loadingAnima = (AnimationDrawable) iv.getBackground();
        loadingAnima.start();
        loadingIV.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        // loadingAnima.stop();
        loadingIV.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        return mGestureDetector.onTouchEvent(arg1);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                           float arg3) {
        int num = 0;
        float diffX = arg0.getX() - arg1.getX();
        float diffY = arg0.getY() - arg1.getY();
        // TODO Auto-generated method stub
        if (diffY > webview.getHeight() / 5 && Math.abs(arg2) > 30) {
            webview.loadUrl("javascript:jump()");
        } else if (diffY < -(webview.getHeight() / 2) && Math.abs(arg2) > 30) {
            webview.loadUrl("javascript:OnUpdate()");
        } else {

            if (diffX > webview.getWidth() / 5
                    && Math.abs(diffX) > Math.abs(diffY) && Math.abs(arg2) > 20) {
                if (pagenum == 0) {
                    // ��ת��һ��
                    num = menuBar.moveleft();
                    if (4 == num || 8 == num || 12 == num || 16 == num
                            || 20 == num) {
                        if (pagerIndex < n) {
                            pagerIndex++;
                            viewPager.setCurrentItem(pagerIndex);
                        }
                    }
                    tempUrl = StringUtils.isUrl(Botscan, baseUrl, resname)
                            + "&Mycondition=" + constants.menus[num];
                    webview.loadUrl(tempUrl);
                } else {
                    webview.loadUrl("javascript:tonext()");
                    webview.loadUrl("javascript:jumppage(3)");
                }
                // }
            } else if (diffX < -(webview.getWidth() / 5)
                    && Math.abs(diffX) > Math.abs(diffY) && Math.abs(arg2) > 20) {
                // ��ת��һ��
                if (pagenum == 0) {
                    num = menuBar.moveright();
                    if (3 == num || 7 == num || 11 == num || 15 == num
                            || 19 == num) {
                        pagerIndex--;
                        viewPager.setCurrentItem(pagerIndex);
                    }
                    tempUrl = StringUtils.isUrl(Botscan, baseUrl, resname)
                            + "&Mycondition=" + constants.menus[num];
                    webview.loadUrl(tempUrl);
                } else {
                    webview.loadUrl("javascript:toprev()");
                    // System.out.println("javascript:tonext(q)");
                    webview.loadUrl("javascript:jumppage(2)");
                }
            }

        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
                            float arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * 重置左右窗口的宽度
     */
    private void iniWidth() {
        int SplitRatio = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "LeftWidth", "50", (byte) 0));
        int SplitRatioRight = Integer.parseInt(m_iniFileIO.getIniString(
                userIni, "APPSHOW", "RightWidth", "50", (byte) 0));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        SlidingActivity.mSlidingMenu.setBehindOffset(screenWidth * (100 - SplitRatio) / 100);// 设置左菜单宽度
        SlidingActivity.mSlidingMenu.setRightMenuOffset(screenWidth * SplitRatioRight / 100);// 设置右菜单宽度
    }

    /**
     * 防止在旋转时，重启Activity的onCreate方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        int SplitRatio = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "APPSHOW", "LeftWidth", "50", (byte) 0));
        int SplitRatioRight = Integer.parseInt(m_iniFileIO.getIniString(
                userIni, "APPSHOW", "RightWidth", "50", (byte) 0));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏的操作
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏的操作
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
        }
        SlidingActivity.mSlidingMenu.setBehindOffset(screenWidth * (100 - SplitRatio) / 100);// 设置左菜单宽度
        SlidingActivity.mSlidingMenu.setRightMenuOffset(screenWidth * SplitRatioRight / 100);// 设置右菜单宽度
    }
}
