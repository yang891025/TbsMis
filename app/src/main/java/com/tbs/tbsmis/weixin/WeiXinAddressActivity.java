package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.WechatUserAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.database.dao.DBUtil;
import com.tbs.tbsmis.database.table.WX_USER_TABLE;
import com.tbs.tbsmis.entity.WXGroupEntity;
import com.tbs.tbsmis.entity.WXUserEntity;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.TxtImport;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.Util;
import com.tbs.tbsmis.widget.SideBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//import com.tbs.chat.constants.Constants;
//import com.tbs.chat.util.Util;

public class WeiXinAddressActivity extends Activity implements View.OnClickListener {
	private String userIni;
	private IniFile IniFile;
	private ArrayList<WXUserEntity> Userlist;
	private ArrayList<WXUserEntity> searchAddressList;
	private List<String> listTag;
	private WechatUserAdapter lvUserAdapter;
	private ImageView backBtn;
	private TextView title_tv;
	private BroadcastReceiver MyBroadCastReceiver;
	private ListView search_chat_content_lv;
	private DBUtil dao;
	private LinearLayout searchRoot;
	private EditText searchEdit;
	private ImageButton cleanBtn;
	private Button searchBtn;
	private TextView empty_voicesearch_tip_tv;
	private TextView empty_blacklist_tip_tv;
	private SideBar sideBar;
	private LinearLayout address_selected_contact_area;
	private LinearLayout address_selectd_avatar_ll;
	private ImageView dot_avatar;
	private Button address_select_finish_btn;
	private View myView;
	private int searchState;
	private String dbName;
	private ProgressBar mProgress;
	private boolean isOpenPop;
	private PopupWindow moreWindow2;
	private RelativeLayout cloudTitle;
	private ImageView menuBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weixin_address_list);
		MyActivity.getInstance().addActivity(this);

        this.dao = DBUtil.getInstance(this);
        this.addressView();
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(constants.REFRESH_ADDRESS);
		filter.addCategory("default");
        this.MyBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (constants.REFRESH_ADDRESS.equals(intent.getAction())) {
                    WeiXinAddressActivity.this.init();
				}
			}
		};
        this.registerReceiver(this.MyBroadCastReceiver, filter);
        this.title_tv = (TextView) this.findViewById(R.id.title_tv);
        this.backBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.mProgress = (ProgressBar) this.findViewById(R.id.title_progress);
        this.title_tv.setText("用户管理");
        this.menuBtn = (ImageView) this.findViewById(R.id.search_btn2);
        this.menuBtn.setVisibility(View.VISIBLE);
        this.menuBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeiXinAddressActivity.this.changMorePopState2(v);
			}
		});
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
				"user_sort", 0);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                WeiXinAddressActivity.this.finish();
			}
		});
		UIHelper.setSharePerference(this,
				constants.SAVE_LOCALMSGNUM, "refreshUser", 1);
		initPath();
        dbName = this.IniFile.getIniString(this.userIni, "WeiXin", "iniPath",
                "wechat", (byte) 0);
        this.connect(1);
	}
    private void initPath(){
        IniFile = new IniFile();
        String webRoot = UIHelper.getSoftPath(this);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }
	@Override
	protected void onResume() {
		super.onResume();
        this.connect(1);
	}

	public void init() {
        this.listTag = new ArrayList<String>();
        this.Userlist = new ArrayList<WXUserEntity>();
        this.Userlist = this.dao.getUsersInfo(this, this.dbName);
        this.listTag.clear();
		final int user_sort = UIHelper.getShareperference(
                this, constants.SAVE_LOCALMSGNUM,
				"user_sort", 0);
		Collections.sort(this.Userlist, new Comparator<WXUserEntity>() {
			@Override
			public int compare(WXUserEntity object1, WXUserEntity object2) {
				if (user_sort == 0) {
					String name1 = Util.converterToFirstSpell(object1
							.getNickname().trim());
					String name2 = Util.converterToFirstSpell(object2
							.getNickname().trim());
					return name1.compareTo(name2);
				} else if (user_sort == 1) {
					return WeiXinAddressActivity.this.longToCompareInt(object2.getSubscribe_time()
							- object1.getSubscribe_time());
				} else {
					return WeiXinAddressActivity.this.longToCompareInt(object1.getGroupid()
							- object2.getGroupid());
				}
			}
		});
		if (user_sort == 0) {
			for (int j = 0; j < this.Userlist.size(); j++) {
				if (!StringUtils.isEmpty(this.Userlist.get(j).getNickname())) {
					String name = Util.converterToFirstSpell(this.Userlist.get(j)
							.getNickname().trim());
                    this.listTag.add(name);
				}
			}
		} else if (user_sort == 2) {
			for (int j = 0; j < this.Userlist.size(); j++) {
                this.listTag.add(this.Userlist.get(j).getGroupname());
			}
		} else {
			for (int j = 0; j < this.Userlist.size(); j++) {
                this.listTag.add(this.Userlist.get(j).getCity());
			}
		}
        this.lvUserAdapter = new WechatUserAdapter(this.Userlist,
                this, this.listTag);

	}

	public void searchinit() {
        this.searchAddressList = new ArrayList<WXUserEntity>();
        this.searchAddressList = this.dao.getSearchUser(this,
                this.searchEdit.getText().toString(), this.dbName);
		final int user_sort = UIHelper.getShareperference(
                this, constants.SAVE_LOCALMSGNUM,
				"user_sort", 0);
		Collections.sort(this.searchAddressList, new Comparator<WXUserEntity>() {
			@Override
			public int compare(WXUserEntity object1, WXUserEntity object2) {
				if (user_sort == 0) {
					String name1 = Util.converterToFirstSpell(object1
							.getNickname().trim());
					String name2 = Util.converterToFirstSpell(object2
							.getNickname().trim());
					return name1.compareTo(name2);
				} else if (user_sort == 1) {
					return WeiXinAddressActivity.this.longToCompareInt(object2.getSubscribe_time()
							- object1.getSubscribe_time());
				} else {
					return WeiXinAddressActivity.this.longToCompareInt(object1.getGroupid()
							- object2.getGroupid());
				}
			}
		});
		List<String> listTag = new ArrayList<String>();
		if (user_sort == 0) {
			for (int j = 0; j < this.searchAddressList.size(); j++) {
				if (!StringUtils
						.isEmpty(this.searchAddressList.get(j).getNickname())) {
					String name = Util.converterToFirstSpell(this.searchAddressList
							.get(j).getNickname().trim());
					listTag.add(name);
				}
			}
		} else if (user_sort == 2) {
			for (int j = 0; j < this.searchAddressList.size(); j++) {
				listTag.add(this.searchAddressList.get(j).getGroupname());
			}
		} else {
			for (int j = 0; j < this.searchAddressList.size(); j++) {
				listTag.add(this.searchAddressList.get(j).getCity());
			}
		}
	}

	private int longToCompareInt(long result) {
		return result > 0 ? 1 : result < 0 ? -1 : 0;
	}

	public void initlist() {
        this.search_chat_content_lv.setAdapter(this.lvUserAdapter);
		int user_sort = UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "user_sort", 0);
		if (user_sort == 0) {
			ViewTreeObserver vto2 = this.sideBar.getViewTreeObserver();// 获得控件高度
			vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
                    WeiXinAddressActivity.this.sideBar.getViewTreeObserver().removeGlobalOnLayoutListener(
							this);
					int sidebarHeight = WeiXinAddressActivity.this.sideBar.getHeight() / SideBar.l.length;
					SideBar.m_nItemHeight = sidebarHeight;// 设置indexbar的字母间隔高度
				}
			});

            this.sideBar.setListView(this.search_chat_content_lv);// 设置listview
            this.sideBar.setContext(this);
            this.sideBar.setActivity(this);
            this.sideBar.setVisibility(View.VISIBLE);
            this.sideBar.postInvalidate();
		} else {
            this.sideBar.setVisibility(View.GONE);
		}
        this.searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
                WeiXinAddressActivity.this.searchState = 1;// 短信搜索
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(s)) {
                    WeiXinAddressActivity.this.cleanBtn.setVisibility(View.INVISIBLE);
                    WeiXinAddressActivity.this.search_chat_content_lv.setVisibility(View.VISIBLE);
                    WeiXinAddressActivity.this.search_chat_content_lv.setAdapter(WeiXinAddressActivity.this.lvUserAdapter);
                    WeiXinAddressActivity.this.searchState = 0;// 短信搜索
				} else {
                    WeiXinAddressActivity.this.cleanBtn.setVisibility(View.VISIBLE);
                    WeiXinAddressActivity.this.searchinit();
					if (WeiXinAddressActivity.this.searchAddressList.size() > 0) {
                        WeiXinAddressActivity.this.empty_voicesearch_tip_tv.setVisibility(View.GONE);
						WechatUserAdapter searchAdapter = new WechatUserAdapter(
                                WeiXinAddressActivity.this.searchAddressList, WeiXinAddressActivity.this,
                                WeiXinAddressActivity.this.listTag);
                        WeiXinAddressActivity.this.search_chat_content_lv.setVisibility(View.VISIBLE);
                        WeiXinAddressActivity.this.search_chat_content_lv.setAdapter(searchAdapter);
					} else {
                        WeiXinAddressActivity.this.empty_voicesearch_tip_tv.setVisibility(View.VISIBLE);
                        WeiXinAddressActivity.this.search_chat_content_lv.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

        this.searchEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
                    WeiXinAddressActivity.this.cleanBtn.setVisibility(View.INVISIBLE);
				} else {
					if (((EditText) v).getText().length() > 0) {
                        WeiXinAddressActivity.this.cleanBtn.setVisibility(View.VISIBLE);
					}
				}
			}
		});

        this.cleanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                WeiXinAddressActivity.this.searchEdit.setText("");
                WeiXinAddressActivity.this.empty_voicesearch_tip_tv.setVisibility(View.GONE);
                WeiXinAddressActivity.this.search_chat_content_lv.setAdapter(WeiXinAddressActivity.this.lvUserAdapter);
                WeiXinAddressActivity.this.search_chat_content_lv.setVisibility(View.VISIBLE);
                WeiXinAddressActivity.this.searchState = 0;// 短信搜索
			}
		});
	}

	private void addressView() {
        this.searchRoot = (LinearLayout) this.findViewById(R.id.search_ll);
        this.searchEdit = (EditText) this.findViewById(R.id.search_bar_et);
        this.cleanBtn = (ImageButton) this.findViewById(R.id.search_clear_bt);
        this.searchBtn = (Button) this.findViewById(R.id.search_more_btn);
        this.search_chat_content_lv = (ListView) this.findViewById(R.id.listview);
        this.empty_voicesearch_tip_tv = (TextView) this.findViewById(R.id.empty_voicesearch_tip_tv);
        this.empty_blacklist_tip_tv = (TextView) this.findViewById(R.id.empty_blacklist_tip_tv);
        this.sideBar = (SideBar) this.findViewById(R.id.address_scrollbar);
        this.address_selected_contact_area = (LinearLayout) this.findViewById(R.id.address_selected_contact_area);
        this.address_selectd_avatar_ll = (LinearLayout) this.findViewById(R.id.address_selectd_avatar_ll);
        this.dot_avatar = (ImageView) this.findViewById(R.id.dot_avatar);
        this.address_select_finish_btn = (Button) this.findViewById(R.id.address_select_finish_btn);
        this.myView = this.findViewById(R.id.myview);

        this.sideBar.setVisibility(View.INVISIBLE);
        this.myView.setVisibility(View.INVISIBLE);
        this.search_chat_content_lv.setVisibility(View.VISIBLE);
        this.empty_voicesearch_tip_tv.setText("无结果");
        this.search_chat_content_lv.setVerticalScrollBarEnabled(false);
        this.search_chat_content_lv
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Bundle b = new Bundle();
						if (WeiXinAddressActivity.this.searchState == 0) {
							b.putSerializable("WXUserEntity",
                                    WeiXinAddressActivity.this.Userlist.get(position));
						} else if (WeiXinAddressActivity.this.searchState == 1) {
							b.putSerializable("WXUserEntity",
                                    WeiXinAddressActivity.this.searchAddressList.get(position));
						}
						Intent intent = new Intent(WeiXinAddressActivity.this,
								WeixinUserInfoActivity.class);
						intent.putExtras(b);
                        WeiXinAddressActivity.this.startActivity(intent);
					}
				});
	}

	public void changMorePopState2(View v) {
        this.isOpenPop = !this.isOpenPop;
		if (this.isOpenPop) {
            this.popWindow2(v);
		} else {
			if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
			}
		}
	}

	public int impot2Text() {
		// String m_fieldNameSep = "";// 获得xml中的内容
		// String m_recordSep = "";// 获得xml中的内容
		// int m_recordSepCount = 3;// 获得xml中的内容
		// String m_lineSplitter = "";// 获得xml中的内容
		TxtImport importTest = new TxtImport(this);// 实例化TxtImport类

		// 此处需要做入库判断，所还需要想思路
		// importTest.setScanParams(m_fieldNameSep, m_recordSep,
		// m_recordSepCount,
		// m_lineSplitter);
		// 设定导入导入的数据库表
		importTest.changeTable(WX_USER_TABLE.TABLE_NAME);
		// 根据需要设定表中各字段的别名
		importTest.setColumnAlias("openid", "账户id");
		importTest.setColumnAlias("username", "用户名");
		importTest.setColumnAlias("nickname", "昵称");
		importTest.setColumnAlias("remark", "备注名");
		importTest.setColumnAlias("city", "城市");
		importTest.setColumnAlias("province", "省份");
		importTest.setColumnAlias("country", "国家");
		importTest.setColumnAlias("sex", "性别");
		importTest.setColumnAlias("language", "语言");
		importTest.setColumnAlias("headimgurl", "头像");
		importTest.setColumnAlias("subscribe_time", "订阅时间");
		// 保存路径
		String webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String savePath = webRoot
				+ "WeiXin/"
				+ this.IniFile.getIniString(this.userIni, "WeiXin", "Account",
						"TBS软件", (byte) 0) + "/user/user.txt";
		// 导入数据库的数量
		int tempStr = importTest.export2Txt(savePath, this.dbName, false);
		return tempStr;
	}

	@SuppressWarnings("deprecation")
	private void popWindow2(View parent) {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = lay.inflate(R.layout.weixin_save_menu, null);
		RelativeLayout send_text = (RelativeLayout) view
				.findViewById(R.id.menu_save);
		RelativeLayout menu_sort = (RelativeLayout) view
				.findViewById(R.id.menu_sort);
		RelativeLayout menu_refresh = (RelativeLayout) view
				.findViewById(R.id.menu_refresh);
		menu_refresh.setBackgroundResource(R.drawable.more_down);
		menu_sort.setVisibility(View.VISIBLE);
		menu_refresh.setVisibility(View.VISIBLE);
		menu_sort.setOnClickListener(this);
		menu_refresh.setOnClickListener(this);
		send_text.setOnClickListener(this);
        this.moreWindow2 = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
                WeiXinAddressActivity.this.isOpenPop = false;
			}
		});
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
	}

	protected void showSortDialog() {
		// TODO Auto-generated method stub
		new Builder(this)
				.setTitle("选择类型")
				.setSingleChoiceItems(
                        R.array.user_sort_options,
						UIHelper.getShareperference(this,
								constants.SAVE_LOCALMSGNUM, "user_sort", 0),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								UIHelper.setSharePerference(
										WeiXinAddressActivity.this,
										constants.SAVE_LOCALMSGNUM,
										"user_sort", which);
                                WeiXinAddressActivity.this.connect(3);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu_save:
			int count = this.impot2Text();
			Toast.makeText(this, "保存了" + count + "条用户信息", Toast.LENGTH_SHORT)
					.show();
            this.moreWindow2.dismiss();
			break;
		case R.id.menu_sort:
            this.showSortDialog();
            this.moreWindow2.dismiss();
			break;
		case R.id.menu_refresh:
            this.connect(1);
            this.moreWindow2.dismiss();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        this.unregisterReceiver(this.MyBroadCastReceiver);
		MyActivity.getInstance().finishActivity(this);
	}

	public void initJson2db(String json, String dbName) {
		try {
			JSONObject jsonresult = new JSONObject(json);
			JSONArray jsonUsers = jsonresult.getJSONArray("users");
			JSONArray jsonGroups = jsonresult.getJSONArray("group");
			ArrayList<WXGroupEntity> groups = new ArrayList<WXGroupEntity>();
			for (int i = 0; i < jsonGroups.length(); i++) {
				JSONObject ob = jsonGroups.getJSONObject(i);
				WXGroupEntity group = new WXGroupEntity();
				group.setId(ob.getInt("groupid"));
				group.setName(ob.getString("groupname"));
				group.setCount(ob.getInt("groupcount"));
				groups.add(group);
			}
			if (jsonGroups.length() > 0)
                this.dao.addOrUpdateGruop(groups);
			ArrayList<WXUserEntity> users = new ArrayList<WXUserEntity>();
			for (int i = 0; i < jsonUsers.length(); i++) {
				JSONObject ob = jsonUsers.getJSONObject(i);
				WXUserEntity user = new WXUserEntity();
				user.setOpenid(ob.getString("openid"));
				user.setNickname(ob.getString("nickname"));
				user.setCity(ob.getString("city"));
				user.setProvince(ob.getString("province"));
				user.setCountry(ob.getString("country"));
				user.setSex(ob.getInt("sex"));
				user.setLanguage(ob.getString("language"));
				user.setHeadimgurl(ob.getString("headimgurl"));
				user.setSubscribe_time(ob.getInt("subscribe_time"));
				user.setRemark(ob.getString("remark"));
				user.setGroupid(ob.getInt("groupid"));
				user.setSubscribe(ob.getInt("subscribe"));
				users.add(user);
			}
			if (jsonUsers.length() > 0)
                this.dao.addOrUpdateUser(users, dbName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void connect(int count) {
		WeiXinAddressActivity.GetAsyncTask task = new WeiXinAddressActivity.GetAsyncTask(this, count);
		task.execute();
	}

	/**
	 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
	 * 
	 */
	class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

		private final Context context;
		private final int count;

		public GetAsyncTask(Context context, int count) {
            this.context = context;
			this.count = count;
		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
            WeiXinAddressActivity.this.menuBtn.setVisibility(View.GONE);
            WeiXinAddressActivity.this.mProgress.setVisibility(View.VISIBLE);

		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			if (this.count == 1 || this.count == 3) {
                WeiXinAddressActivity.this.init();
			} else if (this.count == 2) {
				HttpConnectionUtil connection = new HttpConnectionUtil();
				String username = "";
				String verifyURL = WeiXinAddressActivity.this.IniFile.getIniString(WeiXinAddressActivity.this.userIni, "WeiXin",
						"WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
						+ "?action=getUsersInfo&username="
						+ username
						+ "&deviceid=" + UIHelper.DeviceMD5ID(this.context);
				String verify = connection.asyncConnect(verifyURL, null,
						HttpConnectionUtil.HttpMethod.GET, this.context);
				if (!StringUtils.isEmpty(verify)) {
                    WeiXinAddressActivity.this.initJson2db(verify, WeiXinAddressActivity.this.dbName);
				}
				return "";
			}
			return null;
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
			if (this.count == 1) {
                WeiXinAddressActivity.this.initlist();
                WeiXinAddressActivity.this.connect(2);
			} else if (this.count == 3) {
                WeiXinAddressActivity.this.initlist();
                WeiXinAddressActivity.this.mProgress.setVisibility(View.GONE);
                WeiXinAddressActivity.this.menuBtn.setVisibility(View.VISIBLE);
			} else {
                WeiXinAddressActivity.this.mProgress.setVisibility(View.GONE);
                WeiXinAddressActivity.this.menuBtn.setVisibility(View.VISIBLE);
			}

		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

	}
}
