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

public class BackupSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;
    private RelativeLayout backup_upload;
    private RelativeLayout backup_show_layout;
    private CheckBox backup_show_box;
    private IniFile m_iniFileIO;
    private String userIni;

    private EditText personal_address;
    private EditText personal_port;
    private EditText personal_path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        title.setText("备份设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        backup_upload = (RelativeLayout) findViewById(R.id.backup_upload);
        backup_show_layout = (RelativeLayout) findViewById(R.id.backup_show_layout);
        backup_show_box = (CheckBox) findViewById(R.id.backup_show_box);
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Backup",
                "backup_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            backup_show_box.setChecked(true);
        }
        backup_upload.setOnClickListener(this);
        backup_show_layout.setOnClickListener(this);
        initpersonalback();
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

    private void initpersonalback() {
        personal_address = (EditText) findViewById(R.id.personal_address);
        personal_port = (EditText) findViewById(R.id.personal_port);
        personal_path = (EditText) findViewById(R.id.personal_path);
        personal_address.setText(m_iniFileIO.getIniString(userIni,
                "Backup", "personalAddress", constants.DefaultServerIp, (byte) 0));
        personal_port.setText(m_iniFileIO.getIniString(userIni,
                "Backup", "personalPort", constants.DefaultServerPort, (byte) 0));
        personal_path.setText(m_iniFileIO.getIniString(userIni,
                "Backup", "personalPath", "/SkyDrive/SaveFile.cbs", (byte) 0));
    }

    private void saveEt() {
        String address_editTxt = String
                .valueOf(personal_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查接受地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                personal_address.setFocusable(true);
                return;
            }
        } else {
            Toast.makeText(this,
                    "接受地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            personal_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(personal_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "发送地址端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            personal_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Backup",
                "personalAddress", personal_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Backup",
                "personalPort", personal_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Backup",
                "personalPath", personal_path.getText()
                        .toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                saveEt();
                finish();
                break;
            case R.id.backup_upload:
                saveEt();
                Intent intent = new Intent();
                intent.putExtra("systemSet", false);
                intent.setClass(this, MyBackupActivity.class);
                startActivity(intent);
                break;
            case R.id.backup_show_layout:
                if (backup_show_box.isChecked()) {
                    backup_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Backup", "backup_show_in_set", "0");
                } else {
                    backup_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Backup", "backup_show_in_set", "1");
                }
                break;
        }
    }
}
