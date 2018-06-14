package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.download.DownloadActivity;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.NotificationSettingsActivity;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.update.UpdateActivity;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/3/29.
 */

public class SetUpActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;
    private IniFile m_iniFileIO;
    private String userIni;

    private RelativeLayout user_setup;
    private ImageView arrows;
    private TextView username;
    private ImageView user_imageview;
    private TextView accountname;
    private Button cancellationBtn;

    private RelativeLayout general_setup;
    private RelativeLayout app_setup;
    private RelativeLayout app_manager;


  //  private RelativeLayout function_setup;
    private RelativeLayout module_push;
    private RelativeLayout module_offlinedownload;



//    private RelativeLayout manager_setup;
//    private RelativeLayout file_manager;
//    private RelativeLayout device_manager;
//    private RelativeLayout backup_manager;
//    private RelativeLayout weixin_manager;


    private RelativeLayout update_apk;
    private TextView mainsize;
    private RelativeLayout clearTemp;
    private RelativeLayout feedback;
    private RelativeLayout about;
    private RelativeLayout complete_exit;
    private RelativeLayout function_layout;
//    private RelativeLayout skydrive_manager;
//    private RelativeLayout manager_2_setup;
//    private RelativeLayout module_msi;
//    private RelativeLayout email_manager;
//    private RelativeLayout storage_backup_setup;
//    private RelativeLayout storage_manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        title.setText("设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);

        user_setup = (RelativeLayout) this.findViewById(R.id.user_setup);
        cancellationBtn = (Button) this.findViewById(R.id.cancellation_btn);
        arrows = (ImageView) this.findViewById(R.id.user_arrows);
        user_imageview = (ImageView) this.findViewById(R.id.user_imageview);
        username = (TextView) this.findViewById(R.id.usename_view);
        accountname = (TextView) this.findViewById(R.id.account_view);

        general_setup = (RelativeLayout) findViewById(R.id.general_setup);
        app_setup = (RelativeLayout) findViewById(R.id.app_setup);
        app_manager = (RelativeLayout) findViewById(R.id.app_manager);



        //function_setup = (RelativeLayout) findViewById(R.id.function_setup);
        module_push = (RelativeLayout) findViewById(R.id.module_push);
        module_offlinedownload = (RelativeLayout) findViewById(R.id.module_offlinedownload);

        function_layout = (RelativeLayout) findViewById(R.id.function_layout);
//        manager_2_setup = (RelativeLayout) findViewById(R.id.manager_2_setup);
//        weixin_manager = (RelativeLayout) findViewById(R.id.weixin_manager);
//        module_msi = (RelativeLayout) findViewById(R.id.module_msi);
//        email_manager = (RelativeLayout) findViewById(R.id.email_manager);
//
//        manager_setup = (RelativeLayout) findViewById(R.id.manager_setup);
//        file_manager = (RelativeLayout) findViewById(R.id.file_manager);
//        device_manager = (RelativeLayout) findViewById(R.id.device_manager);
//        skydrive_manager = (RelativeLayout) findViewById(R.id.skydrive_2_manager);
//
//        storage_backup_setup = (RelativeLayout) findViewById(R.id.storage_backup_setup);
//        storage_manager= (RelativeLayout) findViewById(R.id.storage_manager);
//        backup_manager = (RelativeLayout) findViewById(R.id.backup_manager);

        update_apk = (RelativeLayout) findViewById(R.id.update_apk);
        mainsize = (TextView) findViewById(R.id.tool_arrows2);
        clearTemp = (RelativeLayout) findViewById(R.id.clearTemp);
        feedback = (RelativeLayout) findViewById(R.id.feedback);
        about = (RelativeLayout) findViewById(R.id.about);

        complete_exit = (RelativeLayout) this.findViewById(R.id.complete_exit);

        cancellationBtn.setOnClickListener(this);
        function_layout.setOnClickListener(this);
        app_setup.setOnClickListener(this);
        app_manager.setOnClickListener(this);

        module_push.setOnClickListener(this);
        module_offlinedownload.setOnClickListener(this);


