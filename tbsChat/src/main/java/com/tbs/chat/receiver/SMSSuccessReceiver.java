package com.tbs.chat.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.tbs.chat.constants.Constants;
import com.tbs.chat.database.DataBaseUtil;
import com.tbs.chat.database.table.MESSAGE_TABLE;

public class SMSSuccessReceiver extends BroadcastReceiver {

	private static final String TAG = "SMSSuccessReceiver";
	Intent intent;
	
	String autoId = null;//返回给服务器的id
	String resoultId = "";//添加到短信数据表中
	String returnBufferId = "";//删除缓冲表中数据
	String resoultId2 = "";//添加到短信数据表中
	String returnBufferId2 = "";//删除缓冲表中数据
	
	private ContentValues values;
	private int resoult;
	private int delResoult;
	
	String username;
	String password;
	String url;
	
	public SMSSuccessReceiver(){
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent.getAction().startsWith(Constants.SENT_SMS_ACTION)) {
			String[] resoultValue = intent.getAction().substring(Constants.SENT_SMS_ACTION.length(), intent.getAction().length()).split(",");
			resoultId = resoultValue[0];
			returnBufferId = resoultValue[1];

			Log.d(TAG, "SENT_SMS_ACTION");
			switch (getResultCode()) {
			case Activity.RESULT_OK:// 发送短信成功
				Log.d(TAG, "SENT_SMS_ACTION RESULT_OK");
				values = new ContentValues();
				values.put("read_type", "1");
				//更新短信状态
				resoult = new DataBaseUtil(context).getDataBase().update(MESSAGE_TABLE.TABLE_NAME, values, "_id = ?", new String[] { "" + resoultId });
				// 这里处理短信记录内容
				values.clear();
				Toast.makeText(context, "短信发送成功", Toast.LENGTH_LONG).show();
				break;

			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:// 发送短信失败
				Log.d(TAG, "SENT_SMS_ACTION RESULT_ERROR_GENERIC_FAILURE");
				// 短信失败后的操作
				values = new ContentValues();
				values.put("read_type", "2");
				//更新短信状态
				resoult = new DataBaseUtil(context).getDataBase().update(MESSAGE_TABLE.TABLE_NAME, values, "_id = ?", new String[] { "" + resoultId });
				// 这里处理短信记录内容
				values.clear();
				Toast.makeText(context, "短信发送失败", Toast.LENGTH_LONG).show();
				break;

			case SmsManager.RESULT_ERROR_RADIO_OFF:
				break;

			case SmsManager.RESULT_ERROR_NULL_PDU:
				break;
			}
		}
		if (intent.getAction().startsWith(Constants.DELIVERED_SMS_ACTION)) {// 短信送达后的广播
			String[] resoultValue = intent.getAction().substring(Constants.DELIVERED_SMS_ACTION.length(), intent.getAction().length()).split(",");
			resoultId2 = resoultValue[0];
			returnBufferId2 = resoultValue[1];

			Log.d(TAG, "DELIVERED_SMS_ACTION");
			switch (getResultCode()) {
			case Activity.RESULT_OK:// 短信已送达
				// 短信送达后的操作
				values = new ContentValues();
				values.put("read_type", "3");
				resoult = new DataBaseUtil(context).getDataBase().update(MESSAGE_TABLE.TABLE_NAME, values, "_id = ?", new String[] { "" + resoultId2 });
				Toast.makeText(context, "短信已送达", Toast.LENGTH_LONG).show();
				break;

			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:// 短信未送达
				// 短信未送达后的操作
				break;

			case SmsManager.RESULT_ERROR_RADIO_OFF:
				break;

			case SmsManager.RESULT_ERROR_NULL_PDU:
				break;
			}
			context.unregisterReceiver(this);
		}
	}
}