package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.cbs.CBSInterpret;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.check.ManagerMyAdapter;
import com.tbs.tbsmis.check.OfflineTimerDialog;
import com.tbs.tbsmis.check.TimerMyAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressLint({ "HandlerLeak", "ShowToast" })
public class OfflineActivity extends Activity implements View.OnClickListener {

	Intent intent;
	private PopupWindow set_CheckTime;
	private ListView lv, Timerlv;
	private ManagerMyAdapter mAdapter;
	private TimerMyAdapter TimermAdapter;
	private ArrayList<String> timerList;
	private ArrayList<HashMap<String, String>> list;
	private CheckBox bt_selectall, down_check_box;
	private ProgressDialog dialog;
	private int MsgNum, MsgNum2, MsgNum1, offline_download,
			n;
	private int imageflag, Begin = 1, End, number = 100;
	private String downmsg[];
	private String onlinePort, onlineIp, username,
			userpwd, baseUrl, resname, NetUrl,
			resnameC;
	private int length;
	private Thread numloadthread;
	private ApiClient Client;
	private Button btnStart;
	private ImageView btnBack;
	private ImageView btnDown;
	private LinearLayout set_view;
	private EditText set_num_view;
	private boolean ValueChanged;
	private RadioButton img_check_box;
	private RadioButton txt_check_box;
	private CheckBox offline_box;
	private RelativeLayout offline_set;
	private RelativeLayout offlineTime_set;
	private CheckBox wifi_CheckBox;
	private CheckBox msgPush_CheckBox;
	private CheckBox changeIndex_CheckBox;
	private CheckBox Message_CheckBox;
	protected String CheckIndex;
	private CheckBox check_Offline;
	private LinearLayout check_set;
	private RelativeLayout auto_download;
	private boolean isOpenPop;
	private SeekBar seekBar2;
	private TextView textView5;
	private TextView soundName;
	private ImageButton mClose;
	private LinearLayout check_time;
	private TextView show_time;
	private ImageView btnAdd;
	private OfflineTimerDialog OfflineTimerdialog;
	private OfflineActivity.OfflineReceiver OfflineReceiver;
	private CheckBox check_CheckBox;
	private CheckBox vibrate_CheckBox;
	private CheckBox sound_CheckBox;
	private Vibrator vibrator;
	private MediaPlayer mMediaPlayer;
	private RelativeLayout wifi_TypeItem;
	private RelativeLayout Message_TypeItem;
	private RelativeLayout msgPush_TypeItem;
	private RelativeLayout changeIndex_TypeItem;
	private RelativeLayout Timer_check;
	private RelativeLayout check_sound;
	private RelativeLayout check_vibrate;
	private RelativeLayout sound_select;
	private IniFile m_iniFileIO;
	private int TimeNumber;
	private String appNewsFile;
	private String WebIniFile;
	private String rootPath;
	private CheckBox check_Content;
	private RelativeLayout Content_set;
	private LinearLayout content_set;
	protected boolean stop = true;
    private String userIni;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.new_offline_setting);
		MyActivity.getInstance().addActivity(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("Offline" + getString(R.string.about_title));
        this.OfflineReceiver = new OfflineActivity.OfflineReceiver();
        this.registerReceiver(this.OfflineReceiver, intentFilter);
        this.init();
	}

	private void init() {
        this.Client = new ApiClient();
        this.lv = (ListView) this.findViewById(R.id.listTopicItems);
        this.Timerlv = (ListView) this.findViewById(R.id.listTimerItems);
        this.set_view = (LinearLayout) this.findViewById(R.id.show_set);
        this.check_time = (LinearLayout) this.findViewById(R.id.check_time);
        this.check_set = (LinearLayout) this.findViewById(R.id.checkdata_set);
        this.content_set = (LinearLayout) this.findViewById(R.id.content_set);
        this.set_num_view = (EditText) this.findViewById(R.id.set_count);
        this.show_time = (TextView) this.findViewById(R.id.show_time);
        this.soundName = (TextView) this.findViewById(R.id.select_soundname);
        this.offline_box = (CheckBox) this.findViewById(R.id.No_Offline);

        this.offline_set = (RelativeLayout) this.findViewById(R.id.offline_set);
        this.Content_set = (RelativeLayout) this.findViewById(R.id.Content_set);
        this.auto_download = (RelativeLayout) this.findViewById(R.id.auto_download);
        this.offlineTime_set = (RelativeLayout) this.findViewById(R.id.OfflineTime_set);
        this.Timer_check = (RelativeLayout) this.findViewById(R.id.check_TypeItem);
        this.check_sound = (RelativeLayout) this.findViewById(R.id.sound_TypeItem);
        this.sound_select = (RelativeLayout) this.findViewById(R.id.sound_select);
        this.check_vibrate = (RelativeLayout) this.findViewById(R.id.vibrate_TypeItem);
        this.wifi_TypeItem = (RelativeLayout) this.findViewById(R.id.wifi_TypeItem);
        this.Message_TypeItem = (RelativeLayout) this.findViewById(R.id.Message_TypeItem);
        this.msgPush_TypeItem = (RelativeLayout) this.findViewById(R.id.msgPush_TypeItem);
        this.changeIndex_TypeItem = (RelativeLayout) this.findViewById(R.id.changeIndex_TypeItem);
        this.img_check_box = (RadioButton) this.findViewById(R.id.radioBtnImg);
        this.txt_check_box = (RadioButton) this.findViewById(R.id.radioBtnTxt);

        this.check_CheckBox = (CheckBox) this.findViewById(R.id.check_CheckBox);
        this.check_Content = (CheckBox) this.findViewById(R.id.check_Content);
        this.vibrate_CheckBox = (CheckBox) this.findViewById(R.id.vibrate_CheckBox);
        this.sound_CheckBox = (CheckBox) this.findViewById(R.id.sound_CheckBox);
        this.down_check_box = (CheckBox) this.findViewById(R.id.down_check_box);
        this.bt_selectall = (CheckBox) this.findViewById(R.id.chxSelectAll);
        this.check_Offline = (CheckBox) this.findViewById(R.id.check_Offline);
        this.wifi_CheckBox = (CheckBox) this.findViewById(R.id.wifi_CheckBox);
        this.msgPush_CheckBox = (CheckBox) this.findViewById(R.id.msgPush_CheckBox);
        this.changeIndex_CheckBox = (CheckBox) this.findViewById(R.id.changeIndex_CheckBox);
        this.Message_CheckBox = (CheckBox) this.findViewById(R.id.Message_CheckBox);

        this.btnAdd = (ImageView) this.findViewById(R.id.add_offlineTimer);
        this.btnStart = (Button) this.findViewById(R.id.btnKeyToStart);
        this.btnBack = (ImageView) this.findViewById(R.id.btnClose);
        this.btnDown = (ImageView) this.findViewById(R.id.btnDown);

        this.check_sound.setOnClickListener(this);
        this.sound_select.setOnClickListener(this);
        this.check_vibrate.setOnClickListener(this);
        this.Timer_check.setOnClickListener(this);
        this.btnAdd.setOnClickListener(this);
        this.btnStart.setOnClickListener(this);
        this.btnBack.setOnClickListener(this);
        this.btnDown.setOnClickListener(this);
        this.check_time.setOnClickListener(this);
        this.offlineTime_set.setOnClickListener(this);
        this.offline_set.setOnClickListener(this);
        this.Content_set.setOnClickListener(this);
        this.bt_selectall.setOnClickListener(this);
        this.img_check_box.setOnClickListener(this);
        this.txt_check_box.setOnClickListener(this);
        this.auto_download.setOnClickListener(this);
        this.wifi_TypeItem.setOnClickListener(this);
        this.msgPush_TypeItem.setOnClickListener(this);
        this.changeIndex_TypeItem.setOnClickListener(this);
        this.Message_TypeItem.setOnClickListener(this);
        this.m_iniFileIO = new IniFile();
        this.initPath();
        this.list = new ArrayList<HashMap<String, String>>();/* 在数组中存放数据 */
        this.GetDbMsgThread();
		int Timer_check_flag = Integer.parseInt(this.m_iniFileIO.getIniString(
                this.appNewsFile, "OFFLINESETTING", "timeCheck", "0", (byte) 0));
		if (Timer_check_flag == 1) {
            this.check_CheckBox.setChecked(true);
		}
		int check_vibrate_flag = Integer.parseInt(this.m_iniFileIO.getIniString(
                this.appNewsFile, "OFFLINESETTING", "timeCheck_vibrate", "0",
				(byte) 0));
		if (check_vibrate_flag == 1) {
            this.vibrate_CheckBox.setChecked(true);
		}
		int check_sound_flag = Integer.parseInt(this.m_iniFileIO
				.getIniString(this.appNewsFile, "OFFLINESETTING", "timeCheck_sound",
						"0", (byte) 0));
		if (check_sound_flag == 1) {
            this.sound_select.setVisibility(View.VISIBLE);
            this.sound_CheckBox.setChecked(true);
		}
		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		String soundname = RingtoneManager.getRingtone(this,
				notification).getTitle(this);
		if (this.m_iniFileIO.getIniString(this.appNewsFile, "OFFLINESETTING",
				"timeCheck_soundUrl", notification.toString(), (byte) 0) == null) {
            this.soundName.setText(soundname);
		} else {
			soundname = this.m_iniFileIO.getIniString(this.appNewsFile, "OFFLINESETTING",
					"timeCheck_soundName", soundname, (byte) 0);
            this.soundName.setText(soundname);
		}
		int checkTime = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "time", "10", (byte) 0));
        this.show_time.setText(checkTime + "分钟");
        this.imageflag = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "setup_image", "0", (byte) 0));
        this.number = UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "number", 100);
		if (this.imageflag == 1) {
            this.img_check_box.setChecked(true);
		} else {
            this.txt_check_box.setChecked(true);
		}
        this.set_num_view.setText("" + this.number);
        this.set_num_view.addTextChangedListener(new TextWatcher() {

			private String set_numTxt;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

                this.set_numTxt = String.valueOf(set_num_view.getText());

				if (null != this.set_numTxt && !this.set_numTxt.equals("")
						&& !this.set_numTxt.equals("\\s{1,}")) {
                    ValueChanged = true;
				}
				Log.i("set_num_view", "onTextChanged...");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				Log.i("set_num_view", "beforeTextChanged...");
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i("set_num_view", "afterTextChanged...");
			}
		});
        this.offline_download = Integer.parseInt(this.m_iniFileIO.getIniString(
                this.appNewsFile, "OFFLINESETTING", "auto", "0", (byte) 0));
		if (this.offline_download == 1) {
            this.down_check_box.setChecked(true);
		} else {
            this.down_check_box.setChecked(false);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"Offline", "setup_wifi", "1", (byte) 0)) == 1) {
            this.wifi_CheckBox.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "setup_tbf", "0", (byte) 0)) == 1) {
            this.msgPush_CheckBox.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "setup_index", "0", (byte) 0)) == 1) {
            this.changeIndex_CheckBox.setChecked(true);
		}
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "setup_line", "0", (byte) 0)) == 1) {
            this.Message_CheckBox.setChecked(true);
		}
		// ��listView�ļ�����
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// ȡ��ViewHolder���������ʡȥ��ͨ�����findViewByIdȥʵ��������Ҫ��cbʵ��Ĳ���
				ManagerMyAdapter.ViewHolder holder = (ManagerMyAdapter.ViewHolder) arg1.getTag();
				// ��CheckBox��ѡ��״����¼����
				String apptext = (String) holder.re.getText();
				if (holder.cb.isChecked() == false) {
					holder.cb.setChecked(true);
					int DataNumber = Integer.parseInt(m_iniFileIO
							.getIniString(appNewsFile, "WEBDATA", "DataNumber",
									"0", (byte) 0));
					if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
							"WEBDATA", apptext, "2", (byte) 0)) == 2) {
						String key = String.valueOf(DataNumber + 1);
                        m_iniFileIO.writeIniString(appNewsFile, "WEBDATA",
								apptext + "title", (String) holder.tv.getText());
                        m_iniFileIO.writeIniString(appNewsFile, "WEBDATA",
								"DataNumber", key);
                        m_iniFileIO.writeIniString(appNewsFile, "WEBDATA",
								"dataname" + key, apptext);
					}
                    m_iniFileIO.writeIniString(appNewsFile, "WEBDATA", apptext,
							"1");
				} else {
					holder.cb.setChecked(false);
                    m_iniFileIO.writeIniString(appNewsFile, "WEBDATA", apptext,
							"0");
				}
			}
		});
        this.timerList = new ArrayList<String>();
        this.initTimerDate();
        this.TimermAdapter = new TimerMyAdapter(this.timerList, this);
        this.Timerlv.setAdapter(this.TimermAdapter);
        this.setListViewHeightBasedOnChildren(this.Timerlv);
	}

	private void initPath() {
		// TODO Auto-generated method stub
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
        this.rootPath = webRoot;
        this.WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(this.WebIniFile, "TBSWeb", "IniName",
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
		String devicemd5 = this.m_iniFileIO.getIniString(this.appNewsFile, "DEVICE",
				"device_md5", "0", (byte) 0);
		if (devicemd5.equals("0")) {
			devicemd5 = UIHelper.DeviceMD5ID(this);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "DEVICE", "device_md5",
					devicemd5);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 结束Activity&从堆栈中移除
		if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
		}
        this.unregisterReceiver(this.OfflineReceiver);
		MyActivity.getInstance().finishActivity(this);
	}

	@SuppressWarnings("deprecation")
	public void GetDbMsgThread() {
		Thread dbmsgThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String ipUrl = m_iniFileIO.getIniString(appNewsFile, "SERVICE",
						"currentAddress", constants.DefaultLocalIp, (byte) 0);
				String portUrl = m_iniFileIO.getIniString(appNewsFile,
						"SERVICE", "currentPort", constants.DefaultLocalPort,
						(byte) 0);
                baseUrl = "http://" + ipUrl + ":" + portUrl;
				String DbMsg = m_iniFileIO.getIniString(appNewsFile, "MSGURL",
						"DbMsg", "", (byte) 0);
                if(!DbMsg.isEmpty()){
                    String url = baseUrl + DbMsg;
                    try {
                        constants.messageStr = null;
                        constants.messageStr  = Client.DbMsg(url);
                    } catch (AppException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler1.sendMessage(msg);
                }
			}
		});
		if (dbmsgThread.isAlive()) {
			dbmsgThread.stop();
		}
		dbmsgThread.start();
	}

	// ��ʼ�����
	private void initDate() {
		for (int i = 0; i < constants.messageStr.length; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ResName", constants.messageStr[i][0]);
			map.put("AppName", constants.messageStr[i][1]);
            this.list.add(map);
		}
	}

	private void initTimerDate() {
        this.TimeNumber = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"CheckTime", "TimeNumber", "0", (byte) 0));
		if (this.TimeNumber > 0) {
			for (int i = 1; i <= this.TimeNumber; i++) {
				String key = String.valueOf(i);
				String middletime1 = this.m_iniFileIO.getIniString(this.appNewsFile,
						"CheckTime", "StartTime" + key, "0", (byte) 0);
				String middletime2 = this.m_iniFileIO.getIniString(this.appNewsFile,
						"CheckTime", "EndTime" + key, "0", (byte) 0);
                this.timerList.add(middletime1 + "-" + middletime2);
			}
		}
	}

	private void dataChanged() {
        this.mAdapter.notifyDataSetChanged();
	}

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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.chxSelectAll:
			if (this.bt_selectall.isChecked()) {
				if (constants.messageStr == null) {
					Toast.makeText(this, "暂无更新内容",
							Toast.LENGTH_SHORT).show();
				} else {
					for (int i = 0; i < this.list.size(); i++) {
						String key = String.valueOf(i + 1);
                        this.m_iniFileIO.writeIniString(this.appNewsFile, "WEBDATA", this.list
								.get(i).get("ResName").toString(), "1");
                        this.m_iniFileIO.writeIniString(this.appNewsFile, "WEBDATA", this.list
								.get(i).get("ResName")
								+ "title", this.list.get(i).get("AppName")
								.toString());
                        this.m_iniFileIO.writeIniString(this.appNewsFile, "WEBDATA",
								"dataname" + key, this.list.get(i).get("ResName")
										.toString());
					}
					String key = String.valueOf(this.list.size());
                    this.m_iniFileIO.writeIniString(this.appNewsFile, "WEBDATA",
							"DataNumber", key);
                    this.dataChanged();
				}
			} else {
				if (constants.messageStr == null) {
					Toast.makeText(this, "暂无更新内容",
							Toast.LENGTH_SHORT).show();
				} else {
					for (int i = 0; i < this.list.size(); i++) {
                        this.m_iniFileIO.writeIniString(this.appNewsFile, "WEBDATA", this.list
								.get(i).get("ResName").toString(), "0");
                        this.m_iniFileIO.writeIniString(this.appNewsFile, "WEBDATA", this.list
								.get(i).get("ResName")
								+ "title", this.list.get(i).get("AppName")
								.toString());
					}
                    this.dataChanged();
				}
			}
			break;
		case R.id.btnKeyToStart:
			int downflag = UIHelper.getShareperference(this,
					constants.SAVE_LOCALMSGNUM, "downflag", 0);
			if (downflag == 1) {
				Toast.makeText(this, "已有数据更新提示",
						Toast.LENGTH_SHORT).show();
			} else if (downflag == 2) {
				Toast.makeText(this, "正在更新...",
						Toast.LENGTH_SHORT).show();
			} else {
                this.length = this.list.size() - 1;
				int flag = 0;
				for (int i = 0; i < this.list.size(); i++) {
					if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
							"WEBDATA", this.list.get(i).get("ResName").toString(),
							"0", (byte) 0)) == 1) {
						flag = 1;
                        this.downnum();
						break;
					}
				}
				if (flag == 0)
					Toast.makeText(this, "至少选择一个信息下载",
							Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.auto_download:
			if (this.down_check_box.isChecked()) {
                this.down_check_box.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"auto", "0");
			} else {
                this.down_check_box.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"auto", "1");
			}
			break;
		case R.id.radioBtnImg:
            this.ValueChanged = true;
			break;
		case R.id.radioBtnTxt:
            this.ValueChanged = true;
			break;
		case R.id.wifi_TypeItem:
			if (this.wifi_CheckBox.isChecked()) {
                this.wifi_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_wifi", "0");
			} else {
                this.wifi_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_wifi", "1");
			}
			break;
		case R.id.msgPush_TypeItem:
			if (this.msgPush_CheckBox.isChecked()) {
                this.msgPush_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_tbf", "0");
			} else {
                this.msgPush_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_tbf", "1");
			}
			break;
		case R.id.changeIndex_TypeItem:
			if (this.changeIndex_CheckBox.isChecked()) {
                this.changeIndex_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_index", "0");
			} else {
                this.changeIndex_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_index", "1");
			}
			break;
		case R.id.check_TypeItem:
			if (this.check_CheckBox.isChecked()) {
                this.check_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"timeCheck", "0");
