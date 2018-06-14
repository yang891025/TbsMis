package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.tbs.tbsmis.download.DownloadActivity;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class OfflineSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;
    private RelativeLayout offline_layout;
    private RelativeLayout offline_show_layout;
    private CheckBox offline_show_box;
    private EditText offline_address;
    private EditText offline_port;
    private EditText offline_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("离线设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initOffline();
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

    private void initOffline(){
        offline_layout = (RelativeLayout) findViewById(R.id.offline_layout);
        offline_show_layout = (RelativeLayout) findViewById(R.id.offline_show_layout);
        offline_show_box = (CheckBox) findViewById(R.id.offline_show_box);
        offline_address = (EditText) findViewById(R.id.offline_address);
        offline_port = (EditText) findViewById(R.id.offline_port);
        offline_path = (EditText) findViewById(R.id.offline_path);
        offline_address.setText(m_iniFileIO.getIniString(userIni,
                "Offline", "offlineAddress", constants.DefaultServerIp, (byte) 0));
        offline_port.setText(m_iniFileIO.getIniString(userIni,
                "Offline", "offlinePort", constants.DefaultServerPort, (byte) 0));
        offline_path.setText(m_iniFileIO.getIniString(userIni,
                "Offline", "offlinePath", "", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Offline",
                "offlineSetting", "0", (byte) 0));
        if (nVal == 1) {
            offline_show_box.setChecked(true);
        }
        offline_layout.setOnClickListener(this);
        offline_show_layout.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
    private void saveET(){
        String address_editTxt = String
                .valueOf(offline_address.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                offline_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            offline_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(offline_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            offline_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Offline",
                "offlineAddress", offline_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Offline",
                "offlinePort", offline_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Offline",
                "offlinePath", offline_path.getText()
                        .toString());
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
               saveET();
                break;
            case R.id.offline_layout:
                String address_editTxt = String
                        .valueOf(offline_address.getText());
                if (null !=address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查接受地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        offline_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "接受地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    offline_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(offline_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "发送地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    offline_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Offline",
                        "offlineAddress", offline_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Offline",
                        "offlinePort", offline_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Offline",
                        "offlinePath", offline_path.getText()
                                .toString());
                Intent intent = new Intent();
                intent.setClass(this,DownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.offline_show_layout:
                if (offline_show_box.isChecked()) {
                    offline_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Offline", "offlineSetting", "0");
                } else {
                    offline_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Offline", "offlineSetting", "1");
                }
                break;
        }
    }
}
