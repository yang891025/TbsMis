package com.tbs.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.ui.base.MyImageView;
import com.tbs.chat.util.BitmapUtil;

import java.util.List;

public class GalleryAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private ViewTreeObserver viewTreeObserver;
	private Context mContext;
	ViewHolder holder = null;
	private List<MessageEntity> curList;
	private int screenWidth;// 锟斤拷幕锟斤拷锟�
	private int screenHeight;// 锟斤拷幕锟竭讹拷
	private int state_height;// 状态锟斤拷锟侥高讹拷
	private Activity activity;
	private Bitmap bitmap = null;
	private boolean flag = false;
	private LinearLayout topView;
	private LinearLayout bottomView;
	

	public GalleryAdapter(Context c) {
		mContext = c;
	}

	public GalleryAdapter(Context c, Activity activity, List<MessageEntity> list, LinearLayout topView, LinearLayout bottomView, int screenWidth, int screenHeight) {
		this.mContext = c;
		this.activity = activity;
		this.curList = list;
		this.topView = topView;
		this.bottomView = bottomView;
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return curList.size();
	}

	@Override
	public Object getItem(int position) {
		return curList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageEntity msg = curList.get(position);
		final String path = msg.getContent();
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.image_gallery_item, null);
			holder.image_gallery_download_success = (RelativeLayout) convertView.findViewById(R.id.image_gallery_download_success);
			holder.image = (MyImageView) convertView.findViewById(R.id.image);
			holder.image_gallery_downloading = (LinearLayout) convertView.findViewById(R.id.image_gallery_downloading);
			holder.downloading_thumb_iv = (ImageView) convertView.findViewById(R.id.downloading_thumb_iv);
			holder.downloading_pb = (ProgressBar) convertView.findViewById(R.id.downloading_pb);
			holder.downloading_hd_tip_tv = (TextView) convertView.findViewById(R.id.downloading_hd_tip_tv);
			holder.downloading_percent_tv = (TextView) convertView.findViewById(R.id.downloading_percent_tv);
			holder.image_gallery_download_fail = (LinearLayout) convertView.findViewById(R.id.image_gallery_download_fail);
			holder.download_fail_icon = (ImageView) convertView.findViewById(R.id.download_fail_icon);
			holder.download_fail_text = (TextView) convertView.findViewById(R.id.download_fail_text);
			holder.image.setTopView(topView);
			holder.image.setBottomView(bottomView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setmActivity(activity);//注锟斤拷Activity.
		/* 
		 * 锟斤拷锟斤拷状态锟斤拷锟竭讹拷 
		 */
		viewTreeObserver = holder.image.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				if (state_height == 0) {// 锟斤拷取状锟斤拷锟斤拷锟竭讹拷
					Rect frame = new Rect();
					activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
					state_height = frame.top;
					holder.image.setScreen_H(screenHeight - state_height);
					holder.image.setScreen_W(screenWidth);
				}
			}
		});
		holder.image_gallery_download_success.setVisibility(View.GONE);
		holder.image_gallery_download_fail.setVisibility(View.GONE);
		holder.downloading_thumb_iv.setVisibility(View.GONE);
		holder.downloading_hd_tip_tv.setVisibility(View.GONE);
		bitmap = BitmapUtil.ReadBitmapById(mContext, path, screenWidth, screenHeight);
        flag = bitmap != null;
		if (flag) {
			holder.image.setImageBitmap(bitmap);
			holder.image_gallery_download_success.setVisibility(View.VISIBLE);
			holder.image_gallery_download_fail.setVisibility(View.GONE);
			holder.image_gallery_downloading.setVisibility(View.GONE);
		} else {
			holder.image_gallery_download_success.setVisibility(View.GONE);
			holder.image_gallery_download_fail.setVisibility(View.VISIBLE);
			holder.image_gallery_downloading.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private Handler mHandler = new Handler();
	
}

   class ViewHolder {
	RelativeLayout image_gallery_download_success;
	MyImageView image;
	LinearLayout image_gallery_downloading;
	ImageView downloading_thumb_iv;
	ProgressBar downloading_pb;
	TextView downloading_hd_tip_tv;
	TextView downloading_percent_tv;
	LinearLayout image_gallery_download_fail;
	ImageView download_fail_icon;
	TextView download_fail_text;
}