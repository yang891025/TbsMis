package com.tbs.tbsmis.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.cbs.CBSInterpret;
import com.tbs.chat.ui.conversation.MainTab;
import com.tbs.circle.activity.CircleActivity;
import com.tbs.circle.activity.CircleMeActivity;
import com.tbs.ini.IniFile;
import com.tbs.player.activity.PlayerActivity;
import com.tbs.tbsmis.Film.FilmActivity;
import com.tbs.tbsmis.Live.LiveListMainActivity;
import com.tbs.tbsmis.Live.LivePlayer;
import com.tbs.tbsmis.Musics.MusicPublicActivity;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.ShortVideo.ShortVideoPlayer;
import com.tbs.tbsmis.activity.AboutActivity;
import com.tbs.tbsmis.activity.AppManagerActivity;
import com.tbs.tbsmis.activity.AppSetupActivity;
import com.tbs.tbsmis.activity.AppearanceSettingActivity;
import com.tbs.tbsmis.activity.BrowserActivity;
import com.tbs.tbsmis.activity.ChoseFileToSkyDriveDialog;
import com.tbs.tbsmis.activity.CloudSetupActivity;
import com.tbs.tbsmis.activity.DetailActivity;
import com.tbs.tbsmis.activity.DeviceManagerActivity;
import com.tbs.tbsmis.activity.DisplaySettingActivity;
import com.tbs.tbsmis.activity.FeedbackActivity;
import com.tbs.tbsmis.activity.FunctionActivity;
import com.tbs.tbsmis.activity.InitializeToolbarActivity;
import com.tbs.tbsmis.activity.LanuchActivity;
import com.tbs.tbsmis.activity.LogSetupActivity;
import com.tbs.tbsmis.activity.LoginActivity;
import com.tbs.tbsmis.activity.MyAppsActivity;
import com.tbs.tbsmis.activity.MyCloudActivity;
import com.tbs.tbsmis.activity.MyCollectActivity;
import com.tbs.tbsmis.activity.MyCommentActivity;
import com.tbs.tbsmis.activity.MyPageActivity;
import com.tbs.tbsmis.activity.MySkyDriveActivity;
import com.tbs.tbsmis.activity.MySubscribeActivity;
import com.tbs.tbsmis.activity.MyWalletActivity;
import com.tbs.tbsmis.activity.NetTaskActivity;
import com.tbs.tbsmis.activity.NewSeekBarActivity;
import com.tbs.tbsmis.activity.OverviewActivity;
import com.tbs.tbsmis.activity.PopMenuActivity;
import com.tbs.tbsmis.activity.PushMsgActivity;
import com.tbs.tbsmis.activity.RegisterActivity;
import com.tbs.tbsmis.activity.ResourceStoreActivity;
import com.tbs.tbsmis.activity.ScanActivity;
import com.tbs.tbsmis.activity.SetUpActivity;
import com.tbs.tbsmis.activity.SubscribeActivity;
import com.tbs.tbsmis.activity.SystemSetupActivityNew;
import com.tbs.tbsmis.activity.TerminalSetupActivity;
import com.tbs.tbsmis.aliapi.sendAliPay;
import com.tbs.tbsmis.app.AppContext;
import com.tbs.tbsmis.app.MethodsCompat;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.app.StartTbsweb;
import com.tbs.tbsmis.check.ApkChooseDialog;
import com.tbs.tbsmis.check.AppEditDialog;
import com.tbs.tbsmis.check.LoginDialog;
import com.tbs.tbsmis.check.PathChooseDialog;
import com.tbs.tbsmis.check.StorageChooseDialog;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.download.DownloadActivity;
import com.tbs.tbsmis.entity.StorageBean;
import com.tbs.tbsmis.file.FileExplorerTabActivity;
import com.tbs.tbsmis.file.MimeUtils;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.NotificationSettingsActivity;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.search.SearchActivityNew;
import com.tbs.tbsmis.search.SearchSetupActivity;
import com.tbs.tbsmis.source.FileSourceActivity;
import com.tbs.tbsmis.update.CollectAsyncTask;
import com.tbs.tbsmis.update.UpdateActivity;
import com.tbs.tbsmis.update.Updateapp;
import com.tbs.tbsmis.video.Video;
import com.tbs.tbsmis.video.VideoPlayer;
import com.tbs.tbsmis.video.VideoPlayerWeb;
import com.tbs.tbsmis.weixin.WeiXinAddressActivity;
import com.tbs.tbsmis.weixin.WeixinActivity;
import com.tbs.tbsmis.weixin.WeixinMenuActivity;
import com.tbs.tbsmis.weixin.WeixinNewMessage;
import com.tbs.tbsmis.weixin.WeixinSendManager;
import com.tbs.tbsmis.weixin.WeixinSetUpActivity;
import com.tbs.tbsmis.wxapi.sendPay;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.sharesdk.onekeyshare.OnekeyShare;
import me.wcy.music.activity.SongAlbumActivity;
import me.wcy.music.activity.SongPlayActivity;

import static java.net.URLEncoder.encode;

//import com.tbs.tbsmis.activity.EamilSettingActivity;

@SuppressLint("ShowToast")
public class UIHelper
{
    private static String Nowurl;
//    public static final int LISTVIEW_ACTION_INIT = 0x01;
//    public static final int LISTVIEW_ACTION_REFRESH = 0x02;
//    public static final int LISTVIEW_ACTION_SCROLL = 0x03;
//    public static final int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

    public static final int LISTVIEW_DATA_MORE = 0x01;
    public static final int LISTVIEW_DATA_LOADING = 0x02;
    public static final int LISTVIEW_DATA_FULL = 0x03;
//    public static final int LISTVIEW_DATA_EMPTY = 0x04;

