package com.tbs.tbsmis.source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.util.FileUtils;

import java.io.File;
import java.util.List;

public class FileListViewActivity extends Activity implements View.OnClickListener {

    private TextView mTitleTextView;
    private ImageView mBackBtn;
    private ListView mListView;
    private FileListViewAdapter exlist_adapter;
    private List<FileInfo> allDir;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        MyActivity.getInstance().addActivity(this);
        this.setContentView(R.layout.biz_plugin_weather_select_city);
        this.initView();
        if (this.getIntent().getExtras() != null) {
            Intent intent = this.getIntent();
            String fileName = intent.getStringExtra("fileName");
            String filePath = intent.getStringExtra("filePath");
            this.mTitleTextView.setText(fileName);
            this.allDir =  FileUtils.listFilePath(filePath);
            this.exlist_adapter = new FileListViewAdapter(this, this.allDir);
            this.mListView.setAdapter(this.exlist_adapter);
        }

    }

    private void initView() {
        this.mTitleTextView = (TextView) this.findViewById(R.id.title_name);
        this.mBackBtn = (ImageView) this.findViewById(R.id.title_back);
        this.mBackBtn.setOnClickListener(this);
        this.empty = (TextView) this.findViewById(R.id.empty);
        this.empty.setText("暂无文件");
        this.mListView = (ListView) this.findViewById(R.id.citys_list);
        // mCityContainer = findViewById(R.id.city_content_container);
        this.mListView.setEmptyView(this.findViewById(R.id.citys_list_empty));
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                List<FileInfo> allDirs =  FileUtils.listFilePath(FileListViewActivity.this.allDir.get(position).filePath);
                File file = new File(allDir.get(position).filePath +"/"+ allDir.get(position).fileName + ".ini");
                // System.out.println(allDir.get(position).filePath + allDir.get(position).fileName + ".ini");
                if (allDirs.size() > 0 && !file.exists()) {
                    Intent intent = new Intent();
                    intent.setClass(FileListViewActivity.this,
                            FileListViewActivity.class);
                    intent.putExtra("fileName", FileListViewActivity.this.allDir.get(position).fileName);
                    intent.putExtra("filePath", FileListViewActivity.this.allDir.get(position).filePath);
                    FileListViewActivity.this.startActivity(intent);
                }else{
                    Intent intent = new Intent();
                    intent.setClass(FileListViewActivity.this,
                            FileViewActivity.class);
                    intent.putExtra("fileName", FileListViewActivity.this.allDir.get(position).fileName);
                    intent.putExtra("filePath", FileListViewActivity.this.allDir.get(position).filePath);
                    FileListViewActivity.this.startActivity(intent);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 结束Activity&从堆栈中移除
        MyActivity.getInstance().finishActivity(this);
    }
}