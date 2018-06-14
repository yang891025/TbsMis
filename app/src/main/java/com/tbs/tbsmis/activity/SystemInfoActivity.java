package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class SystemInfoActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;

    private TextView project_name_text;
    private EditText project_en_name_text;
    private EditText company_name_text;
    private EditText company_address_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        project_name_text = (TextView) findViewById(R.id.project_name_text);
        project_en_name_text = (EditText) findViewById(R.id.project_en_name_text);
        company_name_text = (EditText) findViewById(R.id.company_name_text);
        company_address_text = (EditText) findViewById(R.id.company_address_text);
        title.setText(getString(R.string.system_msg));
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        project_name_text.setText(R.string.app_name);
        String about_title = m_iniFileIO.getIniString(userIni, "System",
                "about_title", getString(R.string.about_title), (byte) 0);
        project_en_name_text.setText(about_title);
        String copyright_content = m_iniFileIO.getIniString(userIni, "System",
                "copyright_content", getString(R.string.copyright_content), (byte) 0);
        company_name_text.setText(copyright_content);
        String technique_provide_content = m_iniFileIO.getIniString(userIni, "System",
                "technique_provide_content", getString(R.string.technique_provide_content), (byte) 0);
        company_address_text.setText(technique_provide_content);
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

    protected void saveChange() {
        m_iniFileIO.writeIniString(userIni, "System", "about_title",
                project_en_name_text.getText().toString());
        m_iniFileIO.writeIniString(userIni, "System", "copyright_content",
                company_name_text.getText().toString());
        m_iniFileIO.writeIniString(userIni, "System", "technique_provide_content",
                company_address_text.getText().toString());
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
                saveChange();
                finish();
                break;
        }
    }
}