//        file_manager.setOnClickListener(this);
//        device_manager.setOnClickListener(this);
//        storage_manager.setOnClickListener(this);
//        backup_manager.setOnClickListener(this);
//        module_msi.setOnClickListener(this);
//        email_manager.setOnClickListener(this);
//        weixin_manager.setOnClickListener(this);
//
//        skydrive_manager.setOnClickListener(this);


        update_apk.setOnClickListener(this);
        clearTemp.setOnClickListener(this);
        feedback.setOnClickListener(this);
        about.setOnClickListener(this);

        complete_exit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPath();
        initUser();
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

    private void initUser() {

        int setup = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginFlag", "0", (byte) 0));
        if (setup == 1) {
            this.user_setup.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(SetUpActivity.this, MyPageActivity.class);
                    startActivity(intent);
                }
            });
            this.cancellationBtn.setVisibility(View.VISIBLE);
            this.arrows.setVisibility(View.GONE);
            String nickName = this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "NickName", "匿名", (byte) 0);
            if (StringUtils.isEmpty(nickName)) {
                this.username.setText("匿名");
            } else {
                this.username.setText(nickName);
            }
            this.accountname.setVisibility(View.VISIBLE);
            if (StringUtils.isEmpty(this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "UserCode", "", (byte) 0))) {
                this.accountname.setText("账号:"
                        + this.m_iniFileIO.getIniString(this.userIni, "Login", "Account",
                        "", (byte) 0));
            } else {
                this.accountname.setText("账号:"
                        + this.m_iniFileIO.getIniString(this.userIni, "Login", "UserCode",
                        "", (byte) 0));
            }
            String headPath = "http://"
                    + this.m_iniFileIO.getIniString(this.userIni, "Login",
                    "ebsAddress", constants.DefaultServerIp, (byte) 0)
                    + ":"
                    + this.m_iniFileIO.getIniString(this.userIni, "Login", "ebsPort",
                    "8083", (byte) 0)
                    + this.m_iniFileIO.getIniString(this.userIni, "Login", "ebsPath",
                    "/EBS/UserServlet", (byte) 0)
                    + "?act=downloadHead&account="
                    + this.m_iniFileIO.getIniString(this.userIni, "Login", "Account", "",
                    (byte) 0);
            ImageLoader imageLoader = new ImageLoader(this,R.drawable.default_avatar);
            imageLoader.DisplayImage(headPath, this.user_imageview);
        } else {
            this.user_setup.setOnClickListener(this);
            this.cancellationBtn.setVisibility(View.GONE);
            this.arrows.setVisibility(View.VISIBLE);
            this.user_imageview.setImageResource(R.drawable.dc2);
            this.username.setText(R.string.sapi_click_login);
            this.accountname.setVisibility(View.GONE);
            this.m_iniFileIO.writeIniString(this.userIni, "Login", "LoginFlag", "0");
        }
    }

    private void initSetUp() {

        int setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "display_show_in_set", "1", (byte) 0));
        int setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "application_show_in_set", "0", (byte) 0));
        int setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Push",
                "pushSetting", "1", (byte) 0));
        int setup4 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Offline",
                "offlineSetting", "0", (byte) 0));
        if (setup == 0 && setup2 == 0&& setup3 == 0&& setup4 == 0) {
            general_setup.setVisibility(View.GONE);
        } else {
            general_setup.setVisibility(View.VISIBLE);
            if (setup == 0) {
                app_setup.setVisibility(View.GONE);
            } else {
                app_setup.setVisibility(View.VISIBLE);
            }
            if (setup2 == 0) {
                app_manager.setVisibility(View.GONE);
            } else {
                app_manager.setVisibility(View.VISIBLE);
            }
            if (setup3 == 0) {
                module_push.setVisibility(View.GONE);
            } else {
                module_push.setVisibility(View.VISIBLE);
            }
            if (setup4 == 0) {
                module_offlinedownload.setVisibility(View.GONE);
            } else {
                module_offlinedownload.setVisibility(View.VISIBLE);
            }

        }
//        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
//                "email_show_in_set", "0", (byte) 0));
//        setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
//                "msi_show_in_set", "0", (byte) 0));
//        setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "WeiXin",
//                "weixin_show_in_set", "0", (byte) 0));
//        if (setup == 0 && setup2 == 0 && setup3 == 0) {
//            manager_2_setup.setVisibility(View.GONE);
//        } else {
//            manager_2_setup.setVisibility(View.VISIBLE);
//            if (setup == 0) {
//                email_manager.setVisibility(View.GONE);
//            } else {
//                email_manager.setVisibility(View.VISIBLE);
//            }
//            if (setup2 == 0) {
//                module_msi.setVisibility(View.GONE);
//            } else {
//                module_msi.setVisibility(View.VISIBLE);
//            }
//            if (setup3 == 0) {
//                weixin_manager.setVisibility(View.GONE);
//            } else {
//                weixin_manager.setVisibility(View.VISIBLE);
//            }
//        }

