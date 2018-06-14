package com.tbs.tbsmis.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;

import java.util.ArrayList;

/**
 * ���������˵���������
 * 
 * @Description: ���������˵���������
 * 
 * @FileName: SlideMenuLayout.java
 * 
 * @Package com.slide.menu
 * 
 * 
 * 
 * @Date 2012-4-20 ����11:17:31
 * 
 * @Version V1.0
 */
public class SlideMenuLayout
{

	public int getFlag() {
		return this.flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	// ��˵���ArrayList
	private ArrayList<TextView> menuList;
	private String menuTag;
	private int flag;
	private Activity activity;
	private TextView textView;

	// private SlideMenuUtil menuUtil = null;
	public SlideMenuLayout(Activity activity) {
		this.activity = activity;
        this.menuList = new ArrayList<TextView>();
		// menuUtil = new SlideMenuUtil();
	}

	public SlideMenuLayout() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * ���������˵�����
	 * 
	 * @param n
	 * @param layoutWidth
	 */

	public View getSlideMenuLinerLayout(int layoutWidth, int n) {
		// ��TextView��LinearLayout
		LinearLayout menuLinerLayout = new LinearLayout(this.activity);
		menuLinerLayout.setOrientation(LinearLayout.HORIZONTAL);
		// ��������
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT, 1);
		menuLinerLayoutParames.gravity = Gravity.CENTER_HORIZONTAL;
		// ���TextView�ؼ�
		if (constants.menus.length >= 4) {
			int count = constants.menus.length - n * 4;
			if (count >= 4) {
				for (int i = n * 4; i < n * 4 + 4; i++) {
					TextView tvMenu = new TextView(this.activity);
					// ���ñ�ʶֵ
					tvMenu.setTag(constants.menus[i]);
					tvMenu.setLayoutParams(new ViewGroup.LayoutParams(layoutWidth / 4, 40));
					// tvMenu.setPadding(30, 12, 30, 0);
					tvMenu.setText(constants.menus[i]);
					tvMenu.setTextColor(Color.BLACK);
					tvMenu.setGravity(Gravity.CENTER_VERTICAL
							| Gravity.CENTER_HORIZONTAL);
					tvMenu.setOnClickListener(this.SlideMenuOnClickListener);

					if (i == 0) {
						tvMenu.setBackgroundResource(R.drawable.menu_down_bg);
					}
					menuLinerLayout.addView(tvMenu, menuLinerLayoutParames);
                    this.menuList.add(tvMenu);
				}
			} else {
				for (int i = n * 4; i < constants.menus.length; i++) {
					TextView tvMenu = new TextView(this.activity);
					// ���ñ�ʶֵ
					tvMenu.setTag(constants.menus[i]);
					tvMenu.setLayoutParams(new ViewGroup.LayoutParams(
							layoutWidth / count, 40));
					// tvMenu.setPadding(30, 12, 30, 0);
					tvMenu.setText(constants.menus[i]);
					tvMenu.setTextColor(Color.BLACK);
					tvMenu.setGravity(Gravity.CENTER_VERTICAL
							| Gravity.CENTER_HORIZONTAL);
					tvMenu.setOnClickListener(this.SlideMenuOnClickListener);
					if (i == 0) {
						tvMenu.setBackgroundResource(R.drawable.menu_down_bg);
						// flag = 0;
					}
					menuLinerLayout.addView(tvMenu, menuLinerLayoutParames);
                    this.menuList.add(tvMenu);
				}
			}
		} else {
			for (int i = 0; i < constants.menus.length; i++) {
				TextView tvMenu = new TextView(this.activity);
				// ���ñ�ʶֵ
				tvMenu.setTag(constants.menus[i]);
				tvMenu.setLayoutParams(new ViewGroup.LayoutParams(layoutWidth
						/ constants.menus.length, 40));
				// tvMenu.setPadding(30, 12, 30, 0);
				tvMenu.setText(constants.menus[i]);
				tvMenu.setTextColor(Color.BLACK);
				tvMenu.setGravity(Gravity.CENTER_VERTICAL
						| Gravity.CENTER_HORIZONTAL);
				tvMenu.setOnClickListener(this.SlideMenuOnClickListener);
				if (i == 0) {
					tvMenu.setBackgroundResource(R.drawable.menu_down_bg);
				}
				menuLinerLayout.addView(tvMenu, menuLinerLayoutParames);
                this.menuList.add(tvMenu);
			}
		}
		return menuLinerLayout;
	}

