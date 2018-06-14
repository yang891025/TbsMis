package com.tbs.tbsmis.source;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.app.MyActivity;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.file.FileInfo;
import com.tbs.tbsmis.util.FileUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.io.File;
import java.util.List;

/**
 * Created by TBS on 2016/7/5.
 */
public class FileSourceActivity extends Activity implements OnClickListener
{
    private PullToRefreshGridView maingv;
    private ImageView finishBtn;
    private ImageView downBtn;
    private TextView title;
    private FileGridviewAdapter exlist_adapter;
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
        this.maingv.setMode(Mode.DISABLED);
        this.finishBtn = (ImageView) this.findViewById(R.id.more_btn);
        this.downBtn = (ImageView) this.findViewById(R.id.finish_btn);
        edit_btn = (Button) this.findViewById(R.id.edit_btn);
        this.title = (TextView) this.findViewById(R.id.textView1);
        this.title.setText("我的资源");
        this.downBtn.setOnClickListener(this);
        this.finishBtn.setOnClickListener(this);
        this.downBtn.setVisibility(View.GONE);
        String webRoot = UIHelper.getStoragePath(this);
        webRoot = webRoot + constants.SD_CARD_TBSFILE_PATH6;
        final List<FileInfo> allDir = FileUtils.listFilePath(webRoot);
        this.exlist_adapter = new FileGridviewAdapter(this, allDir);
        this.maingv.setAdapter(this.exlist_adapter);
        this.maingv.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 跳转到分类文件
                List<FileInfo> allDirs = FileUtils.listFilePath(allDir.get(i).filePath);
                File file = new File(allDir.get(i).filePath + "/" + allDir.get(i).fileName + ".ini");
                //System.out.println(allDir.get(i).filePath + allDir.get(i).fileName+".ini");
                if (allDirs.size() > 0 && !file.exists()) {
                    Intent intent = new Intent();
                    intent.setClass(FileSourceActivity.this,
                            FileListViewActivity.class);
                    intent.putExtra("fileName", allDir.get(i).fileName);
                    intent.putExtra("filePath", allDir.get(i).filePath);
                    FileSourceActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(FileSourceActivity.this,
                            FileViewActivity.class);
                    intent.putExtra("fileName", allDir.get(i).fileName);
                    intent.putExtra("filePath", allDir.get(i).filePath);
                    FileSourceActivity.this.startActivity(intent);
                }

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_btn:
                this.finish();
                break;
            default:
                break;
        }
    }
}
