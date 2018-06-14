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
package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Activity for displaying the notification setting view.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@SuppressLint("SetJavaScriptEnabled")
public class LogSetupActivity extends Activity implements
		View.OnClickListener {

	private ImageView finishBtn;
	private ImageView downBtn;
	private TextView title;
	private WebView webview;
	private AnimationDrawable loadingAnima;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private boolean loadingDialogState;
	private RelativeLayout showDetail;
	private IniFile m_iniFileIO;
	private String PromptMsg;
	private String resname;
	private LinearLayout log_set;
	private LinearLayout log_log;
	private CheckBox showlog_checkbox;
	private CheckBox log_checkbox;
	private LinearLayout log_url;
	private RelativeLayout log_enable;
	private CheckBox log_box;
	private String appNewsFile;

	public LogSetupActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting_log);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.showDetail = (RelativeLayout) this.findViewById(R.id.log_logcat);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.log_enable = (RelativeLayout) this.findViewById(R.id.log_enable);

        this.log_set = (LinearLayout) this.findViewById(R.id.log_set);
        this.log_log = (LinearLayout) this.findViewById(R.id.log_log);
        this.log_url = (LinearLayout) this.findViewById(R.id.log_url);

        this.showlog_checkbox = (CheckBox) this.findViewById(R.id.showlog_checkbox);
        this.log_checkbox = (CheckBox) this.findViewById(R.id.log_checkbox);
        this.log_box = (CheckBox) this.findViewById(R.id.log_box);

        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.webview = (WebView) this.findViewById(R.id.log_webview);
        this.webview.getSettings().setJavaScriptEnabled(true);// 允许JS执行
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.webview.getSettings().setDisplayZoomControls(false);
		}
		// 取消滚动条
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
                LogSetupActivity.this.loadingDialogState = progress < 100;

				if (LogSetupActivity.this.loadingDialogState) {
                    LogSetupActivity.this.startAnimation();
				} else {
                    LogSetupActivity.this.stopAnimation();
				}

			}
		});
        this.webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);// 点击超链接的时候重新在原来进程上加载URL
				return true;
			}
		});
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("我的日志");

        this.log_enable.setOnClickListener(this);
        this.log_set.setOnClickListener(this);
        this.log_log.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
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
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
				"logSetup_enable", "0", (byte) 0)) == 1) {
            this.log_box.setChecked(true);
		}
        this.showlog_checkbox.setChecked(true);
        this.showDetail.setVisibility(View.VISIBLE);
        this.PromptMsg = this.m_iniFileIO.getIniString(this.appNewsFile, "MSGURL",
				"LogMsg", "0", (byte) 0);
        this.resname = UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
		String ipUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(this.appNewsFile,
				"SERVICE", "currentPort", constants.DefaultLocalPort,
				(byte) 0);
		String baseUrl = "http://" + ipUrl + ":" + portUrl;
		String tempUrl = StringUtils.isUrl(this.PromptMsg, baseUrl, this.resname);
        this.webview.loadUrl(tempUrl);
	}

	public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
	}

	public void stopAnimation() {
		// loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
	}

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
		case R.id.log_set:
			if (this.log_checkbox.isChecked()) {
                this.log_checkbox.setChecked(false);
                this.log_url.setVisibility(View.GONE);
			} else {
                this.log_checkbox.setChecked(true);
                this.log_url.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.log_log:
			if (this.showlog_checkbox.isChecked()) {
                this.showlog_checkbox.setChecked(false);
                this.showDetail.setVisibility(View.GONE);
                this.webview.stopLoading();
			} else {
                this.showlog_checkbox.setChecked(true);
                this.showDetail.setVisibility(View.VISIBLE);
                this.PromptMsg = this.m_iniFileIO.getIniString(this.appNewsFile, "MSGURL",
						"LogMsg", "", (byte) 0);
                this.resname = UIHelper.getShareperference(this,
						constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
				String ipUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "SERVICE",
						"currentAddress", constants.DefaultLocalIp, (byte) 0);
				String portUrl = this.m_iniFileIO.getIniString(this.appNewsFile,
						"SERVICE", "currentPort", constants.DefaultLocalPort,
						(byte) 0);
				String baseUrl = "http://" + ipUrl + ":" + portUrl;
				String tempUrl = StringUtils.isUrl(this.PromptMsg, baseUrl, this.resname);
                this.webview.loadUrl(tempUrl);
			}
			break;
		case R.id.log_enable:
			if (this.log_box.isChecked()) {
                this.log_box.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "SETUP",
						"logSetup_enable", "0");
			} else {
                this.log_box.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "SETUP",
						"logSetup_enable", "1");
			}
			break;
		}
	}
}
