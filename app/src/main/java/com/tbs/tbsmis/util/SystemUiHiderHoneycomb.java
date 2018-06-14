package com.tbs.tbsmis.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * An API 11+ implementation of {@link SystemUiHider}. Uses APIs available in
 * Honeycomb and later (specifically {@link View#setSystemUiVisibility(int)}) to
 * show and hide the system UI.
 */
@TargetApi(VERSION_CODES.HONEYCOMB)
public class SystemUiHiderHoneycomb extends SystemUiHiderBase {
	/**
	 * Flags for {@link View#setSystemUiVisibility(int)} to use when showing the
	 * system UI.
	 */
	private int mShowFlags;

	/**
	 * Flags for {@link View#setSystemUiVisibility(int)} to use when hiding the
	 * system UI.
	 */
	private int mHideFlags;

	/**
	 * Flags to test against the first parameter in
	 * {@link OnSystemUiVisibilityChangeListener#onSystemUiVisibilityChange(int)}
	 * to determine the system UI visibility state.
	 */
	private int mTestFlags;

	/**
	 * Whether or not the system UI is currently visible. This is cached from
	 * {@link OnSystemUiVisibilityChangeListener}.
	 */
	private boolean mVisible = true;

	/**
	 * Constructor not intended to be called by clients. Use
	 * {@link SystemUiHider#getInstance} to obtain an instance.
	 */
	protected SystemUiHiderHoneycomb(Activity activity, View anchorView,
			int flags) {
		super(activity, anchorView, flags);

        this.mShowFlags = View.SYSTEM_UI_FLAG_VISIBLE;
        this.mHideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        this.mTestFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE;

		if ((this.mFlags & SystemUiHider.FLAG_FULLSCREEN) != 0) {
			// If the client requested fullscreen, add flags relevant to hiding
			// the status bar. Note that some of these constants are new as of
			// API 16 (Jelly Bean). It is safe to use them, as they are inlined
			// at compile-time and do nothing on pre-Jelly Bean devices.
            this.mShowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            this.mHideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		if ((this.mFlags & SystemUiHider.FLAG_HIDE_NAVIGATION) != 0) {
			// If the client requested hiding navigation, add relevant flags.
            this.mShowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            this.mHideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            this.mTestFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setup() {
        this.mAnchorView
				.setOnSystemUiVisibilityChangeListener(this.mSystemUiVisibilityChangeListener);
	}

	/** {@inheritDoc} */
	@Override
	public void hide() {
        this.mAnchorView.setSystemUiVisibility(this.mHideFlags);
	}

	/** {@inheritDoc} */
	@Override
	public void show() {
        this.mAnchorView.setSystemUiVisibility(this.mShowFlags);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVisible() {
		return this.mVisible;
	}

	private final OnSystemUiVisibilityChangeListener mSystemUiVisibilityChangeListener = new OnSystemUiVisibilityChangeListener() {
		@Override
		public void onSystemUiVisibilityChange(int vis) {
			// Test against mTestFlags to see if the system UI is visible.
			if ((vis & SystemUiHiderHoneycomb.this.mTestFlags) != 0) {
				if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
					// Pre-Jelly Bean, we must manually hide the action bar
					// and use the old window flags API.
                    SystemUiHiderHoneycomb.this.mActivity.getActionBar().hide();
                    SystemUiHiderHoneycomb.this.mActivity.getWindow().setFlags(
							LayoutParams.FLAG_FULLSCREEN,
							LayoutParams.FLAG_FULLSCREEN);
				}

				// Trigger the registered listener and cache the visibility
				// state.
                SystemUiHiderHoneycomb.this.mOnVisibilityChangeListener.onVisibilityChange(false);
                SystemUiHiderHoneycomb.this.mVisible = false;

			} else {
                SystemUiHiderHoneycomb.this.mAnchorView.setSystemUiVisibility(SystemUiHiderHoneycomb.this.mShowFlags);
				if (VERSION.SDK_INT < VERSION_CODES.JELLY_BEAN) {
					// Pre-Jelly Bean, we must manually show the action bar
					// and use the old window flags API.
                    SystemUiHiderHoneycomb.this.mActivity.getActionBar().show();
                    SystemUiHiderHoneycomb.this.mActivity.getWindow().setFlags(0,
							LayoutParams.FLAG_FULLSCREEN);
				}

				// Trigger the registered listener and cache the visibility
				// state.
                SystemUiHiderHoneycomb.this.mOnVisibilityChangeListener.onVisibilityChange(true);
                SystemUiHiderHoneycomb.this.mVisible = true;
			}
		}
	};
}
