package com.tbs.chat.constants;

public interface Config {
	int MESSAGE_FROM = -2;//锟斤拷息锟斤拷锟斤拷锟斤拷
	int MESSAGE_TO = -1;//锟斤拷息锟斤拷锟酵革拷
	int MESSAGE_TYPE_TXT = 0;//锟侥憋拷锟斤拷息
	int MESSAGE_TYPE_IMG = 1;//图片锟斤拷息
	int MESSAGE_TYPE_AUDIO = 2;//锟斤拷频锟斤拷息
	int MESSAGE_TYPE_ADD_FRIEND = 3;//锟斤拷雍锟斤拷锟斤拷锟较�
	int MESSAGE_TYPE_VADIO = 4;//锟斤拷频锟斤拷息
	int MESSAGE_TYPE_SMS = 5;//锟斤拷锟斤拷锟斤拷息
	int MESSAGE_TYPE_IMS = 6;//ims锟斤拷息
	int REQUEST_LOGIN = 13;//锟斤拷录锟斤拷锟斤拷
	int REQUEST_REGIST = 14;//注锟斤拷锟斤拷锟斤拷
	int REQUEST_UPDATE_HEAD = 15;//锟斤拷锟斤拷头锟斤拷锟斤拷锟斤拷
	int REQUEST_ADD_FRIEND = 16;//锟斤拷雍锟斤拷锟斤拷锟斤拷锟�
	int REQUEST_SEND_TXT = 17;//锟斤拷锟斤拷锟侥憋拷锟斤拷锟斤拷
	int REQUEST_SEND_IMG = 18;//锟斤拷锟斤拷图片锟斤拷锟斤拷
	int REQUEST_SEND_AUDIO = 19;//锟斤拷锟斤拷 锟斤拷频锟斤拷锟斤拷
	int REQUEST_GET_OFFLINE_MSG = 20;//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷息
	int REQUEST_GET_FRIENDS = 21;//锟斤拷锟斤拷锟斤拷锟�
	int REQUEST_SEARCH_USER = 22;//锟斤拷锟斤拷锟斤拷液锟斤拷锟�
	int REQUEST_EXIT = 23;//锟斤拷锟斤拷锟剿筹拷
	int REQUEST_GET_HEAD = 24;//锟斤拷锟斤拷头锟斤拷
	int REQUEST_GET_USER = 25;//锟斤拷锟斤拷锟矫伙拷
	int REQUEST_UPDATE_NICK = 26;//锟斤拷锟斤拷锟斤拷锟斤拷浅锟�
	int REQUEST_UPDATE_GENDER = 27;//锟斤拷锟斤拷锟斤拷锟斤拷员锟�
	int REQUEST_UPDATE_PASSWORD = 28;//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
	int REQUEST_EXIT_USER = 29;//锟斤拷锟斤拷锟剿筹拷锟矫伙拷
	int REQUEST_UPDATE_DIR = 30;// 锟斤拷锟斤拷锟斤拷碌锟斤拷锟�
	int REQUEST_VERIFY_PHONE = 31;// 锟斤拷锟斤拷锟斤拷证锟界话
	//public static final int REQUEST_PUSH_USER = 32;// 锟斤拷锟斤拷锟斤拷锟矫伙拷
    int REQUEST_SEND_SUGGEST = 33;// 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
	int REQUEST_GET_VERSION = 34;// 锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷姹撅拷锟斤拷锟�
	int REQUEST_GET_DOWNLOAD = 35;// 锟斤拷锟截革拷锟斤拷apk
	int REQUEST_GET_FRIENDS_INFO = 36;//锟斤拷锟斤拷锟斤拷锟�
	int REQUEST_VERIFY_NUMBER = 37;// 锟斤拷证锟街伙拷锟斤拷证锟斤拷
	int REQUEST_SETTING_USERNUM = 38;// 锟斤拷证锟街伙拷锟斤拷证锟斤拷
	
	
	int RESULT_LOGIN_ERRO = 97;//锟斤拷录锟届常锟斤拷锟�
	int RESULT_LOGIN_SUCCESS = 98;//锟斤拷录锟缴癸拷锟斤拷锟�
	int RESULT_LOGIN_UNSUCCESS = 99;//锟斤拷录锟斤拷锟缴癸拷锟斤拷锟�
	int RESULT_LOGIN = 100;//锟斤拷录
	int RESULT_REGIST = 101;
	int RESULT_UPDATE_HEAD = 102;
	int RESULT_GET_OFFLINE_MSG = 103;
	int RESULT_GET_FRIENDS = 104;
	int RESULT_SEARCH_USER = 105;
	int RESULT_GET_HEAD = 106;
	int RESULT_GET_USER = 107;
	int RESULT_EXIT_USER = 108;
	int RESULT_VERIFY_PHONE = 109;
	int RESULT_REPEAT_LOGIN = 110;//锟截革拷锟斤拷录
	int RESULT_GET_VERSION = 111;
	int RESULT_GET_DOWNLOAD = 112;
	int RESULT_GET_FRIENDS_INFO = 113;
	int RESULT_SETTING_USERNUM = 114;
	
	
	
	int RECEIVE_TEXT = 500;
	int RECEIVE_AUDIO = 501;
	int RECEIVE_IMG = 502;
	int RECEIVE_MESSAGE = 503;
	int IMG_NEED_UPDATE = 600;
	int IMG_NO_UPDATE = 601;
	
	
	int LOGIN_SUCCESS = 1000;
	int LOGIN_FAILED = 1001;
	int RIGEST_SUCCESS = 1002;
	int RIGEST_FAILED = 1003;
	int SEARCH_USER_SUCCESS = 1004;
	int SEARCH_USER_FALSE = 1005;
	int USER_HAS_IMG = 1006;
	int USER_NOT_IMG = 1007;
	int ADD_FRIEND = 1008;
	int REGIST_HAS_IMG = 1009;
	int EXIT_USER_SUCCESS = 1010;
	int EXIT_USER_UNSUCCESS = 1011;
	int VERIFY_PHONE_SUCCESS = 1012;
	int VERIFY_PHONE_UNSUCCESS = 1013;
	int VERIFY_PHONE_SUCCESS_EXIST = 1014;
	int BACK_CURRENT_ACTIVITY = 1015;
	int SETTING_USERNUM_SUCCESS = 1016;
	int SETTING_USERNUM_EXIST = 1017;
	
	int SEND_NOTIFICATION = 2000;
	int COONECT_SERVER_SUCCESS = 3000;
	/**
	 * 8000锟斤拷锟斤拷为menuResoult
	 */
    int REQUEST_UPDATE_HEAD_MENU = 8000;
	/**
	 * 9000锟斤拷锟斤拷为onActivityResoult
	 */
    int REQUEST_UPDATE_GENDER_ONACTIVITYRESOULT = 9000;
	int REQUEST_UPDATE_HEAD_ONACTIVITYRESOULT = 9001;
	int REQUEST_UPDATE_DIR_ONACTIVITYRESOULT = 9002;
	
	String UPDATE_PASSWORD_SUCCESS = "锟睫革拷锟斤拷锟斤拷晒锟�";

}
