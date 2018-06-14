package com.tbs.tbsmis.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler
{

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pay_result);

        this.api = WXAPIFactory.createWXAPI(this, constants.APP_ID);
        this.api.handleIntent(this.getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        this.api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onResp(BaseResp resp) {
        Log.d(WXPayEntryActivity.TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (0 == resp.errCode) {
                Builder builder = new Builder(this);
                builder.setTitle(R.string.app_tip);
                builder.setMessage(this.getString(R.string.pay_result_callback_msg, "成功"));
                builder.setNegativeButton("OK", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent();
                        intent1.setAction("pay" + getString(R.string.about_title));
                        intent1.putExtra("flag", 0);
                        WXPayEntryActivity.this.sendBroadcast(intent1);
                        dialogInterface.dismiss();
                        WXPayEntryActivity.this.finish();

                    }
                }).show();
            } else {
                Builder builder = new Builder(this);

                builder.setTitle(R.string.app_tip);
                builder.setMessage(this.getString(R.string.pay_result_callback_msg, "失败"));
                builder.setNegativeButton("关闭", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent();
                        intent1.setAction("pay" + getString(R.string.about_title));
                        intent1.putExtra("flag", 1);
                        WXPayEntryActivity.this.sendBroadcast(intent1);
                        dialogInterface.dismiss();
                        WXPayEntryActivity.this.finish();
                    }
                }).show();
            }
        }
    }
}