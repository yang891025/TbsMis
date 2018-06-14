package com.tbs.chat.adapter;

import java.io.FileNotFoundException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tbs.chat.R;
import com.tbs.chat.R.anim;
import com.tbs.chat.constants.Config;
import com.tbs.chat.database.dao.DBUtil;
import com.tbs.chat.entity.FriendEntity;
import com.tbs.chat.entity.MessageEntity;
import com.tbs.chat.entity.UserEntity;
import com.tbs.chat.ui.address.SelectContactUI;
import com.tbs.chat.ui.tools.ImageGalleryUI;
import com.tbs.chat.util.AudioRecorder;
import com.tbs.chat.util.BitmapUtil;
import com.tbs.chat.util.TimeUtil;

public class ChattingAdapter extends BaseAdapter {
	
	private static final String TAG = null;
	private Context context;
	private Activity activity;
	private ViewHolder holder;
	private AudioRecorder recorder;
	private List<MessageEntity> chatMessages;
	private String path;
	private DBUtil dao;
	private Bitmap userBitmap = null;
	private Bitmap friendBitmap = null;
	

	public ChattingAdapter(Context context, Activity activity, DBUtil dao, AudioRecorder recorder, List<MessageEntity> messages) {
		super();
		this.dao = dao;
		this.context = context;
		this.activity = activity;
		this.recorder = recorder;
		this.chatMessages = messages;
	}

