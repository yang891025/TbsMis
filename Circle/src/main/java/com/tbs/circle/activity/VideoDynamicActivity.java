package com.tbs.circle.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lzy.imagepicker.Utils;
import com.tbs.circle.R;
import com.tbs.circle.mvp.contract.VideoContract;
import com.tbs.circle.mvp.presenter.VideoPresenter;
import com.tbs.circle.widgets.dialog.UpLoadDialog;

import java.io.Serializable;

/**
 * Created by TBS on 2017/9/14.
 */

public class VideoDynamicActivity extends YWActivity implements VideoContract.View
{
    private View parentView;
    private TextView tv_des;
    private Button mBtnOk;       //确定按钮
    private ImageView mBtnBack;       //确定按钮
    private String videoUrl;
    private VideoView mVideoView;
    private UpLoadDialog uploadDialog;
    private VideoPresenter presenter;
    private EditText et_content;
    private View topBar;


    public static void startPostActivity(Context context, String videoPath) {
        Intent intent = new Intent(context, VideoDynamicActivity.class);
        intent.putExtra("video", (Serializable) videoPath);
        context.startActivity(intent);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentView = getLayoutInflater().inflate(R.layout.activity_video, null);
        setContentView(parentView);
        presenter = new VideoPresenter(this);
        initData();
    }

    private void initData() {
        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.include);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        videoUrl = getIntent().getStringExtra("video");
        mVideoView = (VideoView) findViewById(R.id.mVideoView);
        tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText("视频动态");
        et_content = (EditText) findViewById(R.id.et_content);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setText("发送");
        mBtnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                initUploadDialog();
                showLoading("");
                presenter.setPathList(videoUrl);
                presenter.setContent(et_content.getText().toString());
                presenter.upLoadVideo();

            }
        });
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                backDialog();
            }
        });
        mVideoView.setVideoPath(videoUrl);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setVolume(0f, 0f);
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
    }

    private void initUploadDialog() {
        uploadDialog = new UpLoadDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null)
            mVideoView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null)
            mVideoView.pause();
    }

    @Override
    public void onBackPressed() {
        backDialog();
        return;
    }

    private void backDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("退出此次编辑？").setPositiveButton("退出", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNeutralButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void showLoading(String msg) {
        uploadDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.recycle();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void hideLoading() {
        uploadDialog.dismiss();
    }

    @Override
    public void LoadingProgress(int progress) {
        uploadDialog.setPercentsProgress(progress);
    }

    @Override
    public void showError(String errorMsg) {

    }
}
