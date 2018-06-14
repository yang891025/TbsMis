package com.tbs.tbsmis.Live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class LiveSetupActivity extends AppCompatActivity implements View.OnClickListener
{

    private ImageView backBtn;
    private ImageView finishBtn;
    private TextView title;
    private IniFile m_iniFileIO;
    private String userIni;
    private RelativeLayout live_layout;
    private RelativeLayout live_show_layout;
    private CheckBox live_show_box;
    private EditText live_address;
    private EditText live_port;
    private EditText pull_address;
    private EditText pull_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("直播设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initFts();
    }
    private void initPath(){
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
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }

    private void initFts(){
        live_layout = (RelativeLayout) findViewById(R.id.live_layout);
        live_show_layout = (RelativeLayout) findViewById(R.id.live_show_layout);
        live_show_box = (CheckBox) findViewById(R.id.live_show_box);

        live_address = (EditText) findViewById(R.id.live_address);
        live_port = (EditText) findViewById(R.id.live_port);

        pull_address = (EditText) findViewById(R.id.pull_address);
        pull_port = (EditText) findViewById(R.id.pull_port);

        live_address.setText(m_iniFileIO.getIniString(userIni, "Live",
                "liveAddress", constants.DefaultServerIp, (byte) 0));
        live_port.setText(m_iniFileIO.getIniString(userIni, "Live",
                "livePort", "1115", (byte) 0));

        pull_address.setText(m_iniFileIO.getIniString(userIni, "Live",
                "pullAddress", "168.160.111.26", (byte) 0));
        pull_port.setText(m_iniFileIO.getIniString(userIni, "Live",
                "pullPort", "1936", (byte) 0));

        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Live",
                "my_live_show_in_menu", "0", (byte) 0));
        if (nVal == 1) {
            live_show_box.setChecked(true);
        }
        live_layout.setOnClickListener(this);
        live_show_layout.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
    private void saveLive(){

        String address_editTxt = String
                .valueOf(live_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查直播地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                live_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "直播地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            live_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(live_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "直播端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            live_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Live",
                "liveAddress", live_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Live",
                "livePort", live_port.getText()
                        .toString());
    }

    private void savePull(){

        String address_editTxt = String
                .valueOf(pull_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查直播地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                pull_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "直播地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            pull_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(pull_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "直播端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            pull_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Live",
                "pullAddress", pull_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Live",
                "pullPort", pull_port.getText()
                        .toString());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                saveLive();
                savePull();
                finish();
                break;
            case R.id.live_layout:
                saveLive();
                Intent intent = new Intent();
                intent.setClass(this, LiveListMainActivity.class);
                startActivity(intent);
                break;
            case R.id.live_show_layout:
                if (live_show_box.isChecked()) {
                    live_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Live", "my_live_show_in_menu", "0");
                } else {
                    live_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Live", "my_live_show_in_menu", "1");
                }
                break;
        }
    }
}
