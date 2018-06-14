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

public class SkydriveSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;
    private RelativeLayout skyDrive_layout;
    private RelativeLayout skydrive_show_layout;
    private CheckBox skydrive_show_box;
    private EditText skydrive_address;
    private EditText skydrive_port;
    private EditText skydrive_path;
    private RelativeLayout skydrive_2_show_layout;
    private CheckBox skydrive_2_show_box;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skydrive_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("云盘设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initSkydrive();
    }
    private void initPath(){
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

    private void initSkydrive(){
        skyDrive_layout = (RelativeLayout) findViewById(R.id.skyDrive_layout);
        skydrive_show_layout = (RelativeLayout) findViewById(R.id.skydrive_show_layout);
        skydrive_show_box = (CheckBox) findViewById(R.id.skydrive_show_box);
        skydrive_2_show_layout = (RelativeLayout) findViewById(R.id.skydrive_2_show_layout);
        skydrive_2_show_box = (CheckBox) findViewById(R.id.skydrive_2_show_box);
        skydrive_address = (EditText) findViewById(R.id.skydrive_address);
        skydrive_port = (EditText) findViewById(R.id.skydrive_port);
        skydrive_path = (EditText) findViewById(R.id.skydrive_path);

        skydrive_address.setText(m_iniFileIO.getIniString(userIni, "Skydrive",
                "skydriveAddress", constants.DefaultServerIp, (byte) 0));
        skydrive_port.setText(m_iniFileIO.getIniString(userIni, "Skydrive",
                "skydrivePort", constants.DefaultServerPort, (byte) 0));
        skydrive_path.setText(m_iniFileIO.getIniString(userIni, "Skydrive",
                "skydrivePath", "/SkyDrive/MySkyDrive.cbs", (byte) 0));

        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
                "myDrive_show_in_menu", "0", (byte) 0));
        if (nVal == 1) {
            skydrive_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
                "skydrive_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            skydrive_2_show_box.setChecked(true);
        }
        skyDrive_layout.setOnClickListener(this);
        skydrive_show_layout.setOnClickListener(this);
        skydrive_2_show_layout.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
    private void saveEt(){
        String address_editTxt = String
                .valueOf(skydrive_address.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查我的云盘地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                skydrive_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "我的云盘地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            skydrive_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(skydrive_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "我的云盘端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            skydrive_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Skydrive",
                "skydriveAddress", skydrive_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Skydrive",
                "skydrivePort", skydrive_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Skydrive",
                "skydrivePath", skydrive_path.getText()
                        .toString());
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                saveEt();
                break;
            case R.id.skyDrive_layout:
                String address_editTxt = String
                        .valueOf(skydrive_address.getText());
                if (null !=address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查我的云盘地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        skydrive_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "我的云盘地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    skydrive_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(skydrive_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "我的云盘端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    skydrive_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Skydrive",
                        "skydriveAddress", skydrive_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Skydrive",
                        "skydrivePort", skydrive_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Skydrive",
                        "skydrivePath", skydrive_path.getText()
                                .toString());
                Intent intent  = new Intent();
                intent.setClass(this, MySkyDriveActivity.class);
                startActivity(intent);
                break;
            case R.id.skydrive_show_layout:
                if (skydrive_show_box.isChecked()) {
                    skydrive_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Skydrive", "myDrive_show_in_menu", "0");
                } else {
                    skydrive_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Skydrive", "myDrive_show_in_menu", "1");
                }
                break;
            case R.id.skydrive_2_show_layout:
                if (skydrive_2_show_box.isChecked()) {
                    skydrive_2_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Skydrive", "skydrive_show_in_set", "0");
                } else {
                    skydrive_2_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Skydrive", "skydrive_show_in_set", "1");
                }
                break;
        }
    }
}
