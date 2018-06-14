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
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class InterfaceSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;
    private LinearLayout menu_setup;
    private LinearLayout appearance_setup;

    private RelativeLayout display_show_layout;
    private CheckBox display_show_box;
    private RelativeLayout display_setup;

    private IniFile m_iniFileIO;
    private String userIni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interface_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        menu_setup = (LinearLayout)findViewById(R.id.menu_setup);
        appearance_setup = (LinearLayout)findViewById(R.id.appearance_setup);
        display_show_layout = (RelativeLayout) findViewById(R.id.display_show_layout);
        display_show_box = (CheckBox) findViewById(R.id.display_show_box);
        display_setup = (RelativeLayout) findViewById(R.id.display_setup);

        title.setText("界面设置");
        initPath();

        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "display_show_in_set", "1", (byte) 0));
        if (nVal == 1) {
            display_show_box.setChecked(true);
        }

        display_show_layout.setOnClickListener(this);
        display_setup.setOnClickListener(this);
        menu_setup.setOnClickListener(this);
        appearance_setup.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.menu_setup:
                intent = new Intent();
                intent.setClass(this, MenuSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.appearance_setup:
                intent = new Intent();
                intent.setClass(this, AppearanceSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.display_show_layout:
                if (display_show_box.isChecked()) {
                    display_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "display_show_in_set", "0");
                } else {
                    display_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "display_show_in_set", "1");
                }
                break;
            case R.id.display_setup:
                Intent display_intent = new Intent();
                display_intent.setClass(this, DisplaySettingActivity.class);
                startActivity(display_intent);
                break;
        }
    }
}
