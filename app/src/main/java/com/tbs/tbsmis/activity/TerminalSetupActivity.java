package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.check.NetTaskTimerDialog;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TerminalSetupActivity extends Activity implements View.OnClickListener {

	private ImageView finishBtn, downBtn;
	private EditText emailEt, passWordEt, localAddress, localPort;
	private String localIpTxt, localPortTxt;
	private String tempAddressTxt, tempPortTxt;
	private TextView title;
	private Button restart;
	private CheckBox No_CheckBox;
	private CheckBox Local_CheckBox;
	private CheckBox Other_CheckBox;
	private LinearLayout show_other;
	private LinearLayout show_no;
	private int CurrentServer;
	private LinearLayout No_TypeItem;
	private LinearLayout Local_TypeItem;
	private LinearLayout Other_TypeItem;
	private LinearLayout show_local;
	private Button start;
	private Button stop;
	private Button check;
	private TextView status;
	private CheckBox status_CheckBox;
	private Object opString;
	private String webRoot;
	private IniFile m_iniFileIO;
	private CheckBox restart_web_CheckBox;
	private CheckBox dirbrowser_web_CheckBox;
	private CheckBox remotevisit_web_CheckBox;
	private LinearLayout restart_web;
	private LinearLayout dirbrowser_web;
	private LinearLayout remotevisit_web;
	private String WebIniFile;
	private LinearLayout web_server_set;
	private Button no_restart;
	private Button no_start;
	private Button no_stop;
	private Button no_check;
	private TextView no_status;
	private CheckBox dirbrowser_no_CheckBox;
	private CheckBox remotevisit_no_CheckBox;
	private CheckBox no_CheckBox;
	private LinearLayout remotevisit_no;
	private LinearLayout dirbrowser_no;
	private EditText noAddress;
	private EditText noPort;
	private LinearLayout no_server_set;
	private LinearLayout No_web_TypeItem;
	private LinearLayout Http_TypeItem;
	private CheckBox Http_CheckBox;
	private LinearLayout user_TypeItem;
	private LinearLayout task_TypeItem;
	private LinearLayout show_user_set;
	private LinearLayout show_task;
	private CheckBox user_CheckBox;
	private CheckBox task_CheckBox;
	private CheckBox user_start_CheckBox;
	private LinearLayout user_start_set;
	private PopupWindow set_CheckTime;
	private boolean isOpenPop;

	private LinearLayout user_task_set;
	private LinearLayout user_inactive_set;
	private LinearLayout user_task_time;
	private LinearLayout user_task_startime;
	private TextView show_task_startime;
	private TextView show_time;
	private TextView show_inactive_time;
	private TextView show_task_time;

	private TerminalSetupActivity.ServiceReceiver ServiceReceiver;
	private LinearLayout user_show_auto_set;
	private CheckBox user_show_CheckBox;
	private LinearLayout start_task;
	private CheckBox menu_show_CheckBox;
	private LinearLayout user_show_set;
	private CheckBox start_task_CheckBox;
	private String appTimeFile;
	private LinearLayout show_http;
	private TextView http_status;
	private Button http_restart;
	private Button http_start;
	private Button http_stop;
	private Button http_check;
	private EditText httpAddress;
	private EditText httpPort;
	private LinearLayout restart_http;
	private CheckBox restart_http_CheckBox;
    private CheckBox No_web_CheckBox;
    private String userIni;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting_email_basic_layout);
		MyActivity.getInstance().addActivity(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("Service" + getString(R.string.about_title));
        this.ServiceReceiver = new TerminalSetupActivity.ServiceReceiver();
        this.registerReceiver(this.ServiceReceiver, intentFilter);
        this.init();
	}

	private void init() {
        this.opString = null;

        this.user_task_set = (LinearLayout) this.findViewById(R.id.user_task_set);
        this.user_inactive_set = (LinearLayout) this.findViewById(R.id.user_inactive_set);
        this.user_task_time = (LinearLayout) this.findViewById(R.id.user_task_time);
        this.user_task_startime = (LinearLayout) this.findViewById(R.id.user_task_startime);
        this.show_task_startime = (TextView) this.findViewById(R.id.show_task_startime);
        this.show_time = (TextView) this.findViewById(R.id.show_time);
        this.show_inactive_time = (TextView) this.findViewById(R.id.show_inactive_time);
        this.show_task_time = (TextView) this.findViewById(R.id.show_task_time);

        this.No_web_TypeItem = (LinearLayout) this.findViewById(R.id.No_web_TypeItem);
        this.No_web_CheckBox = (CheckBox) this.findViewById(R.id.No_web_CheckBox);
        this.Http_TypeItem = (LinearLayout) this.findViewById(R.id.Http_TypeItem);
        this.Http_CheckBox = (CheckBox) this.findViewById(R.id.Http_CheckBox);
        this.No_TypeItem = (LinearLayout) this.findViewById(R.id.No_TypeItem);
        this.No_CheckBox = (CheckBox) this.findViewById(R.id.No_CheckBox);
        this.Local_TypeItem = (LinearLayout) this.findViewById(R.id.Local_TypeItem);
        this.Local_CheckBox = (CheckBox) this.findViewById(R.id.Local_CheckBox);
        this.Other_TypeItem = (LinearLayout) this.findViewById(R.id.Other_TypeItem);
        this.Other_CheckBox = (CheckBox) this.findViewById(R.id.Other_CheckBox);
        this.user_TypeItem = (LinearLayout) this.findViewById(R.id.User_TypeItem);
        this.user_CheckBox = (CheckBox) this.findViewById(R.id.User_CheckBox);
        this.task_TypeItem = (LinearLayout) this.findViewById(R.id.task_TypeItem);
        this.task_CheckBox = (CheckBox) this.findViewById(R.id.task_show_CheckBox);

        this.restart_web = (LinearLayout) this.findViewById(R.id.restart_web);
        this.web_server_set = (LinearLayout) this.findViewById(R.id.web_server_set);
        this.dirbrowser_web = (LinearLayout) this.findViewById(R.id.dirbrowser_web);
        this.remotevisit_web = (LinearLayout) this.findViewById(R.id.remotevisit_web);

        this.user_start_set = (LinearLayout) this.findViewById(R.id.user_start_set);
        this.user_start_CheckBox = (CheckBox) this.findViewById(R.id.user_start_CheckBox);
        this.user_show_auto_set = (LinearLayout) this.findViewById(R.id.user_show_auto_set);
        this.user_show_CheckBox = (CheckBox) this.findViewById(R.id.user_show_CheckBox);

        this.start_task = (LinearLayout) this.findViewById(R.id.start_task);
        this.menu_show_CheckBox = (CheckBox) this.findViewById(R.id.menu_show_CheckBox);

        this.user_show_set = (LinearLayout) this.findViewById(R.id.user_show_set);
        this.start_task_CheckBox = (CheckBox) this.findViewById(R.id.task_CheckBox);

        this.show_no = (LinearLayout) this.findViewById(R.id.show_no);
        this.show_other = (LinearLayout) this.findViewById(R.id.show_other);
        this.show_local = (LinearLayout) this.findViewById(R.id.show_local);
        this.show_http = (LinearLayout) this.findViewById(R.id.show_http);
        this.show_user_set = (LinearLayout) this.findViewById(R.id.show_user_set);
        this.show_task = (LinearLayout) this.findViewById(R.id.show_task);

        this.restart = (Button) this.findViewById(R.id.TbsWebRestart);
        this.start = (Button) this.findViewById(R.id.TbsWebStart);
        this.stop = (Button) this.findViewById(R.id.TbsWebDown);
        this.check = (Button) this.findViewById(R.id.TbsWebCheck);
        this.status = (TextView) this.findViewById(R.id.web_status);

        this.restart_http = (LinearLayout) this.findViewById(R.id.restart_http);
        this.http_status = (TextView) this.findViewById(R.id.http_status);
        this.http_restart = (Button) this.findViewById(R.id.TbsHttpRestart);
        this.http_start = (Button) this.findViewById(R.id.TbsHttpStart);
        this.http_stop = (Button) this.findViewById(R.id.TbsHttpDown);
        this.http_check = (Button) this.findViewById(R.id.TbsHttpCheck);

        this.no_restart = (Button) this.findViewById(R.id.no_TbsWebRestart);
        this.no_start = (Button) this.findViewById(R.id.no_TbsWebStart);
        this.no_stop = (Button) this.findViewById(R.id.no_TbsWebDown);
        this.no_check = (Button) this.findViewById(R.id.no_TbsWebCheck);
        this.no_status = (TextView) this.findViewById(R.id.no_status);
        this.no_server_set = (LinearLayout) this.findViewById(R.id.no_server_set);
        this.dirbrowser_no_CheckBox = (CheckBox) this.findViewById(R.id.dirbrowser_no_CheckBox);
        this.remotevisit_no_CheckBox = (CheckBox) this.findViewById(R.id.remotevisit_no_CheckBox);
        this.no_CheckBox = (CheckBox) this.findViewById(R.id.no_CheckBox);
        this.dirbrowser_no = (LinearLayout) this.findViewById(R.id.dirbrowser_no);
        this.remotevisit_no = (LinearLayout) this.findViewById(R.id.remotevisit_no);

        this.restart_web_CheckBox = (CheckBox) this.findViewById(R.id.restart_web_CheckBox);
        this.restart_http_CheckBox = (CheckBox) this.findViewById(R.id.restart_http_CheckBox);
        this.dirbrowser_web_CheckBox = (CheckBox) this.findViewById(R.id.dirbrowser_web_CheckBox);
        this.remotevisit_web_CheckBox = (CheckBox) this.findViewById(R.id.remotevisit_web_CheckBox);
        this.status_CheckBox = (CheckBox) this.findViewById(R.id.status_CheckBox);

        this.httpAddress = (EditText) this.findViewById(R.id.local_http_Address);
        this.httpPort = (EditText) this.findViewById(R.id.local_http_port);
        this.localAddress = (EditText) this.findViewById(R.id.local_Address);
        this.localPort = (EditText) this.findViewById(R.id.local_port);
        this.noAddress = (EditText) this.findViewById(R.id.local_no_Address);
        this.noPort = (EditText) this.findViewById(R.id.local_no_port);
        this.emailEt = (EditText) this.findViewById(R.id.txtAddress);
        this.passWordEt = (EditText) this.findViewById(R.id.txtPWD);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);

        this.user_task_set.setOnClickListener(this);
        this.user_inactive_set.setOnClickListener(this);
        this.user_task_time.setOnClickListener(this);
        this.user_task_startime.setOnClickListener(this);
        this.user_show_auto_set.setOnClickListener(this);
        this.user_show_set.setOnClickListener(this);
        this.start_task.setOnClickListener(this);

        this.No_web_TypeItem.setOnClickListener(this);
        this.No_TypeItem.setOnClickListener(this);
        this.Http_TypeItem.setOnClickListener(this);
        this.Local_TypeItem.setOnClickListener(this);
        this.Other_TypeItem.setOnClickListener(this);
        this.user_TypeItem.setOnClickListener(this);
        this.task_TypeItem.setOnClickListener(this);

        this.status_CheckBox.setOnClickListener(this);
        this.restart_web.setOnClickListener(this);
        this.restart_http.setOnClickListener(this);
        this.dirbrowser_web.setOnClickListener(this);
        this.remotevisit_web.setOnClickListener(this);
        this.user_start_set.setOnClickListener(this);
        this.restart.setOnClickListener(this);
        this.start.setOnClickListener(this);
        this.stop.setOnClickListener(this);
        this.check.setOnClickListener(this);

        this.no_CheckBox.setOnClickListener(this);
        this.dirbrowser_no.setOnClickListener(this);
        this.remotevisit_no.setOnClickListener(this);
        this.no_restart.setOnClickListener(this);
        this.no_start.setOnClickListener(this);
        this.no_stop.setOnClickListener(this);
        this.no_check.setOnClickListener(this);

        this.http_restart.setOnClickListener(this);
        this.http_start.setOnClickListener(this);
        this.http_stop.setOnClickListener(this);
        this.http_check.setOnClickListener(this);

        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
        this.title.setText(R.string.advanced_setup);
        this.initPath();
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"webRestart", "0", (byte) 0)) == 1) {
            this.restart_web_CheckBox.setChecked(true);
            this.restart_http_CheckBox.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.WebIniFile, "SERVICE",
				"dirbrowser", "0", (byte) 0)) == 1) {
            this.dirbrowser_web_CheckBox.setChecked(true);
            this.dirbrowser_no_CheckBox.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.WebIniFile, "SERVICE",
				"RemoteVisit", "0", (byte) 0)) == 1) {
            this.remotevisit_web_CheckBox.setChecked(true);
            this.remotevisit_no_CheckBox.setChecked(true);
		}
		String StartTime = this.m_iniFileIO.getIniString(this.appTimeFile, "sys",
				"StartTime", "0100", (byte) 0);
		String EndTime = this.m_iniFileIO.getIniString(this.appTimeFile, "sys",
				"EndTime", "2359", (byte) 0);

        this.show_task_startime.setText(StartTime.substring(0, 2) + ":"
				+ StartTime.substring(2) + "-" + EndTime.substring(0, 2) + ":"
				+ EndTime.substring(2));

        this.tempAddressTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"internalAddress", constants.DefaultLocalIp, (byte) 0);
        this.tempPortTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"internalPort", constants.DefaultLocalPort, (byte) 0);
		// 给EditText进行 初始化付值，以方便运行程序
        this.noAddress.setText(this.tempAddressTxt);
        this.noPort.setText(this.tempPortTxt);
        this.tempAddressTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"externalAddress", constants.DefaultServerIp, (byte) 0);
        this.tempPortTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"externalPort", constants.DefaultServerPort, (byte) 0);
		// 给EditText进行 初始化付值，以方便运行程序
        this.emailEt.setText(this.tempAddressTxt);
        this.passWordEt.setText(this.tempPortTxt);
        this.tempAddressTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"localAddress", constants.DefaultLocalIp, (byte) 0);
        this.tempPortTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"localPort", constants.DefaultLocalPort, (byte) 0);
        this.localAddress.setText(this.tempAddressTxt);
        this.localPort.setText(this.tempPortTxt);

        this.tempAddressTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"httpAddress", constants.DefaultLocalIp, (byte) 0);
        this.tempPortTxt = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"httpPort", constants.DefaultLocalPort, (byte) 0);
        this.httpAddress.setText(this.tempAddressTxt);
        this.httpPort.setText(this.tempPortTxt);

        this.show_time.setText(this.m_iniFileIO.getIniString(userIni, "Login",
				"userTime", "3", (byte) 0) + "分钟");
        this.show_inactive_time.setText(this.m_iniFileIO.getIniString(userIni,
				"Login", "inactiveTime", "5", (byte) 0) + "分钟");
        this.show_task_time.setText(Integer.parseInt(this.m_iniFileIO.getIniString(
                this.appTimeFile, "sys", "CheckInterval", "10", (byte) 0))
				/ 60
				+ "分钟");

		boolean isStarted = StartTbsweb.isMyServiceRunning(this, null);
		if (isStarted) {
            this.status.setText(R.string.on_status);
            this.http_status.setText(R.string.on_status);
            this.http_start.setVisibility(View.GONE);
            this.http_stop.setVisibility(View.VISIBLE);
            this.start.setVisibility(View.GONE);
            this.stop.setVisibility(View.VISIBLE);
            this.no_status.setText(R.string.on_status);
            this.no_start.setVisibility(View.GONE);
            this.no_stop.setVisibility(View.VISIBLE);
		} else {
            this.status.setText(R.string.off_status);
            this.http_status.setText(R.string.off_status);
            this.http_start.setVisibility(View.VISIBLE);
            this.http_stop.setVisibility(View.GONE);
            this.start.setVisibility(View.VISIBLE);
            this.stop.setVisibility(View.GONE);
            this.no_status.setText(R.string.off_status);
            this.no_start.setVisibility(View.VISIBLE);
            this.no_stop.setVisibility(View.GONE);
		}
		this.CurrentServer = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
				"SERVICE", "serverMarks", "4", (byte) 0));
