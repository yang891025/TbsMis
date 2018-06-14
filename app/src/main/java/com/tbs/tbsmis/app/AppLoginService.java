package com.tbs.tbsmis.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.UIHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class AppLoginService extends Service {

	private IniFile m_iniFileIO;
	private String appNewsFile;
	private AppLoginService.userTimerTask userTimerTask;
    private String userIni;

    @Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private static final String TAG = "AppUserService";

	@Override
	public void onCreate() {
		Log.i(AppLoginService.TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(AppLoginService.TAG, "onDestroy");
		super.onDestroy();
		if (this.userTimerTask != null) {
            this.userTimerTask.cancel();
		}
		AppLoginService.MyAsyncTask task = new AppLoginService.MyAsyncTask(this, 1);
		task.execute();
		// stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(AppLoginService.TAG, "onStartCommand~~~~~~~~~~~~");
        this.iniPath();
		Timer userTimer = new Timer();
		if (userTimer != null) {
			if (this.userTimerTask != null) {
                this.userTimerTask.cancel();
			}
		}
		int userTime = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
				"Login", "userTime", "3", (byte) 0));
        this.userTimerTask = new AppLoginService.userTimerTask();
		userTimer.schedule(this.userTimerTask, userTime * 60000, userTime * 60000);
		return Service.START_STICKY;
	}

	protected void iniPath() {
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
        this.appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appNewsFile;
        if(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
	}

	public Map<String, String> update() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "update");
		params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
				"LoginId", "", (byte) 0));
		return params;
	}

	public Map<String, String> LoginQuit() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("act", "logOut");
		params.put("clientId", UIHelper.DeviceMD5ID(this));
		params.put("loginId", this.m_iniFileIO.getIniString(this.userIni, "Login",
				"LoginId", "", (byte) 0));
		return params;
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
		private final Context context;
		private final int action;

		public MyAsyncTask(Context c, int action) {
            context = c;
			this.action = action;
		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			Log.i(AppLoginService.TAG, "update");
			HttpConnectionUtil connection = new HttpConnectionUtil();
			constants.verifyURL = "http://"
					+ AppLoginService.this.m_iniFileIO.getIniString(AppLoginService.this.userIni, "Login",
							"ebsAddress", constants.DefaultServerIp, (byte) 0)
					+ ":"
					+ AppLoginService.this.m_iniFileIO.getIniString(AppLoginService.this.userIni, "Login",
							"ebsPort", "8083", (byte) 0)
					+ AppLoginService.this.m_iniFileIO.getIniString(AppLoginService.this.userIni, "Login",
							"ebsPath", "/EBS/UserServlet", (byte) 0);
			switch (this.action) {
			case 1:
				return connection.asyncConnect(constants.verifyURL,
                        AppLoginService.this.LoginQuit(), HttpConnectionUtil.HttpMethod.POST, this.context);
			default:
				return connection.asyncConnect(constants.verifyURL, AppLoginService.this.update(),
						HttpConnectionUtil.HttpMethod.POST, this.context);
			}
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				if (result.equals("true")) {
                    AppLoginService.this.m_iniFileIO.writeIniString(AppLoginService.this.userIni, "Login",
							"LoginFlag", "1");
				} else {
                    AppLoginService.this.m_iniFileIO.writeIniString(AppLoginService.this.userIni, "Login",
							"LoginFlag", "0");
                    AppLoginService.this.m_iniFileIO.writeIniString(AppLoginService.this.userIni, "Login", "LoginId",
							"");
                    userTimerTask.cancel();
				}
			} else {
                AppLoginService.this.m_iniFileIO.writeIniString(AppLoginService.this.userIni, "Login", "LoginFlag",
						"0");
			}

		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {

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

	class userTimerTask extends TimerTask {
		@Override
		public void run() {
		    System.out.println("AppLoginService-TimerTask");
            AppLoginService.this.iniPath();
			if (Integer.parseInt(AppLoginService.this.m_iniFileIO.getIniString(AppLoginService.this.userIni, "Login",
					"LoginFlag", "0", (byte) 0)) == 1) {
				AppLoginService.MyAsyncTask task = new AppLoginService.MyAsyncTask(AppLoginService.this, 0);
				task.execute();
			}
		}
	}
}