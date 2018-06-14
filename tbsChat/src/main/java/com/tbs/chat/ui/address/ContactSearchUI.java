package com.tbs.chat.ui.address;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.dialog.EmptyDialog;
import com.tbs.chat.socket.Communication;

public class ContactSearchUI extends Activity implements OnClickListener {

	protected static final String TAG = "MainAddress";

	private BroadcastReceiver MyBroadCastReceiver;// 广播
	private Intent intent;
	private LayoutInflater inflater;
	private LinearLayout rootLayout;
	private View titleView;
	private View moreView;
	private Button rightBtn;
	private Button leftBtn;
	private TextView title;
	private TextView subTitle;
	private ProgressBar mProgress;
	private EditText contact_search_account_et;
	private Dialog dialog;
	private int white;
	private int grey;

	private String friendid = "";

	private String friendname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_linearlayout);
		init();
		titleView();
		moreAction();
		main();
	}

	private void init() {

		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
		filter.addAction(Constants.SEARCH_FRIEND_RESOULT);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				} else if (Constants.SEARCH_FRIEND_RESOULT.equals(intent
						.getAction())) {
					dismissDialog();
					// 获取bundle
					Bundle bundle = intent.getExtras();
					// 从bundle中取state
					int state = bundle.getInt("state", 0);
					// 判断头像状态
					if (state == Config.SEARCH_USER_FALSE) {
						Intent openDialog = new Intent(ContactSearchUI.this,
								EmptyDialog.class);
						openDialog.putExtra("title", "提示");
						openDialog.putExtra("content", "用户不存在");
						startActivity(openDialog);
					} else if (state == Config.SEARCH_USER_SUCCESS) {
						Intent openFriendDialog = new Intent(
								ContactSearchUI.this, SearchFriendUI.class);
						openFriendDialog.putExtras(bundle);
						startActivity(openFriendDialog);
					}
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);

		/*
		 * 获得根布局 将标题布局增加到根布局中 将登录布局增加到根布局中 启动activity时不自动弹出软键盘
		 */
		rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);// 获得根布局
		inflater = getLayoutInflater();// 获得适配器
		titleView = inflater.inflate(R.layout.mm_title_faq, null);
		moreView = inflater.inflate(R.layout.friend_search, null);
		rootLayout.addView(titleView);// 添加布局
		rootLayout.addView(moreView);// 添加布局
	}

	private void titleView() {
		white = getResources().getColor(R.color.white);
		grey = getResources().getColor(R.color.grey);
		rightBtn = (Button) rootLayout.findViewById(R.id.title_btn1);
		leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
		title = (TextView) rootLayout.findViewById(R.id.title);
		subTitle = (TextView) rootLayout.findViewById(R.id.sub_title);
		mProgress = (ProgressBar) rootLayout.findViewById(R.id.title_progress);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText("返回");
		title.setText("添加朋友");
		leftBtn.setTextColor(white);
		leftBtn.setOnClickListener(this);
		rightBtn.setText("查找");
		rightBtn.setTextColor(grey);
		rightBtn.setOnClickListener(this);
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setEnabled(false);
	}

	private void moreAction() {
		contact_search_account_et = (EditText) rootLayout
				.findViewById(R.id.contact_search_account_et);
		contact_search_account_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					rightBtn.setEnabled(false);
					rightBtn.setTextColor(grey);
				} else {
					rightBtn.setEnabled(true);
					rightBtn.setTextColor(white);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void main() {
		// 实例化DButil
		DBUtil dao = DBUtil.getInstance(this);
		Constants.userEbs = dao.getUserEbs();// 查询用户信息的账号密码
		if (Constants.con == null) {
			// 启动连接
			Constants.con = Communication.newInstance(this);
		}
		if (getIntent().getExtras() != null) {
			Intent intent = getIntent();
			friendid = intent.getStringExtra("friendid");
			friendname = intent.getStringExtra("friendname");
			// showDialog();
			contact_search_account_et.setText(friendname);
			// Constants.con.searchUser(friendid);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_btn4) {
			finish();
		} else if (v.getId() == R.id.title_btn1) {
			showDialog();
			String friendID = contact_search_account_et.getText().toString()
					.trim();
			if (!friendid.equalsIgnoreCase(""))
				Constants.con.searchUser(friendid);
			else
				Constants.con.searchUser(friendID);
		} else {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constants.con = null;
		unregisterReceiver(MyBroadCastReceiver);
	}

	/*
	 * 通用的提示对话框 弹出dialog
	 */
	public void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.loading, null);
		TextView tv = (TextView) view.findViewById(R.id.textview);
		tv.setText("正在查找联系人...");
		dialog = new Dialog(ContactSearchUI.this, R.style.MyDialogStyle);// 设置它的ContentView
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void dismissDialog() {
		dialog.cancel();
	}
}
