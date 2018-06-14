package com.tbs.tbsmis.weixin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.check.PicGridViewAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by TBS on 2016/3/9.
 */
public class WeiXinPicActivity extends Activity
{
    private PullToRefreshGridView maingv;
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private List<String> childs;
    private PicGridViewAdapter exlist_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gridview_activity);
        // 获取到GridView
        this.maingv = (PullToRefreshGridView) findViewById(R.id.gv_all);
        this.maingv.setMode(Mode.PULL_FROM_START);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        this.finishBtn.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view) {
                WeiXinPicActivity.this.finish();
            }
        });
        this.downBtn.setVisibility(View.GONE);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("选择图片");
        MyActivity.getInstance().addActivity(this);
        final List<String> childs = new ArrayList<>();
        childs.add("http://e.tbs.com.cn:8003/image/novod.jpg");
        if (this.getIntent().getExtras() != null) {
            Intent intent;
            intent = this.getIntent();
            String html = intent.getStringExtra("html");
            Document doc1 = Jsoup.parse(html);
            Elements imagElements = doc1.select("img[src]");
            Iterator<Element> iterator = imagElements.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                String picUrl = element.attr("abs:src");
                if(!picUrl.isEmpty()){
                    if(!picUrl.endsWith("gif"))
                    childs.add(picUrl);
                }
            }
            Elements imagElements2 = doc1.select("img[data-src]");
            Iterator<Element> iterator2 = imagElements2.iterator();
            while (iterator2.hasNext()) {
                Element element = iterator2.next();
                String picUrl = element.attr("abs:data-src");
                if(!picUrl.isEmpty()){
                    if(!picUrl.endsWith("gif"))
                    childs.add(picUrl);
                }
            }
        }
        this.exlist_adapter = new PicGridViewAdapter(this, childs);
        this.maingv.setAdapter(this.exlist_adapter);
        this.maingv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String picUrl = childs.get(i);
                Intent in = new Intent();
                in.putExtra( "result", picUrl );
                WeiXinPicActivity.this.setResult(Activity.RESULT_OK, in );
                WeiXinPicActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivity.getInstance().finishActivity(this);
    }
}
