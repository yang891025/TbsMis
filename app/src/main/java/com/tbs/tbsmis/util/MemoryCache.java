package com.tbs.tbsmis.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

public class MemoryCache {

	private static final String TAG = "MemoryCache";
	// 放入缓存时是个同步操作
	// LinkedHashMap构造方法的最后一个参数true代表这个map里的元素将按照最近使用次数由少到多排列，即LRU
	// 这样的好处是如果要将缓存中的元素替换，则先遍历出最近最少使用的元素来替换以提高效率
	private final Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f, true));
	// 缓存中图片所占用的字节，初始0，将通过此变量严格控制缓存所占用的堆内存
	private long size;// current allocated size
	// 缓存只能占用的最大堆内存
	private long limit = 1000000;// max memory in bytes

	public MemoryCache() {
		// use 25% of available heap size
        this.setLimit(Runtime.getRuntime().maxMemory() / 4);
	}

	public void setLimit(long new_limit) {
        this.limit = new_limit;
		// Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. +
		// "MB");
	}

	public Bitmap get(String id) {
		try {
			if (!this.cache.containsKey(id)) {
				return null;
			}
			return this.cache.get(id);
		} catch (NullPointerException ex) {
			return null;
		}
	}
	public int getSize() {
		return this.cache.size();
	}
	public void put(String id, Bitmap bitmap) {
		try {
			if (!this.cache.containsKey(id)){
                this.cache.put(id, bitmap);
                this.size += this.getSizeInBytes(bitmap);
                this.checkSize();
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/**
	 * 严格控制堆内存，如果超过将首先替换最近最少使用的那个图片缓存
	 * 
	 */
	private void checkSize() {
		Log.i(MemoryCache.TAG, "cache size=" + this.size + " length=" + this.cache.size());
		if (this.size > this.limit) {
			// 先遍历最近最少使用的元素
			Iterator<Map.Entry<String, Bitmap>> iter = this.cache.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Bitmap> entry = iter.next();
                this.size -= this.getSizeInBytes(entry.getValue());
				iter.remove();
				System.out.println("remove");
				if (this.size <= this.limit)
					break;
			}
			Log.i(MemoryCache.TAG, "Clean cache. New size " + this.cache.size());
		}
	}

	public void clear() {
        this.cache.clear();
	}

	/**
	 * 图片占用的内存
	 * 
	 * @param bitmap
	 * @return
	 */
	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}