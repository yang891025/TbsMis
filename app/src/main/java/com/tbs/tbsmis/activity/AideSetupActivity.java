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
import com.tbs.tbsmis.update.UpdateManageActivity;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

/**
 * Created by TBS on 2017/2/16.
 */

public class AideSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private RelativeLayout task_setup;
    private RelativeLayout about_app_msg;
    private RelativeLayout update_manage;
    private RelativeLayout feedback_manage;
    private RelativeLayout update_up;
    private EditText software_address;
    private EditText software_port;
    private EditText update_path;
    private EditText update_up_path;
    private EditText update_manage_path;
    private EditText aboutapp_path;
    private EditText feedback_path;
    private EditText feedback_manage_path;
    private RelativeLayout feedback_show_layout;
    private RelativeLayout feedback_layout;
    private CheckBox feedback_show_box;
    private RelativeLayout about_app_layout;
    private RelativeLayout aboutapp_show_layout;
    private CheckBox aboutapp_show_box;
    private IniFile m_iniFileIO;
    private String userIni;
    private RelativeLayout introduction_layout;
    private RelativeLayout introduction_show_layout;
    private CheckBox introduction_show_box;
    private EditText introduction_path;
    private RelativeLayout operationGuide_layout;
    private RelativeLayout operationGuide_show_layout;
    private CheckBox operationGuide_show_box;
    private EditText operationGuide_path;
    private RelativeLayout update_show_layout;
    private CheckBox update_show_box;
    private RelativeLayout clear_show_layout;
    private CheckBox clear_show_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.software_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
        initPath();
        initset();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        task_setup = (RelativeLayout) findViewById(R.id.task_setup);
        about_app_msg = (RelativeLayout) findViewById(R.id.about_app_msg);
        update_manage = (RelativeLayout) findViewById(R.id.update_manage);
        update_up = (RelativeLayout) findViewById(R.id.update_up);
        feedback_manage = (RelativeLayout) findViewById(R.id.feedback_manage);
        task_setup.setVisibility(View.GONE);
        title.setText("辅助设置");
        backBtn.setOnClickListener(this);
        task_setup.setOnClickListener(this);
        update_manage.setOnClickListener(this);
        update_up.setOnClickListener(this);
        feedback_manage.setOnClickListener(this);
        about_app_msg.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    private void initset() {
        feedback_layout = (RelativeLayout) findViewById(R.id.feedback_layout);
        feedback_show_layout = (RelativeLayout) findViewById(R.id.feedback_show_layout);
        feedback_show_box = (CheckBox) findViewById(R.id.feedback_show_box);

        about_app_layout = (RelativeLayout) findViewById(R.id.about_app_layout);
        aboutapp_show_layout = (RelativeLayout) findViewById(R.id.aboutapp_show_layout);
        aboutapp_show_box = (CheckBox) findViewById(R.id.aboutapp_show_box);

        introduction_layout = (RelativeLayout) findViewById(R.id.introduction_layout);
        introduction_show_layout = (RelativeLayout) findViewById(R.id.introduction_show_layout);
        introduction_show_box = (CheckBox) findViewById(R.id.introduction_show_box);
        introduction_path = (EditText) findViewById(R.id.introduction_path);

        operationGuide_layout = (RelativeLayout) findViewById(R.id.operationGuide_layout);
        operationGuide_show_layout = (RelativeLayout) findViewById(R.id.operationGuide_show_layout);
        operationGuide_show_box = (CheckBox) findViewById(R.id.operationGuide_show_box);
        operationGuide_path = (EditText) findViewById(R.id.operationGuide_path);

        update_show_layout = (RelativeLayout) findViewById(R.id.update_show_layout);
        update_show_box = (CheckBox) findViewById(R.id.update_show_box);
        update_path = (EditText) findViewById(R.id.update_path);
        update_up_path = (EditText) findViewById(R.id.update_up_path);
        update_manage_path = (EditText) findViewById(R.id.update_manage_path);

        clear_show_layout = (RelativeLayout) findViewById(R.id.clear_show_layout);
        clear_show_box = (CheckBox) findViewById(R.id.clear_show_box);

        software_address = (EditText) findViewById(R.id.software_address);
        software_port = (EditText) findViewById(R.id.software_port);
        feedback_path = (EditText) findViewById(R.id.feedback_path);
        feedback_manage_path = (EditText) findViewById(R.id.feedback_manage_path);
        aboutapp_path = (EditText) findViewById(R.id.aboutapp_path);
        software_address.setText(m_iniFileIO.getIniString(userIni,
                "Software", "softwareAddress", constants.DefaultServerIp, (byte) 0));
        software_port.setText(m_iniFileIO.getIniString(userIni,
                "Software", "softwarePort", constants.DefaultServerPort, (byte) 0));
        update_path.setText(UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "update_url",
                getString(R.string.UpdateUrl)));
        update_manage_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "updateManagePath", "/Store/ShowManageInfo.cbs?rePath=TbsMis", (byte) 0));
        update_up_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "updateUpPath", "/Store/Upload.cbs?rePath=TbsMis", (byte) 0));
        aboutapp_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "aboutPath", "/tbsapp/about/about.html", (byte) 0));
        feedback_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "feedbackPath", "/feedback/feedback.cbs", (byte) 0));
        feedback_manage_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "feedbackManagePath", "/feedback/feedbackall.cbs", (byte) 0));
        operationGuide_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "operationGuidePath", "/tbsapp/help/help.html", (byte) 0));
        introduction_path.setText(m_iniFileIO.getIniString(this.userIni, "Software",
                "IntroductionPath", "/tbsapp/introduction/introduction.html", (byte) 0));
        int nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "feedback_show_in_set", "1", (byte) 0));
        if (nVal == 1) {
            feedback_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "aboutapp_show_in_about", "1", (byte) 0));
        if (nVal == 1) {
            aboutapp_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "operationGuideSetting", "0", (byte) 0));
        if (nVal == 1) {
            operationGuide_show_box.setChecked(true);
        }
        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "introductionSetting", "0", (byte) 0));
        if (nVal == 1) {
            introduction_show_box.setChecked(true);
        }

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "clear_show_in_set", "1", (byte) 0));
        if (nVal == 1) {
            clear_show_box.setChecked(true);
        }

        nVal = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Software",
                "update_show_in_set", "1", (byte) 0));
        if (nVal == 1) {
            update_show_box.setChecked(true);
        }

        feedback_layout.setOnClickListener(this);
        about_app_layout.setOnClickListener(this);
        introduction_layout.setOnClickListener(this);
        operationGuide_layout.setOnClickListener(this);
        feedback_show_layout.setOnClickListener(this);
        update_show_layout.setOnClickListener(this);
        clear_show_layout.setOnClickListener(this);
        aboutapp_show_layout.setOnClickListener(this);
        introduction_show_layout.setOnClickListener(this);
        operationGuide_show_layout.setOnClickListener(this);
    }

    private void saveET() {
        String address_editTxt = String
                .valueOf(software_address.getText());
        if (null != address_editTxt
                && !address_editTxt.equals("")
                && !address_editTxt.equals("\\s{1,}")) {
            if (!StringUtils.isIp(address_editTxt)) {
                Toast.makeText(this,
                        "请检查网络地址的正确性", Toast.LENGTH_SHORT)
                        .show();
                software_address.setFocusable(true);
                return;
            }
        } else {
            Toast.makeText(this,
                    "网络地址不正确或为空", Toast.LENGTH_SHORT)
                    .show();
            software_address.setFocusable(true);
            return;
        }
        address_editTxt = String
                .valueOf(software_port.getText());
        if (StringUtils.isEmpty(address_editTxt)) {
            Toast.makeText(this,
                    "网络地址端口不可为空", Toast.LENGTH_SHORT)
                    .show();
            software_port.setFocusable(true);
            return;
        }
        m_iniFileIO.writeIniString(userIni, "Software",
                "softwareAddress", software_address.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "softwarePort", software_port.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "feedbackPath", feedback_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "feedbackManagePath", feedback_manage_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "aboutPath", aboutapp_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "operationGuidePath", operationGuide_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "IntroductionPath", introduction_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "updateUpPath", update_up_path.getText()
                        .toString());
        m_iniFileIO.writeIniString(userIni, "Software",
                "updateManagePath", update_manage_path.getText()
                        .toString());
        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                "update_url", update_path.getText().toString());
        finish();
    }

    @Override
    public void onClick(View v) {
        String address_editTxt = "";
        switch (v.getId()) {
            case R.id.feedback_layout:
                 address_editTxt = String
                        .valueOf(software_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查网络地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        software_address.setFocusable(true);
                        return;
                    }
                } else {
                    Toast.makeText(this,
                            "网络地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    software_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(software_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "网络地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    software_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwareAddress", software_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwarePort", software_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "feedbackPath", feedback_path.getText()
                                .toString());
                Intent intent1 = new Intent();
                intent1.setClass(this, FeedBack_New.class);
                startActivity(intent1);
                break;
            case R.id.about_app_layout:
                 address_editTxt = String
                        .valueOf(software_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查网络地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        software_address.setFocusable(true);
                        return;
                    }
                } else {
                    Toast.makeText(this,
                            "网络地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    software_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(software_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "网络地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    software_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwareAddress", software_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwarePort", software_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "aboutPath", aboutapp_path.getText()
                                .toString());
                Intent about = new Intent();
                about.setClass(this, AboutAppActivity.class);
                startActivity(about);
                break;
            case R.id.introduction_layout:
                 address_editTxt = String
                        .valueOf(software_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查网络地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        software_address.setFocusable(true);
                        return;
                    }
                } else {
                    Toast.makeText(this,
                            "网络地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    software_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(software_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "网络地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    software_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwareAddress", software_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwarePort", software_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "IntroductionPath", introduction_path.getText()
                                .toString());
                Intent Introduction = new Intent();
                Introduction.setClass(this, SystemIntrActivity.class);
                startActivity(Introduction);
                break;
            case R.id.operationGuide_layout:
                 address_editTxt = String
                        .valueOf(software_address.getText());
                if (null != address_editTxt
                        && !address_editTxt.equals("")
                        && !address_editTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(address_editTxt)) {
                        Toast.makeText(this,
                                "请检查网络地址的正确性", Toast.LENGTH_SHORT)
                                .show();
                        software_address.setFocusable(true);
                        return;
                    }
                } else {
                    Toast.makeText(this,
                            "网络地址不正确或为空", Toast.LENGTH_SHORT)
                            .show();
                    software_address.setFocusable(true);
                    return;
                }
                address_editTxt = String
                        .valueOf(software_port.getText());
                if (StringUtils.isEmpty(address_editTxt)) {
                    Toast.makeText(this,
                            "网络地址端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    software_port.setFocusable(true);
                    return;
                }
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwareAddress", software_address.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "softwarePort", software_port.getText()
                                .toString());
                m_iniFileIO.writeIniString(userIni, "Software",
                        "operationGuidePath", operationGuide_path.getText()
                                .toString());
                Intent operationGuide = new Intent();
                operationGuide.setClass(this, OperationGuideActivity.class);
                startActivity(operationGuide);
                break;
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                saveET();
                break;
            case R.id.task_setup:
                Intent intent = new Intent();
                intent.setClass(this, NetTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.about_app_msg:
                Intent about_app = new Intent();
                about_app.setClass(this, SystemInfoActivity.class);
                startActivity(about_app);
                break;
            case R.id.update_manage:
                String updateUpPath = m_iniFileIO.getIniString(userIni, "Software",
                        "updateManagePath", "/Store/ShowManageInfo.cbs?rePath=TbsMis",
                        (byte) 0);
                String appName = "TbsMis";
                if(updateUpPath.contains("?")){
                    appName = updateUpPath.substring(updateUpPath.lastIndexOf("=") + 1);
                    updateUpPath = updateUpPath.substring(0, updateUpPath.lastIndexOf("?"));
                }
                Intent intent2 = new Intent();
                intent2.putExtra("path",updateUpPath+"?path="+"安卓应用/"+appName+"&selType=all");
                intent2.setClass(this, BackgroundManageActivity.class);
                startActivity(intent2);
                break;
            case R.id.update_up:
                Intent intentUp = new Intent();
                intentUp.setClass(this, UpdateManageActivity.class);
                startActivity(intentUp);
                break;
            case R.id.feedback_manage:
                Intent intent3 = new Intent();
                intent3.putExtra("flag",1);
                intent3.setClass(this, FeedBack_New.class);
                startActivity(intent3);
                break;
            case R.id.feedback_show_layout:
                if (feedback_show_box.isChecked()) {
                    feedback_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "feedback_show_in_set", "0");
                } else {
                    feedback_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "feedback_show_in_set", "1");
                }
                break;
            case R.id.operationGuide_show_layout:
                if (operationGuide_show_box.isChecked()) {
                    operationGuide_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "operationGuideSetting", "0");
                } else {
                    operationGuide_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "operationGuideSetting", "1");
                }
                break;
            case R.id.aboutapp_show_layout:
                if (aboutapp_show_box.isChecked()) {
                    aboutapp_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "aboutapp_show_in_about", "0");
                } else {
                    aboutapp_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "aboutapp_show_in_about", "1");
                }
                break;
            case R.id.clear_show_layout:
                if (clear_show_box.isChecked()) {
                    clear_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "clear_show_in_set", "0");
                } else {
                    clear_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "clear_show_in_set", "1");
                }
                break;
            case R.id.update_show_layout:
                if (update_show_box.isChecked()) {
                    update_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "update_show_in_set", "0");
                } else {
                    update_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "update_show_in_set", "1");
                }
                break;
            case R.id.introduction_show_layout:
                if (introduction_show_box.isChecked()) {
                    introduction_show_box.setChecked(false);
                    m_iniFileIO.writeIniString(userIni, "Software", "introductionSetting", "0");
                } else {
                    introduction_show_box.setChecked(true);
                    m_iniFileIO.writeIniString(userIni, "Software", "introductionSetting", "1");
                }
                break;
        }
    }
}
