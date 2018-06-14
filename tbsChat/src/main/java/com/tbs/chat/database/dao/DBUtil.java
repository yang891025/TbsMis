package com.tbs.chat.database.dao;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.DataBaseUtil;
import com.tbs.chat.database.table.COUNTEYCODE_TABLE;
import com.tbs.chat.database.table.FRIENDS_TABLE;
import com.tbs.chat.database.table.LOGINSTATE_TABLE;
import com.tbs.chat.database.table.LOGIN_TABLE;
import com.tbs.chat.database.table.MESSAGE_TABLE;
import com.tbs.chat.database.table.TREE_TABLE;
import com.tbs.chat.database.table.USER_TABLE;
import com.tbs.chat.database.table.ebs.USERHEAD_TABLE;
import com.tbs.chat.entity.CountryEntity;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.LoginEntity;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.entity.TreeEntity;
import com.tbs.chat.entity.UserEntity;
import com.tbs.chat.entity.ebs.UserEbsEntity;
import com.tbs.chat.entity.ebs.UserHeadEbsEntity;
import com.tbs.chat.util.LogUtil;
import com.tbs.chat.util.TimeUtil;
import com.tbs.chat.util.Util;
import com.tbs.ini.IniFile;

import java.util.ArrayList;
import java.util.List;

//一个业务类
public class DBUtil
{

    private static final String TAG = "DBUtil";
    private static DBUtil dao = null;
    private Context context;

    private DBUtil(Context context) {
        this.context = context;
    }

    public static DBUtil getInstance(Context context) {
        if (dao == null) {
            dao = new DBUtil(context);
        }
        return dao;
    }

    public SQLiteDatabase getConnection() {
        SQLiteDatabase sqliteDatabase = null;
        try {
            sqliteDatabase = new DataBaseUtil(context).getDataBase();
        } catch (Exception e) {
        }
        return sqliteDatabase;
    }

