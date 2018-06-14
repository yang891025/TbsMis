package com.tbs.tbsmis.check;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.constants.constants;
import com.tbs.tbsmis.entity.WXUserEntity;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class WechatUserAdapter extends BaseAdapter implements SectionIndexer {
	// �����ݵ�list
	private final ArrayList<WXUserEntity> list;
	private LayoutInflater inflater;
	private  List<String> listTag;
	private  Context context;
	private  ImageLoader imageLoader;

	// ������
	@SuppressLint("UseSparseArrays")
	public WechatUserAdapter(ArrayList<WXUserEntity> userlist, Context context,
			List<String> listTag) {
		this.context = context;
        list = userlist;
		this.listTag = listTag;
        this.imageLoader = new ImageLoader(context,R.drawable.default_avatar);
        this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WechatUserAdapter.ViewHolder holder = null;
		// ���ViewHolder����
		if (convertView == null) {
			holder = new WechatUserAdapter.ViewHolder();
			convertView = this.inflater.inflate(R.layout.weixin_user_item, null);
			holder.tv = (TextView) convertView.findViewById(R.id.name);
			holder.re = (TextView) convertView.findViewById(R.id.message);
			holder.id = (TextView) convertView.findViewById(R.id.alpha);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.catalog = (TextView) convertView.findViewById(R.id.tag);
			holder.head = (ImageView) convertView.findViewById(R.id.imageview);
			convertView.setTag(holder);
		} else {
			holder = (WechatUserAdapter.ViewHolder) convertView.getTag();
		}
		holder.id.setText(this.list.get(position).getOpenid());
		if (this.list.get(position).getRemark().equals(""))
			holder.tv.setText(this.list.get(position).getNickname());
		else {
			holder.tv.setText(this.list.get(position).getRemark() + "("
					+ this.list.get(position).getNickname() + ")");
		}
		holder.re.setText(this.list.get(position).getProvince() + "  "
				+ this.list.get(position).getCity());
		String time = StringUtils.LongtoDate(this.list.get(position)
				.getSubscribe_time() + "000");
		holder.time.setText(time);

        this.imageLoader.DisplayImage(this.list.get(position).getHeadimgurl(),
				holder.head);
		if (UIHelper.getShareperference(this.context, constants.SAVE_LOCALMSGNUM,
				"user_sort", 0) == 1) {
			holder.catalog.setVisibility(View.GONE);
		} else {
			String catalog = this.listTag.get(position);
			if (position == 0) {
				holder.catalog.setVisibility(View.VISIBLE);
				holder.catalog.setText(catalog);
			} else {
				String lastCatalog = this.listTag.get(position - 1);
				if (catalog.equals(lastCatalog)) {
					holder.catalog.setVisibility(View.GONE);
				} else {
					holder.catalog.setVisibility(View.VISIBLE);
					holder.catalog.setText(catalog);
				}
			}
		}
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv;
		public TextView re;
		public TextView id;
		public TextView time;
		public TextView group;
		public TextView catalog;
		public ImageView head;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < this.list.size(); i++) {
			char firstChar = this.listTag.get(i).toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}