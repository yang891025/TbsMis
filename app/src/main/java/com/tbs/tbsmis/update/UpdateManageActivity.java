package com.tbs.tbsmis.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.ApkChooseDialog;
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
import java.util.HashMap;
import java.util.Map;

public class UpdateManageActivity extends Activity implements View.OnClickListener
{
    private ImageView finishBtn;
    private TextView title;

    private RelativeLayout update_title_layout;
    private TextView update_local_file;
    private EditText app_name;
    private TextView app_ver;
    private IniFile m_iniFileIO;
    private String appIniFile;
    private EditText update_content;
    private RelativeLayout start_backup;
    private ProgressDialog Prodialog;


    private static boolean Unupdate;
    private String userIni;
    private String packageName;
    private String updateUpPath;

    public static boolean isUnupdate() {
        return Unupdate;
    }

    public static void setUnupdate(boolean unupdate) {
        Unupdate = unupdate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.update_manage_layout);
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    private void init() {
        // TODO Auto-generated method stub
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.title = (TextView) this.findViewById(R.id.title_tv);

        this.update_title_layout = (RelativeLayout) this.findViewById(R.id.update_title_layout);
        this.start_backup = (RelativeLayout) this.findViewById(R.id.start_backup);

        this.update_local_file = (TextView) this.findViewById(R.id.update_local_file);

        this.update_content = (EditText) this.findViewById(R.id.update_content);
        this.app_name = (EditText) this.findViewById(R.id.app_name);
        this.app_ver = (TextView) this.findViewById(R.id.app_ver);
        start_backup.setEnabled(false);
        this.initPath();

        this.update_title_layout.setOnClickListener(this);

        this.start_backup.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
    }


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
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        this.appIniFile = webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
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
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn2:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.start_backup:
                showRinghtsDialog();
                break;
            case R.id.update_title_layout:
                UIHelper.showFileChooseDialog(this,
                        new ApkChooseDialog.ChooseListener()
                        {
                            @Override
                            public void onComplete(String CheckedFile) {
                                PackageManager pm = getPackageManager();
                                PackageInfo info = pm.getPackageArchiveInfo(CheckedFile, PackageManager.GET_ACTIVITIES);
                                //String packageName = info.packageName;
                                update_local_file.setText(CheckedFile);
                                packageName = info.packageName;
                                String packageVersionName = info.versionName;
                                app_ver.setText(packageVersionName);
                                start_backup.setEnabled(true);
                                app_name.setText(info.applicationInfo.loadLabel(pm));
//                                if(packageName.equals(getPackageName())){
//                                    int packageVersion = info.versionCode;
//                                    app_ver.setText(packageVersion+"");
//                                    start_backup.setEnabled(true);
//                                    update_local_file.setText(CheckedFile);
//                                }else{
//                                    start_backup.setEnabled(false);
//                                    Toast.makeText(UpdateManageActivity.this, "安装包与当前应用不匹配", Toast.LENGTH_LONG)
// .show();
//                                }
                            }
                        });
                break;
        }
    }

    private void showRinghtsDialog() {
        updateUpPath = m_iniFileIO.getIniString(userIni, "Software",
                "updateUpPath", "/Store/Upload.cbs?rePath=TbsMis",
                (byte) 0);
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
                dialog.dismiss();
            }
        });
        MyAsyncTask task = new MyAsyncTask(this, 1);
        task.execute();
    }

    @SuppressWarnings("deprecation")
    private void showUploadDialog(String FilePath, int count) {
        String appName = "TbsMis";
        if(updateUpPath.contains("?")){
            appName = updateUpPath.substring(updateUpPath.lastIndexOf("=") + 1);
            updateUpPath = updateUpPath.substring(0, updateUpPath.lastIndexOf("?"));
        }
        File file = new File(FilePath);
        String Path = file.getParent();
        FilePath = Path + "/" + appName + "-"
                + StringUtils.getTime();
        try {
            FileUtils.copyFileTo(file, new File(FilePath + ".apk"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pckAbout = "appPack:"
                + FilePath.substring(FilePath.lastIndexOf("/") + 1) + ".apk"
                + "\r\nappName:"
                + app_name.getText()
                + "\r\nappTitle:"
                + app_name.getText()
                + "\r\nappCode:"
                + appName
                + "\r\nappCate:"
                + "安卓应用" + "\r\nappInfo:"
                + update_content.getText() + "\r\nappVer:"
                + app_ver.getText() + "\r\nappDate:"
                + StringUtils.getDate() + "\r\npackPath:app:"
                + packageName
                + "\r\nappSize:";
        FileIO.CreateTxt(Path, pckAbout, FilePath.substring(FilePath.lastIndexOf("/") + 1) + ".txt");
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
                setUnupdate(false);
                dialog.dismiss();

            }
        });
        setUnupdate(true);
        this.connect("安卓应用/" + appName, FilePath + ".apk", count);
    }

    private void connect(String rePath, String CheckedFile, int count) {
        MyAsyncTask task = new MyAsyncTask(rePath, CheckedFile, this, count);
        task.execute();
    }

    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {

        private String rePath;
        private String FilePath;
        private Context context;
        private int count;
        private PowerManager.WakeLock wakeLock;

        public MyAsyncTask(String rePath, String CheckedFile, Context context, int count) {
            this.FilePath = CheckedFile;
            this.rePath = rePath;
            this.context = context;
            this.count = count;
            UIHelper.acquireWakeLock(context, this.wakeLock);
        }

        public MyAsyncTask(Context context, int count) {
            this.count = count;
            this.context = context;
            UIHelper.acquireWakeLock(context, this.wakeLock);
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            if (count == 1) {
                HttpConnectionUtil connection = new HttpConnectionUtil();
                if (updateUpPath.toLowerCase().contains("http:")) {
                    constants.verifyURL = updateUpPath;
                } else {
                    constants.verifyURL = "http://"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storeAddress",
                            constants.DefaultServerIp, (byte) 0)
                            + ":"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storePort",
                            constants.DefaultServerPort, (byte) 0)
                            + updateUpPath;

                }
                return connection.asyncConnect(
                        constants.verifyURL,
                        this.UserRights(m_iniFileIO.getIniString(userIni, "Login",
                                "LoginId", "", (byte) 0), m_iniFileIO
                                .getIniString(userIni, "Login", "Account", "",
                                        (byte) 0)), HttpConnectionUtil.HttpMethod.GET, this.context);
            } else if (count == 2) {
                if (updateUpPath.toLowerCase().contains("http:")) {
                    constants.verifyURL = updateUpPath;
                } else {
                    constants.verifyURL = "http://"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storeAddress",
                            constants.DefaultServerIp, (byte) 0)
                            + ":"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storePort",
                            constants.DefaultServerPort, (byte) 0)
                            + updateUpPath;

                }
                Map<String, String> param = new HashMap<String, String>();
                // params.put("action", "SaveFile.cbs");
                param.put("flag", "upload");
                param.put("rePath", rePath);
                param.put("filepath", FilePath);
                param.put("userName", m_iniFileIO
                        .getIniString(userIni, "Login", "Account", "",
                                (byte) 0));
                param.put("login_id", m_iniFileIO.getIniString(userIni, "Login",
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
                        constants.verifyURL += "&" + name + "="
                                + URLEncoder.encode(param.get(name));
                    }
                }
                if (constants.verifyURL.contains("?&")) {
                    constants.verifyURL = constants.verifyURL.substring(0, constants.verifyURL.indexOf("&")) +
                            constants.verifyURL.substring(constants.verifyURL.indexOf("&") + 1);
                }
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
                        if (isUnupdate()) {
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
                    if (isUnupdate()) {
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
            } else {
                HttpConnectionUtil connection = new HttpConnectionUtil();
                if (updateUpPath.toLowerCase().contains("http:")) {
                    constants.verifyURL = updateUpPath;
                } else {
                    constants.verifyURL = "http://"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storeAddress",
                            constants.DefaultServerIp, (byte) 0)
                            + ":"
                            + m_iniFileIO.getIniString(userIni, "Store",
                            "storePort",
                            constants.DefaultServerPort, (byte) 0)
                            + updateUpPath;

                }
                Map<String, String> params2 = new HashMap<String, String>();
                // params.put("action", "SaveFile.cbs");
                params2.put("flag", "upload");
                params2.put("rePath", rePath);
                params2.put("filepath", FilePath);
                params2.put("userName", m_iniFileIO
                        .getIniString(userIni, "Login", "Account", "",
                                (byte) 0));
                params2.put("login_id", m_iniFileIO.getIniString(userIni, "Login",
                        "LoginId", "", (byte) 0));
                return connection.asyncConnect(constants.verifyURL, params2, this.FilePath,
                        this.context);

            }

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
            if (result == null) {
                if (Prodialog.isShowing()) {
                    Prodialog.dismiss();
                }
                Toast.makeText(this.context, "网络连接错误", Toast.LENGTH_LONG).show();
                if (null != this.wakeLock && this.wakeLock.isHeld()) {
                    this.wakeLock.release();
                    this.wakeLock = null;
                }
                return;
            }
            if (count == 1) {
                if (result.equalsIgnoreCase("true")) {
                    Prodialog.dismiss();
                    showUploadDialog(update_local_file.getText().toString(), 2);
                } else {
                    Prodialog.dismiss();
                    Toast.makeText(this.context, "您还没有上传权限，无法上传文件！",
                            Toast.LENGTH_LONG).show();
                }
                if (null != this.wakeLock && this.wakeLock.isHeld()) {
                    this.wakeLock.release();
                    this.wakeLock = null;
                }
            } else if (count == 2 && isUnupdate()) {
                FileUtils.deleteFileWithPath(this.FilePath);
                if (this.FilePath.indexOf(".") > 0) {
                    this.FilePath = this.FilePath.substring(0, this.FilePath.indexOf("."))
                            + ".txt";
                }
                connect(this.rePath, this.FilePath, this.count + 1);
            } else {
                if (Prodialog.isShowing()) {
                    Prodialog.dismiss();
                }
                FileUtils.deleteFileWithPath(this.FilePath);
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
                if (null != this.wakeLock && this.wakeLock.isHeld()) {
                    this.wakeLock.release();
                    this.wakeLock = null;
                }
            }
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            if (!Prodialog.isShowing()) {
                Prodialog.show();
            }
        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (isUnupdate()) {
                Prodialog.setProgress(values[0]);
                Prodialog.setMessage("正在上传，请稍候...(" + values[0] + "%)");
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}