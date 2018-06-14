/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tbs.Tbszlib.JTbszlib;
import com.tbs.fts.FtClient;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.AppContext;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileOperationHelper
{
    private static final String LOG_TAG = "FileOperation";

    private final ArrayList<FileInfo> mCurFileNameList = new ArrayList<FileInfo>();
    private boolean mMoving;

    private final FileOperationHelper.IOperationProgressListener mOperationListener;

    private FilenameFilter mFilter;

    public interface IOperationProgressListener
    {
        void onFinish();

        void onFileChanged(String path);
    }

    public FileOperationHelper(FileOperationHelper.IOperationProgressListener l) {
        this.mOperationListener = l;
    }

    public void setFilenameFilter(FilenameFilter f) {
        this.mFilter = f;
    }

    public boolean CreateFolder(String path, String name) {
        Log.v(FileOperationHelper.LOG_TAG, "CreateFolder >>> " + path + "," + name);

        File f = new File(Util.makePath(path, name));
        if (f.exists())
            return false;

        return f.mkdir();
    }

    public void Copy(ArrayList<FileInfo> files) {
        this.copyFileList(files);
    }

    public boolean Paste(String path) {
        if (!this.mCurFileNameList.get(0).isLoacal)
            return false;
        final String _path = path;
        this.asnycExecute(new Runnable()
        {
            @Override
            public void run() {
                for (FileInfo f : FileOperationHelper.this.mCurFileNameList) {
                    FileOperationHelper.this.CopyFile(f, _path);
                }
                FileOperationHelper.this.mOperationListener.onFileChanged(Environment
                        .getExternalStorageDirectory().getAbsolutePath());
                FileOperationHelper.this.clear();
            }
        });
        return true;
    }

    public void Paste(final Context context, final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("粘贴");
//                builder.setMessage("");
        builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.archive_progress_dialog, null);
        LinearLayout time_remaining_panel = (LinearLayout) v.findViewById(R.id.time_remaining_panel);
        //LinearLayout total_progress_panel = (LinearLayout) v.findViewById(R.id.total_progress_panel);
        LinearLayout item_progress_panel = (LinearLayout) v.findViewById(R.id.item_progress_panel);
        //final TextView item_percent = (TextView) v.findViewById(R.id.item_percent);
        final ProgressBar total_progress_bar = (ProgressBar) v.findViewById(R.id.total_progress_bar);
        //final TextView time_remaining = (TextView) v.findViewById(R.id.time_remaining);
        final TextView message = (TextView) v.findViewById(R.id.message);
        TextView from = (TextView) v.findViewById(R.id.from);
        TextView to = (TextView) v.findViewById(R.id.to);
        //final TextView curr_message = (TextView) v.findViewById(R.id.curr_message);
        final TextView num_completed = (TextView) v.findViewById(R.id.num_completed);
        final TextView num_total = (TextView) v.findViewById(R.id.num_total);
        final TextView total_percent = (TextView) v.findViewById(R.id.total_percent);
        final TextView time_remaining_title = (TextView) v.findViewById(R.id.time_remaining_title);
        final TextView speed_t = (TextView) v.findViewById(R.id.speed);
        time_remaining_panel.setVisibility(View.VISIBLE);
        item_progress_panel.setVisibility(View.GONE);
        time_remaining_title.setVisibility(View.GONE);
        builder.setView(v);
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                OkGo.getInstance().cancelTag(this);
                dialog.dismiss();
            }
        });
        final AlertDialog ModifyDialog = builder.create();
        ModifyDialog.setCanceledOnTouchOutside(false);
        message.setText("正在计算文件个数...");
//                from.setText(mCurFileNameList.get(0).filePath.substring(0, mCurFileNameList.get(0).filePath
//                        .lastIndexOf("/")));
        to.setText(path);
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        IniFile IniFile = new IniFile();
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appTestFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appTestFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        constants.verifyURL = "http://"
                + IniFile.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + IniFile.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + "/EBS/DIRServlet";
        String fileName = mCurFileNameList.get(0).fileName;
        for (int i = 1; i < mCurFileNameList.size(); i++) {
            fileName = fileName + "&selFiles=" + mCurFileNameList.get(i).fileName;
        }
        constants.verifyURL = constants.verifyURL + "?selFiles=" + fileName;
        Map<String, String> params = new HashMap<String, String>();
        String parentPath = FileOperationHelper.this.mCurFileNameList.get(0).filePath;
        from.setText("网盘"+parentPath);
