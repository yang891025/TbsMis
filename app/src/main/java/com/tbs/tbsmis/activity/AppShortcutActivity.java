package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;

public class AppShortcutActivity extends Activity {
//	 private LinearLayout app_yqxx;
//	 private LinearLayout app_dbn;
	private ImageView finishBtn;
	private ImageView downBtn;
	private TextView title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_select_activity);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// app_yqxx = (LinearLayout) findViewById(R.id.app_yqxx);
		// app_dbn = (LinearLayout) findViewById(R.id.app_dbn);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("快捷方式");
        this.downBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AppShortcutActivity.this.finish();
                AppShortcutActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
        this.finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AppShortcutActivity.this.finish();
                AppShortcutActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
		// app_yqxx.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// UIHelper.showcreateDeskShortCut(AppShortcutActivity.this,
		// getString(R.string.app_yqxx), "yqxx");
		// }
		// });
		// app_dbn.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// UIHelper.showcreateDeskShortCut(AppShortcutActivity.this,
		// getString(R.string.app_dbn), "dbn");
		// }
		// });
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		MyActivity.getInstance().finishActivity(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
		}
		return true;
	}
}