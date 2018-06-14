package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.PopMenuAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetTaskActivity extends Activity implements View.OnTouchListener,
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
    private PopMenuAdapter MenuListAdapter;
    private String baseUrl;
    private int screenWidth;
    private int screenHight;
    private NetTaskActivity.MyBroadcastReciver MyBroadcastReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mycloud_activity);
        MyActivity.getInstance().addActivity(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Action_pop"
                + getString(R.string.about_title));
        this.MyBroadcastReciver = new NetTaskActivity.MyBroadcastReciver();
        this.registerReceiver(this.MyBroadcastReciver, intentFilter);
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
        this.webtitle.setText("任务管理");
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
        // flash支持
        //webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webview.getSettings().setAppCacheEnabled(true);
        this.webview.getSettings().setAppCacheMaxSize(10240);
        this.webview.clearCache(true);
        this.webview.setClickable(true);
        this.webview.setLongClickable(true);
        this.webview.setOnTouchListener(this);
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
                    NetTaskActivity.this.webtitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int progress) {
                NetTaskActivity.this.loadingDialogState = progress < 100;
                if (NetTaskActivity.this.loadingDialogState) {
                    NetTaskActivity.this.startAnimation();
                } else {
                    NetTaskActivity.this.stopAnimation();
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
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenWidth = dm.widthPixels;
        this.screenHight = dm.heightPixels;
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
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            appNewsFile = dataPath + "TbsApp.ini";
        }
        String portUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "SERVICE",
                "currentPort", constants.DefaultServerPort, (byte) 0);
        String ipUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "SERVICE",
                "currentAddress", constants.DefaultServerIp, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
        tempUrl = "/task/Manage_task.cbs";
        this.tempUrl = StringUtils.isUrl(this.tempUrl, this.baseUrl, UIHelper
                .getShareperference(this, constants.SAVE_LOCALMSGNUM,
                        "resname", "yqxx"));

