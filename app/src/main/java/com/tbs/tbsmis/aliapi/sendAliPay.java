package com.tbs.tbsmis.aliapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.wxapi.Util;

import java.util.Map;

/**
 * Created by TBS on 2016/6/3.
 */
public class sendAliPay
{
    private final Activity activity;

    public sendAliPay(Activity activity) {
        this.activity = activity;
    }

    private static final int SDK_PAY_FLAG = 1;

    public void alipayReq(String orderId, String money) {
        IniFile IniFile = new IniFile();
        String webRoot = UIHelper.getShareperference(this.activity, constants.SAVE_INFORMATION,
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
            String dataPath = this.activity.getFilesDir().getParentFile()
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
        UIHelper.setSharePerference(this.activity, constants.SAVE_INFORMATION, "orderid", orderId);
        final String url = "http://" + walletAddress + ":" + wallet_port +
                "/TBS-PAY/pay/AlipayServlet?act=getOrderForAlipay&orderId=" + orderId +
                "&money=" + money + "&loginId=" + loginId;
        Runnable payRunnable = new Runnable()
        {

            @Override
            public void run() {
                //获取服务端加密订单信息
                byte[] buf = Util.httpGet(url);
                if (buf != null && buf.length > 0) {
                    String content = new String(buf);
                    // 构造PayTask 对象
                    PayTask alipay = new PayTask(sendAliPay.this.activity);
                    // 调用支付接口，获取支付结果
                    Map<String, String> result = alipay.payV2(content, true);
                    Message msg = new Message();
                    msg.what = sendAliPay.SDK_PAY_FLAG;
                    msg.obj = result;
                    sendAliPay.this.mHandler.sendMessage(msg);
                }
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler()
    {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case sendAliPay.SDK_PAY_FLAG:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    //System.out.println("resultStatus"+ resultStatus);
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Builder builder = new Builder(sendAliPay.this.activity);
                        builder.setTitle(R.string.app_tip);
                        builder.setMessage(sendAliPay.this.activity.getString(R.string.alipay_result_callback_msg, "成功"));
                        builder.setNegativeButton("OK", new OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent1 = new Intent();
                                intent1.setAction("pay" + sendAliPay.this.activity.getString(R.string.about_title));
                                intent1.putExtra("flag", 0);
                                sendAliPay.this.activity.sendBroadcast(intent1);
                                dialogInterface.dismiss();
                            }
                        }).show();

                        Toast.makeText(sendAliPay.this.activity, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        //System.out.println("resultStatus="+resultStatus);
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(sendAliPay.this.activity, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            Builder builder = new Builder(sendAliPay.this.activity);

                            builder.setTitle(R.string.app_tip);
                            builder.setMessage(sendAliPay.this.activity.getString(R.string.alipay_result_callback_msg,
                                    "失败"));
                            builder.setNegativeButton("关闭", new OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent1 = new Intent();
                                    intent1.setAction("pay" + sendAliPay.this.activity.getString(R.string.about_title));
                                    intent1.putExtra("flag", 1);
                                    sendAliPay.this.activity.sendBroadcast(intent1);
                                    dialogInterface.dismiss();

                                }
                            }).show();
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(sendAliPay.this.activity, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };
}
