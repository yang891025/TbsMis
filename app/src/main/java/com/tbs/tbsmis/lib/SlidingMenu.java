package com.tbs.tbsmis.lib;

import android.R.attr;
import android.R.id;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tbs.tbsmis.R;

import java.lang.reflect.Method;


public class SlidingMenu extends RelativeLayout {

	private static final String TAG = "SlidingMenu";

	public static final int SLIDING_WINDOW = 0;
	public static final int SLIDING_CONTENT = 1;
	private boolean mActionbarOverlay;

	public static final int TOUCHMODE_MARGIN = 0;

	public static final int TOUCHMODE_FULLSCREEN = 1;

	public static final int TOUCHMODE_NONE = 2;

	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int LEFT_RIGHT = 2;

	private CustomViewAbove mViewAbove;

	private final CustomViewBehind mViewBehind;

	private SlidingMenu.OnOpenListener mOpenListener;

	private SlidingMenu.OnCloseListener mCloseListener;

	public interface OnOpenListener {

		void onOpen();
	}

	public interface OnOpenedListener {
		void onOpened();
	}

	public interface OnCloseListener {

		void onClose();
	}

	public interface OnClosedListener {

		void onClosed();
	}

	public interface CanvasTransformer {

		void transformCanvas(Canvas canvas, float percentOpen);
	}

	public SlidingMenu(Context context) {
		this(context, null);
	}

	public SlidingMenu(Activity activity, int slideStyle) {
		this(activity, null);
        attachToActivity(activity, slideStyle);
	}

