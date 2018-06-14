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
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;

import com.handmark.pulltorefresh.library.R.dimen;
import com.handmark.pulltorefresh.library.R.styleable;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.IndicatorLayout;

public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T> implements
		AbsListView.OnScrollListener {

	private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
		FrameLayout.LayoutParams newLp = null;

		if (null != lp) {
			newLp = new FrameLayout.LayoutParams(lp);

			if (lp instanceof LayoutParams) {
				newLp.gravity = ((LayoutParams) lp).gravity;
			} else {
				newLp.gravity = Gravity.CENTER;
			}
		}

		return newLp;
	}

	private boolean mLastItemVisible;
	private AbsListView.OnScrollListener mOnScrollListener;
	private PullToRefreshBase.OnLastItemVisibleListener mOnLastItemVisibleListener;
	private View mEmptyView;

	private IndicatorLayout mIndicatorIvTop;
	private IndicatorLayout mIndicatorIvBottom;

	private boolean mShowIndicator;
	private boolean mScrollEmptyView = true;

	public PullToRefreshAdapterViewBase(Context context) {
		super(context);
        this.mRefreshableView.setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.mRefreshableView.setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, PullToRefreshBase.Mode mode) {
		super(context, mode);
        this.mRefreshableView.setOnScrollListener(this);
	}

	public PullToRefreshAdapterViewBase(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.AnimationStyle animStyle) {
		super(context, mode, animStyle);
        this.mRefreshableView.setOnScrollListener(this);
	}

	/**
	 * Gets whether an indicator graphic should be displayed when the View is in
	 * a state where a Pull-to-Refresh can happen. An example of this state is
	 * when the Adapter View is scrolled to the top and the mode is set to
	 * {@link PullToRefreshBase.Mode#PULL_FROM_START}. The default value is <var>true</var> if
	 * {@link PullToRefreshBase#isPullToRefreshOverScrollEnabled()
	 * isPullToRefreshOverScrollEnabled()} returns false.
	 *
	 * @return true if the indicators will be shown
	 */
	public boolean getShowIndicator() {
		return this.mShowIndicator;
	}

	public final void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                               int totalItemCount) {

		if (PullToRefreshBase.DEBUG) {
			Log.d(PullToRefreshBase.LOG_TAG, "First Visible: " + firstVisibleItem + ". Visible Count: " + visibleItemCount
					+ ". Total Items:" + totalItemCount);
		}

		/**
		 * Set whether the Last Item is Visible. lastVisibleItemIndex is a
		 * zero-based index, so we minus one totalItemCount to check
		 */
		if (null != this.mOnLastItemVisibleListener) {
            this.mLastItemVisible = totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount - 1;
		}

		// If we're showing the indicator, check positions...
		if (this.getShowIndicatorInternal()) {
            this.updateIndicatorViewsVisibility();
		}

		// Finally call OnScrollListener if we have one
		if (null != this.mOnScrollListener) {
            this.mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public final void onScrollStateChanged(AbsListView view, int state) {
		/**
		 * Check that the scrolling has stopped, and that the last item is
		 * visible.
		 */
		if (state == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && null != this.mOnLastItemVisibleListener && this
                .mLastItemVisible) {

            this.mOnLastItemVisibleListener.onLastItemVisible();
		}

		if (null != this.mOnScrollListener) {
            this.mOnScrollListener.onScrollStateChanged(view, state);
		}
	}

	/**
	 * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
	 * getRefreshableView()}.
	 * {@link AdapterView#setAdapter(Adapter)}
	 * setAdapter(adapter)}. This is just for convenience!
	 *
	 * @param adapter - Adapter to set
	 */
	public void setAdapter(ListAdapter adapter) {
        this.mRefreshableView.setAdapter(adapter);
	}

	/**
	 * Sets the Empty View to be used by the Adapter View.
	 * <p/>
	 * We need it handle it ourselves so that we can Pull-to-Refresh when the
	 * Empty View is shown.
	 * <p/>
	 * Please note, you do <strong>not</strong> usually need to call this method
	 * yourself. Calling setEmptyView on the AdapterView will automatically call
	 * this method and set everything up. This includes when the Android
	 * Framework automatically sets the Empty View based on it's ID.
	 *
	 * @param newEmptyView - Empty View to be used
	 */
	public final void setEmptyView(View newEmptyView) {
		FrameLayout refreshableViewWrapper = this.getRefreshableViewWrapper();

		if (null != newEmptyView) {
			// New view needs to be clickable so that Android recognizes it as a
			// target for Touch Events
			newEmptyView.setClickable(true);

			ViewParent newEmptyViewParent = newEmptyView.getParent();
			if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
			}

			// We need to convert any LayoutParams so that it works in our
			// FrameLayout
			FrameLayout.LayoutParams lp = PullToRefreshAdapterViewBase.convertEmptyViewLayoutParams(newEmptyView.getLayoutParams());
			if (null != lp) {
				refreshableViewWrapper.addView(newEmptyView, lp);
			} else {
				refreshableViewWrapper.addView(newEmptyView);
			}
		}

		if (this.mRefreshableView instanceof EmptyViewMethodAccessor) {
			((EmptyViewMethodAccessor) this.mRefreshableView).setEmptyViewInternal(newEmptyView);
		} else {
            this.mRefreshableView.setEmptyView(newEmptyView);
		}
        this.mEmptyView = newEmptyView;
	}

	/**
	 * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
	 * getRefreshableView()}.
	 * {@link AdapterView#setOnItemClickListener(AdapterView.OnItemClickListener)
	 * setOnItemClickListener(listener)}. This is just for convenience!
	 *
	 * @param listener - OnItemClickListener to use
	 */
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mRefreshableView.setOnItemClickListener(listener);
	}

	public final void setOnLastItemVisibleListener(PullToRefreshBase.OnLastItemVisibleListener listener) {
        this.mOnLastItemVisibleListener = listener;
	}

	public final void setOnScrollListener(AbsListView.OnScrollListener listener) {
        this.mOnScrollListener = listener;
	}

	public final void setScrollEmptyView(boolean doScroll) {
        this.mScrollEmptyView = doScroll;
	}

	/**
	 * Sets whether an indicator graphic should be displayed when the View is in
	 * a state where a Pull-to-Refresh can happen. An example of this state is
	 * when the Adapter View is scrolled to the top and the mode is set to
	 * {@link PullToRefreshBase.Mode#PULL_FROM_START}
	 *
	 * @param showIndicator - true if the indicators should be shown.
	 */
	public void setShowIndicator(boolean showIndicator) {
        this.mShowIndicator = showIndicator;

		if (this.getShowIndicatorInternal()) {
			// If we're set to Show Indicator, add/update them
            this.addIndicatorViews();
		} else {
			// If not, then remove then
            this.removeIndicatorViews();
		}
	}

    @Override
	protected void onPullToRefresh() {
		super.onPullToRefresh();

		if (this.getShowIndicatorInternal()) {
			switch (this.getCurrentMode()) {
				case PULL_FROM_END:
                    this.mIndicatorIvBottom.pullToRefresh();
					break;
				case PULL_FROM_START:
                    this.mIndicatorIvTop.pullToRefresh();
					break;
				default:
					// NO-OP
					break;
			}
		}
	}

	protected void onRefreshing(boolean doScroll) {
		super.onRefreshing(doScroll);

		if (this.getShowIndicatorInternal()) {
            this.updateIndicatorViewsVisibility();
		}
	}

	@Override
	protected void onReleaseToRefresh() {
		super.onReleaseToRefresh();

		if (this.getShowIndicatorInternal()) {
			switch (this.getCurrentMode()) {
				case PULL_FROM_END:
                    this.mIndicatorIvBottom.releaseToRefresh();
					break;
				case PULL_FROM_START:
                    this.mIndicatorIvTop.releaseToRefresh();
					break;
				default:
					// NO-OP
					break;
			}
		}
	}

	@Override
	protected void onReset() {
		super.onReset();

		if (this.getShowIndicatorInternal()) {
            this.updateIndicatorViewsVisibility();
		}
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		// Set Show Indicator to the XML value, or default value
        this.mShowIndicator = a.getBoolean(styleable.PullToRefresh_ptrShowIndicator, !this
                .isPullToRefreshOverScrollEnabled());
	}

	protected boolean isReadyForPullStart() {
		return this.isFirstItemVisible();
	}

	protected boolean isReadyForPullEnd() {
		return this.isLastItemVisible();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (null != this.mEmptyView && !this.mScrollEmptyView) {
            this.mEmptyView.scrollTo(-l, -t);
		}
	}

	@Override
	protected void updateUIForMode() {
		super.updateUIForMode();

		// Check Indicator Views consistent with new Mode
		if (this.getShowIndicatorInternal()) {
            this.addIndicatorViews();
		} else {
            this.removeIndicatorViews();
		}
	}

	private void addIndicatorViews() {
		PullToRefreshBase.Mode mode = this.getMode();
		FrameLayout refreshableViewWrapper = this.getRefreshableViewWrapper();

		if (mode.showHeaderLoadingLayout() && null == this.mIndicatorIvTop) {
			// If the mode can pull down, and we don't have one set already
            this.mIndicatorIvTop = new IndicatorLayout(this.getContext(), PullToRefreshBase.Mode.PULL_FROM_START);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.rightMargin = this.getResources().getDimensionPixelSize(dimen.indicator_right_padding);
			params.gravity = Gravity.TOP | Gravity.RIGHT;
			refreshableViewWrapper.addView(this.mIndicatorIvTop, params);

		} else if (!mode.showHeaderLoadingLayout() && null != this.mIndicatorIvTop) {
			// If we can't pull down, but have a View then remove it
			refreshableViewWrapper.removeView(this.mIndicatorIvTop);
            this.mIndicatorIvTop = null;
		}

		if (mode.showFooterLoadingLayout() && null == this.mIndicatorIvBottom) {
			// If the mode can pull down, and we don't have one set already
            this.mIndicatorIvBottom = new IndicatorLayout(this.getContext(), PullToRefreshBase.Mode.PULL_FROM_END);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.rightMargin = this.getResources().getDimensionPixelSize(dimen.indicator_right_padding);
			params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
			refreshableViewWrapper.addView(this.mIndicatorIvBottom, params);

		} else if (!mode.showFooterLoadingLayout() && null != this.mIndicatorIvBottom) {
			// If we can't pull down, but have a View then remove it
			refreshableViewWrapper.removeView(this.mIndicatorIvBottom);
            this.mIndicatorIvBottom = null;
		}
	}

	private boolean getShowIndicatorInternal() {
		return this.mShowIndicator && this.isPullToRefreshEnabled();
	}

	private boolean isFirstItemVisible() {
		Adapter adapter = this.mRefreshableView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			if (PullToRefreshBase.DEBUG) {
				Log.d(PullToRefreshBase.LOG_TAG, "isFirstItemVisible. Empty View.");
			}
			return true;

		} else {

			/**
			 * This check should really just be:
			 * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
			 * internally use a HeaderView which messes the positions up. For
			 * now we'll just add one to account for it and rely on the inner
			 * condition which checks getTop().
			 */
			if (this.mRefreshableView.getFirstVisiblePosition() <= 1) {
				View firstVisibleChild = this.mRefreshableView.getChildAt(0);
				if (firstVisibleChild != null) {
					return firstVisibleChild.getTop() >= this.mRefreshableView.getTop();
				}
			}
		}

		return false;
	}

	private boolean isLastItemVisible() {
		Adapter adapter = this.mRefreshableView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			if (PullToRefreshBase.DEBUG) {
				Log.d(PullToRefreshBase.LOG_TAG, "isLastItemVisible. Empty View.");
			}
			return true;
		} else {
			int lastItemPosition = this.mRefreshableView.getCount() - 1;
			int lastVisiblePosition = this.mRefreshableView.getLastVisiblePosition();

			if (PullToRefreshBase.DEBUG) {
				Log.d(PullToRefreshBase.LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
						+ lastVisiblePosition);
			}

			/**
			 * This check should really just be: lastVisiblePosition ==
			 * lastItemPosition, but PtRListView internally uses a FooterView
			 * which messes the positions up. For me we'll just subtract one to
			 * account for it and rely on the inner condition which checks
			 * getBottom().
			 */
			if (lastVisiblePosition >= lastItemPosition - 1) {
				int childIndex = lastVisiblePosition - this.mRefreshableView.getFirstVisiblePosition();
				View lastVisibleChild = this.mRefreshableView.getChildAt(childIndex);
				if (lastVisibleChild != null) {
					return lastVisibleChild.getBottom() <= this.mRefreshableView.getBottom();
				}
			}
		}

		return false;
	}

	private void removeIndicatorViews() {
		if (null != this.mIndicatorIvTop) {
            this.getRefreshableViewWrapper().removeView(this.mIndicatorIvTop);
            this.mIndicatorIvTop = null;
		}

		if (null != this.mIndicatorIvBottom) {
            this.getRefreshableViewWrapper().removeView(this.mIndicatorIvBottom);
            this.mIndicatorIvBottom = null;
		}
	}

	private void updateIndicatorViewsVisibility() {
		if (null != this.mIndicatorIvTop) {
			if (!this.isRefreshing() && this.isReadyForPullStart()) {
				if (!this.mIndicatorIvTop.isVisible()) {
                    this.mIndicatorIvTop.show();
				}
			} else {
				if (this.mIndicatorIvTop.isVisible()) {
                    this.mIndicatorIvTop.hide();
				}
			}
		}

		if (null != this.mIndicatorIvBottom) {
			if (!this.isRefreshing() && this.isReadyForPullEnd()) {
				if (!this.mIndicatorIvBottom.isVisible()) {
                    this.mIndicatorIvBottom.show();
				}
			} else {
				if (this.mIndicatorIvBottom.isVisible()) {
                    this.mIndicatorIvBottom.hide();
				}
			}
		}
	}
}