//        for (FileInfo f : FileOperationHelper.this.mCurFileNameList) {
//            params.put("selFiles", f.fileName);
//        }
        params.put("act", "downloadUserFile");
        params.put("type", "disk");
        params.put("path", parentPath);
        params.put("account", IniFile.getIniString(userIni, "Login",
                "Account", "", (byte) 0));
        ModifyDialog.show();
        OkGo.<File>get(constants.verifyURL)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params(params, false)
                .execute(new FileCallback(path, "temp.zip")
                {

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        message.setText("正在下载中");
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        message.setText("正在解压文件...");
                        JTbszlib.UnZipFile(path+"/temp.zip",path, 1, "");
                        FileUtils.deleteFileWithPath(path+"/temp.zip");
                        Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                        FileOperationHelper.this.mOperationListener.onFileChanged(Environment
                                .getExternalStorageDirectory().getAbsolutePath());
                        FileOperationHelper.this.clear();
                        ModifyDialog.dismiss();
                    }

                    @Override
                    public void onError(Response<File> response) {
                        ModifyDialog.dismiss();
                        Toast.makeText(context, "下载出错，已取消", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        NumberFormat numberFormat = NumberFormat.getPercentInstance();
                        numberFormat.setMinimumFractionDigits(2);
                        String downloadLength = Formatter.formatFileSize(context, progress
                                .currentSize);
                        String totalLength = Formatter.formatFileSize(context, progress
                                .totalSize);
                        num_completed.setText(downloadLength );
                        num_total.setText(totalLength);
                        String speed = Formatter.formatFileSize(context, progress.speed);
                        speed_t.setText(String.format("%s/s", speed));
                        total_percent.setText(numberFormat.format(progress.fraction));
                        total_progress_bar.setMax(10000);
                        total_progress_bar.setProgress((int) (progress.fraction * 10000));
                    }
                });
    }

    public boolean Paste(final String path, final FtMSGNotify ftMSGNotify) {
        String _path = path;
        this.asnycExecute(new Runnable()
        {
            @Override
            public void run() {
                ftMSGNotify.setDesPath(FileOperationHelper.this.mCurFileNameList.get(0).filePath);
                long size = 0;
                long sum = 0;
                List<FileInfo> mOperFileNameList = new ArrayList<FileInfo>();
                String messages = "名称:";
                try {
                    IniFile m_iniFileIO = new IniFile();
                    String webRoot = UIHelper.getSoftPath(AppContext.getInstance());
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    webRoot += AppContext.getInstance().getString(R.string.SD_CARD_TBSAPP_PATH2);
                    webRoot = UIHelper.getShareperference(AppContext.getInstance(), constants
                                    .SAVE_INFORMATION,
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
                        String dataPath = AppContext.getInstance().getFilesDir().getParentFile()
                                .getAbsolutePath();
                        if (dataPath.endsWith("/") == false) {
                            dataPath = dataPath + "/";
                        }
                        userIni = dataPath + "TbsApp.ini";
                    }
                    String ftsAddress = m_iniFileIO.getIniString(userIni, "Skydrive",
                            "ftsAddress", constants.DefaultServerIp, (byte) 0);
                    String ftsPort = m_iniFileIO.getIniString(userIni, "Skydrive",
                            "ftsPort", "1239", (byte) 0);
                    FtClient ftclient = FtClient.createObject("fts://" + ftsAddress + ":" + ftsPort,
                            FileOperationHelper.this.mCurFileNameList.get(0).filePath);

                    String LoginId = m_iniFileIO.getIniString(userIni, "Login",
                            "LoginId", "", (byte) 0);
                    ftclient.setLoginID(LoginId, "");
                    ftclient.connect();
                    if (ftclient == null) {
                        ftMSGNotify.setCancelFlag(true);
                        ftMSGNotify.setiCancelFlag(3);
                        return;
                    }
//
                    for (FileInfo f : FileOperationHelper.this.mCurFileNameList) {
                        if (ftMSGNotify.getiCancelFlag() == 2) {
                            return;
                        }
                        if (f.IsDir) {
                            String destPath = Util.makePath(path, f.fileName);
                            File destFile = new File(destPath);
                            int i = 1;
                            while (destFile.exists()) {
                                destPath = Util.makePath(path, f.fileName + "(" + i++ + ")");
                                destFile = new File(destPath);
                            }
                            long dir = 0;
                            List<com.tbs.fts.FileInfo> FileNameList = ftclient.listEx(f.fileName + "/*",
                                    true);
                            for (com.tbs.fts.FileInfo fs : FileNameList) {
                                //System.out.println("f.fileName = " + fs.getM_path());
                                if (!fs.isDirectory()) {
                                    FileInfo Fileinfo = new FileInfo();
                                    Fileinfo.fileName = f.fileName + "/" + fs.getM_path();
                                    Fileinfo.fileSize = fs.getLength();
                                    Fileinfo.IsDir = fs.isDirectory();
                                    Fileinfo.filePath = ftclient.getCwd();
                                    Fileinfo.refileName = destPath.substring(destPath.lastIndexOf("/") + 1)
                                            + "/" + fs.getM_path();
                                    Fileinfo.canRead = fs.isReadonly();
                                    Fileinfo.ModifiedDate = fs.getModifyTime();
                                    Fileinfo.Selected = true;
                                    Fileinfo.isLoacal = false;
                                    dir = dir + fs.getLength();
                                    mOperFileNameList.add(Fileinfo);
                                }
                            }
                            // mOperFileNameList.addAll(FileNameList);
                            sum = mOperFileNameList.size();
                            size = size + dir;
                            ftMSGNotify.setAllPath("总共:" + sum +
                                    "项,  大小:" + FileUtils.formatFileSize(size));
                        } else {
                            String destPath = Util.makePath(path, f.fileName);
                            File destFile = new File(path + "/" + f.filePath);
                            int i = 1;
                            while (destFile.exists()) {
                                destPath = Util.makePath(path, f.fileName + "(" + i++ + ")");
                                destFile = new File(destPath);
                            }
                            size = size + f.fileSize;
                            f.refileName = destPath.substring(destPath.lastIndexOf("/") + 1);
                            mOperFileNameList.add(f);
                            sum = mOperFileNameList.size();
                            ftMSGNotify.setAllPath("总共:" + sum +
                                    "项,  大小:" + FileUtils.formatFileSize(size));
                        }
                        messages = messages + f.fileName + ",";
                    }
                    //System.out.println("message = "+messages);
                    ftMSGNotify.setAllmessage(messages.substring(0, messages.lastIndexOf(",")));
                    ftMSGNotify.setFileCount((int) sum);
                    ftMSGNotify.setTotalSize(size);
                    int i = 0;
                    long filesize = 0;
                    for (FileInfo f : mOperFileNameList) {
                        if (ftMSGNotify.getiCancelFlag() == 2) {
                            break;
                        }
                        ftMSGNotify.setCancelFlag(false);
                        ftMSGNotify.setCfilecount(i++);
                        File destFile = new File(FileUtils.getPath(path + "/" + f.refileName));
                        if (!destFile.exists())
                            destFile.mkdirs();
                        ftMSGNotify.setiSize(ftMSGNotify.getiSize() + filesize);
                        filesize = f.fileSize;
                        ftclient.downloadToFile(f.fileName, path + "/" + f.refileName, ftMSGNotify, true);
                    }
                    ftMSGNotify.setiSize(size);
                    if (mOperFileNameList.size() <= 0) {
                        ftMSGNotify.setCancelFlag(true);
                        ftMSGNotify.setiCancelFlag(7);
                    }
                    ftclient.disconnect();
                } catch (URISyntaxException e) {
                    ftMSGNotify.setCancelFlag(true);
                    ftMSGNotify.setiCancelFlag(4);
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    ftMSGNotify.setiCancelFlag(5);
                    ftMSGNotify.setCancelFlag(true);
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                FileOperationHelper.this.mOperationListener.onFileChanged(Environment
                        .getExternalStorageDirectory().getAbsolutePath());
                FileOperationHelper.this.clear();
            }
        });
        return true;
    }

    public boolean canPaste() {
        return this.mCurFileNameList.size() != 0;
    }

    public void StartMove(ArrayList<FileInfo> files) {
        if (this.mMoving)
            return;
        this.mMoving = true;
        this.copyFileList(files);
    }

    public boolean isMoveState() {
        return this.mMoving;
    }

    public boolean canMove(String path) {
        for (FileInfo f : this.mCurFileNameList) {
            if (!f.IsDir)
                continue;

            if (Util.containsPath(f.filePath, path))
                return false;
        }

        return true;
    }

    public void clear() {
        synchronized (this.mCurFileNameList) {
            this.mCurFileNameList.clear();
        }
    }

    public boolean EndMove(String path) {
        if (!this.mMoving)
            return false;
        this.mMoving = false;

        if (TextUtils.isEmpty(path))
            return false;

        final String _path = path;
        this.asnycExecute(new Runnable()
        {
            @Override
            public void run() {
                for (FileInfo f : FileOperationHelper.this.mCurFileNameList) {
                    FileOperationHelper.this.MoveFile(f, _path);
                }
                FileOperationHelper.this.mOperationListener.onFileChanged(Environment
                        .getExternalStorageDirectory().getAbsolutePath());
                FileOperationHelper.this.clear();
            }
        });

        return true;
    }

    public ArrayList<FileInfo> getFileList() {
        return this.mCurFileNameList;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void asnycExecute(Runnable r) {
        final Runnable _r = r;
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... params) {
                synchronized (FileOperationHelper.this.mCurFileNameList) {
                    _r.run();
                }
                if (FileOperationHelper.this.mOperationListener != null) {
                    FileOperationHelper.this.mOperationListener.onFinish();
                }

                return null;
            }
        }.execute();
    }

    public boolean isFileSelected(String path) {
        synchronized (this.mCurFileNameList) {
            for (FileInfo f : this.mCurFileNameList) {
                if (f.filePath.equalsIgnoreCase(path))
                    return true;
            }
        }
        return false;
    }

    public boolean Rename(FileInfo f, String newName) {
        if (f == null || newName == null) {
            Log.e(FileOperationHelper.LOG_TAG, "Rename: null parameter");
            return false;
        }

        File file = new File(f.filePath);
        String newPath = Util.makePath(Util.getPathFromFilepath(f.filePath),
                newName);
        boolean needScan = file.isFile();
        try {
            boolean ret = file.renameTo(new File(newPath));
            if (ret) {
                if (needScan) {
                    this.mOperationListener.onFileChanged(f.filePath);
                }
                this.mOperationListener.onFileChanged(newPath);
            }
            return ret;
        } catch (SecurityException e) {
            Log.e(FileOperationHelper.LOG_TAG, "Fail to rename file," + e);
        }
        return false;
    }

    public boolean Delete(ArrayList<FileInfo> files) {
        this.copyFileList(files);
        this.asnycExecute(new Runnable()
        {
            @Override
            public void run() {
                for (FileInfo f : FileOperationHelper.this.mCurFileNameList) {
                    FileOperationHelper.this.DeleteFile(f);
                }

                FileOperationHelper.this.mOperationListener.onFileChanged(Environment
                        .getExternalStorageDirectory().getAbsolutePath());

                FileOperationHelper.this.clear();
            }
        });
        return true;
    }

    protected void DeleteFile(FileInfo f) {
        if (f == null) {
            Log.e(FileOperationHelper.LOG_TAG, "DeleteFile: null parameter");
            return;
        }

        File file = new File(f.filePath);
        boolean directory = file.isDirectory();
        if (directory) {
            for (File child : file.listFiles(this.mFilter)) {
                if (Util.isNormalFile(child.getAbsolutePath())) {
                    this.DeleteFile(Util.GetFileInfo(child, this.mFilter, true));
                }
            }
        }

        file.delete();

        Log.v(FileOperationHelper.LOG_TAG, "DeleteFile >>> " + f.filePath);
    }

    private void CopyFile(FileInfo f, String dest) {
        if (f == null || dest == null) {
            Log.e(FileOperationHelper.LOG_TAG, "CopyFile: null parameter");
            return;
        }

        File file = new File(f.filePath);
        if (file.isDirectory()) {

            // directory exists in destination, rename it
            String destPath = Util.makePath(dest, f.fileName);
            File destFile = new File(destPath);
            int i = 1;
            while (destFile.exists()) {
                destPath = Util.makePath(dest, f.fileName + " " + i++);
                destFile = new File(destPath);
            }

            for (File child : file.listFiles(this.mFilter)) {
                if (!child.isHidden()
                        && Util.isNormalFile(child.getAbsolutePath())) {
                    this.CopyFile(Util.GetFileInfo(child, this.mFilter, Settings
                            .instance().getShowDotAndHiddenFiles()), destPath);
                }
            }
        } else {
            @SuppressWarnings("unused")
            String destFile = Util.copyFile(f.filePath, dest);
        }
        Log.v(FileOperationHelper.LOG_TAG, "CopyFile >>> " + f.filePath + "," + dest);
    }

    private boolean MoveFile(FileInfo f, String dest) {
        Log.v(FileOperationHelper.LOG_TAG, "MoveFile >>> " + f.filePath + "," + dest);

        if (f == null || dest == null) {
            Log.e(FileOperationHelper.LOG_TAG, "CopyFile: null parameter");
            return false;
        }

        File file = new File(f.filePath);
        String newPath = Util.makePath(dest, f.fileName);
        try {
            return file.renameTo(new File(newPath));
        } catch (SecurityException e) {
            Log.e(FileOperationHelper.LOG_TAG, "Fail to move file," + e);
        }
        return false;
    }

    private void copyFileList(ArrayList<FileInfo> files) {
        synchronized (this.mCurFileNameList) {
            this.mCurFileNameList.clear();
            for (FileInfo f : files) {
                this.mCurFileNameList.add(f);
            }
        }
    }

}
