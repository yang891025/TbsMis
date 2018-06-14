package com.tbs.tbsmis.activity;

import android.R.color;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.util.Detail_GestureDetector;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

@SuppressLint("SetJavaScriptEnabled")
public class DetailActivity extends Activity implements View.OnClickListener,
        View.OnTouchListener
{

    private Button bigger, smaller;
    private TextView titleTV;
    private CheckBox check, title_check;
    private WebView webview;
    private String tempUrl, winStrState;
    private int position, count;
    private String ResName;
    private LinearLayout goBtn, backBtn;
    private ImageView imageViewBack, imageViewGo, imageViewHome;
    private RelativeLayout toolBar;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private AnimationDrawable loadingAnima;
    private boolean loadingDialogState;
    private View.OnClickListener listener;
    private PopupWindow moreWindow, moreWindow2;
    private boolean isOpenPop;
    private int flag3;

    private GestureDetector mGestureDetector;// ������
    private SimpleOnGestureListener I;
    private Button searchBtn3;
    private Button menuBtn3;
    private LinearLayout shareBtn;
    private LinearLayout settingBtn;
    // private ProgressBar pb;
    private LinearLayout homeBtn;
    private DetailActivity.DetailBroadcastReciver DetailBroadcastReciver;
    private String Url;
    private RelativeLayout detailtitle;
    private int notificationflag;
    private String notificationId;
    private String userIni;
    private IniFile m_iniFileIO;
    private RelativeLayout toolBar2;
    // private RelativeLayout toolBarExt;
    private LinearLayout comment_settingBtn;
    private int detail;
    private LinearLayout comment_homeBtn;
    private LinearLayout comment_backBtn;
    private LinearLayout comment_shareBtn;
    private ImageButton comment;
    private ViewSwitcher mFootViewSwitcher;
    private ImageButton mFootPubcomment;
    private EditText mFootEditer;
    private InputMethodManager imm;
    private RelativeLayout mFootSync;
    private CheckBox comment_check;
    private RelativeLayout detailcomment;

    private View mCustomView;
    //private final Bitmap mDefaultVideoPoster;
    //private final View mVideoProgressView;

    private FrameLayout mFullscreenContainer;

    private CustomViewCallback mCustomViewCallback;

    private String appNewsFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.detail_show_activity1);
        MyActivity.getInstance().addActivity(this);
        this.init();
        int detailtool = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "detailtool", 1);
        this.detail = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
                "APPSHOW", "DetailTool", "0", (byte) 0));
        this.m_iniFileIO.writeIniString(userIni, "APPSHOW", "DetailTool", this.detail
                + "");
        int detailtitleflag = Integer.parseInt(this.m_iniFileIO.getIniString(
                userIni, "APPSHOW", "DetailTitle", "0", (byte) 0));
        if (detailtool == 0) {
            this.toolBar2.setVisibility(View.GONE);
            this.toolBar.setVisibility(View.GONE);
        } else {
            if (this.detail == 0) {
                this.toolBar2.setVisibility(View.GONE);
            } else {
                this.toolBar.setVisibility(View.GONE);
            }

        }
        if (detailtitleflag == 1) {
            this.detailtitle.setVisibility(View.VISIBLE);
        } else {
            this.detailtitle.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void init() {
        this.webview = (WebView) this.findViewById(R.id.webview4);
        this.backBtn = (LinearLayout) this.findViewById(R.id.news_detail_backBtn);
        this.goBtn = (LinearLayout) this.findViewById(R.id.news_detail_goBtn);
        this.settingBtn = (LinearLayout) this.findViewById(R.id.news_detail_settingBtn);
        this.homeBtn = (LinearLayout) this.findViewById(R.id.news_detail_homeBtn);
        this.shareBtn = (LinearLayout) this.findViewById(R.id.pic_share);
        this.imageViewBack = (ImageView) this.findViewById(R.id.news_detail_backIV);
        this.imageViewGo = (ImageView) this.findViewById(R.id.news_detail_goIV);
        this.imageViewHome = (ImageView) this.findViewById(R.id.news_detail_homeIV);
        this.toolBar = (RelativeLayout) this.findViewById(R.id.include_bottom2);
        this.toolBar2 = (RelativeLayout) this.findViewById(R.id.include_bottom);
        // toolBarExt= (RelativeLayout) findViewById(R.id.content_layout);
        this.comment = (ImageButton) this.findViewById(R.id.ibtnClose);
        this.comment_backBtn = (LinearLayout) this.findViewById(R.id.ibtnback);
        this.comment_settingBtn = (LinearLayout) this.findViewById(R.id.ibtnShare);
        this.comment_homeBtn = (LinearLayout) this.findViewById(R.id.ibtnCommentSize);
        this.comment_shareBtn = (LinearLayout) this.findViewById(R.id.ibtnCollection);
        this.detailtitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);

        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.menuBtn3 = (Button) this.findViewById(R.id.more_btn2);
        this.searchBtn3 = (Button) this.findViewById(R.id.search_btn2);
        this.imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        this.mFootViewSwitcher = (ViewSwitcher) this.findViewById(R.id.news_detail_foot_viewswitcher);
        this.mFootPubcomment = (ImageButton) this.findViewById(R.id.ibtnInput);
        this.mFootEditer = (EditText) this.findViewById(R.id.etComment);
        this.mFootSync = (RelativeLayout) this.findViewById(R.id.CommentSync);
        this.mFootEditer.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DetailActivity.this.imm.showSoftInput(v, 0);
                } else {
                    DetailActivity.this.imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (DetailActivity.this.mFootViewSwitcher.getDisplayedChild() == 1) {
                        DetailActivity.this.mFootViewSwitcher.setDisplayedChild(0);
                        DetailActivity.this.mFootEditer.clearFocus();
                        DetailActivity.this.mFootEditer.setVisibility(View.GONE);
                        DetailActivity.this.mFootSync.setVisibility(View.GONE);
                    }
                }
            }
        });
        this.mFootEditer.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (DetailActivity.this.mFootViewSwitcher.getDisplayedChild() == 1) {
                        DetailActivity.this.mFootViewSwitcher.setDisplayedChild(0);
                        DetailActivity.this.mFootEditer.clearFocus();
                        DetailActivity.this.mFootEditer.setVisibility(View.GONE);
                        DetailActivity.this.mFootSync.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });
        this.settingBtn.setOnClickListener(this);
        this.comment.setOnClickListener(this);
        this.comment_backBtn.setOnClickListener(this);
        this.mFootPubcomment.setOnClickListener(this);
        this.comment_settingBtn.setOnClickListener(this);
        this.comment_homeBtn.setOnClickListener(this);
        this.comment_shareBtn.setOnClickListener(this);
        this.homeBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(null);
        this.goBtn.setOnClickListener(null);
        this.shareBtn.setOnClickListener(this);
        this.menuBtn3.setOnClickListener(this);
        this.searchBtn3.setOnClickListener(this);
        this.m_iniFileIO = new IniFile();
        this.titleTV = (TextView) this.findViewById(R.id.title_tv);
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
        if (this.getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            this.notificationflag = intent.getIntExtra("flag", 0);
            if (this.notificationflag == 1) {
                this.tempUrl = intent.getStringExtra("tempUrl");
                this.ResName = intent.getStringExtra("ResName");
            } else if (this.notificationflag == 2) {
                this.notificationId = intent
                        .getStringExtra(Constants.NOTIFICATION_ID);
                this.ResName = intent.getStringExtra(Constants.NOTIFICATION_TITLE);
                this.tempUrl = intent.getStringExtra(Constants.NOTIFICATION_URI);
            }
            this.position = intent.getIntExtra("position", 0);
            this.count = intent.getIntExtra("count", 0);
            this.winStrState = intent.getStringExtra("winStrState");
        }
        // System.out.println(position+";"+count+";"+ResName+"url="+tempUrl);
        if (this.tempUrl == null || this.tempUrl.equals("")) {
            this.finish();
            Toast.makeText(this, "已在新窗口", Toast.LENGTH_SHORT).show();
        }
        this.I = new Detail_GestureDetector(this, this.tempUrl);
        this.mGestureDetector = new GestureDetector(this, I);
        // 接受广播注册
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(this.tempUrl);
        intentFilter.addAction("Action_detail"
                + this.getString(R.string.about_title));
        this.DetailBroadcastReciver = new DetailActivity.DetailBroadcastReciver();
        this.registerReceiver(this.DetailBroadcastReciver, intentFilter);
        String ipUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
                "currentAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(userIni, "SERVICE",
                "currentPort", constants.DefaultLocalPort, (byte) 0);
        String baseUrl = "http://" + ipUrl + ":" + portUrl;
        // if (!StringUtils.isUrl(tempUrl)) {
        // Url = baseUrl + tempUrl;
        // } else {
        // Url = tempUrl;
        // }
        this.Url = StringUtils.isUrl(this.tempUrl, baseUrl, null);
        this.startAnimation();
        if (this.count == 1) {
            this.imageViewBack.setImageResource(R.drawable.bottombar_btn_unback);
            this.imageViewGo.setImageResource(R.drawable.bottombar_btn_ungo);
            this.backBtn.setOnClickListener(null);
            this.goBtn.setOnClickListener(null);
        } else if (this.position < this.count && this.position > 0) {
            this.imageViewBack.setImageResource(R.drawable.bottombar_btn_back);
            this.imageViewGo.setImageResource(R.drawable.bottombar_btn_go);
            this.backBtn.setOnClickListener(this.listener);
            this.goBtn.setOnClickListener(this.listener);
        } else if (this.position == this.count) {
            this.imageViewBack.setImageResource(R.drawable.bottombar_btn_unback);
            this.imageViewGo.setImageResource(R.drawable.bottombar_btn_go);
            this.backBtn.setOnClickListener(null);
            this.goBtn.setOnClickListener(this.listener);
        } else if (this.position == 0) {
            this.imageViewBack.setImageResource(R.drawable.bottombar_btn_back);
            this.imageViewGo.setImageResource(R.drawable.bottombar_btn_ungo);
            this.backBtn.setOnClickListener(this.listener);
            this.goBtn.setOnClickListener(null);
        }
        this.titleTV.setText(this.ResName + "(" + (this.count - this.position + 1) + "/" + this.count
                + ")");

        /**
         * webview
         */
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
        this.webview.getSettings().setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
        }
        //this.webview.getSettings().setDefaultTextEncodingName("gb2312");
        this.webview.getSettings().setSupportMultipleWindows(true);
        this.webview.getSettings().setDomStorageEnabled(true);
        // webview.getSettings().setUseWideViewPort(true);
        // webview.getSettings().setLoadWithOverviewMode(true);
        //webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webview.setOnTouchListener(this);

        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        this.webview.setDownloadListener(UIHelper.MyWebViewDownLoadListener(this));
        //
        UIHelper.addJavascript(this, this.webview, this.tempUrl);
        this.webview.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public void onShowCustomView(View view,
                                         CustomViewCallback callback) {
                //System.out.println("onShowCustomView");
                DetailActivity.this.showCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                //System.out.println("onHideCustomView");
                DetailActivity.this.hideCustomView();
            }

            @Override
            public void onCloseWindow(WebView view) {
                //System.out.println("onCloseWindow");
                DetailActivity.this.finish();
                //DetailActivity.this.overridePendingTransition(anim.push_in, anim.push_out);

            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                DetailActivity.this.imageViewHome.setImageResource(R.drawable.bottombar_btn_cancle);
                DetailActivity.this.homeBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        DetailActivity.this.webview.stopLoading();
                    }
                });
                DetailActivity.this.loadingDialogState = progress < 100;

                if (DetailActivity.this.loadingDialogState) {
                    DetailActivity.this.startAnimation();
                } else {
                    DetailActivity.this.stopAnimation();
                }
                if (progress == 100) {
                    if (DetailActivity.this.count == 1) {
                        DetailActivity.this.imageViewBack
                                .setImageResource(R.drawable.bottombar_btn_unback);
                        DetailActivity.this.imageViewGo
                                .setImageResource(R.drawable.bottombar_btn_ungo);
                        DetailActivity.this.imageViewHome
                                .setImageResource(R.drawable.bottombar_btn_refresh);
                        DetailActivity.this.homeBtn.setOnClickListener(DetailActivity.this.listener);
                        DetailActivity.this.backBtn.setOnClickListener(null);
                        DetailActivity.this.goBtn.setOnClickListener(null);
                    } else if (DetailActivity.this.position < DetailActivity.this.count && DetailActivity.this.position > 1) {
                        DetailActivity.this.imageViewBack
                                .setImageResource(R.drawable.bottombar_btn_back);
                        DetailActivity.this.imageViewGo
                                .setImageResource(R.drawable.bottombar_btn_go);
                        DetailActivity.this.imageViewHome
                                .setImageResource(R.drawable.bottombar_btn_refresh);
                        DetailActivity.this.homeBtn.setOnClickListener(DetailActivity.this.listener);
                        DetailActivity.this.backBtn.setOnClickListener(DetailActivity.this.listener);
                        DetailActivity.this.goBtn.setOnClickListener(DetailActivity.this.listener);
                    } else if (DetailActivity.this.position == DetailActivity.this.count) {
                        DetailActivity.this.imageViewBack
                                .setImageResource(R.drawable.bottombar_btn_unback);
                        DetailActivity.this.imageViewGo
                                .setImageResource(R.drawable.bottombar_btn_go);
                        DetailActivity.this.imageViewHome
                                .setImageResource(R.drawable.bottombar_btn_refresh);
                        DetailActivity.this.homeBtn.setOnClickListener(DetailActivity.this.listener);
                        DetailActivity.this.backBtn.setOnClickListener(null);
                        DetailActivity.this.goBtn.setOnClickListener(DetailActivity.this.listener);
                    } else if (DetailActivity.this.position == 1) {
                        DetailActivity.this.imageViewBack
                                .setImageResource(R.drawable.bottombar_btn_back);
                        DetailActivity.this.imageViewGo
                                .setImageResource(R.drawable.bottombar_btn_ungo);
                        DetailActivity.this.imageViewHome
                                .setImageResource(R.drawable.bottombar_btn_refresh);
                        DetailActivity.this.homeBtn.setOnClickListener(DetailActivity.this.listener);
                        DetailActivity.this.backBtn.setOnClickListener(DetailActivity.this.listener);
                        DetailActivity.this.goBtn.setOnClickListener(null);
                    }
                    DetailActivity.this.titleTV.setText(DetailActivity.this.ResName + "(" + (DetailActivity.this.count - DetailActivity.this.position + 1)
                            + "/" + DetailActivity.this.count + ")");
                }
            }

        });
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
        this.webview.loadUrl(this.Url);
        // UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "load",
        // 0);
        this.listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.news_detail_backBtn:
                        DetailActivity.this.position = DetailActivity.this.position + 1;
                        if (DetailActivity.this.position < DetailActivity.this.count && DetailActivity.this.position > 1) {
                            DetailActivity.this.webview.loadUrl("javascript:toprev()");
                            DetailActivity.this.webview.loadUrl("javascript:jumppage(2)");

                        } else if (DetailActivity.this.position == DetailActivity.this.count) {
                            DetailActivity.this.imageViewBack
                                    .setImageResource(R.drawable.bottombar_btn_unback);
                            DetailActivity.this.imageViewGo
                                    .setImageResource(R.drawable.bottombar_btn_go);
                            DetailActivity.this.imageViewHome
                                    .setImageResource(R.drawable.bottombar_btn_refresh);
                            DetailActivity.this.homeBtn.setOnClickListener(DetailActivity.this.listener);
                            DetailActivity.this.backBtn.setOnClickListener(null);
                            DetailActivity.this.goBtn.setOnClickListener(this);
                            DetailActivity.this.webview.loadUrl("javascript:toprev()");
                            DetailActivity.this.webview.loadUrl("javascript:jumppage(2)");
                        }
                        break;

                    case R.id.news_detail_goBtn:
                        DetailActivity.this.position = DetailActivity.this.position - 1;
                        if (DetailActivity.this.position < DetailActivity.this.count && DetailActivity.this.position > 0) {
                            DetailActivity.this.webview.loadUrl("javascript:tonext()");
                            DetailActivity.this.webview.loadUrl("javascript:jumppage(3)");
                        } else if (DetailActivity.this.position == 1) {
                            DetailActivity.this.imageViewBack
                                    .setImageResource(R.drawable.bottombar_btn_back);
                            DetailActivity.this.imageViewGo
                                    .setImageResource(R.drawable.bottombar_btn_ungo);
                            DetailActivity.this.imageViewHome
                                    .setImageResource(R.drawable.bottombar_btn_refresh);
                            DetailActivity.this.homeBtn.setOnClickListener(DetailActivity.this.listener);
                            DetailActivity.this.backBtn.setOnClickListener(this);
                            DetailActivity.this.goBtn.setOnClickListener(null);
                            DetailActivity.this.webview.loadUrl("javascript:tonext()");
                            DetailActivity.this.webview.loadUrl("javascript:jumppage(3)");
                        }
                        break;
                    case R.id.news_detail_homeBtn:
                        DetailActivity.this.webview.reload();
                        break;
                }
            }
        };
    }

    private class DetailBroadcastReciver extends BroadcastReceiver
    {
        @TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DetailActivity.this.tempUrl)) {
                int SelBtn = intent.getIntExtra("flag", 1);
                switch (SelBtn) {
                    case 1:
                        if (DetailActivity.this.position == 1) {
                            Toast.makeText(DetailActivity.this, "已到最后",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            DetailActivity.this.goBtn.callOnClick();
                        }
                        break;
                    case 2:
                        if (DetailActivity.this.position == DetailActivity.this.count) {
                            DetailActivity.this.finish();
                            DetailActivity.this.overridePendingTransition(R.anim.push_in,
                                    R.anim.push_out);
                        } else {
                            DetailActivity.this.backBtn.callOnClick();
                        }
                        break;
                    case 3:
                        DetailActivity.this.tempUrl = intent.getStringExtra("tempUrl");
                        DetailActivity.this.ResName = intent.getStringExtra("ResName");
                        DetailActivity.this.position = intent.getIntExtra("position", 0);
                        DetailActivity.this.count = intent.getIntExtra("count", 0);
                        DetailActivity.this.titleTV.setText(DetailActivity.this.ResName + "(" + (DetailActivity.this.count - DetailActivity.this.position + 1)
                                + "/" + DetailActivity.this.count + ")");
                        String ipUrl = DetailActivity.this.m_iniFileIO.getIniString(userIni,
                                "SERVICE", "currentAddress",
                                constants.DefaultLocalIp, (byte) 0);
                        String portUrl = DetailActivity.this.m_iniFileIO.getIniString(userIni,
                                "SERVICE", "currentPort",
                                constants.DefaultLocalPort, (byte) 0);
                        String baseUrl = "http://" + ipUrl + ":" + portUrl;
                        DetailActivity.this.Url = StringUtils.isUrl(DetailActivity.this.tempUrl, baseUrl, null);
                        DetailActivity.this.webview.loadUrl(DetailActivity.this.Url);
                        break;
                }
            } else if (action.equals("Action_detail"
                    + getString(R.string.about_title))) {
                int SelBtn = intent.getIntExtra("flag", 1);
                switch (SelBtn) {
                    case 1:
                        DetailActivity.this.finish();
                        DetailActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                        break;
                }
            }
        }
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
            this.webview.getSettings().setTextZoom(
                    Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
                            "BASIC_SETUP", "TextZoom", "100", (byte) 0)));
        }


    }

