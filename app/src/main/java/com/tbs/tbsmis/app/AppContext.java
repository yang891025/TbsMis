package com.tbs.tbsmis.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.support.multidex.MultiDex;

import com.lzy.okgo.OkGo;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;

import me.wcy.music.application.AppCache;
import me.wcy.music.application.ForegroundObserver;
import me.wcy.music.service.PlayService;
import me.wcy.music.storage.db.DBManager;

//import android.support.multidex.MultiDex;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("deprecation")
public class AppContext extends Application {
	//
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
    public static AppContext instance;

	// private String Resname = null; //登录用户的id
    public static AppContext getInstance(){
        return AppContext.instance;
    }
	@Override
	public void onCreate() {
		super.onCreate();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException
				.getAppExceptionHandler());
        //MultiDex.install(this);
        OkGo.getInstance().init(this);
        this.init();
		// initData();
	}

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
	/**
	 * 初始化
	 */
	private void init() {
        AppContext.instance = this;
    }
    /**
     * 初始化
     */
    public void initMusic() {
        AppCache.get().init(this);
        AppCache.get().init(this);
        ForegroundObserver.init(this);
        DBManager.get().init(this);
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	@SuppressLint("DefaultLocale")
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = AppContext.NETTYPE_CMNET;
				} else {
					netType = AppContext.NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = AppContext.NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
        String webRoot = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (webRoot.endsWith("/") == false)
        {
            webRoot += "/";
        }
        String picDir = webRoot+ "lazyList";
		// 清除webview缓存
		webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String filePath = webRoot + "tmp";

		File file = new File(filePath);
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			file.delete();
		}
        file = new File(picDir);
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        this.deleteDatabase("webview.db");
        this.deleteDatabase("webview.db-shm");
        this.deleteDatabase("webview.db-wal");
        this.deleteDatabase("webviewCache.db");
        this.deleteDatabase("webviewCache.db-shm");
        this.deleteDatabase("webviewCache.db-wal");
		// 清除数据缓存
        this.clearCacheFolder(this.getFilesDir(), System.currentTimeMillis());
        this.clearCacheFolder(this.getCacheDir(), System.currentTimeMillis());
		//clearCacheFolder(fileTemp, System.currentTimeMillis());
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (AppContext.isMethodsCompat(VERSION_CODES.FROYO)) {
            this.clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
        int tmpCount = UIHelper.getShareperference(this, "tmp", "tmpCount", 0);
        for (int i = 0; i < tmpCount; i++) {
            FileUtils.deleteFile(UIHelper.getShareperference(this, "tmp", "tmpFile" + i,
                    ""));
        }
        UIHelper.setSharePerference(this, "tmp", "tmpCount", 0);
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param curTime
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += this.clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

}
