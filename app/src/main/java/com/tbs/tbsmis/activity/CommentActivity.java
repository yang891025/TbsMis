package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.EditText;
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
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends Activity implements View.OnClickListener
{
    private WebView webview;
    private RelativeLayout loadingIV;
    private ImageView iv;

    private AnimationDrawable loadingAnima;
    private boolean loadingDialogState;
    private String tempUrl;
    private ImageView backBtn;
    private TextView title;
    private IniFile m_iniFileIO;
    private String appNewsFile;
    private ViewSwitcher mFootViewSwitcher;
    private ImageButton mFootPubcomment;
    private EditText mFootEditer;
    private InputMethodManager imm;
    private ImageButton comment;
    private LinearLayout comment_backBtn;
    private RelativeLayout mFootSync;
    private ImageButton commentbtnRight;
    private ImageView menuBtn;
    private RelativeLayout home;
    private RelativeLayout go;
    private RelativeLayout goBack;
    private RelativeLayout refresh;
    private RelativeLayout favorite;
    private RelativeLayout share;
    private RelativeLayout open;
    private RelativeLayout setup;
    private PopupWindow moreWindow2;
    private boolean isOpenPop;
    private RelativeLayout cloudTitle;
    private String userIni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.comment_detail);
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @SuppressWarnings("deprecation")
    public void init() {
        this.backBtn = (ImageView) this.findViewById(R.id.basic_back_btn);
        menuBtn = (ImageView) this.findViewById(R.id.basic_down_btn);
        this.title = (TextView) this.findViewById(R.id.title_tvv);
        cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.webview = (WebView) this.findViewById(R.id.webview4);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        menuBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.title.setText("评论/笔记");
        this.comment_backBtn = (LinearLayout) this.findViewById(R.id.Commentbtnback);
        this.comment = (ImageButton) this.findViewById(R.id.commentbtnClose);
        this.commentbtnRight = (ImageButton) this.findViewById(R.id.commentbtnRight);
        this.comment_backBtn.setOnClickListener(this);
        commentbtnRight.setOnClickListener(this);
        this.comment.setOnClickListener(this);
        this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.mFootViewSwitcher = (ViewSwitcher) this.findViewById(R.id.comment_detail_foot_viewswitcher);
        this.mFootPubcomment = (ImageButton) this.findViewById(R.id.commentbtnInput);
        this.mFootPubcomment.setOnClickListener(this);
        this.mFootSync = (RelativeLayout) this.findViewById(R.id.CommentSync);
        this.mFootEditer = (EditText) this.findViewById(R.id.detailetComment);
        this.mFootEditer.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    CommentActivity.this.imm.showSoftInput(v, 0);
                } else {
                    CommentActivity.this.imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (CommentActivity.this.mFootViewSwitcher.getDisplayedChild() == 1) {
                        CommentActivity.this.mFootViewSwitcher.setDisplayedChild(0);
                        CommentActivity.this.mFootEditer.clearFocus();
                        CommentActivity.this.mFootEditer.setVisibility(View.GONE);
                        CommentActivity.this.mFootSync.setVisibility(View.GONE);
                    }
                }
            }
        });
        this.mFootEditer.setOnKeyListener(new OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (CommentActivity.this.mFootViewSwitcher.getDisplayedChild() == 1) {
                        CommentActivity.this.mFootViewSwitcher.setDisplayedChild(0);
                        CommentActivity.this.mFootEditer.clearFocus();
                        CommentActivity.this.mFootEditer.setVisibility(View.GONE);
                        CommentActivity.this.mFootSync.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });

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
        this.appNewsFile = webRoot
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
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlineAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlinePort", constants.DefaultServerPort, (byte) 0);
        String baseUrl = "http://" + ipUrl + ":" + portUrl;
        this.tempUrl = this.m_iniFileIO.getIniString(this.userIni, "MSGURL",
                "CommentMsg", "/comment/comment.cbs", (byte) 0);
        /**
         * webview��������
         */
        this.webview.getSettings().setSavePassword(false);
        this.webview.getSettings().setSaveFormData(false);
        this.webview.getSettings().setJavaScriptEnabled(true);
        this.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        // ���ñ����ʽ
