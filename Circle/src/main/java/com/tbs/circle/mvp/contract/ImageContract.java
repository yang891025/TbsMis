package com.tbs.circle.mvp.contract;

import com.lzy.imagepicker.bean.ImageItem;

import java.util.List;

/**
 * Created by TBS on 2017/9/27.
 */

public interface ImageContract
{

    interface View extends BaseView{
           void finish();
    }

    interface Presenter extends BasePresenter
    {
        void upLoadImage( );
        void setPathList(List<ImageItem> pathList);
        void setContent(String content);
        String getContent();
        List<ImageItem> getPathList();
    }
}
