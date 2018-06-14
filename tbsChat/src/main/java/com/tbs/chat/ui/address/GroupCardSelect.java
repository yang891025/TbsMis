package com.tbs.chat.ui.address;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.ui.chatting.FMessageConversationUI;
import com.tbs.chat.util.BitmapUtil;
import com.tbs.chat.util.FileUtil;
import com.tbs.chat.util.Util;
import com.tbs.chat.wight.SideBar;

public class GroupCardSelect extends Activity implements OnClickListener{

	protected static final String TAG = "SelectContactUI";
	private final Context mContext = this;
	
	private HashMap<Integer, Boolean> isCheck = new HashMap<Integer, Boolean>();
	
	private BroadcastReceiver MyBroadCastReceiver;//广播
	private WindowManager mWindowManager;
	private Intent intent;
	private LayoutInflater inflater;
	private LinearLayout rootLayout;
	private View titleView;
	private View addressView;
	private View toastView;
	private ImageButton rightBtn;
	private Button leftBtn;
	private TextView title;
	private TextView subTitle;
	private ProgressBar mProgress;
	private LinearLayout searchRoot;
	private EditText searchEdit;
	private ImageButton cleanBtn;
	private Button searchBtn;
	private ListView search_chat_content_lv;
	private TextView empty_voicesearch_tip_tv;
	private TextView empty_blacklist_tip_tv;
	private SideBar sideBar;
	private LinearLayout address_selected_contact_area;
	private LinearLayout address_selectd_avatar_ll;
	private ImageView dot_avatar;
	private Button address_select_finish_btn;
	private SelectContactAdapter adapter;
	private TextView mDialogText;
	private int searchState = 0;
	private ArrayList<FriendEntity> friendList;
	private View myView;
	private int sendState;
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
		 *  从intent中获得extr
		 *  如果relogin是true需要进行登录
		 *  如果是false不需要登录
		 */
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			sendState = intent.getIntExtra("sendState", 0);
		}
		
		dao = DBUtil.getInstance(mContext);
		
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.EXIT_ACTIVITY);
		filter.addCategory("default");
		MyBroadCastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
					finish();
				}
			}
		};
		registerReceiver(MyBroadCastReceiver, filter);
		
		/*
		 * 获得根布局
		 * 将标题布局增加到根布局中
		 * 将登录布局增加到根布局中
		 * 启动activity时不自动弹出软键盘
		 */
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);//获得根布局
		inflater = getLayoutInflater();//获得适配器
		titleView = inflater.inflate(R.layout.mm_title, null);
		addressView = inflater.inflate(R.layout.address_chat, null);
		toastView = inflater.inflate(R.layout.address_dialog, null);
		rootLayout.addView(titleView);//添加布局
		rootLayout.addView(addressView);//添加布局
		mDialogText = (TextView) toastView.findViewById(R.id.textview);//找到listview
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.TYPE_APPLICATION,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
		mWindowManager.addView(toastView, lp);
	}
	
	
	private void titleView() {
		int white = getResources().getColor(R.color.white);
		rightBtn = (ImageButton) rootLayout.findViewById(R.id.title_btn1);
		leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
		title = (TextView) rootLayout.findViewById(R.id.title);
		subTitle = (TextView) rootLayout.findViewById(R.id.sub_title);
		mProgress = (ProgressBar) rootLayout.findViewById(R.id.title_progress);
		rightBtn.setVisibility(View.INVISIBLE);
		leftBtn.setVisibility(View.VISIBLE);
		title.setText("选择联系人");
		leftBtn.setText("取消");
		leftBtn.setTextColor(white);
		leftBtn.setOnClickListener(this);
	}

	private void addressView() {
		searchRoot = (LinearLayout) rootLayout.findViewById(R.id.search_ll);
		searchEdit = (EditText) rootLayout.findViewById(R.id.search_bar_et);
		cleanBtn = (ImageButton) rootLayout.findViewById(R.id.search_clear_bt);
		searchBtn = (Button) rootLayout.findViewById(R.id.search_more_btn);
		search_chat_content_lv = (ListView) rootLayout.findViewById(R.id.listview);
		empty_voicesearch_tip_tv = (TextView) rootLayout.findViewById(R.id.empty_voicesearch_tip_tv);
		empty_blacklist_tip_tv = (TextView) rootLayout.findViewById(R.id.empty_blacklist_tip_tv);
		sideBar = (SideBar) rootLayout.findViewById(R.id.address_scrollbar);
		address_selected_contact_area = (LinearLayout) rootLayout.findViewById(R.id.address_selected_contact_area);
		address_selectd_avatar_ll = (LinearLayout) rootLayout.findViewById(R.id.address_selectd_avatar_ll);
		dot_avatar = (ImageView) rootLayout.findViewById(R.id.dot_avatar);
		address_select_finish_btn = (Button) rootLayout.findViewById(R.id.address_select_finish_btn);
		myView = rootLayout.findViewById(R.id.myview);
		
		address_select_finish_btn.setVisibility(View.VISIBLE);
		myView.setVisibility(View.INVISIBLE);
		search_chat_content_lv.setVisibility(View.VISIBLE);
		empty_voicesearch_tip_tv.setText("无结果");
		address_selected_contact_area.setVisibility(View.VISIBLE);
		
		address_select_finish_btn.setOnClickListener(this);
	}
	
	
	private void main() {
		friendList = dao.getFriendEntity(Constants.userEbs.getUserId());
		Constants.pinyinFirst.clear();
		for (int j = 0; j < friendList.size(); j++) {
			String name = Util.converterToFirstSpell(friendList.get(j).getNickName());
			Constants.pinyinFirst.add(name);
		}
		
		adapter = new SelectContactAdapter(this, friendList);
		
		search_chat_content_lv.setAdapter(adapter);
		
		Util.setListViewFastScoll(search_chat_content_lv, this, R.drawable.fast_scroll_bg);

		ViewTreeObserver vto2 = sideBar.getViewTreeObserver();// 获得控件高度
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				sideBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				int sidebarHeight = sideBar.getHeight() / SideBar.l.length;
				SideBar.m_nItemHeight = sidebarHeight;// 设置indexbar的字母间隔高度
			}
		});
		
		sideBar.setListView(search_chat_content_lv);//设置listview
		sideBar.setContext(this);
		sideBar.setActivity(this);
		sideBar.setToastView(toastView);
		sideBar.setTextView(mDialogText);//设置textview
		
		searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				searchState = 2;// 短信搜索
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					cleanBtn.setVisibility(View.INVISIBLE);
					search_chat_content_lv.setAdapter(adapter);
					searchState = 0;// 短信搜索
				} else {
					cleanBtn.setVisibility(View.VISIBLE);
					ArrayList<FriendEntity> searchAddressList = Util.getFriendEntity(GroupCardSelect.this, searchEdit.getText().toString());
					if (searchAddressList.size() > 0) {
						empty_voicesearch_tip_tv.setVisibility(View.GONE);
						SelectContactAdapter searchAdapter = new SelectContactAdapter(GroupCardSelect.this, searchAddressList);
						search_chat_content_lv.setVisibility(View.VISIBLE);
						search_chat_content_lv.setAdapter(searchAdapter);
					} else {
						empty_voicesearch_tip_tv.setVisibility(View.VISIBLE);
						search_chat_content_lv.setVisibility(View.INVISIBLE);
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		searchEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					cleanBtn.setVisibility(View.INVISIBLE);
				} else {
					if (((EditText) v).getText().length() > 0) {
						cleanBtn.setVisibility(View.VISIBLE);
					}
				}
			}
		});

		cleanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchEdit.setText("");
				empty_voicesearch_tip_tv.setVisibility(View.GONE);
				search_chat_content_lv.setAdapter(adapter);
				search_chat_content_lv.setVisibility(View.VISIBLE);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_btn4) {
			finish();
			overridePendingTransition(R.anim.push_empty_out, R.anim.push_down_out);
		} else if (v.getId() == R.id.address_select_finish_btn) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < isCheck.size(); i++) {
				if(isCheck.get(i)){
					list.add(friendList.get(i).getFriendID());
				}
			}
			Intent intent = new Intent(GroupCardSelect.this,FMessageConversationUI.class);
			intent.putStringArrayListExtra("list", list);
			intent.putExtra("sendState", sendState);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.fast_faded_in, R.anim.push_down_out);
		} else {
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mWindowManager.removeView(toastView);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(MyBroadCastReceiver);
	}
	
	// 键盘按钮监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DEL) {// 点击删除按钮
			if (searchEdit.getText().toString().length() > 0) {
				ArrayList<FriendEntity> searchAddressList = Util.getFriendEntity(GroupCardSelect.this, searchEdit.getText().toString());
				if (searchAddressList.size() > 0) {
					empty_voicesearch_tip_tv.setVisibility(View.GONE);
					search_chat_content_lv.setVisibility(View.VISIBLE);
					SelectContactAdapter searchAdapter = new SelectContactAdapter(GroupCardSelect.this, searchAddressList);// 查询数据库中短信息分组
					search_chat_content_lv.setAdapter(searchAdapter);
				} else {
					empty_voicesearch_tip_tv.setVisibility(View.VISIBLE);
					search_chat_content_lv.setVisibility(View.INVISIBLE);
				}
			} else {
				empty_voicesearch_tip_tv.setVisibility(View.GONE);
				search_chat_content_lv.setVisibility(View.VISIBLE);
				search_chat_content_lv.setAdapter(adapter);// 存放原始数据
			}
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.push_empty_out, R.anim.push_down_out);
		}
		return true;
	}
	
	
	/*
	 * 适配器
	 */
	public class SelectContactAdapter extends BaseAdapter implements SectionIndexer {

		private static final String TAG = "AddressAdapter";
		private Map<Integer, ImageView> ivMap = new HashMap<Integer, ImageView>();
		private ArrayList<FriendEntity> array = new ArrayList<FriendEntity>();
		private ArrayList<Bitmap> bmArray = new ArrayList<Bitmap>();
		private ArrayList<String> list = new ArrayList<String>();
		private ViewHolder viewHolder = null;
		private Context mContext;
		private Bitmap bitmap = null;
		private Uri fileUri;

		public SelectContactAdapter(Context mContext, ArrayList<FriendEntity> array) {
			this.mContext = mContext;
			this.array = array;
			isCheck.clear();
		}

		@Override
		public int getCount() {
			return array.size();
		}

		@Override
		public Object getItem(int position) {
			return array.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String nickName = array.get(position).getNickName();
			final String headPath = array.get(position).getHead();
			final String phone = array.get(position).getPhone();
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.address_item, null);
				viewHolder.catalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
				viewHolder.avatar = (ImageView) convertView.findViewById(R.id.content);
				viewHolder.nick = (TextView) convertView.findViewById(R.id.contactitem_nick);
				viewHolder.account = (TextView) convertView.findViewById(R.id.contactitem_account);
				viewHolder.contactitem_signature = (TextView) convertView.findViewById(R.id.contactitem_signature);
				viewHolder.contactitem_select_cb = (CheckBox) convertView.findViewById(R.id.contactitem_select_cb);
				viewHolder.slide_del_view = (LinearLayout) convertView.findViewById(R.id.slide_del_view);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			String catalog = Constants.pinyinFirst.get(position);
			
			String lastCatalog = null;
			
			if (catalog.equals(lastCatalog)) {
				viewHolder.catalog.setVisibility(View.GONE);
			} else {
				viewHolder.catalog.setVisibility(View.VISIBLE);
				viewHolder.catalog.setText(catalog);
			}
			
			catalog = lastCatalog;
			
			viewHolder.nick.setText(nickName);
			
			if (headPath != null && !headPath.equals("")) {
				//判断头像是否存在
				boolean fileExist = FileUtil.headFileExist(headPath);
				if(fileExist){
					bitmap = BitmapUtil.ReadBitmapById(mContext, headPath, 60, 60);
					if (bitmap != null) {
						// 设置头像
						viewHolder.avatar.setImageBitmap(bitmap);
						bmArray.add(bitmap);
					}else{
						bmArray.add(null);
					}
				}
			}else{
				bmArray.add(null);
			}
			
			viewHolder.account.setText(phone);
			
			viewHolder.account.setVisibility(View.GONE);
			
			viewHolder.contactitem_signature.setVisibility(View.INVISIBLE);
			
			if(isCheck.get(position) != null){
				isCheck.put(position, isCheck.get(position));
				viewHolder.contactitem_select_cb.setClickable(isCheck.get(position));
			}else{
				isCheck.put(position, false);
				viewHolder.contactitem_select_cb.setClickable(false);
			}
			
			viewHolder.contactitem_select_cb.setTag(R.id.position, position);
			
			viewHolder.contactitem_select_cb.setTag(R.id.viewholder, viewHolder);
			
			viewHolder.contactitem_select_cb.setVisibility(View.VISIBLE);
			
			viewHolder.slide_del_view.setTag(viewHolder);
			
			viewHolder.slide_del_view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ViewHolder holder = (ViewHolder) v.getTag();
					if(holder.contactitem_select_cb.isChecked()){
						holder.contactitem_select_cb.setChecked(false);
					}else{
						holder.contactitem_select_cb.setChecked(true);
					}
				}
			});
			
			viewHolder.contactitem_select_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int position = (Integer) buttonView.getTag(R.id.position);
					ViewHolder holder = (ViewHolder) buttonView.getTag(R.id.viewholder);
					if(isChecked){
						isCheck.put(position, isChecked);
						
						ImageView child = new ImageView(GroupCardSelect.this);
						child.setTag(R.id.position,position);
						child.setTag(R.id.viewholder,holder);
						
						child.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								int position = (Integer) v.getTag(R.id.position);
								ViewHolder holder = (ViewHolder) v.getTag(R.id.viewholder);
								if(isCheck.get(position)){
									holder.contactitem_select_cb.setChecked(false);
								}else{
									holder.contactitem_select_cb.setChecked(true);
								}
								notifyDataSetChanged();
							}
						});
						
						//String headPath = friendList.get(position).getHead();
						//if (headPath != null && !headPath.equals("")) {
							//判断头像是否存在
							//boolean fileExist = FileUtil.headFileExist(headPath);
							//if(fileExist){
								bitmap = bmArray.get(position);
								if (bitmap != null) {
									child.setImageBitmap(bitmap);// 设置头像
								}else{
									child.setImageResource(R.drawable.mini_avatar);
								}
							//}else{
								//child.setImageResource(R.drawable.mini_avatar);
							//}
						//}
						
						ivMap.put(position, child);
						
						address_selectd_avatar_ll.addView(child);
					}else{
						isCheck.put(position, false);
						address_selectd_avatar_ll.removeView(ivMap.get(position));
					}
					
					if(address_selectd_avatar_ll.getChildCount() > 0){
						address_select_finish_btn.setEnabled(true);
						address_select_finish_btn.setText("确定("+address_selectd_avatar_ll.getChildCount()+")");
					}else{
						address_select_finish_btn.setEnabled(false);
						address_select_finish_btn.setText("确定("+address_selectd_avatar_ll.getChildCount()+")");
					}
					
					notifyDataSetChanged();
				}
			});
			
			return convertView;
		}
		
		class ViewHolder {
			TextView catalog;// 目录
			ImageView avatar;// 头像
			TextView nick;// 昵称
			TextView account;//账号
			TextView contactitem_signature;
			CheckBox contactitem_select_cb;
			LinearLayout slide_del_view;
		}
		
		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < array.size(); i++) {
				char firstChar = Constants.pinyinFirst.get(i).toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
		
		/**
		 * 汉字转换位汉语拼音首字母，英文字符不变
		 * @param chines 汉字
		 * @return 拼音
		 */
		public String converterToFirstSpell(String chines) {
			String pinyinName = "";
			char[] nameChar = chines.toCharArray();
			HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
			defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
			defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			if (nameChar[0] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[0];
			}
			return pinyinName.toUpperCase();
		}
	}
}
