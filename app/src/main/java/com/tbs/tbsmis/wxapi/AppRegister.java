package com.tbs.tbsmis.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tbs.tbsmis.constants.constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);

		// ����appע�ᵽ΢��
		msgApi.registerApp(constants.APP_ID);
	}
}
