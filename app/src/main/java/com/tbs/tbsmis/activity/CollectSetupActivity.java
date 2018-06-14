package com.tbs.tbsmis.activity;

/**
 * Created by TBS on 2017/6/15.
 */

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
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class CollectSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;

    private RelativeLayout my_collect_manage;
    private RelativeLayout collect_show_layout;
    private CheckBox collect_show_box;
    private RelativeLayout mycollect_show_layout;
    private CheckBox mycollect_show_box;
    private EditText collect_address;
    private EditText collect_port;
    private EditText collect_path;
    private EditText mycollect_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("收藏设置");
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
        my_collect_manage = (RelativeLayout) findViewById(R.id.my_collect_manage);
        collect_show_layout = (RelativeLayout) findViewById(R.id.collect_show_layout);
        collect_show_box = (CheckBox) findViewById(R.id.collect_show_box);
        mycollect_show_layout = (RelativeLayout) findViewById(R.id.mycollect_show_layout);
        mycollect_show_box = (CheckBox) findViewById(R.id.mycollect_show_box);
        collect_address = (EditText) findViewById(R.id.collect_address);
        collect_port = (EditText) findViewById(R.id.collect_port);
        collect_path = (EditText) findViewById(R.id.collect_path);
        mycollect_path = (EditText) findViewById(R.id.mycollect_path);
        collect_address.setText(m_iniFileIO.getIniString(userIni,
                "Collect", "collectAddress", constants.DefaultServerIp, (byte) 0));
        collect_port.setText(m_iniFileIO.getIniString(userIni,
                "Collect", "collectPort", constants.DefaultServerPort, (byte) 0));
        collect_path.setText(m_iniFileIO.getIniString(userIni,
                "Collect", "collectPath", "/collect/collect.cbs", (byte) 0));
        mycollect_path.setText(m_iniFileIO.getIniString(userIni,
                "Collect", "mycollectPath", "/collect/mycollect.cbs", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Collect",
                "collect_show_in_menu", "0", (byte) 0));
        if (nVal == 1) {
            collect_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Collect",
                "collect_show_in_my", "0", (byte) 0));
        if (nVal == 1) {
            mycollect_show_box.setChecked(true);
        }
        my_collect_manage.setOnClickListener(this);
        collect_show_layout.setOnClickListener(this);
        mycollect_show_layout.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
    private void saveET(){
        String address_editTxt = String
                .valueOf(collect_address.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                collect_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            collect_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(collect_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            collect_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Collect",
                "collectAddress", collect_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Collect",
                "collectPort", collect_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Collect",
                "collectPath", collect_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Collect",
                "mycollectPath", mycollect_path.getText()
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
            case R.id.my_collect_manage:
                String address_editTxt = String
                        .valueOf(collect_address.getText());
                if (null !=address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        collect_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    collect_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(collect_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    collect_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Collect",
                        "collectAddress", collect_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Collect",
                        "collectPort", collect_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Collect",
                        "mycollectPath", mycollect_path.getText()
                                .toString());
                Intent store = new Intent();
                store.setClass(this, MyCollectActivity.class);
                startActivity(store);
                break;
            case R.id.collect_show_layout:
                if (collect_show_box.isChecked()) {
                    collect_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Collect", "collect_show_in_menu", "0");
                } else {
                    collect_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Collect", "collect_show_in_menu", "1");
                }
                break;
            case R.id.mycollect_show_layout:
                if (mycollect_show_box.isChecked()) {
                    mycollect_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Collect", "collect_show_in_my", "0");
                } else {
                    mycollect_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Collect", "collect_show_in_my", "1");
                }
                break;
        }
    }
}

