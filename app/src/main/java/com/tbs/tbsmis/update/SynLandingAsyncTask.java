package com.tbs.tbsmis.update;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.notification.Constants;
import com.tbs.tbsmis.notification.ServiceManager;
import com.tbs.tbsmis.util.DES;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SynLandingAsyncTask extends AsyncTask<String, Integer, String>
{

    Context context;
    private String user;
    private final String retLogid;
    private IniFile IniFile;
    private String webRoot;
    private String appTestFile;
    private String retStr;
    private final String account;
    private String userIni;

    public SynLandingAsyncTask(Context c, String LoginId, String account) {
        context = c;
        retLogid = LoginId;
        this.account = account;
    }

    // 运行在UI线程中，在调用doInBackground()之前执行
    @Override
    protected void onPreExecute() {
    }

    // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
    @Override
    protected String doInBackground(String... params) {
        this.iniPath();
        HttpConnectionUtil connection = new HttpConnectionUtil();
        constants.verifyURL = "http://"
                + this.IniFile.getIniString(this.userIni, "Login", "ebsAddress",
                constants.DefaultServerIp, (byte) 0)
                + ":"
                + this.IniFile.getIniString(this.userIni, "Login", "ebsPort",
                "8083", (byte) 0)
                + this.IniFile.getIniString(this.userIni, "Login", "ebsPath",
                "/EBS/UserServlet", (byte) 0);
        return connection.asyncConnect(constants.verifyURL, this.login(),
                HttpConnectionUtil.HttpMethod.POST, this.context);
    }

    // 运行在ui线程中，在doInBackground()执行完毕后执行
    @Override
    protected void onPostExecute(String result) {
        ServiceManager serviceManager;
        // stopAnimation();
        if (result != null) {
            JSONObject json;
            try {
                json = new JSONObject(result);
                this.retStr = json.getString("msg");
                System.out.println("msg=" + this.retStr);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                this.retStr = result;
            }
            try {
                json = new JSONObject(result);
                this.user = json.getString("user");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (this.retStr != null && this.retLogid != null) {
                if (this.user != null) {
                    try {
                        if (this.retStr.equals("查询成功")) {
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "UserName", this.account);
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "LoginId", this.retLogid);
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "LoginFlag", "1");
                            json = new JSONObject(this.user);
                            if (account.equalsIgnoreCase(json.getString("userName")) || account.equalsIgnoreCase(json
                                    .getString("email")) || account.equalsIgnoreCase(json.getString("mobile")) ||
                                    account.equalsIgnoreCase(json.getString("account")) || account.equalsIgnoreCase
                                    (json.getString("userCode"))) {

                            } else
                                this.IniFile.writeIniString(this.userIni, "Login",
                                        "PassWord", "");
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "NickName", json.getString("userName"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "newEMail", json.getString("newEMail"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Mobile", json.getString("mobile"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Contact", json.getString("email"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Account", json.getString("account"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "UserCode", json.getString("userCode"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Signature", json.getString("idiograph"));
                            this.IniFile.writeIniString(this.userIni, "Login", "Sex",
                                    json.getString("sex"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Address", json.getString("myURL"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Location_province",
                                    json.getString("province"));
                            this.IniFile.writeIniString(this.userIni, "Login",
                                    "Location_city", json.getString("city"));
                            if (StringUtils.isEmpty(json
                                    .getString("userCodeModifyNum"))) {
                                this.IniFile.writeIniString(this.userIni, "Login",
                                        "AccountFlag", "0");
                            } else {
                                this.IniFile.writeIniString(this.userIni, "Login",
                                        "AccountFlag", "1");
                            }
                            if (UIHelper.getShareperference(this.context,
                                    Constants.SHARED_PREFERENCE_NAME,
                                    Constants.SETTINGS_NOTIFICATION_ENABLED,
                                    true)) {
                                serviceManager = new ServiceManager(this.context);
                                serviceManager
                                        .setNotificationIcon(R.drawable.notification);
                                serviceManager
                                        .setUserInfo(this.IniFile.getIniString(
                                                this.userIni, "Login", "Account",
                                                "", (byte) 0), DES
                                                .encrypt(this.IniFile.getIniString(
                                                        this.userIni, "Login",
                                                        "PassWord", "",
                                                        (byte) 0)), this.IniFile
                                                .getIniString(this.userIni,
                                                        "Login", "LoginId", "",
                                                        (byte) 0));
                                serviceManager.restartService();
                            }
                            if (Integer.parseInt(this.IniFile.getIniString(
                                    userIni, "Login", "UserUpate", "1",
                                    (byte) 0)) == 1) {
//                                this.context.startService(new Intent(this.context
//										.getString(string.ServerName1)));
                                Intent mIntent = new Intent();
                                mIntent.setAction(context
                                        .getString(R.string.ServerName1));//你定义的service的action
                                mIntent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
                                context.startService(mIntent);
                            }
                            Toast.makeText(this.context,
                                    R.string.sapi_login_success,
                                    Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

        }
        //
    }

    // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    /*
     * ini文件读写参数初始化
     */
    private void iniPath() {
        this.IniFile = new IniFile();
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
        this.appTestFile = this.webRoot
                + this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        this.userIni = this.appTestFile;
        if (Integer.parseInt(this.IniFile.getIniString(this.userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = this.context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            this.userIni = dataPath + "TbsApp.ini";
        }
    }

    public Map<String, String> login() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("act", "getUser");
        params.put("account", this.account);
        params.put("loginId", this.retLogid);
        return params;
    }
}