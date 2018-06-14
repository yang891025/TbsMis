package com.tbs.tbsmis.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class StartTbsweb {
	public StartTbsweb() {

	}

	public static void Startapp(Context context, int what) {
        //System.out.println("what = "+what);
		String webRoot = UIHelper.getSoftPath(context);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(context,
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		IniFile IniFile = new IniFile();
		String appNewsFile = webRoot
				+ IniFile.getIniString(
						webRoot + constants.WEB_CONFIG_FILE_NAME, "TBSWeb",
						"IniName", constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appNewsFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
		if (what == 0) {
            if (StartTbsweb.isMyServiceRunning(context,
                    context.getString(R.string.TbsWebServer))) {
                Intent intent = new Intent();
                intent.setAction(context.getString(R.string.TbsStarWebServer));//你定义的service的action
                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                context.stopService(intent);
            }
		} else {
			 if(Integer.parseInt(IniFile.getIniString(userIni,
                    "SERVICE", "serverMarks", "4", (byte) 0)) == 0) {
				if (StartTbsweb.isMyServiceRunning(context,
						context.getString(R.string.TbsWebServer))) {
                    Intent stopIntent = new Intent();
                    stopIntent.setAction(context
                            .getString(R.string.TbsStarWebServer));//你定义的service的action
                    stopIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                    context.stopService(stopIntent);
                    Intent mIntent = new Intent();
                    mIntent.setAction(context
                            .getString(R.string.TbsStarWebServer));//你定义的service的action
                    mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                    context.startService(mIntent);
				} else {
                    Intent mIntent = new Intent();
                    mIntent.setAction(context
                            .getString(R.string.TbsStarWebServer));//你定义的service的action
                    mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                    context.startService(mIntent);
				}
			}else {
                 if (StartTbsweb.isMyServiceRunning(context,
                         context.getString(R.string.TbsStarWebServer))) {
                     Intent stopIntent = new Intent();
                     stopIntent.setAction(context
                             .getString(R.string.TbsStarWebServer));//你定义的service的action
                     stopIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                     context.stopService(stopIntent);
                 }
             }
		}
	}

	public static boolean isMyServiceRunning(Context context, String SvrName) {
		if (StringUtils.isEmpty(SvrName)) {
			String webRoot = UIHelper.getSoftPath(context);
			if (webRoot.endsWith("/") == false) {
				webRoot += "/";
			}
			webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
			webRoot = UIHelper.getShareperference(context,
					constants.SAVE_INFORMATION, "Path", webRoot);
			if (webRoot.endsWith("/") == false) {
				webRoot += "/";
			}
			IniFile IniFile = new IniFile();
			String appNewsFile = webRoot
					+ IniFile.getIniString(webRoot
							+ constants.WEB_CONFIG_FILE_NAME, "TBSWeb",
							"IniName", constants.NEWS_CONFIG_FILE_NAME,
							(byte) 0);
            String userIni = appNewsFile;
            if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1) {
                String dataPath = context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
			if (Integer.parseInt(IniFile.getIniString(userIni, "SERVICE",
					"serverMarks", "4", (byte) 0)) == 3) {
				SvrName = context.getString(R.string.TbsHttpServer);
			} else if(Integer.parseInt(IniFile.getIniString(userIni, "SERVICE",
                    "serverMarks", "4", (byte) 0)) == 0){
				SvrName = context.getString(R.string.TbsWebServer);
			}
		}
		if(StringUtils.isEmpty(SvrName))
		    return true;
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (SvrName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}