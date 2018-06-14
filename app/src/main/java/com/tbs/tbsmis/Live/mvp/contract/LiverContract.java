package com.tbs.tbsmis.Live.mvp.contract;

import java.util.Map;

/**
 * Created by TBS on 2018/1/4.
 */

public interface LiverContract
{
    interface View extends BaseView
    {
        void addentiomCallback();

    }

    interface Presenter extends BasePresenter
    {

        void addentiom(Map map);
    }
}
