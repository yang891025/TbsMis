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
 * Created by TBS on 2017/6/16.
 */

public class StoreSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private RelativeLayout store_layout;
    private RelativeLayout store_show_layout;
    private RelativeLayout store_manage;
    private RelativeLayout store_upload;
    private CheckBox store_show_box;
    private RelativeLayout myapplication_show_layout;
    private CheckBox myapplication_show_box;
    private EditText storeAddressEt;
    private EditText storePortEt;
    private EditText storePathEt;
    private EditText storeManagePathEt;
    private EditText storeUploadPathEt;

    private RelativeLayout images_upload_layout;
    private RelativeLayout app_upload_layout;
    private EditText fts_address;
    private EditText fts_port;
    private EditText fts_app_path;
    private EditText fts_image_path;

    private IniFile m_iniFileIO;
    private String userIni;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("应用商城设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initAppStore();
        initFtsStore();
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
    private void initAppStore(){
        store_layout = (RelativeLayout) findViewById(R.id.store_layout);
        store_show_layout = (RelativeLayout) findViewById(R.id.store_show_layout);
        store_manage = (RelativeLayout) findViewById(R.id.store_manage);
        store_upload = (RelativeLayout) findViewById(R.id.store_upload);
        store_show_box = (CheckBox) findViewById(R.id.store_show_box);
        myapplication_show_layout = (RelativeLayout) findViewById(R.id.myapplication_show_layout);
        myapplication_show_box = (CheckBox) findViewById(R.id.myapplication_show_box);

        storeAddressEt = (EditText) findViewById(R.id.store_address);
        storePortEt = (EditText) findViewById(R.id.store_port);
        storePathEt = (EditText) findViewById(R.id.store_path);
        storeManagePathEt = (EditText) findViewById(R.id.store_manage_path);
        storeUploadPathEt = (EditText) findViewById(R.id.store_upload_path);

        storeAddressEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "storeAddress", constants.DefaultServerIp, (byte) 0));
        storePortEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "storePort", constants.DefaultServerPort, (byte) 0));
        storePathEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "storePath", "/Store/AppStore.cbs", (byte) 0));
        storeManagePathEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "storeManagePath", "/Store/ShowStoreManage.cbs", (byte) 0));
        storeUploadPathEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "storeUploadPath", "Store/Upload.cbs", (byte) 0));

        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                "store_show_in_menu", "1", (byte) 0));
        if (nVal == 1) {
            store_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                "myapplication_show_in_menu", "1", (byte) 0));
        if (nVal == 1) {
            myapplication_show_box.setChecked(true);
        }
        store_layout.setOnClickListener(this);
        store_show_layout.setOnClickListener(this);
        store_manage.setOnClickListener(this);
        store_upload.setOnClickListener(this);
        myapplication_show_layout.setOnClickListener(this);
    }
    private void initFtsStore(){
        images_upload_layout = (RelativeLayout) findViewById(R.id.images_upload_layout);
        app_upload_layout = (RelativeLayout) findViewById(R.id.app_upload_layout);

        fts_address = (EditText) findViewById(R.id.fts_address);
        fts_port = (EditText) findViewById(R.id.fts_port);
        fts_app_path = (EditText) findViewById(R.id.fts_app_path);
        fts_image_path = (EditText) findViewById(R.id.fts_image_path);

        fts_address.setText(m_iniFileIO.getIniString(userIni, "Store",
                "ftsAddress", constants.DefaultServerIp, (byte) 0));
        fts_port.setText(m_iniFileIO.getIniString(userIni, "Store",
                "ftsPort", "1239", (byte) 0));
        fts_app_path.setText(m_iniFileIO.getIniString(userIni, "Store",
                "ftsAppPath", "/home/tbs/tbs_soft/TBS/TBS-App", (byte) 0));
        fts_image_path.setText(m_iniFileIO.getIniString(userIni, "Store",
                "ftsImagePath", "/home/tbs/tbs_soft/TBSWcm/Web/Store/image", (byte) 0));
        images_upload_layout.setOnClickListener(this);
        app_upload_layout.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    private void saveEt(){
        String address_editTxt = String
                .valueOf(storeAddressEt.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查应用商城地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                storeAddressEt.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "请检查应用商城地址的正确性", Toast.LENGTH_SHORT)
                    .show();
            storeAddressEt.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(storePortEt.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "应用商城端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            storePortEt.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(fts_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查文件上传地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                fts_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "文件上传地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            fts_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(fts_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "文件上传端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            fts_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Store",
                "ftsAddress", fts_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "ftsPort", fts_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "ftsAppPath", fts_app_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "ftsImagePath", fts_image_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "storeAddress", storeAddressEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "storePort", storePortEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "storePath", storePathEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "storeManagePath", storeManagePathEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "storeUploadPath", storeUploadPathEt.getText()
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
            case R.id.images_upload_layout:
                Intent intent_images_upload = new Intent();
                intent_images_upload.putExtra("upload","imageUpload");
                intent_images_upload.setClass(StoreSetupActivity.this, FileExplorerTabActivity.class);
                startActivity(intent_images_upload);
                break;
            case R.id.app_upload_layout:
                Intent intent_app_upload = new Intent();
                intent_app_upload.putExtra("upload","appUpload");
                intent_app_upload.setClass(StoreSetupActivity.this, FileExplorerTabActivity.class);
                startActivity(intent_app_upload);
                break;
            case R.id.store_layout:
                Intent intent = new Intent();
                intent.setClass(StoreSetupActivity.this, MyCloudActivity.class);
                startActivity(intent);
                break;
            case R.id.store_upload:
                Intent intent_upload = new Intent();
                intent_upload.putExtra("systemSet", true);
                intent_upload.setClass(this, MyBackupActivity.class);
                startActivity(intent_upload);
                break;
            case R.id.store_show_layout:
                if (store_show_box.isChecked()) {
                    store_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Store", "store_show_in_menu", "0");
                } else {
                    store_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Store", "store_show_in_menu", "1");
                }
                break;
            case R.id.myapplication_show_layout:
                if (myapplication_show_box.isChecked()) {
                    myapplication_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Store", "myapplication_show_in_menu", "0");
                } else {
                    myapplication_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Store", "myapplication_show_in_menu", "1");
                }
                break;
            case R.id.store_manage:
                String Path = storeManagePathEt.getText().toString();
                if(!Path.toLowerCase().contains("http:")){
                    String address_editTxt = String
                            .valueOf(storeAddressEt.getText());
                    if (null !=address_editTxt
                            && !address_editTxt.equals("")
                            && !address_editTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(address_editTxt)) {
                            Toast.makeText(this,
                                    "请检查应用商城地址的正确性", Toast.LENGTH_SHORT)
                                    .show();
                            storeAddressEt.setFocusable(true);
                            return;
                        }
                    }else{
                        Toast.makeText(this,
                                "请检查应用商城地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        storeAddressEt.setFocusable(true);
                        return;
                    }
                    address_editTxt = String
                            .valueOf(storePortEt.getText());
                    if (StringUtils.isEmpty(address_editTxt)) {
                        Toast.makeText(this,
                                "应用商城端口不可为空", Toast.LENGTH_SHORT)
                                .show();
                        storePortEt.setFocusable(true);
                        return;
                    }
                    address_editTxt = String
                            .valueOf(storeManagePathEt.getText());
                    if (StringUtils.isEmpty(address_editTxt)) {
                        Toast.makeText(this,
                                "管理路径不可为空", Toast.LENGTH_SHORT)
                                .show();
                        storeManagePathEt.setFocusable(true);
                        return;
                    }
                    Path = "http://"
                            +storeAddressEt.getText().toString()
                            + ":"
                            + storePortEt.getText()
                            + Path;
                }
                Intent store = new Intent();
                store.putExtra("path",Path);
                store.setClass(this, BackgroundManageActivity.class);
                startActivity(store);
                break;
        }
    }
}
