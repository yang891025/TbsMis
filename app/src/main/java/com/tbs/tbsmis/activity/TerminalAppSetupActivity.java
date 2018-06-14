package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/14.
 */

public class TerminalAppSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile IniFile;
    private String appIniFile;
    private String userIni;

    private LinearLayout email_setup;
    private LinearLayout msi_setup;
    private LinearLayout disk_setup;
    private LinearLayout player_setup;
    private LinearLayout music_setup;
    private LinearLayout mcs_setup;
    private LinearLayout reader_setup;
    private LinearLayout publish_setup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terminal_app_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);

        publish_setup = (LinearLayout) findViewById(R.id.publish_setup);
        email_setup = (LinearLayout) findViewById(R.id.email_setup);
        msi_setup = (LinearLayout) findViewById(R.id.msi_setup);
        reader_setup = (LinearLayout) findViewById(R.id.reader_setup);
        mcs_setup = (LinearLayout) findViewById(R.id.mcs_setup);
        music_setup = (LinearLayout) findViewById(R.id.music_setup);
        player_setup = (LinearLayout) findViewById(R.id.player_setup);
        disk_setup = (LinearLayout) findViewById(R.id.disk_setup);

        title.setText("终端应用");
        publish_setup.setOnClickListener(this);
        email_setup.setOnClickListener(this);
        msi_setup.setOnClickListener(this);
        disk_setup.setOnClickListener(this);
        mcs_setup.setOnClickListener(this);
        player_setup.setOnClickListener(this);
        reader_setup.setOnClickListener(this);
        music_setup.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        finishBtn.setOnClickListener(this);
        IniFile = new IniFile();
        initPath();
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
            case R.id.publish_setup:
                intent = new Intent();
                intent.setClass(this, VideoPublishSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.msi_setup:
                intent = new Intent();
                intent.setClass(this, MsiSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.email_setup:
                intent = new Intent();
                intent.setClass(this, EmailSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.music_setup:
                intent = new Intent();
                intent.setClass(this, MusicSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.disk_setup:
                intent = new Intent();
                intent.setClass(this, DiskSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.mcs_setup:
                intent = new Intent();
                intent.setClass(this, McsSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.reader_setup:
                intent = new Intent();
                intent.setClass(this, ReaderSetupActivity.class);
                startActivity(intent);
                break;
            case R.id.player_setup:
                intent = new Intent();
                intent.setClass(this, PlayerSetupActivity.class);
                startActivity(intent);
                break;
        }
    }
}
