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

@SuppressLint("HandlerLeak")
public class NetTaskTimerDialog extends Dialog {
	private final View customView;
	private Button setBtn;
	private Button cancleBtn;
	private final Context context;
	private TimePicker timePick1;
	private TimePicker timePick2;
	protected String appTimeFile;
	private IniFile m_iniFileIO;

	public NetTaskTimerDialog(Context context) {
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
		String webRoot =UIHelper.getSoftPath(context);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		webRoot += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
		webRoot = UIHelper.getShareperference(this.context,
				constants.SAVE_INFORMATION, "Path", webRoot);
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
        this.appTimeFile = webRoot + constants.TASK_CONFIG_FILE_NAME;
		// 是否使用24小时制
        this.timePick1.setIs24HourView(true);
        this.timePick2.setIs24HourView(true);
        this.setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int h = NetTaskTimerDialog.this.timePick1.getCurrentHour();
				int m = NetTaskTimerDialog.this.timePick1.getCurrentMinute();
				int h2 = NetTaskTimerDialog.this.timePick2.getCurrentHour();
				int m2 = NetTaskTimerDialog.this.timePick2.getCurrentMinute();
				String time;
				String time2;
				String time3;
				String time4;

				if (h < 10) {
					if (m < 10) {
						time = "0" + h + ":0" + m;
						time3 = "0" + h + "0" + m;
					} else {
						time = "0" + h + ":" + m;
						time3 = "0" + h + "" + m;
					}
				} else {
					if (m < 10) {
						time = h + ":0" + m;
						time3 = h + "0" + m;
					} else {
						time = h + ":" + m;
						time3 = h + "" + m;
					}
				}
				if (h2 < 10) {
					if (m2 < 10) {
						time2 = "0" + h2 + ":0" + m2;
						time4 = "0" + h2 + "0" + m2;
					} else {
						time2 = "0" + h2 + ":" + m2;
						time4 = "0" + h2 + "" + m2;
					}
				} else {
					if (m2 < 10) {
						time2 = h2 + ":0" + m2;
						time4 = h2 + "0" + m2;
					} else {
						time2 = h2 + ":" + m2;
						time4 = h2 + "" + m2;
					}
				}
				if (h > h2) {
					Toast.makeText(NetTaskTimerDialog.this.context, "请检查起止时间是否合法 ", Toast.LENGTH_LONG)
							.show();
				} else if (h == h2) {
					if (m2 <= m) {
						Toast.makeText(NetTaskTimerDialog.this.context, "请检查起止时间是否合法 ",
								Toast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent();
						intent.setAction("Service"
								+ NetTaskTimerDialog.this.context.getString(R.string.about_title));
						intent.putExtra("flag", 1);
						intent.putExtra("timer", time + "-" + time2);
                        NetTaskTimerDialog.this.context.sendBroadcast(intent);
                        NetTaskTimerDialog.this.m_iniFileIO.writeIniString(NetTaskTimerDialog.this.appTimeFile, "sys",
								"StartTime", time3);
                        NetTaskTimerDialog.this.m_iniFileIO.writeIniString(NetTaskTimerDialog.this.appTimeFile, "sys",
								"EndTime", time4);
						UIHelper.setSharePerference(NetTaskTimerDialog.this.context,
								constants.SAVE_LOCALMSGNUM, "task_time", time + "-" + time2);
                        cancel();
					}
				} else {
					Intent intent = new Intent();
					intent.setAction("Service"
							+ NetTaskTimerDialog.this.context.getString(R.string.about_title));
					intent.putExtra("flag", 1);
					intent.putExtra("timer", time + "-" + time2);
                    NetTaskTimerDialog.this.context.sendBroadcast(intent);
                    NetTaskTimerDialog.this.m_iniFileIO.writeIniString(NetTaskTimerDialog.this.appTimeFile, "sys", "StartTime",
							time3);
                    NetTaskTimerDialog.this.m_iniFileIO.writeIniString(NetTaskTimerDialog.this.appTimeFile, "sys", "EndTime",
							time4);
					UIHelper.setSharePerference(NetTaskTimerDialog.this.context,
							constants.SAVE_LOCALMSGNUM, "task_time", time + "-" + time2);
                    cancel();
				}
				
				((Activity) NetTaskTimerDialog.this.context)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		});

        this.cancleBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((Activity) NetTaskTimerDialog.this.context)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                cancel();
			}
		});
	}

}
