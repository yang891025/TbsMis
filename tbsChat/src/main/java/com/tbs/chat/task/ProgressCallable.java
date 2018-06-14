package com.tbs.chat.task;

public interface ProgressCallable<T> {
    T call(final IProgressListener pProgressListener) throws Exception;
}