    /**
     * 查询用户账号密码
     */
    public UserEntity getUser(String userID) {

        Cursor cursor = null;
        UserEntity user = null;
        SQLiteDatabase database = null;

        database = getConnection();
        try {
            String selection = "self_id = ?";
            String[] selectionArgs = {userID};
            cursor = database.query(USER_TABLE.TABLE_NAME, null, selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    user = new UserEntity();
                    user.setUserID(cursor.getString(cursor
                            .getColumnIndex("self_id")));
                    user.setPassword(cursor.getString(cursor
                            .getColumnIndex("psw")));
                    user.setNickName(cursor.getString(cursor
                            .getColumnIndex("nick_Name")));
                    user.setHead(cursor.getString(cursor.getColumnIndex("head")));
                    user.setModifyTime(cursor.getString(cursor
                            .getColumnIndex("modify_time")));
                    user.setPhone(cursor.getString(cursor
                            .getColumnIndex("phone")));
                    user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
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
        return user;
    }

    /**
     * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据
     */
    public synchronized void addOrUpdateUser(String selfID, String password,
                                             String modifyTime, String nickName, String head, String phone,
                                             String countryCode, String dir, String userNum) {
        Cursor cursor = null;
        ContentValues values = null;
        SQLiteDatabase database = null;

        database = getConnection();

        values = new ContentValues();
        values.put(USER_TABLE.SELF_ID, selfID);
        values.put(USER_TABLE.PASSWORD, password);
        values.put(USER_TABLE.MODIFY_TIME, modifyTime);
        values.put(USER_TABLE.COUNTRY_CODE, countryCode);
        values.put(USER_TABLE.PHONE, phone);
        values.put(USER_TABLE.HEAD, head);
        values.put(USER_TABLE.NICK, nickName);
        values.put(USER_TABLE.TIME, TimeUtil.getdate());
        values.put(USER_TABLE.DATA2, dir);
        values.put(USER_TABLE.USERNUM, userNum);

        try {
            String selection = "self_id = ?";
            String[] selectionArgs = {selfID};
            cursor = database.query(USER_TABLE.TABLE_NAME, null, selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    String whereClause = "self_id = ?";
                    String[] whereArgs = {selfID};
                    database.update(USER_TABLE.TABLE_NAME, values, whereClause,
                            whereArgs);
                } else {
                    database.insert(USER_TABLE.TABLE_NAME, null, values);
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
     * 查询用户账号密码
     */
    public LoginEntity getLoginUser() {

        Cursor cursor = null;
        LoginEntity loginUser = null;
        SQLiteDatabase database = null;

        database = getConnection();
        try {
            cursor = database.query(LOGIN_TABLE.TABLE_NAME, null, null, null,
                    null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    loginUser = new LoginEntity();
                    loginUser.setUserID(cursor.getString(cursor
                            .getColumnIndex("userID")));
                    loginUser.setPassword(cursor.getString(cursor
                            .getColumnIndex("password")));
                    loginUser.setLoginID(cursor.getString(cursor
                            .getColumnIndex("loginID")));
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
        return loginUser;
    }

    /**
     * 查询数据库数据，如果存在数据则更新数据 如果没有数据就插入数据
     */
    public synchronized void addOrUpdateLoginUser(String loginUserID,
                                                  String password, String userID) {
        Cursor cursor = null;
        ContentValues values = null;
        SQLiteDatabase database = null;

        database = getConnection();

        values = new ContentValues();
        values.put(LOGIN_TABLE.LOGINID, loginUserID);
        values.put(LOGIN_TABLE.PASSWORD, password);
        values.put(LOGIN_TABLE.USERID, userID);

        try {
            String selection = "userID = ?";
            String[] selectionArgs = {userID};
            cursor = database.query(LOGIN_TABLE.TABLE_NAME, null, selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    String whereClause = "_id = ?";
                    String[] whereArgs = {"" + 1};
                    database.update(LOGIN_TABLE.TABLE_NAME, values,
                            whereClause, whereArgs);
                } else {
                    database.insert(LOGIN_TABLE.TABLE_NAME, null, values);
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
    public synchronized ArrayList<MessageEntity> getMessage(Context context,
                                                            String userID) {
        Cursor cursor = null;
        MessageEntity msg = null;
        SQLiteDatabase database = null;
        ArrayList<MessageEntity> array = new ArrayList<MessageEntity>();

        database = getConnection();

        String sql = "select * from MessageTable where _id in(select max(_id) from MessageTable where self_id = '"
                + userID + "' group by friend_id)";
        try {
            cursor = new DataBaseUtil(context).getDataBase()
                    .rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msg = new MessageEntity();

                    String content = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.CONTENT));
                    String time = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.TIME));
                    String friendID = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.FRIEND_ID));
                    String selfID = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.SELF_ID));
                    int readType = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.READ_TYPE));
                    int type = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.TYPE));
                    int direction = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.DIRECTION));
                    String data5 = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.DATA5));

                    msg.setContent(content);
                    msg.setDirection(direction);
                    msg.setFriend(friendID);
                    msg.setRead_type(readType);
                    msg.setSelf(selfID);
                    msg.setTime(time);
                    msg.setType(type);
                    msg.setData5(data5);
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


    /**
     * 查询信息
     */
    public synchronized List<MessageEntity> getMessage2(Context context,
                                                        String value) {
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        MessageEntity msg = null;
        SQLiteDatabase database = null;
        Cursor cursor = null;

        database = getConnection();

        String selection = MESSAGE_TABLE.FRIEND_ID + " = ?";
        String[] selectionArgs = {"" + value};
        try {
            cursor = database.query(MESSAGE_TABLE.TABLE_NAME, null, selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {

                    msg = new MessageEntity();
                    String content = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.CONTENT));
                    String time = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.TIME));
                    int read_type = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.READ_TYPE));
                    int direction = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.DIRECTION));
                    String friend = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.FRIEND_ID));
                    String self = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.SELF_ID));
                    int type = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.TYPE));
                    String data5 = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.DATA5));

                    msg.setDirection(direction);
                    msg.setRead_type(read_type);
                    msg.setContent(content);
                    msg.setFriend(friend);
                    msg.setSelf(self);
                    msg.setTime(time);
                    msg.setType(type);
                    msg.setData5(data5);
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
     * 查询信息 详细查询根据传入的值查询
     */
    public synchronized ArrayList<MessageEntity> getMessage(Context context,
                                                            String value, String userID) {
        Cursor cursor = null;
        MessageEntity msg = null;
        SQLiteDatabase database = null;
        ArrayList<MessageEntity> array = new ArrayList<MessageEntity>();

        database = getConnection();
        String sql = "select mt.* from (select m.* from MessageTable m inner join FriendsTable f on m.friend_id =  f" +
                ".friend_id where f.friend_id like '%"
                + value
                + "%' or m.content like '%"
                + value
                + "%' or f.nick_Name like '%"
                + value
                + "%' or m.friend_id like '%"
                + value
                + "%' and self_id = "
                + userID
                + ") mt where mt._id in (select max(mt1.m_id) from (select m._id as m_id,f._id as f_id,f.*,m.* from " +
                "MessageTable m inner join FriendsTable f on m.friend_id=  f.friend_id where f.friend_id like '%"
                + value
                + "%' or m.content like '%"
                + value
                + "%' or f.nick_Name like '%"
                + value
                + "%' or m.friend_id like '%"
                + value
                + "%' and self_id = "
                + userID + ") mt1 group by mt1.f_id)";
        try {
            cursor = new DataBaseUtil(context).getDataBase()
                    .rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msg = new MessageEntity();

                    String content = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.CONTENT));
                    String time = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.TIME));
                    String friendID = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.FRIEND_ID));
                    String selfID = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.SELF_ID));
                    int readType = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.READ_TYPE));
                    int type = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.TYPE));
                    int direction = cursor.getInt(cursor
                            .getColumnIndex(MESSAGE_TABLE.DIRECTION));
                    String data5 = cursor.getString(cursor
                            .getColumnIndex(MESSAGE_TABLE.DATA5));

                    msg.setContent(content);
                    msg.setDirection(direction);
                    msg.setFriend(friendID);
                    msg.setRead_type(readType);
                    msg.setSelf(selfID);
                    msg.setTime(time);
                    msg.setType(type);
                    msg.setData5(data5);
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

    /**
     * 查询自己的指定好友
     */
    public synchronized UserEntity detailUser(Context context, String userID,
                                              String password) {

        UserEntity user = null;
        Cursor cursor = null;
        SQLiteDatabase database = null;

        database = getConnection();
        String sql = "select * from UserTable e where self_id = '" + userID
                + "' or psw = '" + password + "'";
        try {
            cursor = database.rawQuery(sql, null);
            // 如果游标为空（查找失败）或查到的信息数位0，返回null
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    user = new UserEntity();
                    user.setUserID(cursor.getString(cursor
                            .getColumnIndex("self_id")));
                    user.setNickName(cursor.getString(cursor
                            .getColumnIndex("nick_Name")));
                    user.setHead(cursor.getString(cursor.getColumnIndex("head")));
                    user.setModifyTime(cursor.getString(cursor
                            .getColumnIndex("modify_time")));
                    user.setType(cursor.getString(cursor.getColumnIndex("type")));
                    user.setContent(cursor.getString(cursor
                            .getColumnIndex("content")));
                    user.setTime(cursor.getString(cursor.getColumnIndex("time")));
                    user.setPhone(cursor.getString(cursor
                            .getColumnIndex("phone")));
                    user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                    user.setData1(cursor.getString(cursor
                            .getColumnIndex("data1")));
                    user.setData2(cursor.getString(cursor
                            .getColumnIndex("data2")));
                    user.setData3(cursor.getString(cursor
                            .getColumnIndex("data3")));
                    user.setData4(cursor.getString(cursor
                            .getColumnIndex("data4")));
                    user.setData5(cursor.getString(cursor
                            .getColumnIndex("data5")));
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
        return user;
    }

    /**
     * 查询用户自己的信息
     */
    public synchronized UserEntity queryUser(Context context, String tableName,
                                             String selfId) {
        String webRoot = Util.getShareperference(context,
                Constants.SAVE_INFORMATION, "Path", Constants.CONFIG_INI_URL);
        String appTestFile = webRoot;
        IniFile IniFile = new IniFile();
        if (!webRoot.endsWith(".ini")) {
            if (webRoot.endsWith("/") == false) {
                webRoot += "/";
            }
            String WebIniFile = webRoot + Constants.WEB_CONFIG_FILE_NAME;
            appTestFile = webRoot
                    + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    Constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        }
        String userIni = appTestFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        UserEntity user = null;
        user = new UserEntity();
        user.setUserID(IniFile.getIniString(userIni, "Login", "Account",
                "", (byte) 0));
        user.setPassword(IniFile.getIniString(userIni, "Login", "PassWord",
                "", (byte) 0));
        user.setCountryCode("");
        user.setHead("");
        user.setTime(TimeUtil.getdate());
        user.setContent(IniFile.getIniString(userIni, "Login", "Signature",
                "", (byte) 0));
        user.setPhone(IniFile.getIniString(userIni, "Login", "Mobile", "",
                (byte) 0));
        user.setSex(IniFile.getIniString(userIni, "Login", "Sex", "",
                (byte) 0));
        user.setNickName(IniFile.getIniString(userIni, "Login", "NickName",
                "", (byte) 0));

        return user;
        // UserEntity user = null;
        // Cursor cursor = null;
        // SQLiteDatabase database = null;
        //
        // database = getConnection();
        //
        // String selection = "self_id = ?";
        // String[] selectionArgs = { selfId };
        //
        // try {
        // cursor = database.query(tableName, null, selection, selectionArgs,
        // null, null, null);
        // if (cursor != null) {
        // if (cursor.moveToFirst()) {
        // user = new UserEntity();
        // if (!cursor.isAfterLast()) {
        // user.setUserID(cursor.getString(cursor
        // .getColumnIndex("self_id")));
        // user.setNickName(cursor.getString(cursor
        // .getColumnIndex("nick_Name")));
        // user.setHead(cursor.getString(cursor
        // .getColumnIndex("head")));
        // user.setModifyTime(cursor.getString(cursor
        // .getColumnIndex("modify_time")));
        // }
        // }
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // } finally {
        // if (null != database) {
        // database.close();
        // }
        // if (null != cursor) {
        // cursor.close();
        // }
        // }
        // return user;
    }

    /**
     * 查询用户自己的好友信息
     */
    public synchronized FriendEntity queryUser(Context context,
                                               String tableName, String selfID, String friendID) {
        FriendEntity user = null;
        Cursor cursor = null;
        SQLiteDatabase database = null;

        database = getConnection();

        String selection = "self_id = ? and friend_id = ?";
        String[] selectionArgs = {selfID, friendID};

        try {
            cursor = database.query(tableName, null, selection, selectionArgs,
                    null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    user = new FriendEntity();
                    if (!cursor.isAfterLast()) {
                        user.setUserID(cursor.getString(cursor
                                .getColumnIndex("self_id")));
                        user.setFriendID(cursor.getString(cursor
                                .getColumnIndex("friend_id")));
                        user.setNickName(cursor.getString(cursor
                                .getColumnIndex("nick_Name")));
                        user.setHead(cursor.getString(cursor
                                .getColumnIndex("head")));
                        user.setModifyTime(cursor.getString(cursor
                                .getColumnIndex("modify_time")));
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
        return user;
    }

    /**
     * 更新好友头像
     *
     * @param friend
     */
    public synchronized void updateFirendHead(FriendEntity friend) {
        SQLiteDatabase database = null;
        database = getConnection();

        ContentValues values = new ContentValues();
        values.put("head", friend.getHead());
        values.put("modify_time", friend.getModifyTime());
        values.put("sex", friend.getSex());
        values.put("data2", friend.getData2());
        values.put("phone", friend.getPhone());
        values.put("country_code", friend.getCountryCode());
        values.put("nick_Name", friend.getNickName());

        String where = "self_id = " + friend.getUserID() + " and friend_id = "
                + friend.getFriendID();
        try {
            int number = database.update(FRIENDS_TABLE.TABLE_NAME, values,
                    where, null);
            LogUtil.record("-----------------------------------------------------------");
            LogUtil.record("****************** updateFirendHead number:"
                    + number + " *****************");
            LogUtil.record("-----------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 更新好友头像
     *
     * @param friend
     */
    public synchronized void updateFirendInfo(FriendEntity friend) {
        SQLiteDatabase database = null;
        database = getConnection();

        ContentValues values = new ContentValues();
        values.put("sex", friend.getSex());
        values.put("data2", friend.getData2());
        values.put("phone", friend.getPhone());
        values.put("country_code", friend.getCountryCode());
        values.put("nick_Name", friend.getNickName());

        String whereClause = "self_id = ? and friend_id = ?";
        String[] whereArgs = {friend.getUserID(), friend.getFriendID()};
        try {
            int number = database.update(FRIENDS_TABLE.TABLE_NAME, values,
                    whereClause, whereArgs);
            LogUtil.record("-----------------------------------------------------------");
            LogUtil.record("****************** updateFirendHead number:"
                    + number + " *****************");
            LogUtil.record("-----------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 插入一个好友信息
     */
    public synchronized void insertFriend(Context context, ContentValues values) {
        SQLiteDatabase database = null;
        database = getConnection();
        try {
            long id = database.insert(FRIENDS_TABLE.TABLE_NAME, null, values);
            LogUtil.record("-----------------------------------------------------------");
            LogUtil.record("****************** insertFriend id:" + id
                    + " *****************");
            LogUtil.record("-----------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 插入一个信息内容
     */
    public synchronized long insertMessage(Context context, ContentValues values) {
        SQLiteDatabase database = null;
        long id = 0;
        database = getConnection();
        try {
            id = database.insert(MESSAGE_TABLE.TABLE_NAME, null, values);
            LogUtil.record("-----------------------------------------------------------");
            LogUtil.record("****************** insertMessage id:" + id
                    + " *****************");
            LogUtil.record("-----------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
        return id;
    }

    /**
     * 查询信息
     */
    public synchronized FriendEntity getFriend(Context context, String value) {
        FriendEntity friend = null;
        SQLiteDatabase database = null;
        Cursor cursor = null;

        database = getConnection();

        String selection = FRIENDS_TABLE.FRIEND_ID + " = ?";
        String[] selectionArgs = {"" + value};

        try {
            cursor = database.query(FRIENDS_TABLE.TABLE_NAME, null, selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    friend = new FriendEntity();

                    String nike = cursor.getString(cursor
                            .getColumnIndex(FRIENDS_TABLE.NICK));
                    String phone = cursor.getString(cursor
                            .getColumnIndex(FRIENDS_TABLE.PHONE));
                    String friendID = cursor.getString(cursor
                            .getColumnIndex(FRIENDS_TABLE.FRIEND_ID));
                    String selfID = cursor.getString(cursor
                            .getColumnIndex(FRIENDS_TABLE.SELF_ID));

                    friend.setNickName(nike);
                    friend.setPhone(phone);
                    friend.setUserID(selfID);
                    friend.setFriendID(friendID);
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
        return friend;
    }

    /**
     * 插入消息的步骤： 1.往message表中插入一条新消息 2.更新friend表的type,content,time字段
     *
     * @param userID
     */
    public synchronized void updateMessage(Context context, String userID,
                                           String friendID, int direction, int type, String time,
                                           String content, int read_type) {
        SQLiteDatabase database = null;
        database = getConnection();

        // 更新同好友最后的一条聊天消息的时间、内容、类型
        ContentValues updates = new ContentValues();
        updates.put("type", type);
        updates.put("content", content);
        updates.put("time", time);
        updates.put("direction", direction);
        updates.put("read_type", read_type);
        // 参数
        String whereClause = MESSAGE_TABLE.SELF_ID + " = ? and "
                + MESSAGE_TABLE.FRIEND_ID + " = ? and " + MESSAGE_TABLE.CONTENT
                + " = ?";
        String[] whereArgs = {userID, friendID, content};
        try {
            // 执行更新
            database.update(MESSAGE_TABLE.TABLE_NAME, updates, whereClause,
                    whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
    }

    /**
     * 详细查询 通过id查询姓名
     */
    public synchronized FriendEntity queryName(String friend, String self) {
        Cursor cursor = null;
        SQLiteDatabase database = null;
        database = getConnection();

        FriendEntity mfriend = new FriendEntity();
        String[] selectionArgs = {friend, self};
        try {
            cursor = database.query(FRIENDS_TABLE.TABLE_NAME, null,
                    "friend_id = ? and self_id = ?", selectionArgs, null, null,
                    null);
            if (cursor.moveToFirst()) {
                mfriend.setUserID(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.SELF_ID)));
                mfriend.setFriendID(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.FRIEND_ID)));
                mfriend.setNickName(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.NICK)));
                mfriend.setHead(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.HEAD)));
                mfriend.setPhone(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.PHONE)));
                mfriend.setSex(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.SEX)));
                mfriend.setModifyTime(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.MODIFY_TIME)));
                mfriend.setCountryCode(cursor.getString(cursor
                        .getColumnIndex(FRIENDS_TABLE.COUNTRY_CODE)));
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
     * 更新头像
     *
     * @param context
     * @param selfId
     * @param friendId
     * @param headPath
     * @param modifyTime
     * @return
     */
    public synchronized int updateUserHead(Context context, String selfId,
                                           String friendId, String headPath, String modifyTime) {
        int resoult = 0;
        SQLiteDatabase database = null;
        database = getConnection();

        try {
            if (selfId != null && friendId != null && selfId.equals(friendId)) {
                ContentValues values = new ContentValues();
                values.put("head", headPath);
                values.put("modify_time", modifyTime);
                String selection = "self_id = ?";
                String[] selectionArgs = {selfId};
                resoult = database.update(USER_TABLE.TABLE_NAME, values,
                        selection, selectionArgs);
            } else {
                ContentValues values = new ContentValues();
                values.put("head", headPath);
                values.put("modify_time", modifyTime);
                String selection = "self_id = ? and friend_id = ?";
                String[] selectionArgs = {selfId, friendId};
                resoult = database.update(FRIENDS_TABLE.TABLE_NAME, values,
                        selection, selectionArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
        return resoult;
    }

    /**
     * 更新头像
     */
    public synchronized long updateUserInfo(String userID, String infoTitle,
                                            String info) {

        long resoult = 0;
        SQLiteDatabase database = null;
        database = getConnection();

        ContentValues values = new ContentValues();
        values.put(infoTitle, info);

        String whereClause = "self_id = ?";
        String[] whereArgs = {userID};
        try {
            resoult = database.update(USER_TABLE.TABLE_NAME, values,
                    whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
        return resoult;
    }

    /**
     * 查询的数据添加到friend实体中 并且保存到list集合中
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public synchronized ArrayList<FriendEntity> getFriendEntity(String userID) {

        Cursor cursor = null;
        SQLiteDatabase database = null;
        database = getConnection();

        ArrayList<FriendEntity> friendList = new ArrayList<FriendEntity>();

        String[] selectionArgs = {"" + userID};
        try {
            cursor = database.query(FRIENDS_TABLE.TABLE_NAME, null,
                    "self_id = ?", selectionArgs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    FriendEntity friend = new FriendEntity();
                    friend.setContent(cursor.getString(cursor
                            .getColumnIndex("content")));
                    friend.setCountry(cursor.getString(cursor
                            .getColumnIndex("country")));
                    friend.setCountryCode(cursor.getString(cursor
                            .getColumnIndex("country_code")));
                    friend.setCountryInitial(cursor.getString(cursor
                            .getColumnIndex("country_initial")));
                    friend.setFriendID(cursor.getString(cursor
                            .getColumnIndex("friend_id")));
                    friend.setHead(cursor.getString(cursor
                            .getColumnIndex("head")));
                    friend.setModifyTime(cursor.getString(cursor
                            .getColumnIndex("modify_time")));
                    String nickName = cursor.getString(cursor
                            .getColumnIndex("nick_Name"));
                    if (nickName.isEmpty()) {
                        friend.setNickName(cursor.getString(cursor
                                .getColumnIndex("friend_id")));
                    } else
                        friend.setNickName(nickName);

                    friend.setPhone(cursor.getString(cursor
                            .getColumnIndex("phone")));
                    friend.setUserID(cursor.getString(cursor
                            .getColumnIndex("self_id")));
                    friend.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                    friend.setTime(cursor.getString(cursor
                            .getColumnIndex("time")));
                    friend.setType(cursor.getString(cursor
                            .getColumnIndex("type")));
                    friend.setData1(cursor.getString(cursor
                            .getColumnIndex("data1")));
                    friend.setData2(cursor.getString(cursor
                            .getColumnIndex("data2")));
                    friend.setData3(cursor.getString(cursor
                            .getColumnIndex("data3")));
                    friend.setData4(cursor.getString(cursor
                            .getColumnIndex("data4")));
                    friend.setData5(cursor.getString(cursor
                            .getColumnIndex("data5")));
                    friendList.add(friend);
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
        return friendList;
    }

    // 查询数据库模糊查询
    public synchronized ArrayList<TreeEntity> getTree(String value) {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        database = getConnection();
        // 实例化实体类
        ArrayList<TreeEntity> list = new ArrayList<TreeEntity>();

        String[] selectionArgs = {value};
        try {
            cursor = database.query(TREE_TABLE.TABLE_NAME, null, "parent = ?",
                    selectionArgs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    TreeEntity entity = new TreeEntity();// 实例化实体
                    entity.setLabel(cursor.getString(cursor
                            .getColumnIndex(TREE_TABLE.COLUMN_LABEL)));
                    entity.setParent(cursor.getString(cursor
                            .getColumnIndex(TREE_TABLE.COLUMN_PARENT)));
                    entity.setFlag(cursor.getInt(cursor
                            .getColumnIndex(TREE_TABLE.COLUMN_FLAG)));
                    list.add(entity);
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
     * 插入消息的步骤： 1.往message表中插入一条新消息 2.更新friend表的type,content,time字段
     *
     * @param userID
     */
    public synchronized int updateFriend(Context context, String userID,
                                         String friendID, int direction, int type, String time,
                                         String content, int read_type) {
        SQLiteDatabase database = null;
        database = getConnection();
        int number = 0;
        try {
            // 更新同好友最后的一条聊天消息的时间、内容、类型
            ContentValues updates = new ContentValues();
            updates.put("type", type);
            updates.put("content", content);
            updates.put("time", time);

            String whereClause = "self_id = ? and friend_id = ?";
            String[] whereArgs = {userID, friendID};

            number = database.update(FRIENDS_TABLE.TABLE_NAME, updates,
                    whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != database) {
                database.close();
            }
        }
        return number;
    }

    /**
     * 查询的数据添加到friend实体中 并且保存到list集合中
     */
    public synchronized String getFriendNick(String friendID) {

        Cursor cursor = null;
        SQLiteDatabase database = null;
        database = getConnection();
        String nick = null;

        String[] selectionArgs = {"" + friendID};
        try {
            cursor = database.query(FRIENDS_TABLE.TABLE_NAME,
                    new String[]{"nick_Name"}, "friend_id = ?",
                    selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    nick = cursor.getString(cursor.getColumnIndex("nick_Name"));
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
        return nick;
    }

    /*
     * 查询数据库精确查询
     */
    public synchronized List<CountryEntity> getCountryCursor(Context context,
                                                             String contant) {

        Cursor cursor = null;
        SQLiteDatabase database = null;
        database = getConnection();

        List<CountryEntity> list = new ArrayList<CountryEntity>();// 实例化实体类
        String sql = "select * from countryCodeTable where country_code = "
                + contant;
        try {
            cursor = database.rawQuery(sql, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    CountryEntity entity = new CountryEntity();// 实例化实体
                    entity.setCountry(cursor.getString(cursor
                            .getColumnIndex(COUNTEYCODE_TABLE.COUNTRY)));
                    entity.setNumber(cursor.getString(cursor
                            .getColumnIndex(COUNTEYCODE_TABLE.COUNTRY_CODE)));
                    entity.setCountry_initial(cursor.getString(cursor
                            .getColumnIndex(COUNTEYCODE_TABLE.COUNTRY_INITIAL)));
                    list.add(entity);
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

    /*
     * 数据库查询，查询城市列表等
     */
    public synchronized List<CountryEntity> getCountryCursor() {
        Cursor cursor = null;
        SQLiteDatabase database = null;
        database = getConnection();

        ArrayList<CountryEntity> array = new ArrayList<CountryEntity>();
        try {
            cursor = database.query(COUNTEYCODE_TABLE.TABLE_NAME, null, null,
                    null, null, null, "_id"); // 获得_id属性
            while (cursor.moveToNext()) {
                CountryEntity entity = new CountryEntity();// 实例化实体
                entity.setCountry(cursor.getString(cursor
                        .getColumnIndex("country")));
                entity.setCountry_initial(cursor.getString(cursor
                        .getColumnIndex("country_initial")));
                entity.setNumber(cursor.getString(cursor
                        .getColumnIndex("country_code")));
                array.add(entity);
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

    //
    // /**
    // * 登录后loginID的增改查
    // */
    // public LoginStateEntity getLoginID() {
    //
    // Cursor cursor = null;
    // LoginStateEntity loginState = null;
    // SQLiteDatabase database = null;
    //
    // database = getConnection();
    // try {
    // cursor = database.query(LOGINSTATE_TABLE.TABLE_NAME, null, null, null,
    // null, null, null);
    // if (cursor != null) {
    // if (cursor.moveToFirst()) {
    // loginState = new LoginStateEntity();
    // loginState.setUserID(cursor.getString(cursor.getColumnIndex("userID")));
    // loginState.setLoginID(cursor.getString(cursor.getColumnIndex("loginID")));
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (null != database) {
    // database.close();
    // }
    // if (null != cursor) {
    // cursor.close();
    // }
    // }
    // return loginState;
    // }

    /**
     * 添加或者更新数据库数据
     */
    public synchronized void addOrUpdateLoginState(String userID, String loginID) {
        Cursor cursor = null;
        ContentValues values = null;
        SQLiteDatabase database = null;

        database = getConnection();
        values = new ContentValues();
        values.put(LOGINSTATE_TABLE.USERID, userID);
        values.put(LOGINSTATE_TABLE.LOGINID, loginID);
        String whereClause = "userID = ?";
        String[] whereArgs = {userID};
        try {
            cursor = database.query(LOGINSTATE_TABLE.TABLE_NAME, null,
                    whereClause, whereArgs, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    database.update(LOGINSTATE_TABLE.TABLE_NAME, values,
                            whereClause, whereArgs);
                } else {
                    database.insert(LOGINSTATE_TABLE.TABLE_NAME, null, values);
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

    // //**************************EBS*******************************************
    //
    // //增加或更新用户登录信息
    // public synchronized void addOrUpdateLoginUserEbs(LoginEbsEntity login) {
    // Cursor cursor = null;
    // ContentValues values = null;
    // SQLiteDatabase database = null;
    // database = getConnection();
    // try {
    // String selection = USERLOGIN_TABLE.USERCODE+" = ?";
    // String[] selectionArgs = {login.getUserCode()};
    // cursor = database.query(USERLOGIN_TABLE.TABLE_NAME, null, selection,
    // selectionArgs, null, null, null);
    // if (cursor != null) {
    // values = new ContentValues();
    // values.put(USERLOGIN_TABLE.ACCOUNT, login.getAccount());
    // values.put(USERLOGIN_TABLE.LOGINID, login.getLoginID());
    // values.put(USERLOGIN_TABLE.LOGINSTATE, login.getLoginState());
    // values.put(USERLOGIN_TABLE.MODIFYTIME, login.getModifyTime());
    // values.put(USERLOGIN_TABLE.PASSWORD, login.getPassword());
    // values.put(USERLOGIN_TABLE.USERCODE, login.getUserCode());
    // if (cursor.getCount() > 0) {
    // database.update(USERLOGIN_TABLE.TABLE_NAME, values, selection,
    // selectionArgs);
    // } else {
    // database.insert(USERLOGIN_TABLE.TABLE_NAME, null, values);
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (null != database) {
    // database.close();
    // }
    // if (null != cursor) {
    // cursor.close();
    // }
    // }
    // }
    //
    // // 增加或更新用户登录信息
    // public synchronized void updateLoginState(LoginEbsEntity login) {
    // SQLiteDatabase database = null;
    // database = getConnection();
    // try {
    // String selection = USERLOGIN_TABLE.USERCODE + " != ?";
    // String[] selectionArgs = { login.getUserCode() };
    // ContentValues values = new ContentValues();
    // values.put(USERLOGIN_TABLE.LOGINSTATE, "1");
    // database.update(USERLOGIN_TABLE.TABLE_NAME, values, selection,
    // selectionArgs);
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (null != database) {
    // database.close();
    // }
    // }
    // }

    // 增加或更新用户信息
    public synchronized void addOrUpdateUserInfoEbs(UserEbsEntity user,
                                                    String loginID, String password) {
        String webRoot = Util.getShareperference(context,
                Constants.SAVE_INFORMATION, "Path", Constants.CONFIG_INI_URL);
        String appTestFile = webRoot;
        IniFile IniFile = new IniFile();
        if (!webRoot.endsWith(".ini")) {
            if (webRoot.endsWith("/") == false) {
                webRoot += "/";
            }
            String WebIniFile = webRoot + Constants.WEB_CONFIG_FILE_NAME;
            appTestFile = webRoot
                    + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    Constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        }

        String userIni = appTestFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        if (!password.equalsIgnoreCase("")) {
            IniFile.writeIniString(userIni, "Login", "PassWord", password);
        }
        IniFile.writeIniString(userIni, "Login", "LoginId", loginID);
        IniFile.writeIniString(userIni, "Login", "Account", user.getUserId());
        IniFile.writeIniString(userIni, "Login", "LoginFlag", "1");
        IniFile.writeIniString(userIni, "Login", "UserCode",
                user.getUserCode());
        IniFile.writeIniString(userIni, "Login", "Location_city",
                user.getCity());
        IniFile.writeIniString(userIni, "Login", "Contact", user.getEmail());
        IniFile.writeIniString(userIni, "Login", "Signature",
                user.getIdiograph());
        IniFile.writeIniString(userIni, "Login", "Mobile", user.getMobile());
        IniFile.writeIniString(userIni, "Login", "Address", user.getMyURL());
        IniFile.writeIniString(userIni, "Login", "newEMail",
                user.getNewEMail());
        IniFile.writeIniString(userIni, "Login", "Sex", user.getSex());
        IniFile.writeIniString(userIni, "Login", "NickName",
                user.getUserName());
    }

    // // 查询用户账号密码
    // public LoginEbsEntity getLoginUserEbs(String loginState) {
    // Cursor cursor = null;
    // LoginEbsEntity loginUser = null;
    // SQLiteDatabase database = getConnection();
    // try {
    // String selection = USERLOGIN_TABLE.LOGINSTATE+" = ?";
    // String[] selectionArgs = {loginState};
    // cursor = database.query(USERLOGIN_TABLE.TABLE_NAME, null, selection,
    // selectionArgs, null, null, null);
    // if (cursor != null) {
    // if (cursor.moveToFirst()) {
    // loginUser = new LoginEbsEntity();
    // loginUser.setAccount(cursor.getString(cursor.getColumnIndex(USERLOGIN_TABLE.ACCOUNT)));
    // loginUser.setUserCode(cursor.getString(cursor.getColumnIndex(USERLOGIN_TABLE.USERCODE)));
    // loginUser.setPassword(cursor.getString(cursor.getColumnIndex(USERLOGIN_TABLE.PASSWORD)));
    // loginUser.setLoginState(cursor.getString(cursor.getColumnIndex(USERLOGIN_TABLE.LOGINSTATE)));
    // loginUser.setLoginID(cursor.getString(cursor.getColumnIndex(USERLOGIN_TABLE.LOGINID)));
    // loginUser.setModifyTime(cursor.getString(cursor.getColumnIndex(USERLOGIN_TABLE.MODIFYTIME)));
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // } finally {
    // if (null != database) {
    // database.close();
    // }
    // if (null != cursor) {
    // cursor.close();
    // }
    // }
    // return loginUser;
    // }
    //
    // 查询用户账号密码
    public UserEbsEntity getUserEbs() {
        String webRoot = Util.getShareperference(context,
                Constants.SAVE_INFORMATION, "Path", Constants.CONFIG_INI_URL);
        String appTestFile = webRoot;
        IniFile IniFile = new IniFile();
        if (!webRoot.endsWith(".ini")) {
            if (webRoot.endsWith("/") == false) {
                webRoot += "/";
            }
            String WebIniFile = webRoot + Constants.WEB_CONFIG_FILE_NAME;
            appTestFile = webRoot
                    + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    Constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        }
        String userIni = appTestFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        //System.out.println("userIni = " + userIni);
        UserEbsEntity user = null;
        user = new UserEbsEntity();
        user.setUserCode(IniFile.getIniString(userIni, "Login", "UserCode",
                "", (byte) 0));
        user.setPassword(IniFile.getIniString(userIni, "Login", "PassWord",
                "", (byte) 0));
        user.setLoginID(IniFile.getIniString(userIni, "Login", "LoginId",
                "", (byte) 0));
        user.setUserId(IniFile.getIniString(userIni, "Login", "Account",
                "", (byte) 0));
        user.setCity(IniFile.getIniString(userIni, "Login", "Location_city",
                "", (byte) 0));
        user.setCountryCode("");
        user.setModifyTime(TimeUtil.getdate());
        user.setEmail(IniFile.getIniString(userIni, "Login", "Contact", "",
                (byte) 0));
        user.setIdiograph(IniFile.getIniString(userIni, "Login",
                "Signature", "", (byte) 0));
        user.setMobile(IniFile.getIniString(userIni, "Login", "Mobile", "",
                (byte) 0));
        user.setMyURL(IniFile.getIniString(userIni, "Login", "Address", "",
                (byte) 0));
        user.setNewEMail(IniFile.getIniString(userIni, "Login", "newEMail",
                "", (byte) 0));
        user.setSex(IniFile.getIniString(userIni, "Login", "Sex", "",
                (byte) 0));
        user.setUserName(IniFile.getIniString(userIni, "Login", "NickName",
                "", (byte) 0));
        return user;
    }

    //
    // 查询用户头像信息
    public UserHeadEbsEntity getUserHeadEbs(String userCode) {
        Cursor cursor = null;
        UserHeadEbsEntity userHead = null;
        SQLiteDatabase database = getConnection();
        try {
            String selection = USERHEAD_TABLE.USERCODE + " = ?";
            String[] selectionArgs = {userCode};
            cursor = database.query(USERHEAD_TABLE.TABLE_NAME, null, selection,
                    selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    userHead = new UserHeadEbsEntity();
                    userHead.setUserCode(cursor.getString(cursor
                            .getColumnIndex(USERHEAD_TABLE.USERCODE)));
                    userHead.setHttpUrl(cursor.getString(cursor
                            .getColumnIndex(USERHEAD_TABLE.HTTPURL)));
                    userHead.setFileSize(cursor.getLong(cursor
                            .getColumnIndex(USERHEAD_TABLE.FILESIZE)));
                    userHead.setModifyTime(cursor.getString(cursor
                            .getColumnIndex(USERHEAD_TABLE.MODIFYTIME)));
                    userHead.setSavePath(cursor.getString(cursor
                            .getColumnIndex(USERHEAD_TABLE.SAVEPATH)));
                    userHead.setFileName(cursor.getString(cursor
                            .getColumnIndex(USERHEAD_TABLE.FILENAME)));
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
        return userHead;
    }
}