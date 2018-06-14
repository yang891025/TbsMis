package me.wcy.music.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.wcy.lrcview.LrcView;
import me.wcy.music.R;
import me.wcy.music.adapter.PlayPagerAdapter;
import me.wcy.music.constants.Actions;
import me.wcy.music.enums.PlayModeEnum;
import me.wcy.music.executor.PlayOnlineMusics;
import me.wcy.music.executor.SearchLrc;
import me.wcy.music.fragment.PlaylistFragment;
import me.wcy.music.model.Music;
import me.wcy.music.model.OnlineMusic;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.OnPlayerEventListener;
import me.wcy.music.storage.preference.Preferences;
import me.wcy.music.utils.CoverLoader;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.ScreenUtils;
import me.wcy.music.utils.SystemUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.widget.AlbumCoverView;
import me.wcy.music.widget.IndicatorLayout;

/**
 * Created by TBS on 2018/3/8.
 */

public class SongPlayActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, OnPlayerEventListener,
        LrcView.OnPlayClickListener
{
    //@Bind(R.id.ll_content)
    private LinearLayout llContent;
    //@Bind(R.id.iv_play_page_bg)
    private ImageView ivPlayingBg;
    //@Bind(R.id.iv_back)
    private ImageView ivBack;
    //@Bind(R.id.tv_title)
    private TextView tvTitle;
    //@Bind(R.id.tv_artist)
    private TextView tvArtist;
    //@Bind(R.id.vp_play_page)
    private ViewPager vpPlay;
    //@Bind(R.id.il_indicator)
    private IndicatorLayout ilIndicator;
    //@Bind(R.id.sb_progress)
    private SeekBar sbProgress;
    //@Bind(R.id.tv_current_time)
    private TextView tvCurrentTime;
    //@Bind(R.id.tv_total_time)
    private TextView tvTotalTime;
    //@Bind(R.id.iv_mode)
    private ImageView ivMode;
    //@Bind(R.id.iv_play)
    private ImageView ivPlay;
    //@Bind(R.id.iv_next)
    private ImageView ivNext;
    //@Bind(R.id.iv_prev)
    private ImageView ivPrev;
    private ImageView ivMenu;
    private AlbumCoverView mAlbumCoverView;
    private LrcView mLrcViewSingle;
    private LrcView mLrcViewFull;
    private SeekBar sbVolume;

    private AudioManager mAudioManager;
    private List<View> mViewPagerContent;
    private int mLastProgress;
    private boolean isDraggingProgress;
    private List<OnlineMusic> mMusicList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_song);
        vpPlay = (ViewPager) findViewById(R.id.vp_play_page);
        ilIndicator = (IndicatorLayout) findViewById(R.id.il_indicator);
        sbProgress = (SeekBar) findViewById(R.id.sb_progress);
        ivPlayingBg = (ImageView) findViewById(R.id.iv_play_page_bg);
        llContent = (LinearLayout) findViewById(R.id.ll_content);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        ivMode = (ImageView) findViewById(R.id.iv_mode);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        ivPrev = (ImageView) findViewById(R.id.iv_prev);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivMenu.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        initSystemBar();
        initViewPager();
        ilIndicator.create(mViewPagerContent.size());
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
        vpPlay.addOnPageChangeListener(this);
        AudioPlayer.get().addOnPlayEventListener(this);
        if (getIntent().getExtras() != null) {
            String sourceId = getIntent().getStringExtra("sourceId");
            getMusic(sourceId);
        } else {
            Toast.makeText(SongPlayActivity.this, "获取歌曲信息失败,稍候重试~~", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void getMusic(String sourceId) {
        showProgress();
        OkGo.<String>get(MusicUtils.URL_MUSIC_INFO)//
                .tag(this)
                .params("sourceId", sourceId)
                .params("account", "")
                .params("LoginId", "")
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                JSONObject json = jsonArray.getJSONObject(0);
                                String id = json.getString("id");
                                String albumId = json.getString("albumId");
                                String artist = json.getString("artist");
                                String album = json.getString("album");
                                String title = json.getString("title");
                                String description = json.getString("description");
                                String duration = json.getString("duration");
                                String pic = json.getString("pic");
                                if (pic.contains(";"))
                                    pic = pic.substring(0, pic.lastIndexOf(";"));
                                String video = json.getString("video");
                                if (video.contains(";"))
                                    video = video.substring(0, video.lastIndexOf(";"));
                                String updateTime = json.getString("updateTime");
                                OnlineMusic onlineMusic = new OnlineMusic();
                                onlineMusic.setSong_id(id);
                                onlineMusic.setTitle(title);
                                onlineMusic.setArtist_name(artist);
                                onlineMusic.setLrclink("");
                                onlineMusic.setAlbum_title(album);
                                onlineMusic.setTing_uid(albumId);
                                onlineMusic.setPic_big(MusicUtils.HOST + "filePath/static/tbsermImage/special/" +
                                        pic);
                                onlineMusic.setPic_small(MusicUtils.HOST + "filePath/static/tbsermImage/special/"
                                        + pic);
                                onlineMusic.setDuration(Long.parseLong(duration) * 1000);
                                onlineMusic.setPath(MusicUtils.HOST + "filePath/static/tbsermVideo/special/" + video);
                                mMusicList.add(onlineMusic);
                                play(mMusicList, 0);
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            cancelProgress();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        cancelProgress();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Actions.VOLUME_CHANGED_ACTION);
        registerReceiver(mVolumeReceiver, filter);
    }

    /**
     * 沉浸式状态栏
     */
    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = ScreenUtils.getStatusBarHeight();
            llContent.setPadding(0, top, 0, 0);
        }
    }

    private void initViewPager() {
        View coverView = LayoutInflater.from(this).inflate(R.layout.fragment_play_page_cover, null);
        View lrcView = LayoutInflater.from(this).inflate(R.layout.fragment_play_page_lrc, null);
        mAlbumCoverView = (AlbumCoverView) coverView.findViewById(R.id.album_cover_view);
        mLrcViewSingle = (LrcView) coverView.findViewById(R.id.lrc_view_single);
        mLrcViewFull = (LrcView) lrcView.findViewById(R.id.lrc_view_full);
        sbVolume = (SeekBar) lrcView.findViewById(R.id.sb_volume);
        mAlbumCoverView.initNeedle(AudioPlayer.get().isPlaying());
        mLrcViewFull.setOnPlayClickListener(this);
        initVolume();

        mViewPagerContent = new ArrayList<>(2);
        mViewPagerContent.add(coverView);
        mViewPagerContent.add(lrcView);
        vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));
    }

    private void initVolume() {
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void initPlayMode() {
        int mode = Preferences.getPlayMode();
        ivMode.setImageLevel(mode);
    }

    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
        mAlbumCoverView.start();
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
        mAlbumCoverView.pause();
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int total, int progress) {
        if (!isDraggingProgress) {
            sbProgress.setMax(total);
            sbProgress.setProgress(progress);
        }

        if (mLrcViewSingle.hasLrc()) {
            mLrcViewSingle.updateTime(progress);
            mLrcViewFull.updateTime(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            onBackPressed();

        } else if (i == R.id.iv_mode) {
            switchPlayMode();

        } else if (i == R.id.iv_play) {
            play();

        } else if (i == R.id.iv_next) {
            next();

        } else if (i == R.id.iv_prev) {
            prev();

        } else if (i == R.id.iv_menu) {
            PlaylistFragment playlistFragment = new PlaylistFragment();
            playlistFragment.show(this.getSupportFragmentManager(), "playlistFragment");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ilIndicator.setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvTotalTime.setText(formatTime(seekBar.getMax()));
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
                int progress = seekBar.getProgress();
                AudioPlayer.get().seekTo(progress);

                if (mLrcViewSingle.hasLrc()) {
                    mLrcViewSingle.updateTime(progress);
                    mLrcViewFull.updateTime(progress);
                }
            } else {
                seekBar.setProgress(0);
            }
        } else if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    @Override
    public boolean onPlayClick(long time) {
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
            AudioPlayer.get().seekTo((int) time);
            if (AudioPlayer.get().isPausing()) {
                AudioPlayer.get().playPause();
            }
            return true;
        }
        return false;
    }

    private void onChangeImpl(Music music) {
        if (music == null) {
            return;
        }

        tvTitle.setText(music.getTitle());
        tvArtist.setText(music.getArtist());
        sbProgress.setProgress((int) AudioPlayer.get().getAudioPosition());
        sbProgress.setSecondaryProgress(0);
        sbProgress.setMax((int) music.getDuration());
        mLastProgress = 0;
        tvCurrentTime.setText(R.string.play_time_start);
        tvTotalTime.setText(formatTime(music.getDuration()));
        setCoverAndBg(music);
        setLrc(music);
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
            ivPlay.setSelected(true);
            mAlbumCoverView.start();
        } else {
            ivPlay.setSelected(false);
            mAlbumCoverView.pause();
        }
    }

    private void play(List<OnlineMusic> onlineMusic, int position) {
        new PlayOnlineMusics(this, onlineMusic)
        {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onListExecuteSuccess(List<Music> music) {
                cancelProgress();
                initPlayMode();
                AudioPlayer.get().addAndPlay(music, position);
                onChangeImpl(AudioPlayer.get().getPlayMusic());
                //ToastUtils.show("已添加到播放");
            }

            @Override
            public void onExecuteSuccess(Music music) {

            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(R.string.unable_to_play);
            }
        }.execute();
    }

    private void play() {
        AudioPlayer.get().playPause();
    }

    private void next() {
        AudioPlayer.get().next();
    }

    private void prev() {
        AudioPlayer.get().prev();
    }

    private void switchPlayMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf(Preferences.getPlayMode());
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtils.show(R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtils.show(R.string.mode_one);
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtils.show(R.string.mode_loop);
                break;
        }
        Preferences.savePlayMode(mode.value());
        initPlayMode();
    }
  @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void setCoverAndBg(Music music) {
        if (music.getCoverPath() != null && !music.getCoverPath().contains("content://")) {
            Glide.with(this).load(music.getCoverPath()).asBitmap().into(new SimpleTarget<Bitmap>()
            {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mAlbumCoverView.setCoverBitmap(CoverLoader.get().loadRound(resource));
                    ivPlayingBg.setImageBitmap(CoverLoader.get().loadBlur(resource));
                }
            }); //方法中设置asBitmap可以设置回调类型
        } else {
            mAlbumCoverView.setCoverBitmap(CoverLoader.get().loadRound(music));
            ivPlayingBg.setImageBitmap(CoverLoader.get().loadBlur(music));
        }

    }

    private void setLrc(final Music music) {
        if (music.getType() == Music.Type.LOCAL) {
            String lrcPath = FileUtils.getLrcFilePath(music);
            if (!TextUtils.isEmpty(lrcPath)) {
                loadLrc(lrcPath);
            } else {
                new SearchLrc(music.getArtist(), music.getTitle())
                {
                    @Override
                    public void onPrepare() {
                        // 设置tag防止歌词下载完成后已切换歌曲
                        vpPlay.setTag(music);
                        loadLrc("");
                        setLrcLabel("正在搜索歌词");
                    }

                    @Override
                    public void onListExecuteSuccess(List<String> t) {

                    }

                    @Override
                    public void onExecuteSuccess(@NonNull String lrcPath) {
                        if (vpPlay.getTag() != music) {
                            return;
                        }

                        // 清除tag
                        vpPlay.setTag(null);

                        loadLrc(lrcPath);
                        setLrcLabel("暂无歌词");
                    }

                    @Override
                    public void onExecuteFail(Exception e) {
                        if (vpPlay.getTag() != music) {
                            return;
                        }

                        // 清除tag
                        vpPlay.setTag(null);

                        setLrcLabel("暂无歌词");
                    }
                }.execute();
            }
        } else {
            String lrcPath = FileUtils.getLrcDir() + FileUtils.getLrcFileName(music.getArtist(), music.getTitle());
            loadLrc(lrcPath);
        }
    }

    private void loadLrc(String path) {
        File file = new File(path);
        mLrcViewSingle.loadLrc(file);
        mLrcViewFull.loadLrc(file);
    }

    private void setLrcLabel(String label) {
        mLrcViewSingle.setLabel(label);
        mLrcViewFull.setLabel(label);
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(mVolumeReceiver);
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }
}
