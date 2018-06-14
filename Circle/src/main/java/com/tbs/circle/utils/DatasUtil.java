package com.tbs.circle.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.tbs.chat.constants.Constants;
import com.tbs.chat.util.Util;
import com.tbs.circle.bean.CircleItem;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.circle.bean.PhotoInfo;
import com.tbs.circle.bean.User;
import com.tbs.ini.IniFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiw
 * @ClassName: DatasUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2015-12-28 下午4:16:21
 */
public class DatasUtil
{
    //保存本地数据
    public static final String SAVE_INFORMATION = "save_information";
    //服务器端
    public static final String HOST = "http://e.tbs.com.cn:1112/api/";
    //    朋友圈接口
    public static final String URL_PUBLISH = HOST + "publish.cbs";//发布动态
    //    朋友圈接口
    public static final String URL_urlPUBLISH = HOST + "urlpublish.cbs";//发布动态
    public static final String URL_PUBLISH_FILE = HOST + "upload.cbs";//动态内容
    public static final String URL_SOCIAL = HOST + "fetchTimeline.cbs";//获取动态列表
    public static final String URL_SOCIAL_DELETE = HOST + "removeTimeline.cbs";//删除动态
    public static final String URL_SOCIAL_FRIEND = HOST + "fetchOtherTimeline.cbs";//获取好友朋友圈列表
    public static final String URL_SOCIAL_COMMENT = HOST + "commentTimeline.cbs";//朋友圈动态评论
    public static final String URL_SOCIAL_DELETE_COMMENT = HOST + "deleteCommentTimeline.cbs";//删除朋友圈动态评论
    public static final String URL_SOCIAL_REPLY_COMMENT = HOST + "replyCommentTimeline.cbs";//回复朋友圈动态评论
    public static final String URL_SOCIAL_DELETE_REPLY_COMMENT = HOST + "deleteReplyCommentTimeline.cbs";//删除朋友圈动态评论回复
    public static final String URL_SOCIAL_GOOD = HOST + "praiseTimeline.cbs";//点赞
    public static final String URL_SOCIAL_GOOD_CANCEL = HOST + "deletePraiseTimeline.cbs";//取消点赞
    public static final String URL_SOCIAL_GET_PRAISELIST = HOST + "fetchTimelineParises.cbs";//获取赞列表
    public static final String URL_SOCIAL_GET_COMMENTLIST = HOST + "fetchTimelineComments.cbs";//获取评论列表
    public static final String URL_SOCIAL_GET_DETAIL = HOST + "dynamicInfo.cbs";//获取评论列表

