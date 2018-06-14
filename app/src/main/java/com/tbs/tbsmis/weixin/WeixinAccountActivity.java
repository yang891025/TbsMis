package com.tbs.tbsmis.weixin;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.WeChatAccountAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.widget.NewDataToast;
import com.tbs.tbsmis.widget.PullToRefreshListView;
import com.tbs.tbsmis.widget.PullToRefreshListView.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WeixinAccountActivity extends Activity {
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private PullToRefreshListView lvNews;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	private String userIni;
	private IniFile IniFile;
	private String appIniFile;
	private ArrayList<HashMap<String, String>> Userlist;
	private WeChatAccountAdapter lvUserAdapter;
	private ImageView backBtn;
	private TextView title_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_user_list);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.title_tv.setText("公众号管理");
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(WeixinAccountActivity.this,
						WeixinActivity.class);
                WeixinAccountActivity.this.startActivity(intent);
                WeixinAccountActivity.this.finish();
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
        this.lvNews.addFooterView(this.lvNews_footer);// 添加底部视图 必须在setAdapter前
        this.lvNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击头部、底部栏无效
				if (position == 0 || position == WeixinAccountActivity.this.Userlist.size() + 1)
					return;
				WeChatAccountAdapter.ViewHolder holder = (WeChatAccountAdapter.ViewHolder) view.getTag();
				String apptext = holder.svrPath;
				String iniPath = holder.iniPath;
				String category = holder.category;
				String newsUrl = holder.newsUrl;
				String account = holder.tv.getText().toString();
				// 跳转到公众号页
                WeixinAccountActivity.this.IniFile.writeIniString(WeixinAccountActivity.this.userIni, "WeiXin", "WeToken", apptext);
                WeixinAccountActivity.this.IniFile.writeIniString(WeixinAccountActivity.this.userIni, "WeiXin", "iniPath", iniPath);
                WeixinAccountActivity.this.IniFile.writeIniString(WeixinAccountActivity.this.userIni, "WeiXin", "Account", account);
                WeixinAccountActivity.this.IniFile.writeIniString(WeixinAccountActivity.this.userIni, "WeiXin", "category",
						category);
                WeixinAccountActivity.this.IniFile.writeIniString(WeixinAccountActivity.this.userIni, "WeiXin", "newsUrl", newsUrl);
				Intent intent = new Intent();
				intent.setClass(WeixinAccountActivity.this,
						WeixinActivity.class);
                WeixinAccountActivity.this.startActivity(intent);
			}
		});
        this.lvNews.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                WeixinAccountActivity.this.lvNews.onScrollStateChanged(view, scrollState);
				// 数据为空--不用继续下面代码了
				if (WeixinAccountActivity.this.Userlist.isEmpty())
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(WeixinAccountActivity.this.lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(WeixinAccountActivity.this.lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    WeixinAccountActivity.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    WeixinAccountActivity.this.lvNews_foot_more
							.setText(R.string.pull_to_refresh_refreshing_label);
                    WeixinAccountActivity.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					// int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    WeixinAccountActivity.this.initUserData(2);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
                WeixinAccountActivity.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
        this.lvNews.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
                WeixinAccountActivity.this.initUserData(0);
			}
		});
        this.Userlist = new ArrayList<HashMap<String, String>>();
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
            this.lvUserAdapter = new WeChatAccountAdapter(this.Userlist, this);
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
		WeixinAccountActivity.GetAsyncTask task = new WeixinAccountActivity.GetAsyncTask(count, this);
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
			if (this.count == 0)
                WeixinAccountActivity.this.startAnimation();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			String verifyURL = WeixinAccountActivity.this.IniFile.getIniString(WeixinAccountActivity.this.userIni, "WeiXin",
					"WeAccount", "http://e.tbs.com.cn/wechatServlet.do",
					(byte) 0)
					+ "?action=getWeChatAll";
			return connection.asyncConnect(verifyURL, null, HttpConnectionUtil.HttpMethod.GET,
                    this.context);
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
                WeixinAccountActivity.this.stopAnimation();
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONArray jsonUsers = new JSONObject(result)
							.getJSONArray("users");
					for (int i = jsonUsers.length() - 1; i >= 0; i--) {
						JSONObject ob = jsonUsers.getJSONObject(i);
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("name", ob.getString("name"));
						map.put("svrPath", ob.getString("svrPath"));
						map.put("iniPath", ob.getString("iniPath"));
						map.put("category", ob.getString("category"));
						map.put("newsUrl", ob.getString("newsUrl"));
                        WeixinAccountActivity.this.Userlist.add(map);
					}
				} catch (JSONException e) {
					Toast.makeText(this.context, "data is illegal",
							Toast.LENGTH_SHORT).show();
				}
                WeixinAccountActivity.this.initUserData(0);
                WeixinAccountActivity.this.stopAnimation();
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

}
