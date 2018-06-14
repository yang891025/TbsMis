
package com.tbs.tbsmis.download;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A single instance Download Manager, we use this class manage all download task.
 *
 * @author offbye@gmail.com
 */
public class DownloadTaskManager {

    private static final String TAG = "DownloadTaskManager";

    /**
     * default  save path: /sdcard/download
     */
    private static final String DEFAULT_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/download/";

    /**
     * single instance
     */
    private static DownloadTaskManager sMe;

    private static int mMaxTask = 0;

    /**
     * Download Database Helper
     */
    private DownloadDBHelper mDownloadDBHelper;

    /**
     * one download task own a download worker
     */
    private static HashMap<ChapterDownloadTask, DownloadOperator> mDownloadMap;

    private static HashMap<ChapterDownloadTask, CopyOnWriteArraySet<DownloadListener>> mDownloadListenerMap;

    /**
     * private constructor
     *
     * @param context
     */
    private DownloadTaskManager(Context context) {
        mDownloadMap = new HashMap<ChapterDownloadTask, DownloadOperator>();
        mDownloadListenerMap = new HashMap<ChapterDownloadTask, CopyOnWriteArraySet<DownloadListener>>();
        // 数据库操作对象实例化
        mDownloadDBHelper = new DownloadDBHelper(context, "download.db");
    }

    /**
     * Get a single instance of DownloadTaskManager
     *
     * @param context Context
     * @return DownloadTaskManager instance
     */
    public static synchronized DownloadTaskManager getInstance(Context context) {
        if (sMe == null) {
            sMe = new DownloadTaskManager(context);
//            if (DownloadOperator.check(context)< 2){
//                mMaxTask  = 5;
//            }
        }
        return sMe;
    }

    /**
     * Start new download Task, if a same download Task already existed,it will exit and leave a "task existed" log.
     *
     * @param chapterDownloadTask ChapterDownloadTask
     */
    public void startDownload(ChapterDownloadTask chapterDownloadTask) {
        if (chapterDownloadTask.getFilePath() == null || chapterDownloadTask.getFilePath().trim().length() == 0) {
            Log.w(TAG, "file path is invalid. file path : " + chapterDownloadTask.getFilePath()
                    + ", use default file path : " + DEFAULT_FILE_PATH);
            chapterDownloadTask.setFilePath(DEFAULT_FILE_PATH);
        }

        if (chapterDownloadTask.getFileName() == null || chapterDownloadTask.getFileName().trim().length() == 0) {
            Log.w(TAG, "file name is invalid. file name : " + chapterDownloadTask.getFileName());
            throw new IllegalArgumentException("file name is invalid");
        }

        if (null == chapterDownloadTask.getUrl() || !URLUtil.isHttpUrl(chapterDownloadTask.getUrl())) {
            Log.w(TAG, "invalid http url: " + chapterDownloadTask.getUrl());
            throw new IllegalArgumentException("invalid http url");
        }

        if (isUrlDownloaded(chapterDownloadTask.getUrl())) {
            Log.w(TAG, "task Downloaded");
            //continueDownload(chapterDownloadTask);
            return;
        }
        if( mMaxTask > 0 && mDownloadMap.size() > mMaxTask) {
            Log.w(TAG, "trial version can only add " + mMaxTask + " download task, please buy  a lincense");
            return;
        }
        //
        if (existRunningTask(chapterDownloadTask) && chapterDownloadTask.getDownloadState()!=DownloadState.FINISHED) {
            Log.w(TAG, "task existed");
            continueDownload(chapterDownloadTask);
            return;
        }
        chapterDownloadTask.setDownloadState(DownloadState.WAITING);
        if (null == mDownloadListenerMap.get(chapterDownloadTask)) {
            CopyOnWriteArraySet<DownloadListener> set = new CopyOnWriteArraySet<DownloadListener>();
            mDownloadListenerMap.put(chapterDownloadTask, set);
        }

        // save to database if the download task is valid, and start download.
        if (!chapterDownloadTask.equals(queryDownloadTask(chapterDownloadTask.getUrl()))) {
            Log.w(TAG, "task insertDownloadTask");
            insertDownloadTask(chapterDownloadTask);
        }else{
            Log.w(TAG, "task updateDownloadTask");
            updateDownloadTask(chapterDownloadTask);
        }

        DownloadOperator dlOperator = new DownloadOperator(this, chapterDownloadTask);
        mDownloadMap.put(chapterDownloadTask, dlOperator);
        dlOperator.startDownload();

    }

