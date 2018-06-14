package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.LoginDialogResult;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/14.
 */

public class SystemSetupActivityNew extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private TextView login_type_txt;
    private Switch login_type_open_box;
    private LinearLayout system_msg;
    private LinearLayout login_setup;
    private LinearLayout push_setup;
    private LinearLayout interface_setup;


    private LinearLayout offline_setup;
    private LinearLayout software_setup;
    private LinearLayout other_setup;

    private IniFile IniFile;
    private String appIniFile;
    private String userIni;

    private LinearLayout manager_setup;
    private LinearLayout module_manager;
    private LinearLayout terminal_app_setup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_setup_new);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        system_msg = (LinearLayout) findViewById(R.id.system_msg);
        login_setup = (LinearLayout) findViewById(R.id.login_setup);
        push_setup = (LinearLayout) findViewById(R.id.push_setup);
        interface_setup = (LinearLayout) findViewById(R.id.interface_setup);
        manager_setup = (LinearLayout) findViewById(R.id.manager_setup);
        module_manager = (LinearLayout) findViewById(R.id.module_manager);
        offline_setup = (LinearLayout) findViewById(R.id.offline_setup);
        software_setup = (LinearLayout) findViewById(R.id.software_setup);
        other_setup = (LinearLayout) findViewById(R.id.other_setup);
        terminal_app_setup = (LinearLayout) findViewById(R.id.terminal_app_setup);
        title.setText(R.string.system_set);
        system_msg.setOnClickListener(this);
        login_setup.setOnClickListener(this);
        push_setup.setOnClickListener(this);
        interface_setup.setOnClickListener(this);
        manager_setup.setOnClickListener(this);
        offline_setup.setOnClickListener(this);
        module_manager.setOnClickListener(this);
        software_setup.setOnClickListener(this);
        other_setup.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        terminal_app_setup.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        IniFile = new IniFile();
        initPath();
        login_type_open_box = (Switch) findViewById(R.id.login_type_open_box);
        login_type_txt = (TextView) findViewById(R.id.login_type_txt);
        int nVal = Integer.parseInt(IniFile.getIniString(appIniFile, "Login",
                "LoginType", "0", (byte) 0));
        if (nVal == 1) {
            login_type_open_box.setChecked(true);
            login_type_txt.setText("正在使用系统配置");
        } else {
            login_type_open_box.setChecked(false);
            login_type_txt.setText("正在使用应用配置");
        }
        login_type_open_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setAction(getString(R.string.ServerName1));//你定义的service的action
                intent.setPackage(getPackageName());//这里你需要设置你应用的包名
                stopService(intent);
                SharedPreferences pre = getSharedPreferences(
                        Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                if (pre.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                    ServiceManager serviceManager = new ServiceManager(SystemSetupActivityNew.this);
                    serviceManager.EBSLogout();
                }
                if (isChecked) {
                    login_type_txt.setText("正在使用系统配置");
                    IniFile.writeIniString(appIniFile, "Login", "LoginType",
                            "1");
                    initPath();
                    IniFile.writeIniString(userIni, "Login", "LoginFlag", "0");
                } else {
                    login_type_txt.setText("正在使用应用配置");
                    IniFile.writeIniString(appIniFile, "Login", "LoginType",
                            "0");
                    initPath();
                    IniFile.writeIniString(userIni, "Login", "LoginFlag", "0");
                }
            }
        });
    }

    private void initPath() {
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
        appIniFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (Integer.parseInt(IniFile.getIniString(this.userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 0) {
            this.startActivityForResult(new Intent(this, LoginDialogResult.class), 1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode== 1 &&resultCode == 1) {
            this.initPath();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.system_msg:
                intent = new Intent();
                intent.setClass(this, SystemInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.login_setup:
                intent = new Intent();
                intent.setClass(this, LoginSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.push_setup:
                intent = new Intent();
                intent.setClass(this, PushSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.interface_setup:
                intent = new Intent();
                intent.setClass(this, InterfaceSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.store_setup:
                intent = new Intent();
                intent.setClass(this, StoreSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.terminal_app_setup:
                intent = new Intent();
                intent.setClass(this, TerminalAppSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.offline_setup:
                intent = new Intent();
                intent.setClass(this, OfflineSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.software_setup:
                intent = new Intent();
                intent.setClass(this, AideSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.other_setup:
                intent = new Intent();
                intent.setClass(this, OtherSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.manager_setup:
                intent = new Intent();
                intent.setClass(this, ManagerSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.module_manager:
                intent = new Intent();
                intent.setClass(this, ModuleManagerActivity.class);
                startActivity(intent);
                break;
        }
    }
}
