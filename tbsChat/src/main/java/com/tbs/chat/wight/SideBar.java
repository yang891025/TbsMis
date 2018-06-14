package com.tbs.chat.wight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.chat.R;

public class SideBar extends View {

	public static int m_nItemHeight = 16;// 字母 之间的间距
	public static char[] l;// 字符串数据
	private SectionIndexer sectionIndexter = null;
	private ListView list;// 传进来的listview
	private Context context;
	private Activity activity;
	private View view;
	private TextView mDialogText;

	public SideBar(Context context) {
		super(context);
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		l = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J','K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V','W', 'X', 'Y', 'Z' };
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
		list = _list;
	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	// toast通知弹出，toast为自定义部分
	public void myToast(String content) {
		TextView message = (TextView) view.findViewById(R.id.textview);
		message.setText(content);
		Toast toastStart = new Toast(context);
		toastStart.setGravity(Gravity.CENTER, 0, 0);
		toastStart.setDuration(50);
		toastStart.setView(view);
		toastStart.show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

//		HeaderViewListAdapter ha = (HeaderViewListAdapter) list.getAdapter();
//		sectionIndexter = (SectionIndexer) ha.getWrappedAdapter();
		sectionIndexter = (SectionIndexer) list.getAdapter();

		int i = (int) event.getY();
		int idx = i / m_nItemHeight;
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			// myToast("" + l[idx]);//弹出toast
			this.setBackgroundResource(R.drawable.scrollbar_bg);//设置触摸的背景
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText("" + l[idx]);
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) list.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(l[idx]);
			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		} else {
			mDialogText.setVisibility(View.INVISIBLE);
			this.setBackgroundResource(0);//设置触摸的背景
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(0xff595c61);
		paint.setTextSize(16);//设置字体大小
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < l.length; i++) {
			canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);
		}
	}
}