    /**
     * Pause a downloading task
     *
     * @param chapterDownloadTask ChapterDownloadTask
     */
    public void pauseDownload(ChapterDownloadTask chapterDownloadTask) {
        if (mDownloadMap.containsKey(chapterDownloadTask)) {
            mDownloadMap.get(chapterDownloadTask).pauseDownload();
            //mDownloadMap.remove(chapterDownloadTask);
        }

    }

    /**
     * Continue or restart a chapterDownloadTask.
     *
     * @param chapterDownloadTask ChapterDownloadTask
     */
    public void continueDownload(ChapterDownloadTask chapterDownloadTask) {
        if (chapterDownloadTask.getFilePath() == null || chapterDownloadTask.getFilePath().trim().length() == 0) {
            Log.w(TAG, "file path is invalid. file path : " + chapterDownloadTask.getFilePath()
                    + ", use default file path : " + DEFAULT_FILE_PATH);
            chapterDownloadTask.setFilePath(DEFAULT_FILE_PATH);
        }

        if (chapterDownloadTask.getFileName() == null || chapterDownloadTask.getFileName().trim().length() == 0) {
            Log.w(TAG, "file name is invalid. file name : " + chapterDownloadTask.getFileName());
            throw new IllegalArgumentException("file name is invalid");
        }

        if (null == chapterDownloadTask.getUrl() || !URLUtil.isHttpUrl(chapterDownloadTask.getUrl())) {
            Log.w(TAG, "invalid http url: " + chapterDownloadTask.getUrl());
            throw new IllegalArgumentException("invalid http url");
        }
        chapterDownloadTask.setDownloadState(DownloadState.WAITING);
        if (null == mDownloadListenerMap.get(chapterDownloadTask)) {
            CopyOnWriteArraySet<DownloadListener> set = new CopyOnWriteArraySet<DownloadListener>();
            Log.w(TAG, "add mDownloadListenerMap");
            mDownloadListenerMap.put(chapterDownloadTask, set);
        }
        // save to database if the download task is valid, and start download.
        if (!chapterDownloadTask.equals(queryDownloadTask(chapterDownloadTask.getUrl()))) {
            Log.w(TAG, "task insertDownloadTask");
            insertDownloadTask(chapterDownloadTask);
        }else{
            Log.w(TAG, "task updateDownloadTask");
            updateDownloadTask(chapterDownloadTask);
        }

        DownloadOperator dlOperator = new DownloadOperator(this, chapterDownloadTask);
        mDownloadMap.put(chapterDownloadTask, dlOperator);
        dlOperator.startDownload();

    }

    /**
     * Stop a task,this method  not used now。Please use pauseDownload instead.
     *
     * @param chapterDownloadTask ChapterDownloadTask
     */
    @Deprecated
    public void stopDownload(ChapterDownloadTask chapterDownloadTask) {
        mDownloadMap.get(chapterDownloadTask).stopDownload();
        mDownloadMap.remove(chapterDownloadTask);
    }

    /**
     * get all Download task from database
     *
     * @return ChapterDownloadTask list
     */
    public List<ChapterDownloadTask> getAllDownloadTask() {
        return mDownloadDBHelper.queryAll();
    }

    /**
     * get all Downloading task from database
     * @return ChapterDownloadTask list
     */
    public List<ChapterDownloadTask> getDownloadingTask() {
        return mDownloadDBHelper.queryUnDownloaded();
    }

    /**
     * get all download finished task from database
     * @return ChapterDownloadTask list
     */
    public List<ChapterDownloadTask> getFinishedDownloadTask() {
        return mDownloadDBHelper.queryDownloaded();
    }

    /**
     * insert a download task to database
     *
     * @param chapterDownloadTask
     */
    void insertDownloadTask(ChapterDownloadTask chapterDownloadTask) {
        mDownloadDBHelper.insert(chapterDownloadTask);
    }

    /**
     * update a download task to database
     *
     * @param chapterDownloadTask
     */
    void updateDownloadTask(ChapterDownloadTask chapterDownloadTask) {
        mDownloadDBHelper.update(chapterDownloadTask);
    }

    /**
     * delete a download task from download queue, remove it's listeners, and delete it from database.
     *
     * @param chapterDownloadTask
     */
    public void deleteDownloadTask(ChapterDownloadTask chapterDownloadTask) {
        if(chapterDownloadTask.getDownloadState() != DownloadState.FINISHED){
            for (DownloadListener l : getListeners(chapterDownloadTask)) {
                l.onDownloadStop();
            }
            getListeners(chapterDownloadTask).clear();
        }
        mDownloadMap.remove(chapterDownloadTask);
        mDownloadListenerMap.remove(chapterDownloadTask);
        mDownloadDBHelper.delete(chapterDownloadTask);
    }

