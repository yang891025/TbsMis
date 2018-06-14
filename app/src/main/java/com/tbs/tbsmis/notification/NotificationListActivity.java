package com.tbs.tbsmis.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.BrowserActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.NotificationAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.widget.NewDataToast;
import com.tbs.tbsmis.widget.PullToRefreshListView;
import com.tbs.tbsmis.widget.PullToRefreshListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import NewsTool.NewsContent;

public class NotificationListActivity extends Activity implements View.OnClickListener
{
    private View lvNews_footer;
    private TextView lvNews_foot_more;
    private ProgressBar lvNews_foot_progress;
    private PullToRefreshListView lvNews;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private AnimationDrawable loadingAnima;
    private ArrayList<HashMap<String, String>> Userlist;
    private List<String> listTag;
    private NotificationAdapter lvUserAdapter;
    private ImageView backBtn;
    private TextView title_tv;
    private ImageView menuBtn;
    //private final boolean isOpenPop;
    private PopupWindow moreWindow2;
    private RelativeLayout cloudTitle;
    private Button clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_user_list);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.clearBtn = (Button) this.findViewById(R.id.clearBtn);
        this.clearBtn.setVisibility(View.VISIBLE);
        this.clearBtn.setText("清空");
        this.menuBtn.setVisibility(View.GONE);
        this.title_tv.setText("推送信息");
        this.clearBtn.setOnClickListener(this);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationListActivity.this.finish();
            }
        });

        this.lvNews_footer = this.getLayoutInflater().inflate(R.layout.listview_footer,
                null);
        this.lvNews_foot_more = (TextView) this.lvNews_footer
                .findViewById(R.id.listview_foot_more);
        this.lvNews_foot_progress = (ProgressBar) this.lvNews_footer
                .findViewById(R.id.listview_foot_progress);
        this.lvNews = (PullToRefreshListView) this.findViewById(R.id.frame_listview_users);
        // 添加底部视图 必须在setAdapter前
        this.lvNews.addFooterView(this.lvNews_footer);
        this.lvNews.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                NotificationAdapter.ViewHolder holder = (NotificationAdapter.ViewHolder) view.getTag();
//                // 点击头部、底部栏无效
                if (position == (Userlist.size() + 1))
                    return;

                String sourceUrl = holder.url;
                // 跳转到新闻详情
                Intent intent = new Intent();
                intent.setClass(NotificationListActivity.this,
                        BrowserActivity.class);
                intent.putExtra("tempUrl", sourceUrl);
                NotificationListActivity.this.startActivity(intent);
            }
        });
        this.lvNews.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                NotificationListActivity.this.lvNews.onScrollStateChanged(view, scrollState);
                // 数据为空--不用继续下面代码了
                if (NotificationListActivity.this.Userlist.isEmpty())
                    return;
                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(NotificationListActivity.this.lvNews_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(NotificationListActivity.this.lvNews.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    NotificationListActivity.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    NotificationListActivity.this.lvNews_foot_more
                            .setText(R.string.pull_to_refresh_refreshing_label);
                    NotificationListActivity.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    // int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    NotificationListActivity.this.initUserData(2);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                NotificationListActivity.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        this.lvNews.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                NotificationListActivity.this.initUserData(1);
            }
        });
        this.connect(0);
    }

    public void clearData() {
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String TextPath = webRoot + "notification/" + "notification.txt";
        FileUtils.deleteFileWithPath(TextPath);
        String savePath = webRoot + "notification/" + "notification.txt.NDX";
            FileUtils.deleteFileWithPath(savePath);
    }

    public void initLocalData() {
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String inipath = webRoot + "notification/" + "notification.ini";
        String savePath = webRoot + "notification/" + "notification.txt";
        this.Userlist = new ArrayList<HashMap<String, String>>();
        NewsContent NewsContent = new NewsContent();
        if (NewsContent.initialize(inipath)) {
          ArrayList<HashMap<String, String>> grouplist = new ArrayList<HashMap<String, String>>();
          long countFile = NewsContent.countField();
          NewsContent.parseNewsFile(savePath,
                  NewsTool.NewsContent.parse_flag_full, 0, 0);
          long news_count = NewsContent.countNews();
          if (news_count > 0) {
              for (long j = news_count-1; j >= 0; j--) {
                  HashMap<String, String> map = new HashMap<String, String>();
                  for (long m = 0; m < countFile; m++) {
                      map.put(NewsContent.getFieldInternalName(m),
                              NewsContent.getNewsField(j, m));
                  }
                  grouplist.add(map);
              }
              this.Userlist.addAll(grouplist);
          }
        }
    }

    public void initUserData(int count) {
        if (count == 0) {
            this.lvUserAdapter = new NotificationAdapter(this.Userlist, this);
            this.lvNews.setAdapter(this.lvUserAdapter);
        }
        if (count == 1) {
            this.lvNews.onRefreshComplete(this.getString(R.string.pull_to_refresh_update)
                    + new Date().toLocaleString());
            NewDataToast.makeText(this, "已是最新信息", false).show();
        } else
            this.lvNews.onRefreshComplete();
        this.lvUserAdapter.notifyDataSetChanged();
        this.lvNews.setTag(UIHelper.LISTVIEW_DATA_FULL);
        if (this.Userlist.size() <= 0)
            this.lvNews_foot_more.setText("无最新消息");
        else
            this.lvNews_foot_more.setText(R.string.load_full);
        this.lvNews_foot_progress.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    private void connect(int count) {
        NotificationListActivity.GetAsyncTask task = new NotificationListActivity.GetAsyncTask(count, this);
        task.execute();
    }

    /**
     * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
     *
     */
    class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

        private final Context context;
        private final int count;

        public GetAsyncTask(int count, Context context) {
            this.context = context;
            this.count = count;
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            NotificationListActivity.this.startAnimation();
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            if (this.count == 1)
                NotificationListActivity.this.clearData();
            NotificationListActivity.this.initLocalData();
            return "ok";
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                NotificationListActivity.this.stopAnimation();
                Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
            } else {
                if (this.count == 1)
                    Toast.makeText(this.context, "已清空", Toast.LENGTH_SHORT).show();
                NotificationListActivity.this.initUserData(0);
                NotificationListActivity.this.stopAnimation();
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
            case R.id.clearBtn:
                this.connect(1);
                break;
        }
    }

}
