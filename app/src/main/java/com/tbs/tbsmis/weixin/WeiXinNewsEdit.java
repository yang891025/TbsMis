package com.tbs.tbsmis.weixin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.LoginDialogResult;
import com.tbs.tbsmis.check.PopMenus;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.TextInputDialog;
import com.tbs.tbsmis.file.TextInputDialog.OnFinishListener;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.JsoupExam;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.htmlJsoupUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import NewsTool.NewsContent;
import NewsTool.NewsMessage;

@SuppressLint("SetJavaScriptEnabled")
public class WeiXinNewsEdit extends Activity implements View.OnClickListener,
        View.OnTouchListener
{

    PopMenus popupWindow_custommenu;
    LinearLayout layout_custommenu, layout_customemenu;

    /**
     * 这个字符串可以从服务端获取
     */
    private ImageView backBtn;
    private ImageView menuBtn;
    protected ValueCallback<Uri> mUploadMessage;
    private TextView wetitle;
    protected boolean isOpenPop;
    private EditText news_url;
    private EditText news_title;
    private TextView news_time;
    private EditText news_content;
    private String fileName;
    private int index = -1;
    private ImageView take_content;

    private EditText news_author;
    private EditText news_describe;
    private TextView news_pic;
    private EditText news_classifi;
    private EditText news_source;
    private EditText news_address;
    private RelativeLayout news_address_layout;
    private RelativeLayout news_source_layout;
    private RelativeLayout news_classifi_layout;
    private RelativeLayout news_pic_layout;
    private RelativeLayout news_author_layout;
    private RelativeLayout news_describe_layout;
    private RelativeLayout news_date_layout;
    private PopupWindow moreWindow2;
    private RelativeLayout cloudTitle;
    private ImageView author_delete;
    private ImageView address_delete;
    private ImageView source_delete;
    private ImageView classifi_delete;
    private ImageView pic_delete;
    private ImageView describe_delete;
    private ImageView take_add;
    private ImageView take_extend;
    private LinearLayout edit_extend;

    protected static final int FILECHOOSER_RESULTCODE = 0;
    private ProgressDialog Prodialog;
    private WeiXinNewsEdit.GetAsyncTask task;
    private Button news_extend_button;
    //private TextView news_extend;
    private boolean isEdit;
    private ImageView img_pic;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_news_edit);

        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.wetitle = (TextView) this.findViewById(R.id.title_tv);
        this.news_extend_button = (Button) this.findViewById(R.id.news_extend_button);
        this.news_address_layout = (RelativeLayout) this.findViewById(R.id.news_address_layout);
        this.news_source_layout = (RelativeLayout) this.findViewById(R.id.news_source_layout);
        this.news_classifi_layout = (RelativeLayout) this.findViewById(R.id.news_classifi_layout);
        this.news_pic_layout = (RelativeLayout) this.findViewById(R.id.news_pic_layout);
        this.news_author_layout = (RelativeLayout) this.findViewById(R.id.news_author_layout);
        this.news_describe_layout = (RelativeLayout) this.findViewById(R.id.news_describe_layout);
        this.news_date_layout = (RelativeLayout) this.findViewById(R.id.news_date_layout);
        this.take_content = (ImageView) this.findViewById(R.id.take_content);
        this.take_add = (ImageView) this.findViewById(R.id.take_add);
        this.author_delete = (ImageView) this.findViewById(R.id.author_delete);
        this.address_delete = (ImageView) this.findViewById(R.id.address_delete);
        this.source_delete = (ImageView) this.findViewById(R.id.source_delete);
        this.classifi_delete = (ImageView) this.findViewById(R.id.classifi_delete);
        this.pic_delete = (ImageView) this.findViewById(R.id.pic_delete);
        this.describe_delete = (ImageView) this.findViewById(R.id.describe_delete);
        this.take_extend = (ImageView) this.findViewById(R.id.take_extend);

        this.edit_extend = (LinearLayout) this.findViewById(R.id.edit_extend);

        this.news_url = (EditText) this.findViewById(R.id.news_url);
        this.news_title = (EditText) this.findViewById(R.id.news_title);
        this.news_time = (TextView) this.findViewById(R.id.news_time);
        this.news_content = (EditText) this.findViewById(R.id.news_content);
        //news_extend = (TextView) findViewById(R.R.id.news_extend);
        this.news_address = (EditText) this.findViewById(R.id.news_address);
        this.news_source = (EditText) this.findViewById(R.id.news_source);
        this.news_classifi = (EditText) this.findViewById(R.id.news_classifi);
        this.news_pic = (TextView) this.findViewById(R.id.news_pic);
        this.img_pic = (ImageView) this.findViewById(R.id.img_pic);
        this.news_author = (EditText) this.findViewById(R.id.news_author);
        this.news_describe = (EditText) this.findViewById(R.id.news_describe);
        this.news_extend_button = (Button) this.findViewById(R.id.news_extend_button);

        if (this.getIntent().getExtras() != null) {
            Intent intent;
            intent = this.getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if ((Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action)) && type != null) {
                if ("text/plain".equals(type)) {
                    this.handleSendText(intent); // 处理发送来的文字
                }
            } else {
                this.index = intent.getIntExtra("index", 0);
                this.fileName = intent.getStringExtra("sourceUrl");
                this.wetitle.setText("信息修改");
                this.isEdit = true;
                this.init();
            }
        } else {
            this.wetitle.setText("新信息");
            this.news_time.setText(StringUtils.getDate());
        }
