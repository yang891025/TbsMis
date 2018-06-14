package com.tbs.tbsmis.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.ini.IniFile;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.HttpConnectionUtil;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class SearchActivityNew extends Activity implements View.OnClickListener
{
    private static final String TAG = "SearchActivityNew";
    private ImageView back_btn;
    //private RadioGroup search_category_group;
    private Spinner search_category_btn;
    private EditText news_search_edit;
    private ImageView search_submit_button;
    private AnimationDrawable loadingAnima;
    private RelativeLayout loadingIV;
    private ImageView iv;
    private ImageView finish_btn;
    private IniFile m_iniFileIO;
    private ArrayList<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String userIni;
    private TextView tv_clear;
    private TextView tv_tip;
    /*列表及其适配器*/
    private Search_Listview listView;
    private BaseAdapter adapter;
    /*数据库变量*/
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private String appCode;
    private int curNum;
    private String baseUrl;
    private ListView keyword_listView;
    private ScrollView history_layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_sapi_searchnew);
        Log.i(SearchActivityNew.TAG, "onCreate");
        MyActivity.getInstance().addActivity(this);
        this.init();
    }

    private void init() {
        // TODO Auto-generated method stub
        this.back_btn = (ImageView) this.findViewById(R.id.back_btn);
        this.finish_btn = (ImageView) this.findViewById(R.id.finish_btn);
        this.search_category_btn = (Spinner) this.findViewById(R.id.search_category_btn);
        this.news_search_edit = (EditText) this.findViewById(R.id.news_search_edit);
        this.search_submit_button = (ImageView) this.findViewById(R.id.search_submit_button);
        this.loadingIV = (RelativeLayout) this.findViewById(R.id.loading_dialog);
        this.iv = (ImageView) this.findViewById(R.id.gifview);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        history_layout = (ScrollView) findViewById(R.id.history_layout);
        listView = (Search_Listview) findViewById(R.id.listView);
        keyword_listView = (ListView) findViewById(R.id.keyword_listView);
        //"清空搜索历史"按钮
        tv_clear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                //清空数据库
                deleteData();
                queryData("");
            }
        });
        keyword_listView.setVisibility(View.GONE);
        this.back_btn.setOnClickListener(this);
        this.finish_btn.setOnClickListener(this);
        this.finish_btn.setVisibility(View.GONE);
        this.search_submit_button.setOnClickListener(this);
        this.news_search_edit.setFocusableInTouchMode(true);
        this.news_search_edit.requestFocus();
        loadingIV.setVisibility(View.INVISIBLE);
        this.initPath();
        //实例化数据库SQLiteOpenHelper子类对象
        helper = new RecordSQLiteOpenHelper(this);
        // 第一次进入时查询所有的历史记录
        queryData("");
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int cateNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "search", "SearchCount", "0", (byte) 0));
        curNum = Integer.parseInt(this.m_iniFileIO.getIniString(this.userIni,
                "search", "CurrentSearch", "0", (byte) 0));
        if (cateNum <= curNum) {
            curNum = cateNum - 1;
            this.m_iniFileIO.writeIniString(this.userIni,
                    "search", "CurrentSearch", curNum + "");
        }
        this.data_list = new ArrayList<String>();
        for (int i = 0; i < cateNum; i++) {
            String search_Name = this.m_iniFileIO.getIniString(this.userIni, "search",
                    "search" + i, "", (byte) 0);
            this.data_list.add(this.m_iniFileIO.getIniString(this.userIni, search_Name,
                    "SearchTitle", "简单检索", (byte) 0));

        }
        //适配器
        this.arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, this.data_list);
        //设置样式
        this.arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        this.search_category_btn.setAdapter(this.arr_adapter);
        this.search_category_btn.setSelection(curNum);
        this.search_category_btn.setOnItemSelectedListener(
                new OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView,
                                               View
                                                       view, int i, long l) {
                        String search_Name = SearchActivityNew.this.m_iniFileIO.getIniString(SearchActivityNew.this.userIni,
                                "search", "search" + i, "", (byte) 0);
                        String tempUrl = StringUtils.isUrl(SearchActivityNew.this
                                        .m_iniFileIO
                                        .getIniString(SearchActivityNew.this.userIni, search_Name,
                                                "SearchURL", "", (byte) 0),
                                SearchActivityNew.this.baseUrl,
                                UIHelper.getShareperference(
                                        SearchActivityNew.this,
                                        constants.SAVE_LOCALMSGNUM, "resname",
                                        "yqxx"));
                        if (UIHelper.TbsMotion(SearchActivityNew.this, tempUrl)) {
                            SearchActivityNew.this.m_iniFileIO.writeIniString(SearchActivityNew.this.userIni, "search",
                                    "CurrentSearch", i + "");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?>
                                                          adapterView) {

                    }
                }
        );

        this.news_search_edit.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub
                        // s:变化后的所有字符
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int
                            start, int count, int after) {
                        // s:变化前的所有字符； start:字符开始的位置；
                        // count:变化前的总字节数；after:变化后的字节数
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        if (s.toString().trim().length() == 0) {
                            //若搜索框为空,则模糊搜索空字符,即显示所有的搜索历史
                            history_layout.setVisibility(View.VISIBLE);
                            keyword_listView.setVisibility(View.GONE);
                            tv_tip.setText("搜索历史");
                        } else {
                            tv_tip.setText("搜索结果");
                            MyAsyncTask task = new SearchActivityNew.MyAsyncTask(SearchActivityNew.this, news_search_edit.getText().toString());
                            task.execute();
                        }

                        //每次输入后都查询数据库并显示
                        //根据输入的值去模糊查询数据库中有没有数据
                        String tempName = news_search_edit.getText().toString();
                        queryData(tempName);

                    }

                }

        );
        news_search_edit.setOnEditorActionListener(
                new TextView.OnEditorActionListener()
                {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            if (StringUtils.isEmpty(news_search_edit.getText().toString())) {
                                Toast.makeText(SearchActivityNew.this, "检索词为空", Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                                boolean hasData = hasData(news_search_edit.getText().toString().trim());
                                if (!hasData) {
                                    insertData(news_search_edit.getText().toString().trim());

                                    queryData("");

                                }
                                Intent intent = new Intent();
                                intent.setClass(SearchActivityNew.this, SearchResultActivity.class);
                                intent.putExtra("currentSearch", curNum);
                                intent.putExtra("searchWord", news_search_edit.getText().toString().trim());
                                startActivity(intent);
                            }

                            return true;
                        }
                        return false;
                    }
                });
        //列表监听
        //即当用户点击搜索历史里的字段后,会直接将结果当作搜索字段进行搜索
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //获取到用户点击列表里的文字,并自动填充到搜索框内
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String name = textView.getText().toString();
                news_search_edit.setText(name);
                Intent intent = new Intent();
                intent.setClass(SearchActivityNew.this, SearchResultActivity.class);
                intent.putExtra("currentSearch", curNum);
                intent.putExtra("searchWord", name);
                startActivity(intent);

            }
        });

    }

    private void initPath() {
        // TODO Auto-generated method stub
        this.m_iniFileIO = new IniFile();
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
        userIni = webRoot
                + this.m_iniFileIO.getIniString(WebIniFile, "TBSWeb", "IniName",
                constants.NEWS_CONFIG_FILE_NAME, (byte) 0);
        appCode = userIni.substring(0, userIni.lastIndexOf("."));
        //userIni = userIni;
        if (Integer.parseInt(m_iniFileIO.getIniString(userIni, "Login",
                "LoginType", "0", (byte) 0)) == 1) {
            String dataPath = getFilesDir().getParentFile()
                    .getAbsolutePath();
            if (dataPath.endsWith("/") == false) {
                dataPath = dataPath + "/";
            }
            userIni = dataPath + "TbsApp.ini";
        }
        String ipUrl = this.m_iniFileIO.getIniString(userIni, "search",
                "searchAddress", constants.DefaultLocalIp, (byte) 0);
        String portUrl = this.m_iniFileIO.getIniString(userIni, "search",
                "searchPort", constants.DefaultLocalPort, (byte) 0);
        this.baseUrl = "http://" + ipUrl + ":" + portUrl;
    }

    @Override
    protected void onStart() {
        Log.i(SearchActivityNew.TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        news_search_edit.setSelection(news_search_edit.getText().toString().length());//将光标移至文字末尾
    }

    public void startAnimation() {
        this.loadingAnima = (AnimationDrawable) this.iv.getBackground();
        this.loadingAnima.start();
        this.loadingIV.setVisibility(View.VISIBLE);
    }

    public void stopAnimation() {
        // loadingAnima.stop();
        this.loadingIV.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onDestroy() {
        Log.i(SearchActivityNew.TAG, "onDestroy");
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.clear_search_word:
                this.news_search_edit.setText("");
                break;
            case R.id.search_submit_button:
                this.initPath();
                if (StringUtils.isEmpty(this.news_search_edit.getText().toString())) {
                    Toast.makeText(this, "检索词为空", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    boolean hasData = hasData(news_search_edit.getText().toString().trim());
                    if (!hasData) {
                        insertData(news_search_edit.getText().toString().trim());
                        queryData("");
                    }
                    Intent intent = new Intent();
                    intent.setClass(SearchActivityNew.this, SearchResultActivity.class);
                    intent.putExtra("currentSearch", curNum);
                    intent.putExtra("searchWord", news_search_edit.getText().toString().trim());
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return false;
    }

    /*插入数据*/
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name,appCode) values('" + tempName + "','" + appCode + "')");
        db.close();
    }

    /*模糊查询数据 并显示在ListView列表上*/
    private void queryData(String tempName) {

        //模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' and appCode = '" + appCode +
                        "' order by id desc ", null);
        // 创建adapter适配器对象,装入模糊搜索的结果
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, cursor, new
                String[]{"name"},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /*检查数据库中是否已经有该条记录*/
    private boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =? and appCode =?", new String[]{tempName, appCode});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /*清空数据*/
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records where appCode = '" + appCode + "'");
        db.close();
    }


    class MyAsyncTask extends AsyncTask<String, Integer, String>
    {

        Context context;
        private String Token;

        public MyAsyncTask(Context context, String Token) {
            this.context = context;
            this.Token = Token;
        }

        // 运行在UI线程中，在调用doInBackground()之前执行
        @Override
        protected void onPreExecute() {
            startAnimation();
        }

        // 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
        @Override
        protected String doInBackground(String... params) {
            HttpConnectionUtil connection = new HttpConnectionUtil();
            String search_Name = m_iniFileIO.getIniString(userIni, "search",
                    "search" + curNum, "", (byte) 0);
            String KeywordURL = m_iniFileIO.getIniString(userIni,
                    search_Name, "KeywordURL", "", (byte) 0);
            if(!StringUtils.isEmpty(KeywordURL)) {
                String verifyURL = StringUtils.isUrl(KeywordURL, baseUrl, UIHelper
                        .getShareperference(context,
                                constants.SAVE_LOCALMSGNUM, "resname", "yqxx"));
                return connection.asyncConnect(verifyURL, setUrl(),
                        HttpConnectionUtil.HttpMethod.GET, context);
            }
            return "";

        }

        public Map<String, String> setUrl() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Tword", Token);
            params.put("UserID", m_iniFileIO.getIniString(SearchActivityNew.this.userIni, "Login",
                    "LoginId", "", (byte) 0));
            return params;
        }

        // 运行在ui线程中，在doInBackground()执行完毕后执行
        @Override
        protected void onPostExecute(String result) {
            stopAnimation();
            if (result == null) {
                Toast.makeText(this.context, "请检查网络设置", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONArray JSONArray = new JSONArray(result);
                    final ArrayList data_list = new ArrayList<String>();
                    ArrayList data_list2 = new ArrayList<String>();
                    for (int i = 0; i < JSONArray.length(); i++) {
                        JSONObject ob = JSONArray.getJSONObject(i);
                        String word = ob.getString("word");
                        data_list.add(word);
                        int index = word.indexOf(Token);
                        int len = Token.length();
                        Spanned temp = Html.fromHtml(word.substring(0, index)
                                + "<font color=#FF0000>"
                                + word.substring(index, index + len) + "</font>"
                                + word.substring(index + len, word.length()));
                        data_list2.add(temp);
                    }
                    if(data_list.size() > 0){
                        keyword_listView.setVisibility(View.VISIBLE);
                        history_layout.setVisibility(View.GONE);
                        //适配器
                        ArrayAdapter arr_adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, data_list2);
                        //设置样式
                        //arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        keyword_listView.setAdapter(arr_adapter);
                        keyword_listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                boolean hasData = hasData(data_list.get(position).toString());
                                if (!hasData) {
                                    insertData(data_list.get(position).toString());
                                    queryData("");
                                }
                                Intent intent = new Intent();
                                intent.setClass(SearchActivityNew.this, SearchResultActivity.class);
                                intent.putExtra("currentSearch", curNum);
                                intent.putExtra("searchWord", data_list.get(position).toString());
                                startActivity(intent);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        // 在publishProgress()被调用以后执行，publishProgress()用于更新进度
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}