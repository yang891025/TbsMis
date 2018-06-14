package com.tbs.tbsmis.update;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.HttpConnectionUtil.HttpMethod;
import com.tbs.tbsmis.util.UIHelper;

import java.net.URLEncoder;

public class PlayerLogService extends Service
{

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    private static final String TAG = "PlayerLogService";

    @Override
    public void onCreate() {
        Log.i(PlayerLogService.TAG, "onCreate");
        super.onCreate();
        IniFile IniFile = new IniFile();
        String savePath = UIHelper.getStoragePath(this) + "/Log/playerLog.ini";
        int count = Integer.parseInt(IniFile.getIniString(savePath, "Log", "count", "0",
                (byte) 0));
        MyAsyncTask task = new MyAsyncTask(PlayerLogService.this, savePath, count);
        task.execute();
    }

    @Override
    public void onDestroy() {
        Log.i(PlayerLogService.TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.i(PlayerLogService.TAG, "onStartCommand~~~~~~~~~~~~");
        return super.onStartCommand(intent, flags, startId);
    }


    class MyAsyncTask extends AsyncTask<Integer, Integer, String>
    {
        private final Context context;
        private String pathString;
        private int count = 1;
        private IniFile IniFile;

        public MyAsyncTask(Context c, String pathString, int count) {
            context = c;
            this.pathString = pathString;
            this.count = count;
            IniFile = new IniFile();
        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            //System.out.println("count = " + count);
            if (count > 0) {
                String webRoot = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appTestFile = webRoot
                        + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                String ipUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                        "webAddress", constants.DefaultServerIp, (byte) 0);
                String portUrl = IniFile.getIniString(appTestFile, "TBSAPP",
                        "webPort", constants.DefaultServerPort, (byte) 0);
                String baseUrl = "http://" + ipUrl + ":" + portUrl;
                HttpConnectionUtil connection = new HttpConnectionUtil();
                String starttime = IniFile.getIniString(pathString, "log" + count, "starttime", "", (byte) 0);
                String UserName = IniFile.getIniString(pathString, "log" + count, "UserName", "", (byte) 0);
                String code = IniFile.getIniString(pathString, "log" + count, "code", "", (byte) 0);
                String path = IniFile.getIniString(pathString, "log" + count, "path", "", (byte) 0);
                String stoptime = IniFile.getIniString(pathString, "log" + count, "stoptime", "", (byte) 0);
                String playtime = IniFile.getIniString(pathString, "log" + count, "playtime", "", (byte) 0);
                String verifyURL = baseUrl
                        + "/FileStore/courseLog.cbs?path=" + URLEncoder.encode(path) + "&fileCode=" + code +
                        "&startTime=" + URLEncoder.encode(starttime) +
                        "&stopTime=" + URLEncoder.encode(stoptime) + "&playTime=" + playtime + "&UserName=" +
                        UserName + "&clientId=" + UIHelper.DeviceMD5ID(this
                        .context) +
                        "&clientName=" + URLEncoder.encode(Build.MANUFACTURER + Build.MODEL);
                //System.out.println("verifyURL = " + verifyURL);
                return connection.asyncConnect(verifyURL, null,
                        HttpMethod.GET, this.context);
            }
            return "false";
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (result.equalsIgnoreCase("success")) {
                    if (count > 0) {
                        IniFile.deleteIniSection(pathString, "log" + count);
                        count--;
                        MyAsyncTask task = new MyAsyncTask(context, pathString, count);
                        task.execute();
                    } else {
                        IniFile.writeIniString(pathString, "Log", "count", count + "");
                        stopSelf();
                    }
                } else {
                    IniFile.writeIniString(pathString, "Log", "count", count + "");
                    stopSelf();
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