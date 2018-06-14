package com.tbs.tbsmis.city;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CityDB {
	public static final String CITY_DB_NAME = "city.db";
	private static final String CITY_TABLE_NAME = "city";
	private final SQLiteDatabase db;

	public CityDB(Context context, String path) {
        this.db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
	}

	public List<String> getAllCity(String Province) {
		List<String> list = new ArrayList<String>();
		Cursor c = this.db.rawQuery("SELECT city from " + CityDB.CITY_TABLE_NAME
				+ " where province=? order by city ", new String[] { Province });
		while (c.moveToNext()) {
			String city = c.getString(c.getColumnIndex("city"));
			list.add(city);
		}
		return list;
	}
	
	public List<String> getAllProvince() {
		List<String> list = new ArrayList<String>();
		Cursor c = this.db.rawQuery("select province from " + CityDB.CITY_TABLE_NAME + " group by province order by province",null);
				//"SELECT * from " + CITY_TABLE_NAME, null);
		while (c.moveToNext()) {
			String province = c.getString(c.getColumnIndex("province"));
			list.add(province);
		}
		return list;
	}
}
