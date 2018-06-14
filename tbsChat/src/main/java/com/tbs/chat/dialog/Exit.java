package com.tbs.chat.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.tbs.chat.R;
import com.tbs.chat.ui.chatting.MainMessage;

public class Exit extends Activity {
	// private MyDialog dialog;
	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exit_dialog);
		layout = (LinearLayout) findViewById(R.id.exit_layout);
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
		Intent intent = new Intent(this,MainMessage.class);
		setResult(RESULT_OK,intent);
		this.finish();
	}
}
