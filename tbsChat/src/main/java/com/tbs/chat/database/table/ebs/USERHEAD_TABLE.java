/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author 任雪涛
 * @version 2012-12-21
 * @time 下午12:21:00
 * @function 
 */
package com.tbs.chat.database.table.ebs;

import android.provider.BaseColumns;

public interface USERHEAD_TABLE extends BaseColumns {

	String TABLE_NAME = "UserHeadTable";// 表名
	String USERCODE = "userCode";// 用户外部账号
	String FILENAME = "fileName";// 头像文件名称
	String SAVEPATH = "savePath";// 头像路径
	String FILESIZE = "fileSize";// 文件大小
	String MODIFYTIME = "modifyTime";// 头像更新时间
	String HTTPURL = "httpUrl";// 头像更新时间

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + USERCODE + " text,"
			+ FILENAME + " text," + SAVEPATH + " text," + FILESIZE + " long,"
			+ MODIFYTIME + " text," + HTTPURL + " text" + ")";
}
