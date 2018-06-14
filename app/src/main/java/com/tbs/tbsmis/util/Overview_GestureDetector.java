package com.tbs.tbsmis.util;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.webkit.WebView;

public class Overview_GestureDetector extends
		SimpleOnGestureListener {
	private final Activity Overview;
	private final WebView webview;

	public Overview_GestureDetector(Activity paramNewsOverviewActivity,
			WebView webview) {
        Overview = paramNewsOverviewActivity;
		this.webview = webview;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO
        this.Overview.finish();
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		float diffY = arg0.getY() - arg1.getY();
		float diffX = arg0.getX() - arg1.getX();
		// TODO Auto-generated method stub
		if (diffY > this.webview.getHeight() / 6 && Math.abs(arg3) > 20) {
            this.webview.loadUrl("javascript:jump()");
		} else if (diffX > 0 && Math.abs(diffX) > Math.abs(diffY)
				&& Math.abs(arg2) > 20) {
			// ��ת��һ��
            this.webview.loadUrl("javascript:tonext()");
            this.webview.loadUrl("javascript:jumppage(3)");
		} else if (diffX < 0 && Math.abs(diffX) > Math.abs(diffY)
				&& Math.abs(arg2) > 20) {
			// ��ת��һ��
            this.webview.loadUrl("javascript:toprev()");
            this.webview.loadUrl("javascript:jumppage(2)");
		}
		return false;
	}
}