package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.tbs.tbsmis.check.TemplateViewAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.HttpConnectionUtil.HttpMethod;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TBS on 2016/4/18.
 */
public class WeiXinTemplateActivity extends Activity implements View.OnClickListener
{
    public static final String TAG = "MyAppsActivity";
    private PullToRefreshGridView maingv;
    private List<Map<String, String>> child;
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private TemplateViewAdapter exlist_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gridview_activity);
        this.maingv = (PullToRefreshGridView) findViewById(R.id.gv_all);
        // 获取到GridView
        MyActivity.getInstance().addActivity(this);
        this.maingv.setMode(Mode.PULL_FROM_START);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("模板选择");
        this.downBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        // 给gridview设置数据适配器
        this.maingv.setOnRefreshListener(new OnRefreshListener<GridView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                Log.e("TAG", "onPullDownToRefresh"); // Do work to
                String label = DateUtils.formatDateTime(
                        WeiXinTemplateActivity.this.getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy()
                        .setLastUpdatedLabel(label);
                //refreshListView();
                WeiXinTemplateActivity.this.connect(1);
            }
        });
        this.maingv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new Builder(WeiXinTemplateActivity.this).setNegativeButton("选择", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        UIHelper.setSharePerference(WeiXinTemplateActivity.this,
                                constants.SAVE_INFORMATION, "WXhtml", WeiXinTemplateActivity.this.child.get(i).get("content"));
                        dialogInterface.dismiss();
                        WeiXinTemplateActivity.this.finish();
                    }
                }).setNeutralButton("编辑", new OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        Intent intent = new Intent();
                        intent.putExtra("isNew",false);
                        intent.putExtra("WXhtml", WeiXinTemplateActivity.this.child.get(i).get("content"));
                        intent.putExtra("pic", WeiXinTemplateActivity.this.child.get(i).get("pic"));
                        intent.putExtra("name", WeiXinTemplateActivity.this.child.get(i).get("name"));
                        intent.putExtra("count", (i + 1) + "");
                        intent.setClass(WeiXinTemplateActivity.this, WeiXinTemplateEditActivity.class);
                        WeiXinTemplateActivity.this.startActivity(intent);
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        this.maingv.setRefreshing();
        this.connect(1);
    }

    @Override
    protected void onResume() {
        this.connect(1);
        super.onResume();
    }

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
                intent.putExtra("pic", this.child.get(0).get("pic").substring(0, this.child.get(0).get("pic").lastIndexOf("/") + 1));
                intent.putExtra("count", this.child.size() + 1 +"");
                intent.putExtra("isNew",true);
                intent.setClass(this, WeiXinTemplateEditActivity.class);
                this.startActivity(intent);
                break;
        }
    }

    private void connect(int count) {
        WeiXinTemplateActivity.GetDataTask task = new WeiXinTemplateActivity.GetDataTask(count, this);
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
            IniFile IniFile = new IniFile();
            String webRoot = UIHelper.getShareperference(context,constants.SAVE_INFORMATION,"Path","");
            String WebIniFile = webRoot + constants.WEB_CONFIG_FILE_NAME;
            String appIniFile = webRoot
                    + IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                    constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
            String userIni = appIniFile;
            if(Integer.parseInt(IniFile.getIniString(userIni, "Login",
                    "LoginType", "0", (byte) 0)) == 1){
                String dataPath = getFilesDir().getParentFile()
                        .getAbsolutePath();
                if (dataPath.endsWith("/") == false) {
                    dataPath = dataPath + "/";
                }
                userIni = dataPath + "TbsApp.ini";
            }
            String verifyURL = IniFile.getIniString(userIni, "WeiXin",
                    "WeTemplate", "http://e.tbs.com.cn/wechatServlet.do", (byte) 0)
                    + "?action=getTemplate";
            return connection.asyncConnect(verifyURL, null, HttpMethod.GET,
                    this.context);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Toast.makeText(this.context, "ERR203:无法获取模板信息", Toast.LENGTH_SHORT).show();
            } else {
                WeiXinTemplateActivity.this.child = new ArrayList<Map<String, String>>();
                try {
                    JSONArray jsonUsers = new JSONObject(result)
                            .getJSONArray("template");
                    for (int i = 0; i < jsonUsers.length(); i++) {
                        // 获取每一个JsonObject对象
                        JSONObject myjObject = jsonUsers.getJSONObject(i);
                        Map<String, String> childdata = new HashMap<String, String>();
                        childdata.put("content", myjObject.getString("content"));
                        childdata.put("pic", myjObject.getString("pic"));
                        childdata.put("name", myjObject.getString("name"));
                        WeiXinTemplateActivity.this.child.add(childdata);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(this.context, "获取数据异常", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                WeiXinTemplateActivity.this.exlist_adapter = new TemplateViewAdapter(this.context, WeiXinTemplateActivity.this.child);
                WeiXinTemplateActivity.this.maingv.setAdapter(WeiXinTemplateActivity.this.exlist_adapter);
                // Call onRefreshComplete when the list has been refreshed.
                WeiXinTemplateActivity.this.maingv.onRefreshComplete();
            }
        }
    }
}