//		int nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
//				"SERVICE", "intService", "0", (byte) 0));
//		if (nVal == 1 || this.CurrentServer == 0) {
//            this.No_TypeItem.setVisibility(View.VISIBLE);
//		} else {
//            this.No_TypeItem.setVisibility(View.GONE);
//		} ]
//		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
//				"SERVICE", "locService", "0", (byte) 0));
//		if (nVal == 1 || this.CurrentServer == 1) {
//            this.Local_TypeItem.setVisibility(View.VISIBLE);
//		} else {
//            this.Local_TypeItem.setVisibility(View.GONE);
//		}
//		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
//				"SERVICE", "extService", "0", (byte) 0));
//		if (nVal == 1 || this.CurrentServer == 2) {
//            this.Other_TypeItem.setVisibility(View.VISIBLE);
//		} else {
//            this.Other_TypeItem.setVisibility(View.GONE);
//		}
//		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
//				"SERVICE", "noService", "0", (byte) 0));
//		if (nVal == 1 || this.CurrentServer == 4) {
//            this.No_web_TypeItem.setVisibility(View.VISIBLE);
//		} else {
//            this.No_web_TypeItem.setVisibility(View.GONE);
//		}
//		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
//				"SERVICE", "bkService", "0", (byte) 0));
//		if (nVal == 1 || this.CurrentServer == 3) {
//            this.Http_TypeItem.setVisibility(View.VISIBLE);
//		} else {
//            this.Http_TypeItem.setVisibility(View.GONE);
//		}
		int nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
				"SERVICE", "userService", "0", (byte) 0));
		if (nVal == 1) {
            this.user_TypeItem.setVisibility(View.VISIBLE);
		} else {
            this.user_TypeItem.setVisibility(View.GONE);
		}
		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
				"SERVICE", "taskService", "0", (byte) 0));
		if (nVal == 1) {
            this.task_TypeItem.setVisibility(View.VISIBLE);
		} else {
            this.task_TypeItem.setVisibility(View.GONE);
		}
		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
				"Login", "UserUpate", "1", (byte) 0));
		if (nVal == 1) {
            this.user_start_CheckBox.setChecked(true);
		}
		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
				"Login", "autoLoginShow", "1", (byte) 0));
		if (nVal == 1) {
            this.user_show_CheckBox.setChecked(true);
		}
		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
				"Login", "menuShowUser", "1", (byte) 0));
		if (nVal == 1) {
            this.menu_show_CheckBox.setChecked(true);
		}
		nVal = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
				"SERVICE", "autoStarTask", "0", (byte) 0));
		if (nVal == 1) {
            this.start_task_CheckBox.setChecked(true);
		}
		switch (this.CurrentServer) {
		case 0:
            this.No_CheckBox.setChecked(true);
            this.show_no.setVisibility(View.VISIBLE);
			break;
		case 1:
            this.Local_CheckBox.setChecked(true);
            this.show_local.setVisibility(View.VISIBLE);
			break;
		case 2:
            this.Other_CheckBox.setChecked(true);
            this.show_other.setVisibility(View.VISIBLE);
			break;
		case 3:
            this.Http_CheckBox.setChecked(true);
            this.show_http.setVisibility(View.VISIBLE);
			break;
        case 4:
            this.No_web_CheckBox.setChecked(true);
            //show_http.setVisibility(View.VISIBLE);
            break;
		default:
			break;
		}
	}

	private void initPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
        this.webRoot = UIHelper.getSoftPath(this);
		if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
		}
        this.webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        this.webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
				"Path", this.webRoot);
		if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
		}
        this.WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appTimeFile = this.webRoot + constants.TASK_CONFIG_FILE_NAME;
        String appNewsFile = this.webRoot
				+ this.m_iniFileIO.getIniString(this.WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
        this.unregisterReceiver(this.ServiceReceiver);
		MyActivity.getInstance().finishActivity(this);
	}

	@Override
	public void onClick(View v) {
		boolean isStarted;
		switch (v.getId()) {
		case R.id.feedback_close_button:
            this.set_CheckTime.dismiss();
			break;
		case R.id.user_task_set:
            this.changSetPopState(v, 1);
			break;
		case R.id.user_inactive_set:
            this.changSetPopState(v, 2);
			break;
		case R.id.user_task_time:
            this.changSetPopState(v, 3);
			break;
		case R.id.more_btn:
//            Intent intent1 = new Intent();
//            intent1.setClass(this,AppManagerActivity.class);
//            startActivity(intent1);
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.user_task_startime:
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			NetTaskTimerDialog NetTaskTimerdialog = new NetTaskTimerDialog(
                    this);
			NetTaskTimerdialog.setCanceledOnTouchOutside(false);
			NetTaskTimerdialog.show();
			break;
		case R.id.Local_TypeItem:
			if (this.isAvilible(this, "com.tbs.tbsweb")) {
                this.Local_CheckBox.setChecked(true);
				if (this.Local_CheckBox.isChecked()) {
                    this.No_CheckBox.setChecked(false);
                    this.No_web_CheckBox.setChecked(false);
                    this.Http_CheckBox.setChecked(false);
                    this.Other_CheckBox.setChecked(false);
                    this.show_local.setVisibility(View.VISIBLE);
                    this.show_other.setVisibility(View.GONE);
                    this.show_http.setVisibility(View.GONE);
                    this.show_no.setVisibility(View.GONE);
				}
				try {
					StartTbsweb.Startapp(this, 0);
                    this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
							"serverMarks", "1");
					StartTbsweb.Startapp(this, 1);
					if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
							"SERVICE", "webRestart", "0", (byte) 0)) == 1){
//                        this.startService(new Intent(
//                                getString(R.string.WebServerName)));
                        Intent mIntent = new Intent();
                        mIntent.setAction(this
                                .getString(R.string.WebServerName));//你定义的service的action
                        mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                        this.startService(mIntent);
                    }
                    this.opString = "启动服务";
                    this.status.setText(R.string.on_status);
					Toast.makeText(this,
                            this.opString + "成功", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(this,
                            this.opString + "失败", Toast.LENGTH_SHORT).show();
				}
			} else {
                this.initPath();
                this.quitDialog();
			}
			break;
		case R.id.Http_TypeItem:
			if (this.isAvilible(this, "com.tbs.tbsweb")) {
                this.Http_CheckBox.setChecked(true);
				if (this.Http_CheckBox.isChecked()) {
                    this.No_CheckBox.setChecked(false);
                    this.No_web_CheckBox.setChecked(false);
                    this.Other_CheckBox.setChecked(false);
                    this.Local_CheckBox.setChecked(false);
                    this.show_local.setVisibility(View.GONE);
                    this.show_other.setVisibility(View.GONE);
                    this.show_http.setVisibility(View.VISIBLE);
                    this.show_no.setVisibility(View.GONE);
				}
				try {
					StartTbsweb.Startapp(this, 0);
                    this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
							"serverMarks", "3");
					StartTbsweb.Startapp(this, 1);
					if (Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
							"SERVICE", "webRestart", "0", (byte) 0)) == 1)
