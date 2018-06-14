package com.tbs.tbsmis.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MyActivity
{

	private static Stack<Activity> activityStack;
	private static MyActivity instance;

	private MyActivity() {
	}

	/**
	 * 单一实例
	 */
	public static MyActivity getInstance() {
		if (MyActivity.instance == null) {
            MyActivity.instance = new MyActivity();
		}
		return MyActivity.instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (MyActivity.activityStack == null) {
            MyActivity.activityStack = new Stack<Activity>();
		}
        MyActivity.activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = MyActivity.activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = MyActivity.activityStack.lastElement();
        this.finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
            MyActivity.activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : MyActivity.activityStack) {
			if (activity.getClass().equals(cls)) {
                this.finishActivity(activity);
			}
		}
	}

	/**
	 * 判断Activity是否在栈里
	 * 
	 * @return
	 */
	public boolean isActivity(Activity activity) {
		for (Activity midActivity : MyActivity.activityStack) {
			if (midActivity == activity) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = MyActivity.activityStack.size(); i < size; i++) {
			if (null != MyActivity.activityStack.get(i)) {
                MyActivity.activityStack.get(i).finish();
			}
		}
        MyActivity.activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	@SuppressWarnings("deprecation")
	public void AppExit(Context context) {
		try {
            this.finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}
}