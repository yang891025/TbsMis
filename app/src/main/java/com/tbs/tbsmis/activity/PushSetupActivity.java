package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.tbs.tbsmis.notification.NotificationSettingsActivity;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class PushSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String webRoot;
    private String userIni;

    private RelativeLayout msg_push_layout;
    private RelativeLayout push_show_layout;
    private CheckBox push_show_box;
    private EditText module_push_address;
    private EditText module_push_port;
    private EditText module_push_path;
    private Button app_send_push;
    private RelativeLayout send_show_layout;
    private CheckBox send_show_box;
    private EditText module_send_address;
    private EditText module_send_port;
    private EditText module_send_path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("推送设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initPush();
        initSend();
    }

    private void initPath(){
        m_iniFileIO = new IniFile();
        webRoot = UIHelper.getSoftPath(this);
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
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }
    private void initPush(){
        msg_push_layout = (RelativeLayout) findViewById(R.id.msg_push_layout);
        push_show_layout = (RelativeLayout) findViewById(R.id.push_show_layout);
        push_show_box = (CheckBox) findViewById(R.id.push_show_box);
        send_show_layout = (RelativeLayout) findViewById(R.id.send_show_layout);
        send_show_box = (CheckBox) findViewById(R.id.send_show_box);
        module_push_address = (EditText) findViewById(R.id.module_push_address);
        module_push_port = (EditText) findViewById(R.id.module_push_port);
        module_push_path = (EditText) findViewById(R.id.module_push_path);
        module_push_address.setText(m_iniFileIO.getIniString(userIni,
                "Push", "pushAddress", constants.DefaultServerIp, (byte) 0));
        module_push_port.setText(m_iniFileIO.getIniString(userIni,
                "Push", "pushPort", "5222", (byte) 0));
        module_push_path.setText(m_iniFileIO.getIniString(userIni,
                "Push", "pushPath", "", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Push",
                "pushSetting", "1", (byte) 0));
        if (nVal == 1) {
            push_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Push",
                "sendSetting", "0", (byte) 0));
        if (nVal == 1) {
            send_show_box.setChecked(true);
        }
        msg_push_layout.setOnClickListener(this);
        send_show_layout.setOnClickListener(this);
        push_show_layout.setOnClickListener(this);
    }
    private void initSend(){
        app_send_push = (Button) findViewById(R.id.app_send_push);
        module_send_address = (EditText) findViewById(R.id.module_send_address);
        module_send_port = (EditText) findViewById(R.id.module_send_port);
        module_send_path = (EditText) findViewById(R.id.module_send_path);
        module_send_address.setText(m_iniFileIO.getIniString(userIni,
                "Push", "sendAddress", constants.DefaultServerIp, (byte) 0));
        module_send_port.setText(m_iniFileIO.getIniString(userIni,
                "Push", "sendPort", "8080", (byte) 0));
        module_send_path.setText(m_iniFileIO.getIniString(userIni,
                "Push", "sendPath", "", (byte) 0));
        app_send_push.setOnClickListener(this);
    }
    private void saveET(){
        String address_editTxt = String
                .valueOf(module_push_address.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查接受地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                module_push_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "接受地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            module_push_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(module_push_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "发送地址端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            module_send_port.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(module_send_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查发送地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                module_send_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "发送地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            module_send_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(module_send_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "发送地址端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            module_send_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Push",
                "pushAddress", module_push_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Push",
                "pushPath", module_push_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Push",
                "pushPort", module_push_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Push",
                "sendAddress", module_send_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Push",
                "sendPath", module_send_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Push",
                "sendPort", module_send_port.getText()
                        .toString());
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
               saveET();
                break;
            case R.id.push_show_layout:
                if (push_show_box.isChecked()) {
                    push_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Push", "pushSetting", "0");
                } else {
                    push_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Push", "pushSetting", "1");
                }
                break;
            case R.id.send_show_layout:
                if (send_show_box.isChecked()) {
                    send_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Push", "sendSetting", "0");
                } else {
                    send_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Push", "sendSetting", "1");
                }
                break;
            case R.id.app_send_push:
                UIHelper.showPushPage(this);
                break;
            case R.id.msg_push_layout:
                String address_editTxt = String
                        .valueOf(module_push_address.getText());
                if (null !=address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查接受地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        module_push_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "接受地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    module_push_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(module_push_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "发送地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    module_send_port.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(module_send_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查发送地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        module_send_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "发送地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    module_send_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(module_send_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "发送地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    module_send_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Push",
                        "pushAddress", module_push_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Push",
                        "pushPath", module_push_path.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Push",
                        "pushPort", module_push_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Push",
                        "sendAddress", module_send_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Push",
                        "sendPath", module_send_path.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Push",
                        "sendPort", module_send_port.getText()
                                .toString());
                Intent intent = new Intent();
                intent.setClass(this, NotificationSettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
