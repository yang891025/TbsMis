package com.tbs.tbsmis.weixin;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.CustomMenuActivity;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.SetMenuAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.UIHelper;
import com.tbs.tbsmis.util.httpRequestUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeixinMenuActivity extends ListActivity implements View.OnClickListener {
    private ImageView finishBtn;
    private IniFile IniFile;
    private ArrayList<Map<String, String>> groups;
    private ListView exlist;
    private SetMenuAdapter exlist_adapter;
    private Button sendBtn;
    private Button cancleBtn;
    private ProgressDialog Prodialog;
    private boolean isOpenPop;
    private PopupWindow moreWindow2;
    private RelativeLayout cloudTitle;
    private ImageView menu_btn;
    private String type;
    private String userIni;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.weixin_set_menu);
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        // TODO Auto-generated method stub
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn2);
        this.menu_btn = (ImageView) this.findViewById(R.id.menu_btn);
        this.cloudTitle = (RelativeLayout) this.findViewById(R.id.titlebar_layout);
        this.sendBtn = (Button) this.findViewById(R.id.send_data);
        this.cancleBtn = (Button) this.findViewById(R.id.cancleBtn);
        this.cancleBtn.setOnClickListener(this);
        this.menu_btn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.initPath();

        this.exlist = (ListView) this.findViewById(android.R.id.list);
        // 构建expandablelistview的适配器

        // exlist.setGroupIndicator(null);
        this.exlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SetMenuAdapter.ViewHolder holder = (SetMenuAdapter.ViewHolder) view.getTag();
                if (WeixinMenuActivity.this.exlist_adapter.countSelected() < 3) {
                    if (holder.children_cb.isChecked() == true) {
                        holder.children_cb.toggle();
                        WeixinMenuActivity.this.IniFile.writeIniString(userIni, "MENU_ALL", "Check"
                                + (position + 1), "0");
                        WeixinMenuActivity.this.exlist_adapter.getIsSelected().put(position,
                                holder.children_cb.isChecked());
                    } else {
                        holder.children_cb.toggle();
                        WeixinMenuActivity.this.IniFile.writeIniString(userIni, "MENU_ALL", "Check"
                                + (position + 1), "1");
                        WeixinMenuActivity.this.exlist_adapter.getIsSelected().put(position,
                                holder.children_cb.isChecked());
                    }
                } else {
                    if (holder.children_cb.isChecked() == true) {
                        holder.children_cb.toggle();
                        WeixinMenuActivity.this.IniFile.writeIniString(userIni, "MENU_ALL", "Check"
                                + (position + 1), "0");
                        WeixinMenuActivity.this.exlist_adapter.getIsSelected().put(position,
                                holder.children_cb.isChecked());
                    } else {
                        Toast.makeText(WeixinMenuActivity.this, "微信菜单最多选择 3 项",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        this.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WeixinMenuActivity.this.exlist_adapter.countSelected() <= 0) {
                    Toast.makeText(WeixinMenuActivity.this, "请至少选择一个菜单项",
                            Toast.LENGTH_LONG).show();
                } else {
                    new Builder(WeixinMenuActivity.this)
                            .setCancelable(false)
                            .setMessage("确定上传")
                            .setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            WeixinMenuActivity.this.showUpDialog(1, WeixinMenuActivity.this.exlist_adapter
                                                    .getSelected());
                                        }
                                    }).setNegativeButton("取消", null).show();

                }
            }
        });
        this.Prodialog = new ProgressDialog(this);
        this.Prodialog.setTitle("提交请求");
        this.Prodialog.setMessage("正在请求，请稍候...");
        this.Prodialog.setIndeterminate(false);
        this.Prodialog.setCanceledOnTouchOutside(false);
        this.Prodialog.setButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method
                dialog.dismiss();
            }
        });
    }

    public void initList() {
        this.groups = new ArrayList<Map<String, String>>();
        // 创建两个一级条目标题
        int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                "MENU_ALL", "Count", "0", (byte) 0));
        for (int i = 1; i <= resnum; i++) {
            Map<String, String> group = new HashMap<String, String>();
            group.put("Title", this.IniFile.getIniString(userIni, "MENU_ALL",
                    "Title" + i, "", (byte) 0));
            group.put("ID", this.IniFile.getIniString(userIni, "MENU_ALL", "ID"
                    + i, "", (byte) 0));
            group.put("Type", this.IniFile.getIniString(userIni, "MENU_ALL",
                    "Type" + i, "", (byte) 0));
            group.put("Url", this.IniFile.getIniString(userIni, "MENU_ALL", "Url"
                    + i, "", (byte) 0));
            group.put("Key", this.IniFile.getIniString(userIni, "MENU_ALL", "Key"
                    + i, "", (byte) 0));
            group.put("Check", this.IniFile.getIniString(userIni, "MENU_ALL",
                    "Check" + i, "0", (byte) 0));
            this.groups.add(group);
        }
        this.exlist_adapter = new SetMenuAdapter(this.groups, this);
        this.exlist.setAdapter(this.exlist_adapter); // 绑定视图－适配器
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.initList();
    }

    protected void showUpDialog(int isSer,
                                ArrayList<Map<String, String>> selected) {
        // TODO Auto-generated method stub

        String menu_url = "{\"button\":[";
        int menu_count = selected.size();
        for (int i = 0; i < menu_count; i++) {
            int button = Integer.parseInt(this.IniFile.getIniString(userIni,
                    selected.get(i).get("ID"), "Count", "0", (byte) 0));
            String type = selected.get(i).get("Type");
            String key = selected.get(i).get("Key");
            String url = selected.get(i).get("Url");
            String name = selected.get(i).get("Title");
            if (button > 0) {
                if (button > 5)
                    button = 5;
                menu_url = menu_url + "{\"name\":\"" + name
                        + "\",\"sub_button\":[";
                for (int j = 1; j <= button; j++) {
                    String sub_type = this.IniFile.getIniString(userIni, selected
                            .get(i).get("ID"), "type" + j, "", (byte) 0);
                    String sub_key = this.IniFile.getIniString(userIni, selected
                            .get(i).get("ID"), "key" + j, "", (byte) 0);
                    String sub_url = this.IniFile.getIniString(userIni, selected
                            .get(i).get("ID"), "Url" + j, "", (byte) 0);
                    String appid = this.IniFile.getIniString(userIni, selected
                            .get(i).get("ID"), "appid" + j, "", (byte) 0);
                    String pagepath = this.IniFile.getIniString(userIni, selected
                            .get(i).get("ID"), "pagepath" + j, "", (byte) 0);
                    String sub_name = this.IniFile.getIniString(userIni, selected
                            .get(i).get("ID"), "Title" + j, "", (byte) 0);
                    menu_url = menu_url + "{\"name\":\"" + sub_name + "\",";
                    if (sub_type.equalsIgnoreCase("view")) {
                        menu_url = menu_url + "\"type\":\"" + sub_type
                                + "\",\"url\":\"" + sub_url + "\"}";
                    } else if (sub_type.equalsIgnoreCase("click")) {
                        menu_url = menu_url + "\"type\":\"" + sub_type
                                + "\",\"key\":\"" + sub_key + "\"}";
                    } else if (sub_type.equalsIgnoreCase("miniprogram")) {
                        menu_url = menu_url + "\"type\":\"" + sub_type
                                + "\",\"key\":\"" + sub_key + "\",\"appid\":\"" + appid + "\",\"pagepath\":\"" + pagepath + "\",\"url\":\"" + sub_url + "\"}";
                    } else {
                        menu_url = menu_url + "\"type\":\"click\",\"key\":\""
                                + sub_url + "\"}";
                    }
                    if (j < button) {
                        menu_url += ",";
                    }
                }
                menu_url += "]}";
            } else {
                menu_url = menu_url + "{\"name\":\"" + name + "\",";
                if (type.equalsIgnoreCase("view")) {
                    menu_url = menu_url + "\"type\":\"" + type
                            + "\",\"url\":\"" + url + "\"}";
                } else if (type.equalsIgnoreCase("click")) {
                    menu_url = menu_url + "\"type\":\"" + type
                            + "\",\"key\":\"" + key + "\"}";
                } else {
                    menu_url = menu_url + "\"type\":\"view\",\"url\":\"" + url
                            + "\"}";
                }
            }
            if (i < menu_count - 1) {
                menu_url += ",";
            }
        }
        menu_url += "]}";
        if (isSer == 1)
            this.connect(0, menu_url);
        else
            this.connect(5, menu_url);

    }

    //
    // connect(FilePath, currentPath + "temp.zip", currentPath, 2);

    private void initPath() {
        // TODO Auto-generated method stub
        this.IniFile = new IniFile();
        String webRoot = UIHelper.getSoftPath(this);
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
        String appIniFile = webRoot
                + this.IniFile.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        userIni = appIniFile;
        if (Integer.parseInt(IniFile.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
    }

    public void changMenuPopState(View v) {
        this.isOpenPop = !this.isOpenPop;
        if (this.isOpenPop) {
            this.popWindow2(v);
        } else {
            if (this.moreWindow2 != null) {
                this.moreWindow2.dismiss();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void popWindow2(View parent) {
        LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = lay.inflate(R.layout.weixin_syn_menu, null);
        RelativeLayout edit_menu = (RelativeLayout) view
                .findViewById(R.id.edit_menu);
        RelativeLayout syn_menu = (RelativeLayout) view
                .findViewById(R.id.syn_menu);
        RelativeLayout rset_default = (RelativeLayout) view
                .findViewById(R.id.rset_default);
        RelativeLayout set_default = (RelativeLayout) view
                .findViewById(R.id.set_default);
        edit_menu.setOnClickListener(this);
        rset_default.setOnClickListener(this);
        set_default.setOnClickListener(this);
        syn_menu.setOnClickListener(this);
        this.moreWindow2 = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // �˶�ʵ�ֵ���հ״�����popwindow
        this.moreWindow2.setFocusable(true);
        this.moreWindow2.setOutsideTouchable(false);
        this.moreWindow2.setBackgroundDrawable(new BitmapDrawable());
        this.moreWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WeixinMenuActivity.this.isOpenPop = false;
            }
        });
        this.moreWindow2.showAtLocation(parent, Gravity.RIGHT | Gravity.TOP, 10,
                this.cloudTitle.getHeight() * 3 / 2);
        this.moreWindow2.update();
    }

    @Override
    public void onClick(View v) {
        this.initPath();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.more_btn2:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.cancleBtn:
                this.finish();
                this.overridePendingTransition(R.anim.push_in, R.anim.push_out);
                break;
            case R.id.edit_menu:
                this.moreWindow2.dismiss();
                Intent intent = new Intent();
                intent.setClass(this, CustomMenuActivity.class);
                this.startActivity(intent);
                break;
            case R.id.syn_menu:
                this.moreWindow2.dismiss();
                this.connect(3, null);
                break;
            case R.id.rset_default:
                this.moreWindow2.dismiss();
                new Builder(this)
                        .setCancelable(false)
                        .setMessage("确定恢复")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        WeixinMenuActivity.this.connect(2, null);
                                    }
                                }).setNegativeButton("取消", null).show();
                break;
            case R.id.set_default:
                this.moreWindow2.dismiss();
                new Builder(this)
                        .setCancelable(false)
                        .setMessage("确定设置")
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        WeixinMenuActivity.this.showUpDialog(0,
                                                WeixinMenuActivity.this.exlist_adapter.getSelected());
                                    }
                                }).setNegativeButton("取消", null).show();
                break;
            case R.id.menu_btn:
                this.changMenuPopState(v);
                break;
        }
    }

    private void connect(int count, String menuUrl) {
        WeixinMenuActivity.GetAsyncTask task = new WeixinMenuActivity.GetAsyncTask(count, this, menuUrl);
        task.execute();
    }

    private void connect(int count, String token, String menuUrl) {
        WeixinMenuActivity.GetAsyncTask task = new WeixinMenuActivity.GetAsyncTask(count, this, token, menuUrl);
        task.execute();
    }

    /**
     * 生成该类的对象，并调用execute方法之后 首先执行的是onProExecute方法 其次执行doInBackgroup方法
     */
    class GetAsyncTask extends AsyncTask<Integer, Integer, String> {

        private final Context context;
        private final int count;
        private String Token;
        private final String menuUrl;

        public GetAsyncTask(int count, Context context, String menuUrl) {
            this.context = context;
            this.count = count;
            this.menuUrl = menuUrl;
        }

        public GetAsyncTask(int count, Context context, String Token,
                            String menuUrl) {
            this.context = context;
            this.count = count;
            this.Token = Token;
            this.menuUrl = menuUrl;
        }

        // 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
        @Override
        protected void onPreExecute() {
            WeixinMenuActivity.this.Prodialog.show();

        }

        /**
         * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
         */
        @Override
        protected String doInBackground(Integer... params) {
            HttpConnectionUtil connection = new HttpConnectionUtil();
            if (this.count == 0 || this.count == 3) {
                String verifyURL = WeixinMenuActivity.this.IniFile.getIniString(userIni, "WeiXin",
                        "WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
                        + "?action=getToken";
                return connection.asyncConnect(verifyURL, null, HttpConnectionUtil.HttpMethod.GET,
                        this.context);
            } else if (this.count == 2) {
                String verifyURL = WeixinMenuActivity.this.IniFile.getIniString(userIni, "WeiXin",
                        "WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
                        + "?action=creatMenu";
                return connection.asyncConnect(verifyURL, null, HttpConnectionUtil.HttpMethod.GET,
                        this.context);
            } else if (this.count == 4) {
                String verifyURL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token="
                        + this.Token;
                return connection.asyncConnect(verifyURL, null,
                        HttpConnectionUtil.HttpMethod.POST, this.context);
            } else if (this.count == 5) {
                String verifyURL = WeixinMenuActivity.this.IniFile.getIniString(userIni, "WeiXin",
                        "WeToken", "http://e.tbs.com.cn/wechat.do", (byte) 0)
                        + "?action=resetMenu";
                JSONObject jsonObject = httpRequestUtil.httpRequest(verifyURL,
                        "GET", this.menuUrl);
                if (null != jsonObject) {
                    try {
                        if (jsonObject.getInt("errcode") == 0) {
                            return "设置成功";
                        } else {
                            return jsonObject.getInt("errcode") + ":"
                                    + jsonObject.getString("errmsg");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return null;
            } else {
                String verifyURL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
                        + this.Token;
                JSONObject jsonObject = httpRequestUtil.httpsRequest(verifyURL,
                        "GET", this.menuUrl);
                //System.out.println(this.menuUrl);
                if (null != jsonObject) {
                    try {
                        if (jsonObject.getInt("errcode") == 0) {
                            return "创建成功";
                        } else {
                            return jsonObject.getInt("errcode") + ":"
                                    + jsonObject.getString("errmsg");
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }

        /**
         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
         */
        @Override
        protected void onPostExecute(String result) {
            if (result == null || result == "") {
                WeixinMenuActivity.this.Prodialog.dismiss();
                Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
            } else {
                if (this.count == 0) {
                    if (result.equalsIgnoreCase("请求无响应")) {
                        WeixinMenuActivity.this.Prodialog.dismiss();
                        Toast.makeText(this.context, "无法获取Token值",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        WeixinMenuActivity.this.connect(1, result, this.menuUrl);
                    }
                } else if (this.count == 3) {
                    WeixinMenuActivity.this.connect(4, result, null);
                } else if (this.count == 2) {
                    WeixinMenuActivity.this.Prodialog.dismiss();
                    Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
                } else if (this.count == 4) {
                    WeixinMenuActivity.this.iniMenu(result);
                    WeixinMenuActivity.this.Prodialog.dismiss();
                    Toast.makeText(this.context, "获取成功", Toast.LENGTH_SHORT).show();
                } else {
                    WeixinMenuActivity.this.Prodialog.dismiss();
                    if (result.equalsIgnoreCase("0")) {
                        Toast.makeText(this.context, "自定义菜单成功", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(this.context, result, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        }

        /**
         * 这里的Intege参数对应AsyncTask中的第二个参数
         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }

    private void iniMenu(String jsonStr) {
        try {
            this.IniCustomMenu(new JSONObject(jsonStr));
            this.initList();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void IniCustomMenu(JSONObject jsonObject) throws JSONException {
        // TODO Auto-generated method stub
        String jsonMenu = jsonObject.get("menu").toString();
        JSONArray jsonCustomMenu = new JSONObject(jsonMenu)
                .getJSONArray("button");
        if (jsonCustomMenu != null && jsonCustomMenu.length() > 0) {
            JSONArray btnJson = jsonCustomMenu;
            for (int i = 0; i < btnJson.length(); i++) {
                JSONObject ob = btnJson.getJSONObject(i);
                String name = ob.getString("name");
                this.type = "";
                int resnum = Integer.parseInt(this.IniFile.getIniString(userIni,
                        "MENU_ALL", "Count", "0", (byte) 0));
                boolean isResnum = true;
                boolean ischanged = true;
                for (int j = 1; j <= resnum; j++) {
                    if (this.IniFile.getIniString(userIni, "MENU_ALL",
                            "Title" + j, "", (byte) 0).equalsIgnoreCase(name)) {
                        isResnum = false;
                        String Id = this.IniFile.getIniString(userIni,
                                "MENU_ALL", "ID" + j, "", (byte) 0);
                        // IniFile.writeIniString(appIniFile, "MENU_ALL",
                        // "Title"
                        // + j, name);
                        if (ob.getJSONArray("sub_button").length() == Integer
                                .parseInt(this.IniFile.getIniString(userIni, Id,
                                        "Count", "0", (byte) 0))) // 显示三角
                        {
                            if (ob.getJSONArray("sub_button").length() <= 0) {
                                this.type = ob.getString("type");
                                if (this.type.equalsIgnoreCase(this.IniFile.getIniString(
                                        userIni, "MENU_ALL", "Type" + j, "",
                                        (byte) 0))) {
                                    if (this.type.equalsIgnoreCase("view")) {
                                        if (ob.getString("url")
                                                .equalsIgnoreCase(
                                                        this.IniFile.getIniString(
                                                                userIni,
                                                                "MENU_ALL",
                                                                "Url" + j, "",
                                                                (byte) 0))) {
                                            ischanged = false;
                                            break;
                                        }
                                    } else {
                                        if (ob.getString("key")
                                                .equalsIgnoreCase(
                                                        this.IniFile.getIniString(
                                                                userIni,
                                                                "MENU_ALL",
                                                                "Key" + j, "",
                                                                (byte) 0))) {
                                            ischanged = false;
                                            break;
                                        }
                                    }
                                }
                            } else {
                                JSONArray jsonArray = ob
                                        .getJSONArray("sub_button");
                                for (int n = 0; n < jsonArray.length(); n++) {
                                    JSONObject sub = jsonArray.getJSONObject(n);
                                    String subname = sub.getString("name");
                                    String subtype = sub.getString("type");
                                    if (subname.equalsIgnoreCase(this.IniFile
                                            .getIniString(userIni, Id,
                                                    "Title" + (n + 1), "",
                                                    (byte) 0))
                                            && subtype.equalsIgnoreCase(this.IniFile
                                            .getIniString(userIni,
                                                    Id, "Type"
                                                            + (n + 1),
                                                    "", (byte) 0))) {
                                        if (this.type.equalsIgnoreCase("view")) {
                                            if (sub.getString("url")
                                                    .equalsIgnoreCase(
                                                            this.IniFile.getIniString(
                                                                    userIni,
                                                                    Id,
                                                                    "Url"
                                                                            + (n + 1),
                                                                    "",
                                                                    (byte) 0))) {
                                                ischanged = false;
                                                break;
                                            }
                                        } else {
                                            if (sub.getString("key")
                                                    .equalsIgnoreCase(
                                                            this.IniFile.getIniString(
                                                                    userIni,
                                                                    Id,
                                                                    "Key"
                                                                            + (n + 1),
                                                                    "",
                                                                    (byte) 0))) {
                                                ischanged = false;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
                if (isResnum || ischanged) {
                    int noResnum = resnum + 1;
                    this.IniFile.writeIniString(userIni, "MENU_ALL", "ID"
                            + noResnum, "menu_weixin" + noResnum);
                    this.IniFile.writeIniString(userIni, "MENU_ALL", "Title"
                            + noResnum, name);
                    this.IniFile.writeIniString(userIni, "MENU_ALL", "Count",
                            noResnum + "");
                    if (ob.getJSONArray("sub_button").length() <= 0) // 显示三角
                    {
                        this.IniFile.writeIniString(userIni, "menu_weixin"
                                + noResnum, "Count", "0");
                        this.type = ob.getString("type");
                        if (this.type.equalsIgnoreCase("view")) {
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "Type" + noResnum, this.type);
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "Url" + noResnum, ob.getString("url"));
                        } else if (this.type.equalsIgnoreCase("miniprogram")) {
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "Type" + noResnum, this.type);
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "Url" + noResnum, ob.getString("url"));
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "appid" + noResnum, ob.getString("appid"));
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "pagepath" + noResnum, ob.getString("pagepath"));
                        } else {
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "Type" + noResnum, this.type);
                            this.IniFile.writeIniString(userIni, "MENU_ALL",
                                    "Key" + noResnum, ob.getString("key"));
                        }
                    } else {
                        JSONArray jsonArray = ob.getJSONArray("sub_button");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            JSONObject sub = jsonArray.getJSONObject(n);
                            String subname = sub.getString("name");
                            String subtype = sub.getString("type");
                            this.IniFile.writeIniString(userIni, "menu_weixin"
                                    + noResnum, "Count", jsonArray.length()
                                    + "");
                            if (subtype.equalsIgnoreCase("view")) {
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Title"
                                                + (n + 1), subname);
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Type"
                                                + (n + 1), subtype);
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Url"
                                                + (n + 1), sub.getString("url"));
                            } else if (subtype.equalsIgnoreCase("miniprogram")) {
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Title"
                                                + (n + 1), subname);
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Type"
                                                + (n + 1), subtype);
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Url"
                                                + (n + 1), sub.getString("url"));
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "appid"
                                                + (n + 1), sub.getString("appid"));
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "pagepath"
                                                + (n + 1), sub.getString("pagepath"));

                            } else {
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Title"
                                                + (n + 1), subname);
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Type"
                                                + (n + 1), subtype);
                                this.IniFile.writeIniString(userIni,
                                        "menu_weixin" + noResnum, "Kye"
                                                + (n + 1), sub.getString("key"));
                            }
                        }
                    }
                }
            }
        }
    }

}