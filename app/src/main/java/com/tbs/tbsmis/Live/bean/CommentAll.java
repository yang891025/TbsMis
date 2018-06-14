package com.tbs.tbsmis.Live.bean;

import android.text.TextUtils;

import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.circle.bean.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TBS on 2018/1/11.
 */

public class CommentAll implements Serializable
{
    private String id;
    private String content;
    private String path;
    private String createTime;
    private List<CommentItem> comments;
    private List<FavortItem> favorters;
    private List<FavortItem> unFavorters;
    private User user;
    private String type;//1:链接  2:图片 3:视频
    private boolean isExpand;
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CommentAll{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", comments=" + comments +
                ", user=" + user +
                ", type='" + type + '\'' +
                ", isExpand=" + isExpand +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpand(boolean isExpand){
        this.isExpand = isExpand;
    }

    public boolean isExpand(){
        return this.isExpand;
    }

    public boolean hasComment(){
        return comments != null && comments.size() > 0;
    }
    public int hasFavorter(String curUserId){
        int favortid = -1;
        if(!TextUtils.isEmpty(curUserId) && favorters != null && favorters.size() > 0){
            for(FavortItem item : favorters){
                favortid = favortid + 1;
                if(curUserId.equals(item.getUserName())){
                    return favortid;
                }
            }
        }
        return -1;
    }
    public int hasUnFavorter(String curUserId){
        int favortid = -1;
        if(!TextUtils.isEmpty(curUserId) && unFavorters != null && unFavorters.size() > 0){
            for(FavortItem item : unFavorters){
                favortid = favortid + 1;
                if(curUserId.equals(item.getUserName())){
                    return favortid;
                }
            }
        }
        return -1;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FavortItem> getFavorters() {
        return favorters;
    }

    public void setFavorters(List<FavortItem> favorters) {
        this.favorters = favorters;
    }

    public List<FavortItem> getUnFavorters() {
        return unFavorters;
    }

    public void setUnFavorters(List<FavortItem> unFavorters) {
        this.unFavorters = unFavorters;
    }
}
