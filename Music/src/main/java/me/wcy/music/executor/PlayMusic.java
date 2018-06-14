package me.wcy.music.executor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.List;

import me.wcy.music.R;
import me.wcy.music.model.Music;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.NetworkUtils;

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
public abstract class PlayMusic implements IExecutor<Music> {
    private Context mActivity;
    protected Music music;
    protected List<Music> musics;
    private int mTotalStep;
    protected int mCounter = 0;

    public PlayMusic(Context activity, int totalStep) {
        mActivity = activity;
        mTotalStep = totalStep;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        boolean mobileNetworkPlay = Preferences.enableMobileNetworkPlay();
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkPlay) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.play_tips);
            builder.setPositiveButton(R.string.play_tips_sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences.saveMobileNetworkPlay(true);
                    getPlayInfoWrapper();
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            getPlayInfoWrapper();
        }
    }

    private void getPlayInfoWrapper() {
        onPrepare();
        getPlayInfo();
    }

    protected abstract void getPlayInfo();

    protected void checkCounter() {
        mCounter++;
        //System.out.println("mCounter = " + mCounter);
        if (mCounter == mTotalStep) {
            onExecuteSuccess(music);
        }
    }

    protected void checkCounters() {
        mCounter++;
        if (mCounter == mTotalStep) {
            onListExecuteSuccess(musics);
        }
    }
}
