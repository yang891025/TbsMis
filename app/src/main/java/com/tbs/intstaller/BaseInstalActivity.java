package com.tbs.intstaller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.cbs.JTbsPDFOE;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.activity.InitializeToolbarActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.IOException;

public class BaseInstalActivity extends Activity {
	private IniFile m_iniFileIO;
	private String appUserFile;
	private ProgressDialog Prodialog;
	protected boolean unzip = true;
	private String appName;
	private String FilePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		// LayoutInflater lay = (LayoutInflater) this
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// View view = lay.inflate(R.layout.left_frame, null);
		// setContentView(view);
        this.FilePath = this.getIntent().getData().toString();
        this.FilePath = this.FilePath.substring(7, this.FilePath.length());
		String FileName = FileUtils.getFileName(this.FilePath);
        this.appName = FileUtils.geURLRelativeName(this.FilePath);
		//System.out.println("appName = "+appName);
		new Builder(this)
				.setTitle("安装应用包")
				.setMessage(FileName)
				.setPositiveButton("确定",// 提示框的两个按钮
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
                                BaseInstalActivity.this.showModifyDialog(BaseInstalActivity.this.FilePath, BaseInstalActivity.this.appName);
							}
						})
				.setNegativeButton("取消",
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
                                finish();
							}
						}).create().show();
	}

	private void initPath() {
		// TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
		String configPath = this.getApplicationContext().getFilesDir()
				.getParentFile().getAbsolutePath();
		if (configPath.endsWith("/") == false) {
			configPath = configPath + "/";
		}
        this.appUserFile = configPath + constants.USER_CONFIG_FILE_NAME;
	}

	@SuppressLint("ShowToast")
	@SuppressWarnings("deprecation")
	private void showModifyDialog(String FilePath, String appName) {
		if (appName.equalsIgnoreCase("/audio")
				|| appName.equalsIgnoreCase("/video")
				|| appName.equalsIgnoreCase("/picture")
				|| appName.equalsIgnoreCase("/news")
				|| appName.equalsIgnoreCase("/pdf")) {
			Toast.makeText(this, "请检查是否为应用包", Toast.LENGTH_SHORT).show();
            finish();
		} else {
            this.Prodialog = new ProgressDialog(this);
            this.Prodialog.setTitle("安装应用包");
            this.Prodialog.setMessage("正在安装，请稍候...");
            this.Prodialog.setIndeterminate(false);
            this.Prodialog.setCanceledOnTouchOutside(false);
            this.Prodialog.setButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method
                    BaseInstalActivity.this.unzip = false;
					dialog.dismiss();
				}
			});
			BaseInstalActivity.MyAsyncTask task = new BaseInstalActivity.MyAsyncTask(this, FilePath, appName);
			task.execute();
		}
	}

	class MyAsyncTask extends AsyncTask<Integer, Integer, String> {
		private final Context context;
		private String pathString;
		private final String appPath;

		public MyAsyncTask(Context c, String pathString, String appPath) {
            context = c;
			this.appPath = appPath;
			this.pathString = pathString;

		}

		/**
		 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
		 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
		 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
		 */
		@Override
		protected String doInBackground(Integer... params) {
			String webRoot = UIHelper.getStoragePath(context);
			String end;
			if (FileUtils.getFileFormat(this.pathString).equalsIgnoreCase("tbk")) {
				webRoot += constants.SD_CARD_TBSSOFT_PATH3 + this.appPath;
				JTbszlib.UnZipFile(this.pathString, webRoot, 1, "");
                BaseInstalActivity.this.initPath();
                String iniPath = BaseInstalActivity.this.m_iniFileIO.getIniString(webRoot
                                + File.separator + constants.WEB_CONFIG_FILE_NAME,
                        "TBSWeb", "IniName", "", (byte) 0);
				if (!StringUtils.isEmpty(iniPath)) {
                    if(!StringUtils.isEmpty(BaseInstalActivity.this.m_iniFileIO.getIniString(webRoot
                                    + File.separator + iniPath,
                            "pdfpath", "url", "", (byte) 0)))
                    JTbsPDFOE.AddLocalMachine(webRoot);
					if (BaseInstalActivity.this.AddApplication(webRoot, this.appPath)) {
						end = "success";
					} else {
						end = "exits";
					}
				} else {
                    JTbsPDFOE.AddLocalMachine(webRoot);
					end = "false";
				}
                this.pathString = FileUtils.getFileName(this.pathString);
				String endTime = this.pathString.substring(
                        this.pathString.indexOf("-") + 1, this.pathString.indexOf("."));

                BaseInstalActivity.this.m_iniFileIO.writeIniString(BaseInstalActivity.this.appUserFile, "data", this.appPath,
						endTime);
				return end;
			} else {
				webRoot += constants.SD_CARD_TBSSOFT_PATH3 + "/"
						+ FileUtils.getFileRelativepath(this.pathString);
				webRoot += FileUtils.getFileName(this.pathString);
				File toPath = new File(webRoot);
				File MovePath = new File(this.pathString);
				if (!toPath.getParentFile().exists()) {// 如果文件父目录不存在
					toPath.getParentFile().mkdirs();
				}
				try {
					if (!toPath.exists()) {// 如果文件不存在
						toPath.createNewFile();// 创建新文件
					} else {
						FileUtils.deleteFileWithPath(webRoot);
						toPath.createNewFile();// 创建新文件
					}
					FileUtils.copyFileTo(MovePath, toPath);
					FileUtils.deleteFileWithPath(this.pathString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "file";
			}
		}

		/**
		 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
		 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
		 */
		@Override
		protected void onPostExecute(String result) {
            BaseInstalActivity.this.Prodialog.dismiss();
			if (result.equals("success")) {
				Toast.makeText(this.context, "已添加到我的应用", Toast.LENGTH_LONG).show();
                Message msg = new Message();
                Bundle b = new Bundle();
                b.putString("path", this.appPath);
                msg.what = 1;
                msg.setData(b);
                BaseInstalActivity.this.handler.sendMessage(msg);
                return;
			} else if (result.equals("exits")) {
				Toast.makeText(this.context, "应用已更新", Toast.LENGTH_LONG).show();
			} else if (result.equals("false")) {
				Toast.makeText(this.context, "应用格式不正确，无法添加", Toast.LENGTH_LONG)
						.show();
			} else if (result.equals("file")) {
				Toast.makeText(this.context, "文件已更新", Toast.LENGTH_LONG).show();
			} else if (result.equals("data")) {
				Toast.makeText(this.context, "数据已更新", Toast.LENGTH_LONG).show();
			}

			if (UIHelper.getShareperference(BaseInstalActivity.this,
					Constants.SHARED_PREFERENCE_NAME, "appOn", 0) == 0
					&& !result.equals("false")) {
				Intent intent = new Intent(BaseInstalActivity.this,
						InitializeToolbarActivity.class);
                BaseInstalActivity.this.startActivity(intent);
			}
            finish();

		}

		// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
		@Override
		protected void onPreExecute() {
            BaseInstalActivity.this.Prodialog.show();
		}

		/**
		 * 这里的Intege参数对应AsyncTask中的第二个参数
		 * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
		 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	protected boolean AddApplication(String path,String Relativepath) {
		// TODO Auto-generated method stub
        if(Relativepath.startsWith("/"))
            Relativepath = Relativepath.substring(1);
		String inifle = this.m_iniFileIO.getIniString(path + File.separator
                        + constants.WEB_CONFIG_FILE_NAME, "TBSWeb", "IniName", "",
                (byte) 0);
		String title = this.m_iniFileIO.getIniString(path + File.separator + inifle,
				"TBSAPP", "AppName", "", (byte) 0);
		String resTitle = this.m_iniFileIO.getIniString(path + File.separator
				+ inifle, "TBSAPP", "AppCode", "tbs-mis", (byte) 0);
		String AppCategory = this.m_iniFileIO.getIniString(path + File.separator
						+ inifle, "TBSAPP", "AppCategory", "",
				(byte) 0);
        String AppVersion = this.m_iniFileIO.getIniString(path + File.separator
                        + inifle, "TBSAPP", "AppVersion", "1.0",
                (byte) 0);
		int resnum = Integer.parseInt(this.m_iniFileIO.getIniString(this.appUserFile,
				"resource", "resnum", "0", (byte) 0));
		int groupId = 0;
		for (int i = 1; i <= resnum; i++) {
			int groupresnum = Integer.parseInt(this.m_iniFileIO.getIniString(
                    this.appUserFile, "group" + i, "resnum", "0", (byte) 0));
			String resname = this.m_iniFileIO.getIniString(this.appUserFile, "resource",
					"resname" + i, "0", (byte) 0);
			if (resname.equals(AppCategory)||resname.equals("我的下载")) {
				groupId = i;
			}
			for (int j = 1; j <= groupresnum; j++) {
				if (resTitle.equals(this.m_iniFileIO.getIniString(this.appUserFile,
						"group" + i, "res" + j, "", (byte) 0))) {
                    this.m_iniFileIO.writeIniString(this.appUserFile, resTitle, "version", AppVersion);
					return false;
				}
			}
		}
		if (groupId == 0) {
            this.AddCategory("我的下载", "5");
			groupId = resnum + 1;
		}
		int groupresnum = Integer.parseInt(this.m_iniFileIO.getIniString(this.appUserFile,
				"group" + groupId, "resnum", "0", (byte) 0));
        this.m_iniFileIO.writeIniString(this.appUserFile, "group" + groupId, "resnum",
				(groupresnum + 1) + "");
        this.m_iniFileIO.writeIniString(this.appUserFile, "group" + groupId, "res"
				+ (groupresnum + 1), resTitle);
        this.m_iniFileIO.writeIniString(this.appUserFile, resTitle, "title", title);
        this.m_iniFileIO.writeIniString(this.appUserFile, resTitle, "instdir", Relativepath);
        this.m_iniFileIO.writeIniString(this.appUserFile, resTitle, "storePath", AppCategory + "/" + Relativepath);
        this.m_iniFileIO.writeIniString(this.appUserFile, resTitle, "version", AppVersion);
		return true;
	}

	protected void AddCategory(String categoryName, String categoryColor) {
		// TODO Auto-generated method stub
		int resnum = Integer.parseInt(this.m_iniFileIO.getIniString(this.appUserFile,
                "resource", "resnum", "0", (byte) 0));
		resnum = resnum + 1;
        this.m_iniFileIO.writeIniString(this.appUserFile, "resource", "resnum", resnum
				+ "");
        this.m_iniFileIO.writeIniString(this.appUserFile, "resource", "resid" + resnum,
				"group" + resnum);
        this.m_iniFileIO.writeIniString(this.appUserFile, "resource", "resname" + resnum,
				categoryName);
        this.m_iniFileIO.writeIniString(this.appUserFile, "resource", "resicon" + resnum,
				"");
        this.m_iniFileIO.writeIniString(this.appUserFile, "resource", "resbkcolor"
				+ resnum, categoryColor);
	}

    public void setSortlink(final String Path){
        Builder builder = new Builder(this);
        builder.setTitle("设置快捷方式")
                .setMessage("是否为该应用添加快捷方式？").setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String webRoot = UIHelper.getStoragePath(BaseInstalActivity.this);
                webRoot += constants.SD_CARD_TBSSOFT_PATH3 + Path;
               // IniFile iniFile = new IniFile();
                String inifle = BaseInstalActivity.this.m_iniFileIO.getIniString(webRoot + File.separator
                                + constants.WEB_CONFIG_FILE_NAME, "TBSWeb", "IniName", "",
                        (byte) 0);
                String APPName = BaseInstalActivity.this.m_iniFileIO.getIniString(webRoot+"/"+inifle,"TBSAPP","AppName","", (byte) 1);
                UIHelper.showcreateDeskShortCut(BaseInstalActivity.this, APPName, "", webRoot);
                if (UIHelper.getShareperference(BaseInstalActivity.this,
                        Constants.SHARED_PREFERENCE_NAME, "appOn", 0) == 0) {
                    Intent intent = new Intent(BaseInstalActivity.this,
                            InitializeToolbarActivity.class);
                    BaseInstalActivity.this.startActivity(intent);
                }
                finish();
            }
        }).setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (UIHelper.getShareperference(BaseInstalActivity.this,
                        Constants.SHARED_PREFERENCE_NAME, "appOn", 0) == 0) {
                    Intent intent = new Intent(BaseInstalActivity.this,
                            InitializeToolbarActivity.class);
                    BaseInstalActivity.this.startActivity(intent);
                }
                finish();
            }
        }).show();
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                   Bundle b = msg.getData();
                    BaseInstalActivity.this.setSortlink(b.getString("path"));
                    break;
            }
            super.handleMessage(msg);
        }

    };
}