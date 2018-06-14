package com.tbs.tbsmis.Exam.mvp.contract;

import android.content.Context;

/**
 * Created by tbs on 16/4/1.
 */
public interface BaseView {
    void showLoading(String msg);
    void hideLoading();
    void LoadingProgress(int progress);
    void showError(String errorMsg);
    Context getBaseContext();

}
