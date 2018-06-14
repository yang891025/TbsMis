package com.tbs.chat.ui.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

/****
 * 锟斤拷锟斤拷锟斤拷要锟斤拷锟阶硷拷锟斤拷锟斤拷锟斤拷执锟叫碉拷锟斤拷锟教ｏ拷 锟斤拷锟斤拷ImageView锟角继筹拷锟斤拷View锟斤拷锟斤拷锟斤拷.
 * onLayout锟斤拷锟斤拷锟斤拷锟斤拷一锟斤拷锟截碉拷锟斤拷锟斤拷.锟矫凤拷锟斤拷锟斤拷锟斤拷锟斤拷View锟叫碉拷layout锟斤拷锟斤拷锟斤拷执锟叫ｏ拷锟斤拷执锟斤拷layout锟斤拷锟斤拷前锟斤拷锟斤拷锟斤拷锟街达拷锟絪etFrame锟斤拷锟斤拷.
 * layout锟斤拷锟斤拷锟斤拷
 * setFrame锟斤拷锟斤拷锟斤拷锟叫讹拷锟斤拷锟角碉拷View锟角凤拷锟斤拷锟戒化锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷浠拷锟斤拷锟矫达拷锟斤拷锟斤拷碌锟絣锟斤拷t锟斤拷r锟斤拷b锟斤拷锟捷革拷View锟斤拷然锟斤拷刷锟铰斤拷锟叫讹拷态锟斤拷锟斤拷UI.
 * 锟斤拷锟揭凤拷锟斤拷ture.没锟叫变化锟斤拷锟斤拷false.
 * invalidate锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷刷锟铰碉拷前锟截硷拷,
 * @author zhangjia
 */
public class MyImageView extends ImageView {

	private static final String TAG = "MyImageView";
	private Activity mActivity;
	private int screen_W, screen_H;// 锟缴硷拷锟斤拷幕锟侥匡拷叨锟�
	private int bitmap_W, bitmap_H;// 锟斤拷前图片锟斤拷锟�
	private int MAX_W, MAX_H, MIN_W, MIN_H;// 锟斤拷锟斤拷值
	private int current_Top, current_Right, current_Bottom, current_Left;// 锟斤拷前图片锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	private int start_Top = -1, start_Right = -1, start_Bottom = -1, start_Left = -1;// 锟斤拷始锟斤拷默锟斤拷位锟斤拷.
	private int start_x, start_y, current_x, current_y;// 锟斤拷锟斤拷位锟斤拷
	private float beforeLenght, afterLenght;// 锟斤拷锟斤拷锟斤拷锟斤拷锟�
	private float scale_temp;// 锟斤拷锟脚憋拷锟斤拷
	private GestureDetector gestureScanner;//锟斤拷锟斤拷锟斤拷
	private LinearLayout topView;
	private LinearLayout bottomView;

	/*
	 * 模式 NONE锟斤拷锟斤拷 DRAG锟斤拷锟斤拷拽. ZOOM:锟斤拷锟斤拷
	 */
	private enum MODE {
		NONE, DRAG, ZOOM
	}

    private MODE mode = MODE.NONE;// 默锟斤拷模式
	private boolean isControl_V = false;// 锟斤拷直锟斤拷锟�
	private boolean isControl_H = false;// 水平锟斤拷锟�
	private ScaleAnimation scaleAnimation;// 锟斤拷锟脚讹拷锟斤拷
	private boolean isScaleAnim = false;// 锟斤拷锟脚讹拷锟斤拷
	private MyAsyncTask myAsyncTask;// 锟届步锟斤拷锟斤拷

