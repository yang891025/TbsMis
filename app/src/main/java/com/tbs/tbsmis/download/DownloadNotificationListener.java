
package com.tbs.tbsmis.download;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.StringUtils;

import java.io.File;

/**
 * DownloadNotificationListener implements DownloadListener and finished the job
 * of show download progress and messages in notification bar.You can use it
 * show messages in notification bar, or you can write own DownloadListener.
 *
 * @author offbye@gmail.com
 */

public class DownloadNotificationListener implements DownloadListener
{

    private static final String TAG = "DownloadNotificationListener";
    private Context mContext;

    private Notification.Builder mNotification;

    private int mId;

    private NotificationManager mNotificationManager;

    private int mProgress = 0;

    private ChapterDownloadTask mTask;

    public DownloadNotificationListener(Context context, ChapterDownloadTask task) {
        mContext = context;
        mId = task.getUrl().hashCode();
        mTask = task;
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = initNotifiction(task.getTitle());
    }

    @Override
    public void onDownloadStop() {
        mNotification.setTicker(mContext.getString(R.string.download_stopped));
        mNotification.setContentText(mContext.getString(R.string.download_stopped));
        mNotificationManager.notify(mId, mNotification.build());
        mNotificationManager.cancel(mId);
        Intent intent = new Intent();
        intent.setAction("download_refresh" + mContext.getString(R.string.app_name));
        mContext.sendBroadcast(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDownloadStart() {
        Log.w(TAG, "onDownloadStart");
        mNotificationManager.notify(mId, mNotification.build());
        Intent intent = new Intent();
        intent.setAction("download_refresh" + mContext.getString(R.string.app_name));
        mContext.sendBroadcast(intent);
    }

    @SuppressLint({"LongLogTag", "StringFormatMatches"})
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onDownloadProgress(long finishedSize, long totalSize, int speed) {
        Log.w(TAG, "onDownloadProgress");
        int percent = (int) (finishedSize * 100 / totalSize);
        if (percent - mProgress > 1) { // 降低状态栏进度刷新频率，性能问题
            mProgress = percent;
            mNotification.setProgress((int)totalSize,(int)finishedSize,false);
            mNotification.setContentText(String.format(mContext.getString(R.string.download_speed),mProgress, speed));
//            mNotification.createContentView();
//            mNotification.build().contentView.setTextViewText(R.id.notify_state,
//                    String.format(mContext.getString(R.string.download_speed), mProgress, speed));
//            mNotification.build().setProgressBar(R.id.notify_processbar, 100, percent, false);
            mNotificationManager.notify(mId, mNotification.build());
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDownloadPause() {
        Log.w(TAG, "onDownloadPause");
        mNotification.setTicker(mContext.getString(R.string.download_paused));
        mNotification.setContentText(mContext.getString(R.string.download_paused));
//        mNotification.contentView.setTextViewText(R.id.notify_state,
//                mContext.getString(R.string.download_paused));
//        mNotification.contentView.setProgressBar(R.id.notify_processbar, 100, 0, true);
        mNotificationManager.notify(mId, mNotification.build());
        Intent intent = new Intent();
        intent.setAction("download_refresh" + mContext.getString(R.string.app_name));
        mContext.sendBroadcast(intent);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDownloadFinish(String filepath, String title) {
        Log.w(TAG, "onDownloadFinish");
        mNotification.setTicker(mContext.getString(R.string.download_finished));
        mNotification.setContentText(mContext.getString(R.string.download_finished));
        mNotification.setSmallIcon(android.R.drawable.stat_sys_download_done);
        mNotification.setAutoCancel(true);
        Intent intent = new Intent(mContext, DownloadActivity.class);
        intent.putExtra("isDownloaded", true);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
//        mNotification.setLatestEventInfo(mContext, title, mContext.getString(R.string.download_finished),
//                mNotification.contentIntent);
        mNotificationManager.notify(mId, mNotification.build());
        IniFile IniFile = new IniFile();

        String Path = mTask.getFilePath();
        String inipath = Path+"/"+Path.substring(Path.lastIndexOf("/"))+".ini";
        File file = new File(Path);
        if (!file.exists())
            file.mkdirs();
        int count = Integer.parseInt(IniFile.getIniString(inipath, "file", "count", "0", (byte) 0));
        int filecount = 1;
        boolean ishave = false;
        for (int i = 1; i <= count; i++) {
            String countCode = IniFile.getIniString(inipath, "file" + i, "code", "", (byte) 0);
            if (countCode.equalsIgnoreCase(mTask.getCode())) {
                filecount = i;
                ishave = true;
                break;
            }
        }
        if (!ishave) {
            filecount = count + 1;
            IniFile.writeIniString(inipath, "file", "count", filecount + "");
        }
        IniFile.writeIniString(inipath, "file" + filecount, "time", StringUtils.getDate
                ());
        IniFile.writeIniString(inipath, "file" + filecount, "id", mTask.getId());
        IniFile.writeIniString(inipath, "file" + filecount, "title", mTask.getTitle());
        IniFile.writeIniString(inipath, "file" + filecount, "name", mTask.getFileName());
        IniFile.writeIniString(inipath, "file" + filecount, "code", mTask.getCode());
        IniFile.writeIniString(inipath, "file" + filecount, "path", mTask.getFilePath());
        IniFile.writeIniString(inipath, "file" + filecount, "type", mTask.getType());
        intent = new Intent();
        intent.setAction("download_refresh" + mContext.getString(R.string.app_name));
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onDownloadFail() {
        mNotification.setTicker(mContext.getString(R.string.download_failed));
        mNotification.setContentText(mContext.getString(R.string.download_failed));
//        mNotification.contentView.setTextViewText(R.id.notify_state,
//                mContext.getString(R.string.download_failed));
//        mNotification.contentView.setProgressBar(R.id.notify_processbar, 100, 0, true);
        mNotificationManager.notify(mId, mNotification.build());
        mNotificationManager.cancel(mId);
        Intent intent = new Intent();
        intent.setAction("download_refresh" + mContext.getString(R.string.app_name));
        mContext.sendBroadcast(intent);
    }

    public Notification.Builder initNotifiction(String title) {
        Notification.Builder builder1 = new Notification.Builder(mContext);
        builder1.setSmallIcon(android.R.drawable.stat_sys_download); //设置图标
        builder1.setTicker(mContext.getString(R.string.downloading_msg));
        builder1.setContentTitle(title); //设置标题
        builder1.setContentText(mContext.getString(R.string.downloading_msg)); //消息内容
        builder1.setWhen(System.currentTimeMillis()); //发送时间
        //builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        //builder1.setAutoCancel(true);//打开程序后图标消失
        Intent intent = new Intent(mContext, DownloadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        builder1.setContentIntent(pendingIntent);
        return builder1;
    }
}
