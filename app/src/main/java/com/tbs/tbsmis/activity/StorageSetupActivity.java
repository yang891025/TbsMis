package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.StorageChooseDialog;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.entity.StorageBean;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.StorageUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StorageSetupActivity extends Activity implements View.OnClickListener
{
    private ImageView finishBtn;
    private ImageView downBtn;
    private RelativeLayout phone_storage;
    private RelativeLayout sd_storage;
    private TextView sd_size;
    private TextView phone_size;
    private RelativeLayout otg_storage;
    private TextView otg_size;
    private ProgressDialog Prodialog;
    private IntentFilter usbDeviceStateFilter;
    public userTimerTask userTimerTask;
    private CheckBox phone_check_box;
    private CheckBox sd_check_box;
    private CheckBox otg_check_box;
    private TextView root_set_subtitle;
    private TextView old_root;
    private TextView old_device;
    private RelativeLayout copy_root;
    private RelativeLayout move_root;
    private RelativeLayout root_path_enable;
    private Button reduce_changed_btn;
    private Button save_changed_btn;

    private String storagePathSD = "";
    private String storagePathOTG = "";
    private boolean otg_alive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.storage_setup);
        MyActivity.getInstance().addActivity(this);
        usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);
        this.init();
    }

    private void init() {
        // TODO Auto-generated method stub
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.phone_storage = (RelativeLayout) this.findViewById(R.id.phone_storage);
        this.sd_storage = (RelativeLayout) this.findViewById(R.id.sd_storage);
        root_path_enable = (RelativeLayout) this.findViewById(R.id.root_path_enable);
        copy_root = (RelativeLayout) this.findViewById(R.id.copy_root);
        move_root = (RelativeLayout) this.findViewById(R.id.move_root);
        this.phone_size = (TextView) this.findViewById(R.id.phone_size);
        this.sd_size = (TextView) this.findViewById(R.id.sd_size);
        this.otg_storage = (RelativeLayout) this.findViewById(R.id.otg_storage);
        this.otg_size = (TextView) this.findViewById(R.id.otg_size);
        phone_check_box = (CheckBox) findViewById(R.id.phone_check_box);
        sd_check_box = (CheckBox) findViewById(R.id.sd_check_box);
        otg_check_box = (CheckBox) findViewById(R.id.otg_check_box);
        old_device = (TextView) this.findViewById(R.id.old_device);
        old_root = (TextView) this.findViewById(R.id.old_root);
        root_set_subtitle = (TextView) this.findViewById(R.id.root_set_subtitle);
        reduce_changed_btn = (Button) findViewById(R.id.reduce_changed_btn);
        save_changed_btn = (Button) findViewById(R.id.save_changed_btn);

        String rootPath = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
        old_root.setText("原:" + rootPath);
        root_set_subtitle.setText("当前路径：" + rootPath);
        String storagePath = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
        ArrayList<StorageBean> storageData = StorageUtils.getStorageData(this);
        for (int i = 0; i < storageData.size(); i++) {
            if (storageData.get(i).getPath().toLowerCase().contains("usb")) {
                 boolean mounted = storageData.get(i).getMounted().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
                if (mounted) {
                    otg_alive = true;
                    if (storagePath.equalsIgnoreCase(storageData.get(i).getPath())) {
                        otg_check_box.setChecked(true);
                        old_device.setText("原：OTG存储");
                    }
                    long totalSize = storageData.get(i).getTotalSize();
                    String totalSpaceStr = StorageUtils.fmtSpace(totalSize);
                    long availableSize = storageData.get(i).getAvailableSize();
                    String availableSizeStr = StorageUtils.fmtSpace(availableSize);
                    otg_size.setText(availableSizeStr + "/" + totalSpaceStr);
                    storagePathOTG = storageData.get(i).getPath();
                } else {
                    if (storagePath.equalsIgnoreCase(storageData.get(i).getPath())) {
                        phone_check_box.setChecked(true);
                        old_device.setText("原:手机内存");
                        storagePath = FileUtils.getFirstExterPath();
                        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
                                .getFirstExterPath());
                    }
                    if(!otg_alive)
                    otg_storage.setVisibility(View.GONE);
                }
            } else if (storageData.get(i).getRemovable()) {
                 boolean mounted = storageData.get(i).getMounted().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
                if (mounted) {
                    if (storagePath.equalsIgnoreCase(storageData.get(i).getPath()+ "/Android/data/" + getPackageName())) {
                        sd_check_box.setChecked(true);
                        old_device.setText("原：SD卡");
                    }
                    long totalSize = storageData.get(i).getTotalSize();
                    String totalSpaceStr = StorageUtils.fmtSpace(totalSize);
                    long availableSize = storageData.get(i).getAvailableSize();
                    String availableSizeStr = StorageUtils.fmtSpace(availableSize);
                    sd_size.setText(availableSizeStr + "/" + totalSpaceStr);
                    storagePathSD = storageData.get(i).getPath()+ "/Android/data/" + getPackageName();
                } else {
                    if (storagePath.equalsIgnoreCase(storageData.get(i).getPath())) {
                        phone_check_box.setChecked(true);
                        old_device.setText("原:手机内存");
                        storagePath = FileUtils.getFirstExterPath();
                        UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
                                .getFirstExterPath());
                    }
                    sd_size.setText("未安装SD卡");
                }
            } else {
                if (storagePath.equalsIgnoreCase(storageData.get(i).getPath())) {
                    phone_check_box.setChecked(true);
                    old_device.setText("原:手机内存");
                }
                long totalSize = storageData.get(i).getTotalSize();
                String totalSpaceStr = StorageUtils.fmtSpace(totalSize);
                long availableSize = storageData.get(i).getAvailableSize();
                String availableSizeStr = StorageUtils.fmtSpace(availableSize);
                phone_size.setText(availableSizeStr + "/" + totalSpaceStr);
            }

        }
        otg_alive = false;
