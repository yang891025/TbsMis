package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({ "HandlerLeak", "NewApi" })
public class LanuchActivity1 extends Activity {
	private static final String TAG = "LanuchActivity";
	public static final int ENABLE_SDCARD_REQUEST = 0;
	public static final int THREAD_ERROR_NONE = 0;
	public static final int THREAD_ERROR_SDCARD = -1;
	public static final int THREAD_ERROR_TBSWEB = 1;
	private String launchState;// 是否为第一次启动标记
	private boolean ShowSplash = true;
	private Handler handler;
	private String versionname;
	private String webRoot;
	private WebView webview;
	private IniFile IniFile;
	private String appTestFile;
	private String path;
	private String name;
	private String appIniFile;
	private ProgressDialog Prodialog;

	// private void startInitThread() {
	// Thread initThread = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// }
	// });
	// initThread.start();
	// }

	@SuppressLint({ "SetJavaScriptEnabled", "SdCardPath" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.lanucher_activity);
        this.webview = (WebView) this.findViewById(R.id.webView1);
		WebSettings webSettings = this.webview.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 设置 内容适应屏幕
		webSettings.setLoadWithOverviewMode(true);
		// flash支持
		//webview.getSettings().setPluginsEnabled(true);
        this.webview.getSettings().setPluginState(WebSettings.PluginState.ON);
		// 设置编码格式
		webSettings.setDefaultTextEncodingName("gb2312");
		// 取消滚动条
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		// 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
		String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        this.webview.requestFocus();
		String configPath = this.getApplicationContext().getFilesDir()
				.getParentFile().getAbsolutePath();
		if (configPath.endsWith("/") == false) {
			configPath = configPath + "/";
		}
        this.appTestFile = configPath + constants.APP_CONFIG_FILE_NAME;
        this.webRoot = UIHelper.getStoragePath(this);
        this.IniFile = new IniFile();
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
			int flag = intent.getIntExtra("flag", 0);
			if (flag == 1) {
				String iniPath = intent.getStringExtra("iniPath");
				String Title = intent.getStringExtra("tempUrl");
				String ResName = intent.getStringExtra("ResName");
				if (!StringUtils.isEmpty(Title)) {
                    this.IniFile.writeIniString(iniPath, "TBSAPP", "AppName", Title);
				}
				if (!StringUtils.isEmpty(ResName)) {
                    this.IniFile.writeIniString(iniPath, "TBSAPP", "resname",
							ResName);
				}
				if (iniPath.endsWith("/") == false) {
                    this.path = FileUtils.getPath(iniPath);
					if (this.path.endsWith("/") == false) {
                        this.path += "/";
					}
                    this.name = FileUtils.getPathName(iniPath);
                    this.IniFile.writeIniString(this.path
							+ constants.WEB_CONFIG_FILE_NAME, "TBSWeb",
							"IniName", this.name);
				}
				UIHelper.setSharePerference(this, constants.SAVE_INFORMATION,
						"Path", iniPath);

			}
		}
        this.path = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", this.webRoot+constants.SD_CARD_TBSSOFT_PATH3+"/"+ getString(R.string.SD_CARD_TBSAPP_PATH2));
		if (this.path.endsWith("/") == false) {
            this.path += "/";
		}
		String WebIniFile = this.path + constants.WEB_CONFIG_FILE_NAME;
        this.appIniFile = this.path
				+ this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
		/*File IniFiel = new File(appIniFile);
		if(!IniFiel.exists()){
			String appName = path.substring(path.length()-1);
			appName = appName.substring(appName.lastIndexOf("/"
			));
			appIniFile = path +appName+".ini";
		}*/
		UIHelper.setSharePerference(this, constants.SAVE_INFORMATION, "Path",
                this.path);
		File file = new File(this.path
				+ this.IniFile.getIniString(this.appIniFile, "APPABOUT", "WelcomePath",
						"", (byte) 0));
		if (file.exists() && file.isFile()) {
            this.webview.loadUrl("file://"
					+ this.path
					+ this.IniFile.getIniString(this.appIniFile, "APPABOUT",
							"WelcomePath", "", (byte) 0));
		} else {
            this.webview.loadUrl("file:///android_asset/welcome.html");
		}
		try {
            this.versionname = this.getVersionName();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        this.launchState = UIHelper.getShareperference(this,
				constants.SHARED_PREFERENCE_FIRST_LAUNCH, "launchState",
				"1.0.0");
        this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == LanuchActivity1.THREAD_ERROR_SDCARD) {
					Intent intent = new Intent(LanuchActivity1.this,
							SDUnavailableActivity.class);
                    LanuchActivity1.this.startActivityForResult(intent, LanuchActivity1.ENABLE_SDCARD_REQUEST);
				} else if (msg.what == LanuchActivity1.THREAD_ERROR_NONE) {
					if (LanuchActivity1.this.launchState.equals(LanuchActivity1.this.versionname)) {// 等于true跳转到软件主界面
						Intent mainIntent = new Intent(LanuchActivity1.this,
								InitializeToolbarActivity.class);
						if (LanuchActivity1.this.getIntent().getExtras() != null) {
							Intent intent = LanuchActivity1.this.getIntent();
							int flag = intent.getIntExtra("flag", 0);
							if (flag == 2) {
								String notificationId = intent
										.getStringExtra(Constants.NOTIFICATION_ID);
								String notificationTitle = intent
										.getStringExtra(Constants.NOTIFICATION_TITLE);
								String notificationUri = intent
										.getStringExtra(Constants.NOTIFICATION_URI);
								mainIntent.putExtra(Constants.NOTIFICATION_ID,
										notificationId);
								mainIntent.putExtra(
										Constants.NOTIFICATION_TITLE,
										notificationTitle);
								mainIntent.putExtra(Constants.NOTIFICATION_URI,
										notificationUri);
							} else if (flag == 3) {
								mainIntent.putExtra("flag", 1);
							} else if (flag == 4) {
								mainIntent.putExtra("flag", 2);
							}
						}
                        startActivity(mainIntent);
                        finish();
					} else {// 等于false跳转到导航导航界面
						Intent mainIntent = new Intent(LanuchActivity1.this,
								LauncherActivity.class);
                        startActivity(mainIntent);
                        finish();
					}
				}
			}
		};
        this.showModifyDialog();
		LanuchActivity1.MyAsyncTask task = new LanuchActivity1.MyAsyncTask(this, this.webRoot);// 启动异步加载功能
		task.execute();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	public void Startapp() {
		boolean isStarted = StartTbsweb.isMyServiceRunning(this, null);
		if (!isStarted)
			StartTbsweb.Startapp(this, 1);
	}

	private boolean doDeploy(String assetTbk, String webRootTbk, String packName) {
		String webRoot = this.webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/";
		File webRootFile = new File(webRoot + packName);
		if ((webRootFile.exists() && webRootFile.isDirectory()) == false) {
			webRootFile.mkdirs();
		}
		try {
			InputStream is = this.getBaseContext().getAssets().open(assetTbk);
			OutputStream os = new FileOutputStream(webRootTbk);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int resoult = JTbszlib.UnZipFile(webRootTbk, webRoot + packName, 1, "");
		if (0 != resoult) {
			return false;
		}
        this.delZipFile(webRootTbk);
		return true;
	}

	private boolean iniDeploy(String FileName) {
		// 从assets目录解压配置文件"TBSWeb.ini"
		String dataPath = this.getApplicationContext().getFilesDir().getParentFile()
				.getAbsolutePath();
		if (dataPath.endsWith("/") == false) {
			dataPath = dataPath + "/";
		}
		File configFile = new File(dataPath + FileName);
		if (configFile.exists() == false) {
			File f = new File(dataPath);
			if (!f.exists()) {
				f.mkdirs();
			}
			try {
				InputStream is = this.getBaseContext().getAssets().open(FileName);
				OutputStream os = new FileOutputStream(dataPath + FileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	private boolean iniCDeploy(String FileName) {
		// 从assets目录解压配置文件"TBSWeb.ini"
		String dataPath = this.getApplicationContext().getFilesDir().getParentFile()
				.getAbsolutePath();
		if (dataPath.endsWith("/") == false) {
			dataPath = dataPath + "/";
		}
		File configFile = new File(dataPath + FileName);
		if (configFile.exists() == false || !this.launchState.equals(this.versionname)) {
			File f = new File(dataPath);
			if (!f.exists()) {
				f.mkdirs();
			}
			try {
				InputStream is = this.getBaseContext().getAssets().open(FileName);
				OutputStream os = new FileOutputStream(dataPath + FileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				os.flush();
				os.close();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LanuchActivity1.ENABLE_SDCARD_REQUEST) {
			// If the request was cancelled, then we are cancelled as well.
			if (resultCode == Activity.RESULT_CANCELED) {
                this.finish();
			} else {
                this.ShowSplash = false;
                this.showModifyDialog();
				LanuchActivity1.MyAsyncTask task = new LanuchActivity1.MyAsyncTask(this, this.webRoot);// 启动异步加载功能
				task.execute();

			}
		}
	}

	// @SuppressLint("SimpleDateFormat")
	// void prepareData() {
	//
	// boolean installed = false;
	// int Unzip = 0;
	// File webROOT = null;
	// // 判断SD卡是否可用
	// if (Environment.getExternalStorageState().equals(
	// Environment.MEDIA_MOUNTED)) {
	// webROOT = new File(webRoot);
	// installed = webROOT.exists() && webROOT.isDirectory();
	// Unzip = Integer.parseInt(IniFile.getIniString(appTestFile,
	// "INSTALL", "WebUnzip", "0", (byte) 0));
	// if (installed == false || Unzip == 1 || launchState.equals("1.0.0")) {
	// File webRootFile = new File(webRoot);
	// webRootFile.mkdirs();
	// IniFile.writeIniString(appTestFile, "INSTALL", "WebUnzip", "0");
	// if (false == doDeploy(webRootWeb)) {
	// return;
	// }
	// }
	// }
	//
	// }

	protected void delZipFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	// 获取程序的版本号
	private String getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = this.getPackageManager();
		// getPackageName()���㵱ǰ��İ���0����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(),
				0);
		return packInfo.versionName;
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Message> {
		private final Context context;
		private final String Path;
		private final String webRoot;

		public MyAsyncTask(Context c, String webRoot) {
            context = c;
            this.Path = "data";
			this.webRoot = webRoot;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected Message doInBackground(Void... params) {
			long timeStart = System.currentTimeMillis();
			long timeSleep = 0;
			Message msg = new Message();
			msg.obj = 0;
			// check the status of SDCard
			if (false == Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				timeSleep = constants.ShowSplashMillisecond
						- (System.currentTimeMillis() - timeStart);
				if (LanuchActivity1.this.ShowSplash && timeSleep > 0) {
					try {
						Thread.sleep(timeSleep / 2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				timeSleep = (constants.ShowSplashMillisecond - (System
						.currentTimeMillis() - timeStart)) / 2;
				msg.what = LanuchActivity1.THREAD_ERROR_SDCARD;
			} else {
				timeSleep = constants.ShowSplashMillisecond
						- (System.currentTimeMillis() - timeStart);
				msg.what = LanuchActivity1.THREAD_ERROR_NONE;
                LanuchActivity1.this.iniDeploy(constants.USER_CONFIG_FILE_NAME);
                LanuchActivity1.this.iniCDeploy(constants.APP_CONFIG_FILE_NAME);
				List<String> list = new ArrayList<String>();
				list = LanuchActivity1.this.getfileFromAssets(this.Path);
				for (int i = 0; i < list.size(); i++) {
					if (LanuchActivity1.this.launchState.equals("1.0.0")) {
                        this.publishProgress(i, list.size());
					}
					String AppPath = this.webRoot + constants.SD_CARD_TBSSOFT_PATH3
							+ "/";
					String packName = FileUtils.getFileNameNoFormat(AppPath
							+ list.get(i));
					File webROOT = new File(AppPath + packName);
					if (!(webROOT.exists() && webROOT.isDirectory())) {
                        LanuchActivity1.this.doDeploy(this.Path + "/" + list.get(i),
								AppPath + list.get(i), packName);
					}
				}
                LanuchActivity1.this.Startapp();
			}
			if (LanuchActivity1.this.ShowSplash && timeSleep > 0) {
				try {
					Thread.sleep(timeSleep / 2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return msg;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Message integer) {
            LanuchActivity1.this.handler.sendMessage(integer);
			if (LanuchActivity1.this.Prodialog.isShowing()) {
                LanuchActivity1.this.Prodialog.dismiss();
			}
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
            LanuchActivity1.this.Prodialog.show();
            LanuchActivity1.this.Prodialog.setMessage(" 正在安装，请稍候...(" + (values[0] + 1) + "/"
					+ values[1] + ")");
		}
	}

	public List<String> getfileFromAssets(String path) {
		AssetManager assetManager = getAssets();
		String[] files = null;
		List<String> list = new ArrayList<String>();
		try {
			files = assetManager.list(path);
			for (int i = 0; i < files.length; i++) {
				list.add(files[i]);
				//System.out.println(files[i]);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	private void showModifyDialog() {
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("安装数据");
        this.Prodialog.setMessage("正在安装，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
	}
	// public List listHtmlOfAssets() {
	// List list = new ArrayList();
	// files = getfileFromAssets("html");
	// for (int i = 0; i < files.length; i++) {
	// HashMap map = new HashMap();
	// map.put("htmlname", files[i]);
	// list.add(map);
	// }
	// return list;
	// }
}
