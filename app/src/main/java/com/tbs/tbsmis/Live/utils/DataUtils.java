package com.tbs.tbsmis.Live.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.circle.bean.User;
import com.tbs.tbsmis.Live.bean.CommentAll;
import com.tbs.tbsmis.Live.bean.LiveAlllist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by TBS on 2017/11/30.
 */

public class DataUtils
{
    //保存本地数据
    public static final String SAVE_INFORMATION = "save_information";
    //服务器端
    public static String HOST = "http://e.tbs.com.cn:1115/";
    //拉流服务器端
    public static String GETHOST = "http://168.160.111.26/hls/";
    //所有直播列表接口
    public static final String URL_ALL_LIVING = HOST + "servlet/mobileApi/getLiving.cbs";
    //所有评论
    public static final String URL_ALL_COMMENTS = HOST + "servlet/mobileApi/getLiveComment.cbs";
    //所有点赞
    public static final String URL_ALL_PRAISES = HOST + "servlet/mobileApi/getPraises.cbs";
    //点赞
    public static final String URL_ADD_PRAISES = HOST + "servlet/mobileApi/addLiveApproval.cbs";
    //取消点赞
    public static final String URL_CANCEL_PRAISES = HOST + "servlet/mobileApi/cancelLiveApproval.cbs";
    //所有评论
    public static final String URL_ADD_COMMENTS = HOST + "servlet/mobileApi/addLiveComment.cbs";
    //进入直播间接口
    public static final String URL_ENTRY_LIVING = HOST + "servlet/mobileApi/entryLive.cbs";
    //历史观看接口
    public static final String URL_urlPUBLISH = HOST + "servlet/mobileApi/getLivingHistory.cbs";
//    //音乐详情接口
//    public static final String URL_MUSIC_INFO = HOST + "servlet/mobileApi/getMusicInfo.cbs";
    //短视频详情接口
    public static final String URL_VIDEO_INFO = HOST + "servlet/mobileApi/getVideoInfo.cbs";
    //课程详情接口
    public static final String URL_COURSE_INFO = HOST + "servlet/mobileApi/getCourseInfo.cbs";
    //课程章节详情接口
    public static final String URL_COURSE_CHAPTER = HOST + "servlet/mobileApi/getCourseChapter.cbs";
    //剧集详情接口
    public static final String URL_FILM_INFO = HOST + "servlet/mobileApi/getFilmInfo.cbs";
    //剧集章节详情接口
    public static final String URL_FILM_CHAPTER = HOST + "servlet/mobileApi/getFilmChapter.cbs";

    public static List<LiveAlllist> createAllLiveDatas(JSONArray jsonarray) {

        List<LiveAlllist> circleDatas = new ArrayList<LiveAlllist>();
        for (int i = 0; i < jsonarray.length(); i++) {
            LiveAlllist item = new LiveAlllist();
            try {
                JSONObject json = jsonarray.getJSONObject(i);
                // 点赞评论的数据

                String room_id = json.getString("room_id");
                String room_src = json.getString("room_src");
                String cate_id = json.getString("cate_id");
                String room_name = json.getString("room_name");
                String show_time = json.getString("show_time");
                String owner_uid = json.getString("owner_uid");
                String nickname = json.getString("nickname");
                String online = json.getString("online");
                String state = json.getString("state");
                String tel = json.getString("tel");
                String qq = json.getString("qq");
                String owner_pic = json.getString("owner_pic");
                String notice = json.getString("notice");
                String isLiving = json.getString("isLiving");
                if (isLiving.equals("1")) {
                    item.setShow_status("6");
                } else {
                    item.setShow_status(state);
                }
                String id = json.getString("liveId");
                item.setId(id);
                item.setTel(tel);
                item.setQq(qq);
                item.setOwner_pic(owner_pic);
                item.setSpecific_catalog(notice);
                item.setCate_id(Integer.parseInt(cate_id));
                item.setNickname(nickname);
                item.setRoom_id(room_id);
                item.setUrl(GETHOST + "live" + room_id + "/index.m3u8");
                item.setRoom_src(room_src);
                item.setRoom_name(room_name);
                item.setShow_time(show_time);
                item.setOwner_uid(owner_uid);
                item.setOnline(Integer.parseInt(online));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            circleDatas.add(item);
        }
        return circleDatas;
    }

    public static void setSharePerference(Context context,
                                          String perferenceName, String MapName, String value) {
        SharedPreferences setting = context.getSharedPreferences(
                perferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(MapName, value);
        editor.commit();
    }


    public static String getShareperference(Context context,
                                            String perferenceName, String MapName, String defvalue) {
        String MsgNum = null;
        SharedPreferences Getting = context.getSharedPreferences(
                perferenceName, Context.MODE_PRIVATE);
        MsgNum = Getting.getString(MapName, defvalue);
        return MsgNum;
    }

    public static String getStringTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date);
        return startTime;
    }

