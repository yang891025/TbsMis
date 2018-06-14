package com.tbs.tbsmis.check;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 路径选择弹窗
 * 
 * @author yeguozhong@yeah.net 修改 yzt
 * 
 */
public class AppEditDialog extends Dialog {

	private Button cancleBtn;
	private Button setBtn;
	private final String FileName;
	private final String AppName;
	private final String ResId;
	private final Context context;
	private EditText set_filename;
	private EditText set_resname;
	private EditText set_resid;
	private TextView edit_title;
	private final int flag;
	private IniFile IniFile;
	private String cutName;

	public AppEditDialog(Context context, String FileName, String AppName,
			String ResId, int flag) {
		super(context);
		this.FileName = FileName;
		this.AppName = AppName;
		this.ResId = ResId;
		this.context = context;
		this.flag = flag;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.app_edit);
        this.setCanceledOnTouchOutside(false);
        this.init();
	}

	private void init() {
		// TODO Auto-generated method stub
        this.set_filename = (EditText) this.findViewById(R.id.set_filename);
        this.set_resname = (EditText) this.findViewById(R.id.set_resname);
        this.set_resid = (EditText) this.findViewById(R.id.set_resid);
        this.edit_title = (TextView) this.findViewById(R.id.edit_title);
        this.cancleBtn = (Button) this.findViewById(R.id.cancleBtn);
        this.setBtn = (Button) this.findViewById(R.id.setBtn);
        this.cutName = FileUtils.getFileNameNoFormat(this.FileName);
        this.set_filename.setText(this.cutName);
        this.set_resname.setText(this.AppName);
        this.set_resid.setText(this.ResId);
        this.IniFile = new IniFile();
		if (this.flag == 1) {
            this.edit_title.setText("修改应用");
		} else if (this.flag == 0) {
            this.edit_title.setText("复制应用");
		}
		// 取消
        this.cancleBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                AppEditDialog.this.dismiss();
			}
		});
		// 确定
        this.setBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String filename = AppEditDialog.this.set_filename.getText().toString();
				if (filename.isEmpty()) {
					Toast.makeText(AppEditDialog.this.context, "应用配置不可为空", Toast.LENGTH_SHORT)
							.show();
                    AppEditDialog.this.set_filename.setFocusableInTouchMode(true);
					return;
				}
				String resname = AppEditDialog.this.set_resname.getText().toString();
				if (resname.isEmpty()) {
					Toast.makeText(AppEditDialog.this.context, "应用名称不可为空", Toast.LENGTH_SHORT)
							.show();
                    AppEditDialog.this.set_resname.setFocusableInTouchMode(true);
					return;
				}
				String resid = AppEditDialog.this.set_resid.getText().toString();
				if (resid.isEmpty()) {
					Toast.makeText(AppEditDialog.this.context, "资源代号不可为空", Toast.LENGTH_SHORT)
							.show();
                    AppEditDialog.this.set_resid.setFocusableInTouchMode(true);
					return;
				}
				String path = FileUtils.getPath(AppEditDialog.this.FileName);
				if (path.endsWith("/") == false) {
					path += "/";
				}
				if (AppEditDialog.this.flag == 1) {
                    AppEditDialog.this.IniFile.writeIniString(path + filename + ".ini", "TBSAPP",
							"resname", resid);
                    AppEditDialog.this.IniFile.writeIniString(path + filename + ".ini", "TBSAPP",
							"AppName", resname);
				} else if (AppEditDialog.this.flag == 0) {
					if (AppEditDialog.this.cutName.equals(resname)) {
						Toast.makeText(AppEditDialog.this.context, "应用配置为改变", Toast.LENGTH_SHORT)
								.show();
                        AppEditDialog.this.set_filename.setFocusableInTouchMode(true);
						return;
					} else {
						File srcFile = new File(AppEditDialog.this.FileName);
						File destFile = new File(path + filename + ".ini");
						try {
							FileUtils.copyFileTo(srcFile, destFile);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        AppEditDialog.this.IniFile.writeIniString(path + filename + ".ini",
								"TBSAPP", "resname", resid);
                        AppEditDialog.this.IniFile.writeIniString(path + filename + ".ini",
								"TBSAPP", "AppName", resname);
					}
				}
				Intent intent = new Intent();
				intent.setAction("app_manager"+ AppEditDialog.this.context.getString(R.string.about_title));
                AppEditDialog.this.context.sendBroadcast(intent);
                AppEditDialog.this.dismiss();
			}
		});
	}
}