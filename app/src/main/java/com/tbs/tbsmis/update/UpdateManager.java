package com.tbs.tbsmis.update;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.R.id;
import com.tbs.tbsmis.app.AppException;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.ApiClient;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

/*
 * 应用程序更新工具包
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.1
 * @created 2012-6-29
 */
@SuppressLint("HandlerLeak")
public class UpdateManager
{

    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL = 1;
    private static UpdateManager updateManager;

    private Context mContext;
    // 通知对话框
    private Dialog noticeDialog;
    // 下载对话框
    private Dialog downloadDialog;
    // '已经是最新' 或者 '无法获取最新版本' 的对话框
    private Dialog latestOrFailDialog;
    // 进度条
    private ProgressBar mProgress;
    // 显示下载数值
    private TextView mProgressText;
    // 查询动画
    private ProgressDialog mProDialog;
    // 进度值
    private int progress;
    // 下载线程
    private Thread downLoadThread;
    // 终止标记
    private boolean interceptFlag;
    // 提示语
    private String updateMsg = "";
    // 返回的安装包url
    private String apkUrl = "";
    // 下载包保存路径
    private String savePath = "";
    // apk保存完整路径
    private String apkFilePath = "";
    // 临时下载文件路径
    private String tmpFilePath = "";
    // 下载文件大小
    private String apkFileSize;
    // 已下载文件大小
    private String tmpFileSize;
    private int curVersionCode;
    private Updateapk mUpdate;