//    @Override
//    public void finish() {
//        ViewGroup view = (ViewGroup) this.getWindow().getDecorView();
//        view.removeAllViews();
//        super.finish();
//    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (this.webview != null) {
            this.webview.setVisibility(View.GONE);
            this.webview.destroy();
        }
        this.unregisterReceiver(this.DetailBroadcastReciver);
        MyActivity.getInstance().finishActivity(this);
        super.onDestroy();
    }

    public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        this.loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
    }

    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return this.mGestureDetector.onTouchEvent(event);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.news_detail_settingBtn:
                if (this.detailtitle.getHeight() > UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "poplocation", 0)) {
                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                            "poplocation", this.detailtitle.getHeight());
                }
                this.changMorePopState2(arg0, 0);
                break;
            case R.id.ibtnCollection:
                if (this.detailtitle.getHeight() > UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "poplocation", 0)) {
                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                            "poplocation", this.detailtitle.getHeight());
                }
                this.changMorePopState2(arg0, 0);
                break;
            case R.id.more_btn2:
                this.finish();
                break;
            case R.id.ibtnback:
                this.finish();
                break;
            case R.id.search_btn2:
                if (this.detailtitle.getHeight() > UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "poplocation", 0)) {
                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                            "poplocation", this.detailtitle.getHeight());
                }
                this.changMorePopState2(arg0, 1);
                break;
            case R.id.pic_share:
                JsoupExam.getSearchEngine(2, webview.getUrl(), this);
                //this.webview.loadUrl("javascript:gettitle()");
                break;
            case R.id.ibtnShare:
                JsoupExam.getSearchEngine(2, webview.getUrl(), this);
               // this.webview.loadUrl("javascript:gettitle()");
                break;
            case R.id.ibtnCommentSize:
                Intent intent = new Intent();
                // intent.putExtra("phoneNum", phoneTxt);
                intent.setClass(this, CommentActivity.class);
                this.startActivity(intent);
                break;
            case R.id.ibtnInput:
                this.mFootViewSwitcher.showNext();
                this.mFootEditer.setVisibility(View.VISIBLE);
                this.mFootSync.setVisibility(View.VISIBLE);
                this.mFootEditer.requestFocus();
                break;
            case R.id.ibtnClose:
                // 恢复初始底部栏
                this.mFootViewSwitcher.setDisplayedChild(0);
                this.mFootEditer.clearFocus();
                this.mFootEditer.setVisibility(View.GONE);
                this.mFootSync.setVisibility(View.GONE);
                this.mFootEditer.setText("");
                break;
            case R.id.share:
                this.moreWindow2.dismiss();
                JsoupExam.getSearchEngine(2, webview.getUrl(), this);
                //this.webview.loadUrl("javascript:gettitle()");
                break;
            case R.id.favourite:
                this.moreWindow2.dismiss();
                JsoupExam.getSearchEngine(1, webview.getUrl(), this);
                //this.changMorePopState(arg0);
                break;

        }
    }

    // final class DemoJavaScriptInterface {}

    // ϸ��ҳ�� ��ఴť����popwindow
    public void changMorePopState(View v) {

        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow(v);
        } else {
            if (this.moreWindow != null) {
                this.moreWindow.dismiss();
            }
        }
    }

    public void changMorePopState2(View v, int location) {
        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow2(v, location);
        } else {
            if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow(View parent) {
        RelativeLayout detailDelete, detailFavourite;
        ImageView deletePic, FavoritePic;
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.more_newsfunction_dialog, null);
        detailDelete = (RelativeLayout) view.findViewById(R.id.pic_delet);
        detailFavourite = (RelativeLayout) view.findViewById(R.id.pic_favorite);
        FavoritePic = (ImageView) view.findViewById(R.id.imageview2);
        deletePic = (ImageView) view.findViewById(R.id.imageview3);
        if (constants.STATEFORCOLLECT.equals(this.winStrState)) {
            FavoritePic.setImageResource(R.drawable.news_detail_collect_nor);
            detailDelete.setOnClickListener(this.popListener);
        } else if (constants.STATEFORDATAMANAGE.equals(this.winStrState)) {
            FavoritePic.setImageResource(R.drawable.news_detail_collect);
            deletePic.setImageResource(R.drawable.news_detail_delete);
            detailFavourite.setOnClickListener(this.popListener);
            detailDelete.setOnClickListener(this.popListener);
        } else {
            deletePic.setImageResource(R.drawable.news_detail_delete_nor);
            detailFavourite.setOnClickListener(this.popListener);
        }
        this.moreWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ��հ״�����popwindow
        this.moreWindow.setFocusable(true);
        this.moreWindow.setOutsideTouchable(false);
        this.moreWindow.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                DetailActivity.this.isOpenPop = false;
            }
        });
        this.moreWindow
                .showAtLocation(parent, Gravity.CENTER | Gravity.CENTER, 0, 0);
        this.moreWindow.update();
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        this.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @SuppressWarnings("deprecation")
    private void popWindow2(View parent, int location) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = lay.inflate(R.layout.menu_list, null);
        this.check = (CheckBox) view1.findViewById(R.id.detail_check_box);
        this.title_check = (CheckBox) view1.findViewById(R.id.title_check_box);
        this.comment_check = (CheckBox) view1.findViewById(R.id.comment_check_box);
        RelativeLayout tools = (RelativeLayout) view1
                .findViewById(R.id.detailtool);
        this.detailcomment = (RelativeLayout) view1.findViewById(R.id.detailcomment);
        RelativeLayout title = (RelativeLayout) view1
                .findViewById(R.id.titletool);
        this.bigger = (Button) view1.findViewById(R.id.text_bigger);
        this.smaller = (Button) view1.findViewById(R.id.text_smaller);
        RelativeLayout favourite = (RelativeLayout) view1
                .findViewById(R.id.favourite);
        RelativeLayout share = (RelativeLayout) view1.findViewById(R.id.share);
        RelativeLayout settingMore = (RelativeLayout) view1
                .findViewById(R.id.setting_more);
        favourite.setOnClickListener(this);
        share.setOnClickListener(this);
        this.detailcomment.setOnClickListener(this.popListener);
        title.setOnClickListener(this.popListener);
        tools.setOnClickListener(this.popListener);
        settingMore.setOnClickListener(this.popListener);
        int detailtitleflag = Integer.parseInt(this.m_iniFileIO.getIniString(
                userIni, "APPSHOW", "DetailTitle", "1", (byte) 0));
        if (detailtitleflag == 1) {
            this.title_check.setChecked(true);
        } else {
            this.title_check.setChecked(false);
        }

        detailtitleflag = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "detailtool", 1);
        if (detailtitleflag == 1) {
            this.check.setChecked(true);
            if (Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
                    "APPSHOW", "DetailTool", "0", (byte) 0)) == 1) {
                this.comment_check.setChecked(true);
            } else {
                this.comment_check.setChecked(false);
            }
        } else {
            this.detailcomment.setVisibility(View.GONE);
            this.check.setChecked(false);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(userIni, "SETUP",
                "commentSetup_enable", "0", (byte) 0)) == 0) {
            this.detailcomment.setVisibility(View.GONE);
        }
        this.flag3 = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "Text_size", 0);
        if (this.flag3 == 0) {
            this.smaller.setBackgroundResource(R.drawable.text_smaller_pressed);
            this.bigger.setOnClickListener(this.popListener);
            this.smaller.setOnClickListener(null);
        }
        if (this.flag3 > 0 && this.flag3 < 5) {
            this.bigger.setOnClickListener(this.popListener);
            this.smaller.setOnClickListener(this.popListener);
        }
        if (this.flag3 == 5) {
            this.bigger.setBackgroundResource(R.drawable.text_bigger_pressed);
            this.bigger.setOnClickListener(null);
            this.smaller.setOnClickListener(this.popListener);
        }
        this.moreWindow2 = new PopupWindow(view1,
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
                DetailActivity.this.isOpenPop = false;
            }
        });
        int hight = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "poplocation",
                this.detailtitle.getHeight());
        if (location == 0) {
            this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.BOTTOM,
                    10, hight);
        } else if (location == 1) {
            this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                    hight * 3 / 2);
        }
        this.moreWindow2.update();
    }

    View.OnClickListener popListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_smaller:
                    DetailActivity.this.flag3 = DetailActivity.this.flag3 - 1;
                    UIHelper.setSharePerference(DetailActivity.this,
                            constants.SAVE_LOCALMSGNUM, "Text_size", DetailActivity.this.flag3);
                    if (DetailActivity.this.flag3 > 0 && DetailActivity.this.flag3 < 5) {
                        DetailActivity.this.bigger.setBackgroundResource(R.drawable.text_bigger);
                        DetailActivity.this.bigger.setOnClickListener(DetailActivity.this.popListener);
                    }
                    if (DetailActivity.this.flag3 == 0) {
                        DetailActivity.this.smaller.setBackgroundResource(R.drawable.text_smaller_pressed);
                        DetailActivity.this.smaller.setOnClickListener(null);
                    }
                    DetailActivity.this.webview.loadUrl("javascript:big(0)");
                    break;
                case R.id.text_bigger:
                    DetailActivity.this.flag3 = DetailActivity.this.flag3 + 1;
                    UIHelper.setSharePerference(DetailActivity.this,
                            constants.SAVE_LOCALMSGNUM, "Text_size", DetailActivity.this.flag3);
                    if (DetailActivity.this.flag3 > 0 && DetailActivity.this.flag3 < 5) {
                        DetailActivity.this.smaller.setBackgroundResource(R.drawable.text_smaller);
                        DetailActivity.this.smaller.setOnClickListener(DetailActivity.this.popListener);
                    }
                    if (DetailActivity.this.flag3 == 5) {
                        DetailActivity.this.bigger.setBackgroundResource(R.drawable.text_bigger_pressed);
                        DetailActivity.this.bigger.setOnClickListener(null);
                    }
                    DetailActivity.this.webview.loadUrl("javascript:big(1)");
                    break;
                case R.id.pic_delet:
                    DetailActivity.this.webview.loadUrl("javascript:delsc()");
                    DetailActivity.this.moreWindow.dismiss();
                    break;
                case R.id.pic_favorite:
                    DetailActivity.this.moreWindow.dismiss();
                    DetailActivity.this.webview.loadUrl("javascript:addtosc()");
                    break;
                case R.id.setting_more:
                    DetailActivity.this.moreWindow2.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(DetailActivity.this, SetUpActivity.class);
                    DetailActivity.this.startActivity(intent);
                    break;
                case R.id.detailtool:
                    int detailtool = UIHelper.getShareperference(
                            DetailActivity.this, constants.SAVE_LOCALMSGNUM,
                            "detailtool", 1);
                    if (detailtool == 1) {
                        UIHelper.setSharePerference(DetailActivity.this,
                                constants.SAVE_LOCALMSGNUM, "detailtool", 0);
                        DetailActivity.this.toolBar.setVisibility(View.GONE);
                        DetailActivity.this.toolBar2.setVisibility(View.GONE);
                        DetailActivity.this.detailcomment.setVisibility(View.GONE);
                        DetailActivity.this.check.setChecked(false);
                    } else {
                        UIHelper.setSharePerference(DetailActivity.this,
                                constants.SAVE_LOCALMSGNUM, "detailtool", 1);
                        DetailActivity.this.detail = Integer.parseInt(DetailActivity.this.m_iniFileIO
                                .getIniString(userIni, "APPSHOW", "DetailTool",
                                        "0", (byte) 0));
                        if (DetailActivity.this.detail == 1) {
                            DetailActivity.this.toolBar2.setVisibility(View.VISIBLE);
                        } else {
                            DetailActivity.this.toolBar.setVisibility(View.VISIBLE);
                        }
                        DetailActivity.this.detailcomment.setVisibility(View.VISIBLE);
                        DetailActivity.this.check.setChecked(true);
                    }
                    break;
                case R.id.titletool:
                    int titletool = Integer.parseInt(DetailActivity.this.m_iniFileIO.getIniString(
                            userIni, "APPSHOW", "DetailTitle", "1", (byte) 0));
                    if (titletool == 1) {
                        DetailActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
                                "DetailTitle", "0");
                        DetailActivity.this.detailtitle.setVisibility(View.GONE);
                        DetailActivity.this.title_check.setChecked(false);
                    } else {
                        DetailActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
                                "DetailTitle", "1");
                        DetailActivity.this.detailtitle.setVisibility(View.VISIBLE);
                        DetailActivity.this.title_check.setChecked(true);
                    }
                    break;
                case R.id.detailcomment:
                    int titlecomment = Integer.parseInt(DetailActivity.this.m_iniFileIO.getIniString(
                            userIni, "APPSHOW", "DetailTool", "0", (byte) 0));
                    if (titlecomment == 1) {
                        DetailActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
                                "DetailTool", "0");
                        DetailActivity.this.toolBar.setVisibility(View.VISIBLE);
                        DetailActivity.this.toolBar2.setVisibility(View.GONE);
                        DetailActivity.this.comment_check.setChecked(false);
                    } else {
                        DetailActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
                                "DetailTool", "1");
                        DetailActivity.this.toolBar.setVisibility(View.GONE);
                        DetailActivity.this.toolBar2.setVisibility(View.VISIBLE);
                        DetailActivity.this.comment_check.setChecked(true);
                    }
                    break;
            }
        }
    };

    private void showCustomView(View view,
                                CustomViewCallback callback) {
        // if a view already exists then immediately terminate the new one
        if (this.mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        getWindow().getDecorView();
        FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        FrameLayout decor = (FrameLayout) this.getWindow().getDecorView();
        this.mFullscreenContainer = new DetailActivity.FullscreenHolder(this);
        this.mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(this.mFullscreenContainer, COVER_SCREEN_PARAMS);
        this.mCustomView = view;
        this.setStatusBarVisibility(false);
        this.mCustomViewCallback = callback;
    }

    private void hideCustomView() {
        if (this.mCustomView == null)
            return;

        this.setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) this.getWindow().getDecorView();
        decor.removeView(this.mFullscreenContainer);
        this.mFullscreenContainer = null;
        this.mCustomView = null;
        this.mCustomViewCallback.onCustomViewHidden();
    }

//    @Override
//    public void onReq(BaseReq arg0) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onResp(BaseResp arg0) {
//        // TODO Auto-generated method stub
//
//        System.out.println("&&&&&&&&&&&&&");
//        System.out.println("resp=" + arg0.errCode);
//        int result = 0;
//
//        switch (arg0.errCode) {
//            case ErrCode.ERR_OK:
//                result = string.errcode_success;
//                break;
//            case ErrCode.ERR_USER_CANCEL:
//                result = string.errcode_cancel;
//                break;
//            case ErrCode.ERR_AUTH_DENIED:
//                result = string.errcode_deny;
//                break;
//            default:
//                result = string.errcode_unknown;
//                break;
//        }
//
//        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//    }

    static class FullscreenHolder extends FrameLayout
    {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            this.setBackgroundColor(ctx.getResources().getColor(
                    color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return true;
    }
}
