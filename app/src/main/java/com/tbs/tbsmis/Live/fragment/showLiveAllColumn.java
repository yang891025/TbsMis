package com.tbs.tbsmis.Live.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tbs.circle.widgets.DivItemDecoration;
import com.tbs.tbsmis.Live.adapter.LiveAllListAdapter;
import com.tbs.tbsmis.Live.bean.LiveAlllist;
import com.tbs.tbsmis.Live.mvp.contract.LiveContract;
import com.tbs.tbsmis.Live.mvp.presenter.LivePresenter;
import com.tbs.tbsmis.R;

import java.util.List;

/**
 * Created by TBS on 2017/11/30.
 */

public class showLiveAllColumn extends Fragment implements LiveContract.View
{
    private LivePresenter presenter;
    private SuperRecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    //    起始位置
    private  int offset = 0;
    //    每页加载数量
    private  int limit = 20;
    private LiveAllListAdapter mLiveAllListAdapter;

    public static showLiveAllColumn newInstance() {
        showLiveAllColumn fragment = new showLiveAllColumn();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_live_allcolumn, container, false);
        this.initViews(view);
        return view;
    }

    public void initViews(View view) {
        presenter = new LivePresenter(this);
        recyclerView = (SuperRecyclerView) view.findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this.getBaseContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DivItemDecoration(2, false));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        offset = 1;
                        presenter.loadData(offset);
                    }
                },10);
            }
        };
        recyclerView.setRefreshListener(refreshListener);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    Glide.with(showLiveAllColumn.this).resumeRequests();
                }else{
                    Glide.with(showLiveAllColumn.this).pauseRequests();
                }

            }
        });

        mLiveAllListAdapter = new LiveAllListAdapter(this.getBaseContext());
        recyclerView.setAdapter(mLiveAllListAdapter);
        //实现自动下拉刷新功能
        recyclerView.getSwipeToRefresh().post(new Runnable(){
            @Override
            public void run() {
                recyclerView.setRefreshing(true);//执行下拉刷新的动画
                refreshListener.onRefresh();//执行数据加载操作
            }
        });
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void LoadingProgress(int progress) {

    }

    @Override
    public void showError(String errorMsg) {

    }

    @Override
    public Context getBaseContext() {
        return this.getContext();
    }


    @Override
    public void update2loadData(int loadType, List<LiveAlllist> datas) {
        if(datas.size() < 20 && datas.size() > 0) {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }else{
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.loadData(offset++);
                        }
                    }, 10);
                }
            }, 1);
        }
        if (loadType == 1){
            recyclerView.setRefreshing(false);
            mLiveAllListAdapter.setDatas(datas);
        }else{
            mLiveAllListAdapter.getDatas().addAll(datas);
        }

        mLiveAllListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if(presenter !=null){
            presenter.recycle();
        }
        super.onDestroy();
    }
}
