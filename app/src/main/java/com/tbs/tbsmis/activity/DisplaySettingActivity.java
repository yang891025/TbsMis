package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.cbs.CBSInterpret;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

public class DisplaySettingActivity extends Activity implements View.OnClickListener {

	protected static final String TAG = "MySetupActivity";
	private ImageView leftBtn;
	private TextView title;
	private ImageView RightBtn;
	private IniFile m_iniFileIO;
	private String webRoot;
	private String appFile;
	private RelativeLayout default_color;
	private RelativeLayout green_color;
	private CheckBox default_checkbox;
	private CheckBox green_checkbox;
	private RelativeLayout deep_color;
	private CheckBox deep_checkbox;
	private RelativeLayout purple_color;
	private CheckBox purple_checkbox;
	private WebView webview;
	private SeekBar seekBar;
	private TextView show_zoom;
	private TextView show_title_textsize;
	private TextView show_body_textsize;
	private Button title_add_size;
	private Button title_down_size;
	private Button body_add_size;
	private Button body_down_size;
	private WebSettings Settings;
	private CBSInterpret mInterpret;
	private String WebIniFile;
	private int TitleFontSize;
	private int TextSize;
	private int TextMaxSize;
	private int TextMiniSize;
	private TextView default_textcolor;
	private TextView green_textcolor;
	private TextView purple_textcolor;
	private TextView deep_textcolor;
	private TextView text_font;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.browse_setting_activity);
        this.initPath();
        this.titleView();
        this.initView();
	}

	private void initPath() {
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
        this.appFile = this.webRoot
				+ this.m_iniFileIO.getIniString(this.WebIniFile, "TBSWeb", "IniName",
						constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
	}

	private void titleView() {
        this.leftBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.RightBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.leftBtn.setOnClickListener(this);
        this.RightBtn.setOnClickListener(this);
        this.title.setText("显示设置");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
        this.default_color = (RelativeLayout) this.findViewById(R.id.default_color);
        this.default_checkbox = (CheckBox) this.findViewById(R.id.default_checkbox);
        this.green_color = (RelativeLayout) this.findViewById(R.id.green_color);
        this.green_checkbox = (CheckBox) this.findViewById(R.id.green_checkbox);
        this.deep_color = (RelativeLayout) this.findViewById(R.id.deep_color);
        this.deep_checkbox = (CheckBox) this.findViewById(R.id.deep_checkbox);
        this.purple_color = (RelativeLayout) this.findViewById(R.id.purple_color);
        this.purple_checkbox = (CheckBox) this.findViewById(R.id.purple_checkbox);

        this.title_add_size = (Button) this.findViewById(R.id.title_add_size);
        this.title_down_size = (Button) this.findViewById(R.id.title_down_size);
        this.body_add_size = (Button) this.findViewById(R.id.body_add_size);
        this.body_down_size = (Button) this.findViewById(R.id.body_down_size);
        this.title_add_size.setOnClickListener(this);
        this.title_down_size.setOnClickListener(this);
        this.body_add_size.setOnClickListener(this);
        this.body_down_size.setOnClickListener(this);

        this.show_title_textsize = (TextView) this.findViewById(R.id.show_title_textsize);
        this.show_body_textsize = (TextView) this.findViewById(R.id.show_body_textsize);
        this.show_zoom = (TextView) this.findViewById(R.id.show_textzoom);
        this.text_font = (TextView) this.findViewById(R.id.text_font);

        this.default_textcolor = (TextView) this.findViewById(R.id.default_textcolor);
        this.green_textcolor = (TextView) this.findViewById(R.id.green_textcolor);
        this.deep_textcolor = (TextView) this.findViewById(R.id.deep_textcolor);
        this.purple_textcolor = (TextView) this.findViewById(R.id.purple_textcolor);

        this.show_title_textsize.setText(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TitleFontSize", "16", (byte) 0));
        this.show_body_textsize.setText(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TextSize", "16", (byte) 0));
        this.text_font.setText(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
				"TextFont", "宋体", (byte) 0));
        this.getTextSize();
		if (this.TitleFontSize == this.TextMiniSize) {
            this.title_down_size.setEnabled(false);
		}
		if (this.TitleFontSize == this.TextMaxSize) {
            this.title_add_size.setEnabled(false);
		}
		if (this.TextSize == this.TextMaxSize) {
            this.body_add_size.setEnabled(false);
		}
		if (this.TextSize == this.TextMiniSize) {
            this.body_down_size.setEnabled(false);
		}

        this.seekBar = (SeekBar) this.findViewById(R.id.textsize_bar);

        this.webview = (WebView) this.findViewById(R.id.show_webview);
        this.Settings = this.webview.getSettings();
        this.Settings.setDefaultTextEncodingName("utf-8");
        this.Settings.setSupportZoom(true);
        this.Settings.setBuiltInZoomControls(true);
        this.Settings.setJavaScriptEnabled(true);
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.Settings.setDisplayZoomControls(false);
		}

        this.Settings.setSupportMultipleWindows(true);
        this.Settings.setDomStorageEnabled(true);
        this.default_color.setOnClickListener(this);
        this.green_color.setOnClickListener(this);
        this.purple_color.setOnClickListener(this);
        this.deep_color.setOnClickListener(this);
		switch (Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "BackColor", "0", (byte) 0))) {
		case 0:
            this.default_checkbox.setChecked(true);
			break;
		case 1:
            this.green_checkbox.setChecked(true);
			break;
		case 2:
            this.deep_checkbox.setChecked(true);
			break;
		case 3:
            this.purple_checkbox.setChecked(true);
			break;
		}
        this.webview.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"BackColorValue", "#f6f6f6", (byte) 0)));
        this.doInterpret();
		// 3.0版本才有setDisplayZoomControls的功能
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            this.Settings.setTextZoom(Integer.parseInt(this.m_iniFileIO.getIniString(
                    this.appFile, "BASIC_SETUP", "TextZoom", "100", (byte) 0)));
		}

        this.seekBar.setProgress(Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TextZoom", "100", (byte) 0)) - 50);
        this.show_zoom.setText(Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TextZoom", "100", (byte) 0)) + "%");

        this.default_textcolor.setTextColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"TextColor0", "#000000", (byte) 0)));
        this.green_textcolor.setTextColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"TextColor1", "#ffffff", (byte) 0)));
        this.deep_textcolor.setTextColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"TextColor2", "#0c6ce2", (byte) 0)));
        this.purple_textcolor.setTextColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"TextColor3", "#000000", (byte) 0)));

        this.default_color.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"BackColor0", "#f6f6f6", (byte) 0)));
        this.green_color.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"BackColor1", "#6a970a", (byte) 0)));
        this.deep_color.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"BackColor2", "#faaa5f", (byte) 0)));
        this.purple_color.setBackgroundColor(Color
				.parseColor(this.m_iniFileIO.getIniString(this.appFile, "BASIC_SETUP",
						"BackColor3", "#a45fb8", (byte) 0)));

        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() // ����������
		{
			private int size;

			@Override
			public void onProgressChanged(SeekBar arg0, int progress,
					boolean fromUser) {
                this.size = progress;
				if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
                    DisplaySettingActivity.this.Settings.setTextZoom(progress + 50);
				}
                DisplaySettingActivity.this.show_zoom.setText((progress + 50) + "%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				// textView2.setText("确定停靠在此位置？...");
				// Settings.setTextZoom(size + 50);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
                DisplaySettingActivity.this.m_iniFileIO.writeIniString(DisplaySettingActivity.this.appFile, "BASIC_SETUP", "TextZoom",
						(this.size + 50) + "");
				if (VERSION.SDK_INT <= VERSION_CODES.GINGERBREAD_MR1) {
					Toast.makeText(DisplaySettingActivity.this, "系统版本不支持",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void getTextSize() {
		// TODO Auto-generated method stub
        this.TitleFontSize = Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TitleFontSize", "16", (byte) 0));
        this.TextSize = Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TextSize", "16", (byte) 0));
        this.TextMaxSize = Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TextMaxSize", "32", (byte) 0));
        this.TextMiniSize = Integer.parseInt(this.m_iniFileIO.getIniString(this.appFile,
				"BASIC_SETUP", "TextMiniSize", "8", (byte) 0));
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
        this.initPath();
		switch (arg0.getId()) {
		case R.id.more_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.finish_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.title_add_size:
            this.getTextSize();
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP", "TitleFontSize",
					(this.TitleFontSize + 2) + "");
            this.show_title_textsize.setText((this.TitleFontSize + 2) + "");
			if (this.TextMaxSize - this.TitleFontSize <= 2) {
                this.title_add_size.setEnabled(false);
			} else {
                this.title_add_size.setEnabled(true);
                this.title_down_size.setEnabled(true);
			}
            this.doInterpret();
			break;
		case R.id.title_down_size:
            this.getTextSize();
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP", "TitleFontSize",
					(this.TitleFontSize - 2) + "");
            this.show_title_textsize.setText((this.TitleFontSize - 2) + "");
			if (this.TitleFontSize - this.TextMiniSize <= 2) {
                this.title_down_size.setEnabled(false);
			} else {
                this.title_add_size.setEnabled(true);
                this.title_down_size.setEnabled(true);
			}
            this.doInterpret();
			break;
		case R.id.body_add_size:
            this.getTextSize();
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP", "TextSize",
					(this.TextSize + 2) + "");
            this.show_body_textsize.setText((this.TextSize + 2) + "");
			if (this.TextMaxSize - this.TextSize <= 2) {
                this.body_add_size.setEnabled(false);
			} else {
                this.body_add_size.setEnabled(true);
                this.body_down_size.setEnabled(true);
			}
            this.doInterpret();
			break;
		case R.id.body_down_size:
            this.getTextSize();
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP", "TextSize",
					(this.TextSize - 2) + "");
            this.show_body_textsize.setText((this.TextSize - 2) + "");
			if (this.TextSize - this.TextMiniSize <= 2) {
                this.body_down_size.setEnabled(false);
			} else {
                this.body_add_size.setEnabled(true);
                this.body_down_size.setEnabled(true);
			}
            this.doInterpret();
			break;
		case R.id.default_color:
            this.m_iniFileIO
					.writeIniString(this.appFile, "BASIC_SETUP", "BackColor", "0");
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"BackColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColor0", "#f6f6f6", (byte) 0));
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"TextColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "TextColor0", "#000000", (byte) 0));
            this.webview.setBackgroundColor(Color
					.parseColor(this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColorValue", "#f6f6f6",
							(byte) 0)));
            this.doInterpret();
            this.default_checkbox.setChecked(true);
            this.green_checkbox.setChecked(false);
            this.deep_checkbox.setChecked(false);
            this.purple_checkbox.setChecked(false);
			break;
		case R.id.green_color:
            this.m_iniFileIO
					.writeIniString(this.appFile, "BASIC_SETUP", "BackColor", "1");
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"BackColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColor1", "#6a970a", (byte) 0));
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"TextColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "TextColor1", "#ffffff", (byte) 0));
            this.webview.setBackgroundColor(Color
					.parseColor(this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColorValue", "#f6f6f6",
							(byte) 0)));
            this.doInterpret();
            this.default_checkbox.setChecked(false);
            this.green_checkbox.setChecked(true);
            this.deep_checkbox.setChecked(false);
            this.purple_checkbox.setChecked(false);
			break;
		case R.id.deep_color:
            this.m_iniFileIO
					.writeIniString(this.appFile, "BASIC_SETUP", "BackColor", "2");
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"BackColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColor2", "#faaa5f", (byte) 0));
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"TextColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "TextColor2", "#0c6ce2", (byte) 0));
            this.webview.setBackgroundColor(Color
					.parseColor(this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColorValue", "#f6f6f6",
							(byte) 0)));
            this.doInterpret();
            this.default_checkbox.setChecked(false);
            this.green_checkbox.setChecked(false);
            this.deep_checkbox.setChecked(true);
            this.purple_checkbox.setChecked(false);
			break;
		case R.id.purple_color:
            this.m_iniFileIO
					.writeIniString(this.appFile, "BASIC_SETUP", "BackColor", "3");
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"BackColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColor3", "#a45fb8", (byte) 0));
            this.m_iniFileIO.writeIniString(this.appFile, "BASIC_SETUP",
					"TextColorValue", this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "TextColor3", "#000000", (byte) 0));
            this.webview.setBackgroundColor(Color
					.parseColor(this.m_iniFileIO.getIniString(this.appFile,
							"BASIC_SETUP", "BackColorValue", "#f6f6f6",
							(byte) 0)));
            this.doInterpret();
            this.default_checkbox.setChecked(false);
            this.green_checkbox.setChecked(false);
            this.deep_checkbox.setChecked(false);
            this.purple_checkbox.setChecked(true);
			break;
		}
	}

	private void doInterpret() {
		// TODO Auto-generated method stub
		if (!StringUtils.isEmpty(UIHelper.getShareperference(this,
				constants.SAVE_LOCALMSGNUM, "interpretFileName", ""))) {
			FileUtils.deleteFileWithPath(UIHelper.getShareperference(this,
					constants.SAVE_LOCALMSGNUM, "interpretFileName", ""));
		}
        this.mInterpret = new CBSInterpret();
        this.mInterpret.initGlobal(this.WebIniFile, this.webRoot);
		String interpretFile = this.mInterpret.Interpret(
                this.m_iniFileIO.getIniString(this.appFile, "APPABOUT", "WebPreView",
						"/show.cbs", (byte) 0)
						+ "?inifile="
						+ this.appFile, "GET", "", null, 0);
		UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM,
				"interpretFileName", interpretFile);
        this.webview.loadUrl("file://" + interpretFile);
	}
}