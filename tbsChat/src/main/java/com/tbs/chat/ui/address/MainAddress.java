package com.tbs.chat.ui.address;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.adapter.AddressAdapter;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.menu.ExitAppMenu;
import com.tbs.chat.ui.base.BaseActivity;
import com.tbs.chat.util.Util;
import com.tbs.chat.wight.SideBar;

import java.util.ArrayList;

public class MainAddress extends BaseActivity
{

    private static final String TAG = "MainAddress";
    private final Context mContext = this;

    private BroadcastReceiver MyBroadCastReceiver;// 广播
    private WindowManager mWindowManager;
    private LayoutInflater inflater;
    private LinearLayout rootLayout;
    private View titleView;
    private View addressView;
    private View toastView;
    private ImageButton rightBtn;
    private Button leftBtn;
    private TextView title;
    private EditText searchEdit;
    private ImageButton cleanBtn;
    private ListView search_chat_content_lv;
    private TextView empty_voicesearch_tip_tv;
    private SideBar sideBar;
    private AddressAdapter adapter;
    private TextView mDialogText;
    private int searchState = 0;
    private View myView;
    private ArrayList<FriendEntity> friendList = null;
    private ArrayList<FriendEntity> searchAddressList = null;
    private DBUtil dao = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_linearlayout);
        view();
        titleView();
        addressView();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager.addView(toastView, lp);
        if (friendList == null) {
            MyAsyncTask task = new MyAsyncTask(mContext);
            task.execute();
        }
    }

    private void view() {

        dao = DBUtil.getInstance(mContext);

        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.EXIT_ACTIVITY);
        filter.addAction(Constants.REFRESH_ADDRESS);
        filter.addAction(Constants.FLUSH_HEAD);// 刷新头像
        filter.addCategory("default");
        MyBroadCastReceiver = new BroadcastReceiver()
        {
            @TargetApi(Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
                    finish();
                } else if (Constants.REFRESH_ADDRESS.equals(intent.getAction())) {
                    friendList = dao.getFriendEntity(Constants.userEbs
                            .getUserId());
                    Constants.pinyinFirst.clear();
                    for (int j = 0; j < friendList.size(); j++) {
                        String name = Util.converterToFirstSpell(friendList
                                .get(j).getNickName());
                        Constants.pinyinFirst.add(name);
                    }
                    adapter = new AddressAdapter(mContext, friendList);
                    search_chat_content_lv.setAdapter(adapter);
                }
            }
        };
        registerReceiver(MyBroadCastReceiver, filter);

		/*
         * 获得根布局 将标题布局增加到根布局中 将登录布局增加到根布局中 启动activity时不自动弹出软键盘
		 */
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);// 获得根布局
        inflater = getLayoutInflater();// 获得适配器
        titleView = inflater.inflate(R.layout.mm_title, null);
        addressView = inflater.inflate(R.layout.address_chat, null);
        toastView = inflater.inflate(R.layout.address_dialog, null);
        rootLayout.addView(titleView);// 添加布局
        rootLayout.addView(addressView);// 添加布局
        mDialogText = (TextView) toastView.findViewById(R.id.textview);// 找到listview

    }

    private void titleView() {
        rightBtn = (ImageButton) rootLayout.findViewById(R.id.title_btn1);
        leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
        title = (TextView) rootLayout.findViewById(R.id.title);
        leftBtn.setVisibility(View.VISIBLE);
        // 白颜色
        int white = getResources().getColor(R.color.white);
        leftBtn.setTextColor(white);
        leftBtn.setText("返回");
        title.setText("通讯录");
        rightBtn.setImageResource(R.drawable.addicon);
        rightBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(Constants.EXIT_ACTIVITY);
//				sendBroadcast(intent);
                Intent intent = new Intent(MainAddress.this, ExitAppMenu.class);
                startActivity(intent);
            }
        });
        rightBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainAddress.this,
                        AddMoreFriendsUI.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_empty_out);
            }
        });
    }

    private void addressView() {
        searchEdit = (EditText) rootLayout.findViewById(R.id.search_bar_et);
        cleanBtn = (ImageButton) rootLayout.findViewById(R.id.search_clear_bt);
        search_chat_content_lv = (ListView) rootLayout
                .findViewById(R.id.listview);
        empty_voicesearch_tip_tv = (TextView) rootLayout
                .findViewById(R.id.empty_voicesearch_tip_tv);
        sideBar = (SideBar) rootLayout.findViewById(R.id.address_scrollbar);
        myView = rootLayout.findViewById(R.id.myview);

        sideBar.setVisibility(View.INVISIBLE);
        myView.setVisibility(View.INVISIBLE);
        search_chat_content_lv.setVisibility(View.VISIBLE);
        empty_voicesearch_tip_tv.setText("无结果");

        search_chat_content_lv
                .setOnItemClickListener(new OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Bundle b = new Bundle();
                        if (searchState == 0) {
                            b.putSerializable("friendEntity",
                                    friendList.get(position));
                        } else if (searchState == 1) {
                            b.putSerializable("friendEntity",
                                    searchAddressList.get(position));
                        }
                        Intent intent = new Intent(MainAddress.this,
                                SelectContactUI.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
    }

    /**
     * 初始化数据
     */
    private void init() {
        friendList = dao.getFriendEntity(Constants.userEbs.getUserId());

        Constants.pinyinFirst.clear();
        for (int j = 0; j < friendList.size(); j++) {
            String name = Util.converterToFirstSpell(friendList.get(j)
                    .getNickName());
            Constants.pinyinFirst.add(name);
        }

        adapter = new AddressAdapter(this, friendList);
    }

    /**
     *
     */
    private void main() {
        search_chat_content_lv.setAdapter(adapter);

//		Util.setListViewFastScoll(search_chat_content_lv, this,
//				R.drawable.fast_scroll_bg);

        ViewTreeObserver vto2 = sideBar.getViewTreeObserver();// 获得控件高度
        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout() {
                sideBar.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
                int sidebarHeight = sideBar.getHeight() / SideBar.l.length;
                SideBar.m_nItemHeight = sidebarHeight;// 设置indexbar的字母间隔高度
            }
        });

        sideBar.setListView(search_chat_content_lv);// 设置listview
        sideBar.setContext(this);
        sideBar.setActivity(this);
        sideBar.setToastView(toastView);
        sideBar.setTextView(mDialogText);// 设置textview
        sideBar.setVisibility(View.VISIBLE);

        searchEdit.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                searchState = 1;// 短信搜索
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    cleanBtn.setVisibility(View.INVISIBLE);
                    search_chat_content_lv.setAdapter(adapter);
                    searchState = 0;// 短信搜索
                } else {
                    cleanBtn.setVisibility(View.VISIBLE);
                    searchAddressList = Util.getFriendEntity(MainAddress.this,
                            searchEdit.getText().toString());
                    if (searchAddressList.size() > 0) {
                        empty_voicesearch_tip_tv.setVisibility(View.GONE);
                        AddressAdapter searchAdapter = new AddressAdapter(
                                MainAddress.this, searchAddressList);
                        search_chat_content_lv.setVisibility(View.VISIBLE);
                        search_chat_content_lv.setAdapter(searchAdapter);
                    } else {
                        empty_voicesearch_tip_tv.setVisibility(View.VISIBLE);
                        search_chat_content_lv.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchEdit.setOnFocusChangeListener(new OnFocusChangeListener()
        {

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

        cleanBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                searchEdit.setText("");
                empty_voicesearch_tip_tv.setVisibility(View.GONE);
                search_chat_content_lv.setAdapter(adapter);
                search_chat_content_lv.setVisibility(View.VISIBLE);
                searchState = 0;// 短信搜索
            }
        });

        sideBar.postInvalidate();
    }

    /*
     * 异步任务 后台任务前 后台任务中 后台任务结束
     */
    class MyAsyncTask extends AsyncTask<Void, Integer, Integer>
    {

        private Context context;

        public MyAsyncTask(Context c) {
            this.context = c;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            showProgressDialog("通讯录加载中...");
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected Integer doInBackground(Void... params) {
             init();
            return null;
        }

        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(Integer integer) {
            main();
            showProgressDialog("通讯录加载中...");
        }

        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    // 键盘按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {// 点击删除按钮
            if (searchEdit.getText().toString().length() > 0) {
                searchAddressList = Util.getFriendEntity(MainAddress.this,
                        searchEdit.getText().toString());
                if (searchAddressList.size() > 0) {
                    empty_voicesearch_tip_tv.setVisibility(View.GONE);
                    search_chat_content_lv.setVisibility(View.VISIBLE);
                    AddressAdapter searchAdapter = new AddressAdapter(
                            MainAddress.this, searchAddressList);// 查询数据库中短信息分组
                    search_chat_content_lv.setAdapter(searchAdapter);
                } else {
                    empty_voicesearch_tip_tv.setVisibility(View.VISIBLE);
                    search_chat_content_lv.setVisibility(View.GONE);
                }
            } else {
                empty_voicesearch_tip_tv.setVisibility(View.GONE);
                search_chat_content_lv.setVisibility(View.VISIBLE);
                search_chat_content_lv.setAdapter(adapter);// 存放原始数据
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) { // 获取
            // Intent home = new Intent(Intent.ACTION_MAIN);
            // home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // home.addCategory(Intent.CATEGORY_HOME);
            // startActivity(home);
//			Intent intent = new Intent(Constants.EXIT_ACTIVITY);
//			sendBroadcast(intent);
            Intent intent = new Intent(MainAddress.this, ExitAppMenu.class);
            startActivity(intent);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
            Intent intent = new Intent(MainAddress.this, ExitAppMenu.class);
            startActivity(intent);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tbs.chat.ui.base.BaseActivity#findViewById()
     */
    @Override
    protected void findViewById() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.tbs.chat.ui.base.BaseActivity#initView()
     */
    @Override
    protected void initView() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onPause() {
        super.onPause();
        mWindowManager.removeView(toastView);
        mWindowManager = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.tbs.chat.ui.base.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyBroadCastReceiver != null) {
            unregisterReceiver(MyBroadCastReceiver);
        }
        if (mWindowManager != null) {
            mWindowManager.removeView(toastView);
            mWindowManager = null;
        }
    }
}
