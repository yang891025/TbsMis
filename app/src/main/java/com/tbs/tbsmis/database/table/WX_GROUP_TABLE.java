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

public interface WX_GROUP_TABLE extends BaseColumns {

	String TABLE_NAME = "WXGroupTable";
	String ID = "id";
	String NAME = "name";
	String COUNT = "count";

	String CREATE_TABLE = "create table " + WX_GROUP_TABLE.TABLE_NAME + "("
			+ BaseColumns._ID + " integer primary key autoincrement," + WX_GROUP_TABLE.ID + " integer,"
			+ WX_GROUP_TABLE.NAME + " varchar(50)," + WX_GROUP_TABLE.COUNT + " integer" + ")";
}