    private final Handler mHandler = new Handler()
    {
        @Override
        @SuppressLint("ShowToast")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UpdateManager.DOWN_UPDATE:
                    UpdateManager.this.mProgress.setProgress(UpdateManager.this.progress);
                    UpdateManager.this.mProgressText.setText(UpdateManager.this.tmpFileSize + "/" + UpdateManager
                            .this.apkFileSize);
                    break;
                case UpdateManager.DOWN_OVER:
                    UpdateManager.this.downloadDialog.dismiss();
                    UpdateManager.this.installApk();
                    break;
                case UpdateManager.DOWN_NOSDCARD:
                    UpdateManager.this.downloadDialog.dismiss();
                    Toast.makeText(UpdateManager.this.mContext, "无法下载安装文件，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public static UpdateManager getUpdateManager() {
        if (UpdateManager.updateManager == null) {
            UpdateManager.updateManager = new UpdateManager();
        }
        UpdateManager.updateManager.interceptFlag = false;
        return UpdateManager.updateManager;
    }

    /*
     * 检查App更新
     *
     * @param context
     * @param isShowMsg
     *                   是否显示提示消息
     */
    @SuppressLint("HandlerLeak")
    @SuppressWarnings("deprecation")
    public void checkAppUpdate(Context context, final boolean isShowMsg) {
        mContext = context;
        this.getCurrentVersion();
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                // 进度条对话框不显示 - 检测结果也不显示
                if (UpdateManager.this.mProDialog != null && !UpdateManager.this.mProDialog.isShowing()) {
                    return;
                }
                // 关闭并释放释放进度条对话框
                if (isShowMsg && UpdateManager.this.mProDialog != null) {
                    UpdateManager.this.mProDialog.dismiss();
                    UpdateManager.this.mProDialog = null;
                }
                // 显示检测结果
                if (msg.what == 1) {
                    UpdateManager.this.mUpdate = (Updateapk) msg.obj;
                    if (UpdateManager.this.mUpdate != null) {
                        //System.out.println(curVersionCode+"~~~~~~who~~~~~~~"+mUpdate.getVersionCode());
                        if (UpdateManager.this.curVersionCode < UpdateManager.this.mUpdate.getVersionCode()) {
                            UpdateManager.this.apkUrl = UpdateManager.this.mUpdate.getDownloadUrl();
                            UpdateManager.this.updateMsg = UpdateManager.this.mUpdate.getUpdateLog();
                            UpdateManager.this.showNoticeDialog();
                        } else if (isShowMsg) {
                            UpdateManager.this.showLatestOrFailDialog(UpdateManager.DIALOG_TYPE_LATEST);
                        }
                    }
                } else if (isShowMsg) {
                    UpdateManager.this.showLatestOrFailDialog(UpdateManager.DIALOG_TYPE_FAIL);
                }
            }
        };
        if (isShowMsg) {
            if (this.mProDialog == null) {
                this.mProDialog = new ProgressDialog(this.mContext);
                // 设置进度条风格，风格为圆形，旋转的
                this.mProDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                // 设置ProgressDialog 标题
                this.mProDialog.setTitle("版本更新");
                // 设置ProgressDialog 提示信息
                this.mProDialog.setMessage("正在检测，请稍候...");
                // 设置ProgressDialog 的进度条是否不明确
                this.mProDialog.setIndeterminate(false);
                // 设置ProgressDialog 是否可以按退回按键取消
                this.mProDialog.setCancelable(true);
                // 设置ProgressDialog 的一个Button
                this.mProDialog.setButton("取消", new SureButtonListener());
                // 让ProgressDialog显示
                this.mProDialog.show();
            } else if (this.mProDialog.isShowing()
                    || this.latestOrFailDialog != null && this.latestOrFailDialog
                    .isShowing())
                return;
            else if (!this.mProDialog.isShowing()) {
                this.mProDialog.show();
            }

        }
        new Thread()
        {
            @Override
            public void run() {
                Message msg = new Message();
                try {

                    Updateapk update = ApiClient
                            .checkVersion(StringUtils.isUrl(
                                    UIHelper.getShareperference(
                                            UpdateManager.this.mContext,
                                            constants.SAVE_LOCALMSGNUM,
                                            "update_url",
                                            UpdateManager.this.mContext.getString(R.string.UpdateUrl)),
                                    UIHelper.getShareperference(
                                            UpdateManager.this.mContext,
                                            constants.SAVE_LOCALMSGNUM,
                                            "baseUrl", ""), null));
                    msg.what = 1;
                    msg.obj = update;
                } catch (AppException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();


    }

    // Dialog中取消按钮的监听器
    private class SureButtonListener implements
            OnClickListener
    {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            // 点击“确定按钮”取消对话框
            dialog.cancel();
        }

    }

    /*
     * 显示'已经是最新'或者'无法获取版本信息'对话框
     */
    private void showLatestOrFailDialog(int dialogType) {
        if (this.latestOrFailDialog != null) {
            // 关闭并释放之前的对话框
            this.latestOrFailDialog.dismiss();
            this.latestOrFailDialog = null;
        }
        Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(false);
        builder.setTitle("系统提示");
        if (dialogType == UpdateManager.DIALOG_TYPE_LATEST) {
            builder.setMessage("您当前已经是最新版本");
        } else if (dialogType == UpdateManager.DIALOG_TYPE_FAIL) {
            builder.setMessage("无法获取版本更新信息");
        }
        builder.setPositiveButton("确定", null);
        this.latestOrFailDialog = builder.create();
        this.latestOrFailDialog.show();
    }

    /*
     * 获取当前客户端版本信息
     */
    private void getCurrentVersion() {
        try {
            PackageInfo info = this.mContext.getPackageManager().getPackageInfo(
                    this.mContext.getPackageName(), 0);
            this.curVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }

    /*
     * 显示版本更新通知对话框
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(this.updateMsg);
        builder.setCancelable(false);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateManager.this.showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //System.out.println("~~~~~~~~更新2~~~~~~~~~~");
        this.noticeDialog = builder.create();
        this.noticeDialog.show();
    }

    /*
     * 显示下载对话框
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle("正在下载新版本");
        builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        View v = inflater.inflate(R.layout.update_progress, null);
        this.mProgress = (ProgressBar) v.findViewById(id.update_progress);
        this.mProgressText = (TextView) v.findViewById(id.update_progress_text);
        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateManager.this.interceptFlag = true;
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                UpdateManager.this.interceptFlag = true;
            }
        });
        this.downloadDialog = builder.create();
        this.downloadDialog.setCanceledOnTouchOutside(false);
        this.downloadDialog.show();

        this.downloadApk();
    }

    private final Runnable mdownApkRunnable = new Runnable()
    {
        @Override
        public void run() {
            try {
                String apkName = "TBSMis_" + UpdateManager.this.mUpdate.getVersionName() + ".apk";
                String tmpApk = "TBSMis_" + UpdateManager.this.mUpdate.getVersionName() + ".tmp";
                // 判断是否挂载了SD卡
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    UpdateManager.this.savePath = UIHelper.getSoftPath(mContext) + "/Update/";
                    File file = new File(UpdateManager.this.savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    UpdateManager.this.apkFilePath = UpdateManager.this.savePath + apkName;
                    UpdateManager.this.tmpFilePath = UpdateManager.this.savePath + tmpApk;
                }

                // 没有挂载SD卡，无法下载文件
                if (UpdateManager.this.apkFilePath == null || UpdateManager.this.apkFilePath == "") {
                    UpdateManager.this.mHandler.sendEmptyMessage(UpdateManager.DOWN_NOSDCARD);
                    return;
                }

                File ApkFile = new File(UpdateManager.this.apkFilePath);

                // 是否已下载更新文件
                if (ApkFile.exists()) {
                    UpdateManager.this.downloadDialog.dismiss();
                    UpdateManager.this.installApk();
                    return;
                }

                // 输出临时下载文件
                File tmpFile = new File(UpdateManager.this.tmpFilePath);
                FileOutputStream fos = new FileOutputStream(tmpFile);


                if (!apkUrl.toLowerCase().contains("http:")) {

                    String path = UIHelper.getShareperference(UpdateManager.this.mContext,
                            constants.SAVE_LOCALMSGNUM, "update_url",
                            UpdateManager.this.mContext.getString(R.string.UpdateUrl));
                    path = path.substring(0, path.lastIndexOf(File.separator));
                    if (!path.endsWith(File.separator)) {
                        path = path + File.separator;
                    }
                    apkUrl = StringUtils.isUrl(path, UIHelper.getShareperference(mContext,
                            constants.SAVE_LOCALMSGNUM, "baseUrl", ""), null) + apkUrl;
                }

                URL url = new URL(UpdateManager.this.apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                // int length = conn.getContentLength();
                long length = FileUtils.getNetFileSize(apkUrl);
                InputStream is = conn.getInputStream();

                // 显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                // 进度条下面显示的总文件大小
                UpdateManager.this.apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 进度条下面显示的当前下载文件大小
                    UpdateManager.this.tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    // 当前进度值
                    UpdateManager.this.progress = (int) ((float) count / length * 100);
                    // 更新进度
                    UpdateManager.this.mHandler.sendEmptyMessage(UpdateManager.DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成 - 将临时下载文件转成APK文件
                        if (tmpFile.renameTo(ApkFile)) {
                            // 通知安装
                            UpdateManager.this.mHandler.sendEmptyMessage(UpdateManager.DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!UpdateManager.this.interceptFlag);// 点击取消就停止下载
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        this.downLoadThread = new Thread(this.mdownApkRunnable);
        this.downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(this.apkFilePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile),
                "application/vnd.android.package-archive");
        this.mContext.startActivity(i);
        MyActivity.getInstance().AppExit(this.mContext);
    }
}
