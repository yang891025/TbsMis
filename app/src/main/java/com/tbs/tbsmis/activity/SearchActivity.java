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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;

@SuppressLint("SetJavaScriptEnabled")
public class SearchActivity extends Activity implements View.OnClickListener
{
    private static final String TAG = "SearchActivity";
    private ImageView back_btn;
    //private RadioGroup search_category_group;
    private Spinner search_category_btn;
    private EditText news_search_edit;
    private ImageView clear_search_word, search_submit_button;
    private RelativeLayout search_category, search_history;
    private WebView search_result_webview, search_history_webview,
            search_category_webview;
    private WebSettings Settings;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private RelativeLayout loadingIV1;
    private ImageView iv1;
    private RelativeLayout loadingIV2;
    private ImageView iv2;
    private AnimationDrawable loadingAnima1;
    private AnimationDrawable loadingAnima2;
    private ImageView finish_btn;
    private IniFile m_iniFileIO;
    private String baseUrl;
    private SearchActivity.MyBroadcastReciver MyBroadcastReciver;
    private int screenHeight;
    private int screenWidth;
    private FrameLayout search_window;
    private RelativeLayout.LayoutParams laParams;
    public boolean searchAction;
    protected boolean isOpenPop;
    private PopupWindow moreWindow2;
    private RelativeLayout layout_search_title;
    public String tempUrl;
    private ArrayList<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String userIni;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_search);
        Log.i(SearchActivity.TAG, "onCreate");
        MyActivity.getInstance().addActivity(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Action_search"
                + getString(R.string.about_title));
        this.MyBroadcastReciver = new SearchActivity.MyBroadcastReciver();
        this.registerReceiver(this.MyBroadcastReciver, intentFilter);
        this.init();
    }

    private void init() {
        // TODO Auto-generated method stub
        this.back_btn = (ImageView) this.findViewById(R.id.back_btn);
        this.finish_btn = (ImageView) this.findViewById(R.id.finish_btn);
        this.search_window = (FrameLayout) this.findViewById(R.id.search_window);
        this.search_category_btn = (Spinner) this.findViewById(R.id.search_category_btn);
        this.news_search_edit = (EditText) this.findViewById(R.id.news_search_edit);
        this.clear_search_word = (ImageView) this.findViewById(R.id.clear_search_word);
        this.search_submit_button = (ImageView) this.findViewById(R.id.search_submit_button);
        this.search_category = (RelativeLayout) this.findViewById(R.id.search_category);
        this.layout_search_title = (RelativeLayout) this.findViewById(R.id.layout_search_title);
        this.search_history = (RelativeLayout) this.findViewById(R.id.search_history);
        this.search_result_webview = (WebView) this.findViewById(R.id.search_result_webview);
        this.search_history_webview = (WebView) this.findViewById(R.id.search_history_webview);
        this.search_category_webview = (WebView) this.findViewById(R.id.search_category_webview);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.loadingIV1 = (RelativeLayout) this.findViewById(R.id.loading_dialog1);
        this.iv1 = (ImageView) this.findViewById(R.id.gifview1);
        this.loadingIV2 = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv2 = (ImageView) this.findViewById(R.id.gifview2);
        this.back_btn.setOnClickListener(this);
        this.finish_btn.setOnClickListener(this);
        this.finish_btn.setVisibility(View.GONE);
        this.clear_search_word.setOnClickListener(this);
        this.search_submit_button.setOnClickListener(this);
        this.news_search_edit.setFocusableInTouchMode(true);
        this.news_search_edit.requestFocus();

        this.initWebview();
        this.initPath();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenHeight = dm.heightPixels;
        this.screenWidth = dm.widthPixels;
        this.laParams = (RelativeLayout.LayoutParams) this.search_window.getLayoutParams();
        this.laParams.height = this.screenHeight * Integer
                .parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
                        "Window_Hight", "75", (byte) 0)) / 100;
        this.laParams.width = this.screenWidth * Integer
                .parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
                        "Window_Width", "50", (byte) 0)) / 100;
        this.laParams.topMargin = this.screenWidth * Integer
                .parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
                        "Window_Border", "1", (byte) 0)) / 100;
        this.search_window.setLayoutParams(this.laParams);
        this.loadingIV.setVisibility(View.INVISIBLE);
        int cateNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "search", "SearchCount", "0", (byte) 0));
        int curNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "search", "CurrentSearch", "0", (byte) 0));
        if (cateNum <= curNum) {
            curNum = cateNum - 1;
            this.m_iniFileIO.writeIniString(this.userIni,
                    "search", "CurrentSearch", curNum + "");
        }
        this.data_list = new ArrayList<String>();
        for (int i = 0; i < cateNum; i++) {
            String search_Name = this.m_iniFileIO.getIniString(this.userIni, "search",
                    "search" + i, "", (byte) 0);
            this.data_list.add(this.m_iniFileIO.getIniString(this.userIni, search_Name,
                    "SearchTitle", "简单检索", (byte) 0));

        }
        //适配器
        this.arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.data_list);
        //设置样式
        this.arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        this.search_category_btn.setAdapter(this.arr_adapter);
        this.search_category_btn.setSelection(curNum);
        this.search_category_btn.setOnItemSelectedListener(new OnItemSelectedListener()
                                                      {
                                                          @Override
                                                          public void onItemSelected(AdapterView<?> adapterView, View
                                                                  view, int i, long l) {
                                                              SearchActivity.this.initPath();
                                                              String search_Name = SearchActivity.this.m_iniFileIO.getIniString(SearchActivity.this.userIni,
                                                                      "search", "search" + i, "", (byte) 0);
                                                              String tempUrl = StringUtils.isUrl(SearchActivity.this
                                                                      .m_iniFileIO
                                                                              .getIniString(SearchActivity.this.userIni, search_Name,
                                                                                      "SearchURL", "", (byte) 0),
                                                                      SearchActivity.this.baseUrl,
                                                                      UIHelper.getShareperference(
                                                                              SearchActivity.this,
                                                                              constants.SAVE_LOCALMSGNUM, "resname",
                                                                              "yqxx"));
                                                              if (UIHelper.TbsMotion(SearchActivity.this, tempUrl)) {
                                                                  SearchActivity.this.search_category_webview.loadUrl(tempUrl);
                                                                  SearchActivity.this.m_iniFileIO.writeIniString(SearchActivity.this.userIni, "search",
                                                                          "CurrentSearch", i + "");
                                                              }
                                                          }
                                                          @Override
                                                          public void onNothingSelected(AdapterView<?> adapterView) {

                                                          }
                                                      }
        );
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "search",
                "HisWordWindow", "0", (byte) 0)) == 0)

        {
            this.search_history.setVisibility(View.GONE);
        }

        String search_Name = this.m_iniFileIO.getIniString(this.userIni, "search",
                "search" + curNum, "", (byte) 0);

        String tempUrl = StringUtils.isUrl(this.m_iniFileIO.getIniString(this.userIni,
                search_Name, "SearchURL", "", (byte) 0), this.baseUrl, UIHelper
                .getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
        this.search_category_webview.loadUrl(tempUrl);
        String history = StringUtils.isUrl(this.m_iniFileIO.getIniString(this.userIni,
                search_Name, "HistoryURL", "", (byte) 0), this.baseUrl, UIHelper
                .getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
        this.search_history_webview.loadUrl(history);
//        search_category_group
//                .setOnCheckedChangeListener(new OnCheckedChangeListener()
//                {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                        // 根据ID判断选择的按钮
//                        initPath();
//                        String search_Name = m_iniFileIO.getIniString(userIni,
//                                "search", "search" + checkedId, "", (byte) 0);
//                        String tempUrl = StringUtils.isUrl(m_iniFileIO
//                                        .getIniString(userIni, search_Name,
//                                                "SearchURL", "", (byte) 0), baseUrl,
//                                UIHelper.getShareperference(
//                                        SearchActivity.this,
//                                        constants.SAVE_LOCALMSGNUM, "resname",
//                                        "yqxx"));
//                        if (UIHelper.TbsMotion(SearchActivity.this, tempUrl)) {
////                            search_category_btn.setText(m_iniFileIO
////                                    .getIniString(userIni, search_Name,
////                                            "SearchTitle", "", (byte) 0));
//                            search_category_webview.loadUrl(tempUrl);
//                            m_iniFileIO.writeIniString(userIni, "search",
//                                    "CurrentSearch", checkedId + "");
//                        }
//                    }
//                });
        this.news_search_edit.addTextChangedListener(new

                                                        TextWatcher()
                                                        {
                                                            @Override
                                                            public void afterTextChanged(Editable s) {
                                                                // TODO Auto-generated method stub
                                                                // s:变化后的所有字符
                                                            }

                                                            @Override
                                                            public void beforeTextChanged(CharSequence s, int start,
                                                                                          int count,
                                                                                          int after) {
                                                                // s:变化前的所有字符； start:字符开始的位置；
                                                                // count:变化前的总字节数；after:变化后的字节数
                                                            }

                                                            @Override
                                                            public void onTextChanged(CharSequence s, int start, int
                                                                    before,
                                                                                      int count) {
                                                                // TODO Auto-generated method stub
                                                                // S：变化后的所有字符；start：字符起始的位置；before:
                                                                // 变化之前的总字节数；count:变化后的字节数
                                                                if (Integer.parseInt(SearchActivity.this.m_iniFileIO.getIniString(SearchActivity.this.userIni,
                                                                        "search", "HisWordWindow", "0", (byte) 0)) == 1
                                                                        && SearchActivity.this.searchAction == false) {
                                                                    SearchActivity.this.search_history.setVisibility(View.VISIBLE);
                                                                } else {
                                                                    SearchActivity.this.searchAction = false;
                                                                    SearchActivity.this.search_history.setVisibility(View.GONE);
                                                                }
                                                                SearchActivity.this.search_category.setVisibility(View.GONE);
                                                                SearchActivity.this.initPath();
                                                                int checkedId = Integer.parseInt(SearchActivity.this
                                                                        .m_iniFileIO
                                                                        .getIniString(
                                                                                SearchActivity.this.userIni, "search", "CurrentSearch", "0",
                                                                                (byte) 0));
                                                                String search_Name = SearchActivity.this.m_iniFileIO.getIniString(SearchActivity.this.userIni,
                                                                        "search", "search" + checkedId, "", (byte) 0);
                                                                if (count == 0 && start == 0) {
                                                                    //layout_search_history_word.setChecked(true);
                                                                    String history = StringUtils.isUrl(SearchActivity
                                                                            .this.m_iniFileIO
                                                                            .getIniString(SearchActivity.this.userIni, search_Name,
                                                                                    "HistoryURL",
                                                                                    "", (byte) 0), SearchActivity
                                                                            .this.baseUrl, UIHelper
                                                                            .getShareperference(SearchActivity.this,
                                                                                    constants.SAVE_LOCALMSGNUM,
                                                                                    "resname",
                                                                                    "yqxx"));
                                                                    SearchActivity.this.search_history_webview.loadUrl(history);
                                                                } else if (start == 0 && before == 0) {
                                                                    //layout_search_word.setChecked(true);
                                                                    String key_word = SearchActivity.this.m_iniFileIO.getIniString(SearchActivity.this.userIni,
                                                                            search_Name, "KeywordURL", "", (byte) 0);
                                                                    key_word = StringUtils.isUrl(key_word, SearchActivity.this.baseUrl,
                                                                            UIHelper
                                                                                    .getShareperference
                                                                                            (SearchActivity.this,
                                                                                                    constants
                                                                                                            .SAVE_LOCALMSGNUM,
                                                                                                    "resname",
                                                                                                    "yqxx"));
                                                                    if (key_word.indexOf("?") < 0) {
                                                                        key_word = key_word + "?keyword=" + s;
                                                                    } else {
                                                                        key_word = key_word + "&keyword=" + s;
                                                                    }
                                                                    SearchActivity.this.search_history_webview.loadUrl(key_word);
                                                                } else if (start > 0) {
                                                                    //layout_search_word.setChecked(true);
                                                                    String keyAction = SearchActivity.this.m_iniFileIO.getIniString(SearchActivity.this.userIni,
                                                                            search_Name, "KeyAction", "", (byte) 0);
                                                                    SearchActivity.this.search_history_webview.loadUrl("javascript:" +
                                                                            keyAction
                                                                            + "('" + s + "')");
                                                                }
                                                            }

                                                        }

        );
        this.news_search_edit.setOnFocusChangeListener(new

                                                          View.OnFocusChangeListener()
                                                          {
                                                              @Override
                                                              public void onFocusChange(View v, boolean hasFocus) {
                                                                  if (hasFocus) {
                                                                      // 此处为得到焦点时的处理内容
                                                                      SearchActivity.this.search_category.setVisibility(View.GONE);
                                                                      if (Integer.parseInt(SearchActivity.this
                                                                              .m_iniFileIO.getIniString
                                                                              (SearchActivity.this.userIni,
                                                                                      "search", "HisWordWindow", "0",
                                                                                      (byte)
                                                                                              0)) == 1
                                                                              && SearchActivity.this.searchAction == false) {
                                                                          SearchActivity.this.search_history.setVisibility(View.VISIBLE);
                                                                      }
                                                                  } else {
                                                                      // 此处为失去焦点时的处理内容
                                                                  }
                                                              }
                                                          }

        );
        news_search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				/*判断是否是“GO”键*/
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    initPath();
                    if (StringUtils.isEmpty(news_search_edit.getText().toString())) {
                        Toast.makeText(SearchActivity.this, "检索词为空", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        search_category.setVisibility(View.GONE);
                        search_history.setVisibility(View.GONE);
                        int checkedId = Integer.parseInt(m_iniFileIO.getIniString(
                                userIni, "search", "CurrentSearch", "0", (byte) 0));
                        String search_Name = m_iniFileIO.getIniString(userIni,
                                "search", "search" + checkedId, "", (byte) 0);
                        String searchAction = m_iniFileIO.getIniString(userIni,
                                search_Name, "SearchAction", "", (byte) 0);
                        search_category_webview.loadUrl("javascript:" + searchAction
                                + "('" + news_search_edit.getText() + "','"
                                + news_search_edit.getText() + "')");
                        startAnimation();
                    }

                    return true;
                }
                return false;
            }
        });
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
        String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "search",
                "searchAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(this.userIni, "search",
                "searchPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
    }

    public void changMorePopState(View v) {
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
        RelativeLayout home = (RelativeLayout) view.findViewById(R.id.home);
        RelativeLayout go = (RelativeLayout) view.findViewById(R.id.go);
        RelativeLayout goBack = (RelativeLayout) view.findViewById(R.id.goBack);
        RelativeLayout refresh = (RelativeLayout) view
                .findViewById(R.id.refresh);
        // font = (RelativeLayout) view.findViewById(R.R.id.font);
        // background = (RelativeLayout) view.findViewById(R.R.id.background);
        RelativeLayout favorite = (RelativeLayout) view
                .findViewById(R.id.favorite);
        RelativeLayout share = (RelativeLayout) view.findViewById(R.id.share);
        RelativeLayout setup = (RelativeLayout) view.findViewById(R.id.setup);
        RelativeLayout open = (RelativeLayout) view.findViewById(R.id.open);

        if (this.search_result_webview.canGoBack()) {
            goBack.setEnabled(true);
        } else {
            goBack.setEnabled(false);
        }
        if (this.search_result_webview.canGoForward()) {
            go.setEnabled(true);
        } else {
            go.setEnabled(false);
        }
        home.setOnClickListener(this);
        go.setOnClickListener(this);
        goBack.setOnClickListener(this);
        refresh.setOnClickListener(this);
        favorite.setOnClickListener(this);
        share.setOnClickListener(this);
        setup.setOnClickListener(this);
        open.setOnClickListener(this);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "APPSHOW",
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
                SearchActivity.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.layout_search_title.getHeight() * 3 / 2);
        this.moreWindow2.update();
    }

    @SuppressWarnings("deprecation")
    private void initWebview() {
        // TODO Auto-generated method stub
        // 检索结果webview初始化
        this.Settings = this.search_result_webview.getSettings();
        this.Settings.setDefaultTextEncodingName("gb2312");
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
                    SearchActivity.this.startAnimation();
                } else {
                    SearchActivity.this.stopAnimation();
                }
            }
        });

        // 分类检索 search_category_webview初始化
        this.Settings = this.search_category_webview.getSettings();
        this.Settings.setDefaultTextEncodingName("gb2312");
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
        ua = search_category_webview.getSettings().getUserAgentString();
        search_category_webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        UIHelper.addJavascript(this, this.search_category_webview);
        this.search_category_webview.setWebViewClient(UIHelper.getWebViewClient());
        this.search_category_webview.setWebChromeClient(new WebChromeClient()
        {
            private boolean loadingDialogState;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                this.loadingDialogState = progress < 100;

                if (this.loadingDialogState) {
                    SearchActivity.this.startAnimation2();
                } else {
                    SearchActivity.this.stopAnimation2();
                }
            }
        });
        // 检索历史 search_history_webview初始化
        this.Settings = this.search_history_webview.getSettings();
        this.Settings.setDefaultTextEncodingName("gb2312");
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
        ua = search_history_webview.getSettings().getUserAgentString();
        search_history_webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        UIHelper.addJavascript(this, this.search_history_webview);
        this.search_history_webview.setWebViewClient(UIHelper.getWebViewClient());
        this.search_history_webview.setWebChromeClient(new WebChromeClient()
        {
            private boolean loadingDialogState;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                this.loadingDialogState = progress < 100;
                if (this.loadingDialogState) {
                    SearchActivity.this.startAnimation1();
                } else {
                    SearchActivity.this.stopAnimation1();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        Log.i(SearchActivity.TAG, "onStart");
        super.onStart();
//        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "search",
//                "SearchBar", "0", (byte) 0)) == 0) {
//            layout_category_title.setVisibility(View.GONE);
//        } else {
//            layout_category_title.setVisibility(View.VISIBLE);
//        }
//        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "search",
//                "HisWordBar", "0", (byte) 0)) == 0) {
//            layout_history_title.setVisibility(View.GONE);
//        } else {
//            layout_history_title.setVisibility(View.VISIBLE);
//        }
    }

    public void startAnimation1() {
        this.loadingAnima1 = (AnimationDrawable) this.iv1.getBackground();
        this.loadingAnima1.start();
        this.loadingIV1.setVisibility(View.VISIBLE);
    }

    public void stopAnimation1() {
        // loadingAnima.stop();
        this.loadingIV1.setVisibility(View.INVISIBLE);
    }

    public void startAnimation2() {
        this.loadingAnima2 = (AnimationDrawable) this.iv2.getBackground();
        this.loadingAnima2.start();
        this.loadingIV2.setVisibility(View.VISIBLE);
    }

    public void stopAnimation2() {
        // loadingAnima.stop();
        this.loadingIV2.setVisibility(View.INVISIBLE);
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

    private class MyBroadcastReciver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SearchActivity.this.initPath();
            if (action.equals("Action_search"
                    + getString(R.string.about_title))) {
                int SelBtn = intent.getIntExtra("flag", 1);
                switch (SelBtn) {
                    case 1:
                        SearchActivity.this.tempUrl = intent.getStringExtra("tempUrl");
                        String keyWord = intent.getStringExtra("keyWord");
                        SearchActivity.this.news_search_edit.setText(keyWord);
                        SearchActivity.this.news_search_edit.setSelection(keyWord.length());
                        SearchActivity.this.tempUrl = StringUtils.isUrl(SearchActivity.this.tempUrl, SearchActivity.this.baseUrl, UIHelper
                                .getShareperference(SearchActivity.this,
                                        constants.SAVE_LOCALMSGNUM, "resname",
                                        "yqxx"));
                        SearchActivity.this.search_result_webview.loadUrl(SearchActivity.this.tempUrl);
                        SearchActivity.this.searchAction = true;
                        SearchActivity.this.search_category.setVisibility(View.GONE);
                        SearchActivity.this.search_history.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(SearchActivity.TAG, "onDestroy");
        super.onDestroy();
        this.unregisterReceiver(this.MyBroadcastReciver);
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.finish_btn:
                this.changMorePopState(arg0);
                break;
//            case R.R.id.layout_category_back:
//                search_category.setVisibility(View.GONE);
//                break;
//            case R.R.id.layout_history_back:
//                search_history.setVisibility(View.GONE);
//                break;
            case R.id.search_category_btn:
                if (this.search_category.isShown()) {
                    this.search_category.setVisibility(View.GONE);
                } else {
                    this.search_category.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.clear_search_word:
                this.news_search_edit.setText("");
                break;
            case R.id.search_submit_button:
                this.initPath();
                if (StringUtils.isEmpty(this.news_search_edit.getText().toString())) {
                    Toast.makeText(this, "检索词为空", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    this.search_category.setVisibility(View.GONE);
                    this.search_history.setVisibility(View.GONE);
                    int checkedId = Integer.parseInt(this.m_iniFileIO.getIniString(
                            this.userIni, "search", "CurrentSearch", "0", (byte) 0));
                    String search_Name = this.m_iniFileIO.getIniString(this.userIni,
                            "search", "search" + checkedId, "", (byte) 0);
                    String searchAction = this.m_iniFileIO.getIniString(this.userIni,
                            search_Name, "SearchAction", "", (byte) 0);
                    this.search_category_webview.loadUrl("javascript:" + searchAction
                            + "('" + this.news_search_edit.getText() + "','"
                            + this.news_search_edit.getText() + "')");
                    this.startAnimation();
                }
                break;
            case R.id.home:
                //iniPath();
                this.search_result_webview.loadUrl(this.tempUrl);
                this.moreWindow2.dismiss();
                break;
            case R.id.go:
                this.search_result_webview.goForward();
                this.moreWindow2.dismiss();
                break;
            case R.id.goBack:
                this.search_result_webview.goBack();
                this.moreWindow2.dismiss();
                break;
            case R.id.refresh:
                this.search_result_webview.reload();
                this.moreWindow2.dismiss();
                break;
            case R.id.share:
                JsoupExam.getSearchEngine(2, search_result_webview.getUrl(), this);
                this.moreWindow2.dismiss();
                break;
            case R.id.favorite:
                JsoupExam.getSearchEngine(1, search_result_webview.getUrl(), this);
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
                UIHelper.showBrowsere(this, search_result_webview.getUrl());
                this.moreWindow2.dismiss();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (this.search_result_webview.canGoBack()) {
                this.search_result_webview.goBack();
            } else {
                this.finish();
            }
        }
        return false;
    }
}