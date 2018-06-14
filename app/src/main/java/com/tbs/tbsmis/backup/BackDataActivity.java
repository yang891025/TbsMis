package com.tbs.tbsmis.backup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.PathChooseDialog;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileIO;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import NewsTool.NewsContent;

public class BackDataActivity extends Activity implements View.OnClickListener
{
    private ImageView finishBtn;
    private TextView title;
    private ImageView take_add;
    private TextView news_time;
    private RelativeLayout serverPath;
    private RelativeLayout localPath;
    private RelativeLayout appName;
    private RelativeLayout appVer;
    private TextView backup_server_dir;
    private TextView backup_local_dir;
    private EditText app_name;
    private EditText app_ver;
    private RelativeLayout subdirPath;
    private TextView backup_sub_dir;
    private BroadcastReceiver BackMsgreceiver;
    private ImageView app_name_delete;
    private ImageView app_ver_delete;
    private IniFile m_iniFileIO;
    private String appIniFile;
    private EditText back_content;
    private RelativeLayout start_backup;
    private HttpConnectionUtil connection;

    private ProgressBar mProgress;
    private ProgressDialog Prodialog;
    private TextView mProgressText;
    private AlertDialog ModifyDialog;

    private static boolean Unzip;

    public static boolean isUnzip() {
        return BackDataActivity.Unzip;
    }

    public static void setUnzip(boolean unzip) {
        BackDataActivity.Unzip = unzip;
    }

    private static boolean Unupdate;

    public static boolean isUnupdate() {
        return BackDataActivity.Unupdate;
    }

    public static void setUnupdate(boolean unupdate) {
        BackDataActivity.Unupdate = unupdate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.back_detail_layout);
        MyActivity.getInstance().addActivity(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("backup");
        this.BackMsgreceiver = new myBroadcastreceiver();
        this.registerReceiver(this.BackMsgreceiver, intentFilter);
        this.init();
    }

