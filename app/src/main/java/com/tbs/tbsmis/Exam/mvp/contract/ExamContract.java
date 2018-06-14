package com.tbs.tbsmis.Exam.mvp.contract;


import com.tbs.tbsmis.Exam.bean.AnswerInfo;

import java.util.List;

/**
 * Created by suneee on 2016/7/15.
 */
public interface ExamContract
{

    interface View extends BaseView
    {

        void updateData(List<AnswerInfo> datas);

    }

    interface Presenter extends com.tbs.tbsmis.Exam.mvp.presenter.BasePresenter
    {
        void loadData(String paperId);
        String getCurrentTime();
    }
}
