package com.tbs.circle.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.Utils;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbs.circle.R;
import com.tbs.circle.utils.DatasUtil;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TBS on 2017/10/18.
 */

public class ShareUrlActivity extends YWActivity
{

    private TextView tv_des;
    protected static final String TAG = ShareUrlActivity.class.getSimpleName();
    private ProgressBar progressBar1;
    private EditText news_title;
    private EditText news_desc;
    private NineGridView image_1;
    private String sharedText;
    private EditText news_url;
    private View topBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_url_edit);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.include);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }

        tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText("分享到");
        Button mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setText("分享");
        mBtnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if(news_url.getText().toString().isEmpty()){
                    Toast.makeText(ShareUrlActivity.this, "链接为空，退出分享", Toast.LENGTH_LONG).show();
                    finish();
                } else if(news_desc.getText().toString().isEmpty()){
                    Toast.makeText(ShareUrlActivity.this, "描述不可为空", Toast.LENGTH_LONG).show();
                }else if(news_title.getText().toString().isEmpty()){
                    Toast.makeText(ShareUrlActivity.this, "标题不可为空", Toast.LENGTH_LONG).show();
                }else{
                    sendSocial();
                }
            }
        });
        initView();
        ImageView mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (this.getIntent().getExtras() != null) {
            Intent intent;
            intent = this.getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            // 处理发送来的文字
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null) {
                if(sharedText.toLowerCase().indexOf("http") > -1){
                    sharedText = sharedText.substring(sharedText.toLowerCase().indexOf("http"));
                }
                if(sharedText.indexOf("@") > -1){
                    sharedText = sharedText.substring(0,sharedText.indexOf("@"));
                }
                if(sharedText.indexOf("】") > -1){
                    sharedText = sharedText.substring(sharedText.lastIndexOf("】")+1);
                }
                if(sharedText.indexOf("(") > -1){
                    sharedText = sharedText.substring(0,sharedText.indexOf("("));
                }
                asyncTask.execute(sharedText);
            }
            news_url.setText(sharedText);
            ArrayList<Uri> uris = new ArrayList<>();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if("image/*".equals(type)){
                    //处理单张图片
                    Uri uri=intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    uris.add(uri);
                }
            }else if(Intent.ACTION_SEND_MULTIPLE.equals(action)&& type != null){
                //处理多张图片
                uris=intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            }

            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            for (Uri uri : uris) {
                //System.out.println("uri.getPath() = " + uri.getPath());
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(uri.getPath());
                info.setBigImageUrl(uri.getPath());
                imageInfo.add(info);
            }
            image_1.setVisibility(View.VISIBLE);
            NineGridViewClickAdapter nineGridViewClickAdapter = new NineGridViewClickAdapter(this, imageInfo);
            image_1.setSingleImageSize(125);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                image_1.setLayoutMode(NineGridView.MODE_GRID);
            }
            image_1.setAdapter(nineGridViewClickAdapter);
        }
    }
    public void initView() {
        progressBar1 = (ProgressBar) this.findViewById(R.id.progressBar1);
        news_desc = (EditText) this.findViewById(R.id.news_desc);
        news_title = (EditText) this.findViewById(R.id.news_title);
        news_url = (EditText) this.findViewById(R.id.news_url);
        image_1 = (NineGridView) this.findViewById(R.id.multiImagView);
    }
    private void sendSocial() {
        progressBar1.setVisibility(View.VISIBLE);
        Map params = new HashMap();
        params.put("userId", DatasUtil.getUserMsg(this).getId());
        params.put("category", "0");
        params.put("coordinate", "");
        params.put("content", news_desc.getText().toString());
        params.put("location", "");
        params.put("type", "1");
        params.put("imagestr", "");
        params.put("title", news_title.getText().toString());
        params.put("url", sharedText);
        OkGo.<String>get(DatasUtil.URL_urlPUBLISH)//
                .tag(this)
                .params(params, false)
                .cacheMode(CacheMode.NO_CACHE)  //无缓冲
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 1:
                                    progressBar1.setVisibility(View.GONE);
                                    Toast.makeText(ShareUrlActivity.this, "分享成功" , Toast
                                            .LENGTH_LONG).show();
                                    break;
                            }
                        } catch (Exception e) {
                            progressBar1.setVisibility(View.GONE);
                            Toast.makeText(ShareUrlActivity.this, "请求失败 原因:" + response.body().toString(), Toast
                                    .LENGTH_LONG).show();
                            e.printStackTrace();
                        } finally {
                            finish();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        progressBar1.setVisibility(View.GONE);
                        finish();
                        Toast.makeText(ShareUrlActivity.this, "请求失败,请稍候重试", Toast.LENGTH_LONG).show();
                    }
                });
    }
    AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {

        @Override
        protected void onPreExecute() {//此函数是在任务没被线程池执行之前调用 运行在UI线程中 比如现在一个等待下载进度Progress
            super.onPreExecute();
            Log.e(TAG, "AsyncTask onPreExecute");
        }

        @Override
        protected String doInBackground(String[] params) {//此函数是在任务被线程池执行时调用 运行在子线程中，在此处理比较耗时的操作 比如执行下载
            String url = params[0];
            String title = "";
            String description = "";
            try {
                Document doc = Jsoup.connect(url).timeout(30000).get();
                title = doc.title();

                Elements metas = doc.head().select("meta");
                for (Element meta : metas) {
                    String content = meta.attr("content");
                    if ("keywords".equalsIgnoreCase(meta.attr("name"))) {
                        //System.out.println("关键字："+content);
                    }
                    if ("description".equalsIgnoreCase(meta.attr("name"))) {
                        description = content;
                    }
                }
                if (description == "") {
                    description = title;
                }
                if (title.contains("-"))
                    title = title.substring(0, title.indexOf("-"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return title + "-" + description;
        }

        @Override
        protected void onPostExecute(String s) {//此函数任务在线程池中执行结束了，回调给UI主线程的结果 比如下载结果
            super.onPostExecute(s);
            Log.e(TAG, "AsyncTask onPostExecute result---->" + s);
            progressBar1.setVisibility(View.GONE);
            news_desc.setText(s.substring(s.indexOf("-")+1));
            news_title.setText(s.substring(0,s.indexOf("-")));
        }

        @Override
        protected void onCancelled() {//此函数表示任务关闭
            super.onCancelled();
            Log.e(TAG, "AsyncTask onCancelled");
        }

        @Override
        protected void onCancelled(String s) {//此函数表示任务关闭 返回执行结果 有可能为null
            super.onCancelled(s);
            Log.e(TAG, "AsyncTask onCancelled---->" + s);
        }
    };
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