	// �����˵��¼�
	View.OnClickListener SlideMenuOnClickListener = new View.OnClickListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            SlideMenuLayout.this.menuTag = v.getTag().toString();
			// flag = 0;
			if (v.isClickable()) {
                SlideMenuLayout.this.textView = (TextView) v;
                SlideMenuLayout.this.textView.setBackgroundResource(R.drawable.menu_down_bg);
				for (int i = 0; i < SlideMenuLayout.this.menuList.size(); i++) {
					if (!SlideMenuLayout.this.menuTag.equals(SlideMenuLayout.this.menuList.get(i).getText())) {
                        SlideMenuLayout.this.menuList.get(i).setBackgroundDrawable(null);
					} else if (SlideMenuLayout.this.menuTag.equals(SlideMenuLayout.this.menuList.get(i).getText())) {
						// menuTag = (String) menuList.get(i).getText();
						// setFlag(i);
						Intent intent = new Intent();
						intent.setAction("Action_main"
								+ SlideMenuLayout.this.activity.getApplicationContext().getString(
                                R.string.about_title));
						intent.putExtra("flag", 4);
						intent.putExtra("menuNum", i);
                        SlideMenuLayout.this.activity.getApplicationContext().sendBroadcast(intent);
                        SlideMenuLayout.this.setFlag(i);
					}
				}
				// setFlag(slideMenuOnChange(menuTag));
			}
		}
	};

	@SuppressWarnings("deprecation")
	public int moveright() {
		int num = this.getFlag();
        this.menuTag = (String) this.menuList.get(num).getText();
		// num = slideMenuOnChange(menuTag);
		if (num != 0) {
            this.menuList.get(num).setBackgroundDrawable(null);
			num--;
            this.menuList.get(num).setBackgroundResource(R.drawable.menu_down_bg);
            this.setFlag(num);
		}
		return num;
	}

	@SuppressWarnings("deprecation")
	public int moveleft() {
		int num = this.getFlag();
        this.menuTag = (String) this.menuList.get(num).getText();
		// num = slideMenuOnChange(menuTag);
		if (num != this.menuList.size() - 1) {
            this.menuList.get(num).setBackgroundDrawable(null);
			num++;
            this.menuList.get(num).setBackgroundResource(R.drawable.menu_down_bg);
            this.menuTag = (String) this.menuList.get(num).getText();
			// flag = slideMenuOnChange(menuTag);
            this.setFlag(num);
		}
		return num;
	}

	// @SuppressWarnings("deprecation")
	// public int Start() {
	// int num = getFlag();
	// if (num != 0) {
	// menuList.get(num).setBackgroundDrawable(null);
	// // if (num > 3) {
	// // SlidingActivity.pagerIndex = 0;
	// // SlidingActivity.viewPager
	// // .setCurrentItem(SlidingActivity.pagerIndex);
	// // }
	// num = 0;
	// menuList.get(num).setBackgroundResource(R.drawable.menu_bg);
	// setFlag(num);
	// // menuTag = (String) menuList.get(flag).getText();
	// }
	// menuTag = (String) menuList.get(num).getText();
	// // num = slideMenuOnChange(menuTag);
	// return num;
	// }

	// ���ʱ������
	// @SuppressWarnings("unused")
	// private int slideMenuOnChange(String menuTag) {
	// String tempUrl = null;
	// // LayoutInflater inflater = activity.getLayoutInflater();
	// // WebView webview = (WebView)activity.findViewById(R.id.webview);
	// // webview.getSettings();
	// for (int i = 0; i < constants.menus.length; i++) {
	// if (menuTag.equals(constants.menus[i])) {
	// return i;
	// }
	// }
	// return 0;
	// }
}