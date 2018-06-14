package com.tbs.chat.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.constants.Config;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.entity.UserEntity;
import com.tbs.chat.util.BitmapUtil;
import com.tbs.chat.util.TimeUtil;

public class MessageAdapter extends BaseAdapter {

	private static final String TAG = "MessageAdapter";
	
	private Context mContext;
	private ArrayList<MessageEntity> msgList;// 短信列表
	private HashMap<String, Bitmap> headMap = new HashMap<String, Bitmap>();
	private Map<String, Integer> map;
	private MessageEntity message;
	private FriendEntity friend;
	private UserEntity user;
	private DBUtil dao = null;
	private Bitmap userBitmap = null;
	private Bitmap friendBitmap = null;
	private int count = 0;

	public MessageAdapter(Context mContext, ArrayList<MessageEntity> msgList, DBUtil dao) {
		this.mContext = mContext;
		this.msgList = msgList;
		this.dao = dao;
	}
	
	public MessageAdapter(Context mContext, ArrayList<MessageEntity> msgList, Map<String, Integer> map, DBUtil dao) {
		this.mContext = mContext;
		this.msgList = msgList;
		this.map = map;
		this.dao = dao;
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//holder
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.conversation_item, null);
			viewHolder.title = (TextView) convertView.findViewById(R.id.nickname_tv);
			viewHolder.message = (TextView) convertView.findViewById(R.id.last_msg_tv);
			viewHolder.time = (TextView) convertView.findViewById(R.id.update_time_tv);
			viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar_iv);
			viewHolder.tipcnt_tv = (TextView) convertView.findViewById(R.id.tipcnt_tv);
			viewHolder.avatar_prospect_iv = (TextView) convertView.findViewById(R.id.avatar_prospect_iv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		message = msgList.get(position);
		//获得消息类型
		int type = message.getType();
		//获取用户ID
		String userID = message.getSelf();
		//获取好友ID
		String friendID = message.getFriend();
		if(map != null && map.containsKey(friendID)){
			count = map.get(friendID);
		}else{
			count=0;
		}
		//获取短信读取类型
		int readType = message.getRead_type();
		//查询好友
		friend = dao.queryName(friendID, userID);
		// 获得好友id
		friendID = friend.getFriendID();
		// 获得好友头像
		String friendHead = friend.getHead();
		// 创建好友头像
		if (!headMap.containsKey(friendID)) {
			if(friendHead != null && !friendHead.equals("")){
				friendBitmap = BitmapUtil.ReadBitmapById(mContext, friendHead, 60, 60);
				headMap.put(friendID, friendBitmap);
			}
		}else{
			friendBitmap = headMap.get(friendID);
		}
		if(friendBitmap != null){
			viewHolder.avatar.setImageBitmap(friendBitmap);
		}
		viewHolder.title.setText(friend.getNickName());
		if(count > 0){
			viewHolder.tipcnt_tv.setVisibility(View.VISIBLE);
			viewHolder.tipcnt_tv.setText(""+count);
		}else{
			viewHolder.tipcnt_tv.setVisibility(View.INVISIBLE);
		}
		if (type == Config.MESSAGE_TYPE_TXT) {
			viewHolder.message.setText(message.getContent());
		} else if (type == Config.MESSAGE_TYPE_SMS) {
			viewHolder.message.setText("[短信]"+message.getContent());
		} else if (type == Config.MESSAGE_TYPE_IMG) {
			viewHolder.message.setText("[图片]");
		} else if (type == Config.MESSAGE_TYPE_AUDIO) {
			viewHolder.message.setText("[语音]");
		}
		String time = message.getTime();
		if (time != null && !"".equals(time)) {
			viewHolder.time.setText(TimeUtil.getRelativeTime(time));
		}
		return convertView;
	}
	
	class ViewHolder {
		TextView title;// 标题
		ImageView avatar;// 头像
		TextView message;// 内容
		TextView time;// 时间
		TextView tipcnt_tv;//大圆圈
		TextView avatar_prospect_iv;//小的圆圈
	}
}