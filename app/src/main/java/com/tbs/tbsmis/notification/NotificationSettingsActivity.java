/*
 * Copyright 2010 the original author or authors.
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.DES;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Activity for displaying the notification setting view.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@SuppressLint("SetJavaScriptEnabled")
public class NotificationSettingsActivity extends Activity implements
		View.OnClickListener {

	private ImageView finishBtn;
	private ImageView downBtn;
	private TextView title;
//	private WebView webview;
//	private AnimationDrawable loadingAnima;
//	private RelativeLayout loadingIV;
//	private ImageView iv;
	//private final boolean loadingDialogState;
	//private RelativeLayout showDetail;
	private IniFile m_iniFileIO;
	private String PromptMsg;
	private String resname;
	private LinearLayout pushmsg_set;
	//private CheckBox log_checkbox;
	private CheckBox pushmsg_checkbox;
	private LinearLayout pushmsg_url;
	private RelativeLayout notification_enable;
	private RelativeLayout sound_enable;
	private RelativeLayout vibrate_enable;
	private CheckBox notification_box;
	private CheckBox sound_box;
	private CheckBox vibrate_box;
	private String appNewsFile;
	protected String address_editTxt;
	private Button app_push;
	private Button log_checkbox_btn;
	private RelativeLayout show_notification_enable;
	private CheckBox show_notification_box;
    private String userIni;

    public NotificationSettingsActivity() {
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting_pushmsg);
		MyActivity.getInstance().addActivity(this);
		//showDetail = (RelativeLayout) findViewById(R.R.id.update_logcat);
		//loadingIV = (RelativeLayout) findViewById(R.R.id.loading_dialog);
        this.notification_enable = (RelativeLayout) this.findViewById(R.id.notification_enable);
        this.sound_enable = (RelativeLayout) this.findViewById(R.id.sound_enable);
        this.vibrate_enable = (RelativeLayout) this.findViewById(R.id.vibrate_enable);

        this.pushmsg_set = (LinearLayout) this.findViewById(R.id.pushmsg_set);
        this.pushmsg_url = (LinearLayout) this.findViewById(R.id.pushmsg_url);
        this.app_push = (Button) this.findViewById(R.id.app_send_push);
		//log_checkbox = (CheckBox) findViewById(R.R.id.log_checkbox);
        this.log_checkbox_btn = (Button) this.findViewById(R.id.log_checkbox_btn);
        this.pushmsg_checkbox = (CheckBox) this.findViewById(R.id.pushmsg_checkbox);
        this.notification_box = (CheckBox) this.findViewById(R.id.notification_box);
        this.sound_box = (CheckBox) this.findViewById(R.id.sound_box);
        this.vibrate_box = (CheckBox) this.findViewById(R.id.vibrate_box);
        this.show_notification_enable = (RelativeLayout) this.findViewById(R.id.show_notification_enable);
        this.show_notification_box = (CheckBox) this.findViewById(R.id.show_notification_box);

//		iv = (ImageView) findViewById(R.R.id.gifview);
//		webview = (WebView) findViewById(R.R.id.update_webview);
//		webview.getSettings().setJavaScriptEnabled(true);// 允许JS执行
//		webview.getSettings().setSupportZoom(true);
//		webview.getSettings().setBuiltInZoomControls(true);
//		webview.getSettings().setDisplayZoomControls(false);
//		// 取消滚动�?
//		webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
//		webview.setWebChromeClient(new WebChromeClient() {
//			@Override
//			public void onProgressChanged(WebView view, int progress) {
//                loadingDialogState = progress < 100;
//
//				if (loadingDialogState) {
//					startAnimation();
//				} else {
//					stopAnimation();
//				}
//
//			}
//		});
//		webview.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				view.loadUrl(url);// 点击超链接的时候重新在原来进程上加载URL
//				return true;
//			}
//		});
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("信息推送");
        this.show_notification_enable.setOnClickListener(this);
        this.notification_enable.setOnClickListener(this);
        this.sound_enable.setOnClickListener(this);
        this.vibrate_enable.setOnClickListener(this);
        this.pushmsg_set.setOnClickListener(this);
        this.log_checkbox_btn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.app_push.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
		String webRoot = UIHelper.getSoftPath(this);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.m_iniFileIO = new IniFile();
        this.appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appNewsFile;
        if(Integer.parseInt(this.m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Push",
                "sendSetting", "0", (byte) 0));
        if (nVal == 1) {
            app_push.setVisibility(View.VISIBLE);
        }else{
            app_push.setVisibility(View.GONE);
        }
		SharedPreferences pre = this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		if (pre.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
            this.notification_box.setChecked(true);
		} else {
            this.show_notification_enable.setEnabled(false);
            this.sound_enable.setEnabled(false);
            this.vibrate_enable.setEnabled(false);
		}
		if (pre.getBoolean(Constants.SETTINGS_SOUND_ENABLED, true)) {
            this.sound_box.setChecked(true);
		}
		if (pre.getBoolean(Constants.SETTINGS_VIBRATE_ENABLED, false)) {
            this.vibrate_box.setChecked(true);
		}
		if (pre.getBoolean(Constants.SETTINGS_SHOW_NOTIFICATION, true)) {
            this.show_notification_box.setChecked(true);
		}
		//log_checkbox.setChecked(true);
		//showDetail.setVisibility(View.VISIBLE);
//		PromptMsg = m_iniFileIO.getIniString(appNewsFile, "MSGURL", "PushMsg",
//				"", (byte) 0);
//		resname = UIHelper.getShareperference(this, constants.SAVE_LOCALMSGNUM,
//				"resname", "yqxx");
//		String ipUrl = m_iniFileIO.getIniString(appNewsFile, "SERVICE",
//				"currentAddress", constants.DefaultLocalIp, (byte) 0);
//		String portUrl = m_iniFileIO.getIniString(appNewsFile, "SERVICE",
//				"currentPort", constants.DefaultLocalPort, (byte) 0);
//		String baseUrl = "http://" + ipUrl + ":" + portUrl;
//		String tempUrl = StringUtils.isUrl(PromptMsg, baseUrl, resname);
//		webview.loadUrl(tempUrl);

	}

//	public void startAnimation() {
//		loadingAnima = (AnimationDrawable) iv.getBackground();
//		loadingAnima.start();
//		loadingIV.setVisibility(View.VISIBLE);
//	}
//
//	public void stopAnimation() {
//		// loadingAnima.stop();
//		loadingIV.setVisibility(View.INVISIBLE);
//	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.more_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.finish_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.app_send_push:
			UIHelper.showPushPage(this);
			break;
		case R.id.pushmsg_set:
			if (this.pushmsg_checkbox.isChecked()) {
                this.pushmsg_checkbox.setChecked(false);
                this.pushmsg_url.setVisibility(View.GONE);
			} else {
                this.pushmsg_checkbox.setChecked(true);
                this.pushmsg_url.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.log_checkbox_btn:
            Intent intent = new Intent();
            intent.setClass(this,
                    NotificationListActivity.class);
            //intent.putExtra("tempUrl", sourceUrl);
            this.startActivity(intent);
//			if (log_checkbox.isChecked()) {
//				log_checkbox.setChecked(false);
//				showDetail.setVisibility(View.GONE);
//				//webview.stopLoading();
//			} else {
//				log_checkbox.setChecked(true);
//				showDetail.setVisibility(View.VISIBLE);
//				PromptMsg = m_iniFileIO.getIniString(appNewsFile, "MSGURL",
//						"PushMsg", "", (byte) 0);
//				resname = UIHelper.getShareperference(this,
//						constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
//				String ipUrl = m_iniFileIO.getIniString(appNewsFile, "SERVICE",
//						"currentAddress", constants.DefaultLocalIp, (byte) 0);
//				String portUrl = m_iniFileIO.getIniString(appNewsFile,
//						"SERVICE", "currentPort", constants.DefaultLocalPort,
//						(byte) 0);
//				String baseUrl = "http://" + ipUrl + ":" + portUrl;
//				String tempUrl = StringUtils.isUrl(PromptMsg, baseUrl, resname);
//				//webview.loadUrl(tempUrl);
//			}
			break;
		case R.id.notification_enable:
			if (this.notification_box.isChecked()) {
                this.notification_box.setChecked(false);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_NOTIFICATION_ENABLED, false);
                this.sound_enable.setEnabled(false);
                this.vibrate_enable.setEnabled(false);
                this.show_notification_enable.setEnabled(false);
				ServiceManager serviceManager = new ServiceManager(this);
				serviceManager.setNotificationIcon(R.drawable.notification);
				serviceManager.stopNotificationService();
			} else {
                this.notification_box.setChecked(true);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_NOTIFICATION_ENABLED, true);
                this.sound_enable.setEnabled(true);
                this.vibrate_enable.setEnabled(true);
                this.show_notification_enable.setEnabled(true);
				if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
						"Login", "LoginFlag", "0", (byte) 0)) == 1) {
					ServiceManager serviceManager = new ServiceManager(this);
					serviceManager.setNotificationIcon(R.drawable.notification);
					serviceManager.setUserInfo(this.m_iniFileIO.getIniString(
                            this.userIni, "Login", "Account", "", (byte) 0), DES
							.encrypt(this.m_iniFileIO.getIniString(this.userIni,
									"Login", "PassWord", "", (byte) 0)),
                            this.m_iniFileIO.getIniString(this.userIni, "Login",
									"LoginId", "", (byte) 0));
					serviceManager.restartService();
				} else {
					ServiceManager serviceManager = new ServiceManager(this);
					serviceManager.setNotificationIcon(R.drawable.notification);
					serviceManager.restartService();
				}
			}
			break;
		case R.id.sound_enable:
			if (this.sound_box.isChecked()) {
                this.sound_box.setChecked(false);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_SOUND_ENABLED, false);
			} else {
                this.sound_box.setChecked(true);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_SOUND_ENABLED, true);
			}
			break;
		case R.id.show_notification_enable:
			if (this.show_notification_box.isChecked()) {
                this.show_notification_box.setChecked(false);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_SHOW_NOTIFICATION, false);
			} else {
                this.show_notification_box.setChecked(true);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_SHOW_NOTIFICATION, true);
			}
			break;
		case R.id.vibrate_enable:
			if (this.vibrate_box.isChecked()) {
                this.vibrate_box.setChecked(false);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_VIBRATE_ENABLED, false);
			} else {
                this.vibrate_box.setChecked(true);
				UIHelper.setSharePerference(this,
						Constants.SHARED_PREFERENCE_NAME,
						Constants.SETTINGS_VIBRATE_ENABLED, true);
			}
			break;
		}
	}

}
