package com.tbs.chat.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.RemoteViews;

import com.tbs.chat.R;
import com.tbs.chat.constants.Config;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.ui.conversation.MainTab;
import com.tbs.chat.util.Util;

public class SMSReceiver extends BroadcastReceiver {
	
	private static final String TAG = "SMSReceiver";
	
	private NotificationManager mNotificationManager;
	private Notification notification;
	private RemoteViews remoteviews;
	private DBUtil dao = null;
	
	private String sendTime = null;
	private String phone = null;
	private String name = null;
	private String msg = null;
	private String username = null;
	private String password = null;
	private String url = null;
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
			//实例化数据库工具类
			dao = DBUtil.getInstance(context);
			//实例化stringbuffer
			StringBuilder sb = new StringBuilder();
			// 接收由SMS传过来的数据
			Bundle bundle = intent.getExtras();
			// 判断是否有数据
			if (bundle != null) {
				// 通过pdus可以获得接收到的所有短信消息
				Object[] objArray = (Object[]) bundle.get("pdus");
				/* 构建短信对象array,并依据收到的对象长度来创建array的大小 */
				SmsMessage[] messages = new SmsMessage[objArray.length];
				for (int i = 0; i < objArray.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) objArray[i]);
				}
				/* 将送来的短信合并自定义信息于StringBuilder当中 */
				for (SmsMessage currentMessage : messages) {
					phone = currentMessage.getDisplayOriginatingAddress();// 获得接收短信的电话号码
					Log.d(TAG, "phone:"+phone);
					// String phoneThree = phone.trim().substring(0,
					// 3);//截取钱三位字符串
					// if(phoneThree != null &&
					// phoneThree.equals("+86")){//判断是不是+86,如果是+86截取后面的字符串
					// phone = phone.substring(3, phone.length());
					// }
					/*
					 * 将取到的电话直接传入
					 * 通过国家代码和电话拼接做查询
					 */
					FriendEntity friend = Util.detailFriends(context, phone);//查询是否存在好友，否则不拦截短信
					if(friend != null){
						msg = currentMessage.getDisplayMessageBody();// 获得短信的内容
						Date date = new Date(currentMessage.getTimestampMillis());
						SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
						sendTime = format.format(date);// 获取短信发送时间；
						//将内容保存到数据库
						MessageEntity chatMessage = new MessageEntity(
								friend.getUserID(), friend.getFriendID(),
								Config.MESSAGE_FROM, Config.MESSAGE_TYPE_SMS,
								sendTime, msg, 1, "", "", "", "", "");
						// 向数据库插入数据
						ContentValues values = new ContentValues();
						values.put("self_id", friend.getUserID());
						values.put("friend_id", friend.getFriendID());
						values.put("direction", Config.MESSAGE_FROM);
						values.put("type", Config.MESSAGE_TYPE_SMS);
						values.put("content", msg);
						values.put("time", sendTime);
						values.put("read_type", 1);
						//long id = Util.insertMessage(context, values);
						dao.insertMessage(context, values);
						Bundle b = new Bundle();
						b.putSerializable("chatMessage", chatMessage);
						Intent message = new Intent(Constants.REFRESH_MESSAGE);
						message.putExtras(bundle);
						context.sendBroadcast(message);
						
						Intent message_detail = new Intent(Constants.REFRESH_MESSAGE_DETAIL);
						message_detail.putExtras(bundle);
						context.sendBroadcast(message_detail);
						
						
						//设置notification的界面
						mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//获取系统服务
						notification = new Notification(android.R.drawable.ic_dialog_email, "您有新的短消息!",System.currentTimeMillis());//设置通知icon，tiltle
						remoteviews = new RemoteViews(context.getPackageName(), R.layout.mynotiifcation);
						remoteviews.setImageViewResource(android.R.drawable.ic_dialog_email, android.R.drawable.ic_dialog_email);
						notification.contentView = remoteviews;
						
						// PendingIntent
						Intent mainIntent = new Intent(context, MainTab.class);
						PendingIntent pendingintent = PendingIntent.getActivity(context, 0,mainIntent, 0);// 调用的系统的安装隐士意图 后面红色的代码
						notification.contentIntent = pendingintent;
						
						mNotificationManager.notify(8088, notification);//刷新通知界面
					}
				}
			}
		}
	}
}