    private class myBroadcastreceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            String action = arg1.getAction();
            if (action.equalsIgnoreCase("backup")) {
                String subDir = arg1.getStringExtra("subPath");
                BackDataActivity.this.backup_sub_dir.setText(subDir);
            }
        }

    }

    private void init() {
        // TODO Auto-generated method stub
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.title = (TextView) this.findViewById(R.id.title_tv);
        this.title.setText("备份数据");
        this.take_add = (ImageView) this.findViewById(R.id.take_add);

        this.serverPath = (RelativeLayout) this.findViewById(R.id.news_url_layout);
        this.localPath = (RelativeLayout) this.findViewById(R.id.news_title_layout);
        this.subdirPath = (RelativeLayout) this.findViewById(R.id.back_subdir_layout);
        this.appName = (RelativeLayout) this.findViewById(R.id.news_author_layout);
        this.appVer = (RelativeLayout) this.findViewById(R.id.news_address_layout);
        this.start_backup = (RelativeLayout) this.findViewById(R.id.start_backup);

        this.backup_sub_dir = (TextView) this.findViewById(R.id.backup_sub_dir);
        this.news_time = (TextView) this.findViewById(R.id.news_time);
        this.backup_server_dir = (TextView) this.findViewById(R.id.backup_server_dir);
        this.backup_local_dir = (TextView) this.findViewById(R.id.backup_local_dir);

        this.back_content = (EditText) this.findViewById(R.id.back_content);
        this.app_name = (EditText) this.findViewById(R.id.app_name);
        this.app_ver = (EditText) this.findViewById(R.id.app_ver);

        this.app_name_delete = (ImageView) this.findViewById(R.id.app_name_delete);
        this.app_ver_delete = (ImageView) this.findViewById(R.id.app_ver_delete);

        this.initPath();

        this.app_ver.setText(this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP",
                "AppVersion", "1.0", (byte) 0));
        this.app_name.setText(this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP",
                "AppName", "", (byte) 0));
        this.news_time.setText(StringUtils.getDate());
        backup_server_dir.setText("个人目录");
        back_content.setText(m_iniFileIO.getIniString(this.appIniFile, "TBSAPP",
                "AppMsg", "", (byte) 0));
        subdirPath.setVisibility(View.GONE);
        this.app_name_delete.setOnClickListener(this);
        this.app_ver_delete.setOnClickListener(this);
        this.subdirPath.setOnClickListener(this);
        //this.serverPath.setOnClickListener(this);
        this.localPath.setOnClickListener(this);
        this.take_add.setOnClickListener(this);
        this.start_backup.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
    }

    private void startBackup() {
        // TODO Auto-generated method stub
        String backupdir = this.backup_server_dir.getText().toString();
        if (StringUtils.isEmpty(backupdir)) {
            Toast.makeText(this, "服务目录不可为空", Toast.LENGTH_SHORT).show();
        } else {
            String localdir = this.backup_local_dir.getText().toString();
            if (StringUtils.isEmpty(localdir)) {
                Toast.makeText(this, "备份目录不可为空", Toast.LENGTH_SHORT).show();
            } else {
                String content = this.back_content.getText().toString();
                if (StringUtils.isEmpty(content)) {
                    Toast.makeText(this, "备份描述不可为空", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    this.showRinghtsDialog(
                            this.backup_local_dir.getText().toString(), 1);
                }
            }

        }
    }
    //  }

    private void initPath() {
        // TODO Auto-generated method stub
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
        backup_local_dir.setText(webRoot);
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appIniFile = webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
    }

    private void addclick() {
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getShareperference(this,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String inipath = webRoot + "backup/" + "backTBK.ini";
        List<String> map = new ArrayList<String>();
        NewsContent NewsContent = new NewsContent();
        if (NewsContent.initialize(inipath)) {
            long countFile = NewsContent.countField();
            for (long m = 2; m < countFile; m++) {
                String InternalName = NewsContent.getFieldInternalName(m);
                if (InternalName.equalsIgnoreCase("appInfo:")
                        || InternalName.equalsIgnoreCase("appCate:")
                        || InternalName.equalsIgnoreCase("appDate:")
                        || InternalName.equalsIgnoreCase("appSize:")
                        || InternalName.equalsIgnoreCase("appPack:")
                        || InternalName.equalsIgnoreCase("packPath:")
                        || InternalName.equalsIgnoreCase("appTitle:")
                        || InternalName.equalsIgnoreCase("appCode:")
                        || InternalName.equalsIgnoreCase("officialWeb:")) {

                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("appName:")) {
                    if (this.appName.getVisibility() == View.GONE) {
                        map.add(NewsContent.getFieldName(m));
                    }
                } else if (NewsContent.getFieldInternalName(m)
                        .equalsIgnoreCase("appVer:")) {
                    if (this.appVer.getVisibility() == View.GONE) {
                        map.add(NewsContent.getFieldName(m));
                    }
                } else {
                    map.add(NewsContent.getFieldName(m));
                }
            }
            if (map.size() > 0) {
                final String[] items = new String[map.size()];
                // 在数组中存放数据 */
                for (int i = 0; i < map.size(); i++) {
                    items[i] = map.get(i);
                }
                new Builder(this).setTitle("请点击选择")
                        .setPositiveButton("关闭", null)
                        .setItems(items, new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (items[which].equalsIgnoreCase("应用名称")) {
                                    BackDataActivity.this.appName.setVisibility(View.VISIBLE);
                                } else if (items[which]
                                        .equalsIgnoreCase("当前版本")) {
                                    BackDataActivity.this.appVer.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(BackDataActivity.this,
                                            "无字段信息", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            } else {
                Toast.makeText(this, "无字段可添加", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) { // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn2:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.take_add:
                this.addclick();
                break;
            case R.id.start_backup:
                String userIni = this.appIniFile;
                if (Integer.parseInt(this.m_iniFileIO.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1) {
                    String dataPath = getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                if (Integer.parseInt(this.m_iniFileIO.getIniString(userIni, "Login",
                        "LoginFlag", "0", (byte) 0)) == 1) {
                    this.startBackup();
                } else {
                    UIHelper.showLoginDialog(this, 0);
                }
                break;
            case R.id.back_subdir_layout:
                UIHelper.getDirPath(this, "dataBackup");
                break;
            case R.id.app_name_delete:
                this.appName.setVisibility(View.GONE);
                break;
            case R.id.app_ver_delete:
                this.appVer.setVisibility(View.GONE);
                break;
            case R.id.news_url_layout:
                final String[] items = {"个人目录"};
                new Builder(this).setTitle("请点击选择")
                        .setPositiveButton("关闭", null)
                        .setItems(items, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BackDataActivity.this.backup_server_dir.setText(items[which]);
                                if (which == 0) {
                                    BackDataActivity.this.subdirPath.setVisibility(View.VISIBLE);
                                } else {
                                    BackDataActivity.this.subdirPath.setVisibility(View.GONE);
                                }
                            }
                        }).show();
                break;
            case R.id.news_title_layout:
                String appPath = UIHelper.getShareperference(this,
                        constants.SAVE_INFORMATION, "Path", "");
                if (appPath.endsWith("/") == false) {
                    appPath += "/";
                }
                UIHelper.showFilePathDialog(this, 1, appPath + "data/",
                        new PathChooseDialog.ChooseCompleteListener()
                        {
                            @Override
                            public void onComplete(String finalPath) {
                                finalPath = finalPath + File.separator;
                                BackDataActivity.this.backup_local_dir.setText(finalPath);
                            }
                        });
                break;
        }
    }

    private void showDownloadDialog(String CheckedPath, int serverDir) {
        String webRoot1 = UIHelper.getStoragePath(this);
        String zipFile1 = webRoot1 + constants.SD_CARD_ZIP_PATH2;
        File zip = new File(zipFile1);
        if (!zip.exists()) {
            zip.mkdirs();
        }
        String zipFile;
        String webRoot2 = UIHelper.getStoragePath(this);
        webRoot2 += constants.SD_CARD_TBSSOFT_PATH3;
        int length = webRoot2.length();
        this.m_iniFileIO.writeIniString(this.appIniFile, "TBSAPP", "AppVersion",
                this.app_ver.getText().toString());
        this.m_iniFileIO.writeIniString(this.appIniFile, "TBSAPP", "AppMsg",
                back_content.getText().toString());
        if (CheckedPath.endsWith("/") == false) {
            String packName1 = CheckedPath.substring(CheckedPath
                    .lastIndexOf(File.separator) + 1)
                    + "-"
                    + StringUtils.getTime();
            zipFile = zipFile1 + packName1 + ".tbk";
            String pckAbout = "appPack:"
                    + packName1
                    + ".tbk"
                    + "\r\nappName:"
                    + this.app_name.getText()
                    + "\r\nappTitle:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP", "Title",
                    "", (byte) 0)
                    + "\r\nappCode:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP", "AppCode",
                    "", (byte) 0)
                    + "\r\nofficialWeb:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP", "defaultUrl",
                    "", (byte) 0)
                    + "\r\nappCate:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP",
                    "AppCategory", "", (byte) 0) + "\r\nappInfo:"
                    + this.back_content.getText() + "\r\nappVer:"
                    + this.app_ver.getText() + "\r\nappDate:"
                    + this.news_time.getText() + "\r\npackPath:"
                    + CheckedPath.substring(length + 1, CheckedPath.length())
                    + "\r\nappSize:";
            FileIO.CreateTxt(zipFile1, pckAbout, packName1 + ".txt");
        } else {
            CheckedPath = CheckedPath.substring(0,
                    CheckedPath.lastIndexOf(File.separator));
            String packName = CheckedPath.substring(CheckedPath
                    .lastIndexOf(File.separator) + 1)
                    + "-"
                    + StringUtils.getTime();
            zipFile = zipFile1 + packName + ".tbk";
            String pckAbout = "appPack:"
                    + packName
                    + ".tbk"
                    + "\r\nappName:"
                    + this.app_name.getText()
                    + "\r\nappTitle:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP", "Title",
                    "", (byte) 0)
                    + "\r\nappCode:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP", "AppCode",
                    "", (byte) 0)
                    + "\r\nofficialWeb:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP", "defaultUrl",
                    "", (byte) 0)
                    + "\r\nappCate:"
                    + this.m_iniFileIO.getIniString(this.appIniFile, "TBSAPP",
                    "AppCategory", "", (byte) 0) + "\r\nappInfo:"
                    + this.back_content.getText() + "\r\nappVer:"
                    + this.app_ver.getText() + "\r\nappDate:"
                    + this.news_time.getText() + "\r\npackPath:"
                    + CheckedPath.substring(length + 1, CheckedPath.length())
                    + "\r\nappSize:";
            FileIO.CreateTxt(zipFile1, pckAbout, packName + ".txt");
        }
        Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("正在压缩文件");
        builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.update_progress, null);
        this.mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        this.mProgressText = (TextView) v.findViewById(R.id.update_progress_text);
        builder.setView(v);
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BackDataActivity.setUnzip(false);
                dialog.dismiss();

            }
        });
        this.ModifyDialog = builder.create();
        this.ModifyDialog.setCanceledOnTouchOutside(false);
        this.ModifyDialog.show();
        List<String> fileList = new ArrayList<String>();
        FileUtils.getDirFiles(CheckedPath, fileList);
        if (fileList.isEmpty()) {
            Toast.makeText(this, CheckedPath + "目录为空", Toast.LENGTH_SHORT)
                    .show();
        } else {
            this.connect(fileList, zipFile, 0, this.backup_sub_dir.getText().toString(),
                    serverDir);
        }

    }

    @SuppressWarnings("deprecation")
    private void showRinghtsDialog(String CheckedPath, int serverDir) {
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("验证");
        this.Prodialog.setMessage("正在验证权限，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method
                // stub
                BackDataActivity.setUnzip(false);
                dialog.dismiss();
                // Unupdate = false;
            }
        });
        BackDataActivity.setUnzip(true);
        this.connect("", 3, CheckedPath, serverDir);
    }

    @SuppressWarnings("deprecation")
    private void showModifyDialog(String FilePath, int count,
                                  String CheckedPath, int serverDir) {
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("上传应用包");
        this.Prodialog.setMessage("正在上传，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener()
        {
            @SuppressWarnings("static-access")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method
                // stub
                BackDataActivity.setUnupdate(false);
                BackDataActivity.this.connection.setUnupdata(false);
                dialog.dismiss();

            }
        });
        BackDataActivity.setUnupdate(true);
        this.connect(FilePath, count, CheckedPath, serverDir);
    }

    private void connect(List<String> CheckedFile, String zipFile, int count,
                         String CheckedPath, int serverDir) {
        BackDataActivity.MyAsyncTask task = new BackDataActivity.MyAsyncTask(CheckedFile, zipFile, count, this,
                CheckedPath, serverDir);
        task.execute();
    }

    private void connect(String CheckedFile, int count, String CheckedPath,
                         int serverDir) {
        BackDataActivity.MyAsyncTask task = new BackDataActivity.MyAsyncTask(CheckedFile, count, this,
                CheckedPath, serverDir);
        task.execute();
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        private List<String> CheckedFile;
        private String FilePath;
        private final Context context;
        private final int count;
        private String zipFile;
        private String CheckedPath;
        private final int serverDir;
        private PowerManager.WakeLock wakeLock;
        private HttpConnectionUtil connection;

        public MyAsyncTask(List<String> CheckedFile, String zipFile, int count,
                           Context context, String CheckedPath, int serverDir) {
            this.CheckedFile = CheckedFile;
            this.zipFile = zipFile;
            this.context = context;
            this.count = count;
            this.CheckedPath = CheckedPath;
            this.serverDir = serverDir;
            UIHelper.acquireWakeLock(context, this.wakeLock);
        }

        public MyAsyncTask(String CheckedFile, int count, Context context,
                           String CheckedPath, int serverDir) {
            FilePath = CheckedFile;
            this.context = context;
            this.count = count;
            this.CheckedPath = CheckedPath;
            this.serverDir = serverDir;
            UIHelper.acquireWakeLock(context, this.wakeLock);
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            String rootPath = UIHelper.getSoftPath(context);
            if (rootPath.endsWith("/") == false) {
                rootPath += "/";
            }
            rootPath += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
            rootPath = UIHelper.getShareperference(this.context,
                    constants.SAVE_INFORMATION, "Path", rootPath);
            if (rootPath.endsWith("/") == false) {
                rootPath += "/";
            }
            String WebIniFile = rootPath + constants.WEB_CONFIG_FILE_NAME;
            rootPath = rootPath
                    + BackDataActivity.this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                    constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = rootPath;
            if (Integer.parseInt(BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1) {
                String dataPath = this.context.getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
            this.connection = new HttpConnectionUtil();
            switch (this.count) {
                case 3:
                    if (this.serverDir == 0) {
                        constants.verifyURL = "http://"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Store",
                                "storeAddress", constants.DefaultServerIp,
                                (byte) 0)
                                + ":"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Store",
                                "storePort", constants.DefaultServerPort,
                                (byte) 0) + "/Store/Upload.cbs";
                    } else if (this.serverDir == 1) {
                        constants.verifyURL = "http://"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Skydrive",
                                "skydriveAddress",
                                constants.DefaultServerIp, (byte) 0)
                                + ":"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Skydrive",
                                "skydrivePort",
                                constants.DefaultServerPort, (byte) 0)
                                + "/SkyDrive/SaveFile.cbs";
                    }
                    // System.out.println("constants.verifyURL ="
                    // + constants.verifyURL);
                    return this.connection.asyncConnect(
                            constants.verifyURL,
                            this.UserRights(BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Login",
                                    "LoginId", "", (byte) 0), BackDataActivity.this.m_iniFileIO
                                    .getIniString(userIni, "Login", "Account", "",
                                            (byte) 0)), HttpConnectionUtil.HttpMethod.GET, this.context);
                case 0:
                    int i = -1;
                    long Openzip = JTbszlib.OpenZip(this.zipFile, 0);
                    for (String FilePath : this.CheckedFile) {
                        if (BackDataActivity.isUnzip()) {
                            i = i + 1;
                            this.publishProgress(i, this.CheckedFile.size());
                            File fs = new File(FilePath);
                            int length = UIHelper.getShareperference(this.context,
                                    constants.SAVE_INFORMATION, "Path", "")
                                    .length();
                            if (!UIHelper.getShareperference(this.context,
                                    constants.SAVE_INFORMATION, "Path", "").endsWith("/"))
                                length = length + 1;
                            String rootPath1 = FilePath.substring(length - 1,
                                    FilePath.length());
                            if (fs.isDirectory()) {
                                JTbszlib.EnZipDir(FilePath, Openzip, 1, 1, "",
                                        rootPath1);
                            } else {
                                if (rootPath1.indexOf("/") != -1) {
                                    rootPath1 = rootPath1.substring(0,
                                            rootPath1.lastIndexOf("/"));
                                }
                                if (rootPath1.endsWith("/")) {
                                    rootPath1 = rootPath1.substring(0,
                                            rootPath1.length() - 1);
                                }
                                JTbszlib.EnZipFile(FilePath, rootPath1, Openzip, 1,
                                        1, "");
                            }
                        } else {
                            JTbszlib.CloseZip(Openzip);
                            this.CheckedFile.clear();
                            // downloadDialog.dismiss();
                            return this.zipFile;
                        }
                    }
                    this.publishProgress(i + 1, this.CheckedFile.size());
                    JTbszlib.CloseZip(Openzip);
                    this.CheckedFile.clear();
                    // downloadDialog.dismiss();
                    return this.zipFile;
                case 2:
                    if (this.serverDir == 0) {
                        constants.verifyURL = "http://"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Store",
                                "storeAddress", constants.DefaultServerIp,
                                (byte) 0)
                                + ":"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Store",
                                "storePort", constants.DefaultServerPort,
                                (byte) 0) + "/Store/Upload.cbs";
                    } else if (this.serverDir == 1) {
                        constants.verifyURL = "http://"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Skydrive",
                                "skydriveAddress",
                                constants.DefaultServerIp, (byte) 0)
                                + ":"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Skydrive",
                                "skydrivePort",
                                constants.DefaultServerPort, (byte) 0)
                                + "/SkyDrive/SaveFile.cbs";
                    }
                    return this.connection.asyncConnect(
                            constants.verifyURL,
                            this.UpdateFile(BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Login",
                                    "LoginId", "", (byte) 0), BackDataActivity.this.m_iniFileIO
                                    .getIniString(userIni, "Login", "Account", "",
                                            (byte) 0), this.CheckedPath), this.FilePath,
                            this.context);
                case 1:
                    if (this.serverDir == 0) {
                        constants.verifyURL = "http://"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Store",
                                "storeAddress", constants.DefaultServerIp,
                                (byte) 0)
                                + ":"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Store",
                                "storePort", constants.DefaultServerPort,
                                (byte) 0) + "/Store/Upload.cbs";
                    } else if (this.serverDir == 1) {
                        this.CheckedPath = "";
                        constants.verifyURL = "http://"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Skydrive",
                                "skydriveAddress",
                                constants.DefaultServerIp, (byte) 0)
                                + ":"
                                + BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Skydrive",
                                "skydrivePort",
                                constants.DefaultServerPort, (byte) 0)
                                + "/SkyDrive/SaveFile.cbs";
                    }

