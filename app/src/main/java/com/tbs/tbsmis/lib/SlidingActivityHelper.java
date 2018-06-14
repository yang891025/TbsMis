package com.tbs.tbsmis.lib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbs.tbsmis.R;


public class SlidingActivityHelper {

	private final Activity mActivity;

	private SlidingMenu mSlidingMenu;

	private View mViewAbove;

	private View mViewBehind;

	private boolean mBroadcasting;

	private boolean mOnPostCreateCalled;

	private boolean mEnableSlide = true;

	/**
	 * Instantiates a new SlidingActivityHelper.
	 *
	 * @param activity the associated activity
	 */
	public SlidingActivityHelper(Activity activity) {
        this.mActivity = activity;
	}

	/**
	 * Sets mSlidingMenu as a newly inflated SlidingMenu. Should be called within the activitiy's onCreate()
	 *
	 * @param savedInstanceState the saved instance state (unused)
	 */
	public void onCreate(Bundle savedInstanceState) {
        this.mSlidingMenu = (SlidingMenu) LayoutInflater.from(this.mActivity).inflate(R.layout.main, null);
	}

	/**
	 * Further SlidingMenu initialization. Should be called within the activitiy's onPostCreate()
	 *
	 * @param savedInstanceState the saved instance state (unused)
	 */
	public void onPostCreate(Bundle savedInstanceState) {
		if (this.mViewBehind == null || this.mViewAbove == null) {
			throw new IllegalStateException("Both setBehindContentView must be called " +
					"in onCreate in addition to setContentView.");
		}

        this.mOnPostCreateCalled = true;

        this.mSlidingMenu.attachToActivity(this.mActivity,
                this.mEnableSlide ? SlidingMenu.SLIDING_WINDOW : SlidingMenu.SLIDING_CONTENT);
		
		final boolean open;
		final boolean secondary;
		if (savedInstanceState != null) {
			open = savedInstanceState.getBoolean("SlidingActivityHelper.open");
			secondary = savedInstanceState.getBoolean("SlidingActivityHelper.secondary");
		} else {
			open = false;
			secondary = false;
		}
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				if (open) {
					if (secondary) {
                        SlidingActivityHelper.this.mSlidingMenu.showSecondaryMenu(false);
					} else {
                        SlidingActivityHelper.this.mSlidingMenu.showMenu(false);
					}
				} else {
                    SlidingActivityHelper.this.mSlidingMenu.showContent(false);
				}
			}
		});
	}

	/**
	 * Controls whether the ActionBar slides along with the above view when the menu is opened,
	 * or if it stays in place.
	 *
	 * @param slidingActionBarEnabled True if you want the ActionBar to slide along with the SlidingMenu,
	 * false if you want the ActionBar to stay in place
	 */
	public void setSlidingActionBarEnabled(boolean slidingActionBarEnabled) {
		if (this.mOnPostCreateCalled)
			throw new IllegalStateException("enableSlidingActionBar must be called in onCreate.");
        this.mEnableSlide = slidingActionBarEnabled;
	}

	/**
	 * Finds a view that was identified by the id attribute from the XML that was processed in onCreate(Bundle).
	 * 
	 * @param id the resource id of the desired view
	 * @return The view if found or null otherwise.
	 */
	public View findViewById(int id) {
		View v;
		if (this.mSlidingMenu != null) {
			v = this.mSlidingMenu.findViewById(id);
			if (v != null)
				return v;
		}
		return null;
	}

	/**
	 * Called to retrieve per-instance state from an activity before being killed so that the state can be
	 * restored in onCreate(Bundle) or onRestoreInstanceState(Bundle) (the Bundle populated by this method
	 * will be passed to both). 
	 *
	 * @param outState Bundle in which to place your saved state.
	 */
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("SlidingActivityHelper.open", this.mSlidingMenu.isMenuShowing());
		outState.putBoolean("SlidingActivityHelper.secondary", this.mSlidingMenu.isSecondaryMenuShowing());
	}

	/**
	 * Register the above content view.
	 *
	 * @param v the above content view to register
	 * @param params LayoutParams for that view (unused)
	 */
	public void registerAboveContentView(View v, ViewGroup.LayoutParams params) {
		if (!this.mBroadcasting)
            this.mViewAbove = v;
	}

	/**
	 * Set the activity content to an explicit view. This view is placed directly into the activity's view
	 * hierarchy. It can itself be a complex view hierarchy. When calling this method, the layout parameters
	 * of the specified view are ignored. Both the width and the height of the view are set by default to
	 * MATCH_PARENT. To use your own layout parameters, invoke setContentView(android.view.View,
	 * android.view.ViewGroup.LayoutParams) instead.
	 *
	 * @param v The desired content to display.
	 */
	public void setContentView(View v) {
        this.mBroadcasting = true;
        this.mActivity.setContentView(v);
	}

	/**
	 * Set the behind view content to an explicit view. This view is placed directly into the behind view 's view hierarchy.
	 * It can itself be a complex view hierarchy.
	 *
	 * @param view The desired content to display.
	 * @param layoutParams Layout parameters for the view. (unused)
	 */
	public void setBehindContentView(View view, ViewGroup.LayoutParams layoutParams) {
        this.mViewBehind = view;
        this.mSlidingMenu.setMenu(this.mViewBehind);
	}

	/**
	 * Gets the SlidingMenu associated with this activity.
	 *
	 * @return the SlidingMenu associated with this activity.
	 */
	public SlidingMenu getSlidingMenu() {
		return this.mSlidingMenu;
	}

	/**
	 * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
	 */
	public void toggle() {
        this.mSlidingMenu.toggle();
	}

	/**
	 * Close the SlidingMenu and show the content view.
	 */
	public void showContent() {
        this.mSlidingMenu.showContent();
	}

	/**
	 * Open the SlidingMenu and show the menu view.
	 */
	public void showMenu() {
        this.mSlidingMenu.showMenu();
	}

	/**
	 * Open the SlidingMenu and show the secondary menu view. Will default to the regular menu
	 * if there is only one.
	 */
	public void showSecondaryMenu() {
        this.mSlidingMenu.showSecondaryMenu();
	}

	/**
	 * On key up.
	 *
	 * @param keyCode the key code
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && this.mSlidingMenu.isMenuShowing()) {
            this.showContent();
			return true;
		}
		return false;
	}

}