//        if (storagePath.equals(FileUtils.getFirstExterPath())) {
//            if (isInnerSdcardMounted()) {
//                phone_check_box.setChecked(true);
//                old_device.setText("原:手机内存");
//            }
//        } else if (storagePath.equals(FileUtils.getSDPath(this))) {
//            if (FileUtils.getVolumeState(this, FileUtils.getSDPath(this)) == null) {
//                phone_check_box.setChecked(true);
//                old_device.setText("原：手机内存");
//                UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
//                        .getFirstExterPath());
//            } else {
//                sd_check_box.setChecked(true);
//                old_device.setText("原：SD卡");
//            }
//        } else {
//            String USBState = FileUtils.getVolumeState(this, FileUtils.getUSBPath(this));
//            if (USBState == null) {
//                phone_check_box.setChecked(true);
//                old_device.setText("原：手机内存");
//                UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
//                        .getFirstExterPath());
//            } else {
//                if (USBState.equals(android.os.Environment.MEDIA_MOUNTED)) {
//                    otg_check_box.setChecked(true);
//                    old_device.setText("原：OTG存储");
//                } else {
//                    phone_check_box.setChecked(true);
//                    old_device.setText("原：手机内存");
//                    UIHelper.setSharePerference(this, constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils
//                            .getFirstExterPath());
//                }
//            }
//        }
//
//        if (isInnerSdcardMounted()) {
//            File path = Environment.getExternalStorageDirectory();
//            StatFs stat = new StatFs(path.getPath());
//            long blockSize = stat.getBlockSize();
//            long totalBlocks = stat.getBlockCount();
//            long availableBlocks = stat.getAvailableBlocks();
//            phone_size.setText(Formatter
//                    .formatFileSize(this, blockSize * availableBlocks) + "/" + Formatter.formatFileSize(this,
//                    blockSize * totalBlocks));
//        }
//        if (FileUtils.getVolumeState(this, FileUtils.getSDPath(this)) == null) {
//            sd_size.setText("未安装SD卡");
//        } else {
//            File SDpath = new File(FileUtils.getSDPath(this));
//            StatFs stat = new StatFs(SDpath.getPath());
//            long blockSize = stat.getBlockSize();
//            long totalBlocks = stat.getBlockCount();
//            long availableBlocks = stat.getAvailableBlocks();
//            sd_size.setText(Formatter
//                    .formatFileSize(this, blockSize * availableBlocks) + "/" + Formatter.formatFileSize(this,
//                    blockSize * totalBlocks));
//        }
//
//        String USBState = FileUtils.getVolumeState(this, FileUtils.getUSBPath(this));
//        if (USBState == null) {
//            otg_storage.setVisibility(View.GONE);
//        } else {
//            if (USBState.equals(android.os.Environment.MEDIA_MOUNTED)) {
//                File SDpath = new File(FileUtils.getUSBPath(this));
//                StatFs stat = new StatFs(SDpath.getPath());
//                long blockSize = stat.getBlockSize();
//                long totalBlocks = stat.getBlockCount();
//                long availableBlocks = stat.getAvailableBlocks();
//                otg_size.setText(Formatter
//                        .formatFileSize(this, blockSize * availableBlocks) + "/" + Formatter.formatFileSize(this,
//                        blockSize * totalBlocks));
//            } else {
//                otg_storage.setVisibility(View.GONE);
//            }
//        }
        downBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        root_path_enable.setOnClickListener(this);
        reduce_changed_btn.setOnClickListener(this);
        save_changed_btn.setOnClickListener(this);
        move_root.setOnClickListener(this);
        copy_root.setOnClickListener(this);
        phone_storage.setOnClickListener(this);
        otg_storage.setOnClickListener(this);
        sd_storage.setOnClickListener(this);
    }

    public static boolean isInnerSdcardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    @Override
    public void onClick(View v) { // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn:
                if (backFunction()) {
                    finish();
                }
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.save_changed_btn:
                if (backFunction()) {
                    finish();
                }
                break;
            case R.id.reduce_changed_btn:
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
                finish();
                break;
            case R.id.move_root:
                final String storagePath = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
                final String storagePath_New = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                final String rootPath = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
                final String rootPath_New = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
                if (storagePath_New.equals(storagePath) || storagePath_New == "") {

                    if (rootPath_New.equals(rootPath) || rootPath_New == "") {
                        Toast.makeText(this, "未设置到新设备或新的跟路径，无法进行目录操作", Toast.LENGTH_LONG).show();
                    } else {
                        showModifyDialog();
                        MyAsyncTask myasynctask = new MyAsyncTask(storagePath + "/" + rootPath, storagePath + "/" +
                                rootPath_New, 2, StorageSetupActivity.this);
                        myasynctask.execute();
                    }
                } else {
                    if (rootPath_New.equals(rootPath) || rootPath_New == "") {
                        File sdFile = new File(storagePath_New + "/" + rootPath);
                        if (sdFile.exists()) {
                            new AlertDialog.Builder(this).setMessage("目录已存在，是否覆盖？").setNeutralButton("否", null)
                                    .setNegativeButton("是", new DialogInterface.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            showModifyDialog();
                                            MyAsyncTask myasynctask = new MyAsyncTask(storagePath + "/" + rootPath,
                                                    storagePath_New + "/" + rootPath, 2, StorageSetupActivity.this);
                                            myasynctask.execute();
                                        }
                                    }).create().show();
                        } else {
                            showModifyDialog();
                            MyAsyncTask myasynctask = new MyAsyncTask(storagePath + "/" + rootPath, storagePath_New +
                                    "/" + rootPath, 2, StorageSetupActivity.this);
                            myasynctask.execute();
                        }
                    } else {
                        File sdFile = new File(storagePath_New + "/" + rootPath_New);
                        if (sdFile.exists()) {
                            new AlertDialog.Builder(this).setMessage("根目录已存在，是否覆盖？").setNeutralButton("否", null)
                                    .setNegativeButton("是", new DialogInterface.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            showModifyDialog();
                                            MyAsyncTask myasynctask = new MyAsyncTask(storagePath + "/" + rootPath,
                                                    storagePath_New + "/" + rootPath_New, 2, StorageSetupActivity.this);
                                            myasynctask.execute();
                                        }
                                    }).create().show();
                        } else {
                            showModifyDialog();
                            MyAsyncTask myasynctask = new MyAsyncTask(storagePath + "/" + rootPath, storagePath_New +
                                    "/" + rootPath_New, 2, StorageSetupActivity.this);
                            myasynctask.execute();
                        }
                    }
                }
                break;
            case R.id.copy_root:
                final String storagePathC = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
                final String storagePath_NewC = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                final String rootPathC = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
                final String rootPath_NewC = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
                if (storagePath_NewC.equals(storagePathC) || storagePath_NewC == "") {
                    if (rootPath_NewC.equals(rootPathC) || rootPath_NewC == "") {
                        Toast.makeText(this, "未设置到新设备或新的跟路径，无法进行目录操作", Toast.LENGTH_LONG).show();
                    } else {
                        showModifyDialog();
                        MyAsyncTask myasynctask = new MyAsyncTask(storagePathC + "/" + rootPathC, storagePathC + "/"
                                + rootPath_NewC, 1, StorageSetupActivity.this);
                        myasynctask.execute();
                    }
                } else {
                    if (rootPath_NewC.equals(rootPathC) || rootPath_NewC == "") {
                        File sdFile = new File(storagePath_NewC + "/" + rootPathC);
                        if (sdFile.exists()) {
                            new AlertDialog.Builder(this).setMessage("根目录已存在，是否覆盖？").setNeutralButton("否", null)
                                    .setNegativeButton("是", new DialogInterface.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            showModifyDialog();
                                            MyAsyncTask myasynctask = new MyAsyncTask(storagePathC + "/" + rootPathC,
                                                    storagePath_NewC + "/" + rootPathC, 1, StorageSetupActivity.this);
                                            myasynctask.execute();
                                        }
                                    }).create().show();
                        } else {
                            showModifyDialog();
                            MyAsyncTask myasynctask = new MyAsyncTask(storagePathC + "/" + rootPathC,
                                    storagePath_NewC + "/" + rootPathC, 1, StorageSetupActivity.this);
                            myasynctask.execute();
                        }
                    } else {
                        File sdFile = new File(storagePath_NewC + "/" + rootPath_NewC);
                        if (sdFile.exists()) {
                            new AlertDialog.Builder(this).setMessage("目录已存在，是否覆盖？").setNeutralButton("否", null)
                                    .setNegativeButton("是", new DialogInterface.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            showModifyDialog();
                                            MyAsyncTask myasynctask = new MyAsyncTask(storagePathC + "/" + rootPathC,
                                                    storagePath_NewC + "/" + rootPath_NewC, 1, StorageSetupActivity
                                                    .this);
                                            myasynctask.execute();
                                        }
                                    }).create().show();
                        } else {
                            showModifyDialog();
                            MyAsyncTask myasynctask = new MyAsyncTask(storagePathC + "/" + rootPathC,
                                    storagePath_NewC + "/" + rootPath_NewC, 1, StorageSetupActivity.this);
                            myasynctask.execute();
                        }
                    }
                }
                break;
            case R.id.phone_storage:
                if (!phone_check_box.isChecked()) {
                    sd_check_box.setChecked(false);
                    phone_check_box.setChecked(true);
                    otg_check_box.setChecked(false);

                    UIHelper.setSharePerference(this,
                            constants.SAVE_LOCALMSGNUM, "storagePath_New", FileUtils.getFirstExterPath());
                }
                break;
            case R.id.sd_storage:
                if (!sd_check_box.isChecked()) {
                    sd_check_box.setChecked(true);
                    phone_check_box.setChecked(false);
                    otg_check_box.setChecked(false);
                    UIHelper.setSharePerference(this,
                            constants.SAVE_LOCALMSGNUM, "storagePath_New", storagePathSD);
                }
                break;
            case R.id.otg_storage:
                if (!otg_check_box.isChecked()) {
                    sd_check_box.setChecked(false);
                    phone_check_box.setChecked(false);
                    otg_check_box.setChecked(true);
                    UIHelper.setSharePerference(this,
                            constants.SAVE_LOCALMSGNUM, "storagePath_New", storagePathOTG);
                }
                break;
            case R.id.root_path_enable:
                final String storagePathR = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
                final String storagePath_NewR = UIHelper.getShareperference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                if (storagePath_NewR.equals(storagePathR) || storagePath_NewR == "") {
                    UIHelper.showStoragePathDialog(StorageSetupActivity.this, storagePathR,
                            new StorageChooseDialog.ChooseCompleteListener()
                            {
                                @Override
                                public void onComplete(String finalPath) {

                                    root_set_subtitle.setText("当前路径:" + finalPath.substring(finalPath.lastIndexOf
                                            ("/") + 1));
                                    UIHelper.setSharePerference(StorageSetupActivity.this,
                                            constants.SAVE_LOCALMSGNUM, "rootPath_New", finalPath.substring(finalPath
                                                    .lastIndexOf("/") + 1));
                                }
                            });
                } else {
                    UIHelper.showStoragePathDialog(StorageSetupActivity.this, storagePath_NewR,
                            new StorageChooseDialog.ChooseCompleteListener()
                            {
                                @Override
                                public void onComplete(String finalPath) {

                                    root_set_subtitle.setText("当前路径:" + finalPath.substring(finalPath.lastIndexOf
                                            ("/") + 1));
                                    UIHelper.setSharePerference(StorageSetupActivity.this,
                                            constants.SAVE_LOCALMSGNUM, "rootPath_New", finalPath.substring(finalPath
                                                    .lastIndexOf("/") + 1));

                                }
                            });
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, String>
    {
        private final Context context;
        private String sdPath;
        private String phonePath;
        private int action;

        public MyAsyncTask(String phonePath, String sdPath, int action, Context context) {
            this.sdPath = sdPath;
            this.phonePath = phonePath;
            this.action = action;
            this.context = context;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            Prodialog.show();
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected String doInBackground(Void... params) {

            int tmpCount = UIHelper.getShareperference(context, "tmp", "tmpCount", 0);
            for (int i = 0; i < tmpCount; i++) {
                FileUtils.deleteFile(UIHelper.getShareperference(context, "tmp", "tmpFile" + i,
                        ""));
            }
            UIHelper.setSharePerference(context, "tmp", "tmpCount", 0);

            if (!(new File(sdPath)).exists())
                if (!(new File(sdPath)).mkdirs()) {
                    return "false";
                }
            // 获取源文件夹当前下的文件或目录
            File[] file = (new File(phonePath)).listFiles();
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    // 复制文件
                    try {
                        FileUtils.copyFileTo(file[i], new File(sdPath + File.separator + file[i].getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (file[i].isDirectory()) {
                    // 复制目录
                    String sourceDir = phonePath + File.separator + file[i].getName();
                    String targetDir = sdPath + File.separator + file[i].getName();
                    try {
                        copyDirectiory(sourceDir, targetDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (action == 2)
                FileUtils.deleteDirectory(phonePath);

            return "true";
        }

        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(String integer) {
            Prodialog.dismiss();
            if (action == 3)
                finish();
        }

        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void showModifyDialog() {
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("处理数据");
        this.Prodialog.setMessage("正在处理数据，可能需要几分钟，请耐心等待...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new
                        File(new File(targetDir).getAbsolutePath()
                        + File.separator + file[i].getName());
                FileUtils.copyFileTo(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //System.out.println("action = "+action);
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                otg_storage.setVisibility(View.GONE);
                if (userTimerTask != null) {
                    userTimerTask.cancel();
                    userTimerTask = null;
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                userTimerTask = new userTimerTask();
                Timer userTimer = new Timer();
                userTimer.schedule(userTimerTask, 1000, 1000);
            }
        }
    };

    class userTimerTask extends TimerTask
    {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ArrayList<StorageBean> storageData = StorageUtils.getStorageData(StorageSetupActivity.this);
                    for (int i = 0; i < storageData.size(); i++) {
                        if (storageData.get(i).getPath().toLowerCase().contains("usb")) {
                            boolean mounted = storageData.get(i).getMounted().equalsIgnoreCase(Environment
                                    .MEDIA_MOUNTED);
                            if (mounted) {
                                otg_alive = true;
                                otg_storage.setVisibility(View.VISIBLE);
                                long totalSize = storageData.get(i).getTotalSize();
                                String totalSpaceStr = StorageUtils.fmtSpace(totalSize);
                                long availableSize = storageData.get(i).getAvailableSize();
                                String availableSizeStr = StorageUtils.fmtSpace(availableSize);
                                otg_size.setText(availableSizeStr + "/" + totalSpaceStr);
                                storagePathOTG = storageData.get(i).getPath();
                            } else {
                                if(!otg_alive)
                                otg_storage.setVisibility(View.GONE);
                            }
                        }
                    }
                    otg_alive = false;
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (backFunction()) {
                finish();
            }
        }
        return true;
    }

    private boolean backFunction() {
        String storagePath = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "storagePath", FileUtils.getFirstExterPath());
        String storagePath_New = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
        String rootPath = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "rootPath", constants.SD_CARD_TBS_PATH1);
        String rootPath_New = UIHelper.getShareperference(this,
                constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
        if (storagePath.equals(storagePath_New) || storagePath_New == "") {
            if (rootPath.equals(rootPath_New)) {
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
                return true;
            } else if (rootPath_New != "") {
                String webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.substring(webRoot.lastIndexOf("/")).length() > 1) {
                    File file = new File(storagePath + "/" + rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                    if (!file.exists()) {
                        copyNowApp(storagePath, rootPath_New, storagePath + "/" + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1), storagePath + "/" + rootPath_New +
                                constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                        return false;
                    } else {
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath_New);
                        String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        if (webRoot != "") {
                            if (webRoot.endsWith("/") == true) {
                                webRoot = webRoot.substring(0, webRoot.length() - 1);
                            }
                            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                        }
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", storagePath_New + File.separator + rootPath_New + constants
                                        .SD_CARD_TBSSOFT_PATH3 + "/" + appName);
                    }
                } else {
                    webRoot = webRoot.substring(0, webRoot.length() - 1);
                    File file = new File(storagePath + "/" + rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                    if (!file.exists()) {
                        copyNowApp(storagePath, rootPath_New, storagePath + "/" + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1), storagePath + "/" + rootPath_New +
                                constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                        return false;
                    } else {
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath_New);
                        String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        if (webRoot != "") {
                            if (webRoot.endsWith("/") == true) {
                                webRoot = webRoot.substring(0, webRoot.length() - 1);
                            }
                            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                        }
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", storagePath + File.separator + rootPath_New + constants.SD_CARD_TBSSOFT_PATH3
                                        + "/" + appName);
                    }
                }
            }
        } else if (storagePath_New != "") {
            if (rootPath.equals(rootPath_New)) {
                String webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                        "Path", "");
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                UIHelper.setSharePerference(this,
                        constants.SAVE_LOCALMSGNUM, "storagePath", storagePath_New);
                String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                if (webRoot != "") {
                    if (webRoot.endsWith("/") == true) {
                        webRoot = webRoot.substring(0, webRoot.length() - 1);
                    }
                    appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                }
                UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                        "Path", storagePath_New + File.separator + rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 +
                                "/" + appName);
                return true;
            } else if (rootPath_New != "") {
                String webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.substring(webRoot.lastIndexOf("/")).length() > 1) {
                    File file = new File(storagePath_New + "/" + rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 +
                            "/" + webRoot.substring(webRoot.lastIndexOf("/")));
                    if (!file.exists()) {
                        copyNowApp(storagePath_New, rootPath_New, storagePath + "/" + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1), storagePath_New + "/" +
                                rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                        return false;
                    } else {
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "storagePath", storagePath_New);
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath_New);
                        String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        if (webRoot != "") {
                            if (webRoot.endsWith("/") == true) {
                                webRoot = webRoot.substring(0, webRoot.length() - 1);
                            }
                            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                        }
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", storagePath_New + File.separator + rootPath_New + constants
                                        .SD_CARD_TBSSOFT_PATH3 + "/" + appName);
                    }

                } else {
                    webRoot = webRoot.substring(0, webRoot.length() - 1);
                    File file = new File(storagePath_New + "/" + rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 +
                            "/" + webRoot.substring(webRoot.lastIndexOf("/")));
                    if (!file.exists()) {
                        copyNowApp(storagePath_New, rootPath_New, storagePath + "/" + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1), storagePath_New + "/" +
                                rootPath_New + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                        return false;
                    } else {
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "storagePath", storagePath_New);
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath_New);
                        String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        if (webRoot != "") {
                            if (webRoot.endsWith("/") == true) {
                                webRoot = webRoot.substring(0, webRoot.length() - 1);
                            }
                            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                        }
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", storagePath_New + File.separator + rootPath_New + constants
                                        .SD_CARD_TBSSOFT_PATH3 + "/" + appName);
                    }

                }
            } else {
                String webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                        "Path", "");
                if (webRoot.substring(webRoot.lastIndexOf("/")).length() > 1) {
                    File file = new File(storagePath_New + "/" + rootPath + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + webRoot.substring(webRoot.lastIndexOf("/")));
                    if (!file.exists()) {
                        copyNowApp(storagePath_New, rootPath, storagePath + "/" + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1), storagePath_New + "/" + rootPath +
                                constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                        return false;
                    } else {
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "storagePath", storagePath_New);
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath);
                        String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        if (webRoot != "") {
                            if (webRoot.endsWith("/") == true) {
                                webRoot = webRoot.substring(0, webRoot.length() - 1);
                            }
                            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                        }
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", storagePath_New + File.separator + rootPath + constants.SD_CARD_TBSSOFT_PATH3
                                        + "/" + appName);
                    }

                } else {
                    webRoot = webRoot.substring(0, webRoot.length() - 1);
                    File file = new File(storagePath_New + "/" + rootPath + constants.SD_CARD_TBSSOFT_PATH3 + "/"
                            + webRoot.substring(webRoot.lastIndexOf("/")));
                    if (!file.exists()) {
                        copyNowApp(storagePath_New, rootPath, storagePath + "/" + rootPath + constants
                                .SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1), storagePath_New + "/" + rootPath +
                                constants.SD_CARD_TBSSOFT_PATH3 + "/"
                                + webRoot.substring(webRoot.lastIndexOf("/") + 1));
                        return false;
                    } else {
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "storagePath", storagePath_New);
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath);
                        String appName = StorageSetupActivity.this.getString(R.string.SD_CARD_TBSAPP_PATH2);
                        if (webRoot != "") {
                            if (webRoot.endsWith("/") == true) {
                                webRoot = webRoot.substring(0, webRoot.length() - 1);
                            }
                            appName = webRoot.substring(webRoot.lastIndexOf("/") + 1);
                        }
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", storagePath_New + File.separator + rootPath + constants.SD_CARD_TBSSOFT_PATH3
                                        + "/" + appName);
                    }

                }
            }
        }
        UIHelper.setSharePerference(this,
                constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
        UIHelper.setSharePerference(this,
                constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
        return true;
    }

    public void copyNowApp(final String storagePath, final String rootPath, final String res, final String des) {
        new AlertDialog.Builder(this).setMessage("新的根路径下无现应用路径,请做复制工作目录操作或者点击确定,应用会自动为你复制当前应用目录").setNegativeButton
                ("确定", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showModifyDialog();
                        MyAsyncTask myasynctask = new MyAsyncTask(res,
                                des, 3, StorageSetupActivity.this);
                        myasynctask.execute();
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "storagePath_New", "");
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath_New", "");
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "storagePath", storagePath);
                        UIHelper.setSharePerference(StorageSetupActivity.this,
                                constants.SAVE_LOCALMSGNUM, "rootPath", rootPath);
                        UIHelper.setSharePerference(StorageSetupActivity.this, constants.SAVE_INFORMATION,
                                "Path", des);
                    }
                }).setNeutralButton("取消", null).create().show();
    }
}