package com.tbs.chat.ui.chatting;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.chat.R;
import com.tbs.chat.adapter.MessageAdapter;
import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.menu.ExitAppMenu;
import com.tbs.chat.receiver.SMSReceiver;
import com.tbs.chat.service.HttpServer;
import com.tbs.chat.socket.Communication;
import com.tbs.chat.ui.address.GroupCardSelect;
import com.tbs.chat.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainMessage extends BaseActivity
{

    protected static final String TAG = "MainMessage";
    private final Context mContext = this;

    private BroadcastReceiver MyBroadCastReceiver;// 广播
    private SMSReceiver mySmsReceiver;
    private Intent intent;
    private LayoutInflater inflater;
    private LinearLayout rootLayout;
    private View titleView;
    private View netWorkBar;
    private View searchBar;
    private View chatView;
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
    private TextView empty_search_result_tv;
    private TextView search_chat_content_bg;

    private LinearLayout netWore;
    private ImageView nw_icon;
    private TextView nw_detail;
    private TextView nw_detail_tip;
    private TextView nw_hint_tip;
    private ProgressBar nw_prog;
    private TextView nw_btn;
    private ImageView forward_icon;
    private ImageView close_icon;

    private boolean reLogin;
    private boolean isSearch = false;
    private boolean resLogin = false;

    private Map<String, Integer> map = new HashMap<String, Integer>();
    private ArrayList<MessageEntity> message = new ArrayList<MessageEntity>();
    private ArrayList<MessageEntity> searchMsgList = new ArrayList<MessageEntity>();
    private MessageAdapter adapter;

    private DBUtil dao = null;
    private Context context = this;
    private int msgIndex = 0;
    private NotificationManager notificationManager;// 通知管理者
    private ArrayList<String> list = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_linearlayout);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        /*
         * 获得根布局 将标题布局增加到根布局中 将登录布局增加到根布局中
		 */
        rootLayout = (LinearLayout) findViewById(R.id.base_linearlayout);// 获得根布局
        inflater = getLayoutInflater();// 获得适配器
        titleView = inflater.inflate(R.layout.mm_title, null);
        netWorkBar = inflater.inflate(R.layout.net_warn_item, null);
        searchBar = inflater.inflate(R.layout.contact_search_bar, null);
        chatView = inflater.inflate(R.layout.search_chat_content, null);
        rootLayout.addView(titleView);// 添加布局
        rootLayout.addView(searchBar);// 添加布局
        rootLayout.addView(netWorkBar);// 添加布局
        rootLayout.addView(chatView);// 添加布局

        rightBtn = (ImageButton) rootLayout.findViewById(R.id.title_btn1);
        leftBtn = (Button) rootLayout.findViewById(R.id.title_btn4);
        leftBtn.setVisibility(View.VISIBLE);
        title = (TextView) rootLayout.findViewById(R.id.title);
        subTitle = (TextView) rootLayout.findViewById(R.id.sub_title);
        mProgress = (ProgressBar) rootLayout.findViewById(R.id.title_progress);

        searchRoot = (LinearLayout) rootLayout.findViewById(R.id.search_ll);
        searchEdit = (EditText) rootLayout.findViewById(R.id.search_bar_et);
        cleanBtn = (ImageButton) rootLayout.findViewById(R.id.search_clear_bt);
        searchBtn = (Button) rootLayout.findViewById(R.id.search_more_btn);

        netWore = (LinearLayout) netWorkBar.findViewById(R.id.nwview);
        nw_icon = (ImageView) netWorkBar.findViewById(R.id.nw_icon);
        nw_detail = (TextView) netWorkBar.findViewById(R.id.nw_detail);
        nw_detail_tip = (TextView) netWorkBar.findViewById(R.id.nw_detail_tip);
        nw_hint_tip = (TextView) netWorkBar.findViewById(R.id.nw_hint_tip);
        nw_prog = (ProgressBar) netWorkBar.findViewById(R.id.nw_prog);
        nw_btn = (TextView) netWorkBar.findViewById(R.id.nw_btn);
        forward_icon = (ImageView) netWorkBar.findViewById(R.id.forward_icon);
        close_icon = (ImageView) netWorkBar.findViewById(R.id.close_icon);

        search_chat_content_lv = (ListView) rootLayout
                .findViewById(R.id.search_chat_content_lv);
        empty_search_result_tv = (TextView) rootLayout
                .findViewById(R.id.empty_search_result_tv);
        search_chat_content_bg = (TextView) rootLayout
                .findViewById(R.id.search_chat_content_bg);

    }

    @Override
    protected void initView() {
        IntentFilter smsFilter = new IntentFilter();
        smsFilter.addAction(Constants.ACTION);
        smsFilter.setPriority(20);// 设置优先级别
        smsFilter.addCategory("default");
        mySmsReceiver = new SMSReceiver();
        registerReceiver(mySmsReceiver, smsFilter);

        // 注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.EXIT_ACTIVITY);
        filter.addAction(Constants.LOGIN_RESOULT);
        filter.addAction(Constants.REFRESH_MESSAGE);
        filter.addAction(Constants.HIDE_MESSAGE_NOTIFY);
        filter.addAction(Constants.REQUEST_OFF_MESSAGE);
        filter.addAction(Constants.RELOGIN_USER);
        filter.addAction(Constants.START_TIMER);
        filter.addAction(Constants.SOCKET_CONNECTION);
        // filter.addAction(Constants.FLUSH_HEAD);// 刷新头像
        filter.addCategory("default");
        MyBroadCastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
                    finish();
                } else if (Constants.LOGIN_RESOULT.equals(intent.getAction())) {
                    int isLogin = intent.getIntExtra("isLogin", 0);
                    Log.d(TAG, "LOGIN_RESOULT ISLOGIN:" + isLogin);
                    if (isLogin == Config.RESULT_LOGIN_SUCCESS) {
                        rightBtn.setVisibility(View.VISIBLE);
                    } else {
                        if (Constants.con != null) {
                            if (!resLogin) {
                                Constants.con.login(
                                        Constants.userEbs.getUserId(),
                                        Constants.userEbs.getPassword());
                                resLogin = true;
                            }else {
                                Toast.makeText(MainMessage.this,"登陆异常，请稍候重试!",Toast.LENGTH_LONG).show();
                               finish();
                            }
                        }
                    }
                    // 隐藏进度条
                    mProgress.setVisibility(View.GONE);

                } else if (Constants.REFRESH_MESSAGE.equals(intent.getAction())) {// 刷新界面
                    String friendID = intent.getStringExtra("friendID");
                    int direction = intent.getIntExtra("direction", 0);
                    if (direction == Config.MESSAGE_FROM) {
                        msgIndex++;
                        if (map.containsKey(friendID)) {
                            map.put(friendID, map.get(friendID) + 1);
                        } else {
                            map.put(friendID, 1);
                        }
                        list = new ArrayList<String>();
                        list.add(friendID);
                        String nick = dao.getFriendNick(friendID);
                        showNotification(map.size(), list, nick, msgIndex);
                    }
                    message = dao.getMessage(context,
                            Constants.userEbs.getUserId());// 查询消息
                    adapter = new MessageAdapter(MainMessage.this, message,
                            map, dao);// 实例化适配器
                    search_chat_content_lv.setAdapter(adapter);// 填充适配器
                } else if (Constants.HIDE_MESSAGE_NOTIFY.equals(intent
                        .getAction())) {// 请求离线消息
                    ArrayList<Integer> list = intent
                            .getIntegerArrayListExtra("friendID");
                    for (int i = 0; i < list.size(); i++) {
                        map.remove("" + list.get(i));
                    }
                    // 刷新适配器
                    adapter.notifyDataSetChanged();
                    // 关闭提示
                    clearNotification();
                    // 重置新消息计数器
                    msgIndex = 0;
                } else if (Constants.REQUEST_OFF_MESSAGE.equals(intent
                        .getAction())) {// 请求离线消息
                    // 向服务器发送获取为离线消息请求
                    Log.d(TAG, "向服务器发送获取为离线消息请求");
                    Constants.con.getOfflineMessage(Constants.userEbs
                            .getUserId());
                } else if (Constants.RELOGIN_USER.equals(intent.getAction())) {// 重新登录
                    Log.d(TAG, "RELOGIN_USER;");
                    // 隐藏网络不可用提示
                    netWore.setVisibility(View.GONE);
                    // 重新登录操作
                    Constants.con.login(Constants.userEbs.getUserId(),
                            Constants.userEbs.getPassword());
                } else if (Constants.START_TIMER.equals(intent.getAction())) {// 连接异常
                    Log.d(TAG, "SOCKET_UNCONNECT;");
                    rightBtn.setVisibility(View.GONE);
                    mProgress.setVisibility(View.VISIBLE);
                    nw_detail_tip.setText("网络连接不可用");
                    nw_btn.setVisibility(View.GONE);
                    nw_detail.setVisibility(View.GONE);
                    close_icon.setVisibility(View.GONE);
                    netWore.setVisibility(View.VISIBLE);
                    // 启动重新连接服务
                    Intent service = new Intent(mContext, HttpServer.class);
                    startService(service);
                } else if (Constants.SOCKET_CONNECTION.equals(intent
                        .getAction())) {
                    boolean isConnect = intent.getBooleanExtra("isConnect",
                            false);
                    Log.d(TAG, "SOCKET_CONNECTION=="+ isConnect);
                    if (isConnect) {
                            MyAsyncTask task = new MyAsyncTask(MainMessage.this);
                            task.execute();
                    } else {
                        // 启动连接
                        Constants.con = Communication.newInstance(MainMessage.this);
                    }
                }
            }
        };
        registerReceiver(MyBroadCastReceiver, filter);

		/*
         * 从intent中获得extr 如果relogin是true需要进行登录 如果是false不需要登录
		 */
        if (getIntent().getExtras() != null) {
            intent = getIntent();
            reLogin = intent.getBooleanExtra("reLogin", false);
        }

        // 实例化DButil
        dao = DBUtil.getInstance(mContext);
        // 白颜色
        int white = getResources().getColor(R.color.white);
        leftBtn.setTextColor(white);
        leftBtn.setText("返回");
        title.setText("信息");
        rightBtn.setImageResource(R.drawable.mm_title_btn_compose_normal);
        rightBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Constants.EXIT_ACTIVITY);