//        String relatPath = webRoot+"Web";
//        CBSInterpret interpret = new CBSInterpret();
//        interpret.initGlobal(webRoot + "TbsWeb.ini", webRoot);
//        String interpretFile = interpret.Interpret(tempUrl, "GET", "", null, 0);
//        //System.out.println("url.substring(relatPath.length()-1) = "+url.substring(relatPath.length()-1));
//        String FileName = FileUtils.getFileName(interpretFile);
//        String FilePath = FileUtils.getPath(relatPath+tempUrl);
//        File file = new File(FilePath + FileName);
//        try {
//            file.createNewFile();
//            File srcfile = new File(interpretFile);
//            FileUtils.copyFileTo(srcfile,file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        FileUtils.deleteFile(interpretFile);
//        int tmpCount = UIHelper.getShareperference(this, "tmp", "tmpCount", 0);
//        UIHelper.setSharePerference(this,"tmp","tmpFile" + tmpCount, FilePath+ FileName);
//        UIHelper.setSharePerference(this,"tmp","tmpCount", tmpCount+1);
//        tempUrl =  "file://"+FilePath+ FileName;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(this.MyBroadcastReciver);
        MyActivity.getInstance().finishActivity(this);
        if (this.webview != null) {
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
        if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile, "APPSHOW",
                "open_in_browse", "1", (byte) 0)) == 0) {
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
                NetTaskActivity.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.skydriveTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
    }

    public void changMenu(View v, String action) {
        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow_Menu(v, action);
        } else {
            if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow_Menu(View parent, final String action) {
        // iniPath();
        ArrayList<Map<String, String>> MenuList = new ArrayList<Map<String, String>>();
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.main_menu_prefrences, null);
        LinearLayout userinfo = (LinearLayout) view.findViewById(R.id.userinfo);
        ListView menu_list = (ListView) view.findViewById(R.id.listMenuItems);
        userinfo.setVisibility(View.GONE);
        int groupresnum = Integer.parseInt(this.m_iniFileIO.getIniString(
                this.appNewsFile, action, "Count", "0", (byte) 0));
        for (int i = 1; i <= groupresnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            group.put("Title", this.m_iniFileIO.getIniString(this.appNewsFile, action,
                    "Title" + i, "", (byte) 0));
            group.put("Icon", this.m_iniFileIO.getIniString(this.appNewsFile, action,
                    "Icon" + i, "0", (byte) 0));
            MenuList.add(group);
        }
        this.MenuListAdapter = new PopMenuAdapter(MenuList, this);
        menu_list.setAdapter(this.MenuListAdapter);
        // ��listView�ļ�����
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                NetTaskActivity.this.iniPath();
                String url = NetTaskActivity.this.m_iniFileIO.getIniString(NetTaskActivity.this.appNewsFile, action,
                        "Url" + (arg2 + 1), "", (byte) 0);
                if (UIHelper.TbsMotion(NetTaskActivity.this, StringUtils.isUrl(
                        url, NetTaskActivity.this.baseUrl, UIHelper.getShareperference(
                                NetTaskActivity.this,
                                constants.SAVE_LOCALMSGNUM, "resname", "yqxx")))) {
                    NetTaskActivity.this.webview.loadUrl(StringUtils.isUrl(url, NetTaskActivity.this.baseUrl, UIHelper
                            .getShareperference(NetTaskActivity.this,
                                    constants.SAVE_LOCALMSGNUM, "resname",
                                    "yqxx")));
                }
                NetTaskActivity.this.moreWindow2.dismiss();
            }
        });
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
                NetTaskActivity.this.isOpenPop = false;
            }
        });
        String location = this.m_iniFileIO.getIniString(this.appNewsFile, action,
                "location", "right_top", (byte) 0);
        int x = this.screenWidth
                * Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
                action, "location_x", "1", (byte) 0)) / 100;
        int y = this.screenHight
                * Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
                action, "location_y", "0", (byte) 0)) / 100;
        if (location.equalsIgnoreCase("left_bot")) {
            this.moreWindow2.showAtLocation(
                    parent,
                    Gravity.LEFT | Gravity.BOTTOM,
                    x,
                    y
                            + UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "poplocation",
                            this.skydriveTitle.getHeight()));
        } else {
            this.moreWindow2.showAtLocation(
                    parent,
                    Gravity.RIGHT | Gravity.TOP,
                    x,
                    y
                            + UIHelper.getShareperference(this,
                            constants.SAVE_LOCALMSGNUM, "poplocation",
                            this.skydriveTitle.getHeight()) * 3 / 2);
        }
        this.moreWindow2.update();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn2:
                this.finish();
                break;
            case R.id.search_btn2:
                this.iniPath();
                String BtnAction = this.m_iniFileIO.getIniString(this.appNewsFile, "MSGURL",
                        "taskMenu", "tbs:custom_menu", (byte) 0);
                if (StringUtils.isEmpty(BtnAction)) {
                    this.changMorePopState2(v);
                } else {
                    if (UIHelper
                            .TbsMotion(this, StringUtils.isUrl(
                                    BtnAction, this.baseUrl, UIHelper
                                            .getShareperference(
                                                    this,
                                                    constants.SAVE_LOCALMSGNUM,
                                                    "resname", "yqxx")))) {
                        if (!UIHelper.MenuMotion(this, StringUtils
                                .isUrl(BtnAction, this.baseUrl, UIHelper
                                        .getShareperference(this,
                                                constants.SAVE_LOCALMSGNUM,
                                                "resname", "yqxx")))) {
                            this.changMenu(v, StringUtils.isUrl(BtnAction, this.baseUrl,
                                    UIHelper.getShareperference(
                                            this,
                                            constants.SAVE_LOCALMSGNUM, "resname",
                                            "yqxx")));
                        } else {
                            this.changMorePopState2(v);
                        }
                    } else {
                        this.changMorePopState2(v);
                    }
                }
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

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            NetTaskActivity.this.iniPath();
            if (action.equals("Action_pop"
                    + getString(R.string.about_title))) {
                int SelBtn = intent.getIntExtra("flag", 1);
                switch (SelBtn) {
                    case 1:
                        NetTaskActivity.this.webview.loadUrl(NetTaskActivity.this.tempUrl);
                        break;
                    case 2:
                        if (NetTaskActivity.this.webview.canGoForward())
                            NetTaskActivity.this.webview.goForward();
                        else {
                            NetTaskActivity.this.webview.reload();
                        }
                        break;
                    case 3:
                        if (NetTaskActivity.this.webview.canGoBack())
                            NetTaskActivity.this.webview.goBack();
                        else {
                            NetTaskActivity.this.webview.reload();
                        }
                        break;
                    case 4:
                        NetTaskActivity.this.webview.reload();
                        break;
                    case 5:
                        UIHelper.showShareMore(NetTaskActivity.this, NetTaskActivity.this.webtitle
                                .getText().toString());
                        break;
                    case 6:
                        UIHelper.showBrowsere(NetTaskActivity.this, NetTaskActivity.this.tempUrl);
                        break;
                }
            }
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float diffX = e1.getX() - e2.getX();
        float diffY = e1.getY() - e2.getY();
        // TODO Auto-generated method stub
        if (diffY > this.webview.getHeight() / 5 && Math.abs(velocityX) > 30) {
            this.webview.loadUrl("javascript:jump()");
        } else if (diffY < -(this.webview.getHeight() / 5) && Math.abs(velocityX) > 30) {
            this.webview.loadUrl("javascript:OnUpdate()");
        } else if (diffX > 0 && Math.abs(diffX) > Math.abs(diffY)
                && Math.abs(velocityX) > 20) {
            this.webview.loadUrl("javascript:tonext()");
            this.webview.loadUrl("javascript:jumppage(3)");
        } else if (diffX < 0 && Math.abs(diffX) > Math.abs(diffY)
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