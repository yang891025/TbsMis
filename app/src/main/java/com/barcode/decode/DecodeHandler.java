/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barcode.decode;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.tbs.tbsmis.R;
import com.tbs.tbsmis.activity.ScanActivity;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Map;

final class DecodeHandler extends Handler {

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final ScanActivity activity;

	private MultiFormatReader multiFormatReader;

	private boolean running = true;

	DecodeHandler(ScanActivity activity, Map<DecodeHintType, Object> hints) {
        this.multiFormatReader = new MultiFormatReader();
        this.multiFormatReader.setHints(hints);
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message message) {
		if (!this.running) {
			return;
		}
		switch (message.what) {
		case R.id.decode:
            this.decode((byte[]) message.obj, message.arg1, message.arg2);
			break;
		case R.id.quit:
            this.running = false;
			Looper.myLooper().quit();
			break;
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it
	 * took. For efficiency, reuse the same reader objects from one decode to
	 * the next.
	 * 
	 * @param data
	 *            The YUV preview frame.
	 * @param width
	 *            The width of the preview frame.
	 * @param height
	 *            The height of the preview frame.
	 */
	private void decode(byte[] data, int width, int height) {
		long start = System.currentTimeMillis();
		Result rawResult = null;

		byte[] rotatedData = new byte[data.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++)
				rotatedData[x * height + height - y - 1] = data[x + y * width];
		}
		int tmp = width;
		width = height;
		height = tmp;

		PlanarYUVLuminanceSource source = this.activity.getCameraManager()
				.buildLuminanceSource(rotatedData, width, height);
		if (source != null) {
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			try {
				// 预览界面最终取到的是个bitmap，然后对其进行解码
				rawResult = this.multiFormatReader.decodeWithState(bitmap);
			} catch (ReaderException re) {
				// continue
			} finally {
                this.multiFormatReader.reset();
			}
		}

		Handler handler = this.activity.getHandler();
		if (rawResult != null) {
			// Don't log the barcode contents for security.
			long end = System.currentTimeMillis();
			Log.d(DecodeHandler.TAG, "Found barcode in " + (end - start) + " ms");
			if (handler != null) {
				Message message = Message.obtain(handler,
                        R.id.decode_succeeded, rawResult);
				Bundle bundle = new Bundle();
                DecodeHandler.bundleThumbnail(source, bundle);
				message.setData(bundle);
				message.sendToTarget();
			}
		} else {
			if (handler != null) {
				Message message = Message.obtain(handler, R.id.decode_failed);
				message.sendToTarget();
			}
		}
	}

	private static void bundleThumbnail(PlanarYUVLuminanceSource source,
			Bundle bundle) {
		int[] pixels = source.renderThumbnail();
		int width = source.getThumbnailWidth();
		int height = source.getThumbnailHeight();
		Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height,
				Config.ARGB_8888);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 50, out);
		bundle.putByteArray(DecodeThread.BARCODE_BITMAP, out.toByteArray());
		bundle.putFloat(DecodeThread.BARCODE_SCALED_FACTOR, (float) width
				/ source.getWidth());
	}

	public void setHints(Hashtable<DecodeHintType, Object> paramHashtable) {
        this.multiFormatReader = new MultiFormatReader();
        this.multiFormatReader.setHints(paramHashtable);
	}
}