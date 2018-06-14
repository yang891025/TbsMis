package com.tbs.tbsmis.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;


public class SideBar extends View {

	public static int m_nItemHeight = 2;// 字母 之间的间距
	public static char[] l;// 字符串数据
	private SectionIndexer sectionIndexter;
	private ListView list;// 传进来的listview
	private Context context;
	private Activity activity;
	private View view;

	public SideBar(Context context) {
		super(context);
        this.init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.init();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        this.init();
	}

	private void init() {
        SideBar.l = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z' };
	}

	public void setToastView(View view) {
		this.view = view;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void setListView(ListView _list) {
        this.list = _list;
	}

	// toast通知弹出，toast为自定义部分
	public void myToast(String content) {
		TextView message = (TextView) this.view.findViewById(R.id.textview);
		message.setText(content);
		Toast toastStart = new Toast(this.context);
		toastStart.setGravity(Gravity.CENTER, 0, 0);
		toastStart.setDuration(50);
		toastStart.setView(this.view);
		toastStart.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		// HeaderViewListAdapter ha = (HeaderViewListAdapter) list.getAdapter();
		// sectionIndexter = (SectionIndexer) ha.getWrappedAdapter();
        this.sectionIndexter = (SectionIndexer) this.list.getAdapter();

		int i = (int) event.getY();
		int idx = i / SideBar.m_nItemHeight;
		if (idx >= SideBar.l.length) {
			idx = SideBar.l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			// myToast("" + l[idx]);//弹出toast
          setBackgroundResource(R.drawable.scrollbar_bg);// 设置触摸的背景
			// mDialogText.setVisibility(View.VISIBLE);
			// mDialogText.setText("" + l[idx]);
			if (this.sectionIndexter == null) {
                this.sectionIndexter = (SectionIndexer) this.list.getAdapter();
			}
			int position = this.sectionIndexter.getPositionForSection(SideBar.l[idx]);
			if (position == -1) {
				return true;
			}
            this.list.setSelection(position);
		} else {
			// mDialogText.setVisibility(View.INVISIBLE);
            setBackgroundResource(0);// 设置触摸的背景
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.rgb(33, 65, 98));
		paint.setTextSize(24);// 设置字体大小
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		// float widthCenter = getMeasuredWidth() / 2;
		int height = this.getHeight() - 30;// 获取对应高度
		int width  = this.getWidth();// 获取对应宽度
        SideBar.m_nItemHeight = height / SideBar.l.length;//
		for (int i = 0; i < SideBar.l.length; i++) {
			float  widthCenter = width / 2 - paint.measureText(String.valueOf(SideBar.l[i])) / 2;
			canvas.drawText(String.valueOf(SideBar.l[i]), widthCenter, SideBar.m_nItemHeight
					+ i * SideBar.m_nItemHeight, paint);
		}
		paint.reset();// 重置画笔
	}
}
