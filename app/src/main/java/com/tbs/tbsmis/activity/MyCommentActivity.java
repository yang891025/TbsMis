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
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
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
public class MyCommentActivity extends Activity implements
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
	private LinearLayout comment_set;
	private LinearLayout comment_log;
	private CheckBox log_checkbox;
	private CheckBox comment_checkbox;
	private LinearLayout comment_url;
	private RelativeLayout comment_enable;
	private CheckBox comment_box;
	private String appNewsFile;
    private String userIni;

    public MyCommentActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting_comment);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.showDetail = (RelativeLayout) this.findViewById(R.id.comment_logcat);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.comment_enable = (RelativeLayout) this.findViewById(R.id.comment_enable);

        this.comment_set = (LinearLayout) this.findViewById(R.id.comment_set);
        this.comment_log = (LinearLayout) this.findViewById(R.id.comment_log);
        this.comment_url = (LinearLayout) this.findViewById(R.id.comment_url);

        this.log_checkbox = (CheckBox) this.findViewById(R.id.log_checkbox);
        this.comment_checkbox = (CheckBox) this.findViewById(R.id.comment_checkbox);
        this.comment_box = (CheckBox) this.findViewById(R.id.comment_box);

        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.webview = (WebView) this.findViewById(R.id.comment_webview);
        this.webview.getSettings().setJavaScriptEnabled(true);// 允许JS执行
        this.webview.getSettings().setSupportZoom(true);
        this.webview.getSettings().setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.webview.getSettings().setDisplayZoomControls(false);
        }
        // 取消滚动条
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
                MyCommentActivity.this.loadingDialogState = progress < 100;

				if (MyCommentActivity.this.loadingDialogState) {
                    MyCommentActivity.this.startAnimation();
				} else {
                    MyCommentActivity.this.stopAnimation();
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
        this.title.setText("我的评论/笔记");

        this.comment_enable.setOnClickListener(this);
        this.comment_set.setOnClickListener(this);
        this.comment_log.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
		String webRoot =UIHelper.getSoftPath(this);
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
        if(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
				"commentSetup_enable", "0", (byte) 0)) == 1) {
            this.comment_box.setChecked(true);
		}

//        this.log_checkbox.setChecked(true);
//        this.showDetail.setVisibility(View.VISIBLE);
        this.PromptMsg = this.m_iniFileIO.getIniString(this.appNewsFile, "MSGURL",
				"CommentMsg", "/comment/comment.cbs", (byte) 0);
        this.resname = UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
		String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "Offline",
				"offlineAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(this.userIni,
				"Offline", "offlinePort", constants.DefaultLocalPort,
				(byte) 0);
		String baseUrl = "http://" + ipUrl + ":" + portUrl;
		String tempUrl = StringUtils.isUrl(baseUrl+PromptMsg,baseUrl, this.resname);
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
		case R.id.comment_set:
			if (this.comment_checkbox.isChecked()) {
                this.comment_checkbox.setChecked(false);
                this.comment_url.setVisibility(View.GONE);
			} else {
                this.comment_checkbox.setChecked(true);
                this.comment_url.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.comment_log:
//			if (this.log_checkbox.isChecked()) {
//                this.log_checkbox.setChecked(false);
//                this.showDetail.setVisibility(View.GONE);
//                this.webview.stopLoading();
//			} else {
//                this.log_checkbox.setChecked(true);
//                this.showDetail.setVisibility(View.VISIBLE);
//                this.PromptMsg = this.m_iniFileIO.getIniString(this.appNewsFile, "MSGURL",
//						"CommentMsg", "/comment/comment.cbs", (byte) 0);
//                this.resname = UIHelper.getShareperference(this,
//						constants.SAVE_LOCALMSGNUM, "resname", "yqxx");
//				String ipUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
//						"offlineAddress", constants.DefaultServerIp, (byte) 0);
//				String portUrl = this.m_iniFileIO.getIniString(this.appNewsFile,
//						"NETWORK", "offlinePort", constants.DefaultServerPort,
//						(byte) 0);
//				String baseUrl = "http://" + ipUrl + ":" + portUrl;
//				String tempUrl = StringUtils.isUrl(this.PromptMsg,baseUrl, this.resname);
//                this.webview.loadUrl(tempUrl);
//			}
            Intent intent = new Intent();
            // intent.putExtra("phoneNum", phoneTxt);
            intent.setClass(this, CommentActivity.class);
            this.startActivity(intent);
			break;
		case R.id.comment_enable:
			if (this.comment_box.isChecked()) {
                this.comment_box.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "SETUP",
						"commentSetup_enable", "0");
			} else {
                this.comment_box.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "SETUP",
						"commentSetup_enable", "1");
			}
			break;
		}
	}

}
