package com.tbs.tbsmis.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;


//import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
//import com.slidingmenu.lib.SlidingMenu.OnOpenListener;

public class CustomViewAbove extends ViewGroup {

	private static final String TAG = "CustomViewAbove";
	private static final boolean DEBUG = true;

	private static final boolean USE_CACHE = false;

	private static final int MAX_SETTLE_DURATION = 600; // ms
	private static final int MIN_DISTANCE_FOR_FLING = 25; // dips

	private static final Interpolator sInterpolator = new Interpolator() {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	private View mContent;

	public View getmContent() {
		return this.mContent;
	}

	public void setmContent(View mContent) {
		this.mContent = mContent;
	}

	public  int mCurItem;

	private Scroller mScroller;

	private boolean mScrollingCacheEnabled;

	private boolean mScrolling;

	private boolean mIsBeingDragged;
	private boolean mIsUnableToDrag;
	private int mTouchSlop;
	private float mInitialMotionX;
	/**
	 * Position of the last motion event.
	 */
	private float mLastMotionX;
	private float mLastMotionY;
	/**
	 * ID of the active pointer. This is used to retain consistency during
	 * drags/flings if multiple pointers are used.
	 */
	protected int mActivePointerId = CustomViewAbove.INVALID_POINTER;
	/**
	 * Sentinel value for no current active pointer.
	 * Used by {@link #mActivePointerId}.
	 */
	private static final int INVALID_POINTER = -1;

	/**
	 * Determines speed during touch scrolling
	 */
	protected VelocityTracker mVelocityTracker;
	private int mMinimumVelocity;
	protected int mMaximumVelocity;
	private int mFlingDistance;

	private CustomViewBehind mViewBehind;
	//	private int mMode;
	private boolean mEnabled = true;

	private CustomViewAbove.OnPageChangeListener mOnPageChangeListener;
	private CustomViewAbove.OnPageChangeListener mInternalPageChangeListener;

	//	private OnCloseListener mCloseListener;
	//	private OnOpenListener mOpenListener;
	private SlidingMenu.OnClosedListener mClosedListener;
	private SlidingMenu.OnOpenedListener mOpenedListener;

	private final List<View> mIgnoredViews = new ArrayList<View>();

	//	private int mScrollState = SCROLL_STATE_IDLE;

	/**
	 * Callback interface for responding to changing state of the selected page.
	 */
	public interface OnPageChangeListener {

		/**
		 * This method will be invoked when the current page is scrolled, either as part
		 * of a programmatically initiated smooth scroll or a user initiated touch scroll.
		 *
		 * @param position Position index of the first page currently being displayed.
		 *                 Page position+1 will be visible if positionOffset is nonzero.
		 * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
		 * @param positionOffsetPixels Value in pixels indicating the offset from position.
		 */
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

		/**
		 * This method will be invoked when a new page becomes selected. Animation is not
		 * necessarily complete.
		 *
		 * @param position Position index of the new selected page.
		 */
        void onPageSelected(int position);

	}

	/**
	 * Simple implementation of the {@link CustomViewAbove.OnPageChangeListener} interface with stub
	 * implementations of each method. Extend this if you do not intend to override
	 * every method of {@link CustomViewAbove.OnPageChangeListener}.
	 */
	public static class SimpleOnPageChangeListener implements CustomViewAbove.OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// This space for rent
		}

		@Override
		public void onPageSelected(int position) {
			// This space for rent
		}

