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

package com.barcode.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import com.google.zxing.PlanarYUVLuminanceSource;

import java.io.IOException;

/**
 * 相机管理类
 * 
 */
public final class CameraManager {

	private static final String TAG = CameraManager.class.getSimpleName();

	// private static final int MIN_FRAME_WIDTH = 350;
	// private static final int MIN_FRAME_HEIGHT = 240;
	// private static final int MAX_FRAME_WIDTH = 420;
	// private static final int MAX_FRAME_HEIGHT = 400;

	private static final int MIN_FRAME_WIDTH = 240;
	// private static final int MIN_FRAME_HEIGHT = 240;
	private static final int MAX_FRAME_WIDTH = 420;

	private final Context context;
	private final CameraConfigurationManager configManager;
	private Camera camera;
	private AutoFocusManager autoFocusManager;
	private Rect framingRect;
	private Rect framingRectInPreview;
	private boolean initialized;
	private boolean previewing;
	private int requestedFramingRectWidth;
	private int requestedFramingRectHeight;
	/**
	 * Preview frames are delivered here, which we pass on to the registered
	 * handler. Make sure to clear the handler so it will only receive one
	 * message.
	 */
	private final PreviewCallback previewCallback;

	public CameraManager(Context context) {
		this.context = context;
        configManager = new CameraConfigurationManager(context);
        this.previewCallback = new PreviewCallback(this.configManager);
	}

	/**
	 * Opens the camera driver and initializes the hardware parameters.
	 * 
	 * @param holder
	 *            The surface object which the camera will draw preview frames
	 *            into.
	 * @throws IOException
	 *             Indicates the camera driver failed to open.
	 */
	public synchronized void openDriver(SurfaceHolder holder)
			throws IOException {
		Camera theCamera = this.camera;
		if (theCamera == null) {
			theCamera = new OpenCameraManager().build().open();
			if (theCamera == null) {
				throw new IOException();
			}
            this.camera = theCamera;
		}
		theCamera.setPreviewDisplay(holder);

		if (!this.initialized) {
            this.initialized = true;
            this.configManager.initFromCameraParameters(theCamera);
			if (this.requestedFramingRectWidth > 0 && this.requestedFramingRectHeight > 0) {
                this.setManualFramingRect(this.requestedFramingRectWidth,
                        this.requestedFramingRectHeight);
                this.requestedFramingRectWidth = 0;
                this.requestedFramingRectHeight = 0;
			}
		}

		Parameters parameters = theCamera.getParameters();
		String parametersFlattened = parameters == null ? null : parameters
				.flatten(); // Save these, temporarily
		try {
            this.configManager.setDesiredCameraParameters(theCamera, false);
		} catch (RuntimeException re) {
			// Driver failed
			Log.w(CameraManager.TAG,
					"Camera rejected parameters. Setting only minimal safe-mode parameters");
			Log.i(CameraManager.TAG, "Resetting to saved camera params: "
					+ parametersFlattened);
			// Reset:
			if (parametersFlattened != null) {
				parameters = theCamera.getParameters();
				parameters.unflatten(parametersFlattened);
				try {
					theCamera.setParameters(parameters);
                    this.configManager.setDesiredCameraParameters(theCamera, true);
				} catch (RuntimeException re2) {
					// Well, darn. Give up
					Log.w(CameraManager.TAG,
							"Camera rejected even safe-mode parameters! No configuration");
				}
			}
		}

	}

	public synchronized boolean isOpen() {
		return this.camera != null;
	}

	/**
	 * Closes the camera driver if still in use.
	 */
	public synchronized void closeDriver() {
		if (this.camera != null) {
            this.camera.release();
            this.camera = null;
			// Make sure to clear these each time we close the camera, so that
			// any scanning rect
			// requested by intent is forgotten.
            this.framingRect = null;
            this.framingRectInPreview = null;
		}
	}

	/**
	 * Asks the camera hardware to begin drawing preview frames to the screen.
	 */
	public synchronized void startPreview() {
		Camera theCamera = this.camera;
		if (theCamera != null && !this.previewing) {
			theCamera.startPreview();
            this.previewing = true;
            this.autoFocusManager = new AutoFocusManager(this.context, this.camera);
		}
	}

	/**
	 * Tells the camera to stop drawing preview frames.
	 */
	public synchronized void stopPreview() {
		if (this.autoFocusManager != null) {
            this.autoFocusManager.stop();
            this.autoFocusManager = null;
		}
		if (this.camera != null && this.previewing) {
            this.camera.stopPreview();
            this.previewCallback.setHandler(null, 0);
            this.previewing = false;
		}
	}

