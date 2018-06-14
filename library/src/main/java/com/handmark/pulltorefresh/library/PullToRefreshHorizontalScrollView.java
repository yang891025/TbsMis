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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.handmark.pulltorefresh.library.R.id;

public class PullToRefreshHorizontalScrollView extends PullToRefreshBase<HorizontalScrollView> {

	public PullToRefreshHorizontalScrollView(Context context) {
		super(context);
	}

	public PullToRefreshHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshHorizontalScrollView(Context context, PullToRefreshBase.Mode mode) {
		super(context, mode);
	}

	public PullToRefreshHorizontalScrollView(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final PullToRefreshBase.Orientation getPullToRefreshScrollDirection() {
		return PullToRefreshBase.Orientation.HORIZONTAL;
	}

	@Override
	protected HorizontalScrollView createRefreshableView(Context context, AttributeSet attrs) {
		HorizontalScrollView scrollView;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			scrollView = new InternalHorizontalScrollViewSDK9(context, attrs);
		} else {
			scrollView = new HorizontalScrollView(context, attrs);
		}

		scrollView.setId(id.scrollview);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullStart() {
		return this.mRefreshableView.getScrollX() == 0;
	}

	@Override
	protected boolean isReadyForPullEnd() {
		View scrollViewChild = this.mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return this.mRefreshableView.getScrollX() >= scrollViewChild.getWidth() - this.getWidth();
		}
		return false;
	}

	@TargetApi(9)
	final class InternalHorizontalScrollViewSDK9 extends HorizontalScrollView {

		public InternalHorizontalScrollViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshHorizontalScrollView.this, deltaX, scrollX, deltaY, scrollY,
                    this.getScrollRange(), isTouchEvent);

			return returnValue;
		}

		/**
		 * Taken from the AOSP ScrollView source
		 */
		private int getScrollRange() {
			int scrollRange = 0;
			if (this.getChildCount() > 0) {
				View child = this.getChildAt(0);
				scrollRange = Math.max(0, child.getWidth() - (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()));
			}
			return scrollRange;
		}
	}
}
