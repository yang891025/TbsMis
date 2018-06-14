package com.tbs.circle.mvp.contract;

/**
 * Created by TBS on 2017/9/28.
 */

public interface VideoContract
{
    interface View extends BaseView{
        void finish();
    }

    interface Presenter extends BasePresenter
    {
        void upLoadVideo( );
        void setPathList(String path);
        void setContent(String content);
        String getContent();
        String getPath();
    }
}
