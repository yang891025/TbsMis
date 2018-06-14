package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.cbs.CBSInterpret;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.update.DownloadAsyncTask;
import com.tbs.tbsmis.update.UpdateManager;
import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.DES;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("HandlerLeak")
@SuppressWarnings("deprecation")
public class InitializeToolbarActivity extends TabActivity implements View.OnClickListener,
        View.OnTouchListener, GestureDetector.OnGestureListener
{
    private RadioButton navigation, setting;
    private RadioButton home, search, mananger;
    private TabHost mTabHost;
    public FrameLayout main_radio;
    public RelativeLayout buttom_message, Prompt;
    private TextView msgView;
    private WebView web_view;
    private GestureDetector mTbsGestureDetector;
    private InitializeToolbarActivity.MyBroadcastReciver MyBroadcastReciver;
    private IniFile m_iniFileIO;
    private String AppName, PromptMsg;
    private String resname;
    private String DownloadResname;
    private ImageView msgBtn;
    private LinearLayout.LayoutParams laParams;
    private int screenHeight;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private boolean loadingDialogState;
    private String appNewsFile;
    private String baseUrl;
    private String WebIniFile;
    private String rootPath;
    protected CBSInterpret mInterpret;
    private String userIni;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.maintabs_activity);
        MyActivity.getInstance().addActivity(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("loadView"
                + getString(R.string.about_title));
        this.MyBroadcastReciver = new InitializeToolbarActivity.MyBroadcastReciver();
        this.mTbsGestureDetector = new GestureDetector(this);
        registerReceiver(this.MyBroadcastReciver, intentFilter);
        this.mTabHost = this.getTabHost();
        this.initRadios();
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
                "OFFLINESETTING", "auto", "0", (byte) 0)) == 1) {
//            this.startService(new Intent(
//                    this.getString(string.ServerName)));
            Intent mIntent = new Intent();
            mIntent.setAction(this
                    .getString(R.string.ServerName));//你定义的service的action
            mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
            this.startService(mIntent);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
                "autoStarTask", "0", (byte) 0)) == 1) {
//            this.startService(new Intent(
//                    this.getString(string.TbsTaskServer)));
            Intent mIntent = new Intent();
            mIntent.setAction(this
                    .getString(R.string.TbsTaskServer));//你定义的service的action
            mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
            this.startService(mIntent);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
                "serverMarks", "4", (byte) 0)) == 0
                && Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "SERVICE", "webRestart", "0", (byte) 0)) == 1) {
//            this.startService(new Intent(
//                    this.getString(string.WebServerName)));
            Intent mIntent = new Intent();
            mIntent.setAction(this
                    .getString(R.string.WebServerName));//你定义的service的action
            mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
            this.startService(mIntent);

        }
        if (UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "check_update", 1) == 1) {
            //System.out.println("~~~~~~~~更新~~~~~~~~~~");
            UpdateManager.getUpdateManager().checkAppUpdate(this, false);
        }
        UIHelper.setSharePerference(this,
                constants.SAVE_LOCALMSGNUM, "downflag", 0);
        UIHelper.setSharePerference(this,
                Constants.SHARED_PREFERENCE_NAME, "appOn", 1);
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void initRadios() {
        this.iniPath();
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "Login",
                "LoginType", "0", (byte) 0)) == 0) {
            this.m_iniFileIO.writeIniString(this.userIni, "Login", "LoginFlag", "0");
            this.m_iniFileIO.writeIniString(this.userIni, "Login", "LoginId", "");

            if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "autoLogin", "0", (byte) 0)) == 1) {
                this.connect(0);
            } else {
                if (UIHelper.getShareperference(this,
                        Constants.SHARED_PREFERENCE_NAME,
                        Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                    ServiceManager serviceManager = new ServiceManager(
                            this);
                    serviceManager.setNotificationIcon(R.drawable.notification);
                    serviceManager.restartService();
                }
            }
        } else {
            if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login", "LoginFlag", "0", (byte) 1)) ==
                    0) {
                this.m_iniFileIO.writeIniString(this.userIni, "Login", "LoginId", "");
                if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                        "autoLogin", "0", (byte) 0)) == 1) {
                    this.connect(0);
                } else {
                    if (UIHelper.getShareperference(this,
                            Constants.SHARED_PREFERENCE_NAME,
                            Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                        ServiceManager serviceManager = new ServiceManager(
                                this);
                        serviceManager.setNotificationIcon(R.drawable.notification);
                        serviceManager.restartService();
                    }
                }
            } else {
                this.connect(1);
            }
        }
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "UpdateSysConfig", "1", (byte) 0));
        if (nVal == 1) {
            GetSystemIniThread();
        }
        this.buttom_message = (RelativeLayout) this.findViewById(R.id.buttom_message);
        this.Prompt = (RelativeLayout) this.findViewById(R.id.Prompt);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.msgView = (TextView) this.findViewById(R.id.msgView);
        this.web_view = (WebView) this.findViewById(R.id.msg_webview);
        this.web_view.getSettings().setSupportZoom(true);
        this.web_view.getSettings().setBuiltInZoomControls(true);
        this.web_view.getSettings().setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.web_view.getSettings().setDisplayZoomControls(false);
        }
        this.web_view.getSettings().setSupportMultipleWindows(true);
        this.web_view.getSettings().setDefaultTextEncodingName("gb2312");
        this.web_view.getSettings().setDomStorageEnabled(true);

        this.web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.web_view.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.web_view.setOnTouchListener(this);
        this.web_view.setWebViewClient(UIHelper.getWebViewClient());
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.web_view.getSettings().getUserAgentString();
        this.web_view.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        UIHelper.addJavascript(this, this.web_view);
        this.web_view.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                InitializeToolbarActivity.this.loadingDialogState = progress < 100;

                if (InitializeToolbarActivity.this.loadingDialogState) {
                    InitializeToolbarActivity.this.startAnimation();
                } else {
                    InitializeToolbarActivity.this.stopAnimation();
                }
            }
        });
        this.msgBtn = (ImageView) this.findViewById(R.id.Button01);
        this.main_radio = (FrameLayout) this.findViewById(R.id.main_radio);
        this.navigation = (RadioButton) this.findViewById(R.id.radio_home);
        this.home = (RadioButton) this.findViewById(R.id.radio_mention);
        this.setting = (RadioButton) this.findViewById(R.id.radio_more);
        this.search = (RadioButton) this.findViewById(R.id.radio_favourite);
        this.mananger = (RadioButton) this.findViewById(R.id.radio_manager);
        this.mananger.setOnClickListener(this);
        this.navigation.setOnClickListener(this);
        this.home.setOnClickListener(this);
        this.setting.setOnClickListener(this);
        this.search.setOnClickListener(this);
        this.msgBtn.setOnClickListener(this);
        // mTabHost.setCurrentTabByTag(constants.FAVOURITE_TAB);
        this.initlayout();

    }

    private void initlayout() {
        // TODO Auto-generated method stub
        int ButtonShowflag = Integer.parseInt(this.m_iniFileIO.getIniString(
                this.appNewsFile, "APPSHOW", "ButtonMode", "0", (byte) 0));
        Drawable img_on;
        Resources res = this.getResources();
        switch (ButtonShowflag) {
            case 0:
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "NavigationImageId", "0", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.navigation.setCompoundDrawables(null, img_on, null, null);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "BOT3ImageId", "3", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.mananger.setCompoundDrawables(null, img_on, null, null);
                // mananger.setCompoundDrawablePadding(-3);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "MoreBtnImageId", "4", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.setting.setCompoundDrawables(null, img_on, null, null);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "BOT2ImageId", "2", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.search.setCompoundDrawables(null, img_on, null, null);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "BOT1ImageId", "1", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.home.setCompoundDrawables(null, img_on, null, null);
                break;
            case 1:
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "NavigationImageId", "0", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.navigation.setCompoundDrawables(img_on, null, null, null);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "BOT3ImageId", "3", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.mananger.setCompoundDrawables(img_on, null, null, null);
                // mananger.setCompoundDrawablePadding(-3);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "MoreBtnImageId", "4", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.setting.setCompoundDrawables(img_on, null, null, null);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "BOT2ImageId", "2", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.search.setCompoundDrawables(img_on, null, null, null);
                img_on = res.getDrawable(constants.ButtonImageId[Integer
                        .parseInt(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                                "BOT1ImageId", "1", (byte) 0))]);
                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                        img_on.getMinimumHeight());
                this.home.setCompoundDrawables(img_on, null, null, null);
                break;
            case 2:
                LinearLayout.LayoutParams laParams = (LinearLayout.LayoutParams) this.main_radio.getLayoutParams();
                laParams.height = 52;
                this.main_radio.setLayoutParams(laParams);
                break;

        }
        String resname = this.m_iniFileIO.getIniString(this.userIni, "TBSAPP",
                "resname", "", (byte) 0);
        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                "resname", resname);
        Intent intent = new Intent();
        intent.setClass(this, SlidingActivity.class);
        if (this.getIntent().getExtras() != null) {
            Intent notificationIntent = this.getIntent();
            int flag = notificationIntent.getIntExtra("flag", 0);
            if (flag == 0) {
                String notificationId = notificationIntent
                        .getStringExtra(Constants.NOTIFICATION_ID);
                String notificationTitle = notificationIntent
                        .getStringExtra(Constants.NOTIFICATION_TITLE);
                String notificationUri = notificationIntent
                        .getStringExtra(Constants.NOTIFICATION_URI);
                intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
                intent.putExtra(Constants.NOTIFICATION_TITLE, notificationTitle);
                intent.putExtra(Constants.NOTIFICATION_URI, notificationUri);
            } else if (flag == 1) {
                intent.putExtra("flag", flag);
            } else if (flag == 2) {
                intent.putExtra("flag", flag);
            }
        }
        // sendBroadcast(intent);
        this.mTabHost.addTab(this.buildTabSpec(constants.MENTION_TAB, intent));
        Intent intent1 = new Intent();
        intent1.setAction("Action_main" + getString(R.string.about_title));
        intent1.putExtra("pagenum", 1);
        intent1.putExtra("navigatoin", 0);
        intent1.putExtra("flag", 15);
        this.sendBroadcast(intent1);
        int ShowBot = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "ShowBOT1", "0", (byte) 0));
        if (ShowBot == 1) {
            String Title = this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                    "BOT1Txt", "浏览", (byte) 0);
            this.home.setText(Title);
            this.home.setVisibility(View.VISIBLE);
        } else {
            this.home.setVisibility(View.GONE);
        }
        ShowBot = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "ShowBOT2", "0", (byte) 0));
        if (ShowBot == 1) {
            String Title = this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                    "BOT2Txt", "检索", (byte) 0);
            this.search.setText(Title);
            this.search.setVisibility(View.VISIBLE);
        } else {
            this.search.setVisibility(View.GONE);
        }
        ShowBot = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "ShowBOT3", "0", (byte) 0));
        if (ShowBot == 1) {
            String Title = this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                    "BOT3Txt", "订阅", (byte) 0);
            this.mananger.setText(Title);
            this.mananger.setVisibility(View.VISIBLE);
        } else {
            this.mananger.setVisibility(View.GONE);
        }
        int MoreBtnflag = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "MoreBtn", 1);
        if (MoreBtnflag == 1) {
            this.setting.setText(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                    "MoreBtnTxt", "更多", (byte) 0));
            this.setting.setVisibility(View.VISIBLE);
        } else {
            this.setting.setVisibility(View.GONE);
        }
        int NavigationBtn = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "NavigationBtn", 1);
        if (NavigationBtn == 1) {
            this.navigation.setText(this.m_iniFileIO.getIniString(this.userIni, "BUTTON",
                    "NavigationTxt", "导航", (byte) 0));
            this.navigation.setVisibility(View.VISIBLE);
        } else {
            this.navigation.setVisibility(View.GONE);
        }
        ShowBot = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "StatusLine", "0", (byte) 0));
        if (ShowBot == 1) {
            this.buttom_message.setVisibility(View.VISIBLE);
            String ShowMsg = this.m_iniFileIO.getIniString(this.userIni, "APPSHOW",
                    "ShowMsg", "", (byte) 0);
            this.msgView.setVisibility(View.VISIBLE);
            this.msgView.setText(ShowMsg);
        }else{
            buttom_message.setVisibility(View.GONE);
            msgView.setVisibility(View.GONE);
        }
        this.msgView.setSelected(true);
        ShowBot = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "ToolSet", "0", (byte) 0));
        if (ShowBot == 1) {
            this.main_radio.setVisibility(View.VISIBLE);
        } else {
            this.main_radio.setVisibility(View.GONE);
        }
        ShowBot = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "Prompt", "0", (byte) 0));
        if (ShowBot == 1) {
            this.msgBtn.setVisibility(View.VISIBLE);
        } else {
            this.Prompt.setVisibility(View.GONE);
            UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                    "bottom_window", 0);
            this.msgBtn.setVisibility(View.GONE);
        }
        int SplitRatio = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "PromptHight", "50", (byte) 0));
        this.laParams = (LinearLayout.LayoutParams) this.Prompt.getLayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenHeight = dm.heightPixels;
        this.laParams.height = this.screenHeight * SplitRatio / 100;
        this.Prompt.setLayoutParams(this.laParams);
        this.PromptMsg = this.m_iniFileIO.getIniString(this.userIni, "MSGURL",
                "PromptMsg", "", (byte) 0);
        resname = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
        String protUrl;
        protUrl = StringUtils.isUrl(this.PromptMsg, this.baseUrl, resname);
        if (UIHelper.TbsMotion(this, protUrl)) {
            this.web_view.loadUrl(protUrl);
        }
    }

    private void iniPath() {
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
        this.rootPath = webRoot;
        this.WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appNewsFile = webRoot
                + this.m_iniFileIO.getIniString(this.WebIniFile, "TBSWeb", "IniName",
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
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
                "currentAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
                "currentPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
    }

    private TabSpec buildTabSpec(String tag, Intent intent) {
        TabSpec tabSpec = this.mTabHost.newTabSpec(tag);
        tabSpec.setContent(intent).setIndicator("",
                this.getResources().getDrawable(R.drawable.ic_launcher));
        return tabSpec;
    }

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            InitializeToolbarActivity.this.iniPath();
            Button downloadBtn = (Button) InitializeToolbarActivity.this.findViewById(R.id.button1);
            Button cancleBtn = (Button) InitializeToolbarActivity.this.findViewById(R.id.buttom_cancle);
            TextView loadView = (TextView) InitializeToolbarActivity.this.findViewById(R.id.loadView);
            loadView.setSelected(true);
            if (action.equals("loadView"
                    + getString(R.string.about_title))) {
                int SelBtn = intent.getIntExtra("flag", 1);
                int author = intent.getIntExtra("author", 1);
                switch (SelBtn) {
                    case 2:
                        if (author == 2) {
                            if (InitializeToolbarActivity.this.Prompt.getVisibility() == View.GONE) {
                                InitializeToolbarActivity.this.Prompt.setVisibility(View.VISIBLE);
                                InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                        .popupbox_radio_button_checked);
                                InitializeToolbarActivity.this.PromptMsg = InitializeToolbarActivity.this.m_iniFileIO
                                        .getIniString
                                                (InitializeToolbarActivity.this.userIni,
                                                        "MSGURL", "PromptMsg", "", (byte) 0);
                                InitializeToolbarActivity.this.resname = UIHelper.getShareperference(
                                        InitializeToolbarActivity.this,
                                        constants.SAVE_LOCALMSGNUM, "resname",
                                        "yqxx");
                                String protUrl;
                                protUrl = StringUtils.isUrl(InitializeToolbarActivity.this.PromptMsg,
                                        InitializeToolbarActivity.this
                                                .baseUrl,
                                        InitializeToolbarActivity.this.resname);
                                InitializeToolbarActivity.this.web_view.loadUrl(protUrl);
                                UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                        constants.SAVE_LOCALMSGNUM,
                                        "bottom_window", 1);
                            } else {
                                InitializeToolbarActivity.this.Prompt.setVisibility(View.GONE);
                                UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                        constants.SAVE_LOCALMSGNUM,
                                        "bottom_window", 0);
                                InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                        .popupbox_radio_button_checked_right);
                            }
                        } else {
                            InitializeToolbarActivity.this.Prompt.setVisibility(View.VISIBLE);
                            InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                    .popupbox_radio_button_checked);
                            InitializeToolbarActivity.this.PromptMsg = InitializeToolbarActivity.this.m_iniFileIO
                                    .getIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "MSGURL", "PromptMsg", "0", (byte) 0);
                            InitializeToolbarActivity.this.resname = UIHelper.getShareperference(
                                    InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
                            String protUrl;
                            protUrl = StringUtils
                                    .isUrl(InitializeToolbarActivity.this.PromptMsg, InitializeToolbarActivity.this
                                                    .baseUrl,
                                            InitializeToolbarActivity.this.resname);
                            if (author == 1) {
                                if (UIHelper.TbsMotion(InitializeToolbarActivity.this,
                                        protUrl)) {
                                    InitializeToolbarActivity.this.web_view.loadUrl(protUrl);
                                }
                            }
                            UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "bottom_window", 1);
                        }

                        break;
                    case 3:
                        if (author == 1) {
                            InitializeToolbarActivity.this.main_radio.setVisibility(View.GONE);
                        }
                        if (author == 2) {
                            InitializeToolbarActivity.this.main_radio.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 4:
                        if (author == 1) {
                            InitializeToolbarActivity.this.buttom_message.setVisibility(View.GONE);
                            InitializeToolbarActivity.this.Prompt.setVisibility(View.GONE);
                            UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "bottom_window", 0);
                        } else if (author == 2) {
                            InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                    .popupbox_radio_button_checked_right);
                            InitializeToolbarActivity.this.buttom_message.setVisibility(View.VISIBLE);
                        } else if (author == 3) {
                            InitializeToolbarActivity.this.Prompt.setVisibility(View.VISIBLE);
                            UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "bottom_window", 1);
                            InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                    .popupbox_radio_button_checked);
                        } else if (author == 4) {
                            InitializeToolbarActivity.this.Prompt.setVisibility(View.GONE);
                            UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "bottom_window", 0);
                            InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                    .popupbox_radio_button_checked_right);
                        }
                        break;
                    case 5:
                        if (author == 1) {
                            int ShowBot = Integer.parseInt(InitializeToolbarActivity.this.m_iniFileIO
                                    .getIniString(InitializeToolbarActivity.this.userIni, "APPSHOW",
                                            "ShowBOT1", "0", (byte) 0));
                            if (ShowBot == 1) {
                                InitializeToolbarActivity.this.home.setText(InitializeToolbarActivity.this
                                        .m_iniFileIO.getIniString
                                                (InitializeToolbarActivity.this.userIni,
                                                        "BUTTON", "BOT1Txt", "浏览", (byte) 0));
                                InitializeToolbarActivity.this.home.setVisibility(View.VISIBLE);
                            } else {
                                InitializeToolbarActivity.this.home.setVisibility(View.GONE);
                            }
                            ShowBot = Integer.parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                    InitializeToolbarActivity.this.userIni, "APPSHOW", "ShowBOT2", "0",
                                    (byte) 0));
                            if (ShowBot == 1) {
                                InitializeToolbarActivity.this.search.setText(InitializeToolbarActivity.this
                                        .m_iniFileIO.getIniString(
                                                InitializeToolbarActivity.this.userIni, "BUTTON", "BOT2Txt", "检索",
                                                (byte) 0));
                                InitializeToolbarActivity.this.search.setVisibility(View.VISIBLE);
                            } else {
                                InitializeToolbarActivity.this.search.setVisibility(View.GONE);
                            }
                            ShowBot = Integer.parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                    InitializeToolbarActivity.this.userIni, "APPSHOW", "ShowBOT3", "0",
                                    (byte) 0));
                            if (ShowBot == 1) {
                                InitializeToolbarActivity.this.mananger.setVisibility(View.VISIBLE);
                                InitializeToolbarActivity.this.mananger.setText(InitializeToolbarActivity.this
                                        .m_iniFileIO.getIniString(
                                                InitializeToolbarActivity.this.userIni, "BUTTON", "BOT3Txt", "订阅",
                                                (byte) 0));
                            } else {
                                InitializeToolbarActivity.this.mananger.setVisibility(View.GONE);
                            }
                            int MoreBtnflag = UIHelper.getShareperference(
                                    InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "MoreBtn", 1);
                            if (MoreBtnflag == 1) {
                                InitializeToolbarActivity.this.setting.setText(InitializeToolbarActivity.this
                                        .m_iniFileIO.getIniString(
                                                InitializeToolbarActivity.this.userIni, "BUTTON", "MoreBtnTxt", "更多",
                                                (byte) 0));
                                InitializeToolbarActivity.this.setting.setVisibility(View.VISIBLE);
                            } else {
                                InitializeToolbarActivity.this.setting.setVisibility(View.GONE);
                            }
                            int NavigationBtn = UIHelper.getShareperference(
                                    InitializeToolbarActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "NavigationBtn", 1);
                            if (NavigationBtn == 1) {
                                InitializeToolbarActivity.this.navigation.setText(InitializeToolbarActivity.this
                                        .m_iniFileIO.getIniString(
                                                InitializeToolbarActivity.this.userIni, "BUTTON", "NavigationTxt",
                                                "导航", (byte) 0));
                                InitializeToolbarActivity.this.navigation.setVisibility(View.VISIBLE);
                            } else {
                                InitializeToolbarActivity.this.navigation.setVisibility(View.GONE);
                            }
                            // }
                        } else if (author == 0) {
                            InitializeToolbarActivity.this.initlayout();
                        }
                        break;
                    case 6:
                        int value = intent.getIntExtra("value", 50);
                        if (author == 1) {
                            InitializeToolbarActivity.this.laParams.height = InitializeToolbarActivity.this
                                    .screenHeight * value / 100;
                            InitializeToolbarActivity.this.Prompt.setLayoutParams(InitializeToolbarActivity.this
                                    .laParams);
                        }
                        break;
                    case 7:
                        if (author == 1) {
                            InitializeToolbarActivity.this.msgBtn.setVisibility(View.VISIBLE);
                            InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                    .popupbox_radio_button_checked_right);
                        } else if (author == 2) {
                            // Prompt.setVisibility(View.GONE);
                            InitializeToolbarActivity.this.msgBtn.setVisibility(View.GONE);
                        }
                        break;
                    case 8:
                        InitializeToolbarActivity.this.Prompt.setVisibility(View.VISIBLE);
                        InitializeToolbarActivity.this.msgBtn.setBackgroundResource(R.drawable
                                .popupbox_radio_button_checked);
                        String tempUrl = intent.getStringExtra("tempUrl");
                        tempUrl = StringUtils.isUrl(tempUrl, InitializeToolbarActivity.this.baseUrl, UIHelper
                                .getShareperference(context,
                                        constants.SAVE_LOCALMSGNUM, "resname",
                                        "yqxx"));
                        if (author == 1) {
                            if (UIHelper.TbsMotion(InitializeToolbarActivity.this, tempUrl)) {
                                InitializeToolbarActivity.this.web_view.loadUrl(tempUrl);
                            }
                        }
                        UIHelper.setSharePerference(InitializeToolbarActivity.this,
                                constants.SAVE_LOCALMSGNUM, "bottom_window", 1);
                        break;
                    case 9:
                        Drawable img_on;
                        Resources res = InitializeToolbarActivity.this.getResources();
                        int ButtonShowflag = Integer.parseInt(InitializeToolbarActivity.this.m_iniFileIO
                                .getIniString(InitializeToolbarActivity.this.userIni, "APPSHOW", "ButtonMode",
                                        "0", (byte) 0));
                        switch (ButtonShowflag) {
                            case 0:
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "NavigationImageId", "0",
                                                        (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.navigation.setCompoundDrawables(null, img_on, null,
                                        null);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "BOT3ImageId", "3", (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.mananger.setCompoundDrawables(null, img_on, null, null);
                                // mananger.setCompoundDrawablePadding(-3);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO
                                                        .getIniString(InitializeToolbarActivity.this.userIni,
                                                                "BUTTON",
                                                                "MoreBtnImageId", "4",
                                                                (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.setting.setCompoundDrawables(null, img_on, null, null);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "BOT2ImageId", "2", (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.search.setCompoundDrawables(null, img_on, null, null);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "BOT1ImageId", "1", (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.home.setCompoundDrawables(null, img_on, null, null);
                                break;
                            case 1:
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "NavigationImageId", "0",
                                                        (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.navigation.setCompoundDrawables(img_on, null, null,
                                        null);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "BOT3ImageId", "3", (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.mananger.setCompoundDrawables(img_on, null, null, null);
                                // mananger.setCompoundDrawablePadding(-3);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO
                                                        .getIniString(InitializeToolbarActivity.this.userIni,
                                                                "BUTTON",
                                                                "MoreBtnImageId", "4",
                                                                (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.setting.setCompoundDrawables(img_on, null, null, null);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "BOT2ImageId", "2", (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.search.setCompoundDrawables(img_on, null, null, null);
                                img_on = res
                                        .getDrawable(constants.ButtonImageId[Integer
                                                .parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString(
                                                        InitializeToolbarActivity.this.userIni, "BUTTON",
                                                        "BOT1ImageId", "1", (byte) 0))]);
                                img_on.setBounds(0, 0, img_on.getMinimumWidth(),
                                        img_on.getMinimumHeight());
                                InitializeToolbarActivity.this.home.setCompoundDrawables(img_on, null, null, null);
                                break;
                            case 2:
                                break;

                        }
                        break;
                    case 10:
                        if (author == 1) {
                            InitializeToolbarActivity.this.main_radio.setVisibility(View.GONE);
                        } else if (author == 2) {
                            InitializeToolbarActivity.this.main_radio.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 11:
                        InitializeToolbarActivity.this.getNavMsgThread();
                        break;
                    case 12:
                        boolean cont = intent.getBooleanExtra("socket", true);
                        InitializeToolbarActivity.this.buttom_message.setVisibility(View.VISIBLE);
                        downloadBtn.setVisibility(View.GONE);
                        cancleBtn.setVisibility(View.GONE);
                        loadView.setVisibility(View.GONE);
                        InitializeToolbarActivity.this.msgView.setVisibility(View.VISIBLE);
                        if (cont) {
                            InitializeToolbarActivity.this.msgView.setText("错误(E00024)：找不到该页面！");
                        } else {
                            InitializeToolbarActivity.this.msgView.setText("错误(E00027)：服务已断开！");
                        }
                        break;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                    "APPSHOW", "ToolSet", "0", (byte) 0)) == 0) {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + getString(R.string.about_title));
                intent.putExtra("flag", 14);
                this.sendBroadcast(intent);
                return true;
            } else if (SlidingActivity.mSlidingMenu.getmViewAbove()
                    .getCurrentItem() == 1) {
                if (this.Prompt.getVisibility() == View.VISIBLE) {
                    this.Prompt.setVisibility(View.GONE);
                    this.msgBtn.setBackgroundResource(R.drawable.popupbox_radio_button_checked_right);
                    UIHelper.setSharePerference(this,
                            constants.SAVE_LOCALMSGNUM, "bottom_window", 0);
                } else {
                    UIHelper.showQuitDialog(this);
                }
                return true;
            } else {
                SlidingActivity.mSlidingMenu.showContent();
                return true;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public void GetSystemIniThread() {
        Thread dbmsgThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String url = getString(R.string.UpdateIniUrl);
                ApiClient Client = new ApiClient();
                try {
                    constants.menus = Client.navigationmsg(url);
                } catch (AppException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        });
        if (dbmsgThread.isAlive()) {
            dbmsgThread.stop();
        }
        dbmsgThread.start();
    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (constants.menus != null) {
                        String dataPath = getFilesDir().getParentFile()
                                .getAbsolutePath();
                        if (dataPath.endsWith("/") == false) {
                            dataPath = dataPath + "/";
                        }
                        String appIni = dataPath + constants.APP_CONFIG_FILE_NAME;
                        String userIni = dataPath + constants.USER_CONFIG_FILE_NAME;
                        if (constants.menus[0].equalsIgnoreCase(m_iniFileIO.getIniString(appIni,
                                "TBSAPP", "AppCode", "",
                                (byte) 0))) {
                            if (constants.menus[1].compareTo(m_iniFileIO.getIniString(appIni,
                                    "TBSAPP", "AppVersion", "1.0",
                                    (byte) 0)) > 0) {
                                DownloadAsyncTask task = new DownloadAsyncTask(InitializeToolbarActivity.this,
                                        constants.menus[2], appIni);
                                DownloadAsyncTask task1 = new DownloadAsyncTask(InitializeToolbarActivity.this,
                                        constants.menus[3], userIni);
                                task1.execute();
                                task.execute();
                            }
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.web_view.setBackgroundColor(Color
                .parseColor(this.m_iniFileIO.getIniString(this.appNewsFile,
                        "BASIC_SETUP", "BackColorValue", "#f6f6f6", (byte) 0)));
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
            this.web_view.getSettings().setTextZoom(
                    Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
                            "BASIC_SETUP", "TextZoom", "100", (byte) 0)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.MyBroadcastReciver);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        Intent intent1 = new Intent();
        Intent intent = new Intent();
        this.iniPath();
        switch (arg0.getId()) {
            case R.id.buttom_cancle:
                this.buttom_message.setVisibility(View.GONE);
                intent.setAction("downloadAgain"
                        + getString(R.string.about_title));
                this.sendBroadcast(intent);
                intent1.setAction("downloadCycle"
                        + getString(R.string.about_title));
                this.sendBroadcast(intent1);
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "downflag", 0);
                break;
            case R.id.Button01:
                if (this.Prompt.getVisibility() == View.GONE) {
                    this.Prompt.setVisibility(View.VISIBLE);
                    this.msgBtn.setBackgroundResource(R.drawable.popupbox_radio_button_checked);
                    this.PromptMsg = this.m_iniFileIO.getIniString(this.userIni, "MSGURL",
                            "PromptMsg", "", (byte) 0);
                    this.resname = UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
                    if(!StringUtils.isEmpty(PromptMsg)) {
                        String protUrl = StringUtils.isUrl(this.PromptMsg, this.baseUrl, this.resname);
                        if (UIHelper.TbsMotion(this, protUrl)) {
                            this.web_view.loadUrl(protUrl);
                        }
                        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                                "bottom_window", 1);
                    }
                } else {
                    this.Prompt.setVisibility(View.GONE);
                    this.msgBtn.setBackgroundResource(R.drawable.popupbox_radio_button_checked_right);
                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                            "bottom_window", 0);
                }
                break;
            case R.id.radio_home:
                intent.setClass(this, SlidingActivity.class);
                this.mTabHost.addTab(this.buildTabSpec(constants.MENTION_TAB, intent));
                intent1.setAction("Action_main"
                        + getString(R.string.about_title));
                intent1.putExtra("navigatoin", 1);
                intent1.putExtra("flag", 2);
                this.sendBroadcast(intent1);
                this.mTabHost.setCurrentTabByTag(constants.HOME_TAB);
                break;
            case R.id.radio_mention:
                intent.setClass(this, SlidingActivity.class);
                this.mTabHost.addTab(this.buildTabSpec(constants.MENTION_TAB, intent));
                intent1.setAction("Action_main"
                        + getString(R.string.about_title));
                intent1.putExtra("navigatoin", 2);
                intent1.putExtra("flag", 2);
                this.sendBroadcast(intent1);
                this.mTabHost.setCurrentTabByTag(constants.MENTION_TAB);
                break;
            case R.id.radio_more:
                intent.setClass(this, SlidingActivity.class);
                this.mTabHost.addTab(this.buildTabSpec(constants.MENTION_TAB, intent));
                intent1.setAction("Action_main"
                        + getString(R.string.about_title));
                intent1.putExtra("navigatoin", 5);
                intent1.putExtra("flag", 2);
                this.sendBroadcast(intent1);
                this.mTabHost.setCurrentTabByTag(constants.MORE_TAB);
                break;
            case R.id.radio_manager:
                intent.setClass(this, SlidingActivity.class);
                this.mTabHost.addTab(this.buildTabSpec(constants.MENTION_TAB, intent));
                intent1.setAction("Action_main"
                        + getString(R.string.about_title));
                intent1.putExtra("navigatoin", 4);
                intent1.putExtra("flag", 2);
                this.sendBroadcast(intent1);
                this.mTabHost.setCurrentTabByTag(constants.MANAGER_TAB);
                break;
            case R.id.radio_favourite:
                intent.setClass(this, SlidingActivity.class);
                this.mTabHost.addTab(this.buildTabSpec(constants.MENTION_TAB, intent));
                intent1.setAction("Action_main"
                        + getString(R.string.about_title));
                intent1.putExtra("navigatoin", 3);
                intent1.putExtra("flag", 2);
                this.sendBroadcast(intent1);
                this.mTabHost.setCurrentTabByTag(constants.FAVOURITE_TAB);
                break;
            default:
                break;
        }
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

    public void getNavMsgThread() {
        this.iniPath();
        Thread appThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                InitializeToolbarActivity.this.resname = UIHelper.getShareperference(InitializeToolbarActivity.this,
                        constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
                String NavMsg = InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity
                                .this.userIni,
                        "MSGURL",
                        "NavMsg", "/tbsnews/page/layer-getfl.cbs?resname=", (byte) 0);
                String url;
                url = StringUtils.isUrl(NavMsg, InitializeToolbarActivity.this.baseUrl, InitializeToolbarActivity
                        .this.resname);
                ApiClient ApiClient = new ApiClient();
                try {
                    constants.menus = ApiClient.navigationmsg(url);
                } catch (AppException e) {
                    constants.menus = null;
                    e.printStackTrace();
                }
                Intent intent1 = new Intent();
                if (constants.menus != null) {
                    intent1.setAction("Action_main"
                            + getString(R.string.about_title));
                    intent1.putExtra("pagenum", 0);
                    intent1.putExtra("navigatoin", 1);
                    intent1.putExtra("flag", 1);
                    InitializeToolbarActivity.this.sendBroadcast(intent1);
                } else {
                    intent1.setAction("Action_main"
                            + getString(R.string.about_title));
                    intent1.putExtra("pagenum", 1);
                    intent1.putExtra("navigatoin", 0);
                    intent1.putExtra("flag", 1);
                    InitializeToolbarActivity.this.sendBroadcast(intent1);
                }
            }
        });
        if (appThread.isAlive()) {
            appThread.stop();
        }
        appThread.start();
    }

    public Map<String, String> login() {
        String pmt = DES.encrypt("password="
                + this.m_iniFileIO.getIniString(this.userIni, "Login", "PassWord", "",
                (byte) 0));
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "login");
        params.put("account", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "UserName", "", (byte) 0));
        params.put("clientId", UIHelper.DeviceMD5ID(this));
        params.put("pmt", pmt);
        return params;
    }

    public Map<String, String> update() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "update");
        params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    private void connect(int count) {
        InitializeToolbarActivity.MyAsyncTask task = new InitializeToolbarActivity.MyAsyncTask(this, count);
        task.execute();
    }

    class MyAsyncTask extends AsyncTask<String, Integer, String>
    {

        Context context;
        private String retStr;
        private String retLogid;
        private String user;
        private final int count;

        public MyAsyncTask(Context c, int count) {
            context = c;
            this.count = count;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected String doInBackground(String... params) {
            InitializeToolbarActivity.this.iniPath();
            HttpConnectionUtil connection = new HttpConnectionUtil();
            if (this.count == 1) {
                constants.verifyURL = "http://"
                        + InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity.this
                                .userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp,
                        (byte) 0)
                        + ":"
                        + InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity.this
                                .userIni, "Login",
                        "ebsPort", "8083", (byte) 0)
                        + InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity.this
                                .userIni, "Login",
                        "ebsPath", "/EBS/UserServlet", (byte) 0);
                return connection.asyncConnect(constants.verifyURL,
                        InitializeToolbarActivity.this.update(), HttpConnectionUtil.HttpMethod.POST, this.context);
            } else {
                constants.verifyURL = "http://"
                        + InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity.this
                                .userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp,
                        (byte) 0)
                        + ":"
                        + InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity.this
                                .userIni, "Login",
                        "ebsPort", "8083", (byte) 0)
                        + InitializeToolbarActivity.this.m_iniFileIO.getIniString(InitializeToolbarActivity.this
                                .userIni, "Login",
                        "ebsPath", "/EBS/UserServlet", (byte) 0);
                return connection.asyncConnect(constants.verifyURL, InitializeToolbarActivity.this.login(),
                        HttpConnectionUtil.HttpMethod.POST, this.context);
            }
        }

        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(String result) {
            InitializeToolbarActivity.this.stopAnimation();
            if (this.count == 1) {
                if (result != null) {
                    if (result.equals("true")) {
                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this
                                        .userIni, "Login",
                                "LoginFlag", "1");
                    } else {
                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this
                                        .userIni, "Login",
                                "LoginFlag", "0");
                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this
                                        .userIni, "Login", "LoginId",
                                "");
                        if (Integer.parseInt(InitializeToolbarActivity.this.m_iniFileIO.getIniString
                                (InitializeToolbarActivity.this.userIni, "Login",
                                        "autoLogin", "0", (byte) 0)) == 1) {
                            InitializeToolbarActivity.this.connect(0);
                        }
                    }
                } else {
                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this.userIni,
                            "Login", "LoginFlag",
                            "0");
                }
            } else {
                if (result != null) {
                    JSONObject json;
                    try {
                        json = new JSONObject(result);
                        this.retStr = json.getString("msg");
                        this.retLogid = json.getString("loginId");
                        this.user = json.getString("user");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this
                                        .userIni, "Login",
                                "LoginFlag", "0");
                        if (UIHelper.getShareperference(this.context,
                                Constants.SHARED_PREFERENCE_NAME,
                                Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                            ServiceManager serviceManager = new ServiceManager(
                                    InitializeToolbarActivity.this);
                            serviceManager
                                    .setNotificationIcon(R.drawable.notification);
                            serviceManager.restartService();
                        }
                    }
                    if (this.retStr != null && this.retLogid != null) {
                        if (this.user != null) {
                            try {
                                if (this.retStr.equals("登录成功")) {
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "LoginId", this.retLogid);
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "LoginFlag", "1");
                                    json = new JSONObject(this.user);
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "NickName",
                                                    json.getString("userName"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "newEMail",
                                                    json.getString("newEMail"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Mobile",
                                                    json.getString("mobile"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Contact",
                                                    json.getString("email"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Account",
                                                    json.getString("account"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "UserCode",
                                                    json.getString("userCode"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Signature",
                                                    json.getString("idiograph"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Sex",
                                                    json.getString("sex"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Address",
                                                    json.getString("myURL"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Location_province",
                                                    json.getString("province"));
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "Location_city",
                                                    json.getString("city"));
                                    if (StringUtils.isEmpty(json
                                            .getString("userCodeModifyNum"))) {
                                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                                (InitializeToolbarActivity.this.userIni,
                                                        "Login", "AccountFlag", "0");
                                    } else {
                                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                                (InitializeToolbarActivity.this.userIni,
                                                        "Login", "AccountFlag", "1");
                                    }
                                    if (UIHelper
                                            .getShareperference(
                                                    this.context,
                                                    Constants.SHARED_PREFERENCE_NAME,
                                                    Constants.SETTINGS_NOTIFICATION_ENABLED,
                                                    true)) {
                                        ServiceManager serviceManager = new ServiceManager(
                                                InitializeToolbarActivity.this);
                                        serviceManager
                                                .setNotificationIcon(R.drawable.notification);
                                        serviceManager.setUserInfo(InitializeToolbarActivity.this.m_iniFileIO
                                                        .getIniString(InitializeToolbarActivity.this.userIni,
                                                                "Login", "Account", "",
                                                                (byte) 0), DES
                                                        .encrypt(InitializeToolbarActivity.this.m_iniFileIO
                                                                .getIniString(
                                                                        InitializeToolbarActivity.this.userIni,
                                                                        "Login",
                                                                        "PassWord", "",
                                                                        (byte) 0)),
                                                InitializeToolbarActivity.this.m_iniFileIO
                                                        .getIniString(
                                                                InitializeToolbarActivity.this.userIni,
                                                                "Login",
                                                                "LoginId", "",
                                                                (byte) 0));
                                        serviceManager.restartService();
                                    }
                                    if (Integer.parseInt(InitializeToolbarActivity.this.m_iniFileIO
                                            .getIniString(userIni,
                                                    "Login", "UserUpate",
                                                    "1", (byte) 0)) == 1) {
//                                        InitializeToolbarActivity.this.startService(new Intent(
//                                                getString(string.ServerName1)));
                                        Intent mIntent = new Intent();
                                        mIntent.setAction(InitializeToolbarActivity.this
                                                .getString(R.string.ServerName1));//你定义的service的action
                                        mIntent.setPackage(InitializeToolbarActivity.this.getPackageName());
                                        //这里你需要设置你应用的包名
                                        InitializeToolbarActivity.this.startService(mIntent);

                                    }
                                    ///connect(1);
                                } else {
                                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString
                                            (InitializeToolbarActivity.this.userIni,
                                                    "Login", "LoginFlag", "0");
                                    if (UIHelper
                                            .getShareperference(
                                                    this.context,
                                                    Constants.SHARED_PREFERENCE_NAME,
                                                    Constants.SETTINGS_NOTIFICATION_ENABLED,
                                                    true)) {
                                        ServiceManager serviceManager = new ServiceManager(
                                                InitializeToolbarActivity.this);
                                        serviceManager
                                                .setNotificationIcon(R.drawable.notification);
                                        serviceManager.restartService();
                                    }
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity
                                                .this.userIni, "Login",
                                        "LoginFlag", "0");
                                if (UIHelper
                                        .getShareperference(
                                                this.context,
                                                Constants.SHARED_PREFERENCE_NAME,
                                                Constants.SETTINGS_NOTIFICATION_ENABLED,
                                                true)) {
                                    ServiceManager serviceManager = new ServiceManager(
                                            InitializeToolbarActivity.this);
                                    serviceManager
                                            .setNotificationIcon(R.drawable.notification);
                                    serviceManager.restartService();
                                }
                            }
                        } else {
                            InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this
                                            .userIni, "Login",
                                    "LoginFlag", "0");
                            if (UIHelper.getShareperference(this.context,
                                    Constants.SHARED_PREFERENCE_NAME,
                                    Constants.SETTINGS_NOTIFICATION_ENABLED,
                                    true)) {
                                ServiceManager serviceManager = new ServiceManager(
                                        InitializeToolbarActivity.this);
                                serviceManager
                                        .setNotificationIcon(R.drawable.notification);
                                serviceManager.restartService();
                            }
                        }
                    } else {
                        InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this
                                        .userIni, "Login",
                                "LoginFlag", "0");
                        if (UIHelper.getShareperference(this.context,
                                Constants.SHARED_PREFERENCE_NAME,
                                Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                            ServiceManager serviceManager = new ServiceManager(
                                    InitializeToolbarActivity.this);
                            serviceManager
                                    .setNotificationIcon(R.drawable.notification);
                            serviceManager.restartService();
                        }
                    }
                } else {
                    InitializeToolbarActivity.this.m_iniFileIO.writeIniString(InitializeToolbarActivity.this.userIni,
                            "Login",
                            "LoginFlag", "0");
                    if (UIHelper.getShareperference(this.context,
                            Constants.SHARED_PREFERENCE_NAME,
                            Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                        ServiceManager serviceManager = new ServiceManager(
                                InitializeToolbarActivity.this);
                        serviceManager
                                .setNotificationIcon(R.drawable.notification);
                        serviceManager.restartService();
                    }
                    Toast.makeText(this.context,
                            this.context.getString(R.string.sapi_login_error),
                            Toast.LENGTH_SHORT).show();
                }
            }

        }

        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        return this.mTbsGestureDetector.onTouchEvent(arg1);
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
        if (diffY > this.web_view.getHeight() / 5 && Math.abs(velocityX) > 30) {
            this.web_view.loadUrl("javascript:jump()");
        } else if (diffY < -(this.web_view.getHeight() / 5)
                && Math.abs(velocityX) > 30) {
            this.web_view.loadUrl("javascript:OnUpdate()");
        } else if (diffX > 0 && Math.abs(diffX) > Math.abs(diffY)
                && Math.abs(velocityX) > 20) {
            this.web_view.loadUrl("javascript:tonext()");
            this.web_view.loadUrl("javascript:jumppage(3)");
        } else if (diffX < 0 && Math.abs(diffX) > Math.abs(diffY)
                && Math.abs(velocityX) > 20) {
            this.web_view.loadUrl("javascript:toprev()");
            this.web_view.loadUrl("javascript:jumppage(2)");
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

    /**
     * 防止在旋转时，重启Activity的onCreate方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        int SplitRatio = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "APPSHOW", "PromptHight", "50", (byte) 0));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横屏的操作
            DisplayMetrics dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            this.screenHeight = dm.heightPixels;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏的操作
            DisplayMetrics dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            this.screenHeight = dm.heightPixels;
        }
        this.laParams.height = this.screenHeight * SplitRatio / 100;
        this.Prompt.setLayoutParams(this.laParams);
    }

}
