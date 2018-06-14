package com.tbs.tbsmis.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;

import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.update.AndroidDownloadAsyncTask;
import com.tbs.tbsmis.update.CbsDownloadAsyncTask;
import com.tbs.tbsmis.update.DownloadAsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class FileUtils
{
    private static final String TAG = "FileUtils";

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @param context
     * @param fileName
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";

        try {
            File file = new File(fileName);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文本文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return FileUtils.readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String readInStream(FileInputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }
            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.i("FileTest", e.getMessage());
        }
        return null;
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    /**
     * 向手机写图片
     *
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static boolean writeFile(byte[] buffer, String folder,
                                    String fileName) {
        boolean writeSucc = false;

        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory()
                    + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folderPath + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }

    /*
     * 保存返回信息为头像
     */
    public static void saveHead(String Head, String Path) {
        Bitmap bitmap = FileUtils.convertStringToIcon(Head);
        if (bitmap != null) {
            File fileTemp = FileUtils.createHeadFile(Path);
            writeFile(fileTemp, bitmap);
        }
    }

    /**
     * string转成bitmap
     *
     * @param st
     */
    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ����һ����userIdΪ�ļ�����ļ��������û�ͷ���ļ�·��Ϊ
     * "/sdcard/woliao/selfId/friendId.jpg"
     *
     * @param PATH
     * @return
     */
    public static File createHeadFile(String PATH) {
        File file = new File(PATH);
        if (!file.exists()) {
            try {
                file = new File(PATH.substring(0,
                        PATH.lastIndexOf(File.separator)));

                file.mkdirs();
                file = new File(PATH);
                boolean flag = file.createNewFile();
                if (flag) {
                    Log.d(FileUtils.TAG, "head file is creat:" + flag);
                } else {
                    Log.d(FileUtils.TAG, "head file isn't creat:" + flag);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static boolean writeFile(File file, Bitmap bitmap) {
        // Log.d("FileUtil", "cr:"+cr+", file:"+file+", uri:"+uri);
        boolean result = true;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Log.d("FileUtil", "fout:" + fos);
            // Bitmap bitmap =
            // BitmapFactory.decodeStream(cr.openInputStream(uri));
            Log.d("FileUtil", "bitmap:" + bitmap);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                result = false;
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            result = false;
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 拷贝一个文件,srcFile源文件，destFile目标文件
     *
     * @param destFile
     * @throws IOException
     */
    public static boolean copyFileTo(File srcFile, File destFile)
            throws IOException {
        if (srcFile.isDirectory() || destFile.isDirectory())
            return false;// 判断是否是文件
        if (!destFile.exists()) {// 如果文件不存在
            destFile.createNewFile();// 创建新文件
        }
        FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile);
        int readLen = 0;
        byte[] buf = new byte[1024];
        while ((readLen = fis.read(buf)) != -1) {
            fos.write(buf, 0, readLen);
        }
        fos.flush();
        fos.close();
        fis.close();
        return true;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return "";
        int point = filePath.lastIndexOf("/");
        if (point == -1)
            return "";
        return filePath.substring(point + 1);
        // }

    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getSmbFileName(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return "";
        int point = filePath.lastIndexOf("@");
        if (point == -1)
            return "";
        return filePath.substring(+1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        if (point == -1)
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 根据文件的下载路径（files/）获取文件相对路径
     *
     * @param filePath
     * @return
     */
    public static String getFileRelativepath(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.indexOf("files/");
        if (point == -1)
            return "";
        filePath = FileUtils.getPath(filePath);
        if (filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        }
        if (filePath.length() == point + 5)
            return filePath.substring(point + 5);
        else
            return filePath.substring(point + 6);
    }

    /**
     * 根据文件的下载URL获取文件相对路径
     *
     * @param filePath
     * @return
     */
    public static String geURLRelativeName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf(File.separator);
        String fileName = FileUtils.getFileName(filePath);
        if (fileName.indexOf("-") > 0) {
            fileName = fileName.substring(0, fileName.indexOf("-"));
            filePath = FileUtils.getPath(filePath) + fileName;
        } else if (fileName.indexOf(".") > 0) {
            fileName = fileName.substring(0, fileName.indexOf("."));
            filePath = FileUtils.getPath(filePath) + fileName;
        }

        return filePath.substring(point);
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtils.isEmpty(fileName))
            return "";
        int point = fileName.lastIndexOf('.');
        if (point == -1)
            return "";
        return fileName.substring(point + 1);
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * @param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        DecimalFormat df = new DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += FileUtils.getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * @param path
     * @return
     */
    public static long getFileList(String path) {
        long count = 0;
        File dir = new File(path);
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + FileUtils.getFileList(file.getAbsolutePath());// 递归
                count--;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * @param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;
    }

    /**
     * 检查路径是否存在
     *
     * @param path
     * @return
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return freeSpace;
    }

    /**
     * 新建目录
     *
     * @param directoryName
     * @return
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path + directoryName);
            status = newPath.mkdir();
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        status = sDCardStatus.equals(Environment.MEDIA_MOUNTED);
        return status;
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * @param fileName
     * @return
     */
    @SuppressLint("LongLogTag")
    public static void deleteDirectory(String fileName) {
        SecurityManager checker = new SecurityManager();
        if (!fileName.equals("")) {
            File newPath = new File(fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath + "/"
                                + listfile[i]);
                        if (deletedFile.isDirectory()) {
                            FileUtils.deleteDirectory(newPath + "/"
                                    + listfile[i]);
                        } else {
                            deletedFile.delete();
                        }
                    }
                    newPath.delete();
                    Log.d("DirectoryManager deleteDirectory", fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    @SuppressLint("LongLogTag")
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        if (fileName != null) {
            if (!fileName.equals("")) {
                File newPath = new File(fileName);
                checker.checkDelete(newPath.toString());
                if (newPath.isFile()) {
                    try {
                        Log.i("DirectoryManager deleteFile", fileName);
                        newPath.delete();
                        status = true;
                    } catch (SecurityException se) {
                        se.printStackTrace();
                        status = false;
                    }
                } else
                    status = false;
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除空目录
     * <p>
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 重命名
     *
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    @SuppressLint("LongLogTag")
    public static boolean deleteFileWithPath(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return false;
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            Log.i("DirectoryManager deleteFile", filePath);
            f.delete();
            return true;
        }
        return false;
    }

    /**
     * 获取SD卡的根目录，末尾带\
     *
     * @return
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     * @return 绝对路径
     */
    public static List<String> listAll(String root) {
        List<String> all = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (!f.isHidden() || !f.getName().startsWith("."))
                    all.add(f.getAbsolutePath());
            }
        }
        return all;
    }

    /**
     * 列出root目录下所有子目录和apk文件
     *
     * @param root
     * @return 绝对路径
     */
    public static List<String> listapk(String root) {
        List<String> all = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isFile()) {
                    if (f.getAbsolutePath().toLowerCase().endsWith(".apk"))
                        all.add(f.getAbsolutePath());
                } else {
                    if (!f.isHidden() || !f.getName().startsWith("."))
                        all.add(f.getAbsolutePath());
                }
            }
        }
        return all;
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     * @return 绝对路径
     */

    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            if (path.listFiles() != null) {
                for (File f : path.listFiles()) {
                    if (f.isDirectory()) {
                        if (!f.isHidden() || !f.getName().startsWith(".")) {
                            String absolutePath = f.getAbsolutePath();
                            if (com.tbs.tbsmis.file.Util.isNormalFile(absolutePath) && com.tbs.tbsmis.file.Util
                                    .shouldShowFile(absolutePath)) {
                                allDir.add(f.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        return allDir;
    }

    /**
     * 列出root目录下所有子目录
     *
     * @param root
     * @return 绝对路径
     */
    public static List<FileInfo> listFilePath(String root) {
        List<FileInfo> allDir = new ArrayList<FileInfo>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            if (path.listFiles() != null) {
                for (File f : path.listFiles()) {
                    if (f.isDirectory()) {
                        if (!f.isHidden() || !f.getName().startsWith(".")) {
                            String absolutePath = f.getAbsolutePath();
                            if (com.tbs.tbsmis.file.Util.isNormalFile(absolutePath) && com.tbs.tbsmis.file.Util
                                    .shouldShowFile(absolutePath)) {
                                FileInfo info = com.tbs.tbsmis.file.Util.GetFileInfo(f.getAbsolutePath());
                                allDir.add(info);
                            }
                        }
                    }
                }
            }
        }
        return allDir;
    }

    /**
     * 列出root目录下所有文件
     *
     * @param root
     * @return 绝对路径
     */
    public static List<FileInfo> GetallPath(String root) {
        List<FileInfo> allDir = new ArrayList<FileInfo>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            if (path.listFiles() != null) {
                for (File f : path.listFiles()) {
                    if (f.isDirectory()) {
                        if (!f.isHidden() || !f.getName().startsWith("."))
                            allDir.addAll(FileUtils.GetallPath(f.getAbsolutePath()));
                    } else if (f.isFile()) {
                        String absolutePath = f.getAbsolutePath();
                        if (com.tbs.tbsmis.file.Util.isNormalFile(absolutePath) && com.tbs.tbsmis.file.Util
                                .shouldShowFile(absolutePath)) {
                            FileInfo info = com.tbs.tbsmis.file.Util.GetFileInfo(f.getAbsolutePath());
                            allDir.add(info);
                        }
                    }
                }
            }
        }
        return allDir;
    }

    public enum PathStatus
    {
        SUCCESS, EXITS, ERROR
    }

    /**
     * 创建目录
     *
     * @param newPath
     */
    public static FileUtils.PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return FileUtils.PathStatus.EXITS;
        }
        if (path.mkdirs()) {
            return FileUtils.PathStatus.SUCCESS;
        } else {
            return FileUtils.PathStatus.ERROR;
        }
    }

    /**
     * 截取路径名
     *
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 截取路径信息
     *
     * @return
     */
    public static String getPath(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        // int end = absolutePath.length();
        return absolutePath.substring(0, start);
    }

    // android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    /**
     * 查找文件
     *
     * @param baseDirName    查找的文件夹路径
     * @param targetFileName 需要查找的文件名
     * @param fileList       查找到的文件集合
     */
    public static void findFiles(String baseDirName, String targetFileName,
                                 List<String> fileList) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的文件,不包括子文件夹，
         * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹跳过，程序结束，返回结果。
         */
        String tempName = null;
        // 判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            System.out.println("文件查找失败：" + baseDirName + "不是一个目录！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(baseDirName + filelist[i]);
                // System.out.println(readfile.getName());
                if (!readfile.isDirectory()) {
                    tempName = readfile.getName();
                    if (FileUtils.wildcardMatch(targetFileName, tempName)) {
                        // 匹配成功，将文件名添加到结果集
                        fileList.add(tempName);
                    }
                }
            }
        }
    }

    /**
     * 查找指定目录下的所有文件
     *
     * @param baseDirName 查找的文件夹路径
     * @param fileList    查找到的文件集合
     */
    public static void getDirFiles(String baseDirName, List<String> fileList) {
        File rootDir = new File(baseDirName);
        if (!rootDir.isDirectory()) {
            fileList.add(rootDir.getAbsolutePath());
        } else {
            String[] Listfile = rootDir.list();
            for (int i = 0; i < Listfile.length; i++) {
                baseDirName = rootDir.getAbsolutePath() + "/" + Listfile[i];
                FileUtils.getDirFiles(baseDirName, fileList);
            }
        }
    }

    /**
     * 通配符匹配
     *
     * @param pattern 通配符模式
     * @param str     待匹配的字符串
     * @return 匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (FileUtils.wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if (strIndex >= strLength || ch != str.charAt(strIndex)) {
                    return false;
                }
                strIndex++;
            }
        }
        return strIndex == strLength;
    }

    public static void downFile(Context context, String url, String path,
                                boolean isShow) {
        if (isShow) {
            AndroidDownloadAsyncTask task = new AndroidDownloadAsyncTask(
                    context, url, path);
            task.execute();
        } else {
            DownloadAsyncTask task = new DownloadAsyncTask(context, url, path);
            task.execute();
        }
    }

    static void updateData(Context context, String category) {
        CbsDownloadAsyncTask task = new CbsDownloadAsyncTask(context, category);
        task.execute();
    }

    public static long getNetFileSize(String path) {
        try {
//            URL url= new URL(path);
//            HttpURLConnection urlcon=(HttpURLConnection)url.openConnection();
//            //根据响应获取文件大小
//            long contentLength =urlcon.getContentLength();

            BasicHttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                    5000);
            HttpConnectionParams.setSoTimeout(httpParameters,
                    2000);
            HttpGet getMethod = new HttpGet(path);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(getMethod);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                long contentLength = response.getEntity().getContentLength();
                // Logs.d("contentLength:" + contentLength);
                return contentLength;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getFirstExterPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

}