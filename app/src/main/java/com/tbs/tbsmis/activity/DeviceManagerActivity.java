package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.DeviceAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.ScanNet;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;

/**
 * Activity for displaying the notification setting view.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@SuppressLint("SetJavaScriptEnabled")
public class DeviceManagerActivity extends Activity implements View.OnClickListener {

	private ImageView finishBtn;
	private ImageView downBtn;
	private TextView title;
	private String webRoot;
	private CheckBox device_set_checkbox;
	private LinearLayout device_set;
	private LinearLayout device_set_url;
	private RelativeLayout device_set_gps;
	private CheckBox gps_CheckBox;
	private IniFile m_iniFileIO;
	private String appNewsFile;
	private TextView device_md5;
	private ImageView addBtn;
	private ImageView searchBtn;
	// 通知对话框
	private Dialog noticeDialog;
	private ScanNet ScanNet;
	// 服务器列表
	private ListView Devicelv;
	private ArrayList<String> DeviceList;
	private DeviceAdapter DevicemAdapter;
	private int DeviceNumber;
	// 设备信息广播
	private DeviceManagerActivity.DeviceReceiver DeviceReceiver;
	private EditText username;
	private EditText password;
	private EditText network_domain;
	private EditText location;
	private EditText label_username;
	private EditText label_password;
	private EditText display;
	private ProgressDialog mProDialog;
	private Handler handler;
	private TextView device_wifi;
	private TextView device_ip;
	private LinearLayout ftp_set;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting_device_manager);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("Device" + getString(R.string.about_title));
        this.DeviceReceiver = new DeviceManagerActivity.DeviceReceiver();
        this.registerReceiver(this.DeviceReceiver, intentFilter);
        this.Devicelv = (ListView) this.findViewById(R.id.DevicelistTopicItems);
        this.device_set_checkbox = (CheckBox) this.findViewById(R.id.device_set_checkbox);
        this.gps_CheckBox = (CheckBox) this.findViewById(R.id.gps_CheckBox);
        this.device_set_gps = (RelativeLayout) this.findViewById(R.id.device_set_gps);
        this.device_set = (LinearLayout) this.findViewById(R.id.device_set);
        this.ftp_set = (LinearLayout) this.findViewById(R.id.ftp_set);
        this.device_set_url = (LinearLayout) this.findViewById(R.id.device_set_url);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.addBtn = (ImageView) this.findViewById(R.id.add_button);
        this.searchBtn = (ImageView) this.findViewById(R.id.search_button);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.device_md5 = (TextView) this.findViewById(R.id.device_md5);
        this.device_wifi = (TextView) this.findViewById(R.id.device_wifi);
        this.device_ip = (TextView) this.findViewById(R.id.device_ip);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("设备管理");
        this.ftp_set.setOnClickListener(this);
        this.ftp_set.setVisibility(View.GONE);
        this.addBtn.setOnClickListener(this);
        this.searchBtn.setOnClickListener(this);
        this.device_set.setOnClickListener(this);
        this.device_set_gps.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setOnClickListener(this);
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
        this.ScanNet = new ScanNet(this);
		TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			WifiInfo info = wm.getConnectionInfo();
			String ipText = this.ScanNet.intToIp(info.getIpAddress());
            this.device_ip.setText(ipText);
            this.device_wifi.setText(info.getSSID());
		} else {
            this.device_ip.setText("未连接");
            this.device_wifi.setText("未连接");
		}
		String WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appNewsFile = this.webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "Device"
				+ getString(R.string.about_title), "device_gps", "0",
				(byte) 0)) == 1) {
            this.gps_CheckBox.setChecked(true);
		} else {
            this.gps_CheckBox.setChecked(false);
		}
		String devicemd5 = this.m_iniFileIO.getIniString(this.appNewsFile, "DEVICE",
				"device_md5", "0", (byte) 0);
		if (devicemd5.equals("0")) {
			devicemd5 = UIHelper.DeviceMD5ID(this);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "DEVICE", "device_md5",
					devicemd5);
            this.device_md5.setText(devicemd5);
		} else {
            this.device_md5.setText(devicemd5);
		}
        this.Devicelv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				DeviceAdapter.ViewHolder holder = (DeviceAdapter.ViewHolder) arg1.getTag();
				String apptext = (String) holder.tv.getText();
				apptext = DeviceManagerActivity.this.CheckUser(apptext);
				if (apptext == null) {
                    DeviceManagerActivity.this.showSendDialog((String) holder.tv.getText());
				} else {
					Intent intent = new Intent();
					intent.setClass(DeviceManagerActivity.this,
							SmbFileListActivity.class);
					intent.putExtra("tempUrl", apptext);
                    DeviceManagerActivity.this.startActivity(intent);
				}
			}
		});
        this.DeviceList = new ArrayList<String>();
        this.initDeviceDate();
        this.DevicemAdapter = new DeviceAdapter(this.DeviceList,
                this);
        this.Devicelv.setAdapter(this.DevicemAdapter);
        this.setListViewHeightBasedOnChildren(this.Devicelv);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
        this.unregisterReceiver(this.DeviceReceiver);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.more_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.finish_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.search_button:
            ScanNet san = new ScanNet(this);
			san.scan();
			break;
		case R.id.add_button:
			if (this.ScanNet.getLocAddress().isEmpty()) {
				Toast.makeText(this, "请检查wifi网络", Toast.LENGTH_LONG).show();
			} else {
                this.showDownloadDialog();
			}
			break;
		case R.id.device_set:
			if (this.device_set_checkbox.isChecked()) {
                this.device_set_checkbox.setChecked(false);
                this.device_set_url.setVisibility(View.GONE);
			} else {
                this.device_set_checkbox.setChecked(true);
                this.device_set_url.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.ftp_set:
			Intent intent = new Intent();
			// intent.setClass(this, FileManagerActivity.class);
			intent.setClass(this, FileExplorerTabActivity.class);
			intent.putExtra("flag", 1);
            this.startActivity(intent);
			break;
		case R.id.device_set_gps:
			if (this.gps_CheckBox.isChecked()) {
                this.gps_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile,
						"Device" + getString(R.string.about_title),
						"device_gps", "0");
			} else {
                this.gps_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile,
						"Device" + getString(R.string.about_title),
						"device_gps", "1");
			}
			break;
		}
	}

	/*
	 * 初始化设备信息
	 */
	private void initDeviceDate() {
        this.DeviceNumber = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"Device" + getString(R.string.about_title),
				"DeviceNumber", "0", (byte) 0));
		if (this.DeviceNumber > 0) {
			for (int i = 0; i < this.DeviceNumber; i++) {
				String middletime1 = this.m_iniFileIO.getIniString(this.appNewsFile,
						"Device" + getString(R.string.about_title),
						"DeviceName" + i, "0", (byte) 0);
                this.DeviceList.add(middletime1);
			}
		}
	}

	/*
	 * 检测是否已记录用户名
	 */
	private String CheckUser(String deviceUser) {
        this.DeviceNumber = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"Device" + getString(R.string.about_title),
				"DeviceNumber", "0", (byte) 0));
		if (this.DeviceNumber > 0) {
			for (int i = 0; i < this.DeviceNumber; i++) {
				String middletime1 = this.m_iniFileIO.getIniString(this.appNewsFile,
						"Device" + getString(R.string.about_title),
						"DeviceName" + i, "0", (byte) 0);
				if (deviceUser.equals(middletime1)) {
					String middleuser = this.m_iniFileIO.getIniString(this.appNewsFile,
							"Device" + getString(R.string.about_title),
							"DeviceUser" + i, "0", (byte) 0);
					String middlepwd = this.m_iniFileIO.getIniString(this.appNewsFile,
							"Device" + getString(R.string.about_title),
							"DevicePwd" + i, "0", (byte) 0);
					deviceUser = "smb://" + middleuser + ":" + middlepwd + "@"
							+ deviceUser + "/";
					return deviceUser;
				}
			}
		}
		return null;
	}

	/*
	 * 计算listview的高度
	 */

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int Height = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			Height = listItem.getMeasuredHeight();
			totalHeight += listItem.getMeasuredHeight();
		}
		LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + Height
				+ listView.getDividerHeight() * listAdapter.getCount();
		listView.setLayoutParams(params);
	}

	/**
	 * 显示进度条对话框
	 */
	private void showScanDialog() {
		if (this.mProDialog == null) {
			// 创建ProgressDialog对象
            this.mProDialog = new ProgressDialog(this);
			// 设置进度条风格，风格为圆形，旋转的
            this.mProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 标题
            this.mProDialog.setTitle("增加服务器");
			// 设置ProgressDialog 提示信息
            this.mProDialog.setMessage("正在验证，请稍候...");
			// 设置ProgressDialog 的进度条是否不明确
            this.mProDialog.setIndeterminate(false);
			// 设置ProgressDialog 是否可以按退回按键取消
            this.mProDialog.setCancelable(true);
			// 让ProgressDialog显示
            this.mProDialog.show();
		}
	}

	/*
	 * 连接服务器
	 */
	// private void connectDevice(String apptext){
	// remotUrl = "smb://tbs:tbs@" + apptext + "/";
	// new Thread(new Runnable() {
	// @SuppressWarnings("static-access")
	// public void run() {
	// FileIO.getInstance().smbTraversal(remotUrl);
	// }
	// });
	// }
	/*
	 * 显示下载对话框
	 */
	@SuppressLint("ShowToast")
	private void showDownloadDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("服务器");
		builder.setCancelable(false);
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.new_smb_server, null);
        this.network_domain = (EditText) v.findViewById(R.id.network_domain);
        this.location = (EditText) v.findViewById(R.id.location);
        this.label_username = (EditText) v.findViewById(R.id.label_username);
        this.label_password = (EditText) v.findViewById(R.id.label_password);
        this.display = (EditText) v.findViewById(R.id.display);
		builder.setView(v);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String Adress = DeviceManagerActivity.this.location.getText().toString();
				final String contant = DeviceManagerActivity.this.label_username.getText().toString();
				final String contact = DeviceManagerActivity.this.label_password.getText().toString();
				if (Adress.isEmpty()) {
					Toast.makeText(DeviceManagerActivity.this, "服务器不可为空",
							Toast.LENGTH_SHORT);
					return;
				}
				if (contant.isEmpty()) {
					Toast.makeText(DeviceManagerActivity.this, "用户名不可为空",
							Toast.LENGTH_SHORT);
					return;
				}
                DeviceManagerActivity.this.showScanDialog();
				new Thread() {
					@Override
					public void run() {
						String curUrl = "smb://" + contant + ":" + contact
								+ "@" + Adress + "/";
						if (FileIO.checkUser(curUrl)) {
                            DeviceManagerActivity.this.DeviceNumber = Integer.parseInt(DeviceManagerActivity.this.m_iniFileIO.getIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceNumber", "0", (byte) 0));
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceName" + DeviceManagerActivity.this.DeviceNumber, Adress);
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceUser" + DeviceManagerActivity.this.DeviceNumber, contant);
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DevicePwd" + DeviceManagerActivity.this.DeviceNumber, contact);
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceNumber", String
											.valueOf(DeviceManagerActivity.this.DeviceNumber + 1));
                            DeviceManagerActivity.this.mProDialog.dismiss();
							if (!DeviceManagerActivity.this.DeviceList.contains(Adress)) {
                                DeviceManagerActivity.this.DeviceList.add(Adress);
                                DeviceManagerActivity.this.DevicemAdapter.notifyDataSetChanged();
                                DeviceManagerActivity.this.setListViewHeightBasedOnChildren(DeviceManagerActivity.this.Devicelv);
							}
						} else {
                            DeviceManagerActivity.this.mProDialog.dismiss();
							Intent intent = new Intent();
							intent.setAction("Device"
									+ getString(R.string.about_title));
							intent.putExtra("flag", 4);
                            DeviceManagerActivity.this.sendBroadcast(intent);
						}
					}
				}.start();
			}
		});
        this.noticeDialog = builder.create();
        this.noticeDialog.show();
	}

	/**
	 * 输入用户名密码对话框
	 */
	private void showSendDialog(final String DeviceName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("正在连接到 smb://" + DeviceName + "/");
		builder.setCancelable(false);
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.new_username_pasword, null);
        this.username = (EditText) v.findViewById(R.id.username);
        this.password = (EditText) v.findViewById(R.id.password);
		builder.setView(v);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			@SuppressLint("ShowToast")
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				final String contant = DeviceManagerActivity.this.username.getText().toString();
				final String contact = DeviceManagerActivity.this.password.getText().toString();
				if (contant.isEmpty()) {
					Toast.makeText(DeviceManagerActivity.this, "用户名不可为空",
							Toast.LENGTH_SHORT);
					return;
				}
				new Thread() {
					@Override
					public void run() {
						String curUrl = "smb://" + contant + ":" + contact
								+ "@" + DeviceName + "/";
						if (FileIO.checkUser(curUrl)) {
                            DeviceManagerActivity.this.DeviceNumber = Integer.parseInt(DeviceManagerActivity.this.m_iniFileIO.getIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceNumber", "0", (byte) 0));
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceName" + DeviceManagerActivity.this.DeviceNumber, DeviceName);
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceUser" + DeviceManagerActivity.this.DeviceNumber, contant);
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DevicePwd" + DeviceManagerActivity.this.DeviceNumber, contact);
                            DeviceManagerActivity.this.m_iniFileIO.writeIniString(
                                    DeviceManagerActivity.this.appNewsFile,
									"Device"
											+ getString(R.string.about_title),
									"DeviceNumber", String
											.valueOf(DeviceManagerActivity.this.DeviceNumber + 1));
							Intent intent = new Intent();
							intent.setClass(DeviceManagerActivity.this,
									SmbFileListActivity.class);
							intent.putExtra("tempUrl", curUrl);
                            DeviceManagerActivity.this.startActivity(intent);
						} else {
							Intent intent = new Intent();
							intent.setAction("Device"
									+ getString(R.string.about_title));
							intent.putExtra("flag", 3);
							intent.putExtra("DeviceName", DeviceName);
                            DeviceManagerActivity.this.sendBroadcast(intent);
						}
					}
				}.start();
			}
		});
        this.noticeDialog = builder.create();
        this.noticeDialog.show();
	}

	/*
	 * 接受设备信息广播
	 */
	private class DeviceReceiver extends BroadcastReceiver {
		@SuppressLint("ShowToast")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String DeviceName;
			if (action.equals("Device"
					+ getString(R.string.about_title))) {
				int SelBtn = intent.getIntExtra("flag", 1);
				switch (SelBtn) {
				case 1:
					DeviceName = intent.getStringExtra("DeviceName");
					if (DeviceName != null) {
						if (!DeviceManagerActivity.this.DeviceList.contains(DeviceName)) {
                            DeviceManagerActivity.this.DeviceList.add(DeviceName);
                            DeviceManagerActivity.this.DevicemAdapter.notifyDataSetChanged();
                            DeviceManagerActivity.this.setListViewHeightBasedOnChildren(DeviceManagerActivity.this.Devicelv);
						}
					}
					break;
				case 2:
                    DeviceManagerActivity.this.setListViewHeightBasedOnChildren(DeviceManagerActivity.this.Devicelv);
					break;
				case 3:
					DeviceName = intent.getStringExtra("DeviceName");
					if (DeviceName != null) {
                        DeviceManagerActivity.this.showSendDialog(DeviceName);
					}
					break;
				}
			}
		}
	}
}
