/*
 * Copyright 2014 the original author or authors.

 */
package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.check.AppListAdapter;
import com.tbs.tbsmis.check.PathChooseDialog;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.update.DownloadAsyncTask;
import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity for displaying the notification setting view.
 *
 * @author Sehwan
 */
@SuppressLint("SetJavaScriptEnabled")
public class AppSetupActivity extends Activity implements View.OnClickListener
{

    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private TextView app_set_subtitle;
    private LinearLayout app_set_url;
    private RelativeLayout app_set_enable;
    private ArrayList<HashMap<String, Object>> appList;
    private ArrayList<String> midList;
    private AppListAdapter AppListAdapter;
    private ListView AppListlv;
    private String fileName;
    private IniFile m_iniFileIO;
    private String webRoot;
    private LinearLayout app_manager;
    private int position;
    private AppSetupActivity.AppManagerBroadReciver AppManagerBroadReciver;
    private RelativeLayout app_set_ini;
    private TextView app_ini_subtitle;
    private View app_msg;
    private LinearLayout app_msg_url;
    private CheckBox app_msg_checkbox;
    private TextView app_name_text;
    private TextView app_category_text;
    private EditText app_show_text;
    private ImageView app_image_text;
    private TextView login_type_txt;
    private EditText app_url_text;
    private String userIni;
    private RelativeLayout app_image;
    private LinearLayout app_manager_updata;
    private LinearLayout app_manager_dataup;
    private LinearLayout app_data_instal;
    private TextView app_argument_text;
    private RelativeLayout app_argument;
    private RelativeLayout app_name;
    private Button update_set_system_ini_check;
    private Button app_configuration_check;
    private Switch login_type_open_box;
    private String rcIni;
    private Switch login_auto_open_box;
    private TextView login_auto_txt;
    private RelativeLayout app_dir_enable;
    private RelativeLayout app_root_enable;
    private TextView app_dir_subtitle;
    private TextView app_device_subtitle;
    private TextView app_root_subtitle;
    private TextView update_set_system_ini_subtitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_app_manager);
        // 添加Activity到堆栈
        MyActivity.getInstance().addActivity(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("app_manager"
                + getString(R.string.about_title));
        AppManagerBroadReciver = new AppSetupActivity.AppManagerBroadReciver();
        registerReceiver(AppManagerBroadReciver, intentFilter);

        app_dir_enable = (RelativeLayout) findViewById(R.id.app_dir_enable);
        app_root_enable = (RelativeLayout) findViewById(R.id.app_root_enable);
        app_dir_subtitle = (TextView) findViewById(R.id.app_dir_subtitle);
        app_device_subtitle = (TextView) findViewById(R.id.app_device_subtitle);
        app_root_subtitle = (TextView) findViewById(R.id.app_root_subtitle);
        update_set_system_ini_subtitle = (TextView) findViewById(R.id.update_set_system_ini_subtitle);
        app_set_enable = (RelativeLayout) findViewById(R.id.app_set_enable);
        app_set_ini = (RelativeLayout) findViewById(R.id.app_set_ini);
        app_set_url = (LinearLayout) findViewById(R.id.app_set_url);
        app_msg = findViewById(R.id.app_msg);
        app_msg_url = (LinearLayout) findViewById(R.id.app_msg_url);
        app_msg_checkbox = (CheckBox) findViewById(R.id.app_msg_checkbox);
        app_manager = (LinearLayout) findViewById(R.id.app_manager);
        finishBtn = (ImageView) findViewById(R.id.more_btn);
        downBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        AppListlv = (ListView) findViewById(R.id.ApplistTopicItems);
        app_set_subtitle = (TextView) findViewById(R.id.app_set_subtitle);
        app_ini_subtitle = (TextView) findViewById(R.id.app_ini_subtitle);
        app_name = (RelativeLayout) findViewById(R.id.app_name);
        app_name_text = (TextView) findViewById(R.id.app_name_text);
        app_category_text = (TextView) findViewById(R.id.app_category_text);
        app_show_text = (EditText) findViewById(R.id.app_show_text);
        app_image = (RelativeLayout) findViewById(R.id.app_image);
        app_image_text = (ImageView) findViewById(R.id.app_image_text);
        app_argument = (RelativeLayout) findViewById(R.id.app_argument);
        app_argument_text = (TextView) findViewById(R.id.app_argument_text);
        login_type_txt = (TextView) findViewById(R.id.login_type_txt);
        app_url_text = (EditText) findViewById(R.id.app_url_text);
        app_configuration_check = (Button) findViewById(R.id.app_configuration_check);
        update_set_system_ini_check = (Button) findViewById(R.id.update_set_system_ini_check);
        app_manager_updata = (LinearLayout) findViewById(R.id.app_manager_updata);
        app_data_instal = (LinearLayout) findViewById(R.id.app_data_instal);
        app_manager_dataup = (LinearLayout) findViewById(R.id.app_manager_dataup);

        title.setText("当前应用");

        app_manager_updata.setOnClickListener(this);
        app_manager_dataup.setOnClickListener(this);
        app_set_ini.setOnClickListener(this);
        app_image.setOnClickListener(this);
        app_data_instal.setOnClickListener(this);
        app_name.setOnClickListener(this);
        app_argument.setOnClickListener(this);
        app_set_enable.setOnClickListener(this);
        app_msg.setOnClickListener(this);
        update_set_system_ini_check.setOnClickListener(this);
        app_configuration_check.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        initPath();
        String storagePath = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
        if (storagePath.equals(FileUtils.getFirstExterPath())) {
            app_device_subtitle.setText("当前设备：手机内存");
        } else if (storagePath.toLowerCase().contains("sd")) {
            app_device_subtitle.setText("当前设备：SD卡");
        } else {
            app_device_subtitle.setText("当前设备：OTG存储");
        }
        app_set_subtitle.setText("当前路径：" + webRoot);
        String dir = webRoot.substring(0, webRoot.lastIndexOf("/"));
        dir = dir.substring(dir.lastIndexOf("/") + 1);
        app_dir_subtitle.setText("当前工作目录：" + dir);
        app_root_subtitle.setText("当前根路径：" + UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1));
        update_set_system_ini_subtitle.setText(constants.APP_CONFIG_FILE_NAME.substring(0,constants.APP_CONFIG_FILE_NAME.lastIndexOf(".")) + "," + constants.USER_CONFIG_FILE_NAME.substring(0,constants.USER_CONFIG_FILE_NAME.lastIndexOf(".")));
        appListInit();
        login_type_open_box = (Switch) findViewById(R.id.login_type_open_box);
        login_type_txt = (TextView) findViewById(R.id.login_type_txt);
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(rcIni, "Login",
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
                    ServiceManager serviceManager = new ServiceManager(AppSetupActivity.this);
                    serviceManager.EBSLogout();
                }
                if (isChecked) {
                    login_type_txt.setText("正在使用系统配置");
                    m_iniFileIO.writeIniString(rcIni, "Login", "LoginType",
                            "1");
                    initPath();
                    m_iniFileIO.writeIniString(userIni, "Login", "LoginFlag", "0");
                } else {
                    login_type_txt.setText("正在使用应用配置");
                    m_iniFileIO.writeIniString(rcIni, "Login", "LoginType",
                            "0");
                    initPath();
                    m_iniFileIO.writeIniString(userIni, "Login", "LoginFlag", "0");
                }
            }
        });
        login_auto_open_box = (Switch) findViewById(R.id.login_auto_open_box);
        login_auto_txt = (TextView) findViewById(R.id.login_auto_txt);

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "TBSAPP",
                "app_path_show", "0", (byte) 0));
        if (nVal == 1) {
            app_set_enable.setVisibility(View.VISIBLE);
        } else {
            app_set_enable.setVisibility(View.GONE);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "TBSAPP",
                "app_root_show", "1", (byte) 0));
        if (nVal == 1) {
            app_root_enable.setVisibility(View.VISIBLE);
        } else {
            app_root_enable.setVisibility(View.GONE);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "UpdateSysConfig", "1", (byte) 0));
        if (nVal == 1) {
            login_auto_open_box.setChecked(true);
            login_auto_txt.setText("开启自动更新");
        } else {
            login_auto_open_box.setChecked(false);
            login_auto_txt.setText("关闭自动更新");
        }
        login_auto_open_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    login_auto_txt.setText("开启自动更新");
                    m_iniFileIO.writeIniString(userIni, "Login", "UpdateSysConfig", "1");
                } else {
                    login_auto_txt.setText("关闭自动更新");
                    m_iniFileIO.writeIniString(userIni, "Login", "UpdateSysConfig", "0");
                }
            }
        });
    }

    private void initPath() {
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
        m_iniFileIO = new IniFile();
        String WebIniFile = webRoot
                + constants.WEB_CONFIG_FILE_NAME;
        rcIni = m_iniFileIO.getIniString(WebIniFile, "TBSWeb",
                "IniName", constants.NEWS_CONFIG_FILE_NAME,
                (byte) 0);
        userIni = webRoot + rcIni;
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

    @SuppressWarnings("deprecation")
    public void GetAppIniThread() {

        Thread dbmsgThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String ipUrl = m_iniFileIO.getIniString(userIni,
                        "Software", "softwareAddress", constants.DefaultServerIp,
                        (byte) 0);
                String portUrl = m_iniFileIO.getIniString(userIni,
                        "Software", "softwarePort", constants.DefaultServerPort,
                        (byte) 0);
                String baseUrl = "http://" + ipUrl + ":" + portUrl;
                String DbMsg = "/update/update.cbs";
                String url = baseUrl + DbMsg;
                ApiClient Client = new ApiClient();
                try {
                    constants.menus = Client.navigationmsg(url);
                } catch (AppException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        });
        if (dbmsgThread.isAlive()) {
            dbmsgThread.stop();
        }
        dbmsgThread.start();
    }

    @SuppressWarnings("deprecation")
    public void GetSystemIniThread() {
        Thread dbmsgThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                String url = getString(R.string.UpdateIniUrl);
                ApiClient Client = new ApiClient();
                try {
                    constants.menus = Client.navigationmsg(url);
                } catch (AppException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        });
        if (dbmsgThread.isAlive()) {
            dbmsgThread.stop();
        }
        dbmsgThread.start();
    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (constants.menus != null) {
                        String dataPath = getFilesDir().getParentFile()
                                .getAbsolutePath();
                        if (dataPath.endsWith("/") == false) {
                            dataPath = dataPath + "/";
                        }
                        final String appIni = dataPath + constants.APP_CONFIG_FILE_NAME;
                        final String userIni = dataPath + constants.USER_CONFIG_FILE_NAME;
                        if (constants.menus[0].equalsIgnoreCase(m_iniFileIO.getIniString(appIni,
                                "TBSAPP", "AppCode", "",
                                (byte) 0))) {
                            if (constants.menus[1].compareTo(m_iniFileIO.getIniString(appIni,
                                    "TBSAPP", "AppVersion", "1.0",
                                    (byte) 0)) > 0) {
                                new Builder(AppSetupActivity.this)
                                        .setTitle("配置可更新")
                                        .setMessage("点击确定进行更新")
                                        .setPositiveButton("取消", null)
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                DownloadAsyncTask task = new DownloadAsyncTask(AppSetupActivity.this,
                                                        constants.menus[2], appIni);
                                                task.execute();
                                                DownloadAsyncTask task1 = new DownloadAsyncTask(AppSetupActivity.this,
                                                        constants.menus[3], userIni);
                                                task1.execute();
                                            }
                                        }).show();
                            } else {
                                Toast.makeText(AppSetupActivity.this, "系统配置已是最新版本",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AppSetupActivity.this, "系统配置已是最新版本",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AppSetupActivity.this, "系统配置已是最新版本",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case 1:
                    if (constants.menus != null) {
                        //System.out.println("AppVersion="+constants.menus[1]);
                        if (constants.menus[0].equalsIgnoreCase(m_iniFileIO.getIniString(userIni,
                                "TBSAPP", "AppCode", "",
                                (byte) 0))) {
                            if (constants.menus[1].compareTo(m_iniFileIO.getIniString(userIni,
                                    "TBSAPP", "AppVersion", "1.0",
                                    (byte) 0)) > 0) {
                                new Builder(AppSetupActivity.this)
                                        .setTitle("配置可更新")
                                        .setMessage("点击确定进行更新")
                                        .setPositiveButton("取消", null)
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String type = userIni.substring(userIni.lastIndexOf("."));
                                                String name = userIni.substring(0,userIni.lastIndexOf("."));
                                                int count = 1;
                                                File file = new File(name+count+type);
                                                while(file.exists()){
                                                    count++;
                                                    file = new File(name+count+type);
                                                }
                                                try {
                                                    File file1 = new File(userIni);
                                                    file1.renameTo(file);
                                                    file.createNewFile();

                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                DownloadAsyncTask task = new DownloadAsyncTask(AppSetupActivity.this,
                                                        constants.menus[2], userIni);
                                                task.execute();
                                            }
                                        }).show();
                            } else {
                                Toast.makeText(AppSetupActivity.this, "应用配置已是最新版本",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AppSetupActivity.this, "应用配置已是最新版本",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AppSetupActivity.this, "应用配置已是最新版本",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private void appListInit() {
        // TODO Auto-generated method stub
        fileName = "*.ini";
        // appList = new ArrayList<String>();
        appList = new ArrayList<HashMap<String, Object>>();/* 在数组中存放数据 */
        midList = new ArrayList<String>();
        FileUtils.findFiles(webRoot, fileName, midList);
        if (midList.size() >= 2) {
            for (int i = 0; i < midList.size(); i++) {
                if (!m_iniFileIO.getIniSection(midList.get(i),
                        "TBSAPP").isEmpty()) {
                    String AppName = m_iniFileIO.getIniString(
                            midList.get(i), "TBSAPP", "AppName",
                            "水利热点", (byte) 0);
                    String ResName = m_iniFileIO.getIniString(
                            midList.get(i), "TBSAPP", "resname",
                            "yqxx", (byte) 0);
                    int drawable = UIHelper.sourceId_drawable(ResName);
                    app_ini_subtitle.setText("当前应用：" + userIni.substring(userIni.lastIndexOf("/") + 1,userIni.lastIndexOf(".")));
                    app_argument_text.setText(m_iniFileIO.getIniString(userIni, "TBSAPP", "resname", "", (byte) 0));
                    app_name_text.setText(m_iniFileIO.getIniString(userIni, "TBSAPP", "AppName", "", (byte) 0));
                    app_category_text.setText(m_iniFileIO.getIniString(userIni, "TBSAPP", "AppCategory", "", (byte) 0));
                    app_show_text.setText(m_iniFileIO.getIniString(userIni, "TBSAPP", "AppMsg", "暂无", (byte) 0));
                    app_url_text.setText(m_iniFileIO.getIniString(userIni, "TBSAPP", "defaultUrl", "", (byte) 0));
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemImage", drawable);
                    map.put("AppName", AppName);
                    map.put("ResName", midList.get(i));
                    if (userIni.substring(userIni.lastIndexOf("/") + 1).equals(midList.get(i))) {
                        appList.add(map);
                        position = appList.size() - 1;
                    } else {
                        appList.add(map);
                    }
                }
            }
        }
        AppListAdapter = new AppListAdapter(appList, this, webRoot);
        AppListAdapter.setSelectItem(position);
        AppListlv.setAdapter(AppListAdapter);
        UIHelper.setListViewHeightBasedOnChildren(AppListlv, 0);
        this.AppListlv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                AppListAdapter.ViewHolder holder = (AppListAdapter.ViewHolder) arg1.getTag();
                if (holder.btnLayout.getVisibility() == View.GONE) {
                    holder.btnLayout.setVisibility(View.VISIBLE);
                    holder.more.setBackgroundResource(R.drawable.update_up);
                    UIHelper.setListViewHeightBasedOnChildren(AppListlv, 0);
                } else {
                    holder.btnLayout.setVisibility(View.GONE);
                    holder.more.setBackgroundResource(R.drawable.update_down);
                    UIHelper.setListViewHeightBasedOnChildren(AppListlv, 0);
                }
            }
        });
    }

    protected void saveChange() {
        m_iniFileIO.writeIniString(userIni, "TBSAPP", "defaultUrl",
                app_url_text.getText().toString());
        m_iniFileIO.writeIniString(userIni, "TBSAPP", "AppMsg",
                app_show_text.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(AppManagerBroadReciver);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.update_set_system_ini_check:
                GetSystemIniThread();
                break;
            case R.id.app_configuration_check:
                GetAppIniThread();
                break;
            case R.id.app_data_instal:
                Intent intentC = new Intent();
                intentC.setClass(this, tbkChooseActivity.class);
                startActivity(intentC);
                break;
            case R.id.app_image:
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*"); // 设置文件类型
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, 1);
                break;
            case R.id.finish_btn:
                saveChange();
                finish();
                overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.app_set:
                break;
            case R.id.app_manager_updata:
                Intent intent = new Intent();
                intent.putExtra("rights", 4);
                intent.setClass(this, MyResourceActivity.class);
                startActivity(intent);
                break;
            case R.id.app_manager_dataup:
                Intent intents = new Intent();
                intents.putExtra("rights", 5);
                intents.setClass(this, MyResourceActivity.class);
                startActivity(intents);

                break;
            case R.id.app_msg:
                if (app_msg_checkbox.isChecked()) {
                    app_msg_checkbox.setChecked(false);
                    app_msg_url.setVisibility(View.GONE);
                } else {
                    app_msg_checkbox.setChecked(true);
                    app_msg_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.app_set_enable:
                UIHelper.showFilePathDialog(this, 0, null,
                        new PathChooseDialog.ChooseCompleteListener()
                        {
                            @Override
                            public void onComplete(String finalPath) {
                                finalPath = finalPath + File.separator;
                                Toast.makeText(AppSetupActivity.this,
                                        "目前路径:" + finalPath, Toast.LENGTH_SHORT)
                                        .show();
                                StartTbsweb.Startapp(AppSetupActivity.this, 0);
                                UIHelper.setSharePerference(
                                        AppSetupActivity.this,
                                        constants.SAVE_INFORMATION, "Path",
                                        finalPath);
                                StartTbsweb.Startapp(AppSetupActivity.this, 1);
                                Intent intent = new Intent();
                                intent.setAction("Action_main"
                                        + getString(R.string.about_title));
                                intent.putExtra("flag", 12);
                                sendBroadcast(intent);
                                intent = new Intent();
                                intent.setAction("loadView"
                                        + getString(R.string.about_title));
                                intent.putExtra("flag", 5);
                                intent.putExtra("author", 0);
                                sendBroadcast(intent);
                                app_set_subtitle.setText("当前路径：" + finalPath);
                                appListInit();
                            }
                        });
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr
                        .openInputStream(uri));
                app_image_text.setImageBitmap(bitmap);
                m_iniFileIO.writeIniString(userIni, "TBSAPP", "icon",
                        uri.toString());
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class AppManagerBroadReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("app_manager"
                    + getString(R.string.about_title))) {
                initPath();
                appListInit();
            }
        }
    }
}
