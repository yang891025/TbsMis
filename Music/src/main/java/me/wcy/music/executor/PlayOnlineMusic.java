package me.wcy.music.executor;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

import me.wcy.music.http.HttpCallback;
import me.wcy.music.http.HttpClient;
import me.wcy.music.model.Music;
import me.wcy.music.utils.FileUtils;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusic extends PlayMusic {

    public PlayOnlineMusic(Context activity, Music onlineMusic) {
        super(activity, 2);
        music = onlineMusic;
    }

    @Override
    protected void getPlayInfo() {
        if(music.getType() == Music.Type.ONLINE){
            // 下载歌词
            String lrcFileName = FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
            File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
            if (!lrcFile.exists() && !TextUtils.isEmpty(music.getFileName())) {
                downloadLrc(music.getFileName(), lrcFileName);
            } else {
                mCounter++;
            }
            // 下载封面
            String albumFileName = FileUtils.getAlbumFileName(music.getArtist(), music.getTitle());
            File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
            String picUrl = music.getCoverPath();
            if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
                downloadAlbum(picUrl, albumFileName);
            } else {
                mCounter++;
            }
            if(albumFile.exists())
            music.setCoverPath(albumFile.getPath());
            checkCounter();
//            // 获取歌曲播放链接
//            HttpClient.getMusicDownloadInfo(music.getSongId()+"", new HttpCallback<DownloadInfo>()
//            {
//                @Override
//                public void onSuccess(DownloadInfo response) {
//                    if (response == null || response.getBitrate() == null) {
//                        onFail(null);
//                        return;
//                    }
//
//                    music.setPath(response.getBitrate().getFile_link());
//                    music.setDuration(response.getBitrate().getFile_duration() * 1000);
//                    checkCounter();
//                }

//                @Override
//                public void onFail(Exception e) {
//                    onExecuteFail(e);
//                }
//            });
        }


    }

    private void downloadLrc(String url, String fileName) {
        HttpClient.downloadFile(url, FileUtils.getLrcDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {
            }

            @Override
            public void onFail(Exception e) {
            }

            @Override
            public void onFinish() {
                checkCounter();
            }
        });
    }

    private void downloadAlbum(String picUrl, String fileName) {
        HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
            @Override
            public void onSuccess(File file) {
            }

            @Override
            public void onFail(Exception e) {
            }

            @Override
            public void onFinish() {
                checkCounter();
            }
        });
    }
}
