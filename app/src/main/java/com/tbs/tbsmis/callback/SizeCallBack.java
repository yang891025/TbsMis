package com.tbs.tbsmis.callback;

public interface SizeCallBack {

	void onGlobalLayout();

	void getViewSize(int idx, int width, int height, int[] dims);
}