	/**
	 * Convenience method for {@link com.barcode.core.Capture}
	 */
	public synchronized void setTorch(boolean newSetting) {
		if (this.camera != null) {
			if (this.autoFocusManager != null) {
                this.autoFocusManager.stop();
			}
            this.configManager.setTorch(this.camera, newSetting);
			if (this.autoFocusManager != null) {
                this.autoFocusManager.start();
			}
		}
	}

	/**
	 * A single preview frame will be returned to the handler supplied. The data
	 * will arrive as byte[] in the message.obj field, with width and height
	 * encoded as message.arg1 and message.arg2, respectively.
	 * 
	 * @param handler
	 *            The handler to send the message to.
	 * @param message
	 *            The what field of the message to be sent.
	 */
	public synchronized void requestPreviewFrame(Handler handler, int message) {
		Camera theCamera = this.camera;
		if (theCamera != null && this.previewing) {
            this.previewCallback.setHandler(handler, message);
			theCamera.setOneShotPreviewCallback(this.previewCallback);
		}
	}

	/**
	 * 动态获得扫描框的大小 Calculates the framing rect which the UI should draw to show
	 * the user where to place the barcode. This target helps with alignment as
	 * well as forces the user to hold the device far enough away to ensure the
	 * image will be in focus.
	 * 
	 * @return The rectangle to draw on screen in window coordinates.
	 */
	public synchronized Rect getFramingRect() {
		if (this.framingRect == null) {
			if (this.camera == null) {
				return null;
			}
			Point screenResolution = this.configManager.getScreenResolution();

			if (screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			// 计算扫描框的宽高
			DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();

			int width = (int) (metrics.widthPixels * 0.6);

			int height = width;
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
			// int width = screenResolution.x * 3 / 4;
			// if (width < MIN_FRAME_WIDTH) {
			// width = MIN_FRAME_WIDTH;
			// } else if (width > MAX_FRAME_WIDTH) {
			// width = MAX_FRAME_WIDTH;
			// }
			// int height = width;
			// int leftOffset = (screenResolution.x - width) / 2;
			// int topOffset = (screenResolution.y - width) / 2;
            this.framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
		}
		return this.framingRect;
	}

	/**
	 * Like {@link #getFramingRect} but coordinates are in terms of the preview
	 * frame, not UI / screen.
	 */
	public synchronized Rect getFramingRectInPreview() {
		if (this.framingRectInPreview == null) {
			Rect framingRect = this.getFramingRect();
			if (framingRect == null) {
				return null;
			}
			Rect rect = new Rect(framingRect);
			Point cameraResolution = this.configManager.getCameraResolution();
			Point screenResolution = this.configManager.getScreenResolution();
			if (cameraResolution == null || screenResolution == null) {
				// Called early, before init even finished
				return null;
			}
			rect.left = rect.left * cameraResolution.y / screenResolution.x;
			rect.right = rect.right * cameraResolution.y / screenResolution.x;
			rect.top = rect.top * cameraResolution.x / screenResolution.y;
			rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            this.framingRectInPreview = rect;
		}
		return this.framingRectInPreview;
	}

	/**
	 * Allows third party apps to specify the scanning rectangle dimensions,
	 * rather than determine them automatically based on screen resolution.
	 * 
	 * @param width
	 *            The width in pixels to scan.
	 * @param height
	 *            The height in pixels to scan.
	 */
	public synchronized void setManualFramingRect(int width, int height) {
		if (this.initialized) {
			Point screenResolution = this.configManager.getScreenResolution();
			if (width > screenResolution.x) {
				width = screenResolution.x;
			}
			if (height > screenResolution.y) {
				height = screenResolution.y;
			}
			int leftOffset = (screenResolution.x - width) / 2;
			int topOffset = (screenResolution.y - height) / 2;
            this.framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
					topOffset + height);
			Log.d(CameraManager.TAG, "Calculated manual framing rect: " + this.framingRect);
            this.framingRectInPreview = null;
		} else {
            this.requestedFramingRectWidth = width;
            this.requestedFramingRectHeight = height;
		}
	}

	/**
	 * A factory method to build the appropriate LuminanceSource object based on
	 * the format of the preview buffers, as described by Camera.Parameters.
	 * 
	 * @param data
	 *            A preview frame.
	 * @param width
	 *            The width of the image.
	 * @param height
	 *            The height of the image.
	 * @return A PlanarYUVLuminanceSource instance.
	 */
	public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
			int width, int height) {
		Rect rect = this.getFramingRectInPreview();
		if (rect == null) {
			return null;
		}
		// Go ahead and assume it's YUV rather than die.
		return new PlanarYUVLuminanceSource(data, width, height, rect.left,
				rect.top, rect.width(), rect.height(), false);
	}

}
