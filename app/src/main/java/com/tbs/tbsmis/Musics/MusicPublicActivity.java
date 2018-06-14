package com.tbs.tbsmis.Musics;

public class MusicPublicActivity
//        extends BaseActivity implements View.OnClickListener, OnPlayerEventListener
{

//    private SeekBar sbProgress;
//    private TextView tvCurrentTime;
//    private TextView tvTotalTime;
//    private ImageView ivMode;
//    private ImageView ivPlay;
//    private ImageView ivNext;
//    private ImageView ivPrev;
//    private ImageView ivPlayingBg;
//    private ImageView ivBack;
//    private TextView tvTitle;
//    private TextView tvArtist;
//    private ServiceConnection mPlayServiceConnection;
//    private ProgressDialog mProgressDialog;
//    private PlayFragment mPlayFragment;
//    private boolean isPlayFragmentShow = false;
//    private Music music;
//    private LinearLayout llContent;
//    private TabLayout mTabLayout;
//    private ViewPager mViewPager;
//    private List<String> mTitleList;
//    private List<Fragment> list_fragment;
//    private showMusicContent mHotRec;
//    private showComments mFragmPraises;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_music_public);
//        initView();
//        initSystemBar();
//        if (AppCache.get().getPlayService() == null) {
//            startService();
//            mHandler.postDelayed(new Runnable()
//            {
//                @Override
//                public void run() {
//                    bindService();
//                }
//            }, 1000);
//        } else {
//            getPlayService().setOnPlayEventListener(MusicPublicActivity.this);
//            getData();
//            getMusic((int) music.getId());
//        }
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setMessage(getString(me.wcy.music.R.string.loading));
//    }
//
//    private void getData() {
//        Bundle bundle = this.getIntent().getExtras();
//        music = (Music) bundle.getSerializable("music");
//    }
//
//    private void startService() {
//        Intent intent = new Intent(this, PlayService.class);
//        startService(intent);
//    }
//
//    private void bindService() {
//        Intent intent = new Intent();
//        intent.setClass(this, PlayService.class);
//        mPlayServiceConnection = new PlayServiceConnection();
//        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    private class PlayServiceConnection implements ServiceConnection
//    {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            final PlayService playService = ((PlayService.PlayBinder) service).getService();
//            AppCache.get().setPlayService(playService);
//            PermissionReq.with(MusicPublicActivity.this)
//                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .result(new PermissionReq.Result()
//                    {
//                        @Override
//                        public void onGranted() {
//                            scanMusic(playService);
//                        }
//
//                        @Override
//                        public void onDenied() {
//                            ToastUtils.show(me.wcy.music.R.string.no_permission_storage);
//                            finish();
//                            playService.quit();
//                        }
//                    })
//                    .request();
//            getPlayService().setOnPlayEventListener(MusicPublicActivity.this);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//        }
//    }
//
//    private void scanMusic(final PlayService playService) {
//        playService.updateMusicList(new EventCallback<Void>()
//        {
//            @Override
//            public void onEvent(Void aVoid) {
//                //System.out.println("scanMusic");
//                getData();
//                getMusic((int) music.getId());
//            }
//        });
//    }
//
//    private void getMusic(final int sourceId) {
//        OkGo.<String>get(DataUtils.URL_MUSIC_INFO)//
//                .tag(this)
//                .params("sourceId", sourceId)
//                .params("account", "")
//                .params("LoginId", "")
//                .execute(new StringCallback()
//                {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.body().toString());// 转换为JSONObject
//                            boolean code = jsonObject.getBoolean("result");
//                            if (code) {
//                                JSONArray jsonArray = jsonObject.getJSONArray("data");
//                                JSONObject json = jsonArray.getJSONObject(0);
//                                String id = json.getString("id");
//                                String albumId = json.getString("albumId");
//                                String artist = json.getString("artist");
//                                String album = json.getString("album");
//                                String title = json.getString("title");
//                                String description = json.getString("description");
//                                String duration = json.getString("duration");
//                                String pic = json.getString("pic");
//                                if (pic.contains(";"))
//                                    pic = pic.substring(0, pic.lastIndexOf(";"));
//                                String video = json.getString("video");
//                                if (video.contains(";"))
//                                    video = video.substring(0, video.lastIndexOf(";"));
//                                String updateTime = json.getString("updateTime");
//                                Music music = new Music();
//                                music.setType(Music.Type.ONLINE);
//                                music.setTitle(title);
//                                music.setArtist(artist);
//                                music.setAlbum(album);
//                                music.setAlbumId(Long.parseLong(albumId));
//                                music.setCoverPath(DataUtils.HOST + "filePath/static/tbsermImage/special/" + pic);
//                                music.setId(Long.parseLong(id));
//                                music.setDuration(Long.parseLong(duration) * 1000);
//                                music.setFileName(description);
//                                music.setPath(DataUtils.HOST + "filePath/static/tbsermVideo/special/" + video);
//                                play(music);
//                            } else {
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                    }
//                });
//    }
//
//    private void play(Music onlineMusic) {
//        this.mViewPager = (ViewPager) findViewById(R.id.vp_view);
//        this.mTabLayout = (TabLayout) findViewById(R.id.tabs);
//        this.list_fragment = new ArrayList<>();
//        mTitleList = new ArrayList<String>();
//        if (!onlineMusic.getFileName().isEmpty()) {
//            this.mHotRec = showMusicContent.newInstance(onlineMusic.getId()+"");
//            this.list_fragment.add(this.mHotRec);
//            this.mTitleList.add("详情");
//        }
//        this.mFragmPraises = showComments.newInstance(onlineMusic.getId() + "", "13");
//        this.list_fragment.add(this.mFragmPraises);
//        this.mTitleList.add("评论互动");
//
//        this.mTabLayout.setTabMode(TabLayout.MODE_FIXED);// 设置tab模式，当前为系统默认模式
//        this.mViewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager())
//        {
//            @Override
//            public Fragment getItem(int position) {
//                return list_fragment.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                //return super.getItemId(position);
//                if (list_fragment != null) {
//                    if (position < list_fragment.size()) {
//                        return list_fragment.get(position).hashCode();
//                    }
//                }
//                return super.getItemId(position);
//            }
//
//            @Override
//            public int getCount() {
//                return mTitleList.size();
//            }
//
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return mTitleList.get(position);
//            }
//        });
//        this.mTabLayout.setupWithViewPager(this.mViewPager);// 将TabLayout和ViewPager关联起来。
//        new PlaySpeicalMusic(this, onlineMusic)
//        {
//            @Override
//            public void onPrepare() {
//                mProgressDialog.show();
//            }
//
//            @Override
//            public void onExecuteSuccess(Music music) {
//                mProgressDialog.cancel();
//                getPlayService().play(music);
//
//                ToastUtils.show(getString(me.wcy.music.R.string.now_play, music.getTitle()));
//            }
//
//            @Override
//            public void onExecuteFail(Exception e) {
//                mProgressDialog.cancel();
//                ToastUtils.show(me.wcy.music.R.string.unable_to_play);
//            }
//        }.execute();
//    }
//
//    protected void initView() {
//        sbProgress = (SeekBar) findViewById(me.wcy.music.R.id.sb_progress);
//        ivPlayingBg = (ImageView) findViewById(me.wcy.music.R.id.iv_play_page_bg);
//        ivBack = (ImageView) findViewById(me.wcy.music.R.id.iv_back);
//        llContent = (LinearLayout) findViewById(me.wcy.music.R.id.ll_content);
//        tvTitle = (TextView) findViewById(me.wcy.music.R.id.tv_title);
//        tvArtist = (TextView) findViewById(me.wcy.music.R.id.tv_artist);
//        tvCurrentTime = (TextView) findViewById(me.wcy.music.R.id.tv_current_time);
//        tvTotalTime = (TextView) findViewById(me.wcy.music.R.id.tv_total_time);
//        ivMode = (ImageView) findViewById(me.wcy.music.R.id.iv_mode);
//        ivPlay = (ImageView) findViewById(me.wcy.music.R.id.iv_play);
//        ivNext = (ImageView) findViewById(me.wcy.music.R.id.iv_next);
//        ivPrev = (ImageView) findViewById(me.wcy.music.R.id.iv_prev);
//        //ivMode.setVisibility(View.INVISIBLE);
//        ivNext.setVisibility(View.INVISIBLE);
//        ivPrev.setVisibility(View.INVISIBLE);
//        ivPlay.setOnClickListener(this);
//        ivBack.setOnClickListener(this);
////        tvArtist.setOnClickListener(this);
////        tvTitle.setOnClickListener(this);
//        llContent.setOnClickListener(this);
//
//    }
//
//    /**
//     * 沉浸式状态栏
//     */
//    @SuppressLint("ResourceAsColor")
//    private void initSystemBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            int top = ScreenUtils.getStatusBarHeight();
//            llContent.setPadding(0, top, 0, 0);
//        }
//    }
//
//    public PlayService getPlayService() {
//        PlayService playService = AppCache.get().getPlayService();
//        if (playService == null) {
//            throw new NullPointerException("play service is null");
//        }
//        return playService;
//    }
//
//    @Override
//    public void onClick(View view) {
//        int i = view.getId();
//        if (i == me.wcy.music.R.id.iv_back) {
//            onBackPressed();
//        } else if (i == me.wcy.music.R.id.iv_play) {
//            play();
//        } else {
//            showPlayingFragment();
//        }
//    }
//
//    @Override
//    public void onChange(Music music) {
//        onChangeImpl(music);
//        if (mPlayFragment != null && mPlayFragment.isAdded()) {
//            mPlayFragment.onChange(music);
//        }
//    }
//
//    @Override
//    public void onPlayerStart() {
//        ivPlay.setSelected(true);
//        if (mPlayFragment != null && mPlayFragment.isAdded()) {
//            mPlayFragment.onPlayerStart();
//        }
//    }
//
//    @Override
//    public void onPlayerPause() {
//        ivPlay.setSelected(false);
//        if (mPlayFragment != null && mPlayFragment.isAdded()) {
//            mPlayFragment.onPlayerPause();
//        }
//    }
//
//    @Override
//    public void onPublish(int progress) {
//        tvCurrentTime.setText(formatTime(progress));
//        sbProgress.setProgress(progress);
//        if (mPlayFragment != null && mPlayFragment.isAdded()) {
//            mPlayFragment.onPublish(progress);
//        }
//    }
//
//    @Override
//    public void onBufferingUpdate(int percent) {
//
//    }
//
//    @Override
//    public void onTimer(long remain) {
//
//    }
//
//    @Override
//    public void onMusicListUpdate() {
//    }
//
//    private void onChangeImpl(Music music) {
//        if (music == null) {
//            return;
//        }
//
//        tvTitle.setText(music.getTitle());
//        tvArtist.setText(music.getArtist());
//        ivPlay.setSelected(getPlayService().isPlaying() || getPlayService().isPreparing());
//        sbProgress.setProgress((int) getPlayService().getCurrentPosition());
//        sbProgress.setSecondaryProgress(0);
//        sbProgress.setMax((int) music.getDuration());
//        tvCurrentTime.setText(me.wcy.music.R.string.play_time_start);
//        tvTotalTime.setText(formatTime(music.getDuration()));
//        if (getPlayService().isPlaying() || getPlayService().isPreparing()) {
//            ivPlay.setSelected(true);
//        } else {
//            ivPlay.setSelected(false);
//        }
//    }
//
//    private void play() {
//        getPlayService().playPause();
//    }
//
//    private void next() {
//        getPlayService().next();
//    }
//
//    private void showPlayingFragment() {
//        if (isPlayFragmentShow) {
//            return;
//        }
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(me.wcy.music.R.anim.fragment_slide_up, 0);
//        if (mPlayFragment == null) {
//            mPlayFragment = new PlayFragment();
//            ft.replace(android.R.id.content, mPlayFragment);
//        } else {
//            ft.show(mPlayFragment);
//        }
//        ft.commitAllowingStateLoss();
//        isPlayFragmentShow = true;
//    }
//
//    private void hidePlayingFragment() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.setCustomAnimations(0, me.wcy.music.R.anim.fragment_slide_down);
//        ft.hide(mPlayFragment);
//        ft.commitAllowingStateLoss();
//        isPlayFragmentShow = false;
//    }
//
//    private String formatTime(long time) {
//        return SystemUtils.formatTime("mm:ss", time);
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (mPlayFragment != null && isPlayFragmentShow) {
//            hidePlayingFragment();
//            return;
//        }
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (mPlayServiceConnection != null) {
//            unbindService(mPlayServiceConnection);
//        }
//        super.onDestroy();
//    }
}
