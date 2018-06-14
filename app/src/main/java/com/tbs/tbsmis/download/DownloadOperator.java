
package com.tbs.tbsmis.download;

import android.os.AsyncTask;
import android.util.Log;

import com.tbs.tbsmis.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Download worker
 *
 * @author offbye@gmail.com
 */
public class DownloadOperator extends AsyncTask<Void, Integer, Void>
{

    private static final int BUFFER_SIZE = 4096;

    private static final int UPDATE_DB_PER_SIZE = 102400;

    /**
     * debug tag
     */
    private static final String TAG = "DownloadOperator";


    private ChapterDownloadTask mChapterDownloadTask;

    /**
     * DownloadTaskManager
     */
    private DownloadTaskManager mDlTaskMng;

    /**
     * pause flag
     */
    private volatile boolean mPause = false;

    /**
     * stop flag, not used now.
     */
    private volatile boolean mStop = false;


    /**
     * Constructor
     *
     * @param dlTaskMng
     * @param chapterDownloadTask
     */
    DownloadOperator(DownloadTaskManager dlTaskMng, ChapterDownloadTask chapterDownloadTask) {
        mChapterDownloadTask = chapterDownloadTask;
        mDlTaskMng = dlTaskMng;
        Log.d(TAG, "file path : " + mChapterDownloadTask.getFilePath());
        Log.d(TAG, "file name : " + mChapterDownloadTask.getFileName());
        Log.d(TAG, "download url : " + mChapterDownloadTask.getUrl());
    }

    /**
     * <BR>
     *
     * @param params Void...
     * @return Void
     * @see AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
        // 1. create file if not exist.
        createFile();
        mChapterDownloadTask.setDownloadState(DownloadState.DOWNLOADING);
        mDlTaskMng.updateDownloadTask(mChapterDownloadTask);

        for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
            l.onDownloadStart();
        }

        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        InputStream is = null;
        long finishedSize = 0;
        long totalSize = 0;
        long startSize = 0;
        try {
            URL url = new URL(mChapterDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //conn.setRequestProperty("Accept-Encoding", "musixmatch");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Range", "bytes=" + mChapterDownloadTask.getFinishedSize() + "-"
                    + mChapterDownloadTask.getTotalSize());
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            conn.setRequestProperty("Connection", "Keep-Alive");

            accessFile = new RandomAccessFile(mChapterDownloadTask.getFilePath() + "/"
                    + mChapterDownloadTask.getFileName(), "rwd");
            accessFile.seek(mChapterDownloadTask.getFinishedSize());

            finishedSize = mChapterDownloadTask.getFinishedSize();
            totalSize = mChapterDownloadTask.getTotalSize();
            startSize = finishedSize;

            is = conn.getInputStream();
            Log.d(TAG, "downloadListeners size=" +  mDlTaskMng.getListeners(mChapterDownloadTask).size());

            Log.i(TAG, "start writing data to file.");
            //int size = totalSize / 200 > UPDATE_DB_PER_SIZE ? UPDATE_DB_PER_SIZE : totalSize / 200; //decrease refresh frequency
            byte[] buffer = new byte[BUFFER_SIZE];
            int length = -1;
            long startTime = System.currentTimeMillis();
            int speed = 0;
            while ((length = is.read(buffer)) != -1) {
                if(finishedSize >= totalSize){
                    break;
                }
                // pause download
                if (mPause) {
                    Log.i(TAG, "pause download, exit download loop.");
                    mChapterDownloadTask.setDownloadState(DownloadState.PAUSE);
                    mChapterDownloadTask.setFinishedSize(finishedSize);

                    for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
                        l.onDownloadPause();
                    }
                    mDlTaskMng.updateDownloadTask(mChapterDownloadTask);

                    return null;
                }

                // stop download, delete the download task
                /* if (mStop) {
                    Log.i(TAG, "stop download, exit download loop and delete download task.");
                    for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
                        l.onDownloadStop();
                    }
                    //mDlTaskMng.deleteDownloadTask(mChapterDownloadTask);
                    return null;
                } */

                finishedSize += length;
                //Log.d(TAG, "length=" +length);
                accessFile.write(buffer, 0, length);

