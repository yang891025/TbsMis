package com.tbs.tbsmis.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

import java.util.Timer;
import java.util.TimerTask;

public class WebService extends Service {

	private IniFile m_iniFileIO;
	private String userIni;
	private WebService.webTimerTask webTimerTask;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private static final String TAG = "WebService";

	@Override
	public void onCreate() {
		Log.i(WebService.TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(WebService.TAG, "onDestroy");
		super.onDestroy();
		if (this.webTimerTask != null) {
            this.webTimerTask.cancel();
		}
        this.stopSelf();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(WebService.TAG, "onStartCommand~~~~~~~~~~~~");
        this.iniPath();
		Timer webTimer = new Timer();
		if (webTimer != null) {
			if (this.webTimerTask != null) {
                this.webTimerTask.cancel();
			}
		}
        this.webTimerTask = new WebService.webTimerTask();
		webTimer.schedule(this.webTimerTask, 29 * 60000, 29 * 60000);
		return Service.START_STICKY;
	}

	protected void iniPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
		String configPath = this.getApplicationContext().getFilesDir()
				.getParentFile().getAbsolutePath();
		if (configPath.endsWith("/") == false) {
			configPath = configPath + "/";
		}
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
        String appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
	}

	class webTimerTask extends TimerTask {
		@Override
		public void run() {
            System.out.println("WebService-TimerTask");
            WebService.this.iniPath();
			Log.i(WebService.TAG, "webrestart");
			if (Integer.parseInt(WebService.this.m_iniFileIO.getIniString(WebService.this.userIni,
					"SERVICE", "serverMarks", "4", (byte) 0)) == 0
					&& Integer.parseInt(WebService.this.m_iniFileIO.getIniString(WebService.this.userIni,
							"SERVICE", "webRestart", "0", (byte) 0)) == 1) {
				StartTbsweb.Startapp(WebService.this, 1);
			}
		}
	}
}