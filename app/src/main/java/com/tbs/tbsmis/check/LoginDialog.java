package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.DynamicLoginActivity;
import com.tbs.tbsmis.activity.FindPasswordActivity;
import com.tbs.tbsmis.activity.MyPageActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.util.DES;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 路径选择弹窗
 *
 * @author yeguozhong@yeah.net 修改 yzt
 */
public class LoginDialog extends Dialog
{

    private static IniFile IniFile;
    private static String appTestFile;
    private final Context context;
    private LinearLayout dynamicTag;
    private TextView dynamicTxt;
    private EditText userEt;
    private EditText passwordEt;
    private ImageView iv;
    private CheckBox rememberCheckBox;
    private Button findPwdBtn;
    private Button dynamicBtn;
    private Button loginBtn;
    private Button loginBack;
    private Button loginOut;
    private AnimationDrawable loadingAnima;
    private TextView login_title;
    private LinearLayout account_sm_layout;
    private RelativeLayout account_user_set;
    private RelativeLayout account_user_set2;
    private RelativeLayout user_setup;
    private TextView username;
    private TextView accountname;
    private ImageView user_imageview;
    private Bitmap bitmap;
    private String webRoot;
    private String userIni;

    public LoginDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.layout_sapi_login_sm);
        this.setCanceledOnTouchOutside(false);
        this.init();
    }

    private void init() {
        // TODO Auto-generated method stub
        this.dynamicTag = (LinearLayout) this.findViewById(R.id.account_tip);
        this.account_sm_layout = (LinearLayout) this.findViewById(R.id.account_sm_layout);
        this.account_user_set = (RelativeLayout) this.findViewById(R.id.account_user_set);
        this.account_user_set2 = (RelativeLayout) this.findViewById(R.id.account_user_set2);
        this.user_setup = (RelativeLayout) this.findViewById(R.id.user_setup);
        this.dynamicTxt = (TextView) this.findViewById(R.id.account_error_text);
        this.login_title = (TextView) this.findViewById(R.id.login_title);
        this.iv = (ImageView) this.findViewById(R.id.account_loading);
        this.loginBtn = (Button) this.findViewById(R.id.user_login);
        this.loginBack = (Button) this.findViewById(R.id.user_back);
        this.loginOut = (Button) this.findViewById(R.id.user_loginOut);
        this.dynamicTag.setVisibility(View.GONE);
        this.iniPath();
        if (Integer.parseInt(LoginDialog.IniFile.getIniString(this.userIni, "Login",
                "LoginFlag", "0", (byte) 0)) == 0) {
            this.loginOut.setEnabled(false);
            this.login_title.setText("未登录");
            this.user_setup.setVisibility(View.GONE);
            this.userEt = (EditText) this.findViewById(R.id.account);
            this.passwordEt = (EditText) this.findViewById(R.id.account_password);
            this.rememberCheckBox = (CheckBox) this.findViewById(R.id.account_remember_checkBox);
            this.findPwdBtn = (Button) this.findViewById(R.id.account_forget_password);
            this.dynamicBtn = (Button) this.findViewById(R.id.account_dynamic_login);
            this.userEt.setText(LoginDialog.IniFile.getIniString(this.userIni, "Login",
                    "UserName", "", (byte) 0));
            this.userEt.setSelection(this.userEt.getText().toString().length());// 光标位置
            if (Integer.parseInt(LoginDialog.IniFile.getIniString(this.userIni, "Login",
                    "rememberPwd", "0", (byte) 0)) == 1) {
                this.rememberCheckBox.setChecked(true);
                this.passwordEt.setText(LoginDialog.IniFile.getIniString(this.userIni, "Login",
                        "PassWord", "", (byte) 0));
            }
            this.account_user_set.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    if (LoginDialog.this.rememberCheckBox.isChecked()) {
                        LoginDialog.this.rememberCheckBox.setChecked(false);
                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                "rememberPwd", "0");
                    } else {
                        LoginDialog.this.rememberCheckBox.setChecked(true);
                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                "rememberPwd", "1");
                    }

                }
            });
            this.findPwdBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(LoginDialog.this.context, FindPasswordActivity.class);
                    LoginDialog.this.context.startActivity(intent);
                    LoginDialog.this.dismiss();
                }
            });
            this.dynamicBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(LoginDialog.this.context, DynamicLoginActivity.class);
                    LoginDialog.this.context.startActivity(intent);
                    LoginDialog.this.dismiss();
                }

            });
        } else {
            this.login_title.setText("已登录");
            this.loginBtn.setEnabled(false);
            this.account_sm_layout.setVisibility(View.GONE);
            this.account_user_set.setVisibility(View.GONE);
            this.account_user_set2.setVisibility(View.GONE);
            this.username = (TextView) this.findViewById(R.id.usename_view);
            this.accountname = (TextView) this.findViewById(R.id.account_view);
            this.user_imageview = (ImageView) this.findViewById(R.id.user_imageview);
            String nickName = LoginDialog.IniFile.getIniString(this.userIni, "Login",
                    "NickName", "匿名", (byte) 0);
            if (StringUtils.isEmpty(nickName)) {
                this.username.setText("匿名");
            } else {
                this.username.setText(nickName);
            }
            if (StringUtils.isEmpty(LoginDialog.IniFile.getIniString(this.userIni, "Login",
                    "UserCode", "", (byte) 0))) {
                this.accountname.setText("账号:"
                        + LoginDialog.IniFile.getIniString(this.userIni, "Login", "Account",
                        "", (byte) 0));
            } else {
                this.accountname.setText("账号:"
                        + LoginDialog.IniFile.getIniString(this.userIni, "Login", "UserCode",
                        "", (byte) 0));
            }
            String headPath = "http://"
                    + LoginDialog.IniFile.getIniString(this.userIni, "Login",
                    "ebsAddress", constants.DefaultServerIp, (byte) 0)
                    + ":"
                    + LoginDialog.IniFile.getIniString(this.userIni, "Login", "ebsPort",
                    "8083", (byte) 0)
                    + LoginDialog.IniFile.getIniString(this.userIni, "Login", "ebsPath",
                    "/EBS/UserServlet", (byte) 0)
                    + "?act=downloadHead&account="
                    + LoginDialog.IniFile.getIniString(this.userIni, "Login", "Account", "",
                    (byte) 0);
            ImageLoader imageLoader = new ImageLoader(this.context, R.drawable.default_avatar);
            imageLoader.DisplayImage(headPath, this.user_imageview);
        }
        this.loginBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (LoginDialog.this.bitmap != null) {
                    LoginDialog.this.bitmap.recycle();
                }
                LoginDialog.this.dismiss();
            }
        });
        this.loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoginDialog.this.connect(1);
            }
        });
        this.loginOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoginDialog.this.connect(2);
            }
        });
        this.user_setup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginDialog.this.context, MyPageActivity.class);
                LoginDialog.this.context.startActivity(intent);
                if (LoginDialog.this.bitmap != null) {
                    LoginDialog.this.bitmap.recycle();
                }
                LoginDialog.this.dismiss();
            }

        });
    }

    /*
     * ini文件读写参数初始化
     */
    private void iniPath() {
        LoginDialog.IniFile = new IniFile();
        this.webRoot = UIHelper.getSoftPath(context);
        if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
        }
        this.webRoot += this.context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        this.webRoot = UIHelper.getShareperference(this.context,
                constants.SAVE_INFORMATION, "Path", this.webRoot);
        if (this.webRoot.endsWith("/") == false) {
            this.webRoot += "/";
        }
        String WebIniFile = this.webRoot + constants.WEB_CONFIG_FILE_NAME;
        LoginDialog.appTestFile = this.webRoot
                + LoginDialog.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = LoginDialog.appTestFile;
        if (Integer.parseInt(LoginDialog.IniFile.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = this.context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
    }

    public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.iv.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        this.loadingAnima.stop();
        this.iv.setVisibility(View.INVISIBLE);
    }

    public Map<String, String> login() {
        String pmt = DES.encrypt("password=" + this.passwordEt.getText());
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "login");
        params.put("account", this.userEt.getText().toString());
        params.put("clientId", UIHelper.DeviceMD5ID(this.context));
        params.put("pmt", pmt);
        return params;
    }

    public Map<String, String> LoginQuit() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "logOut");
        params.put("clientId", UIHelper.DeviceMD5ID(this.context));
        params.put("loginId", LoginDialog.IniFile.getIniString(this.userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler1 = new Handler()
    {
        @Override
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LoginDialog.this.stopAnimation();
                    break;
                case 2:
                    LoginDialog.this.dynamicTag.setVisibility(View.VISIBLE);
                    LoginDialog.this.dynamicTxt.setText(msg.getData().getString("message"));
                    break;
                case 3:
                    LoginDialog.this.dynamicTag.setVisibility(View.VISIBLE);
                    LoginDialog.this.dynamicTxt.setText(R.string.sapi_login_error);
                    break;
                case 4:
                    LoginDialog.this.dynamicTag.setVisibility(View.VISIBLE);
                    LoginDialog.this.dynamicTxt.setText(R.string.sapi_login_error_msg);
                    break;
            }
        }
    };

    private void connect(int count) {
        LoginDialog.MyAsyncTask task = new LoginDialog.MyAsyncTask(this.context, count);
        task.execute();
    }

    @SuppressLint("ShowToast")
    class MyAsyncTask extends AsyncTask<String, Integer, String>
    {

        Context context;
        int count;
        private String user;
        private String retStr;
        private String retLogid;

        public MyAsyncTask(Context c, int count) {
            context = c;
            this.count = count;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            LoginDialog.this.startAnimation();
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected String doInBackground(String... params) {
            LoginDialog.this.iniPath();
            HttpConnectionUtil connection = new HttpConnectionUtil();
            constants.verifyURL = "http://"
                    + LoginDialog.IniFile.getIniString(LoginDialog.this.userIni, "Login",
                    "ebsAddress", constants.DefaultServerIp, (byte) 0)
                    + ":"
                    + LoginDialog.IniFile.getIniString(LoginDialog.this.userIni, "Login", "ebsPort",
                    "8083", (byte) 0)
                    + LoginDialog.IniFile.getIniString(LoginDialog.this.userIni, "Login", "ebsPath",
                    "/EBS/UserServlet", (byte) 0);
            if (this.count == 1) {
                return connection.asyncConnect(constants.verifyURL, LoginDialog.this.login(),
                        HttpConnectionUtil.HttpMethod.POST, this.context);
            } else if (this.count == 2) {
                return connection.asyncConnect(constants.verifyURL,
                        LoginDialog.this.LoginQuit(), HttpConnectionUtil.HttpMethod.POST, this.context);
            }
            return null;
        }

        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(String result) {
            Message msg = new Message();
            msg.what = 1;
            LoginDialog.this.mHandler1.sendMessage(msg);
            ServiceManager serviceManager;
            switch (this.count) {
                case 2:
                    // 登陆标志位下线
//                this.context.stopService(new Intent(this.context
//						.getString(string.ServerName1)));
                    Intent mIntent = new Intent();
                    mIntent.setAction(context
                            .getString(R.string.ServerName1));//你定义的service的action
                    mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                    context.stopService(mIntent);
                    SharedPreferences pre = this.context.getSharedPreferences(
                            Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                    if (pre.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
                            true)) {
                        serviceManager = new ServiceManager(this.context);
                        serviceManager.EBSLogout();
                    }
                    LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login", "LoginFlag", "0");
                    Toast.makeText(this.context, "已退出登录", Toast.LENGTH_SHORT).show();
                    if (LoginDialog.this.bitmap != null) {
                        LoginDialog.this.bitmap.recycle();
                    }
                    LoginDialog.this.dismiss();
                    break;
                default:
                    // stopAnimation();
                    if (result != null) {
                        JSONObject json;
                        try {
                            json = new JSONObject(result);
                            this.retStr = json.getString("msg");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            this.retStr = result;
                        }
                        try {
                            json = new JSONObject(result);
                            this.retLogid = json.getString("loginId");
                            this.user = json.getString("user");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            msg = new Message();
                            msg.what = 4;
                            LoginDialog.this.mHandler1.sendMessage(msg);
                        }
                        if (this.retStr != null && !StringUtils.isEmpty(this.retLogid)) {
                            if (this.user != null) {
                                try {
                                    if (this.retStr.equals("登录成功")) {
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "UserName", LoginDialog.this.userEt.getText()
                                                        .toString());
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "PassWord", LoginDialog.this.passwordEt.getText()
                                                        .toString());
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "LoginId", this.retLogid);
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "LoginFlag", "1");
                                        json = new JSONObject(this.user);
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "NickName",
                                                json.getString("userName"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "newEMail",
                                                json.getString("newEMail"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Mobile", json.getString("mobile"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Contact", json.getString("email"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Account",
                                                json.getString("account"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "UserCode",
                                                json.getString("userCode"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Signature",
                                                json.getString("idiograph"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Sex", json.getString("sex"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Address", json.getString("myURL"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Location_province",
                                                json.getString("province"));
                                        LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni, "Login",
                                                "Location_city",
                                                json.getString("city"));
                                        if (StringUtils.isEmpty(json
                                                .getString("userCodeModifyNum"))) {
                                            LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni,
                                                    "Login", "AccountFlag", "0");
                                        } else {
                                            LoginDialog.IniFile.writeIniString(LoginDialog.this.userIni,
                                                    "Login", "AccountFlag", "1");
                                        }
                                        if (UIHelper
                                                .getShareperference(
                                                        this.context,
                                                        Constants.SHARED_PREFERENCE_NAME,
                                                        Constants.SETTINGS_NOTIFICATION_ENABLED,
                                                        true)) {
                                            serviceManager = new ServiceManager(
                                                    this.context);
                                            serviceManager
                                                    .setNotificationIcon(R.drawable.notification);
                                            serviceManager.setUserInfo(LoginDialog.IniFile
                                                    .getIniString(LoginDialog.this.userIni,
                                                            "Login", "Account", "",
                                                            (byte) 0), DES
                                                    .encrypt(LoginDialog.IniFile.getIniString(
                                                            LoginDialog.this.userIni, "Login",
                                                            "PassWord", "",
                                                            (byte) 0)), LoginDialog.IniFile
                                                    .getIniString(LoginDialog.this.userIni,
                                                            "Login", "LoginId", "",
                                                            (byte) 0));
                                            serviceManager.restartService();
                                        }
                                        if (Integer.parseInt(LoginDialog.IniFile.getIniString(userIni, "Login", "UserUpate", "1", (byte) 0)) == 1) {
//                                        this.context.startService(new Intent(
//                                                this.context.getString(string.ServerName1)));
                                            Intent intent = new Intent();
                                            intent.setAction(context
                                                    .getString(R.string.ServerName1));//你定义的service的action
                                            intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                            context.startService(intent);
                                        }
                                        Toast.makeText(this.context,
                                                R.string.sapi_login_success,
                                                Toast.LENGTH_SHORT).show();
                                        LoginDialog.this.dismiss();

                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            msg = new Message();
                            msg.what = 2;
                            Bundle data = new Bundle();
                            data.putString("message", this.retStr);
                            msg.setData(data);
                            LoginDialog.this.mHandler1.sendMessage(msg);
                        }
                    } else {
                        msg = new Message();
                        msg.what = 3;
                        LoginDialog.this.mHandler1.sendMessage(msg);
                    }
                    break;

            }
            //
        }

        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}