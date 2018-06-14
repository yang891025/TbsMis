package com.tbs.chat.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.tbs.chat.constants.Constants;
import com.tbs.chat.socket.Communication;

public class HttpServer extends Service {
	
	private static final String TAG = "HttpServer";
	
	private Timer timer = null;
	//private boolean flag = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "Communication is start");
		timer = new Timer(true);
		timer.schedule(task,1, 10000); //延时1000ms后执行，1000ms执行一次
	}

	@Override
	public void onDestroy() {
		timer.cancel(); //退出计时器
	}
	
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if(Constants.con == null){
					Log.d(TAG, "HttpServer handler 1 is run");
					Constants.con = Communication.newInstance(getApplication());	
				}else if(Constants.con != null && !Constants.con.getTransportWorker().isOnSocket()){
					Log.d(TAG, "HttpServer handler 2 is run");
					Constants.con.setInstanceNull();
					Constants.con = Communication.newInstance(getApplication());
				} else if(Constants.con != null && Constants.con.getTransportWorker().isOnSocket()) {
					Log.d(TAG, "HttpServer handler 3 is run");
					
					Intent RELOGIN_USER = new Intent(Constants.RELOGIN_USER);
					getApplication().sendBroadcast(RELOGIN_USER);
					
					stopSelf();
				}
				break;
			}
		}
	};
}
