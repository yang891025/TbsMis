package com.tbs.circle.bean;

/**
 * Created by tbs on 16/3/2.
 */
public class CommentConfig {
    public enum Type{
        PUBLIC("public"), REPLY("reply");

        private String value;
        Type(String value){
            this.value = value;
        }

    }

    public int circlePosition;
    public String circleId;
    public int commentPosition;
    public int commentId;
    public Type commentType;
    public String replyUser;
    public String path;
    public String replyName;
    @Override
    public String toString() {
        String replyUserStr = "";
        if(replyUser != null){
            replyUserStr = replyUser.toString();
        }
        return "circlePosition = " + circlePosition
                + "; commentPosition = " + commentPosition
                + "; commentType ＝ " + commentType
                + "; replyUser = " + replyUserStr;
    }
}
