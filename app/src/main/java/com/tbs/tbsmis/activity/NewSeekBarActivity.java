package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

/**
 * 用户反馈
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class NewSeekBarActivity extends Activity {

	private ImageButton mClose;
	private SeekBar seekBar, seekBar1, seekBar2;
	private TextView textView1, textView3, textView5;
	private int splitratio, splitratio1, screenWidth,
			splitratioMargin;
	private int Flag;
	private LinearLayout seekBarMain;
	private LinearLayout seekBarAsid;
	protected boolean m_bChanged;
    private String userIni;
	private IniFile m_iniFileIO;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.new_seekbar);
		// 添加Activity到堆栈
		MyActivity.getInstance().addActivity(this);
        initView();
	}

	// 初始化视图控件
	private void initView() {
		if (this.getIntent().getExtras() != null) {
			Intent intent = this.getIntent();
            this.Flag = intent.getIntExtra("flag", 1);
		}
		DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenWidth = dm.widthPixels;
        this.m_iniFileIO = new IniFile();
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
        String appNewsFile = webRoot
				+ this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
		// SharedPreferences pre = getSharedPreferences(
		// constants.SAVE_LOCALMSGNUM, MODE_PRIVATE);
        this.seekBarMain = (LinearLayout) findViewById(R.id.touch_width);
        this.seekBarAsid = (LinearLayout) findViewById(R.id.seek_bar);
        this.seekBar = (SeekBar) findViewById(R.id.SeekBar_01);
        this.textView1 = (TextView) findViewById(R.id.Seekbar_TextView1);
        this.seekBar1 = (SeekBar) findViewById(R.id.SeekBar02);
        this.textView3 = (TextView) findViewById(R.id.TextView03);
        this.seekBar2 = (SeekBar) findViewById(R.id.SeekBar03);
        this.textView5 = (TextView) findViewById(R.id.TextView05);
		switch (this.Flag) {
		case 1:
            this.seekBarMain.setVisibility(View.GONE);
            this.seekBar1.setVisibility(View.GONE);
            this.textView3.setVisibility(View.GONE);
			// splitratio = (int) (pre.getFloat("SplitRatio", (float) 0.5) *
			// 100);
            this.splitratio = Integer.parseInt(this.m_iniFileIO.getIniString(userIni,
					"APPSHOW", "LeftWidth", "50", (byte) 0));
            this.seekBar.setProgress(this.splitratio);
            this.textView1.setText("左窗口宽度为屏幕宽度的:" + this.splitratio + "%");
            this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() // ����������
			{
				@Override
				public void onProgressChanged(SeekBar arg0, int progress,
						boolean fromUser) {
                    NewSeekBarActivity.this.m_bChanged = true;
                    NewSeekBarActivity.this.splitratio = progress;
                    NewSeekBarActivity.this.textView1.setText("左窗口宽度为屏幕宽度的:" + progress + "%");

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					// textView2.setText("确定停靠在此位置？...");

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					float value = (float) NewSeekBarActivity.this.splitratio / 100;
					System.out.println(value);
					// setSharePerference(constants.SAVE_LOCALMSGNUM, value, 1);
                    NewSeekBarActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
							"LeftWidth", NewSeekBarActivity.this.splitratio + "");
					// textView2.setText("停靠比例:" + value);
					// splitratio = progress/seekBar.getProgress()
					SlidingActivity.mSlidingMenu
							.setBehindOffset((int) (NewSeekBarActivity.this.screenWidth * (1 - value)));
				}
			});
			break;
		case 2:
            this.seekBarMain.setVisibility(View.GONE);
            this.seekBar.setVisibility(View.GONE);
            this.textView1.setVisibility(View.GONE);
			// splitratio1 = (int) (pre.getFloat("SplitRatioRight", (float) 0.5)
			// * 100);
            this.splitratio1 = Integer.parseInt(this.m_iniFileIO.getIniString(
                    userIni, "APPSHOW", "RightWidth", "50", (byte) 0));
            this.seekBar1.setProgress(this.splitratio1);
            this.textView3.setText("右窗口宽度为屏幕宽度的:"
					+ (this.seekBar1.getMax() - this.splitratio1) + "%");
            this.seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
                    NewSeekBarActivity.this.m_bChanged = true;
                    NewSeekBarActivity.this.splitratio1 = progress;
                    NewSeekBarActivity.this.textView3.setText("右窗口宽度为屏幕宽度的:"
							+ (seekBar.getMax() - progress) + "%");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					// textView4.setText("确定停靠在此位置？...");
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					float value = (float) NewSeekBarActivity.this.splitratio1 / 100;
					// setSharePerference(constants.SAVE_LOCALMSGNUM, (value),
					// 2);
                    NewSeekBarActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
							"RightWidth", NewSeekBarActivity.this.splitratio1 + "");
					SlidingActivity.mSlidingMenu
							.setRightMenuOffset((int) (NewSeekBarActivity.this.screenWidth * value));
				}

			});
			break;
		case 3:
            this.seekBarAsid.setVisibility(View.GONE);
			// splitratioMargin = (int) (pre.getFloat("MARGIN", (float) 0.5) *
			// 100);
            this.splitratioMargin = Integer.parseInt(this.m_iniFileIO.getIniString(
                    userIni, "APPSHOW", "PromptHight", "50", (byte) 0));
            this.seekBar2.setProgress(this.splitratioMargin);
            this.textView5.setText("高度为屏幕高度的:" + this.splitratioMargin + "%");
            this.seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
                    NewSeekBarActivity.this.m_bChanged = true;
                    NewSeekBarActivity.this.splitratioMargin = progress;
                    NewSeekBarActivity.this.textView5.setText("高度为屏幕高度的:" + progress + "%");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					// textView6.setText("确定在此位置？...");
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					// float value = (float) splitratioMargin / 100;
					// setSharePerference(constants.SAVE_LOCALMSGNUM, value, 3);
                    NewSeekBarActivity.this.m_iniFileIO.writeIniString(userIni, "APPSHOW",
							"PromptHight", NewSeekBarActivity.this.splitratioMargin + "");
					Intent intent = new Intent();
					intent.setAction("loadView"
							+ getString(R.string.about_title));
					intent.putExtra("flag", 6);
					intent.putExtra("value", NewSeekBarActivity.this.splitratioMargin);
					intent.putExtra("author", 1);
                    NewSeekBarActivity.this.sendBroadcast(intent);
				}

			});
			break;
		}
        this.mClose = (ImageButton) this.findViewById(R.id.feedback_close_button);
        this.mClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (NewSeekBarActivity.this.m_bChanged == true) {
					Intent intent = new Intent();
					switch (NewSeekBarActivity.this.Flag) {
					case 1:
						intent.setAction("SetMain"
								+ getString(R.string.about_title));
						intent.putExtra("progress", NewSeekBarActivity.this.splitratio);
						intent.putExtra("flag", 1);
                        NewSeekBarActivity.this.sendBroadcast(intent);
						break;
					case 2:
						intent.setAction("SetMain"
								+ getString(R.string.about_title));
						intent.putExtra("progress", NewSeekBarActivity.this.splitratio1);
						intent.putExtra("flag", 2);
                        NewSeekBarActivity.this.sendBroadcast(intent);
						break;
					case 3:
						intent.setAction("SetMain"
								+ getString(R.string.about_title));
						intent.putExtra("progress", NewSeekBarActivity.this.splitratioMargin);
						intent.putExtra("flag", 3);
                        NewSeekBarActivity.this.sendBroadcast(intent);
						break;
					}
				}
                NewSeekBarActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.m_bChanged == true) {
				Intent intent = new Intent();
				switch (this.Flag) {
				case 1:
					intent.setAction("SetMain"
							+ getString(R.string.about_title));
					intent.putExtra("progress", this.splitratio);
					intent.putExtra("flag", 1);
                    this.sendBroadcast(intent);
					break;
				case 2:
					intent.setAction("SetMain"
							+ getString(R.string.about_title));
					intent.putExtra("progress", this.splitratio1);
					intent.putExtra("flag", 2);
                    this.sendBroadcast(intent);
					break;
				case 3:
					intent.setAction("SetMain"
							+ getString(R.string.about_title));
					intent.putExtra("progress", this.splitratioMargin);
					intent.putExtra("flag", 3);
                    this.sendBroadcast(intent);
					break;
				}
			}
            this.finish();
		}
		return true;
	}
}