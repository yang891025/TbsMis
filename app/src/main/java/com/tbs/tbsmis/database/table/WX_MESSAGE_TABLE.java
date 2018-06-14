/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author 任雪涛
 * @version 2012-12-21
 * @time 下午12:21:00
 * @function 
 */
package com.tbs.tbsmis.database.table;

import android.provider.BaseColumns;

public interface WX_MESSAGE_TABLE extends BaseColumns {

	String TABLE_NAME = "WXTextTable";// 表名
	String TOUSERNAME = "ToUserName";// 登录人的id
	String FROMUSERNAME = "FromUserName";// 好友id
	String CREATETIME = "CreateTime";// 时间
	String MSGTYPE = "MsgType";// 类型
	String CONTENT = "Content";// 内容
	String MSGID = "MsgId";// 收发时间
	String DIRECTION = "Direction";// 方向
	String SENDSTATE = "SendState";// 收发时间

	String CREATE_TABLE = "create table " + WX_MESSAGE_TABLE.TABLE_NAME + "(" + BaseColumns._ID
			+ " integer primary key autoincrement," + WX_MESSAGE_TABLE.TOUSERNAME
			+ " varchar(50)," + WX_MESSAGE_TABLE.FROMUSERNAME + " varchar(50)," + WX_MESSAGE_TABLE.CREATETIME
			+ " integer," + WX_MESSAGE_TABLE.MSGTYPE + " varchar(10)," + WX_MESSAGE_TABLE.CONTENT + " text,"
			+ WX_MESSAGE_TABLE.MSGID + " bigint," + WX_MESSAGE_TABLE.SENDSTATE + " integer," + WX_MESSAGE_TABLE.DIRECTION
			+ " integer" + ")";
}
