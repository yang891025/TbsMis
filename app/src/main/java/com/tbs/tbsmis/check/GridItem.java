package com.tbs.tbsmis.check;
/**
 * @author Kiritor
 * ʵ���Լ���View�̳�Checable�ӿ�*/

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.headergridview.selectbuttonaction.StickyGridItem;

public class GridItem extends RelativeLayout implements Checkable {

    private Context mContext;
    private boolean mChecked;//�жϸ�ѡ���Ƿ�ѡ�ϵı�־��
    private ImageView mSecletView = null;

    public GridItem(Context context, AttributeSet attrs, StickyGridItem data) {
        this(context, attrs,0,data);
    }

    public GridItem(Context context, AttributeSet attrs, int defStyle, StickyGridItem data) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.select_menu_item, this);
        TextView menu_title = (TextView) findViewById(R.id.menu_title);
        TextView menu_url = (TextView) findViewById(R.id.menu_url);
        menu_title.setText(data.getName());
        String url = data.getPath();
        menu_url.setText(url.substring(url.lastIndexOf(":")+1));
        mSecletView = (ImageView) findViewById(R.id.select);
    }

    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub
        mChecked = checked;
        mSecletView.setVisibility(checked ? View.VISIBLE : View.GONE);//ѡ��������ʾС��ͼƬ
    }

    @Override
    public boolean isChecked() {
        // TODO Auto-generated method stub
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }


}
