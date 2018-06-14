package com.tbs.tbsmis.update;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("HandlerLeak")
public class OfflineMsgService extends Service {
	private IniFile m_iniFileIO;
	public TimerTask mTimerTask;
	private BroadcastReceiver downloadReceiver;
	public int NetWorkType;
	private String appNewsFile;
	public int count;
    private String userIni;

    // private class MyBroadcastReciver extends BroadcastReceiver {
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
	// if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
	// ConnectivityManager connectivityManager = (ConnectivityManager)
	// getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo info = connectivityManager
	// .getActiveNetworkInfo();
	// if (info != null && info.isAvailable()
	// && info.isConnected()) {
	// if (info.getState() == NetworkInfo.State.CONNECTED) {
	// iniPath();
	// // 增加时间段判断
	// if (Integer.parseInt(m_iniFileIO.getIniString(
	// appNewsFile, "OFFLINESETTING", "timeCheck",
	// "0", (byte) 0)) == 1) {
	// if (checkTime()) {
	// Timer timer = new Timer();
	// if (timer != null) {
	// if (mTimerTask == null) {
	// startDataThread();
	// }
	// }
	// } else {
	// Intent intent_again = new Intent();
	// intent_again.setAction("downloadAgain"+this.getString(R.R.string.about_title));
	// sendBroadcast(intent_again);
	// }
	// } else {
	// startDataThread();
	// }
	// NetWorkType = info.getType();
	// }
	// } else {
	// Toast.makeText(TbsMsgService.this, "网络状态不可用",
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
	// }
	// };

