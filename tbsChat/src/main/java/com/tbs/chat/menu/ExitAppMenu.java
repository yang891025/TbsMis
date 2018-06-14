package com.tbs.chat.menu;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.tbs.chat.R;
import com.tbs.chat.constants.Constants;

public class ExitAppMenu extends Activity {

	protected static final String TAG = "ExitAppMenu";
	
	LinearLayout exitBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog_menu_list_layout_button);
		init();
	}

	private void init() {
		
        getWindow().setGravity(Gravity.BOTTOM);//设置靠右对齐
		getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		
		exitBtn = (LinearLayout) findViewById(R.id.popup_layout);
		exitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(Constants.OPEN_EXIT_DIALOG);
				sendBroadcast(intent);				
			}
		});
	}
}
