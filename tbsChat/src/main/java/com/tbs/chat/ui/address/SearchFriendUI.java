package com.tbs.chat.ui.address;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.database.table.FRIENDS_TABLE;
import com.tbs.chat.dialog.EmptyDialog;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.ui.chatting.FMessageConversationUI;
import com.tbs.chat.util.BitmapUtil;
import com.tbs.chat.util.FileUtil;

public class SearchFriendUI extends Activity implements OnClickListener {

	private static final String TAG = "SearchFriendUI";

	private final Context mContext = SearchFriendUI.this;

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
	private Bitmap bitmap = null;
	private Bundle b;
	private int white;
	private Dialog dialog;
	private DBUtil dao = null;

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
			b = intent.getExtras();
		}

		dao = DBUtil.getInstance(this);

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
		rightBtn.setVisibility(View.INVISIBLE);
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
		sendBtn.setVisibility(View.INVISIBLE);
	}

	private void main() {
		// 实例化好友实体
		FriendEntity friendC = new FriendEntity();
		// 从bundle中取friend
		friendC = (FriendEntity) b.getSerializable("friend");
		// 获取userid，friendid
		String userId = friendC.getUserID();
		String friendId = friendC.getFriendID();
		String nickName = friendC.getNickName();
		String sex = friendC.getSex();
		String phone = friendC.getPhone();
		String dir = friendC.getData2();
		String countryCode = friendC.getCountryCode();
		String modifyTime = friendC.getModifyTime();
		String head = friendC.getHead();

		Log.d(TAG, "userId" + userId);
		Log.d(TAG, "friendId" + friendId);
		Log.d(TAG, "nickName" + nickName);
		Log.d(TAG, "modifyTime" + modifyTime);
		Log.d(TAG, "head" + head);

		// //查询用户
		// UserEntity user = dao.queryUser(mContext, USER_TABLE.TABLE_NAME,
		// ""+friendId);
		// //判断用户状态，显示不同界面
		// if(user != null){
		// rightBtn.setVisibility(View.INVISIBLE);
		// sendBtn.setVisibility(View.INVISIBLE);
		// }else{
		FriendEntity friendx = dao.queryUser(mContext, FRIENDS_TABLE.TABLE_NAME,
				"" + userId, "" + friendId);
		if (friendx != null) {
			rightBtn.setVisibility(View.VISIBLE);
			sendBtn.setVisibility(View.VISIBLE);
			sendBtn.setText("发消息");
			sendBtn.setOnClickListener(mOnclick);
		} else {
			rightBtn.setVisibility(View.VISIBLE);
			sendBtn.setVisibility(View.VISIBLE);
			sendBtn.setText("添加好友到通讯录");
			sendBtn.setOnClickListener(this);
		}

		// 设置头像
		if (head != null && !head.equals("")) {
			// 判断头像是否存在
			boolean fileExist = FileUtil.headFileExist(head);
			if (fileExist) {
				bitmap = BitmapUtil.ReadBitmapById(mContext, head, 60, 60);
				if (bitmap != null) {
					contact_info_avatar_iv.setImageBitmap(bitmap);// 设置头像
				}
			}
		}

		contact_info_nickname_tv.setText(nickName);

		// 设置性别
		if (sex != null && !sex.equals("")) {
			if (sex.equals("0")) {
				contact_info_sex_iv.setImageResource(R.drawable.ic_sex_male);
			} else {
				contact_info_sex_iv.setImageResource(R.drawable.ic_sex_female);
			}
		} else {
			contact_info_sex_iv.setVisibility(View.INVISIBLE);
		}

		contact_info_username_tv.setText("IMS号:" + friendId);

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

		// 转存好友信息
		friend = new FriendEntity();
		friend.setUserID(userId);
		friend.setFriendID(friendId);
		friend.setNickName(nickName);
		friend.setSex(sex);
		friend.setPhone(phone);
		friend.setData2(dir);
		friend.setCountryCode(countryCode);
		friend.setModifyTime(modifyTime);
		friend.setHead(head);
	}

	OnClickListener mOnclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(friend.getFriendID());
			Intent intent = new Intent(SearchFriendUI.this,
					FMessageConversationUI.class);
			intent.putStringArrayListExtra("list", list);
			startActivity(intent);
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_btn4) {
			finish();
		} else if (v.getId() == R.id.sendBtn) {
			MyAsyncTask mTask = new MyAsyncTask(this);
			mTask.execute();
		} else {
		}
	}

	/*
	 * 通用的提示对话框 弹出dialog
	 */
	public void showDialog() {
		View view = getLayoutInflater().inflate(R.layout.loading, null);
		TextView tv = (TextView) view.findViewById(R.id.textview);
		tv.setText("正在添加联系人...");
		dialog = new Dialog(SearchFriendUI.this, R.style.MyDialogStyle);// 设置它的ContentView
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void dismissDialog() {
		dialog.cancel();
	}

	/*
	 * 异步任务 后台任务前 后台任务中 后台任务结束
	 */
	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		private static final String TAG = "MyAsyncTask";
		private Context context;
		private boolean result;

		public MyAsyncTask(Context c) {
			this.context = c;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
			showDialog();
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected Integer doInBackground(Void... params) {
			// 添加好友请求
			result = Constants.con.addFriend("" + friend.getUserID(), ""
					+ friend.getFriendID());
			if (result == true) {
				ContentValues values = new ContentValues();
				values.put(FRIENDS_TABLE.SELF_ID, friend.getUserID());
				values.put(FRIENDS_TABLE.FRIEND_ID, friend.getFriendID());
				values.put(FRIENDS_TABLE.NICK, friend.getNickName());
				values.put(FRIENDS_TABLE.SEX, friend.getSex());
				values.put(FRIENDS_TABLE.HEAD, friend.getHead());
				values.put(FRIENDS_TABLE.COUNTRY_CODE, friend.getCountryCode());
				values.put(FRIENDS_TABLE.PHONE, friend.getPhone());
				values.put(FRIENDS_TABLE.MODIFY_TIME, friend.getModifyTime());
				values.put(FRIENDS_TABLE.DATA2, friend.getData2());
				dao.insertFriend(context, values);
			}
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			dismissDialog();
			if (result == true) {
//				Intent intent = new Intent(Constants.REFRESH_ADDRESS);
//				context.sendBroadcast(intent);
				// 更新按钮为发送消息
				sendBtn.setText("发消息");
				sendBtn.setOnClickListener(mOnclick);
			} else {
				Intent openDialog = new Intent(SearchFriendUI.this,
						EmptyDialog.class);
				openDialog.putExtra("title", "提示");
				openDialog.putExtra("content", "添加用户失败");
				startActivity(openDialog);
			}
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {
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
