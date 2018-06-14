package com.tbs.tbsmis.video;


import java.io.Serializable;

public class Video implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -7920222595800367956L;
    private int id = 0;
    private String title = "";
    private String album = "";
    private String artist = "";
    private String displayName = "";
    private String mimeType = "";
    private String path = "";
    private long size = 0;
    private long duration = 0;
    private String chapter = "";
    /** 视频标题拼音 */
    private String title_key = "";
    /**
     * 相关
     */
    private String code = "";
    private String txtPath = "";
    private String description = "";
    private String price = "";
    private String visitors = "";
    private String content = "";
    private String relateExam = "";
    private String relateExamUrl = "";
    private String relateKnowled = "";
    private String relateKnowledUrl = "";

    private String cate;
    private String tety;
    private Type type;

    public enum Type {
        LOCAL,
        ONLINE
    }
    public Video() {
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    /**
     * @param id
     * @param title
     * @param album
     * @param artist
     * @param displayName
     * @param mimeType
     * @param size
     * @param duration
     * @param title_key
     */
    public Video(int id, String title, String album, String artist,
                 String displayName, String mimeType, String path, long size,
                 long duration, String title_key) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.path = path;
        this.size = size;
        this.duration = duration;
        this.title_key = title_key;
    }
    public String getRelateExam() {
        return relateExam;
    }

    public void setRelateExam(String relateExam) {
        this.relateExam = relateExam;
    }

    public String getRelateExamUrl() {
        return relateExamUrl;
    }

    public void setRelateExamUrl(String relateExamUrl) {
        this.relateExamUrl = relateExamUrl;
    }

    public String getRelateKnowled() {
        return relateKnowled;
    }

    public void setRelateKnowled(String relateKnowled) {
        this.relateKnowled = relateKnowled;
    }

    public String getRelateKnowledUrl() {
        return relateKnowledUrl;
    }

    public void setRelateKnowledUrl(String relateKnowledUrl) {
        this.relateKnowledUrl = relateKnowledUrl;
    }


    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

//    //public LoadedImage getImage(){
//    	return this.image;
//    }

//    public void setImage(LoadedImage image){
//    	this.image = image;
//    }

    public final String getTitle_key(){
        return this.title_key;
    }

    public final void setTitle_key(String title_key){
        this.title_key = title_key;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTxtPath() {
        return txtPath;
    }

    public void setTxtPath(String txtPath) {
        this.txtPath = txtPath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getTety() {
        return tety;
    }

    public void setTety(String tety) {
        this.tety = tety;
    }
}