                // update database per 100K.
                if (finishedSize - mChapterDownloadTask.getFinishedSize() > UPDATE_DB_PER_SIZE) {
                    mChapterDownloadTask.setFinishedSize(finishedSize);
                    mDlTaskMng.updateDownloadTask(mChapterDownloadTask);
                    speed =  (int)((finishedSize - startSize)/(int)(System.currentTimeMillis() + 1 - startTime));
                    publishProgress((int)finishedSize, (int)totalSize, speed);
                } else if (totalSize - finishedSize < UPDATE_DB_PER_SIZE) {//send message in this case
                    mChapterDownloadTask.setFinishedSize(finishedSize);
                    speed =  (int)((finishedSize - startSize)/(int)(System.currentTimeMillis() + 1 - startTime));
                    publishProgress((int)finishedSize, (int)totalSize, speed);
                }

            }
            conn.disconnect();

            mChapterDownloadTask.setDownloadState(DownloadState.FINISHED);
            mChapterDownloadTask.setFinishedSize(finishedSize);
            Log.d(TAG, "finished " + mChapterDownloadTask);
            mDlTaskMng.updateDownloadTask(mChapterDownloadTask);

            for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
                l.onDownloadFinish(mChapterDownloadTask.getUrl(),mChapterDownloadTask.getTitle());
            }
            mDlTaskMng.getListeners(mChapterDownloadTask).clear();
            mDlTaskMng.removeListener(mChapterDownloadTask);

        } catch (Exception e) {
            Log.e(TAG, "download exception : " + e.getMessage());
            e.printStackTrace();
            mChapterDownloadTask.setDownloadState(DownloadState.FAILED);
            mChapterDownloadTask.setFinishedSize(finishedSize);

            for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
                l.onDownloadFail();
            }
            mDlTaskMng.updateDownloadTask(mChapterDownloadTask);

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (accessFile != null) {
                    accessFile.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * <BR>
     *
     * @param values int array
     * @see AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        int finished = values[0];
        int total = values[1];
        int speed = values[2];

        for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
            l.onDownloadProgress(finished, total, speed);
        }
    }

    /**
     * pauseDownload
     */
    void pauseDownload() {
        Log.i(TAG, "pause download.");
        mPause = true;
        mStop = false;
    }

    /**
     * stopDownload
     */
    @Deprecated
    void stopDownload() {
        Log.i(TAG, "stop download.");
        mStop = true;
        mPause = false;
    }

    /**
     * continueDownload
     */
    void continueDownload() {
        Log.i(TAG, "continue download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * startDownload
     */
    void startDownload() {
        Log.i(TAG, "start download.");
        mPause = false;
        mStop = false;
        execute();
    }

    /**
     * createFile
     */
    private void createFile() {
        HttpURLConnection conn = null;
        RandomAccessFile accessFile = null;
        try {
          /*  URL url = new URL(mChapterDownloadTask.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);*/
            // conn.setRequestProperty("Accept-Encoding","musixmatch");
//            conn.setRequestProperty("Accept-Encoding", "identity");
            //conn.setRequestMethod("HEAD");
//            conn.setRequestMethod("GET");
//            int fileSize = conn.getContentLength();
            long fileSize = FileUtils.getNetFileSize(mChapterDownloadTask.getUrl());
            Log.i(TAG, "total size[" + fileSize + "]");
            mChapterDownloadTask.setTotalSize(fileSize);
            //conn.disconnect();

            File downFilePath = new File(mChapterDownloadTask.getFilePath());
            if (!downFilePath.exists()) {
                downFilePath.mkdirs();
            }

            File file = new File(mChapterDownloadTask.getFilePath() + "/" + mChapterDownloadTask.getFileName());
            if (!file.exists()) {
                file.createNewFile();

                // if new file created, then reset the finished size.
                mChapterDownloadTask.setFinishedSize(0);
            }

            accessFile = new RandomAccessFile(file, "rwd");
            Log.d(TAG, "fileSize:" + fileSize);
            if(fileSize > 0) {
                accessFile.setLength(fileSize);
            }
            accessFile.close();
        } catch (MalformedURLException e) {
            Log.e(TAG, "createFile MalformedURLException",e);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "createFile FileNotFoundException",e);
            for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
                l.onDownloadFail();
            }
        } catch (IOException e) {
            Log.e(TAG, "createFile IOException",e);
            for (DownloadListener l : mDlTaskMng.getListeners(mChapterDownloadTask)) {
                l.onDownloadFail();
            }
        }
    }


    protected static String md5(String string) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e(TAG, "NoSuchAlgorithm");
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    protected static String getKey(String aKey) {

        char[] aKeyChars = { 49, 87, 89, 90, 86, 50, 74, 78, 88, 82, 72, 51,
                79, 73, 71, 53, 67, 52, 80, 54, 65, 76, 55, 85, 70, 56, 83, 69,
                68, 57, 84, 66, 48, 81, 75, 77 };
        byte[] keyBytes;
        int patternLength;
        int keyCharsOffset;
        int i;
        int j;
        StringBuilder result = new StringBuilder("#####-#####-#####-#####-#####");
        keyBytes = aKey.getBytes();
        patternLength = result.length();
        keyCharsOffset = 0;
        i = 0;
        j = 0;
        while ((i < keyBytes.length) && (j < patternLength)) {
            keyCharsOffset = keyCharsOffset + Math.abs(keyBytes[i]);
            while (keyCharsOffset >= aKeyChars.length) {
                keyCharsOffset = keyCharsOffset - aKeyChars.length;
            }
            while ((result.charAt(j) != 35) && (j < patternLength)) {
                j++;
            }
            result.setCharAt(j, aKeyChars[keyCharsOffset]);
            if (i == (keyBytes.length - 1)) {
                i = -1;
            }
            i++;
            j++;
        }
        return result.toString();
    }

//    protected static int check(Context context) {
//        String key = ManifestMetaData.getString(context, "DOWNLOAD_KEY");
//        String pack = context.getPackageName();
//        StringBuilder sb = new StringBuilder();
//        sb.append(pack);
//        sb.reverse();
//        sb.append(pack);
//        if(key.equals(getKey(md5(sb.toString())))){
//            return 2;
//        } else if (key.equals("testkey")){
//            Toast.makeText(context, "The download manger you use is a trial version,please buy a license", Toast.LENGTH_LONG).show();
//            return 1;
//        } else {
//            Toast.makeText(context, "The download manger key you use is invalid,please buy a license", Toast.LENGTH_LONG).show();
//            return -1;
//        }
//    }

}