    public static final String[] CONTENTS = {"",
            //"哈哈，18123456789,ChinaAr  http://www.ChinaAr.com;一个不错的VR网站。哈哈，ChinaAr  http://www.ChinaAr.com;
            // 一个不错的VR网站。哈哈，ChinaAr  http://www.ChinaAr.com;一个不错的VR网站。哈哈，ChinaAr  http://www.ChinaAr.com;一个不错的VR网站。",
            //"今天是个好日子，http://www.ChinaAr.com;一个不错的VR网站,18123456789,",
            //"呵呵，http://www.ChinaAr.com;一个不错的VR网站,18123456789,",
            //"只有http|https|ftp|svn://开头的网址才能识别为网址，正则表达式写的不太好，如果你又更好的正则表达式请评论告诉我，谢谢！",
            "如何自己实现一个多图效果",
            //"哈哈哈哈",
            //"图不错",
            "我勒个去"};
    /*public static final String[] PHOTOS = {
            "http://f.hiphotos.baidu.com/image/pic/item/faf2b2119313b07e97f760d908d7912396dd8c9c.jpg",
            "http://g.hiphotos.baidu.com/image/pic/item/4b90f603738da977c76ab6fab451f8198718e39e.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/902397dda144ad343de8b756d4a20cf430ad858f.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/a6efce1b9d16fdfa0fbc1ebfb68f8c5495ee7b8b.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134e61e0f90211f95cad1c85e36.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/7dd98d1001e939011b9c86d07fec54e737d19645.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/f11f3a292df5e0fecc3e83ef586034a85edf723d.jpg",
            "http://cdn.duitang.com/uploads/item/201309/17/20130917111400_CNmTr.thumb.224_0.png",
            "http://pica.nipic.com/2007-10-17/20071017111345564_2.jpg",
            "http://pic4.nipic.com/20091101/3672704_160309066949_2.jpg",
            "http://pic4.nipic.com/20091203/1295091_123813163959_2.jpg",
            "http://pic31.nipic.com/20130624/8821914_104949466000_2.jpg",
            "http://pic6.nipic.com/20100330/4592428_113348099353_2.jpg",
            "http://pic9.nipic.com/20100917/5653289_174356436608_2.jpg",
            "http://img10.3lian.com/sc6/show02/38/65/386515.jpg",
            "http://pic1.nipic.com/2008-12-09/200812910493588_2.jpg",
            "http://pic2.ooopic.com/11/79/98/31bOOOPICb1_1024.jpg" };*/
    public static final String[] HEADIMG = {
            "http://www.feizl.com/upload2007/2014_06/1406272351394618.png",
            "http://v1.qzone.cc/avatar/201308/30/22/56/5220b2828a477072.jpg%21200x200.jpg",
            "http://v1.qzone.cc/avatar/201308/22/10/36/521579394f4bb419.jpg!200x200.jpg",
            "http://v1.qzone.cc/avatar/201408/20/17/23/53f468ff9c337550.jpg!200x200.jpg",
            "http://cdn.duitang.com/uploads/item/201408/13/20140813122725_8h8Yu.jpeg",
            "http://img.woyaogexing.com/touxiang/nv/20140212/9ac2117139f1ecd8%21200x200.jpg",
            "http://p1.qqyou.com/touxiang/uploadpic/2013-3/12/2013031212295986807.jpg"};

//    public static List<User> users = new ArrayList<User>();
//    public static List<PhotoInfo> PHOTOS = new ArrayList<>();
    /**
     * 动态id自增长
     */
    private static int circleId = 0;
    /**
     * 点赞id自增长
     */
    private static int favortId = 0;
    /**
     * 评论id自增长
     */
    private static int commentId = 0;
    //public static final User curUser = getUserMsg(context);

