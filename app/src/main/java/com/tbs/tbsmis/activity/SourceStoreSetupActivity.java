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

public class SourceStoreSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private RelativeLayout source_store_btn;
    private RelativeLayout source_store_layout;
    private RelativeLayout source_store_manage;
    private CheckBox source_store_box;
    private RelativeLayout mysource_show_layout;
    private CheckBox mysource_show_box;
    private EditText sourceStoreAddressEt;
    private EditText sourceStorePortEt;
    private EditText sourceStorePathEt;
    private EditText sourceStoreManagePathEt;

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
        setContentView(R.layout.source_store_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("资源商城设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initPath();
        initSourceStore();
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

    private void initSourceStore(){
        source_store_btn = (RelativeLayout) findViewById(R.id.source_store_btn);
        source_store_layout = (RelativeLayout) findViewById(R.id.source_store_layout);
        source_store_manage = (RelativeLayout) findViewById(R.id.source_store_manage);
        source_store_box = (CheckBox) findViewById(R.id.source_store_box);
        mysource_show_layout = (RelativeLayout) findViewById(R.id.mysource_show_layout);
        mysource_show_box = (CheckBox) findViewById(R.id.mysource_show_box);
        sourceStoreAddressEt = (EditText) findViewById(R.id.source_store_address);
        sourceStorePortEt = (EditText) findViewById(R.id.source_store_port);
        sourceStorePathEt = (EditText) findViewById(R.id.source_store_path);
        sourceStoreManagePathEt = (EditText) findViewById(R.id.source_store_manage_path);
        sourceStoreAddressEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "sourceStoreAddress", constants.DefaultServerIp, (byte) 0));
        sourceStorePortEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "sourceStorePort", constants.DefaultServerPort, (byte) 0));
        sourceStorePathEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "sourceStorePath", "/FileStore/AppStore.cbs", (byte) 0));
        sourceStoreManagePathEt.setText(m_iniFileIO.getIniString(userIni, "Store",
                "sourceStoreManagePath", "/FileStore/SourceStoreManage.cbs", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                "sourceStore_show_in_menu", "0", (byte) 0));
        if (nVal == 1) {
            source_store_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Store",
                "mysource_show_in_menu", "0", (byte) 0));
        if (nVal == 1) {
            mysource_show_box.setChecked(true);
        }
        source_store_btn.setOnClickListener(this);
        source_store_layout.setOnClickListener(this);
        source_store_manage.setOnClickListener(this);
        mysource_show_layout.setOnClickListener(this);
    }

    private void initFtsStore(){
        images_upload_layout = (RelativeLayout) findViewById(R.id.images_upload_layout);
        app_upload_layout = (RelativeLayout) findViewById(R.id.app_upload_layout);

        fts_address = (EditText) findViewById(R.id.fts_address);
        fts_port = (EditText) findViewById(R.id.fts_port);
        fts_app_path = (EditText) findViewById(R.id.fts_app_path);
        fts_image_path = (EditText) findViewById(R.id.fts_image_path);

        fts_address.setText(m_iniFileIO.getIniString(userIni, "Store",
                "source_ftsAddress", constants.DefaultServerIp, (byte) 0));
        fts_port.setText(m_iniFileIO.getIniString(userIni, "Store",
                "source_ftsPort", "1239", (byte) 0));
        fts_app_path.setText(m_iniFileIO.getIniString(userIni, "Store",
                "source_ftsAppPath", "/home/tbs/tbs_soft/TBS/TBS-File", (byte) 0));
        fts_image_path.setText(m_iniFileIO.getIniString(userIni, "Store",
                "source_ftsImagePath", "/home/tbs/tbs_soft/TBSWcm/Web/FileStore/image", (byte) 0));
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
                .valueOf(sourceStoreAddressEt.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查资源商城地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                sourceStoreAddressEt.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "资源商城地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            sourceStoreAddressEt.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(sourceStorePortEt.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "资源商城端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            sourceStorePortEt.setFocusable(true);
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
                "source_ftsAddress", fts_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "source_ftsPort", fts_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "source_ftsAppPath", fts_app_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "source_ftsImagePath", fts_image_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "sourceStoreAddress", sourceStoreAddressEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "sourceStorePort", sourceStorePortEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "sourceStorePath", sourceStorePathEt.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Store",
                "sourceStoreManagePath", sourceStoreManagePathEt.getText()
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
                intent_images_upload.putExtra("upload","source_imageUpload");
                intent_images_upload.setClass(SourceStoreSetupActivity.this, FileExplorerTabActivity.class);
                startActivity(intent_images_upload);
                break;
            case R.id.app_upload_layout:
                Intent intent_app_upload = new Intent();
                intent_app_upload.putExtra("upload","source_appUpload");
                intent_app_upload.setClass(SourceStoreSetupActivity.this, FileExplorerTabActivity.class);
                startActivity(intent_app_upload);
                break;
            case R.id.source_store_btn:
                Intent intent = new Intent();
                intent.setClass(SourceStoreSetupActivity.this, ResourceStoreActivity.class);
                startActivity(intent);
                break;
            case R.id.source_store_layout:
                if (source_store_box.isChecked()) {
                    source_store_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Store", "sourceStore_show_in_menu", "0");
                } else {
                    source_store_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Store", "sourceStore_show_in_menu", "1");
                }
                break;
            case R.id.mysource_show_layout:
                if (mysource_show_box.isChecked()) {
                    mysource_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Store", "mysource_show_in_menu", "0");
                } else {
                    mysource_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Store", "mysource_show_in_menu", "1");
                }
                break;
            case R.id.source_store_manage:
                String sourcePath = sourceStoreManagePathEt.getText().toString();
                if(!sourcePath.toLowerCase().contains("http:")){
                    String address_editTxt = String
                            .valueOf(sourceStoreAddressEt.getText());
                    if (null !=address_editTxt
                            && !address_editTxt.equals("")
                            && !address_editTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(address_editTxt)) {
                            Toast.makeText(this,
                                    "请检查资源商城地址的正确性", Toast.LENGTH_SHORT)
                                    .show();
                            sourceStoreAddressEt.setFocusable(true);
                            return;
                        }
                    }else{
                        Toast.makeText(this,
                                "请检查资源商城地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        sourceStoreAddressEt.setFocusable(true);
                        return;
                    }
                    address_editTxt = String
                            .valueOf(sourceStorePortEt.getText());
                    if (StringUtils.isEmpty(address_editTxt)) {
                        Toast.makeText(this,
                                "资源商城端口不可为空", Toast.LENGTH_SHORT)
                                .show();
                        sourceStorePortEt.setFocusable(true);
                        return;
                    }
                    address_editTxt = String
                            .valueOf(sourceStoreManagePathEt.getText());
                    if (StringUtils.isEmpty(address_editTxt)) {
                        Toast.makeText(this,
                                "管理路径不可为空", Toast.LENGTH_SHORT)
                                .show();
                        sourceStoreManagePathEt.setFocusable(true);
                        return;
                    }
                    sourcePath = "http://"
                            +sourceStoreAddressEt.getText().toString()
                            + ":"
                            + sourceStorePortEt.getText()
                            + sourcePath;
                }
                Intent source = new Intent();
                source.putExtra("path",sourcePath);
                source.setClass(this, BackgroundManageActivity.class);
                startActivity(source);
                break;
        }
    }
}
