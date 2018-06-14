package com.tbs.tbsmis.lib;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;


public class BaseSlidingFragmentActivity extends FragmentActivity implements SlidingActivityBase {

	private SlidingActivityHelper mHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.mHelper = new SlidingActivityHelper(this);
        this.mHelper.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
        this.mHelper.onPostCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#findViewById(int)
	 */
	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return this.mHelper.findViewById(id);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        this.mHelper.onSaveInstanceState(outState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(int id) {
        this.setContentView(this.getLayoutInflater().inflate(id, null));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setContentView(android.view.View)
	 */
	@Override
	public void setContentView(View v) {
        this.setContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setContentView(View v, ViewGroup.LayoutParams params) {
		super.setContentView(v, params);
        this.mHelper.registerAboveContentView(v, params);
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(int)
	 */
	@Override
	public void setBehindContentView(int id) {
        this.setBehindContentView(this.getLayoutInflater().inflate(id, null));
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android.view.View)
	 */
	@Override
	public void setBehindContentView(View v) {
        this.setBehindContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setBehindContentView(android.view.View, android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setBehindContentView(View v, ViewGroup.LayoutParams params) {
        this.mHelper.setBehindContentView(v, params);
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#getSlidingMenu()
	 */
	@Override
	public SlidingMenu getSlidingMenu() {
		return this.mHelper.getSlidingMenu();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#toggle()
	 */
	@Override
	public void toggle() {
        this.mHelper.toggle();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showAbove()
	 */
	@Override
	public void showContent() {
        this.mHelper.showContent();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showBehind()
	 */
	@Override
	public void showMenu() {
        this.mHelper.showMenu();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#showSecondaryMenu()
	 */
	@Override
	public void showSecondaryMenu() {
        this.mHelper.showSecondaryMenu();
	}

	/* (non-Javadoc)
	 * @see com.slidingmenu.lib.app.SlidingActivityBase#setSlidingActionBarEnabled(boolean)
	 */
	@Override
	public void setSlidingActionBarEnabled(boolean b) {
        this.mHelper.setSlidingActionBarEnabled(b);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean b = this.mHelper.onKeyUp(keyCode, event);
		if (b) return b;
		return super.onKeyUp(keyCode, event);
	}

}