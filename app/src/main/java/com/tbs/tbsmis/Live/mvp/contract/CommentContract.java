package com.tbs.tbsmis.Live.mvp.contract;

import com.tbs.circle.bean.CommentConfig;
import com.tbs.circle.bean.CommentItem;
import com.tbs.circle.bean.FavortItem;
import com.tbs.tbsmis.Live.bean.CommentAll;

import java.util.List;
import java.util.Map;

/**
 * Created by TBS on 2018/1/11.
 */

public interface CommentContract
{
    interface View extends BaseView
    {
        void getPraisesCallback(List<FavortItem> datas,List<CommentAll> Commentdatas);
        void addPraisesCallback(Map map,int position);
        void cancelPraisesCallback(int position,int commentPosition,int PraisesType);
        void getCommentsCallback(List<CommentAll> datas);
        void addCommentCallback(int circlePosition, CommentItem addItem);
        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);
    }

    interface Presenter extends BasePresenter
    {

        void getComments(Map map);
        void addComment(Map map,CommentConfig config);
        void deleteComment(Map map);
        void getPraises(Map map,List<CommentAll> datas);
        void addPraises(Map map,int position);
        void cancelPraises(Map map,int position,int commentPosition,int PraisesType);
    }
}
