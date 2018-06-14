package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("HandlerLeak")
public class OfflineTimerDialog extends Dialog {
	private final View customView;
	private Button setBtn;
	private Button cancleBtn;
	private final Context context;
	private TimePicker timePick1;
	private TimePicker timePick2;
	protected String appTimeFile;
	private IniFile m_iniFileIO;
	private int TimeNumber;
	private int CheckInterval;

	public OfflineTimerDialog(Context context) {
		super(context, R.style.OfflineTimerDialog);
        this.customView = LayoutInflater.from(context).inflate(R.layout.time_wheel,
				null);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(this.customView);
        this.init();
	}

	private void init() {
        this.timePick1 = (TimePicker) this.customView.findViewById(R.id.timePicker1);
        this.timePick2 = (TimePicker) this.customView.findViewById(R.id.timePicker2);
        this.setBtn = (Button) this.customView.findViewById(R.id.setBtn);
        this.cancleBtn = (Button) this.customView.findViewById(R.id.cancleBtn);
        this.m_iniFileIO = new IniFile();
		String webRoot = UIHelper.getSoftPath(context);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this.context,
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appTimeFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);

        this.TimeNumber = Integer.parseInt(this.m_iniFileIO.getIniString(this.appTimeFile,
				"CheckTime", "TimeNumber", "0", (byte) 0));
        this.CheckInterval = Integer.parseInt(this.m_iniFileIO.getIniString(this.appTimeFile,
				"OFFLINESETTING", "time", "10", (byte) 0));
		// 是否使用24小时制
        this.timePick1.setIs24HourView(true);
        this.timePick2.setIs24HourView(true);
        this.setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int h = OfflineTimerDialog.this.timePick1.getCurrentHour();
				int m = OfflineTimerDialog.this.timePick1.getCurrentMinute();
				int h2 = OfflineTimerDialog.this.timePick2.getCurrentHour();
				int m2 = OfflineTimerDialog.this.timePick2.getCurrentMinute();
				constants.timerStr = new String[1][2];
				if (h < 10) {
					if (m < 10) {
						constants.timerStr[0][0] = "0" + h + ":0" + m;
					} else {
						constants.timerStr[0][0] = "0" + h + ":" + m;
					}
				} else {
					if (m < 10) {
						constants.timerStr[0][0] = h + ":0" + m;
					} else {
						constants.timerStr[0][0] = h + ":" + m;
					}
				}
				if (h2 < 10) {
					if (m2 < 10) {
						constants.timerStr[0][1] = "0" + h2 + ":0" + m2;
					} else {
						constants.timerStr[0][1] = "0" + h2 + ":" + m2;
					}
				} else {
					if (m2 < 10) {
						constants.timerStr[0][1] = h2 + ":0" + m2;
					} else {
						constants.timerStr[0][1] = h2 + ":" + m2;
					}
				}
				if (OfflineTimerDialog.this.TimeNumber == 0) {
					if (h > h2) {
						Toast.makeText(OfflineTimerDialog.this.context, "请检查起止时间是否合法 ",
								Toast.LENGTH_LONG).show();
					} else if (h == h2) {
						if (m2 <= m) {
							Toast.makeText(OfflineTimerDialog.this.context, "请检查起止时间是否合法 ",
									Toast.LENGTH_LONG).show();
						} else if (m2 - m >= OfflineTimerDialog.this.CheckInterval) {
							Intent intent = new Intent();
							intent.setAction("Offline"
									+ OfflineTimerDialog.this.context.getString(R.string.about_title));
							intent.putExtra("flag", 1);
                            OfflineTimerDialog.this.context.sendBroadcast(intent);
                            OfflineTimerDialog.this.m_iniFileIO.writeIniString(OfflineTimerDialog.this.appTimeFile,
									"CheckTime", "StartTime1",
									constants.timerStr[0][0]);
                            OfflineTimerDialog.this.m_iniFileIO.writeIniString(OfflineTimerDialog.this.appTimeFile,
									"CheckTime", "EndTime1",
									constants.timerStr[0][1]);
                            OfflineTimerDialog.this.m_iniFileIO.writeIniString(OfflineTimerDialog.this.appTimeFile,
									"CheckTime", "TimeNumber", "1");
                            cancel();
						} else {
							Toast.makeText(OfflineTimerDialog.this.context, "时间间隔小于定时检测间隔 ",
									Toast.LENGTH_LONG).show();
						}
					} else {
						if (60 * (h2 - h) + m2 - m >= OfflineTimerDialog.this.CheckInterval) {
							Intent intent = new Intent();
							intent.setAction("Offline"
									+ OfflineTimerDialog.this.context.getString(R.string.about_title));
							intent.putExtra("flag", 1);
                            OfflineTimerDialog.this.context.sendBroadcast(intent);
                            OfflineTimerDialog.this.m_iniFileIO.writeIniString(OfflineTimerDialog.this.appTimeFile,
									"CheckTime", "StartTime1",
									constants.timerStr[0][0]);
                            OfflineTimerDialog.this.m_iniFileIO.writeIniString(OfflineTimerDialog.this.appTimeFile,
									"CheckTime", "EndTime1",
									constants.timerStr[0][1]);
                            OfflineTimerDialog.this.m_iniFileIO.writeIniString(OfflineTimerDialog.this.appTimeFile,
									"CheckTime", "TimeNumber", "1");
                            cancel();
						} else {
							Toast.makeText(OfflineTimerDialog.this.context, "时间间隔小于定时检测间隔 ",
									Toast.LENGTH_LONG).show();
						}
					}
				} else {
					if (h > h2) {
						Toast.makeText(OfflineTimerDialog.this.context, "请检查起止时间是否合法 ",
								Toast.LENGTH_LONG).show();
					} else if (h == h2) {
						if (m2 <= m) {
							Toast.makeText(OfflineTimerDialog.this.context, "请检查起止时间是否合法 ",
									Toast.LENGTH_LONG).show();
						} else if (m2 - m >= OfflineTimerDialog.this.CheckInterval) {
							if (OfflineTimerDialog.this.check_time(constants.timerStr[0][0],
									constants.timerStr[0][1])) {
								Intent intent = new Intent();
								intent.setAction("Offline"
										+ OfflineTimerDialog.this.context
												.getString(R.string.about_title));
								intent.putExtra("flag", 1);
                                OfflineTimerDialog.this.context.sendBroadcast(intent);
                                cancel();
							} else {
								Toast.makeText(OfflineTimerDialog.this.context, "不允许出现时间交叉 ",
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(OfflineTimerDialog.this.context, "时间间隔小于定时检测间隔 ",
									Toast.LENGTH_LONG).show();
						}
					} else {
						if (60 * (h2 - h) + m2 - m >= OfflineTimerDialog.this.CheckInterval) {
							if (OfflineTimerDialog.this.check_time(constants.timerStr[0][0],
									constants.timerStr[0][1])) {
								Intent intent = new Intent();
								intent.setAction("Offline"
										+ OfflineTimerDialog.this.context
												.getString(R.string.about_title));
								intent.putExtra("flag", 1);
                                OfflineTimerDialog.this.context.sendBroadcast(intent);
                                cancel();
							} else {
								Toast.makeText(OfflineTimerDialog.this.context, "不允许出现时间交叉 ",
										Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(OfflineTimerDialog.this.context, "时间间隔小于定时检测间隔 ",
									Toast.LENGTH_LONG).show();
						}
					}
				}
				((Activity) OfflineTimerDialog.this.context)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		});

        this.cancleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) OfflineTimerDialog.this.context)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                cancel();
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	public boolean check_time(String starTime, String endTime) {
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy-MM-dd");
		String data = matter1.format(dt);
		int i;
		for (i = 1; i <= this.TimeNumber; i++) {
			String key = String.valueOf(i);
			String middletime1 = this.m_iniFileIO.getIniString(this.appTimeFile,
					"CheckTime", "StartTime" + key, "0", (byte) 0);
			middletime1 = data + " " + middletime1;
			String middletime2 = this.m_iniFileIO.getIniString(this.appTimeFile,
					"CheckTime", "EndTime" + key, "0", (byte) 0);
			middletime2 = data + " " + middletime2;
			String addtime1 = data + " " + starTime;
			String addtime2 = data + " " + endTime;
			if (!OfflineTimerDialog.compare_date(middletime1, middletime2, addtime1, addtime2)) {
				return false;
			}
		}
		String key = String.valueOf(this.TimeNumber + 1);
        this.m_iniFileIO.writeIniString(this.appTimeFile, "CheckTime", "StartTime" + key,
				starTime);
        this.m_iniFileIO.writeIniString(this.appTimeFile, "CheckTime", "EndTime" + key,
				endTime);
        this.m_iniFileIO.writeIniString(this.appTimeFile, "CheckTime", "TimeNumber", key);
		return true;
	}

	@SuppressLint("SimpleDateFormat")
	public static boolean compare_date(String DATE1, String DATE2,
			String DATE3, String DATE4) {
		boolean flag = false;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			Date dt3 = df.parse(DATE3);
			Date dt4 = df.parse(DATE4);
			if (dt3.getTime() >= dt2.getTime()) {
				// System.out.println("dt1 在dt2前");
				flag = true;
			} else flag = dt4.getTime() <= dt1.getTime();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return flag;
	}
}
