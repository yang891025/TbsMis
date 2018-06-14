/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author 任雪涛
 * @version 2012-12-21
 * @time 下午12:21:00
 * @function 
 */
package com.tbs.chat.database.table;

import android.provider.BaseColumns;

public interface MESSAGE_TABLE extends BaseColumns {

	String TABLE_NAME = "MessageTable";// 表名
	String SELF_ID = "self_id";// 登录人的id
	String FRIEND_ID = "friend_id";// 好友id
	String DIRECTION = "direction";// 方向
	String TYPE = "type";// 类型
	String CONTENT = "content";// 内容
	String TIME = "time";// 收发时间
	String READ_TYPE = "read_type";// 收发时间
	String DATA1 = "data1";// 其他资源
	String DATA2 = "data2";// 其他资源
	String DATA3 = "data3";// 其他资源
	String DATA4 = "data4";// 其他资源
	String DATA5 = "data5";// 其他资源

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + SELF_ID + " text,"
			+ FRIEND_ID + " text," + DIRECTION + " text," + TYPE + " text,"
			+ CONTENT + " text," + TIME + " text," + READ_TYPE + " text," + DATA1 + " text," + DATA2
			+ " text," + DATA3 + " text," + DATA4 + " text," + DATA5 + " text"
			+ ")";
}