		public void onPageScrollStateChanged(int state) {
			// This space for rent
		}

	}

	public CustomViewAbove(Context context) {
		this(context, null);
	}

	public CustomViewAbove(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.initCustomViewAbove();
	}

	void initCustomViewAbove() {
//		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        this.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        this.setClickable(true);
        this.setFocusable(true);
        this.setWillNotDraw(false);
		Context context = this.getContext();
        this.mScroller = new Scroller(context, CustomViewAbove.sInterpolator);
		ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        this.setInternalPageChangeListener(new CustomViewAbove.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (CustomViewAbove.this.mViewBehind != null) {
					switch (position) {
					case 0:
					case 2:
                        CustomViewAbove.this.mViewBehind.setChildrenEnabled(true);
						break;
					case 1:
                        CustomViewAbove.this.mViewBehind.setChildrenEnabled(false);
						break;
					}
				}
			}
		});

		float density = context.getResources().getDisplayMetrics().density;
        this.mFlingDistance = (int) (CustomViewAbove.MIN_DISTANCE_FOR_FLING * density);
	}

	/**
	 * Set the currently selected page. If the CustomViewPager has already been through its first
	 * layout there will be a smooth animated transition between the current item and the
	 * specified item.
	 *
	 * @param item Item index to select
	 */
	public void setCurrentItem(int item) {
        this.setCurrentItemInternal(item, true, false);
	}

	/**
	 * Set the currently selected page.
	 *
	 * @param item Item index to select
	 * @param smoothScroll True to smoothly scroll to the new item, false to transition immediately
	 */
	public void setCurrentItem(int item, boolean smoothScroll) {
        this.setCurrentItemInternal(item, smoothScroll, false);
	}

	public int getCurrentItem() {
		return this.mCurItem;
	}

	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        this.setCurrentItemInternal(item, smoothScroll, always, 0);
	}

	void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
		if (!always && this.mCurItem == item) {
            this.setScrollingCacheEnabled(false);
			return;
		}

		item = this.mViewBehind.getMenuPage(item);

		boolean dispatchSelected = this.mCurItem != item;
        this.mCurItem = item;
		int destX = this.getDestScrollX(this.mCurItem);
		if (dispatchSelected && this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageSelected(item);
		}
		if (dispatchSelected && this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageSelected(item);
		}
		if (smoothScroll) {
            this.smoothScrollTo(destX, 0, velocity);
		} else {
            this.completeScroll();
            this.scrollTo(destX, 0);
		}
	}

	/**
	 * Set a listener that will be invoked whenever the page changes or is incrementally
	 * scrolled. See {@link CustomViewAbove.OnPageChangeListener}.
	 *
	 * @param listener Listener to set
	 */
	public void setOnPageChangeListener(CustomViewAbove.OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
	}
	/*
	public void setOnOpenListener(OnOpenListener l) {
		mOpenListener = l;
	}

	public void setOnCloseListener(OnCloseListener l) {
		mCloseListener = l;
	}
	 */
	public void setOnOpenedListener(SlidingMenu.OnOpenedListener l) {
        this.mOpenedListener = l;
	}

	public void setOnClosedListener(SlidingMenu.OnClosedListener l) {
        this.mClosedListener = l;
	}

	/**
	 * Set a separate OnPageChangeListener for internal use by the support library.
	 *
	 * @param listener Listener to set
	 * @return The old listener that was set, if any.
	 */
	CustomViewAbove.OnPageChangeListener setInternalPageChangeListener(CustomViewAbove.OnPageChangeListener listener) {
		CustomViewAbove.OnPageChangeListener oldListener = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = listener;
		return oldListener;
	}

	public void addIgnoredView(View v) {
		if (!this.mIgnoredViews.contains(v)) {
            this.mIgnoredViews.add(v);
		}
	}

	public void removeIgnoredView(View v) {
        this.mIgnoredViews.remove(v);
	}

	public void clearIgnoredViews() {
        this.mIgnoredViews.clear();
	}

	// We want the duration of the page snap animation to be influenced by the distance that
	// the screen has to travel, however, we don't want this duration to be effected in a
	// purely linear fashion. Instead, we use this method to moderate the effect that the distance
	// of travel has on the overall snap duration.
	float distanceInfluenceForSnapDuration(float f) {
		f -= 0.5f; // center the values about 0.
		f *= 0.3f * Math.PI / 2.0f;
		return (float) Math.sin(f);
	}

	public int getDestScrollX(int page) {
		switch (page) {
		case 0:
		case 2:
			return this.mViewBehind.getMenuLeft(this.mContent, page);
		case 1:
			return this.mContent.getLeft();
		}
		return 0;
	}

	private int getLeftBound() {
		return this.mViewBehind.getAbsLeftBound(this.mContent);
	}

	private int getRightBound() {
		return this.mViewBehind.getAbsRightBound(this.mContent);
	}

	public int getContentLeft() {
		return this.mContent.getLeft() + this.mContent.getPaddingLeft();
	}

	public boolean isMenuOpen() {
		return this.mCurItem == 0 || this.mCurItem == 2;
	}

	private boolean isInIgnoredView(MotionEvent ev) {
		Rect rect = new Rect();
		for (View v : this.mIgnoredViews) {
			v.getHitRect(rect);
			if (rect.contains((int)ev.getX(), (int)ev.getY())) return true;
		}
		return false;
	}

	public int getBehindWidth() {
		if (this.mViewBehind == null) {
			return 0;
		} else {
			return this.mViewBehind.getBehindWidth();
		}
	}

	public int getChildWidth(int i) {
		switch (i) {
		case 0:
			return this.getBehindWidth();
		case 1:
			return this.mContent.getWidth();
		default:
			return 0;
		}
	}

	public boolean isSlidingEnabled() {
		return this.mEnabled;
	}

	public void setSlidingEnabled(boolean b) {
        this.mEnabled = b;
	}

	/**
	 * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
	 *
	 * @param x the number of pixels to scroll by on the X axis
	 * @param y the number of pixels to scroll by on the Y axis
	 */
	void smoothScrollTo(int x, int y) {
        this.smoothScrollTo(x, y, 0);
	}

	/**
	 * Like {@link View#scrollBy}, but scroll smoothly instead of immediately.
	 *
	 * @param x the number of pixels to scroll by on the X axis
	 * @param y the number of pixels to scroll by on the Y axis
	 * @param velocity the velocity associated with a fling, if applicable. (0 otherwise)
	 */
	void smoothScrollTo(int x, int y, int velocity) {
		if (this.getChildCount() == 0) {
			// Nothing to do.
            this.setScrollingCacheEnabled(false);
			return;
		}
		int sx = this.getScrollX();
		int sy = this.getScrollY();
		int dx = x - sx;
		int dy = y - sy;
		if (dx == 0 && dy == 0) {
            this.completeScroll();
			if (this.isMenuOpen()) {
				if (this.mOpenedListener != null)
                    this.mOpenedListener.onOpened();
			} else {
				if (this.mClosedListener != null)
                    this.mClosedListener.onClosed();
			}
			return;
		}

        this.setScrollingCacheEnabled(true);
        this.mScrolling = true;

		int width = this.getBehindWidth();
		int halfWidth = width / 2;
		float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
		float distance = halfWidth + halfWidth *
                this.distanceInfluenceForSnapDuration(distanceRatio);

		int duration = 0;
		velocity = Math.abs(velocity);
		if (velocity > 0) {
			duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
		} else {
			float pageDelta = (float) Math.abs(dx) / width;
			duration = (int) ((pageDelta + 1) * 100);
			duration = CustomViewAbove.MAX_SETTLE_DURATION;
		}
		duration = Math.min(duration, CustomViewAbove.MAX_SETTLE_DURATION);

        this.mScroller.startScroll(sx, sy, dx, dy, duration);
        this.invalidate();
	}

	public void setContent(View v) {
		if (this.mContent != null)
            removeView(this.mContent);
        this.mContent = v;
//		mContent.setClickable(true);
        this.mContent.setFocusable(true);
        this.addView(this.mContent);
	}

	public View getContent() {
		return this.mContent;
	}

	public void setCustomViewBehind(CustomViewBehind cvb) {
        this.mViewBehind = cvb;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int width = View.getDefaultSize(0, widthMeasureSpec);
		int height = View.getDefaultSize(0, heightMeasureSpec);
        this.setMeasuredDimension(width, height);

		int contentWidth = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, width);
		int contentHeight = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, height);
        this.mContent.measure(contentWidth, contentHeight);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Make sure scroll position is set correctly.
		if (w != oldw) {
			// [ChrisJ] - This fixes the onConfiguration change for orientation issue..
			// maybe worth having a look why the recomputeScroll pos is screwing
			// up?
            this.completeScroll();
            this.scrollTo(this.getDestScrollX(this.mCurItem), this.getScrollY());
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = r - l;
		int height = b - t;
        this.mContent.layout(0, 0, width, height);
	}

	public void setAboveOffset(int i) {
		//		RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams)mContent.getLayoutParams());
		//		params.setMargins(i, params.topMargin, params.rightMargin, params.bottomMargin);
        this.mContent.setPadding(i, this.mContent.getPaddingTop(),
                this.mContent.getPaddingRight(), this.mContent.getPaddingBottom());
	}


	@Override
	public void computeScroll() {
		if (!this.mScroller.isFinished()) {
			if (this.mScroller.computeScrollOffset()) {
				int oldX = this.getScrollX();
				int oldY = this.getScrollY();
				int x = this.mScroller.getCurrX();
				int y = this.mScroller.getCurrY();

				if (oldX != x || oldY != y) {
                    this.scrollTo(x, y);
                    this.pageScrolled(x);
				}

				// Keep on drawing until the animation has finished.
                this.invalidate();
				return;
			}
		}

		// Done with scroll, clean up state.
        this.completeScroll();
	}

	private void pageScrolled(int xpos) {
		int widthWithMargin = this.getWidth();
		int position = xpos / widthWithMargin;
		int offsetPixels = xpos % widthWithMargin;
		float offset = (float) offsetPixels / widthWithMargin;

        this.onPageScrolled(position, offset, offsetPixels);
	}

	/**
	 * This method will be invoked when the current page is scrolled, either as part
	 * of a programmatically initiated smooth scroll or a user initiated touch scroll.
	 * If you override this method you must call through to the superclass implementation
	 * (e.g. super.onPageScrolled(position, offset, offsetPixels)) before onPageScrolled
	 * returns.
	 *
	 * @param position Position index of the first page currently being displayed.
	 *                 Page position+1 will be visible if positionOffset is nonzero.
	 * @param offset Value from [0, 1) indicating the offset from the page at position.
	 * @param offsetPixels Value in pixels indicating the offset from position.
	 */
	protected void onPageScrolled(int position, float offset, int offsetPixels) {
		if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
		}
		if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrolled(position, offset, offsetPixels);
		}
	}

	private void completeScroll() {
		boolean needPopulate = this.mScrolling;
		if (needPopulate) {
			// Done with scroll, no longer want to cache view drawing.
            this.setScrollingCacheEnabled(false);
            this.mScroller.abortAnimation();
			int oldX = this.getScrollX();
			int oldY = this.getScrollY();
			int x = this.mScroller.getCurrX();
			int y = this.mScroller.getCurrY();
			if (oldX != x || oldY != y) {
                this.scrollTo(x, y);
			}
			if (this.isMenuOpen()) {
				if (this.mOpenedListener != null)
                    this.mOpenedListener.onOpened();
			} else {
				if (this.mClosedListener != null)
                    this.mClosedListener.onClosed();
			}
		}
        this.mScrolling = false;
	}

	protected int mTouchMode = SlidingMenu.TOUCHMODE_MARGIN;

	public void setTouchMode(int i) {
        this.mTouchMode = i;
	}

	public int getTouchMode() {
		return this.mTouchMode;
	}

	private boolean thisTouchAllowed(MotionEvent ev) {
		int x = (int) (ev.getX() + this.mScrollX);
		if (this.isMenuOpen()) {
			return this.mViewBehind.menuOpenTouchAllowed(this.mContent, this.mCurItem, x);
		} else {
			switch (this.mTouchMode) {
			case SlidingMenu.TOUCHMODE_FULLSCREEN:
				return !this.isInIgnoredView(ev);
			case SlidingMenu.TOUCHMODE_NONE:
				return false;
			case SlidingMenu.TOUCHMODE_MARGIN:
				return this.mViewBehind.marginTouchAllowed(this.mContent, x);
			}
		}
		return false;
	}

	private boolean thisSlideAllowed(float dx) {
		boolean allowed = false;
		if (this.isMenuOpen()) {
			allowed = this.mViewBehind.menuOpenSlideAllowed(dx);
		} else {
			allowed = this.mViewBehind.menuClosedSlideAllowed(dx);
		}
		if (CustomViewAbove.DEBUG)
			Log.v(CustomViewAbove.TAG, "this slide allowed " + allowed + " dx: " + dx);
		return allowed;
	}

	private int getPointerIndex(MotionEvent ev, int id) {
		int activePointerIndex = MotionEventCompat.findPointerIndex(ev, id);
		if (activePointerIndex == -1)
            this.mActivePointerId = CustomViewAbove.INVALID_POINTER;
		return activePointerIndex;
	}

	private boolean mQuickReturn;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (!this.mEnabled)
			return false;

		int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

		if (action == MotionEvent.ACTION_DOWN && CustomViewAbove.DEBUG)
			Log.v(CustomViewAbove.TAG, "Received ACTION_DOWN");

		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP
				|| action != MotionEvent.ACTION_DOWN && this.mIsUnableToDrag) {
            this.endDrag();
			return false;
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			try{
				int activePointerId = this.mActivePointerId;
				if (activePointerId == CustomViewAbove.INVALID_POINTER)
					break;
				int pointerIndex = getPointerIndex(ev, activePointerId);
				float x = MotionEventCompat.getX(ev, pointerIndex);
				float dx = x - this.mLastMotionX;
				float xDiff = Math.abs(dx);
				float y = MotionEventCompat.getY(ev, pointerIndex);
				float yDiff = Math.abs(y - this.mLastMotionY);
				if (CustomViewAbove.DEBUG) Log.v(CustomViewAbove.TAG, "onInterceptTouch moved to:(" + x + ", " + y + "), diff:(" + xDiff + ", " + yDiff + "), mLastMotionX:" + this.mLastMotionX);
				if (xDiff > this.mTouchSlop && xDiff > yDiff && this.thisSlideAllowed(dx)) {
					if (CustomViewAbove.DEBUG) Log.v(CustomViewAbove.TAG, "Starting drag! from onInterceptTouch");
                    this.startDrag();
                    this.mLastMotionX = x;
                    this.setScrollingCacheEnabled(true);
				} else if (yDiff > this.mTouchSlop) {
                    this.mIsUnableToDrag = true;
				}
			}
			catch(IllegalArgumentException e)
			{
				e.printStackTrace();
			}
			break;

		case MotionEvent.ACTION_DOWN:
            this.mActivePointerId = ev.getAction() & (VERSION.SDK_INT >= 8 ? MotionEvent.ACTION_POINTER_INDEX_MASK :
				MotionEvent.ACTION_POINTER_INDEX_MASK);
            this.mLastMotionX = this.mInitialMotionX = MotionEventCompat.getX(ev, this.mActivePointerId);
            this.mLastMotionY = MotionEventCompat.getY(ev, this.mActivePointerId);
			if (this.thisTouchAllowed(ev)) {
                this.mIsBeingDragged = false;
                this.mIsUnableToDrag = false;
				if (this.isMenuOpen() && this.mViewBehind.menuTouchInQuickReturn(this.mContent, this.mCurItem, ev.getX() + this.mScrollX)) {
                    this.mQuickReturn = true;
				}
			} else {
                this.mIsUnableToDrag = true;
			}
			break;
		case MotionEventCompat.ACTION_POINTER_UP:


            this.onSecondaryPointerUp(ev);
			break;
		}

		if (!this.mIsBeingDragged) {
			if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
			}
            this.mVelocityTracker.addMovement(ev);
		}
		return this.mIsBeingDragged || this.mQuickReturn;
	}


	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (!this.mEnabled)
			return false;

		//		if (!mIsBeingDragged && !thisTouchAllowed(ev))
		//			return false;

		if (!this.mIsBeingDragged && !this.mQuickReturn)
			return false;

		int action = ev.getAction();

		if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
		}
        this.mVelocityTracker.addMovement(ev);

		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			/*
			 * If being flinged and user touches, stop the fling. isFinished
			 * will be false if being flinged.
			 */
            this.completeScroll();

			// Remember where the motion event started
            this.mLastMotionX = this.mInitialMotionX = ev.getX();
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			break;
		case MotionEvent.ACTION_MOVE:
			if (!this.mIsBeingDragged) {
				if (this.mActivePointerId == CustomViewAbove.INVALID_POINTER)
					break;
				int pointerIndex = this.getPointerIndex(ev, this.mActivePointerId);
				float x = MotionEventCompat.getX(ev, pointerIndex);
				float dx = x - this.mLastMotionX;
				float xDiff = Math.abs(dx);
				float y = MotionEventCompat.getY(ev, pointerIndex);
				float yDiff = Math.abs(y - this.mLastMotionY);
				if (CustomViewAbove.DEBUG) Log.v(CustomViewAbove.TAG, "onTouch moved to:(" + x + ", " + y + "), diff:(" + xDiff + ", " + yDiff + ")\nmIsBeingDragged:" + this.mIsBeingDragged + ", mLastMotionX:" + this.mLastMotionX);
				if ((xDiff > this.mTouchSlop || this.mQuickReturn && xDiff > this.mTouchSlop / 4)
						&& xDiff > yDiff && this.thisSlideAllowed(dx)) {
					if (CustomViewAbove.DEBUG) Log.v(CustomViewAbove.TAG, "Starting drag! from onTouch");
                    this.startDrag();
                    this.mLastMotionX = x;
                    this.setScrollingCacheEnabled(true);
				} else {
					if (CustomViewAbove.DEBUG) Log.v(CustomViewAbove.TAG, "onTouch returning false");
					return false;
				}
			}
			if (this.mIsBeingDragged) {
				// Scroll to follow the motion event
				int activePointerIndex = this.getPointerIndex(ev, this.mActivePointerId);
				if (this.mActivePointerId == CustomViewAbove.INVALID_POINTER) {
					break;
				}
				float x = MotionEventCompat.getX(ev, activePointerIndex);
				float deltaX = this.mLastMotionX - x;
                this.mLastMotionX = x;
				float oldScrollX = this.getScrollX();
				float scrollX = oldScrollX + deltaX;
				float leftBound = this.getLeftBound();
				float rightBound = this.getRightBound();
				if (scrollX < leftBound) {
					scrollX = leftBound;
				} else if (scrollX > rightBound) {
					scrollX = rightBound;
				}
				// Don't lose the rounded component
                this.mLastMotionX += scrollX - (int) scrollX;
                this.scrollTo((int) scrollX, this.getScrollY());
                this.pageScrolled((int) scrollX);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (this.mIsBeingDragged) {
				VelocityTracker velocityTracker = this.mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
				int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
						velocityTracker, this.mActivePointerId);
				int scrollX = this.getScrollX();
				//				final int widthWithMargin = getWidth();
				//				final float pageOffset = (float) (scrollX % widthWithMargin) / widthWithMargin;
				// TODO test this. should get better flinging behavior
				float pageOffset = (float) (scrollX - this.getDestScrollX(this.mCurItem)) / this.getBehindWidth();
				int activePointerIndex = this.getPointerIndex(ev, this.mActivePointerId);
				if (this.mActivePointerId != CustomViewAbove.INVALID_POINTER) {
					float x = MotionEventCompat.getX(ev, activePointerIndex);
					int totalDelta = (int) (x - this.mInitialMotionX);
					int nextPage = this.determineTargetPage(pageOffset, initialVelocity, totalDelta);
                    this.setCurrentItemInternal(nextPage, true, true, initialVelocity);
				} else {
                    this.setCurrentItemInternal(this.mCurItem, true, true, initialVelocity);
				}
                this.mActivePointerId = CustomViewAbove.INVALID_POINTER;
                this.endDrag();
			} else if (this.mQuickReturn && this.mViewBehind.menuTouchInQuickReturn(this.mContent, this.mCurItem, ev.getX() + this.mScrollX)) {
				// close the menu
                this.setCurrentItem(1);
                this.endDrag();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (this.mIsBeingDragged) {
                this.setCurrentItemInternal(this.mCurItem, true, true);
                this.mActivePointerId = CustomViewAbove.INVALID_POINTER;
                this.endDrag();
			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN:
            int index = MotionEventCompat.getActionIndex(ev);
            float x = MotionEventCompat.getX(ev, index);
            this.mLastMotionX = x;
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, index);
            break;
            case MotionEventCompat.ACTION_POINTER_UP:
            this.onSecondaryPointerUp(ev);
			int pointerIndex = getPointerIndex(ev, this.mActivePointerId);
			if (this.mActivePointerId == CustomViewAbove.INVALID_POINTER)
				break;
            this.mLastMotionX = MotionEventCompat.getX(ev, pointerIndex);
			break;
		}
		return true;
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
        this.mScrollX = x;
		if (this.mEnabled)
            this.mViewBehind.scrollBehindTo(this.mContent, x, y);