//                        this.startService(new Intent(
//                                getString(R.string.WebServerName)));
                    {
                        Intent mIntent = new Intent();
                        mIntent.setAction(this
                                .getString(R.string.WebServerName));//你定义的service的action
                        mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                        this.startService(mIntent);
                    }
                    this.opString = "启动服务";
                    this.http_status.setText(R.string.on_status);
					Toast.makeText(this,
                            this.opString + "成功", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(this,
                            this.opString + "失败", Toast.LENGTH_SHORT).show();
				}
			} else {
                this.initPath();
                this.quitDialog();
			}
			break;
		case R.id.No_TypeItem:
            this.No_CheckBox.setChecked(true);
			if (this.No_CheckBox.isChecked()) {
                this.Http_CheckBox.setChecked(false);
                this.No_web_CheckBox.setChecked(false);
                this.Local_CheckBox.setChecked(false);
                this.Other_CheckBox.setChecked(false);
				// editSharePreFernces(0);
				StartTbsweb.Startapp(this, 0);
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.WebServerName));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
                //this.stopService(new Intent(getString(R.string.WebServerName)));
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"serverMarks", "0");
				StartTbsweb.Startapp(this, 1);
                this.show_local.setVisibility(View.GONE);
                this.show_other.setVisibility(View.GONE);
                this.show_http.setVisibility(View.GONE);
                this.show_no.setVisibility(View.VISIBLE);
			}
			break;
        case R.id.No_web_TypeItem:
            this.No_web_CheckBox.setChecked(true);
            if (this.No_web_CheckBox.isChecked()) {
                this.Other_CheckBox.setChecked(false);
                this.No_CheckBox.setChecked(false);
                this.Http_CheckBox.setChecked(false);
                this.Local_CheckBox.setChecked(false);
                StartTbsweb.Startapp(this, 0);
                this.show_local.setVisibility(View.GONE);
                this.show_no.setVisibility(View.GONE);
                this.show_http.setVisibility(View.GONE);
                this.show_other.setVisibility(View.GONE);
            }
            break;
		case R.id.Other_TypeItem:
            this.Other_CheckBox.setChecked(true);
			if (this.Other_CheckBox.isChecked()) {
                this.No_CheckBox.setChecked(false);
                this.No_web_CheckBox.setChecked(false);
                this.Http_CheckBox.setChecked(false);
                this.Local_CheckBox.setChecked(false);
				StartTbsweb.Startapp(this, 0);
                this.show_local.setVisibility(View.GONE);
                this.show_no.setVisibility(View.GONE);
                this.show_http.setVisibility(View.GONE);
                this.show_other.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.TbsWebCheck:
			isStarted = StartTbsweb.isMyServiceRunning(
                    this, getString(R.string.TbsWebServer));
			if (isStarted) {
                this.status.setText(R.string.on_status);
                this.start.setVisibility(View.GONE);
                this.stop.setVisibility(View.VISIBLE);
                this.opString = "当前服务为启动状态";
			} else {
                this.status.setText(R.string.off_status);
                this.start.setVisibility(View.VISIBLE);
                this.stop.setVisibility(View.GONE);
                this.opString = "当前服务为停止状态";
			}
			Toast.makeText(this, this.opString + "",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.restart_web:
			if (this.restart_web_CheckBox.isChecked()) {
                this.restart_web_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"webRestart", "0");
//                this.stopService(new Intent(
//                        getString(R.string.WebServerName)));
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.WebServerName));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
			} else {
                this.restart_web_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"webRestart", "1");