	public SlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		RelativeLayout.LayoutParams behindParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.mViewBehind = new CustomViewBehind(context);
        this.addView(this.mViewBehind, behindParams);
		RelativeLayout.LayoutParams aboveParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.mViewAbove = new CustomViewAbove(context);
        this.addView(this.mViewAbove, aboveParams);
		// register the CustomViewBehind with the CustomViewAbove
        this.mViewAbove.setCustomViewBehind(this.mViewBehind);
        this.mViewBehind.setCustomViewAbove(this.mViewAbove);
        this.mViewAbove.setOnPageChangeListener(new CustomViewAbove.OnPageChangeListener() {
			public static final int POSITION_OPEN = 0;
			public static final int POSITION_CLOSE = 1;

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) { }

			@Override
			public void onPageSelected(int position) {
				if (position == POSITION_OPEN && SlidingMenu.this.mOpenListener != null) {
                    SlidingMenu.this.mOpenListener.onOpen();
				} else if (position == POSITION_CLOSE && SlidingMenu.this.mCloseListener != null) {
                    SlidingMenu.this.mCloseListener.onClose();
				}
			}
		});

		// now style everything!
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu);
		// set the above and behind views if defined in xml
		int mode = ta.getInt(R.styleable.SlidingMenu_mode, SlidingMenu.LEFT);
        this.setMode(mode);
		int viewAbove = ta.getResourceId(R.styleable.SlidingMenu_viewAbove, -1);
		if (viewAbove != -1) {
            this.setContent(viewAbove);
		} else {
            this.setContent(new FrameLayout(context));
		}
		int viewBehind = ta.getResourceId(R.styleable.SlidingMenu_viewBehind, -1);
		if (viewBehind != -1) {
            this.setMenu(viewBehind);
		} else {
            this.setMenu(new FrameLayout(context));
		}
		int touchModeAbove = ta.getInt(R.styleable.SlidingMenu_touchModeAbove, SlidingMenu.TOUCHMODE_MARGIN);
        this.setTouchModeAbove(touchModeAbove);
		int touchModeBehind = ta.getInt(R.styleable.SlidingMenu_touchModeBehind, SlidingMenu.TOUCHMODE_MARGIN);
        this.setTouchModeBehind(touchModeBehind);

		int offsetBehind = (int) ta.getDimension(R.styleable.SlidingMenu_behindOffset, -1);
		int widthBehind = (int) ta.getDimension(R.styleable.SlidingMenu_behindWidth, -1);
		if (offsetBehind != -1 && widthBehind != -1)
			throw new IllegalStateException("Cannot set both behindOffset and behindWidth for a SlidingMenu");
		else if (offsetBehind != -1)
            this.setBehindOffset(offsetBehind);
		else if (widthBehind != -1)
            this.setBehindWidth(widthBehind);
		else
            this.setBehindOffset(0);
		float scrollOffsetBehind = ta.getFloat(R.styleable.SlidingMenu_behindScrollScale, 0.33f);
        this.setBehindScrollScale(scrollOffsetBehind);
		int shadowRes = ta.getResourceId(R.styleable.SlidingMenu_shadowDrawable, -1);
		if (shadowRes != -1) {
            this.setShadowDrawable(shadowRes);
		}
		int shadowWidth = (int) ta.getDimension(R.styleable.SlidingMenu_shadowWidth, 0);
        this.setShadowWidth(shadowWidth);
		boolean fadeEnabled = ta.getBoolean(R.styleable.SlidingMenu_fadeEnabled, true);
        this.setFadeEnabled(fadeEnabled);
		float fadeDeg = ta.getFloat(R.styleable.SlidingMenu_fadeDegree, 0.33f);
        this.setFadeDegree(fadeDeg);
		boolean selectorEnabled = ta.getBoolean(R.styleable.SlidingMenu_selectorEnabled, false);
        this.setSelectorEnabled(selectorEnabled);
		int selectorRes = ta.getResourceId(R.styleable.SlidingMenu_selectorDrawable, -1);
		if (selectorRes != -1)
            this.setSelectorDrawable(selectorRes);
		ta.recycle();
	}

	public CustomViewAbove getmViewAbove() {
		return this.mViewAbove;
	}

	public void setmViewAbove(CustomViewAbove mViewAbove) {
		this.mViewAbove = mViewAbove;
	}

	public void attachToActivity(Activity activity, int slideStyle) {
        this.attachToActivity(activity, slideStyle, false);
	}

	public void attachToActivity(Activity activity, int slideStyle, boolean actionbarOverlay) {
		if (slideStyle != SlidingMenu.SLIDING_WINDOW && slideStyle != SlidingMenu.SLIDING_CONTENT)
			throw new IllegalArgumentException("slideStyle must be either SLIDING_WINDOW or SLIDING_CONTENT");

		if (this.getParent() != null)
			throw new IllegalStateException("This SlidingMenu appears to already be attached");

		// get the window background
		TypedArray a = activity.getTheme().obtainStyledAttributes(new int[] {attr.windowBackground});
		int background = a.getResourceId(0, 0);
		a.recycle();

		switch (slideStyle) {
		case SlidingMenu.SLIDING_WINDOW:
            this.mActionbarOverlay = false;
			ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
			ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
			// save ActionBar themes that have transparent assets
			decorChild.setBackgroundResource(background);
			decor.removeView(decorChild);
			decor.addView(this);
            this.setContent(decorChild);
			break;
		case SlidingMenu.SLIDING_CONTENT:
            this.mActionbarOverlay = actionbarOverlay;
			// take the above view out of
			ViewGroup contentParent = (ViewGroup)activity.findViewById(id.content);
			View content = contentParent.getChildAt(0);
			contentParent.removeView(content);
			contentParent.addView(this, 0, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            this.setContent(content);
			// save people from having transparent backgrounds
			if (content.getBackground() == null)
				content.setBackgroundResource(background);
			break;
		}
	}

	public void setContent(int res) {
        this.setContent(LayoutInflater.from(this.getContext()).inflate(res, null));
	}

	public void setContent(View view) {
        this.mViewAbove.setContent(view);
        this.showContent();
	}

	public View getContent() {
		return this.mViewAbove.getContent();
	}

	public void setMenu(int res) {
        this.setMenu(LayoutInflater.from(this.getContext()).inflate(res, null));
	}

	public void setMenu(View v) {
        this.mViewBehind.setContent(v);
	}

	public View getMenu() {
		return this.mViewBehind.getContent();
	}

	public void setSecondaryMenu(int res) {
        this.setSecondaryMenu(LayoutInflater.from(this.getContext()).inflate(res, null));
	}

	public void setSecondaryMenu(View v) {
        this.mViewBehind.setSecondaryContent(v);
		//		mViewBehind.invalidate();
	}

	public View getSecondaryMenu() {
		return this.mViewBehind.getSecondaryContent();
	}

	public void setSlidingEnabled(boolean b) {
        this.mViewAbove.setSlidingEnabled(b);
	}

	public boolean isSlidingEnabled() {
		return this.mViewAbove.isSlidingEnabled();
	}

	public void setMode(int mode) {
		if (mode != SlidingMenu.LEFT && mode != SlidingMenu.RIGHT && mode != SlidingMenu.LEFT_RIGHT) {
			throw new IllegalStateException("SlidingMenu mode must be LEFT, RIGHT, or LEFT_RIGHT");
		}
        this.mViewBehind.setMode(mode);
	}

	public int getMode() {
		return this.mViewBehind.getMode();
	}

	public void setStatic(boolean b) {
		if (b) {
            this.setSlidingEnabled(false);
            this.mViewAbove.setCustomViewBehind(null);
            this.mViewAbove.setCurrentItem(1);
			//			mViewBehind.setCurrentItem(0);
		} else {
            this.mViewAbove.setCurrentItem(1);
			//			mViewBehind.setCurrentItem(1);
            this.mViewAbove.setCustomViewBehind(this.mViewBehind);
            this.setSlidingEnabled(true);
		}
	}

	public void showMenu() {
        this.showMenu(true);
	}

	public void showMenu(boolean animate) {
        this.mViewAbove.setCurrentItem(0, animate);
	}

	public void showSecondaryMenu() {
        this.showSecondaryMenu(true);
	}

	public void showSecondaryMenu(boolean animate) {
        this.mViewAbove.setCurrentItem(2, animate);
	}
//	public void showSecondary(boolean animate) {
//		mViewAbove.setCurrentItem(1, animate);
//	}
	public void showContent() {
        this.showContent(true);
	}

	public void showContent(boolean animate) {
        this.mViewAbove.setCurrentItem(1, animate);
	}
	public void toggle() {
        this.toggle(true);
	}

	public void toggle(boolean animate) {
		if (this.isMenuShowing()) {
            this.showContent(animate);
		} else {
            this.showMenu(animate);
		}
	}

	public boolean isMenuShowing() {
		return this.mViewAbove.getCurrentItem() == 0 || this.mViewAbove.getCurrentItem() == 2;
	}

	public boolean isSecondaryMenuShowing() {
		return this.mViewAbove.getCurrentItem() == 2;
	}

	public int getBehindOffset() {
		return ((RelativeLayout.LayoutParams) this.mViewBehind.getLayoutParams()).rightMargin;
	}

	public void setBehindOffset(int i) {
        this.mViewBehind.setWidthOffset(i);
	}
	//11.3添加
	// 设置右侧边栏菜单展开时距离左边界的偏移量
    public void setRightMenuOffset(int offset) {
        this.mViewBehind.setRightWidthOffset(offset);
    }

    // 设置右侧边栏菜单展开时距离左边界的偏移量
    public void setRightMenuOffsetRes(int resId) {
            int i = (int) this.getContext().getResources().getDimension(resId);
        this.setRightMenuOffset(i);
    }

    // 设置右侧边栏的宽度
    @SuppressWarnings("deprecation")
    public void setRightBehindWidth(int i) {
            int width;
            Display display = ((WindowManager) this.getContext().getSystemService(
                            Context.WINDOW_SERVICE)).getDefaultDisplay();
            try {
                    Class<?> cls = Display.class;
                    Class<?>[] parameterTypes = { Point.class };
                    Point parameter = new Point();
                    Method method = cls.getMethod("getSize", parameterTypes);
                    method.invoke(display, parameter);
                    width = parameter.x;
            } catch (Exception e) {
                    width = display.getWidth();
            }
        this.setRightMenuOffset(width - i);
    }

    // 设置右侧边栏的宽度
    public void setRightBehindWidthRes(int res) {
            int i = (int) this.getContext().getResources().getDimension(res);
        this.setRightBehindWidth(i);
    }
//11.3添加

	public void setBehindOffsetRes(int resID) {
		int i = (int) this.getContext().getResources().getDimension(resID);
        this.setBehindOffset(i);
	}

	public void setAboveOffset(int i) {
        this.mViewAbove.setAboveOffset(i);
	}

	public void setAboveOffsetRes(int resID) {
		int i = (int) this.getContext().getResources().getDimension(resID);
        this.setAboveOffset(i);
	}

	@SuppressWarnings("deprecation")
	public void setBehindWidth(int i) {
		int width;
		Display display = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		try {
			Class<?> cls = Display.class;
			Class<?>[] parameterTypes = {Point.class};
			Point parameter = new Point();
			Method method = cls.getMethod("getSize", parameterTypes);
			method.invoke(display, parameter);
			width = parameter.x;
		} catch (Exception e) {
			width = display.getWidth();
		}
        this.setBehindOffset(width-i);
	}

	public void setBehindWidthRes(int res) {
		int i = (int) this.getContext().getResources().getDimension(res);
        this.setBehindWidth(i);
	}

	public float getBehindScrollScale() {
		return this.mViewBehind.getScrollScale();
	}

	public void setBehindScrollScale(float f) {
		if (f < 0 && f > 1)
			throw new IllegalStateException("ScrollScale must be between 0 and 1");
        this.mViewBehind.setScrollScale(f);
	}

	public void setBehindCanvasTransformer(SlidingMenu.CanvasTransformer t) {
        this.mViewBehind.setCanvasTransformer(t);
	}

	public int getTouchModeAbove() {
		return this.mViewAbove.getTouchMode();
	}

	public void setTouchModeAbove(int i) {
		if (i != SlidingMenu.TOUCHMODE_FULLSCREEN && i != SlidingMenu.TOUCHMODE_MARGIN
				&& i != SlidingMenu.TOUCHMODE_NONE) {
			throw new IllegalStateException("TouchMode must be set to either" +
					"TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
		}
        this.mViewAbove.setTouchMode(i);
	}

	public void setTouchModeBehind(int i) {
		if (i != SlidingMenu.TOUCHMODE_FULLSCREEN && i != SlidingMenu.TOUCHMODE_MARGIN
				&& i != SlidingMenu.TOUCHMODE_NONE) {
			throw new IllegalStateException("TouchMode must be set to either" +
					"TOUCHMODE_FULLSCREEN or TOUCHMODE_MARGIN or TOUCHMODE_NONE.");
		}
        this.mViewBehind.setTouchMode(i);
	}

	public void setShadowDrawable(int resId) {
        this.setShadowDrawable(this.getContext().getResources().getDrawable(resId));
	}

	public void setShadowDrawable(Drawable d) {
        this.mViewBehind.setShadowDrawable(d);
	}

	public void setSecondaryShadowDrawable(int resId) {
        this.setSecondaryShadowDrawable(this.getContext().getResources().getDrawable(resId));
	}

	public void setSecondaryShadowDrawable(Drawable d) {
        this.mViewBehind.setSecondaryShadowDrawable(d);
	}

	public void setShadowWidthRes(int resId) {
        this.setShadowWidth((int) this.getResources().getDimension(resId));
	}

	public void setShadowWidth(int pixels) {
        this.mViewBehind.setShadowWidth(pixels);
	}

	public void setFadeEnabled(boolean b) {
        this.mViewBehind.setFadeEnabled(b);
	}

	public void setFadeDegree(float f) {
        this.mViewBehind.setFadeDegree(f);
	}

	public void setSelectorEnabled(boolean b) {
        this.mViewBehind.setSelectorEnabled(true);
	}

	public void setSelectedView(View v) {
        this.mViewBehind.setSelectedView(v);
	}

	public void setSelectorDrawable(int res) {
        this.mViewBehind.setSelectorBitmap(BitmapFactory.decodeResource(this.getResources(), res));
	}

	public void setSelectorBitmap(Bitmap b) {
        this.mViewBehind.setSelectorBitmap(b);
	}

	public void addIgnoredView(View v) {
        this.mViewAbove.addIgnoredView(v);
	}

	public void removeIgnoredView(View v) {
        this.mViewAbove.removeIgnoredView(v);
	}

	public void clearIgnoredViews() {
        this.mViewAbove.clearIgnoredViews();
	}

	public void setOnOpenListener(SlidingMenu.OnOpenListener listener) {
		//mViewAbove.setOnOpenListener(listener);
        this.mOpenListener = listener;
	}

	public void setOnCloseListener(SlidingMenu.OnCloseListener listener) {
		//mViewAbove.setOnCloseListener(listener);
        this.mCloseListener = listener;
	}

	public void setOnOpenedListener(SlidingMenu.OnOpenedListener listener) {
        this.mViewAbove.setOnOpenedListener(listener);
	}

	public void setOnClosedListener(SlidingMenu.OnClosedListener listener) {
        this.mViewAbove.setOnClosedListener(listener);
	}

	public static class SavedState extends View.BaseSavedState {

		private final int mItem;

		public SavedState(Parcelable superState, int item) {
			super(superState);
            this.mItem = item;
		}

		private SavedState(Parcel in) {
			super(in);
            this.mItem = in.readInt();
		}

		public int getItem() {
			return this.mItem;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(this.mItem);
		}

		public static final Creator<SlidingMenu.SavedState> CREATOR =
				new Creator<SlidingMenu.SavedState>() {
			@Override
			public SlidingMenu.SavedState createFromParcel(Parcel in) {
				return new SlidingMenu.SavedState(in);
			}

			@Override
			public SlidingMenu.SavedState[] newArray(int size) {
				return new SlidingMenu.SavedState[size];
			}
		};

	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SlidingMenu.SavedState ss = new SlidingMenu.SavedState(superState, this.mViewAbove.getCurrentItem());
		return ss;
	}
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SlidingMenu.SavedState ss = (SlidingMenu.SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());
        this.mViewAbove.setCurrentItem(ss.getItem());
	}
	@SuppressLint("NewApi")
	@Override
	protected boolean fitSystemWindows(Rect insets) {
		int leftPadding = insets.left;
		int rightPadding = insets.right;
		int topPadding = insets.top;
		int bottomPadding = insets.bottom;
		if (!this.mActionbarOverlay) {
			Log.v(SlidingMenu.TAG, "setting padding!");
            this.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
		}
		return true;
	}
//	
	private final Handler mHandler = new Handler();
//
	@TargetApi(VERSION_CODES.HONEYCOMB)
	public void manageLayers(float percentOpen) {
		if (VERSION.SDK_INT < 11) return;

		boolean layer = percentOpen > 0.0f && percentOpen < 1.0f;
		final int layerType = layer ? View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE;

		if (layerType != this.getContent().getLayerType()) {
            this.mHandler.post(new Runnable() {
				@Override
				public void run() {
					Log.v(SlidingMenu.TAG, "changing layerType. hardware? " + (layerType == View.LAYER_TYPE_HARDWARE));
                    SlidingMenu.this.getContent().setLayerType(layerType, null);
                    SlidingMenu.this.getMenu().setLayerType(layerType, null);
					if (SlidingMenu.this.getSecondaryMenu() != null) {
                        SlidingMenu.this.getSecondaryMenu().setLayerType(layerType, null);
					}
				}
			});
		}
	}
}