package com.tbs.tbsmis.check;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.entity.WXTextEntity;
import com.tbs.tbsmis.entity.WXUserEntity;
import com.tbs.tbsmis.util.ImageLoader;
import com.tbs.tbsmis.util.StringUtils;
import com.tbs.tbsmis.weixin.WeixinUserInfoActivity;

import java.util.ArrayList;

public class ChatMsgViewAdapter extends BaseAdapter {

	private static final String TAG = "ChatMsgViewAdapter";
	private final ArrayList<WXTextEntity> msgList;// �����б�
	private final LayoutInflater mInflater;
	private final Context ctx;
	private final ImageLoader imageLoader;
	private final WXUserEntity userInfo;
	private String Path;

	public interface IMsgViewType {
		int IMVT_TO_MSG = 0;
		int IMVT_COM_MSG = 1;
		int IMVT_CAOGAO_MSG = 2;
	}

	public ChatMsgViewAdapter(Context context, ArrayList<WXTextEntity> coll,
			WXUserEntity userInfo) {
        this.mInflater = LayoutInflater.from(context);
        msgList = coll;
        ctx = context;
		this.userInfo = userInfo;
        this.imageLoader = new ImageLoader(this.ctx, R.drawable.default_avatar);
	}

	@Override
	public int getCount() {
		return this.msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		int isComMsg = this.msgList.get(position).getDirection();
		if (isComMsg == 1) {
			return ChatMsgViewAdapter.IMsgViewType.IMVT_TO_MSG;
		} else if (isComMsg == 0) {
			return ChatMsgViewAdapter.IMsgViewType.IMVT_COM_MSG;
		} else {
			return ChatMsgViewAdapter.IMsgViewType.IMVT_CAOGAO_MSG;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// MessageEntity entity = msgList.get(position);
		int isComMsg = this.msgList.get(position).getDirection();
		ChatMsgViewAdapter.ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg == 1) {
				convertView = this.mInflater.inflate(
                        R.layout.chatting_item_msg_text_right, null);
			} else if (isComMsg == 0) {
				convertView = this.mInflater.inflate(
                        R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = this.mInflater.inflate(
                        R.layout.chatting_item_msg_text_right, null);
			}
			viewHolder = new ChatMsgViewAdapter.ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			viewHolder.msgState = (TextView) convertView
					.findViewById(R.id.send_state);
			viewHolder.isComMsg = isComMsg;
			viewHolder.icon.setTag(position);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChatMsgViewAdapter.ViewHolder) convertView.getTag();
		}
		viewHolder.tvSendTime.setText(StringUtils.friendly_time(StringUtils
				.LongtoDate(this.msgList.get(position).getCreateTime() + "000")));
		viewHolder.tvContent.setText(this.msgList.get(position).getContent());
		if (isComMsg == 1) {
			// imageLoader.DisplayImage(Path, viewHolder.icon);
			int sendState = this.msgList.get(position).getSendState();
			if (sendState == 0) {
				viewHolder.msgState.setText("未发送");
			} else if (sendState == 1) {
				viewHolder.msgState.setText("正在发送");
			} else if (sendState == 2) {
				viewHolder.msgState.setText("发送成功");
			} else if (sendState == 4) {
				viewHolder.msgState.setText("送达");
			} else if (sendState == 3) {
				viewHolder.msgState.setText("发送失败");
			}

		} else {
            this.imageLoader.DisplayImage(this.userInfo.getHeadimgurl(), viewHolder.icon);
			// viewHolder.tvSendTime.setText(msgList.get(position).get("MsgTime"));
			viewHolder.msgState.setVisibility(View.INVISIBLE);
			viewHolder.icon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putSerializable("WXUserEntity", ChatMsgViewAdapter.this.userInfo);
					Intent intent = new Intent(ChatMsgViewAdapter.this.ctx,
							WeixinUserInfoActivity.class);
					intent.putExtras(b);
                    ChatMsgViewAdapter.this.ctx.startActivity(intent);
				}
			});

		}
		return convertView;
	}

	static class ViewHolder {
		public TextView tvSendTime;
		public TextView tvContent;
		public TextView msgState;
		public ImageView icon;
		public int isComMsg = 1;
	}

}