//                this.startService(new Intent(
//                        getString(R.string.WebServerName)));
                    Intent mIntent = new Intent();
                    mIntent.setAction(this
                            .getString(R.string.WebServerName));//你定义的service的action
                    mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                    this.startService(mIntent);
			}
			break;
		case R.id.restart_http:
			if (this.restart_http_CheckBox.isChecked()) {
                this.restart_http_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"webRestart", "0");
//                this.stopService(new Intent(
//                        getString(R.string.WebServerName)));
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.WebServerName));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
			} else {
                this.restart_http_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"webRestart", "1");
//                this.startService(new Intent(
//                        getString(R.string.WebServerName)));
                Intent mIntent = new Intent();
                mIntent.setAction(this
                        .getString(R.string.WebServerName));//你定义的service的action
                mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.startService(mIntent);

			}
			break;
		case R.id.status_CheckBox:
			if (this.status_CheckBox.isChecked()) {
                this.web_server_set.setVisibility(View.VISIBLE);
			} else {
                this.web_server_set.setVisibility(View.GONE);
			}
			break;
		case R.id.dirbrowser_web:
			if (this.dirbrowser_web_CheckBox.isChecked()) {
                this.dirbrowser_web_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE", "dirbrowser",
						"0");
			} else {
                this.dirbrowser_web_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE", "dirbrowser",
						"1");
			}
			break;
		case R.id.user_show_auto_set:
			if (this.user_show_CheckBox.isChecked()) {
                this.user_show_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(userIni, "Login",
						"autoLoginShow", "0");
			} else {
                this.user_show_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(userIni, "Login",
						"autoLoginShow", "1");
			}
			break;
		case R.id.user_show_set:
			if (this.menu_show_CheckBox.isChecked()) {
                this.menu_show_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(userIni, "Login",
						"menuShowUser", "0");
			} else {
                this.menu_show_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(userIni, "Login",
						"menuShowUser", "1");
			}
			break;
		case R.id.start_task:
			if (this.start_task_CheckBox.isChecked()) {
                this.start_task_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"autoStarTask", "0");
			} else {
                this.start_task_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
						"autoStarTask", "1");
			}
			break;
		case R.id.user_start_set:
			if (this.user_start_CheckBox.isChecked()) {
                this.user_start_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(userIni, "Login", "UserUpate",
						"0");
//                this.stopService(new Intent(
//                        getString(R.string.ServerName1)));
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.ServerName1));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
			} else {
                this.user_start_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(userIni, "Login", "UserUpate",
						"1");
//                this.startService(new Intent(
//                        getString(R.string.ServerName1)));
                Intent mIntent = new Intent();
                mIntent.setAction(this
                        .getString(R.string.ServerName1));//你定义的service的action
                mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.startService(mIntent);
			}
			break;
		case R.id.remotevisit_web:
			if (this.remotevisit_web_CheckBox.isChecked()) {
                this.remotevisit_web_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE",
						"RemoteVisit", "0");
			} else {
                this.remotevisit_web_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE",
						"RemoteVisit", "1");
			}
			break;

		case R.id.TbsWebRestart:
			try {
				StartTbsweb.Startapp(this, 1);
                this.opString = "重新启动服务";
                this.status.setText(R.string.on_status);
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.TbsWebStart:
			try {
				StartTbsweb.Startapp(this, 1);
                this.opString = "启动服务";
                this.status.setText(R.string.on_status);
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
                this.start.setVisibility(View.GONE);
                this.stop.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.TbsWebDown:
			try {
				StartTbsweb.Startapp(this, 0);
                //this.stopService(new Intent(getString(R.string.WebServerName)));
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.WebServerName));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
                this.opString = "停止服务";
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
                this.status.setText(R.string.off_status);
                this.start.setVisibility(View.VISIBLE);
                this.stop.setVisibility(View.GONE);
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.no_TbsWebCheck:
			isStarted = StartTbsweb.isMyServiceRunning(
                    this, getString(R.string.TbsWebServer));
			if (isStarted) {
                this.no_status.setText(R.string.on_status);
                this.no_start.setVisibility(View.GONE);
                this.no_stop.setVisibility(View.VISIBLE);
                this.opString = "当前服务为启动状态";
			} else {
                this.no_status.setText(R.string.off_status);
                this.no_start.setVisibility(View.VISIBLE);
                this.no_stop.setVisibility(View.GONE);
                this.opString = "当前服务为停止状态";
			}
			Toast.makeText(this, this.opString + "",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.no_CheckBox:
			if (this.no_CheckBox.isChecked()) {
                this.no_server_set.setVisibility(View.VISIBLE);
			} else {
                this.no_server_set.setVisibility(View.GONE);
			}
			break;
		case R.id.dirbrowser_no:
			if (this.dirbrowser_no_CheckBox.isChecked()) {
                this.dirbrowser_no_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE", "dirbrowser",
						"0");
			} else {
                this.dirbrowser_no_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE", "dirbrowser",
						"1");
			}
			break;
		case R.id.remotevisit_no:
			if (this.remotevisit_no_CheckBox.isChecked()) {
                this.remotevisit_no_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE",
						"RemoteVisit", "0");
			} else {
                this.remotevisit_no_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.WebIniFile, "SERVICE",
						"RemoteVisit", "1");
			}
			break;

		case R.id.no_TbsWebRestart:
			try {
				StartTbsweb.Startapp(this, 1);
                this.opString = "重新启动服务";
                this.no_status.setText(R.string.on_status);
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
                this.no_status.setText(R.string.off_status);
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.no_TbsWebStart:
			try {
				StartTbsweb.Startapp(this, 1);
                this.opString = "启动服务";
                this.no_status.setText(R.string.on_status);
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
                this.no_start.setVisibility(View.GONE);
                this.no_stop.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.no_TbsWebDown:
			try {
				StartTbsweb.Startapp(this, 0);
               // this.stopService(new Intent(getString(R.string.WebServerName)));
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.WebServerName));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
                this.opString = "停止服务";
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
                this.no_status.setText(R.string.off_status);
                this.no_start.setVisibility(View.VISIBLE);
                this.no_stop.setVisibility(View.GONE);
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.TbsHttpRestart:
			try {
				StartTbsweb.Startapp(this, 1);
                this.opString = "重新启动服务";
                this.http_status.setText(R.string.on_status);
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
                this.http_status.setText(R.string.off_status);
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.TbsHttpStart:
			try {
				StartTbsweb.Startapp(this, 1);
                this.opString = "启动服务";
                this.http_status.setText(R.string.on_status);
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
                this.http_start.setVisibility(View.GONE);
                this.http_stop.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.TbsHttpDown:
			try {
				StartTbsweb.Startapp(this, 0);
               // this.stopService(new Intent(getString(R.string.WebServerName)));
                Intent intent = new Intent();
                intent.setAction(this.getString(R.string.WebServerName));//你定义的service的action
                intent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.stopService(intent);
                this.opString = "停止服务";
				Toast.makeText(this, this.opString + "成功",
						Toast.LENGTH_SHORT).show();
                this.http_status.setText(R.string.off_status);
                this.http_start.setVisibility(View.VISIBLE);
                this.http_stop.setVisibility(View.GONE);
			} catch (Exception e) {
				Toast.makeText(this, this.opString + "失败",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.TbsHttpCheck:
			isStarted = StartTbsweb.isMyServiceRunning(
                    this, null);
			if (isStarted) {
                this.http_status.setText(R.string.on_status);
                this.http_start.setVisibility(View.GONE);
                this.http_stop.setVisibility(View.VISIBLE);
                this.opString = "当前服务为启动状态";
			} else {
                this.http_status.setText(R.string.off_status);
                this.http_start.setVisibility(View.VISIBLE);
                this.http_stop.setVisibility(View.GONE);
                this.opString = "当前服务为停止状态";
			}
			Toast.makeText(this, this.opString + "",
					Toast.LENGTH_SHORT).show();

			break;
		case R.id.User_TypeItem:
			if (this.user_CheckBox.isChecked()) {
                this.user_CheckBox.setChecked(false);
                this.show_user_set.setVisibility(View.GONE);
			} else {
                this.user_CheckBox.setChecked(true);
                this.show_user_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.task_TypeItem:
			if (this.task_CheckBox.isChecked()) {
                this.task_CheckBox.setChecked(false);
                this.show_task.setVisibility(View.GONE);
			} else {
                this.task_CheckBox.setChecked(true);
                this.show_task.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.finish_btn:
			if (this.Local_CheckBox.isChecked()) {
                this.saveLocalSetting();
			} else if (this.Other_CheckBox.isChecked()) {
                this.saveEmailSetting();
			} else if (this.Http_CheckBox.isChecked()) {
                this.saveHttpSetting();
			} else if(this.No_web_CheckBox.isChecked()){
                this.saveNowebSetting();
//                Intent intent = new Intent();
//                intent.setClass(this,AppManagerActivity.class);
//                startActivity(intent);
                this.finish();
            }else {
                this.saveInternalSetting();
                //this.finish();
			}

            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		}
	}

    private void saveNowebSetting() {
        this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "serverMarks", 4
                + "");
        // 提交编辑器内容
        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                "baseUrl", "File://" + this.webRoot + "Web");
    }

    public void changSetPopState(View v, int action) {
        this.isOpenPop = !this.isOpenPop;
		if (this.isOpenPop) {
            this.checkTime_Set(v, action);
		} else {
			if (this.set_CheckTime != null) {
                this.set_CheckTime.dismiss();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void checkTime_Set(View parent, int action) {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = lay.inflate(R.layout.set_check_time, null);
		// toolcheck = (CheckBox) view.findViewById(R.R.id.menu_check_box);
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
			checkTime = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
					"Login", "userTime", "3", (byte) 0));
			seekBar2.setProgress(checkTime);
			textView5.setText("检查状态时间间隔为：" + checkTime + "分钟");
			seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				int middleTime;

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					textView5.setText("检查状态时间间隔为：" + progress + "分钟");
                    this.middleTime = progress;
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
                    TerminalSetupActivity.this.m_iniFileIO.writeIniString(userIni, "Login",
							"userTime", this.middleTime + "");
					textView5.setText("检查状态时间间隔为：" + this.middleTime + "分钟");
                    TerminalSetupActivity.this.show_time.setText(this.middleTime + "分钟");
				}

			});
			break;
		case 2:
			seekBar2.setMax(30);
			checkTime = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
					"Login", "inactiveTime", "5", (byte) 0));
			seekBar2.setProgress(checkTime);
			textView5.setText("不活动超时时间为：" + checkTime + "分钟");
			seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				int middleTime;

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					textView5.setText("不活动超时时间为：" + progress + "分钟");
                    this.middleTime = progress;
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
                    TerminalSetupActivity.this.m_iniFileIO.writeIniString(userIni, "Login",
							"inactiveTime", this.middleTime + "");
					textView5.setText("不活动超时时间为：" + this.middleTime + "分钟");
                    TerminalSetupActivity.this.show_inactive_time.setText(this.middleTime + "分钟");
				}

			});
			break;
		case 3:
			checkTime = Integer.parseInt(this.m_iniFileIO.getIniString(this.appTimeFile,
					"sys", "CheckInterval", "60", (byte) 0)) / 60;
			seekBar2.setProgress(checkTime);
			textView5.setText("任务调度时间间隔为：" + checkTime + "分钟");
			seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				int middleTime;

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (progress == 0) {
						textView5.setText("任务调度时间间隔为：" + (progress + 1) + "分钟");
                        this.middleTime = progress + 1;
					} else {
						textView5.setText("任务调度时间间隔为：" + progress + "分钟");
                        this.middleTime = progress;
					}
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
                    TerminalSetupActivity.this.m_iniFileIO.writeIniString(TerminalSetupActivity.this.appTimeFile, "sys",
							"CheckInterval", this.middleTime * 60 + "");
					textView5.setText("任务调度时间间隔为：" + this.middleTime + "分钟");
                    TerminalSetupActivity.this.show_task_time.setText(this.middleTime + "分钟");
				}

			});
			break;
		}
        this.set_CheckTime = new PopupWindow(view,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.set_CheckTime.setFocusable(false);
        this.set_CheckTime.setOutsideTouchable(true);
        this.set_CheckTime.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
                TerminalSetupActivity.this.isOpenPop = false;
			}
		});
		// set_CheckTime.showAsDropDown(parent, -95, 3);
        this.set_CheckTime.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER,
				0, 0);
        this.set_CheckTime.update();
	}

	public void editSharePreFernces(int flag) {
		// 将EditText文本内容添加到编辑器
		String ip = null;
		String port = null;
		if (flag == 0) {
			ip = this.localIpTxt;
			port = this.localPortTxt;
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
					"internalAddress", ip);
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "internalPort",
					port);
		} else if (flag == 1) {
			ip = this.localIpTxt;
			port = this.localPortTxt;
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "localAddress",
					ip);
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "localPort",
					port);
		} else if (flag == 2) {
			ip = this.localIpTxt;
			port = this.localPortTxt;
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE",
					"externalAddress", ip);
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "externalPort",
					port);
		} else if (flag == 3) {
			ip = this.localIpTxt;
			port = this.localPortTxt;
            this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "httpAddress",
					ip);
            this.m_iniFileIO
					.writeIniString(this.userIni, "SERVICE", "httpPort", port);
		}
        this.m_iniFileIO
				.writeIniString(this.userIni, "SERVICE", "currentAddress", ip);
        this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "currentPort", port);
        this.m_iniFileIO.writeIniString(this.userIni, "SERVICE", "serverMarks", flag
                + "");
		// 提交编辑器内容
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
                "baseUrl", "http://" + ip + ":" + port);
	}

	public void saveEmailSetting() {
        this.localIpTxt = String.valueOf(this.emailEt.getText());
        this.localPortTxt = String.valueOf(this.passWordEt.getText());
		if (null != this.localIpTxt && !this.localIpTxt.equals("")
				&& !this.localIpTxt.equals("\\s{1,}") && null != this.localPortTxt
				&& !this.localPortTxt.equals("")
				&& !this.localPortTxt.equals("\\s{1,}")
				&& StringUtils.isIp(this.localIpTxt)) {
            this.editSharePreFernces(2);
            Intent intent = new Intent();
            intent.setClass(this,AppManagerActivity.class);
            startActivity(intent);
            this.finish();
		} else {
            this.onBackPressed();
			Toast.makeText(this, "外部服务配置有误!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void saveInternalSetting() {
        this.localIpTxt = String.valueOf(this.noAddress.getText());
        this.localPortTxt = String.valueOf(this.noPort.getText());
		if (null != this.localIpTxt && !this.localIpTxt.equals("")
				&& !this.localIpTxt.equals("\\s{1,}") && null != this.localPortTxt
				&& !this.localPortTxt.equals("")
				&& !this.localPortTxt.equals("\\s{1,}")
				&& StringUtils.isIp(this.localIpTxt)) {
            this.editSharePreFernces(0);
            Intent intent = new Intent();
            intent.setClass(this,AppManagerActivity.class);
            startActivity(intent);
            finish();
		} else {
            this.onBackPressed();
			Toast.makeText(this, "内置服务配置有误!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void saveLocalSetting() {
        this.localIpTxt = String.valueOf(this.localAddress.getText());
        this.localPortTxt = String.valueOf(this.localPort.getText());
		if (null != this.localIpTxt && !this.localIpTxt.equals("")
				&& !this.localIpTxt.equals("\\s{1,}") && null != this.localPortTxt
				&& !this.localPortTxt.equals("")
				&& !this.localPortTxt.equals("\\s{1,}")
				&& StringUtils.isIp(this.localIpTxt)) {
            this.editSharePreFernces(1);
            Intent intent = new Intent();
            intent.setClass(this,AppManagerActivity.class);
            startActivity(intent);
            this.finish();
		} else {
            this.onBackPressed();
			Toast.makeText(this, "本机服务配置有误!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void saveHttpSetting() {
        this.localIpTxt = String.valueOf(this.httpAddress.getText());
        this.localPortTxt = String.valueOf(this.httpPort.getText());
		if (null != this.localIpTxt && !this.localIpTxt.equals("")
				&& !this.localIpTxt.equals("\\s{1,}") && null != this.localPortTxt
				&& !this.localPortTxt.equals("")
				&& !this.localPortTxt.equals("\\s{1,}")
				&& StringUtils.isIp(this.localIpTxt)) {
            this.editSharePreFernces(3);
            Intent intent = new Intent();
            intent.setClass(this,AppManagerActivity.class);
            startActivity(intent);
            this.finish();
		} else {
            this.onBackPressed();
			Toast.makeText(this, "备用服务配置有误!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private class ServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("Service"
					+ getString(R.string.about_title))) {
				int SelBtn = intent.getIntExtra("flag", 1);
				switch (SelBtn) {
				case 1:
                    TerminalSetupActivity.this.show_task_startime.setText(intent.getStringExtra("timer"));
					break;
				}
			}
		}
	}

	private boolean isAvilible(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
	}

	protected void installApk(String file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(new File(file)),
				"application/vnd.android.package-archive");// 编者按：此处Android应为android，否则造成安装不了
        this.startActivity(intent);
	}

	private void quitDialog() {
		new Builder(this)
				.setTitle(R.string.alerm_title)
				.setIcon(null)
				.setCancelable(false)
				.setMessage(R.string.alert_tbsweb)
				.setPositiveButton(R.string.alert_yes_button,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String webApk = UIHelper.getSoftPath(TerminalSetupActivity.this);
								if (webApk.endsWith("/") == false) {
									webApk += "/";
								}
								webApk += getString(R.string.SD_CARD_TBSAPP_PATH2);
                                TerminalSetupActivity.this.installApk(webApk + "/TBSWeb.apk");
							}
						}).create().show();
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