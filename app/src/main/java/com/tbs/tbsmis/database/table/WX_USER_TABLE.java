/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author yangzt
 * @version 2015-7-3
 * @time 上午09:58:10
 * @function 
 */
package com.tbs.tbsmis.database.table;

import android.provider.BaseColumns;

public interface WX_USER_TABLE extends BaseColumns {

	String TABLE_NAME = "WXUserTable";// 表名
	String OPEN_ID = "openid";// 用户账号
	String WX_ID = "wxid";// 用户账号
	String USERNAME = "username";// 用户号
	String NICKNAME = "nickname";// 昵称
	String REMARKNAME = "remarkname";// 备注名
	String CITY = "city";// 城市
	String PROVINCE = "province";// 省份
	String COUNTRY = "country";// 国家名称
	String SEX = "sex";// 性别
	String LANGUAGE = "language";// 语言
	String HEADIMGURL = "headimgurl";// 头像链接
	String SUBSCRIBE_TIME = "subscribe_time";// 订阅时间
	String REMARK = "remark";// 星标
	String GROUPID = "groupid";// 分组id
	String SUBSCRIBE = "subscribe";// 是否订阅

	String CREATE_TABLE = "create table " + WX_USER_TABLE.TABLE_NAME + "(" + BaseColumns._ID
			+ " integer primary key autoincrement," + WX_USER_TABLE.WX_ID + " varchar(50),"
			+ WX_USER_TABLE.OPEN_ID + " varchar(50)," + WX_USER_TABLE.USERNAME + " varchar(50)," + WX_USER_TABLE.NICKNAME
			+ " varchar(50)," + WX_USER_TABLE.REMARKNAME + " varchar(50)," + WX_USER_TABLE.CITY
			+ " varchar(50)," + WX_USER_TABLE.PROVINCE + " varchar(50)," + WX_USER_TABLE.COUNTRY
			+ " varchar(50)," + WX_USER_TABLE.SEX + " integer," + WX_USER_TABLE.LANGUAGE + " varchar(50),"
			+ WX_USER_TABLE.HEADIMGURL + " varchar(300)," + WX_USER_TABLE.SUBSCRIBE_TIME + " integer,"
			+ WX_USER_TABLE.REMARK + " varchar(50)," + WX_USER_TABLE.GROUPID + " integer," + WX_USER_TABLE.SUBSCRIBE
			+ " integer" + ")";
}
