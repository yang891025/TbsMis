package com.tbs.chat.common;

import android.app.Application;
import android.content.res.Configuration;


public class BaseApplication extends Application {
	
	private static BaseApplication mInstance;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	public static BaseApplication getInstance() {
		return mInstance;
	}
}