	@Override
	public int getCount() {
		return chatMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return chatMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//实例化hodler
		holder = new ViewHolder();
		//get message
		MessageEntity message = chatMessages.get(position);
		//获得时间
		String time = message.getTime();
		//获取用户ID
		String userID = message.getSelf();
		//获取好友ID
		String friendID = message.getFriend();
		//查询用户
		UserEntity user = dao.queryUser(context, "UserTable", ""+userID);
		//重新复制用户ID
		userID = user.getUserID();
		//获得用户头像path
		String userHead = user.getHead();
		//查询好友头像内容
		FriendEntity friend = dao.queryName(friendID, userID);
		//获得好友id
		friendID = friend.getFriendID();
		//获得好友头像
		String friendHead = friend.getHead();
		//获得消息内容
		String content = message.getContent();
		//获得消息方向,direction: to or from ?
		int direction = message.getDirection();
		//获得消息类型
		int type = message.getType();
		//创建用户头像
		if(userBitmap == null){
			if(userHead != null && !userHead.equals("")){
				userBitmap = BitmapUtil.ReadBitmapById(context, userHead, 60, 60);
			}
		}
		//创建好友头像
		if(friendBitmap == null){
			if(friendHead != null && !friendHead.equals("")){
				friendBitmap = BitmapUtil.ReadBitmapById(context, friendHead, 60, 60);
			}
		}
		
		/*
		 * 实例化控件 根据不同的内容 包括音频 文字 图片三种格式 以后需要扩展视频等
		 */
		if (direction == Config.MESSAGE_FROM) {//接收到的消息
			 if(type == Config.MESSAGE_TYPE_IMG){//图片格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from_picture, null);
				holder.btn = (FrameLayout) convertView.findViewById(R.id.chatting_click_area);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.user = (TextView) convertView.findViewById(R.id.chatting_user_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.downloading_pb);
				holder.contentIV = (ImageView) convertView.findViewById(R.id.chatting_content_iv);
				holder.head.setTag(friend);//将friend传入控件内
				holder.btn.setTag(R.id.chatting_voice_play_content,position);//将position传入控件内
				holder.btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag(R.id.chatting_voice_play_content);
						Bundle b = new Bundle();
						b.putSerializable("message", chatMessages.get(position));
						Intent intent = new Intent(context,ImageGalleryUI.class);
						intent.putExtras(b);
						context.startActivity(intent);
						activity.overridePendingTransition(anim.fast_faded_in, anim.fast_faded_out);
					}
				});
			}else if(type == Config.MESSAGE_TYPE_AUDIO){//音频格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from_voice, null);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.content = (TextView) convertView.findViewById(R.id.chatting_voice_play_anim_tv);
				holder.voiceTime = (TextView) convertView.findViewById(R.id.chatting_content_itv);
				holder.stateIV = (ImageView) convertView.findViewById(R.id.chatting_state_iv);
				holder.btn = (FrameLayout) convertView.findViewById(R.id.chatting_voice_play_content);
				holder.content.setTag(R.id.chatting_voice_play_content,position);//将position传入控件内
				holder.content.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag(R.id.chatting_voice_play_content);
						MessageEntity msg = chatMessages.get(position);
						// 更新数据库
						dao.updateMessage(context, "" + msg.getSelf(), "" + msg.getFriend(), msg.getDirection(), msg.getType(), msg.getTime(), msg.getContent(), 1);
						// 存储数据
						msg.setRead_type(1);
						// 存储数据
						chatMessages.set(position, msg);
						// 刷新界面
						notifyDataSetChanged();
						// 播放音频文件
						String recodePath = msg.getContent();
						recorder.audioManager(recodePath);
					}
				});
			}else if(type == Config.MESSAGE_TYPE_SMS){//音频格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.user = (TextView) convertView.findViewById(R.id.chatting_user_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.content = (TextView) convertView.findViewById(R.id.chatting_content_itv);
			}else{//文本格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_from, null);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.user = (TextView) convertView.findViewById(R.id.chatting_user_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.content = (TextView) convertView.findViewById(R.id.chatting_content_itv);
			}
			holder.head.setTag(friend);
			holder.head.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					FriendEntity friend = (FriendEntity) v.getTag();
					Bundle b = new Bundle();
					b.putSerializable("friendEntity", friend);
					Intent intent = new Intent(context,SelectContactUI.class);
					intent.putExtras(b);
					context.startActivity(intent);
				}
			});
			
			if(friendBitmap != null){//设置头像
				holder.head.setImageBitmap(friendBitmap);
			}
			
			
		}else if(direction == Config.MESSAGE_TO) {//发送的消息
			if(type == Config.MESSAGE_TYPE_IMG){//图片格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to_picture, null);
				holder.btn = (FrameLayout) convertView.findViewById(R.id.chatting_click_area);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.user = (TextView) convertView.findViewById(R.id.chatting_user_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.uploading_pb);
				holder.updateTV = (TextView) convertView.findViewById(R.id.uploading_tv);
				holder.stateIV = (ImageView) convertView.findViewById(R.id.chatting_state_iv);
				holder.contentIV = (ImageView) convertView.findViewById(R.id.chatting_content_iv);
				holder.contentIV.setTag(R.id.chatting_voice_play_content,position);//将position传入控件内
				holder.btn.setTag(R.id.chatting_voice_play_content,position);//将position传入控件内
				holder.btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag(R.id.chatting_voice_play_content);
						Bundle b = new Bundle();
						b.putSerializable("message", chatMessages.get(position));
						Intent intent = new Intent(context,ImageGalleryUI.class);
						intent.putExtras(b);
						context.startActivity(intent);
						activity.overridePendingTransition(anim.fast_faded_in, anim.fast_faded_out);
					}
				});
			}else if(type == Config.MESSAGE_TYPE_AUDIO){//音频格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to_voice, null);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.chatting_voice_sending);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.content = (TextView) convertView.findViewById(R.id.chatting_voice_play_anim_tv);
				holder.voiceTime = (TextView) convertView.findViewById(R.id.chatting_content_itv);
				holder.stateIV = (ImageView) convertView.findViewById(R.id.chatting_state_iv);
				holder.btn = (FrameLayout) convertView.findViewById(R.id.chatting_voice_play_content);
				holder.sendBg = (TextView) convertView.findViewById(R.id.chatting_voice_sending_bg);
				holder.content.setTag(R.id.chatting_voice_play_content,position);
				holder.content.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						int position = (Integer) v.getTag(R.id.chatting_voice_play_content);
						MessageEntity msg = chatMessages.get(position);
						//播放音频
				        recorder.audioManager(msg.getContent());
					}
				});
			}else if(type==Config.MESSAGE_TYPE_SMS){//短信格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to, null);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.user = (TextView) convertView.findViewById(R.id.chatting_user_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.content = (TextView) convertView.findViewById(R.id.chatting_content_itv);
				holder.stateIV = (ImageView) convertView.findViewById(R.id.chatting_state_iv);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.uploading_pb);
				holder.stateTV = (TextView) convertView.findViewById(R.id.textview);
			}else{//文本格式内容
				convertView = LayoutInflater.from(context).inflate(R.layout.chatting_item_to, null);
				holder.time = (TextView) convertView.findViewById(R.id.chatting_time_tv);
				holder.user = (TextView) convertView.findViewById(R.id.chatting_user_tv);
				holder.head = (ImageView) convertView.findViewById(R.id.chatting_avatar_iv);
				holder.content = (TextView) convertView.findViewById(R.id.chatting_content_itv);
				holder.stateIV = (ImageView) convertView.findViewById(R.id.chatting_state_iv);
				holder.progress = (ProgressBar) convertView.findViewById(R.id.uploading_pb);
			} 
			if(friendBitmap != null){//设置头像
				holder.head.setImageBitmap(userBitmap);
			}
		}
		
		/**
		 * 是指时间参数
		 */
		if(time != null && !"".equals(time)){
			String relativeTime = TimeUtil.getRelativeTime(time);
			holder.time.setText(relativeTime);
			holder.time.setVisibility(View.VISIBLE);
		}
		
		//分别处理不同内容的信息
		switch (type) {
		case Config.MESSAGE_TYPE_TXT: //处理"文本"和"表情"
			if(direction == Config.MESSAGE_FROM){
				holder.content.setText(content);
			}else if(direction == Config.MESSAGE_TO){
				holder.content.setText(content);
				if(message.getRead_type() == 0){//0是发送中
					holder.progress.setVisibility(View.VISIBLE);
					holder.stateIV.setVisibility(View.GONE);
				}else if(message.getRead_type() == 1){//1是发送成功
					holder.progress.setVisibility(View.GONE);
					holder.stateIV.setVisibility(View.GONE);
				}else if(message.getRead_type() == 2){//2是失败
					holder.stateIV.setImageResource(R.drawable.msg_state_failed_resend);
					holder.progress.setVisibility(View.GONE);
					holder.stateIV.setVisibility(View.VISIBLE);
				}
			}
			break;
			
		case Config.MESSAGE_TYPE_SMS: //处理"文本"和"表情"
			if(direction == Config.MESSAGE_FROM){
				holder.content.setText(content);
			}else if(direction == Config.MESSAGE_TO){
				holder.content.setText(content);
				if(message.getRead_type() == 0){//0是发送中
					holder.progress.setVisibility(View.VISIBLE);
					holder.stateIV.setVisibility(View.GONE);
					holder.stateTV.setVisibility(View.GONE);
				}else if(message.getRead_type() == 1){//1是发送成功
					holder.progress.setVisibility(View.GONE);
					holder.stateIV.setVisibility(View.GONE);
					holder.stateTV.setVisibility(View.VISIBLE);
					holder.stateTV.setText("成功");
				}else if(message.getRead_type() == 2){//2是失败
					holder.progress.setVisibility(View.GONE);
					holder.stateIV.setImageResource(R.drawable.msg_state_failed_resend);
					holder.stateIV.setVisibility(View.VISIBLE);
					holder.stateTV.setVisibility(View.GONE);
				}else if(message.getRead_type() == 3){//3已经送达
					holder.progress.setVisibility(View.GONE);
					holder.stateIV.setVisibility(View.GONE);
					holder.stateTV.setVisibility(View.VISIBLE);
					holder.stateTV.setText("已送达");
				}
			}
			break;
		
		case Config.MESSAGE_TYPE_IMG: // 处理 “图片”
			Bitmap bitmap = null;
			if(direction == Config.MESSAGE_FROM){
				String filePath = message.getContent();
				try {
					bitmap = getThumbNail(filePath, 100);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				holder.contentIV.setImageBitmap(bitmap);
			}else if(direction == Config.MESSAGE_TO){
				String filePath = message.getContent();
				try {
					bitmap = getThumbNail(filePath, 100);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				if(message.getRead_type() == 0){//0是发送中
					holder.progress.setVisibility(View.VISIBLE);
					holder.contentIV.setVisibility(View.GONE);
				}else if(message.getRead_type() == 1){//1是发送成功
					holder.progress.setVisibility(View.GONE);
					holder.contentIV.setImageBitmap(bitmap);
				}else if(message.getRead_type() == 2){//2是失败
					holder.progress.setVisibility(View.GONE);
					holder.contentIV.setImageBitmap(bitmap);
					holder.stateIV.setImageResource(R.drawable.msg_state_failed_resend);
					holder.stateIV.setVisibility(View.VISIBLE);
				}
			}
			break;
			
		case Config.MESSAGE_TYPE_AUDIO: //处理 “音频”
			// 语音时长
			holder.voiceTime.setText(message.getData5()+"″");
			if(direction == Config.MESSAGE_FROM){
				if(message.getRead_type() == 0){//0是发送中
					holder.stateIV.setVisibility(View.VISIBLE);
				}else if(message.getRead_type() == 1){//1是发送成功
					holder.stateIV.setVisibility(View.GONE);
				}
			}else if(direction == Config.MESSAGE_TO){
				// 语音时长
				holder.voiceTime.setText(message.getData5()+"″");
				if(message.getRead_type() == 0){//0是发送中
					holder.sendBg.setVisibility(View.VISIBLE);
					holder.progress.setVisibility(View.VISIBLE);
					holder.content.setVisibility(View.GONE);
					holder.stateIV.setVisibility(View.GONE);
					holder.voiceTime.setVisibility(View.GONE);
				}else if(message.getRead_type() == 1){//1是发送成功
					holder.sendBg.setVisibility(View.GONE);
					holder.progress.setVisibility(View.GONE);
					holder.content.setVisibility(View.VISIBLE);
					holder.voiceTime.setVisibility(View.VISIBLE);
					holder.stateIV.setVisibility(View.GONE);
				}else if(message.getRead_type() == 2){//2是失败
					holder.sendBg.setVisibility(View.GONE);
					holder.voiceTime.setVisibility(View.GONE);
					holder.progress.setVisibility(View.GONE);
					holder.content.setVisibility(View.VISIBLE);
					holder.stateIV.setImageResource(R.drawable.msg_state_failed_resend);
					holder.stateIV.setVisibility(View.VISIBLE);
				}
			}
			break;
		}
		return convertView;
	}

	
	
	/*
	 * 获取uri指定的图片的缩略图
	 * @param uri   指向图片的URI
	 * @param cr    内容解决者
	 * @param width 图片想要显示的宽度（像素）
	 * @return
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unused")
	private Bitmap getThumbNail(String filePath, int width) throws FileNotFoundException {
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(filePath, options);
		int be = (int)(options.outWidth/(float)width);
		options.inSampleSize = be;
		options.inJustDecodeBounds = false;
		Bitmap bitmap=BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}
	
	// 优化listview的Adapter
	static class ViewHolder {
		FrameLayout btn;
		TextView time;
		TextView user;
		ImageView head;
		TextView content;
		ImageView voiceIV;
		TextView voiceTime;
		ImageView playTV;
		ImageView contentIV;
		ProgressBar progress;
		TextView updateTV;
		ImageView stateIV;
		TextView stateTV;
		TextView sendBg;
		
	}
}
