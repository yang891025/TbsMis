package me.wcy.music.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import me.wcy.music.R;
import me.wcy.music.adapter.FragmentAdapter;
import me.wcy.music.constants.Extras;
import me.wcy.music.constants.Keys;
import me.wcy.music.executor.ControlPanel;
import me.wcy.music.executor.NaviMenuExecutor;
import me.wcy.music.fragment.LocalMusicFragment;
import me.wcy.music.fragment.PlayFragment;
import me.wcy.music.fragment.PlaylistFragment;
import me.wcy.music.service.AudioPlayer;
import me.wcy.music.service.QuitTimer;
import me.wcy.music.utils.SystemUtils;

public class MusicActivity extends BaseActivity implements View.OnClickListener, QuitTimer.OnTimerListener,
        NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener
{
    // @Bind(R.id.drawer_layout)
    private DrawerLayout drawerLayout;
    //@Bind(R.id.navigation_view)
    private NavigationView navigationView;
    //@Bind(R.id.iv_menu)
    private ImageView ivMenu;
    // @Bind(R.id.iv_search)
    private ImageView ivSearch;
    // @Bind(R.id.tv_local_music)
    private TextView tvLocalMusic;
    //    @Bind(R.id.tv_online_music)
//    private TextView tvOnlineMusic;
    //   @Bind(R.id.viewpager)
    private ViewPager mViewPager;
    //@Bind(R.id.fl_play_bar)
    private FrameLayout flPlayBar;

    private View vNavigationHeader;
    private LocalMusicFragment mLocalMusicFragment;
    //private SheetListFragment mSheetListFragment;
    private PlayFragment mPlayFragment;
    private ControlPanel controlPanel;
    private NaviMenuExecutor naviMenuExecutor;
    private MenuItem timerItem;
    private boolean isPlayFragmentShow;
    private ImageView flMenuBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();
    }

    @Override
    protected void onServiceBound() {
        setupView();
        controlPanel = new ControlPanel(flPlayBar);
        naviMenuExecutor = new NaviMenuExecutor(this);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        QuitTimer.get().setOnTimerListener(this);
        parseIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    protected void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        tvLocalMusic = (TextView) findViewById(R.id.tv_local_music);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        flPlayBar = (FrameLayout) findViewById(R.id.fl_play_bar);
        flMenuBar = (ImageView) findViewById(R.id.iv_play_bar_playlist);
    }

    private void setupView() {
        // add navigation header
        vNavigationHeader = LayoutInflater.from(this).inflate(R.layout.navigation_header, navigationView, false);
        navigationView.addHeaderView(vNavigationHeader);

        // setup view pager
        mLocalMusicFragment = new LocalMusicFragment();
        //mSheetListFragment = new SheetListFragment();
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(mLocalMusicFragment);
        //adapter.addFragment(mSheetListFragment);
        mViewPager.setAdapter(adapter);
        tvLocalMusic.setSelected(true);

        ivMenu.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        tvLocalMusic.setOnClickListener(this);
        flMenuBar.setOnClickListener(this);
        flPlayBar.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = navigationView.getMenu().findItem(R.id.action_timer);
        }
        String title = getString(R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_menu) {
            drawerLayout.openDrawer(GravityCompat.START);

        } else if (i == R.id.iv_search) {
            startActivity(new Intent(this, SearchMusicActivity.class));

        } else if (i == R.id.tv_local_music) {
            mViewPager.setCurrentItem(0);

//            case R.id.tv_online_music:
//                mViewPager.setCurrentItem(1);
//                break;
        } else if (i == R.id.fl_play_bar) {
            showPlayingFragment();

        } else if (i == R.id.iv_play_bar_playlist) {
            PlaylistFragment playQueueFragment = new PlaylistFragment();
            playQueueFragment.show(getSupportFragmentManager(), "PlaylistFragment");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawers();
        handler.postDelayed(() -> item.setChecked(false), 500);
        return naviMenuExecutor.onNavigationItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
//            tvOnlineMusic.setSelected(false);
        } else {
            tvLocalMusic.setSelected(false);
//            tvOnlineMusic.setSelected(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
        mLocalMusicFragment.onSaveInstanceState(outState);
        //mSheetListFragment.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        mViewPager.post(() -> {
            mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
            mLocalMusicFragment.onRestoreInstanceState(savedInstanceState);
            //mSheetListFragment.onRestoreInstanceState(savedInstanceState);
        });
    }

    @Override
    protected void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(controlPanel);
        QuitTimer.get().setOnTimerListener(null);
        super.onDestroy();
    }
}
