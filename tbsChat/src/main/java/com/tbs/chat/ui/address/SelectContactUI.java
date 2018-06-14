package com.tbs.chat.ui.address;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.constants.Config;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.ui.chatting.FMessageConversationUI;
import com.tbs.chat.util.BitmapUtil;
import com.tbs.chat.util.FileUtil;

public class SelectContactUI extends Activity implements OnClickListener {

	private final Context mContext = this;

	private LayoutInflater inflater;
	private LinearLayout rootLayout;
	private Intent intent;
	private View titleView;
	private View addressView;

	private ImageButton rightBtn;
	private Button leftBtn;
	private TextView title;
	private TextView subTitle;
	private ProgressBar mProgress;

	private ImageView contact_info_avatar_iv;
	private TextView contact_info_nickname_tv;
	private TextView contact_info_username_tv;
	private ImageView contact_info_sex_iv;
	private TextView district_value;
	private TextView signature_value;
	private LinearLayout picture_value;
	private Button sendBtn;
	private FriendEntity friend;
	private RelativeLayout district_root;
	private RelativeLayout signature_root;
	private RelativeLayout picture_root;

	private Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_linearlayout);
		init();
		titleView();
		addressView();
		main();
	}

	private void init() {
		/*
		 * 从intent中获得extr 如果relogin是true需要进行登录 如果是false不需要登录
		 */
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			friend = (FriendEntity) intent.getSerializableExtra("friendEntity");
		}
		/*
		 * 获得根布局 将标题布局增加到根布局中 将登录布局增加到根布局中
		 */
		rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);// 获得根布局
		inflater = getLayoutInflater();// 获得适配器
		titleView = inflater.inflate(R.layout.mm_title_webview, null);
		addressView = inflater.inflate(R.layout.address_detail_layout, null);
		rootLayout.addView(titleView);// 添加布局
		rootLayout.addView(addressView);// 添加布局
	}

	private void titleView() {
		int white = getResources().getColor(R.color.white);
		rightBtn = (ImageButton) rootLayout.findViewById(R.id.title_btn1);
		leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
		title = (TextView) rootLayout.findViewById(R.id.title);
		subTitle = (TextView) rootLayout.findViewById(R.id.sub_title);
		mProgress = (ProgressBar) rootLayout.findViewById(R.id.title_progress);
		title.setText("详细资料");
		rightBtn.setImageResource(R.drawable.mm_title_btn_menu);
		rightBtn.setVisibility(View.VISIBLE);
		leftBtn.setTextColor(white);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText("返回");
		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
	}

	private void addressView() {
		contact_info_avatar_iv = (ImageView) rootLayout
				.findViewById(R.id.contact_info_avatar_iv);
		contact_info_nickname_tv = (TextView) rootLayout
				.findViewById(R.id.contact_info_nickname_tv);
		contact_info_username_tv = (TextView) rootLayout
				.findViewById(R.id.contact_info_username_tv);
		contact_info_sex_iv = (ImageView) rootLayout
				.findViewById(R.id.contact_info_sex_iv);
		district_value = (TextView) rootLayout
				.findViewById(R.id.district_value);
		signature_value = (TextView) rootLayout
				.findViewById(R.id.signature_value);
		picture_value = (LinearLayout) rootLayout
				.findViewById(R.id.picture_value);
		sendBtn = (Button) rootLayout.findViewById(R.id.sendBtn);
		district_root = (RelativeLayout) rootLayout
				.findViewById(R.id.district_root);
		signature_root = (RelativeLayout) rootLayout
				.findViewById(R.id.signature_root);
		picture_root = (RelativeLayout) rootLayout
				.findViewById(R.id.picture_root);
		signature_root.setVisibility(View.INVISIBLE);
		picture_root.setVisibility(View.INVISIBLE);
		sendBtn.setOnClickListener(this);
		district_value.setText("中国");
		district_root.setBackgroundResource(R.drawable.preference_single_item);
	}

	private void main() {
		String headPath = friend.getHead();
		String nickName = friend.getNickName();
		String sex = friend.getSex();
		String id = friend.getFriendID();
		String dir = friend.getData2();

		// 设置头像
		if (headPath != null && !headPath.equals("")) {
			// 判断头像是否存在
			boolean fileExist = FileUtil.headFileExist(headPath);
			if (fileExist) {
				bitmap = BitmapUtil.ReadBitmapById(mContext, headPath, 60, 60);
				if (bitmap != null) {
					contact_info_avatar_iv.setImageBitmap(bitmap);// 设置头像
				}
			}
		}

		contact_info_nickname_tv.setText(nickName);

		if (sex != null && !sex.equals("")) {
			if (sex.equals("0")) {
				contact_info_sex_iv.setImageResource(R.drawable.ic_sex_male);
			} else {
				contact_info_sex_iv.setImageResource(R.drawable.ic_sex_female);
			}
		} else {
			contact_info_sex_iv.setVisibility(View.INVISIBLE);
		}

		contact_info_username_tv.setText("IMS号:" + id);

		// 设置地址
		if (dir != null && !dir.equals("")) {
			if (!dir.equals("全球")) {
				district_value.setText(dir);
			} else {
				district_value.setText("");
			}
		} else {
			district_value.setText("");
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_btn4) {
			finish();
		} else if (v.getId() == R.id.sendBtn) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(friend.getFriendID());
			Intent intent = new Intent(SelectContactUI.this,
					FMessageConversationUI.class);
			intent.putStringArrayListExtra("list", list);
			intent.putExtra("sendState", Config.MESSAGE_TYPE_IMS);
			startActivity(intent);
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
		if (bitmap != null) {
			bitmap.recycle();
		}
	}
}
