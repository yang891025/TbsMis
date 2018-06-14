package com.tbs.tbsmis.util;

import com.tbs.tbsmis.activity.DetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class Detail_GestureDetector extends
		SimpleOnGestureListener {
	private final Activity Detail;
	private final String resname;
	private final int screenWidth;
	//private int screenHight;

	public Detail_GestureDetector(DetailActivity paramNewsDetailActivity,
			String Resname) {
        Detail = paramNewsDetailActivity;
        resname = Resname;
		DisplayMetrics dm = new DisplayMetrics();
        Detail.getWindowManager().getDefaultDisplay().getMetrics(dm);

        this.screenWidth = dm.widthPixels;
		//screenHight = dm.heightPixels;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO
        this.Detail.finish();
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float diffX = e1.getX() - e2.getX();
		float diffY = e1.getY() - e2.getY();
		Intent intent = new Intent();
		if (diffX > this.screenWidth / 3 && Math.abs(diffX) > Math.abs(diffY)
				&& Math.abs(velocityX) > 20) {
			// ��ת��һ��
			intent.setAction(this.resname);
			intent.putExtra("flag", 1);
            this.Detail.sendBroadcast(intent);
			// DetailActivity.goBtn.callOnClick();
		} else if (diffX < -(this.screenWidth / 3)
				&& Math.abs(diffX) > Math.abs(diffY)
				&& Math.abs(velocityX) > 20) {
			// ��ת��һ��
			intent.setAction(this.resname);
			intent.putExtra("flag", 2);
            this.Detail.sendBroadcast(intent);
			// DetailActivity.backBtn.callOnClick();
		}
		return false;
	}
}