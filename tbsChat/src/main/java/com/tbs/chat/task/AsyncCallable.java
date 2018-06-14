package com.tbs.chat.task;



public interface AsyncCallable<T> {
	void call(final Callback<T> pCallback, final Callback<Exception> pExceptionCallback);
}
