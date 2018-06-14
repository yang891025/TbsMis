package com.tbs.tbsmis.Musics.executor;

import android.app.Activity;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;

import java.io.File;

import me.wcy.music.executor.PlayMusic;
import me.wcy.music.model.Music;
import me.wcy.music.utils.FileUtils;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlaySpeicalMusic extends PlayMusic
{

    public PlaySpeicalMusic(Activity activity, Music onlineMusic) {
        super(activity, 2);
        music = onlineMusic;
    }

    @Override
    protected void getPlayInfo() {
        String artist = music.getArtist();
        String title = music.getTitle();
//        // 下载歌词
//        String lrcFileName = FileUtils.getLrcFileName(artist, title);
//        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
//        if (!lrcFile.exists() && !TextUtils.isEmpty(music.getLrclink())) {
//            downloadLrc(mOnlineMusic.getLrclink(), lrcFileName);
//        } else {
//            mCounter++;
//        }

        // 下载封面
        String albumFileName = FileUtils.getAlbumFileName(artist, title);
        File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
        String picUrl = music.getCoverPath();
        if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
            downloadAlbum(picUrl, albumFileName);
        } else {
            mCounter++;
        }
        music.setCoverPath(albumFile.getPath());
        //System.out.println("mCounter = " + mCounter);
        checkCounter();
    }

    private void downloadAlbum(String picUrl, String fileName) {
        OkGo.<File>get(picUrl)
                .execute(new FileCallback(FileUtils.getAlbumDir(), fileName)
                {
                    @Override
                    public void onSuccess(Response<File> response) {
                        //checkCounter();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        checkCounter();
                    }
                });

    }
}