//        String WXhtml = UIHelper.getShareperference(this,
//                constants.SAVE_INFORMATION, "WXhtml", "");
//         if (WXhtml.isEmpty()) {
//         news_extend.setText(Html.fromHtml(constants.WXhtml));
//
//        } else {
//            news_extend.setText(Html.fromHtml(WXhtml));
//        }
        this.news_pic_layout.setVisibility(View.VISIBLE);
        ///news_pic_layout.setOnClickListener(this);
        this.news_extend_button.setOnClickListener(this);
        this.take_extend.setOnClickListener(this);
        this.author_delete.setOnClickListener(this);
        this.address_delete.setOnClickListener(this);
        this.source_delete.setOnClickListener(this);
        this.classifi_delete.setOnClickListener(this);
        this.pic_delete.setOnClickListener(this);
        this.describe_delete.setOnClickListener(this);
        this.take_add.setOnClickListener(this);
        this.take_content.setOnClickListener(this);
        this.backBtn.setOnClickListener(this);
        this.menuBtn.setOnClickListener(this);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        //System.out.println(sharedText);
        if (sharedText != null) {
            // 根据分享的文字更新UI
            this.wetitle.setText("新信息");
            this.news_time.setText(StringUtils.getDate());
            if (sharedText.contains("-")) {
                if (sharedText.lastIndexOf("-") > sharedText.lastIndexOf(".")) {
                    this.news_url.setText(sharedText.substring(sharedText.indexOf("http://"), sharedText.lastIndexOf("-")));
                    this.news_source.setText(sharedText.substring(sharedText.lastIndexOf("-") + 1));
                } else {
                    this.news_url.setText(sharedText.substring(sharedText.indexOf("http://")));
                }
            } else {
                this.news_url.setText(sharedText.substring(sharedText.indexOf("http://")));
            }
            this.news_title.setText(sharedText.substring(0, sharedText.indexOf("http://")));
        }
    }

    private void init() {
        // TODO Auto-generated method stub

        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String filePath = webRoot
                + "WeiXin/"
                + IniFile.getIniString(userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0) + "/info/";
        String inipath = filePath + "News.ini";
        NewsContent NewsContent = new NewsContent();
        if (NewsContent.initialize(inipath)) {
            long countFile = NewsContent.countField();
            NewsContent.parseNewsFile(this.fileName,
                    NewsTool.NewsContent.parse_flag_full, 0, 0);
            for (long m = 2; m < countFile; m++) {
                if (NewsContent.getFieldInternalName(m).equalsIgnoreCase("TI:"))
                    this.news_title.setText(NewsContent.getNewsField(this.index, m));
                else if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                        "TX:")) {
                    //html = NewsContent.getNewsField(index, m);
                    if (this.Prodialog == null) {
                        this.Prodialog = new ProgressDialog(this);
                        this.Prodialog.setMessage("正在加载内容，请稍候...");
                        this.Prodialog.setIndeterminate(false);
                        this.Prodialog.setCanceledOnTouchOutside(false);
                        this.Prodialog.setButton("隐藏", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method
                                dialog.dismiss();
                            }
                        });
                        this.Prodialog.show();
                    }
                    new imageThread(NewsContent.getNewsField(this.index, m), 1).start();
                } else if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                        "DT:"))
                    this.news_time.setText(NewsContent.getNewsField(this.index, m));
                else if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                        "HL:"))
                    this.news_url.setText(NewsContent.getNewsField(this.index, m));
                else if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                        "CL:")) {
                    if (!StringUtils
                            .isEmpty(NewsContent.getNewsField(this.index, m))) {
                        this.news_classifi_layout.setVisibility(View.VISIBLE);
                        this.news_classifi.setText(NewsContent
                                .getNewsField(this.index, m));
                    }
                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("AR:")) {
                    if (!StringUtils
                            .isEmpty(NewsContent.getNewsField(this.index, m))) {
                        this.news_address_layout.setVisibility(View.VISIBLE);
                        this.news_address
                                .setText(NewsContent.getNewsField(this.index, m));
                    }
                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("SU:")) {
                    if (!StringUtils
                            .isEmpty(NewsContent.getNewsField(this.index, m))) {
                        this.news_source_layout.setVisibility(View.VISIBLE);
                        this.news_source.setText(NewsContent.getNewsField(this.index, m));
                    }
                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("EW:")) {
                    if (!StringUtils
                            .isEmpty(NewsContent.getNewsField(this.index, m))) {
                        this.news_describe_layout.setVisibility(View.VISIBLE);
                        this.news_describe.setText(NewsContent
                                .getNewsField(this.index, m));
                    }
                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("AU:")) {
                    if (!StringUtils
                            .isEmpty(NewsContent.getNewsField(this.index, m))) {
                        this.news_author_layout.setVisibility(View.VISIBLE);
                        this.news_author.setText(NewsContent.getNewsField(this.index, m));
                    }
                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("PL:")) {
                    if (!StringUtils
                            .isEmpty(NewsContent.getNewsField(this.index, m))) {
                        this.news_pic_layout.setVisibility(View.VISIBLE);
                        this.news_pic.setText(NewsContent.getNewsField(this.index, m));
                        ImageLoader imgload = new ImageLoader(this, R.drawable.clean_category_thumbnails);
                        imgload.DisplayImage(NewsContent.getNewsField(this.index, m), this.img_pic);
                    }
                }
            }
        }
    }

    private void addclick(int flag) {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String filePath = webRoot
                + "WeiXin/"
                + IniFile.getIniString(userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0) + "/info/";
        String inipath = filePath + "News.ini";
        List<String> map = new ArrayList<String>();
        NewsContent NewsContent = new NewsContent();
        if (NewsContent.initialize(inipath)) {
            long countFile = NewsContent.countField();
            if (flag == 1) {
                NewsContent.parseNewsFile(this.fileName,
                        NewsTool.NewsContent.parse_flag_full, 0, 0);
                for (long m = 2; m < countFile; m++) {
                    if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                            "CL:")) {
                        if (this.news_classifi_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AR:")) {
                        if (this.news_address_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("SU:")) {
                        if (this.news_source_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("EW:")) {
                        if (this.news_describe_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AU:")) {
                        if (this.news_author_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("PL:")) {
                        if (this.news_pic_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (StringUtils.isEmpty(NewsContent.getNewsField(
                            this.index, m)))
                        map.add(NewsContent.getFieldName(m));
                }

            } else {
                for (long m = 2; m < countFile; m++) {
                    String InternalName = NewsContent.getFieldInternalName(m);
                    if (InternalName.equalsIgnoreCase("TI:")
                            || InternalName.equalsIgnoreCase("TX:")
                            || InternalName.equalsIgnoreCase("DT:")
                            || InternalName.equalsIgnoreCase("HL:")) {

                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("CL:")) {
                        if (this.news_classifi_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AR:")) {
                        if (this.news_address_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("SU:")) {
                        if (this.news_source_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("EW:")) {
                        if (this.news_describe_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AU:")) {
                        if (this.news_author_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("PL:")) {
                        if (this.news_pic_layout.getVisibility() == View.GONE) {
                            map.add(NewsContent.getFieldName(m));
                        }
                    } else {
                        map.add(NewsContent.getFieldName(m));
                    }
                }
            }
            if (map.size() > 0) {
                final String[] items = new String[map.size()];
                // 在数组中存放数据 */
                for (int i = 0; i < map.size(); i++) {
                    items[i] = map.get(i);
                }
                new Builder(this).setTitle("请点击选择")
                        .setPositiveButton("关闭", null)
                        .setItems(items, new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (items[which].equalsIgnoreCase("类别")) {
                                    WeiXinNewsEdit.this.news_classifi_layout
                                            .setVisibility(View.VISIBLE);
                                } else if (items[which].equalsIgnoreCase("区域")) {
                                    WeiXinNewsEdit.this.news_address_layout
                                            .setVisibility(View.VISIBLE);
                                } else if (items[which].equalsIgnoreCase("来源")) {
                                    WeiXinNewsEdit.this.news_source_layout
                                            .setVisibility(View.VISIBLE);
                                } else if (items[which].equalsIgnoreCase("描述")) {
                                    WeiXinNewsEdit.this.news_describe_layout
                                            .setVisibility(View.VISIBLE);
                                } else if (items[which]
                                        .equalsIgnoreCase("图片路径")) {
                                    WeiXinNewsEdit.this.news_pic_layout.setVisibility(View.VISIBLE);
                                } else if (items[which].equalsIgnoreCase("作者")) {
                                    WeiXinNewsEdit.this.news_author_layout
                                            .setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(WeiXinNewsEdit.this,
                                            "无字段信息", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            } else {
                Toast.makeText(this, "无字段可添加", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void savaChange(String newUrl) {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String filePath = webRoot
                + "WeiXin/"
                + IniFile.getIniString(userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0);
        String inipath = filePath + "/info/" + "News.ini";
        String url = newUrl;
        this.news_url.getText().toString();
//        if (relPath.isEmpty()) {
        filePath = filePath + "/info/";
//        } else {
//            inipath = filePath + "/infoSave/" + "News.ini";
//            filePath = filePath + "/infoSave/" + relPath;
        //url = newUrl;
//            File file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//        }
        if (this.index == -1) {
            String savePath = filePath + StringUtils.getDateTime() + ".txt";
            NewsContent NewsContent = new NewsContent();
            if (NewsContent.initialize(inipath)) {
                long countFile = NewsContent.countField();
                NewsMessage msg = new NewsMessage(false);
                for (long m = 2; m < countFile; m++) {
                    if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                            "TI:"))
                        msg.setValue(m, this.news_title.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("TX:"))
                        msg.setValue(m, Html.toHtml(this.news_content.getText()));
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("DT:"))
                        msg.setValue(m, this.news_time.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("HL:"))
                        msg.setValue(m, url);
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("PL:")) {
                        if (StringUtils.isEmpty(this.news_pic.getText().toString()))
                            msg.setValue(m,
                                    "http://e.tbs.com.cn:8003/image/novod.jpg");
                        else
                            msg.setValue(m, this.news_pic.getText().toString());
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("EW:"))
                        msg.setValue(m, this.news_describe.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("CL:"))
                        msg.setValue(m, this.news_classifi.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AR:"))
                        msg.setValue(m, this.news_address.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("SU:"))
                        msg.setValue(m, this.news_source.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AU:"))
                        msg.setValue(m, this.news_author.getText().toString());
                    else
                        msg.setValue(m, "");
                }
                NewsContent.addNewsFiled(savePath, msg.getMessageHandle(), -1);
                msg.freeHandle();
            }

        } else {
            NewsContent NewsContent = new NewsContent();
            if (NewsContent.initialize(inipath)) {
                long countFile = NewsContent.countField();
                NewsContent.parseNewsFile(this.fileName,
                        NewsTool.NewsContent.parse_flag_full, 0, 0);
                NewsMessage msg = new NewsMessage(false);
                for (long m = 2; m < countFile; m++) {
                    if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                            "TI:"))
                        msg.setValue(m, this.news_title.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("TX:"))
                        msg.setValue(m, Html.toHtml(this.news_content.getText()));
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("DT:"))
                        msg.setValue(m, this.news_time.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("HL:"))
                        msg.setValue(m, url);
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("PL:")) {
                        if (StringUtils.isEmpty(this.news_pic.getText().toString()))
                            msg.setValue(m,
                                    "http://e.tbs.com.cn:8003/image/novod.jpg");
                        else
                            msg.setValue(m, this.news_pic.getText().toString());
                    } else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("EW:"))
                        msg.setValue(m, this.news_describe.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("CL:"))
                        msg.setValue(m, this.news_classifi.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AR:"))
                        msg.setValue(m, this.news_address.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("SU:"))
                        msg.setValue(m, this.news_source.getText().toString());
                    else if (NewsContent.getFieldInternalName(m)
                            .equalsIgnoreCase("AU:"))
                        msg.setValue(m, this.news_author.getText().toString());
                    else
                        msg.setValue(m, NewsContent.getNewsField(this.index, m));

                }
                NewsContent.modifyNewsFile(this.fileName, msg.getMessageHandle(),
                        this.index);
                msg.freeHandle();
            }
        }
    }

    private String getPath(String content) {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String filePath = webRoot
                + "WeiXin/"
                + IniFile.getIniString(userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0) + "/info/";
        String savePath = filePath;
        if (content.equalsIgnoreCase("path")) {
            savePath = filePath + "src/";
        }
        return savePath;
    }

    private String save(String fileName, String content, String url) {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String filePath = webRoot
                + "WeiXin/"
                + IniFile.getIniString(userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0) + "/info/";
        String savePath = filePath + fileName;
        if (StringUtils.isEmpty(fileName)) {
            savePath = filePath + StringUtils.getTime() + ".html";
        }
        if (StringUtils.isEmpty(content)) {
            try {
                FileIO.CreateFile(savePath, htmlJsoupUtil.modifyLink(JsoupExam.getHtml(url), "", this.getWebPath
                        (url) + "/" + this.getRelatePath(url) + "/"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (content.equalsIgnoreCase("path")) {
            savePath = filePath + "src/" + fileName;
            FileIO.CreateNewFile(savePath);
        } else {
            FileIO.CreateFile(savePath, content);
        }
        return savePath;
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
        View view = lay.inflate(R.layout.weixin_save_menu, null);
        RelativeLayout send_text = (RelativeLayout) view
                .findViewById(R.id.menu_save);
        RelativeLayout send_upload = (RelativeLayout) view
                .findViewById(R.id.menu_upload);
//        RelativeLayout send_save_page = (RelativeLayout) view
//                .findViewById(R.R.id.menu_save_page);
        RelativeLayout send_news = (RelativeLayout) view
                .findViewById(R.id.menu_take);
        RelativeLayout receive_news = (RelativeLayout) view
                .findViewById(R.id.menu_add);
        send_text.setOnClickListener(this);
        send_text.setBackgroundResource(R.drawable.more_all);
        if (!this.isEdit) {
            send_news.setVisibility(View.VISIBLE);
            send_upload.setVisibility(View.VISIBLE);
//        send_save_page.setVisibility(View.VISIBLE);
            receive_news.setVisibility(View.VISIBLE);
            receive_news.setOnClickListener(this);
            send_upload.setOnClickListener(this);
//        send_save_page.setOnClickListener(WeiXinNewsEdit.this);
            send_news.setOnClickListener(this);
            send_text.setVisibility(View.GONE);
            send_upload.setBackgroundResource(R.drawable.more_up);
        }

        this.moreWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss() {
                WeiXinNewsEdit.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //System.out.println(resultCode);
        switch (resultCode) {
            case Activity.RESULT_OK:
                // System.out.println(data.getExtras().getString( "result" ));
                this.news_pic.setText(data.getExtras().getString("result"));
                ImageLoader imgload = new ImageLoader(this,R.drawable.clean_category_thumbnails);
                imgload.DisplayImage(data.getExtras().getString("result"), this.img_pic);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn2:
                this.finish();
                break;
            case R.id.news_pic_layout:

                break;
            case R.id.author_delete:
                this.news_author_layout.setVisibility(View.GONE);
                //news_author.setText("");
                break;
            case R.id.address_delete:
                this.news_address_layout.setVisibility(View.GONE);
                //news_address.setText("");
                break;
            case R.id.classifi_delete:
                this.news_classifi_layout.setVisibility(View.GONE);
                this.news_classifi.setText("");
                break;
            case R.id.pic_delete:
                if (this.news_content.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请先拉取信息",
                            Toast.LENGTH_SHORT).show();
                } else {
                    new Builder(this).setNeutralButton("编辑链接", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TextInputDialog dialog = new TextInputDialog(WeiXinNewsEdit.this, "编辑链接", "请输入新链接",
                                    WeiXinNewsEdit.this.news_pic.getText().toString(),
                                    new OnFinishListener()
                                    {
                                        @Override
                                        public boolean onFinish(String text) {
                                            if (TextUtils.isEmpty(text))
                                                return false;
                                            WeiXinNewsEdit.this.news_pic.setText(text);
                                            ImageLoader imgload = new ImageLoader(WeiXinNewsEdit.this,R.drawable.clean_category_thumbnails);
                                            imgload.DisplayImage(text, WeiXinNewsEdit.this.img_pic);
                                            return true;
                                        }
                                    });
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    }).setNegativeButton("选择图片", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Document doc1 = Jsoup.parse(Html.toHtml(WeiXinNewsEdit.this.news_content.getText()));
                            Elements imagElements = doc1.select("img[src]");
                            Iterator<Element> iterator = imagElements.iterator();
                            if (iterator.hasNext()) {
                                //String picUrl = imagElements.first().attr("abs:src");
                                Intent intent = new Intent();
                                intent.putExtra("html", Html.toHtml(WeiXinNewsEdit.this.news_content.getText()));
                                intent.setClass(WeiXinNewsEdit.this, WeiXinPicActivity.class);
                                WeiXinNewsEdit.this.startActivityForResult(intent, 0);
                            } else {
                                Elements imagElements2 = doc1.select("img[data-src]");
                                Iterator<Element> iterator2 = imagElements2.iterator();
                                if (iterator2.hasNext()) {
                                    Intent intent = new Intent();
                                    intent.putExtra("html", Html.toHtml(WeiXinNewsEdit.this.news_content.getText()));
                                    intent.setClass(WeiXinNewsEdit.this, WeiXinPicActivity.class);
                                    WeiXinNewsEdit.this.startActivityForResult(intent, 0);
                                } else {
                                    Toast.makeText(WeiXinNewsEdit.this, "暂无其他图片，可编辑默认",
                                            Toast.LENGTH_SHORT).show();
                                    TextInputDialog dialog = new TextInputDialog(WeiXinNewsEdit.this, "编辑链接", "",
                                            WeiXinNewsEdit.this.news_pic.getText().toString(),
                                            new OnFinishListener()
                                            {
                                                @Override
                                                public boolean onFinish(String text) {
                                                    if (TextUtils.isEmpty(text))
                                                        return false;
                                                    WeiXinNewsEdit.this.news_pic.setText(text);
                                                    ImageLoader imgload = new ImageLoader(WeiXinNewsEdit.this,R.drawable.clean_category_thumbnails);
                                                    imgload.DisplayImage(text, WeiXinNewsEdit.this.img_pic);
                                                    return true;
                                                }
                                            });
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.show();
                                }
                            }
                        }
                    }).setPositiveButton("隐藏", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            WeiXinNewsEdit.this.news_pic_layout.setVisibility(View.GONE);
                            dialogInterface.dismiss();
                        }
                    }).show();
                }

                //news_pic.setText("");
                break;
            case R.id.describe_delete:
                this.news_describe_layout.setVisibility(View.GONE);
                //news_describe.setText;
                break;
            case R.id.source_delete:
                this.news_source_layout.setVisibility(View.GONE);
                //news_source.setText("");
                break;
            case R.id.menu_take:
                if (this.news_url.getText().toString().startsWith("http://") || this.news_url.getText().toString().startsWith
                        ("https://")) {
                    new Builder(this).setMessage("是否确认拉取信息？").setTitle("提示")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    WeiXinNewsEdit.this.connect(0, WeiXinNewsEdit.this.news_url.getText().toString());
                                }
                            }).setNegativeButton("取消", null).show();
                } else {
                    Toast.makeText(this, "请开头添加“http://”进行访问",
                            Toast.LENGTH_SHORT).show();
                }
                this.moreWindow2.dismiss();
                break;
            case R.id.take_extend:
                if (this.edit_extend.getVisibility() == View.GONE) {
                    this.edit_extend.setVisibility(View.VISIBLE);
                    this.news_date_layout.setVisibility(View.VISIBLE);
                } else {
                    this.edit_extend.setVisibility(View.GONE);
                    this.news_date_layout.setVisibility(View.GONE);
                }
                break;
            case R.id.menu_add:
                if (this.index == -1) {
                    this.addclick(0);
                } else {
                    this.addclick(1);
                }
                this.moreWindow2.dismiss();
                break;
            case R.id.menu_upload:
                IniFile m_iniFileIO = new IniFile();
                String webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appNewsFile = webRoot
                        + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String userIni = appNewsFile;
                if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1){
                    String dataPath = getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                        "LoginFlag", "0", (byte) 0)) == 1) {
                    if (StringUtils.isEmpty(this.news_title.getText().toString())
                            || StringUtils.isEmpty(this.news_content.getText().toString())
                            || StringUtils.isEmpty(this.news_url.getText().toString())
                            && this.news_url.getText().toString().length() > 15) {
                        Toast.makeText(this, "字段不可为空/正文不少15个字符",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        new Builder(this).setMessage("是否确认上传信息和资源？").setTitle("提示")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        WeiXinNewsEdit.this.connect(2, WeiXinNewsEdit.this.news_url.getText().toString());
                                    }
                                }).setNegativeButton("取消", null).show();
                    }

                } else {
                    this.startActivity(new Intent(this, LoginDialogResult.class));
                }
                this.moreWindow2.dismiss();
                break;
//            case R.R.id.menu_save_page:
//                IniFile iniFileIO = new IniFile();
//                String webRoot1 = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
//                        "Path", "");
//                if (webRoot1.endsWith("/") == false) {
//                    webRoot1 += "/";
//                }
//                String WebIniFile1 = webRoot1 + constants.WEB_CONFIG_FILE_NAME;
//                String appNewsFile1 = webRoot1
//                        + iniFileIO.getIniString(WebIniFile1, "TBSWeb", "IniName",
//                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
//                if (Integer.parseInt(iniFileIO.getIniString(appNewsFile1, "USER",
//                        "LoginFlag", "0", (byte) 0)) == 1) {
//                    if (StringUtils.isEmpty(news_title.getText().toString())
//                            || StringUtils.isEmpty(news_content.getText().toString())
//                            || StringUtils.isEmpty(news_url.getText().toString())
//                            && news_url.getText().toString().length() > 15) {
//                        Toast.makeText(WeiXinNewsEdit.this, "字段不可为空/正文不少15个字符",
//                                Toast.LENGTH_SHORT).show();
//                    } else {
//                        new AlertDialog.Builder(this).setMessage("是否确认上传信息和资源？").setTitle("提示")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener()
//                                {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        UIHelper.setSharePerference(WeiXinNewsEdit.this,
//                                                constants.SAVE_INFORMATION, "WXhtml", news_extend.getText()
// .toString());
//                                        connect(2, news_url.getText().toString());
//                                    }
//                                }).setNegativeButton("取消", null).show();
//                    }
//
//                } else {
//                    startActivity(new Intent(this, LoginDialogResult.class));
//                }
//                moreWindow2.dismiss();
//                break;
            case R.id.menu_save:
                if (StringUtils.isEmpty(this.news_title.getText().toString())
                        || StringUtils.isEmpty(this.news_content.getText().toString())
                        || StringUtils.isEmpty(this.news_url.getText().toString())
                        && this.news_url.getText().toString().length() > 15) {
                    Toast.makeText(this, "字段不可为空/正文不少15个字符",
                            Toast.LENGTH_SHORT).show();
                } else {
                    this.savaChange(this.news_url.getText().toString());
                    this.finish();
                }
                break;
            case R.id.take_add:
                if (this.index == -1) {
                    this.addclick(0);
                } else {
                    this.addclick(1);
                }

                break;
            case R.id.news_extend_button:
                new Builder(this)
//                        .setNeutralButton("编辑", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        LayoutInflater factory = LayoutInflater.from(WeiXinNewsEdit.this);// 提示框
//                        final View view = factory.inflate(R.layout.edite_extend, null);// 这里必须是final的
//                        final EditText edit = (EditText) view.findViewById(R.R.id.extend_content);// 获得输入框对象
//                        edit.setText(UIHelper.getShareperference(WeiXinNewsEdit.this,
//                                constants.SAVE_INFORMATION, "WXhtml", constants.WXhtml));
//                        edit.setSelection(edit.getText().toString().length());// 光标位置
//                        // edit.setCursorVisible(false);
//                        edit.setSelectAllOnFocus(true);// 全选文本
//                        new AlertDialog.Builder(WeiXinNewsEdit.this).setTitle("编辑附件").setView(view).setNeutralButton("保存", new DialogInterface.OnClickListener()
//
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                UIHelper.setSharePerference(WeiXinNewsEdit.this,
//                                        constants.SAVE_INFORMATION, "WXhtml", edit.getText().toString());
//                                dialogInterface.dismiss();
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).setCancelable(false).show();
//                    }
//                })
        .setNegativeButton("选择模板", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        intent.setClass(WeiXinNewsEdit.this, WeiXinTemplateActivity.class);
                        WeiXinNewsEdit.this.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("预览", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (WeiXinNewsEdit.this.Prodialog == null) {
                            WeiXinNewsEdit.this.Prodialog = new ProgressDialog(WeiXinNewsEdit.this);
                            WeiXinNewsEdit.this.Prodialog.setMessage("正在加载内容，请稍候...");
                            WeiXinNewsEdit.this.Prodialog.setIndeterminate(false);
                            WeiXinNewsEdit.this.Prodialog.setCanceledOnTouchOutside(false);
                            WeiXinNewsEdit.this.Prodialog.setButton("隐藏", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            if (!WeiXinNewsEdit.this.Prodialog.isShowing()) {
                                WeiXinNewsEdit.this.Prodialog = new ProgressDialog(WeiXinNewsEdit.this);
                                WeiXinNewsEdit.this.Prodialog.setMessage("正在加载内容，请稍候...");
                                WeiXinNewsEdit.this.Prodialog.setIndeterminate(false);
                                WeiXinNewsEdit.this.Prodialog.setCanceledOnTouchOutside(false);
                                WeiXinNewsEdit.this.Prodialog.setButton("隐藏", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                        WeiXinNewsEdit.this.Prodialog.show();
                        new imageThread(constants.WXhtml, 2).start();
                        dialogInterface.dismiss();
                    }
                }).show();

                // if (news_url.getText().toString().startsWith("http://")) {
                // connect(0);
                // }
                break;

            case R.id.take_content:
                if (this.news_url.getText().toString().startsWith("http://") || this.news_url.getText().toString().startsWith
                        ("https://")) {
                    new Builder(this).setMessage("是否确认拉取信息？").setTitle("提示")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    WeiXinNewsEdit.this.connect(0, WeiXinNewsEdit.this.news_url.getText().toString());
                                }
                            }).setNegativeButton("取消", null).show();
                } else {
                    Toast.makeText(this, "请开头添加“http://”进行访问",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.search_btn2:
                this.changMorePopState2(v);
                break;
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    private void connect(int count, String path) {
        this.task = new WeiXinNewsEdit.GetAsyncTask(count, this, path);
        this.task.execute();
    }

    /**
     * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
     */
    class GetAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        private final Context context;
        private final int count;
        private String filePath;

        public GetAsyncTask(int count, Context context, String Path) {
            this.context = context;
            this.count = count;
            WeiXinNewsEdit.this.Prodialog = new ProgressDialog(context);
            filePath = Path;
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            if (this.count == 1) {
                WeiXinNewsEdit.this.Prodialog.setMessage("正在上传，请稍候...");

            } else {
                WeiXinNewsEdit.this.Prodialog.setMessage("正在拉取，请稍候...");
            }
            WeiXinNewsEdit.this.Prodialog.setIndeterminate(false);
            WeiXinNewsEdit.this.Prodialog.setCanceledOnTouchOutside(false);
            WeiXinNewsEdit.this.Prodialog.setButton("取消", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method
                    WeiXinNewsEdit.this.task.cancel(true);
                    dialog.dismiss();
                }
            });
            WeiXinNewsEdit.this.Prodialog.show();
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            HttpConnectionUtil connection = new HttpConnectionUtil();
            if (this.count == 1) {
                IniFile m_iniFileIO = new IniFile();
                String webRoot = UIHelper.getShareperference(WeiXinNewsEdit.this, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }

                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appNewsFile = webRoot
                        + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String userIni = appNewsFile;
                if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1){
                    String dataPath = this.context.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                String verifyURL = m_iniFileIO.getIniString(userIni, "WeiXin",
                        "WeAccount", "http://e.tbs.com.cn/wechatServlet.do",
                        (byte) 0)
                        + "?action=uploadFile";
                try {
                    Elements FileMents = JsoupExam.DoJsoup(this.filePath);
                    for (int i = 0; i < FileMents.size(); i++) {
                        this.publishProgress(i, FileMents.size());
                        String srcPath = FileMents.get(i).attr("abs:href");
                        if (StringUtils.isEmpty(srcPath)) {
                            srcPath = FileMents.get(i).attr("abs:src");
                        }
                        String fileName = FileUtils.getFileName(srcPath);
                        if (fileName.contains("?")) {
                            fileName = fileName.substring(0, fileName.indexOf("?"));
                        }
                        if (fileName.isEmpty()) {
                            continue;
                        }
                        String savePath = WeiXinNewsEdit.this.getPath("path");
                        //String relatePath = WeiXinNewsEdit.this.getRelatePath(srcPath);
                        Map<String, String> param = new HashMap<>();
                        param.put("path", "src");
                        param.put("account", m_iniFileIO.getIniString(userIni, "Login",
                                "Account", "", (byte) 0));
                        connection.asyncConnect(verifyURL,
                                param, savePath + fileName, this.context);
                    }
                    //String relatePath = WeiXinNewsEdit.this.getRelatePath(this.filePath);
                    Map<String, String> param = new HashMap<>();
                    param.put("path", "/");
                    param.put("account", m_iniFileIO.getIniString(userIni, "Login",
                            "Account", "", (byte) 0));
                    this.filePath = WeiXinNewsEdit.this.save("", "", this.filePath);
                    String verify = connection.asyncConnect(verifyURL,
                            param, this.filePath, this.context);
                    if ("true".equalsIgnoreCase(verify)) {
                        String pathurl = WeiXinNewsEdit.this.getWebPath(verifyURL) + "/upload/" + m_iniFileIO.getIniString(userIni,
                                "Login",
                                "Account", "", (byte) 0) + "/" + StringUtils.getDateTime() + "/" +
                                FileUtils.getFileName(this.filePath);
                        WeiXinNewsEdit.this.savaChange(pathurl);
                        return "true";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "false";
            } else if (this.count == 2) {
                try {
                    Elements FileMents = JsoupExam.DoJsoup(this.filePath);
                    for (int i = 0; i < FileMents.size(); i++) {
                        this.publishProgress(i, FileMents.size());
                        String srcPath = FileMents.get(i).attr("abs:href");
                        if (StringUtils.isEmpty(srcPath)) {
                            srcPath = FileMents.get(i).attr("abs:src");
                        }
                        String fileName = FileUtils.getFileName(srcPath);
                        if (fileName.contains("?")) {
                            fileName = fileName.substring(0, fileName.indexOf("?"));
                        }
                        if (fileName.isEmpty()) {
                            continue;
                        }
                        String savePath = WeiXinNewsEdit.this.save(fileName, "path", "");
                        connection.downFile(srcPath, savePath);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "";
            } else {
//                String webRoot = UIHelper.getShareperference(WeiXinNewsEdit.this,
//                        constants.SAVE_INFORMATION, "Path", "");
//                if (webRoot.endsWith("/") == false) {
//                    webRoot += "/";
//                }
//                IniFile IniFile = new IniFile();
//                String verifyURL = IniFile.getIniString(configPath, "WeiXin",
//                        "WeAccount", "http://e.tbs.com.cn/wechatServlet.do",
//                        (byte) 0)
//                        + "?action=uploadFile";
//                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
//                String appNewsFile = webRoot
//                        + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
//                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
//                String userIni = appNewsFile;
//                if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
//                        "LoginType", "0", (byte) 0)) == 1){
//                    String dataPath = this.context.getFilesDir().getParentFile()
//                            .getAbsolutePath();
//                    if (dataPath.endsWith("/") == false) {
//                        dataPath = dataPath + "/";
//                    }
//                    userIni = dataPath + "TbsApp.ini";
//                }
//                verifyURL = WeiXinNewsEdit.this.getWebPath(verifyURL) + "/upload/" + IniFile.getIniString(userIni, "USER",
//                        "Account", "", (byte) 0) + "/" + StringUtils.getDateTime() + "/";
                String title = "";
                String html = "";
                try {
                    Document doc = Jsoup.connect(this.filePath).timeout(30000).get();
                    title = doc.title();
                    html = WeiXinNewsEdit.this.getHtml(doc);
                    html = htmlJsoupUtil.modifyLink(html, "", WeiXinNewsEdit.this.getWebPath(this.filePath) + "/" + WeiXinNewsEdit.this
                            .getRelatePath
                            (this.filePath) + "/");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (title == "")
                    return html;
                else
                    return html + ":" + title;
            }
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {

            if (result == null) {
                WeiXinNewsEdit.this.Prodialog.dismiss();
                Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
            } else {
                if (this.count == 1) {
                    WeiXinNewsEdit.this.Prodialog.dismiss();
                    if ("true".equalsIgnoreCase(result)) {
                        Toast.makeText(this.context, "保存成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this.context, "保存失败", Toast.LENGTH_SHORT).show();
                    }
                    FileUtils.deleteFile(this.filePath);
                    FileUtils.deleteDirectory(WeiXinNewsEdit.this.getPath("path"));
                } else if (this.count == 2) {
                    WeiXinNewsEdit.this.Prodialog.dismiss();
                    WeiXinNewsEdit.this.connect(1, this.filePath);
                    //news_content.setText(result);
                    //news_webview.loadUrl(news_url.getText().toString());
                } else {
                    if (result.contains(":")) {
                        if (WeiXinNewsEdit.this.news_title.getText().toString().equals("")) {
                            WeiXinNewsEdit.this.news_title.setText(result.substring(result.lastIndexOf(":") + 1));
                        }
                        new imageThread(result.substring(0, result.lastIndexOf(":")), 1).start();
                        //news_content.setText(Html.fromHtml(result.substring(0, result.lastIndexOf(":")), null, null));
                        Document doc1 = Jsoup.parse(result.substring(0, result.lastIndexOf(":")));
                        Elements imagElements = doc1.select("img[src]");
                        Iterator<Element> iterator = imagElements.iterator();
                        while (iterator.hasNext()) {
                            Element element = iterator.next();
                            String picUrl = element.attr("abs:src");
                            if (!picUrl.isEmpty()) {
                                if (!picUrl.endsWith("gif")) {
                                    WeiXinNewsEdit.this.news_pic.setText(picUrl);
                                    ImageLoader imgload = new ImageLoader(WeiXinNewsEdit.this,R.drawable.clean_category_thumbnails);
                                    imgload.DisplayImage(picUrl, WeiXinNewsEdit.this.img_pic);
                                    break;
                                }
                            }
                        }
                        if (WeiXinNewsEdit.this.news_pic.getText().toString().isEmpty()) {
                            Elements imagElements2 = doc1.select("img[data-src]");
                            Iterator<Element> iterator2 = imagElements2.iterator();
                            while (iterator2.hasNext()) {
                                Element element = iterator2.next();
                                String picUrl = element.attr("abs:data-src");
                                if (!picUrl.isEmpty()) {
                                    if (!picUrl.endsWith("gif")) {
                                        WeiXinNewsEdit.this.news_pic.setText(picUrl);
                                        ImageLoader imgload = new ImageLoader(WeiXinNewsEdit.this,R.drawable.clean_category_thumbnails);
                                        imgload.DisplayImage(picUrl, WeiXinNewsEdit.this.img_pic);
                                        break;
                                    }
                                }
                            }
                        }
                        if (WeiXinNewsEdit.this.news_pic.getText().toString().isEmpty()) {
                            WeiXinNewsEdit.this.news_pic.setText("http://e.tbs.com.cn:8003/image/novod.jpg");
                            ImageLoader imgload = new ImageLoader(WeiXinNewsEdit.this,R.drawable.clean_category_thumbnails);
                            imgload.DisplayImage("http://e.tbs.com.cn:8003/image/novod.jpg", WeiXinNewsEdit.this.img_pic);
                        }
                    } else {
                        //news_content.setText(Html.fromHtml(result, imgGetter, null));
                        //html = result;
                        new imageThread(result, 1).start();
                    }
                    //news_webview.loadUrl(news_url.getText().toString());
                }
            }

        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (this.count == 1) {
                WeiXinNewsEdit.this.Prodialog.setMessage(" 正在上传资源文件，请稍候...(" + (values[0] + 1) + "/"
                        + values[1] + ")");
            } else {
                WeiXinNewsEdit.this.Prodialog.setMessage(" 正在拉取资源文件，请稍候...(" + (values[0] + 1) + "/"
                        + values[1] + ")");
            }

        }

    }

    private String getHtml(Document doc) {
        Element html = doc.getElementById("js_content");
        if (null == html) {
            html = doc.getElementById("Cnt-Main-Article-QQ");
            if (null == html) {
                html = doc.getElementById("artibody");
                if (html == null) {
                    html = doc.getElementById("endText");
                    if (html == null) {
                        html = doc.getElementById("article");
                        if (html == null) {
                            html = doc.getElementById("content-inner");
                            if (html == null) {
                                html = doc.getElementById("main_content");
                                if (html == null) {
                                    html = doc.getElementById("content");
                                    if (html == null) {
                                        html = doc.body();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return html.html();
    }

    private String getRelatePath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(srcPath.indexOf("/") + 1);
        else
            srcPath = "";
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.lastIndexOf("/"));
        return srcPath;
    }

    private String getWebPath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.indexOf("/"));
        return "http://" + srcPath;
    }

    public class imageThread extends Thread
    {
        private String html = "";
        private int cate = 1;

        public imageThread(String html, int cate) {
            this.html = html;
            this.cate = cate;
        }

        Message msg = Message.obtain();

        @Override
        public void run() {
            // TODO Auto-generated method stub
            /**
             * 要实现图片的显示需要使用Html.fromHtml的一个重构方法：public static Spanned
             * fromHtml (String source, Html.ImageGetterimageGetter,
             * Html.TagHandler
             * tagHandler)其中Html.ImageGetter是一个接口，我们要实现此接口，在它的getDrawable
             * (String source)方法中返回图片的Drawable对象才可以。
             */
            ImageGetter imageGetter = new ImageGetter()
            {

                @Override
                public Drawable getDrawable(String source) {
                    // TODO Auto-generated method stub
                    //System.out.println(source);
                    URL url;
                    Drawable drawable = null;
                    try {
                        url = new URL(source);
                        drawable = Drawable.createFromStream(
                                url.openStream(), null);
                        drawable.setBounds(0, 0,
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return drawable;
                }
            };
            //System.out.println("html = "+ html);
            CharSequence test = Html.fromHtml(this.html, imageGetter, null);
            this.msg.what = this.cate;
            this.msg.obj = test;
            WeiXinNewsEdit.this.handler.sendMessage(this.msg);
        }

    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 1) {
                if (WeiXinNewsEdit.this.Prodialog != null) {
                    if (WeiXinNewsEdit.this.Prodialog.isShowing())
                        WeiXinNewsEdit.this.Prodialog.dismiss();
                }
                WeiXinNewsEdit.this.news_content.setText((CharSequence) msg.obj);
            } else if (msg.what == 2) {
                new Builder(WeiXinNewsEdit.this).setMessage((CharSequence) msg.obj).setCancelable(false)
                        .setPositiveButton("关闭", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                if (WeiXinNewsEdit.this.Prodialog != null) {
                    if (WeiXinNewsEdit.this.Prodialog.isShowing())
                        WeiXinNewsEdit.this.Prodialog.dismiss();
                }
                //news_extend.setText((CharSequence) msg.obj);
            }
            super.handleMessage(msg);
        }
    };
}