//                this.stopService(new Intent(
//                        getString(string.ServerName)));
                Intent intent = new Intent();
                intent.setAction(getString(R.string.ServerName));//你定义的service的action
                intent.setPackage(getPackageName());//这里你需要设置你应用的包名
                stopService(intent);
			} else {
                this.check_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"timeCheck", "1");
                Intent mIntent = new Intent();
                mIntent.setAction(this
                        .getString(R.string.ServerName));//你定义的service的action
                mIntent.setPackage(this.getPackageName());//这里你需要设置你应用的包名
                this.startService(mIntent);
//                this.startService(new Intent(
//                        getString(string.ServerName)));
			}
			break;
		case R.id.vibrate_TypeItem:
			if (this.vibrate_CheckBox.isChecked()) {
                this.vibrate_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"timeCheck_vibrate", "0");

				try {
					if (null != this.vibrator) {
                        this.vibrator.cancel();
                        this.vibrator = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
                this.vibrate_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"timeCheck_vibrate", "1");
				/*
				 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
				 */
				try {
                    this.vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
					long[] pattern = { 800, 150, 400, 130 }; // OFF/ON/OFF/ON...
                    this.vibrator.vibrate(pattern, -1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.sound_TypeItem:
			if (this.sound_CheckBox.isChecked()) {
                this.sound_CheckBox.setChecked(false);
                this.sound_select.setVisibility(View.GONE);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"timeCheck_sound", "0");

				try {
					if (this.mMediaPlayer != null) {
						if (this.mMediaPlayer.isPlaying()) {
                            this.mMediaPlayer.stop();
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.release();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
                this.sound_CheckBox.setChecked(true);
                this.sound_select.setVisibility(View.VISIBLE);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"timeCheck_sound", "1");
				Uri alert = null;
				try {
					if (this.m_iniFileIO.getIniString(this.appNewsFile, "OFFLINESETTING",
							"timeCheck_soundUrl", "0", (byte) 0) == "0") {
						alert = RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					} else {
						String sounduri = this.m_iniFileIO.getIniString(this.appNewsFile,
								"OFFLINESETTING", "timeCheck_soundUrl", "0",
								(byte) 0);
						alert = Uri.parse(sounduri);
					}
                    this.mMediaPlayer = new MediaPlayer();
                    this.mMediaPlayer.setDataSource(this, alert);
                    this.mMediaPlayer
							.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
                    this.mMediaPlayer.setLooping(false);
                    this.mMediaPlayer.prepare();
                    this.mMediaPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.Message_TypeItem:
			if (this.Message_CheckBox.isChecked()) {
                this.Message_CheckBox.setChecked(false);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_line", "0");
			} else {
                this.Message_CheckBox.setChecked(true);
                this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
						"setup_line", "1");
			}
			break;
		case R.id.offline_set:
			if (this.offline_box.isChecked()) {
                this.offline_box.setChecked(false);
                this.set_view.setVisibility(View.GONE);
			} else {
                this.offline_box.setChecked(true);
                this.set_view.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.add_offlineTimer:
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            this.OfflineTimerdialog = new OfflineTimerDialog(this);
            this.OfflineTimerdialog.setCanceledOnTouchOutside(false);
            this.OfflineTimerdialog.show();
			break;
		case R.id.check_time:
            this.changSetPopState(arg0);
			break;
		case R.id.sound_select:
			Uri middleUri = null;
			String sounduri = this.m_iniFileIO.getIniString(this.appNewsFile,
					"OFFLINESETTING", "timeCheck_soundUrl", "0", (byte) 0);
			if (sounduri == "0") {
				middleUri = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			} else {
				middleUri = Uri.parse(sounduri);
			}
			// 打开系统铃声设置
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			// 设置铃声类型和title
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					middleUri);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
					RingtoneManager.TYPE_NOTIFICATION);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置铃声");
			// 当设置完成之后返回到当前的Activity
            this.startActivityForResult(intent, R.id.sound_select);
			break;
		case R.id.btnClose:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.feedback_close_button:
            this.set_CheckTime.dismiss();
			break;
		case R.id.OfflineTime_set:
			if (this.check_Offline.isChecked()) {
                this.check_Offline.setChecked(false);
                this.check_set.setVisibility(View.GONE);
			} else {
                this.check_Offline.setChecked(true);
                this.check_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.Content_set:
			if (this.check_Content.isChecked()) {
                this.check_Content.setChecked(false);
                this.content_set.setVisibility(View.GONE);
			} else {
                this.check_Content.setChecked(true);
                this.content_set.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btnDown:
			if (this.ValueChanged) {
				if (this.img_check_box.isChecked()) {
                    this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
							"setup_image", "1");
				} else if (this.txt_check_box.isChecked()) {
                    this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
							"setup_image", "0");
				}
				String count = this.set_num_view.getText().toString();
				if (!count.equals("") && count != null) {
                    this.number = Integer.parseInt(count);
					if (this.number == 0) {
                        this.number = 100;
					}
					UIHelper.setSharePerference(this,
							constants.SAVE_LOCALMSGNUM, "number", this.number);
				}
			}
            this.finish();
            this.overridePendingTransition(R.anim.push_in,R.anim.push_out);
			break;
		}
	}

    public void changSetPopState(View v) {
        this.isOpenPop = !this.isOpenPop;
		if (this.isOpenPop) {
            this.checkTime_Set(v);
		} else {
			if (this.set_CheckTime != null) {
                this.set_CheckTime.dismiss();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void checkTime_Set(View parent) {
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = lay.inflate(R.layout.set_check_time, null);
		// toolcheck = (CheckBox) view.findViewById(R.R.id.menu_check_box);
        this.seekBar2 = (SeekBar) view.findViewById(R.id.SeekBar03);
        this.textView5 = (TextView) view.findViewById(R.id.TextView05);
        this.mClose = (ImageButton) view.findViewById(R.id.feedback_close_button);
        this.mClose.setOnClickListener(this);
		int checkTime = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "time", "10", (byte) 0));
        this.seekBar2.setProgress(checkTime);
        this.textView5.setText("定时检测时间间隔为：" + checkTime + "分钟");
        this.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			int middleTime;
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
                textView5.setText("定时检测时间间隔为：" + progress + "分钟");
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
                m_iniFileIO.writeIniString(appNewsFile, "OFFLINESETTING",
						"time", this.middleTime + "");
                textView5.setText("定时检测时间间隔为：" + this.middleTime + "分钟");
                show_time.setText(this.middleTime + "分钟");
			}
		});
        this.set_CheckTime = new PopupWindow(view,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		// �˶�ʵ�ֵ���հ״�����popwindow
        this.set_CheckTime.setFocusable(false);
        this.set_CheckTime.setOutsideTouchable(true);
        this.set_CheckTime.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
                isOpenPop = false;
			}
		});
		// set_CheckTime.showAsDropDown(parent, -95, 3);
        this.set_CheckTime.showAtLocation(parent, Gravity.CENTER | Gravity.CENTER,
				0, 0);
        this.set_CheckTime.update();
	}

	@SuppressWarnings("deprecation")
	private void downnum() {
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile, "WEBDATA",
                this.list.get(this.length).get("ResName").toString(), "0", (byte) 0)) == 1) {
			UIHelper.setSharePerference(this,
					constants.SAVE_LOCALMSGNUM, "downflag", 1);
            this.resname = this.list.get(this.length).get("ResName").toString();
            this.resnameC = this.list.get(this.length).get("AppName").toString();
		} else {
			Message msg = new Message();
			msg.what = 5;
            this.mHandler1.sendMessage(msg);
			return;
		}
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle(this.resnameC);
		dialog.setMessage("正在检查数据更新，请稍候...");
		dialog.setIndeterminate(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (((Dialog) dialog).isShowing()) {
					dialog.dismiss();
					Message msg = new Message();
					msg.what = 4;
                    mHandler1.sendMessage(msg);
				}
			}
		});
		dialog.show();
        this.onlinePort = this.m_iniFileIO.getIniString(this.userIni, "Offline",
				"offlinePort", constants.DefaultServerPort, (byte) 0);
        this.onlineIp = this.m_iniFileIO.getIniString(this.userIni, "Offline",
				"offlineAddress", constants.DefaultServerIp, (byte) 0);
		String ipUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(this.userIni, "SERVICE",
				"currentPort", constants.DefaultLocalPort, (byte) 0);
        this.userpwd = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
				"networkPwd", "guest", (byte) 0);
        this.username = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
				"networkUserName", "guest", (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
        this.NetUrl = "http://" + this.onlineIp + ":" + this.onlinePort;
        this.numloadthread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String OfflineDownloadpage = m_iniFileIO.getIniString(
                            appNewsFile, "DOWNLOADURL", "OfflineDownloadpage",
							"0", (byte) 0);
					if (onlinePort != "" && onlinePort != null
							&& onlineIp != "" && onlineIp != null
							&& userpwd != "" && userpwd != null
							&& username != "" && username != null) {
						String baseUrl1 = baseUrl + OfflineDownloadpage
								+ username + "&password=" + userpwd
								+ "&webserverip=" + onlineIp
								+ "&webserverport=" + onlinePort + "&resname="
								+ resname;
                        downmsg = Client.DownloadData(baseUrl1);
						Message msg = new Message();
						if (downmsg == null) {
							msg.what = 2;
						} else {
							if (downmsg[0].equals("0") || downmsg[0].equals("")) {
								msg.what = 0;
							} else {
								msg.what = 1;
							}
						}
                        mHandler1.sendMessage(msg);
						dialog.dismiss();
					} else {
						dialog.dismiss();
						Toast.makeText(OfflineActivity.this, "检查服务设置 ",
								Toast.LENGTH_SHORT).show();
					}
				} catch (AppException e) {
					UIHelper.setSharePerference(OfflineActivity.this,
							constants.SAVE_LOCALMSGNUM, "downflag", 0);
					dialog.dismiss();
				}

			}
		});
		if (this.numloadthread.isAlive()) {
            this.numloadthread.stop();
		}
        this.numloadthread.start();
	}

	@SuppressWarnings("deprecation")
	public void downdata() {
		UIHelper.setSharePerference(this,
				constants.SAVE_LOCALMSGNUM, "downflag", 2);
        this.dialog = new ProgressDialog(this);
        this.dialog.setTitle(this.resnameC);
        this.dialog.setMessage("数据正在下载，请稍候...");
        this.dialog.setIndeterminate(false);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				UIHelper.setSharePerference(OfflineActivity.this,
						constants.SAVE_LOCALMSGNUM, "downflag", 0);
                n = 0;
				dialog.dismiss();
			}
		});
        this.dialog.show();
		Thread dataloadthread = new Thread(new Runnable() {
			private CBSInterpret mInterpret;

			@Override
			public void run() {
				try {
                    MsgNum1 = MsgNum2 - MsgNum;
					if (MsgNum == 1) {
                        n = 0;
					} else {
                        n = (MsgNum - 1) / number;
					}
                    initPath();
                    CheckIndex = m_iniFileIO.getIniString(appNewsFile,
							"DOWNLOADURL", "CheckIndex", "0", (byte) 0);
					String OfflineDownloadpic = m_iniFileIO.getIniString(
                            appNewsFile, "DOWNLOADURL", "OfflineDownloadpic",
							"0", (byte) 0);
					String OfflineDownloadpart = m_iniFileIO.getIniString(
                            appNewsFile, "DOWNLOADURL", "OfflineDownloadpart",
							"0", (byte) 0);
					for (int i = 0; i <= n; i++) {
						if (Client.NetCheck(NetUrl) == true) {
							if (MsgNum > number) {
								int count = MsgNum - i * number;
								if (count > number) {
                                    Begin = MsgNum1 + i * number + 1;
                                    End = Begin + number - 1;
									Message msg = new Message();
									msg.what = 1;
                                    mHandler.sendMessage(msg);
									//System.out.println(Begin);
									//System.out.println(End);
									String baseUrl1 = baseUrl
											+ OfflineDownloadpart + username
											+ "&password=" + userpwd
											+ "&webserverip=" + onlineIp
											+ "&webserverport=" + onlinePort
											+ "&Begin=" + Begin + "&End=" + End
											+ "&resname=" + resname;
                                    Client.http_get(baseUrl1);
									if (imageflag == 1) {
										baseUrl1 = baseUrl + OfflineDownloadpic
												+ username + "&password="
												+ userpwd + "&webserverip="
												+ onlineIp + "&webserverport="
												+ onlinePort + "&Begin="
												+ Begin + "&End=" + End
												+ "&resname=" + resname;
                                        Client.http_get(baseUrl1);
									}
								} else {
                                    Begin = MsgNum1 + i * number + 1;
                                    End = MsgNum2;
									Message msg = new Message();
									msg.what = 1;
                                    mHandler.sendMessage(msg);
									//System.out.println(Begin);
									//System.out.println(End);
									String baseUrl1 = baseUrl
											+ OfflineDownloadpart + username
											+ "&password=" + userpwd
											+ "&webserverip=" + onlineIp
											+ "&webserverport=" + onlinePort
											+ "&Begin=" + Begin + "&End=" + End
											+ "&resname=" + resname;
                                    Client.http_get(baseUrl1);
									if (imageflag == 1) {
										baseUrl1 = baseUrl + OfflineDownloadpic
												+ username + "&password="
												+ userpwd + "&webserverip="
												+ onlineIp + "&webserverport="
												+ onlinePort + "&Begin="
												+ Begin + "&End=" + End
												+ "&resname=" + resname;
                                        Client.http_get(baseUrl1);
									}
									Message msg1 = new Message();
									msg1.what = 4;
                                    mHandler.sendMessage(msg1);
								}
							} else {
                                Begin = MsgNum1 + i * number + 1;
                                End = MsgNum2;
								Message msg = new Message();
								msg.what = 1;
                                mHandler.sendMessage(msg);
								//System.out.println(Begin);
								//System.out.println(End);
								String baseUrl1 = baseUrl + OfflineDownloadpart
										+ username + "&password=" + userpwd
										+ "&webserverip=" + onlineIp
										+ "&webserverport=" + onlinePort
										+ "&Begin=" + Begin + "&End=" + End
										+ "&resname=" + resname;
                                Client.http_get(baseUrl1);
								if (imageflag == 1) {
									baseUrl1 = baseUrl + OfflineDownloadpic
											+ username + "&password=" + userpwd
											+ "&webserverip=" + onlineIp
											+ "&webserverport=" + onlinePort
											+ "&Begin=" + Begin + "&End=" + End
											+ "&resname=" + resname;
                                    Client.http_get(baseUrl1);
								}
								if (Integer.parseInt(m_iniFileIO.getIniString(
                                        appNewsFile, "OFFLINESETTING",
										"setup_index", "0", (byte) 0)) == 1) {
                                    this.mInterpret = new CBSInterpret();
                                    this.mInterpret.initGlobal(WebIniFile, rootPath);
                                    this.mInterpret.Interpret(CheckIndex + resname,
											"GET", "", null, 0);
									// String CheckUrl = baseUrl + CheckIndex
									// + resname;
									// Client.http_get(CheckUrl);
								}
								UIHelper.setSharePerference(
										OfflineActivity.this,
										constants.SAVE_LOCALMSGNUM, "downflag",
										0);
								Message msg1 = new Message();
								msg1.what = 4;
                                mHandler.sendMessage(msg1);
							}
						} else {
							if (Integer.parseInt(m_iniFileIO.getIniString(
                                    appNewsFile, "OFFLINESETTING",
									"setup_index", "0", (byte) 0)) == 1) {
                                this.mInterpret = new CBSInterpret();
                                this.mInterpret.initGlobal(WebIniFile, rootPath);
                                this.mInterpret.Interpret(CheckIndex + resname,
										"GET", "", null, 0);
								// String CheckUrl = baseUrl + CheckIndex
								// + resname;
								// Client.http_get(CheckUrl);
							}
                            dialog.dismiss();
							Message msg = new Message();
							msg.what = 3;
                            mHandler.sendMessage(msg);
							break;
						}
					}
                    dialog.dismiss();
					// SlidingActivity.webview.reload();
				} catch (AppException e) {
                    dialog.dismiss();
					if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
							"OFFLINESETTING", "setup_index", "0", (byte) 0)) == 1) {
                        this.mInterpret = new CBSInterpret();
                        this.mInterpret.initGlobal(WebIniFile, rootPath);
                        this.mInterpret.Interpret(CheckIndex + resname, "GET", "",
								null, 0);
					}
					Message msg = new Message();
					msg.what = 2;
                    mHandler.sendMessage(msg);
				}

			}
		});
		dataloadthread.start();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
                dialog.setMessage("数据正在下载，请稍候..."
						+ (End - MsgNum1) * 100 / MsgNum + "%");
				break;
			case 2:
				UIHelper.setSharePerference(OfflineActivity.this,
						constants.SAVE_LOCALMSGNUM, "downflag", 0);
				Toast.makeText(OfflineActivity.this, "错误(E0002)：更新中断,服务异常",
						Toast.LENGTH_SHORT).show();
				StartTbsweb.Startapp(OfflineActivity.this, 1);
				break;
			case 3:
				UIHelper.setSharePerference(OfflineActivity.this,
						constants.SAVE_LOCALMSGNUM, "downflag", 0);
				Toast.makeText(OfflineActivity.this,
						"错误(E0001)：网络异常，请检查网络是否连接", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				if (length > 0 && stop == true) {
                    length--;
                    downnum();
				} else {
					UIHelper.setSharePerference(OfflineActivity.this,
							constants.SAVE_LOCALMSGNUM, "downflag", 0);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	Handler mHandler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(OfflineActivity.this, resnameC + "已是最新信息 ",
						Toast.LENGTH_SHORT).show();
				if (length > 0 && stop == true) {
                    length--;
                    downnum();
				} else {
					UIHelper.setSharePerference(OfflineActivity.this,
							constants.SAVE_LOCALMSGNUM, "downflag", 0);
				}
				break;
			case 1:
                MsgNum = Integer.parseInt(downmsg[0]);
                MsgNum2 = Integer.parseInt(downmsg[1]);
				if (MsgNum > 0 && MsgNum2 >= MsgNum) {
					// CreateSetNum();
                    downdata();
				} else {
					Toast.makeText(OfflineActivity.this,
                            resnameC + "已是最新信息 ", Toast.LENGTH_SHORT).show();
					if (length > 0 && stop == true) {
                        length--;
                        downnum();
					} else {
						UIHelper.setSharePerference(OfflineActivity.this,
								constants.SAVE_LOCALMSGNUM, "downflag", 0);
					}
				}
				break;
			case 2:
				UIHelper.setSharePerference(OfflineActivity.this,
						constants.SAVE_LOCALMSGNUM, "downflag", 0);
				Toast.makeText(OfflineActivity.this,
						"错误(E00028)：无法连接到下载服务！", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				if (constants.messageStr != null) {
					// list = new ArrayList<HashMap<String, Object>>();/*
					// 在数组中存放数据 */
                    initDate();
                    mAdapter = new ManagerMyAdapter(list,
							OfflineActivity.this, appNewsFile);
                    lv.setAdapter(mAdapter);
                    setListViewHeightBasedOnChildren(lv);
				}
				break;
			case 4:
                numloadthread.interrupt();
                stop = false;
				UIHelper.setSharePerference(OfflineActivity.this,
						constants.SAVE_LOCALMSGNUM, "downflag", 0);
				break;
			case 5:
				if (length > 0 && stop == true) {
                    length--;
                    downnum();
				} else {
					UIHelper.setSharePerference(OfflineActivity.this,
							constants.SAVE_LOCALMSGNUM, "downflag", 0);
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	private class OfflineReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("Offline"
					+ getString(R.string.about_title))) {
				int SelBtn = intent.getIntExtra("flag", 1);
				switch (SelBtn) {
				case 1:
					if (constants.timerStr != null) {
                        timerList.add(constants.timerStr[0][0] + "-"
								+ constants.timerStr[0][1]);
                        TimermAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(Timerlv);
					}
					break;
				case 2:
                    setListViewHeightBasedOnChildren(Timerlv);
					break;
				}
			}
		}
	}

	/* 当设置铃声之后的回调函数 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case R.id.sound_select:
			try {
				// 得到我们选择的铃声
				Uri pickedUri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (pickedUri != null) {
					String soundname = RingtoneManager.getRingtone(
                            this, pickedUri).getTitle(
                            this);
                    this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
							"timeCheck_soundUrl", pickedUri.toString());
                    this.m_iniFileIO.writeIniString(this.appNewsFile, "OFFLINESETTING",
							"timeCheck_soundName", soundname);
                    this.soundName.setText(soundname);
				}
			} catch (Exception e) {
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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