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
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;

public class PullToRefreshExpandableListView extends PullToRefreshAdapterViewBase<ExpandableListView> {

	public PullToRefreshExpandableListView(Context context) {
		super(context);
	}

	public PullToRefreshExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullToRefreshExpandableListView(Context context, PullToRefreshBase.Mode mode) {
		super(context, mode);
	}

	public PullToRefreshExpandableListView(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.AnimationStyle style) {
		super(context, mode, style);
	}

	@Override
	public final PullToRefreshBase.Orientation getPullToRefreshScrollDirection() {
		return PullToRefreshBase.Orientation.VERTICAL;
	}

	@Override
	protected ExpandableListView createRefreshableView(Context context, AttributeSet attrs) {
		ExpandableListView lv;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			lv = new InternalExpandableListViewSDK9(context, attrs);
		} else {
			lv = new PullToRefreshExpandableListView.InternalExpandableListView(context, attrs);
		}

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(id.list);
		return lv;
	}

	class InternalExpandableListView extends ExpandableListView implements EmptyViewMethodAccessor {

		public InternalExpandableListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshExpandableListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}
	}

	@TargetApi(9)
	final class InternalExpandableListViewSDK9 extends PullToRefreshExpandableListView.InternalExpandableListView {

		public InternalExpandableListViewSDK9(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

			boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
					scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

			// Does all of the hard work...
			OverscrollHelper.overScrollBy(PullToRefreshExpandableListView.this, deltaX, scrollX, deltaY, scrollY,
					isTouchEvent);

			return returnValue;
		}
	}
}
