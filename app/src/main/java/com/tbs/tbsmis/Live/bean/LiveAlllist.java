package com.tbs.tbsmis.Live.bean;

import java.io.Serializable;

/**
 *  版本号：1.0
 *  类描述： 全部直播
 *  备注消息：
 *  修改时间：2017/2/7 下午5:21
 **/
public class LiveAlllist implements Serializable
{

    /**
     * room_id : 522423
     * room_src : https://rpic.douyucdn.cn/a1702/07/17/522423_170207171134.jpg
     * owner_pic : https://rpic.douyucdn.cn/a1702/07/17/522423_170207171134.jpg
     * isVertical : 0
     * cate_id : 1
     * room_name : 2017LCK春季赛 AFS VS ROX
     * show_status : 1
     * subject :
     * show_time : 1486432800
     * owner_uid : 34222876
     * specific_catalog : lck
     * specific_status : 1
     * vod_quality : 0
     * nickname : Riot丶LCK
     * online : 788228
     * url : /lck
     * game_url : /directory/game/LOL
     * game_name : 英雄联盟
     * child_id : 37
     * avatar : https://apic.douyucdn.cn/upload/avanew/face/201612/22/11/d78b969ee15585976403feee9f246d51_big.jpg
     * tel : https://apic.douyucdn.cn/upload/avanew/face/201612/22/11/d78b969ee15585976403feee9f246d51_middle.jpg
     * qq : https://apic.douyucdn.cn/upload/avanew/face/201612/22/11/d78b969ee15585976403feee9f246d51_small.jpg
     * jumpUrl :
     * fans : 498556
     * ranktype : 2
     * anchor_city :
     */
    private String id;
    private String room_id;
    private String room_src;
    private String owner_pic;
    private int isVertical;
    private int cate_id;
    private String room_name;
    private String show_status;
    private String subject;
    private String show_time;
    private String owner_uid;
    private String specific_catalog;
    private String specific_status;
    private String vod_quality;
    private String nickname;
    private int online;
    private String url;
    private String game_url;
    private String game_name;
    private int child_id;
    private String avatar;
    private String tel;
    private String qq;
    private String jumpUrl;
    private String fans;
    private int ranktype;
    private String anchor_city;

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_src() {
        return room_src;
    }

    public void setRoom_src(String room_src) {
        this.room_src = room_src;
    }

    public String getOwner_pic() {
        return owner_pic;
    }

    public void setOwner_pic(String owner_pic) {
        this.owner_pic = owner_pic;
    }

    public int getIsVertical() {
        return isVertical;
    }

    public void setIsVertical(int isVertical) {
        this.isVertical = isVertical;
    }

    public int getCate_id() {
        return cate_id;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getShow_status() {
        return show_status;
    }

    public void setShow_status(String show_status) {
        this.show_status = show_status;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getShow_time() {
        return show_time;
    }

    public void setShow_time(String show_time) {
        this.show_time = show_time;
    }

    public String getOwner_uid() {
        return owner_uid;
    }

    public void setOwner_uid(String owner_uid) {
        this.owner_uid = owner_uid;
    }

    public String getSpecific_catalog() {
        return specific_catalog;
    }

    public void setSpecific_catalog(String specific_catalog) {
        this.specific_catalog = specific_catalog;
    }

    public String getSpecific_status() {
        return specific_status;
    }

    public void setSpecific_status(String specific_status) {
        this.specific_status = specific_status;
    }

    public String getVod_quality() {
        return vod_quality;
    }

    public void setVod_quality(String vod_quality) {
        this.vod_quality = vod_quality;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public int getChild_id() {
        return child_id;
    }

    public void setChild_id(int child_id) {
        this.child_id = child_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public int getRanktype() {
        return ranktype;
    }

    public void setRanktype(int ranktype) {
        this.ranktype = ranktype;
    }

    public String getAnchor_city() {
        return anchor_city;
    }

    public void setAnchor_city(String anchor_city) {
        this.anchor_city = anchor_city;
    }

    @Override
    public String toString() {
        return "{" +
                "room_id:'" + room_id + '\'' +
                ", room_src:'" + room_src + '\'' +
                ", owner_pic:'" + owner_pic + '\'' +
                ", isVertical:" + isVertical +
                ", cate_id:" + cate_id +
                ", room_name:'" + room_name + '\'' +
                ", show_status:'" + show_status + '\'' +
                ", subject:'" + subject + '\'' +
                ", show_time:'" + show_time + '\'' +
                ", owner_uid:'" + owner_uid + '\'' +
                ", specific_catalog:'" + specific_catalog + '\'' +
                ", specific_status:'" + specific_status + '\'' +
                ", vod_quality:'" + vod_quality + '\'' +
                ", nickname:'" + nickname + '\'' +
                ", online:" + online +
                ", url:'" + url + '\'' +
                ", game_url:'" + game_url + '\'' +
                ", game_name:'" + game_name + '\'' +
                ", child_id:" + child_id +
                ", avatar:'" + avatar + '\'' +
                ", tel:'" + tel + '\'' +
                ", qq:'" + qq + '\'' +
                ", jumpUrl:'" + jumpUrl + '\'' +
                ", fans:'" + fans + '\'' +
                ", ranktype:" + ranktype +
                ", anchor_city:'" + anchor_city + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
