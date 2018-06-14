package com.tbs.tbsmis.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.apache.commons.httpclient.HttpException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressWarnings("serial")
public class AppException extends Exception implements Thread.UncaughtExceptionHandler {

	private static final boolean Debug = true;// 是否保存错误日志

	/** 定义异常类型 */
    public static final byte TYPE_NETWORK = 0x01;
	public static final byte TYPE_SOCKET = 0x02;
	public static final byte TYPE_HTTP_CODE = 0x03;
	public static final byte TYPE_HTTP_ERROR = 0x04;
	public static final byte TYPE_XML = 0x05;
	public static final byte TYPE_IO = 0x06;
	public static final byte TYPE_RUN = 0x07;

	private byte type;
	private int code;

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private AppException() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	private AppException(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;
		if (AppException.Debug) {
            saveErrorLog(excp);
		}
	}

	public int getCode() {
		return code;
	}

	public int getType() {
		return type;
	}

	/**
	 * 提示友好的错误信息
	 * 
	 * @param ctx
	 */
	public void makeToast(Context ctx) {
		switch (getType()) {
		case AppException.TYPE_HTTP_CODE:
			String err = ctx.getString(R.string.http_status_code_error,
                    getCode());
			Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
			break;
		case AppException.TYPE_HTTP_ERROR:
			Toast.makeText(ctx, R.string.http_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case AppException.TYPE_SOCKET:
			Toast.makeText(ctx, R.string.socket_exception_error,
					Toast.LENGTH_SHORT).show();
			break;
		case AppException.TYPE_NETWORK:
			Toast.makeText(ctx, R.string.network_not_connected,
					Toast.LENGTH_SHORT).show();
			break;
		case AppException.TYPE_XML:
			Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT)
					.show();
			break;
		case AppException.TYPE_IO:
			Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT)
					.show();
			break;
		case AppException.TYPE_RUN:
			Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	/**
	 * 保存异常日志
	 * 
	 * @param excp
	 */
	@SuppressWarnings("deprecation")
	public void saveErrorLog(Exception excp) {
		String errorlog = "error_log.txt";
		String savePath = "";
		String logFilePath = "";
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			// 判断是否挂载了SD卡
			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                Context context = MyActivity.getInstance().currentActivity();
				savePath = UIHelper.getStoragePath(context) +"/Log/";
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				logFilePath = savePath + errorlog;
			}
			// 没有挂载SD卡，无法写文件
			if (logFilePath == "") {
				return;
			}
			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			fw = new FileWriter(logFile, true);
			pw = new PrintWriter(fw);
			pw.println("--------------------" + new Date().toLocaleString()
					+ "---------------------");
			excp.printStackTrace(pw);
			pw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
				}
			}
		}

	}

	/*
	 * 保存用户日志
     */
	public static void saveUserLog(Context context,String doWhat) {
		String errorlog = StringUtils.getDate() + ".txt";
		String savePath = "";
		try {
			savePath = UIHelper.getStoragePath(context)+"/Log/";
			FileIO.CreateTxt(savePath, doWhat, errorlog);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	public static AppException http(int code) {
		return new AppException(AppException.TYPE_HTTP_CODE, code, null);
	}

	public static AppException http(Exception e) {
		return new AppException(AppException.TYPE_HTTP_ERROR, 0, e);
	}

	public static AppException socket(Exception e) {
		return new AppException(AppException.TYPE_SOCKET, 0, e);
	}

	public static AppException io(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(AppException.TYPE_NETWORK, 0, e);
		} else if (e instanceof IOException) {
			return new AppException(AppException.TYPE_IO, 0, e);
		}
		return AppException.run(e);
	}

	public static AppException xml(Exception e) {
		return new AppException(AppException.TYPE_XML, 0, e);
	}

	public static AppException network(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(AppException.TYPE_NETWORK, 0, e);
		} else if (e instanceof HttpException) {
			return AppException.http(e);
		} else if (e instanceof SocketException) {
			return AppException.socket(e);
		}
		return AppException.http(e);
	}

	public static AppException run(Exception e) {
		return new AppException(AppException.TYPE_RUN, 0, e);
	}

	/**
	 * 获取APP异常崩溃处理对象
	 * 
	 * @return
	 */
	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!this.handleException(ex) && this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(thread, ex);
		}

	}

	/**
	 * 自定义异常处理:收集错误信息&发送错误报告
	 * 
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		final Context context = MyActivity.getInstance().currentActivity();

		if (context == null) {
			return false;
		}
		final String crashReport = this.getCrashReport(context, ex);
		// 显示异常信息&发送报告
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				UIHelper.sendAppCrashReport(context, crashReport);
				Looper.loop();
			}

		}.start();
		return true;
	}

	/**
	 * 获取APP崩溃异常报告
	 * 
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageManager pinMgr = context.getApplicationContext()
				.getPackageManager();
		PackageInfo pinfo = null;
		try {
			pinfo = pinMgr.getPackageInfo(context.getApplicationContext()
					.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "("
				+ pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + VERSION.RELEASE
				+ "(" + Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i] + "\n");
		}
		ex.printStackTrace();
        AppException.saveUserLog(context,exceptionStr.toString());
		return exceptionStr.toString();
	}
}
