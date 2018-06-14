package com.tbs.chat.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tbs.chat.R;
import com.tbs.chat.ui.chatting.FMessageConversationUI;

public class BackCurrnetActivity extends Activity implements OnClickListener{
	
	private Button backBtn1 = null;
	private Button backBtn2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_dialog_item5);
		init();
	}

	/**
	 * 
	 */
	private void init() {
		backBtn1 = (Button) findViewById(R.id.confirm_dialog_btn1);
		backBtn2 = (Button) findViewById(R.id.confirm_dialog_btn3);
		
		backBtn1.setOnClickListener(this);
		backBtn2.setOnClickListener(this);
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.confirm_dialog_btn1) {
			Intent intent = new Intent(this,FMessageConversationUI.class);
			setResult(RESULT_OK,intent);
			this.finish();
		} else if (v.getId() == R.id.confirm_dialog_btn3) {
		} else {
		}
	}
}