//                sendBroadcast(intent);
                Intent intent = new Intent(MainMessage.this, ExitAppMenu.class);
                startActivity(intent);
            }
        });
        rightBtn.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMessage.this,
                        GroupCardSelect.class);
                intent.putExtra("sendState", Config.MESSAGE_TYPE_IMS);
                startActivity(intent);
                getParent().overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_empty_out);
            }
        });

        rightBtn.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                // 长按开启短信发送
                Intent intent = new Intent(MainMessage.this,
                        GroupCardSelect.class);
                intent.putExtra("sendState", Config.MESSAGE_TYPE_SMS);
                startActivity(intent);
                getParent().overridePendingTransition(R.anim.push_up_in,
                        R.anim.push_empty_out);
                return false;
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                isSearch = true;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (TextUtils.isEmpty(s)) {
                    cleanBtn.setVisibility(View.INVISIBLE);
                    search_chat_content_lv.setAdapter(adapter);// 存放原始数据
                    isSearch = false;// 短信搜索
                } else {
                    cleanBtn.setVisibility(View.VISIBLE);
                    search_chat_content_lv.setVisibility(View.VISIBLE);
                    searchMsgList = dao.getMessage(context, searchEdit
                            .getText().toString(), Constants.userEbs
                            .getUserId());
                    if (searchMsgList.size() > 0) {
                        empty_search_result_tv.setVisibility(View.GONE);
                        search_chat_content_lv.setVisibility(View.VISIBLE);
                        // 查询数据库中短信息分组
                        MessageAdapter smsAdapter = new MessageAdapter(context,
                                searchMsgList, dao);
                        search_chat_content_lv.setAdapter(smsAdapter);
                    } else {
                        empty_search_result_tv.setVisibility(View.VISIBLE);
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
                empty_search_result_tv.setVisibility(View.GONE);
                search_chat_content_lv.setVisibility(View.VISIBLE);
                search_chat_content_lv.setAdapter(adapter);// 填充原始数据
                isSearch = false;// 短信搜索
            }
        });
        netWore.setVisibility(View.GONE);
        search_chat_content_lv.setVisibility(View.VISIBLE);
        empty_search_result_tv.setText("无结果");

        Constants.userEbs = dao.getUserEbs();// 查询用户信息的账号密码

        // 查询信息添加适配器
        message = dao.getMessage(this, Constants.userEbs.getUserId());
        // 查询未读消息
        // countArray = dao.getUnReadMessage(mContext, message);
        // 实例化适配器
        adapter = new MessageAdapter(MainMessage.this, message, dao);
        // adapter = new MessageAdapter(MainMessage.this, message, countArray,
        // dao);
        // 填充适配器
        search_chat_content_lv.setAdapter(adapter);

        search_chat_content_lv
                .setOnItemClickListener(new OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        list = new ArrayList<String>();

                        if (!isSearch) {
                            list.add(message.get(position).getFriend());
                        } else {
                            list.add(searchMsgList.get(position).getFriend());
                        }

                        Intent intent = new Intent(mContext,
                                FMessageConversationUI.class);
                        intent.putStringArrayListExtra("list", list);
                        intent.putExtra("sendState", Config.MESSAGE_TYPE_IMS);
                        startActivity(intent);

                        if (notificationManager != null) {
                            clearNotification();
                        }
                    }
                });


    }

    // 键盘按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL) {// 点击删除按钮
            if (searchEdit.getText().toString().length() > 0) {
                searchMsgList = dao.getMessage(context, searchEdit.getText()
                        .toString(), Constants.userEbs.getUserId());
                if (searchMsgList.size() > 0) {
                    empty_search_result_tv.setVisibility(View.GONE);
                    search_chat_content_lv.setVisibility(View.VISIBLE);
                    MessageAdapter smsAdapter = new MessageAdapter(context,
                            searchMsgList, dao);// 查询数据库中短信息分组
                    search_chat_content_lv.setAdapter(smsAdapter);
                } else {
                    empty_search_result_tv.setVisibility(View.VISIBLE);
                    search_chat_content_lv.setVisibility(View.GONE);
                }
            } else {
                empty_search_result_tv.setVisibility(View.GONE);
                search_chat_content_lv.setVisibility(View.VISIBLE);
                search_chat_content_lv.setAdapter(adapter);// 存放原始数据
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 获取
            // Intent home = new Intent(Intent.ACTION_MAIN);
            // home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // home.addCategory(Intent.CATEGORY_HOME);
            // startActivity(home);
//            Intent intent = new Intent(Constants.EXIT_ACTIVITY);
//            sendBroadcast(intent);
            Intent intent = new Intent(context, ExitAppMenu.class);
            startActivity(intent);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) { // 获取 Menu键
            Intent intent = new Intent(context, ExitAppMenu.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyBroadCastReceiver != null) {
            unregisterReceiver(MyBroadCastReceiver);
        }
        if (mySmsReceiver != null) {
            unregisterReceiver(mySmsReceiver);
        }
    }

    /*
     * 异步任务 后台任务前 后台任务中 后台任务结束
     */
    class MyAsyncTask extends AsyncTask<Void, Integer, Integer>
    {

        // private boolean loginState;
        private Context context;

        // private LoginEbsEntity loginUser;

        public MyAsyncTask(Context c) {
            this.context = c;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            rightBtn.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);

            // loginUser = dao.getLoginUserEbs("0");
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected Integer doInBackground(Void... params) {
            String password = Constants.userEbs.getPassword();
            Constants.con.login(Constants.userEbs.getUserId(), password);
            // System.out.println("求情登陆");
            // 加密密码
//            password = DES.encrypt("password=" + password);
//            // 创建请求体
//            Map<String, String> httpParams = new HashMap<String, String>();
//            httpParams.put("act", "login");
//            httpParams.put("account", userCode);
//            httpParams.put("pmt", password);
//            httpParams.put("clientId", "gtmobile");
//            // 发送请求
//            connection.asyncConnect(Constants.HTTP_REQUEST_HEARD, httpParams,
//                    HttpMethod.POST, new LoginCallBack(), context);
            return null;
        }

        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(Integer integer) {

        }

        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    /**
     * 在状态栏显示通知
     */
    private void showNotification(int friendCount, ArrayList<String> list,
                                  String friendNick, int messageCount) {
        // 创建一个NotificationManager的引用
        notificationManager = (NotificationManager) this
                .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        // 定义Notification的各种属性
        Notification notification = new Notification(R.drawable.icon_notify,
                "TBSIMS", System.currentTimeMillis());// R.drawable.icon_notify,
        // "TBSIMS",
        // System.currentTimeMillis()
        // FLAG_AUTO_CANCEL 该通知能被状态栏的清除按钮给清除掉
        // FLAG_NO_CLEAR 该通知不能被状态栏的清除按钮给清除掉
        // FLAG_ONGOING_EVENT 通知放置在正在运行
        // FLAG_INSISTENT 是否一直进行，比如音乐一直播放，知道用户响应
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_ONGOING_EVENT; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        // DEFAULT_ALL 使用所有默认值，比如声音，震动，闪屏等等
        // DEFAULT_LIGHTS 使用默认闪光提示
        // DEFAULT_SOUNDS 使用默认提示声音
        // DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission
        // android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_ALL;
        // notification.defaults =
        // Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;// 叠加效果常量
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS = 5000; // 闪光时间，毫秒

        // 设置通知的事件消息
        CharSequence contentTitle = "";
        CharSequence contentText = "";
        if (friendCount == 1) {
            contentTitle = friendNick; // 通知栏标题
            contentText = "发来" + messageCount + "条消息"; // 通知栏内容
        } else if (friendCount > 0) {
            contentTitle = "TBSIMS"; // 通知栏标题
            contentText = "" + friendCount + "个联系人发来" + messageCount + "条消息"; // 通知栏内容
        }

        // RemoteViews contentView = new RemoteViews(getPackageName(),
        // R.layout.chat_notiifcation);
        // contentView.setImageViewResource(R.id.imageview, R.drawable.icon);
        // contentView.setTextViewText(R.id.textview, contentTitle);
        // contentView.setTextViewText(R.id.textview1,
        // ""+System.currentTimeMillis());
        // contentView.setTextViewText(R.id.textview2, contentText);
        // notification.contentView = contentView;

        // notification.icon = R.drawable.icon_notify;

        // 点击该通知后要跳转的Activity
        Intent notificationIntent = new Intent(mContext,
                FMessageConversationUI.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putStringArrayListExtra("list", list);
        notificationIntent.putExtra("sendState", Config.MESSAGE_TYPE_IMS);

        PendingIntent contentItent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(mContext, contentTitle, contentText,
                contentItent);

        // 把Notification传递给NotificationManager
        notificationManager.notify(8888, notification);

        // notification.icon = R.drawable.icon_notify;

        // 把Notification传递给NotificationManager
        // notificationManager.notify(8888, notification);
    }

    // 删除通知
    private void clearNotification() {
        // 启动后删除之前我们定义的通知
        if (notificationManager != null) {
            notificationManager.cancel(8888);
        }
    }
}
