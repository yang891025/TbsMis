package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class CloudSetupActivity extends Activity implements View.OnClickListener
{

    private ImageView finishBtn, downBtn;
    private EditText urlPortEt, urlIpEt, userNameEt, userPwdEt;

    private EditText netName;

    protected String userPwdTxt;
    protected String userNameTxt;
    protected String urlPortTxt;
    protected String urlIpTxt;

    private EditText webAddressEt;
    private EditText webPortEt;
    private EditText tbsAddressEt;
    private EditText tbsPortEt;
    private EditText ebsAddressEt;
    private EditText ebsPortEt;
    private EditText ftsAddressEt;
    private EditText ftsPortEt;

    private CheckBox network_checkbox;
    private CheckBox web_checkbox;
    private CheckBox tbs_checkbox;
    private CheckBox ebs_checkbox;
    private CheckBox fts_checkbox;

    private RelativeLayout network_url;
    private LinearLayout web_url;
    private LinearLayout tbs_url;
    private LinearLayout ebs_url;
    private LinearLayout fts_url;
    private LinearLayout network_set;
    private LinearLayout web_set;
    private LinearLayout tbs_set;
    private LinearLayout ebs_set;
    private LinearLayout fts_set;

    private Button webCheck;
    protected String webAddressTxt;
    protected String tbsAddressTxt;
    protected String ebsAddressTxt;
    protected String ftsAddressTxt;
    private String webRoot;
    private IniFile m_iniFileIO;
    private String appNewsFile;
    private LinearLayout skydrive_set;
    private LinearLayout skydrive_url;
    private EditText skydriveAddressEt;
    private EditText skydrivePortEt;
    private CheckBox skydrive_checkbox;
    protected String skydriveAddressTxt;
    private View email_set;
    private CheckBox email_checkbox;
    private LinearLayout email_url;
    private EditText smtpAddressEt;
    private EditText smtpPortEt;
    private EditText imapAddressEt;
    private EditText imapPortEt;
    private EditText popAddressEt;
    private EditText popPortEt;
    protected String smtpAddressTxt;
    protected String imapAddressTxt;
    protected String popAddressTxt;
    private EditText urlPathEt;
    private EditText skydrivePathEt;
    private EditText ebsPathEt;
    private LinearLayout store_set;
    private LinearLayout store_url;
    private EditText storeAddressEt;
    private EditText storePortEt;
    private EditText storePathEt;
    private CheckBox store_checkbox;
    protected String storeAddressTxt;

    private LinearLayout module_push_set;
    private CheckBox module_push_checkbox;
    private LinearLayout module_push_name;
    private EditText module_push_address;
    private EditText module_push_port;
    private EditText module_push_path;

    private LinearLayout send_net_set;
    private EditText module_send_address;
    private EditText module_send_port;
    private EditText module_send_path;
    private CheckBox send_net_box;
    private RelativeLayout send_net_btn;
    protected String address_editTxt;
    private RelativeLayout accept_net_btn;
    private LinearLayout accept_net_set;
    private CheckBox accept_net_box;

    private boolean Save;
    private EditText ftsPathEt;
    private String userIni;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.network_setup_activity);
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    private void init() {
        this.netName = (EditText) this.findViewById(R.id.netName);
        this.network_set = (LinearLayout) this.findViewById(R.id.main_set);
        this.web_set = (LinearLayout) this.findViewById(R.id.web_set);
        this.skydrive_set = (LinearLayout) this.findViewById(R.id.skydrive_set);
        this.store_set = (LinearLayout) this.findViewById(R.id.store_set);
        this.email_set = this.findViewById(R.id.email_set);
        this.tbs_set = (LinearLayout) this.findViewById(R.id.tbs_set);
        this.ebs_set = (LinearLayout) this.findViewById(R.id.ebs_set);
        this.fts_set = (LinearLayout) this.findViewById(R.id.fts_set);
        this.network_url = (RelativeLayout) this.findViewById(R.id.NetWork);
        this.web_url = (LinearLayout) this.findViewById(R.id.web_url);
        this.skydrive_url = (LinearLayout) this.findViewById(R.id.skydrive_url);
        this.store_url = (LinearLayout) this.findViewById(R.id.store_url);
        this.email_url = (LinearLayout) this.findViewById(R.id.email_url);
        this.tbs_url = (LinearLayout) this.findViewById(R.id.tbs_url);
        this.ebs_url = (LinearLayout) this.findViewById(R.id.ebs_url);
        this.fts_url = (LinearLayout) this.findViewById(R.id.fts_url);

        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);

        this.webCheck = (Button) this.findViewById(R.id.webCheck);

        this.urlPortEt = (EditText) this.findViewById(R.id.port);
        this.urlIpEt = (EditText) this.findViewById(R.id.Address);
        this.urlPathEt = (EditText) this.findViewById(R.id.PATH);
        this.userNameEt = (EditText) this.findViewById(R.id.Username);
        this.userPwdEt = (EditText) this.findViewById(R.id.PWD);
        this.webAddressEt = (EditText) this.findViewById(R.id.web_address);
        this.webPortEt = (EditText) this.findViewById(R.id.web_port);
        this.smtpAddressEt = (EditText) this.findViewById(R.id.smtp_address);
        this.smtpPortEt = (EditText) this.findViewById(R.id.smtp_port);
        this.imapAddressEt = (EditText) this.findViewById(R.id.imap_address);
        this.imapPortEt = (EditText) this.findViewById(R.id.imap_port);
        this.popAddressEt = (EditText) this.findViewById(R.id.pop_address);
        this.popPortEt = (EditText) this.findViewById(R.id.pop_port);
        this.skydriveAddressEt = (EditText) this.findViewById(R.id.skydrive_address);
        this.skydrivePortEt = (EditText) this.findViewById(R.id.skydrive_port);
        this.skydrivePathEt = (EditText) this.findViewById(R.id.skydrive_path);
        this.storeAddressEt = (EditText) this.findViewById(R.id.store_address);
        this.storePortEt = (EditText) this.findViewById(R.id.store_port);
        this.storePathEt = (EditText) this.findViewById(R.id.store_path);
        this.tbsAddressEt = (EditText) this.findViewById(R.id.tbs_address);
        this.tbsPortEt = (EditText) this.findViewById(R.id.tbs_port);
        this.ebsAddressEt = (EditText) this.findViewById(R.id.ebs_address);
        this.ebsPortEt = (EditText) this.findViewById(R.id.ebs_port);
        this.ebsPathEt = (EditText) this.findViewById(R.id.ebs_path);
        this.ftsAddressEt = (EditText) this.findViewById(R.id.fts_address);
        this.ftsPortEt = (EditText) this.findViewById(R.id.fts_port);
        this.ftsPathEt = (EditText) this.findViewById(R.id.fts_path);

        this.network_checkbox = (CheckBox) this.findViewById(R.id.network_checkbox);
        this.web_checkbox = (CheckBox) this.findViewById(R.id.web_checkbox);
        this.skydrive_checkbox = (CheckBox) this.findViewById(R.id.skydrive_checkbox);
        this.store_checkbox = (CheckBox) this.findViewById(R.id.store_checkbox);
        this.email_checkbox = (CheckBox) this.findViewById(R.id.email_checkbox);
        this.tbs_checkbox = (CheckBox) this.findViewById(R.id.tbs_checkbox);
        this.ebs_checkbox = (CheckBox) this.findViewById(R.id.ebs_checkbox);
        this.fts_checkbox = (CheckBox) this.findViewById(R.id.fts_checkbox);

        this.module_push_set = (LinearLayout) this.findViewById(R.id.module_push_set);
        this.module_push_name = (LinearLayout) this.findViewById(R.id.module_push_name);
        this.module_push_checkbox = (CheckBox) this.findViewById(R.id.module_push_checkbox);
        this.module_push_address = (EditText) this.findViewById(R.id.module_push_address);
        this.module_push_port = (EditText) this.findViewById(R.id.module_push_port);
        this.module_push_path = (EditText) this.findViewById(R.id.module_push_path);
        this.module_send_address = (EditText) this.findViewById(R.id.module_send_address);
        this.module_send_port = (EditText) this.findViewById(R.id.module_send_port);
        this.module_send_path = (EditText) this.findViewById(R.id.module_send_path);
        this.send_net_btn = (RelativeLayout) this.findViewById(R.id.send_net_btn);
        this.send_net_box = (CheckBox) this.findViewById(R.id.send_net_box);
        this.send_net_set = (LinearLayout) this.findViewById(R.id.send_net_set);
        this.accept_net_btn = (RelativeLayout) this.findViewById(R.id.accept_net_btn);
        this.accept_net_box = (CheckBox) this.findViewById(R.id.accept_net_box);
        this.accept_net_set = (LinearLayout) this.findViewById(R.id.accept_net_set);

        this.module_push_set.setOnClickListener(this);
        this.send_net_btn.setOnClickListener(this);
        this.accept_net_btn.setOnClickListener(this);
        this.webCheck.setOnClickListener(this);

        this.network_set.setOnClickListener(this);
        this.web_set.setOnClickListener(this);
        this.skydrive_set.setOnClickListener(this);
        this.store_set.setOnClickListener(this);
        this.email_set.setOnClickListener(this);
        this.tbs_set.setOnClickListener(this);
        this.ebs_set.setOnClickListener(this);
        this.fts_set.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);

        this.m_iniFileIO = new IniFile();
        this.webRoot = UIHelper.getSoftPath(this);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        this.webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        this.webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                "Path", this.webRoot);
        if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
        }
        String WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appNewsFile = this.webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appNewsFile;
        if(Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
        this.netName.setText(this.m_iniFileIO.getIniString(this.appNewsFile, "TBSAPP",
                "webAddress", constants.DefaultServerIp, (byte) 0)/*
                                                                 * m_iniFileIO.
																 * getIniString
																 * (appNewsFile,
																 * "NETWORK",
																 * "network_title"
																 * , "", (byte)
																 * 0)
																 */);
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_offline", "1", (byte) 0)) == 0) {
            this.network_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_push", "1", (byte) 0)) == 0) {
            this.module_push_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_skydrive", "0", (byte) 0)) == 0) {
            this.skydrive_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_store", "0", (byte) 0)) == 0) {
            this.store_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_email", "0", (byte) 0)) == 0) {
            this.email_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_web", "1", (byte) 0)) == 0) {
            this.web_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_ebs", "1", (byte) 0)) == 0) {
            this.ebs_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_tbs", "0", (byte) 0)) == 0) {
            this.tbs_set.setVisibility(View.GONE);
        }
        if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "SETUP",
                "networkSetting_fts", "0", (byte) 0)) == 0) {
            this.fts_set.setVisibility(View.GONE);
        }
        String tempUrlIpTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "TBSAPP",
                "webAddress", constants.DefaultServerIp, (byte) 0);
        String tempUrlPortTxt = this.m_iniFileIO.getIniString(this.appNewsFile,
                "TBSAPP", "webPort", constants.DefaultServerPort, (byte) 0);

        this.webAddressEt.setText(tempUrlIpTxt);
        this.webPortEt.setText(tempUrlPortTxt);

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "skydriveAddress", constants.DefaultServerIp, (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "skydrivePort", constants.DefaultServerPort, (byte) 0);

        this.skydriveAddressEt.setText(tempUrlIpTxt);
        this.skydrivePortEt.setText(tempUrlPortTxt);
        this.skydrivePathEt.setText(this.m_iniFileIO.getIniString(this.userIni,
                "Skydrive", "skydrivePath", "/SkyDrive/MySkyDrive.cbs", (byte) 0));

        tempUrlIpTxt = this.m_iniFileIO.getIniString(userIni, "Store",
                "storeAddress", constants.DefaultServerIp, (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(userIni, "Store",
                "storePort", constants.DefaultServerPort, (byte) 0);

        this.storeAddressEt.setText(tempUrlIpTxt);
        this.storePortEt.setText(tempUrlPortTxt);
        this.storePathEt.setText(this.m_iniFileIO.getIniString(userIni, "Store",
                "storePath", "/Store/AppStore.cbs", (byte) 0));

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "smtpAddress", "", (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "smtpPort", "", (byte) 0);

        this.smtpAddressEt.setText(tempUrlIpTxt);
        this.smtpPortEt.setText(tempUrlPortTxt);

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "imapAddress", "", (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "imapPort", "", (byte) 0);

        this.imapAddressEt.setText(tempUrlIpTxt);
        this.imapPortEt.setText(tempUrlPortTxt);

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "popAddress", "", (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "popPort", "", (byte) 0);

        this.popAddressEt.setText(tempUrlIpTxt);
        this.popPortEt.setText(tempUrlPortTxt);

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "tbsAddress", constants.DefaultServerIp, (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "tbsPort", constants.DefaultServerPort, (byte) 0);

        this.tbsAddressEt.setText(tempUrlIpTxt);
        this.tbsPortEt.setText(tempUrlPortTxt);

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.userIni, "Login",
                "ebsPort", "8083", (byte) 0);

        this.ebsAddressEt.setText(tempUrlIpTxt);
        this.ebsPortEt.setText(tempUrlPortTxt);
        this.ebsPathEt.setText(this.m_iniFileIO.getIniString(this.userIni, "Login",
                "ebsPath", "/EBS/UserServlet", (byte) 0));
        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "ftsAddress", constants.DefaultServerIp, (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "ftsPort", "1239", (byte) 0);
        this.ftsPathEt.setText(this.m_iniFileIO.getIniString(this.userIni, "Skydrive",
                "ftsPath", "/home/tbs/tbs_soft/TBS", (byte) 0));
        this.ftsAddressEt.setText(tempUrlIpTxt);
        this.ftsPortEt.setText(tempUrlPortTxt);

        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlineAddress", constants.DefaultServerIp, (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlinePort", constants.DefaultServerPort, (byte) 0);
        this.urlIpEt.setText(tempUrlIpTxt);
        this.urlPortEt.setText(tempUrlPortTxt);
        this.urlPathEt.setText(this.m_iniFileIO.getIniString(this.userIni, "Offline",
                "offlinePath", "/", (byte) 0));
        tempUrlIpTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "networkUserName", "guest", (byte) 0);
        tempUrlPortTxt = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
                "networkPwd", "guest", (byte) 0);
        this.userNameEt.setText(tempUrlIpTxt);
        this.userPwdEt.setText(tempUrlPortTxt);

        this.module_push_address.setText(this.m_iniFileIO.getIniString(userIni,
                "Push", "pushAddress", constants.DefaultServerIp, (byte) 0));
        this.module_push_port.setText(this.m_iniFileIO.getIniString(userIni,
                "Push", "pushPort", "5222", (byte) 0));
        this.module_push_path.setText(this.m_iniFileIO.getIniString(userIni,
                "Push", "pushPath", "", (byte) 0));
        this.module_send_address.setText(this.m_iniFileIO.getIniString(userIni,
                "Push", "sendAddress", constants.DefaultServerIp, (byte) 0));
        this.module_send_port.setText(this.m_iniFileIO.getIniString(userIni,
                "Push", "sendPort", "8080", (byte) 0));
        this.module_send_path.setText(this.m_iniFileIO.getIniString(userIni,
                "Push", "sendPath", "", (byte) 0));
        this.module_push_address
                .setOnFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            CloudSetupActivity.this.address_editTxt = String
                                    .valueOf(CloudSetupActivity.this.module_push_address.getText());
                            if (null != CloudSetupActivity.this.address_editTxt
                                    && !CloudSetupActivity.this.address_editTxt.equals("")
                                    && !CloudSetupActivity.this.address_editTxt.equals("\\s{1,}")) {
                                if (!StringUtils.isIp(CloudSetupActivity.this.address_editTxt)) {
                                    Toast.makeText(CloudSetupActivity.this,
                                            "请检查填写内容的正确性", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                });
        this.module_send_address
                .setOnFocusChangeListener(new View.OnFocusChangeListener()
                {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            CloudSetupActivity.this.address_editTxt = String
                                    .valueOf(CloudSetupActivity.this.module_send_address.getText());
                            if (null != CloudSetupActivity.this.address_editTxt
                                    && !CloudSetupActivity.this.address_editTxt.equals("")
                                    && !CloudSetupActivity.this.address_editTxt.equals("\\s{1,}")) {
                                if (!StringUtils.isIp(CloudSetupActivity.this.address_editTxt)) {
                                    Toast.makeText(CloudSetupActivity.this,
                                            "请检查填写内容的正确性", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        }
                    }
                });
        this.webAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.webAddressTxt = String.valueOf(CloudSetupActivity.this.webAddressEt.getText());
                    if (null != CloudSetupActivity.this.webAddressTxt && !CloudSetupActivity.this.webAddressTxt.equals("")
                            && !CloudSetupActivity.this.webAddressTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(CloudSetupActivity.this.webAddressTxt)) {
                            Toast.makeText(CloudSetupActivity.this,
                                    "请检查填写内容的正确性", Toast.LENGTH_SHORT).show();
                        } else {
                            CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "TBSAPP",
                                    "webAddress", CloudSetupActivity.this.webAddressTxt);
                        }
                    }
                }
            }
        });
        this.webPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "TBSAPP",
                            "webPort", CloudSetupActivity.this.webPortEt.getText().toString());
                }
            }
        });
        this.skydriveAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.skydriveAddressTxt = String.valueOf(CloudSetupActivity.this.skydriveAddressEt
                            .getText());
                    if (null != CloudSetupActivity.this.skydriveAddressTxt
                            && !CloudSetupActivity.this.skydriveAddressTxt.equals("")
                            && !CloudSetupActivity.this.skydriveAddressTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(CloudSetupActivity.this.skydriveAddressTxt)) {
                            Toast.makeText(CloudSetupActivity.this,
                                    "请检查填写内容的正确性", Toast.LENGTH_SHORT).show();
                        } else {
                            CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Skydrive",
                                    "skydriveAddress", CloudSetupActivity.this.skydriveAddressTxt);
                        }
                    }
                }
            }
        });
        this.skydrivePortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO
                            .writeIniString(CloudSetupActivity.this.userIni, "Skydrive",
                                    "skydrivePort", CloudSetupActivity.this.skydrivePortEt.getText()
                                            .toString());
                }
            }
        });
        this.skydrivePathEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO
                            .writeIniString(CloudSetupActivity.this.userIni, "Skydrive",
                                    "skydrivePath", CloudSetupActivity.this.skydrivePathEt.getText()
                                            .toString());
                }
            }
        });
        this.storeAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.storeAddressTxt = String.valueOf(CloudSetupActivity.this.storeAddressEt.getText());
                    if (null != CloudSetupActivity.this.storeAddressTxt
                            && !CloudSetupActivity.this.storeAddressTxt.equals("")
                            && !CloudSetupActivity.this.storeAddressTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(CloudSetupActivity.this.storeAddressTxt)) {
                            Toast.makeText(CloudSetupActivity.this,
                                    "请检查填写内容的正确性", Toast.LENGTH_SHORT).show();
                        } else {
                            CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Store",
                                    "storeAddress", CloudSetupActivity.this.storeAddressTxt);
                        }
                    }
                }
            }
        });
        this.storePortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Store",
                            "storePort", CloudSetupActivity.this.storePortEt.getText().toString());
                }
            }
        });
        this.storePathEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Store",
                            "storePath", CloudSetupActivity.this.storePathEt.getText().toString());
                }
            }
        });
        this.smtpAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.smtpAddressTxt = String.valueOf(CloudSetupActivity.this.smtpAddressEt.getText());
                    if (null != CloudSetupActivity.this.smtpAddressTxt && !CloudSetupActivity.this.smtpAddressTxt.equals("")
                            && !CloudSetupActivity.this.smtpAddressTxt.equals("\\s{1,}")) {
                        CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                                "smtpAddress", CloudSetupActivity.this.smtpAddressTxt);
                    }
                }
            }
        });
        this.smtpPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                            "smtpPort", CloudSetupActivity.this.smtpPortEt.getText().toString());
                }
            }
        });
        this.imapAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.imapAddressTxt = String.valueOf(CloudSetupActivity.this.imapAddressEt.getText());
                    if (null != CloudSetupActivity.this.imapAddressTxt && !CloudSetupActivity.this.imapAddressTxt.equals("")
                            && !CloudSetupActivity.this.imapAddressTxt.equals("\\s{1,}")) {
                        CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                                "imapAddress", CloudSetupActivity.this.imapAddressTxt);
                    }
                }
            }
        });
        this.imapPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                            "imapPort", CloudSetupActivity.this.imapPortEt.getText().toString());
                }
            }
        });
        this.popAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.popAddressTxt = String.valueOf(CloudSetupActivity.this.popAddressEt.getText());
                    if (null != CloudSetupActivity.this.popAddressTxt && !CloudSetupActivity.this.popAddressTxt.equals("")
                            && !CloudSetupActivity.this.popAddressTxt.equals("\\s{1,}")) {
                        CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                                "popAddress", CloudSetupActivity.this.popAddressTxt);
                    }
                }
            }
        });
        this.popPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                            "popPort", CloudSetupActivity.this.popPortEt.getText().toString());
                }
            }
        });
        this.tbsAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.tbsAddressTxt = String.valueOf(CloudSetupActivity.this.tbsAddressEt.getText());
                    if (null != CloudSetupActivity.this.tbsAddressTxt && !CloudSetupActivity.this.tbsAddressTxt.equals("")
                            && !CloudSetupActivity.this.tbsAddressTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(CloudSetupActivity.this.tbsAddressTxt)) {
                            Toast.makeText(CloudSetupActivity.this,
                                    "请检查填写内容的正确性", Toast.LENGTH_SHORT).show();
                        } else {
                            CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                                    "tbsAddress", CloudSetupActivity.this.tbsAddressTxt);
                        }
                    }
                }
            }
        });
        this.tbsPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                            "tbsPort", CloudSetupActivity.this.tbsPortEt.getText().toString());

                }
            }
        });
        this.ebsAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.ebsAddressTxt = String.valueOf(CloudSetupActivity.this.ebsAddressEt.getText());
                    if (null != CloudSetupActivity.this.ebsAddressTxt && !CloudSetupActivity.this.ebsAddressTxt.equals("")
                            && !CloudSetupActivity.this.ebsAddressTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(CloudSetupActivity.this.ebsAddressTxt)) {
                            Toast.makeText(CloudSetupActivity.this,
                                    "请检查填写内容的正确性", Toast.LENGTH_SHORT).show();
                        } else {
                            CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Login",
                                    "ebsAddress", CloudSetupActivity.this.ebsAddressTxt);
                        }
                    }
                }
            }
        });
        this.ebsPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Login",
                            "ebsPort", CloudSetupActivity.this.ebsPortEt.getText().toString());
                }
            }
        });
        this.ebsPathEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Login",
                            "ebsPath", CloudSetupActivity.this.ebsPathEt.getText().toString());
                }
            }
        });
        this.ftsAddressEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.ftsAddressTxt = String.valueOf(CloudSetupActivity.this.ftsAddressEt.getText());
                    if (null != CloudSetupActivity.this.ftsAddressTxt && !CloudSetupActivity.this.ftsAddressTxt.equals("")
                            && !CloudSetupActivity.this.ftsAddressTxt.equals("\\s{1,}")) {
                        if (!StringUtils.isIp(CloudSetupActivity.this.ftsAddressTxt)) {
                            Toast.makeText(CloudSetupActivity.this,
                                    "请检查填写内容的正确性", Toast.LENGTH_SHORT).show();
                        } else {
                            CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Skydrive",
                                    "ftsAddress", CloudSetupActivity.this.ftsAddressTxt);
                        }
                    }
                }
            }
        });
        this.ftsPortEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Skydrive",
                            "ftsPort", CloudSetupActivity.this.ftsPortEt.getText().toString());
                }
            }
        });
        this.ftsPathEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.userIni, "Skydrive",
                            "ftsPath", CloudSetupActivity.this.ftsPathEt.getText().toString());
                }
            }
        });
        this.urlPathEt.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    CloudSetupActivity.this.m_iniFileIO.writeIniString(CloudSetupActivity.this.appNewsFile, "NETWORK",
                            "offlinePath", CloudSetupActivity.this.urlPathEt.getText().toString());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.webCheck:
                this.useall();
                break;
            case R.id.main_set:
                if (this.network_checkbox.isChecked()) {
                    this.network_checkbox.setChecked(false);
                    this.network_url.setVisibility(View.GONE);
                } else {
                    this.network_checkbox.setChecked(true);
                    this.network_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.web_set:
                if (this.web_checkbox.isChecked()) {
                    this.web_checkbox.setChecked(false);
                    this.web_url.setVisibility(View.GONE);
                } else {
                    this.web_checkbox.setChecked(true);
                    this.web_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.skydrive_set:
                if (this.skydrive_checkbox.isChecked()) {
                    this.skydrive_checkbox.setChecked(false);
                    this.skydrive_url.setVisibility(View.GONE);
                } else {
                    this.skydrive_checkbox.setChecked(true);
                    this.skydrive_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.store_set:
                if (this.store_checkbox.isChecked()) {
                    this.store_checkbox.setChecked(false);
                    this.store_url.setVisibility(View.GONE);
                } else {
                    this.store_checkbox.setChecked(true);
                    this.store_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.email_set:
                if (this.email_checkbox.isChecked()) {
                    this.email_checkbox.setChecked(false);
                    this.email_url.setVisibility(View.GONE);
                } else {
                    this.email_checkbox.setChecked(true);
                    this.email_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.ebs_set:
                if (this.ebs_checkbox.isChecked()) {
                    this.ebs_checkbox.setChecked(false);
                    this.ebs_url.setVisibility(View.GONE);
                } else {
                    this.ebs_checkbox.setChecked(true);
                    this.ebs_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tbs_set:
                if (this.tbs_checkbox.isChecked()) {
                    this.tbs_checkbox.setChecked(false);
                    this.tbs_url.setVisibility(View.GONE);
                } else {
                    this.tbs_checkbox.setChecked(true);
                    this.tbs_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fts_set:
                if (this.fts_checkbox.isChecked()) {
                    this.fts_checkbox.setChecked(false);
                    this.fts_url.setVisibility(View.GONE);
                } else {
                    this.fts_checkbox.setChecked(true);
                    this.fts_url.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.module_push_set:
                if (this.module_push_checkbox.isChecked()) {
                    this.module_push_checkbox.setChecked(false);
                    this.module_push_name.setVisibility(View.GONE);
                } else {
                    this.module_push_checkbox.setChecked(true);
                    this.module_push_name.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.send_net_btn:
                if (this.send_net_box.isChecked()) {
                    this.send_net_box.setChecked(false);
                    this.send_net_set.setVisibility(View.GONE);
                } else {
                    this.send_net_box.setChecked(true);
                    this.send_net_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.accept_net_btn:
                if (this.accept_net_box.isChecked()) {
                    this.accept_net_box.setChecked(false);
                    this.accept_net_set.setVisibility(View.GONE);
                } else {
                    this.accept_net_box.setChecked(true);
                    this.accept_net_set.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.finish_btn:
                if (this.Save) {
                    if (!this.savePushSetting()) {
                        break;
                    }
                    if (!this.saveFtsSetting()) {
                        break;
                    }
                    if (!this.saveEbsSetting()) {
                        break;
                    }
                    if (!this.saveTbsSetting()) {
                        break;
                    }
                    if (!this.saveEmailSetting()) {
                        break;
                    }
                    if (!this.saveStoreSetting()) {
                        break;
                    }
                    if (!this.saveSkyDriveSetting()) {
                        break;
                    }
                    if (!this.saveWebSetting()) {
                        break;
                    }
                } else {
                    if (this.web_url.isShown()) {
                        if (!this.saveWebSetting()) {
                            break;
                        }
                    }
                    if (this.skydrive_url.isShown()) {
                        if (!this.saveSkyDriveSetting()) {
                            break;
                        }
                    }
                    if (this.store_url.isShown()) {
                        if (!this.saveStoreSetting()) {
                            break;
                        }
                    }
                    if (this.email_url.isShown()) {
                        if (!this.saveEmailSetting()) {
                            break;
                        }
                    }
                    if (this.tbs_url.isShown()) {
                        if (!this.saveTbsSetting()) {
                            break;
                        }
                    }
                    if (this.ebs_url.isShown()) {
                        if (!this.saveEbsSetting()) {
                            break;
                        }
                    }
                    if (this.fts_url.isShown()) {
                        if (!this.saveFtsSetting()) {
                            break;
                        }
                    }
                    if (this.module_push_name.isShown()) {
                        if (!this.savePushSetting()) {
                            break;
                        }
                    }
                }
                this.saveNetWorkSetting();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
        }
    }

    private void useall() {
        // TODO Auto-generated method stub
        String tempUrlIpTxt = this.netName.getText().toString();
        this.webAddressEt.setText(tempUrlIpTxt);
        this.module_push_address.setText(tempUrlIpTxt);
        this.module_send_address.setText(tempUrlIpTxt);
        this.skydriveAddressEt.setText(tempUrlIpTxt);
        this.storeAddressEt.setText(tempUrlIpTxt);
        this.smtpAddressEt.setText(tempUrlIpTxt);
        this.imapAddressEt.setText(tempUrlIpTxt);
        this.popAddressEt.setText(tempUrlIpTxt);
        this.tbsAddressEt.setText(tempUrlIpTxt);
        this.ebsAddressEt.setText(tempUrlIpTxt);
        this.ftsAddressEt.setText(tempUrlIpTxt);
        this.urlIpEt.setText(tempUrlIpTxt);
        //netName.setText(tempUrlIpTxt);
        this.Save = true;
    }

    private void saveNetWorkSetting() {
        // TODO Auto-generated method stub
        this.urlIpTxt = String.valueOf(this.urlIpEt.getText());
        this.urlPortTxt = String.valueOf(this.urlPortEt.getText());
        this.userNameTxt = String.valueOf(this.userNameEt.getText());
        this.userPwdTxt = String.valueOf(this.userPwdEt.getText());
        if (null != this.urlIpTxt && !this.urlIpTxt.equals("")
                && !this.urlIpTxt.equals("\\s{1,}") && null != this.urlPortTxt
                && !this.urlPortTxt.equals("") && !this.urlPortTxt.equals("\\s{1,}")) {
            this.editSharePreFernces();
            this.finish();
        } else {
            this.onBackPressed();
            Toast.makeText(this, "请正确填写网络设置内容后在尝试保存",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private boolean saveWebSetting() {
        // TODO Auto-generated method stub
        this.webAddressTxt = String.valueOf(this.webAddressEt.getText());
        if (null != this.webAddressTxt && !this.webAddressTxt.equals("")
                && !this.webAddressTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.webAddressTxt)) {
                Toast.makeText(this, "请正确填写web内容后在尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.m_iniFileIO.writeIniString(this.appNewsFile, "TBSAPP",
                        "webAddress", this.webAddressTxt);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "TBSAPP", "webPort",
                        this.webPortEt.getText().toString());
                return true;
            }
        }
        return false;
    }


    private boolean saveFtsSetting() {
        // TODO Auto-generated method stub
        this.ftsAddressTxt = String.valueOf(this.ftsAddressEt.getText());
        if (null != this.ftsAddressTxt && !this.ftsAddressTxt.equals("")
                && !this.ftsAddressTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.ftsAddressTxt)) {
                Toast.makeText(this, "请正确填写fts内容后在尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.m_iniFileIO.writeIniString(this.userIni, "Skydrive",
                        "ftsAddress", this.ftsAddressTxt);
                this.m_iniFileIO.writeIniString(this.userIni, "Skydrive", "ftsPort",
                        this.ftsPortEt.getText().toString());
                this.m_iniFileIO.writeIniString(this.userIni, "Skydrive", "ftsPath",
                        this.ftsPathEt.getText().toString());
                return true;
            }
        }
        return false;
    }

    private boolean savePushSetting() {
        // TODO Auto-generated method stub
        this.address_editTxt = String.valueOf(this.module_push_address.getText());
        if (null != this.address_editTxt && !this.address_editTxt.equals("")
                && !this.address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.address_editTxt)) {
                Toast.makeText(this, "请正确填写内容后再尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.address_editTxt = String.valueOf(this.module_send_address.getText());
                if (null != this.address_editTxt && !this.address_editTxt.equals("")
                        && !this.address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(this.address_editTxt)) {
                        Toast.makeText(this,
                                "请正确填写内容后再尝试保存", Toast.LENGTH_SHORT).show();
                    } else {
                        this.m_iniFileIO.writeIniString(userIni, "Push",
                                "pushAddress", this.module_push_address.getText()
                                        .toString());
                        this.m_iniFileIO.writeIniString(userIni, "Push",
                                "pushPath", this.module_push_path.getText()
                                        .toString());
                        this.m_iniFileIO.writeIniString(userIni, "Push",
                                "pushPort", this.module_push_port.getText()
                                        .toString());
                        this.m_iniFileIO.writeIniString(userIni, "Push",
                                "sendAddress", this.module_send_address.getText()
                                        .toString());
                        this.m_iniFileIO.writeIniString(userIni, "Push",
                                "sendPath", this.module_send_path.getText()
                                        .toString());
                        this.m_iniFileIO.writeIniString(userIni, "Push",
                                "sendPort", this.module_send_port.getText()
                                        .toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean saveEmailSetting() {
        // TODO Auto-generated method stub
        this.smtpAddressTxt = String.valueOf(this.smtpAddressEt.getText());
        this.imapAddressTxt = String.valueOf(this.imapAddressEt.getText());
        this.popAddressTxt = String.valueOf(this.popAddressEt.getText());
        if (null != this.smtpAddressTxt && !this.smtpAddressTxt.equals("")
                && !this.smtpAddressTxt.equals("\\s{1,}")
                && null != this.imapAddressTxt && !this.imapAddressTxt.equals("")
                && !this.imapAddressTxt.equals("\\s{1,}") && null != this.popAddressTxt
                && !this.popAddressTxt.equals("")
                && !this.popAddressTxt.equals("\\s{1,}")) {
            this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "smtpAddress",
                    this.smtpAddressTxt);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "smtpPort",
                    this.smtpPortEt.getText().toString());
            this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "imapAddress",
                    this.imapAddressTxt);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "imapPort",
                    this.imapPortEt.getText().toString());
            this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "popAddress",
                    this.popAddressTxt);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "popPort",
                    this.popPortEt.getText().toString());
        }
        return true;
    }

    private boolean saveSkyDriveSetting() {
        // TODO Auto-generated method stub
        this.skydriveAddressTxt = String.valueOf(this.skydriveAddressEt.getText());
        if (null != this.skydriveAddressTxt && !this.skydriveAddressTxt.equals("")
                && !this.skydriveAddressTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.skydriveAddressTxt)) {
                Toast.makeText(this, "请正确填写网盘内容后在尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.m_iniFileIO.writeIniString(this.userIni, "Skydrive",
                        "skydriveAddress", this.skydriveAddressTxt);
                this.m_iniFileIO.writeIniString(this.userIni, "Skydrive",
                        "skydrivePort", this.skydrivePortEt.getText().toString());
                this.m_iniFileIO.writeIniString(this.userIni, "Skydrive",
                        "skydrivePath", this.skydrivePathEt.getText().toString());
                return true;
            }
        }
        return false;
    }

    private boolean saveStoreSetting() {
        // TODO Auto-generated method stub
        this.storeAddressTxt = String.valueOf(this.storeAddressEt.getText());
        if (null != this.storeAddressTxt && !this.storeAddressTxt.equals("")
                && !this.storeAddressTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.storeAddressTxt)) {
                Toast.makeText(this, "请正确填写商城内容后在尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.m_iniFileIO.writeIniString(this.userIni, "Store",
                        "storeAddress", this.storeAddressTxt);
                this.m_iniFileIO.writeIniString(this.userIni, "Store", "storePort",
                        this.storePortEt.getText().toString());
                this.m_iniFileIO.writeIniString(this.userIni, "Store",
                        "storePath", this.storePathEt.getText().toString());
                return true;
            }
        }
        return false;
    }

    private boolean saveTbsSetting() {
        // TODO Auto-generated method stub
        this.tbsAddressTxt = String.valueOf(this.tbsAddressEt.getText());
        if (null != this.tbsAddressTxt && !this.tbsAddressTxt.equals("")
                && !this.tbsAddressTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.tbsAddressTxt)) {
                Toast.makeText(this, "请正确填写tbs内容后在尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK",
                        "tbsAddress", this.tbsAddressTxt);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "tbsPort",
                        this.tbsPortEt.getText().toString());
                return true;
            }
        }
        return false;
    }


    private boolean saveEbsSetting() {
        // TODO Auto-generated method stub
        this.ebsAddressTxt = String.valueOf(this.ebsAddressEt.getText());
        if (null != this.ebsAddressTxt && !this.ebsAddressTxt.equals("")
                && !this.ebsAddressTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(this.ebsAddressTxt)) {
                Toast.makeText(this, "请正确填写ebs内容后在尝试保存",
                        Toast.LENGTH_SHORT).show();
            } else {
                this.m_iniFileIO.writeIniString(this.userIni, "Login",
                        "ebsAddress", this.ebsAddressTxt);
                this.m_iniFileIO.writeIniString(this.userIni, "Login", "ebsPort",
                        this.ebsPortEt.getText().toString());
                this.m_iniFileIO.writeIniString(this.userIni, "Login", "ebsPath",
                        this.ebsPathEt.getText().toString());
                return true;
            }
        }
        return false;
    }

    private void editSharePreFernces() {
        // TODO Auto-generated method stub
        // 获得编辑器
        this.m_iniFileIO.writeIniString(this.userIni, "Offline", "offlineAddress",
                this.urlIpEt.getText().toString());
        this.m_iniFileIO.writeIniString(this.userIni, "Offline", "offlinePort",
                this.urlPortEt.getText().toString());
        this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK",
                "networkUserName", this.userNameEt.getText().toString());
        this.m_iniFileIO.writeIniString(this.appNewsFile, "NETWORK", "networkPwd",
                this.userPwdEt.getText().toString());

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onBackPressed();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
        }
        return true;
    }

}
