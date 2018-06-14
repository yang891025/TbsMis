package com.tbs.tbsmis.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.tbs.tbsmis.database.DataBaseUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DataBaseExcute {

	private static final String TAG = "DataBaseExcute";
	public Map<String, DataBaseExcute.FieldInfo> columnsMap = new HashMap<String, DataBaseExcute.FieldInfo>();
	private String m_tableName;
	Context con;

	public class FieldInfo {
		String fieldName;
		String aliaName;
		boolean isNumberType;
	}

	public DataBaseExcute(Context c) {
        con = c;
	}

	// 设定当前表并初始化字段信息
	boolean useTable(String tableName) {
        this.m_tableName = tableName;
		return this.initTableinfo();
	}

	// 获取当前表
	String getTableName() {
		return this.m_tableName;
	}

	// 判定字段类型是否数字类型,SQL语句中字符串类型值需要家单引号.
	public boolean isNumberType(int dataType) {
		switch (dataType) {
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.NUMERIC:
		case Types.REAL:
		case Types.ROWID:
		case Types.SMALLINT:
		case Types.TINYINT:
			return true;
		default:
			return false;
		}
	}

	// 设定字段别名
	public boolean SetAlias(String fieldName, String aliasName) {
		DataBaseExcute.FieldInfo info = this.columnsMap.get(fieldName);
		if (info == null) {
			return false;
		} else {
			info.aliaName = aliasName;
            this.columnsMap.put(fieldName, info);
			return true;
		}
	}

	// 初始化表信息
	@SuppressLint("NewApi")
	private boolean initTableinfo() {
		try {
            this.columnsMap.clear();
			Cursor c = new DataBaseUtil(this.con).getDataBase().query(this.m_tableName, null, null, null, null, null, null);
			String columnNmae[] = c.getColumnNames();
			for (int i = 1; i < columnNmae.length; i++) {
				DataBaseExcute.FieldInfo field = new DataBaseExcute.FieldInfo();
				field.fieldName = columnNmae[i];
				//	int dataType = c.getType(i);
				//	Log.d(TAG, "dataType = " + dataType);
				//	field.isNumberType = isNumberType(dataType);
				//	Log.d(TAG, "isNumberType = " + field.isNumberType);
                this.columnsMap.put(field.fieldName, field);
			}
			c.close();
			return true;
			
		} catch (Exception e) {
			Log.e(DataBaseExcute.TAG, "Error Trace in initTableinfo() : " + e.getMessage());
		}
		return false;
	}

	
}
