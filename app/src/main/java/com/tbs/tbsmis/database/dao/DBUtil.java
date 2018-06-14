package com.tbs.tbsmis.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.database.DataBaseUtil;
import com.tbs.tbsmis.database.table.WX_GROUP_TABLE;
import com.tbs.tbsmis.database.table.WX_MESSAGE_TABLE;
import com.tbs.tbsmis.database.table.WX_USER_TABLE;
import com.tbs.tbsmis.entity.WXGroupEntity;
import com.tbs.tbsmis.entity.WXTextEntity;
import com.tbs.tbsmis.entity.WXUserEntity;

import java.util.ArrayList;

//import com.tbs.chat.constants.Constants;

//һ��ҵ����
public class DBUtil {

	private static final String TAG = "DBUtil";
	private static DBUtil dao;
	private final Context context;

	private DBUtil(Context context) {
		this.context = context;
	}

	public static DBUtil getInstance(Context context) {
		if (DBUtil.dao == null) {
            DBUtil.dao = new DBUtil(context);
		}
		return DBUtil.dao;
	}

	public SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DataBaseUtil(this.context).getDataBase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}

	/**
	 * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据
	 */
	public synchronized void addOrUpdateUser(ArrayList<WXUserEntity> user,
			String dbName) {
		Cursor cursor = null;
		ContentValues values = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		try {
			for (int i = 0; i < user.size(); i++) {
				values = new ContentValues();
				values.put(WX_USER_TABLE.WX_ID, dbName);
				values.put(WX_USER_TABLE.OPEN_ID, user.get(i).getOpenid());
				values.put(WX_USER_TABLE.NICKNAME, user.get(i).getNickname());
				values.put(WX_USER_TABLE.CITY, user.get(i).getCity());
				values.put(WX_USER_TABLE.PROVINCE, user.get(i).getProvince());
				values.put(WX_USER_TABLE.COUNTRY, user.get(i).getCountry());
				values.put(WX_USER_TABLE.SEX, user.get(i).getSex());
				values.put(WX_USER_TABLE.LANGUAGE, user.get(i).getLanguage());
				values.put(WX_USER_TABLE.HEADIMGURL, user.get(i)
						.getHeadimgurl());
				values.put(WX_USER_TABLE.SUBSCRIBE_TIME, user.get(i)
						.getSubscribe_time());
				values.put(WX_USER_TABLE.REMARK, user.get(i).getRemark());
				values.put(WX_USER_TABLE.GROUPID, user.get(i).getGroupid());
				values.put(WX_USER_TABLE.SUBSCRIBE, user.get(i).getSubscribe());
				String sqlStr = "select * from " + WX_USER_TABLE.TABLE_NAME
						+ " where " + WX_USER_TABLE.OPEN_ID + " = '"
						+ user.get(i).getOpenid() + "';";
				cursor = database.rawQuery(sqlStr, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						String whereClause = WX_USER_TABLE.OPEN_ID + " = ?";
						String[] whereArgs = { user.get(i).getOpenid() };
						database.update(WX_USER_TABLE.TABLE_NAME, values,
								whereClause, whereArgs);
					} else {
						database.insert(WX_USER_TABLE.TABLE_NAME, null, values);
					}
				}
			}
			Intent intent = new Intent(constants.REFRESH_ADDRESS);
            this.context.sendBroadcast(intent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
	}

	/**
	 * 修改用户备注
	 * 
	 * @author yangzt
	 */
	public void modRemark(String openId, String remark) {
		ContentValues values = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		values = new ContentValues();
		values.put(WX_USER_TABLE.REMARK, remark);
		String whereClause = WX_USER_TABLE.OPEN_ID+"= ?";
		String[] whereArgs = { openId };
		try {
			database.update(WX_USER_TABLE.TABLE_NAME, values, whereClause, whereArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改用户备注
	 * 
	 * @author yangzt
	 */
	public void modGroupid(String openId, int groupid) {

		ContentValues values = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		values = new ContentValues();
		values.put(WX_USER_TABLE.GROUPID, groupid);
		String whereClause = WX_USER_TABLE.OPEN_ID+"= ?";
		String[] whereArgs = { openId };
		try {
			database.update(WX_USER_TABLE.TABLE_NAME, values, whereClause, whereArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 详细查询 通过id查询姓名
	 */
	public synchronized WXUserEntity queryName(String friend, String self) {
		Cursor cursor = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		WXUserEntity mfriend = new WXUserEntity();
		String sql = "select m.*,g." + WX_GROUP_TABLE.NAME + " from "
				+ WX_USER_TABLE.TABLE_NAME + " m left join "
				+ WX_GROUP_TABLE.TABLE_NAME + " g on m."
				+ WX_USER_TABLE.GROUPID + "=g." + WX_GROUP_TABLE.ID
				+ " where m." + WX_USER_TABLE.WX_ID + "='" + self + "' and m."
				+ WX_USER_TABLE.OPEN_ID + " ='" + friend + "';";
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				String openid = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.OPEN_ID));
				String nickname = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.NICKNAME));
				String city = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.CITY));
				String province = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.PROVINCE));
				String country = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.COUNTRY));
				String groupname = cursor.getString(cursor
						.getColumnIndex(WX_GROUP_TABLE.NAME));
				int sex = cursor.getInt(cursor
						.getColumnIndex(WX_USER_TABLE.SEX));
				String language = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.LANGUAGE));
				String headimgurl = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
				int subscribe_time = cursor.getInt(cursor
						.getColumnIndex(WX_USER_TABLE.SUBSCRIBE_TIME));
				String remark = cursor.getString(cursor
						.getColumnIndex(WX_USER_TABLE.REMARK));
				int groupid = cursor.getInt(cursor
						.getColumnIndex(WX_USER_TABLE.GROUPID));
				int subscribe = cursor.getInt(cursor
						.getColumnIndex(WX_USER_TABLE.SUBSCRIBE));
				mfriend.setOpenid(openid);
				mfriend.setNickname(nickname);
				mfriend.setCity(city);
				mfriend.setProvince(province);
				mfriend.setCountry(country);
				mfriend.setSex(sex);
				mfriend.setLanguage(language);
				mfriend.setHeadimgurl(headimgurl);
				mfriend.setSubscribe_time(subscribe_time);
				mfriend.setRemark(remark);
				mfriend.setGroupid(groupid);
				mfriend.setSubscribe(subscribe);
				mfriend.setGroupname(groupname);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return mfriend;
	}

	/**
	 * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据(微信公众号分组信息)
	 */
	public synchronized void addOrUpdateGruop(ArrayList<WXGroupEntity> group) {
		Cursor cursor = null;
		ContentValues values = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		try {
			for (int i = 0; i < group.size(); i++) {
				values = new ContentValues();
				values.put(WX_GROUP_TABLE.ID, group.get(i).getId());
				values.put(WX_GROUP_TABLE.NAME, group.get(i).getName());
				values.put(WX_GROUP_TABLE.COUNT, group.get(i).getCount());
				String sqlStr = "select * from " + WX_GROUP_TABLE.TABLE_NAME
						+ " where " + WX_GROUP_TABLE.ID + " = "
						+ group.get(i).getId() + ";";
				cursor = database.rawQuery(sqlStr, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						String whereClause = WX_GROUP_TABLE.ID + " = ?";
						String[] whereArgs = { group.get(i).getId() + "" };
						database.update(WX_GROUP_TABLE.TABLE_NAME, values,
								whereClause, whereArgs);
					} else {
						database.insert(WX_GROUP_TABLE.TABLE_NAME, null, values);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
	}

	/**
	 * 查询m分组信息
	 */
	public synchronized ArrayList<WXGroupEntity> getAllGroupInfo(Context context) {
		Cursor cursor = null;
		ArrayList<WXGroupEntity> groups = new ArrayList<WXGroupEntity>();
		SQLiteDatabase database = null;
		database = this.getConnection();
		String sql = "select * from " + WX_GROUP_TABLE.TABLE_NAME + ";";
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					WXGroupEntity group = new WXGroupEntity();
					String name = cursor.getString(cursor
							.getColumnIndex(WX_GROUP_TABLE.NAME));
					int id = cursor.getInt(cursor
							.getColumnIndex(WX_GROUP_TABLE.ID));
					int count = cursor.getInt(cursor
							.getColumnIndex(WX_GROUP_TABLE.COUNT));
					group.setCount(count);
					group.setId(id);
					group.setName(name);
					groups.add(group);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return groups;
	}

	/**
	 * 查询m分组信息
	 */
	public synchronized WXGroupEntity getGroupInfo(Context context, int groupid) {
		Cursor cursor = null;
		WXGroupEntity group = new WXGroupEntity();
		SQLiteDatabase database = null;
		database = this.getConnection();
		String sql = "select * from " + WX_GROUP_TABLE.TABLE_NAME + " where "
				+ WX_GROUP_TABLE.ID + "=" + groupid + ";";
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToNext()) {
					String name = cursor.getString(cursor
							.getColumnIndex(WX_GROUP_TABLE.NAME));
					int id = cursor.getInt(cursor
							.getColumnIndex(WX_GROUP_TABLE.ID));
					int count = cursor.getInt(cursor
							.getColumnIndex(WX_GROUP_TABLE.COUNT));
					group.setCount(count);
					group.setId(id);
					group.setName(name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return group;
	}

	/**
	 * 查询所有好友信息
	 */
	public synchronized ArrayList<WXUserEntity> getUsersInfo(Context context,
			String dbName) {
		Cursor cursor = null;
		WXUserEntity user = null;
		SQLiteDatabase database = null;
		ArrayList<WXUserEntity> array = new ArrayList<WXUserEntity>();
		database = this.getConnection();
		String sql = "select m.*,g." + WX_GROUP_TABLE.NAME + " from "
				+ WX_USER_TABLE.TABLE_NAME + " m left join "
				+ WX_GROUP_TABLE.TABLE_NAME + " g on m."
				+ WX_USER_TABLE.GROUPID + "=g." + WX_GROUP_TABLE.ID
				+ " where m." + WX_USER_TABLE.WX_ID + "='" + dbName + "';";
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					user = new WXUserEntity();
					String openid = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.OPEN_ID));
					String nickname = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.NICKNAME));
					String city = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.CITY));
					String province = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.PROVINCE));
					String country = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.COUNTRY));
					String groupname = cursor.getString(cursor
							.getColumnIndex(WX_GROUP_TABLE.NAME));
					int sex = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.SEX));
					String language = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.LANGUAGE));
					String headimgurl = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
					int subscribe_time = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.SUBSCRIBE_TIME));
					String remark = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.REMARK));
					int groupid = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.GROUPID));
					int subscribe = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.SUBSCRIBE));
					user.setOpenid(openid);
					user.setNickname(nickname);
					user.setCity(city);
					user.setProvince(province);
					user.setCountry(country);
					user.setSex(sex);
					user.setLanguage(language);
					user.setHeadimgurl(headimgurl);
					user.setSubscribe_time(subscribe_time);
					user.setRemark(remark);
					user.setGroupid(groupid);
					user.setSubscribe(subscribe);
					user.setGroupname(groupname);
					array.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return array;
	}

	/**
	 * 获取检索结果
	 */
	public synchronized ArrayList<WXUserEntity> getSearchUser(Context context,
			String value, String dbName) {
		Cursor cursor = null;
		WXUserEntity user = null;
		SQLiteDatabase database = null;
		ArrayList<WXUserEntity> UserList = new ArrayList<WXUserEntity>();
		database = this.getConnection();
		String sql = "select m.*,g." + WX_GROUP_TABLE.NAME + " from "
				+ WX_USER_TABLE.TABLE_NAME + " m left join "
				+ WX_GROUP_TABLE.TABLE_NAME + " g on m."
				+ WX_USER_TABLE.GROUPID + "=g." + WX_GROUP_TABLE.ID
				+ " where m." + WX_USER_TABLE.WX_ID + "='" + dbName
				+ "' and m." + WX_USER_TABLE.NICKNAME + " like '%" + value
				+ "%' or m." + WX_USER_TABLE.REMARK + " like '%" + value
				+ "%';";
		try {
			cursor = database.rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					user = new WXUserEntity();
					String openid = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.OPEN_ID));
					String nickname = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.NICKNAME));
					String city = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.CITY));
					String province = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.PROVINCE));
					String country = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.COUNTRY));
					String groupname = cursor.getString(cursor
							.getColumnIndex(WX_GROUP_TABLE.NAME));
					int sex = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.SEX));
					String language = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.LANGUAGE));
					String headimgurl = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
					int subscribe_time = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.SUBSCRIBE_TIME));
					String remark = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.REMARK));
					int groupid = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.GROUPID));
					int subscribe = cursor.getInt(cursor
							.getColumnIndex(WX_USER_TABLE.SUBSCRIBE));
					user.setGroupname(groupname);
					user.setOpenid(openid);
					user.setNickname(nickname);
					user.setCity(city);
					user.setProvince(province);
					user.setCountry(country);
					user.setSex(sex);
					user.setLanguage(language);
					user.setHeadimgurl(headimgurl);
					user.setSubscribe_time(subscribe_time);
					user.setRemark(remark);
					user.setGroupid(groupid);
					user.setSubscribe(subscribe);
					UserList.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return UserList;
	}

	/**
	 * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据
	 */
	public synchronized void addOrUpdateMessage(ArrayList<WXTextEntity> msg,
			String dbName) {
		Cursor cursor = null;
		ContentValues values = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		try {
			for (int i = 0; i < msg.size(); i++) {
				values = new ContentValues();
				values.put(WX_MESSAGE_TABLE.TOUSERNAME, dbName);
				values.put(WX_MESSAGE_TABLE.FROMUSERNAME, msg.get(i)
						.getFromUserName());
				values.put(WX_MESSAGE_TABLE.MSGTYPE, msg.get(i).getMsgType());
				values.put(WX_MESSAGE_TABLE.CONTENT, msg.get(i).getContent());
				values.put(WX_MESSAGE_TABLE.CREATETIME, msg.get(i)
						.getCreateTime());
				values.put(WX_MESSAGE_TABLE.MSGID, msg.get(i).getMsgId());
				values.put(WX_MESSAGE_TABLE.SENDSTATE, msg.get(i)
						.getSendState());
				values.put(WX_MESSAGE_TABLE.DIRECTION, msg.get(i)
						.getDirection());
				String sqlStr = "select * from " + WX_MESSAGE_TABLE.TABLE_NAME
						+ " where " + WX_MESSAGE_TABLE.MSGID + " = "
						+ msg.get(i).getMsgId() + ";";
				cursor = database.rawQuery(sqlStr, null);
				if (cursor != null) {
					if (cursor.getCount() > 0) {
						// String whereClause = WX_MESSAGE_TABLE.MSGID + " = ?";
						// String[] whereArgs = { group.get(i).getId() + "" };
						// database.update(WX_MESSAGE_TABLE.TABLE_NAME, values,
						// whereClause, whereArgs);
					} else {
						database.insert(WX_MESSAGE_TABLE.TABLE_NAME, null,
								values);
						Intent intent = new Intent(constants.REFRESH_MESSAGE);
                        this.context.sendBroadcast(intent);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
	}

	public void addOrUpdateMessage(WXTextEntity msg, String dbName) {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		ContentValues values = null;
		SQLiteDatabase database = null;
		database = this.getConnection();
		try {
			values = new ContentValues();
			values.put(WX_MESSAGE_TABLE.TOUSERNAME, dbName);
			values.put(WX_MESSAGE_TABLE.FROMUSERNAME, msg.getFromUserName());
			values.put(WX_MESSAGE_TABLE.MSGTYPE, msg.getMsgType());
			values.put(WX_MESSAGE_TABLE.CONTENT, msg.getContent());
			values.put(WX_MESSAGE_TABLE.CREATETIME, msg.getCreateTime());
			values.put(WX_MESSAGE_TABLE.MSGID, msg.getMsgId());
			values.put(WX_MESSAGE_TABLE.SENDSTATE, msg.getSendState());
			values.put(WX_MESSAGE_TABLE.DIRECTION, msg.getDirection());
			String sqlStr = "select * from " + WX_MESSAGE_TABLE.TABLE_NAME
					+ " where " + WX_MESSAGE_TABLE.MSGID + " = "
					+ msg.getMsgId() + ";";
			cursor = database.rawQuery(sqlStr, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					// String whereClause = WX_MESSAGE_TABLE.MSGID + " = ?";
					// String[] whereArgs = { group.get(i).getId() + "" };
					// database.update(WX_MESSAGE_TABLE.TABLE_NAME, values,
					// whereClause, whereArgs);
				} else {
					database.insert(WX_MESSAGE_TABLE.TABLE_NAME, null, values);
					Intent intent = new Intent(constants.REFRESH_MESSAGE);
                    this.context.sendBroadcast(intent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}

	}

	/**
	 * 查询信息
	 */
	public synchronized ArrayList<WXTextEntity> getMessage(Context context,
			String value) {
		ArrayList<WXTextEntity> list = new ArrayList<WXTextEntity>();
		WXTextEntity msg = null;
		SQLiteDatabase database = null;
		Cursor cursor = null;
		database = this.getConnection();
		String sqlStr = "select * from (select m.*,g." + WX_USER_TABLE.NICKNAME
				+ ",g." + WX_USER_TABLE.HEADIMGURL + " from "
				+ WX_MESSAGE_TABLE.TABLE_NAME + " m left join "
				+ WX_USER_TABLE.TABLE_NAME + " g on m."
				+ WX_MESSAGE_TABLE.FROMUSERNAME + "=g." + WX_USER_TABLE.OPEN_ID
				+ ") where " + WX_MESSAGE_TABLE.FROMUSERNAME + " = '" + value
				+ "';";
		try {
			cursor = database.rawQuery(sqlStr, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					msg = new WXTextEntity();
					String content = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CONTENT));
					String tousername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.TOUSERNAME));
					String fromusername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.FROMUSERNAME));
					String msgtype = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGTYPE));
					String nickname = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.NICKNAME));
					String headimgurl = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
					int direction = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.DIRECTION));
					int sendstate = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.SENDSTATE));
					int creattime = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CREATETIME));
					long msgid = cursor.getLong(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGID));
					msg.setToUserName(tousername);
					msg.setFromUserName(fromusername);
					msg.setNickname(nickname);
					msg.setHeadimgurl(headimgurl);
					msg.setMsgType(msgtype);
					msg.setContent(content);
					msg.setCreateTime(creattime);
					msg.setDirection(direction);
					msg.setSendState(sendstate);
					msg.setMsgId(msgid);
					list.add(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 查询聊天内容相关信息
	 */
	public synchronized ArrayList<WXTextEntity> getMessage2(Context context,
			String value) {
		ArrayList<WXTextEntity> list = new ArrayList<WXTextEntity>();
		WXTextEntity msg = null;
		SQLiteDatabase database = null;
		Cursor cursor = null;
		database = this.getConnection();
		String sqlStr = "select * from (select m.*,g." + WX_USER_TABLE.NICKNAME
				+ ",g." + WX_USER_TABLE.HEADIMGURL + " from "
				+ WX_MESSAGE_TABLE.TABLE_NAME + " m left join "
				+ WX_USER_TABLE.TABLE_NAME + " g on m."
				+ WX_MESSAGE_TABLE.FROMUSERNAME + "=g." + WX_USER_TABLE.OPEN_ID
				+ ") where " + WX_MESSAGE_TABLE.CONTENT + " like '%" + value
				+ "%';";
		try {
			cursor = database.rawQuery(sqlStr, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					msg = new WXTextEntity();
					String content = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CONTENT));
					String tousername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.TOUSERNAME));
					String fromusername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.FROMUSERNAME));
					String msgtype = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGTYPE));
					String nickname = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.NICKNAME));
					String headimgurl = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
					int direction = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.DIRECTION));
					int sendstate = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.SENDSTATE));
					int creattime = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CREATETIME));
					long msgid = cursor.getLong(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGID));
					msg.setToUserName(tousername);
					msg.setFromUserName(fromusername);
					msg.setNickname(nickname);
					msg.setHeadimgurl(headimgurl);
					msg.setMsgType(msgtype);
					msg.setContent(content);
					msg.setCreateTime(creattime);
					msg.setDirection(direction);
					msg.setSendState(sendstate);
					msg.setMsgId(msgid);
					list.add(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 查询指定人聊天内容相关信息
	 */
	public synchronized ArrayList<WXTextEntity> getMessage2(Context context,
			String value, String openid) {
		ArrayList<WXTextEntity> list = new ArrayList<WXTextEntity>();
		WXTextEntity msg = null;
		SQLiteDatabase database = null;
		Cursor cursor = null;
		database = this.getConnection();
		String sqlStr = "select * from (select m.*,g." + WX_USER_TABLE.NICKNAME
				+ ",g." + WX_USER_TABLE.HEADIMGURL + " from "
				+ WX_MESSAGE_TABLE.TABLE_NAME + " m left join "
				+ WX_USER_TABLE.TABLE_NAME + " g on m."
				+ WX_MESSAGE_TABLE.FROMUSERNAME + "=g." + WX_USER_TABLE.OPEN_ID
				+ ") where " + WX_MESSAGE_TABLE.CONTENT + " like '%" + value
				+ "%' and " + WX_MESSAGE_TABLE.FROMUSERNAME + "= '" + openid
				+ "';";
		try {
			cursor = database.rawQuery(sqlStr, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					msg = new WXTextEntity();
					String content = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CONTENT));
					String tousername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.TOUSERNAME));
					String fromusername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.FROMUSERNAME));
					String msgtype = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGTYPE));
					String nickname = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.NICKNAME));
					String headimgurl = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
					int direction = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.DIRECTION));
					int sendstate = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.SENDSTATE));
					int creattime = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CREATETIME));
					long msgid = cursor.getLong(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGID));
					msg.setToUserName(tousername);
					msg.setFromUserName(fromusername);
					msg.setNickname(nickname);
					msg.setHeadimgurl(headimgurl);
					msg.setMsgType(msgtype);
					msg.setContent(content);
					msg.setCreateTime(creattime);
					msg.setDirection(direction);
					msg.setSendState(sendstate);
					msg.setMsgId(msgid);
					list.add(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 删除发给某人的消息
	 * 
	 * @param receiveId
	 */
	public void deleteMessages(String receiveId) {
		SQLiteDatabase database = null;
		database = this.getConnection();
		try {
			database.delete(WX_MESSAGE_TABLE.TABLE_NAME,
					WX_MESSAGE_TABLE.FROMUSERNAME + "=?",
					new String[] { receiveId });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询信息(用户对话个数)
	 */
	public synchronized ArrayList<WXTextEntity> getMessage1(Context context,
			String userID) {
		Cursor cursor = null;
		WXTextEntity msg = null;
		SQLiteDatabase database = null;
		ArrayList<WXTextEntity> array = new ArrayList<WXTextEntity>();

		database = this.getConnection();

		String sql = "select * from " + "(select m.*,g."
				+ WX_USER_TABLE.NICKNAME + ",g." + WX_USER_TABLE.HEADIMGURL
				+ " from " + WX_MESSAGE_TABLE.TABLE_NAME + " m left join "
				+ WX_USER_TABLE.TABLE_NAME + " g on m."
				+ WX_MESSAGE_TABLE.FROMUSERNAME + "=g." + WX_USER_TABLE.OPEN_ID
				+ ") where _id in(select max(_id) from "
				+ WX_MESSAGE_TABLE.TABLE_NAME + " where "
				+ WX_MESSAGE_TABLE.TOUSERNAME + " = '" + userID + "' group by "
				+ WX_MESSAGE_TABLE.FROMUSERNAME + ");";

		try {
			cursor = new DataBaseUtil(context).getDataBase()
					.rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					msg = new WXTextEntity();
					String content = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CONTENT));
					String tousername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.TOUSERNAME));
					String fromusername = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.FROMUSERNAME));
					String msgtype = cursor.getString(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGTYPE));
					String nickname = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.NICKNAME));
					String headimgurl = cursor.getString(cursor
							.getColumnIndex(WX_USER_TABLE.HEADIMGURL));
					int direction = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.DIRECTION));
					int sendstate = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.SENDSTATE));
					int creattime = cursor.getInt(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.CREATETIME));
					long msgid = cursor.getLong(cursor
							.getColumnIndex(WX_MESSAGE_TABLE.MSGID));
					msg.setNickname(nickname);
					msg.setHeadimgurl(headimgurl);
					msg.setToUserName(tousername);
					msg.setFromUserName(fromusername);
					msg.setMsgType(msgtype);
					msg.setContent(content);
					msg.setCreateTime(creattime);
					msg.setDirection(direction);
					msg.setSendState(sendstate);
					msg.setMsgId(msgid);
					array.add(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != database) {
				database.close();
			}
			if (null != cursor) {
				cursor.close();
			}
		}
		return array;
	}
}