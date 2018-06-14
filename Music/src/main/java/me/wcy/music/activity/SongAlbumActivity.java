package me.wcy.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import me.wcy.music.R;
import me.wcy.music.adapter.OnMoreClickListener;
import me.wcy.music.adapter.OnlineMusicAdapter;
import me.wcy.music.constants.Extras;
import me.wcy.music.enums.LoadStateEnum;
import me.wcy.music.executor.ControlPanel;
import me.wcy.music.executor.DownloadOnlineMusic;
import me.wcy.music.executor.PlayOnlineMusics;
import me.wcy.music.executor.ShareOnlineMusic;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.PlaylistFragment;
import me.wcy.music.model.Music;
import me.wcy.music.model.OnlineMusic;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.utils.FileUtils;
import me.wcy.music.utils.ImageUtils;
import me.wcy.music.utils.MusicUtils;
import me.wcy.music.utils.ScreenUtils;
import me.wcy.music.utils.ToastUtils;
import me.wcy.music.utils.ViewUtils;
import me.wcy.music.widget.AutoLoadListView;

public class SongAlbumActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener
        , OnMoreClickListener, AutoLoadListView.OnLoadListener
{
    private PlayFragment mPlayFragment;
    private boolean isPlayFragmentShow;
    //@Bind(R.id.lv_online_music_list)
    private AutoLoadListView lvOnlineMusic;
    //@Bind(R.id.ll_loading)
    private LinearLayout llLoading;
    //@Bind(R.id.ll_load_fail)
    private LinearLayout llLoadFail;
    //@Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;
    private View vHeader;
    //private SheetInfo mListInfo;
    //private OnlineMusicList mOnlineMusicList;
    private List<OnlineMusic> mMusicList = new ArrayList<>();
    private OnlineMusicAdapter mAdapter = new OnlineMusicAdapter(mMusicList);
    private int mOffset = 0;
    private ControlPanel controlPanel;
    private String albumPath, playlistName, playlistDetail, playlistAuthor;
    private String sourceId;
    private ImageView flMenuBar;
    private FrameLayout list_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_album);
        lvOnlineMusic = (AutoLoadListView) findViewById(R.id.lv_online_music_list);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        llLoadFail = (LinearLayout) findViewById(R.id.ll_load_fail);
        flPlayBar = (FrameLayout) findViewById(R.id.fl_play_bar);
        list_content = (FrameLayout) findViewById(R.id.list_content);
        flMenuBar = (ImageView) findViewById(R.id.iv_play_bar_playlist);
        initSystemBar();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void onServiceBound() {
//        mListInfo = (SheetInfo) getIntent().getSerializableExtra(Extras.MUSIC_LIST_TYPE);
        controlPanel = new ControlPanel(flPlayBar);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        if (getIntent().getExtras() != null) {
            sourceId = getIntent().getStringExtra("sourceId");
            getSpecial();
        } else {
            Toast.makeText(SongAlbumActivity.this, "获取专辑信息失败,稍候重试~~", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
        /**
     * 沉浸式状态栏
     */
    @SuppressLint("ResourceAsColor")
    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = ScreenUtils.getStatusBarHeight();
            list_content.setPadding(0, top, 0, 0);
        }
    }
    private void initView() {
        vHeader = LayoutInflater.from(this).inflate(R.layout.activity_online_music_list_header, null);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtils.dp2px(150));
        vHeader.setLayoutParams(params);
        lvOnlineMusic.addHeaderView(vHeader, null, false);
        lvOnlineMusic.setAdapter(mAdapter);
        lvOnlineMusic.setOnLoadListener(this);
        ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOADING);
        flPlayBar.setOnClickListener(this);
        flMenuBar.setOnClickListener(this);
        lvOnlineMusic.setOnItemClickListener(this);
        mAdapter.setOnMoreClickListener(this);
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void getSpecial() {
        showProgress();
        OkGo.<String>get(MusicUtils.URL_SPECIAL_INFO)//
                .tag(this)
                .params("account", "")
                .params("sourceId", sourceId)
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
                                //playlistCount = json.getString("audioNum");
                                playlistName = json.getString("name");
                                playlistAuthor = json.getString("author");
                                playlistDetail = json.getString("description");
                                String pic = json.getString("pic");
                                if (pic.contains(";"))
                                    pic = pic.substring(0, pic.lastIndexOf(";"));
                                albumPath = MusicUtils.HOST + "filePath/static/tbsermImage/special/" + pic;
                                //setTitle(playlistName);
                                initView();
                                onLoad();
                                //mCollected = PlaylistInfo.getInstance(mContext).hasPlaylist(Long.parseLong
                                // (playlsitId));
                                //setUpEverything();
                            } else {
                                Toast.makeText(SongAlbumActivity.this, "获取专辑信息失败,稍候重试~~", Toast.LENGTH_SHORT).show();
                                finish();
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

    private void getMusic() {
        showProgress();
        OkGo.<String>get(MusicUtils.URL_MUSIC_LIST)//
                .tag(this)
                .params("account", "")
                .params("sourceId", sourceId)
                .params("LoginId", "")
                .execute(new StringCallback()
                {
                    @Override
                    public void onSuccess(Response<String> response) {

                        lvOnlineMusic.onLoadComplete();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
                            boolean code = jsonObject.getBoolean("result");
                            if (code) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    OnlineMusic onlineMusic = new OnlineMusic();
                                    onlineMusic.setSong_id(json.getString("id"));
                                    onlineMusic.setTitle(json.getString("name"));
                                    onlineMusic.setArtist_name(playlistAuthor);
                                    onlineMusic.setLrclink("");
                                    onlineMusic.setAlbum_title(playlistName);
                                    String pic = json.getString("pic");
                                    if (pic.contains(";"))
                                        pic = pic.substring(0, pic.lastIndexOf(";"));
                                    onlineMusic.setPic_big(MusicUtils.HOST + "filePath/static/tbsermImage/special/" +
                                            pic);
                                    onlineMusic.setPic_small(MusicUtils.HOST + "filePath/static/tbsermImage/special/"
                                            + pic);
                                    String duration = json.getString("time");
                                    onlineMusic.setDuration(Long.parseLong(duration) * 1000);
                                    String video = json.getString("video");
                                    if (video.contains(";"))
                                        video = video.substring(0, video.lastIndexOf(";"));
                                    onlineMusic.setPath(MusicUtils.HOST + "filePath/static/tbsermVideo/special/" + video);
                                    mMusicList.add(onlineMusic);
                                }
                                initHeader();
                                ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum
                                        .LOAD_SUCCESS);
                                lvOnlineMusic.setEnable(false);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum
                                        .LOAD_FAIL);
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
                        lvOnlineMusic.onLoadComplete();
                        ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
                    }
                });
