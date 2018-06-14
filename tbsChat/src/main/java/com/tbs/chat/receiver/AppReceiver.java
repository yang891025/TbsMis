package com.tbs.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.util.LogUtil;

public class AppReceiver extends BroadcastReceiver {

	private static final String TAG = "AppReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.SOCKET_CONNECTION.equals(intent.getAction())) {
			boolean isConnect = intent.getBooleanExtra("isConnect", false);
			if (isConnect) {
                LogUtil.record("---------------------连接成功----------------------");
			} else {
                LogUtil.record("---------------------连接失败----------------------");
			}
		}
	}
}
