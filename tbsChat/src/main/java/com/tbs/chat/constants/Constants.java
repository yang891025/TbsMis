package com.tbs.chat.constants;

import android.os.Environment;

import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.ebs.UserEbsEntity;
import com.tbs.chat.socket.Communication;

import java.util.ArrayList;

public class Constants {

	public static final String LAUNCHER_PREFERENCE = "launch_preferences";
	
	public static final String LAUNCHER_PREFERENCE_PATH = "//data//data//com.tbs//shared_prefs//launch_preferences.xml";
	
	public static final String SAVE_INFORMATION = "save_information";
	
	public static final String SAVE_INFORMATIONPATH = "//data//data//com.tbs//shared_prefs//save_information.xml";

	// SDCard路径
	public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	// 软件路径
	public static final String BASE_PATH = SD_PATH + "/Tbs";

	// 软件路径
	public static final String BASE_APP_PATH = BASE_PATH + "/Tbs-App";
	// 应用路径
	public static final String BASE_SOFT_PATH = BASE_PATH + "/Tbs-App/TbsChat/";

	// 配置文件存储目录
	public static final String CONFIG_PATH = BASE_APP_PATH + "/TbsChat/config";
	
	//ini文件路径
	public static final String CONFIG_INI_URL = CONFIG_PATH + "/TbsChat.ini";
	public static final String WEB_CONFIG_FILE_NAME = "TbsWeb.ini";
	public static final String NEWS_CONFIG_FILE_NAME = "TbsTbs.ini";

	// .properties文件路径
	public static final String CONFIG_URL = CONFIG_PATH + "/TbsChat.properties";

	// assets中配置文件目录
	public static final String ASSETS_CONFIG = "TbsChat/config/TbsChat.properties";
	
	// assets中配置文件目录
	public static final String ASSETS_CONFIG_INI = "TbsChat/config/TbsChat.ini";

	// 缓存图片路径
	public static final String CACHE_PATH = BASE_APP_PATH + "/TbsChat/cache";

	// Log文件路径
	public static final String LOG_PATH = BASE_APP_PATH + "/TbsChat/Log";

	// 图片存储路径
	public static final String IMG_PATH = BASE_APP_PATH + "/TbsChat/image";

	// socket连接状态
	public static final String SOCKET_CONNECTION = "socket_connection";
	
	// 退出软件
	public static final String EXIT_REGIST = "exit_regist";
	
	// 退出软件
	public static final String EXIT_ONLY_ACTIVITY = "exit_only_activity";
	
	// 退出软件
	public static final String EXIT_ACTIVITY = "exit_activity";
	
	// 退出软件
	public static final String EXIT_USER = "exit_user";
	
	// 打开对话框
	public static final String OPEN_EXIT_DIALOG = "open_exit_dialog";
	
	// 登录结果
	public static final String LOGIN_RESOULT = "login_resoult";
	
	// 短信验证码验证结果
	public static final String VERIFYREG_RESOULT = "verifyreg_resoult";
	
	// 发送短信验证码结果
	public static final String SENDVERIFYCODE_RESOULT = "sendverifycode_resoult";
	
	// 重复登录结果
	public static final String LOGIN_REPEAT_RESOULT = "login_repeat_resoult";
	
	// 注册结果
	public static final String REG_RESOULT = "reg_resoult";
	
	// 注册结果
	public static final String VERIFY_PHONE = "verify_phone";
	
	// 刷新短信界面
	public static final String REFRESH_MESSAGE = "refresh_message";
	
	// 取消消息通知气泡
	public static final String HIDE_MESSAGE_NOTIFY = "hide_message_notify";
	
	// 刷新短信界面
	public static final String REFRESH_MESSAGE_DETAIL = "refresh_message_detail";
	
	// 刷新好友列表
	public static final String REFRESH_ADDRESS = "refresh_address";
	
	// 注册结果
	public static final String SEND_NOTIFICATION = "send_notification";
	
	// 刷新短信界面
	public static final String SEARCH_FRIEND_RESOULT = "search_friend_resoult";
	
	// 短信发送成功广播
	public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	
	// 短信接收成功广播
	public static final String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
	
	// 接收短信广播标记
	public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	// 刷新头像
	public static final String FLUSH_HEAD = "flush_head";
	
	// 刷新昵称
	public static final String FLUSH_NICK = "flush_nick";
	
	// 刷新性别
	public static final String FLUSH_SEX = "flush_sex";
	
	// 刷新地区
	public static final String FLUSH_DIR = "flush_dir";
	
	// 更新地区
	public static final String UPDATE_DIR = "update_dir";
	
	// 更新地区
	public static final String UPDATE_PASSWORD = "update_password";
	
	// 关闭更新密码
	public static final String CLOSE_UPDATE_PASSWORD = "close_update_password";
	
	// 请求离线信息
	public static final String REQUEST_OFF_MESSAGE = "request_off_message";

	// 更新软件
	public static final String UPDATE_APK = "update_apk";
	
	// 更新软件
	public static final String FLUSH_DOWNLOAD_APK = "flush_download_apk";
	
	// 更新软件
	public static final String INSTALL_APK = "install_apk";
	
	// 启动定时器
	public static final String START_TIMER = "start_timer";
	
	// 启动定时器
	public static final String SOCKET_UNCONNECT = "socket_unconnect";
	
	// 重新登录
	public static final String RELOGIN_USER = "relogin_user";
	
	// 填充用户姓名
	public static final String UPDATE_FRIENDNIKE = "update_friendnike";
	
	// 设置ims账号状态
	public static final String SETTING_USERNUM_STATE = "setting_usernum_state";

	// 连接静态变量
	public static Communication con = null;
	
	// 拼音首字母集合
	public static ArrayList<String> pinyinFirst = new ArrayList<String>();

	public static UserEbsEntity userEbs = new UserEbsEntity();
	
	// 朋友实体类
	public static FriendEntity friend = new FriendEntity();
	
	// 是否登录的标记
	public static boolean isLogin = false;

	
	// ini配置文档标签
	public static final String EBSSERVER = "Login";
	public static final String URL = "ebsAddress";
	public static final String PORT = "ebsPort";

}
