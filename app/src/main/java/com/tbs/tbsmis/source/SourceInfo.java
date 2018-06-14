package com.tbs.tbsmis.source;

/**
 * Created by TBS on 2016/7/6.
 */
public class SourceInfo
{
    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPath() {
        return this.Path;
    }

    public void setPath(String path) {
        this.Path = path;
    }

    public long getSize() {
        return this.Size;
    }

    public void setSize(long size) {
        this.Size = size;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getPlay() {
        return this.play;
    }

    public void setPlay(long play) {
        this.play = play;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStop() {
        return this.stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getNetworkPath() {
        return this.networkPath;
    }

    public void setNetworkPath(String networkPath) {
        this.networkPath = networkPath;
    }

    public String getLongtime() {
        return this.longtime;
    }

    public void setLongtime(String longtime) {
        this.longtime = longtime;
    }

    public long getDownload() {
        return this.download;
    }

    public void setDownload(long download) {
        this.download = download;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setTitle(String title) {
        this.Title = title;
    }
    private String content = "";

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    private String range = "";
    private String txtPath = "";
    private String relateExam = "";
    private String relateExamUrl = "";
    private String relateKnowled = "";

    public String getRelateKnowledUrl() {
        return relateKnowledUrl;
    }

    public void setRelateKnowledUrl(String relateKnowledUrl) {
        this.relateKnowledUrl = relateKnowledUrl;
    }

    public String getRelateKnowled() {
        return relateKnowled;
    }

    public void setRelateKnowled(String relateKnowled) {
        this.relateKnowled = relateKnowled;
    }

    public String getRelateExamUrl() {
        return relateExamUrl;
    }

    public void setRelateExamUrl(String relateExamUrl) {
        this.relateExamUrl = relateExamUrl;
    }

    public String getRelateExam() {
        return relateExam;
    }

    public void setRelateExam(String relateExam) {
        this.relateExam = relateExam;
    }

    private String relateKnowledUrl = "";
    private String Title = "";
    private String Name = "";
    private String code = "";
    private String type = "";
    private String pic = "";
    private String Path = "";
    private String upateUrl = "";
    private long Size = 0;
    private int isStop = 0;

    private String category = "";

    private String description = "";

    private long download = 0;

    private String time = "";

    private long play = 0;

    private String longtime  = "";

    private String stop = "";
    private String networkPath = "";
    private String chapter = "";
    private int location = 0;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUpateUrl() {
        return upateUrl;
    }

    public void setUpateUrl(String upateUrl) {
        this.upateUrl = upateUrl;
    }

    public int getIsStop() {
        return isStop;
    }

    public void setIsStop(int isStop) {
        this.isStop = isStop;
    }
}
