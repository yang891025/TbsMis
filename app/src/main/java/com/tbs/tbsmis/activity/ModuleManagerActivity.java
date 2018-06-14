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
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class ModuleManagerActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private RelativeLayout module_show_layout;
    private CheckBox module_show_box;
    private RelativeLayout module_setup;
    private IniFile m_iniFileIO;
    private String userIni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_setup);
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
        initPath();
        module_show_layout = (RelativeLayout) findViewById(R.id.module_show_layout);
        module_show_box = (CheckBox) findViewById(R.id.module_show_box);
        module_setup = (RelativeLayout) findViewById(R.id.module_setup);
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Module",
                "module_manager_show_in_set", "1", (byte) 0));
        if (nVal == 1) {
            module_show_box.setChecked(true);
        }
        module_show_layout.setOnClickListener(this);
        module_setup.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                finish();
                break;
            case R.id.module_show_layout:
                if (module_show_box.isChecked()) {
                    module_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Module", "module_manager_show_in_set", "0");
                } else {
                    module_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Module", "module_manager_show_in_set", "1");
                }
                break;
            case R.id.module_setup:
                Intent intent = new Intent();
                intent.setClass(this, FunctionActivity.class);
                startActivity(intent);
                break;
        }
    }
}
