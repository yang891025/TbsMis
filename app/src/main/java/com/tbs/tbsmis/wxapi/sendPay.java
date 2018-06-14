package com.tbs.tbsmis.wxapi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

/**
 * Created by TBS on 2016/5/25.
 */
public class sendPay
{
    private final IWXAPI api;
    private final Context context;

    public sendPay(Context context) {
        this.context = context;
        this.api = WXAPIFactory.createWXAPI(this.context, constants.APP_ID);
        // 将该app注册到微信
        this.api.registerApp(constants.APP_ID);
    }

    public void payReq(String orderId, String money) {
        IniFile IniFile = new IniFile();
        String webRoot = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                "Path", "");
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        String appTestFile = webRoot
                + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        String userIni = appTestFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = this.context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String loginId = IniFile.getIniString(userIni, "Login",
                "LoginId", "", (byte) 0);
        String walletAddress = IniFile.getIniString(userIni,
                "Wallet", "walletAddress", constants.DefaultServerIp, (byte) 0);
        String wallet_port = IniFile.getIniString(userIni,
                "Wallet", "walletPort", "8083", (byte) 0);
        UIHelper.setSharePerference(this.context, constants.SAVE_INFORMATION, "orderid", orderId);
        final String url = "http://" + walletAddress + ":" + wallet_port +
                "/TBS-PAY/pay/WeixinServlet?act=getOrderForWeixin&orderId=" + orderId +
                "&money=" + money + "&loginId=" + loginId;
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    byte[] buf = Util.httpGet(url);
                    if (buf != null && buf.length > 0) {
                        String content = new String(buf);
                        //Log.e("get server pay params:", content);
                        JSONObject json = new JSONObject(content);
                        if (null != json && !json.has("retcode")) {
                            PayReq req = new PayReq();
                            // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
                            req.appId = json.getString("appid");
                            req.partnerId = json.getString("partnerid");
                            req.prepayId = json.getString("prepayid");
                            req.nonceStr = json.getString("noncestr");
                            req.timeStamp = json.getString("timestamp");
                            req.packageValue = json.getString("package");
                            req.sign = json.getString("sign");
                            req.extData = "app data"; // optional
                            msg.what = 1;
                            msg.obj = "正常调起支付";
                            sendPay.this.mHandler.sendMessage(msg);
                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                            sendPay.this.api.sendReq(req);
                        } else {
                            Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
                            msg.what = 2;
                            msg.obj = "返回错误" + json.getString("retmsg");
                            sendPay.this.mHandler.sendMessage(msg);
//                                    Toast.makeText(PayActivity.this,
//                                            "返回错误" + json.getString("retmsg"),
//                                            Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("PAY_GET", "服务器请求错误");
                        msg.what = 3;
                        msg.obj = "服务器请求错误";
                        sendPay.this.mHandler.sendMessage(msg);
//                                Toast.makeText(PayActivity.this, "服务器请求错误",
//                                        Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("PAY_GET", "异常：" + e.getMessage());
                    msg.what = 4;
                    msg.obj = "异常：" + e.getMessage();
                    sendPay.this.mHandler.sendMessage(msg);
//                            Toast.makeText(PayActivity.this, "异常：" + e.getMessage(),
//                                    Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(sendPay.this.context, msg.obj.toString(),
                    Toast.LENGTH_SHORT).show();

        }
    };

}
