package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.BrowserActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.SendNewsAdapter;
import com.tbs.tbsmis.check.SendNewsAdapter.ViewHolder;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.entity.NewsEntity;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.httpRequestUtil;
import com.tbs.tbsmis.widget.NewDataToast;
import com.tbs.tbsmis.widget.PullToRefreshListView;
import com.tbs.tbsmis.widget.PullToRefreshListView.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import NewsTool.NewsContent;
import NewsTool.NewsMessage;

/**
 * Created by TBS on 2016/2/23.
 */
public class WeinXinPageMessage extends Activity implements OnClickListener
{
    private View lvNews_footer;
    private TextView lvNews_foot_more;
    private ProgressBar lvNews_foot_progress;
    private PullToRefreshListView lvNews;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private AnimationDrawable loadingAnima;
    private String userIni;
    private com.tbs.ini.IniFile IniFile;
    private ImageView backBtn;
    private TextView title_tv;
    private ImageView menuBtn;
    private boolean isOpenPop;
    private PopupWindow moreWindow2;
    private RelativeLayout cloudTitle;
    private ArrayList<NewsEntity> News;
    private com.tbs.tbsmis.check.SendNewsAdapter SendNewsAdapter;
    private boolean isMulChoice; // 是否多选
    private LinearLayout layoutButton;
    private Button news_cancle;
    private Button news_send;
    private Button news_check;
    private Button news_preview;
    private Button shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_user_list);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.layoutButton = (LinearLayout) this.findViewById(R.id.layoutButton);
        this.news_send = (Button) this.findViewById(R.id.news_send);
        this.news_preview = (Button) this.findViewById(R.id.news_preview);
        this.news_cancle = (Button) this.findViewById(R.id.news_cancle);
        this.news_check = (Button) this.findViewById(R.id.news_check);
        this.shareBtn = (Button) this.findViewById(R.id.clearBtn);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.news_send.setOnClickListener(this);
        this.news_preview.setOnClickListener(this);
        this.news_cancle.setOnClickListener(this);
        this.news_check.setOnClickListener(this);
        this.layoutButton.setVisibility(View.VISIBLE);
        this.menuBtn.setVisibility(View.GONE);
        this.shareBtn.setVisibility(View.VISIBLE);
        this.shareBtn.setText("分享");
        this.title_tv.setText("页面信息");
        this.backBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                WeinXinPageMessage.this.finish();
            }
        });
        this.menuBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                WeinXinPageMessage.this.changMorePopState2(v);
            }
        });
        this.shareBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                List<String> checked = WeinXinPageMessage.this.SendNewsAdapter.getChecked();
                if (checked.size() <= 0) {
                    if (!WeinXinPageMessage.this.isMulChoice) {
                        WeinXinPageMessage.this.isMulChoice = true;
                        WeinXinPageMessage.this.SendNewsAdapter = new SendNewsAdapter(WeinXinPageMessage.this.isMulChoice, WeinXinPageMessage.this.News,
                                WeinXinPageMessage.this);
                        WeinXinPageMessage.this.lvNews.setAdapter(WeinXinPageMessage.this.SendNewsAdapter);
                    }
                    Toast.makeText(WeinXinPageMessage.this, "请选择一条图文信息",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    //intent.setType("image/*");
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "MIS分享");
                    intent.putExtra(Intent.EXTRA_TEXT, WeinXinPageMessage.this.News.get(Integer.parseInt(checked.get(0))).getTitle()+" "+ WeinXinPageMessage.this.News.get(Integer.parseInt(checked.get(0))).getUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    WeinXinPageMessage.this.startActivity(Intent.createChooser(intent, "分享到"));
                    WeinXinPageMessage.this.isMulChoice = false;
                    WeinXinPageMessage.this.SendNewsAdapter = new SendNewsAdapter(WeinXinPageMessage.this.isMulChoice, WeinXinPageMessage.this.News,
                            WeinXinPageMessage.this);
                    WeinXinPageMessage.this.lvNews.setAdapter(WeinXinPageMessage.this.SendNewsAdapter);
                }
            }
        });
        initPath();
        this.lvNews_footer = this.getLayoutInflater().inflate(R.layout.listview_footer,
                null);
        this.lvNews_foot_more = (TextView) this.lvNews_footer
                .findViewById(R.id.listview_foot_more);
        this.lvNews_foot_progress = (ProgressBar) this.lvNews_footer
                .findViewById(R.id.listview_foot_progress);
        this.lvNews = (PullToRefreshListView) this.findViewById(R.id.frame_listview_users);
        // 添加底部视图 必须在setAdapter前
        this.lvNews.addFooterView(this.lvNews_footer);
        this.lvNews.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                ViewHolder holder = (ViewHolder) view.getTag();
                // 点击头部、底部栏无效
                if (position == 0 || position == WeinXinPageMessage.this.News.size() + 1)
                    return;
                if (WeinXinPageMessage.this.isMulChoice) {
                    holder.news_check.toggle();
                    WeinXinPageMessage.this.SendNewsAdapter.setChecked(position,
                            holder.news_check.isChecked());
                } else {
                    String sourceUrl = holder.content_source_url;
                    // 跳转到新闻详情
                    Intent intent = new Intent();
                    intent.setClass(WeinXinPageMessage.this,
                            BrowserActivity.class);
                    intent.putExtra("tempUrl", sourceUrl);
                    WeinXinPageMessage.this.startActivity(intent);
                }
            }
        });
        this.lvNews.setOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                if (!WeinXinPageMessage.this.isMulChoice) {
                    WeinXinPageMessage.this.isMulChoice = true;
                    WeinXinPageMessage.this.SendNewsAdapter = new SendNewsAdapter(WeinXinPageMessage.this.isMulChoice, WeinXinPageMessage.this.News,
                            WeinXinPageMessage.this);
                    WeinXinPageMessage.this.lvNews.setAdapter(WeinXinPageMessage.this.SendNewsAdapter);
                    WeinXinPageMessage.this.lvNews.setSelection(position);
                }
                return true;
            }
        });
        this.lvNews.setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                WeinXinPageMessage.this.lvNews.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (WeinXinPageMessage.this.News.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(WeinXinPageMessage.this.lvNews_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(WeinXinPageMessage.this.lvNews.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    WeinXinPageMessage.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    WeinXinPageMessage.this.lvNews_foot_more
                            .setText(R.string.pull_to_refresh_refreshing_label);
                    WeinXinPageMessage.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    // int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    WeinXinPageMessage.this.initUserData(2);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                WeinXinPageMessage.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        this.lvNews.setOnRefreshListener(new OnRefreshListener()
        {
            @Override
            public void onRefresh() {
                WeinXinPageMessage.this.connect(0);
            }
        });
        this.connect(0);
    }
    private void initPath(){
        IniFile = new IniFile();
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
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }
    public void initUserData(int count) {
        if (count == 0) {
            this.SendNewsAdapter = new SendNewsAdapter(this.isMulChoice, this.News, this);
            this.lvNews.setAdapter(this.SendNewsAdapter);
        }
        if (count == 1) {
            this.lvNews.onRefreshComplete(this.getString(R.string.pull_to_refresh_update)
                    + new Date().toLocaleString());
            NewDataToast.makeText(this, "已是最新信息", false).show();
        } else
            this.lvNews.onRefreshComplete();
        this.SendNewsAdapter.notifyDataSetChanged();
        this.lvNews.setTag(UIHelper.LISTVIEW_DATA_FULL);
        if (this.News.size() <= 0)
            this.lvNews_foot_more.setText("无最新消息");
        else
            this.lvNews_foot_more.setText(R.string.load_full);
        this.lvNews_foot_progress.setVisibility(View.GONE);
    }

    public void saveData(int count, String Path) {
        // String configPath = getApplicationContext().getFilesDir()
        // .getParentFile().getAbsolutePath();
        // if (configPath.endsWith("/") == false) {
        // configPath = configPath + "/";
        // }
        ArrayList<NewsEntity> NowNews = new ArrayList<NewsEntity>();
        if (count == 1)
            NowNews.addAll(this.News);
        else {
            List<String> checked = this.SendNewsAdapter.getChecked();
            for (int i = 0; i < checked.size(); i++) {
                NowNews.add(this.News.get(Integer.parseInt(checked.get(i))));
            }
        }
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String savePath = webRoot
                + "WeiXin/"
                + this.IniFile.getIniString(this.userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0) + "/" + Path;
        // String savePath = webRoot + "/WeiXin/info";
        String inipath = savePath + "News.ini";
        //System.out.println("inipath=" + inipath);
        NewsContent NewsContent = new NewsContent();
        if (NewsContent.initialize(inipath)) {
            File dirpath = new File(savePath);
            if (!dirpath.exists())
                dirpath.mkdirs();
            savePath += StringUtils.getDateTime() + ".txt";
            // System.out.println("savePath=" + savePath);
            File path = new File(savePath);
            if (!path.exists()) {
                try {
                    path.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < NowNews.size(); i++) {
                NewsMessage msg = new NewsMessage(false);
                for (long j = 2; j < NewsContent.countField(); j++) {
                    if (NewsContent.getFieldInternalName(j).equalsIgnoreCase(
                            "TI:"))
                        msg.setValue(j, NowNews.get(i).getTitle());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("TX:"))
                        msg.setValue(j, NowNews.get(i).getContent());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("CL:"))
                        msg.setValue(j, NowNews.get(i).getMsgType());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("DT:"))
                        msg.setValue(j, StringUtils.getDate());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("HL:"))
                        msg.setValue(j, NowNews.get(i).getUrl());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("AU:"))
                        msg.setValue(j, NowNews.get(i).getAuthor());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("PL:"))
                        msg.setValue(j, NowNews.get(i).getPicUrl());
                    else if (NewsContent.getFieldInternalName(j)
                            .equalsIgnoreCase("EW:"))
                        msg.setValue(j, NowNews.get(i).getDescription());
                    else
                        msg.setValue(j, "");
                }
                NewsContent.addNewsFiled(savePath, msg.getMessageHandle(), -1);
                msg.freeHandle();
            }
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
        send_text.setBackgroundResource(R.drawable.more_all);
        send_text.setOnClickListener(this);
        this.moreWindow2 = new PopupWindow(view,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss() {
                WeinXinPageMessage.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
    }

    public void initLocalData() {
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String filePath = webRoot
                + "WeiXin/"
                + IniFile.getIniString(userIni, "WeiXin", "Account",
                "TBS软件", (byte) 0) + "/infoSave/";
        // String filePath = webRoot+ "/WeiXin/infoSend";
        String fileName = "*.txt";
        this.News = new ArrayList<NewsEntity>();
        String inipath = filePath + "News.ini";
        filePath = filePath + "Page/";

        ArrayList<String> midList = new ArrayList<String>();
        FileUtils.findFiles(filePath, fileName, midList);
        if (midList.size() >= 0) {
            NewsContent NewsContent = new NewsContent();
            if (NewsContent.initialize(inipath)) {
                for (int i = midList.size()-1; i >= 0; i--) {
                    long countFile = NewsContent.countField();
                    NewsContent.parseNewsFile(filePath + midList.get(i),
                            NewsTool.NewsContent.parse_flag_full, 0, 0);
                    long news_count = NewsContent.countNews();
                    for (long j = news_count -1; j >=0; j--) {
                        NewsEntity mapNews = new NewsEntity();
                        for (long m = 2; m < countFile; m++) {
                            if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("TI:"))
                                mapNews.setTitle(NewsContent.getNewsField(j, m));
                            else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("TX:"))
                                mapNews.setContent(NewsContent.getNewsField(j,
                                        m));
                            else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("CL:"))
                                mapNews.setMsgType(NewsContent.getNewsField(j,
                                        m));
                            else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("DT:"))
                                mapNews.setDate(NewsContent.getNewsField(j, m));
                            else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("HL:")) {
                                mapNews.setContentUrl(NewsContent.getNewsField(
                                        j, m));
                                mapNews.setUrl(NewsContent.getNewsField(j, m));
                            } else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("AU:"))
                                mapNews.setAuthor(NewsContent
                                        .getNewsField(j, m));
                            else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("PL:"))
                                mapNews.setPicUrl(NewsContent
                                        .getNewsField(j, m));
                            else if (NewsContent.getFieldInternalName(m)
                                    .equalsIgnoreCase("EW:")) {
                                mapNews.setDescription(NewsContent
                                        .getNewsField(j, m));
                                mapNews.setDigest(NewsContent
                                        .getNewsField(j, m));
                            }
                        }
                        this.News.add(mapNews);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    private void connect(int count) {
        WeinXinPageMessage.GetAsyncTask task = new WeinXinPageMessage.GetAsyncTask(count, this);
        task.execute();
    }

    private void connect(int count, String news) {
        WeinXinPageMessage.GetAsyncTask task = new WeinXinPageMessage.GetAsyncTask(count, this, news);
        task.execute();
    }

    /**
     * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
     */
    class GetAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        private final Context context;
        private final int count;
        private String news;

        public GetAsyncTask(int count, Context context) {
            this.context = context;
            this.count = count;
        }

        public GetAsyncTask(int count, Context context, String news) {
            this.context = context;
            this.count = count;
            this.news = news;
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            WeinXinPageMessage.this.startAnimation();
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            //HttpConnectionUtil connection = new HttpConnectionUtil();
            if (this.count == 0) {
                WeinXinPageMessage.this.initLocalData();
                return "ok";
            } else if (this.count == 1) {
                String verifyURL = WeinXinPageMessage.this.IniFile.getIniString(WeinXinPageMessage.this.userIni, "WeiXin",
                        "WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
                        + "?action=sendPreview";
                JSONObject jsonObject = httpRequestUtil.httpRequest(verifyURL,
                        "GET", this.news);
                if (null != jsonObject) {
                    try {
                        if (jsonObject.getInt("errcode") == 0) {
                            WeinXinPageMessage.this.saveData(0, "infoSend/");
                            return "提交成功";
                        } else {
                            return jsonObject.getInt("errcode") + ":"
                                    + jsonObject.getString("errmsg");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return null;
            } else if (this.count == 2) {
                //saveData(0, "infoSend/");
                String verifyURL = WeinXinPageMessage.this.IniFile.getIniString(WeinXinPageMessage.this.userIni, "WeiXin",
                        "WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
                        + "?action=sendNews";
                JSONObject jsonObject = httpRequestUtil.httpRequest(verifyURL,
                        "GET", this.news);
                if (null != jsonObject) {
                    try {
                        if (jsonObject.getInt("errcode") == 0) {
                            WeinXinPageMessage.this.saveData(0, "infoSend/");
                            return "提交成功";
                        } else {
                            return jsonObject.getInt("errcode") + ":"
                                    + jsonObject.getString("errmsg");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return null;
            } else if (this.count == 3) {
                WeinXinPageMessage.this.saveData(1, "info/");
                return "true";
            }
            return null;
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                WeinXinPageMessage.this.stopAnimation();
                WeinXinPageMessage.this.News = new WeixinNewsUtils().getNewsEntity("");
                WeinXinPageMessage.this.initUserData(0);
                Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
            } else if (this.count == 0) {
                // News = new WeixinNewsUtils().getNewsEntity(result);
                WeinXinPageMessage.this.initUserData(0);
                WeinXinPageMessage.this.stopAnimation();
            } else if (this.count == 1 || this.count == 2) {
                WeinXinPageMessage.this.initUserData(0);
                WeinXinPageMessage.this.stopAnimation();
                Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
            } else if (this.count == 3) {
                WeinXinPageMessage.this.stopAnimation();
                Toast.makeText(this.context, "保存成功", Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.news_preview:
                List<String> checked = this.SendNewsAdapter.getChecked();
                if (checked.size() <= 0) {
                    Toast.makeText(this, "至少选择一条图文信息",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String newsStr = "[";
                    for (int i = 0; i < checked.size(); i++) {
                        String content = this.News.get(Integer.parseInt(checked.get(i)))
                                .getContent().replaceAll("\"", "\'").trim();
                        String WXhtml =  UIHelper.getShareperference(this,
                                constants.SAVE_INFORMATION, "WXhtml", "");
                        String title = this.News.get(Integer.parseInt(checked.get(i)))
                                .getTitle().replaceAll("\"", "\'").trim();
                        if(WXhtml.isEmpty()){

                        }else{
                            content = content + WXhtml;
                        }
                        newsStr += "{\"Title\":\""
                                + title
                                + "\",\"PicUrl\":\""
                                + this.News.get(Integer.parseInt(checked.get(i)))
                                .getPicUrl()
                                + "\",\"author\":\""
                                + this.News.get(Integer.parseInt(checked.get(i)))
                                .getAuthor()
                                + "\",\"content\":\""
                                + content
                                + "\",\"Url\":\""
                                + this.News.get(Integer.parseInt(checked.get(i)))
                                .getContentUrl()
                                + "\",\"digest\":\""
                                + this.News.get(Integer.parseInt(checked.get(i)))
                                .getDigest()
                                + "\",\"show_cover_pic\":\""
                                + this.News.get(Integer.parseInt(checked.get(i)))
                                .getShowCoverPic() + "\"}";
                        if (i < checked.size() - 1) {
                            newsStr += ",";
                        }
                    }
                    newsStr += "]";
                    //FileUtils.writeFile(newsStr.getBytes(), "Tbs/Tbs-App/TbsMis", "aa.txt");
                    this.connect(1, newsStr);
                    this.isMulChoice = false;
                }
                break;
            case R.id.news_check:
                if (!this.isMulChoice) {
                    this.isMulChoice = true;
                    this.SendNewsAdapter = new SendNewsAdapter(this.isMulChoice, this.News,
                            this);
                    this.lvNews.setAdapter(this.SendNewsAdapter);
                }
                break;
            case R.id.menu_save:
                this.connect(3);
                break;
            case R.id.news_cancle:
                this.isMulChoice = false;
                // layoutButton.setVisibility(View.GONE);
                this.SendNewsAdapter = new SendNewsAdapter(this.isMulChoice, this.News,
                        this);
                this.lvNews.setAdapter(this.SendNewsAdapter);
                break;
            case R.id.news_send:
                List<String> sendchecked = this.SendNewsAdapter.getChecked();
                if (sendchecked.size() <= 0) {
                    Toast.makeText(this, "至少选择一条图文信息",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String newsStr = "[";
                    for (int i = 0; i < sendchecked.size(); i++) {
                        String content = this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getContent().replaceAll("\"", "\'");
                        String WXhtml =  UIHelper.getShareperference(this,
                                constants.SAVE_INFORMATION, "WXhtml", "");
                        String title = this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getTitle().replaceAll("\"", "\'").trim();
                        if(WXhtml.isEmpty()){

                        }else{
                            content = content + WXhtml;
                        }
                        newsStr += "{\"Title\":\""
                                + title
                                + "\",\"PicUrl\":\""
                                + this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getPicUrl()
                                + "\",\"author\":\""
                                + this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getAuthor()
                                + "\",\"content\":\""
                                + content
                                + "\",\"Url\":\""
                                + this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getContentUrl()
                                + "\",\"digest\":\""
                                + this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getDigest()
                                + "\",\"show_cover_pic\":\""
                                + this.News.get(Integer.parseInt(sendchecked.get(i)))
                                .getShowCoverPic() + "\"}";

                        if (i < sendchecked.size() - 1) {
                            newsStr += ",";
                        }
                    }
                    newsStr += "]";
                    //System.out.println(newsStr);
                    this.connect(2, newsStr);
                    this.isMulChoice = false;
                }
                break;
        }
    }

}

