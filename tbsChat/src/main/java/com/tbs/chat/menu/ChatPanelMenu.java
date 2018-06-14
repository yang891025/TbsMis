package com.tbs.chat.menu;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tbs.chat.R;

public class ChatPanelMenu extends Activity {

	protected static final String TAG = "RegSetInfoMenu";
	
	private ListView menuListView;
	String[] array = {"拍照","从相册选择","取消"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog_menu_layout);
		init();
	}

	private void init() {
		
        getWindow().setGravity(Gravity.BOTTOM);//设置靠右对齐
		getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		
		menuListView = (ListView) findViewById(R.id.content_list);
		MyAdapter myAdapter = new MyAdapter(this,array);
		menuListView.setAdapter(myAdapter);
		menuListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 2:
					finishActivity();
					break;
				default:
					break;
				}
			}
		});
	}
	
	/*
	 * 退出当前菜单方法
	 */
	public void finishActivity(){
		finish();
	}
	
	
	class MyAdapter extends BaseAdapter{
		
		private Context mContext;
		private LayoutInflater mInflater;
		
		public MyAdapter(Context mContext,String[] array) {
			this.mContext = mContext;
			this.mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return array.length;
		}

		@Override
		public Object getItem(int position) {
			return array[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				if (position == 2) {
					convertView = mInflater.inflate(R.layout.alert_dialog_menu_list_layout_cancel, null);
				} else {
					convertView = mInflater.inflate(R.layout.alert_dialog_menu_list_layout, null);
				}
				viewHolder = new ViewHolder();
				viewHolder.title = (TextView) convertView.findViewById(R.id.popup_text);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.title.setText(array[position]);
			return convertView;
		}
	}
	
	class ViewHolder {
		TextView title;//国家
	}
}