	private class MyBroadcastDownload extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
            OfflineMsgService.this.iniPath();
			if (action.equals("downloadAgain"
					+ getString(R.string.about_title))) {
				if (Integer.parseInt(OfflineMsgService.this.m_iniFileIO.getIniString(OfflineMsgService.this.appNewsFile,
						"OFFLINESETTING", "timeCheck", "0", (byte) 0)) == 1) {
					if (OfflineMsgService.this.count < Integer.parseInt(OfflineMsgService.this.m_iniFileIO
							.getIniString(OfflineMsgService.this.appNewsFile, "WEBDATA", "DataNumber",
									"0", (byte) 0))) {
                        OfflineMsgService.this.count = OfflineMsgService.this.count + 1;
                        OfflineMsgService.this.startDataThread();
					} else {
                        OfflineMsgService.this.count = 0;
						Timer timer = new Timer();
						if (timer != null) {
							if (OfflineMsgService.this.mTimerTask != null) {
                                OfflineMsgService.this.mTimerTask.cancel();
							}
						}
                        OfflineMsgService.this.mTimerTask = new mTimerTask();
						int check_time = Integer.parseInt(OfflineMsgService.this.m_iniFileIO
								.getIniString(OfflineMsgService.this.appNewsFile, "OFFLINESETTING",
										"time", "10", (byte) 0));
						//System.out.println("check_time=" + check_time);
						timer.schedule(OfflineMsgService.this.mTimerTask, check_time * 60000);
					}
				}
			} else if (action.equals("downloadCycle"
					+ getString(R.string.about_title))) {
				if (Integer.parseInt(OfflineMsgService.this.m_iniFileIO.getIniString(OfflineMsgService.this.appNewsFile,
						"OFFLINESETTING", "timeCheck", "0", (byte) 0)) == 0) {
					if (OfflineMsgService.this.count < Integer.parseInt(OfflineMsgService.this.m_iniFileIO
							.getIniString(OfflineMsgService.this.appNewsFile, "WEBDATA", "DataNumber",
									"0", (byte) 0))) {
                        OfflineMsgService.this.count = OfflineMsgService.this.count + 1;
                        OfflineMsgService.this.startDataThread();
					} else {
                        OfflineMsgService.this.count = 0;
					}
				}
			}
		}
	}

    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
        this.iniPath();
		IntentFilter mFilter = new IntentFilter();
		IntentFilter DownloadFilter = new IntentFilter();
		DownloadFilter.addAction("downloadAgain"
				+ getString(R.string.about_title));
		DownloadFilter.addAction("downloadCycle"
				+ getString(R.string.about_title));
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.downloadReceiver = new MyBroadcastDownload();
		// mReceiver = new MyBroadcastReciver();
        this.registerReceiver(this.downloadReceiver, DownloadFilter);
		// registerReceiver(mReceiver, mFilter);
		if (Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"OFFLINESETTING", "timeCheck", "0", (byte) 0)) == 1) {
			if (this.checkTime()) {
				Timer timer = new Timer();
				if (timer != null) {
					if (this.mTimerTask == null) {
                        this.startDataThread();
					}
				}
			} else {
				Intent intent_again = new Intent();
				intent_again.setAction("downloadAgain"
						+ getString(R.string.about_title));
                this.sendBroadcast(intent_again);
			}
		} else {
            this.count = this.count + 1;
            this.startDataThread();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		return Service.START_STICKY;
	}

	public void startDataThread() {
        this.iniPath();
		boolean threadstar = true;
		StartTbsweb StartTbsweb = new StartTbsweb();
		final ApiClient ApiClient = new ApiClient();
		String onlinePort = this.m_iniFileIO
				.getIniString(this.userIni, "Offline", "offlinePort",
						constants.DefaultServerPort, (byte) 0);
		String onlineIp = this.m_iniFileIO.getIniString(this.userIni,
				"Offline", "offlineAddress", constants.DefaultServerIp,
				(byte) 0);
		String userpwd = this.m_iniFileIO.getIniString(this.appNewsFile, "NETWORK",
				"networkPwd", "guest", (byte) 0);
		String username = this.m_iniFileIO.getIniString(this.appNewsFile,
				"NETWORK", "networkUserName", "guest", (byte) 0);
		String ipUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "SERVICE",
				"currentAddress", constants.DefaultLocalIp, (byte) 0);
		String portUrl = this.m_iniFileIO.getIniString(this.appNewsFile, "SERVICE",
				"currentPort", constants.DefaultLocalPort, (byte) 0);
		String baseUrl = "http://" + ipUrl + ":" + portUrl;
		final String resname = this.m_iniFileIO.getIniString(this.appNewsFile, "WEBDATA",
				"dataname" + this.count, "server", (byte) 0);
		String OfflineDownloadpage = this.m_iniFileIO.getIniString(
                this.appNewsFile, "DOWNLOADURL", "OfflineDownloadpage", "0",
				(byte) 0);
		final String baseUrl1 = baseUrl + OfflineDownloadpage + username
				+ "&password=" + userpwd + "&webserverip=" + onlineIp
				+ "&webserverport=" + onlinePort + "&resname=" + resname;
		//System.out.println(baseUrl1 + "(" + count + ")");
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		// NetWorkType = networkInfo.getType();
		if (null != networkInfo && networkInfo.isConnected()) {
			final int NetWorkType = networkInfo.getType();
			while (threadstar) {
				@SuppressWarnings("static-access")
				boolean isStarted = StartTbsweb
						.isMyServiceRunning(this,null);
				if (isStarted == true) {
					threadstar = false;
					new Thread() {
						@Override
						public void run() {
							String[] downmsg = null;
							try {
								int offline_download = Integer
										.parseInt(OfflineMsgService.this.m_iniFileIO.getIniString(
                                                OfflineMsgService.this.appNewsFile, "OFFLINESETTING",
												"auto", "0", (byte) 0));
								int wifi_download = Integer
										.parseInt(OfflineMsgService.this.m_iniFileIO.getIniString(
                                                OfflineMsgService.this.appNewsFile, "OFFLINESETTING",
												"setup_wifi", "0", (byte) 0));
								downmsg = ApiClient.DownloadData(baseUrl1);
								Intent intent = new Intent();
								if (downmsg == null) {
									intent.setAction("loadView"
											+ getString(R.string.about_title));
									intent.putExtra("author", 0);
									intent.putExtra("resname", resname);
									intent.putExtra("flag", 0);
                                    OfflineMsgService.this.sendBroadcast(intent);
								} else if (downmsg[0].equals(downmsg[1])
										&& Integer.parseInt(downmsg[1]) == 0) {
									int DataNumber = Integer
											.parseInt(OfflineMsgService.this.m_iniFileIO
													.getIniString(OfflineMsgService.this.appNewsFile,
															"WEBDATA",
															"DataNumber", "0",
															(byte) 0));
									System.out.println("DataNumber="
											+ DataNumber);
									if (OfflineMsgService.this.count <= DataNumber) {
										intent = new Intent();
										intent.setAction("downloadCycle"
												+ getString(R.string.about_title));
                                        OfflineMsgService.this.sendBroadcast(intent);
										intent = new Intent();
										intent.setAction("downloadAgain"
												+ getString(R.string.about_title));
                                        OfflineMsgService.this.sendBroadcast(intent);
									}
								} else {
									if (offline_download == 1) {
										if (wifi_download == 1) {

											if (NetWorkType == ConnectivityManager.TYPE_WIFI) {
												intent.setAction("loadView"
														+ getString(R.string.about_title));
												intent.putExtra("author", 4);
												intent.putExtra("flag", 1);
												intent.putExtra("MsgNum",
														downmsg[0]);
												intent.putExtra("MsgNum2",
														downmsg[1]);
												intent.putExtra("resname",
														resname);
                                                OfflineMsgService.this.sendBroadcast(intent);
											} else {
												intent.setAction("loadView"
														+ getString(R.string.about_title));
												intent.putExtra("flag", 1);
												intent.putExtra("author", 1);
												intent.putExtra("MsgNum",
														downmsg[0]);
												intent.putExtra("MsgNum2",
														downmsg[1]);
												intent.putExtra("resname",
														resname);
                                                OfflineMsgService.this.sendBroadcast(intent);
											}
										} else if (wifi_download == 0) {
											intent.setAction("loadView"
													+ getString(R.string.about_title));
											intent.putExtra("author", 4);
											intent.putExtra("flag", 1);
											intent.putExtra("MsgNum",
													downmsg[0]);
											intent.putExtra("MsgNum2",
													downmsg[1]);
											intent.putExtra("resname", resname);
                                            OfflineMsgService.this.sendBroadcast(intent);
										}
									} else {
										intent.setAction("loadView"
												+ getString(R.string.about_title));
										intent.putExtra("flag", 1);
										intent.putExtra("author", 1);
										intent.putExtra("MsgNum", downmsg[0]);
										intent.putExtra("MsgNum2", downmsg[1]);
										intent.putExtra("resname", resname);
                                        OfflineMsgService.this.sendBroadcast(intent);
									}
								}

							} catch (AppException e) {
								// TODO Auto-generated catch block
								downmsg = null;
								Intent intent = new Intent();
								intent.setAction("downloadAgain"
										+ getString(R.string.about_title));
                                OfflineMsgService.this.sendBroadcast(intent);
								intent = new Intent();
								intent.setAction("downloadCycle"
										+ getString(R.string.about_title));
                                OfflineMsgService.this.sendBroadcast(intent);
								e.printStackTrace();
							}
						}
					}.start();
				} else {
                    this.Restartapp();
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(mReceiver);
        this.unregisterReceiver(this.downloadReceiver);
		if (this.mTimerTask != null) {
            this.mTimerTask.cancel();
		}
        this.stopSelf();
	}

	@SuppressLint("SimpleDateFormat")
	public boolean checkTime() {
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat matter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String data1 = matter1.format(dt);
		String data = matter2.format(dt);
		int TimeNumber = Integer.parseInt(this.m_iniFileIO.getIniString(this.appNewsFile,
				"CheckTime", "TimeNumber", "0", (byte) 0));
		int i;
		for (i = 1; i <= TimeNumber; i++) {
			String key = String.valueOf(i);
			String middletime1 = this.m_iniFileIO.getIniString(this.appNewsFile,
					"CheckTime", "StartTime" + key, "0", (byte) 0);
			middletime1 = data1 + " " + middletime1;
			String middletime2 = this.m_iniFileIO.getIniString(this.appNewsFile,
					"CheckTime", "EndTime" + key, "0", (byte) 0);
			middletime2 = data1 + " " + middletime2;
			if (StringUtils.isInTimer(data, middletime1, middletime2)) {
				return true;
			}
		}
		return false;
	}

	protected void iniPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
		String configPath = this.getApplicationContext().getFilesDir()
				.getParentFile().getAbsolutePath();
		if (configPath.endsWith("/") == false) {
			configPath = configPath + "/";
		}
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
        this.appNewsFile = webRoot
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
		String devicemd5 = this.m_iniFileIO.getIniString(this.appNewsFile, "DEVICE",
				"device_md5", "0", (byte) 0);
		if (devicemd5.equals("0")) {
			devicemd5 = UIHelper.DeviceMD5ID(this);
            this.m_iniFileIO.writeIniString(this.appNewsFile, "DEVICE", "device_md5",
					devicemd5);
		}
	}

	class mTimerTask extends TimerTask {
		@Override
		public void run() {
			// 增加时间判断
			if (OfflineMsgService.this.checkTime()) {
                OfflineMsgService.this.count = OfflineMsgService.this.count +1;
                OfflineMsgService.this.startDataThread();
			} else {
				Intent intent_again = new Intent();
				intent_again.setAction("downloadAgain"
						+ getString(R.string.about_title));
                OfflineMsgService.this.sendBroadcast(intent_again);
			}
		}
	}

    public void Restartapp() {
		StartTbsweb.Startapp(this, 1);
	}
}