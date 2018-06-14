package com.tbs.chat.ui.conversation;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.dialog.Exit;
import com.tbs.chat.socket.Communication;
import com.tbs.chat.ui.address.MainAddress;
import com.tbs.chat.ui.chatting.MainMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainTab extends TabActivity implements OnCheckedChangeListener
{

    protected static final String TAG = "MainTab";

    private final Context mContext = this;
    private LinearLayout main_tab_group;
    private RadioButton smsBtn;
    private RadioButton addressBtn;
    private Intent messageIntent;
    private Intent addressIntent;
    private TabHost tabHost;
    private Resources res;
    private BroadcastReceiver MyBroadCastReceiver;
    private boolean reLogin;
    private int toolBtn;
    private Intent intent;
    private Dialog dialog;
    private TextView main_tab_unread_tv;
    private Map<String, Integer> map = new HashMap<String, Integer>();
    private DBUtil dao = null;
    private int msgIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        init();
    }

    private void init() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.EXIT_ACTIVITY);
        filter.addAction(Constants.OPEN_EXIT_DIALOG);
        filter.addAction(Constants.REFRESH_MESSAGE);
        filter.addAction(Constants.HIDE_MESSAGE_NOTIFY);
        filter.addAction(Constants.LOGIN_REPEAT_RESOULT);
        filter.addCategory("default");
        MyBroadCastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Constants.EXIT_ACTIVITY.equals(intent.getAction())) {
                    exit();
                    finish();
                } else if (Constants.OPEN_EXIT_DIALOG
                        .equals(intent.getAction())) {
                    Intent openDialogIntent = new Intent();
                    openDialogIntent.setClass(MainTab.this, Exit.class);
                    startActivityForResult(openDialogIntent, 100);
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
                    }
                    if (msgIndex > 0) {
                        main_tab_unread_tv.setText("" + msgIndex);// 填充适配器
                        main_tab_unread_tv.setVisibility(View.VISIBLE);
                    }
                } else if (Constants.HIDE_MESSAGE_NOTIFY.equals(intent
                        .getAction())) {// 请求离线消息
                    ArrayList<Integer> list = intent
                            .getIntegerArrayListExtra("friendID");
                    for (int i = 0; i < list.size(); i++) {
                        if (map != null && map.containsKey("" + list.get(i))) {
                            msgIndex = msgIndex - map.get("" + list.get(i));
                            map.remove("" + list.get(i));
                        }
                    }
                    if (msgIndex > 0) {
                        main_tab_unread_tv.setText("" + msgIndex);// 填充适配器
                        main_tab_unread_tv.setVisibility(View.VISIBLE);
                    } else {
                        main_tab_unread_tv.setText("" + msgIndex);// 填充适配器
                        main_tab_unread_tv.setVisibility(View.INVISIBLE);
                    }
                } else if (Constants.LOGIN_REPEAT_RESOULT.equals(intent
                        .getAction())) {
                    // 重新登录操作
                    Constants.con.login(Constants.userEbs.getUserId(),
                            Constants.userEbs.getPassword());
                }
            }
        };
        registerReceiver(MyBroadCastReceiver, filter);
        // 实例化dbutil
        dao = DBUtil.getInstance(mContext);
        Constants.userEbs = dao.getUserEbs();
        // 从intent中获得extr
        if (getIntent().getExtras() != null) {
            intent = getIntent();
            reLogin = intent.getBooleanExtra("reLogin", false);
            toolBtn = intent.getIntExtra("tool", 0);
        } else {
            reLogin = true;
            toolBtn = 0;
        }
        messageIntent = new Intent(this, MainMessage.class);
        messageIntent.putExtra("reLogin", reLogin);
        if (!reLogin) {
            messageIntent.putExtra("userName",
                    intent.getStringExtra("userName"));
            messageIntent.putExtra("psw", intent.getStringExtra("psw"));
        }
        addressIntent = new Intent(this, MainAddress.class);
        main_tab_group = (LinearLayout) findViewById(R.id.main_tab_group);
        smsBtn = (RadioButton) findViewById(R.id.main_tab_weixin);
        addressBtn = (RadioButton) findViewById(R.id.main_tab_address);
        main_tab_unread_tv = (TextView) findViewById(R.id.main_tab_unread_tv);

        smsBtn.setOnCheckedChangeListener(this);
        addressBtn.setOnCheckedChangeListener(this);

        res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost(); // The activity TabHost
        if(toolBtn != 0){
            main_tab_group.setVisibility(View.GONE);
        }
        tabHost.addTab(tabHost.newTabSpec("message").setIndicator(null, null)
                .setContent(messageIntent));
        tabHost.addTab(tabHost.newTabSpec("address").setIndicator(null, null)
                .setContent(addressIntent));
        if(toolBtn == 2){
            smsBtn.setChecked(false);
            addressBtn.setChecked(true);
        }
        if (Constants.con == null) {
            // 启动连接
            Constants.con = Communication.newInstance(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.main_tab_weixin) {
                tabHost.setCurrentTabByTag("message");
                addressBtn.setChecked(false);
            } else if (buttonView.getId() == R.id.main_tab_address) {
                tabHost.setCurrentTabByTag("address");
                smsBtn.setChecked(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MyBroadCastReceiver != null) {
            unregisterReceiver(MyBroadCastReceiver);
        }
        if (dialog != null && dialog.isShowing()) {
            dismissDialog();
        }
    }

    /*
     * 通用的提示对话框 弹出dialog
     */
    public void showDialog() {
        dialog = new Dialog(MainTab.this, R.style.MyDialogStyle);// 设置它的ContentView
        dialog.setContentView(R.layout.loading_exit);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismissDialog() {
        dialog.cancel();
    }

    public void exit() {
        // 关闭Socket连接、输入输出流
        if (Constants.con != null) {
            Constants.con.sendExitUser();
            Constants.con.stopWork();
            Constants.con.setInstanceNull();
            Constants.con = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                showDialog();
                mhandler.sendMessageDelayed(new Message(), 1000);
            }
        } else if (requestCode == 9000) {
            setSharePerference(Constants.LAUNCHER_PREFERENCE);
            Constants.isLogin = false;
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessageDelayed(msg, 200);
        } else if (requestCode == 1003) {
            if (resultCode == RESULT_OK) {
                Constants.isLogin = false;//重置用户登录状态
                Message msg = new Message();//发送清理请求
                msg.what = 1;
                mExithandler.sendMessageDelayed(msg, 200);
            }
        }
    }

    Handler handler = new Handler()
    {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 1) {
                Message message = new Message();
                message.what = 2;
                handler.sendMessageDelayed(message, 200);
            } else if (msg.what == 2) {
                Message message = new Message();
                message.what = 3;
                handler.sendMessageDelayed(message, 200);
            } else if (msg.what == 3) {
                Intent intent = new Intent(Constants.EXIT_ACTIVITY);
                sendBroadcast(intent);
            }
        }
    };

    Handler mExithandler = new Handler()
    {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if (msg.what == 1) {
                Message message = new Message();
                message.what = 2;
                mExithandler.sendMessageDelayed(message, 200);
            } else if (msg.what == 2) {
                Intent intent = new Intent(Constants.EXIT_ACTIVITY);
                sendBroadcast(intent);
            }
        }
    };

    /**
     * 保存内容到shareperence 设置第一次登录属性为true
     */
    public void setSharePerference(String perferenceName) {
        SharedPreferences setting = getSharedPreferences(perferenceName,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean("loginState", false);
        editor.commit();
    }

    Handler mhandler = new Handler()
    {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            dismissDialog();
            Intent intent = new Intent(Constants.EXIT_ACTIVITY);
            sendBroadcast(intent);
        }
    };

}