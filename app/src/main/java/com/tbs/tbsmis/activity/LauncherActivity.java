package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.check.BookFisherPagerAdapter;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends Activity implements View.OnClickListener,
		View.OnTouchListener, GestureDetector.OnGestureListener {

	private static final String TAG = "LauncherActivity";

	private final int[] ids = { R.drawable.guider_01, R.drawable.guider_02 };
	private final List<View> guides = new ArrayList<View>();
	private ViewPager pager;
	private Button open;
	private ImageView curDot;
	private int offset;
	private int curPos;
	List<Location> loc;
	String url;
	GestureDetector mGestureDetector;// ������
	private String versionname;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_layout);
        this.init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
        this.mGestureDetector = new GestureDetector(this);
		View view = this.getLayoutInflater().inflate(R.layout.guider_open_app, null);
		for (int i = 0; i < this.ids.length; i++) {
			ImageView iv = new ImageView(this);
			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			iv.setImageResource(this.ids[i]);
			iv.setLayoutParams(params);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
            this.guides.add(iv);
		}
        this.guides.add(view);
        this.open = (Button) view.findViewById(R.id.beginBtn);
        this.open.setOnClickListener(this);
		Log.d(LauncherActivity.TAG, "guides length :" + this.guides.size());

        this.curDot = (ImageView) this.findViewById(R.id.cur_dot);
        this.curDot.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
                        LauncherActivity.this.offset = LauncherActivity.this.curDot.getWidth();
						return true;
					}
				});
		BookFisherPagerAdapter adapter = new BookFisherPagerAdapter(this.guides);
        this.pager = (ViewPager) this.findViewById(R.id.contentPager);
        this.pager.setAdapter(adapter);
        this.pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
                LauncherActivity.this.moveCursorTo(arg0);
				if (arg0 == 2) {
                    LauncherActivity.this.pager.setOnTouchListener(LauncherActivity.this);
				}
                LauncherActivity.this.curPos = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		try {
            this.versionname = this.getVersionName();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * �ƶ�ָ�뵽���ڵ�λ��
	 * 
	 * @param position
	 *            ָ�������ֵ
	 * 
	 **/
	private void moveCursorTo(int position) {
		// ʹ�þ��λ��
		TranslateAnimation anim = new TranslateAnimation(this.offset * this.curPos,
                this.offset * position, 0, 0);
		anim.setDuration(0);
		anim.setFillAfter(true);
        this.curDot.startAnimation(anim);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.beginBtn:
			UIHelper.setSharePerference(this,
					constants.SHARED_PREFERENCE_FIRST_LAUNCH, "launchState",
                    this.versionname);
			Intent intent = new Intent();
			intent.setClass(this, InitializeToolbarActivity.class);
            this.startActivity(intent);
			break;
		}

	}

	// �����¼�
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return this.mGestureDetector.onTouchEvent(event);
	}

	// �����¼�
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (e1.getX() - e2.getX() > 80 && Math.abs(velocityX) > 80) {
			UIHelper.setSharePerference(this,
					constants.SHARED_PREFERENCE_FIRST_LAUNCH, "launchState",
                    this.versionname);
			Intent intent = new Intent();
			intent.setClass(this, InitializeToolbarActivity.class);
            this.startActivity(intent);
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	// public void setSharePerference(String perferenceName) {
	// String versionname = null;
	// SharedPreferences setting = getSharedPreferences(perferenceName,
	// MODE_PRIVATE);
	// SharedPreferences.Editor editor = setting.edit();
	// try {
	// versionname = getVersionName();
	// } catch (Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// editor.putString("launchState", versionname);
	// editor.commit();
	// }

	@Override
	protected void onStop() {
		super.onStop();
        this.finish();
	}

	// 获取程序的版本号
	private String getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = this.getPackageManager();
		// getPackageName()���㵱ǰ��İ���0����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(this.getPackageName(),
				0);
		return packInfo.versionName;
	}
}
