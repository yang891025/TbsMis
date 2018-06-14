package com.tbs.chat.ui.chatting;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.adapter.ChattingAdapter;
import com.tbs.chat.adapter.PanelAdapter;
import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.dialog.BackCurrnetActivity;
import com.tbs.chat.dialog.SmsDialog;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.menu.ChatPanelMenu;
import com.tbs.chat.receiver.SMSSuccessReceiver;
import com.tbs.chat.socket.Communication;
import com.tbs.chat.util.AudioRecorder;
import com.tbs.chat.util.FileUtil;
import com.tbs.chat.util.TimeUtil;
import com.tbs.chat.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FMessageConversationUI extends Activity implements
		OnClickListener, OnTouchListener {

	private static final String TAG = "FMessageConversationUI";

	private final Context context = this;

	private static int MAX_TIME = 0; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1
	private static int RECORD_NO = 0; // 不在录音
	private static int RECORD_ING = 1; // 正在录音
	private static int RECODE_ED = 2; // 完成录音
	private static int RECODE_DEL = 3; // 录音时长太短
	private static int RECODE_STATE = 0; // 录音的状态
	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值

	private BroadcastReceiver MyBroadCastReceiver;// 广播
	private ArrayList<String> list = new ArrayList<String>();
	private List<MessageEntity> messages = new ArrayList<MessageEntity>();
	private Intent intent;
	private LayoutInflater inflater;
	private LinearLayout rootLayout;
	private View titleView;
	private View chatView;
	private ImageButton rightBtn;
	private Button leftBtn;
	private TextView title;
	private TextView subTitle;
	private ProgressBar mProgress;
	private ImageButton chatting_mode_btn;
	private ImageButton chatting_attach_btn;
	private ImageButton chatting_smiley_btn;
	private EditText chatting_content_et;
	private LinearLayout chatting_mode_switcher;
	private TextView chatting_wordcount_tv;
	private Button chatting_send_btn;
	private Button voice_record_bt;
	private FrameLayout chatting_bottom_panel;
	private LinearLayout text_panel_ll;
	private ListView chatting_history_lv;
	private GridView app_panel_grid;

	private int sendState = 0;// 0是短信，1是语音，2是图片，3是
	private ChattingAdapter adapter;
	private int index = 0;

	private Handler mHandler = new Handler();
	private Thread recordThread;
	private AudioRecorder recorder;// 录音者
	private ImageView voice_rcd_hint_anim;
	private RelativeLayout voice_rcd_hint_anim_area;
	private RelativeLayout voice_rcd_hint_cancel_area;
	private LinearLayout voice_rcd_hint_loading;
	private LinearLayout voice_rcd_hint_tooshort;
	private FrameLayout voice_rcd_hint_rcding;

	private boolean isRecoder;
	private Uri fileUri;
	private String tempPath;
	private int sendContent;
	private String sendStr;
	private DBUtil dao = null;

	private int startApp = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_linearlayout);
		start();
		titleView();
		popwindow();
		chatView();
		main();
	}

	private void start() {
		/*
		 * 从intent中获得extr 如果relogin是true需要进行登录 如果是false不需要登录
		 */
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			list = intent.getStringArrayListExtra("list");
			sendContent = intent.getIntExtra("sendState", 0);
			startApp = intent.getIntExtra("startApp", 0);
		}

		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
		filter.addAction(Constants.LOGIN_RESOULT);
		filter.addAction(Constants.REFRESH_MESSAGE);
		filter.addAction(Constants.REFRESH_MESSAGE_DETAIL);
		filter.addAction(Constants.UPDATE_FRIENDNIKE);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				} else if (Constants.REFRESH_MESSAGE_DETAIL.equals(intent
						.getAction())) {// 刷新界面
					Bundle b = intent.getExtras();
					MessageEntity msg = (MessageEntity) b
							.getSerializable("chatMessage");
					for (int i = 0; i < list.size(); i++) {		
						if (list.get(i).equalsIgnoreCase(msg.getFriend())) {
							messages.add(msg);
						}
					}
					adapter.notifyDataSetChanged();
					chatting_history_lv.setSelection(messages.size() - 1);
				} else if (Constants.REFRESH_MESSAGE.equals(intent.getAction())) {// 刷新界面
                    adapter.notifyDataSetChanged();
                    chatting_history_lv.setSelection(messages.size() - 1);
                    index++;
				} else if (Constants.UPDATE_FRIENDNIKE.equals(intent
						.getAction())) {
					title.setText(intent.getStringExtra("friendNike "));
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);

		// 实例化数据库工具类
		dao = DBUtil.getInstance(this);

		if (Constants.con == null) {
			// 启动连接
			Constants.con = Communication.newInstance(context);
		}

		/**
		 * 获得根布局 将标题布局增加到根布局中 将登录布局增加到根布局中
		 */
		rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);// 获得根布局
		inflater = getLayoutInflater();// 获得适配器
		titleView = inflater.inflate(R.layout.mm_title_webview, null);
		chatView = inflater.inflate(R.layout.chatting, null);
		rootLayout.addView(titleView);// 添加布局
		rootLayout.addView(chatView);// 添加布局
	}

	private void titleView() {
		// 白颜色
		int white = getResources().getColor(R.color.white);
		// 控件
		rightBtn = (ImageButton) rootLayout.findViewById(R.id.title_btn1);
		leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
		title = (TextView) rootLayout.findViewById(R.id.title);
		subTitle = (TextView) rootLayout.findViewById(R.id.sub_title);
		mProgress = (ProgressBar) rootLayout.findViewById(R.id.title_progress);

		if (list != null) {
			if (list.size() > 1) {// 处理群聊内容
				title.setText("群聊(" + list.size() + ")");
			} else {// 处理单条数据
				FriendEntity friend = dao.getFriend(this, list.get(0));
				if (friend != null) {// 当friend是null的时候通过网络查询用户名称
					title.setText(friend.getNickName());
				} else {
					Constants.con.getFriendsInfo("" + list.get(0));
				}
			}
		}

		// rightBtn.setImageResource(R.drawable.mm_title_btn_contact_normal);
		// rightBtn.setVisibility(View.VISIBLE);
		leftBtn.setTextColor(white);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText("返回");
		leftBtn.setOnClickListener(this);
	}

	private void popwindow() {
		recorder = AudioRecorder.getInstance(); // 实例化录音类
		voice_rcd_hint_rcding = (FrameLayout) rootLayout
				.findViewById(R.id.voice_rcd_hint_rcding);// 录制界面的根布局
		voice_rcd_hint_anim = (ImageView) rootLayout
				.findViewById(R.id.voice_rcd_hint_anim);// 录制的图片
		voice_rcd_hint_anim_area = (RelativeLayout) rootLayout
				.findViewById(R.id.voice_rcd_hint_anim_area);// 录制界面
		voice_rcd_hint_cancel_area = (RelativeLayout) rootLayout
				.findViewById(R.id.voice_rcd_hint_cancel_area);// 取消录制
		voice_rcd_hint_loading = (LinearLayout) rootLayout
				.findViewById(R.id.voice_rcd_hint_loading);// 加载中
		voice_rcd_hint_tooshort = (LinearLayout) rootLayout
				.findViewById(R.id.voice_rcd_hint_tooshort);// 录制时间太短

		voice_rcd_hint_rcding.setVisibility(View.GONE);
		voice_rcd_hint_loading.setVisibility(View.GONE);
		voice_rcd_hint_tooshort.setVisibility(View.GONE);
	}

	private void chatView() {
		chatting_mode_btn = (ImageButton) rootLayout
				.findViewById(R.id.chatting_mode_btn);
		chatting_attach_btn = (ImageButton) rootLayout
				.findViewById(R.id.chatting_attach_btn);
		chatting_smiley_btn = (ImageButton) rootLayout
				.findViewById(R.id.chatting_smiley_btn);
		chatting_content_et = (EditText) rootLayout
				.findViewById(R.id.chatting_content_et);
		chatting_mode_switcher = (LinearLayout) rootLayout
				.findViewById(R.id.chatting_mode_switcher);
		chatting_wordcount_tv = (TextView) rootLayout
				.findViewById(R.id.chatting_wordcount_tv);
		chatting_send_btn = (Button) rootLayout
				.findViewById(R.id.chatting_send_btn);
		voice_record_bt = (Button) rootLayout
				.findViewById(R.id.voice_record_bt);
		chatting_bottom_panel = (FrameLayout) rootLayout
				.findViewById(R.id.chatting_bottom_panel);
		text_panel_ll = (LinearLayout) rootLayout
				.findViewById(R.id.text_panel_ll);
		chatting_history_lv = (ListView) rootLayout
				.findViewById(R.id.chatting_history_lv);
		app_panel_grid = (GridView) rootLayout
				.findViewById(R.id.app_panel_grid);// 输入栏仪表盘
		voice_record_bt.setVisibility(View.GONE);
		chatting_attach_btn.setOnClickListener(this);
		chatting_mode_btn.setOnClickListener(this);
		chatting_send_btn.setOnClickListener(this);
		voice_record_bt.setOnTouchListener(this);

		/*
		 * 此处需要判断当前是sms状态还是ims状态 如果是sms状态需要屏蔽录音盒键盘切换按钮
		 */
		if (sendContent == Config.MESSAGE_TYPE_SMS) {
			chatting_mode_btn.setVisibility(View.GONE);
			chatting_attach_btn.setVisibility(View.GONE);
		}

		/*
		 * 输入点击变化
		 */
		chatting_content_et.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				chatting_bottom_panel.setVisibility(View.GONE);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						Util.openInput(chatting_content_et, context);
					}
				}, 200);
			}
		});

		/*
		 * 输入框焦点变化
		 */
		chatting_content_et
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							chatting_bottom_panel.setVisibility(View.GONE);
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									Util.openInput(chatting_content_et, context);
								}
							}, 200);
						} else {
							mHandler.postDelayed(new Runnable() {
								@Override
								public void run() {
									Util.closeInput(chatting_content_et,
											context);
								}
							}, 200);
						}

					}
				});

		chatting_content_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
					chatting_attach_btn.setVisibility(View.VISIBLE);
					chatting_send_btn.setVisibility(View.GONE);
				} else {
					chatting_attach_btn.setVisibility(View.GONE);
					chatting_send_btn.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		app_panel_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 1:
					chatting_bottom_panel.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(context,
									ChatPanelMenu.class);
							startActivityForResult(intent, 100);
						}
					}, 200);
					break;

				default:
					break;
				}
			}
		});
	}

	private void main() {
		if (list.size() == 1) {
			messages = dao.getMessage2(this, list.get(0));
		}
		index = messages.size();
		/*
		 * 此处需要判断当前是sms状态还是ims状态 如果是sms状态需要查询短信内容 如果是ims状态需要查询ims内容
		 */
		adapter = new ChattingAdapter(this, this, dao, recorder, messages);
		chatting_history_lv.setAdapter(adapter);
		chatting_history_lv.setSelection(messages.size() - 1);

		PanelAdapter pAdapter = new PanelAdapter(this);
		app_panel_grid.setAdapter(pAdapter);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.chatting_mode_btn) {
			switch (sendState) {
			case 0:
				Util.closeInput(chatting_content_et, this);
				chatting_mode_btn
						.setImageResource(R.drawable.chatting_setmode_keyboard_btn);
				text_panel_ll.setVisibility(View.GONE);
				voice_record_bt.setVisibility(View.VISIBLE);
				sendState = 1;
				break;

			case 1:
				chatting_content_et.requestFocus();
				chatting_mode_btn
						.setImageResource(R.drawable.chatting_setmode_voice_btn);
				text_panel_ll.setVisibility(View.VISIBLE);
				voice_record_bt.setVisibility(View.GONE);
				sendState = 0;
				break;

			default:
				break;
			}
			chatting_bottom_panel.setVisibility(View.GONE);
		} else if (v.getId() == R.id.title_btn4) {
			if (startApp == 1) {
				Intent intent = new Intent();
				intent.setClass(context, BackCurrnetActivity.class);
				startActivityForResult(intent, Config.BACK_CURRENT_ACTIVITY);
			} else {
				finish();
			}
		} else if (v.getId() == R.id.chatting_send_btn) {
			int config = -1;
			String str = chatting_content_et.getText().toString().trim();
			if (str != null
					&& (sendStr = str.replaceAll("\r", "").replaceAll("\t", "")
							.replaceAll("\n", "").replaceAll("\f", "")) != "") {
				// 此处需要判断当前是sms状态还是ims状态
				if (sendContent == Config.MESSAGE_TYPE_SMS) {
					config = Config.MESSAGE_TYPE_SMS;
					MyAsyncTask task = new MyAsyncTask(context, sendStr, "",
							config);
					task.execute();
				} else {
					// 判断socket连接状态如果连接中通过网络发送
					if (Constants.con.isConnection()) {
						config = Config.MESSAGE_TYPE_TXT;
						MyAsyncTask task = new MyAsyncTask(context, sendStr,
								"", config);
						task.execute();
					} else {// 如果socket是连接异常的话，提示是否通过短信发送信息
						config = Config.MESSAGE_TYPE_SMS;
						Intent openDialog = new Intent(context, SmsDialog.class);
						openDialog.putExtra("title", "提示");
						openDialog.putExtra("content", "网络状态不稳定,是否通过短信发送消息？");
						startActivityForResult(openDialog, config);
					}
				}
			}
			chatting_content_et.setText("");
			adapter.notifyDataSetChanged();
		} else if (v.getId() == R.id.chatting_attach_btn) {
			Util.closeInput(chatting_content_et, this);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_voice_btn);
					chatting_bottom_panel.setVisibility(View.VISIBLE);
					text_panel_ll.setVisibility(View.VISIBLE);
					voice_record_bt.setVisibility(View.GONE);
					sendState = 0;
				}
			}, 200);
		} else {
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int action = event.getAction();
		float lastLocationX = 0;
		float lastLocationY = 0;
		int height = voice_record_bt.getHeight();
		int width = voice_record_bt.getWidth();

		if (action == MotionEvent.ACTION_DOWN) {

			voice_record_bt
					.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
			voice_record_bt.setText(getResources().getString(
					R.string.chatfooter_releasetofinish));
			// voice_rcd_hint_rcding.setVisibility(View.GONE);
			// voice_rcd_hint_loading.setVisibility(View.VISIBLE);
			// voice_rcd_hint_tooshort.setVisibility(View.GONE);
			if (RECODE_STATE != RECORD_ING) {
				String userID = "" + Constants.userEbs.getUserId();
				isRecoder = recorder.startRecord(userID);
				if (isRecoder) {
					mythread();
					RECODE_STATE = RECORD_ING;
					// mHandler.postDelayed(new Runnable() {
					// public void run() {
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
					voice_rcd_hint_anim_area.setVisibility(View.VISIBLE);
					// }
					// }, 0);
				}
			}
		} else if (action == MotionEvent.ACTION_MOVE) {
			lastLocationX = event.getX();
			lastLocationY = event.getY();

			if (lastLocationX > width || lastLocationX < 0 || lastLocationY < 0
					|| lastLocationY > height) {
				voice_rcd_hint_loading.setVisibility(View.GONE);
				voice_rcd_hint_anim_area.setVisibility(View.GONE);
				voice_rcd_hint_cancel_area.setVisibility(View.VISIBLE);
				RECODE_STATE = RECODE_DEL;
			} else {
				voice_rcd_hint_loading.setVisibility(View.GONE);
				voice_rcd_hint_anim_area.setVisibility(View.VISIBLE);
				voice_rcd_hint_cancel_area.setVisibility(View.GONE);
				RECODE_STATE = RECORD_ING;
			}

		} else if (action == MotionEvent.ACTION_UP) {
			if (RECODE_STATE == RECORD_ING) {
				RECODE_STATE = RECODE_ED;
				// mHandler.postDelayed(new Runnable() {
				// public void run() {
				isRecoder = recorder.stopRecord();
				Log.d(TAG, "recorder isStop...");
				// }
				// }, 200);
				voiceValue = 0.0;
				if (recodeTime < MIX_TIME) {
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
					RECODE_STATE = RECORD_NO;
					if (!isRecoder) {
						recorder.delRecord();
					}
				} else {
					Log.d(TAG, "recorder isSending...");

					String recordPath = recorder.getFileName();

					File file = new File(recordPath);

					Log.d(TAG, "recorder length:" + file.length());
					Log.d(TAG, "recorder Time:" + recodeTime);

					if (!isRecoder) {
						recodeTime = Util.getFloatToInt(recodeTime);
						MyAsyncTask reTask = new MyAsyncTask(context,
								recordPath, "" + (int) recodeTime,
								Config.MESSAGE_TYPE_AUDIO);
						reTask.execute();
					}
					Log.d(TAG, "recorder isSend...");
				}
			} else if (RECODE_STATE == RECODE_DEL && isRecoder) {
				RECODE_STATE = RECODE_ED;
				// mHandler.postDelayed(new Runnable() {
				// public void run() {
				isRecoder = recorder.stopRecord();
				if (!isRecoder) {
					recorder.delRecord();
				}
				// }
				// }, 0);
			}
			voice_record_bt.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
			voice_record_bt.setText(getResources().getString(
					R.string.chatfooter_presstorcd));

			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_anim_area.setVisibility(View.GONE);
					voice_rcd_hint_cancel_area.setVisibility(View.GONE);
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
				}
			}, 0);
		}
		// 刷新界面
		adapter.notifyDataSetChanged();
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Intent intent = new Intent(Constants.HIDE_MESSAGE_NOTIFY);
		// intent.putExtra("friendID", ""+list.get(0));
		intent.putStringArrayListExtra("friendID", list);
		sendBroadcast(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(MyBroadCastReceiver);
		if (recorder != null) {
			recorder.cleanAudio();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (chatting_bottom_panel.getVisibility() == View.VISIBLE) {
				chatting_bottom_panel.setVisibility(View.GONE);
			} else {
				if (startApp == 1) {
					Intent intent = new Intent();
					intent.setClass(context, BackCurrnetActivity.class);
					startActivityForResult(intent, Config.BACK_CURRENT_ACTIVITY);
				} else {
					finish();
				}
				// onBackPressed();
			}
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	/*
	 * activity返回值 首先接收menu返回的值 然后接收头像的返回值 如果返回值是result_ok 判断requestCode
	 * 是100为menu请求支援 接收position变量 判断需要进行的操作
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Log.d(TAG, "requestCode:" + requestCode);
			switch (requestCode) {
			case 100:
				int position = data.getIntExtra("position", 0);
				switch (position) {
				case 0:
					Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
					File fileTemp = FileUtil
							.createFile(TimeUtil.getAbsoluteTime(),
									Config.MESSAGE_TYPE_IMG);
					fileUri = Uri.fromFile(fileTemp);
					it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
					break;

				case 1:
					// 跳转到图片浏览器的应用，选取要发送的图片
					Intent i = new Intent();
					i.setType("image/*");
					i.putExtra("return-data", true);
					i.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(i, Activity.DEFAULT_KEYS_SHORTCUT);
					break;

				default:
					break;
				}
				break;

			case Activity.DEFAULT_KEYS_SHORTCUT:
				Uri uri = data.getData();
				File fileTemp = FileUtil.createFile(TimeUtil.getAbsoluteTime(),
						Config.MESSAGE_TYPE_IMG);
				// File fileTemp =
				// FileUtil.createTempHeadFile(TimeUtil.getAbsoluteTime());
				tempPath = fileTemp.getAbsolutePath();
				boolean result = FileUtil.writeFile(getContentResolver(),
						fileTemp, uri);
				if (result) {
					MyAsyncTask task = new MyAsyncTask(this, tempPath, "",
							Config.MESSAGE_TYPE_IMG);
					task.execute();
				}
				break;

			case Activity.DEFAULT_KEYS_DIALER:
				tempPath = fileUri.getPath();
				MyAsyncTask task = new MyAsyncTask(this, tempPath, "",
						Config.MESSAGE_TYPE_IMG);
				task.execute();
				break;

			case Config.MESSAGE_TYPE_SMS:
				MyAsyncTask smsTask = new MyAsyncTask(this, sendStr, "",
						Config.MESSAGE_TYPE_SMS);
				smsTask.execute();
				break;

			case Config.BACK_CURRENT_ACTIVITY:
				finish();
				overridePendingTransition(R.anim.push_down_out,
						R.anim.push_empty_out);
				// 目前提交订单后直接打开MCS软件
				// Intent intent=new Intent();
				// ComponentName comp = new ComponentName("com.tbs.tbseshop",
				// "com.tbs.tbseshop.ui.conversation.MainTab");//包名 包名+类名（全路径）
				// intent.setComponent(comp);
				// intent.setAction(Intent.ACTION_MAIN);
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);
				break;

			default:
				break;
			}
		}
	}

	/*
	 * 异步任务 后台任务前 后台任务中 后台任务结束
	 */
	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {

		private Context context;
		private String value;
		private boolean resoult;
		private int config;
		private String audioTime;

		public MyAsyncTask(Context c, String value, String audioTime, int config) {
			this.context = c;
			this.value = value;
			this.config = config;
			this.audioTime = audioTime;
		}

		// 运行在UI线程中，在调用doInBackground()之前执行
		@Override
		protected void onPreExecute() {
			addContent(context, config, list, value, 0, "", "", "", "",
					audioTime);
		}

		// 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		@Override
		protected Integer doInBackground(Void... params) {
			resoult = sendChatMessage(context, config, list, value, audioTime,
					0);
			return null;
		}

		// 运行在ui线程中，在doInBackground()执行完毕后执行
		@Override
		protected void onPostExecute(Integer integer) {
			Log.d(TAG, "resoult 1");
			if (resoult) {
				Log.d(TAG, "resoult :" + resoult);
				messages.get(index).setRead_type(1);
			} else {
				Log.d(TAG, "resoult :" + resoult);
				messages.get(index).setRead_type(2);
			}
			Intent intent = new Intent(Constants.REFRESH_MESSAGE);
			sendBroadcast(intent);
		}

		// 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	private long saveMessageToDb(String userID, String friendID, int direction,
			int type, String time, String content, String audioTime,
			int read_type) {
		ContentValues values = new ContentValues();
		values.put("self_id", userID);
		values.put("friend_id", friendID);
		values.put("direction", direction);
		values.put("type", type);
		values.put("time", time);
		values.put("content", content);
		values.put("read_type", read_type);
		values.put("data5", audioTime);
		long resoult = dao.insertMessage(context, values);
		return resoult;
	}

	/*
	 * 添加内容到集合中去
	 */
	public void addContent(Context context, int type, ArrayList<String> list,
			String content, int read_type, String data1, String data2,
			String data3, String data4, String data5) {
		// 查询好友
		FriendEntity friend = dao.getFriend(context, list.get(0));
		// 获取时间
		String time = TimeUtil.getAbsoluteTime();
		// 增加消息数量
		messages.add(new MessageEntity(friend.getUserID(),
				friend.getFriendID(), Config.MESSAGE_TO, type, time, content,
				read_type, data1, data2, data3, data4, data5));
		// 刷新适配器
		adapter.notifyDataSetChanged();
	}

	/*
	 * 刷新集合中的内容
	 */
	public void updateContent(Context context, int type,
			ArrayList<String> list, String content, int read_type,
			String data1, String data2, String data3, String data4, String data5) {
		FriendEntity friend = dao.getFriend(context, list.get(0));
		String time = TimeUtil.getAbsoluteTime();
		messages.add(new MessageEntity(friend.getUserID(),
				friend.getFriendID(), Config.MESSAGE_TO, type, time, content,
				read_type, data1, data2, data3, data4, data5));
		// 刷新适配器
		adapter.notifyDataSetChanged();
	}

	/*
	 * 发送消息
	 */
	private boolean sendChatMessage(Context context, int type,
			ArrayList<String> list, String content, String audioTime,
			int read_type) {
		boolean result = true;
		String time = TimeUtil.getAbsoluteTime();
		for (int i = 0; i < list.size(); i++) {

			FriendEntity friend = dao.getFriend(context, list.get(i));
			String userID = String.valueOf(friend.getUserID());
			String friendID = String.valueOf(friend.getFriendID());
			String phone = friend.getPhone();

			// 插入数据
			long insertValue = saveMessageToDb(userID, friendID,
					Config.MESSAGE_TO, type, time, content, audioTime,
					read_type);// 将发送的短信插入
			Log.d(TAG, "sendChatMessage 1");
			// 判断消息类型
			if (type == Config.MESSAGE_TYPE_TXT) {
				Log.d(TAG, "sendChatMessage 2");
				result = Constants.con
						.sendText(userID, friendID, time, content);
				Log.d(TAG, "sendChatMessage 3");
			} else if (type == Config.MESSAGE_TYPE_AUDIO) {
				result = Constants.con.sendAudio(userID, friendID, time,
						content, audioTime);
			} else if (type == Config.MESSAGE_TYPE_IMG) {
				result = Constants.con.sendImg(userID, friendID, time, content);
			} else if (type == Config.MESSAGE_TYPE_SMS) {
				// 获得sms管理者
				SmsManager smsManager = SmsManager.getDefault();
				// 定义action
				String sentSmsAction = Constants.SENT_SMS_ACTION + insertValue
						+ "," + 0;
				String deliveredSmsAction = Constants.DELIVERED_SMS_ACTION
						+ insertValue + "," + 0;
				// 注册receiver
				SMSSuccessReceiver receiver = new SMSSuccessReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(sentSmsAction);
				filter.addAction(deliveredSmsAction);
				filter.setPriority(Integer.MAX_VALUE);
				context.registerReceiver(receiver, filter);
				// create the sentIntent parameter
				Intent sentIntent = new Intent(sentSmsAction);
				PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
						sentIntent, 0);
				// create the deilverIntent parameter
				Intent deliverIntent = new Intent(deliveredSmsAction);
				PendingIntent deliverPI = PendingIntent.getBroadcast(context,
						0, deliverIntent, 0);
				// 判断短信长度是否大于70个字，如果大于70分多条短信发送
				if (content.length() > 70) {
					// 截取短信息
					List<String> contents = smsManager.divideMessage(content);
					// 循环发送短信
					for (String sms : contents) {
						// 发送短信
						smsManager.sendTextMessage(phone.trim(), null, sms,
								sentPI, deliverPI);
					}
				} else {
					// 发送一条短信
					smsManager.sendTextMessage(phone.trim(), null, content,
							sentPI, deliverPI);
				}
				result = true;
			}
			Log.d(TAG, "sendChatMessage 4");
			if (type != Config.MESSAGE_TYPE_SMS) {
				if (result == true) {
					// 发送成功
					dao.updateMessage(context, userID, friendID,
							Config.MESSAGE_TO, type, time, content, 1);
				} else {
					// 发送失败
					dao.updateMessage(context, userID, friendID,
							Config.MESSAGE_TO, type, time, content, 2);
				}
				dao.updateFriend(context, userID, friendID, Config.MESSAGE_TO,
						type, time, content, 1);
			}
			Log.d(TAG, "sendChatMessage 5");
		}
		return result;
	}

	/*
	 * 录音计时线程
	 */
	void mythread() {
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
		if (voiceValue < 200.0) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp1);
		} else if (voiceValue > 200.0 && voiceValue < 400) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp2);
		} else if (voiceValue > 400.0 && voiceValue < 800) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp2);
		} else if (voiceValue > 800.0 && voiceValue < 1600) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp4);
		} else if (voiceValue > 1600.0 && voiceValue < 3200) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp5);
		} else if (voiceValue > 3200.0 && voiceValue < 5000) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp6);
		} else if (voiceValue > 5000.0) {
			voice_rcd_hint_anim.setImageResource(R.drawable.amp7);
		}
	}

	// 录音线程
	private Runnable ImgThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING) {
				if (recodeTime >= MAX_TIME && MAX_TIME != 0) {
					imgHandle.sendEmptyMessage(0);
				} else {
					try {
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING) {
							voiceValue = recorder.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		Handler imgHandle = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 0:
					if (RECODE_STATE == RECORD_ING) {

						RECODE_STATE = RECODE_ED;

						// mHandler.postDelayed(new Runnable() {
						// public void run() {
						isRecoder = recorder.stopRecord();
						// }
						// }, 200);

						voiceValue = 0.0;

						if (recodeTime < MIX_TIME) {

							voice_rcd_hint_loading.setVisibility(View.GONE);
							voice_rcd_hint_rcding.setVisibility(View.GONE);
							voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);

							RECODE_STATE = RECORD_NO;

							if (!isRecoder) {
								recorder.delRecord();
							}
						} else {
							Log.d(TAG,
									"ACTION_UP USERID:"
											+ Constants.userEbs.getUserId());
							if (!isRecoder) {
								recodeTime = Util.getFloatToInt(recodeTime);
								MyAsyncTask reTask = new MyAsyncTask(context,
										recorder.getFileName(), ""
												+ (int) recodeTime,
										Config.MESSAGE_TYPE_AUDIO);
								reTask.execute();
							}
						}
					} else if (RECODE_STATE == RECODE_DEL && isRecoder) {

						RECODE_STATE = RECODE_ED;

						// mHandler.postDelayed(new Runnable() {
						// public void run() {
						isRecoder = recorder.stopRecord();
						if (!isRecoder) {
							recorder.delRecord();
						}
						// }
						// }, 0);
					}

					voice_record_bt
							.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
					voice_record_bt.setText(getResources().getString(
							R.string.chatfooter_presstorcd));

					// mHandler.postDelayed(new Runnable() {
					// public void run() {
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_anim_area.setVisibility(View.GONE);
					voice_rcd_hint_cancel_area.setVisibility(View.GONE);
					voice_rcd_hint_loading.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					// }
					// }, 0);
					break;

				case 1:
					setDialogImage();
					break;

				default:
					break;
				}
			}
		};
	};
}
