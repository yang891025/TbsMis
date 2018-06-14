package com.tbs.chat.ui.tools;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.R.anim;
import com.tbs.chat.adapter.GalleryAdapter;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.ui.base.MyGallery;

public class ImageGalleryUI extends Activity implements OnClickListener{
	
	private static final String TAG = "ImageGalleryUI";
	/*
	 * titlebar
	 */
	private LinearLayout nav_title;
	private ImageButton rightBtn;
	private Button leftBtn;
	private TextView title;
	private TextView subTitle;
	private ProgressBar mProgress;
	/*
	 * gallery
	 */
	private MyGallery gallery;
	private LinearLayout cropimage_function_bar;
	private Button cropimage_function_btn;
	
	private Intent intent;
	private MessageEntity message;
	private int window_width, window_height;// 鎺т欢瀹藉害

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_gallery);
		titleView();
		gallery();
		init();
	}
	
	private void titleView() {
		if (getIntent().getExtras() != null) {
			intent = getIntent();
			message = (MessageEntity) intent.getSerializableExtra("message");
		}
		
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		
		int white = getResources().getColor(R.color.white);
		nav_title = (LinearLayout) findViewById(R.id.nav_title);
		rightBtn = (ImageButton) findViewById(R.id.title_btn1);
		leftBtn = (Button) findViewById(R.id.title_btn4);
		title = (TextView) findViewById(R.id.title);
		subTitle = (TextView) findViewById(R.id.sub_title);
		mProgress = (ProgressBar) findViewById(R.id.title_progress);
		title.setText("1/1");
		rightBtn.setImageResource(R.drawable.mm_title_btn_menu);
		rightBtn.setVisibility(View.VISIBLE);
		leftBtn.setTextColor(white);
		leftBtn.setVisibility(View.VISIBLE);
		leftBtn.setText("取消");
		leftBtn.setOnClickListener(this);
		rightBtn.setOnClickListener(this);
	}
	
	private void gallery() {
		gallery = (MyGallery) findViewById(R.id.gallery);
		cropimage_function_bar = (LinearLayout) findViewById(R.id.cropimage_function_bar);
		cropimage_function_btn = (Button) findViewById(R.id.cropimage_function_btn);
		
		gallery.setVerticalFadingEdgeEnabled(false);// 鍙栨秷绔栫洿娓愬彉杈规
		gallery.setHorizontalFadingEdgeEnabled(false);// 鍙栨秷姘村钩娓愬彉杈规
	}
	
	private void init() {
		List<MessageEntity> list = new ArrayList<MessageEntity>();
		list.add(message);
		GalleryAdapter adapter = new GalleryAdapter(this, this, list, nav_title, null, window_width, window_height);
		gallery.setAdapter(adapter);
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.title_btn4) {
			finish();
			overridePendingTransition(anim.fast_faded_in, anim.fast_faded_out);
		} else if (v.getId() == R.id.title_btn1) {
		} else {
		}
	}
}
