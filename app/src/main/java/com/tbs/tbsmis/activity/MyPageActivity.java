package com.tbs.tbsmis.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.HashMap;
import java.util.Map;

public class MyPageActivity extends Activity implements View.OnClickListener
{

    protected static final String TAG = "MySetupActivity";
    private ImageView leftBtn;
    private TextView title;
    private RelativeLayout Info_edit;
    private ImageView RightBtn;
    private IniFile m_iniFileIO;
    private String webRoot;
    private String appFile;
    private Bitmap bitmap;
    private ImageView user_imageview;
    private TextView username;
    private TextView accountname;
    private TextView signature;
    private Button quitBtn;
    private ProgressDialog Prodialog;
    private RelativeLayout subscribe;
    private RelativeLayout wallet;
    private RelativeLayout Myres;
    private RelativeLayout personal;
    private RelativeLayout system;
    private static final int QUIT_REQUEST_CODE = 0;
    private RelativeLayout sapi_account_collect;
    private String userIni;
    private LinearLayout user_pay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        setContentView(R.layout.layout_sapi_mypage);
        titleView();
        // setting();
    }

    private void titleView() {
        leftBtn = (ImageView) findViewById(R.id.more_btn);
        RightBtn = (ImageView) findViewById(R.id.finish_btn);
        title = (TextView) findViewById(R.id.textView1);
        Info_edit = (RelativeLayout) findViewById(R.id.sapi_account_edit);
        subscribe = (RelativeLayout) findViewById(R.id.sapi_account_subscribe_li);
        wallet = (RelativeLayout) findViewById(R.id.sapi_account_pay);
        sapi_account_collect = (RelativeLayout) findViewById(R.id.sapi_account_collect);
        Myres = (RelativeLayout) findViewById(R.id.sapi_account_res_li);
        personal = (RelativeLayout) findViewById(R.id.sapi_account_personal);
        system = (RelativeLayout) findViewById(R.id.sapi_account_system);
        user_imageview = (ImageView) findViewById(R.id.avatar_iv);
        username = (TextView) findViewById(R.id.nickname);
        accountname = (TextView) findViewById(R.id.username);
        signature = (TextView) findViewById(R.id.signature);
        quitBtn = (Button) findViewById(R.id.sapi_logout_btn);
        user_pay = (LinearLayout) findViewById(R.id.user_pay);

        quitBtn.setOnClickListener(this);
        subscribe.setOnClickListener(this);
        Info_edit.setOnClickListener(this);
        wallet.setOnClickListener(this);
        sapi_account_collect.setOnClickListener(this);
        Myres.setOnClickListener(this);
        personal.setOnClickListener(this);
        system.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        RightBtn.setOnClickListener(this);
        title.setText("我");
    }

    public void setting() {
        initPath();
        signature.setText(m_iniFileIO.getIniString(userIni, "Login",
                "Signature", "", (byte) 0));
        String nickName = m_iniFileIO.getIniString(userIni, "Login", "NickName",
                "匿名", (byte) 0);
        if (StringUtils.isEmpty(nickName)) {
            username.setText("匿名");
        } else {
            username.setText(nickName);
        }
        if (StringUtils.isEmpty(m_iniFileIO.getIniString(userIni, "Login",
                "UserCode", "", (byte) 0))) {
            accountname.setText("账号:"
                    + m_iniFileIO.getIniString(userIni, "Login", "Account", "",
                    (byte) 0));
        } else {
            accountname.setText("账号:"
                    + m_iniFileIO.getIniString(userIni, "Login", "UserCode", "",
                    (byte) 0));
        }
        // accountname.setText("账号:"
        // + m_iniFileIO.getIniString(appFile, "USER", "Account", "",
        // (byte) 0));
        String headPath = "http://"
                + m_iniFileIO.getIniString(userIni, "Login",
                "ebsAddress", constants.DefaultServerIp, (byte) 0)
                + ":"
                + m_iniFileIO.getIniString(userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + m_iniFileIO.getIniString(userIni, "Login", "ebsPath",
                "/EBS/UserServlet", (byte) 0)
                + "?act=downloadHead&account="
                + m_iniFileIO.getIniString(userIni, "Login", "Account", "",
                (byte) 0);
        ImageLoader imageLoader = new ImageLoader(this,R.drawable.default_avatar);
        imageLoader.DisplayImage(headPath, user_imageview);

        int setup = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Wallet",
                "myWallet_show_in_my", "0", (byte) 0));
        int setup2 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Subscribe",
                "mySubscribe_show_in_my", "0", (byte) 0));
        int setup3 = Integer.parseInt(m_iniFileIO.getIniString(userIni, "Collect",
                "collect_show_in_my", "0", (byte) 0));
        if(setup == 0 && setup2 == 0&& setup3 == 0){
            user_pay.setVisibility(View.GONE);
        }else{
            user_pay.setVisibility(View.VISIBLE);
            if(setup == 1){
                wallet.setVisibility(View.VISIBLE);
            }else{
                wallet.setVisibility(View.GONE);
            }
            if(setup2 == 1){
                subscribe.setVisibility(View.VISIBLE);
            }else{
                subscribe.setVisibility(View.GONE);
            }
            if(setup3 == 1){
                sapi_account_collect.setVisibility(View.VISIBLE);
            }else{
                sapi_account_collect.setVisibility(View.GONE);
            }
        }

    }

    private void initPath() {
        m_iniFileIO = new IniFile();
        webRoot = UIHelper.getSoftPath(this);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(this, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        appFile = webRoot
                + m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appFile;
        if(Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1){
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }

    public Map<String, String> LoginQuit() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "logOut");
        params.put("clientId", UIHelper.DeviceMD5ID(this));
        params.put("loginId", m_iniFileIO.getIniString(userIni, "Login",
                "LoginId", "", (byte) 0));
        return params;
    }

    //
    @Override
    protected void onResume() {
        super.onResume();
        setting();
    }

    // ���ʱ��������
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
        MyActivity.getInstance().finishActivity(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.more_btn:
                finish();
                overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.finish_btn:
                finish();
                overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.sapi_logout_btn:
                connect(MyPageActivity.QUIT_REQUEST_CODE);
                Prodialog = new ProgressDialog(this);
                Prodialog.setTitle("退出登录");
                Prodialog.setMessage("正在退出，请稍候...");
                Prodialog.setIndeterminate(false);
                Prodialog.setCanceledOnTouchOutside(false);
                Prodialog.setButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                Prodialog.show();
                break;
            case R.id.sapi_account_edit:
                intent.setClass(this, MySetupActivity.class);
                startActivity(intent);
                break;
            case R.id.sapi_account_pay:
                intent.putExtra("rights", 1);
                intent.setClass(this, MyWalletActivity.class);
                startActivity(intent);
                break;
            case R.id.sapi_account_collect:
                //intent.putExtra("rights", 2);
                intent.setClass(this, MyCollectActivity.class);
                startActivity(intent);
                break;
            case R.id.sapi_account_res_li:
                intent.putExtra("rights", 1);
                intent.setClass(this, MyResourceActivity.class);
                startActivity(intent);
                break;
            case R.id.sapi_account_personal:
                intent.putExtra("rights", 2);
                intent.setClass(this, MyResourceActivity.class);
                startActivity(intent);
                break;
            case R.id.sapi_account_system:
                intent.putExtra("rights", 3);
                intent.setClass(this, MyResourceActivity.class);
                startActivity(intent);
                break;
            case R.id.sapi_account_subscribe_li:
                intent.setClass(this, MySubscribeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void connect(int count) {
        MyPageActivity.MyAsyncTask task = new MyPageActivity.MyAsyncTask(this, count);
        task.execute();
    }

    // �첽���� ��̨����ǰ ��̨������ ��̨�������
    @SuppressLint("ShowToast")
    class MyAsyncTask extends AsyncTask<String, Integer, String>
    {

        // private static final String TAG = "MyAsyncTask";
        private final Context context;
        private final int action;

        public MyAsyncTask(Context c, int action) {
            context = c;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            switch (this.action) {

            }
        }

        // ��̨���еķ������������з�UI�̣߳�����ִ�к�ʱ�ķ���
        @Override
        protected String doInBackground(String... params) {
            initPath();
            HttpConnectionUtil connection = new HttpConnectionUtil();
            constants.verifyURL = "http://"
                    + m_iniFileIO.getIniString(userIni, "Login",
                    "ebsAddress", constants.DefaultServerIp, (byte) 0)
                    + ":"
                    + m_iniFileIO.getIniString(userIni, "Login", "ebsPort",
                    "8083", (byte) 0)
                    + m_iniFileIO.getIniString(userIni, "Login", "ebsPath",
                    "/EBS/UserServlet", (byte) 0);
            switch (this.action) {
                case MyPageActivity.QUIT_REQUEST_CODE:
                    return connection.asyncConnect(constants.verifyURL,
                            LoginQuit(), HttpConnectionUtil.HttpMethod.POST, this.context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            switch (this.action) {
                case MyPageActivity.QUIT_REQUEST_CODE:
                    Prodialog.dismiss();
                    // 登陆标志位下线
                    m_iniFileIO.writeIniString(userIni, "Login", "LoginFlag", "0");
                    //stopService(new Intent(this.context.getString(string.ServerName1)));
                    Intent intent = new Intent();
                    intent.setAction(context
                            .getString(R.string.ServerName1));//你定义的service的action
                    intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                    context.stopService(intent);
                    SharedPreferences pre = getSharedPreferences(
                            Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
                    if (pre.getBoolean(Constants.SETTINGS_NOTIFICATION_ENABLED,
                            true)) {
                        ServiceManager serviceManager = new ServiceManager(
                                MyPageActivity.this);
                        serviceManager.EBSLogout();
                    }
                    startActivity(new Intent(this.context, SetUpActivity.class));
                    finish();
                    break;
                default:
                    break;

            }
        }

        // ��publishProgress()�������Ժ�ִ�У�publishProgress()���ڸ��½��
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
}