//		((SlidingMenu)getParent()).manageLayers(getPercentOpen());
	}

	private int determineTargetPage(float pageOffset, int velocity, int deltaX) {
		int targetPage = this.mCurItem;
		if (Math.abs(deltaX) > this.mFlingDistance && Math.abs(velocity) > this.mMinimumVelocity) {
			if (velocity > 0 && deltaX > 0) {
				targetPage -= 1;
			} else if (velocity < 0 && deltaX < 0){
				targetPage += 1;
			}
		} else {
			targetPage = Math.round(this.mCurItem + pageOffset);
		}
		return targetPage;
	}

	protected float getPercentOpen() {
		return Math.abs(this.mScrollX - this.mContent.getLeft()) / this.getBehindWidth();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// Draw the margin drawable if needed.
        this.mViewBehind.drawShadow(this.mContent, canvas);
        this.mViewBehind.drawFade(this.mContent, canvas, this.getPercentOpen());
        this.mViewBehind.drawSelector(this.mContent, canvas, this.getPercentOpen());
	}

	// variables for drawing
	private float mScrollX;

	private void onSecondaryPointerUp(MotionEvent ev) {
		if (CustomViewAbove.DEBUG) Log.v(CustomViewAbove.TAG, "onSecondaryPointerUp called");
		int pointerIndex = MotionEventCompat.getActionIndex(ev);
		int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == this.mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
			if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
			}
		}
	}

	private void startDrag() {
        this.mIsBeingDragged = true;
        this.mQuickReturn = false;
	}

	private void endDrag() {
        this.mQuickReturn = false;
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        this.mActivePointerId = CustomViewAbove.INVALID_POINTER;

		if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
		}
	}

	private void setScrollingCacheEnabled(boolean enabled) {
		if (this.mScrollingCacheEnabled != enabled) {
            this.mScrollingCacheEnabled = enabled;
			if (CustomViewAbove.USE_CACHE) {
				int size = this.getChildCount();
				for (int i = 0; i < size; ++i) {
					View child = this.getChildAt(i);
					if (child.getVisibility() != View.GONE) {
						child.setDrawingCacheEnabled(enabled);
					}
				}
			}
		}
	}

	/**
	 * Tests scrollability within child views of v given a delta of dx.
	 *
	 * @param v View to test for horizontal scrollability
	 * @param checkV Whether the view v passed should itself be checked for scrollability (true),
	 *               or just its children (false).
	 * @param dx Delta scrolled in pixels
	 * @param x X coordinate of the active touch point
	 * @param y Y coordinate of the active touch point
	 * @return true if child views of v can be scrolled by delta of dx.
	 */
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		if (v instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) v;
			int scrollX = v.getScrollX();
			int scrollY = v.getScrollY();
			int count = group.getChildCount();
			// Count backwards - let topmost views consume scroll distance first.
			for (int i = count - 1; i >= 0; i--) {
				View child = group.getChildAt(i);
				if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
						y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        this.canScroll(child, true, dx, x + scrollX - child.getLeft(),
								y + scrollY - child.getTop())) {
					return true;
				}
			}
		}

		return checkV && ViewCompat.canScrollHorizontally(v, -dx);
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// Let the focused view and/or our descendants get the key first
		return super.dispatchKeyEvent(event) || this.executeKeyEvent(event);
	}

	/**
	 * You can call this function yourself to have the scroll view perform
	 * scrolling from a key event, just as if the event had been dispatched to
	 * it by the view hierarchy.
	 *
	 * @param event The key event to execute.
	 * @return Return true if the event was handled, else false.
	 */
	public boolean executeKeyEvent(KeyEvent event) {
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_LEFT:
				handled = this.arrowScroll(View.FOCUS_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				handled = this.arrowScroll(View.FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_TAB:
				if (VERSION.SDK_INT >= 11) {
					// The focus finder had a bug handling FOCUS_FORWARD and FOCUS_BACKWARD
					// before Android 3.0. Ignore the tab key on those devices.
					if (event.hasNoModifiers()) {
						handled = this.arrowScroll(View.FOCUS_FORWARD);
					} else if (event.hasModifiers(KeyEvent.META_SHIFT_ON)) {
						handled = this.arrowScroll(View.FOCUS_BACKWARD);
					}
				}
				break;
			}
		}
		return handled;
	}

	public boolean arrowScroll(int direction) {
		View currentFocused = this.findFocus();
		if (currentFocused == this) currentFocused = null;

		boolean handled = false;

		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
				direction);
		if (nextFocused != null && nextFocused != currentFocused) {
			if (direction == View.FOCUS_LEFT) {
				handled = nextFocused.requestFocus();
			} else if (direction == View.FOCUS_RIGHT) {
				// If there is nothing to the right, or this is causing us to
				// jump to the left, then what we really want to do is page right.
				if (currentFocused != null && nextFocused.getLeft() <= currentFocused.getLeft()) {
					handled = this.pageRight();
				} else {
					handled = nextFocused.requestFocus();
				}
			}
		} else if (direction == View.FOCUS_LEFT || direction == View.FOCUS_BACKWARD) {
			// Trying to move left and nothing there; try to page.
			handled = this.pageLeft();
		} else if (direction == View.FOCUS_RIGHT || direction == View.FOCUS_FORWARD) {
			// Trying to move right and nothing there; try to page.
			handled = this.pageRight();
		}
		if (handled) {
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
		}
		return handled;
	}

	boolean pageLeft() {
		if (this.mCurItem > 0) {
            this.setCurrentItem(this.mCurItem -1, true);
			return true;
		}
		return false;
	}

	boolean pageRight() {
		if (this.mCurItem < 1) {
            this.setCurrentItem(this.mCurItem +1, true);
			return true;
		}
		return false;
	}

}
