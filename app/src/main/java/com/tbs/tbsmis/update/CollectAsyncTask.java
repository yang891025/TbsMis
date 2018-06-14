package com.tbs.tbsmis.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
 *
 */
public class CollectAsyncTask extends AsyncTask<Integer, Integer, String> {

    private final String url;
    private final String Title;
    private final Context context;
    private final String type;
    private final String content;
    private final String pic;
    private IniFile m_iniFileIO;
    private String userIni;
    private String baseUrl;

    /**
     *
     * @param context
     * @param url
     * @param Title
     * @param type
     * @param content
     * @param pic
     */
    public CollectAsyncTask(Context context, String url, String Title,String type,String content,String pic) {
        this.url = url;
        this.Title = Title;
        this.content = content;
        this.pic = pic;
        this.type = type;
        this.context = context;
        initPath();
    }

    /**
     * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
     */
    @Override
    protected String doInBackground(Integer... params) {
        HttpConnectionUtil connection = new HttpConnectionUtil();
        String collectUrl = m_iniFileIO.getIniString(userIni,
                "Collect", "collectPath", "/collect/collect.cbs", (byte) 0);
        if(!StringUtils.isEmpty(collectUrl)) {
            String verifyURL = StringUtils.isUrl(baseUrl+collectUrl, baseUrl,"");
            return connection.asyncConnect(verifyURL, setUrl(),
                    HttpConnectionUtil.HttpMethod.GET, context);
        }
        return "";
    }

    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @SuppressLint("ShowToast")
    @Override
    protected void onPostExecute(String result) {
        if (!StringUtils.isEmpty(result)) {
           if(result.equalsIgnoreCase("true")){
               Toast.makeText(context,"收藏成功",Toast.LENGTH_LONG).show();
           }else if(result.equalsIgnoreCase("ishave")){
               Toast.makeText(context,"已收藏",Toast.LENGTH_LONG).show();
           }else if(result.equalsIgnoreCase("needlogin")){
               Toast.makeText(context,"未登录,收藏失败",Toast.LENGTH_LONG).show();
           }else{
               Toast.makeText(context,"收藏失败",Toast.LENGTH_LONG).show();
           }
        }else{
            Toast.makeText(context,"没有配置收藏功能",Toast.LENGTH_LONG).show();
        }

    }

    // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {

    }

    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(Integer... values) {

    }
    private void initPath() {
        // TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
        String webRoot = UIHelper.getSoftPath(context);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        webRoot += context.getString(R.string.SD_CARD_TBSAPP_PATH2);
        webRoot = UIHelper.getShareperference(context, constants.SAVE_INFORMATION,
                "Path", webRoot);
        if (webRoot.endsWith("/") == false) {
            webRoot += "/";
        }
        String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
        userIni = webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        //userIni = userIni;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = context.getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String ipUrl = m_iniFileIO.getIniString(userIni, "Collect",
                "collectAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = m_iniFileIO.getIniString(userIni, "Collect",
                "collectPort", constants.DefaultLocalPort, (byte) 0);
        baseUrl = "http://" + ipUrl + ":" + portUrl;
    }
    public Map<String, String> setUrl() {
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("title", Title);
            params.put("url", url);
            params.put("content", content);
            params.put("type", type);
            params.put("pic", pic);
            params.put("userName", m_iniFileIO.getIniString(this.userIni, "Login",
                    "Account", "", (byte) 0));
            params.put("loginId", m_iniFileIO.getIniString(userIni, "Login",
                    "LoginId", "", (byte) 0));
            params.put("subjectId", StringUtils.getMD5(url));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return params;
    }
}