package com.tbs.tbsmis.activity;

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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.SendNewsAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.DiskInfoEntity;
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
import java.util.Map;

public class SkyDriveActivity extends Activity implements View.OnClickListener {
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;
	private PullToRefreshListView lvNews;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private AnimationDrawable loadingAnima;
	private String AppIniPath;
	private IniFile IniFile;
	private String appIniFile;
	private ImageView backBtn;
	private TextView title_tv;
	private ImageView menuBtn;
	private boolean isOpenPop;
	private PopupWindow moreWindow2;
	private RelativeLayout cloudTitle;
	private Map<String, String> valuse;
	private ArrayList<DiskInfoEntity> diskList;
	private SendNewsAdapter SendNewsAdapter;
	private boolean isMulChoice; // 是否多选
	private LinearLayout layoutButton;
	public String parent;
    private String userIni;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.weixin_user_list);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog2);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.layoutButton = (LinearLayout) this.findViewById(R.id.layoutButton);
        this.iv = (ImageView) this.findViewById(R.id.gifview2);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.layoutButton.setVisibility(View.GONE);
        this.menuBtn.setVisibility(View.VISIBLE);
        this.title_tv.setText("网盘管理");
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SkyDriveActivity.this.finish();
			}
		});
        this.menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                SkyDriveActivity.this.changMorePopState2(v);
			}
		});

		String configPath = this.getApplicationContext().getFilesDir()
				.getParentFile().getAbsolutePath();
		if (configPath.endsWith("/") == false) {
			configPath = configPath + "/";
		}
        this.AppIniPath = configPath + constants.APP_CONFIG_FILE_NAME;
		String webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;

        this.IniFile = new IniFile();
        this.appIniFile = webRoot
				+ this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appIniFile;
        if(Integer.parseInt(this.IniFile.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
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
				if (position == 0 || position == SkyDriveActivity.this.diskList.size() + 1)
					return;
				if (SkyDriveActivity.this.isMulChoice) {
					holder.news_check.toggle();
                    SkyDriveActivity.this.SendNewsAdapter.setChecked(position,
							holder.news_check.isChecked());
				} else {
					String sourceUrl = holder.content_source_url;
					// 跳转到新闻详情
					Intent intent = new Intent();
					intent.setClass(SkyDriveActivity.this,
							BrowserActivity.class);
					intent.putExtra("tempUrl", sourceUrl);
                    SkyDriveActivity.this.startActivity(intent);
				}
			}
		});
        this.lvNews.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!SkyDriveActivity.this.isMulChoice) {
                    SkyDriveActivity.this.isMulChoice = true;

//					SendNewsAdapter = new SendNewsAdapter(isMulChoice, diskList,
//							SkyDriveActivity.this);
                    SkyDriveActivity.this.lvNews.setAdapter(SkyDriveActivity.this.SendNewsAdapter);
                    SkyDriveActivity.this.lvNews.setSelection(position);
				}
				return true;
			}
		});
        this.lvNews.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
                SkyDriveActivity.this.lvNews.onScrollStateChanged(view, scrollState);

				// 数据为空--不用继续下面代码了
				if (SkyDriveActivity.this.diskList.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(SkyDriveActivity.this.lvNews_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(SkyDriveActivity.this.lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    SkyDriveActivity.this.lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    SkyDriveActivity.this.lvNews_foot_more
							.setText(R.string.pull_to_refresh_refreshing_label);
                    SkyDriveActivity.this.lvNews_foot_progress.setVisibility(View.VISIBLE);
					// 当前pageIndex
					// int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    SkyDriveActivity.this.initUserData(2);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
                SkyDriveActivity.this.lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
        this.lvNews.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
                SkyDriveActivity.this.connect(0);
			}
		});
        this.connect(0);
	}

	public void initUserData(int count) {
		if (count == 0) {
//			SendNewsAdapter = new SendNewsAdapter(isMulChoice, diskList, this);
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
		if (this.diskList.size() <= 0)
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
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
                SkyDriveActivity.this.isOpenPop = false;
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
		SkyDriveActivity.GetAsyncTask task = new SkyDriveActivity.GetAsyncTask(count, this);
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
			if (this.count == 0) {
                SkyDriveActivity.this.valuse = new HashMap<String, String>();
                SkyDriveActivity.this.valuse.put("act", "getUserFileList");
                SkyDriveActivity.this.valuse.put("account", SkyDriveActivity.this.IniFile.getIniString(SkyDriveActivity.this.userIni, "Login",
						"Account", "", (byte) 0));
                SkyDriveActivity.this.valuse.put("type", constants.DISK_ROOT);
                SkyDriveActivity.this.valuse.put("sort", "1");
                SkyDriveActivity.this.valuse.put("order", "asc");
                SkyDriveActivity.this.parent = "";
			}
            SkyDriveActivity.this.startAnimation();
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			HttpConnectionUtil connection = new HttpConnectionUtil();
			if (this.count == 0) {
				String verifyURL = "http://"
						+ SkyDriveActivity.this.IniFile.getIniString(SkyDriveActivity.this.userIni, "Login",
								"ebsAddress", constants.DefaultServerIp,
								(byte) 0)
						+ ":"
						+ SkyDriveActivity.this.IniFile.getIniString(SkyDriveActivity.this.userIni, "Login",
								"ebsPort", "8083", (byte) 0)
						+ "/EBS/DIRServlet";
				String reuslt = connection.asyncConnect(verifyURL, null,
						HttpConnectionUtil.HttpMethod.GET, this.context);
				JSONObject jsonResult;
				try {
					jsonResult = new JSONObject(reuslt);
					String response = jsonResult.getString("msg");
					if ("列出文件成功".equals(response)) {
                        SkyDriveActivity.this.diskinfo(jsonResult, SkyDriveActivity.this.parent);
						return "true";
					} else {
						return response;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// ת��ΪJSONObject
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
                SkyDriveActivity.this.stopAnimation();
                SkyDriveActivity.this.diskList = new ArrayList<DiskInfoEntity>();
                SkyDriveActivity.this.initUserData(0);
				Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
			} else if (this.count == 0) {
				if (result.equalsIgnoreCase("true")) {
                    SkyDriveActivity.this.initUserData(0);
                    SkyDriveActivity.this.stopAnimation();
				} else {
					Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
				}

			} else if (this.count == 3) {
                SkyDriveActivity.this.stopAnimation();

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
		case R.id.menu_save:
            this.connect(3);
			break;
		}
	}

	public void diskinfo(JSONObject jsonResult, String parent) {
		// TODO Auto-generated method stub
        this.diskList = new ArrayList<DiskInfoEntity>();
		JSONArray array;
		try {
			array = jsonResult.getJSONArray("files");
			if (array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					DiskInfoEntity disk = new DiskInfoEntity();
					disk.setCreateTime(object.getString("createTime"));
					disk.setDirectory(object.getBoolean("directory"));
					disk.setLength(object.getString("length"));
					disk.setName(object.getString("name"));
					disk.setPath(object.getString("path"));
					disk.setReadonly(object.getBoolean("readonly"));
					disk.setParent(parent);
                    this.diskList.add(disk);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
