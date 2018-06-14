
package com.tbs.tbsmis.download;

import android.text.TextUtils;
import android.webkit.URLUtil;

import java.io.Serializable;

/**
 * ChapterDownloadTask class this class used to create a download task.
 * 
 * @author offbye@gmail.com
 */
public class ChapterDownloadTask implements Serializable
{
    public ChapterDownloadTask(){

    }
    /**
     * download url
     */
    private String url;

    /**
     * fileName
     */
    private String fileName;

    private String title;
    private String id;
    private String code;

    private String thumbnail;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Download file save path,default path is "/sdcard/download"
     */
    private String filePath;
    private String childName;
    private String chapter;
    private String section;
    private String time;
    private String sort;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    private String type;
    private String updateTime;

    /**
     * download finished Size
     */
    private long finishedSize;

    /**
     * total Size
     */
    private long totalSize;

    /**
     * finished percent
     */
    private int percent;

    private int speed;
    
    /**
     * download state
     */
    private volatile DownloadState downloadState;

    /**
     * ChapterDownloadTask constructor for create a new download task.
     * 
     * @param url must a http url.
     * @param filePath if filePath is null, we will use the default download
     *            path "/sdcard/download"
     * @param fileName file name, must input
     * @param title task title for display.Can be null
     * @param thumbnail task thumbnail image,should be a uri string. Can be null
     */
    public ChapterDownloadTask(String url, String filePath, String fileName, String title, String thumbnail) {
        if (!URLUtil.isHttpUrl(url)) {
            throw new IllegalArgumentException("invalid url,nust start with http://");
        }
        if (TextUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("invalid fileName");
        }
        this.url = url;
        this.fileName = fileName;
        this.title = title;
        this.thumbnail = thumbnail;
        this.filePath = filePath;
    }

    /**
     * get url
     * 
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * set url
     * 
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * get fileName
     * 
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * set fileName
     * 
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * get filePath
     * 
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * set filePath
     * 
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * get finishedSize
     * 
     * @return the finishedSize
     */
    public long getFinishedSize() {
        return finishedSize;
    }

    /**
     * set finishedSize
     * 
     * @param finishedSize the finishedSize to set
     */
    public void setFinishedSize(long finishedSize) {
        this.finishedSize = finishedSize;
    }

    /**
     * get totalSize
     * 
     * @return the totalSize
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * set totalSize
     * 
     * @param totalSize the totalSize to set
     */
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * get percent
     * 
     * @return the percent
     */
    public int getPercent() {
        return percent;
    }

    /**
     * set download percent
     * 
     * @param percent
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }
    
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * get downloadState
     * 
     * @return the downloadState
     */
    public DownloadState getDownloadState() {
        return downloadState;
    }

    /**
     * set downloadState
     * 
     * @param downloadState the downloadState to set
     */
    public void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChapterDownloadTask other = (ChapterDownloadTask) obj;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (filePath == null) {
            if (other.filePath != null)
                return false;
        } else if (!filePath.equals(other.filePath))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ChapterDownloadTask [url=" + url + ", finishedSize=" + finishedSize + ", totalSize="
                + totalSize + ", dlPercent=" + percent + ", downloadState=" + downloadState
                + ", fileName=" + fileName + ", title=" + title + "]";
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