//        HttpClient.getSongListInfo(mListInfo.getType(), MUSIC_LIST_SIZE, offset, new HttpCallback<OnlineMusicList>()
//        {
//            @Override
//            public void onSuccess(OnlineMusicList response) {
//                lvOnlineMusic.onLoadComplete();
//                mOnlineMusicList = response;
//                if (offset == 0 && response == null) {
//                    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
//                    return;
//                } else if (offset == 0) {
//                    initHeader();
//                    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_SUCCESS);
//                }
//                if (response == null || response.getSong_list() == null || response.getSong_list().size() == 0) {
//                    lvOnlineMusic.setEnable(false);
//                    return;
//                }
//                mOffset += MUSIC_LIST_SIZE;
//                mMusicList.addAll(response.getSong_list());
//                mAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                lvOnlineMusic.onLoadComplete();
//                if (e instanceof RuntimeException) {
//                    // 歌曲全部加载完成
//                    lvOnlineMusic.setEnable(false);
//                    return;
//                }
//                if (offset == 0) {
//                    ViewUtils.changeViewState(lvOnlineMusic, llLoading, llLoadFail, LoadStateEnum.LOAD_FAIL);
//                } else {
//                    ToastUtils.show(R.string.load_fail);
//                }
//            }
//        });
    }

    @Override
    public void onLoad() {
        getMusic();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0)
            position = position - 1;
        play(mMusicList, position);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fl_play_bar) {
            showPlayingFragment();
        }else if (i == R.id.iv_play_bar_playlist) {
            PlaylistFragment playQueueFragment = new PlaylistFragment();
            playQueueFragment.show(getSupportFragmentManager(), "PlaylistFragment");
        }else if (i == R.id.iv_back) {
            onBackPressed();
        }
    }

    @Override
    public void onMoreClick(int position) {
        final OnlineMusic onlineMusic = mMusicList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(mMusicList.get(position).getTitle());
        String path = FileUtils.getMusicDir() + FileUtils.getMp3FileName(onlineMusic.getArtist_name(), onlineMusic
                .getTitle());
        File file = new File(path);
        int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
        dialog.setItems(itemsId, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    share(onlineMusic);
                    break;
                case 1:// 查看歌手信息
                    artistInfo(onlineMusic);
                    break;
                case 2:// 下载
                    download(onlineMusic);
                    break;
            }
        });
        dialog.show();
    }

    private void initHeader() {
        final ImageView ivHeaderBg = (ImageView) vHeader.findViewById(R.id.iv_header_bg);
        ImageView ivHeaderBack = (ImageView) vHeader.findViewById(R.id.iv_back);
        final ImageView ivCover = (ImageView) vHeader.findViewById(R.id.iv_cover);
        TextView tvTitle = (TextView) vHeader.findViewById(R.id.tv_title);
        TextView albumTitle = (TextView) vHeader.findViewById(R.id.tv_album_title);
        TextView tvUpdateDate = (TextView) vHeader.findViewById(R.id.tv_update_date);
        TextView tvComment = (TextView) vHeader.findViewById(R.id.tv_comment);
        tvTitle.setText(playlistName);
        albumTitle.setText(playlistName);
        ivHeaderBack.setOnClickListener(this);
        tvUpdateDate.setText(getString(R.string.recent_update, playlistAuthor));
        tvComment.setText(playlistDetail);
        Glide.with(this)
                .load(albumPath)
                .asBitmap()
                .placeholder(R.drawable.default_cover)
                .error(R.drawable.default_cover)
                .override(200, 200)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        ivCover.setImageBitmap(resource);
                        ivHeaderBg.setImageBitmap(ImageUtils.blur(resource));
                    }
                });
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
                AudioPlayer.get().addAndPlay(music, position);
                ToastUtils.show("已添加到播放");
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

    private void share(final OnlineMusic onlineMusic) {
        new ShareOnlineMusic(this, onlineMusic.getTitle(), onlineMusic.getSong_id())
        {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onListExecuteSuccess(List<Void> t) {

            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
            }
        }.execute();
    }

    private void artistInfo(OnlineMusic onlineMusic) {
        ArtistInfoActivity.start(this, onlineMusic.getTing_uid());
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayer.get().removeOnPlayEventListener(controlPanel);
    }

    private void download(final OnlineMusic onlineMusic) {
        new DownloadOnlineMusic(this, onlineMusic)
        {
            @Override
            public void onPrepare() {
                showProgress();
            }

            @Override
            public void onListExecuteSuccess(List<Void> t) {

            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                cancelProgress();
                ToastUtils.show(getString(R.string.now_download, onlineMusic.getTitle()));
            }

            @Override
            public void onExecuteFail(Exception e) {
                cancelProgress();
                ToastUtils.show(R.string.unable_to_download);
            }
        }.execute();

    }
}
