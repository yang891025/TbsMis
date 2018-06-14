package com.tbs.circle.mvp.contract;


/**
 * Created by TBS on 2017/9/28.
 */

public interface AudioContract
{
    interface View extends BaseView{
        void finish();
    }

    interface Presenter extends BasePresenter
    {
        void upLoadAudio( );
        void setPath(String path);
        void setLength(Long content);
        void setContent(String content);
        String getContent();
        Long getLength();
        String getPath();
    }
}