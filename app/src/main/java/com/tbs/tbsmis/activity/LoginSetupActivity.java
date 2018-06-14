package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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

public class LoginSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView backBtn;
    private TextView title;
    private ImageView finishBtn;

    private IniFile IniFile;
    private EditText ebsAddressEt;
    private EditText ebsPortEt;
    private EditText ebsPathEt;
    private String userIni;
    private String appIniFile;

    private CheckBox user_start_CheckBox;
    private RelativeLayout user_start_set;
    private LinearLayout user_task_set;
    private LinearLayout user_inactive_set;
    private TextView show_time;
    private TextView show_inactive_time;
    private LinearLayout user_show_auto_set;
    private CheckBox user_show_CheckBox;
    private CheckBox menu_show_CheckBox;
    private LinearLayout user_show_set;
    private boolean isOpenPop = false;
    private PopupWindow set_CheckTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_setup);
        MyActivity.getInstance().addActivity(this);
        initView();
    }

    private void initView() {
        backBtn = (ImageView) findViewById(R.id.more_btn);
        finishBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        title.setText("登录设置");
        backBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        initEbs();
        initServerSet();
    }

    private void initEbs() {
        // TODO Auto-generated method stub
        ebsAddressEt = (EditText) findViewById(R.id.ebs_address);
        ebsPortEt = (EditText) findViewById(R.id.ebs_port);
        ebsPathEt = (EditText) findViewById(R.id.ebs_path);
        IniFile = new IniFile();
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
        ebsAddressEt.setText(IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0));
        ebsPortEt.setText(IniFile.getIniString(userIni, "Login",
                "ebsPort", "8083", (byte) 0));
        ebsPathEt.setText(IniFile.getIniString(userIni, "Login",
                "ebsPath", "/EBS/UserServlet", (byte) 0));
    }

    private void initServerSet() {
        user_start_set = (RelativeLayout) findViewById(R.id.user_start_set);
        user_start_CheckBox = (CheckBox) findViewById(R.id.user_start_CheckBox);
        user_task_set = (LinearLayout) findViewById(R.id.user_task_set);
        user_inactive_set = (LinearLayout) findViewById(R.id.user_inactive_set);
        show_time = (TextView) findViewById(R.id.show_time);
        show_inactive_time = (TextView) findViewById(R.id.show_inactive_time);
        user_show_auto_set = (LinearLayout) findViewById(R.id.user_show_auto_set);
        user_show_CheckBox = (CheckBox) findViewById(R.id.user_show_CheckBox);
        menu_show_CheckBox = (CheckBox) findViewById(R.id.menu_show_CheckBox);
        user_show_set = (LinearLayout) findViewById(R.id.user_show_set);
        int nVal = Integer.parseInt(IniFile.getIniString(userIni,
                "Login", "UserUpate", "1", (byte) 0));
        if (nVal == 1) {
            user_start_CheckBox.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni,
                "Login", "autoLoginShow", "1", (byte) 0));
        if (nVal == 1) {
            user_show_CheckBox.setChecked(true);
        }
        nVal = Integer.parseInt(IniFile.getIniString(userIni,
                "Login", "menuShowUser", "1", (byte) 0));
        if (nVal == 1) {
            menu_show_CheckBox.setChecked(true);
        }
        show_time.setText(IniFile.getIniString(userIni, "Login",
                "userTime", "3", (byte) 0) + "分钟");
        show_inactive_time.setText(IniFile.getIniString(userIni,
                "Login", "inactiveTime", "5", (byte) 0) + "分钟");
        user_start_set.setOnClickListener(this);
        user_task_set.setOnClickListener(this);
        user_inactive_set.setOnClickListener(this);
        user_show_auto_set.setOnClickListener(this);
        user_show_set.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                break;
            case R.id.finish_btn:
                String ebsAddressTxt = String.valueOf(ebsAddressEt.getText());
                if (null != ebsAddressTxt && !ebsAddressTxt.equals("")
                        && !ebsAddressTxt.equals("\\s{1,}")) {
                    if (!StringUtils.isIp(ebsAddressTxt)) {
                        Toast.makeText(LoginSetupActivity.this,
                                "请检查配置地址的正确性", Toast.LENGTH_SHORT).show();
                        ebsAddressEt.setFocusable(true);
                        break;
                    }
                } else {
                    Toast.makeText(LoginSetupActivity.this,
                            "配置地址不正确或为空", Toast.LENGTH_SHORT).show();
                    ebsAddressEt.setFocusable(true);
                    break;
                }
                ebsAddressTxt = String
                        .valueOf(ebsPortEt.getText());
                if (StringUtils.isEmpty(ebsAddressTxt)) {
                    Toast.makeText(this,
                            "配置端口不可为空", Toast.LENGTH_SHORT)
                            .show();
                    ebsPortEt.setFocusable(true);
                    break;
                }
                IniFile.writeIniString(userIni, "Login",
                        "ebsAddress", ebsAddressEt.getText().toString());
                IniFile.writeIniString(userIni, "Login",
                        "ebsPort", ebsPortEt.getText().toString());
                IniFile.writeIniString(userIni, "Login",
                        "ebsPath", ebsPathEt.getText().toString());
                finish();
                break;
            case R.id.user_start_set:
                if (user_start_CheckBox.isChecked()) {
                    user_start_CheckBox.setChecked(false);
                    IniFile.writeIniString(userIni, "Login", "UserUpate",
                            "0");
//                stopService(new Intent(
//                        getString(string.ServerName1)));
                    Intent intent = new Intent();
                    intent.setAction(getString(R.string.ServerName1));//你定义的service的action
                    intent.setPackage(getPackageName());//这里你需要设置你应用的包名
                    stopService(intent);
                } else {
                    user_start_CheckBox.setChecked(true);
                    IniFile.writeIniString(userIni, "Login", "UserUpate",
                            "1");
                    Intent mIntent = new Intent();
                    mIntent.setAction(this
                            .getString(R.string.ServerName1));//你定义的service的action
                    mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                    startService(mIntent);
                }
                break;
            case R.id.user_show_auto_set:
                if (user_show_CheckBox.isChecked()) {
                    user_show_CheckBox.setChecked(false);
                    IniFile.writeIniString(userIni, "Login",
                            "autoLoginShow", "0");
                } else {
                    user_show_CheckBox.setChecked(true);
                    IniFile.writeIniString(userIni, "Login",
                            "autoLoginShow", "1");
                }
                break;
            case R.id.user_task_set:
                changSetSeekState(v, 1);
                break;
            case R.id.user_inactive_set:
                changSetSeekState(v, 2);
                break;
            case R.id.user_show_set:
                if (menu_show_CheckBox.isChecked()) {
                    menu_show_CheckBox.setChecked(false);
                    IniFile.writeIniString(userIni, "Login",
                            "menuShowUser", "0");
                } else {
                    menu_show_CheckBox.setChecked(true);
                    IniFile.writeIniString(userIni, "Login",
                            "menuShowUser", "1");
                }
                break;
            case R.id.feedback_close_button:
                set_CheckTime.dismiss();
                break;
        }

    }

    public void changSetSeekState(View v, int action) {
        isOpenPop = !isOpenPop;
        if (isOpenPop) {
            checkTime_Set(v, action);
            isOpenPop = false;
        } else {
            if (set_CheckTime != null) {
                set_CheckTime.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void checkTime_Set(View parent, int action) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.set_check_time, null);
        // toolcheck = (CheckBox) view.findViewById(R.id.menu_check_box);
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.SeekBar03);
        final TextView textView5 = (TextView) view
                .findViewById(R.id.TextView05);
        ImageButton mClose = (ImageButton) view
                .findViewById(R.id.feedback_close_button);
        mClose.setOnClickListener(this);
        int checkTime;
        switch (action) {
            case 1:
                seekBar2.setMax(15);
                checkTime = Integer.parseInt(IniFile.getIniString(userIni,
                        "Login", "userTime", "3", (byte) 0));
                seekBar2.setProgress(checkTime);
                textView5.setText("检查状态时间间隔为：" + checkTime + "分钟");
                seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {
                    int middleTime;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        textView5.setText("检查状态时间间隔为：" + progress + "分钟");
                        middleTime = progress;
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        // textView6.setText("确定在此位置？...");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        IniFile.writeIniString(userIni, "Login",
                                "userTime", middleTime + "");
                        textView5.setText("检查状态时间间隔为：" + middleTime + "分钟");
                        show_time.setText(middleTime + "分钟");
                    }

                });
                break;
            case 2:
                seekBar2.setMax(30);
                checkTime = Integer.parseInt(IniFile.getIniString(userIni,
                        "Login", "inactiveTime", "5", (byte) 0));
                seekBar2.setProgress(checkTime);
                textView5.setText("不活动超时时间为：" + checkTime + "分钟");
                seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
                {
                    int middleTime;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        textView5.setText("不活动超时时间为：" + progress + "分钟");
                        middleTime = progress;
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        // textView6.setText("确定在此位置？...");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                        IniFile.writeIniString(userIni, "Login",
                                "inactiveTime", middleTime + "");
                        textView5.setText("不活动超时时间为：" + middleTime + "分钟");
                        show_inactive_time.setText(middleTime + "分钟");
                    }

                });
                break;
        }
        set_CheckTime = new PopupWindow(view,
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        set_CheckTime.setFocusable(false);
        set_CheckTime.setOutsideTouchable(true);
//        set_CheckTime.setBackgroundDrawable(new ColorDrawable(
//                getResources().getColor(R.color.dim_checktime_light)));
        set_CheckTime.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss() {
                isOpenPop = false;
            }
        });
        // set_CheckTime.showAsDropDown(parent, -95, 3);
        set_CheckTime.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER,
                0, 0);
        set_CheckTime.update();
    }
}