    /**
     * delete a download task's download file.
     *
     * @param chapterDownloadTask
     */
    public void deleteDownloadTaskFile(ChapterDownloadTask chapterDownloadTask) {
        deleteFile(chapterDownloadTask.getFilePath() + "/" + chapterDownloadTask.getFileName());
    }

    /**
     * query a download task from database according url.
     *
     * @param url 下载url
     * @return ChapterDownloadTask
     */
    ChapterDownloadTask queryDownloadTask(String url) {
        return mDownloadDBHelper.query(url);
    }

    /**
     * query a download task is already running.
     * @param chapterDownloadTask
     * @return
     */
    public boolean existRunningTask(ChapterDownloadTask chapterDownloadTask) {
        return mDownloadMap.containsKey(chapterDownloadTask);
    }

    /**
     * Get all Listeners of a download task
     * @param chapterDownloadTask
     * @return
     */
    public CopyOnWriteArraySet<DownloadListener> getListeners(ChapterDownloadTask chapterDownloadTask) {
        if(null != mDownloadListenerMap.get(chapterDownloadTask)){
            return mDownloadListenerMap.get(chapterDownloadTask);
        } else {
            return new CopyOnWriteArraySet<DownloadListener>();//avoid null pointer exception
        }
    }

    /**
     * Register a DownloadListener to a chapterDownloadTask.
     * You can register many DownloadListener to a chapterDownloadTask in any time.
     * Such as register a listener to update you own progress bar, do something after file download finished.
     *
     * @param chapterDownloadTask
     * @param listener
     */
    public void registerListener(ChapterDownloadTask chapterDownloadTask, DownloadListener listener) {
        if (null != mDownloadListenerMap.get(chapterDownloadTask)) {
            mDownloadListenerMap.get(chapterDownloadTask).add(listener);
            Log.d(TAG, chapterDownloadTask.getFileName() + " addListener ");
        } else {
            CopyOnWriteArraySet<DownloadListener> set = new CopyOnWriteArraySet<DownloadListener>();
            mDownloadListenerMap.put(chapterDownloadTask, set);
            mDownloadListenerMap.get(chapterDownloadTask).add(listener);
        }
    }

    /**
     * Remove Listeners from  a chapterDownloadTask, you do not need manually call this method.
     * @param chapterDownloadTask
     */
    public void removeListener(ChapterDownloadTask chapterDownloadTask) {
        mDownloadListenerMap.remove(chapterDownloadTask);
    }

    /**
     * delete a file
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * If url exist in database and the download state is  FINISHED, and the file existed, return true.
     * @param url
     * @return
     */
    public boolean isUrlDownloaded(String url) {
        boolean re = false;
        ChapterDownloadTask task = mDownloadDBHelper.query(url);
        if (null != task) {
            if (task.getDownloadState() == DownloadState.FINISHED) {
                File file = new File(task.getFilePath() + "/" + task.getFileName());
                if (file.isFile()&&file.exists()) {
                    re = true;
                }
            }
        }
        return re;
    }
    /**
     * If url exist in database and the download state is  Downloading, and the file existed, return true.
     * @param url
     * @return
     */
    public boolean isUrlDownloading(String url){
        boolean re = false;
        ChapterDownloadTask task = mDownloadDBHelper.query(url);
        if (null != task) {
            if (task.getDownloadState() == DownloadState.DOWNLOADING) {
                re = true;
            }
        }
        //System.out.println(task.getFilePath() + "/" + task.getFileName()+" = "+ re);
        return re;
    }
    /**
     * If url exist in database and the download state is  Pause, and the file existed, return true.
     * @param url
     * @return
     */
    public boolean isUrlDownloadPause(String url){
        boolean re = false;
        ChapterDownloadTask task = mDownloadDBHelper.query(url);
        if (null != task) {
            if (task.getDownloadState() == DownloadState.PAUSE) {
                re = true;
            }
        }
        //System.out.println(task.getFilePath() + "/" + task.getFileName()+" = "+ re);
        return re;
    }
    /**
     * If url exist in database and the download state is  Waiting, and the file existed, return true.
     * @param url
     * @return
     */
    public boolean isUrlDownloadWaiting(String url){
        boolean re = false;
        ChapterDownloadTask task = mDownloadDBHelper.query(url);
        if (null != task) {
            if (task.getDownloadState() == DownloadState.WAITING) {
                re = true;
            }
        }
        //System.out.println(task.getFilePath() + "/" + task.getFileName()+" = "+ re);
        return re;
    }
}
