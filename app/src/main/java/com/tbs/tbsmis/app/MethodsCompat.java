package com.tbs.tbsmis.app;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.Window;

/**
 * Android各版本的兼容方法
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-8-6
 */
public class MethodsCompat {

	@TargetApi(5)
	public static void overridePendingTransition(Activity activity,
			int enter_anim, int exit_anim) {
		activity.overridePendingTransition(enter_anim, exit_anim);
	}

	@TargetApi(7)
	public static Bitmap getThumbnail(ContentResolver cr, long origId,
			int kind, BitmapFactory.Options options) {
		return Thumbnails.getThumbnail(cr, origId, kind,
				options);
	}

	@TargetApi(8)
	public static File getExternalCacheDir(Context context) {
		return context.getExternalCacheDir();
	}

	@TargetApi(11)
	public static void recreate(Activity activity) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			activity.recreate();
		}
	}

	@TargetApi(11)
	public static void setLayerType(View view, int layerType, Paint paint) {
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			view.setLayerType(layerType, paint);
		}
	}

	@TargetApi(14)
	public static void setUiOptions(Window window, int uiOptions) {
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			window.setUiOptions(uiOptions);
		}
	}

}