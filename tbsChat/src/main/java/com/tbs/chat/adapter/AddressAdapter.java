package com.tbs.chat.adapter;


import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.constants.Constants;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.util.BitmapUtil;

public class AddressAdapter extends BaseAdapter implements SectionIndexer {

	private static final String TAG = "AddressAdapter";
	private Context mContext;
	private ArrayList<FriendEntity> array;
	private ArrayList<String> list = new ArrayList<String>();
	private Bitmap bitmap;
	private Uri fileUri;
	
	public AddressAdapter(Context mContext, ArrayList<FriendEntity> array) {
		this.mContext = mContext;
		this.array = array;
	}

	@Override
	public int getCount() {
		return array.size();
	}

	@Override
	public Object getItem(int position) {
		return array.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final String nickName = array.get(position).getNickName();
		final String headPath = array.get(position).getHead();
		final String phone = array.get(position).getPhone();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.address_item, null);
			viewHolder.catalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.content);
			viewHolder.nick = (TextView) convertView.findViewById(R.id.contactitem_nick);
			viewHolder.account = (TextView) convertView.findViewById(R.id.contactitem_account);
			viewHolder.contactitem_signature = (TextView) convertView.findViewById(R.id.contactitem_signature);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		String catalog = Constants.pinyinFirst.get(position);
		
		String lastCatalog = null;
		
		if (catalog.equals(lastCatalog)) {
			viewHolder.catalog.setVisibility(View.GONE);
		} else {
			viewHolder.catalog.setVisibility(View.VISIBLE);
			viewHolder.catalog.setText(catalog);
		}
		
		catalog = lastCatalog;
		
		viewHolder.nick.setText(nickName);
		
		if(headPath != null && !headPath.equals("")){
			bitmap = BitmapUtil.ReadBitmapById(mContext, headPath, 60, 60);
			if(bitmap!=null){
				viewHolder.avatar.setImageBitmap(bitmap);
			}
		}else{
			viewHolder.avatar.setImageResource(R.drawable.default_avatar);
		}
		
		viewHolder.account.setText(phone);
		viewHolder.account.setVisibility(View.GONE);
		viewHolder.contactitem_signature.setVisibility(View.INVISIBLE);
		return convertView;
	}
	
	class ViewHolder {
		TextView catalog;// 目录
		ImageView avatar;// 头像
		TextView nick;// 昵称
		TextView account;//账号
		TextView contactitem_signature;
	}
	
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < array.size(); i++) {
			char firstChar = Constants.pinyinFirst.get(i).toUpperCase().charAt(0);
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
	
	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * @param chines 汉字
	 * @return 拼音

	public String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		if (nameChar[0] > 128) {
			try {
				pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat)[0].charAt(0);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else {
			pinyinName += nameChar[0];
		}
		return pinyinName.toUpperCase();
	}
	 */
}