package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

public class AboutActivity extends Activity {
	private TextView versionName;
	private ImageView finishBtn;
	private ImageView downBtn;
	private RelativeLayout feedbackBtn;
	private RelativeLayout OperationGuideBtn;
	private RelativeLayout updateBtn;
	private RelativeLayout about_more;
    private RelativeLayout system_set;
	private IniFile m_iniFileIO;
	private ImageView picture;
    private String userIni;

    private TextView copyright_content_view;
    private TextView technique_provide_content_view;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.about_dialog);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	@Override
	protected void onResume() {
		super.onResume();
        initPath();
		int setup = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Software",
				"aboutapp_show_in_about", "1", (byte) 0));
		if (setup == 0) {
            updateBtn.setVisibility(View.GONE);
            about_more.setVisibility(View.GONE);
		} else {
            updateBtn.setVisibility(View.VISIBLE);
            about_more.setVisibility(View.VISIBLE);
		}
		setup = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Software",
				"introductionSetting", "0", (byte) 0));
		if (setup == 0) {
            this.feedbackBtn.setVisibility(View.GONE);
		} else {
            this.feedbackBtn.setVisibility(View.VISIBLE);
		}
		setup = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni, "Software",
				"operationGuideSetting", "0", (byte) 0));
		if (setup == 0) {
            this.OperationGuideBtn.setVisibility(View.GONE);
		} else {
            this.OperationGuideBtn.setVisibility(View.VISIBLE);
		}
        String copyright_content = m_iniFileIO.getIniString(userIni, "System",
                "copyright_content", getString(R.string.copyright_content), (byte) 0);
        copyright_content_view.setText(copyright_content);
        String technique_provide_content = m_iniFileIO.getIniString(userIni, "System",
                "technique_provide_content", getString(R.string.technique_provide_content), (byte) 0);
        technique_provide_content_view.setText(technique_provide_content);
        String about_title = m_iniFileIO.getIniString(userIni, "System",
                "about_title", getString(R.string.about_title), (byte) 0);
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), 0);
            // mVersion = (TextView) findViewById(R.id.about_version);
            this.versionName.setText(about_title + "  版本:"
                    + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
	}

	private void init() {
        this.versionName = (TextView) this.findViewById(R.id.version_content);
        this.finishBtn = (ImageView) this.findViewById(R.id.basic_back_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.basic_down_btn);
        about_more = (RelativeLayout) this.findViewById(R.id.about_more);

        OperationGuideBtn = (RelativeLayout) this.findViewById(R.id.new_help);
        this.feedbackBtn = (RelativeLayout) this.findViewById(R.id.system_introducte);
        this.updateBtn = (RelativeLayout) this.findViewById(R.id.app_about);
        this.system_set = (RelativeLayout) this.findViewById(R.id.system_set);
        this.picture = (ImageView) this.findViewById(R.id.picture);
        copyright_content_view = (TextView) this.findViewById(R.id.copyright_content);
        technique_provide_content_view = (TextView) this.findViewById(R.id.technique_provide_content);
        this.system_set.setVisibility(View.GONE);
        this.finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AboutActivity.this.finish();
                AboutActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
        this.downBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AboutActivity.this.finish();
                AboutActivity.this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			}
		});
        this.feedbackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AboutActivity.this, SystemIntrActivity.class);
                AboutActivity.this.startActivity(intent);
			}
		});
        this.OperationGuideBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AboutActivity.this, OperationGuideActivity.class);
                AboutActivity.this.startActivity(intent);
			}
		});
        this.updateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AboutActivity.this, AboutAppActivity.class);
                AboutActivity.this.startActivity(intent);
			}
		});
        this.system_set.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, SystemSetupActivityNew.class);
                AboutActivity.this.startActivity(intent);

			}
		});
        this.picture.setOnClickListener(new View.OnClickListener() {
			private int state;

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				if (this.state < 2) {
                    this.state = this.state + 1;
				} else if (this.state == 2) {
                    about_more.setVisibility(View.VISIBLE);
                    AboutActivity.this.system_set.setVisibility(View.VISIBLE);
					Toast.makeText(AboutActivity.this, "系统设置已打开",
							Toast.LENGTH_SHORT).show();
                    AboutActivity.this.picture.setEnabled(false);
				}
			}
		});
		//
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
		// 缁撴潫Activity&浠庡爢鏍堜腑绉婚櫎
		MyActivity.getInstance().finishActivity(this);
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
