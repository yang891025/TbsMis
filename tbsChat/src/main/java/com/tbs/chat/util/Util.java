package com.tbs.chat.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.tbs.chat.database.DataBaseUtil;
import com.tbs.chat.database.table.FRIENDS_TABLE;
import com.tbs.chat.entity.FriendEntity;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;

public class Util
{

    private static final String TAG = "Util";

    static AlertDialog alertDialog;
    static AlertDialog.Builder builder;
    static Activity activity;
    static InputStream inputStreams;

    // 鑾峰緱鏈湴鐨刡itmap
    public static Bitmap getLoacalBitmap(String url) {
        FileInputStream fis = null;
        if (null != url && !(url.equals(""))) {
            try {
                fis = new FileInputStream(url);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(url, options);

                options.inSampleSize = computeSampleSize(options, -1, 128 * 128);
                options.inJustDecodeBounds = false;
                Bitmap bmp = BitmapFactory.decodeFile(url, options);
                fis.close();
                return bmp; // 杩斿洖bitmap
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    // 删除本地文件
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    // 判断网络状态
    public static boolean checkNetState(Context context) {
        Context c = context;
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.getState() == State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    // 关闭输入法 close input method after send
    public static void closeInput(EditText contentTextEdit, Context context) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(contentTextEdit.getWindowToken(), 0);
    }

    // 打开输入法 open input method after send
    public static void openInput(EditText contentTextEdit, Context context) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(contentTextEdit.getWindowToken(),
                InputMethodManager.SHOW_IMPLICIT);
    }

    // 设置listview滑动块
    public static void setListViewFastScoll(ListView listview, Context context,
                                            int drawableResouce) {

        try {
            Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);
            Object o = f.get(listview);
            f = f.getType().getDeclaredField("mThumbDrawable");
            f.setAccessible(true);
            Drawable drawable = (Drawable) f.get(o);
            drawable = context.getResources().getDrawable(drawableResouce);
            f.set(o, drawable);
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static JSONObject object;
    private static String result = null;
    private static float version = 0;

    public static float doGet(String url) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            result = EntityUtils.toString(response.getEntity());
            object = new JSONObject(result);
            Log.d("HttpActivity", result);
            version = object.getInt("version");
            // JSONArray array = object.getJSONArray("data");
            // for (int i = 0; i < array.length(); i++) {
            //
            // JSONObject jsonItem = array.getJSONObject(i);
            // name=(String) jsonItem.get("username");
            // ordernum=(String) jsonItem.get("ordernum");
            // phone=(String) jsonItem.get("phonenum");
            // Log.i("HttpActivity", name);
            // }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return version;
    }

    // inputstream杞琤yte
    private static byte[] getFileBuffer(InputStream inStream, long fileLength)
            throws IOException {
        byte[] buffer = new byte[256 * 1024];
        byte[] fileBuffer = new byte[(int) fileLength];
        int count = 0;
        int length = 0;
        while ((length = inStream.read(buffer)) != -1) {
            for (int i = 0; i < length; ++i) {
                fileBuffer[count + i] = buffer[i];
            }
            count += length;
        }
        return fileBuffer;
    }

    // 杞欢鏇存柊Post鎻愪氦
    public static float doPost(String url) {
        try {
            URL path = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) path.openConnection();
            conn.setReadTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            InputStream inStream = conn.getInputStream();
            // URL newUrl = new URL(url);
            // URLConnection connect = newUrl.openConnection();
            // connect.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            // InputStream inStream = connect.getInputStream();
            byte[] data = getFileBuffer(inStream, 3);
            // byte[] data = new byte[inStream.available()];
            String json = new String(data, "UTF-8");
            Log.d(TAG, "json : " + json);
            // JSONObject object = new JSONObject(json);
            // version = object.getInt("version");
            version = Float.valueOf(json);
            Log.d(TAG, "version : " + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    // 弹出对话框
    public static void checkDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setInverseBackgroundForced(true)
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // 获取sd卡路径
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    // 鏌ヨ鎵嬫満鍐呭瓨璺緞
    public static String getInternalPath() {
        File sdDir = null;
        sdDir = Environment.getRootDirectory();
        return sdDir.toString();
    }

    // float转int
    public static float getFloatToInt(float start) {
        float end = 0;
        int scale = 0;
        int roundingMode = 4;
        BigDecimal bd = new BigDecimal(start);
        bd = bd.setScale(scale, roundingMode);
        end = bd.floatValue();
        return end;
    }

    // 文字转拼音
    public static String converterToFirstSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        try {
            if (nameChar[0] > 128) {
                StringBuffer pinyinName1 = new StringBuffer();
                String[] strs2 = PinyinHelper.toHanyuPinyinStringArray(
                        nameChar[0], defaultFormat);
                if (strs2 != null) {
                    pinyinName1.append(strs2[0].charAt(0));
                    pinyinName += pinyinName1.toString();
                } else {
                    return "#";
                }

            } else if (nameChar[0] < 64 || (nameChar[0] >= 91 && nameChar[0] <= 96)) {
                return "#";
            } else {
                pinyinName += nameChar[0];
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();

        }
        return pinyinName.toUpperCase();
    }

    // 获得外部存储路径
    public static String getExternalPath() {
        try {// read property
            File externalFile = Environment.getExternalStorageDirectory();
            String externalPath = externalFile.getAbsolutePath();
            return externalPath;
        } catch (Exception e) {
            return null;
        }
    }

    // 读取property
    public static Properties getProperty(Context context, String path) {
        PropertyReader pReader = null;
        Properties pro = null;
        File file = new File(path);
        if (file.exists()) {
            try {// read property
                pReader = new PropertyReader(context, path);
                pro = pReader.loadConfig();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return pro;
        } else {
            return null;
        }
    }

    // MD5加密
    public static String MD5(String strSrc) {
        MessageDigest md = null;
        byte[] bt = strSrc.getBytes();
        try {
            if (md == null) {
                md = MessageDigest.getInstance("MD5");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(bt);
        String strDes = bytes2Hex(md.digest());
        return strDes;
    }

    private static String bytes2Hex(byte[] bts) {
        StringBuffer des = new StringBuffer();
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }

    /*
     * 鏌ヨ鑷繁鐨勬墍鏈夊ソ鍙嬩俊鎭�
     */
    public static ArrayList<FriendEntity> queryFriends(Context context,
                                                       String selfId) {
        ArrayList<FriendEntity> list = new ArrayList<FriendEntity>();
        String[] columns = {"friend_id", "nick_Name", "head", "modify_time",
                "type", "content", "time", "phone"};
        String selection = " self_id ='" + selfId + "' and friend_id !='"
                + selfId + "'";
        Cursor cursor = new DataBaseUtil(context).getDataBase().query(
                FRIENDS_TABLE.TABLE_NAME, columns, selection, null, null, null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                FriendEntity friend = new FriendEntity();
                friend.setUserID(selfId);
                friend.setFriendID(cursor.getString(cursor
                        .getColumnIndex("friend_id")));
                friend.setNickName(cursor.getString(cursor
                        .getColumnIndex("nick_Name")));
                friend.setHead(cursor.getString(cursor.getColumnIndex("head")));
                friend.setModifyTime(cursor.getString(cursor
                        .getColumnIndex("modify_time")));
                friend.setType(cursor.getString(cursor.getColumnIndex("type")));
                friend.setContent(cursor.getString(cursor
                        .getColumnIndex("content")));
                friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
                friend.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                list.add(friend);
            }
        }
        cursor.close();
        return list;
    }

    public static FriendEntity detailFriends(Context context, String phone) {
        FriendEntity friend = null;
        String sql = "select (country_code + phone) from FriendsTable e where (country_code + phone) = '"
                + phone + "' or phone = '" + phone + "'";
        Cursor cursor = new DataBaseUtil(context).getDataBase().rawQuery(sql,
                null);
        // 濡傛灉娓告爣涓虹┖锛堟煡鎵惧け璐ワ級鎴栨煡鍒扮殑淇℃伅鏁颁綅0锛岃繑鍥瀗ull
        if (cursor != null) {
            while (cursor.moveToNext()) {
                friend = new FriendEntity();
                friend.setUserID(cursor.getString(cursor
                        .getColumnIndex("self_id")));
                friend.setFriendID(cursor.getString(cursor
                        .getColumnIndex("friend_id")));
                friend.setNickName(cursor.getString(cursor
                        .getColumnIndex("nick_Name")));
                friend.setHead(cursor.getString(cursor.getColumnIndex("head")));
                friend.setModifyTime(cursor.getString(cursor
                        .getColumnIndex("modify_time")));
                friend.setType(cursor.getString(cursor.getColumnIndex("type")));
                friend.setContent(cursor.getString(cursor
                        .getColumnIndex("content")));
                friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
                friend.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            }
        }
        cursor.close();
        return friend;
    }

    public static Cursor queryAllFriend(Context context, String value) {
        Cursor cursor;
        String sql = "select * from FriendsTable where nick_Name like '%"
                + value + "%' or phone like '%" + value
                + "%' or friend_id like '%" + value + "%'";
        cursor = new DataBaseUtil(context).getDataBase().rawQuery(sql, null);
        return cursor;
    }

    public static ArrayList<FriendEntity> getFriendEntity(Context context,
                                                          String value) {
        ArrayList<FriendEntity> friendList = new ArrayList<FriendEntity>();
        Cursor cursor = queryAllFriend(context, value);
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
                friend.setHead(cursor.getString(cursor.getColumnIndex("head")));
                friend.setModifyTime(cursor.getString(cursor
                        .getColumnIndex("modify_time")));
                friend.setNickName(cursor.getString(cursor
                        .getColumnIndex("nick_Name")));
                friend.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                friend.setUserID(cursor.getString(cursor
                        .getColumnIndex("self_id")));
                friend.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                friend.setTime(cursor.getString(cursor.getColumnIndex("time")));
                friend.setType(cursor.getString(cursor.getColumnIndex("type")));
                friend.setData1(cursor.getString(cursor.getColumnIndex("data1")));
                friend.setData2(cursor.getString(cursor.getColumnIndex("data2")));
                friend.setData3(cursor.getString(cursor.getColumnIndex("data3")));
                friend.setData4(cursor.getString(cursor.getColumnIndex("data4")));
                friend.setData5(cursor.getString(cursor.getColumnIndex("data5")));
                friendList.add(friend);
            }
        }
        cursor.close();
        return friendList;
    }

    @SuppressWarnings("static-access")
    public static int getShareperference(Context context,
                                         String perferenceName, String MapName, int defvalue) {
        int MsgNum = 0;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        MsgNum = Getting.getInt(MapName, defvalue);
        return MsgNum;
    }

    @SuppressWarnings("static-access")
    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, int value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt(MapName, value);
        editor.commit();
    }

    @SuppressWarnings("static-access")
    public static String getShareperference(Context context,
                                            String perferenceName, String MapName, String defvalue) {
        String MsgNum = null;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        MsgNum = Getting.getString(MapName, defvalue);
        return MsgNum;
    }

    @SuppressWarnings("static-access")
    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, String value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(MapName, value);
        editor.commit();
    }

    @SuppressWarnings("static-access")
    public static boolean getShareperference(Context context,
                                             String perferenceName, String MapName, boolean value) {
        boolean MsgNum;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        MsgNum = Getting.getBoolean(MapName, value);
        return MsgNum;
    }

    @SuppressWarnings("static-access")
    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, boolean value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(MapName, value);
        editor.commit();
    }
}