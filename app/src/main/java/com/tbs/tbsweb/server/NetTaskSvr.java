package com.tbs.tbsweb.server;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

public class NetTaskSvr extends Service {
	private static final String TAG = "NetTaskSvr";
	private static boolean moduleLoaded;
	private IniFile m_iniFileIO;
	private String webRoot;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.v(NetTaskSvr.TAG, "onCreate");
        this.m_iniFileIO = new IniFile();
		String configPath = this.getApplicationContext().getFilesDir()
				.getParentFile().getAbsolutePath();
		if (configPath.endsWith("/") == false) {
			configPath = configPath + "/";
		}
        this.webRoot = UIHelper.getSoftPath(this);
		if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
		}
        this.webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        this.webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", this.webRoot);
		if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
		}
		String WebIniFile = this.webRoot + constants.TASK_CONFIG_FILE_NAME;
		int opRet = 0;
		String opStr = null;
		opRet = this.start(WebIniFile, this.webRoot);
		if (opRet != 1) {
			Toast.makeText(this, NetTaskSvr.TAG + ": " + opStr + " faild!",
					Toast.LENGTH_SHORT).show();
            this.stopSelf();
		}
	}

	@Override
	public void onDestroy() {
		Log.v(NetTaskSvr.TAG, "onDestroy");
        this.stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(NetTaskSvr.TAG, "onStartCommand");
		int opRet = 0;
		String opStr = null;
		if (NetTaskSvr.moduleLoaded) {
			opStr = "restart";
			opRet = this.restart();
			if (opRet != 1) {
				Toast.makeText(this, NetTaskSvr.TAG + ": " + opStr + " faild!",
						Toast.LENGTH_SHORT).show();
                this.stopSelf();
			}
		}
        NetTaskSvr.moduleLoaded = true;
		return Service.START_STICKY;
	}

	public native int start(String configFile, String basePath);

	public native int stop();

	public native int restart();

	static {
		try {
			// TBS base modules
			System.loadLibrary("gnustl_shared");
			System.loadLibrary("TBSLib");
			System.loadLibrary("CBSInterpret");
			System.loadLibrary("nettask");
		} catch (UnsatisfiedLinkError e) {
			// Log.v(TAG, "Modules doesn't be loaded!");
            NetTaskSvr.moduleLoaded = false;
			e.printStackTrace();
		}
	}
}
