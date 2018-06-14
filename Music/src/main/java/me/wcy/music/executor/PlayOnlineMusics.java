package me.wcy.music.executor;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.wcy.music.model.Music;
import me.wcy.music.model.OnlineMusic;

/**
 * 播放在线音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusics extends PlayMusic {
    private List<OnlineMusic> mOnlineMusic;

    public PlayOnlineMusics(Activity activity, List<OnlineMusic> onlineMusic) {
        super(activity, 1);
        mOnlineMusic = onlineMusic;
    }

    @Override
    protected void getPlayInfo() {
        musics = new ArrayList<Music>();
        for(int i = 0; i< mOnlineMusic.size(); i++) {
            String artist = mOnlineMusic.get(i).getArtist_name();
            String title = mOnlineMusic.get(i).getTitle();

            music = new Music();
            music.setType(Music.Type.ONLINE);
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(mOnlineMusic.get(i).getAlbum_title());
            music.setFileName(mOnlineMusic.get(i).getLrclink());
            String picUrl = mOnlineMusic.get(i).getPic_big();
            if (TextUtils.isEmpty(picUrl)) {
                picUrl = mOnlineMusic.get(i).getPic_small();
            }
            music.setCoverPath(picUrl);
            music.setSongId(Long.parseLong(mOnlineMusic.get(i).getSong_id()));
            music.setPath(mOnlineMusic.get(i).getPath());
            music.setDuration(mOnlineMusic.get(i).getDuration());
            musics.add(music);
        }
        checkCounters();
    }

}
