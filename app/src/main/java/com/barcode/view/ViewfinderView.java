/*
 * Copyright (C) 2008 ZXing authors
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

package com.barcode.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.barcode.camera.CameraManager;
import com.google.zxing.ResultPoint;
import com.tbs.tbsmis.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * <br/>
 * <br/>
 * 该视图是覆盖在相机的预览视图之上的一层视图。扫描区构成原理，其实是在预览视图上画四块遮罩层，
 * 中间留下的部分保持透明，并画上一条激光线，实际上该线条就是展示而已，与扫描功能没有任何关系。
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 四个绿色边角对应的长度
	 */
	private final int ScreenRate;
	/**
	 * 四个绿色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 6;
	private static final int L_WIDTH = 1;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 15;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 8;

	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	/**
	 * 画笔对象的引用
	 */
	private final Paint paint;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;
	private boolean isFirst;
	private static final int MAX_RESULT_POINTS = 20;
	private CameraManager cameraManager;
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;
	private final int resultPointColor;
	private List<ResultPoint> possibleResultPoints;
	private List<ResultPoint> lastPossibleResultPoints;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Initialize these once for performance rather than calling them every
		// time in onDraw().
        ViewfinderView.density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
        this.ScreenRate = (int) (20 * ViewfinderView.density);
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = this.getResources();
        this.maskColor = resources.getColor(R.color.viewfinder_mask);
        this.resultColor = resources.getColor(R.color.result_view);

        this.resultPointColor = resources.getColor(R.color.possible_result_points);
        this.possibleResultPoints = new ArrayList<ResultPoint>(5);
        this.lastPossibleResultPoints = null;
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		if (this.cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		Rect frame = this.cameraManager.getFramingRect();
		Rect previewFrame = this.cameraManager.getFramingRectInPreview();
		if (frame == null || previewFrame == null) {
			return;
		}
		// 初始化中间线滑动的最上边和最下边
		if (!this.isFirst) {
            this.isFirst = true;
            this.slideTop = frame.top - 5;
		}
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		/*
		 * 预览界面的绘制
		 */
        this.paint.setColor(this.resultBitmap != null ? this.resultColor : this.maskColor);
		canvas.drawRect(0, 0, width, frame.top - 1, this.paint);
		canvas.drawRect(0, frame.top - 1, frame.left - 1, frame.bottom + 1,
                this.paint);
		canvas.drawRect(frame.right + 1, frame.top - 1, width,
				frame.bottom + 1, this.paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, this.paint);

		if (this.resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
            this.paint.setAlpha(ViewfinderView.OPAQUE);
			// canvas.drawBitmap(resultBitmap, null, frame, paint);
			canvas.drawBitmap(this.resultBitmap, frame.left, frame.top, this.paint);
		} else {

			// 画扫预览灰线框，共4个部分
            this.paint.setColor(Color.GRAY);
			canvas.drawRect(frame.left, frame.top, frame.right, frame.top
					+ ViewfinderView.L_WIDTH, this.paint);
			canvas.drawRect(frame.left, frame.top, frame.left + ViewfinderView.L_WIDTH,
					frame.bottom, this.paint);
			canvas.drawRect(frame.left, frame.bottom - ViewfinderView.L_WIDTH, frame.right,
					frame.bottom, this.paint);
			canvas.drawRect(frame.right - ViewfinderView.L_WIDTH, frame.top, frame.right,
					frame.bottom, this.paint);
			// 画扫描框边上的角，总共8个部分
            this.paint.setColor(Color.GREEN);
			canvas.drawRect(frame.left, frame.top, frame.left + this.ScreenRate,
					frame.top + ViewfinderView.CORNER_WIDTH, this.paint);
			canvas.drawRect(frame.left, frame.top, frame.left + ViewfinderView.CORNER_WIDTH,
					frame.top + this.ScreenRate, this.paint);
			canvas.drawRect(frame.right - this.ScreenRate, frame.top, frame.right,
					frame.top + ViewfinderView.CORNER_WIDTH, this.paint);
			canvas.drawRect(frame.right - ViewfinderView.CORNER_WIDTH, frame.top, frame.right,
					frame.top + this.ScreenRate, this.paint);
			canvas.drawRect(frame.left, frame.bottom - ViewfinderView.CORNER_WIDTH, frame.left
					+ this.ScreenRate, frame.bottom, this.paint);
			canvas.drawRect(frame.left, frame.bottom - this.ScreenRate, frame.left
					+ ViewfinderView.CORNER_WIDTH, frame.bottom, this.paint);
			canvas.drawRect(frame.right - this.ScreenRate, frame.bottom
					- ViewfinderView.CORNER_WIDTH, frame.right, frame.bottom, this.paint);
			canvas.drawRect(frame.right - ViewfinderView.CORNER_WIDTH, frame.bottom
					- this.ScreenRate, frame.right, frame.bottom, this.paint);

			// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            this.slideTop += ViewfinderView.SPEEN_DISTANCE;
			if (this.slideTop >= frame.bottom - 10) {
                this.slideTop = frame.top - 5;
			}
			Rect lineRect = new Rect();
			lineRect.left = frame.left;
			lineRect.right = frame.right;
			lineRect.top = this.slideTop;
			lineRect.bottom = this.slideTop + ViewfinderView.MIDDLE_LINE_WIDTH;// 扫描线的宽度15
			canvas.drawBitmap(((BitmapDrawable) this.getResources()
					.getDrawable(R.drawable.qrcode_scan_line)).getBitmap(),
					null, lineRect, this.paint);
            this.paint.setColor(Color.WHITE);
            this.paint.setTextSize(15 * ViewfinderView.density);
            this.paint.setAlpha(0xee);
            this.paint.setTypeface(Typeface.create("System", Typeface.BOLD));

			List<ResultPoint> currentPossible = this.possibleResultPoints;
			Collection<ResultPoint> currentLast = this.lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
                this.lastPossibleResultPoints = null;
			} else {
                this.possibleResultPoints = new ArrayList<ResultPoint>(5);
                this.lastPossibleResultPoints = currentPossible;
                this.paint.setAlpha(ViewfinderView.OPAQUE);
                this.paint.setColor(this.resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, this.paint);
				}
			}
			if (currentLast != null) {
                this.paint.setAlpha(ViewfinderView.OPAQUE / 2);
                this.paint.setColor(this.resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, this.paint);
				}
			}
			// 刷新界面区域
            this.postInvalidateDelayed(ViewfinderView.ANIMATION_DELAY, 0, 0, width, height);
		}
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
        this.invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        this.invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = this.possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > ViewfinderView.MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - ViewfinderView.MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

}
