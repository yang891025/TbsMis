package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.update.Updateapp;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/11/15.
 */

public class VideoPublishSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private RelativeLayout publish_show_layout;
    private CheckBox publish_show_box;
    private IniFile m_iniFileIO;
    private String userIni;
    private EditText publish_package_path;
    private TextView is_install_txt;
    private RelativeLayout is_install_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        is_install_txt = (TextView) findViewById(R.id.is_install_txt);
        is_install_app = (RelativeLayout) findViewById(R.id.is_install_app);
        publish_package_path = (EditText) findViewById(R.id.publish_package_path);
        title.setText("视频直播-主播");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        publish_show_layout = (RelativeLayout) findViewById(R.id.publish_show_layout);
        publish_show_box = (CheckBox) findViewById(R.id.publish_show_box);
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Publish",
                "publish_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            publish_show_box.setChecked(true);
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Publish", "packPath",
                "com.tbs.tbsvps", (byte) 0);
        publish_package_path.setText(EmailUrl);
        if (Updateapp.CheckVersion(this, "app:" + EmailUrl, "") == 0) {
            nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                    "store_show_in_menu", "0", (byte) 0));
            if (nVal == 0) {
                is_install_txt.setText("未安装");
                is_install_app.setEnabled(false);
            } else {
                is_install_txt.setText("去安装");
                is_install_app.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(VideoPublishSetupActivity.this, MyCloudActivity.class);
                        startActivity(intent);
                    }
                });
            }
        } else {
            is_install_txt.setText("已安装,点击打开应用");
            is_install_app.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //包管理操作管理类
                    PackageManager pm = getPackageManager();
                    //获取到应用信息
                    Intent it = pm.getLaunchIntentForPackage(publish_package_path.getText().toString());
                    if (it != null)
                        startActivity(it);
                }
            });
        }
        publish_package_path.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                is_install_txt.setText("检测应用包是否安装");
                is_install_app.setEnabled(true);
                is_install_app.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        if (Updateapp.CheckVersion(VideoPublishSetupActivity.this, "app:" + publish_package_path.getText(), "")
                                == 0) {
                            int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                                    "store_show_in_menu", "0", (byte) 0));
                            if (nVal == 0) {
                                is_install_txt.setText("未安装");
                                is_install_app.setEnabled(false);
                            } else {
                                is_install_txt.setText("去安装");
                                is_install_app.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setClass(VideoPublishSetupActivity.this, MyCloudActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else {
                            is_install_txt.setText("已安装,点击打开应用");
                            is_install_app.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v) {
                                    //包管理操作管理类
                                    PackageManager pm = getPackageManager();
                                    //获取到应用信息
                                    Intent it = pm.getLaunchIntentForPackage(publish_package_path.getText().toString());
                                    if (it != null)
                                        startActivity(it);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        publish_show_layout.setOnClickListener(this);
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

    private void saveET() {
        m_iniFileIO.writeIniString(userIni, "Publish",
                "packPath", publish_package_path.getText()
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
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                saveET();
                break;
            case R.id.publish_show_layout:
                if (publish_show_box.isChecked()) {
                    publish_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Publish", "publish_show_in_set", "0");
                } else {
                    publish_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Publish", "publish_show_in_set", "1");
                }
                break;
        }
    }
}
