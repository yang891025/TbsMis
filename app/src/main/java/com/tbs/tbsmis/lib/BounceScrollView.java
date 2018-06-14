package com.tbs.tbsmis.lib;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;
/**
 * ScrollView反弹效果的实�?
 */
public class BounceScrollView extends ScrollView {
	private View inner;// 孩子View

	private float y;// 点击时y坐标

	private final Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否�?��动画.)

	private boolean isCount;// 是否�?��计算

	public BounceScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/***
	 * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之�? 即使子类覆盖�?onFinishInflate
	 * 方法，也应该调用父类的方法，使该方法得以执行.
	 */
	@Override
	protected void onFinishInflate() {
		if (this.getChildCount() > 0) {
            this.inner = this.getChildAt(0);
		}
	}

	/***
	 * 监听touch
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (this.inner != null) {
            this.commOnTouchEvent(ev);
		}

		return super.onTouchEvent(ev);
	}

	/***
	 * 触摸事件
	 * 
	 * @param ev
	 */
	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			// 手指松开.
			if (this.isNeedAnimation()) {
                this.animation();
                this.isCount = false;
			}
			break;
		/***
		 * 排除出第�?��移动计算，因为第�?��无法得知y坐标�?在MotionEvent.ACTION_DOWN中获取不到，
		 * 因为此时是MyScrollView的touch事件传�?到到了LIstView的孩子item上面.�?��从第二次计算�?��.
		 * 然�?我们也要进行初始化，就是第一次移动的时�?让滑动距离归0. 之后记录准确了就正常执行.
		 */
		case MotionEvent.ACTION_MOVE:
			float preY = this.y;// 按下时的y坐标
			float nowY = ev.getY();// 时时y坐标
			int deltaY = (int) (preY - nowY);// 滑动距离
			if (!this.isCount) {
				deltaY = 0; // 在这里要�?.
			}
            this.y = nowY;
			// 当滚动到�?��或�?�?��时就不会再滚动，这时移动布局
			if (this.isNeedMove()) {
				// 初始化头部矩�?
				if (this.normal.isEmpty()) {
					// 保存正常的布�?���?
                    this.normal.set(this.inner.getLeft(), this.inner.getTop(),
                            this.inner.getRight(), this.inner.getBottom());
				}
//				Log.e("jj", "矩形�? + inner.getLeft() + "," + inner.getTop()
//						+ "," + inner.getRight() + "," + inner.getBottom());
				// 移动布局
                this.inner.layout(this.inner.getLeft(), this.inner.getTop() - deltaY / 2,
                        this.inner.getRight(), this.inner.getBottom() - deltaY / 2);
			}
            this.isCount = true;
			break;

		default:
			break;
		}
	}

	/***
	 * 回缩动画
	 */
	public void animation() {
		// �?��移动动画
		TranslateAnimation ta = new TranslateAnimation(0, 0, this.inner.getTop(),
                this.normal.top);
		ta.setDuration(200);
        this.inner.startAnimation(ta);
		// 设置回到正常的布�?���?
        this.inner.layout(this.normal.left, this.normal.top, this.normal.right, this.normal.bottom);
//		Log.e("jj", "回归�? + normal.left + "," + normal.top + "," + normal.right
//				+ "," + normal.bottom);
        this.normal.setEmpty();
	}

	// 是否�?���?��动画
	public boolean isNeedAnimation() {
		return !this.normal.isEmpty();
	}

	/***
	 * 是否�?��移动布局 inner.getMeasuredHeight():获取的是控件的�?高度
	 * 
	 * getHeight()：获取的是屏幕的高度
	 * 
	 * @return
	 */
	public boolean isNeedMove() {
		int offset = this.inner.getMeasuredHeight() - this.getHeight();
		int scrollY = this.getScrollY();
//		Log.e("jj", "scrolly=" + scrollY);
		// 0是顶部，后面那个是底�?
        return scrollY == 0 || scrollY == offset;
    }

}
