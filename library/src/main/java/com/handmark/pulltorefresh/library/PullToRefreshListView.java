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

import android.R.id;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.R.styleable;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {

	private LoadingLayout mHeaderLoadingView;
	private LoadingLayout mFooterLoadingView;

	private FrameLayout mLvFooterLoadingFrame;

	private boolean mListViewExtrasEnabled;

	public PullToRefreshListView(Context context) {
		super(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshListView(Context context, PullToRefreshBase.Mode mode) {
		super(context, mode);
	}

	public PullToRefreshListView(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final PullToRefreshBase.Orientation getPullToRefreshScrollDirection() {
		return PullToRefreshBase.Orientation.VERTICAL;
	}

	@Override
	protected void onRefreshing(boolean doScroll) {
		/**
		 * If we're not showing the Refreshing view, or the list is empty, the
		 * the header/footer views won't show so we use the normal method.
		 */
		ListAdapter adapter = this.mRefreshableView.getAdapter();
		if (!this.mListViewExtrasEnabled || !this.getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
			super.onRefreshing(doScroll);
			return;
		}

		super.onRefreshing(false);

		LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
		int selection, scrollToY;

		switch (this.getCurrentMode()) {
			case MANUAL_REFRESH_ONLY:
			case PULL_FROM_END:
				origLoadingView = this.getFooterLayout();
				listViewLoadingView = this.mFooterLoadingView;
				oppositeListViewLoadingView = this.mHeaderLoadingView;
				selection = this.mRefreshableView.getCount() - 1;
				scrollToY = this.getScrollY() - this.getFooterSize();
				break;
			case PULL_FROM_START:
			default:
				origLoadingView = this.getHeaderLayout();
				listViewLoadingView = this.mHeaderLoadingView;
				oppositeListViewLoadingView = this.mFooterLoadingView;
				selection = 0;
				scrollToY = this.getScrollY() + this.getHeaderSize();
				break;
		}

		// Hide our original Loading View
		origLoadingView.reset();
		origLoadingView.hideAllViews();

		// Make sure the opposite end is hidden too
		oppositeListViewLoadingView.setVisibility(View.GONE);

		// Show the ListView Loading View and set it to refresh.
		listViewLoadingView.setVisibility(View.VISIBLE);
		listViewLoadingView.refreshing();

		if (doScroll) {
			// We need to disable the automatic visibility changes for now
            this.disableLoadingLayoutVisibilityChanges();

			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
            this.setHeaderScroll(scrollToY);

			// Make sure the ListView is scrolled to show the loading
			// header/footer
            this.mRefreshableView.setSelection(selection);

			// Smooth scroll as normal
            this.smoothScrollTo(0);
		}
	}

	@Override
	protected void onReset() {
		/**
		 * If the extras are not enabled, just call up to super and return.
		 */
		if (!this.mListViewExtrasEnabled) {
			super.onReset();
			return;
		}

		LoadingLayout originalLoadingLayout, listViewLoadingLayout;
		int scrollToHeight, selection;
		boolean scrollLvToEdge;

		switch (this.getCurrentMode()) {
			case MANUAL_REFRESH_ONLY:
			case PULL_FROM_END:
				originalLoadingLayout = this.getFooterLayout();
				listViewLoadingLayout = this.mFooterLoadingView;
				selection = this.mRefreshableView.getCount() - 1;
				scrollToHeight = this.getFooterSize();
				scrollLvToEdge = Math.abs(this.mRefreshableView.getLastVisiblePosition() - selection) <= 1;
				break;
			case PULL_FROM_START:
			default:
				originalLoadingLayout = this.getHeaderLayout();
				listViewLoadingLayout = this.mHeaderLoadingView;
				scrollToHeight = -this.getHeaderSize();
				selection = 0;
				scrollLvToEdge = Math.abs(this.mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
				break;
		}

		// If the ListView header loading layout is showing, then we need to
		// flip so that the original one is showing instead
		if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {

			// Set our Original View to Visible
			originalLoadingLayout.showInvisibleViews();

			// Hide the ListView Header/Footer
			listViewLoadingLayout.setVisibility(View.GONE);

			/**
			 * Scroll so the View is at the same Y as the ListView
			 * header/footer, but only scroll if: we've pulled to refresh, it's
			 * positioned correctly
			 */
			if (scrollLvToEdge && this.getState() != PullToRefreshBase.State.MANUAL_REFRESHING) {
                this.mRefreshableView.setSelection(selection);
                this.setHeaderScroll(scrollToHeight);
			}
		}

		// Finally, call up to super
		super.onReset();
	}

	@Override
	protected LoadingLayoutProxy createLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
		LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);

		if (this.mListViewExtrasEnabled) {
			PullToRefreshBase.Mode mode = this.getMode();

			if (includeStart && mode.showHeaderLoadingLayout()) {
				proxy.addLayout(this.mHeaderLoadingView);
			}
			if (includeEnd && mode.showFooterLoadingLayout()) {
				proxy.addLayout(this.mFooterLoadingView);
			}
		}

		return proxy;
	}

	protected ListView createListView(Context context, AttributeSet attrs) {
		ListView lv;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			lv = new InternalListViewSDK9(context, attrs);
		} else {
			lv = new PullToRefreshListView.InternalListView(context, attrs);
		}
		return lv;
	}

	@Override
	protected ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = this.createListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(id.list);
		return lv;
	}

	@Override
	protected void handleStyledAttributes(TypedArray a) {
		super.handleStyledAttributes(a);

        this.mListViewExtrasEnabled = a.getBoolean(styleable.PullToRefresh_ptrListViewExtrasEnabled, true);

		if (this.mListViewExtrasEnabled) {
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);

			// Create Loading Views ready for use later
			FrameLayout frame = new FrameLayout(this.getContext());
            this.mHeaderLoadingView = this.createLoadingLayout(this.getContext(), PullToRefreshBase.Mode.PULL_FROM_START, a);
            this.mHeaderLoadingView.setVisibility(View.GONE);
			frame.addView(this.mHeaderLoadingView, lp);
            this.mRefreshableView.addHeaderView(frame, null, false);

            this.mLvFooterLoadingFrame = new FrameLayout(this.getContext());
            this.mFooterLoadingView = this.createLoadingLayout(this.getContext(), PullToRefreshBase.Mode.PULL_FROM_END, a);
            this.mFooterLoadingView.setVisibility(View.GONE);
            this.mLvFooterLoadingFrame.addView(this.mFooterLoadingView, lp);

			/**
			 * If the value for Scrolling While Refreshing hasn't been
			 * explicitly set via XML, enable Scrolling While Refreshing.
			 */
			if (!a.hasValue(styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
                this.setScrollingWhileRefreshingEnabled(true);
			}
		}
	}

	@TargetApi(9)
	final class InternalListViewSDK9 extends PullToRefreshListView.InternalListView {

		public InternalListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshListView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

			return returnValue;
		}
	}

	protected class InternalListView extends ListView implements EmptyViewMethodAccessor {

		private boolean mAddedLvFooter;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				super.dispatchDraw(canvas);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent ev) {
			/**
			 * This is a bit hacky, but Samsung's ListView has got a bug in it
			 * when using Header/Footer Views and the list is empty. This masks
			 * the issue so that it doesn't cause an FC. See Issue #66.
			 */
			try {
				return super.dispatchTouchEvent(ev);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public void setAdapter(ListAdapter adapter) {
			// Add the Footer View at the last possible moment
			if (null != PullToRefreshListView.this.mLvFooterLoadingFrame && !this.mAddedLvFooter) {
                this.addFooterView(PullToRefreshListView.this.mLvFooterLoadingFrame, null, false);
                this.mAddedLvFooter = true;
			}

			super.setAdapter(adapter);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

	}

}
