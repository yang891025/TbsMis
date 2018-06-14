package com.tbs.tbsmis.weixin;

import com.tbs.tbsmis.entity.NewsEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeixinNewsUtils {
	public ArrayList<NewsEntity> getNewsEntity(String JsonNews) {
		ArrayList<NewsEntity> News = new ArrayList<NewsEntity>();
		try {
			JSONArray json = new JSONArray(JsonNews);
			for (int i = 0; i < json.length(); i++) {
				// 获取每一个JsonObject对象
				JSONObject myjObject = json.getJSONObject(i);
				NewsEntity mapNews = new NewsEntity();
				mapNews.setAuthor(myjObject.getString("author"));
				mapNews.setContent(myjObject.getString("content"));
				mapNews.setContentUrl(myjObject.getString("content_source_url"));
				mapNews.setDescription(myjObject.getString("Descripton"));
				mapNews.setDigest(myjObject.getString("digest"));
				mapNews.setPicUrl(myjObject.getString("PicUrl"));
				mapNews.setTitle(myjObject.getString("Title"));
				mapNews.setUrl(myjObject.getString("Url"));
				News.add(mapNews);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return News;
	}
}