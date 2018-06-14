package me.wcy.music.executor;

import java.util.List;

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
public interface IExecutor<T> {
    void execute();

    void onPrepare();
    void onListExecuteSuccess(List<T> t);
    void onExecuteSuccess(T t);
    void onExecuteFail(Exception e);
}