//        this.webview.getSettings().setDefaultTextEncodingName("gb2312");
        // 自适应屏幕
        // webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
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
        // flash支持
        //webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        this.webview.clearCache(true);
        this.webview.setClickable(true);
        this.webview.setLongClickable(true);
        //
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        UIHelper.addJavascript(this, this.webview);
        this.webview.setWebViewClient(UIHelper.getWebViewClient());
        this.webview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                CommentActivity.this.loadingDialogState = progress < 100;
                if (CommentActivity.this.loadingDialogState) {
                    CommentActivity.this.startAnimation();
                } else {
                    CommentActivity.this.stopAnimation();
                }
            }
        });
        this.webview.setFocusable(true);
        this.webview.requestFocus();
        String resname = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
        this.tempUrl = StringUtils.isUrl(baseUrl + tempUrl, baseUrl, resname);
        this.webview.loadUrl(this.tempUrl);
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
        share.setVisibility(View.GONE);
        favorite.setVisibility(View.GONE);
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "APPSHOW",
                "open_in_browse", "1", (byte) 0)) == 0){
            open.setVisibility(View.GONE);
        }
        this.home.setOnClickListener(this);
        this.go.setOnClickListener(this);
        this.goBack.setOnClickListener(this);
        this.refresh.setOnClickListener(this);
        this.favorite.setOnClickListener(this);
        this.share.setOnClickListener(this);
        this.setup.setOnClickListener(this);
        this.open.setOnClickListener(this);
        this.moreWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                isOpenPop = false;
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
            case R.id.basic_back_btn:
                this.finish();
                break;
            case R.id.basic_down_btn:
                this.changMorePopState2(v);
                break;
            case R.id.Commentbtnback:
                this.finish();
                break;
            case R.id.commentbtnRight:
                new MyAsyncTask(this).execute();
            case R.id.commentbtnInput:
                this.mFootViewSwitcher.showNext();
                this.mFootEditer.setVisibility(View.VISIBLE);
                this.mFootSync.setVisibility(View.VISIBLE);
                this.mFootEditer.requestFocus();
                break;
            case R.id.commentbtnClose:
                // 恢复初始底部栏
                this.mFootViewSwitcher.setDisplayedChild(0);
                this.mFootEditer.clearFocus();
                this.mFootEditer.setVisibility(View.GONE);
                this.mFootSync.setVisibility(View.GONE);
                this.mFootEditer.setText("");
                break;
            case R.id.home:
                //this.iniPath();
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
//            case id.share:
//                UIHelper.showShareMore(this, this.webtitle.getText()
//                        .toString());
//                this.moreWindow2.dismiss();
//                break;
            case R.id.setup:
                Intent intent = new Intent();
                // intent = new Intent();
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

    public Map<String, String> sendverify() {
        String userIni = this.appNewsFile;
        if (Integer.parseInt(this.m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("account", m_iniFileIO.getIniString(userIni, "Login", "Account",
                "", (byte) 0));
        params.put("username", m_iniFileIO.getIniString(userIni, "Login", "UserName",
                "", (byte) 0));
        params.put("comment", mFootEditer.getText().toString());
        params.put("title", "");
        params.put("localTime", StringUtils.getDate());
        return params;
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        private final Context context;
        private ProgressDialog Prodialog;

        public MyAsyncTask(Context c) {
            context = c;
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            HttpConnectionUtil connection = new HttpConnectionUtil();
            constants.verifyURL = "http://"
                    + m_iniFileIO.getIniString(userIni, "Offline",
                    "offlineAddress", constants.DefaultServerIp, (byte) 0)
                    + ":"
                    + m_iniFileIO.getIniString(userIni, "Offline", "offlinePort",
                    constants.DefaultServerPort, (byte) 0)
                    + "/comment/upcomment.cbs";
            return connection.asyncConnect(constants.verifyURL, sendverify(), HttpConnectionUtil.HttpMethod.POST,
                    this.context);
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            Prodialog.dismiss();
            if (result.equals("success")) {
                // 恢复初始底部栏
                mFootViewSwitcher.setDisplayedChild(0);
                mFootEditer.clearFocus();
                mFootEditer.setVisibility(View.GONE);
                mFootSync.setVisibility(View.GONE);
                mFootEditer.setText("");
                webview.reload();
                Toast.makeText(this.context, "提交成功", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this.context, "提交失败", Toast.LENGTH_LONG).show();
            }
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            Prodialog = new ProgressDialog(context);
            Prodialog.setMessage("正在提交评论，请稍候...");
            Prodialog.setIndeterminate(false);
            Prodialog.setCanceledOnTouchOutside(false);
            Prodialog.setButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    dialog.dismiss();
                }
            });
            Prodialog.show();
        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
