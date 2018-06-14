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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.SendNewsAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.entity.NewsEntity;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.widget.NewDataToast;
import com.tbs.tbsmis.widget.PullToRefreshListView;
import com.tbs.tbsmis.widget.PullToRefreshListView.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import NewsTool.NewsContent;

public class WeiXinNewsManager extends Activity implements View.OnClickListener {
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private PullToRefreshListView lvNews;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	private String userIni;
	private IniFile IniFile;
	private ImageView backBtn;
	private TextView title_tv;
	private ImageView menuBtn;
	private ArrayList<NewsEntity> News;
	private SendNewsAdapter SendNewsAdapter;
	private boolean isMulChoice; // 是否多选
	private LinearLayout layoutButton;
	private Button news_cancle;
	private Button news_check;
	private Button news_delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_news_list);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
		// cloudTitle = (RelativeLayout) findViewById(R.R.id.titlebar_layout);
        this.layoutButton = (LinearLayout) this.findViewById(R.id.layoutButton);
        this.news_delete = (Button) this.findViewById(R.id.news_delete);
        this.news_cancle = (Button) this.findViewById(R.id.news_cancle);
        this.news_check = (Button) this.findViewById(R.id.news_check);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.news_delete.setOnClickListener(this);
        this.news_cancle.setOnClickListener(this);
        this.news_check.setOnClickListener(this);
        this.layoutButton.setVisibility(View.VISIBLE);
		// menuBtn.setVisibility(View.GONE);
        this.title_tv.setText("管理信息");
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeiXinNewsManager.this.finish();
			}
		});
        this.menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(WeiXinNewsManager.this, WeiXinNewsEdit.class);
                WeiXinNewsManager.this.startActivity(intent);
			}
		});

        this.IniFile = new IniFile();
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
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
				SendNewsAdapter.ViewHolder holder = (SendNewsAdapter.ViewHolder) view.getTag();
				// 点击头部、底部栏无效
				if (position == 0 || position == WeiXinNewsManager.this.News.size() + 1)
					return;
				if (WeiXinNewsManager.this.isMulChoice) {
					holder.news_check.toggle();
                    WeiXinNewsManager.this.SendNewsAdapter.setChecked(position,
							holder.news_check.isChecked());
				} else {
					String sourceUrl = holder.fileName;
					long index = holder.index;
					// 跳转到新闻详情
					Intent intent = new Intent();
					intent.setClass(WeiXinNewsManager.this,
							WeiXinNewsEdit.class);
					intent.putExtra("sourceUrl", sourceUrl);
					intent.putExtra("index", (int)index);
                    WeiXinNewsManager.this.startActivity(intent);
				}
			}
		});
        this.lvNews.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!WeiXinNewsManager.this.isMulChoice) {
                    WeiXinNewsManager.this.isMulChoice = true;

                    WeiXinNewsManager.this.SendNewsAdapter = new SendNewsAdapter(WeiXinNewsManager.this.isMulChoice, WeiXinNewsManager.this.News,
							WeiXinNewsManager.this);
                    WeiXinNewsManager.this.lvNews.setAdapter(WeiXinNewsManager.this.SendNewsAdapter);
                    WeiXinNewsManager.this.lvNews.setSelection(position);
				}
				return true;
			}
		});
        this.lvNews.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                WeiXinNewsManager.this.lvNews.onScrollStateChanged(view, scrollState);
				// 数据为空--不用继续下面代码了
				if (WeiXinNewsManager.this.News.isEmpty())
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(WeiXinNewsManager.this.lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(WeiXinNewsManager.this.lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    WeiXinNewsManager.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    WeiXinNewsManager.this.lvNews_foot_more
							.setText(R.string.pull_to_refresh_refreshing_label);
                    WeiXinNewsManager.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					// int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    WeiXinNewsManager.this.initUserData(2);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
                WeiXinNewsManager.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
        this.lvNews.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
                WeiXinNewsManager.this.connect(0);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
        this.connect(0);
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

	public void deletData(String Path) {

		ArrayList<NewsEntity> NowNews = new ArrayList<NewsEntity>();

		List<String> checked = this.SendNewsAdapter.getChecked();
		for (int i = 0; i < checked.size(); i++) {
			NowNews.add(this.News.get(Integer.parseInt(checked.get(i))));
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
		String inipath = savePath + "News.ini";
		NewsContent NewsContent = new NewsContent();
		if (NewsContent.initialize(inipath)) {
			for (int i = 0; i < NowNews.size(); i++) {
				NewsContent.parseNewsFile(NowNews.get(i).getFileName(),
                        NewsTool.NewsContent.parse_flag_full, 0, 0);
				NewsContent.removeNews(NowNews.get(i).getNewsCount());
				NewsContent.mergeNewsFile(NowNews.get(i).getFileName());
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

	public void initLocalData() {
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
		String fileName = "*.txt";
        this.News = new ArrayList<NewsEntity>();
		// appList = new ArrayList<String>();
		// appList = new ArrayList<HashMap<String, Object>>();/* 在数组中存放数据 */
		ArrayList<String> midList = new ArrayList<String>();
		FileUtils.findFiles(filePath, fileName, midList);
		if (midList.size() >= 0) {
            Collections.sort(midList, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    // TODO Auto-generated method stub
                    return rhs.compareToIgnoreCase(lhs);
                }
            });
			String inipath = filePath + "News.ini";
			NewsContent NewsContent = new NewsContent();
			if (NewsContent.initialize(inipath)) {
                for (int i = 0; i < midList.size(); i++) {
					long countFile = NewsContent.countField();
					NewsContent.parseNewsFile(filePath + midList.get(i),
                            NewsTool.NewsContent.parse_flag_full, 0, 0);
					long news_count = NewsContent.countNews();
                    for (int j = 0; j < news_count; j++) {
						NewsEntity mapNews = new NewsEntity();
						mapNews.setFileName(filePath + midList.get(i));
						mapNews.setNewsCount(j);
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
							}

							else if (NewsContent.getFieldInternalName(m)
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
		WeiXinNewsManager.GetAsyncTask task = new WeiXinNewsManager.GetAsyncTask(count, this);
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
            WeiXinNewsManager.this.startAnimation();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			if (this.count == 0) {
                WeiXinNewsManager.this.initLocalData();
				return "ok";
			} else if (this.count == 1) {
                WeiXinNewsManager.this.deletData("info/");
                WeiXinNewsManager.this.initLocalData();
				return "ok";
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
                WeiXinNewsManager.this.stopAnimation();
                WeiXinNewsManager.this.News = new WeixinNewsUtils().getNewsEntity("");
                WeiXinNewsManager.this.initUserData(0);
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else if (this.count == 0) {
				// News = new WeixinNewsUtils().getNewsEntity(result);
                WeiXinNewsManager.this.initUserData(0);
                WeiXinNewsManager.this.stopAnimation();
			} else if (this.count == 1) {
                WeiXinNewsManager.this.initUserData(0);
                WeiXinNewsManager.this.stopAnimation();
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
		case R.id.news_delete:
            this.isMulChoice = false;
			List<String> sendchecked = this.SendNewsAdapter.getChecked();
			if (sendchecked.size() <= 0) {
				Toast.makeText(this, "至少选择一条图文信息",
						Toast.LENGTH_SHORT).show();
			} else {
                this.connect(1);
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
		case R.id.news_cancle:
            this.isMulChoice = false;
			// layoutButton.setVisibility(View.GONE);
            this.SendNewsAdapter = new SendNewsAdapter(this.isMulChoice, this.News,
                    this);
            this.lvNews.setAdapter(this.SendNewsAdapter);
			break;
		}
	}

}
