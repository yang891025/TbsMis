package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class MenuSetupActivity extends Activity implements View.OnClickListener
{
    /*
      头部基本项
     */
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;
    private RelativeLayout custom_menu;
    private IniFile IniFile;
    private String userIni;
    /*
          默认左菜单设置选项

        */
    private RelativeLayout module_menu_left_login;
    private CheckBox module_menu_left_login_box;
    private RelativeLayout module_menu_left_application;
    private CheckBox module_menu_left_application_box;
    private RelativeLayout module_menu_left_manager;
    private CheckBox module_menu_left_manager_box;
    private RelativeLayout module_menu_left_home;
    private CheckBox module_menu_left_home_box;
    private RelativeLayout module_menu_left_go;
    private CheckBox module_menu_left_go_box;
    private RelativeLayout module_menu_left_back;
    private CheckBox module_menu_left_back_box;
    private RelativeLayout module_menu_left_refresh;
    private CheckBox module_menu_left_refresh_box;
    private RelativeLayout module_menu_left_favourite;
    private CheckBox module_menu_left_favourite_box;
    private RelativeLayout module_menu_left_share;
    private CheckBox module_menu_left_share_box;
    private RelativeLayout module_menu_left_search;
    private CheckBox module_menu_left_search_box;
    private RelativeLayout module_menu_left_scan;
    private CheckBox module_menu_left_scan_box;
    private RelativeLayout module_menu_left_set;
    private CheckBox module_menu_left_set_box;
    private RelativeLayout module_menu_left_quit;
    private CheckBox module_menu_left_quit_box;
    /*
       默认菜单设置选项

     */
    private RelativeLayout module_menu_login;
    private CheckBox module_menu_login_box;
    private RelativeLayout module_menu_application;
    private CheckBox module_menu_application_box;
    private RelativeLayout module_menu_manager;
    private CheckBox module_menu_manager_box;
    private RelativeLayout module_menu_home;
    private CheckBox module_menu_home_box;
    private RelativeLayout module_menu_go;
    private CheckBox module_menu_go_box;
    private RelativeLayout module_menu_back;
    private CheckBox module_menu_back_box;
    private RelativeLayout module_menu_refresh;
    private CheckBox module_menu_refresh_box;
    private RelativeLayout module_menu_favourite;
    private CheckBox module_menu_favourite_box;
    private RelativeLayout module_menu_share;
    private CheckBox module_menu_share_box;
    private RelativeLayout module_menu_search;
    private CheckBox module_menu_search_box;
    private RelativeLayout module_menu_scan;
    private CheckBox module_menu_scan_box;
    private RelativeLayout module_menu_set;
    private CheckBox module_menu_set_box;
    private RelativeLayout module_menu_quit;
    private CheckBox module_menu_quit_box;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_setup_all);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        custom_menu = (RelativeLayout) findViewById(R.id.custom_menu);
        title.setText("菜单设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        custom_menu.setOnClickListener(this);
        initPath();
        initCustomMenu();
        initLeftCustomMenu();
    }
    /*
       初始化配置路径

     */
    private void initPath() {
        IniFile = new IniFile();
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
    }
     /*
        初始化默认菜单项

      */
    private void initCustomMenu() {
        module_menu_login = (RelativeLayout) findViewById(R.id.module_menu_login);
        module_menu_login_box = (CheckBox) findViewById(R.id.module_menu_login_box);
        module_menu_application = (RelativeLayout) findViewById(R.id.module_menu_application);
        module_menu_application_box = (CheckBox) findViewById(R.id.module_menu_application_box);
        module_menu_search = (RelativeLayout) findViewById(R.id.module_menu_search);
        module_menu_search_box = (CheckBox) findViewById(R.id.module_menu_search_box);
        module_menu_manager = (RelativeLayout) findViewById(R.id.module_menu_manager);
        module_menu_manager_box = (CheckBox) findViewById(R.id.module_menu_manager_box);
        module_menu_scan = (RelativeLayout) findViewById(R.id.module_menu_scan);
        module_menu_scan_box = (CheckBox) findViewById(R.id.module_menu_scan_box);
        module_menu_home = (RelativeLayout) findViewById(R.id.module_menu_home);
        module_menu_home_box = (CheckBox) findViewById(R.id.module_menu_home_box);
        module_menu_go = (RelativeLayout) findViewById(R.id.module_menu_go);
        module_menu_go_box = (CheckBox) findViewById(R.id.module_menu_go_box);
        module_menu_back = (RelativeLayout) findViewById(R.id.module_menu_back);
        module_menu_back_box = (CheckBox) findViewById(R.id.module_menu_back_box);
        module_menu_refresh = (RelativeLayout) findViewById(R.id.module_menu_refresh);
        module_menu_refresh_box = (CheckBox) findViewById(R.id.module_menu_refresh_box);
        module_menu_favourite = (RelativeLayout) findViewById(R.id.module_menu_favourite);
        module_menu_favourite_box = (CheckBox) findViewById(R.id.module_menu_favourite_box);
        module_menu_share = (RelativeLayout) findViewById(R.id.module_menu_share);
        module_menu_share_box = (CheckBox) findViewById(R.id.module_menu_share_box);
        module_menu_set = (RelativeLayout) findViewById(R.id.module_menu_set);
        module_menu_set_box = (CheckBox) findViewById(R.id.module_menu_set_box);
        module_menu_quit = (RelativeLayout) findViewById(R.id.module_menu_quit);
        module_menu_quit_box = (CheckBox) findViewById(R.id.module_menu_quit_box);
        module_menu_login.setOnClickListener(this);
        module_menu_application.setOnClickListener(this);
        module_menu_search.setOnClickListener(this);
        module_menu_manager.setOnClickListener(this);
        module_menu_scan.setOnClickListener(this);
        module_menu_home.setOnClickListener(this);
        module_menu_go.setOnClickListener(this);
        module_menu_back.setOnClickListener(this);
        module_menu_refresh.setOnClickListener(this);
        module_menu_share.setOnClickListener(this);
        module_menu_set.setOnClickListener(this);
        module_menu_favourite.setOnClickListener(this);
        module_menu_quit.setOnClickListener(this);
        int nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "myapplication", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_application_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "login", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_login_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "search", "0", (byte) 0));
        if (nVal == 1) {
            module_menu_search_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "manager", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_manager_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "scan", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_scan_box.setChecked(true);

        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "home", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_home_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "backward", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_back_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "foreword", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_go_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "refresh", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_refresh_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "share", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_share_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "set", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_set_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "quit", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_quit_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "collection", "0", (byte) 0));
        if (nVal == 1) {
            module_menu_favourite_box.setChecked(true);
        }
    }
    /*
           初始化默认菜单项

         */
    private void initLeftCustomMenu() {
        module_menu_left_login = (RelativeLayout) findViewById(R.id.module_menu_left_login);
        module_menu_left_login_box = (CheckBox) findViewById(R.id.module_menu_left_login_box);
        module_menu_left_application = (RelativeLayout) findViewById(R.id.module_menu_left_application);
        module_menu_left_application_box = (CheckBox) findViewById(R.id.module_menu_left_application_box);
        module_menu_left_search = (RelativeLayout) findViewById(R.id.module_menu_left_search);
        module_menu_left_search_box = (CheckBox) findViewById(R.id.module_menu_left_search_box);
        module_menu_left_manager = (RelativeLayout) findViewById(R.id.module_menu_left_manager);
        module_menu_left_manager_box = (CheckBox) findViewById(R.id.module_menu_left_manager_box);
        module_menu_left_scan = (RelativeLayout) findViewById(R.id.module_menu_left_scan);
        module_menu_left_scan_box = (CheckBox) findViewById(R.id.module_menu_left_scan_box);
        module_menu_left_home = (RelativeLayout) findViewById(R.id.module_menu_left_home);
        module_menu_left_home_box = (CheckBox) findViewById(R.id.module_menu_left_home_box);
        module_menu_left_go = (RelativeLayout) findViewById(R.id.module_menu_left_go);
        module_menu_left_go_box = (CheckBox) findViewById(R.id.module_menu_left_go_box);
        module_menu_left_back = (RelativeLayout) findViewById(R.id.module_menu_left_back);
        module_menu_left_back_box = (CheckBox) findViewById(R.id.module_menu_left_back_box);
        module_menu_left_refresh = (RelativeLayout) findViewById(R.id.module_menu_left_refresh);
        module_menu_left_refresh_box = (CheckBox) findViewById(R.id.module_menu_left_refresh_box);
        module_menu_left_favourite = (RelativeLayout) findViewById(R.id.module_menu_left_favourite);
        module_menu_left_favourite_box = (CheckBox) findViewById(R.id.module_menu_left_favourite_box);
        module_menu_left_share = (RelativeLayout) findViewById(R.id.module_menu_left_share);
        module_menu_left_share_box = (CheckBox) findViewById(R.id.module_menu_left_share_box);
        module_menu_left_set = (RelativeLayout) findViewById(R.id.module_menu_left_set);
        module_menu_left_set_box = (CheckBox) findViewById(R.id.module_menu_left_set_box);
        module_menu_left_quit = (RelativeLayout) findViewById(R.id.module_menu_left_quit);
        module_menu_left_quit_box = (CheckBox) findViewById(R.id.module_menu_left_quit_box);
        module_menu_left_login.setOnClickListener(this);
        module_menu_left_application.setOnClickListener(this);
        module_menu_left_search.setOnClickListener(this);
        module_menu_left_manager.setOnClickListener(this);
        module_menu_left_scan.setOnClickListener(this);
        module_menu_left_home.setOnClickListener(this);
        module_menu_left_go.setOnClickListener(this);
        module_menu_left_back.setOnClickListener(this);
        module_menu_left_refresh.setOnClickListener(this);
        module_menu_left_share.setOnClickListener(this);
        module_menu_left_set.setOnClickListener(this);
        module_menu_left_quit.setOnClickListener(this);
        module_menu_left_favourite.setOnClickListener(this);
        int nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_myapplication", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_application_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_login", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_login_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_search", "0", (byte) 0));
        if (nVal == 1) {
            module_menu_left_search_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_manager", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_manager_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_scan", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_scan_box.setChecked(true);

        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_home", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_home_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_backward", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_back_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_foreword", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_go_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_refresh", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_refresh_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_share", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_share_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_set", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_set_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_quit", "1", (byte) 0));
        if (nVal == 1) {
            module_menu_left_quit_box.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni, "custom_menu",
                "left_collection", "0", (byte) 0));
        if (nVal == 1) {
            module_menu_left_favourite_box.setChecked(true);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case R.id.custom_menu:
                Intent intent = new Intent();
                intent.setClass(this, CustomMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.module_menu_login:
                if (module_menu_login_box.isChecked()) {
                    module_menu_login_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "login", "0");
                } else {
                    module_menu_login_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "login", "1");
                }
                break;
            case R.id.module_menu_application:
                if (module_menu_application_box.isChecked()) {
                    module_menu_application_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "myapplication", "0");
                } else {
                    module_menu_application_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "myapplication", "1");
                }
                break;
            case R.id.module_menu_search:
                if (module_menu_search_box.isChecked()) {
                    module_menu_search_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "search", "0");
                } else {
                    module_menu_search_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "search", "1");
                }
                break;
            case R.id.module_menu_manager:
                if (module_menu_manager_box.isChecked()) {
                    module_menu_manager_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "manager", "0");
                } else {
                    module_menu_manager_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "manager", "1");
                }
                break;
            case R.id.module_menu_scan:
                if (module_menu_scan_box.isChecked()) {
                    module_menu_scan_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "scan", "0");
                } else {
                    module_menu_scan_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "scan", "1");
                }
                break;

            case R.id.module_menu_home:
                if (module_menu_home_box.isChecked()) {
                    module_menu_home_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "home", "0");
                } else {
                    module_menu_home_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "home", "1");
                }
                break;
            case R.id.module_menu_back:
                if (module_menu_back_box.isChecked()) {
                    module_menu_back_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "backward",
                            "0");
                } else {
                    module_menu_back_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "backward",
                            "1");
                }
                break;
            case R.id.module_menu_go:
                if (module_menu_go_box.isChecked()) {
                    module_menu_go_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "foreword",
                            "0");
                } else {
                    module_menu_go_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "foreword",
                            "1");
                }
                break;
            case R.id.module_menu_refresh:
                if (module_menu_refresh_box.isChecked()) {
                    module_menu_refresh_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "refresh", "0");
                } else {
                    module_menu_refresh_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "refresh", "1");
                }
                break;
            case R.id.module_menu_share:
                if (module_menu_share_box.isChecked()) {
                    module_menu_share_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "share", "0");
                } else {
                    module_menu_share_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "share", "1");
                }
                break;
            case R.id.module_menu_set:
                if (module_menu_set_box.isChecked()) {
                    module_menu_set_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "set", "0");
                } else {
                    module_menu_set_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "set", "1");
                }
                break;
            case R.id.module_menu_quit:
                if (module_menu_quit_box.isChecked()) {
                    module_menu_quit_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "quit", "0");
                } else {
                    module_menu_quit_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "quit", "1");
                }
                break;
            case R.id.module_menu_favourite:
                if (module_menu_favourite_box.isChecked()) {
                    module_menu_favourite_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "collection",
                            "0");
                } else {
                    module_menu_favourite_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "collection",
                            "1");
                }
                break;
            case R.id.module_menu_left_login:
                if (module_menu_left_login_box.isChecked()) {
                    module_menu_left_login_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_login", "0");
                } else {
                    module_menu_left_login_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_login", "1");
                }
                break;
            case R.id.module_menu_left_application:
                if (module_menu_left_application_box.isChecked()) {
                    module_menu_left_application_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_myapplication", "0");
                } else {
                    module_menu_left_application_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_myapplication", "1");
                }
                break;
            case R.id.module_menu_left_search:
                if (module_menu_left_search_box.isChecked()) {
                    module_menu_left_search_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_search", "0");
                } else {
                    module_menu_left_search_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_search", "1");
                }
                break;
            case R.id.module_menu_left_manager:
                if (module_menu_left_manager_box.isChecked()) {
                    module_menu_left_manager_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_manager", "0");
                } else {
                    module_menu_left_manager_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_manager", "1");
                }
                break;
            case R.id.module_menu_left_scan:
                if (module_menu_left_scan_box.isChecked()) {
                    module_menu_left_scan_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_scan", "0");
                } else {
                    module_menu_left_scan_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_scan", "1");
                }
                break;

            case R.id.module_menu_left_home:
                if (module_menu_left_home_box.isChecked()) {
                    module_menu_left_home_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_home", "0");
                } else {
                    module_menu_left_home_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_home", "1");
                }
                break;
            case R.id.module_menu_left_back:
                if (module_menu_left_back_box.isChecked()) {
                    module_menu_left_back_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_backward",
                            "0");
                } else {
                    module_menu_left_back_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_backward",
                            "1");
                }
                break;
            case R.id.module_menu_left_go:
                if (module_menu_left_go_box.isChecked()) {
                    module_menu_left_go_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_foreword",
                            "0");
                } else {
                    module_menu_left_go_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_foreword",
                            "1");
                }
                break;
            case R.id.module_menu_left_refresh:
                if (module_menu_left_refresh_box.isChecked()) {
                    module_menu_left_refresh_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_refresh", "0");
                } else {
                    module_menu_left_refresh_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_refresh", "1");
                }
                break;
            case R.id.module_menu_left_share:
                if (module_menu_left_share_box.isChecked()) {
                    module_menu_left_share_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_share", "0");
                } else {
                    module_menu_left_share_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_share", "1");
                }
                break;
            case R.id.module_menu_left_set:
                if (module_menu_left_set_box.isChecked()) {
                    module_menu_left_set_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_set", "0");
                } else {
                    module_menu_left_set_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_set", "1");
                }
                break;
            case R.id.module_menu_left_quit:
                if (module_menu_left_quit_box.isChecked()) {
                    module_menu_left_quit_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_quit", "0");
                } else {
                    module_menu_left_quit_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_quit", "1");
                }
                break;
            case R.id.module_menu_left_favourite:
                if (module_menu_left_favourite_box.isChecked()) {
                    module_menu_left_favourite_box.setChecked(false);
                    IniFile.writeIniString(userIni, "custom_menu", "left_collection",
                            "0");
                } else {
                    module_menu_left_favourite_box.setChecked(true);
                    IniFile.writeIniString(userIni, "custom_menu", "left_collection",
                            "1");
                }
                break;
        }
    }
}