//        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
//                "fileManager_show_in_set", "0", (byte) 0));
//        setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
//                "device_show_in_set", "0", (byte) 0));
//        setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
//                "skydrive_show_in_set", "0", (byte) 0));
//        if (setup == 0 && setup2 == 0 && setup3 == 0) {
//            manager_setup.setVisibility(View.GONE);
//        } else {
//            manager_setup.setVisibility(View.VISIBLE);
//            if (setup == 0) {
//                file_manager.setVisibility(View.GONE);
//            } else {
//                file_manager.setVisibility(View.VISIBLE);
//            }
//            if (setup2 == 0) {
//                device_manager.setVisibility(View.GONE);
//            } else {
//                device_manager.setVisibility(View.VISIBLE);
//            }
//            if (setup3 == 0) {
//                skydrive_manager.setVisibility(View.GONE);
//            } else {
//                skydrive_manager.setVisibility(View.VISIBLE);
//            }
//        }
//
//        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
//                "storage_show_in_set", "0", (byte) 0));
//        setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Backup",
//                "backup_show_in_set", "0", (byte) 0));
//        if (setup == 0 && setup2 == 0 ) {
//            storage_backup_setup.setVisibility(View.GONE);
//        } else {
//            storage_backup_setup.setVisibility(View.VISIBLE);
//            if (setup == 0) {
//                storage_manager.setVisibility(View.GONE);
//            } else {
//                storage_manager.setVisibility(View.VISIBLE);
//            }
//            if (setup2 == 0) {
//                backup_manager.setVisibility(View.GONE);
//            } else {
//                backup_manager.setVisibility(View.VISIBLE);
//            }
//        }
        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Module",
                "module_manager_show_in_set", "1", (byte) 0));
        if (setup == 0) {
            function_layout.setVisibility(View.GONE);
        } else {
            function_layout.setVisibility(View.VISIBLE);
        }
        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "feedback_show_in_set", "1", (byte) 0));
        if (setup == 0) {
            feedback.setVisibility(View.GONE);
        } else {
            feedback.setVisibility(View.VISIBLE);
        }

        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "clear_show_in_set", "1", (byte) 0));
        if (setup == 0) {
            clearTemp.setVisibility(View.GONE);
        } else {
            clearTemp.setVisibility(View.VISIBLE);
        }
        setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "update_show_in_set", "1", (byte) 0));
        if (setup == 0) {
            update_apk.setVisibility(View.GONE);
        } else {
            update_apk.setVisibility(View.VISIBLE);
        }
        UIHelper.calculateAppCache(this, mainsize);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.function_layout:
                intent = new Intent();
                intent.setClass(this, FunctionActivity.class);
                this.startActivity(intent);
                break;
            case R.id.about:
                intent = new Intent();
                intent.setClass(this, AboutActivity.class);
                this.startActivity(intent);
                break;
//            case R.id.skydrive_2_manager:
////                intent = new Intent();
////                intent.setClass(this, MyBackupActivity.class);
////                this.startActivity(intent);
//                Toast.makeText(this, "未安装相关应用", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.storage_manager:
//                intent = new Intent();
//                intent.setClass(this, StorageSetupActivity.class);
//                this.startActivity(intent);
//                break;
//            case R.id.backup_manager:
//                intent = new Intent();
//                intent.putExtra("systemSet", false);
//                intent.setClass(this, MyBackupActivity.class);
//                this.startActivity(intent);
//                break;

            case R.id.feedback:
                //intent = new Intent(this, FeedbackActivity.class);
                intent = new Intent(this, FeedBack_New.class);
                this.startActivity(intent);
                break;
            case R.id.update_apk:
                intent = new Intent();
                intent.setClass(this, UpdateActivity.class);
                this.startActivity(intent);
                break;
            case R.id.clearTemp:
                UIHelper.clearAppCache(this);
                this.mainsize.setText("0KB");
                break;
            case R.id.app_setup:
                intent = new Intent();
                intent.setClass(this, DisplaySettingActivity.class);
                this.startActivity(intent);
                break;
            case R.id.module_push:
                intent = new Intent().setClass(this,
                        NotificationSettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.complete_exit:
                UIHelper.showAllQuitDialog(this);
                break;
            case R.id.module_offlinedownload:
                intent = new Intent();
                intent.setClass(this, DownloadActivity.class);
                this.startActivity(intent);
                break;
            case R.id.app_manager:
                intent = new Intent();
                intent.setClass(this, AppSetupActivity.class);
                this.startActivity(intent);
                break;
//            case R.id.module_msi:
//                Toast.makeText(this, "未安装相关应用", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.email_manager:
//                Toast.makeText(this, "未安装相关应用", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.device_manager:
//                intent = new Intent();
//                intent.setClass(this, DeviceManagerActivity.class);
//                this.startActivity(intent);
//                break;
            case R.id.user_setup:
                intent = new Intent();
                intent.setClass(this, LoginActivity.class);
                this.startActivity(intent);
                break;
//            case R.id.file_manager:
//                intent = new Intent();
//                intent.setClass(this, FileExplorerTabActivity.class);
//                this.startActivity(intent);
//                break;
            case R.id.cancellation_btn:
                this.user_setup.setOnClickListener(this);
                this.cancellationBtn.setVisibility(View.GONE);
                this.arrows.setVisibility(View.VISIBLE);
                this.username.setText(R.string.sapi_click_login);
                this.accountname.setVisibility(View.GONE);
                this.user_imageview.setImageResource(R.drawable.dc2);
                //this.stopService(new Intent(getString(string.ServerName1)));
                intent = new Intent();
                intent.setAction(this.getString(R.string.ServerName1));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
                SharedPreferences pre = this.getSharedPreferences(
                        Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                if (pre.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED, true)) {
                    ServiceManager serviceManager = new ServiceManager(
                            this);
                    serviceManager.EBSLogout();
                }
                this.m_iniFileIO.writeIniString(this.userIni, "Login", "LoginFlag", "0");
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
