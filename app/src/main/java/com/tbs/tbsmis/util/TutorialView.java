package com.tbs.tbsmis.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.tbs.tbsmis.R;

public class TutorialView extends RelativeLayout {
	 
    private View baseView;
	private Context mContext;
	private int step;
 
    public TutorialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }
 
    public TutorialView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }
 
    public TutorialView(Context context) {
        this(context, null);
        this.mContext = context;
    }
 
    public TutorialView(Context context, int step) {
        this(context);
        this.step = step;
        this.mContext = context;
        this.init();
    }
 
    private void init() {
        this.baseView = LayoutInflater.from(this.mContext).inflate(
                R.layout.my_relative_view, this);
    }
}