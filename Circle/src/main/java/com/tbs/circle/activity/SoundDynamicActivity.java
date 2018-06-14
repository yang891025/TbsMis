package com.tbs.circle.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.imagepicker.Utils;
import com.tbs.circle.R;
import com.tbs.circle.mvp.contract.AudioContract;
import com.tbs.circle.mvp.presenter.AudioPresenter;
import com.tbs.circle.widgets.dialog.UpLoadDialog;

import java.io.IOException;

public class SoundDynamicActivity extends YWActivity implements AudioContract.View
{
    private static final String LOG_TAG = "SoundDynamicActivity";
    private AudioPresenter presenter;
    private String audioUrl;
    private long audioLength;
    private TextView tv_des;
    private Button mBtnOk;       //确定按钮
    private ImageView mBtnBack;       //确定按钮
    private EditText et_content;
    private UpLoadDialog uploadDialog;
    private MediaPlayer mMediaPlayer = null;
    private RelativeLayout AudioView;
    private ImageView iv_voice;
    private boolean isPlaying = false;
    private AnimationDrawable voiceAnimation = null;
    public static void startPostActivity(Context context, String audioPath, long length) {
        Intent intent = new Intent(context, SoundDynamicActivity.class);
        intent.putExtra("audio", audioPath);
        intent.putExtra("length", length);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_dynamic);
        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        View topBar = findViewById(R.id.include);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
            params.topMargin = Utils.getStatusHeight(this);
            topBar.setLayoutParams(params);
        }
        presenter = new AudioPresenter(this);
        audioUrl = getIntent().getStringExtra("audio");
        audioLength = getIntent().getLongExtra("length", 0);
        tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText("语音动态");
        et_content = (EditText) findViewById(R.id.et_content);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setText("发送");
        mBtnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                initUploadDialog();
                showLoading("");
                presenter.setPath(audioUrl);
                presenter.setLength(audioLength);
                presenter.setContent(et_content.getText().toString());
                presenter.upLoadAudio();

            }
        });
        AudioView = (RelativeLayout) findViewById(R.id.AudioView);
        AudioView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;

            }
        });
        iv_voice = (ImageView) findViewById(R.id.iv_voice);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                backDialog();
            }
        });
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

    private void initUploadDialog() {
        uploadDialog = new UpLoadDialog(this);
    }

    @Override
    public void showLoading(String msg) {
        uploadDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopPlaying();
        }
        presenter.recycle();
    }
    // Play start/stop
    private void onPlay(boolean isPlaying){

        if (!isPlaying) {
            iv_voice.setImageResource(R.drawable.voice_form_icon);
            if(voiceAnimation == null) {
                voiceAnimation = (AnimationDrawable) iv_voice.getDrawable();
                voiceAnimation.start();
            }else{
                if(!voiceAnimation.isRunning()){
                    voiceAnimation.start();
                }
            }
            //currently MediaPlayer is not playing audio
            if(mMediaPlayer == null) {
                startPlaying(); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

        } else {
            //pause the MediaPlayer
            pausePlaying();
            voiceAnimation.stop();
        }
    }

    private void startPlaying() {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(audioUrl);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                voiceAnimation.stop();
                stopPlaying();
                isPlaying = !isPlaying;
            }
        });

    }

    private void pausePlaying() {
        mMediaPlayer.pause();
    }

    private void resumePlaying() {
        mMediaPlayer.start();
    }

    private void stopPlaying() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
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

    @Override
    public void onBackPressed() {
        backDialog();
        return;
    }
}
