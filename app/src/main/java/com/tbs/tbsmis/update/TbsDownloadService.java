package com.tbs.tbsmis.update;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.tbs.Tbszlib.JTbszlib;
import com.tbs.cbs.CBSInterpret;
import com.tbs.cbs.JTbsPDFOE;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.HttpConnectionUtil.HttpMethod;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.getMediaType;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class TbsDownloadService extends Service
{

    private IniFile m_iniFileIO;
    private String appUserFile;
    private BroadcastReceiver DownloadCompleteReceiver;
    private int filecount;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private static final String TAG = "TbsDownloadService";

    @Override
    public void onCreate() {
        Log.i(TbsDownloadService.TAG, "onCreate");
        super.onCreate();
        this.filecount = 0;
        this.DownloadCompleteReceiver = new MyBroadcastReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        this.registerReceiver(this.DownloadCompleteReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.i(TbsDownloadService.TAG, "onDestroy");
        super.onDestroy();
        this.unregisterReceiver(this.DownloadCompleteReceiver);
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(TbsDownloadService.TAG, "onStartCommand~~~~~~~~~~~~");
        this.filecount = this.filecount + 1;
        return super.onStartCommand(intent, flags, startId);
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

    private class MyBroadcastReciver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager dm = (DownloadManager) TbsDownloadService.this.getSystemService(Context.DOWNLOAD_SERVICE);
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c
                            .getInt(columnIndex)) {
                        String uriString = c
                                .getString(c
                                        .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        uriString = URLDecoder.decode(uriString.substring(7));
                        //System.out.println("uriString = " + uriString);
                        TbsDownloadService.MyAsyncTask task = new TbsDownloadService.MyAsyncTask(
                                TbsDownloadService.this, uriString);
                        task.execute();
                        Toast.makeText(
                                context,
                                FileUtils.getFileName(uriString)
                                        + "下载已完成，智能处理中...", Toast.LENGTH_LONG)
                                .show();
                    }

                }
                c.close();
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED
                    .equals(action)) {
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
                TbsDownloadService.this.startActivity(i);
            }
        }
    }

    protected boolean AddApplication(String path, String Relativepath) {
        // TODO Auto-generated method stub
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
                    "resname" + i, "", (byte) 0);
            if (resname.equals(AppCategory) || resname.equals("我的下载")) {
                groupId = i;
                for (int j = 1; j <= groupresnum; j++) {
                    if (resTitle.equals(this.m_iniFileIO.getIniString(this.appUserFile,
                            "group" + i, "res" + j, "", (byte) 0))) {
                        this.m_iniFileIO.writeIniString(this.appUserFile, resTitle, "version", AppVersion);
                        return false;
                    }
                }
                break;
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
        this.m_iniFileIO.writeIniString(this.appUserFile, "resource", "resbkcolor"
                + resnum, categoryColor);
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        private final Context context;
        private String pathString;
        private int count = 1;
        private String newiniPath;

        public MyAsyncTask(Context c, String pathString) {
            context = c;
            this.pathString = pathString;
        }

        public MyAsyncTask(Context c, String newiniPath, String pathString, int count) {
            context = c;
            this.pathString = pathString;
            this.newiniPath = newiniPath;
            this.count = count;
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            if (this.count == 1) {
                String webRoot = UIHelper.getStoragePath(context);
                String end;
                TbsDownloadService.this.initPath();
                //System.out.println("pathString=" + pathString);
                if (FileUtils.getFileFormat(this.pathString).equalsIgnoreCase("tbk")) {
                    String Relativepath = FileUtils.getFileRelativepath(this.pathString);

                    if (Relativepath.indexOf('/') >= 0) {
                        webRoot += constants.SD_CARD_TBSSOFT_PATH3
                                + "/"
                                + Relativepath.substring(0,
                                Relativepath.indexOf('/'));
                        //System.out.println("webRoot=" + webRoot);
                        //System.out.println("pathString=" + pathString);
                        JTbszlib.UnZipFile(this.pathString, webRoot, 1, "");
                        JTbsPDFOE.AddLocalMachine(webRoot);
                        File file = new File(this.pathString);
                        if (file.exists()) {
                            file.delete();
                        }
                        end = "data";
                    } else {
                        Log.i(TbsDownloadService.TAG, "unzip");
                        webRoot += constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + Relativepath;
                        //System.out.println("webRoot=" + webRoot);
                        int zip = JTbszlib.UnZipFile(this.pathString, webRoot, 1, "");
                        JTbsPDFOE.AddLocalMachine(webRoot);
                        File file = new File(this.pathString);
                        if (file.exists()) {
                            file.delete();
                        }
                        String iniPath = TbsDownloadService.this.m_iniFileIO.getIniString(webRoot
                                        + File.separator + constants.WEB_CONFIG_FILE_NAME,
                                "TBSWeb", "IniName", "", (byte) 0);
                        this.newiniPath = webRoot + File.separator + iniPath;
                        if (!StringUtils.isEmpty(iniPath)
                                && FileUtils.getFileFormat(this.pathString)
                                .equalsIgnoreCase("tbk")) {
                            if (TbsDownloadService.this.AddApplication(webRoot, Relativepath)) {
                                end = "success";
                            } else {
                                end = "exits";
                            }

                        } else {
                            end = webRoot;
                        }
                    }
                    String configPath = this.context.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (configPath.endsWith("/") == false) {
                        configPath = configPath + "/";
                    }
                    String appIniFile = configPath
                            + constants.USER_CONFIG_FILE_NAME;
                    this.pathString = FileUtils.getFileName(this.pathString);
                    String endTime = this.pathString.substring(
                            this.pathString.indexOf("-") + 1, this.pathString.indexOf("."));
                    TbsDownloadService.this.m_iniFileIO.writeIniString(appIniFile, "data", Relativepath,
                            endTime);
                    return end;
                } else if (FileUtils.getFileFormat(this.pathString).equalsIgnoreCase(
                        "zip")) {
                    String Relativepath = FileUtils.getFileRelativepath(this.pathString);
                    if (Relativepath.indexOf('/') >= 0) {
                        webRoot += constants.SD_CARD_TBSFILE_PATH6
                                + "/"
                                + Relativepath.substring(0,
                                Relativepath.indexOf('/'));
                        JTbszlib.UnZipFile(this.pathString, webRoot, 1, "");
                        JTbsPDFOE.AddLocalMachine(webRoot);
                        File file = new File(this.pathString);
                        if (file.exists()) {
                            file.delete();
                        }
                        end = "data";
                    } else {
                        Log.i(TbsDownloadService.TAG, "unzip");
                        webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/"
                                + Relativepath;
                        JTbszlib.UnZipFile(this.pathString, webRoot, 1, "");
                        JTbsPDFOE.AddLocalMachine(webRoot);
                        File file = new File(this.pathString);
                        if (file.exists()) {
                            file.delete();
                        }
                        end = "download";
                    }
                    return end;

                } else if (FileUtils.getFileFormat(this.pathString).equalsIgnoreCase("txt")) {
                    webRoot += constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + FileUtils.getFileRelativepath(this.pathString);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
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
                    String webRoot1 = UIHelper.getSoftPath(context);
                    if (webRoot1.endsWith("/") == false) {
                        webRoot1 += "/";
                    }
                    webRoot1 += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
                    webRoot1 = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                            "Path", webRoot1);
                    if (webRoot1.endsWith("/") == false) {
                        webRoot1 += "/";
                    }
                    String WebIniFile = webRoot1 + constants.WEB_CONFIG_FILE_NAME;
                    CBSInterpret mInterpret = new CBSInterpret();
                    mInterpret.initGlobal(WebIniFile, webRoot1);
                    String interpretFile = mInterpret.Interpret("/custom/update.cbs?fname=" + webRoot + "&rid=self",
                            "GET", "", null, 0);
                    //System.out.println(interpretFile);
                    return interpretFile;
                } else if (FileUtils.getFileFormat(this.pathString).equalsIgnoreCase("apk")) {
                    webRoot += "/apk";
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    if (!new File(webRoot).exists()) {// 如果文件父目录不存在
                        new File(webRoot).mkdirs();
                    }
                    webRoot += FileUtils.getFileName(this.pathString);
                    // System.out.println("webRoot=" + webRoot);
                    File toPath = new File(webRoot);
                    File MovePath = new File(this.pathString);
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
                    return webRoot;
                } else {
                    webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/"
                            + FileUtils.getFileRelativepath(this.pathString);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    if (!new File(webRoot).exists()) {// 如果文件父目录不存在
                        new File(webRoot).mkdirs();
                    }
                    webRoot += FileUtils.getFileName(this.pathString);
                    // System.out.println("webRoot=" + webRoot);
                    File toPath = new File(webRoot);
                    File MovePath = new File(this.pathString);
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
                    return webRoot;
                }
            } else {
                IniFile IniFile = new IniFile();
                String webRoot = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appTestFile = webRoot
                        + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String userIni = appTestFile;
                if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1) {
                    String dataPath = this.context.getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                String loginId = IniFile.getIniString(userIni, "Login",
                        "LoginId", "", (byte) 0);
                String appId = "";
                if (this.newiniPath == null) {
                    String webRoot1 = UIHelper.getStoragePath(context);
                    webRoot1 = webRoot1 + constants.SD_CARD_TBSFILE_PATH6 + "/";
                    String path = FileUtils.getFileRelativepath(this.pathString);
                    String inipath = webRoot1 + path + "/" + path.substring(path.lastIndexOf("/") + 1) + ".ini";
                    String name = FileUtils.getFileName(this.pathString);
                    int count = Integer.parseInt(IniFile.getIniString(inipath, "file", "count", "0", (byte) 0));
                    for (int i = 1; i <= count; i++) {
                        String countName = IniFile.getIniString(inipath, "file" + i, "name", "", (byte) 0);
                        if (countName.equals(name)) {
                            appId = IniFile.getIniString(inipath, "file" + i, "code", "", (byte) 0);
                        }
                    }
                    if (appId.isEmpty()) {
                        appId = this.pathString.substring(this.pathString.lastIndexOf("/") + 1);
                        if (appId.contains("-"))
                            appId = appId.substring(0, appId.indexOf("-"));
                    }
                    // System.out.println("appId = "+appId);
                } else {
                    appId = IniFile.getIniString(this.pathString, "TBSAPP",
                            "AppCode", "tbs-mis", (byte) 0);
                }

                String ipUrl = IniFile.getIniString(userIni, "Login",
                        "ebsAddress", constants.DefaultServerIp, (byte) 0);
                String portUrl = IniFile.getIniString(userIni, "Login",
                        "ebsPort", "8083", (byte) 0);
                String baseUrl = "http://" + ipUrl + ":" + portUrl;
                HttpConnectionUtil connection = new HttpConnectionUtil();
                constants.verifyURL = baseUrl
                        + "/TBS-PAY/pay/StoreServlet?act=saveStoreDownLog&clientId=" + UIHelper.DeviceMD5ID(this
                        .context) +
                        "&clientName=" + URLEncoder.encode(Build.MANUFACTURER + Build.MODEL) +
                        "&appId=" + URLEncoder.encode(appId) + "&loginId=" + loginId;
                //System.out.println(constants.verifyURL);
                return connection.asyncConnect(constants.verifyURL, null,
                        HttpMethod.GET, this.context);
            }
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (this.count == 1) {
                TbsDownloadService.MyAsyncTask task = new TbsDownloadService.MyAsyncTask(
                        TbsDownloadService.this, this.newiniPath, this.pathString, 2);
                task.execute();
            }
            if (StringUtils.isEmpty(result)) {
                result = "";
            }
            if (result.equals("success")) {
                Toast.makeText(TbsDownloadService.this, "已添加到我的应用",
                        Toast.LENGTH_LONG).show();
            } else if (result.equals("exits")) {
                Toast.makeText(TbsDownloadService.this, "应用已更新",
                        Toast.LENGTH_LONG).show();
            } else if (result.equals("false")) {
                Toast.makeText(TbsDownloadService.this, "应用格式不正确，无法添加",
                        Toast.LENGTH_LONG).show();
            } else if (result.equals("file")) {
                Toast.makeText(TbsDownloadService.this, "文件已更新",
                        Toast.LENGTH_LONG).show();
            } else if (result.equals("data")) {
                Toast.makeText(TbsDownloadService.this, "数据已更新",
                        Toast.LENGTH_LONG).show();
            } else if (result.equals("download")) {
                Toast.makeText(TbsDownloadService.this, "下载完成",
                        Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(TbsDownloadService.this, result + "已更新",
//                        Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent();
            intent.setAction("recommend"
                    + getString(R.string.about_title));
            TbsDownloadService.this.sendBroadcast(intent);
            intent = new Intent();
            intent.setAction("Action_fresh"
                    + getString(R.string.about_title));
            TbsDownloadService.this.sendBroadcast(intent);
            intent = new Intent();
            intent.setAction("freshView" + getString(R.string.about_title));
            TbsDownloadService.this.sendBroadcast(intent);
            //System.out.println("filecount = "+filecount);
            if (count == 1)
                filecount = filecount - 1;
            if (filecount < 1) {
//                TbsDownloadService.this.stopService(new Intent(
//                        this.context.getString(string.DownloadServerName)));
                intent = new Intent();
                intent.setAction(context.getString(R.string.DownloadServerName));//你定义的service的action
                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                context.stopService(intent);
                File file = new File(result);
                if (file.isFile()) {
                    getMediaType mide = new getMediaType();
                    mide.initReflect();
                    int type = mide.getMediaFileType(result);
                    boolean isAudio = mide.isAudioFile(type);
                    boolean isVideo = mide.isVideoFile(type);
                    if (!isAudio && !isVideo) {
                        String filetype = result.substring(result.lastIndexOf(".") + 1);
                        UIHelper.showOpenFile(this.context, filetype, result);
                    }
                }
            }
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {

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
}