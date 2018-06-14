package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.backup.BackAppActivity;
import com.tbs.tbsmis.backup.BackDataActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MyBackupActivity extends Activity implements View.OnClickListener {
	private ImageView finishBtn;
	private ImageView downBtn;
	private TextView title;
    private TextView backup_introduce;
	private RelativeLayout back_app;
	private RelativeLayout back_data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.my_back_layout);
		MyActivity.getInstance().addActivity(this);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        backup_introduce= (TextView) this.findViewById(R.id.backup_introduce);
        this.back_app = (RelativeLayout) this.findViewById(R.id.back_app);
        this.back_data = (RelativeLayout) this.findViewById(R.id.back_data);

        this.title.setText("我的备份");
        if(getIntent().getExtras() != null){
            Intent intent = this.getIntent();
            if(intent.getBooleanExtra("systemSet",false)){
                back_data.setVisibility(View.GONE);
                backup_introduce.setText("备份应用到应用商城，供其他用户下载");
            }else{
                back_app.setVisibility(View.GONE);
                backup_introduce.setText("备份应用或者数据到个人备份目录，以备不时之需");
            }
        }else{
            //back_data.setVisibility(View.GONE);
            backup_introduce.setText("备份应用到应用商城，供其他用户下载");
        }

        this.downBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.back_app.setOnClickListener(this);
        this.back_data.setOnClickListener(this);
        this.doDeploy();
	}

	private boolean doDeploy() {
		String webRoot = UIHelper.getShareperference(this,
				constants.SAVE_INFORMATION, "Path", "");
		if (webRoot.endsWith("/") == false) {
			webRoot += "/";
		}
		String doPath = webRoot
				+ "backup";
		File webRootFile = new File(doPath);
		String launchState = UIHelper.getShareperference(this,
				constants.SHARED_PREFERENCE_FIRST_LAUNCH, "launchState",
				"1.0.0");
		if ((webRootFile.exists() && webRootFile.isDirectory()) == false
				|| !launchState.equals(this.getVersionName())) {
			webRootFile.mkdirs();
		} else {
			return true;
		}
		String webRootTbk = webRoot + "backup.tbk";
		try {
			InputStream is = this.getBaseContext().getAssets().open(
					"config/backup.tbk");
			OutputStream os = new FileOutputStream(webRootTbk);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int resoult = JTbszlib.UnZipFile(webRootTbk, doPath, 1, "");
		if (0 != resoult) {
			return false;
		}
        this.delZipFile(webRootTbk);
		return true;
	}

	// 获取程序的版本号
	private String getVersionName() {
		PackageManager packageManager = this.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packInfo.versionName;
	}
	protected void delZipFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}
	@Override
	public void onClick(View v) { // TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.more_btn:
            this.finish();
            this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
			break;
		case R.id.back_app:
			intent = new Intent();
			intent.setClass(this, BackAppActivity.class);
            this.startActivity(intent);
			break;
		case R.id.back_data:
			intent = new Intent();
			intent.setClass(this, BackDataActivity.class);
            this.startActivity(intent);
			break;
		}
	}
}