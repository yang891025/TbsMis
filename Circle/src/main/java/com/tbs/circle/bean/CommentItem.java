package com.tbs.circle.bean;

import java.io.Serializable;
/**
 * 
* @ClassName: CommentItem 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author yiw
* @date 2015-12-28 下午3:44:38 
*
 */
public class CommentItem implements Serializable{

	private int id;

    private String Cid;
	private String user;
	private String toReplyUser;
    private String Unickname;
	private String content;
    private String path;
    public CommentItem() {
    }

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getToReplyUser() {
		return toReplyUser;
	}
	public void setToReplyUser(String toReplyUser) {
		this.toReplyUser = toReplyUser;
	}

    public String getUnickname() {
        return Unickname;
    }

    public void setUnickname(String unickname) {
        Unickname = unickname;
    }

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