    /**
     * 发送App异常崩溃报告
     *
     * @param cont
     * @param crashReport .
     *                    tbsmis 本地目录文件列表、参数设置、多目录多文件批量压缩上传 （所有目录文件对话框 都换成新的）
     */
    public static void sendAppCrashReport(final Context cont,
                                          String crashReport) {
        Builder builder = new Builder(cont);
        builder.setIcon(null);
        builder.setTitle(R.string.app_name);
        builder.setCancelable(false);
        builder.setMessage("抱歉应用发生崩溃异常,无法继续运行，详情查看日志信息。。。");
        builder.setPositiveButton(R.string.alert_yes_button,
                new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        StartTbsweb.Startapp(cont, 0);
                        ServiceManager serviceManager = new ServiceManager(cont);
                        serviceManager.stopAllService();
                        MyActivity.getInstance().AppExit(cont);
                    }
                });
        builder.show();
    }

    /**
     * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
     */
    public static void acquireWakeLock(Context context, PowerManager.WakeLock wakeLock) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, context.getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    public static void releaseWakeLock(PowerManager.WakeLock wakeLock) {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /*
        获取根路径信息
     */
    public static String getRootPath(Context context) {
        String storagePath = UIHelper.getShareperference(context,
                constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
        String rootPath = UIHelper.getShareperference(context,
                constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
        File file = new File(storagePath + File.separator + rootPath);
        if (file.exists() && file.canWrite()) {
            return rootPath;
        } else {
            file = new File(FileUtils.getFirstExterPath() + File.separator + rootPath);
            if (file.exists() && file.canWrite()) {
                UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
                        .getFirstExterPath());
                return rootPath;
            } else {
                UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
                        .getFirstExterPath());
                UIHelper.setSharePerference(context,
                        constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
                return constants.SD_CARD_TBS_PATH1;
            }
        }
    }

    /*
               获取软件路径信息
            */
    public static String getSoftPath(Context context) {
        String storagePath = UIHelper.getShareperference(context,
                constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
        String rootPath = UIHelper.getShareperference(context,
                constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
        ArrayList<StorageBean> storageData = StorageUtils.getStorageData(context);
        for (int i = 0; i < storageData.size(); i++) {
            if (storagePath.equalsIgnoreCase(storageData.get(i).getPath() + "/Android/data/" + context.getPackageName
                    ())) {
                boolean mounted = storageData.get(i).getMounted().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
                if (mounted) {
                    String webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                            "Path", "");
                    //String appName = context.getString(R.string.SD_CARD_TBSAPP_PATH2);
                    if (webRoot != "") {
                        return UIHelper.getShareperference(context,
                                constants.SAVE_INFORMATION, "Path", FileUtils.getFirstExterPath()) + File.separator +
                                rootPath + constants.SD_CARD_TBSSOFT_PATH3;
                    } else {
                        UIHelper.setSharePerference(context, constants.SAVE_INFORMATION,
                                "Path", FileUtils.getFirstExterPath() + File.separator + rootPath + constants
                                        .SD_CARD_TBSSOFT_PATH3 + "/" + context.getString(R.string
                                        .SD_CARD_TBSAPP_PATH2));
                        return FileUtils.getFirstExterPath() + File.separator + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3;
                    }
                } else {
                    break;
                }
            }
        }
        String webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                "Path", "");
        String appName = context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        if (webRoot != "") {
            if (webRoot.endsWith("/") == true) {
                webRoot = webRoot.substring(0, webRoot.length() - 1);
            }
            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
        }
        UIHelper.setSharePerference(context, constants.SAVE_INFORMATION,
                "Path", FileUtils.getFirstExterPath() + File.separator + rootPath + constants
                        .SD_CARD_TBSSOFT_PATH3 + "/" + appName);
        return FileUtils.getFirstExterPath() + File.separator + rootPath + constants.SD_CARD_TBSSOFT_PATH3;
    }

    /*
            获取项目路径信息
         */
    public static String getStoragePath(Context context) {
        String storagePath = UIHelper.getShareperference(context,
                constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
        String rootPath = UIHelper.getShareperference(context,
                constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
        ArrayList<StorageBean> storageData = StorageUtils.getStorageData(context);
        for (int i = 0; i < storageData.size(); i++) {

            if (storagePath.equalsIgnoreCase(storageData.get(i).getPath() + "/Android/data/" + context.getPackageName
                    ())) {
                boolean mounted = storageData.get(i).getMounted().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
                if (mounted) {
                    String webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                            "Path", "");

                    if (webRoot != "") {
                        return storagePath + File.separator + rootPath;
                    } else {
                        String appName = context.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        UIHelper.setSharePerference(context, constants.SAVE_INFORMATION,
                                "Path", FileUtils.getFirstExterPath() + File.separator + rootPath + constants
                                        .SD_CARD_TBSSOFT_PATH3 + "/" + appName);
                        return FileUtils.getFirstExterPath() + File.separator + rootPath;
                    }
                } else {
                    break;
                }
            }
        }
        String webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                "Path", "");
        String appName = context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        if (webRoot != "") {
            if (webRoot.endsWith("/") == true) {
                webRoot = webRoot.substring(0, webRoot.length() - 1);
            }
            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
        }
        UIHelper.setSharePerference(context, constants.SAVE_INFORMATION,
                "Path", FileUtils.getFirstExterPath() + File.separator + rootPath + constants
                        .SD_CARD_TBSSOFT_PATH3 + "/" + appName);
        return FileUtils.getFirstExterPath() + File.separator + rootPath;
    }

    /**
     * 生成设备唯一ID
     *
     * @param context
     */
    public static String DeviceMD5ID(Context context) {
        // TODO Auto-generated method stub
        TelephonyManager mTm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        String deviceMD5 = Build.BRAND
                + Build.MODEL
                + mTm.getDeviceId()
                + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID)
                + wm.getConnectionInfo().getMacAddress()
                + Constants.TBSAPP_DEVICE_APIKEY;
        try {
            deviceMD5 = StringUtils.getMD5(deviceMD5);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            deviceMD5 = "";
        }
        return deviceMD5;
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final AppContext ac = (AppContext) activity.getApplication();
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    Toast.makeText(ac, "缓存清除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ac, "缓存清除失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread()
        {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    ac.clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public static void calculateAppCache(Activity activity, final TextView size) {
        final AppContext ac = (AppContext) activity.getApplication();

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    size.setText(msg.obj.toString());
                } else {
                    Toast.makeText(ac, "缓存统计失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
        new Thread()
        {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    String webRoot = Environment.getExternalStorageDirectory()
                            .getAbsolutePath();
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    String picDir = webRoot + "lazyList";
                    webRoot = UIHelper.getSoftPath(ac) + "/" + ac.getString(R.string.SD_CARD_TBSAPP_PATH2);

                    webRoot = getShareperference(ac,
                            constants.SAVE_INFORMATION, "Path", webRoot);
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    // 计算缓存大小
                    long fileSize = 0;
                    String cacheSize = "0KB";
                    File filesDir = ac.getFilesDir();
                    File cacheDir = ac.getCacheDir();
                    File fileTemp = new File(webRoot + "tmp");
                    File picTemp = new File(picDir);

                    fileSize += FileUtils.getDirSize(fileTemp);
                    fileSize += FileUtils.getDirSize(filesDir);
                    fileSize += FileUtils.getDirSize(cacheDir);
                    fileSize += FileUtils.getDirSize(picTemp);
                    // 2.2版本才有将应用缓存转移到sd卡的功能
                    if (AppContext
                            .isMethodsCompat(VERSION_CODES.FROYO)) {
                        File externalCacheDir = MethodsCompat
                                .getExternalCacheDir(ac);
                        fileSize += FileUtils.getDirSize(externalCacheDir);
                    }
                    if (fileSize > 0)
                        cacheSize = FileUtils.formatFileSize(fileSize);
                    msg.obj = cacheSize;
                    ac.clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获取bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapByPath(String filePath) {
        return UIHelper.getBitmapByPath(filePath, null);
    }

    public static Bitmap getBitmapByPath(String filePath,
                                         Options opts) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }

    /**
     * 计算listview的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView,
                                                        int midHight) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int Height = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            Height = listItem.getMeasuredHeight();
            totalHeight += listItem.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + Height + midHight
                + listView.getDividerHeight() * listAdapter.getCount();
        listView.setLayoutParams(params);
    }

    // 返回不同库的快捷图标id
    public static int sourceId_drawable(String sourceID) {
        // Bitmap.CREATOR.createFromParcel(source);
        if (sourceID.equals("yqxx")) {
            return R.drawable.yqxx;
        } else if (sourceID.equals("dbn")) {
            return R.drawable.dbn;
        } else if (sourceID.equals("flfg")) {
            return R.drawable.flfg;
        } else if (sourceID.equals("dzts")) {
            return R.drawable.dzts;
        } else if (sourceID.equals("bkqs")) {
            return R.drawable.bkqs;
        } else if (sourceID.equals("jyzy")) {
            return R.drawable.jyzy;
        } else if (sourceID.equals("wsp") || sourceID.equals("server")
                || sourceID.equals("port") || sourceID.equals("process")) {
            return R.drawable.wsp;
        } else if (sourceID.equals("tpk")) {
            return R.drawable.tpk;
        } else {
            return R.drawable.ic_launcher;
        }
    }

    // 发送Hyperlinks_Move接受参数
    public static void showHyperlinks_Move(Context context, String source,
                                           String sourceUrl) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("pagenum", 1);
        intent.putExtra("navigatoin", 0);
        intent.putExtra("url", sourceUrl);
        intent.putExtra("flag", 5);
        intent.putExtra("name", source);
        context.sendBroadcast(intent);
    }

    // 发送Loading_NoMove接受参数
    public static void showLoading_NoMove(Context context, String sourceUrl) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 0);
        Intent intent = new Intent();
        intent.setAction("loadLeft" + context.getString(R.string.about_title));
        intent.putExtra("sourceUrl", sourceUrl);
        intent.putExtra("flag", 1);
        context.sendBroadcast(intent);
    }

    // 发送showNoLoading_Move接受参数
    public static void showNoLoading_Move(Context context, String source,
                                          String sourceUrl) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("pagenum", 1);
        intent.putExtra("navigatoin", 0);
        intent.putExtra("url", sourceUrl);
        intent.putExtra("name", source);
        intent.putExtra("flag", 6);
        context.sendBroadcast(intent);
    }

    // 发送showLoading_Move接受参数
    public static void showLoading_Move(Context context, String source,
                                        String sourceUrl) {
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("pagenum", 1);
        intent.putExtra("navigatoin", 0);
        intent.putExtra("url", sourceUrl);
        intent.putExtra("name", source);
        intent.putExtra("flag", 6);
        context.sendBroadcast(intent);
    }

    // 快捷方式是否存在
    private static boolean hasShortcut(Context context, String source) {
        int versionLevel = VERSION.SDK_INT;
        String AUTHORITY = "com.android.launcher.settings";
        // 2.2以上的系统的文件文件名字是不一样的
        if (versionLevel > 8) {
            AUTHORITY = "com.android.launcher2.settings";
        } else {
            AUTHORITY = "com.android.launcher.settings";
        }
        boolean isInstallShortcut = false;
        ContentResolver cr = context.getContentResolver();
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,
                new String[]{"title", "iconResource"}, "title=?",
                new String[]{source}, null);
        if (c != null && c.getCount() > 0 && c.moveToFirst()) {
            c.close();
            isInstallShortcut = true;
        }
        return isInstallShortcut;
    }

    // 创建createDeskShortCut实现
    public static void showcreateDeskShortCut(Context context, String source,
                                              String sourceID, String iniPATH) {
        boolean isInstallShortcut = false;
        isInstallShortcut = UIHelper.hasShortcut(context, source);
        Log.i("coder", "------createShortCut--------");
        if (!isInstallShortcut) {
            // 创建快捷方式的Intent
            Intent shortcutIntent = new Intent(
                    "com.android.launcher.action.INSTALL_SHORTCUT");
            // 不允许重复创建
            shortcutIntent.putExtra("duplicate", false);
            // 需要现实的名称
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, source);
            // 快捷图片
            int drawable = UIHelper.sourceId_drawable(sourceID);
            Parcelable icon = ShortcutIconResource.fromContext(context,
                    drawable);
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
            Intent intent = new Intent(context, LanuchActivity.class);
            // 下面两个属性是为了当应用程序卸载时桌面 上的快捷方式会删除
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            // 需要关键参数
            intent.putExtra("flag", 1);
            intent.putExtra("ResName", sourceID);
            intent.putExtra("tempUrl", source);
            intent.putExtra("iniPath", iniPATH);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // 点击快捷图片，运行的程序主入口
            shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            // 发送广播。OK
            context.sendBroadcast(shortcutIntent);
            // Toast.makeText(context, source + "：快捷方式创建成功", 1).show();
        } else {
            Toast.makeText(context, source + "：快捷方式已存在", Toast.LENGTH_LONG).show();
        }
    }

    // 删除createDeskShortCut实现
    public static void showdeleteDeskShortCut(Context context, String source) {
        boolean isInstallShortcut = false;
        isInstallShortcut = UIHelper.hasShortcut(context, source);
        Log.i("coder", "------deleteDeskShortCut--------");
        if (isInstallShortcut) {
            Intent shortcut = new Intent(
                    "com.android.launcher.action.UNINSTALL_SHORTCUT");
            // 快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, source);
            ComponentName comp = new ComponentName(context.getPackageName(),
                    context.getPackageName() + ".activity.LanuchActivity");
            Intent intent = new Intent();
            intent.setComponent(comp);
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
            context.sendBroadcast(shortcut);
        }
    }

    // 打开细览页的js
    public static void showDetailPage(Context context, String source,
                                      String sourceUrl, int num, int count) {
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        int Promptflag = Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                "APPSHOW", "Prompt", "1", (byte) 0));
        int detailshowflag = Integer.parseInt(m_iniFileIO.getIniString(
                appNewsFile, "APPSHOW", "DetailShow", "0", (byte) 0));
        if (detailshowflag == 1 && Promptflag == 1) {
            Intent intent = new Intent();
            intent.setAction("loadView"
                    + context.getString(R.string.about_title));
            intent.putExtra("flag", 8);
            intent.putExtra("author", 1);
            intent.putExtra("tempUrl", sourceUrl);
            context.sendBroadcast(intent);
        } else if (detailshowflag == 2) {
            Intent intent = new Intent();
            intent.setAction("loadRight"
                    + context.getString(R.string.about_title));
            intent.putExtra("flag", 2);
            intent.putExtra("ResName", source);
            intent.putExtra("tempUrl", sourceUrl);
            context.sendBroadcast(intent);
            Intent intent1 = new Intent();
            intent1.setAction("Action_main"
                    + context.getString(R.string.about_title));
            intent.putExtra("navigatoin", 0);
            intent1.putExtra("flag", 8);
            context.sendBroadcast(intent1);
        } else {
            Intent intent = new Intent();
            intent.setClass(context, DetailActivity.class);
            intent.putExtra("flag", 1);
            intent.putExtra("tempUrl", sourceUrl);
            intent.putExtra("position", num);
            intent.putExtra("count", count);
            intent.putExtra("ResName", source);
            intent.putExtra("winStrState", constants.STATEFORSEARCH);
            context.startActivity(intent);
        }
    }

    // 打开细览页的js
    public static void showNewDetailPage(Context context, String source,
                                         String sourceUrl, int num, int count) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, DetailActivity.class);
        intent.putExtra("flag", 1);
        intent.putExtra("tempUrl", sourceUrl);
        intent.putExtra("position", num);
        intent.putExtra("count", count);
        intent.putExtra("ResName", source);
        intent.putExtra("winStrState", constants.STATEFORSEARCH);
        context.startActivity(intent);
    }

    // 打开设置页的js
    public static void showSettingPage(Context context) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent = new Intent();
        intent.setClass(context, SetUpActivity.class);
        context.startActivity(intent);
    }

    // 收藏结果返回
    public static void showFavouriteflag(Context context, int num) {
        if (num == 1) {
            Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
        } else if (num == 2) {
            Toast.makeText(context, "已收藏", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
        }
    }

    // 删除结果返回
    public static void showDeleteflag(Context context, int num) {

        if (num == 1) {
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }

    }

    // 设置主窗口标题
    public static void showMainTitle(Context context, String Title) {
        // setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 10);
        intent.putExtra("name", Title);
        context.sendBroadcast(intent);
    }

    // 设置左窗口标题
    public static void showLeftTitle(Context context, String Title) {
        // setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setAction("loadLeft" + context.getString(R.string.about_title));
        intent.putExtra("flag", 2);
        intent.putExtra("name", Title);
        context.sendBroadcast(intent);
    }

    // 设置右窗口标题
    public static void showRightTitle(Context context, String Title) {
        // setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setAction("loadRight" + context.getString(R.string.about_title));
        intent.putExtra("flag", 3);
        intent.putExtra("name", Title);
        context.sendBroadcast(intent);
    }

    // 打开服务设置页的js
    public static void showLocalSettingpage(Context context) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, TerminalSetupActivity.class);
        context.startActivity(intent);
    }

    // 打开手机系统设置
    public static void showMobileSettingpage(Context context) {
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, TerminalSetupActivity.class);
        context.startActivity(intent);
    }

    // 打开概览页面
    public static void showNewOverviewPage(Context context, String sourceUrl,
                                           String source) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, OverviewActivity.class);
        intent.putExtra("tempUrl", sourceUrl);
        intent.putExtra("ResName", source);
        context.startActivity(intent);
    }

    // 打开概览页面
    public static void showOverviewPage(Context context, String source,
                                        String sourceUrl) {
        // TODO Auto-generated method stub
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (false == webRoot.endsWith("/")) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (!webRoot.endsWith("/")) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        int detailshowflag = Integer.parseInt(m_iniFileIO.getIniString(
                appNewsFile, "APPSHOW", "DetailShow", "0", (byte) 0));
        int showinflag = Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                "APPSHOW", "DetailShowIn", "1", (byte) 0));
        if (detailshowflag == 0) {
            UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
            Intent intent = new Intent();
            intent.setClass(context, OverviewActivity.class);
            intent.putExtra("tempUrl", sourceUrl);
            intent.putExtra("ResName", source);
            context.startActivity(intent);
        } else {
            if (showinflag == 1) {
                UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load",
                        1);
                Intent intent = new Intent();
                intent.setClass(context, OverviewActivity.class);
                intent.putExtra("tempUrl", sourceUrl);
                intent.putExtra("ResName", source);
                context.startActivity(intent);
            } else if (showinflag == 2) {
                if (detailshowflag == 1) {
                    UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM,
                            "load", 1);
                    Intent intent = new Intent();
                    intent.setAction("loadRight"
                            + context.getString(R.string.about_title));
                    intent.putExtra("flag", 2);
                    intent.putExtra("tempUrl", sourceUrl);
                    context.sendBroadcast(intent);
                    Intent intent1 = new Intent();
                    intent1.setAction("Action_main"
                            + context.getString(R.string.about_title));
                    intent.putExtra("navigatoin", 0);
                    intent1.putExtra("flag", 8);
                    context.sendBroadcast(intent1);
                } else if (detailshowflag == 2) {
                    int Promptflag = UIHelper.getShareperference(context,
                            constants.SAVE_LOCALMSGNUM, "Prompt", 1);
                    if (Promptflag == 1) {
                        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM,
                                "load", 1);
                        Intent intent = new Intent();
                        intent.setAction("loadView"
                                + context.getString(R.string.about_title));
                        intent.putExtra("flag", 8);
                        intent.putExtra("author", 1);
                        intent.putExtra("tempUrl", sourceUrl);
                        context.sendBroadcast(intent);
                    } else {
                        Toast.makeText(context, "底部信息区不可用", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }
        }
    }

    /**
     * 显示窗口设置界面
     *
     * @param context
     */
    public static void showSeekBar(Context context, int flag) {
        Intent intent = new Intent(context, NewSeekBarActivity.class);
        intent.putExtra("flag", flag);
        context.startActivity(intent);
    }

    /**
     * 退出对话框
     *
     * @param context
     */
    public static void showQuitDialog(final Context context) {
        new Builder(context)
                .setTitle(R.string.app_name)
                .setIcon(null)
                .setCancelable(false)
                .setMessage(R.string.alert_quit_confirm)
                .setPositiveButton(R.string.alert_yes_button,
                        new OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                setSharePerference(context,
                                        Constants.SHARED_PREFERENCE_NAME,
                                        "appOn", 0);
                                StartTbsweb.Startapp(context, 0);
                                Intent intent = new Intent();
                                intent.setAction(context.getString(R.string.ServerName));//你定义的service的action
                                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                context.stopService(intent);
                                intent = new Intent();
                                intent.setAction(context.getString(R.string.WebServerName));//你定义的service的action
                                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                context.stopService(intent);
                                intent = new Intent();
                                intent.setAction(context.getString(R.string.TbsTaskServer));//你定义的service的action
                                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                context.stopService(intent);
                                intent = new Intent();
                                intent.setAction(context.getString(R.string.ServerName1));//你定义的service的action
                                intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                context.stopService(intent);
                                int tmpCount = getShareperference(context, "tmp", "tmpCount", 0);
                                for (int i = 0; i < tmpCount; i++) {
                                    FileUtils.deleteFile(getShareperference(context, "tmp", "tmpFile" + i,
                                            ""));
                                }
                                setSharePerference(context, "tmp", "tmpCount", 0);
                                MyActivity.getInstance().AppExit(context);
                            }
                        })
                .setNegativeButton(R.string.alert_no_button,
                        new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public static int showLoginDialog(Context context, int action) {
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (action == 0) {
            m_iniFileIO.writeIniString(userIni, "Login", "LoginFlag", "0");
            new LoginDialog(context).show();
        } else if (action == 1) {
            new LoginDialog(context).show();
        }
        return Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginFlag", "0", (byte) 0));
    }

    /**
     * 显示路径选择对话框
     *
     * @param context
     */
    public static void showFilePathDialog(Context context, int count,
                                          String defPath, PathChooseDialog.ChooseCompleteListener listener) {
        new PathChooseDialog(context, count, defPath, listener).show();
    }

    /**
     * 显示路存储径选择对话框
     *
     * @param context
     */
    public static void showStoragePathDialog(Context context,
                                             String defPath, StorageChooseDialog.ChooseCompleteListener listener) {
        new StorageChooseDialog(context, defPath, listener).show();
    }

    /**
     * 完全退出对话框
     *
     * @param context
     */
    public static void showAllQuitDialog(final Context context) {
        new Builder(context)
                .setTitle(R.string.app_name)
                .setIcon(null)
                .setCancelable(false)
                .setMessage(R.string.alert_quit_all)
                .setPositiveButton(R.string.alert_yes_button,
                        new OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                setSharePerference(context,
                                        Constants.SHARED_PREFERENCE_NAME,
                                        "appOn", 0);
                                StartTbsweb.Startapp(context, 0);
                                ServiceManager serviceManager = new ServiceManager(
                                        context);
                                serviceManager.stopAllService();
                                int tmpCount = getShareperference(context, "tmp", "tmpCount", 0);
                                for (int i = 0; i < tmpCount; i++) {
                                    FileUtils.deleteFile(getShareperference(context, "tmp", "tmpFile" + i,
                                            ""));
                                }
                                setSharePerference(context, "tmp", "tmpCount", 0);
                                MyActivity.getInstance().AppExit(context);
                            }
                        })
                .setNegativeButton(R.string.alert_no_button,
                        new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    /**
     * 显示应用编辑对话框
     *
     * @param context
     */
    public static void showAppEditDialog(Context context, String FileName,
                                         String AppName, String ResId, int flag) {
        new AppEditDialog(context, FileName, AppName, ResId, flag).show();
    }

    /**
     * 显示文件选择对话框
     *
     * @param context
     */
    public static void showFileChooseDialog(Context context,
                                            ApkChooseDialog.ChooseListener listener) {
        new ApkChooseDialog(context, listener).show();
    }


    /**
     * 显示上传文件选择对话框
     * <p>
     * 上传文件到网盘
     *
     * @param context
     */
    public static void showUpFileDialog(Context context, String Path) {
        // new UpFileDialog(context, Path).show();
        Intent intent = new Intent();
        intent.setClass(context, ChoseFileToSkyDriveDialog.class);
        intent.putExtra("Path", Path);
        context.startActivity(intent);
    }

    /**
     * 调用系统安装了的应用分享
     *
     * @param context
     * @param title
     */
    public static void showShareMore(Context context, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + UIHelper.Nowurl);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 调用系统安装了的应用分享
     *
     * @param context
     * @param title
     * @param url
     */
    public static void showShareMore(Context context, String title, String url) {
        if (url.isEmpty())
            url = UIHelper.Nowurl;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
        intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
        context.startActivity(Intent.createChooser(intent, "选择分享"));
    }

    /**
     * 调用系统安装了的浏览器
     *
     * @param context
     * @param url
     */
    public static void showBrowsere(Context context, String url) {
        // if (StringUtils.isEmpty(Nowurl)) {
        UIHelper.Nowurl = url;
        // }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(UIHelper.Nowurl);
        intent.setData(content_url);
        context.startActivity(Intent.createChooser(intent, "选择浏览器打开"));
    }

    public static void showOriginalPage(Context context, String sourceUrl) {
        // TODO Auto-generated method stub
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        int detailshowflag = Integer.parseInt(m_iniFileIO.getIniString(
                appNewsFile, "APPSHOW", "DetailShow", "0", (byte) 0));
        int showinflag = Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                "APPSHOW", "DetailShowIn", "1", (byte) 0));
        if (detailshowflag == 0) {
            UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
            Intent intent = new Intent();
            intent.setClass(context, BrowserActivity.class);
            intent.putExtra("tempUrl", sourceUrl);
            context.startActivity(intent);
        } else {
            if (showinflag == 1) {
                UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load",
                        1);
                Intent intent = new Intent();
                intent.setClass(context, BrowserActivity.class);
                intent.putExtra("tempUrl", sourceUrl);
                context.startActivity(intent);
            } else if (showinflag == 2) {
                if (detailshowflag == 1) {
                    UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM,
                            "load", 1);
                    Intent intent = new Intent();
                    intent.setAction("loadRight"
                            + context.getString(R.string.about_title));
                    intent.putExtra("flag", 2);
                    intent.putExtra("tempUrl", sourceUrl);
                    context.sendBroadcast(intent);
                    Intent intent1 = new Intent();
                    intent1.setAction("Action_main"
                            + context.getString(R.string.about_title));
                    intent.putExtra("navigatoin", 0);
                    intent1.putExtra("flag", 8);
                    context.sendBroadcast(intent1);
                } else if (detailshowflag == 2) {
                    int Promptflag = UIHelper.getShareperference(context,
                            constants.SAVE_LOCALMSGNUM, "Prompt", 1);
                    if (Promptflag == 1) {
                        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM,
                                "load", 1);
                        Intent intent = new Intent();
                        intent.setAction("loadView"
                                + context.getString(R.string.about_title));
                        intent.putExtra("flag", 8);
                        intent.putExtra("author", 1);
                        intent.putExtra("tempUrl", sourceUrl);
                        context.sendBroadcast(intent);
                    } else {
                        Toast.makeText(context, "底部信息区不可用", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        }
    }

    public static void showNewOriginalPage(Context context, String sourceUrl) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, BrowserActivity.class);
        intent.putExtra("tempUrl", sourceUrl);
        context.startActivity(intent);
    }

    protected static void setDefLib(Context cxt, String source, String sourceID) {
        IniFile m_iniFileIO = new IniFile();
        // TODO Auto-generated method stub
        String webRoot = UIHelper.getSoftPath(cxt);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += cxt.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(cxt, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        webRoot = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        m_iniFileIO.writeIniString(webRoot, "TBSAPP", "resname", sourceID);
        m_iniFileIO.writeIniString(webRoot, "TBSAPP", "AppName", source);

    }

    protected static void regist(Context cxt) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(cxt, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(cxt, RegisterActivity.class);
        cxt.startActivity(intent);
    }

    protected static void showMyCloud(Context cxt, String url) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(cxt, constants.SAVE_LOCALMSGNUM, "load", 1);
        if (StringUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.setClass(cxt, MyCloudActivity.class);
            cxt.startActivity(intent);
        } else if (url.equalsIgnoreCase("app_store")) {
            Intent intent = new Intent();
            intent.setClass(cxt, MyCloudActivity.class);
            intent.putExtra("flag", 1);
            cxt.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(cxt, MyCloudActivity.class);
            intent.putExtra("tempUrl", url);
            cxt.startActivity(intent);
        }
    }

    protected static void showSearchPage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(cxt, SearchActivityNew.class);
        cxt.startActivity(intent);

    }

    protected static void showSearchSetupPage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(cxt, SearchSetupActivity.class);
        cxt.startActivity(intent);

    }

    protected static void showPopMenu(Context cxt, String url) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(cxt, constants.SAVE_LOCALMSGNUM, "load", 1);
        if (StringUtils.isEmpty(url)) {
            Intent intent = new Intent();
            intent.setClass(cxt, PopMenuActivity.class);
            cxt.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(cxt, PopMenuActivity.class);
            intent.putExtra("tempUrl", url);
            cxt.startActivity(intent);
        }
    }

    protected static void showTaskPage(Context cxt) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(cxt, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(cxt, NetTaskActivity.class);
        cxt.startActivity(intent);
    }

    protected static void showMyApplication(Context cxt) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(cxt, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(cxt, MyAppsActivity.class);
        cxt.startActivity(intent);
    }

    protected static void login(Context cxt) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(cxt, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(cxt);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += cxt.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(cxt, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = cxt.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 1) {
            Intent intent = new Intent();
            intent.setClass(cxt, MyPageActivity.class);
            cxt.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(cxt, LoginActivity.class);
            cxt.startActivity(intent);
        }
    }

    protected static void detail_do(Context cxt, String source,
                                    String sourceUrl, int num, int count, String action) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("flag", 3);
        intent.putExtra("tempUrl", sourceUrl);
        intent.putExtra("position", num);
        intent.putExtra("count", count);
        intent.putExtra("ResName", source);
        cxt.sendBroadcast(intent);
    }

    /**
     * 添加网页js与webview交互
     */
    public static void addJavascript(final Context cxt, WebView wv) {
        wv.addJavascriptInterface(new JavaScriptInterface()
        {
            // Like NoUrl.
            @JavascriptInterface
            public void StartApp(String fileName) {
                UIHelper.StartApp(cxt, fileName);
            }

            @JavascriptInterface
            public void Hyperlinks_Move(String source, String sourceUrl) {
                if (source != null)
                    showHyperlinks_Move(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void Loading_NoMove(String sourceUrl) {
                if (sourceUrl != null)
                    showLoading_NoMove(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void NoLoading_Move(String source, String sourceUrl) {
                showNoLoading_Move(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void Loading_Move(String source, String sourceUrl) {
                showLoading_Move(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void loadsearch(String sourceUrl, String word) {
                UIHelper.loadsearch(cxt, sourceUrl, word);
            }

            @JavascriptInterface
            public void Collect(String url, String Title, String type, String content, String pic) {
                UIHelper.Collect(cxt, url, Title, type, content, pic);
            }

            @JavascriptInterface
            public void Login() {
                login(cxt);
            }

            @JavascriptInterface
            public void SynLanding(String LoginId, String account) {
                UIHelper.SynLanding(cxt, LoginId, account);
            }

            @JavascriptInterface
            public String getLoginInfo() {
                return UIHelper.getLoginInfo(cxt);
            }

            @JavascriptInterface
            public String getDeviceInfo() {
                return UIHelper.getDeviceInfo(cxt);
            }

            @JavascriptInterface
            public int showLoginDialog(int action) {
                return UIHelper.showLoginDialog(cxt, action);
            }

            @JavascriptInterface
            public void Regist() {
                regist(cxt);
            }

            @JavascriptInterface
            public void showMyApplication() {
                UIHelper.showMyApplication(cxt);
            }

            @JavascriptInterface
            public void showMyCloud() {
                UIHelper.showMyCloud(cxt, null);
            }

            @JavascriptInterface
            public void showSearchPage() {
                UIHelper.showSearchPage(cxt);
            }

            @JavascriptInterface
            public void showMySkyDrive() {
                UIHelper.showMySkyDrive(cxt, null);
            }

            //           @JavascriptInterface
//            public void showMyEmail() {
//                UIHelper.showMyEmail(cxt);
//            }
            @JavascriptInterface
            public void showVideoORAudio(int type, String code) {
                switch (type) {
                    case 1:
                        showAudio(cxt, code);
                        break;
                    case 2:
                        showShortVideo(cxt, code);
                        break;
                    case 3:
                        showFilm(cxt, code);
                        break;
                    case 4:
                        showVideo(code);
                        break;
                    case 5:
                        showLive(code);
                        break;
                    case 6:
                        showAlbum(cxt, code);
                        break;
                }
            }

            @JavascriptInterface
            public void showMyResource() {
                UIHelper.showMyResource(cxt);
            }

            @JavascriptInterface
            public void createDeskShortCut(String source, String sourceID,
                                           String iniPATH) {
                showcreateDeskShortCut(cxt, source, sourceID, iniPATH);
            }

            @JavascriptInterface
            public void OriginalLink(String sourceUrl) {
                showOriginalPage(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void NewOriginalLink(String sourceUrl) {
                showNewOriginalPage(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void startTbsweb() {
                StartTbsweb.Startapp(cxt, 1);
            }

            @JavascriptInterface
            public void showDetail(int num, int count, String source,
                                   String sourceUrl) {
                showDetailPage(cxt, source, sourceUrl, num, count);
            }

            @JavascriptInterface
            public void showNewDetail(int num, int count, String source,
                                      String sourceUrl) {
                showNewDetailPage(cxt, source, sourceUrl, num, count);
            }

            @JavascriptInterface
            public void showSettingPage() {
                UIHelper.showSettingPage(cxt);
            }

            @JavascriptInterface
            public void showDisplaySettingPage() {
                UIHelper.showDisplaySettingPage(cxt);
            }

            @JavascriptInterface
            public void showAppearanceSettingPage() {
                UIHelper.showAppearanceSettingPage(cxt);
            }

            @JavascriptInterface
            public void showNetworkSettingPage() {
                UIHelper.showNetworkSettingPage(cxt);
            }

            @JavascriptInterface
            public void showMobileNetworkSettingPage() {
                UIHelper.showMobileNetworkSettingPage(cxt);
            }

            @JavascriptInterface
            public void showApplicationSettingPage() {
                UIHelper.showApplicationSettingPage(cxt);
            }

            @JavascriptInterface
            public void showApplicationManagerPage() {
                UIHelper.showApplicationManagerPage(cxt);
            }

            @JavascriptInterface
            public void showSubscribePage() {
                UIHelper.showSubscribePage(cxt);
            }

            @JavascriptInterface
            public void showInfoPage() {
                UIHelper.showInfoPage(cxt);
            }

            @JavascriptInterface
            public void showInformationPushPage() {
                UIHelper.showInformationPushPage(cxt);
            }

            @JavascriptInterface
            public void showOfflineDownloadPage() {
                UIHelper.showOfflineDownloadPage(cxt);
            }

            @JavascriptInterface
            public void showLogManagerPage() {
                UIHelper.showLogManagerPage(cxt);
            }

            @JavascriptInterface
            public void showCommentManagerPage() {
                UIHelper.showCommentManagerPage(cxt);
            }

            @JavascriptInterface
            public void showDeviceManagerPage() {
                UIHelper.showDeviceManagerPage(cxt);
            }

            @JavascriptInterface
            public void showFileManagerPage() {
                UIHelper.showFileManagerPage(cxt);
            }

            @JavascriptInterface
            public void showFeedBackPage() {
                UIHelper.showFeedBackPage(cxt);
            }

            // public void showFeedBackPage() {
            // UIHelper.showPushPage(cxt);
            // }
            @JavascriptInterface
            public void showAboutPage() {
                UIHelper.showAboutPage(cxt);
            }

            @JavascriptInterface
            public void showQuitAllDialog() {
                showAllQuitDialog(cxt);
            }

            @JavascriptInterface
            public void showQuitDialog() {
                UIHelper.showQuitDialog(cxt);
            }

            @JavascriptInterface
            public void showVideo(String code) {
                UIHelper.showLocalVideo(cxt, code);
            }

            @JavascriptInterface
            public void showOnlineVideo(String path, String title, String chapter) {
                UIHelper.showOnlineVideo(cxt, path, title, chapter);
            }

            @JavascriptInterface
            public void showLive(String info) {
                UIHelper.showLive(cxt, info);
            }

            @JavascriptInterface
            public void showVideoPublish() {
                showMyPublish(cxt);
            }

            @JavascriptInterface
            public void showWebVideo(String category, String path, String title, String chapter) {
                UIHelper.showWebVideo(cxt, category, path, title, chapter);
            }

            @JavascriptInterface
            public void showLocalSettingpage() {
                UIHelper.showLocalSettingpage(cxt);
            }

            @JavascriptInterface
            public void showOverview(String source, String sourceUrl) {
                showOverviewPage(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void showPopWindow(String sourceUrl) {
                showPopMenu(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void showLeftWindow(int action) {
                UIHelper.showLeftWindow(cxt, action, null);
            }

            @JavascriptInterface
            public void showRightWindow(int action) {
                UIHelper.showRightWindow(cxt, action, null);
            }

            @JavascriptInterface
            public void showMainMenu() {
                UIHelper.showMainMenu(cxt);
            }

//            @JavascriptInterface
//            public void showMainSetMenu() {
//                UIHelper.showMainSetMenu(cxt);
//            }

            @JavascriptInterface
            public void getFavouriteFlag(int num) {
                showFavouriteflag(cxt, num);
            }

            @JavascriptInterface
            public void getDeleteFlag(int num) {
                showDeleteflag(cxt, num);
            }

            @JavascriptInterface
            public void getTitle(String name) {
                showShareMore(cxt, name);
            }

            @JavascriptInterface
            public void share(String name, String url) {
                showShareMore(cxt, name, url);
            }

            @JavascriptInterface
            public void getHtmlSource(String category, String title, String urlPath) {
                JsoupExam.getHtmlSource(category, title, urlPath, cxt);
            }

            @JavascriptInterface
            public boolean isHtmlSaven(String category, String urlPath) {
                return JsoupExam.isHtmlSaven(category, urlPath, cxt);
            }

            @JavascriptInterface
            public void showToast(String content) {
                Toast.makeText(cxt, content, Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void setActionName(String action, String name) {
                UIHelper.setActionName(cxt, action, name);
            }

            @JavascriptInterface
            public void setRightTitle(String title) {
                showRightTitle(cxt, title);
            }

            @JavascriptInterface
            public void setLeftTitle(String title) {
                showLeftTitle(cxt, title);
            }

            @JavascriptInterface
            public void setMainTitle(String title) {
                showMainTitle(cxt, title);
            }

            @JavascriptInterface
            public void setDefLib(String source, String sourceID) {
                UIHelper.setDefLib(cxt, source, sourceID);
            }

            @JavascriptInterface
            public void CloseDetail() {
                showDetailDown(cxt);
            }

            @JavascriptInterface
            public void ClosePop() {
                showPopDown(cxt);
            }

            @JavascriptInterface
            public void downloadFile(String url, String savePath,
                                     boolean ishowProgress) {
                FileUtils.downFile(cxt, url, savePath, ishowProgress);
            }

            @JavascriptInterface
            public void backgroundDownFile(final String url, final String savePath) {
                new Thread()
                {
                    @Override
                    public void run() {
                        String webRoot = UIHelper.getStoragePath(cxt);
                        webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + savePath;
                        HttpConnectionUtil connection = new HttpConnectionUtil();
                        connection.downFile(UIHelper.encodeGB(url), webRoot);
                    }

                }.start();
            }

            @JavascriptInterface
            public void openFile(String Type, String Path) {
                showOpenFile(cxt, Type, Path);
            }

            @JavascriptInterface
            public void updateData(String category) {
                FileUtils.updateData(cxt, category);
            }

            @JavascriptInterface
            public void uploadFile(String category) {
                showUpFileDialog(cxt, category);
            }

            @JavascriptInterface
            public int CheckVersion(String fileName, String timer) {
                return Updateapp.CheckVersion(cxt, fileName, timer);
            }

            @JavascriptInterface
            public void OpenWebFile(String fileInfo, String path) {
                openWebFile(cxt, fileInfo, path);
            }

            @JavascriptInterface
            public int CheckFileVersion(String fileName, String timer) {
                return Updateapp.CheckFileVersion(cxt, fileName, timer);
            }

            @JavascriptInterface
            public void FileStoreInfo(String dirPath, String fileinfo) {
                Updateapp.FileStoreInfo(cxt, dirPath, fileinfo);
            }

            @JavascriptInterface
            public void openApp(String appName, String sourceID) {
                UIHelper.openApp(cxt, appName, sourceID);
            }

            @JavascriptInterface
            public void wxPay(String orderId, String money) {
                UIHelper.wxPay(cxt, orderId, money);
            }

            @JavascriptInterface
            public void aliPay(String orderId, String money) {
                UIHelper.aliPay(cxt, orderId, money);
            }
        }, "TBS");
    }


    private static void showLive(Context cxt, String liveId) {
        Intent intent = new Intent();
        intent.setClass(cxt, LivePlayer.class);
        Bundle bundle = new Bundle();
        bundle.putString("liveId", liveId);
        intent.putExtras(bundle);
        cxt.startActivity(intent);
    }

    private static void showOnlineVideo(Context cxt, String path, String title, String fileinfo) {
        String videoPath = path;
        if (path.contains(":")) {
            videoPath = path.substring(path.lastIndexOf(":"));
            videoPath = videoPath.substring(videoPath.indexOf("/") + 1);
            videoPath = videoPath.substring(videoPath.indexOf("/") + 1);
        } else {
            Updateapp.FileStoreInfo(cxt, videoPath, fileinfo);
        }
        String webRoot = UIHelper.getStoragePath(cxt);
        webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + videoPath;

        try {
            JSONObject jsonObject = new JSONObject(fileinfo);
            String chapter = jsonObject.getString("chapter");
            String description = jsonObject.getString("description");
            String content = jsonObject.getString("content");
            String txtPath = jsonObject.getString("path");
            String pic = jsonObject.getString("pic");
            String fileCode = jsonObject.getString("fileCode");
            String relateExam = jsonObject.getString("relateExam");
            String relateExamUrl = jsonObject.getString("relateExamUrl");
            String relateKnowled = jsonObject.getString("relateKnowled");
            String relateKnowledUrl = jsonObject.getString("relateKnowledUrl");
            Intent intent = new Intent();
            intent.setClass(cxt, VideoPlayer.class);
            Bundle bundle = new Bundle();
            Video video = new Video();
            File file = new File(webRoot);
            if (file.isFile() && file.exists()) {
                video.setPath(videoPath);
            } else {
                video.setPath(path);
            }
            video.setTxtPath(txtPath);
            video.setChapter(chapter);
            video.setCode(fileCode);
            video.setTitle(title);
            video.setContent(content);
            video.setRelateKnowledUrl(relateKnowledUrl);
            video.setRelateKnowled(relateKnowled);
            video.setRelateExamUrl(relateExamUrl);
            video.setRelateExam(relateExam);
            video.setDescription(description);
            video.setAlbum(pic);
            bundle.putSerializable("video", video);
            intent.putExtras(bundle);
            cxt.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void showWebVideo(Context cxt, String category, String path, String title, String fileinfo) {
        String videoPath = path;
        String webRoot = UIHelper.getStoragePath(cxt);
        webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/";
        try {
            JSONObject jsonObject = new JSONObject(fileinfo);
            String fileCode = jsonObject.getString("fileCode");
            if (path.contains(":")) {
                videoPath = path.substring(path.lastIndexOf(":"));
                videoPath = videoPath.substring(videoPath.indexOf("/") + 1);
                videoPath = videoPath.substring(videoPath.indexOf("/") + 1);
            } else {
                IniFile iniFile = new IniFile();
                String iniPath = webRoot + category + "/" + category.substring(category.lastIndexOf("/") + 1, category
                        .length()) + ".ini";
                int count = Integer.parseInt(iniFile.getIniString(iniPath, "file", "count", "0", (byte) 0));
                for (int i = 1; i <= count; i++) {
                    if (fileCode.equalsIgnoreCase(iniFile.getIniString(iniPath, "file" + i, "code", "", (byte) 0))) {
                        String filePath = iniFile.getIniString(iniPath, "file" + i, "path", "",
                                (byte) 0);
                        videoPath = filePath + "/" + path;
                        webRoot = webRoot + videoPath;
                        break;
                    }
                }
            }
            String relateKnowledUrl = jsonObject.getString("relateKnowledUrl");
            Intent intent = new Intent();
            intent.setClass(cxt, VideoPlayerWeb.class);
            Bundle bundle = new Bundle();
            Video video = new Video();
            File file = new File(webRoot);

            if (file.isFile() && file.exists()) {
                video.setPath(videoPath);
            } else {
                video.setPath(path);
            }
            video.setCode(fileCode);
            video.setTitle(title);
            video.setRelateKnowledUrl(relateKnowledUrl);
            bundle.putSerializable("video", video);
            intent.putExtras(bundle);
            cxt.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void showLocalVideo(Context cxt, String code) {
        Intent intent = new Intent();
        intent.setClass(cxt, VideoPlayer.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "online");
        bundle.putString("sourceId", code);
        intent.putExtras(bundle);
        cxt.startActivity(intent);
    }

    private static void showShortVideo(Context cxt, String code) {
        Intent intent3 = new Intent();
        Bundle bundle3 = new Bundle();
        bundle3.putString("sourceId", code);
        bundle3.putString("type", "Online");
        intent3.putExtras(bundle3);
        intent3.setClass(cxt, ShortVideoPlayer.class);
        cxt.startActivity(intent3);
    }

    private static void showAudio(Context cxt, String code) {
        AppContext.getInstance().initMusic();
        Intent intent2 = new Intent();
        Bundle bundle2 = new Bundle();
        bundle2.putString("sourceId", code);
        intent2.putExtras(bundle2);
        intent2.setClass(cxt, SongPlayActivity.class);
        cxt.startActivity(intent2);
    }

    private static void showAlbum(Context cxt, String code) {
        AppContext.getInstance().initMusic();
        Intent intent2 = new Intent();
        Bundle bundle2 = new Bundle();
        bundle2.putString("sourceId", code);
        intent2.putExtras(bundle2);
        intent2.setClass(cxt, SongAlbumActivity.class);
        cxt.startActivity(intent2);
    }

    private static void showFilm(Context cxt, String code) {
        Intent intent5 = new Intent();
        Bundle bundle5 = new Bundle();
        bundle5.putString("sourceId", code);
        bundle5.putString("type", "Online");
        intent5.putExtras(bundle5);
        intent5.setClass(cxt, FilmActivity.class);
        cxt.startActivity(intent5);
    }

    public static void showOpenFile(Context cxt, String type, String path) {
        // Code in here
        String webRoot = getShareperference(cxt,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String Midurl = path;
        if (path.indexOf(":") > -1) {
            Midurl = path.substring(path.lastIndexOf(":"));
            Midurl = Midurl.substring(Midurl.indexOf("/") + 1);
        }
        if (path.indexOf("127.0.0.1") <= -1) {
            webRoot = "";
            Midurl = path;
        }
        type = type.toLowerCase();
        getMediaType mide = new getMediaType();
        mide.initReflect();
        int mediatype = mide.getMediaFileType(webRoot + Midurl);
        boolean isAudio = mide.isAudioFile(mediatype);
        boolean isVideo = mide.isVideoFile(mediatype);
        if (type.equalsIgnoreCase("pdf")) {
            PackageManager packageManager = cxt
                    .getPackageManager();
            Intent intent = packageManager
                    .getLaunchIntentForPackage("com.tbs.pdf");
            if (intent == null) {
                try {
                    String mimeType = MimeUtils
                            .guessMimeTypeFromExtension(type);
                    intent = new Intent();
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(
                            webRoot + Midurl)), mimeType);
                    cxt.startActivity(intent);
                } catch (Exception exp) {
                    Toast.makeText(cxt, "无可打开应用", Toast.LENGTH_LONG).show();
                }
            } else {
                Uri uri = Uri.fromFile(new File(webRoot
                        + Midurl));
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setComponent(new ComponentName("com.tbs.pdf",
                        "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                i.setData(uri);
                cxt.startActivity(i);
            }

        }
//        else if (isAudio) {
//            Intent intent = new Intent();
//            intent.setClass(cxt, AcsAudioAcitvity.class);
//            Bundle bundle = new Bundle();
//            Music music = new Music();
//            music.setUri(webRoot + Midurl);
//            bundle.putSerializable("music", music);
//            intent.putExtras(bundle);
//            cxt.startActivity(intent);
//        }
        else if (isVideo || type.equals("flv") || type.equals("flt")) {
            Intent intent = new Intent();
            intent.setClass(cxt, PlayerActivity.class);
            intent.putExtra("videoPath", webRoot + Midurl);
            cxt.startActivity(intent);
        } else {
            String mimeType = MimeUtils
                    .guessMimeTypeFromExtension(type);
            if (StringUtils.isEmpty(mimeType)) {
                try {
                    Uri uri = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            uri);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    cxt.startActivity(intent);
                } catch (Exception exp) {
                    Toast.makeText(cxt, "无可打开应用", Toast.LENGTH_LONG).show();
                }
            } else {
                try {
                    Intent intent = new Intent();
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(
                            webRoot + Midurl)), mimeType);
                    cxt.startActivity(intent);
                } catch (Exception exp) {
                    Toast.makeText(cxt, "无可打开应用", Toast.LENGTH_LONG).show();
                }
            }

        }
        //}
        //	});

    }

    protected static void SynLanding(Context cxt, String loginId, String account) {
        // TODO Auto-generated method stub
        LoginHelper.WebLogin(cxt, account, loginId);
    }

    protected static void showInfoPage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(cxt, MySubscribeActivity.class);
        cxt.startActivity(intent);
    }

    protected static void showScanPage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(cxt, ScanActivity.class);
        cxt.startActivity(intent);
    }

    protected static void showSubscribePage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(cxt, SubscribeActivity.class);
        cxt.startActivity(intent);
    }

    protected static void loadsearch(Context context, String sourceUrl,
                                     String word) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_search"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 1);
        intent.putExtra("tempUrl", sourceUrl);
        intent.putExtra("keyWord", word);
        context.sendBroadcast(intent);
    }

    protected static void showFeedBackPage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(cxt, FeedbackActivity.class);
        cxt.startActivity(intent);
    }

    public static void showPushPage(Context cxt) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(cxt, PushMsgActivity.class);
        cxt.startActivity(intent);
    }

//    protected static void showMainSetMenu(Context context) {
//        // TODO Auto-generated method stub
//        Intent intent = new Intent();
//        intent.setAction("Action_main"
//                + context.getString(R.string.about_title));
//        intent.putExtra("flag", 17);
//        context.sendBroadcast(intent);
//    }

    protected static void showMainMenu(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 16);
        context.sendBroadcast(intent);
    }

    protected static void showPromptWindow(Context context, int action,
                                           String url) {
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (StringUtils.isEmpty(url)) {
            if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                    "APPSHOW", "Prompt", "0", (byte) 0)) == 1) {
                Intent intent = new Intent();
                intent.setAction("loadView"
                        + context.getString(R.string.about_title));
                intent.putExtra("flag", 2);
                intent.putExtra("author", action);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 9);
                intent.putExtra("flag", 2);
                context.sendBroadcast(intent);
            }
        } else {
            if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                    "APPSHOW", "Prompt", "0", (byte) 0)) == 1) {
                Intent intent = new Intent();
                intent.setAction("loadView"
                        + context.getString(R.string.about_title));
                intent.putExtra("flag", 8);
                intent.putExtra("author", 1);
                intent.putExtra("tempUrl", url);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 8);
                intent.putExtra("url", url);
                intent.putExtra("flag", 2);
                context.sendBroadcast(intent);
            }
        }
    }

    protected static void showRightWindow(Context context, int action,
                                          String url) {
        // TODO Auto-generated method stub
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (StringUtils.isEmpty(url)) {
            if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                    "APPSHOW", "ShowMore", "0", (byte) 0)) == 1) {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("flag", 8);
                intent.putExtra("navigatoin", action);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 7);
                intent.putExtra("flag", 2);
                context.sendBroadcast(intent);
            }
        } else {
            if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                    "APPSHOW", "ShowMore", "0", (byte) 0)) == 1) {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 2);
                intent.putExtra("url", url);
                intent.putExtra("flag", 8);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 8);
                intent.putExtra("url", url);
                intent.putExtra("flag", 2);
                context.sendBroadcast(intent);
            }
        }
    }

    protected static void showLeftWindow(Context context, int action, String url) {
        // TODO Auto-generated method stub
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (StringUtils.isEmpty(url)) {
            if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                    "APPSHOW", "ShowMenu", "0", (byte) 0)) == 1) {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", action);
                intent.putExtra("flag", 9);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 6);
                intent.putExtra("flag", 2);
                context.sendBroadcast(intent);
            }
        } else {
            if (Integer.parseInt(m_iniFileIO.getIniString(appNewsFile,
                    "APPSHOW", "ShowMenu", "0", (byte) 0)) == 1) {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 2);
                intent.putExtra("url", url);
                intent.putExtra("flag", 9);
                context.sendBroadcast(intent);
            } else {
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("navigatoin", 8);
                intent.putExtra("url", url);
                intent.putExtra("flag", 2);
                context.sendBroadcast(intent);
            }
        }

    }

    // "tbs:"动作函数
    // tbs:setMainTitle("gtgtggggg");
    public static boolean TbsMotion(Context context, String action) {
        if (action.indexOf(" ") > 0 && !action.endsWith(" ")) {
            String forward = action.substring(0, action.indexOf(" "));
            String backward = action.substring(action.indexOf(" ") + 1);
            if (forward.equalsIgnoreCase("left")) {
                showLeftWindow(context, 1, backward);
            } else if (forward.equalsIgnoreCase("right")) {
                showRightWindow(context, 1, backward);
            } else if (forward.equalsIgnoreCase("extend")) {
                showPromptWindow(context, 1, backward);
            } else if (forward.equalsIgnoreCase("pop")) {
                showPopMenu(context, backward);
            } else if (forward.equalsIgnoreCase("new")) {
                UIHelper.showNewOverviewPage(context, backward,
                        getShareperference(context,
                                constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
            } else if (forward.equalsIgnoreCase("view")) {
                UIHelper.showNewOriginalPage(context, backward);
            } else if (forward.equalsIgnoreCase("update_data")) {
                FileUtils.updateData(context, backward);
            } else {
                return true;
            }
            return false;
        } else {
            if (action.equalsIgnoreCase("setup")) {
                showSettingPage(context);
            } else if (action.equalsIgnoreCase("login")) {
                login(context);
            } else if (action.equalsIgnoreCase("regist")) {
                regist(context);
            } else if (action.equalsIgnoreCase("display_setup")) {
                showDisplaySettingPage(context);
            } else if (action.equalsIgnoreCase("appearance_setup")) {
                showAppearanceSettingPage(context);
            } else if (action.equalsIgnoreCase("network_setup")) {
                showNetworkSettingPage(context);
            } else if (action.equalsIgnoreCase("weixin_setup")) {
                showWechatSettingPage(context);
            } else if (action.equalsIgnoreCase("weixin_manager")) {
                showWechatManagerPage(context);
            } else if (action.equalsIgnoreCase("application_setup")) {
                showApplicationSettingPage(context);
            } else if (action.equalsIgnoreCase("application_manager")) {
                showApplicationManagerPage(context);
            } else if (action.equalsIgnoreCase("task")) {
                showTaskPage(context);
            } else if (action.equalsIgnoreCase("information_push")) {
                showInformationPushPage(context);
            } else if (action.equalsIgnoreCase("offline_download")) {
                showOfflineDownloadPage(context);
            } else if (action.equalsIgnoreCase("log_manager")) {
                showLogManagerPage(context);
            } else if (action.equalsIgnoreCase("comment_manager")) {
                showCommentManagerPage(context);
            } else if (action.equalsIgnoreCase("device_manager")) {
                showDeviceManagerPage(context);
            } else if (action.equalsIgnoreCase("file_manager")) {
                showFileManagerPage(context);
            } else if (action.equalsIgnoreCase("service_setup")) {
                showLocalSettingpage(context);
            } else if (action.equalsIgnoreCase("application")) {
                showMyApplication(context);
            } else if (action.equalsIgnoreCase("file_source")) {
                showMyFileSource(context);
            } else if (action.equalsIgnoreCase("subscribe")) {
                showSubscribePage(context);
            } else if (action.equalsIgnoreCase("info")) {
                showInfoPage(context);
            } else if (action.equalsIgnoreCase("scan")) {
                showScanPage(context);
            } else if (action.equalsIgnoreCase("my_collect")) {
                showMyCollect(context);
            } else if (action.equalsIgnoreCase("my_wallet")) {
                showMyWallet(context);
            } else if (action.equalsIgnoreCase("cloud")) {
                showMyCloud(context, null);
            } else if (action.equalsIgnoreCase("app_store")) {
                showMyCloud(context, "app_store");
            } else if (action.equalsIgnoreCase("source_store")) {
                showSourceStore(context);
            } else if (action.equalsIgnoreCase("feedback")) {
                showFeedBackPage(context);
            } else if (action.equalsIgnoreCase("send_push")) {
                showPushPage(context);
            } else if (action.equalsIgnoreCase("update")) {
                showUpDatePage(context);
            } else if (action.equalsIgnoreCase("about")) {
                showAboutPage(context);
            } else if (action.equalsIgnoreCase("quit")) {
                showQuitDialog(context);
            } else if (action.equalsIgnoreCase("quit_all")) {
                showAllQuitDialog(context);
            } else if (action.equalsIgnoreCase("email")) {
                UIHelper.showMyEmail(context);
            } else if (action.equalsIgnoreCase("chat")) {
                UIHelper.showMyChat(context);
            } else if (action.equalsIgnoreCase("chat_address")) {
                UIHelper.showMyChatAddress(context);
            } else if (action.equalsIgnoreCase("chat_message")) {
                UIHelper.showMyChatMessage(context);
            } else if (action.equalsIgnoreCase("disk")) {
                UIHelper.showMyDisk(context);
            } else if (action.equalsIgnoreCase("player")) {
                showMyPlayer(context);
            } else if (action.equalsIgnoreCase("publish")) {
                showMyPublish(context);
            } else if (action.equalsIgnoreCase("reader")) {
                showMyReader(context);
            } else if (action.equalsIgnoreCase("mcs")) {
                showTbsMcs(context);
            } else if (action.equalsIgnoreCase("skydrive")) {
                showMySkyDrive(context, null);
//            } else if (action.equalsIgnoreCase("skydrive_manager")) {
//                UIHelper.showMySkyDriveManager(context, null);
//            } else if (action.equalsIgnoreCase("skydrive_setup")) {
//                UIHelper.showMySkyDriveSetup(context, null);
            } else if (action.equalsIgnoreCase("friend_circle")) {
                showCircle(context);
            } else if (action.equalsIgnoreCase("my_circle")) {
                showMyCircle(context);
            } else if (action.equalsIgnoreCase("my_live")) {
                showMyLive(context);
            } else if (action.equalsIgnoreCase("left")) {
                showLeftWindow(context, 1, null);
            } else if (action.equalsIgnoreCase("right")) {
                showRightWindow(context, 1, null);
            } else if (action.equalsIgnoreCase("left0")) {
                showLeftWindow(context, 0, null);
            } else if (action.equalsIgnoreCase("right0")) {
                showRightWindow(context, 0, null);
            } else if (action.equalsIgnoreCase("extend")) {
                showPromptWindow(context, 1, null);
            } else if (action.equalsIgnoreCase("extend0")) {
                showPromptWindow(context, 0, null);
            } else if (action.equalsIgnoreCase("extend1")) {
                showPromptWindow(context, 2, null);
            } else if (action.equalsIgnoreCase("browse")) {
                showBrowseNav(context);
            } else if (action.equalsIgnoreCase("search")) {
                showSearchPage(context);
            } else if (action.equalsIgnoreCase("search_setup")) {
                showSearchSetupPage(context);
            } else if (action.equalsIgnoreCase("home")) {
                showMainHome(context);
            } else if (action.equalsIgnoreCase("myhome")) {
                showMyPage(context);
            } else if (action.equalsIgnoreCase("refresh")) {
                showMainFresh(context);
            } else if (action.equalsIgnoreCase("foreword")) {
                showMainForward(context);
            } else if (action.equalsIgnoreCase("backward")) {
                showMainBack(context);
            } else if (action.equalsIgnoreCase("custom_menu")) {
                showMainMenu(context);
            } else if (action.equalsIgnoreCase("left_menu")) {
                showLeftMenu(context);
            } else if (action.equalsIgnoreCase("manager")) {
                showMangerMenuer(context);
            } else if (action.equalsIgnoreCase("music")) {
                showMusicPage(context);
            } else if (action.equalsIgnoreCase("module_manage")) {
                showmoduleManage(context);
            } else if (action.equalsIgnoreCase("system_setup")) {
                showsystemSetup(context);
            } else if (action.equalsIgnoreCase("pop_home")) {
                showPopHome(context);
            } else if (action.equalsIgnoreCase("pop_refresh")) {
                showPopFresh(context);
            } else if (action.equalsIgnoreCase("pop_foreward")) {
                showPopForward(context);
            } else if (action.equalsIgnoreCase("pop_backward")) {
                showPopBack(context);
            } else if (action.equalsIgnoreCase("pop_share")) {
                showPopShare(context);
            } else if (action.equalsIgnoreCase("pop_open")) {
                showPopOpen(context);
            } else if (action.equalsIgnoreCase("pop_close")) {
                showPopDown(context);
            } else if (action.equalsIgnoreCase("detail_close")) {
                showDetailDown(context);
            } else if (action.equalsIgnoreCase("weixin_send")) {
                showWeixinSend(context);
            } else if (action.equalsIgnoreCase("weixin_msg")) {
                showWeixinNews(context);
            } else if (action.equalsIgnoreCase("weixin_user")) {
                showWeixinUser(context);
            } else if (action.equalsIgnoreCase("weixin_menu")) {
                showWeixinMenu(context);
            } else {
                return true;
            }
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    private static void showMyPublish(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Publish", "packPath",
                "com.tbs.tbsvps", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {

            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.tbs.tbsvps", "com.tbs.tbsvps.SplashActivity");
            intent.setComponent(cn);
            intent.setFlags(101);
            intent.putExtra("iniPath", userIni);
            context.startActivity(intent);
        }
    }

    private static void showMyWallet(Context context) {
        Intent intent = new Intent();
        intent.putExtra("rights", 1);
        intent.setClass(context, MyWalletActivity.class);
        context.startActivity(intent);
    }

    private static void showMyCollect(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MyCollectActivity.class);
        context.startActivity(intent);
    }


    public static void Collect(Context context, String url, String Title, String type, String content, String pic) {
        if (StringUtils.isEmpty(url)) {
            CollectAsyncTask task = new CollectAsyncTask(context, Nowurl, Title, type, content, pic);
            task.execute();
        } else {
            CollectAsyncTask task = new CollectAsyncTask(context, url, Title, type, content, pic);
            task.execute();
        }
    }

    public static void Share(Context context, String url, String Title, String content, String pic) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(Title);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(url);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("#分享#" + content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数

        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        if (pic.isEmpty())
            oks.setImageUrl("http://e.tbs.com.cn:8003/image/novod.jpg");
        else
            oks.setImageUrl(pic);
        oks.setUrl(url);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(context.getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl("http://sharesdk.cn");
        // System.out.println("webview.getUrl() = " + webview.getUrl());
// 启动分享GUI
        oks.show(context);
    }

    private static void showMangerMenuer(Context context) {
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 22);
        context.sendBroadcast(intent);
    }

    private static void showsystemSetup(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SystemSetupActivityNew.class);
        context.startActivity(intent);
    }

    private static void showmoduleManage(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FunctionActivity.class);
        context.startActivity(intent);
    }

    private static void showSourceStore(Context context) {

        Intent intent = new Intent();
        intent.setClass(context, ResourceStoreActivity.class);
        context.startActivity(intent);
    }

    private static void showLeftMenu(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 17);
        context.sendBroadcast(intent);
    }

    private static void showMusicPage(Context context) {
        Intent intent = new Intent();
//        me.wcy.music.model.Music music = new me.wcy.music.model.Music();
//        music.setId(13);
        Bundle bundle = new Bundle();
//        bundle.putSerializable("music", music);
        intent.putExtras(bundle);
        intent.setClass(context, MusicPublicActivity.class);
        context.startActivity(intent);
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString("sourceId", "8aac50fa5dcae6dd015dcafdb3c60001");
//        bundle.putString("type","Online");
//        intent.putExtras(bundle);
//        intent.setClass(context, VideoPlayer.class);
//        context.startActivity(intent);

//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString("sourceId", "2820efb85726d10a015726d10a430000");
//        bundle.putString("type","Online");
//        intent.putExtras(bundle);
//        intent.setClass(context, ShortVideoPlayer.class);
//        context.startActivity(intent);
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString("sourceId", "13");
//        bundle.putString("type", "Online");
//        intent.putExtras(bundle);
//        intent.setClass(context, FilmActivity.class);
//        context.startActivity(intent);

    }

    private static void showMyFileSource(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, FileSourceActivity.class);
        context.startActivity(intent);
    }

