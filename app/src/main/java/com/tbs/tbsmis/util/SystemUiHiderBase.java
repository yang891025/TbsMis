package com.tbs.tbsmis.util;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * A base implementation of {@link SystemUiHider}. Uses APIs available in all
 * API levels to show and hide the status bar.
 */
public class SystemUiHiderBase extends SystemUiHider {
	/**
	 * Whether or not the system UI is currently visible. This is a cached value
	 * from calls to {@link #hide()} and {@link #show()}.
	 */
	private boolean mVisible = true;

	/**
	 * Constructor not intended to be called by clients. Use
	 * {@link SystemUiHider#getInstance} to obtain an instance.
	 */
	protected SystemUiHiderBase(Activity activity, View anchorView, int flags) {
		super(activity, anchorView, flags);
	}

	@Override
	public void setup() {
		if ((this.mFlags & SystemUiHider.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES) == 0) {
            this.mActivity.getWindow().setFlags(
					LayoutParams.FLAG_LAYOUT_IN_SCREEN
							| LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					LayoutParams.FLAG_LAYOUT_IN_SCREEN
							| LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	@Override
	public boolean isVisible() {
		return this.mVisible;
	}

	@Override
	public void hide() {
		if ((this.mFlags & SystemUiHider.FLAG_FULLSCREEN) != 0) {
            this.mActivity.getWindow().setFlags(
					LayoutParams.FLAG_FULLSCREEN,
					LayoutParams.FLAG_FULLSCREEN);
		}
        this.mOnVisibilityChangeListener.onVisibilityChange(false);
        this.mVisible = false;
	}

	@Override
	public void show() {
		if ((this.mFlags & SystemUiHider.FLAG_FULLSCREEN) != 0) {
            this.mActivity.getWindow().setFlags(0,
					LayoutParams.FLAG_FULLSCREEN);
		}
        this.mOnVisibilityChangeListener.onVisibilityChange(true);
        this.mVisible = true;
	}
}
