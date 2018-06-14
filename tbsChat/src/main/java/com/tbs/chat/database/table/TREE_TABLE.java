/**
 * Copyright (c) 1994-2012 北京金信桥信息技术有限公司
 * Field.java
 * @author liyimig
 * @version 2012-5-3
 * @time 上午09:58:10
 * @function 
 */
package com.tbs.chat.database.table;

import android.provider.BaseColumns;

public interface TREE_TABLE extends BaseColumns {

	String TABLE_NAME = "tree";
	String COLUMN_LABEL = "label";
	String COLUMN_PARENT = "parent";
	String COLUMN_FLAG = "flag";

	String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + COLUMN_LABEL + " text,"
			+ COLUMN_PARENT + " text," + COLUMN_FLAG + " text" + ")";
}