	/** 锟斤拷锟届方锟斤拷 **/
	public MyImageView(Context context) {
		super(context);
	}
	
	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
		gestureScanner = new GestureDetector(new MySimpleGesture());
	}

	/** 锟缴硷拷锟斤拷幕锟斤拷锟� **/
	public void setScreen_W(int screen_W) {
		this.screen_W = screen_W;
	}

	/** 锟缴硷拷锟斤拷幕锟竭讹拷 **/
	public void setScreen_H(int screen_H) {
		this.screen_H = screen_H;
	}

	/***
	 * 锟斤拷锟斤拷锟斤拷示图片
	 */
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		/** 锟斤拷取图片锟斤拷锟� **/
		bitmap_W = bm.getWidth();
		bitmap_H = bm.getHeight();

		MAX_W = bitmap_W * 3;
		MAX_H = bitmap_H * 3;

		MIN_W = bitmap_W / 2;
		MIN_H = bitmap_H / 2;

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (start_Top == -1) {
			start_Top = top;
			start_Left = left;
			start_Bottom = bottom;
			start_Right = right;
		}

	}

	/***
	 * touch 锟铰硷拷
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		/** 锟斤拷锟斤拷锟姐、锟斤拷愦ワ拷锟� **/
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			onTouchDown(event);
			break;
		// 锟斤拷愦ワ拷锟�
		case MotionEvent.ACTION_POINTER_DOWN:
			onPointerDown(event);
			break;

		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			mode = MODE.NONE;
			break;

		// 锟斤拷锟斤拷煽锟�
		case MotionEvent.ACTION_POINTER_UP:
			mode = MODE.NONE;
			/** 执锟斤拷锟斤拷锟脚伙拷原 **/
			if (isScaleAnim) {
				doScaleAnim();
			}
			break;
		}
		return true;
	}

	/** 锟斤拷锟斤拷 **/
	void onTouchDown(MotionEvent event) {
		mode = MODE.DRAG;

		current_x = (int) event.getRawX();
		current_y = (int) event.getRawY();

		start_x = (int) event.getX();
		start_y = current_y - this.getTop();

	}

	/** 锟斤拷锟斤拷锟斤拷指 只锟杰放达拷锟斤拷小 **/
	void onPointerDown(MotionEvent event) {
		if (event.getPointerCount() == 2) {
			mode = MODE.ZOOM;
			beforeLenght = getDistance(event);// 锟斤拷取锟斤拷锟斤拷木锟斤拷锟�
		}
	}

	/** 锟狡讹拷锟侥达拷锟斤拷 **/
	void onTouchMove(MotionEvent event) {
		int left = 0, top = 0, right = 0, bottom = 0;
		/** 锟斤拷锟斤拷锟较讹拷 **/
		if (mode == MODE.DRAG) {

			/** 锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷锟叫断达拷锟斤拷锟斤拷止锟斤拷drag时锟斤拷越锟斤拷 **/

			/** 锟斤拷取锟斤拷应锟斤拷l锟斤拷t,r ,b **/
			left = current_x - start_x;
			right = current_x + this.getWidth() - start_x;
			top = current_y - start_y;
			bottom = current_y - start_y + this.getHeight();

			/** 水平锟斤拷锟斤拷锟叫讹拷 **/
			if (isControl_H) {
				if (left >= 0) {
					left = 0;
					right = this.getWidth();
				}
				if (right <= screen_W) {
					left = screen_W - this.getWidth();
					right = screen_W;
				}
			} else {
				left = this.getLeft();
				right = this.getRight();
			}
			/** 锟斤拷直锟叫讹拷 **/
			if (isControl_V) {
				if (top >= 0) {
					top = 0;
					bottom = this.getHeight();
				}
				if (bottom <= screen_H) {
					top = screen_H - this.getHeight();
					bottom = screen_H;
				}
			} else {
				top = this.getTop();
				bottom = this.getBottom();
			}
			if (isControl_H || isControl_V){
				this.setPosition(left, top, right, bottom);
			}
			current_x = (int) event.getRawX();
			current_y = (int) event.getRawY();

		}
		/** 锟斤拷锟斤拷锟斤拷锟斤拷 **/
		else if (mode == MODE.ZOOM) {
			afterLenght = getDistance(event);// 锟斤拷取锟斤拷锟斤拷木锟斤拷锟�
			float gapLenght = afterLenght - beforeLenght;// 锟戒化锟侥筹拷锟斤拷
			if (Math.abs(gapLenght) > 5f) {
				scale_temp = afterLenght / beforeLenght;// 锟斤拷锟斤拷锟斤拷诺谋锟斤拷锟�
				this.setScale(scale_temp);
				beforeLenght = afterLenght;
			}
		}

	}

	/** 锟斤拷取锟斤拷锟斤拷木锟斤拷锟� **/
	float getDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);

		return FloatMath.sqrt(x * x + y * y);
	}

	/** 实锟街达拷锟斤拷锟较讹拷 **/
	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

	/** 锟斤拷锟斤拷锟斤拷锟斤拷 **/
	void setScale(float scale) {
		int disX = (int) (this.getWidth() * Math.abs(1 - scale)) / 4;// 锟斤拷取锟斤拷锟斤拷水平锟斤拷锟斤拷
		int disY = (int) (this.getHeight() * Math.abs(1 - scale)) / 4;// 锟斤拷取锟斤拷锟脚达拷直锟斤拷锟斤拷
		if (scale > 1 && this.getWidth() <= MAX_W) {// 锟脚达拷
			current_Left = this.getLeft() - disX;
			current_Top = this.getTop() - disY;
			current_Right = this.getRight() + disX;
			current_Bottom = this.getBottom() + disY;

			this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
			/**
			 * 锟斤拷时锟斤拷为锟斤拷锟角碉拷锟皆称ｏ拷锟斤拷锟斤拷只锟斤拷一锟斤拷锟叫断就匡拷锟斤拷锟剿★拷
			 */
            // 锟斤拷锟斤拷锟斤拷直锟斤拷锟�
            isControl_V = current_Top <= 0 && current_Bottom >= screen_H;
            // 锟斤拷锟斤拷水平锟斤拷锟�
            isControl_H = current_Left <= 0 && current_Right >= screen_W;
		}else if (scale < 1 && this.getWidth() >= MIN_W) {// 锟斤拷小
			current_Left = this.getLeft() + disX;
			current_Top = this.getTop() + disY;
			current_Right = this.getRight() - disX;
			current_Bottom = this.getBottom() - disY;
			/**
			 * 锟斤拷锟斤拷锟斤拷要锟斤拷锟斤拷锟斤拷锟脚达拷锟斤拷
			 */
			// 锟较憋拷越锟斤拷
			if (isControl_V && current_Top > 0) {
				current_Top = 0;
				current_Bottom = this.getBottom() - 2 * disY;
				if (current_Bottom < screen_H) {
					current_Bottom = screen_H;
					isControl_V = false;// 锟截闭达拷直锟斤拷锟斤拷
				}
			}
			// 锟铰憋拷越锟斤拷
			if (isControl_V && current_Bottom < screen_H) {
				current_Bottom = screen_H;
				current_Top = this.getTop() + 2 * disY;
				if (current_Top > 0) {
					current_Top = 0;
					isControl_V = false;// 锟截闭达拷直锟斤拷锟斤拷
				}
			}

			// 锟斤拷锟皆斤拷锟�
			if (isControl_H && current_Left >= 0) {
				current_Left = 0;
				current_Right = this.getRight() - 2 * disX;
				if (current_Right <= screen_W) {
					current_Right = screen_W;
					isControl_H = false;// 锟截憋拷
				}
			}
			// 锟揭憋拷越锟斤拷
			if (isControl_H && current_Right <= screen_W) {
				current_Right = screen_W;
				current_Left = this.getLeft() + 2 * disX;
				if (current_Left >= 0) {
					current_Left = 0;
					isControl_H = false;// 锟截憋拷
				}
			}
			if (isControl_H || isControl_V) {
				this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
			} else {
				this.setFrame(current_Left, current_Top, current_Right, current_Bottom);
				isScaleAnim = true;// 锟斤拷锟斤拷锟斤拷锟脚讹拷锟斤拷
			}
		}
	}

	/***
	 * 锟斤拷锟脚讹拷锟斤拷锟斤拷锟斤拷
	 */
	public void doScaleAnim() {
		myAsyncTask = new MyAsyncTask(screen_W, this.getWidth(), this.getHeight());
		myAsyncTask.setLTRB(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
		myAsyncTask.execute();
		isScaleAnim = false;// 锟截闭讹拷锟斤拷
	}

	/***
	 * 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	 */
	class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
		
		private int screen_W, current_Width, current_Height;
		private int left, top, right, bottom;
		private float scale_WH;// 锟斤拷叩谋锟斤拷锟�

		/** 锟斤拷前锟斤拷位锟斤拷锟斤拷锟斤拷 **/
		public void setLTRB(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}

		private float STEP = 8f;// 锟斤拷锟斤拷
		private float step_H, step_V;// 水平锟斤拷锟斤拷锟斤拷锟斤拷直锟斤拷锟斤拷

		public MyAsyncTask(int screen_W, int current_Width, int current_Height) {
			super();
			this.screen_W = screen_W;
			this.current_Width = current_Width;
			this.current_Height = current_Height;
			scale_WH = (float) current_Height / current_Width;
			step_H = STEP;
			step_V = scale_WH * STEP;
		}

		@Override
		protected Void doInBackground(Void... params) {

			while (current_Width <= screen_W) {

				left -= step_H;
				top -= step_V;
				right += step_H;
				bottom += step_V;

				current_Width += 2 * step_H;

				left = Math.max(left, start_Left);
				top = Math.max(top, start_Top);
				right = Math.min(right, start_Right);
				bottom = Math.min(bottom, start_Bottom);
				onProgressUpdate(left, top, right, bottom);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(final Integer... values) {
			super.onProgressUpdate(values);
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setFrame(values[0], values[1], values[2], values[3]);
				}
			});
		}
	}
	
	private class MySimpleGesture extends SimpleOnGestureListener {
		
		boolean isShowView = true;

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if(isShowView){
				getTopView().setVisibility(View.INVISIBLE);
				isShowView = false;
			}else{
				getTopView().setVisibility(View.VISIBLE);
				isShowView = true;
			}
			return true;
		}
	}

	public LinearLayout getTopView() {
		return topView;
	}

	public void setTopView(LinearLayout topView) {
		this.topView = topView;
	}

	public LinearLayout getBottomView() {
		return bottomView;
	}

	public void setBottomView(LinearLayout bottomView) {
		this.bottomView = bottomView;
	}
	
}
