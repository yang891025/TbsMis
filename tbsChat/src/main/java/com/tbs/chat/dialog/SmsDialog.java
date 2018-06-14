package com.tbs.chat.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.ui.chatting.FMessageConversationUI;

public class SmsDialog extends Activity {
	// private MyDialog dialog;
	private LinearLayout layout;
	private TextView title;
	private TextView content;
	private Button rightBtn;
	private Button leftBtn;
	private Intent intent;
	String titleStr;
	String contentStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		init();
	}

	private void init() {
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			titleStr = intent.getStringExtra("title");
			contentStr = intent.getStringExtra("content");
		}
		
		layout = (LinearLayout) findViewById(R.id.exit_layout);
		leftBtn = (Button) findViewById(R.id.exitBtn1);
		rightBtn = (Button) findViewById(R.id.exitBtn0);
		title = (TextView) findViewById(R.id.title);
		content = (TextView) findViewById(R.id.textview);
		title.setText(titleStr);
		content.setText(contentStr);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton1(View v) {
		this.finish();
	}

	public void exitbutton0(View v) {
		Intent intent = new Intent(this,FMessageConversationUI.class);
		setResult(RESULT_OK,intent);
		this.finish();
	}
}
