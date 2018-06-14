package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.Live.LiveSetupActivity;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/3/13.
 */

public class ManagerSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private RelativeLayout application_show_layout;
    private CheckBox application_show_box;
    private RelativeLayout application_setup;
    private RelativeLayout terminal_show_layout;
    private CheckBox terminal_show_box;
    private RelativeLayout terminal_setup;
    private RelativeLayout device_show_layout;
    private CheckBox device_show_box;
    private RelativeLayout device_setup;

    private RelativeLayout storage_show_layout;
    private CheckBox storage_show_box;
    private RelativeLayout storage_setup;
    private IniFile m_iniFileIO;
    private String userIni;

    private LinearLayout backup_setup;
    private LinearLayout file_setup;
    private LinearLayout skydrive_setup;
    private RelativeLayout app_root_layout;
    private CheckBox app_root_box;
    private RelativeLayout app_path_layout;
    private CheckBox app_path_box;
    private LinearLayout live_setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_setup_layout);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        title.setText("管理设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initSet();
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

    private void initSet() {
        application_show_layout = (RelativeLayout) findViewById(R.id.application_show_layout);
        application_show_box = (CheckBox) findViewById(R.id.application_show_box);
        app_root_layout = (RelativeLayout) findViewById(R.id.app_root_layout);
        app_root_box = (CheckBox) findViewById(R.id.app_root_box);
        app_path_layout = (RelativeLayout) findViewById(R.id.app_path_layout);
        app_path_box = (CheckBox) findViewById(R.id.app_path_box);
        application_setup = (RelativeLayout) findViewById(R.id.application_setup);
        terminal_show_layout = (RelativeLayout) findViewById(R.id.terminal_show_layout);
        terminal_show_box = (CheckBox) findViewById(R.id.terminal_show_box);
        terminal_setup = (RelativeLayout) findViewById(R.id.terminal_setup);
        device_show_layout = (RelativeLayout) findViewById(R.id.device_show_layout);
        device_show_box = (CheckBox) findViewById(R.id.device_show_box);
        device_setup = (RelativeLayout) findViewById(R.id.device_setup);

        storage_show_layout = (RelativeLayout) findViewById(R.id.storage_show_layout);
        storage_show_box = (CheckBox) findViewById(R.id.storage_show_box);
        storage_setup = (RelativeLayout) findViewById(R.id.storage_setup);
        live_setup = (LinearLayout) findViewById(R.id.live_setup);
        skydrive_setup = (LinearLayout) findViewById(R.id.skydrive_setup);
        backup_setup = (LinearLayout) findViewById(R.id.backup_setup);
        file_setup = (LinearLayout) findViewById(R.id.file_setup);

        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "application_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            application_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "terminal_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            terminal_show_box.setChecked(true);
        }

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "device_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            device_show_box.setChecked(true);
        }

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "storage_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            storage_show_box.setChecked(true);
        }

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "TBSAPP",
                "app_root_show", "1", (byte) 0));
        if (nVal == 1) {
            app_root_box.setChecked(true);
        }

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "TBSAPP",
                "app_path_show", "0", (byte) 0));
        if (nVal == 1) {
            app_path_box.setChecked(true);
        }
        application_show_layout.setOnClickListener(this);
        app_root_layout.setOnClickListener(this);
        app_path_layout.setOnClickListener(this);
        application_setup.setOnClickListener(this);
        terminal_show_layout.setOnClickListener(this);
        terminal_setup.setOnClickListener(this);
        device_show_layout.setOnClickListener(this);
        device_setup.setOnClickListener(this);

        storage_show_layout.setOnClickListener(this);
        storage_setup.setOnClickListener(this);
        live_setup.setOnClickListener(this);
        skydrive_setup.setOnClickListener(this);
        backup_setup.setOnClickListener(this);
        file_setup.setOnClickListener(this);
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
                finish();
                break;
            case R.id.application_show_layout:
                if (application_show_box.isChecked()) {
                    application_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "application_show_in_set", "0");
                } else {
                    application_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "application_show_in_set", "1");
                }
                break;
            case R.id.app_root_layout:
                if (app_root_box.isChecked()) {
                    app_root_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "TBSAPP", "app_root_show", "0");
                } else {
                    app_root_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "TBSAPP", "app_root_show", "1");
                }
                break;
            case R.id.app_path_layout:
                if (app_path_box.isChecked()) {
                    app_path_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "TBSAPP", "app_path_show", "0");
                } else {
                    app_path_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "TBSAPP", "app_path_show", "1");
                }
                break;
            case R.id.terminal_show_layout:
                if (terminal_show_box.isChecked()) {
                    terminal_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "terminal_show_in_set", "0");
                } else {
                    terminal_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "terminal_show_in_set", "1");
                }
                break;
            case R.id.device_show_layout:
                if (device_show_box.isChecked()) {
                    device_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "device_show_in_set", "0");
                } else {
                    device_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "device_show_in_set", "1");
                }
                break;
            case R.id.storage_show_layout:
                if (storage_show_box.isChecked()) {
                    storage_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "storage_show_in_set", "0");
                } else {
                    storage_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "storage_show_in_set", "1");
                }
                break;
            case R.id.terminal_setup:
                Intent intent = new Intent();
                intent.setClass(this, TerminalSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.application_setup:
                Intent application_intent = new Intent();
                application_intent.setClass(this, AppSetupActivity.class);
                startActivity(application_intent);
                break;
            case R.id.device_setup:
                Intent device_intent = new Intent();
                device_intent.setClass(this, DeviceManagerActivity.class);
                startActivity(device_intent);
                break;
            case R.id.live_setup:
                Intent live_intent = new Intent();
                live_intent.setClass(this, LiveSetupActivity.class);
                startActivity(live_intent);
                break;
            case R.id.storage_setup:
                Intent storage_intent = new Intent();
                storage_intent.setClass(this, StorageSetupActivity.class);
                startActivity(storage_intent);
                break;
            case R.id.backup_setup:
                intent = new Intent();
                intent.setClass(this, BackupSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.file_setup:
                intent = new Intent();
                intent.setClass(this, FileSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.skydrive_setup:
                intent = new Intent();
                intent.setClass(this, SkydriveSetupActivity.class);
                startActivity(intent);
                break;

        }
    }
}
