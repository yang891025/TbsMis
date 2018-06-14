/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tbs.tbsmis.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * This class is to manage the notificatin service and to load the
 * configuration.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class ServiceManager {

	private static final String LOGTAG = LogUtil
			.makeLogTag(ServiceManager.class);

	private final Context context;

	private final SharedPreferences sharedPrefs;

	// private Properties props;

	private final String version = "0.5.0";

	private final String apiKey;

	private final String xmppHost;

	private final String xmppPort;

	private String callbackActivityPackageName;

	private String callbackActivityClassName;

	private String newusername;

	private String newpassword;

	private final IniFile m_iniFileIO;
    private String userIni;

    public ServiceManager(Context context) {
		this.context = context;

		if (context instanceof Activity) {
			Log.i(ServiceManager.LOGTAG, "Callback Activity...");
			Activity callbackActivity = (Activity) context;
            this.callbackActivityPackageName = callbackActivity.getPackageName();
            this.callbackActivityClassName = callbackActivity.getClass().getName();
		}
        this.m_iniFileIO = new IniFile();
        this.iniPath();
        this.xmppHost = this.m_iniFileIO.getIniString(userIni, "Push",
				"pushAddress", "e.tbs.com.cn", (byte) 0);
        this.xmppPort = this.m_iniFileIO.getIniString(userIni, "Push", "pushPort",
				"5222", (byte) 0);
        this.apiKey = Constants.TBSAPP_DEVICE_APIKEY;

		// props = loadProperties();
		// apiKey = props.getProperty("apiKey", "");
		// xmppHost = props.getProperty("xmppHost", "127.0.0.1");
		// xmppPort = props.getProperty("xmppPort", "5222");

		Log.i(ServiceManager.LOGTAG, "apiKey=" + this.apiKey);
		Log.i(ServiceManager.LOGTAG, "xmppHost=" + this.xmppHost);
		Log.i(ServiceManager.LOGTAG, "xmppPort=" + this.xmppPort);

        this.sharedPrefs = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = this.sharedPrefs.edit();
		editor.putString(Constants.API_KEY, this.apiKey);
		editor.putString(Constants.VERSION, this.version);
		editor.putString(Constants.XMPP_HOST, this.xmppHost);
		editor.putInt(Constants.XMPP_PORT, Integer.parseInt(this.xmppPort));
		editor.putString(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME,
                this.callbackActivityPackageName);
		editor.putString(Constants.CALLBACK_ACTIVITY_CLASS_NAME,
                this.callbackActivityClassName);
		editor.putString(Constants.XMPP_NEW_USERNAME, this.newusername);
		editor.putString(Constants.XMPP_NEW_PASSWORD, this.newpassword);
		editor.putInt(Constants.XMPP_IS_ANONYMITY, 1);
		editor.commit();
		// Log.i(LOGTAG, "sharedPrefs=" + sharedPrefs.toString());
	}

	public void EBSLogout() {
		// Intent intent = NotificationService.getIntent(context);

		//PushMsgService.getXmppManager().EBSLogout();

		// context.stopService(new
		// Intent(context.getString(R.R.string.ServerName)));
	}

	// public void startService() {
	// Thread serviceThread = new Thread(new Runnable() {
	// @Override
	// public void run() {
	// Intent intent = NotificationService.getIntent(context);
	// context.startService(intent);
	// }
	// });
	// serviceThread.start();
	// }

	public void restartService() {
		if (StartTbsweb.isMyServiceRunning(this.context,
                this.context.getString(R.string.PUSH_SERVICE_NAME))) {
			Intent intent = PushMsgService.getIntent(this.context);
            this.context.stopService(intent);
		}
//
//		Thread serviceThread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Intent intent = PushMsgService.getIntent(ServiceManager.this.context);
//                ServiceManager.this.context.startService(intent);
//			}
//		});
//		serviceThread.start();
	}

	public void stopService() {
        //this.context.stopService(new Intent(this.context.getString(R.string.ServerName)));
        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.ServerName));//你定义的service的action
        intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
        context.stopService(intent);
    }

	public void stopNotificationService() {
		Intent intent = PushMsgService.getIntent(this.context);
        this.context.stopService(intent);
	}

	public void stopAllService() {
		Intent intent = PushMsgService.getIntent(this.context);
        this.context.stopService(intent);
        intent = new Intent();
        intent.setAction(context.getString(R.string.ServerName));//你定义的service的action
        intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
        context.stopService(intent);
        intent = new Intent();
        intent.setAction(context.getString(R.string.WebServerName));//你定义的service的action
        intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
        context.stopService(intent);
        intent = new Intent();
        intent.setAction(context.getString(R.string.TbsTaskServer));//你定义的service的action
        intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
        context.stopService(intent);
        intent = new Intent();
        intent.setAction(context.getString(R.string.ServerName1));//你定义的service的action
        intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
        context.stopService(intent);
//        this.context.stopService(new Intent(this.context.getString(R.string.ServerName)));
//        this.context.stopService(new Intent(this.context
//				.getString(R.string.WebServerName)));
//        this.context.stopService(new Intent(this.context
//				.getString(R.string.TbsTaskServer)));
//        this.context.stopService(new Intent(this.context.getString(R.string.ServerName1)));
	}

	protected void iniPath() {
		// TODO Auto-generated method stub
		String webRoot = UIHelper.getSoftPath(context);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this.context,
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
	}

	// public String getVersion() {
	// return version;
	// }
	//
	// public String getApiKey() {
	// return apiKey;
	// }

	public void setNotificationIcon(int iconId) {
		SharedPreferences.Editor editor = this.sharedPrefs.edit();
		editor.putInt(Constants.NOTIFICATION_ICON, iconId);
		editor.commit();
	}

	// public void viewNotificationSettings() {
	// Intent intent = new Intent().setClass(context,
	// NotificationSettingsActivity.class);
	// context.startActivity(intent);
	// }

	public static void viewNotificationSettings(Context context) {
		Intent intent = new Intent().setClass(context,
				NotificationSettingsActivity.class);
		context.startActivity(intent);
	}

	public void setUserInfo(String username, String password, String loginID) {
        newusername = username;
        newpassword = password;

		SharedPreferences.Editor editor = this.sharedPrefs.edit();
		editor.putInt(Constants.XMPP_IS_ANONYMITY, 0);
		editor.putString(Constants.XMPP_NEW_USERNAME, username);
		editor.putString(Constants.XMPP_NEW_PASSWORD, password);
		editor.putString(Constants.XMPP_EBS_LOGINID, loginID);
		editor.commit();
	}

}
