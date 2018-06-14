package com.tbs.tbsmis.update;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.MimeUtils;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.text.DecimalFormat;


/**
 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
 */
public class AndroidDownloadAsyncTask extends
        AsyncTask<Integer, Integer, String>
{

    private final String url;
    private final String path;
    private final Context context;
    private final Builder builder;
    private Dialog showDialog;
    private TextView downloadName;
    private TextView downloadSize;

    public AndroidDownloadAsyncTask(Context context, String url, String path) {
        this.url = url;
        this.path = path;
        this.context = context;
        LayoutInflater factory = LayoutInflater.from(context);// 提示框
        View view = factory.inflate(R.layout.about_app_dialog, null);// 这里必须是final的
        downloadName = (TextView) view.findViewById(R.id.app_title);//
        downloadSize = (TextView) view.findViewById(R.id.version_content);//
        builder = new Builder(context);
        builder.setView(view);

    }

    // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
        System.out.println("path = " + path);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this.context, "需要SD卡。", Toast.LENGTH_LONG).show();
        }
        // // 显示文件大小格式：2个小数点显示
        // DecimalFormat df = new DecimalFormat("0.00");
        // // 进度条下面显示的总文件大小

        String DowmloadFileSize = "正在计算...";
        // df.format((float) contentLength / 1024 / 1024) + "MB";
        if (FileUtils.getFileFormat(this.url).equalsIgnoreCase("tbk")) {
            String webRoot = UIHelper.getStoragePath(context);
            // System.out.println("path = " + path);
            if (StringUtils.isEmpty(this.path) || this.path.indexOf("/") == -1) {
                downloadName.setText(FileUtils.getFileName(this.url));
                downloadSize.setText(DowmloadFileSize);
                builder
                        .setTitle("下载")
                        .setCancelable(false)
//                        .setMessage(
//                                "文件名：" + FileUtils.getFileName(this.url) + "\n大小："
//                                        + DowmloadFileSize)
                        // 提示框标题
                        .setPositiveButton(
                                "确定",// 提示框的两个按钮
                                new OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        DownloadManager mDownloadManager = (DownloadManager) AndroidDownloadAsyncTask
                                                .this.context
                                                .getSystemService(Context.DOWNLOAD_SERVICE);
                                        DownloadManager.Query query = new DownloadManager.Query();
                                        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                                        Cursor cursor = mDownloadManager
                                                .query(query);
                                        if (cursor.moveToFirst()) {// 有记录
                                            String uriString = cursor.getString(cursor
                                                    .getColumnIndex(DownloadManager.COLUMN_URI));
                                            if (UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)
                                                    .equalsIgnoreCase(uriString)) {
                                                Toast.makeText(AndroidDownloadAsyncTask.this.context,
                                                        "正在下载，请稍后。。。",
                                                        Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        }
                                        Request down = new Request(
                                                Uri.parse(UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)));
                                        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                                        down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                                                | Request.NETWORK_WIFI);
                                        // 禁止发出通知，既后台下载
                                        down.setShowRunningNotification(true);
                                        // down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                        if (VERSION.SDK_INT > VERSION_CODES
                                                .GINGERBREAD_MR1) {
                                            down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
                                        }
                                        Intent mIntent = new Intent();
                                        mIntent.setAction(context
                                                .getString(R.string.DownloadServerName));//你定义的service的action
                                        mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                        context.startService(mIntent);
//                                    AndroidDownloadAsyncTask.this.context.startService(new Intent(
//                                            AndroidDownloadAsyncTask.this.context.getString(string
// .DownloadServerName)));
                                        // 不显示下载界面
                                        down.setVisibleInDownloadsUi(true);
                                        // 设置下载后文件存放的位置
                                        down.setDestinationInExternalFilesDir(
                                                AndroidDownloadAsyncTask.this.context, AndroidDownloadAsyncTask.this
                                                        .path,
                                                FileUtils.getFileName(AndroidDownloadAsyncTask.this.url));
                                        // 将下载请求放入队列
                                        mDownloadManager.enqueue(down);
                                    }
                                }).setNegativeButton("取消", null);
                showDialog = builder.create();
                showDialog.show();
            } else {
                String Relativepath = "";
                if (this.path.indexOf("/") > -1) {
                    Relativepath = this.path.substring(0, this.path.indexOf('/'));
                } else {
                    Relativepath = this.path;
                }
                String appPath = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                        + Relativepath;
                File tempFile = new File(appPath);
                if (!tempFile.exists()) {
                    downloadName.setText("应用不存在，请先下载该应用");
                    downloadSize.setText("");
                    builder.setTitle("提醒")
                            .setCancelable(false)
                            //.setMessage("应用不存在，请先下载该应用")
                            .setPositiveButton(
                                    "确定",
                                    new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            return;
                                        }
                                    });
                    showDialog = builder.create();
                    showDialog.show();
                } else {
                    downloadName.setText(FileUtils.getFileName(this.url));
                    downloadSize.setText(DowmloadFileSize);
                    builder
                            .setTitle("下载")
                            .setCancelable(false)
//                            .setMessage(
//                                    "文件名：" + FileUtils.getFileName(this.url) + "\n大小："
//                                            + DowmloadFileSize)
                            // 提示框标题
                            .setPositiveButton(
                                    "确定",// 提示框的两个按钮
                                    new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            DownloadManager mDownloadManager = (DownloadManager)
                                                    AndroidDownloadAsyncTask
                                                            .this.context
                                                            .getSystemService(Context.DOWNLOAD_SERVICE);
                                            DownloadManager.Query query = new DownloadManager.Query();
                                            query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                                            Cursor cursor = mDownloadManager
                                                    .query(query);
                                            if (cursor.moveToFirst()) {// 有记录
                                                String uriString = cursor.getString(cursor
                                                        .getColumnIndex(DownloadManager.COLUMN_URI));
                                                if (UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)
                                                        .equalsIgnoreCase(uriString)) {
                                                    Toast.makeText(AndroidDownloadAsyncTask.this.context,
                                                            "正在下载，请稍后。。。",
                                                            Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                            }
                                            Request down = new Request(
                                                    Uri.parse(UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)));
                                            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                                            down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                                                    | Request.NETWORK_WIFI);
                                            // 禁止发出通知，既后台下载
                                            down.setShowRunningNotification(true);
                                            // down.setNotificationVisibility(DownloadManager.Request
                                            // .VISIBILITY_VISIBLE);
                                            if (VERSION.SDK_INT > VERSION_CODES
                                                    .GINGERBREAD_MR1) {
                                                down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
                                            }
                                            Intent mIntent = new Intent();
                                            mIntent.setAction(context
                                                    .getString(R.string.DownloadServerName));//你定义的service的action
                                            mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                            context.startService(mIntent);
//                                    AndroidDownloadAsyncTask.this.context.startService(new Intent(
//                                            AndroidDownloadAsyncTask.this.context.getString(string
// .DownloadServerName)));
                                            // 不显示下载界面
                                            down.setVisibleInDownloadsUi(true);
                                            // 设置下载后文件存放的位置
                                            down.setDestinationInExternalFilesDir(
                                                    AndroidDownloadAsyncTask.this.context, AndroidDownloadAsyncTask
                                                            .this.path,
                                                    FileUtils.getFileName(AndroidDownloadAsyncTask.this.url));
                                            // 将下载请求放入队列
                                            mDownloadManager.enqueue(down);
                                        }
                                    }).setNegativeButton("取消", null);
                    showDialog = builder.create();
                    showDialog.show();
                }
            }
        } else {
            downloadName.setText(FileUtils.getFileName(this.url));
            downloadSize.setText(DowmloadFileSize);
            builder
                    .setTitle("下载")
                    .setCancelable(false)
//                    .setMessage(
//                            "文件名：" + FileUtils.getFileName(this.url) + "\n大小："
//                                    + DowmloadFileSize)
                    // 提示框标题
                    .setPositiveButton(
                            "确定",// 提示框的两个按钮
                            new OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    try {
                                        DownloadManager mDownloadManager = (DownloadManager) AndroidDownloadAsyncTask
                                                .this.context
                                                .getSystemService(Context.DOWNLOAD_SERVICE);
                                        DownloadManager.Query query = new DownloadManager.Query();
                                        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                                        Cursor cursor = mDownloadManager
                                                .query(query);
                                        if (cursor.moveToFirst()) {// 有记录
                                            String uriString = cursor.getString(cursor
                                                    .getColumnIndex(DownloadManager.COLUMN_URI));
                                            if (UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)
                                                    .equalsIgnoreCase(uriString)) {
                                                Toast.makeText(AndroidDownloadAsyncTask.this.context,
                                                        "正在下载，请稍后。。。",
                                                        Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        }
                                        Request down = new Request(
                                                Uri.parse(UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)));
                                        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                                        down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                                                | Request.NETWORK_WIFI);
                                        // 禁止发出通知，既后台下载
                                        // down.setTitle(url);
                                        down.setShowRunningNotification(true);
                                        if (VERSION.SDK_INT > VERSION_CODES
                                                .GINGERBREAD_MR1) {
                                            down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
                                        }
                                        Intent mIntent = new Intent();
                                        mIntent.setAction(context
                                                .getString(R.string.DownloadServerName));//你定义的service的action
                                        mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                        context.startService(mIntent);

                                        String ext = FileUtils.getFileFormat(AndroidDownloadAsyncTask.this.url)
                                                .toLowerCase();
                                        String mimeType = MimeUtils
                                                .guessMimeTypeFromExtension(ext);
                                        down.setMimeType(mimeType);
                                        // 不显示下载界面
                                        down.setVisibleInDownloadsUi(true);
                                        // 设置下载后文件存放的位置
                                        down.setDestinationInExternalFilesDir(
                                                AndroidDownloadAsyncTask.this.context, AndroidDownloadAsyncTask.this
                                                        .path,
                                                FileUtils.getFileName(AndroidDownloadAsyncTask.this.url));
                                        // 将下载请求放入队列
                                        mDownloadManager.enqueue(down);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).setNegativeButton("取消", null);
            showDialog = builder.create();
            showDialog.show();
        }
    }

    /**
     * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
     */
    @Override
    protected String doInBackground(Integer... params) {
        //            URL Url = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) Url
//                    .openConnection();
//            conn.setRequestProperty("Accept-Encoding", "identity");
//            conn.connect();
//             = conn.getContentLength();
        long length = FileUtils.getNetFileSize(url);
        //System.out.println("length = " + length);
        //System.out.println("url = " + url);
        // 进度条下面显示的总文件大小
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format((float) length / 1024 / 1024) + "MB";
    }

    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @SuppressLint("ShowToast")
    @Override
    protected void onPostExecute(String result) {
        Message msg = new Message();

        if (result != null) {
            //downloadSize.setText(result);
            msg.what = 0;
            msg.obj = result;
        } else {
            result = "获取大小失败";
            msg.what = 0;
            msg.obj = result;
        }
        mHandler.sendMessage(msg);
    }

    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        // 显示文件大小格式：2个小数点显示


    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            int what = msg.what;
            if (what == 0) {    //update
                if(showDialog.isShowing())
                    downloadSize.setText(msg.obj.toString());
            } else {
            }
        }
    };