//    private static void showMyEmailSetup(Context context) {
//        // TODO Auto-generated method stub
//        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
//        Intent intent = new Intent();
//        intent = new Intent();
//        intent.setClass(context, EamilSettingActivity.class);
//        context.startActivity(intent);
//    }

//    private static void showMyUser(Context context, Object object) {
//        // TODO Auto-generated method stub
//        Intent intent = new Intent();
//        intent.setClass(context, MainAddress.class);
//        context.startActivity(intent);
//    }
//
//    private static void showMyMessage(Context context, Object object) {
//        // TODO Auto-generated method stub
//        Intent intent = new Intent();
//        intent.setClass(context, MainTab.class);
//        context.startActivity(intent);
//    }

//    private static void showMySkyDriveSetup(Context context, Object object) {
//        // TODO Auto-generated method stub
//        Intent intent = new Intent();
//        intent.setClass(context, HomeMore.class);
//        context.startActivity(intent);
//    }

//    private static void showMySkyDriveManager(Context context, Object object) {
//        // TODO Auto-generated method stub
//        Intent intent = new Intent();
//        intent.setClass(context, com.tbs.tbsdisk.ui.conversation.MainTab.class);
//        context.startActivity(intent);
//    }

    private static void showWeixinMenu(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, WeixinMenuActivity.class);
        context.startActivity(intent);
    }

    private static void showWeixinUser(Context context) {
        // TODO Auto-generated method stub
        Intent people = new Intent();
        people.setClass(context, WeiXinAddressActivity.class);
        context.startActivity(people);
    }

    private static void showWeixinNews(Context context) {
        // TODO Auto-generated method stub
        Intent news = new Intent();
        news.setClass(context, WeixinNewMessage.class);
        context.startActivity(news);
    }

    private static void showWeixinSend(Context context) {
        // TODO Auto-generated method stub
        Intent send = new Intent();
        send.setClass(context, WeixinSendManager.class);
        context.startActivity(send);
    }

    private static void showWechatManagerPage(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, WeixinActivity.class);
        context.startActivity(intent);
    }

    private static void showWechatSettingPage(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, WeixinSetUpActivity.class);
        context.startActivity(intent);
    }

    private static void showMyPage(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, MyPageActivity.class);
        context.startActivity(intent);
    }

    private static void showUpDatePage(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, UpdateActivity.class);
        context.startActivity(intent);
    }

    public static boolean MenuMotion(Context context, String action) {
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        int resnum = Integer.parseInt(m_iniFileIO.getIniString(userIni,
                "MENU_ALL", "Count", "0", (byte) 0));
        for (int j = 1; j <= resnum; j++) {
            if (action.equalsIgnoreCase(m_iniFileIO.getIniString(userIni,
                    "MENU_ALL", "ID" + j, "", (byte) 0)))
                return false;
        }
        return true;
    }

    private static void showPopBack(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 3);
        context.sendBroadcast(intent);
    }

    private static void showPopForward(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 2);
        context.sendBroadcast(intent);
    }

    private static void showPopFresh(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 4);
        context.sendBroadcast(intent);
    }

    private static void showPopDown(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 7);
        context.sendBroadcast(intent);
    }

    private static void showDetailDown(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_detail"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 1);
        context.sendBroadcast(intent);
    }

    private static void showPopHome(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 1);
        context.sendBroadcast(intent);
    }

    private static void showMainBack(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 18);
        context.sendBroadcast(intent);
    }

    private static void showMainForward(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 19);
        context.sendBroadcast(intent);
    }

    private static void showMainFresh(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 7);
        context.sendBroadcast(intent);
    }

    private static void showMainHome(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_main"
                + context.getString(R.string.about_title));
        intent.putExtra("flag", 20);
        context.sendBroadcast(intent);
    }

    protected static void showPopShare(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 5);
        context.sendBroadcast(intent);
    }

    protected static void showPopOpen(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("Action_pop" + context.getString(R.string.about_title));
        intent.putExtra("flag", 6);
        context.sendBroadcast(intent);
    }

    // private static void showMainSearch(Context context) {
    // // TODO Auto-generated method stub
    //
    // }

    private static void showBrowseNav(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("loadView" + context.getString(R.string.about_title));
        intent.putExtra("flag", 11);
        context.sendBroadcast(intent);
    }

    public static void showMyEmail(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Email", "packPath",
                "com.tbs.email", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {
            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            context.startActivity(intent);
        }
    }

    public static void showMyChat(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Chat", "packPath",
                "com.tbs.chat", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {
            intent = new Intent();
            intent.setClass(context, MainTab.class);

        }
        context.startActivity(intent);
    }

    public static void showMyChatAddress(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.putExtra("tool", 2);
        intent.setClass(context, MainTab.class);
        context.startActivity(intent);
    }

    public static void showMyChatMessage(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.putExtra("tool", 1);
        intent.setClass(context, MainTab.class);
        context.startActivity(intent);
    }

    public static void showCircle(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, CircleActivity.class);
        context.startActivity(intent);
    }

    public static void showMyCircle(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, CircleMeActivity.class);
        context.startActivity(intent);
    }

    public static void showMyLive(Context context) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setClass(context, LiveListMainActivity.class);
        context.startActivity(intent);
    }

    public static void showMyDisk(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Disk", "packPath",
                "com.tbs.disk", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {
            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            context.startActivity(intent);
        }
    }

    public static void showMyPlayer(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Player", "packPath",
                "com.tbsplayer", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {

            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            context.startActivity(intent);
        }
    }

    public static void showMyReader(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Reader", "packPath",
                "com.tbs.pdf", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {
            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            context.startActivity(intent);
        }
    }

    protected static void showMyResource(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent = new Intent();
        intent.setClass(context, FileSourceActivity.class);
        context.startActivity(intent);
    }

    public static void showTbsMcs(Context context) {
        // TODO Auto-generated method stub
        setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String EmailUrl = m_iniFileIO.getIniString(userIni, "Mcs", "packPath",
                "com.tbs.tbsmcs", (byte) 0);
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(EmailUrl);
        if (intent == null) {
            Toast.makeText(context, "应用未安装", Toast.LENGTH_SHORT).show();
        } else {
            context.startActivity(intent);
        }
    }

    protected static void showMySkyDrive(Context context, String Url) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        if (StringUtils.isEmpty(Url)) {
            Intent intent = new Intent();
            intent.setClass(context, MySkyDriveActivity.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(context, MySkyDriveActivity.class);
            intent.putExtra("tempUrl", Url);
            context.startActivity(intent);
        }
    }

    protected static void showAboutPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, AboutActivity.class);
        context.startActivity(intent);
    }

    protected static void showFileManagerPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, FileExplorerTabActivity.class);
        context.startActivity(intent);
    }

    protected static void showDeviceManagerPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, DeviceManagerActivity.class);
        context.startActivity(intent);
    }

    protected static void showCommentManagerPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, MyCommentActivity.class);
        context.startActivity(intent);
    }

    protected static void showLogManagerPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, LogSetupActivity.class);
        context.startActivity(intent);
    }

    protected static void showOfflineDownloadPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, DownloadActivity.class);
        context.startActivity(intent);
    }

    protected static void showInformationPushPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, NotificationSettingsActivity.class);
        context.startActivity(intent);
    }

    protected static void showApplicationManagerPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, AppManagerActivity.class);
        context.startActivity(intent);
    }

    protected static void showApplicationSettingPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, AppSetupActivity.class);
        context.startActivity(intent);
    }

    protected static void showNetworkSettingPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, CloudSetupActivity.class);
        context.startActivity(intent);
    }

    protected static void showMobileNetworkSettingPage(Context context) {
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        context.startActivity(intent);
    }

    protected static void showAppearanceSettingPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, AppearanceSettingActivity.class);
        context.startActivity(intent);
    }

    protected static void showDisplaySettingPage(Context context) {
        // TODO Auto-generated method stub
        UIHelper.setSharePerference(context, constants.SAVE_LOCALMSGNUM, "load", 1);
        Intent intent = new Intent();
        intent.setClass(context, DisplaySettingActivity.class);
        context.startActivity(intent);
    }

    /**
     * 添加网页js与webview交互
     */
    public static void addJavascript(final Context cxt, WebView wv,
                                     final String action) {
        wv.addJavascriptInterface(new JavaScriptInterface()
        {
            // Like NoUrl.
            @JavascriptInterface
            public void StartApp(String fileName) {
                UIHelper.StartApp(cxt, fileName);
            }

            @JavascriptInterface
            public void Hyperlinks_Move(String source, String sourceUrl) {
                if (source != null)
                    showHyperlinks_Move(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void Loading_NoMove(String sourceUrl) {
                if (sourceUrl != null)
                    showLoading_NoMove(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void NoLoading_Move(String source, String sourceUrl) {
                showNoLoading_Move(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void loadsearch(String sourceUrl, String word) {
                UIHelper.loadsearch(cxt, sourceUrl, word);
            }

            @JavascriptInterface
            public void Loading_Move(String source, String sourceUrl) {
                showLoading_Move(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void Collect(String url, String Title, String type, String content, String pic) {
                UIHelper.Collect(cxt, url, Title, type, content, pic);
            }

            @JavascriptInterface
            public void Login() {
                login(cxt);
            }

            @JavascriptInterface
            public void SynLanding(String LoginId, String account) {
                UIHelper.SynLanding(cxt, LoginId, account);
            }

            @JavascriptInterface
            public String getLoginInfo() {
                return UIHelper.getLoginInfo(cxt);
            }

            @JavascriptInterface
            public String getDeviceInfo() {
                return UIHelper.getDeviceInfo(cxt);
            }

            @JavascriptInterface
            public int showLoginDialog(int action) {
                return UIHelper.showLoginDialog(cxt, action);
            }

            @JavascriptInterface
            public void Regist() {
                regist(cxt);
            }

            @JavascriptInterface
            public void GoDetail(int num, int count, String source,
                                 String sourceUrl) {
                detail_do(cxt, source, sourceUrl, num, count, action);
            }

            @JavascriptInterface
            public void showMyApplication() {
                UIHelper.showMyApplication(cxt);
            }

            @JavascriptInterface
            public void showMyCloud() {
                UIHelper.showMyCloud(cxt, null);
            }

            @JavascriptInterface
            public void showMyResource() {
                UIHelper.showMyResource(cxt);
            }

            @JavascriptInterface
            public void showSearchPage() {
                UIHelper.showSearchPage(cxt);
            }

            @JavascriptInterface
            public void createDeskShortCut(String source, String sourceID,
                                           String iniPATH) {
                showcreateDeskShortCut(cxt, source, sourceID, iniPATH);
            }

            @JavascriptInterface
            public void OriginalLink(String sourceUrl) {
                showOriginalPage(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void NewOriginalLink(String sourceUrl) {
                showNewOriginalPage(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void startTbsweb() {
                StartTbsweb.Startapp(cxt, 1);
            }

            @JavascriptInterface
            public void showDetail(int num, int count, String source,
                                   String sourceUrl) {
                showDetailPage(cxt, source, sourceUrl, num, count);
            }

            @JavascriptInterface
            public void showNewDetail(int num, int count, String source,
                                      String sourceUrl) {
                showNewDetailPage(cxt, source, sourceUrl, num, count);
            }

            @JavascriptInterface
            public void showSettingPage() {
                UIHelper.showSettingPage(cxt);
            }

            @JavascriptInterface
            public void showLocalSettingpage() {
                UIHelper.showLocalSettingpage(cxt);
            }

            @JavascriptInterface
            public void showDisplaySettingPage() {
                UIHelper.showDisplaySettingPage(cxt);
            }

            @JavascriptInterface
            public void showAppearanceSettingPage() {
                UIHelper.showAppearanceSettingPage(cxt);
            }

            @JavascriptInterface
            public void showSubscribePage() {
                UIHelper.showSubscribePage(cxt);
            }

            @JavascriptInterface
            public void showInfoPage() {
                UIHelper.showInfoPage(cxt);
            }

            @JavascriptInterface
            public void showNetworkSettingPage() {
                UIHelper.showNetworkSettingPage(cxt);
            }

            @JavascriptInterface
            public void showMobileNetworkSettingPage() {
                UIHelper.showMobileNetworkSettingPage(cxt);
            }

            @JavascriptInterface
            public void showApplicationSettingPage() {
                UIHelper.showApplicationSettingPage(cxt);
            }

            @JavascriptInterface
            public void showApplicationManagerPage() {
                UIHelper.showApplicationManagerPage(cxt);
            }

            @JavascriptInterface
            public void showInformationPushPage() {
                UIHelper.showInformationPushPage(cxt);
            }

            @JavascriptInterface
            public void showOfflineDownloadPage() {
                UIHelper.showOfflineDownloadPage(cxt);
            }

            @JavascriptInterface
            public void showLogManagerPage() {
                UIHelper.showLogManagerPage(cxt);
            }

            @JavascriptInterface
            public void showCommentManagerPage() {
                UIHelper.showCommentManagerPage(cxt);
            }

            @JavascriptInterface
            public void showDeviceManagerPage() {
                UIHelper.showDeviceManagerPage(cxt);
            }

            @JavascriptInterface
            public void showFileManagerPage() {
                UIHelper.showFileManagerPage(cxt);
            }

            @JavascriptInterface
            public void showFeedBackPage() {
                UIHelper.showFeedBackPage(cxt);
            }

            @JavascriptInterface
            public void showAboutPage() {
                UIHelper.showAboutPage(cxt);
            }

            @JavascriptInterface
            public void showQuitAllDialog() {
                showAllQuitDialog(cxt);
            }

            @JavascriptInterface
            public void showQuitDialog() {
                UIHelper.showQuitDialog(cxt);
            }

            @JavascriptInterface
            public void showVideoORAudio(int type, String code) {
                switch (type) {
                    case 1:
                        showAudio(cxt, code);
                        break;
                    case 2:
                        showShortVideo(cxt, code);
                        break;
                    case 3:
                        showFilm(cxt, code);
                        break;
                    case 4:
                        showVideo(code);
                        break;
                    case 5:
                        showLive(code);
                        break;
                    case 6:
                        showAlbum(cxt, code);
                        break;
                }
            }

            @JavascriptInterface
            public void showVideo(String code) {
                UIHelper.showLocalVideo(cxt, code);
            }

            @JavascriptInterface
            public void showOnlineVideo(String path, String title, String chapter) {
                UIHelper.showOnlineVideo(cxt, path, title, chapter);
            }

            @JavascriptInterface
            public void showLive(String info) {
                UIHelper.showLive(cxt, info);
            }

            @JavascriptInterface
            public void showVideoPublish() {
                UIHelper.showMyPublish(cxt);
            }


            @JavascriptInterface
            public void showWebVideo(String category, String path, String title, String chapter) {
                UIHelper.showWebVideo(cxt, category, path, title, chapter);
            }

            @JavascriptInterface
            public void getHtmlSource(String category, String title, String urlPath) {
                JsoupExam.getHtmlSource(category, title, urlPath, cxt);
            }

            @JavascriptInterface
            public boolean isHtmlSaven(String category, String urlPath) {
                return JsoupExam.isHtmlSaven(category, urlPath, cxt);
            }

            @JavascriptInterface
            public void showToast(String content) {
                Toast.makeText(cxt, content, Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public void showOverview(String source, String sourceUrl) {
                showOverviewPage(cxt, source, sourceUrl);
            }

            @JavascriptInterface
            public void showPopWindow(String sourceUrl) {
                showPopMenu(cxt, sourceUrl);
            }

            @JavascriptInterface
            public void showLeftWindow(int action) {
                UIHelper.showLeftWindow(cxt, action, null);
            }

            @JavascriptInterface
            public void showRightWindow(int action) {
                UIHelper.showRightWindow(cxt, action, null);
            }

            @JavascriptInterface
            public void showMainMenu() {
                UIHelper.showMainMenu(cxt);
            }

//            @JavascriptInterface
//            public void showMainSetMenu() {
//                UIHelper.showMainSetMenu(cxt);
//            }

            @JavascriptInterface
            public void getFavouriteFlag(int num) {
                showFavouriteflag(cxt, num);
            }

            @JavascriptInterface
            public void getDeleteFlag(int num) {
                showDeleteflag(cxt, num);
            }

            @JavascriptInterface
            public void getTitle(String name) {
                showShareMore(cxt, name);
            }

            @JavascriptInterface
            public void share(String name, String url) {
                showShareMore(cxt, name, url);
            }

            @JavascriptInterface
            public void setActionName(String action, String name) {
                UIHelper.setActionName(cxt, action, name);
            }

            @JavascriptInterface
            public void setRightTitle(String title) {
                showRightTitle(cxt, title);
            }

            @JavascriptInterface
            public void setLeftTitle(String title) {
                showLeftTitle(cxt, title);

            }

            @JavascriptInterface
            public void setMainTitle(String title) {
                showMainTitle(cxt, title);
            }

            @JavascriptInterface
            public void setDefLib(String source, String sourceID) {
                UIHelper.setDefLib(cxt, source, sourceID);
            }

            @JavascriptInterface
            public void CloseDetail() {
                showDetailDown(cxt);
            }

            @JavascriptInterface
            public void ClosePop() {
                showPopDown(cxt);
            }

            @JavascriptInterface
            public void downloadFile(String url, String savePath,
                                     boolean ishowProgress) {
                // System.out.println("ishowProgress = " + ishowProgress);
                FileUtils.downFile(cxt, url, savePath, ishowProgress);
            }

            @JavascriptInterface
            public void backgroundDownFile(final String url, final String savePath) {
                new Thread()
                {
                    @Override
                    public void run() {
                        String webRoot = UIHelper.getStoragePath(cxt);
                        webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/" + savePath;
                        HttpConnectionUtil connection = new HttpConnectionUtil();
                        connection.downFile(UIHelper.encodeGB(url), webRoot);
                    }

                }.start();
            }

            @JavascriptInterface
            public void openFile(String Type, String Path) {
                showOpenFile(cxt, Type, Path);
            }

            @JavascriptInterface
            public void updateData(String category) {
                FileUtils.updateData(cxt, category);
            }

            @JavascriptInterface
            public void uploadFile(String category) {
                showUpFileDialog(cxt, category);
            }

            @JavascriptInterface
            public int CheckVersion(String fileName, String timer) {
                return Updateapp.CheckVersion(cxt, fileName, timer);
            }

            @JavascriptInterface
            public int CheckFileVersion(String fileName, String timer) {
                return Updateapp.CheckFileVersion(cxt, fileName, timer);
            }

            @JavascriptInterface
            public void OpenWebFile(String fileInfo, String path) {
                openWebFile(cxt, fileInfo, path);
            }

            @JavascriptInterface
            public void FileStoreInfo(String dirPath, String fileinfo) {
                Updateapp.FileStoreInfo(cxt, dirPath, fileinfo);
            }

            @JavascriptInterface
            public void openApp(String appName, String sourceID) {
                UIHelper.openApp(cxt, appName, sourceID);
            }

            @JavascriptInterface
            public void wxPay(String orderId, String money) {
                UIHelper.wxPay(cxt, orderId, money);
            }

            @JavascriptInterface
            public void aliPay(String orderId, String money) {
                UIHelper.aliPay(cxt, orderId, money);
            }
        }, "TBS");
    }

    private static void openWebFile(Context context, String fileInfo, String fileName) {
        String type = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (fileName.toLowerCase().contains("http:")) {
            String mimeType = MimeUtils
                    .guessMimeTypeFromExtension(type);
            if (mimeType == null)
                mimeType = "";

            if (type.equalsIgnoreCase("pdf")) {
                PackageManager packageManager = context
                        .getPackageManager();
                Intent intent = packageManager
                        .getLaunchIntentForPackage("com.tbs.pdf");
                if (intent == null) {
                    try {
                        Uri uri = Uri.parse(fileName);
                        intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = Uri.parse(fileName);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.tbs.pdf",
                            "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                    // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                    i.setData(uri);
                    context.startActivity(i);
                }

            } else if (mimeType.contains("video")) {
                Intent intent = new Intent();
                intent.setClass(context, PlayerActivity.class);
                intent.putExtra("videoPath", fileName);
                context.startActivity(intent);
            } else {
                try {
                    Uri uri = Uri.parse(fileName);
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            uri);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception exp) {
                    Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            String videoPath = fileName;
            String webRoot = UIHelper.getStoragePath(context);
            webRoot += constants.SD_CARD_TBSFILE_PATH6 + "/";
            try {
                JSONObject jsonObject = new JSONObject(fileInfo);
                String fileCode = jsonObject.getString("fileCode");
                String category = jsonObject.getString("category");
                IniFile iniFile = new IniFile();
                String iniPath = webRoot + category + "/" + category.substring(category.lastIndexOf("/") + 1, category
                        .length()) + ".ini";
                int count = Integer.parseInt(iniFile.getIniString(iniPath, "file", "count", "0", (byte) 0));
                for (int i = 1; i <= count; i++) {
                    if (fileCode.equalsIgnoreCase(iniFile.getIniString(iniPath, "file" + i, "code", "", (byte) 0))) {
                        String filePath = iniFile.getIniString(iniPath, "file" + i, "path", "",
                                (byte) 0);
                        videoPath = filePath + "/" + fileName;
                        webRoot = webRoot + videoPath;
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showOpenFile(context, type, webRoot);
        }
    }

    private static void wxPay(Context cxt, String orderId, String money) {
        sendPay pay = new sendPay(cxt);
        pay.payReq(orderId, money);
    }

    private static void aliPay(Context cxt, String orderId, String money) {
        sendAliPay pay = new sendAliPay((Activity) cxt);
        pay.alipayReq(orderId, money);
    }

    protected static void setActionName(Context context, String action,
                                        String name) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("backup");
        intent.putExtra("subPath", name);
        context.sendBroadcast(intent);
    }


    public static void getDirPath(Context context, String Session) {

        LayoutInflater factory = LayoutInflater.from(context);// 提示框
        View view = factory.inflate(R.layout.download_online, null);// 这里必须是final的
        final WebView webview = (WebView) view.findViewById(R.id.webview7);
        final ProgressBar progressBar1 = (ProgressBar) view
                .findViewById(R.id.progressBar1);
        // backup_auto.setVisibility(View.GONE);
        webview.getSettings().setSavePassword(false);
        webview.getSettings().setSaveFormData(false);
        //webview.getSettings().setDefaultTextEncodingName("gb2312");
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setJavaScriptEnabled(true);
        // 3.0版本才有setDisplayZoomControls的功能
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
            webview.getSettings().setDisplayZoomControls(false);
        }
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        // flash支持
        //webview.getSettings().setPluginsEnabled(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setAppCacheMaxSize(10240);
        webview.clearCache(true);
        webview.setClickable(true);

        // 修改ua使得web端正确判断 @ 2013-11-07 11:13:28
        String ua = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString(ua + "; tbsmis/2015");
        // ȡ�������
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        addJavascript(context, webview);
        webview.setWebViewClient(getWebViewClient());
        webview.setFocusable(true);
        webview.requestFocus();
        webview.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar1.setVisibility(View.VISIBLE);
                } else {
                    progressBar1.setVisibility(View.GONE);
                }
            }
        });
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String userIni = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);

        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String tempUrl = "/Store/SelectDir.cbs?sectionName=" + Session;
        // String tempUrl= m_iniFileIO.getIniString(appNewsFile, "NETWORK",
        // "StorePath",
        // "/store/SelectDir_new.cbs", (byte) 0);
        String ipUrl = m_iniFileIO.getIniString(userIni, "Store",
                "storeAddress", constants.DefaultServerIp, (byte) 0);
        String portUrl = m_iniFileIO.getIniString(userIni, "Store",
                "storePort", constants.DefaultServerPort, (byte) 0);
        String baseUrl = "http://" + ipUrl + ":" + portUrl;
        tempUrl = StringUtils.isUrl(baseUrl + tempUrl, baseUrl, null);
        webview.loadUrl(tempUrl);
        new Builder(context).setCancelable(false).setTitle("选择子目录")
                // 提示框标题
                .setView(view).setPositiveButton("确定",// 提示框的两个按钮
                new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        webview.loadUrl("javascript:OnSetSectionName()");
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    protected static String getDeviceInfo(Context context) {
        // TODO Auto-generated method stub
        JSONObject json = new JSONObject();
        try {
            json.put("clientName", Build.MANUFACTURER + Build.MODEL);
            json.put("clientId", UIHelper.DeviceMD5ID(context));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
        return json.toString();
    }

    protected static String getLoginInfo(Context context) {
        // TODO Auto-generated method stub
        IniFile m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = getShareperference(context,
                constants.SAVE_INFORMATION, "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appNewsFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appNewsFile;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        JSONObject json = new JSONObject();
        String LoginFlag = m_iniFileIO.getIniString(userIni, "Login",
                "LoginFlag", "0", (byte) 0);
        String Account = m_iniFileIO.getIniString(userIni, "Login",
                "Account", "", (byte) 0);
        String LoginId = m_iniFileIO.getIniString(userIni, "Login",
                "LoginId", "", (byte) 0);
//        String LoginId = m_iniFileIO.getIniString(userIni, "Login",
//                "LoginId", "", (byte) 0);
        try {
            json.put("LoginFlag", LoginFlag);
            json.put("Account", Account);
            json.put("LoginId", LoginId);
            json.put("clientId", UIHelper.DeviceMD5ID(context));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
        }
        return json.toString();
    }

    /**
     * @param context
     * @return 1: 表示当前可以直接进行下载
     * 2: 表示当前不是wifi状态，下载需要提示 非wifi状态
     * 3: 当前无网络
     * 4: 当前有网络 不判断是否提示 直接下载
     */
    public static int downloadWifiState(Context context) {
        String webRoot = UIHelper.getShareperference(context,
                constants.SAVE_INFORMATION, "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String appNewsFile = webRoot
                + IniFile.getIniString(webRoot
                        + constants.WEB_CONFIG_FILE_NAME, "TBSWeb",
                "IniName", constants.NEWS_CONFIG_FILE_NAME,
                (byte) 0);
        String userIni = appNewsFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (Integer.parseInt(IniFile.getIniString(userIni,
                "Offline", "setup_wifi", "1", (byte) 0)) == 1) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 3;
            }
        } else {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return 1;
            } else {
                return 3;
            }
        }
    }

    // 创建createDeskShortCut实现
    public static void openApp(Context context, String appName, String sourceID) {
        IniFile m_iniFileIO = new IniFile();
        if (appName.contains(".") && appName.contains("/")) {
            String type = appName.substring(appName.lastIndexOf(".") + 1);
            String webRoot = UIHelper.getStoragePath(context);
            webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6 + "/" + appName;
            type = type.toLowerCase();
            getMediaType mide = new getMediaType();
            mide.initReflect();
            int mediatype = mide.getMediaFileType(webRoot);
            boolean isAudio = mide.isAudioFile(mediatype);
            boolean isVideo = mide.isVideoFile(mediatype);
            if (type.equalsIgnoreCase("pdf")) {
                PackageManager packageManager = context
                        .getPackageManager();
                Intent intent = packageManager
                        .getLaunchIntentForPackage("com.tbs.pdf");
                if (intent == null) {
                    try {
                        String mimeType = MimeUtils
                                .guessMimeTypeFromExtension(type);
                        intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = Uri.fromFile(new File(webRoot
                    ));
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.tbs.pdf",
                            "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                    // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                    i.setData(uri);
                    context.startActivity(i);
                }

            }
//            else if (isAudio) {
//                Intent intent = new Intent();
//                intent.setClass(context, AcsAudioAcitvity.class);
//                Bundle bundle = new Bundle();
//                Music music = new Music();
//                music.setUri(webRoot);
//                bundle.putSerializable("music", music);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
            else if (isVideo) {
                Intent intent = new Intent();
                intent.setClass(context, PlayerActivity.class);
                intent.putExtra("videoPath", webRoot);
                context.startActivity(intent);
            } else {
                String mimeType = MimeUtils
                        .guessMimeTypeFromExtension(type);
                if (mimeType == null || mimeType.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(webRoot);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                uri);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                }

            }
        } else if (appName.contains(".") && appName.contains("-")) {
            String dir = appName.substring(0, appName.indexOf("-"));
            String type = appName.substring(appName.lastIndexOf(".") + 1);
            String webRoot = UIHelper.getStoragePath(context);
            webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6 + "/" + dir + "/" + appName;
            getMediaType mide = new getMediaType();
            mide.initReflect();
            int mediatype = mide.getMediaFileType(webRoot);
            boolean isAudio = mide.isAudioFile(mediatype);
            boolean isVideo = mide.isVideoFile(mediatype);
            type = type.toLowerCase();
            if (type.equalsIgnoreCase("pdf")) {
                PackageManager packageManager = context
                        .getPackageManager();
                Intent intent = packageManager
                        .getLaunchIntentForPackage("com.tbs.pdf");
                if (intent == null) {
                    try {
                        String mimeType = MimeUtils
                                .guessMimeTypeFromExtension(type);
                        intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = Uri.fromFile(new File(webRoot
                    ));

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.tbs.pdf",
                            "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                    // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                    i.setData(uri);
                    context.startActivity(i);
                }

            }
//            else if (isAudio) {
//                Intent intent = new Intent();
//                intent.setClass(context, AcsAudioAcitvity.class);
//                Bundle bundle = new Bundle();
//                Music music = new Music();
//                music.setUri(webRoot);
//                bundle.putSerializable("music", music);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
            else if (isVideo || type.equals("flv") || type.equals("flt")) {
                Intent intent = new Intent();
                intent.setClass(context, PlayerActivity.class);
                intent.putExtra("videoPath", webRoot);
                context.startActivity(intent);
            } else {

                String mimeType = MimeUtils
                        .guessMimeTypeFromExtension(type);
                //System.out.println(mimeType);
                if (StringUtils.isEmpty(mimeType)) {
                    try {
                        Uri uri = Uri.parse(webRoot);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                }

            }
        } else {
            String webRoot = getShareperference(context,
                    constants.SAVE_INFORMATION, "Path", "");
            if (webRoot.endsWith("/") == false) {
                webRoot += "/";
            }
            String nowName = webRoot.substring(0, webRoot.lastIndexOf("/"));
            nowName = nowName.substring(nowName.lastIndexOf("/") + 1);
            if (nowName.equalsIgnoreCase(appName)) {
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appNewsFile = webRoot
                        + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                m_iniFileIO.writeIniString(appNewsFile, "TBSAPP", "resname",
                        sourceID);
                Intent mainIntent = new Intent(context, InitializeToolbarActivity.class);
                context.startActivity(mainIntent);
            } else {
                webRoot = UIHelper.getStoragePath(context);
                webRoot = webRoot + constants.SD_CARD_TBSSOFT_PATH3 + appName;
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                String appNewsFile = webRoot
                        + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                        constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                m_iniFileIO.writeIniString(appNewsFile, "TBSAPP", "resname",
                        sourceID);
                StartTbsweb.Startapp(context, 0);
                setSharePerference(context, constants.SAVE_INFORMATION,
                        "Path", webRoot);
                StartTbsweb.Startapp(context, 1);
                Intent intent = new Intent();
                intent.setAction("Action_main"
                        + context.getString(R.string.about_title));
                intent.putExtra("flag", 12);
                context.sendBroadcast(intent);
                intent.setAction("loadView"
                        + context.getString(R.string.about_title));
                intent.putExtra("flag", 5);
                intent.putExtra("author", 0);
                context.sendBroadcast(intent);
                Intent mainIntent = new Intent(context, InitializeToolbarActivity.class);
                context.startActivity(mainIntent);
                MyActivity.getInstance().finishAllActivity();
            }
        }
    }

    /*
        打开文件 特殊文件特殊应用打开
        文件问全路径
     */
    public static boolean openFile(Context context, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            getMediaType mide = new getMediaType();
            mide.initReflect();
            int mediatype = mide.getMediaFileType(fileName);
            boolean isAudio = mide.isAudioFile(mediatype);
            boolean isVideo = mide.isVideoFile(mediatype);
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            type = type.toLowerCase();
            if (type.equalsIgnoreCase("pdf")) {
                PackageManager packageManager = context
                        .getPackageManager();
                Intent intent = packageManager
                        .getLaunchIntentForPackage("com.tbs.pdf");
                if (intent == null) {
                    try {
                        String mimeType = MimeUtils
                                .guessMimeTypeFromExtension(type);
                        intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                fileName)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = Uri.fromFile(new File(fileName
                    ));
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.tbs.pdf",
                            "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                    // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                    i.setData(uri);
                    context.startActivity(i);
                }

            }
//            else if (isAudio) {
//                Intent intent = new Intent();
//                intent.setClass(context, AcsAudioAcitvity.class);
//                Bundle bundle = new Bundle();
//                Music music = new Music();
//                music.setUri(fileName);
//                bundle.putSerializable("music", music);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
            else if (isVideo || type.equals("flv") || type.equals("flt")) {
                Intent intent = new Intent();
                intent.setClass(context, PlayerActivity.class);
                intent.putExtra("videoPath", fileName);
                context.startActivity(intent);
            } else {
                String mimeType = MimeUtils
                        .guessMimeTypeFromExtension(type);
                if (mimeType == null || mimeType.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(fileName);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                uri);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        Intent intent = new Intent();
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                fileName)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                }

            }
            return true;
        }
        return false;

    }

    protected static void StartApp(Context context, String fileName) {
        // TODO Auto-generated method stub
        if (fileName.contains(".") && fileName.contains("/")) {
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            String webRoot = UIHelper.getStoragePath(context);
            webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6 + "/" + fileName;
            getMediaType mide = new getMediaType();
            mide.initReflect();
            int mediatype = mide.getMediaFileType(webRoot);
            boolean isAudio = mide.isAudioFile(mediatype);
            boolean isVideo = mide.isVideoFile(mediatype);
            type = type.toLowerCase();
            if (type.equalsIgnoreCase("pdf")) {
                PackageManager packageManager = context
                        .getPackageManager();
                Intent intent = packageManager
                        .getLaunchIntentForPackage("com.tbs.pdf");
                if (intent == null) {
                    try {
                        String mimeType = MimeUtils
                                .guessMimeTypeFromExtension(type);
                        intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = Uri.fromFile(new File(webRoot
                    ));
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.tbs.pdf",
                            "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                    // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                    i.setData(uri);
                    context.startActivity(i);
                }

            }
//            else if (isAudio) {
//                Intent intent = new Intent();
//                intent.setClass(context, AcsAudioAcitvity.class);
//                Bundle bundle = new Bundle();
//                Music music = new Music();
//                music.setUri(webRoot);
//                bundle.putSerializable("music", music);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
            else if (isVideo || type.equals("flv") || type.equals("flt")) {
                Intent intent = new Intent();
                intent.setClass(context, PlayerActivity.class);
                intent.putExtra("videoPath", webRoot);
                context.startActivity(intent);
            } else {
                String mimeType = MimeUtils
                        .guessMimeTypeFromExtension(type);
                if (mimeType == null || mimeType.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(webRoot);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                uri);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        Intent intent = new Intent();
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                }

            }
        } else if (fileName.contains(".") && fileName.contains("-")) {
            String dir = fileName.substring(0, fileName.indexOf("-"));
            String type = fileName.substring(fileName.lastIndexOf(".") + 1);
            String webRoot = UIHelper.getStoragePath(context);
            webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6 + "/" + dir + "/" + fileName;
            getMediaType mide = new getMediaType();
            mide.initReflect();
            int mediatype = mide.getMediaFileType(webRoot);
            boolean isAudio = mide.isAudioFile(mediatype);
            boolean isVideo = mide.isVideoFile(mediatype);
            type = type.toLowerCase();
            if (type.equalsIgnoreCase("pdf")) {
                PackageManager packageManager = context
                        .getPackageManager();
                Intent intent = new Intent();
                intent = packageManager
                        .getLaunchIntentForPackage("com.tbs.pdf");
                if (intent == null) {
                    try {
                        String mimeType = MimeUtils
                                .guessMimeTypeFromExtension(type);
                        intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Uri uri = Uri.fromFile(new File(webRoot
                    ));
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setComponent(new ComponentName("com.tbs.pdf",
                            "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                    // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                    i.setData(uri);
                    context.startActivity(i);
                }

            }
//            else if (isAudio) {
//                Intent intent = new Intent();
//                intent.setClass(context, AcsAudioAcitvity.class);
//                Bundle bundle = new Bundle();
//                Music music = new Music();
//                music.setUri(webRoot);
//                bundle.putSerializable("music", music);
//                intent.putExtras(bundle);
//                context.startActivity(intent);
//            }
            else if (isVideo || type.equals("flv") || type.equals("flt")) {
                Intent intent = new Intent();
                intent.setClass(context, PlayerActivity.class);
                intent.putExtra("videoPath", webRoot);
                context.startActivity(intent);
            } else {

                String mimeType = MimeUtils
                        .guessMimeTypeFromExtension(type);
                if (mimeType == null || mimeType.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(webRoot);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                uri);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        Intent intent = new Intent();
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(
                                webRoot)), mimeType);
                        context.startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                    }
                }

            }
        } else if (fileName.contains("app:")) {
            fileName = fileName.substring(fileName.indexOf(":") + 1);
            //包管理操作管理类
            PackageManager pm = context.getPackageManager();
            //获取到应用信息
            Intent it = pm.getLaunchIntentForPackage(fileName);
            if (it != null)
                context.startActivity(it);

        } else {
            String webRoot = UIHelper.getStoragePath(context);
            webRoot += constants.SD_CARD_TBSSOFT_PATH3 + "/" + fileName;
            //System.out.println("webRoot = " + webRoot);
            StartTbsweb.Startapp(context, 0);
            setSharePerference(context, constants.SAVE_INFORMATION,
                    "Path", webRoot);
            StartTbsweb.Startapp(context, 1);
            Intent intent = new Intent();
            intent.setAction("Action_main"
                    + context.getString(R.string.about_title));
            intent.putExtra("flag", 12);
            context.sendBroadcast(intent);
            intent.setAction("loadView" + context.getString(R.string.about_title));
            intent.putExtra("flag", 5);
            intent.putExtra("author", 0);
            context.sendBroadcast(intent);
            Intent mainIntent = new Intent(context, InitializeToolbarActivity.class);
            context.startActivity(mainIntent);
            MyActivity.getInstance().finishAllActivity();
        }
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static DownloadListener MyWebViewDownLoadListener(
            final Context context) {
        //System.out.println("download");
        return new DownloadListener()
        {
            @SuppressWarnings("deprecation")
            @Override
            public void onDownloadStart(final String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if (!Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(context, "需要SD卡。", Toast.LENGTH_LONG).show();
                    return;
                }
                // 显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                // 进度条下面显示的总文件大小
                final String DowmloadFileSize = df
                        .format((float) contentLength / 1024 / 1024) + "MB";
                if (FileUtils.getFileFormat(url).equalsIgnoreCase("tbk")) {
                    String webRoot = UIHelper.getStoragePath(context);
                    webRoot += constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + FileUtils.getFileNameNoFormat(url);
                    File tempFile = new File(webRoot);
                    if (tempFile.exists()) {
                        new Builder(context)
                                .setTitle("提醒")
                                .setCancelable(false)
                                .setMessage("文件已存在，是否继续下载安装")
                                .setPositiveButton(
                                        "取消",
                                        new OnClickListener()
                                        {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                return;
                                            }
                                        })
                                .setNegativeButton(
                                        "确定",
                                        new OnClickListener()
                                        {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                new Builder(context)
                                                        .setTitle("下载")
                                                        .setCancelable(false)
                                                        .setMessage(
                                                                "文件名："
                                                                        + FileUtils
                                                                        .getFileName(url)
                                                                        + "\n大小："
                                                                        + DowmloadFileSize)
                                                        // 提示框标题
                                                        .setPositiveButton(
                                                                "确定",// 提示框的两个按钮
                                                                new OnClickListener()
                                                                {
                                                                    @Override
                                                                    public void onClick(
                                                                            DialogInterface dialog,
                                                                            int which) {
                                                                        DownloadManager mDownloadManager =
                                                                                (DownloadManager) context
                                                                                        .getSystemService(Context
                                                                                                .DOWNLOAD_SERVICE);
                                                                        DownloadManager.Query query = new
                                                                                DownloadManager.Query();
                                                                        query.setFilterByStatus(DownloadManager
                                                                                .STATUS_RUNNING);
                                                                        Cursor cursor = mDownloadManager
                                                                                .query(query);
                                                                        if (cursor
                                                                                .moveToFirst()) {// 有记录
                                                                            String uriString = cursor
                                                                                    .getString(cursor
                                                                                            .getColumnIndex
                                                                                                    (DownloadManager
                                                                                                            .COLUMN_URI));
                                                                            if (UIHelper.encodeGB(
                                                                                    url)
                                                                                    .equalsIgnoreCase(
                                                                                            uriString)) {
                                                                                Toast.makeText(
                                                                                        context,
                                                                                        "正在下载，请稍后。。。",
                                                                                        Toast.LENGTH_LONG)
                                                                                        .show();
                                                                                return;
                                                                            }
                                                                        }
                                                                        Request down = new
                                                                                Request(
                                                                                Uri.parse(url));
                                                                        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                                                                        down.setAllowedNetworkTypes(
                                                                                Request.NETWORK_MOBILE
                                                                                        | Request.NETWORK_WIFI);
                                                                        // 禁止发出通知，既后台下载
                                                                        down.setShowRunningNotification(true);
//                                                                        context.startService(new Intent(
//                                                                                context.getString(string
//                                                                                        .DownloadServerName)));
                                                                        Intent mIntent = new Intent();
                                                                        mIntent.setAction(context
                                                                                .getString(R.string.DownloadServerName)
                                                                        );//你定义的service的action
                                                                        mIntent.setPackage(context.getPackageName());
                                                                        //这里你需要设置你应用的包名
                                                                        context.startService(mIntent);
                                                                        // 不显示下载界面
                                                                        down.setVisibleInDownloadsUi(true);
                                                                        // 设置下载后文件存放的位置
                                                                        down.setDestinationInExternalFilesDir(
                                                                                context,
                                                                                null,
                                                                                FileUtils
                                                                                        .getFileName(url));
                                                                        // 将下载请求放入队列
                                                                        mDownloadManager
                                                                                .enqueue(down);
                                                                    }
                                                                })
                                                        .setNegativeButton(
                                                                "取消", null)
                                                        .create().show();
                                            }
                                        }).create().show();
                    } else {
                        new Builder(context)
                                .setTitle("下载")
                                .setCancelable(false)
                                .setMessage(
                                        "文件名：" + FileUtils.getFileName(url)
                                                + "\n大小：" + DowmloadFileSize)
                                // 提示框标题
                                .setPositiveButton(
                                        "确定",// 提示框的两个按钮
                                        new OnClickListener()
                                        {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                DownloadManager mDownloadManager = (DownloadManager) context
                                                        .getSystemService(Context.DOWNLOAD_SERVICE);
                                                DownloadManager.Query query = new DownloadManager.Query();
                                                query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
                                                Cursor cursor = mDownloadManager
                                                        .query(query);
                                                if (cursor.moveToFirst()) {// 有记录
                                                    String uriString = cursor.getString(cursor
                                                            .getColumnIndex(DownloadManager.COLUMN_URI));
                                                    if (UIHelper.encodeGB(url)
                                                            .equalsIgnoreCase(
                                                                    uriString)) {
                                                        Toast.makeText(
                                                                context,
                                                                "正在下载，请稍后。。。",
                                                                Toast.LENGTH_LONG)
                                                                .show();
                                                        return;
                                                    }
                                                }
                                                Request down = new Request(
                                                        Uri.parse(url));
                                                // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                                                down.setAllowedNetworkTypes(Request.NETWORK_MOBILE
                                                        | Request.NETWORK_WIFI);
                                                // 禁止发出通知，既后台下载
                                                down.setShowRunningNotification(true);
//                                                context.startService(new Intent(
//                                                        context.getString(R.string.DownloadServerName)));
                                                Intent mIntent = new Intent();
                                                mIntent.setAction(context
                                                        .getString(R.string.DownloadServerName));//你定义的service的action
                                                mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                                context.startService(mIntent);
                                                // 不显示下载界面
                                                down.setVisibleInDownloadsUi(true);
                                                // 设置下载后文件存放的位置
                                                down.setDestinationInExternalFilesDir(
                                                        context,
                                                        null,
                                                        FileUtils
                                                                .getFileName(url));
                                                // 将下载请求放入队列
                                                mDownloadManager.enqueue(down);
                                            }
                                        }).setNegativeButton("取消", null)
                                .create().show();
                    }
                } else {
                    String webRoot = getShareperference(context,
                            constants.SAVE_INFORMATION, "Path", "");
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    String Midurl = url;
                    if (url.indexOf(":") > -1) {
                        Midurl = url.substring(url.lastIndexOf(":"));
                        Midurl = Midurl.substring(Midurl.indexOf("/") + 1);
                    }
                    String ext = FileUtils.getFileFormat(Midurl)
                            .toLowerCase();
                    if (url.toLowerCase().indexOf("http:") > -1) {
                        webRoot = "";
                        Midurl = url;
                    }
                    if (ext.equalsIgnoreCase("pdf")) {
                        PackageManager packageManager = context
                                .getPackageManager();
                        Intent intent = new Intent();
                        intent = packageManager
                                .getLaunchIntentForPackage("com.tbs.pdf");
                        if (intent == null) {
                            try {
                                String mimeType = MimeUtils
                                        .guessMimeTypeFromExtension(ext);
                                intent = new Intent();
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(
                                        webRoot + Midurl)), mimeType);
                                context.startActivity(intent);
                            } catch (Exception exp) {
                                Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Uri uri = Uri.fromFile(new File(webRoot
                                    + Midurl));
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.setComponent(new ComponentName("com.tbs.pdf",
                                    "com.tbs.pdf.pdfdroid.PdfViewerActivity"));
                            // 包名 类名，可以用APKTool反编译apk包，查AndroidManifest.xml
                            i.setData(uri);
                            context.startActivity(i);
                        }
                    } else if (ext.equalsIgnoreCase("flv") || ext.equalsIgnoreCase("flt")) {
                        Intent intent = new Intent();
                        intent.setClass(context, PlayerActivity.class);
                        intent.putExtra("videoPath", webRoot + Midurl);
                        context.startActivity(intent);
                    } else {
                        String mimeType = MimeUtils
                                .guessMimeTypeFromExtension(ext);
                        if (StringUtils.isEmpty(mimeType)) {
                            try {
                                Uri uri = Uri.parse(webRoot + Midurl);
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        uri);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (Exception exp) {
                                Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                Intent intent = new Intent();
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(
                                        webRoot + Midurl)), mimeType);
                                context.startActivity(Intent.createChooser(intent, "Choose to open"));
                            } catch (Exception exp) {
                                Toast.makeText(context, "无可打开应用", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                }
            }
        };
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {
        return new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("WebViewUrl = " + url);
                if (!StringUtils.isEmpty(url) && !url.startsWith("http") && !url.startsWith("https") && !url
                        .startsWith("ftp") && !url.startsWith("file")) {
                    try {
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent();
                        intent.setData(uri);
                        view.getContext().startActivity(intent);
                    } catch (Exception exp) {
                        Toast.makeText(view.getContext(), "应用未安装", Toast.LENGTH_LONG).show();
                    }
                    return true;
                } else {
                    UIHelper.Nowurl = url;
                    Uri uri = Uri.parse(url);
                    SharedPreferences setting = view.getContext().getSharedPreferences(
                            "tmp", view.getContext().MODE_PRIVATE);
                    if (uri.getScheme().equalsIgnoreCase("file") && url.toLowerCase().contains(".cbs")) {
                        String WebFile = getShareperference(view.getContext(),
                                constants.SAVE_INFORMATION, "Path", "");
                        if (WebFile.endsWith("/") == false) {
                            WebFile += "/";
                        }
                        url = url.substring(7);
                        String relatPath = WebFile + "Web";
                        CBSInterpret interpret = new CBSInterpret();
                        interpret.initGlobal(WebFile + "TbsWeb.ini", WebFile);
                        if (!url.contains(relatPath)) {
                            url = relatPath + url;
                        }
                        String interpretFile = interpret.Interpret(url.substring(relatPath.length()), "GET", "",
                                null, 0);
                        String FileName = FileUtils.getFileName(interpretFile);
                        String FilePath = FileUtils.getPath(url);
                        File file = new File(FilePath + FileName);
                        try {
                            file.createNewFile();
                            File srcfile = new File(interpretFile);
                            FileUtils.copyFileTo(srcfile, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        FileUtils.deleteFile(interpretFile);
                        int tmpCount = setting.getInt("tmpCount", 0);
                        Editor editor = setting.edit();
                        editor.putString("tmpFile" + tmpCount, FilePath + FileName);
                        editor.putInt("tmpCount", tmpCount + 1);
                        editor.commit();
                        url = "file://" + FilePath + FileName;
                    } else if (url.endsWith(".apk") || url.endsWith(".APK")) {
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                        view.getContext().startActivity(viewIntent);
                        return true;
                    }
                    int flag = UIHelper.getShareperference(view.getContext(),
                            constants.SAVE_LOCALMSGNUM, "load", 0);
                    if (flag == 0) {
                        view.loadUrl(url);
                    }
                    UIHelper.setSharePerference(view.getContext(),
                            constants.SAVE_LOCALMSGNUM, "load", 0);
                    return true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                UIHelper.Nowurl = url;
            }

//            /**
//             * 在每一次请求资源时，都会通过这个函数来回调
//             */
//            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//                System.out.println("shouldInterceptRequest1 = " + url);
//                Uri uri = Uri.parse(url);
//                SharedPreferences setting = view.getContext().getSharedPreferences(
//                        "tmp", view.getContext().MODE_PRIVATE);
//                if (uri.getScheme().equalsIgnoreCase("file")&& url.toLowerCase().contains(".cbs")) {
//                    String WebFile = getShareperference(view.getContext(),
//                            constants.SAVE_INFORMATION, "Path", "");
//                    if (WebFile.endsWith("/") == false) {
//                        WebFile += "/";
//                    }
//                    url = url.substring(7);
//                    String relatPath = WebFile + "Web";
//                    CBSInterpret interpret = new CBSInterpret();
//                    interpret.initGlobal(WebFile + "TbsWeb.ini", WebFile);
//                    if (!url.contains(relatPath)) {
//                        url = relatPath + url;
//                    }
//                    System.out.println("shouldInterceptRequest2 = " + url);
//                    String interpretFile = interpret.Interpret(url.substring(relatPath.length()), "GET", "",
//                            null, 0);
//                    String FileName = FileUtils.getFileName(interpretFile);
//                    String FilePath = FileUtils.getPath(url);
//                    File file = new File(FilePath + FileName);
//                    try {
//                        file.createNewFile();
//                        File srcfile = new File(interpretFile);
//                        FileUtils.copyFileTo(srcfile, file);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    FileUtils.deleteFile(interpretFile);
//                    int tmpCount = setting.getInt("tmpCount", 0);
//                    Editor editor = setting.edit();
//                    editor.putString("tmpFile" + tmpCount, FilePath + FileName);
//                    editor.putInt("tmpCount", tmpCount + 1);
//                    editor.commit();
//                    url = "file://" + FilePath + FileName;
//                }
//                System.out.println("shouldInterceptRequest = " + url);
//                return null;
//            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                String webRoot = UIHelper.getSoftPath(view.getContext());
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                webRoot += view.getContext().getString(R.string.SD_CARD_TBSAPP_PATH2);
                webRoot = UIHelper.getShareperference(view.getContext(),
                        constants.SAVE_INFORMATION, "Path", webRoot);
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                IniFile IniFile = new IniFile();
                String appNewsFile = webRoot
                        + IniFile.getIniString(webRoot
                                + constants.WEB_CONFIG_FILE_NAME, "TBSWeb",
                        "IniName", constants.NEWS_CONFIG_FILE_NAME,
                        (byte) 0);
                String userIni = appNewsFile;
                if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                        "LoginType", "0", (byte) 0)) == 1) {
                    String dataPath = view.getContext().getFilesDir().getParentFile()
                            .getAbsolutePath();
                    if (dataPath.endsWith("/") == false) {
                        dataPath = dataPath + "/";
                    }
                    userIni = dataPath + "TbsApp.ini";
                }
                int server = Integer.parseInt(IniFile.getIniString(userIni, "SERVICE",
                        "serverMarks", "4", (byte) 0));
                // System.out.println("failingUrl.contentEquals(\"http://\")="+failingUrl.contentEquals("http://"));
                if (server == 4 || server == 2 || failingUrl.toLowerCase().contains("http://")) {
                    boolean isNetWorkReady = isMobileNetWorkEnabled(view.getContext());
                    if (isNetWorkReady) {
                        String strVal = "file:///android_asset/404.html";
                        view.loadUrl(strVal);
                    } else {
                        String strVal = "file:///android_asset/network.html";
                        view.loadUrl(strVal);
                    }
                } else {
                    if (StartTbsweb.isMyServiceRunning(view.getContext(), null)) {
                        String strVal = "file:///android_asset/404.html";
                        view.loadUrl(strVal);
                    } else {
                        String strVal = "file:///android_asset/error.html";
                        view.loadUrl(strVal);
                    }
                }

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(view.getContext().getString(R.string.title_warning));
                builder.setMessage(view.getContext().getString(R.string.message_untrusted_certificate))
                        .setCancelable(true)
                        .setPositiveButton(view.getContext().getString(R.string.action_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        handler.proceed();
                                    }
                                })
                        .setNegativeButton(view.getContext().getString(R.string.action_no),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        handler.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                if (error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    alert.show();
                } else {
                    handler.proceed();
                }

            }
        };
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     *
     * @param string
     * @return
     */
    public static String encodeGB(String string) {
        // 转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            if (i > 2) {
                split[i] = encode(split[i]);
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");// 处理空格

        return split[0];
    }

    @SuppressWarnings("static-access")
    public static int getShareperference(Context context,
                                         String perferenceName, String MapName, int defvalue) {
        int MsgNum = 0;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        MsgNum = Getting.getInt(MapName, defvalue);
        return MsgNum;
    }

    @SuppressWarnings("static-access")
    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, int value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        Editor editor = setting.edit();
        editor.putInt(MapName, value);
        editor.commit();
    }

    @SuppressWarnings("static-access")
    public static String getShareperference(Context context,
                                            String perferenceName, String MapName, String defvalue) {
        String MsgNum = null;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        MsgNum = Getting.getString(MapName, defvalue);
        return MsgNum;
    }

    @SuppressWarnings("static-access")
    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, String value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        Editor editor = setting.edit();
        editor.putString(MapName, value);
        editor.commit();
    }

    @SuppressWarnings("static-access")
    public static boolean getShareperference(Context context,
                                             String perferenceName, String MapName, boolean value) {
        boolean MsgNum;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        MsgNum = Getting.getBoolean(MapName, value);
        return MsgNum;
    }

    @SuppressWarnings("static-access")
    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, boolean value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        Editor editor = setting.edit();
        editor.putBoolean(MapName, value);
        editor.commit();
    }

    public static boolean isMobileNetWorkEnabled(Context myContext) {
        if (myContext != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}