package com.tbs.tbsmis.constants;

import android.annotation.SuppressLint;

import com.tbs.tbsmis.R;


@SuppressLint("SdCardPath")
public class constants
{
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx0bcc0bd44dc59288";

    public static final String DISK_ROOT = "disk";
    //	public static final String VERIFY_PHONE = "verify_phone";// ע����
//	public static final String OPEN_VERIFY_PHONE = "open_verify_phone";
//	public static final String OPEN_REG_INFO = "open_reg_info";
//	public static final int NEXTPAGE = 0x0001;
    public static String verifyURL = "http://e.tbs.com.cn:8083";
    public static final String SHARED_PREFERENCE_FIRST_LAUNCH = "first_launch_preferences";
    public static final String SD_CARD_TBS_PATH1 = "Tbs-Soft";
    public static final String SD_CARD_ZIP_PATH2 = "/Share/";
    public static final String SD_CARD_TEMP_PATH3 = "/Temp/";
    public static final String SD_CARD_TBSSOFT_PATH3 = "/App";
    public static final String SD_CARD_TBSFILE_PATH5 = "/Screen";
    public static final String SD_CARD_TBSFILE_PATH6 = "/File";
    //	public static final String SD_CARD_TBSAPP_WEB = "web";
    public static final String SD_CARD_TBSAPP_PUSH = "message";
    public static final String WEB_CONFIG_FILE_NAME = "TbsWeb.ini";
    //	public static final String WECHAT_FILE_NAME = "WeChat.ini";
    public static final String TASK_CONFIG_FILE_NAME = "NetTask.ini";
    public static final String APP_CONFIG_FILE_NAME = "TbsApp.ini";
    public static final String NEWS_CONFIG_FILE_NAME = "TbsTbs.ini";
    public static final String USER_CONFIG_FILE_NAME = "TbsUser.ini";
    public static final long ShowSplashMillisecond = 3000;

    public static final String HOME_TAB = "home_tab";
    public static final String MENTION_TAB = "mention_tab";
    public static final String MANAGER_TAB = "manager_tab";
    public static final String MORE_TAB = "more_tab";
    public static final String FAVOURITE_TAB = "favourite_tab";