//    private void StarDownload(String webRoot, String DowmloadFileSize, Builder showDialog) {
//        // TODO Auto-generated method stub
//        showDialog
//                .setTitle("下载")
//                .setCancelable(false)
//                .setMessage(
//                        "文件名：" + FileUtils.getFileName(this.url) + "\n大小："
//                                + DowmloadFileSize)
//                // 提示框标题
//                .setPositiveButton(
//                        "确定",// 提示框的两个按钮
//                        new OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                DownloadManager mDownloadManager = (DownloadManager) AndroidDownloadAsyncTask
//                                        .this.context
//                                        .getSystemService(Context.DOWNLOAD_SERVICE);
//                                DownloadManager.Query query = new DownloadManager.Query();
//                                query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
//                                Cursor cursor = mDownloadManager
//                                        .query(query);
//                                if (cursor.moveToFirst()) {// 有记录
//                                    String uriString = cursor.getString(cursor
//                                            .getColumnIndex(DownloadManager.COLUMN_URI));
//                                    if (UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)
//                                            .equalsIgnoreCase(uriString)) {
//                                        Toast.makeText(AndroidDownloadAsyncTask.this.context,
//                                                "正在下载，请稍后。。。",
//                                                Toast.LENGTH_LONG).show();
//                                        return;
//                                    }
//                                }
//                                Request down = new Request(
//                                        Uri.parse(UIHelper.encodeGB(AndroidDownloadAsyncTask.this.url)));
//                                // 设置允许使用的网络类型，这里是移动网络和wifi都可以
//                                down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
//                                        | Request.NETWORK_WIFI);
//                                // 禁止发出通知，既后台下载
//                                down.setShowRunningNotification(true);
//                                // down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//                                if (VERSION.SDK_INT > VERSION_CODES
//                                        .GINGERBREAD_MR1) {
//                                    down.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
//                                }
//                                Intent mIntent = new Intent();
//                                mIntent.setAction(context
//                                        .getString(string.DownloadServerName));//你定义的service的action
//                                mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
//                                context.startService(mIntent);
////                                    AndroidDownloadAsyncTask.this.context.startService(new Intent(
////                                            AndroidDownloadAsyncTask.this.context.getString(string
//// .DownloadServerName)));
//                                // 不显示下载界面
//                                down.setVisibleInDownloadsUi(true);
//                                // 设置下载后文件存放的位置
//                                down.setDestinationInExternalFilesDir(
//                                        AndroidDownloadAsyncTask.this.context, AndroidDownloadAsyncTask.this.path,
//                                        FileUtils.getFileName(AndroidDownloadAsyncTask.this.url));
//                                // 将下载请求放入队列
//                                mDownloadManager.enqueue(down);
//                            }
//                        }).setNegativeButton("取消", null).create().show();
//    }
}