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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.BrowserActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.WeiXinNewsAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.widget.NewDataToast;
import com.tbs.tbsmis.widget.PullToRefreshListView;
import com.tbs.tbsmis.widget.PullToRefreshListView.OnRefreshListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import NewsTool.NewsContent;

public class WeixinSendManager extends Activity implements View.OnClickListener {
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private PullToRefreshListView lvNews;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	private ArrayList<HashMap<String, String>> Userlist;
	private List<String> listTag;
	private WeiXinNewsAdapter lvUserAdapter;
	private ImageView backBtn;
	private TextView title_tv;
	private ImageView menuBtn;
	private boolean isOpenPop;
	private PopupWindow moreWindow2;
	private RelativeLayout cloudTitle;

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
        this.menuBtn.setVisibility(View.VISIBLE);
        this.title_tv.setText("群发管理");
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinSendManager.this.finish();
			}
		});
        this.menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeixinSendManager.this.changMorePopState2(v);
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
				WeiXinNewsAdapter.ViewHolder holder = (WeiXinNewsAdapter.ViewHolder) view.getTag();
				// 点击头部、底部栏无效
				if (position == 0 ||position == WeixinSendManager.this.Userlist.size() + 1)
					return;

				String sourceUrl = holder.url;
				// 跳转到新闻详情
				Intent intent = new Intent();
				intent.setClass(WeixinSendManager.this,
						BrowserActivity.class);
				intent.putExtra("tempUrl", sourceUrl);
                WeixinSendManager.this.startActivity(intent);
			}
		});
        this.lvNews.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                WeixinSendManager.this.lvNews.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (WeixinSendManager.this.Userlist.isEmpty())
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(WeixinSendManager.this.lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(WeixinSendManager.this.lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    WeixinSendManager.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    WeixinSendManager.this.lvNews_foot_more
							.setText(R.string.pull_to_refresh_refreshing_label);
                    WeixinSendManager.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					// int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    WeixinSendManager.this.initUserData(2);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
                WeixinSendManager.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
        this.lvNews.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
                WeixinSendManager.this.connect(0);
			}
		});
        this.connect(1);
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
		// String filePath = webRoot+ "/WeiXin/infoSend";
		String fileName = "*.txt";
        this.listTag = new ArrayList<String>();
        this.Userlist = new ArrayList<HashMap<String, String>>();
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
					ArrayList<HashMap<String, String>> grouplist = new ArrayList<HashMap<String, String>>();
					HashMap<String, String> Gmap = new HashMap<String, String>();
					Gmap.put("fileName", midList.get(i));

					long countFile = NewsContent.countField();
					NewsContent.parseNewsFile(filePath + midList.get(i),
                            NewsTool.NewsContent.parse_flag_full, 0, 0);
					long news_count = NewsContent.countNews();
                    if(news_count > 0){
                        this.Userlist.add(Gmap);
                        this.listTag.add(midList.get(i));
                        for (int j = 0; j < news_count; j++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            // map.put("fileName", midList.get(i));
                            for (long m = 2; m < countFile; m++) {
                                map.put(NewsContent.getFieldInternalName(m),
                                        NewsContent.getNewsField(j, m));
                            }
                            grouplist.add(map);
                        }
                        this.Userlist.addAll(grouplist);
                    }else{
                        FileUtils.deleteFile(filePath + midList.get(i));
                        FileUtils.deleteFile(filePath + midList.get(i)+".NDX");
                    }

				}
			}
		}
	}

	public void move2save() {
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
		String rootPath = webRoot
				+ "WeiXin/"
				+ IniFile.getIniString(userIni, "WeiXin", "Account",
						"TBS软件", (byte) 0);
		int data = Integer.parseInt(IniFile.getIniString(userIni, "WeiXin",
				"dataCount", "3", (byte) 0));
		String srcPath = rootPath + "/info/";
		String destPath = rootPath + "/infoSave/";
		String fileName = "*.txt";
		// String filePath = webRoot+ "/WeiXin/infoSend";
		ArrayList<String> srcList = new ArrayList<String>();
		ArrayList<String> destList = new ArrayList<String>();
		FileUtils.findFiles(srcPath, fileName, srcList);
		if (srcList.size() >= 0) {
			Collections.sort(srcList, new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					// TODO Auto-generated method stub
					return rhs.compareToIgnoreCase(lhs);
				}
			});
		}
		FileUtils.findFiles(destPath, fileName, destList);
		if (destList.size() >= 0) {
			Collections.sort(destList, new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					// TODO Auto-generated method stub
					return rhs.compareToIgnoreCase(lhs);
				}
			});
		}
		if (srcList.size() > data) {
			for (int i = data; i < srcList.size(); i++) {
				boolean isexists = false;
				for (int j = 0; j < destList.size(); j++) {
					if (srcList.get(i).equalsIgnoreCase(destList.get(j))) {
						isexists = true;
					}
				}
				if (!isexists) {
					File srcFile = new File(srcPath + srcList.get(i));
					File destFile = new File(destPath + srcList.get(i));
					File srcFileNDX = new File(srcPath + srcList.get(i)
							+ ".NDX");
					File destFileNDX = new File(destPath + srcList.get(i)
							+ ".NDX");
					try {
						FileUtils.copyFileTo(srcFile, destFile);
						FileUtils.copyFileTo(srcFileNDX, destFileNDX);
						FileUtils.deleteFileWithPath(srcPath + srcList.get(i));
						FileUtils.deleteFileWithPath(srcPath + srcList.get(i)
								+ ".NDX");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void initUserData(int count) {
		if (count == 0) {
            this.lvUserAdapter = new WeiXinNewsAdapter(this.Userlist, this, this.listTag);
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
		View view = lay.inflate(R.layout.weixin_send_menu, null);
		RelativeLayout send_text = (RelativeLayout) view
				.findViewById(R.id.send_text);
		RelativeLayout send_news = (RelativeLayout) view
				.findViewById(R.id.send_news);
        RelativeLayout page_news = (RelativeLayout) view
                .findViewById(R.id.page_news);
		RelativeLayout receive_news = (RelativeLayout) view
				.findViewById(R.id.receive_news);
		RelativeLayout sended_news = (RelativeLayout) view
				.findViewById(R.id.sended_news);
		RelativeLayout manage_news = (RelativeLayout) view
				.findViewById(R.id.manage_news);
		RelativeLayout history_news = (RelativeLayout) view
				.findViewById(R.id.history_news);
		history_news.setOnClickListener(this);
        page_news.setOnClickListener(this);
		manage_news.setOnClickListener(this);
		sended_news.setOnClickListener(this);
		receive_news.setOnClickListener(this);
		send_news.setOnClickListener(this);
		send_text.setOnClickListener(this);
        page_news.setVisibility(View.GONE);
        this.moreWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
                WeixinSendManager.this.isOpenPop = false;
			}
		});
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MyActivity.getInstance().finishActivity(this);
	}

	private void connect(int count) {
		WeixinSendManager.GetAsyncTask task = new WeixinSendManager.GetAsyncTask(count, this);
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
            WeixinSendManager.this.startAnimation();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			if (this.count == 1) {
                WeixinSendManager.this.move2save();
			}
            WeixinSendManager.this.initLocalData();
			return "ok";
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
            WeixinSendManager.this.initUserData(0);
            WeixinSendManager.this.stopAnimation();
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
		case R.id.send_text:
            this.moreWindow2.dismiss();
			Intent send = new Intent();
			send.setClass(this, WeixinSendActivity.class);
            this.startActivity(send);
			break;
		case R.id.receive_news:
            this.moreWindow2.dismiss();
			Intent receivenews = new Intent();
			receivenews.setClass(this, WeixinReceiveNews.class);
            this.startActivity(receivenews);
			break;
		case R.id.sended_news:
            this.moreWindow2.dismiss();
			Intent sendednews = new Intent();
			sendednews.setClass(this, WeiXinSendedActivity.class);
            this.startActivity(sendednews);
			break;
		case R.id.manage_news:
            this.moreWindow2.dismiss();
			Intent managenews = new Intent();
			managenews.setClass(this, WeiXinNewsManager.class);
            this.startActivity(managenews);
			break;
		case R.id.send_news:
            this.moreWindow2.dismiss();
			Intent sendnews = new Intent();
			sendnews.setClass(this, WeixinSendNews.class);
            this.startActivity(sendnews);
			break;
        case R.id.page_news:
            this.moreWindow2.dismiss();
            Intent pagenews = new Intent();
            pagenews.setClass(this, WeinXinPageMessage.class);
            this.startActivity(pagenews);
            break;
		case R.id.history_news:
            this.moreWindow2.dismiss();
			Intent historynews = new Intent();
			historynews.setClass(this, WeiXinHistoryActivity.class);
            this.startActivity(historynews);
			break;
		}
	}

}
