package com.tbs.circle.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.tbs.circle.R;
import com.tbs.circle.record.PlaybackFragment;
import com.tbs.circle.record.RecordingItem;
import com.tbs.circle.record.RecordingService;

import java.io.File;

public class SoundRecordActivity extends YWActivity
{

    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;
    private Button mListonButton = null;
    private TextView mRecordingPrompt;
    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_record);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextView) findViewById(R.id.recording_status_text);

        mRecordButton = (FloatingActionButton) findViewById(R.id.btnRecord);
        //mRecordButton.setCol(getResources().getColor(R.color.blue));
        mRecordButton.setBackgroundColor(getResources().getColor(R.color.blue));
        mRecordButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });

        mPauseButton = (Button) findViewById(R.id.btnFinish);
        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取成功录像后的视频路径
                SharedPreferences sharePreferences = getSharedPreferences("sp_name_audio", MODE_PRIVATE);
                String filePath = sharePreferences.getString("audio_path", "");
                long elpased = sharePreferences.getLong("elpased", 0);
                Intent data = new Intent();
                data.putExtra("path", filePath);
                data.putExtra("length",elpased);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        mListonButton = (Button) findViewById(R.id.btnListon);
        mListonButton.setVisibility(View.GONE); //hide pause button before recording starts
        mListonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取成功录像后的视频路径
                RecordingItem recordingItem = new RecordingItem();
                SharedPreferences sharePreferences = getSharedPreferences("sp_name_audio", MODE_PRIVATE);
                final String filePath = sharePreferences.getString("audio_path", "");
                long elpased = sharePreferences.getLong("elpased", 0);
                recordingItem.setFilePath(filePath);
                recordingItem.setLength((int) elpased);
                PlaybackFragment fragmentPlay = PlaybackFragment.newInstance(recordingItem);
                fragmentPlay.show(getSupportFragmentManager(), PlaybackFragment.class.getSimpleName());
            }
        });
    }

    //TODO: recording pause
    private void onRecord(boolean start) {
        intent = new Intent(this, RecordingService.class);
        if (start) {
            // start recording
            mRecordButton.setImageResource(R.drawable.ic_media_stop);

            //Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }
            mRecordPromptCount = 0;
            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener()
            {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        mRecordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });
            //start RecordingService
            startService(intent);
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            mPauseButton.setVisibility(View.VISIBLE);
            mListonButton.setVisibility(View.VISIBLE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));
            stopService(intent);
            //allow the screen to turn off again once recording is finished
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!mStartRecording) {
            mChronometer.stop();
            timeWhenPaused = 0;
            stopService(intent);
        }
    }
    //    //TODO: implement pause recording
//    private void onPauseRecord(boolean pause) {
//        if (pause) {
//            //pause recording
//            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
//                    (R.drawable.ic_play_bar_btn_play ,0 ,0 ,0);
//            mRecordingPrompt.setText((String)getString(R.string.resume_recording_button).toUpperCase());
//            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
//            mChronometer.stop();
//        } else {
//            //resume recording
//            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
//                    (R.drawable.ic_play_bar_btn_pause ,0 ,0 ,0);
//            mRecordingPrompt.setText((String)getString(R.string.pause_recording_button).toUpperCase());
//            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
//            mChronometer.start();
//        }
//    }
}
