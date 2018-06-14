package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.weixin.WeixinActivity;

/**
 * Created by TBS on 2017/3/29.
 */

public class FunctionActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;
    private IniFile m_iniFileIO;
    private String userIni;

    private RelativeLayout manager_setup;
    private RelativeLayout file_manager;
    private RelativeLayout device_manager;
    private RelativeLayout backup_manager;
    private RelativeLayout weixin_manager;

    private RelativeLayout skydrive_manager;
    private RelativeLayout manager_2_setup;
    private RelativeLayout module_msi;
    private RelativeLayout email_manager;
    private RelativeLayout storage_backup_setup;
    private RelativeLayout storage_manager;
    private RelativeLayout terminal_setup;
    private RelativeLayout collect_manager;
    private RelativeLayout subscribe_manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_layout);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        title.setText("模块管理");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);

        manager_2_setup = (RelativeLayout) findViewById(R.id.manager_2_setup);
        weixin_manager = (RelativeLayout) findViewById(R.id.weixin_manager);
        module_msi = (RelativeLayout) findViewById(R.id.module_msi);
        email_manager = (RelativeLayout) findViewById(R.id.email_manager);

        manager_setup = (RelativeLayout) findViewById(R.id.manager_setup);
        file_manager = (RelativeLayout) findViewById(R.id.file_manager);
        device_manager = (RelativeLayout) findViewById(R.id.device_manager);
        skydrive_manager = (RelativeLayout) findViewById(R.id.skydrive_2_manager);

        storage_backup_setup = (RelativeLayout) findViewById(R.id.storage_backup_setup);
        storage_manager= (RelativeLayout) findViewById(R.id.storage_manager);
        backup_manager = (RelativeLayout) findViewById(R.id.backup_manager);
        terminal_setup = (RelativeLayout) findViewById(R.id.terminal_setup);

        subscribe_manager = (RelativeLayout) findViewById(R.id.subscribe_manager);
        collect_manager = (RelativeLayout) findViewById(R.id.collect_manager);

        file_manager.setOnClickListener(this);
        device_manager.setOnClickListener(this);
        storage_manager.setOnClickListener(this);
        backup_manager.setOnClickListener(this);
        terminal_setup.setOnClickListener(this);
        module_msi.setOnClickListener(this);
        email_manager.setOnClickListener(this);
        weixin_manager.setOnClickListener(this);
        skydrive_manager.setOnClickListener(this);
        collect_manager.setOnClickListener(this);
        subscribe_manager.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPath();
        initSetUp();
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

    private void initSetUp() {
        
        int setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Email",
                "email_show_in_set", "0", (byte) 0));
        int setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Chat",
                "chat_show_in_set", "0", (byte) 0));
        int setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "WeiXin",
                "weixin_show_in_set", "0", (byte) 0));
        if (setup == 0 && setup2 == 0 && setup3 == 0) {
            manager_2_setup.setVisibility(View.GONE);
        } else {
            manager_2_setup.setVisibility(View.VISIBLE);
            if (setup == 0) {
                email_manager.setVisibility(View.GONE);
            } else {
                email_manager.setVisibility(View.VISIBLE);
            }
            if (setup2 == 0) {
                module_msi.setVisibility(View.GONE);
            } else {
                module_msi.setVisibility(View.VISIBLE);
            }
            if (setup3 == 0) {
                weixin_manager.setVisibility(View.GONE);
            } else {
                weixin_manager.setVisibility(View.VISIBLE);
            }
        }

        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
                "fileManager_show_in_set", "0", (byte) 0));
        setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "device_show_in_set", "0", (byte) 0));
        setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
                "skydrive_show_in_set", "0", (byte) 0));
        if (setup == 0 && setup2 == 0 && setup3 == 0) {
            manager_setup.setVisibility(View.GONE);
        } else {
            manager_setup.setVisibility(View.VISIBLE);
            if (setup == 0) {
                file_manager.setVisibility(View.GONE);
            } else {
                file_manager.setVisibility(View.VISIBLE);
            }
            if (setup2 == 0) {
                device_manager.setVisibility(View.GONE);
            } else {
                device_manager.setVisibility(View.VISIBLE);
            }
            if (setup3 == 0) {
                skydrive_manager.setVisibility(View.GONE);
            } else {
                skydrive_manager.setVisibility(View.VISIBLE);
            }
        }

        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "storage_show_in_set", "0", (byte) 0));
        setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Backup",
                "backup_show_in_set", "0", (byte) 0));
        setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "terminal_show_in_set", "0", (byte) 0));
        if (setup == 0 && setup2 == 0 & setup3 == 0) {
            storage_backup_setup.setVisibility(View.GONE);
        } else {
            storage_backup_setup.setVisibility(View.VISIBLE);
            if (setup == 0) {
                storage_manager.setVisibility(View.GONE);
            } else {
                storage_manager.setVisibility(View.VISIBLE);
            }
            if (setup2 == 0) {
                backup_manager.setVisibility(View.GONE);
            } else {
                backup_manager.setVisibility(View.VISIBLE);
            }
            if (setup3 == 0) {
                terminal_setup.setVisibility(View.GONE);
            } else {
                terminal_setup.setVisibility(View.VISIBLE);
            }
        }

        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Subscribe",
                "subscribeSetting", "0", (byte) 0));
        if (setup == 0) {
            subscribe_manager.setVisibility(View.GONE);
        } else {
            subscribe_manager.setVisibility(View.VISIBLE);
        }
        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Collect",
                "collect_show_in_menu", "0", (byte) 0));
        if (setup == 0) {
            collect_manager.setVisibility(View.GONE);
        } else {
            collect_manager.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.terminal_setup:
                intent = new Intent();
                intent.setClass(this, TerminalSetupActivity.class);
                this.startActivity(intent);
                break;
            case R.id.weixin_manager:
                intent = new Intent();
                intent.setClass(this, WeixinActivity.class);
                this.startActivity(intent);
                break;
            case R.id.skydrive_2_manager:
                UIHelper.showMyDisk(this);
//                intent = new Intent();
//                intent.setClass(this, MyBackupActivity.class);
//                this.startActivity(intent);
//                Toast.makeText(this, "未安装相关应用", Toast.LENGTH_SHORT).show();
                break;
            case R.id.storage_manager:
                intent = new Intent();
                intent.setClass(this, StorageSetupActivity.class);
                this.startActivity(intent);
                break;
            case R.id.backup_manager:
                intent = new Intent();
                intent.putExtra("systemSet", false);
                intent.setClass(this, MyBackupActivity.class);
                this.startActivity(intent);
                break;
            case R.id.module_msi:
                UIHelper.showMyChat(this);
                //Toast.makeText(this, "未安装相关应用", Toast.LENGTH_SHORT).show();
                break;
            case R.id.email_manager:
                UIHelper.showMyEmail(this);
                //Toast.makeText(this, "未安装相关应用", Toast.LENGTH_SHORT).show();
                break;
            case R.id.device_manager:
                intent = new Intent();
                intent.setClass(this, DeviceManagerActivity.class);
                this.startActivity(intent);
                break;
            case R.id.file_manager:
                intent = new Intent();
                intent.setClass(this, FileExplorerTabActivity.class);
                this.startActivity(intent);
                break;
            case R.id.subscribe_manager:
                intent = new Intent();
                intent.setClass(this, SubscribeActivity.class);
                this.startActivity(intent);
                break;
            case R.id.collect_manager:
                Toast.makeText(this, "暂未实现", Toast.LENGTH_SHORT).show();
                break;
            case R.id.finish_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.more_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
        }
    }
}
