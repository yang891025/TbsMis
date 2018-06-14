/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.handmark.pulltorefresh.library.R.styleable;
import com.handmark.pulltorefresh.library.internal.FlipLoadingLayout;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.RotateLoadingLayout;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.handmark.pulltorefresh.library.internal.ViewCompat;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T> {

	// ===========================================================
	// Constants
	// ===========================================================

	static final boolean DEBUG = true;

	static final boolean USE_HW_LAYERS = false;

	static final String LOG_TAG = "PullToRefresh";

	static final float FRICTION = 2.0f;

	public static final int SMOOTH_SCROLL_DURATION_MS = 200;
	public static final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;
	static final int DEMO_SCROLL_INTERVAL = 225;

	static final String STATE_STATE = "ptr_state";
	static final String STATE_MODE = "ptr_mode";
	static final String STATE_CURRENT_MODE = "ptr_current_mode";
	static final String STATE_SCROLLING_REFRESHING_ENABLED = "ptr_disable_scrolling";
	static final String STATE_SHOW_REFRESHING_VIEW = "ptr_show_refreshing_view";
	static final String STATE_SUPER = "ptr_super";

	// ===========================================================
	// Fields
	// ===========================================================

	private int mTouchSlop;
	private float mLastMotionX, mLastMotionY;
	private float mInitialMotionX, mInitialMotionY;

	private boolean mIsBeingDragged;
	private PullToRefreshBase.State mState = PullToRefreshBase.State.RESET;
	private PullToRefreshBase.Mode mMode = PullToRefreshBase.Mode.getDefault();

	private PullToRefreshBase.Mode mCurrentMode;
	T mRefreshableView;
	private FrameLayout mRefreshableViewWrapper;

	private boolean mShowViewWhileRefreshing = true;
	private boolean mScrollingWhileRefreshingEnabled;
	private boolean mFilterTouchEvents = true;
	private boolean mOverScrollEnabled = true;
	private boolean mLayoutVisibilityChangesEnabled = true;

	private Interpolator mScrollAnimationInterpolator;
	private PullToRefreshBase.AnimationStyle mLoadingAnimationStyle = PullToRefreshBase.AnimationStyle.getDefault();

	private LoadingLayout mHeaderLayout;
	private LoadingLayout mFooterLayout;

	private PullToRefreshBase.OnRefreshListener<T> mOnRefreshListener;
	private PullToRefreshBase.OnRefreshListener2<T> mOnRefreshListener2;
	private PullToRefreshBase.OnPullEventListener<T> mOnPullEventListener;

	private PullToRefreshBase.SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PullToRefreshBase(Context context) {
		super(context);
        this.init(context, null);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.init(context, attrs);
	}

	public PullToRefreshBase(Context context, PullToRefreshBase.Mode mode) {
		super(context);
        this.mMode = mode;
        this.init(context, null);
	}

	public PullToRefreshBase(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.AnimationStyle animStyle) {
		super(context);
        this.mMode = mode;
        this.mLoadingAnimationStyle = animStyle;
        this.init(context, null);
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (PullToRefreshBase.DEBUG) {
			Log.d(PullToRefreshBase.LOG_TAG, "addView: " + child.getClass().getSimpleName());
		}

		T refreshableView = this.getRefreshableView();

		if (refreshableView instanceof ViewGroup) {
			((ViewGroup) refreshableView).addView(child, index, params);
		} else {
			throw new UnsupportedOperationException("Refreshable View is not a ViewGroup so can't addView");
		}
	}

	@Override
	public final boolean demo() {
		if (this.mMode.showHeaderLoadingLayout() && this.isReadyForPullStart()) {
            this.smoothScrollToAndBack(-this.getHeaderSize() * 2);
			return true;
		} else if (this.mMode.showFooterLoadingLayout() && this.isReadyForPullEnd()) {
            this.smoothScrollToAndBack(this.getFooterSize() * 2);
			return true;
		}

		return false;
	}

	@Override
	public final PullToRefreshBase.Mode getCurrentMode() {
		return this.mCurrentMode;
	}

	@Override
	public final boolean getFilterTouchEvents() {
		return this.mFilterTouchEvents;
	}

	@Override
	public final ILoadingLayout getLoadingLayoutProxy() {
		return this.getLoadingLayoutProxy(true, true);
	}

	@Override
	public final ILoadingLayout getLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
		return this.createLoadingLayoutProxy(includeStart, includeEnd);
	}

	@Override
	public final PullToRefreshBase.Mode getMode() {
		return this.mMode;
	}

	@Override
	public final T getRefreshableView() {
		return this.mRefreshableView;
	}

	@Override
	public final boolean getShowViewWhileRefreshing() {
		return this.mShowViewWhileRefreshing;
	}

	@Override
	public final PullToRefreshBase.State getState() {
		return this.mState;
	}

	/**
	 * @deprecated See {@link #isScrollingWhileRefreshingEnabled()}.
	 */
	public final boolean isDisableScrollingWhileRefreshing() {
		return !this.isScrollingWhileRefreshingEnabled();
	}

	@Override
	public final boolean isPullToRefreshEnabled() {
		return this.mMode.permitsPullToRefresh();
	}

	@Override
	public final boolean isPullToRefreshOverScrollEnabled() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && this.mOverScrollEnabled
				&& OverscrollHelper.isAndroidOverScrollEnabled(this.mRefreshableView);
	}

	@Override
	public final boolean isRefreshing() {
		return this.mState == PullToRefreshBase.State.REFRESHING || this.mState == PullToRefreshBase.State.MANUAL_REFRESHING;
	}

	@Override
	public final boolean isScrollingWhileRefreshingEnabled() {
		return mScrollingWhileRefreshingEnabled;
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled()) {
			return false;
		}

		final int action = event.getAction();

		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mIsBeingDragged = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
			return true;
		}

		switch (action) {
			case MotionEvent.ACTION_MOVE:
                // If we're refreshing, and the flag is set. Eat all MOVE events
                if (!this.mScrollingWhileRefreshingEnabled && this.isRefreshing()) {
					return true;
				}

                if (this.isReadyForPull()) {
					float y = event.getY(), x = event.getX();
					float diff, oppositeDiff, absDiff;

					// We need to use the correct values, based on scroll
					// direction
					switch (this.getPullToRefreshScrollDirection()) {
						case HORIZONTAL:
							diff = x - this.mLastMotionX;
							oppositeDiff = y - this.mLastMotionY;
							break;
						case VERTICAL:
						default:
							diff = y - this.mLastMotionY;
							oppositeDiff = x - this.mLastMotionX;
							break;
					}
					absDiff = Math.abs(diff);

					if (absDiff > this.mTouchSlop && (!this.mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
						if (this.mMode.showHeaderLoadingLayout() && diff >= 1f && this.isReadyForPullStart()) {
                            this.mLastMotionY = y;
                            this.mLastMotionX = x;
                            this.mIsBeingDragged = true;
							if (this.mMode == PullToRefreshBase.Mode.BOTH) {
                                this.mCurrentMode = PullToRefreshBase.Mode.PULL_FROM_START;
							}
						} else if (this.mMode.showFooterLoadingLayout() && diff <= -1f && this.isReadyForPullEnd()) {
                            this.mLastMotionY = y;
                            this.mLastMotionX = x;
                            this.mIsBeingDragged = true;
							if (this.mMode == PullToRefreshBase.Mode.BOTH) {
                                this.mCurrentMode = PullToRefreshBase.Mode.PULL_FROM_END;
							}
						}
					}
				}
                break;
            case MotionEvent.ACTION_DOWN:
                if (this.isReadyForPull()) {
                    this.mLastMotionY = this.mInitialMotionY = event.getY();
                    this.mLastMotionX = this.mInitialMotionX = event.getX();
                    this.mIsBeingDragged = false;
				}
                break;
        }

		return this.mIsBeingDragged;
	}

	@Override
	public final void onRefreshComplete() {
		if (this.isRefreshing()) {
            this.setState(PullToRefreshBase.State.RESET);
		}
	}

	@Override
	public final boolean onTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled()) {
			return false;
		}

		// If we're refreshing, and the flag is set. Eat the event
		if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
			return false;
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
                if (this.mIsBeingDragged) {
                    this.mLastMotionY = event.getY();
                    this.mLastMotionX = event.getX();
                    this.pullEvent();
					return true;
				}
                break;

            case MotionEvent.ACTION_DOWN:
                if (this.isReadyForPull()) {
this.mLastMotionY = this.mInitialMotionY = event.getY();
this.mLastMotionX = this.mInitialMotionX = event.getX();
                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
                if (this.mIsBeingDragged) {
this.mIsBeingDragged = false;

                    if (this.mState == PullToRefreshBase.State.RELEASE_TO_REFRESH
                            && (null != this.mOnRefreshListener || null != this.mOnRefreshListener2)) {
this.setState(PullToRefreshBase.State.REFRESHING, true);
                        return true;
                    }

                    // If we're already refreshing, just scroll back to the top
                    if (this.isRefreshing()) {
this.smoothScrollTo(0);
                        return true;
                    }

                    // If we haven't returned by here, then we're not in a state
                    // to pull, so just reset
this.setState(PullToRefreshBase.State.RESET);

                    return true;
                }
                break;
        }

		return false;
	}

	public final void setScrollingWhileRefreshingEnabled(boolean allowScrollingWhileRefreshing) {
        this.mScrollingWhileRefreshingEnabled = allowScrollingWhileRefreshing;
	}

	/**
	 * @deprecated See {@link #setScrollingWhileRefreshingEnabled(boolean)}
	 */
	public void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
        this.setScrollingWhileRefreshingEnabled(!disableScrollingWhileRefreshing);
	}

	@Override
	public final void setFilterTouchEvents(boolean filterEvents) {
        this.mFilterTouchEvents = filterEvents;
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy()}.
	 */
	public void setLastUpdatedLabel(CharSequence label) {
        this.getLoadingLayoutProxy().setLastUpdatedLabel(label);
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy()}.
	 */
	public void setLoadingDrawable(Drawable drawable) {
        this.getLoadingLayoutProxy().setLoadingDrawable(drawable);
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy(boolean, boolean)}.
	 */
	public void setLoadingDrawable(Drawable drawable, PullToRefreshBase.Mode mode) {
        this.getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setLoadingDrawable(
				drawable);
	}

	@Override
	public void setLongClickable(boolean longClickable) {
        this.getRefreshableView().setLongClickable(longClickable);
	}

	@Override
	public final void setMode(PullToRefreshBase.Mode mode) {
		if (mode != this.mMode) {
			if (PullToRefreshBase.DEBUG) {
				Log.d(PullToRefreshBase.LOG_TAG, "Setting mode to: " + mode);
			}
            this.mMode = mode;
            this.updateUIForMode();
		}
	}

	public void setOnPullEventListener(PullToRefreshBase.OnPullEventListener<T> listener) {
        this.mOnPullEventListener = listener;
	}

	@Override
	public final void setOnRefreshListener(PullToRefreshBase.OnRefreshListener<T> listener) {
        this.mOnRefreshListener = listener;
        this.mOnRefreshListener2 = null;
	}

	@Override
	public final void setOnRefreshListener(PullToRefreshBase.OnRefreshListener2<T> listener) {
        this.mOnRefreshListener2 = listener;
        this.mOnRefreshListener = null;
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy()}.
	 */
	public void setPullLabel(CharSequence pullLabel) {
        this.getLoadingLayoutProxy().setPullLabel(pullLabel);
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy(boolean, boolean)}.
	 */
	public void setPullLabel(CharSequence pullLabel, PullToRefreshBase.Mode mode) {
        this.getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setPullLabel(pullLabel);
	}

	/**
	 * @param enable Whether Pull-To-Refresh should be used
	 * @deprecated This simple calls setMode with an appropriate mode based on
	 *             the passed value.
	 */
	public final void setPullToRefreshEnabled(boolean enable) {
        this.setMode(enable ? PullToRefreshBase.Mode.getDefault() : PullToRefreshBase.Mode.DISABLED);
	}

	@Override
	public final void setPullToRefreshOverScrollEnabled(boolean enabled) {
        this.mOverScrollEnabled = enabled;
	}

	@Override
	public final void setRefreshing() {
        this.setRefreshing(true);
	}

	@Override
	public final void setRefreshing(boolean doScroll) {
		if (!this.isRefreshing()) {
            this.setState(PullToRefreshBase.State.MANUAL_REFRESHING, doScroll);
		}
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy()}.
	 */
	public void setRefreshingLabel(CharSequence refreshingLabel) {
        this.getLoadingLayoutProxy().setRefreshingLabel(refreshingLabel);
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy(boolean, boolean)}.
	 */
	public void setRefreshingLabel(CharSequence refreshingLabel, PullToRefreshBase.Mode mode) {
        this.getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setRefreshingLabel(
				refreshingLabel);
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy()}.
	 */
	public void setReleaseLabel(CharSequence releaseLabel) {
        this.setReleaseLabel(releaseLabel, PullToRefreshBase.Mode.BOTH);
	}

	/**
	 * @deprecated You should now call this method on the result of
	 *             {@link #getLoadingLayoutProxy(boolean, boolean)}.
	 */
	public void setReleaseLabel(CharSequence releaseLabel, PullToRefreshBase.Mode mode) {
        this.getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setReleaseLabel(
				releaseLabel);
	}

	public void setScrollAnimationInterpolator(Interpolator interpolator) {
        this.mScrollAnimationInterpolator = interpolator;
	}

	@Override
	public final void setShowViewWhileRefreshing(boolean showView) {
        this.mShowViewWhileRefreshing = showView;
	}

	/**
	 * @return Either {@link PullToRefreshBase.Orientation#VERTICAL} or
	 *         {@link PullToRefreshBase.Orientation#HORIZONTAL} depending on the scroll direction.
	 */
	public abstract PullToRefreshBase.Orientation getPullToRefreshScrollDirection();

	final void setState(PullToRefreshBase.State state, boolean... params) {
        this.mState = state;
		if (PullToRefreshBase.DEBUG) {
			Log.d(PullToRefreshBase.LOG_TAG, "State: " + this.mState.name());
		}

		switch (this.mState) {
			case RESET:
                this.onReset();
				break;
			case PULL_TO_REFRESH:
                this.onPullToRefresh();
				break;
			case RELEASE_TO_REFRESH:
                this.onReleaseToRefresh();
				break;
			case REFRESHING:
			case MANUAL_REFRESHING:
                this.onRefreshing(params[0]);
				break;
			case OVERSCROLLING:
				// NO-OP
				break;
		}

		// Call OnPullEventListener
		if (null != this.mOnPullEventListener) {
            this.mOnPullEventListener.onPullEvent(this, this.mState, this.mCurrentMode);
		}
	}

	/**
	 * Used internally for adding view. Need because we override addView to
	 * pass-through to the Refreshable View
	 */
	protected final void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
		super.addView(child, index, params);
	}

	/**
	 * Used internally for adding view. Need because we override addView to
	 * pass-through to the Refreshable View
	 */
	protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
		super.addView(child, -1, params);
	}

	protected LoadingLayout createLoadingLayout(Context context, PullToRefreshBase.Mode mode, TypedArray attrs) {
		LoadingLayout layout = this.mLoadingAnimationStyle.createLoadingLayout(context, mode,
                this.getPullToRefreshScrollDirection(), attrs);
		layout.setVisibility(View.INVISIBLE);
		return layout;
	}

	/**
	 * Used internally for {@link #getLoadingLayoutProxy(boolean, boolean)}.
	 * Allows derivative classes to include any extra LoadingLayouts.
	 */
	protected LoadingLayoutProxy createLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
		LoadingLayoutProxy proxy = new LoadingLayoutProxy();

		if (includeStart && this.mMode.showHeaderLoadingLayout()) {
			proxy.addLayout(this.mHeaderLayout);
		}
		if (includeEnd && this.mMode.showFooterLoadingLayout()) {
			proxy.addLayout(this.mFooterLayout);
		}

		return proxy;
	}

	/**
	 * This is implemented by derived classes to return the created View. If you
	 * need to use a custom View (such as a custom ListView), override this
	 * method and return an instance of your custom class.
	 * <p/>
	 * Be sure to set the ID of the view in this method, especially if you're
	 * using a ListActivity or ListFragment.
	 *
	 * @param context Context to create view with
	 * @param attrs AttributeSet from wrapped class. Means that anything you
	 *            include in the XML layout declaration will be routed to the
	 *            created View
	 * @return New instance of the Refreshable View
	 */
	protected abstract T createRefreshableView(Context context, AttributeSet attrs);

	protected final void disableLoadingLayoutVisibilityChanges() {
        this.mLayoutVisibilityChangesEnabled = false;
	}

	protected final LoadingLayout getFooterLayout() {
		return this.mFooterLayout;
	}

	protected final int getFooterSize() {
		return this.mFooterLayout.getContentSize();
	}

	protected final LoadingLayout getHeaderLayout() {
		return this.mHeaderLayout;
	}

	protected final int getHeaderSize() {
		return this.mHeaderLayout.getContentSize();
	}

	protected int getPullToRefreshScrollDuration() {
		return PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS;
	}

	protected int getPullToRefreshScrollDurationLonger() {
		return PullToRefreshBase.SMOOTH_SCROLL_LONG_DURATION_MS;
	}

	protected FrameLayout getRefreshableViewWrapper() {
		return this.mRefreshableViewWrapper;
	}

	/**
	 * Allows Derivative classes to handle the XML Attrs without creating a
	 * TypedArray themsevles
	 *
	 * @param a - TypedArray of PullToRefresh Attributes
	 */
	protected void handleStyledAttributes(TypedArray a) {
	}

	/**
	 * Implemented by derived class to return whether the View is in a state
	 * where the user can Pull to Refresh by scrolling from the end.
	 *
	 * @return true if the View is currently in the correct state (for example,
	 *         bottom of a ListView)
	 */
	protected abstract boolean isReadyForPullEnd();

	/**
	 * Implemented by derived class to return whether the View is in a state
	 * where the user can Pull to Refresh by scrolling from the start.
	 *
	 * @return true if the View is currently the correct state (for example, top
	 *         of a ListView)
	 */
	protected abstract boolean isReadyForPullStart();

	/**
	 * Called by {@link #onRestoreInstanceState(Parcelable)} so that derivative
	 * classes can handle their saved instance state.
	 *
	 * @param savedInstanceState - Bundle which contains saved instance state.
	 */
	protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
	}

	/**
	 * Called by {@link #onSaveInstanceState()} so that derivative classes can
	 * save their instance state.
	 *
	 * @param saveState - Bundle to be updated with saved state.
	 */
	protected void onPtrSaveInstanceState(Bundle saveState) {
	}

	/**
	 * Called when the UI has been to be updated to be in the
	 * {@link PullToRefreshBase.State#PULL_TO_REFRESH} state.
	 */
	protected void onPullToRefresh() {
		switch (this.mCurrentMode) {
			case PULL_FROM_END:
                this.mFooterLayout.pullToRefresh();
				break;
			case PULL_FROM_START:
                this.mHeaderLayout.pullToRefresh();
				break;
			default:
				// NO-OP
				break;
		}
	}

	/**
	 * Called when the UI has been to be updated to be in the
	 * {@link PullToRefreshBase.State#REFRESHING} or {@link PullToRefreshBase.State#MANUAL_REFRESHING} state.
	 *
	 * @param doScroll - Whether the UI should scroll for this event.
	 */
	protected void onRefreshing(boolean doScroll) {
		if (this.mMode.showHeaderLoadingLayout()) {
            this.mHeaderLayout.refreshing();
		}
		if (this.mMode.showFooterLoadingLayout()) {
            this.mFooterLayout.refreshing();
		}

		if (doScroll) {
			if (this.mShowViewWhileRefreshing) {

				// Call Refresh Listener when the Scroll has finished
				PullToRefreshBase.OnSmoothScrollFinishedListener listener = new PullToRefreshBase.OnSmoothScrollFinishedListener() {
					@Override
					public void onSmoothScrollFinished() {
                        PullToRefreshBase.this.callRefreshListener();
					}
				};

				switch (this.mCurrentMode) {
					case MANUAL_REFRESH_ONLY:
					case PULL_FROM_END:
                        this.smoothScrollTo(this.getFooterSize(), listener);
						break;
					default:
					case PULL_FROM_START:
                        this.smoothScrollTo(-this.getHeaderSize(), listener);
						break;
				}
			} else {
                this.smoothScrollTo(0);
			}
		} else {
			// We're not scrolling, so just call Refresh Listener now
            this.callRefreshListener();
		}
	}

	/**
	 * Called when the UI has been to be updated to be in the
	 * {@link PullToRefreshBase.State#RELEASE_TO_REFRESH} state.
	 */
	protected void onReleaseToRefresh() {
		switch (this.mCurrentMode) {
			case PULL_FROM_END:
                this.mFooterLayout.releaseToRefresh();
				break;
			case PULL_FROM_START:
                this.mHeaderLayout.releaseToRefresh();
				break;
			default:
				// NO-OP
				break;
		}
	}

	/**
	 * Called when the UI has been to be updated to be in the
	 * {@link PullToRefreshBase.State#RESET} state.
	 */
	protected void onReset() {
        this.mIsBeingDragged = false;
        this.mLayoutVisibilityChangesEnabled = true;

		// Always reset both layouts, just in case...
        this.mHeaderLayout.reset();
        this.mFooterLayout.reset();

        this.smoothScrollTo(0);
	}

	@Override
	protected final void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;

            this.setMode(PullToRefreshBase.Mode.mapIntToValue(bundle.getInt(PullToRefreshBase.STATE_MODE, 0)));
            this.mCurrentMode = PullToRefreshBase.Mode.mapIntToValue(bundle.getInt(PullToRefreshBase.STATE_CURRENT_MODE, 0));

            this.mScrollingWhileRefreshingEnabled = bundle.getBoolean(PullToRefreshBase.STATE_SCROLLING_REFRESHING_ENABLED, false);
            this.mShowViewWhileRefreshing = bundle.getBoolean(PullToRefreshBase.STATE_SHOW_REFRESHING_VIEW, true);

			// Let super Restore Itself
			super.onRestoreInstanceState(bundle.getParcelable(PullToRefreshBase.STATE_SUPER));

			PullToRefreshBase.State viewState = PullToRefreshBase.State.mapIntToValue(bundle.getInt(PullToRefreshBase
                    .STATE_STATE, 0));
			if (viewState == PullToRefreshBase.State.REFRESHING || viewState == PullToRefreshBase.State.MANUAL_REFRESHING) {

                this.setState(viewState, true);
			}

			// Now let derivative classes restore their state
            this.onPtrRestoreInstanceState(bundle);
			return;
		}

		super.onRestoreInstanceState(state);
	}

	@Override
	protected final Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();

		// Let derivative classes get a chance to save state first, that way we
		// can make sure they don't overrite any of our values
        this.onPtrSaveInstanceState(bundle);

		bundle.putInt(PullToRefreshBase.STATE_STATE, this.mState.getIntValue());
		bundle.putInt(PullToRefreshBase.STATE_MODE, this.mMode.getIntValue());
		bundle.putInt(PullToRefreshBase.STATE_CURRENT_MODE, this.mCurrentMode.getIntValue());
		bundle.putBoolean(PullToRefreshBase.STATE_SCROLLING_REFRESHING_ENABLED, this.mScrollingWhileRefreshingEnabled);
		bundle.putBoolean(PullToRefreshBase.STATE_SHOW_REFRESHING_VIEW, this.mShowViewWhileRefreshing);
		bundle.putParcelable(PullToRefreshBase.STATE_SUPER, super.onSaveInstanceState());

		return bundle;
	}

	@Override
	protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (PullToRefreshBase.DEBUG) {
			Log.d(PullToRefreshBase.LOG_TAG, String.format("onSizeChanged. W: %d, H: %d", w, h));
		}

		super.onSizeChanged(w, h, oldw, oldh);

		// We need to update the header/footer when our size changes
        this.refreshLoadingViewsSize();

		// Update the Refreshable View layout
        this.refreshRefreshableViewSize(w, h);

		/**
		 * As we're currently in a Layout Pass, we need to schedule another one
		 * to layout any changes we've made here
		 */
        this.post(new Runnable() {
			@Override
			public void run() {
                PullToRefreshBase.this.requestLayout();
			}
		});
	}

	/**
	 * Re-measure the Loading Views height, and adjust internal padding as
	 * necessary
	 */
	protected final void refreshLoadingViewsSize() {
		int maximumPullScroll = (int) (this.getMaximumPullScroll() * 1.2f);

		int pLeft = this.getPaddingLeft();
		int pTop = this.getPaddingTop();
		int pRight = this.getPaddingRight();
		int pBottom = this.getPaddingBottom();

		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				if (this.mMode.showHeaderLoadingLayout()) {
                    this.mHeaderLayout.setWidth(maximumPullScroll);
					pLeft = -maximumPullScroll;
				} else {
					pLeft = 0;
				}

				if (this.mMode.showFooterLoadingLayout()) {
                    this.mFooterLayout.setWidth(maximumPullScroll);
					pRight = -maximumPullScroll;
				} else {
					pRight = 0;
				}
				break;

			case VERTICAL:
				if (this.mMode.showHeaderLoadingLayout()) {
                    this.mHeaderLayout.setHeight(maximumPullScroll);
					pTop = -maximumPullScroll;
				} else {
					pTop = 0;
				}

				if (this.mMode.showFooterLoadingLayout()) {
                    this.mFooterLayout.setHeight(maximumPullScroll);
					pBottom = -maximumPullScroll;
				} else {
					pBottom = 0;
				}
				break;
		}

		if (PullToRefreshBase.DEBUG) {
			Log.d(PullToRefreshBase.LOG_TAG, String.format("Setting Padding. L: %d, T: %d, R: %d, B: %d", pLeft, pTop, pRight, pBottom));
		}
        this.setPadding(pLeft, pTop, pRight, pBottom);
	}

	protected final void refreshRefreshableViewSize(int width, int height) {
		// We need to set the Height of the Refreshable View to the same as
		// this layout
		LayoutParams lp = (LayoutParams) this.mRefreshableViewWrapper.getLayoutParams();

		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				if (lp.width != width) {
					lp.width = width;
                    this.mRefreshableViewWrapper.requestLayout();
				}
				break;
			case VERTICAL:
				if (lp.height != height) {
					lp.height = height;
                    this.mRefreshableViewWrapper.requestLayout();
				}
				break;
		}
	}

	/**
	 * Helper method which just calls scrollTo() in the correct scrolling
	 * direction.
	 *
	 * @param value - New Scroll value
	 */
	protected final void setHeaderScroll(int value) {
		if (PullToRefreshBase.DEBUG) {
			Log.d(PullToRefreshBase.LOG_TAG, "setHeaderScroll: " + value);
		}

		// Clamp value to with pull scroll range
		int maximumPullScroll = this.getMaximumPullScroll();
		value = Math.min(maximumPullScroll, Math.max(-maximumPullScroll, value));

		if (this.mLayoutVisibilityChangesEnabled) {
			if (value < 0) {
                this.mHeaderLayout.setVisibility(View.VISIBLE);
			} else if (value > 0) {
                this.mFooterLayout.setVisibility(View.VISIBLE);
			} else {
                this.mHeaderLayout.setVisibility(View.INVISIBLE);
                this.mFooterLayout.setVisibility(View.INVISIBLE);
			}
		}

		if (PullToRefreshBase.USE_HW_LAYERS) {
			/**
			 * Use a Hardware Layer on the Refreshable View if we've scrolled at
			 * all. We don't use them on the Header/Footer Views as they change
			 * often, which would negate any HW layer performance boost.
			 */
			ViewCompat.setLayerType(this.mRefreshableViewWrapper, value != 0 ? View.LAYER_TYPE_HARDWARE
					: View.LAYER_TYPE_NONE);
		}

		switch (this.getPullToRefreshScrollDirection()) {
			case VERTICAL:
                this.scrollTo(0, value);
				break;
			case HORIZONTAL:
                this.scrollTo(value, 0);
				break;
		}
	}

	/**
	 * Smooth Scroll to position using the default duration of
	 * {@value #SMOOTH_SCROLL_DURATION_MS} ms.
	 *
	 * @param scrollValue - Position to scroll to
	 */
	protected final void smoothScrollTo(int scrollValue) {
        this.smoothScrollTo(scrollValue, this.getPullToRefreshScrollDuration());
	}

	/**
	 * Smooth Scroll to position using the default duration of
	 * {@value #SMOOTH_SCROLL_DURATION_MS} ms.
	 *
	 * @param scrollValue - Position to scroll to
	 * @param listener - Listener for scroll
	 */
	protected final void smoothScrollTo(int scrollValue, PullToRefreshBase.OnSmoothScrollFinishedListener listener) {
        this.smoothScrollTo(scrollValue, this.getPullToRefreshScrollDuration(), 0, listener);
	}

	/**
	 * Smooth Scroll to position using the longer default duration of
	 * {@value #SMOOTH_SCROLL_LONG_DURATION_MS} ms.
	 *
	 * @param scrollValue - Position to scroll to
	 */
	protected final void smoothScrollToLonger(int scrollValue) {
        this.smoothScrollTo(scrollValue, this.getPullToRefreshScrollDurationLonger());
	}

	/**
	 * Updates the View State when the mode has been set. This does not do any
	 * checking that the mode is different to current state so always updates.
	 */
	protected void updateUIForMode() {
		// We need to use the correct LayoutParam values, based on scroll
		// direction
		LayoutParams lp = this.getLoadingLayoutLayoutParams();

		// Remove Header, and then add Header Loading View again if needed
		if (this == this.mHeaderLayout.getParent()) {
            this.removeView(this.mHeaderLayout);
		}
		if (this.mMode.showHeaderLoadingLayout()) {
            this.addViewInternal(this.mHeaderLayout, 0, lp);
		}

		// Remove Footer, and then add Footer Loading View again if needed
		if (this == this.mFooterLayout.getParent()) {
            this.removeView(this.mFooterLayout);
		}
		if (this.mMode.showFooterLoadingLayout()) {
            this.addViewInternal(this.mFooterLayout, lp);
		}

		// Hide Loading Views
        this.refreshLoadingViewsSize();

		// If we're not using Mode.BOTH, set mCurrentMode to mMode, otherwise
		// set it to pull down
        this.mCurrentMode = this.mMode != PullToRefreshBase.Mode.BOTH ? this.mMode : PullToRefreshBase.Mode.PULL_FROM_START;
	}

	private void addRefreshableView(Context context, T refreshableView) {
        this.mRefreshableViewWrapper = new FrameLayout(context);
        this.mRefreshableViewWrapper.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

        this.addViewInternal(this.mRefreshableViewWrapper, new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	}

	private void callRefreshListener() {
		if (null != this.mOnRefreshListener) {
            this.mOnRefreshListener.onRefresh(this);
		} else if (null != this.mOnRefreshListener2) {
			if (this.mCurrentMode == PullToRefreshBase.Mode.PULL_FROM_START) {
                this.mOnRefreshListener2.onPullDownToRefresh(this);
			} else if (this.mCurrentMode == PullToRefreshBase.Mode.PULL_FROM_END) {
                this.mOnRefreshListener2.onPullUpToRefresh(this);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void init(Context context, AttributeSet attrs) {
		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
                this.setOrientation(LinearLayout.HORIZONTAL);
				break;
			case VERTICAL:
			default:
                this.setOrientation(LinearLayout.VERTICAL);
				break;
		}

        this.setGravity(Gravity.CENTER);

		ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();

		// Styleables from XML
		TypedArray a = context.obtainStyledAttributes(attrs, styleable.PullToRefresh);

		if (a.hasValue(styleable.PullToRefresh_ptrMode)) {
            this.mMode = PullToRefreshBase.Mode.mapIntToValue(a.getInteger(styleable.PullToRefresh_ptrMode, 0));
		}

		if (a.hasValue(styleable.PullToRefresh_ptrAnimationStyle)) {
            this.mLoadingAnimationStyle = PullToRefreshBase.AnimationStyle.mapIntToValue(a.getInteger(
					styleable.PullToRefresh_ptrAnimationStyle, 0));
		}

		// Refreshable View
		// By passing the attrs, we can add ListView/GridView params via XML
        this.mRefreshableView = this.createRefreshableView(context, attrs);
        this.addRefreshableView(context, this.mRefreshableView);

		// We need to create now layouts now
        this.mHeaderLayout = this.createLoadingLayout(context, PullToRefreshBase.Mode.PULL_FROM_START, a);
        this.mFooterLayout = this.createLoadingLayout(context, PullToRefreshBase.Mode.PULL_FROM_END, a);

		/**
		 * Styleables from XML
		 */
		if (a.hasValue(styleable.PullToRefresh_ptrRefreshableViewBackground)) {
			Drawable background = a.getDrawable(styleable.PullToRefresh_ptrRefreshableViewBackground);
			if (null != background) {
                this.mRefreshableView.setBackgroundDrawable(background);
			}
		} else if (a.hasValue(styleable.PullToRefresh_ptrAdapterViewBackground)) {
			Utils.warnDeprecation("ptrAdapterViewBackground", "ptrRefreshableViewBackground");
			Drawable background = a.getDrawable(styleable.PullToRefresh_ptrAdapterViewBackground);
			if (null != background) {
                this.mRefreshableView.setBackgroundDrawable(background);
			}
		}

		if (a.hasValue(styleable.PullToRefresh_ptrOverScroll)) {
            this.mOverScrollEnabled = a.getBoolean(styleable.PullToRefresh_ptrOverScroll, true);
		}

		if (a.hasValue(styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
            this.mScrollingWhileRefreshingEnabled = a.getBoolean(
					styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled, false);
		}

		// Let the derivative classes have a go at handling attributes, then
		// recycle them...
        this.handleStyledAttributes(a);
		a.recycle();

		// Finally update the UI for the modes
        this.updateUIForMode();
	}

	private boolean isReadyForPull() {
		switch (this.mMode) {
			case PULL_FROM_START:
				return this.isReadyForPullStart();
			case PULL_FROM_END:
				return this.isReadyForPullEnd();
			case BOTH:
				return this.isReadyForPullEnd() || this.isReadyForPullStart();
			default:
				return false;
		}
	}

	/**
	 * Actions a Pull Event
	 *
	 * @return true if the Event has been handled, false if there has been no
	 *         change
	 */
	private void pullEvent() {
		int newScrollValue;
		int itemDimension;
		float initialMotionValue, lastMotionValue;

		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				initialMotionValue = this.mInitialMotionX;
				lastMotionValue = this.mLastMotionX;
				break;
			case VERTICAL:
			default:
				initialMotionValue = this.mInitialMotionY;
				lastMotionValue = this.mLastMotionY;
				break;
		}

		switch (this.mCurrentMode) {
			case PULL_FROM_END:
				newScrollValue = Math.round(Math.max(initialMotionValue - lastMotionValue, 0) / PullToRefreshBase
                        .FRICTION);
				itemDimension = this.getFooterSize();
				break;
			case PULL_FROM_START:
			default:
				newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / PullToRefreshBase
                        .FRICTION);
				itemDimension = this.getHeaderSize();
				break;
		}

        this.setHeaderScroll(newScrollValue);

		if (newScrollValue != 0 && !this.isRefreshing()) {
			float scale = Math.abs(newScrollValue) / (float) itemDimension;
			switch (this.mCurrentMode) {
				case PULL_FROM_END:
                    this.mFooterLayout.onPull(scale);
					break;
				case PULL_FROM_START:
				default:
                    this.mHeaderLayout.onPull(scale);
					break;
			}

			if (this.mState != PullToRefreshBase.State.PULL_TO_REFRESH && itemDimension >= Math.abs(newScrollValue)) {
                this.setState(PullToRefreshBase.State.PULL_TO_REFRESH);
			} else if (this.mState == PullToRefreshBase.State.PULL_TO_REFRESH && itemDimension < Math.abs(newScrollValue)) {
                this.setState(PullToRefreshBase.State.RELEASE_TO_REFRESH);
			}
		}
	}

	private LinearLayout.LayoutParams getLoadingLayoutLayoutParams() {
		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.MATCH_PARENT);
			case VERTICAL:
			default:
				return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
		}
	}

	private int getMaximumPullScroll() {
		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				return Math.round(this.getWidth() / PullToRefreshBase.FRICTION);
			case VERTICAL:
			default:
				return Math.round(this.getHeight() / PullToRefreshBase.FRICTION);
		}
	}

	/**
	 * Smooth Scroll to position using the specific duration
	 *
	 * @param scrollValue - Position to scroll to
	 * @param duration - Duration of animation in milliseconds
	 */
	private final void smoothScrollTo(int scrollValue, long duration) {
        this.smoothScrollTo(scrollValue, duration, 0, null);
	}

	private final void smoothScrollTo(int newScrollValue, long duration, long delayMillis,
			PullToRefreshBase.OnSmoothScrollFinishedListener listener) {
		if (null != this.mCurrentSmoothScrollRunnable) {
            this.mCurrentSmoothScrollRunnable.stop();
		}

		int oldScrollValue;
		switch (this.getPullToRefreshScrollDirection()) {
			case HORIZONTAL:
				oldScrollValue = this.getScrollX();
				break;
			case VERTICAL:
			default:
				oldScrollValue = this.getScrollY();
				break;
		}

		if (oldScrollValue != newScrollValue) {
			if (null == this.mScrollAnimationInterpolator) {
				// Default interpolator is a Decelerate Interpolator
                this.mScrollAnimationInterpolator = new DecelerateInterpolator();
			}
            this.mCurrentSmoothScrollRunnable = new PullToRefreshBase.SmoothScrollRunnable(oldScrollValue, newScrollValue, duration, listener);

			if (delayMillis > 0) {
                this.postDelayed(this.mCurrentSmoothScrollRunnable, delayMillis);
			} else {
                this.post(this.mCurrentSmoothScrollRunnable);
			}
		}
	}

	private final void smoothScrollToAndBack(int y) {
        this.smoothScrollTo(y, PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS, 0, new PullToRefreshBase.OnSmoothScrollFinishedListener() {

			@Override
			public void onSmoothScrollFinished() {
                PullToRefreshBase.this.smoothScrollTo(0, PullToRefreshBase.SMOOTH_SCROLL_DURATION_MS, PullToRefreshBase.DEMO_SCROLL_INTERVAL, null);
			}
		});
	}

	public enum AnimationStyle {
		/**
		 * This is the default for Android-PullToRefresh. Allows you to use any
		 * drawable, which is automatically rotated and used as a Progress Bar.
		 */
		ROTATE,

		/**
		 * This is the old default, and what is commonly used on iOS. Uses an
		 * arrow image which flips depending on where the user has scrolled.
		 */
		FLIP;

		static PullToRefreshBase.AnimationStyle getDefault() {
			return PullToRefreshBase.AnimationStyle.ROTATE;
		}

		/**
		 * Maps an int to a specific mode. This is needed when saving state, or
		 * inflating the view from XML where the mode is given through a attr
		 * int.
		 *
		 * @param modeInt - int to map a Mode to
		 * @return Mode that modeInt maps to, or ROTATE by default.
		 */
		static PullToRefreshBase.AnimationStyle mapIntToValue(int modeInt) {
			switch (modeInt) {
				case 0x0:
				default:
					return PullToRefreshBase.AnimationStyle.ROTATE;
				case 0x1:
					return PullToRefreshBase.AnimationStyle.FLIP;
			}
		}

		LoadingLayout createLoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
			switch (this) {
				case ROTATE:
				default:
					return new RotateLoadingLayout(context, mode, scrollDirection, attrs);
				case FLIP:
					return new FlipLoadingLayout(context, mode, scrollDirection, attrs);
			}
		}
	}

	public enum Mode {

		/**
		 * Disable all Pull-to-Refresh gesture and Refreshing handling
		 */
		DISABLED(0x0),

		/**
		 * Only allow the user to Pull from the start of the Refreshable View to
		 * refresh. The start is either the Top or Left, depending on the
		 * scrolling direction.
		 */
		PULL_FROM_START(0x1),

		/**
		 * Only allow the user to Pull from the end of the Refreshable View to
		 * refresh. The start is either the Bottom or Right, depending on the
		 * scrolling direction.
		 */
		PULL_FROM_END(0x2),

		/**
		 * Allow the user to both Pull from the start, from the end to refresh.
		 */
		BOTH(0x3),

		/**
		 * Disables Pull-to-Refresh gesture handling, but allows manually
		 * setting the Refresh state via
		 * {@link PullToRefreshBase#setRefreshing() setRefreshing()}.
		 */
		MANUAL_REFRESH_ONLY(0x4);

		/**
		 * @deprecated Use {@link #PULL_FROM_START} from now on.
		 */
		public static PullToRefreshBase.Mode PULL_DOWN_TO_REFRESH = PULL_FROM_START;

		/**
		 * @deprecated Use {@link #PULL_FROM_END} from now on.
		 */
		public static PullToRefreshBase.Mode PULL_UP_TO_REFRESH = PULL_FROM_END;

		/**
		 * Maps an int to a specific mode. This is needed when saving state, or
		 * inflating the view from XML where the mode is given through a attr
		 * int.
		 *
		 * @param modeInt - int to map a Mode to
		 * @return Mode that modeInt maps to, or PULL_FROM_START by default.
		 */
		static PullToRefreshBase.Mode mapIntToValue(int modeInt) {
			for (PullToRefreshBase.Mode value : PullToRefreshBase.Mode.values()) {
				if (modeInt == value.getIntValue()) {
					return value;
				}
			}

			// If not, return default
			return PullToRefreshBase.Mode.getDefault();
		}

		static PullToRefreshBase.Mode getDefault() {
			return PullToRefreshBase.Mode.PULL_FROM_START;
		}

		private final int mIntValue;

		// The modeInt values need to match those from attrs.xml
		Mode(int modeInt) {
            this.mIntValue = modeInt;
		}

		/**
		 * @return true if the mode permits Pull-to-Refresh
		 */
		boolean permitsPullToRefresh() {
			return !(this == PullToRefreshBase.Mode.DISABLED || this == PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
		}

		/**
		 * @return true if this mode wants the Loading Layout Header to be shown
		 */
		public boolean showHeaderLoadingLayout() {
			return this == PullToRefreshBase.Mode.PULL_FROM_START || this == PullToRefreshBase.Mode.BOTH;
		}

		/**
		 * @return true if this mode wants the Loading Layout Footer to be shown
		 */
		public boolean showFooterLoadingLayout() {
			return this == PullToRefreshBase.Mode.PULL_FROM_END || this == PullToRefreshBase.Mode.BOTH || this == PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY;
		}

		int getIntValue() {
			return this.mIntValue;
		}

	}

	// ===========================================================
	// Inner, Anonymous Classes, and Enumerations
	// ===========================================================

	/**
	 * Simple Listener that allows you to be notified when the user has scrolled
	 * to the end of the AdapterView. See (
	 * {@link PullToRefreshAdapterViewBase#setOnLastItemVisibleListener}.
	 *
	 * @author Chris Banes
	 */
	public interface OnLastItemVisibleListener {

		/**
		 * Called when the user has scrolled to the end of the list
		 */
        void onLastItemVisible();

	}

	/**
	 * Listener that allows you to be notified when the user has started or
	 * finished a touch event. Useful when you want to append extra UI events
	 * (such as sounds). See (
	 * {@link PullToRefreshAdapterViewBase#setOnPullEventListener}.
	 *
	 * @author Chris Banes
	 */
	public interface OnPullEventListener<V extends View> {

		/**
		 * Called when the internal state has been changed, usually by the user
		 * pulling.
		 *
		 * @param refreshView - View which has had it's state change.
		 * @param state - The new state of View.
		 * @param direction - One of {@link PullToRefreshBase.Mode#PULL_FROM_START} or
		 *            {@link PullToRefreshBase.Mode#PULL_FROM_END} depending on which direction
		 *            the user is pulling. Only useful when <var>state</var> is
		 *            {@link PullToRefreshBase.State#PULL_TO_REFRESH} or
		 *            {@link PullToRefreshBase.State#RELEASE_TO_REFRESH}.
		 */
        void onPullEvent(PullToRefreshBase<V> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction);

	}

	/**
	 * Simple Listener to listen for any callbacks to Refresh.
	 *
	 * @author Chris Banes
	 */
	public interface OnRefreshListener<V extends View> {

		/**
		 * onRefresh will be called for both a Pull from start, and Pull from
		 * end
		 */
        void onRefresh(PullToRefreshBase<V> refreshView);

	}

	/**
	 * An advanced version of the Listener to listen for callbacks to Refresh.
	 * This listener is different as it allows you to differentiate between Pull
	 * Ups, and Pull Downs.
	 *
	 * @author Chris Banes
	 */
	public interface OnRefreshListener2<V extends View> {
		// TODO These methods need renaming to START/END rather than DOWN/UP

		/**
		 * onPullDownToRefresh will be called only when the user has Pulled from
		 * the start, and released.
		 */
        void onPullDownToRefresh(PullToRefreshBase<V> refreshView);

		/**
		 * onPullUpToRefresh will be called only when the user has Pulled from
		 * the end, and released.
		 */
        void onPullUpToRefresh(PullToRefreshBase<V> refreshView);

	}

	public enum Orientation {
		VERTICAL, HORIZONTAL
    }

	public enum State {

		/**
		 * When the UI is in a state which means that user is not interacting
		 * with the Pull-to-Refresh function.
		 */
		RESET(0x0),

		/**
		 * When the UI is being pulled by the user, but has not been pulled far
		 * enough so that it refreshes when released.
		 */
		PULL_TO_REFRESH(0x1),

		/**
		 * When the UI is being pulled by the user, and <strong>has</strong>
		 * been pulled far enough so that it will refresh when released.
		 */
		RELEASE_TO_REFRESH(0x2),

		/**
		 * When the UI is currently refreshing, caused by a pull gesture.
		 */
		REFRESHING(0x8),

		/**
		 * When the UI is currently refreshing, caused by a call to
		 * {@link PullToRefreshBase#setRefreshing() setRefreshing()}.
		 */
		MANUAL_REFRESHING(0x9),

		/**
		 * When the UI is currently overscrolling, caused by a fling on the
		 * Refreshable View.
		 */
		OVERSCROLLING(0x10);

		/**
		 * Maps an int to a specific state. This is needed when saving state.
		 *
		 * @param stateInt - int to map a State to
		 * @return State that stateInt maps to
		 */
		static PullToRefreshBase.State mapIntToValue(int stateInt) {
			for (PullToRefreshBase.State value : PullToRefreshBase.State.values()) {
				if (stateInt == value.getIntValue()) {
					return value;
				}
			}

			// If not, return default
			return PullToRefreshBase.State.RESET;
		}

		private final int mIntValue;

		State(int intValue) {
            this.mIntValue = intValue;
		}

		int getIntValue() {
			return this.mIntValue;
		}
	}

	final class SmoothScrollRunnable implements Runnable {
		private final Interpolator mInterpolator;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final long mDuration;
		private final PullToRefreshBase.OnSmoothScrollFinishedListener mListener;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(int fromY, int toY, long duration, PullToRefreshBase.OnSmoothScrollFinishedListener listener) {
            this.mScrollFromY = fromY;
            this.mScrollToY = toY;
            this.mInterpolator = PullToRefreshBase.this.mScrollAnimationInterpolator;
            this.mDuration = duration;
            this.mListener = listener;
		}

		@Override
		public void run() {

			/**
			 * Only set mStartTime if this is the first time we're starting,
			 * else actually calculate the Y delta
			 */
			if (this.mStartTime == -1) {
                this.mStartTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = 1000 * (System.currentTimeMillis() - this.mStartTime) / this.mDuration;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				int deltaY = Math.round((this.mScrollFromY - this.mScrollToY)
						* this.mInterpolator.getInterpolation(normalizedTime / 1000f));
                this.mCurrentY = this.mScrollFromY - deltaY;
                PullToRefreshBase.this.setHeaderScroll(this.mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (this.mContinueRunning && this.mScrollToY != this.mCurrentY) {
				ViewCompat.postOnAnimation(PullToRefreshBase.this, this);
			} else {
				if (null != this.mListener) {
                    this.mListener.onSmoothScrollFinished();
				}
			}
		}

		public void stop() {
            this.mContinueRunning = false;
            PullToRefreshBase.this.removeCallbacks(this);
		}
	}

	interface OnSmoothScrollFinishedListener {
		void onSmoothScrollFinished();
	}

}
