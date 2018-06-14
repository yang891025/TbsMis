package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class SubscribeSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;

    private RelativeLayout subscribe_manage;
    private RelativeLayout my_subscribe;
    private RelativeLayout subscribe_show_layout;
    private CheckBox subscribe_show_box;
    private RelativeLayout mysubscribe_show_layout;
    private CheckBox mysubscribe_show_box;
    private EditText subscribe_address;
    private EditText subscribe_port;
    private EditText subscribe_path;
    private EditText mysubscribe_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribe_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        title.setText("订阅设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initOffline();
    }

    private void initPath() {
        m_iniFileIO = new IniFile();
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
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }

    private void initOffline() {
        subscribe_manage = (RelativeLayout) findViewById(R.id.subscribe_manage);
        my_subscribe = (RelativeLayout) findViewById(R.id.my_subscribe);
        subscribe_show_layout = (RelativeLayout) findViewById(R.id.subscribe_show_layout);
        subscribe_show_box = (CheckBox) findViewById(R.id.subscribe_show_box);
        mysubscribe_show_layout = (RelativeLayout) findViewById(R.id.mysubscribe_show_layout);
        mysubscribe_show_box = (CheckBox) findViewById(R.id.mysubscribe_show_box);
        subscribe_address = (EditText) findViewById(R.id.subscribe_address);
        subscribe_port = (EditText) findViewById(R.id.subscribe_port);
        subscribe_path = (EditText) findViewById(R.id.subscribe_path);
        mysubscribe_path = (EditText) findViewById(R.id.subscribed_path);
        subscribe_address.setText(m_iniFileIO.getIniString(userIni,
                "Subscribe", "subscribeAddress", constants.DefaultServerIp, (byte) 0));
        subscribe_port.setText(m_iniFileIO.getIniString(userIni,
                "Subscribe", "subscribePort", constants.DefaultServerPort, (byte) 0));
        subscribe_path.setText(m_iniFileIO.getIniString(userIni,
                "Subscribe", "subscribePath", "/TBSInfo/dy_list.cbs", (byte) 0));
        mysubscribe_path.setText(m_iniFileIO.getIniString(userIni,
                "Subscribe", "mysubscribePath", "/TBSInfo/ListDir.cbs?opentype=dir", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Subscribe",
                "subscribeSetting", "0", (byte) 0));
        if (nVal == 1) {
            subscribe_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Subscribe",
                "mySubscribe_show_in_my", "0", (byte) 0));
        if (nVal == 1) {
            mysubscribe_show_box.setChecked(true);
        }
        my_subscribe.setOnClickListener(this);
        subscribe_manage.setOnClickListener(this);
        subscribe_show_layout.setOnClickListener(this);
        mysubscribe_show_layout.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    private void saveET() {
        String address_editTxt = String
                .valueOf(subscribe_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                subscribe_address.setFocusable(true);
                return;
            }
        } else {
            Toast.makeText(this,
                    "地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            subscribe_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(subscribe_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            subscribe_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Subscribe",
                "subscribeAddress", subscribe_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Subscribe",
                "subscribePort", subscribe_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Subscribe",
                "subscribePath", subscribe_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Subscribe",
                "mySubscribePath", mysubscribe_path.getText()
                        .toString());
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                saveET();
                break;
            case R.id.subscribe_manage:
                String address_editTxt = String
                        .valueOf(subscribe_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查订阅配置地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        subscribe_address.setFocusable(true);
                        return;
                    }
                } else {
                    Toast.makeText(this,
                            "请检查订阅配置地址的正确性", Toast.LENGTH_SHORT)
                            .show();
                    subscribe_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(subscribe_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "订阅配置端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    subscribe_port.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(subscribe_path.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "订阅管理路径不可为空", Toast.LENGTH_SHORT)
                            .show();
                    subscribe_path.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Subscribe",
                        "subscribeAddress", subscribe_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Subscribe",
                        "subscribePort", subscribe_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Subscribe",
                        "subscribePath", subscribe_path.getText()
                                .toString());
                Intent store = new Intent();
                store.setClass(this, SubscribeActivity.class);
                startActivity(store);
                break;
            case R.id.my_subscribe:
                String myaddress_editTxt = String
                        .valueOf(subscribe_address.getText());
                if (null != myaddress_editTxt
                        && !myaddress_editTxt.equals("")
                        && !myaddress_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(myaddress_editTxt)) {
                        Toast.makeText(this,
                                "请检查订阅配置地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        subscribe_address.setFocusable(true);
                        return;
                    }
                } else {
                    Toast.makeText(this,
                            "请检查订阅配置地址的正确性", Toast.LENGTH_SHORT)
                            .show();
                    subscribe_address.setFocusable(true);
                    return;
                }
                myaddress_editTxt = String
                        .valueOf(subscribe_port.getText());
                if (StringUtils.isEmpty(myaddress_editTxt)) {
                    Toast.makeText(this,
                            "订阅配置端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    subscribe_port.setFocusable(true);
                    return;
                }
                myaddress_editTxt = String
                        .valueOf(mysubscribe_path.getText());
                if (StringUtils.isEmpty(myaddress_editTxt)) {
                    Toast.makeText(this,
                            "我的订阅路径不可为空", Toast.LENGTH_SHORT)
                            .show();
                    mysubscribe_path.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Subscribe",
                        "subscribeAddress", subscribe_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Subscribe",
                        "subscribePort", subscribe_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Subscribe",
                        "mySubscribePath", mysubscribe_path.getText()
                                .toString());
                Intent mystore = new Intent();
                mystore.setClass(this, MySubscribeActivity.class);
                startActivity(mystore);
                break;
            case R.id.subscribe_show_layout:
                if (subscribe_show_box.isChecked()) {
                    subscribe_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Subscribe", "subscribeSetting", "0");
                } else {
                    subscribe_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Subscribe", "subscribeSetting", "1");
                }
                break;
            case R.id.mysubscribe_show_layout:
                if (mysubscribe_show_box.isChecked()) {
                    mysubscribe_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Subscribe", "mySubscribe_show_in_my", "0");
                } else {
                    mysubscribe_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Subscribe", "mySubscribe_show_in_my", "1");
                }
                break;
        }
    }
}
