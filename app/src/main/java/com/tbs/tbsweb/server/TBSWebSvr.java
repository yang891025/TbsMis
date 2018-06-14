package com.tbs.tbsweb.server;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

public class TBSWebSvr extends Service {
	private static final String TAG = "TBSWebSever";
	private IniFile m_iniFileIO;
	private String webRoot;
	private static boolean moduleLoaded;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
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
		String WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
		int opRet = 0;
		String opStr = null;
		try {
            this.LoadCBScript(WebIniFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
            TBSWebSvr.moduleLoaded = false;
			e.printStackTrace();
		}
		opRet = this.init(WebIniFile, this.webRoot);
		if (opRet == 0) {
			opStr = "start";
			opRet = this.start();
		}
		if (opRet != 0) {
			// Toast.makeText(this, TAG + ": " + opStr + " faild!",
			// Toast.LENGTH_SHORT).show();
            this.stopSelf();
		}
	}

	@Override
	public void onDestroy() {
		Log.v(TBSWebSvr.TAG, "onDestroy");
        this.stop();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TBSWebSvr.TAG, "onStartCommand");
		int opRet = 0;
		String opStr = null;
		if (TBSWebSvr.moduleLoaded) {
			opStr = "restart";
			opRet = this.restart();
			if (opRet != 0) {
				// Toast.makeText(this, TAG + ": " + opStr + " faild!",
				// Toast.LENGTH_SHORT).show();
                this.stopSelf();
			}
		}
        TBSWebSvr.moduleLoaded = true;
		return Service.START_STICKY;
	}

	static {
		// TBS base modules
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("TBSLib");
		// Web Server module
		System.loadLibrary("TBSWebSvr");
	}

	private void LoadCBScript(String configFile) throws IOException {
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("TBSLib");
		System.loadLibrary("TBSBase");
		System.loadLibrary("TBSDBMan");
		System.loadLibrary("EBSClient24");
		System.loadLibrary("FTClient32");
		System.loadLibrary("NewsTools");
		System.loadLibrary("CBScript70");
		int i = 1;
		String strVal = this.m_iniFileIO.getIniString(configFile, "module", "module"
				+ i, "", (byte) 0);
		while (strVal != null && !strVal.isEmpty()) {
			try {
				System.loadLibrary(strVal);
			} catch (UnsatisfiedLinkError e) {
				Log.v(TBSWebSvr.TAG, e.getMessage());
			}

			i = i + 1;
			strVal = this.m_iniFileIO.getIniString(configFile, "module", "module"
					+ i, "", (byte) 0);
		}
	}

	// 初始化Web服务
	public native int init(String iniFile, String webRoot);

	// 启动后台Web服务
	public native int start();

	// 停止后台Web服务
	public native int stop();

	// 重启后台Web服务
	public native int restart();
}
