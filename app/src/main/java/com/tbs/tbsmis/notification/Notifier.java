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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.tbsmis.activity.LanuchActivity;
import com.tbs.tbsmis.activity.OfflineActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.weixin.WeixinNewMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import NewsTool.NewsContent;
import NewsTool.NewsMessage;

/**
 * This class is to notify the user of messages with NotificationManager.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {

	private static final String LOGTAG = LogUtil.makeLogTag(Notifier.class);

	private static final Random random = new Random(System.currentTimeMillis());

	private final Context context;

	private final SharedPreferences sharedPrefs;

	private final NotificationManager notificationManager;

	public Notifier(Context context) {
		this.context = context;
        sharedPrefs = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@SuppressWarnings("deprecation")
	public void notify(String notificationId, String apiKey, String title,
			String message, String uri) {
		if (this.isNotificationEnabled()) {
			// Show the toast
			if (this.isNotificationToastEnabled()) {
				Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
			}
			// Notification
            Notification notification;
            Notification.Builder builder1 = new Notification.Builder(context);
            builder1.setSmallIcon(this.getNotificationIcon()); //设置图标
            builder1.setTicker(message);
            builder1.setContentTitle(title); //设置标题
            builder1.setWhen(System.currentTimeMillis()); //发送时间
            builder1.setAutoCancel(true);//打开程序后图标消失
            //Intent intent = new Intent(context, DownloadListActivity.class);

			//Notification notification = new Notification();
            builder1.setDefaults(Notification.DEFAULT_SOUND); //设置默认的灯光
			if (this.isNotificationSoundEnabled()) {
                builder1.setDefaults(Notification.DEFAULT_SOUND); //设置默认的提示音
			}
			if (this.isNotificationVibrateEnabled()) {
                builder1.setDefaults(Notification.DEFAULT_VIBRATE); //设置默认振动方式
			}
           // builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
			//notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent intent = new Intent();
			//System.out.println(getNotificationflag());
			if (this.getNotificationflag() == 1) {
				if (!StringUtils.isEmpty(message)) {
					if (message.indexOf(":") > 0) {
						if (message.substring(0, message.indexOf(":") + 1)
								.equals("download:")) {
							intent.setClass(this.context, OfflineActivity.class);
						}else if(message.substring(0, message.indexOf(":") + 1)
								.equals("wechat:")){
							intent.setClass(this.context, WeixinNewMessage.class);
						} else {
//							intent.setClass(this.context, DetailActivity.class);
//							intent.putExtra("flag", 2);
//							intent.putExtra("winStrState",
//									constants.STATEFORSEARCH);
//							intent.putExtra(Constants.NOTIFICATION_ID,
//									notificationId);
//							intent.putExtra(Constants.NOTIFICATION_TITLE, title);
//							intent.putExtra(Constants.NOTIFICATION_URI, uri);
                            intent.setClass(this.context,
                                    NotificationListActivity.class);
						}
					} else {
//						intent.setClass(this.context, DetailActivity.class);
//						intent.putExtra("flag", 2);
//						intent.putExtra("winStrState", constants.STATEFORSEARCH);
//						intent.putExtra(Constants.NOTIFICATION_ID,
//								notificationId);
//						intent.putExtra(Constants.NOTIFICATION_TITLE, title);
//						intent.putExtra(Constants.NOTIFICATION_URI, uri);
                        intent.setClass(this.context,
                                NotificationListActivity.class);
					}
				}
			} else {
				if (message.indexOf(":") > 0) {
					if (message.substring(0, message.indexOf(":") + 1).equals(
							"download:")) {
						intent.setClass(this.context, LanuchActivity.class);
						intent.putExtra("flag", 3);
					}else if(message.substring(0, message.indexOf(":") + 1)
							.equals("wechat:")){
						intent.setClass(this.context, LanuchActivity.class);
						intent.putExtra("flag", 4);
					} else {
//						intent.setClass(this.context, LanuchActivity.class);
//						intent.putExtra("flag", 2);
//						intent.putExtra(Constants.NOTIFICATION_ID,
//								notificationId);
//						intent.putExtra(Constants.NOTIFICATION_TITLE, title);
//						intent.putExtra(Constants.NOTIFICATION_URI, uri);
                        intent.setClass(this.context,
                                NotificationListActivity.class);
					}
				} else {
//					intent.setClass(this.context, LanuchActivity.class);
//					intent.putExtra("flag", 2);
//					intent.putExtra(Constants.NOTIFICATION_ID, notificationId);
//					intent.putExtra(Constants.NOTIFICATION_TITLE, title);
//					intent.putExtra(Constants.NOTIFICATION_URI, uri);
                    intent.setClass(this.context,
                            NotificationListActivity.class);
				}
			}
			int notificationflag = UIHelper.getShareperference(this.context,
					Constants.SHARED_PREFERENCE_NAME, "times", 0);
			if (notificationflag <= 100) {
				notificationflag = notificationflag + 1;
				UIHelper.setSharePerference(this.context,
						Constants.SHARED_PREFERENCE_NAME, "times",
						notificationflag);
			} else {
				notificationflag = 0;
				UIHelper.setSharePerference(this.context,
						Constants.SHARED_PREFERENCE_NAME, "times",
						notificationflag);
			}
//			PendingIntent contentIntent = PendingIntent
//					.getActivity(this.context, notificationflag, intent,
//							PendingIntent.FLAG_UPDATE_CURRENT);
//			notification.setLatestEventInfo(context, title, message,
//					contentIntent);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder1.setContentIntent(pendingIntent);
            notification = builder1.build();
            this.notificationManager.notify(Notifier.random.nextInt(), notification);
            this.doDeploy();
            this.addNotification(notificationId,title,message,uri);
		} else {
			Log.w(Notifier.LOGTAG, "Notificaitons disabled.");
		}
	}

	private int getNotificationIcon() {
		return this.sharedPrefs.getInt(Constants.NOTIFICATION_ICON, 0);
	}

	private int getNotificationflag() {
		return this.sharedPrefs.getInt("appOn", 0);
	}

	private boolean isNotificationEnabled() {
		return this.sharedPrefs.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
				true);
	}

	private boolean isNotificationSoundEnabled() {
		return this.sharedPrefs.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true);
	}

	private boolean isNotificationVibrateEnabled() {
		return this.sharedPrefs.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, true);
	}

	private boolean isNotificationToastEnabled() {
		return this.sharedPrefs.getBoolean(Constants.SETTINGS_TOAST_ENABLED, false);
	}
    private boolean doDeploy() {
        String webRoot = UIHelper.getShareperference(this.context,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String doPath = webRoot
                + "notification";
        File webRootFile = new File(doPath);
        String launchState = UIHelper.getShareperference(this.context,
                constants.SHARED_PREFERENCE_FIRST_LAUNCH, "launchState",
                "1.0.0");
        if ((webRootFile.exists() && webRootFile.isDirectory()) == false
                || !launchState.equals(this.getVersionName())) {
            webRootFile.mkdirs();
        } else {
            return true;
        }
        String webRootTbk = webRoot + "notification.tbk";
        try {
            InputStream is = this.context.getAssets().open(
                    "config/notification.tbk");
            OutputStream os = new FileOutputStream(webRootTbk);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int resoult = JTbszlib.UnZipFile(webRootTbk, doPath, 1, "");
        if (0 != resoult) {
            return false;
        }
        this.delZipFile(webRootTbk);
        return true;
    }
    // 获取程序的版本号
    private String getVersionName() {
        PackageManager packageManager = this.context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(this.context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return packInfo.versionName;
    }
    protected void delZipFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void addNotification(String notificationId, String title,
                                 String message, String uri)
    {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getShareperference(this.context,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false)
        {
            webRoot += "/";
        }
        String inipath = webRoot + "notification/" + "notification.ini";
        String savePath = webRoot + "notification/" + "notification.txt";
        NewsContent NewsContent = new NewsContent();
        if (NewsContent.initialize(inipath))
        {
            NewsMessage msg = new NewsMessage(false);
            long countFile = NewsContent.countField();
            for (long m = 0; m < countFile; m++)
            {
                if (NewsContent.getFieldInternalName(m).equalsIgnoreCase(
                        "title:"))
                    msg.setValue(m, title);
                else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("msg:"))
                    msg.setValue(m, message);
                else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("time:"))
                    msg.setValue(m, StringUtils.getDate());
                else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("id:"))
                    msg.setValue(m, notificationId);
                else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("url:"))
                    msg.setValue(m, uri);
                else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("type:"))
                    msg.setValue(m, "");
            }
            NewsContent.addNewsFiled(savePath, msg.getMessageHandle(), -1);
            msg.freeHandle();
        }
    }
}
