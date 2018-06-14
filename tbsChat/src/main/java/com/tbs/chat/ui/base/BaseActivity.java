package com.tbs.chat.ui.base;

import java.util.Locale;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.chat.R;
import com.tbs.chat.task.AsyncCallable;
import com.tbs.chat.task.Callback;
import com.tbs.chat.task.EMobileTask;
import com.tbs.chat.task.ProgressCallable;

public abstract class BaseActivity extends Activity {

	private static final String TAG = "BaseActivity";

	private InputMethodManager imm = null;
	private TelephonyManager tManager = null;
	private Dialog dialog = null;

	//onCreate方法
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//实例化appmanager,添加activity到管理这种
		tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	}

	//onDestroy方法
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	//onPause方法
	@Override
	protected void onPause() {
		super.onPause();
	}

	//onRestart方法
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	//onResume方法
	@Override
	protected void onResume() {
		super.onResume();
	}

	//onstart方法
	@Override
	protected void onStart() {
		super.onStart();
	}

	//stop方法
	@Override
	protected void onStop() {
		super.onStop();
	}

	//绑定控件id
	protected abstract void findViewById();

	//初始化控件
	protected abstract void initView();

	//通过类名启动Activity
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	//通过类名启动Activity，并且含有Bundle数据
	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}
	
	//通过Action启动Activity，并且含有Bundle数据,并且有返回值
	protected void openActivity(Class<?> pClass, Bundle pBundle, int requestCode) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivityForResult(intent, requestCode);
	}

	//通过Action启动Activity
	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	//通过Action启动Activity，并且含有Bundle数据
	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}
	
	//加载进度条
	protected void showProgressDialog(String title) {
		if(dialog != null && dialog.isShowing()){
			dialog.cancel();
			return;
		}
		View view = getLayoutInflater().inflate(R.layout.loading, null);
		TextView headTV = (TextView) view.findViewById(R.id.textview);
		if(title != null && !title.equals("")){
			headTV.setText(title);
		}else{
			headTV.setText("请稍后...");
		}
		dialog = new Dialog(this, R.style.MyDialogStyle);
	    dialog.setContentView(view);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	//获得dialog对话框
	protected boolean getDialogState(){
		if(dialog != null){
			return dialog.isShowing();
		}else{
			return false;
		}
	}
	
	// 显示toast通知
	protected void DisplayToastLong(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	//弹出toast
	protected void DisplayToastShort(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	//隐藏输入法
	protected void hideOrShowSoftInput(boolean isShowSoft,EditText editText) {
		if (isShowSoft) {
			imm.showSoftInput(editText, 0);
		}else {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}
	
	//获得当前程序版本信息
	protected String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		return packInfo.versionName;
	}

	
	//獲得設備信息
	protected String getDeviceId() throws Exception {
		String deviceId=tManager.getDeviceId();
		return deviceId;
		
	}
	
	//获取SIM卡序列号
	protected String getToken() {
		return tManager.getSimSerialNumber();
	}

	/*獲得系統版本*/
	protected String getClientOs() {
		return android.os.Build.ID;
	}
	
	/*獲得系統版本號*/
	protected String getClientOsVer() {
		return android.os.Build.VERSION.RELEASE;
	}
	
	//獲得系統語言包
	protected String getLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	
	protected String getCountry() {
		return Locale.getDefault().getCountry();
	}
	
	
	/**
	 * @param <T> 模板参数，操作时要返回的内容
	 * @param pCallable 需要异步调用的操作
	 * @param pCallback 回调
	 */ 
	protected <T> void doAsync(final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback,final boolean showDialog, String message) {
		EMobileTask.doAsync(this, null, message, pCallable, pCallback,pExceptionCallback, false, showDialog);
	}

	
	protected <T> void doAsync(final CharSequence pTitle,final CharSequence pMessage, final Callable<T> pCallable, final Callback<T> pCallback, final boolean showDialog) {
		EMobileTask.doAsync(this, pTitle, pMessage, pCallable, pCallback, null,false, showDialog);
	}

	/**
	 * Performs a task in the background, showing a {@link ProgressDialog},
	 * while the {@link Callable} is being processed.
	 * 
	 * @param <T>
	 * @param pTitleResID
	 * @param pMessageResID
	 * @param pErrorMessageResID
	 * @param pCallable
	 * @param pCallback
	 */
	protected <T> void doAsync(final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback) {
		this.doAsync(pTitleResID, pMessageResID, pCallable, pCallback, null);
	}

	/**
	 * Performs a task in the background, showing a indeterminate
	 * {@link ProgressDialog}, while the {@link Callable} is being processed.
	 * 
	 * @param <T>
	 * @param pTitleResID
	 * @param pMessageResID
	 * @param pErrorMessageResID
	 * @param pCallable
	 * @param pCallback
	 * @param pExceptionCallback
	 */
	protected <T> void doAsync(final int pTitleResID, final int pMessageResID, final Callable<T> pCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
		EMobileTask.doAsync(this, pTitleResID, pMessageResID, pCallable, pCallback, pExceptionCallback);
	}

	/**
	 * Performs a task in the background, showing a {@link ProgressDialog} with
	 * an ProgressBar, while the {@link AsyncCallable} is being processed.
	 * 
	 * @param <T>
	 * @param pTitleResID
	 * @param pMessageResID
	 * @param pErrorMessageResID
	 * @param pAsyncCallable
	 * @param pCallback
	 */
	protected <T> void doProgressAsync(final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback) {
		this.doProgressAsync(pTitleResID, pCallable, pCallback, null);
	}

	/**
	 * Performs a task in the background, showing a {@link ProgressDialog} with
	 * a ProgressBar, while the {@link AsyncCallable} is being processed.
	 * 
	 * @param <T>
	 * @param pTitleResID
	 * @param pMessageResID
	 * @param pErrorMessageResID
	 * @param pAsyncCallable
	 * @param pCallback
	 * @param pExceptionCallback
	 */
	protected <T> void doProgressAsync(final int pTitleResID, final ProgressCallable<T> pCallable, final Callback<T> pCallback,	final Callback<Exception> pExceptionCallback) {
		EMobileTask.doProgressAsync(this, pTitleResID, pCallable, pCallback, pExceptionCallback);
	}

	/**
	 * Performs a task in the background, showing an indeterminate
	 * {@link ProgressDialog}, while the {@link AsyncCallable} is being
	 * processed.
	 * 
	 * @param <T>
	 * @param pTitleResID
	 * @param pMessageResID
	 * @param pErrorMessageResID
	 * @param pAsyncCallable
	 * @param pCallback
	 * @param pExceptionCallback
	 */
	protected <T> void doAsync(final int pTitleResID, final int pMessageResID, final AsyncCallable<T> pAsyncCallable, final Callback<T> pCallback, final Callback<Exception> pExceptionCallback) {
		EMobileTask.doAsync(this, pTitleResID, pMessageResID, pAsyncCallable, pCallback, pExceptionCallback);
	}
	
}
