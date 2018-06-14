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
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class FileSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;
    private RelativeLayout fileManager_layout;
    private RelativeLayout filemanager_show_layout;
    private CheckBox filemanager_show_box;
    private EditText fts_address;
    private EditText fts_port;
    private EditText fts_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("文件管理");
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
        fileManager_layout = (RelativeLayout) findViewById(R.id.fileManager_layout);
        filemanager_show_layout = (RelativeLayout) findViewById(R.id.filemanager_show_layout);
        filemanager_show_box = (CheckBox) findViewById(R.id.filemanager_show_box);

        fts_address = (EditText) findViewById(R.id.fts_address);
        fts_port = (EditText) findViewById(R.id.fts_port);
        fts_path = (EditText) findViewById(R.id.fts_path);

        fts_address.setText(m_iniFileIO.getIniString(userIni, "Skydrive",
                "ftsAddress", constants.DefaultServerIp, (byte) 0));
        fts_port.setText(m_iniFileIO.getIniString(userIni, "Skydrive",
                "ftsPort", "1239", (byte) 0));
        fts_path.setText(m_iniFileIO.getIniString(userIni, "Skydrive",
                "ftsPath", "/home/tbs/tbs_soft/TBS", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Skydrive",
                "fileManager_show_in_set", "0", (byte) 0));
        if (nVal == 1) {
            filemanager_show_box.setChecked(true);
        }
        fileManager_layout.setOnClickListener(this);
        filemanager_show_layout.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
    private void saveEt(){

        String address_editTxt = String
                .valueOf(fts_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                fts_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            fts_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(fts_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            fts_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Skydrive",
                "ftsAddress", fts_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Skydrive",
                "ftsPort", fts_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Skydrive",
                "ftsPath", fts_path.getText()
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
                saveEt();
                break;
            case R.id.fileManager_layout:
                String address_editTxt = String
                        .valueOf(fts_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        fts_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    fts_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(fts_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    fts_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Skydrive",
                        "ftsAddress", fts_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Skydrive",
                        "ftsPort", fts_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Skydrive",
                        "ftsPath", fts_path.getText()
                                .toString());
                Intent intent = new Intent();
                intent.setClass(this, FileExplorerTabActivity.class);
                startActivity(intent);
                break;
            case R.id.filemanager_show_layout:
                if (filemanager_show_box.isChecked()) {
                    filemanager_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Skydrive", "fileManager_show_in_set", "0");
                } else {
                    filemanager_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Skydrive", "fileManager_show_in_set", "1");
                }
                break;
        }
    }
}
