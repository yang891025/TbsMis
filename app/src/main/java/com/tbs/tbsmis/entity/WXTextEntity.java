package com.tbs.tbsmis.entity;

import java.io.Serializable;

public class WXTextEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String ToUserName;
	private String FromUserName;
	private String MsgType;
	private String Content;
	private int CreateTime;
	private long MsgId;
	private int Direction;
	private int SendState;
	private String nickname;
	private String headimgurl;

	public WXTextEntity() {

	}

	public String getToUserName() {
		return this.ToUserName;
	}

	public void setToUserName(String toUserName) {
        this.ToUserName = toUserName;
	}

	public long getMsgId() {
		return this.MsgId;
	}

	public void setMsgId(long msgId) {
        this.MsgId = msgId;
	}

	public int getCreateTime() {
		return this.CreateTime;
	}

	public void setCreateTime(int createTime) {
        this.CreateTime = createTime;
	}

	public String getFromUserName() {
		return this.FromUserName;
	}

	public void setFromUserName(String fromUserName) {
        this.FromUserName = fromUserName;
	}

	public String getMsgType() {
		return this.MsgType;
	}

	public void setMsgType(String msgType) {
        this.MsgType = msgType;
	}

	public String getContent() {
		return this.Content;
	}

	public void setContent(String content) {
        this.Content = content;
	}

	public int getSendState() {
		return this.SendState;
	}

	public void setSendState(int sendState) {
        this.SendState = sendState;
	}

	public int getDirection() {
		return this.Direction;
	}

	public void setDirection(int direction) {
        this.Direction = direction;
	}

	public String getNickname() {
		return this.nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return this.headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

}
