package com.tbs.tbsmis.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
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

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

@SuppressLint("SetJavaScriptEnabled")
public class UpdateActivity extends Activity implements View.OnClickListener {
	private ImageView finishBtn;
	private ImageView downBtn;
	private TextView title;
	private RelativeLayout check_update_now;
	private WebView webview;
	private AnimationDrawable loadingAnima;
	private RelativeLayout loadingIV;
	private ImageView iv;
	private boolean loadingDialogState;
	private RelativeLayout showDetail;
	private LinearLayout update_set;
	private LinearLayout update_log;
	private RelativeLayout check_update;
	private CheckBox update_box;
	private CheckBox update_checkbox;
	private CheckBox log_checkbox;
	private LinearLayout check_update_url;

	@Override
	@SuppressLint("SetJavaScriptEnabled")
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        this.extracted();
	}

	private void extracted() {
		// TODO Auto-generated method stub
        this.showDetail = (RelativeLayout) this.findViewById(R.id.update_logcat);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.check_update = (RelativeLayout) this.findViewById(R.id.check_update);
        this.check_update_now = (RelativeLayout) this.findViewById(R.id.check_update_now);

        this.update_set = (LinearLayout) this.findViewById(R.id.update_set);
        this.update_log = (LinearLayout) this.findViewById(R.id.update_log);
        this.check_update_url = (LinearLayout) this.findViewById(R.id.check_update_url);

        this.update_box = (CheckBox) this.findViewById(R.id.update_box);
        this.update_checkbox = (CheckBox) this.findViewById(R.id.update_checkbox);
        this.log_checkbox = (CheckBox) this.findViewById(R.id.log_checkbox);

        this.update_set.setOnClickListener(this);
        this.update_log.setOnClickListener(this);
        this.check_update.setOnClickListener(this);
        this.check_update_now.setOnClickListener(this);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        this.webview = (WebView) this.findViewById(R.id.update_webview);
        this.webview.getSettings().setJavaScriptEnabled(true);// 允许JS执行
		// 设置 内容适应屏幕
        this.webview.getSettings().setLoadWithOverviewMode(true);
		// 取消滚动条
        this.webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        this.webview.requestFocus();
        this.webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
                UpdateActivity.this.loadingDialogState = progress < 100;

				if (UpdateActivity.this.loadingDialogState) {
                    UpdateActivity.this.startAnimation();
				} else {
                    UpdateActivity.this.stopAnimation();
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
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);

        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("版本更新");
		int update_now = UIHelper.getShareperference(
                this, constants.SAVE_LOCALMSGNUM,
				"check_update", 1);
		if (update_now == 1) {
            this.update_box.setChecked(true);
		}
        this.log_checkbox.setChecked(true);
        this.showDetail.setVisibility(View.VISIBLE);
        this.webview.loadUrl("file:///android_asset/update_history.html");
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
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		MyActivity.getInstance().finishActivity(this);
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
		case R.id.update_set:
			if (this.update_checkbox.isChecked()) {
                this.update_checkbox.setChecked(false);
                this.check_update_url.setVisibility(View.GONE);
			} else {
                this.update_checkbox.setChecked(true);
                this.check_update_url.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.update_log:
			if (this.log_checkbox.isChecked()) {
                this.log_checkbox.setChecked(false);
                this.showDetail.setVisibility(View.GONE);
                this.webview.stopLoading();
			} else {
                this.log_checkbox.setChecked(true);
                this.showDetail.setVisibility(View.VISIBLE);
                this.webview.loadUrl("file:///android_asset/update_history.html");
			}
			break;
		case R.id.check_update:
			if (this.update_box.isChecked()) {
                this.update_box.setChecked(false);
				UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
						"check_update", 0);
			} else {
                this.update_box.setChecked(true);
				UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
						"check_update", 1);
			}

			break;
		case R.id.check_update_now:
			UpdateManager.getUpdateManager().checkAppUpdate(
                    this, true);
			break;
		}
	}
}