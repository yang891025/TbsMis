package com.tbs.chat.ui.address;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.chat.R;

public class AddMoreFriendsUI extends Activity implements OnClickListener{

	protected static final String TAG = "MainAddress";
	
	private BroadcastReceiver MyBroadCastReceiver;//广播
	private Intent intent;
	private LayoutInflater inflater;
	private LinearLayout rootLayout;
	private View titleView;
	private View moreView;
	private ImageButton rightBtn;
	private Button leftBtn;
	private TextView title;
	private TextView subTitle;
	private ProgressBar mProgress;
	
	private RelativeLayout search_friend_btn;
	private RelativeLayout add_qq_friend;
	private RelativeLayout add_phone_friend;
	private RelativeLayout search_public_num;
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_linearlayout);
		init();
		titleView();
		moreAction();
	}


	private void init() {
		
		/*
		 * 获得根布局
		 * 将标题布局增加到根布局中
		 * 将登录布局增加到根布局中
		 * 启动activity时不自动弹出软键盘
		 */
		rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);//获得根布局
		inflater = getLayoutInflater();//获得适配器
		titleView = inflater.inflate(R.layout.mm_title_webview, null);
		moreView = inflater.inflate(R.layout.add_friends, null);
		rootLayout.addView(titleView);//添加布局
		rootLayout.addView(moreView);//添加布局
	}
	
	
	private void titleView() {
		int white = getResources().getColor(R.color.white);
		rightBtn = (ImageButton) rootLayout.findViewById(R.id.title_btn1);
		leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
		title = (TextView) rootLayout.findViewById(R.id.title);
		subTitle = (TextView) rootLayout.findViewById(R.id.sub_title);
		mProgress = (ProgressBar) rootLayout.findViewById(R.id.title_progress);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText("取消");
		title.setText("添加朋友");
		leftBtn.setTextColor(white);
		leftBtn.setOnClickListener(this);
	}

	private void moreAction() {
		search_friend_btn = (RelativeLayout) rootLayout.findViewById(R.id.search_friend_btn);
		add_qq_friend = (RelativeLayout) rootLayout.findViewById(R.id.add_qq_friend);
		add_phone_friend = (RelativeLayout) rootLayout.findViewById(R.id.add_phone_friend);
		search_public_num = (RelativeLayout) rootLayout.findViewById(R.id.search_public_num);
		
		search_friend_btn.setOnClickListener(this);
		add_qq_friend.setOnClickListener(this);
		add_phone_friend.setOnClickListener(this);
		search_public_num.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_btn4) {
			finish();
			overridePendingTransition(R.anim.push_empty_out, R.anim.push_down_out);
		} else if (v.getId() == R.id.search_friend_btn) {
			Intent intent = new Intent(AddMoreFriendsUI.this,ContactSearchUI.class);
			startActivity(intent);
		} else if (v.getId() == R.id.add_qq_friend) {
		} else if (v.getId() == R.id.add_phone_friend) {
		} else if (v.getId() == R.id.search_public_num) {
		} else {
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.push_empty_out, R.anim.push_down_out);	
		}
		return true;
	}
}
