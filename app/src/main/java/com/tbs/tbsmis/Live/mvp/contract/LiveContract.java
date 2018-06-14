package com.tbs.tbsmis.Live.mvp.contract;

import com.tbs.tbsmis.Live.bean.LiveAlllist;

import java.util.List;

/**
 * Created by suneee on 2016/7/15.
 */
public interface LiveContract
{

    interface View extends BaseView
    {

        void update2loadData(int loadType, List<LiveAlllist> datas);

    }

    interface Presenter extends BasePresenter
    {
        void loadData(int loadType);
        String getCurrentTime();
    }
}
