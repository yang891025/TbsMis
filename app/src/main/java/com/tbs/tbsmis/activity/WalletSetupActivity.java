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
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class WalletSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile m_iniFileIO;
    private String userIni;
    private RelativeLayout wallet_layout;
    private RelativeLayout wallet_manage;
    private CheckBox bills_show_box;
    private RelativeLayout mywallet_show_layout;
    private CheckBox mywallet_show_box;
    private EditText wallet_address;
    private EditText wallet_port;
    private EditText wallet_path;
    private EditText wallet_manage_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView)findViewById(R.id.more_btn);
        finishBtn = (ImageView)findViewById(R.id.finish_btn);
        title = (TextView)findViewById(R.id.textView1);
        title.setText("钱包设置");
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
        wallet_layout = (RelativeLayout) findViewById(R.id.wallet_layout);
        wallet_manage = (RelativeLayout) findViewById(R.id.wallet_manage);
        bills_show_box = (CheckBox) findViewById(R.id.bills_show_box);
        mywallet_show_layout = (RelativeLayout) findViewById(R.id.mywallet_show_layout);
        mywallet_show_box = (CheckBox) findViewById(R.id.mywallet_show_box);
        wallet_address = (EditText) findViewById(R.id.wallet_address);
        wallet_port = (EditText) findViewById(R.id.wallet_port);
        wallet_path = (EditText) findViewById(R.id.wallet_path);
        wallet_manage_path = (EditText) findViewById(R.id.wallet_manage_path);
        wallet_address.setText(m_iniFileIO.getIniString(userIni,
                "Wallet", "walletAddress", constants.DefaultServerIp, (byte) 0));
        wallet_port.setText(m_iniFileIO.getIniString(userIni,
                "Wallet", "walletPort", "8083", (byte) 0));
        wallet_path.setText(m_iniFileIO.getIniString(userIni,
                "Wallet", "walletPath", "/TBS-PAY/mobile/index.jsp", (byte) 0));
        wallet_manage_path.setText(m_iniFileIO.getIniString(userIni,
                "Wallet", "walletManagePath", "", (byte) 0));
//        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Wallet",
//                "walletSetting", "0", (byte) 0));
//        if (nVal == 1) {
//            wallet_show_box.setChecked(true);
//        }
       int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Wallet",
                "myWallet_show_in_my", "0", (byte) 0));
        if (nVal == 1) {
            mywallet_show_box.setChecked(true);
        }
        wallet_layout.setOnClickListener(this);
        wallet_manage.setOnClickListener(this);
        mywallet_show_layout.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
    private void saveET(){
        String address_editTxt = String
                .valueOf(wallet_address.getText());
        if (null !=address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                wallet_address.setFocusable(true);
                return;
            }
        }else{
            Toast.makeText(this,
                    "地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            wallet_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(wallet_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            wallet_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Wallet",
                "walletAddress", wallet_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Wallet",
                "walletPort", wallet_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Wallet",
                "walletPath", wallet_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Wallet",
                "walletManagePath", wallet_manage_path.getText()
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
            case R.id.wallet_layout:
                String address_edit = String
                        .valueOf(wallet_address.getText());
                if (null !=address_edit
                        && !address_edit.equals("")
                        && !address_edit.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_edit)) {
                        Toast.makeText(this,
                                "请检查地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        wallet_address.setFocusable(true);
                        return;
                    }
                }else{
                    Toast.makeText(this,
                            "地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    wallet_address.setFocusable(true);
                    return;
                }
                address_edit = String
                        .valueOf(wallet_port.getText());
                if (StringUtils.isEmpty(address_edit)) {
                    Toast.makeText(this,
                            "端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    wallet_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Wallet",
                        "walletAddress", wallet_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Wallet",
                        "walletPort", wallet_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Wallet",
                        "walletPath", wallet_path.getText()
                                .toString());
                Intent intent = new Intent();
                intent.putExtra("rights", 1);
                intent.setClass(WalletSetupActivity.this,MyWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.wallet_manage:
                String Path = wallet_manage_path.getText().toString();
                if(!Path.toLowerCase().contains("http:")){
                    String address_editTxt = String
                            .valueOf(wallet_address.getText());
                    if (null !=address_editTxt
                            && !address_editTxt.equals("")
                            && !address_editTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(address_editTxt)) {
                            Toast.makeText(this,
                                    "请检查钱包地址的正确性", Toast.LENGTH_SHORT)
                                    .show();
                            wallet_address.setFocusable(true);
                            return;
                        }
                    }else{
                        Toast.makeText(this,
                                "请检查钱包地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        wallet_address.setFocusable(true);
                        return;
                    }
                    address_editTxt = String
                            .valueOf(wallet_port.getText());
                    if (StringUtils.isEmpty(address_editTxt)) {
                        Toast.makeText(this,
                                "钱包端口不可为空", Toast.LENGTH_SHORT)
                                .show();
                        wallet_port.setFocusable(true);
                        return;
                    }
                    address_editTxt = String
                            .valueOf(wallet_manage_path.getText());
                    if (StringUtils.isEmpty(address_editTxt)) {
                        Toast.makeText(this,
                                "管理路径不可为空", Toast.LENGTH_SHORT)
                                .show();
                        wallet_manage_path.setFocusable(true);
                        return;
                    }
                    Path = "http://"
                            +wallet_address.getText().toString()
                            + ":"
                            + wallet_port.getText()
                            + Path;
                }
                Intent store = new Intent();
                store.putExtra("path",Path);
                store.setClass(this, BackgroundManageActivity.class);
                startActivity(store);
                break;
            case R.id.mywallet_show_layout:
                if (mywallet_show_box.isChecked()) {
                    mywallet_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Wallet", "myWallet_show_in_my", "0");
                } else {
                    mywallet_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Wallet", "myWallet_show_in_my", "1");
                }
                break;
        }
    }
}
