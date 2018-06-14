package com.tbs.tbsmis.Live.mvp.presenter;

import com.tbs.tbsmis.Live.mvp.contract.LiverContract;

import java.util.Map;

/**
 * Created by TBS on 2018/1/4.
 */

public class LiverPresenter implements LiverContract.Presenter
{
    private LiverContract.View view;

    public LiverPresenter(LiverContract.View view) {
        this.view = view;
    }

    @Override
    public void addentiom(Map map) {

    }
}
