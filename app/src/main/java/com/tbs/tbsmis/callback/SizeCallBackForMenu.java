package com.tbs.tbsmis.callback;

import android.widget.Button;

public class SizeCallBackForMenu implements SizeCallBack {

	private final Button menu;
	private int menuWidth;
	
	
	public SizeCallBackForMenu(Button menu){
        this.menu = menu;
	}
	@Override
	public void onGlobalLayout() {
        menuWidth = menu.getMeasuredWidth()+15;
	}

	@Override
	public void getViewSize(int idx, int width, int height, int[] dims) {
		dims[0] = width;
		dims[1] = height;
		
		/*视图不是中间视图*/
		if(idx != 1){
			dims[0] = width - menuWidth;
		}
	}

}