    //    static {
//        User user1 = new User("1", "张三", HEADIMG[1]);
//        User user2 = new User("2", "李四", HEADIMG[2]);
//        User user3 = new User("3", "隔壁老王", HEADIMG[3]);
//        User user4 = new User("4", "赵六", HEADIMG[4]);
//        User user5 = new User("5", "田七", HEADIMG[5]);
//        User user6 = new User("6", "Naoki", HEADIMG[6]);
////		User user7 = new User("7", "这个名字是不是很长，哈哈！因为我是用来测试换行的", HEADIMG[7]);
//
//        users.add(curUser);
//        users.add(user1);
//        users.add(user2);
//        users.add(user3);
//        users.add(user4);
//        users.add(user5);
//        users.add(user6);
////		users.add(user7);
//
//        PhotoInfo p1 = new PhotoInfo();
//        p1.url = "http://pic136.nipic.com/file/20170724/17961491_164410855000_2.jpg";
//        p1.w = 640;
//        p1.h = 792;
//
//        PhotoInfo p2 = new PhotoInfo();
//        p2.url = "http://pic138.nipic.com/file/20170815/25159533_215144162031_2.jpg";
//        p2.w = 640;
//        p2.h = 792;
//
//        PhotoInfo p3 = new PhotoInfo();
//        p3.url = "http://pic136.nipic.com/file/20170722/24874447_100252035000_2.jpg";
//        p3.w = 950;
//        p3.h = 597;
//
//        PhotoInfo p4 = new PhotoInfo();
//        p4.url = "http://pic138.nipic.com/file/20170813/24874447_151657276000_2.jpg";
//        p4.w = 533;
//        p4.h = 800;
//
//        PhotoInfo p5 = new PhotoInfo();
//        p5.url = "http://pic138.nipic.com/file/20170813/24874447_151657702000_2.jpg";
//        p5.w = 700;
//        p5.h = 467;
//
//        PhotoInfo p6 = new PhotoInfo();
//        p6.url = "http://pic138.nipic.com/file/20170812/24874447_143850223000_2.jpg";
//        p6.w = 700;
//        p6.h = 467;
//
//        PhotoInfo p7 = new PhotoInfo();
//        p7.url = "http://pica.nipic.com/2007-10-17/20071017111345564_2.jpg";
//        p7.w = 1024;
//        p7.h = 640;
//
//        PhotoInfo p8 = new PhotoInfo();
//        p8.url = "http://pic4.nipic.com/20091101/3672704_160309066949_2.jpg";
//        p8.w = 1024;
//        p8.h = 768;
//
//        PhotoInfo p9 = new PhotoInfo();
//        p9.url = "http://pic4.nipic.com/20091203/1295091_123813163959_2.jpg";
//        p9.w = 1024;
//        p9.h = 640;
//
//        PhotoInfo p10 = new PhotoInfo();
//        p10.url = "http://pic31.nipic.com/20130624/8821914_104949466000_2.jpg";
//        p10.w = 1024;
//        p10.h = 768;
//
//        PHOTOS.add(p1);
//        PHOTOS.add(p2);
//        PHOTOS.add(p3);
//        PHOTOS.add(p4);
//        PHOTOS.add(p5);
//        PHOTOS.add(p6);
//        PHOTOS.add(p7);
//        PHOTOS.add(p8);
//        PHOTOS.add(p9);
//        PHOTOS.add(p10);
//    }