//                    return connection.asyncConnect(
//                            constants.verifyURL,
//                            UpdateFile(m_iniFileIO.getIniString(rootPath, "USER",
//                                    "LoginId", "", (byte) 0), m_iniFileIO
//                                    .getIniString(rootPath, "USER", "Account", "",
//                                            (byte) 0), CheckedPath), FilePath,
//                            context);
                    Map<String, String> param = new HashMap<String, String>();
                    // params.put("action", "SaveFile.cbs");
                    param.put("flag", "upload");
                    param.put("rePath", this.CheckedPath);
                    param.put("filepath", this.FilePath);
                    param.put("userName", BackDataActivity.this.m_iniFileIO
                            .getIniString(userIni, "Login", "Account", "",
                                    (byte) 0));
                    param.put("login_id", BackDataActivity.this.m_iniFileIO.getIniString(userIni, "Login",
                            "LoginId", "", (byte) 0));

                    String end = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "******";
                    String result = null;

                    FileInputStream fis = null;
                    InputStream is = null;
                    DataOutputStream dos = null;
                    InputStreamReader isr = null;
                    long startPos = 0;// 开始点
                    long endPos = 0;// 结束点
                    long compeleteSize = 0;// 完成度
                    File uploadFile = new File(this.FilePath);
                    if (uploadFile.exists()) {
                        endPos = uploadFile.length();
                    }
                    if (constants.verifyURL.indexOf("?") < 0) {
                        constants.verifyURL += "?";
                    }
                    if (param != null) {
                        for (String name : param.keySet()) {
                            // System.out.println(name + "=" + params.get(name));
                            constants.verifyURL += "&" + name + "="
                                    + URLEncoder.encode(param.get(name));
                        }
                    }
                    if (constants.verifyURL.contains("?&")) {
                        constants.verifyURL = constants.verifyURL.substring(0, constants.verifyURL.indexOf("&")) +
                                constants.verifyURL.substring(constants.verifyURL.indexOf("&") + 1);
                    }
                    //System.out.println("urlstr=" + urlstr);
                    HttpURLConnection connection = null;
                    try {
                        URL url = new URL(constants.verifyURL);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        connection.setConnectTimeout(constants.REQUEST_UPDATE_HEAD_MENU);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.setRequestProperty("Charset", "UTF-8");
                        connection.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        connection.setRequestProperty("Range", "bytes="
                                + (startPos + compeleteSize) + "-" + endPos);
                        // connection.setChunkedStreamingMode(0);
                        dos = new DataOutputStream(connection.getOutputStream());
                        dos.writeBytes(twoHyphens + boundary + end);
                        String fileName = URLEncoder.encode(
                                this.FilePath.substring(this.FilePath.lastIndexOf("/") + 1),
                                "utf-8");
                        dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                                + fileName + "\"" + end);
                        dos.writeBytes(end);
                        fis = new FileInputStream(this.FilePath);
                        int total = fis.available();
                        byte[] buffer = new byte[constants.UPLOAD_BUFFER]; // 8k
                        int count = 0;
                        while ((count = fis.read(buffer)) != -1) {
                            if (BackDataActivity.isUnupdate()) {
                                dos.write(buffer, 0, count);
                                compeleteSize += count;
                                this.publishProgress((int) (compeleteSize / (float) total * 100));
                            } else {
                                try {
                                    if (null != fis) {
                                        fis.close();
                                    }
                                    if (null != dos) {
                                        dos.close();
                                    }
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                                result = this.context.getResources().getString(
                                        R.string.toast_upload_cancel);
                            }
                        }
                        if (BackDataActivity.isUnupdate()) {
                            dos.writeBytes(end);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                            dos.flush();
                            is = connection.getInputStream();
                            isr = new InputStreamReader(is, "UTF-8");
                            BufferedReader br = new BufferedReader(isr);
                            result = br.readLine();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();// 打印错误信息
                        result = this.context.getResources().getString(
                                R.string.toast_uploaderro_cancel);
                    } finally {
                        try {
                            if (null != fis) {
                                fis.close();
                            }
                            if (null != dos) {
                                dos.close();
                            }
                            if (null != is) {
                                is.close();
                            }
                            if (null != isr) {
                                isr.close();
                            }
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                    return result;
            }
            return null;
        }

        private Map<String, String> UpdateFile(String LoginId, String UserName,
                                               String Path) {
            // TODO Auto-generated method stub
            Map<String, String> params = new HashMap<String, String>();
            // params.put("action", "SaveFile.cbs");
            params.put("flag", "upload");
            params.put("rePath", Path);
            params.put("filepath", this.FilePath);
            params.put("userName", UserName);
            params.put("login_id", LoginId);
            return params;
        }

        private Map<String, String> UserRights(String LoginId, String UserName) {
            // TODO Auto-generated method stub
            Map<String, String> params = new HashMap<String, String>();
            params.put("flag", "getRights");
            params.put("rePath", "");
            params.put("filepath", "");
            params.put("userName", UserName);
            params.put("login_id", LoginId);
            return params;
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            //
            if (result == null) {
                if (BackDataActivity.this.Prodialog.isShowing()) {
                    BackDataActivity.this.Prodialog.dismiss();
                }
                if (BackDataActivity.this.ModifyDialog.isShowing()) {
                    BackDataActivity.this.ModifyDialog.dismiss();
                }
                Toast.makeText(this.context, "网络连接错误", Toast.LENGTH_LONG).show();
                return;
            }
            if (this.count == 3) {
                //System.out.println(result);
                if (result.equalsIgnoreCase("true") && BackDataActivity.isUnzip()) {
                    BackDataActivity.this.Prodialog.dismiss();
                    BackDataActivity.this.showDownloadDialog(this.CheckedPath, this.serverDir);
                    // connect(CheckedFile, zipFile, 0, CheckedPath, serverDir);
                    // connect(FilePath, path, 0);
                } else {
                    BackDataActivity.this.Prodialog.dismiss();
                    Toast.makeText(this.context, "您还没有上传权限，无法上传文件！",
                            Toast.LENGTH_LONG).show();
                }
            } else if (this.count == 1 && BackDataActivity.isUnupdate()) {
                FileUtils.deleteFileWithPath(this.FilePath);
                if (this.FilePath.indexOf(".") > 0) {
                    this.FilePath = this.FilePath.substring(0, this.FilePath.indexOf("."))
                            + ".txt";
                }
                BackDataActivity.this.connect(this.FilePath, this.count + 1, this.CheckedPath, this.serverDir);
            } else if (this.count == 0 && BackDataActivity.isUnzip()) {
                if (BackDataActivity.this.ModifyDialog.isShowing()) {
                    BackDataActivity.this.ModifyDialog.dismiss();
                    BackDataActivity.this.showModifyDialog(result, this.count + 1, this.CheckedPath, this.serverDir);
                }
            } else if (this.count == 2) {
                if (BackDataActivity.this.Prodialog.isShowing()) {
                    BackDataActivity.this.Prodialog.dismiss();
                }
                new AlertDialog.Builder(this.context)
                        .setTitle("上传")
                        .setMessage(result)
                        .setNeutralButton("确定", new DialogInterface.OnClickListener()
                        {
                            @SuppressWarnings("static-access")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method
                                dialog.dismiss();

                            }
                        }).show();
                FileUtils.deleteFileWithPath(this.FilePath);
                //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            } else {
                if (BackDataActivity.this.Prodialog.isShowing()) {
                    BackDataActivity.this.Prodialog.dismiss();
                }
                FileUtils.deleteFileWithPath(result);
                FileUtils.deleteFileWithPath(this.FilePath);
                Toast.makeText(this.context, result, Toast.LENGTH_LONG).show();
            }
            if (null != this.wakeLock && this.wakeLock.isHeld()) {
                this.wakeLock.release();
                this.wakeLock = null;
            }
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            if (this.count == 1 || this.count == 3) {
                if (!BackDataActivity.this.Prodialog.isShowing()) {
                    BackDataActivity.this.Prodialog.show();
                }
            } else if (this.count == 0) {
                BackDataActivity.this.mProgress.setMax(this.CheckedFile.size());
            }
        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (this.count == 0 && BackDataActivity.isUnzip()) {
                BackDataActivity.this.mProgressText.setText(values[0] + "/" + values[1]);
                BackDataActivity.this.mProgress.setProgress(values[0]);
            } else if (this.count == 1 && BackDataActivity.isUnupdate()) {
                BackDataActivity.this.Prodialog.setProgress(values[0]);
                BackDataActivity.this.Prodialog.setMessage("正在上传，请稍候...(" + values[0] + "%)");
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(this.BackMsgreceiver);
    }
}