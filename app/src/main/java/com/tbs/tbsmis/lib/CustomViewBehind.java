package com.tbs.tbsmis.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tbs.tbsmis.R;

public class CustomViewBehind extends ViewGroup {

	private static final String TAG = "CustomViewBehind";

	public static int MARGIN_THRESHOLD = 10; // dips
	private int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;

	private CustomViewAbove mViewAbove;

	private View mContent;
	private View mSecondaryContent;
	private final int mMarginThreshold;
	private int mWidthOffset;
	private int mRightWidthOffset;
	private SlidingMenu.CanvasTransformer mTransformer;
	private boolean mChildrenEnabled;

	public CustomViewBehind(Context context) {
		this(context, null);
	}

	public CustomViewBehind(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.mMarginThreshold = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, CustomViewBehind.MARGIN_THRESHOLD, this.getResources()
						.getDisplayMetrics());
	}

	public void setCustomViewAbove(CustomViewAbove customViewAbove) {
        this.mViewAbove = customViewAbove;
	}

	public void setCanvasTransformer(SlidingMenu.CanvasTransformer t) {
        this.mTransformer = t;
	}

	public void setWidthOffset(int i) {
        this.mWidthOffset = i;
        this.requestLayout();
	}

	public int getBehindWidth() {
		return this.mContent.getWidth();
	}

	public void setContent(View v) {
		if (this.mContent != null)
            this.removeView(this.mContent);
        this.mContent = v;
        this.addView(this.mContent);
	}

	public View getContent() {
		return this.mContent;
	}

	/**
	 * Sets the secondary (right) menu for use when setMode is called with
	 * SlidingMenu.LEFT_RIGHT.
	 * 
	 * @param v
	 *            the right menu
	 */
	public void setSecondaryContent(View v) {
		if (this.mSecondaryContent != null)
            this.removeView(this.mSecondaryContent);
        this.mSecondaryContent = v;
        this.addView(this.mSecondaryContent);
	}

	public View getSecondaryContent() {
		return this.mSecondaryContent;
	}

	public void setChildrenEnabled(boolean enabled) {
        this.mChildrenEnabled = enabled;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		if (this.mTransformer != null)
            this.invalidate();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		return !this.mChildrenEnabled;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		return !this.mChildrenEnabled;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (this.mTransformer != null) {
			canvas.save();
            this.mTransformer.transformCanvas(canvas, this.mViewAbove.getPercentOpen());
			super.dispatchDraw(canvas);
			canvas.restore();
		} else
			super.dispatchDraw(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = r - l;
		int height = b - t;
		// int mWidthOffset = SlidingActivity.width/3; //初始停靠右侧宽度
        this.mContent.layout(0, 0, width - this.mWidthOffset, height);
		if (this.mSecondaryContent != null)
            this.mSecondaryContent.layout(0, 0, width - this.mRightWidthOffset, height);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = View.getDefaultSize(0, widthMeasureSpec);
		int height = View.getDefaultSize(0, heightMeasureSpec);
        this.setMeasuredDimension(width, height);
		int contentWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, width
				- this.mWidthOffset);
		int SecondarycontentWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
				0, width - this.mRightWidthOffset);
		int contentHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0,
				height);
        this.mContent.measure(contentWidth, contentHeight);
		if (this.mSecondaryContent != null)
            this.mSecondaryContent.measure(SecondarycontentWidth, contentHeight);
	}

	private int mMode = SlidingMenu.LEFT_RIGHT;
	private boolean mFadeEnabled;
	private final Paint mFadePaint = new Paint();
	private float mScrollScale;
	private Drawable mShadowDrawable;
	private Drawable mSecondaryShadowDrawable;
	private int mShadowWidth;
	private float mFadeDegree;

	public void setMode(int mode) {
		if (mode == SlidingMenu.LEFT || mode == SlidingMenu.RIGHT) {
			if (this.mContent != null)
                this.mContent.setVisibility(View.VISIBLE);
			if (this.mSecondaryContent != null)
                this.mSecondaryContent.setVisibility(View.INVISIBLE);
		}
        this.mMode = mode;
	}

	public int getMode() {
		return this.mMode;
	}

	public void setScrollScale(float scrollScale) {
        this.mScrollScale = scrollScale;
	}

	public float getScrollScale() {
		return this.mScrollScale;
	}

	public void setShadowDrawable(Drawable shadow) {
        this.mShadowDrawable = shadow;
        this.invalidate();
	}

	public void setSecondaryShadowDrawable(Drawable shadow) {
        this.mSecondaryShadowDrawable = shadow;
        this.invalidate();
	}

	public void setShadowWidth(int width) {
        this.mShadowWidth = width;
        this.invalidate();
	}

	public void setFadeEnabled(boolean b) {
        this.mFadeEnabled = b;
	}

	public void setFadeDegree(float degree) {
		if (degree > 1.0f || degree < 0.0f)
			throw new IllegalStateException(
					"The BehindFadeDegree must be between 0.0f and 1.0f");
        this.mFadeDegree = degree;
	}

	public int getMenuPage(int page) {
		page = page > 1 ? 2 : page < 1 ? 0 : page;
		if (this.mMode == SlidingMenu.LEFT && page > 1) {
			return 0;
		} else if (this.mMode == SlidingMenu.RIGHT && page < 1) {
			return 2;
		} else {
			return page;
		}
	}

	public void scrollBehindTo(View content, int x, int y) {
		int vis = View.VISIBLE;
		if (this.mMode == SlidingMenu.LEFT) {
			if (x >= content.getLeft())
				vis = View.INVISIBLE;
            this.scrollTo((int) ((x + this.getBehindWidth()) * this.mScrollScale), y);
		} else if (this.mMode == SlidingMenu.RIGHT) {
			if (x <= content.getLeft())
				vis = View.INVISIBLE;
            this.scrollTo(
					(int) (this.getRightBehindWidth() - this.getWidth() + (x - this.getRightBehindWidth())
							* this.mScrollScale), y);
		} else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
            this.mContent.setVisibility(x >= content.getLeft() ? View.INVISIBLE
					: View.VISIBLE);
            this.mSecondaryContent
					.setVisibility(x <= content.getLeft() ? View.INVISIBLE
							: View.VISIBLE);
			vis = x == 0 ? View.INVISIBLE : View.VISIBLE;
			if (x <= content.getLeft()) {
                this.scrollTo((int) ((x + this.getBehindWidth()) * this.mScrollScale), y);
			} else {
                this.scrollTo(
						(int) (this.getRightBehindWidth() - this.getWidth() + (x - this.getRightBehindWidth())
								* this.mScrollScale), y);
			}
		}
		if (vis == View.INVISIBLE)
			Log.v(CustomViewBehind.TAG, "behind INVISIBLE");
        this.setVisibility(vis);
	}

	public int getMenuLeft(View content, int page) {
        if (this.mMode == SlidingMenu.LEFT) {
                switch (page) {
                case 0:
                        return content.getLeft() - this.getBehindWidth();
                case 2:
                        return content.getLeft();
                }
        } else if (this.mMode == SlidingMenu.RIGHT) {
                switch (page) {
                case 0:
                        return content.getLeft();
                case 2:
                        return content.getLeft() + this.getRightBehindWidth();
                }
        } else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
                switch (page) {
                case 0:
                        return content.getLeft() - this.getBehindWidth();
                case 2:
                        return content.getLeft() + this.getRightBehindWidth();
                }
        }
        return content.getLeft();
}

	public int getAbsLeftBound(View content) {
		if (this.mMode == SlidingMenu.LEFT || this.mMode == SlidingMenu.LEFT_RIGHT) {
			return content.getLeft() - this.getBehindWidth();
		} else if (this.mMode == SlidingMenu.RIGHT) {
			return content.getLeft();
		}
		return 0;
	}

	public int getAbsRightBound(View content) {
		if (this.mMode == SlidingMenu.LEFT) {
			return content.getLeft();
		} else if (this.mMode == SlidingMenu.RIGHT
				|| this.mMode == SlidingMenu.LEFT_RIGHT) {
			return content.getLeft() + this.getRightBehindWidth();
		}
		return 0;
	}

	public boolean marginTouchAllowed(View content, int x) {
		int left = content.getLeft();
		int right = content.getRight();
		if (this.mMode == SlidingMenu.LEFT) {
			return x >= left && x <= this.mMarginThreshold + left;
		} else if (this.mMode == SlidingMenu.RIGHT) {
			return x <= right && x >= right - this.mMarginThreshold;
		} else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
			return x >= left && x <= this.mMarginThreshold + left
					|| x <= right && x >= right - this.mMarginThreshold;
		}
		return false;
	}

	public void setTouchMode(int i) {
        this.mTouchMode = i;
	}

	public boolean menuOpenTouchAllowed(View content, int currPage, float x) {
		switch (this.mTouchMode) {
		case SlidingMenu.TOUCHMODE_FULLSCREEN:
			return true;
		case SlidingMenu.TOUCHMODE_MARGIN:
			return this.menuTouchInQuickReturn(content, currPage, x);
		}
		return false;
	}

	public boolean menuTouchInQuickReturn(View content, int currPage, float x) {
		if (this.mMode == SlidingMenu.LEFT
				|| this.mMode == SlidingMenu.LEFT_RIGHT && currPage == 0) {
			return x >= content.getLeft();
		} else if (this.mMode == SlidingMenu.RIGHT
				|| this.mMode == SlidingMenu.LEFT_RIGHT && currPage == 2) {
			return x <= content.getRight();
		}
		return false;
	}

	public boolean menuClosedSlideAllowed(float dx) {
		if (this.mMode == SlidingMenu.LEFT) {
			return dx > 0;
		} else if (this.mMode == SlidingMenu.RIGHT) {
			return dx < 0;
		} else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
			return true;
		}
		return false;
	}

	public boolean menuOpenSlideAllowed(float dx) {
		if (this.mMode == SlidingMenu.LEFT) {
			return dx < 0;
		} else if (this.mMode == SlidingMenu.RIGHT) {
			return dx > 0;
		} else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
			return true;
		}
		return false;
	}

	public void drawShadow(View content, Canvas canvas) {
		if (this.mShadowDrawable == null || this.mShadowWidth <= 0)
			return;
		int left = 0;
		if (this.mMode == SlidingMenu.LEFT) {
			left = content.getLeft() - this.mShadowWidth;
		} else if (this.mMode == SlidingMenu.RIGHT) {
			left = content.getRight();
		} else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
			if (this.mSecondaryShadowDrawable != null) {
				left = content.getRight();
                this.mSecondaryShadowDrawable.setBounds(left, 0,
						left + this.mShadowWidth, this.getHeight());
                this.mSecondaryShadowDrawable.draw(canvas);
			}
			left = content.getLeft() - this.mShadowWidth;
		}
        this.mShadowDrawable.setBounds(left, 0, left + this.mShadowWidth, this.getHeight());
        this.mShadowDrawable.draw(canvas);
	}

	public void drawFade(View content, Canvas canvas, float openPercent) {
		if (!this.mFadeEnabled)
			return;
		int alpha = (int) (this.mFadeDegree * 255 * Math.abs(1 - openPercent));
        this.mFadePaint.setColor(Color.argb(alpha, 0, 0, 0));
		int left = 0;
		int right = 0;
		if (this.mMode == SlidingMenu.LEFT) {
			left = content.getLeft() - this.getBehindWidth();
			right = content.getLeft();
		} else if (this.mMode == SlidingMenu.RIGHT) {
			left = content.getRight();
			right = content.getRight() + this.getRightBehindWidth();
		} else if (this.mMode == SlidingMenu.LEFT_RIGHT) {
			left = content.getLeft() - this.getBehindWidth();
			right = content.getLeft();
			canvas.drawRect(left, 0, right, this.getHeight(), this.mFadePaint);
			left = content.getRight();
			right = content.getRight() + this.getRightBehindWidth();
		}
		canvas.drawRect(left, 0, right, this.getHeight(), this.mFadePaint);
	}

	private boolean mSelectorEnabled = true;
	private Bitmap mSelectorDrawable;
	private View mSelectedView;

	public void drawSelector(View content, Canvas canvas, float openPercent) {
		if (!this.mSelectorEnabled)
			return;
		if (this.mSelectorDrawable != null && this.mSelectedView != null) {
			String tag = (String) this.mSelectedView.getTag(R.id.selected_view);
			if (tag.equals(CustomViewBehind.TAG + "SelectedView")) {
				canvas.save();
				int left, right, offset;
				offset = (int) (this.mSelectorDrawable.getWidth() * openPercent);
				if (this.mMode == SlidingMenu.LEFT) {
					right = content.getLeft();
					left = right - offset;
					canvas.clipRect(left, 0, right, this.getHeight());
					canvas.drawBitmap(this.mSelectorDrawable, left,
                            this.getSelectorTop(), null);
				} else if (this.mMode == SlidingMenu.RIGHT) {
					left = content.getRight();
					right = left + offset;
					canvas.clipRect(left, 0, right, this.getHeight());
					canvas.drawBitmap(this.mSelectorDrawable, right
							- this.mSelectorDrawable.getWidth(), this.getSelectorTop(),
							null);
				}
				canvas.restore();
			}
		}
	}

	public void setSelectorEnabled(boolean b) {
        this.mSelectorEnabled = b;
	}

	public void setSelectedView(View v) {
		if (this.mSelectedView != null) {
            this.mSelectedView.setTag(R.id.selected_view, null);
            this.mSelectedView = null;
		}
		if (v != null && v.getParent() != null) {
            this.mSelectedView = v;
            this.mSelectedView.setTag(R.id.selected_view, CustomViewBehind.TAG + "SelectedView");
            this.invalidate();
		}
	}

	private int getSelectorTop() {
		int y = this.mSelectedView.getTop();
		y += (this.mSelectedView.getHeight() - this.mSelectorDrawable.getHeight()) / 2;
		return y;
	}

	public void setSelectorBitmap(Bitmap b) {
        this.mSelectorDrawable = b;
        this.refreshDrawableState();
	}

	// 鍙宠竟瀹藉害鍋忕Щ閲�

	public void setRightWidthOffset(int i) {
        this.mRightWidthOffset = i;
        this.requestLayout();
	}

	private int getRightBehindWidth() {
		return this.mSecondaryContent.getWidth();
	}

}
