package com.tbs.chat.util;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.util.Log;

import com.tbs.chat.constants.Config;

/*
 * 这是录音，播放录音的工具类
 */
public class AudioRecorder {
	
	private static final String TAG = "AudioRecorder";
	private static AudioRecorder instance;
	
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	
    private String fileName = null;
    private int length = 0;
    private boolean isPlaying = false;
    private String lastPath = "";
    
    public AudioRecorder() {
    	mPlayer = new MediaPlayer();
    	mPlayer.setOnCompletionListener(mCompleteListener);
	}

    /**
     * 实例化audioRecorder
     * 使用单例模式
     */
	public static AudioRecorder getInstance() {
		if (instance == null) {
			instance = new AudioRecorder();
		}
		return instance;
	}
	
	/**
	 * 启动录制
	 */
	public boolean startRecord(String mark){
		boolean flag = false;
		try {
            Log.e(TAG, "---------------------- startRecord ----------------------------");
			File file = FileUtil.createFile(mark, Config.MESSAGE_TYPE_AUDIO);
			Log.e(TAG, "startRecord Config.MESSAGE_TYPE_AUDIO");
			fileName = file.getAbsolutePath();
			Log.e(TAG, "startRecord file.getAbsolutePath()");
			mRecorder = new MediaRecorder();
			Log.e(TAG, "startRecord new MediaRecorder");
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			Log.e(TAG, "startRecord setAudioSource");
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			Log.e(TAG, "startRecord setOutputFormat");
			mRecorder.setOutputFile(fileName);
			Log.e(TAG, "startRecord setOutputFile");
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			Log.e(TAG, "startRecord setAudioEncoder");
			mRecorder.prepare();
			Log.e(TAG, "startRecord prepare");
			mRecorder.start();
			Log.e(TAG, "startRecord start");
			flag = true;
        } catch (IOException e) {
            Log.e(TAG, "prepare failed: "+e.getMessage());
            flag = false;
        }
		Log.e(TAG, "----------------------- startRecord ---------------------------");
		return flag;
    }
	
	/**
	 * 停止录制
	 */
	public boolean stopRecord(){
		Log.e(TAG, "----------------------- stopRecord ---------------------------");
		if(mRecorder != null){
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 删除录制的语音
	 */
	public void delRecord(){
		File file = new File(fileName);
		file.delete();
	}
	
	public double getAmplitude() {
		if (mRecorder != null) {
			return (mRecorder.getMaxAmplitude());
		} else{
			return 0;
		}
	}
	
	/**
	 * 播放管理者
	 * @param fileName
	 */
	public void audioManager(String fileName){
		if (fileName.equals(lastPath)) {
			try {
				boolean playing = mPlayer.isPlaying();
				if (playing) {
					StopAudio();
				} else {
					playAudio(fileName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			playAudio(fileName);
		}
	}
	
	/**
	 * 播放语音
	 */
	public void playAudio(String fileName) {
		 mPlayer = new MediaPlayer();
		try {
			mPlayer.reset();
			mPlayer.setDataSource(fileName);
			mPlayer.prepare();
			mPlayer.start();
			lastPath = fileName;
		} catch (Exception e) {
			Log.e(TAG, "prepare failed:" + e.getMessage());
		}
	}
	
	/**
	 * 停止播放语音
	 */
	public void StopAudio(){
        try {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            lastPath = "";
        } catch (Exception e) {
            Log.e(TAG, "prepare failed:"+e.getMessage());
        }
	}
	
	public void cleanAudio(){
		if (mPlayer != null && mPlayer.isPlaying()) {
			StopAudio();
		}
	}
	
	/**
     * 播放监听
     */
	MediaPlayer.OnCompletionListener mCompleteListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			lastPath = "";
		}
	};

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
}
