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
package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;
import com.handmark.pulltorefresh.library.R.drawable;
import com.handmark.pulltorefresh.library.R.styleable;

public class RotateLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 1200;

	private final Animation mRotateAnimation;
	private final Matrix mHeaderImageMatrix;

	private float mRotationPivotX, mRotationPivotY;

	private final boolean mRotateDrawableWhilePulling;

	public RotateLoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);

        this.mRotateDrawableWhilePulling = attrs.getBoolean(styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

        this.mHeaderImage.setScaleType(ImageView.ScaleType.MATRIX);
        this.mHeaderImageMatrix = new Matrix();
        this.mHeaderImage.setImageMatrix(this.mHeaderImageMatrix);

        this.mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
        this.mRotateAnimation.setInterpolator(LoadingLayout.ANIMATION_INTERPOLATOR);
        this.mRotateAnimation.setDuration(RotateLoadingLayout.ROTATION_ANIMATION_DURATION);
        this.mRotateAnimation.setRepeatCount(Animation.INFINITE);
        this.mRotateAnimation.setRepeatMode(Animation.RESTART);
	}

	public void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
            this.mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
            this.mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	protected void onPullImpl(float scaleOfLayout) {
		float angle;
		if (this.mRotateDrawableWhilePulling) {
			angle = scaleOfLayout * 90f;
		} else {
			angle = Math.max(0f, Math.min(180f, scaleOfLayout * 360f - 180f));
		}

        this.mHeaderImageMatrix.setRotate(angle, this.mRotationPivotX, this.mRotationPivotY);
        this.mHeaderImage.setImageMatrix(this.mHeaderImageMatrix);
	}

	@Override
	protected void refreshingImpl() {
        this.mHeaderImage.startAnimation(this.mRotateAnimation);
	}

	@Override
	protected void resetImpl() {
        this.mHeaderImage.clearAnimation();
        this.resetImageRotation();
	}

	private void resetImageRotation() {
		if (null != this.mHeaderImageMatrix) {
            this.mHeaderImageMatrix.reset();
            this.mHeaderImage.setImageMatrix(this.mHeaderImageMatrix);
		}
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected int getDefaultDrawableResId() {
		return drawable.default_ptr_rotate;
	}

}
