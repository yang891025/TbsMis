package com.tbs.tbsmis.entity;

/**
 * 接收到的IPS信息实体类
 * 
 * @author pamchen-1
 * 
 */
public class NewsEntity {

	private String PicUrl = "";
	private String Title = "";
	private String Description = "";
	private String Url = "";
	private String author = "";
	private String content_source_url = "";
	private String Content = "";
	private String digest = "";
	private String show_cover_pic = "0";
	private String MsgType = "news";
	private String date = "";
	private String fileName = "";

    public String getSendType() {
        return this.sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    private String sendType = "";
	private long newsCount;

	public String getShowCoverPic() {
		return this.show_cover_pic;
	}

	public void setShowCoverPic(String recognition) {
        this.show_cover_pic = recognition;
	}

	public String getDigest() {
		return this.digest;
	}

	public void setDigest(String format) {
        this.digest = format;
	}

	public String getContent() {
		return this.Content;
	}

	public void setContent(String content) {
        this.Content = content;
	}

	public String getMsgType() {
		return this.MsgType;
	}

	public void setMsgType(String msgType) {
        this.MsgType = msgType;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String scale) {
        this.author = scale;
	}

	public String getContentUrl() {
		return this.content_source_url;
	}

	public void setContentUrl(String label) {
        this.content_source_url = label;
	}

	public String getTitle() {
		return this.Title;
	}

	public void setTitle(String title) {
        this.Title = title;
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(String description) {
        this.Description = description;
	}

	public String getUrl() {
		return this.Url;
	}

	public void setUrl(String url) {
        this.Url = url;
	}

	public String getPicUrl() {
		return this.PicUrl;
	}

	public void setPicUrl(String picUrl) {
        this.PicUrl = picUrl;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getNewsCount() {
		return this.newsCount;
	}

	public void setNewsCount(long newsCount) {
		this.newsCount = newsCount;
	}

}