    public static List<CommentAll> createCommentDatas(JSONArray jsonArray) {
        List<CommentAll> commentDatas = new ArrayList<CommentAll>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject json = jsonArray.getJSONObject(i);
                String parentId = json.getString("parentId");
                CommentAll Pitem = new CommentAll();
                if (parentId.equalsIgnoreCase("0")) {
                    String userId = json.getString("issueUser");
                    String nickName = json.getString("issueName");
                    String avatar = "";
                    User user = new User(userId, nickName, avatar);
                    String Pid = json.getString("id");
                    Pitem.setId(Pid);
                    Pitem.setContent(json.getString("content"));
                    Pitem.setUser(user);
                    Pitem.setPath(json.getString("path"));
                    Pitem.setCreateTime(json.getString("issueDate"));
                    List<CommentItem> items = new ArrayList<CommentItem>();
                    for (int j = (jsonArray.length()-1); j >= 0; j--) {
                        JSONObject json2 = jsonArray.getJSONObject(j);
                        String path = json2.getString("path");
                        if (path.contains(Pid) && !path.equalsIgnoreCase("0")) {

                            CommentItem item = new CommentItem();
                            String id = json2.getString("id");
                            String nick = json2.getString("issueName");
                            String content = json2.getString("content");
                            String cid = json2.getString("issueUser");
                            String Rnickname = json2.getString("parentName");
                            item.setCid(id);
                            item.setPath(path);
                            item.setContent(content);
                            item.setUser(cid);
                            item.setUnickname(nick);
                            if (path.lastIndexOf(",") > 1)
                                item.setToReplyUser(Rnickname);
                            items.add(item);
                        }
                    }
                    Pitem.setComments(items);
                    Pitem.setFavorters(new ArrayList<FavortItem>());
                    Pitem.setUnFavorters(new ArrayList<FavortItem>());
                    commentDatas.add(Pitem);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return commentDatas;
    }

    /**
     * 创建回复评论
     *
     * @return
     */
    public static CommentItem createReplyComment(Map map, String commentId, Context context) {
        CommentItem item = new CommentItem();
        item.setCid(commentId);
        item.setContent((String) map.get("content"));
        item.setUnickname((String) map.get("issueName"));
        item.setUser((String) map.get("issueUser"));
        if (((String) map.get("path")).lastIndexOf(",") > 1)
        item.setToReplyUser((String) map.get("parentName"));
        item.setPath((String) map.get("path"));
        return item;
    }

    public static List<FavortItem> createPraiseDatas(JSONArray jsonArray) {
        List<FavortItem> Praises = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                FavortItem favortItem = new FavortItem();
                JSONObject json = jsonArray.getJSONObject(i);
                favortItem.setId(json.getString("id"));
                favortItem.setType(json.getString("type"));
                favortItem.setUser(json.getString("userId"));
                favortItem.setUserName(json.getString("account"));
                favortItem.setZid(json.getString("zId"));
                Praises.add(favortItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return Praises;
    }
}
