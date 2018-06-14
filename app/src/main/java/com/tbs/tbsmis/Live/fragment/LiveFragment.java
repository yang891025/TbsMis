package com.tbs.tbsmis.Live.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.tbs.tbsmis.Live.adapter.LiveAllCloumnAdapter;
import com.tbs.tbsmis.R;

/**
 * Created by TBS on 2017/11/30.
 */

public class LiveFragment extends Fragment
{
    private SlidingTabLayout liveSlidingTab;
    private ViewPager liveViewpager;
    private String[] mTilte;
    private LiveAllCloumnAdapter mLiveAllColumnAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        this.initViews(view);
        return view;
    }

    public void initViews(View view) {
        liveSlidingTab = (SlidingTabLayout) view.findViewById(R.id.live_sliding_tab);
        liveViewpager = (ViewPager) view.findViewById(R.id.live_viewpager);
        mTilte = new String[2];
        mTilte[0] = "常用";
        mTilte[1] = "全部";
        liveViewpager.setOffscreenPageLimit(mTilte.length);
        mLiveAllColumnAdapter = new LiveAllCloumnAdapter(getChildFragmentManager(),mTilte);
        liveViewpager.setAdapter(mLiveAllColumnAdapter);
        mLiveAllColumnAdapter.notifyDataSetChanged();
        liveSlidingTab.setViewPager(liveViewpager, mTilte);
        liveSlidingTab.setCurrentTab(1);
    }
}