    public static User getUserMsg(Context context){
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
        String NickName = IniFile.getIniString(userIni, "Login", "NickName",
                "", (byte) 0);
        String Account = IniFile.getIniString(userIni, "Login", "Account",
                "yangzt", (byte) 0);
        if(NickName.isEmpty())
            return new User(Account, Account, HEADIMG[0]);
        else
            return new User(Account, NickName, HEADIMG[0]);
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

    public static List<CircleItem> createCircleDatas(JSONArray jsonarray) {

        List<CircleItem> circleDatas = new ArrayList<CircleItem>();
        for (int i = 0; i < jsonarray.length(); i++) {
            CircleItem item = new CircleItem();
            try {
                JSONObject json = jsonarray.getJSONObject(i);
                final String userId = json.getString("userId");
                String nickName = json.getString("usernick");
                String avatar = json.getString("avatar");
                User user = new User(userId, nickName, avatar);
                item.setUser(user);
                // 点赞评论的数据
                JSONArray goodArray = json.getJSONArray("praises");
                JSONArray commentArray = json.getJSONArray("comment");
                item.setFavorters(createFavortItemList(goodArray));
                item.setComments(createCommentItemList(commentArray));
                final String aId = json.getString("id");
                String publishTime = json.getString("time");
                String content = json.getString("content");
                String type = json.getString("type");
                String url = json.getString("url");
                String title = json.getString("title");
                String imageStr = json.getString("imagestr");
                item.setContent(content);
                item.setId(aId);
                item.setLink(url);
                item.setCreateTime(publishTime);
                if (type.equals("0")) {
                    item.setType(type);// 文本
                    item.setLinkTitle(title);
                }else if (type.equals("1")) {
                    item.setType(type);// 链接
                    item.setLinkImg(imageStr);
                    item.setLinkTitle(title);
                } else if (type.equals("2")) {
                    item.setType(type);// 图片
                    item.setPhotos(createPhotos(imageStr));
                } else if (type.equals("3") || type.equals("4")) {
                    item.setType(type);// 视频
                    String videoUrl = imageStr;
                    //String videoImgUrl = imageStr;
                    item.setVideoUrl(videoUrl);
                    //item.setVideoImg();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            circleDatas.add(item);
        }
        return circleDatas;
    }

    public static String getUser(Context context) {
        return getUserMsg(context).getId();
    }

//    public static String getContent() {
//        return CONTENTS[getRandomNum(CONTENTS.length)];
//    }
//
//    public static int getRandomNum(int max) {
//        Random random = new Random();
//        int result = random.nextInt(max);
//        return result;
//    }

    public static List<PhotoInfo> createPhotos(String imageStr) {
        List<PhotoInfo> photos = new ArrayList<PhotoInfo>();
        if (imageStr.contains(",")) {
            String[] images = imageStr.split(",");
            for (int i = 0; i < images.length; i++) {
                PhotoInfo photo = new PhotoInfo();
                photo.url = images[i];
                photos.add(photo);
            }
        } else {
            PhotoInfo photo = new PhotoInfo();
            photo.url = imageStr;
            photos.add(photo);
        }
        return photos;
    }

    public static List<FavortItem> createFavortItemList(JSONArray goodData) {
        List<FavortItem> items = new ArrayList<FavortItem>();
        for (int i = 0; i < goodData.length(); i++) {
            JSONObject goodJson = null;
            try {
                goodJson = goodData.getJSONObject(i);
                FavortItem newItem = createFavortItem(goodJson);
                items.add(newItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return items;
    }

    public static FavortItem createFavortItem(JSONObject goodJson) {
        FavortItem item = new FavortItem();
        try {
            String userId = goodJson.getString("userId");
            String nick = goodJson.getString("nickname");
            String pid = goodJson.getString("pid");
            item.setId(userId);
            item.setUser(pid);
            item.setUserName(nick);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

    public static FavortItem createCurUserFavortItem(int cId,Context context) {
        FavortItem item = new FavortItem();
        item.setId(cId+"");
        item.setUserName(getUserMsg(context).getName());
        item.setUser(getUser(context));
        return item;
    }

    public static List<CommentItem> createCommentItemList(JSONArray comments) {
        List<CommentItem> items = new ArrayList<CommentItem>();
        for (int i = 0; i < comments.length(); i++) {
            try {
                items.add(createComment(comments.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public static CommentItem createComment(JSONObject comment) {
        CommentItem item = new CommentItem();
        try {
            int userId = comment.getInt("userId");
            String nick = comment.getString("nickname");
            String content = comment.getString("content");
            String cid = comment.getString("cid");
            String Rnickname = comment.getString("Rnickname");
            item.setId(userId);
            item.setContent(content);
            item.setUser(cid);
            item.setUnickname(nick);
            item.setToReplyUser(Rnickname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 创建发布评论
     *
     * @return
     */
    public static CommentItem createPublicComment(int cId,String content,Context context) {
        CommentItem item = new CommentItem();
        item.setId(cId);
        item.setContent(content);
        item.setUnickname(getUserMsg(context).getName());
        item.setUser(getUser(context));
        return item;
    }

    /**
     * 创建回复评论
     *
     * @return
     */
    public static CommentItem createReplyComment(String replyUser, String content,int commentId,Context context) {
        CommentItem item = new CommentItem();
        item.setId(commentId);
        item.setContent(content);
        item.setUnickname(getUserMsg(context).getName());
        item.setUser(getUser(context));
        item.setToReplyUser(replyUser);
        return item;
    }


    public static CircleItem createVideoItem(String videoUrl, String imgUrl,Context context) {
        CircleItem item = new CircleItem();
        item.setId(String.valueOf(circleId++));
        item.setUser(getUserMsg(context));
        //item.setContent(getContent());
        item.setCreateTime("12月24日");

        //item.setFavorters(createFavortItemList());
        //item.setComments(createCommentItemList());
        item.setType("3");// 图片
        item.setVideoUrl(videoUrl);
        item.setVideoImgUrl(imgUrl);
        return item;
    }
}
