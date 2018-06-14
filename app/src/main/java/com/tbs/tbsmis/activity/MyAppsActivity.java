package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.AppGridViewAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.HttpConnectionUtil.HttpMethod;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAppsActivity extends Activity implements View.OnClickListener
{
    public static final String TAG = "MyAppsActivity";
    private PullToRefreshGridView maingv;
    private String appIniFile;
    private IniFile IniFile;
    private List<Map<String, String>> child;
    private int resnum;
    private int groupnum;
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private AppGridViewAdapter exlist_adapter;
    private Button edit_btn;
    private boolean isShowDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gridview_activity);
        // 获取到GridView
        MyActivity.getInstance().addActivity(this);
        this.maingv = (PullToRefreshGridView) findViewById(R.id.gv_all);
        this.maingv.setMode(Mode.PULL_FROM_START);
        edit_btn = (Button) this.findViewById(R.id.edit_btn);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("我的应用");
        this.downBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        String configPath = this.getApplicationContext().getFilesDir()
                .getParentFile().getAbsolutePath();
        if (configPath.endsWith("/") == false) {
            configPath = configPath + "/";
        }
        this.appIniFile = configPath + constants.USER_CONFIG_FILE_NAME;
        this.IniFile = new IniFile();
        this.refreshListView();
        // 给gridview设置数据适配器

        this.maingv.setOnRefreshListener(new OnRefreshListener<GridView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                Log.e("TAG", "onPullDownToRefresh"); // Do work to
                String label = DateUtils.formatDateTime(
                        MyAppsActivity.this.getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                //refreshListView();
                MyAppsActivity.this.connect(1);
            }
        });
        edit_btn.setVisibility(View.VISIBLE);
        edit_btn.setText("编辑");
        edit_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (isShowDelete) {
                    isShowDelete = false;
                    edit_btn.setText("编辑");
                } else {
                    isShowDelete = true;
                    edit_btn.setText("取消编辑");
                }
                exlist_adapter.setShowDelete(isShowDelete);
            }

        });
        this.connect(1);
    }

    private void refreshListView() {
        // TODO Auto-generated method stub
        this.child = new ArrayList<Map<String, String>>();
        // 创建两个一级条目标题
        this.resnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile, "resource",
                "resnum", "0", (byte) 0));
        for (int i = 1; i <= this.resnum; i++) {
            String groupid = this.IniFile.getIniString(this.appIniFile, "resource",
                    "resid" + i, "0", (byte) 0);
            this.groupnum = Integer.parseInt(this.IniFile.getIniString(this.appIniFile,
                    groupid, "resnum", "0", (byte) 0));
            for (int j = 1; j <= this.groupnum; j++) {
                String resname = this.IniFile.getIniString(this.appIniFile, groupid,
                        "res" + j, "0", (byte) 0);
                Map<String, String> childdata = new HashMap<String, String>();
                String webRoot;
                if (this.IniFile.getIniString(this.appIniFile, resname, "instdir", "",
                        (byte) 0).startsWith("/")) {
                    webRoot = this.IniFile.getIniString(this.appIniFile, resname,
                            "instdir", "", (byte) 0);
                } else {
                    webRoot = UIHelper.getStoragePath(this);
                    webRoot = webRoot
                            + constants.SD_CARD_TBSSOFT_PATH3
                            + "/"
                            + this.IniFile.getIniString(this.appIniFile, resname,
                            "instdir", "", (byte) 0);
                }
                if (webRoot.endsWith("/") == false) {
                    webRoot += "/";
                }
                File configFile = new File(webRoot);
                if (configFile.exists() && configFile.isDirectory()) {
                    childdata.put("child", this.IniFile.getIniString(this.appIniFile,
                            resname, "title", "", (byte) 0));
                    childdata.put("path", this.IniFile.getIniString(this.appIniFile,
                            resname, "instdir", "", (byte) 0));
                    childdata.put("version", this.IniFile.getIniString(this.appIniFile,
                            resname, "version", "", (byte) 0));
                    childdata.put("update", "0");
                    childdata.put("downUrl", "");
                    childdata.put("appinfo", "");
                    childdata.put("storePath", this.IniFile.getIniString(this.appIniFile,
                            resname, "storePath", "", (byte) 0));
                    childdata.put("color", this.IniFile.getIniString(this.appIniFile,
                            "resource", "resbkcolor" + i, "", (byte) 0));
                    this.child.add(childdata);
                }
            }
        }
        this.exlist_adapter = new AppGridViewAdapter(this, this.child);
        this.maingv.setAdapter(this.exlist_adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.refreshListView();
        this.exlist_adapter.notifyDataSetChanged();
    }

    // Activity创建或者从被覆盖、后台重新回到前台时被调用
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.more_btn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.finish_btn:
                Intent intent = new Intent();
                intent.setClass(this, AppManagerActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    private void connect(int count) {
        MyAppsActivity.GetDataTask task = new MyAppsActivity.GetDataTask(count, this);
        task.execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String>
    {
        private final Context context;
        private final int count;

        public GetDataTask(int count, Context context) {
            this.context = context;
            this.count = count;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpConnectionUtil connection = new HttpConnectionUtil();
            String json = "[";
            for (int i = 0; i < MyAppsActivity.this.child.size(); i++) {
                String storePath = MyAppsActivity.this.child.get(i).get("storePath");
                if (!storePath.isEmpty()) {
                    String webRoot = UIHelper.getShareperference(this.context, constants.SAVE_INFORMATION,
                            "Path", "");
                    if (webRoot.endsWith("/") == false) {
                        webRoot += "/";
                    }
                    String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
                    String appNewsFile = webRoot
                            + MyAppsActivity.this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                            constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
                    String userIni = appNewsFile;
                    if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                            "LoginType", "0", (byte) 0)) == 1) {
                        String dataPath = getFilesDir().getParentFile()
                                .getAbsolutePath();
                        if (dataPath.endsWith("/") == false) {
                            dataPath = dataPath + "/";
                        }
                        userIni = dataPath + "TbsApp.ini";
                    }
                    String portUrl = MyAppsActivity.this.IniFile.getIniString(userIni, "Store",
                            "storePort", constants.DefaultServerPort, (byte) 0);
                    String ipUrl = MyAppsActivity.this.IniFile.getIniString(userIni, "Store",
                            "storeAddress", constants.DefaultServerIp, (byte) 0);
                    String baseUrl = "http://" + ipUrl + ":" + portUrl;
                    String verifyURL = MyAppsActivity.this.IniFile
                            .getIniString(
                                    MyAppsActivity.this.appIniFile,
                                    "system",
                                    "infourl",
                                    "/Store/getUpdateMsg.cbs",
                                    (byte) 0);
                    Map<String, String> param = new HashMap<String, String>();
                    param.put("path", storePath);
                    //System.out.println(storePath);
//                    if (i < MyAppsActivity.this.child.size() - 1) {
                    String reString = connection.asyncConnect(baseUrl + verifyURL, param,
                            HttpMethod.GET,
                            this.context);
                    if (reString == null) {
                        json += "{},";
                    } else {
                        json += reString + ",";
                    }
                }
//                    else {
//                        String reString = connection.asyncConnect(baseUrl + verifyURL, param,
//                                HttpMethod.GET,
//                                this.context);
//                        if (reString == null) {
//                            json += "{}";
//                        } else {
//                            json += reString;
//                        }
//                    }
//               }
            }
            if (json.endsWith(","))
                json = json.substring(0, json.length() - 1) + "]";
            else
                json = json + "]";
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Toast.makeText(this.context, "ERR201:无法获取应用信息", Toast.LENGTH_SHORT).show();
            } else {
                //System.out.println(result);
                try {
                    JSONArray json = new JSONArray(result);
                    for (int i = 0; i < json.length(); i++) {
                        // 获取每一个JsonObject对象
                        JSONObject myjObject = json.getJSONObject(i);
                        if (!myjObject.isNull("path")) {
                            if (myjObject.getString("path").equalsIgnoreCase(MyAppsActivity.this.child.get(i).get
                                    ("path"))) {
                                if (myjObject.getString("version").compareTo(MyAppsActivity.this.child.get(i).get
                                        ("version")) > 0) {
                                    MyAppsActivity.this.child.get(i).put("update", "1");
                                    MyAppsActivity.this.child.get(i).put("downUrl", myjObject.getString("downUrl"));
                                    MyAppsActivity.this.child.get(i).put("appinfo", myjObject.getString("appinfo"));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(this.context, "获取数据异常", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                MyAppsActivity.this.exlist_adapter.notifyDataSetChanged();
                // Call onRefreshComplete when the list has been refreshed.
                MyAppsActivity.this.maingv.onRefreshComplete();
            }
        }
    }
}