    public static int ButtonImageId[] = {
            R.drawable.tabs_home,
            R.drawable.tabs_sort,
            R.drawable.tabs_search,
            R.drawable.tabs_manager,
            R.drawable.tabs_more
    };
    public static int TopButtonImageId[] = {
            R.drawable.title_home_s,
            R.drawable.title_bar_more_btn_pressed,
            R.drawable.title_bar_search_btn_pressed,
            R.drawable.right_navigation_setting_pressed,
            R.drawable.recent_chat_showleft_pressed,
            R.drawable.title_bar_dic_btn_pressed
    };
    public static int TopButtonIcoId[] = {
            R.drawable.title_home,
            R.drawable.menu_btn,
            R.drawable.search_button,
            R.drawable.setup_btn,
            R.drawable.normal_btn,
            R.drawable.menu_fold
    };
    public static int MenuButtonIcoId[] = {
            R.drawable.ic_menu_hard_ware,
            R.drawable.ic_menu_clud,
            R.drawable.ic_menu_my_topic,
            R.drawable.ic_menu_setting,
            R.drawable.menu_browse,
            R.drawable.menu_heart,
            R.drawable.back,
            R.drawable.go,
            R.drawable.menu_home,
            R.drawable.search,
            R.drawable.email,//10
            R.drawable.menu_31,
            R.drawable.menu_19,
            R.drawable.menu_21,
            R.drawable.menu_18,
            R.drawable.menu_1,
            R.drawable.menu_20,
            R.drawable.menu_26,
            R.drawable.msgs,
            R.drawable.menu_car,
            R.drawable.computer,
            R.drawable.camera,//21
            R.drawable.menu_app,
            R.drawable.menu_main,
            R.drawable.menu_music,
            R.drawable.menu_pay,
            R.drawable.menu_quit,
            R.drawable.menu_refrush,
            R.drawable.menu_scan,
            R.drawable.menu_share,
            R.drawable.menu_sky,//30
            R.drawable.menu_source,
            R.drawable.menu_store,
            R.drawable.menu_sub
    };
    public static String firstmenu = "{\"menu\":{\"button\":[{\"name\":\"新建\",\"type\":\"button\",\"key\":\"new\"}," +
            "{\"name\":\"刷新\",\"type\":\"button\",\"key\":\"refresh\"},{\"name\":\"排序\",\"type\":\"button\"," +
            "\"key\":\"order\"},{\"name\":\"返回\"," +
            "\"type\":\"button\",\"key\":\"exit\"}]}}";
    public static String secondmenu = "{\"menu\":{\"button\":[{\"name\":\"删除\",\"type\":\"button\"," +
            "\"key\":\"delete\"},{\"name\":\"复制\",\"type\":\"button\",\"key\":\"copy\"},{\"name\":\"剪切\"," +
            "\"type\":\"button\",\"key\":\"move\"}," +
            "{\"name\":\"更多\",\"key\":\"more\",\"sub_button\":[{\"name\":\"重命名\",\"type\":\"button\"," +
            "\"key\":\"rename\"}," +
            "{\"name\":\"属性\",\"type\":\"button\",\"key\":\"info\"},{\"name\":\"取消\",\"type\":\"button\",\"key\":\"cancle\"}]}]}}";
    public static String thirdmenu = "{\"menu\":{\"button\":[{\"name\":\"粘贴\",\"type\":\"button\",\"key\":\"paste\"}," +
            "{\"name\":\"新建\",\"type\":\"button\",\"key\":\"new\"},{\"name\":\"取消\",\"type\":\"button\"," +
            "\"key\":\"pastecancle\"}]}}";
    /**
     * 8000
     */
    public static final int REQUEST_UPDATE_HEAD_MENU = 8000;
    // 缓存大小
    public static final int UPLOAD_BUFFER = 1024 * 8;

    public static final String SAVE_INFORMATION = "save_information";
    public static final String SAVE_LOCALMSGNUM = "save_msg_num";

    public static final String time = "time";// 更新时间间隔

    public static final String DefaultServerIp = "e.tbs.com.cn";// 默认服务器ip
    public static final String DefaultServerPort = "1112";// 默认服务器端口
    public static final String DefaultLocalIp = "127.0.0.1";// 默认本地ip
    public static final String DefaultLocalPort = "6130";// 默认本地端口

    public static String[][] messageStr;
    public static String[][] timerStr;
    public static String[] menus;


    public static final String STATEFORSEARCH = "搜索";// campany email
    public static final String STATEFORDATAMANAGE = "数据管理";// campany email
    public static final String STATEFORCOLLECT = "我的收藏";// campany email

    // 刷新短信界面
    public static final String REFRESH_MESSAGE = "refresh_message";
    // 刷新好友列表
    public static final String REFRESH_ADDRESS = "refresh_address";
    public static final String WXhtml = "<p style='text-align: center;'><span style='font-size: 14px;'><img " +
            "src='https://mmbiz.qlogo" +
            ".cn/mmbiz/3oujmtfDrfIjfpQwgnn1icvGqHEMPaFF6U9gILCvJq2ep3TgsLVTpLrIRmzGkoIo48Zakvx0Ay5VDvpPASomGsA/0" +
            "?wx_fmt=jpeg'/><p style='font-size:12px;word-break: break-all;text-align:center;" +
            "margin-top:20px'>长按上图可识别二维码</p><p style='font-size:12px;word-break: break-all;text-align:center;" +
            "margin-top:''>关注 <a href='#' onclick='WeiXinAddContact('tbs-941012')' style='color:#0099cc'>TBS软件</a></p><p style='font-size:12px;word-break: break-all;text-align:center;margin-top:''>微信公众号: <a href='#' onclick='WeiXinAddContact('tbs-941012')' style='color:#0099cc'>tbs-941012</a></p>";

    public static final String SP_NAME = "misplayer";//SP文件名
    public static final String PLAY_POS = "playing_position";//播放位置
}
