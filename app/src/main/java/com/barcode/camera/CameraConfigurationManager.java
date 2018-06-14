package com.barcode.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 相机配置管理类
 * 
 */
final class CameraConfigurationManager {

	private static final String TAG = "CameraConfiguration";

	// This is bigger than the size of a small screen, which is still supported.
	// The routine
	// below will still select the default (presumably 320x240) size for these.
	// This prevents
	// accidental selection of very low resolution on some devices.
	private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
	private static final int MAX_PREVIEW_PIXELS = 1280 * 720;

	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	void initFromCameraParameters(Camera camera) {
		Parameters parameters = camera.getParameters();

		WindowManager manager = (WindowManager) this.context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		@SuppressWarnings("deprecation")
		int height = display.getHeight();
        this.screenResolution = new Point(width, height);
		Log.i(CameraConfigurationManager.TAG, "Screen resolution: " + this.screenResolution);
        this.cameraResolution = this.findBestPreviewSizeValue(parameters,
                this.screenResolution);
		// 解决竖屏拉伸
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = this.screenResolution.x;
		screenResolutionForCamera.y = this.screenResolution.y;
		if (this.screenResolution.x < this.screenResolution.y) {
			screenResolutionForCamera.x = this.screenResolution.y;
			screenResolutionForCamera.y = this.screenResolution.x;
		}
		Log.i(CameraConfigurationManager.TAG, "Camera resolution: " + this.cameraResolution);
	}

	@SuppressLint("NewApi")
	void setDesiredCameraParameters(Camera camera, boolean safeMode) {
		camera.setDisplayOrientation(90);
		Parameters parameters = camera.getParameters();

		if (parameters == null) {
			Log.w(CameraConfigurationManager.TAG,
					"Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}

		Log.i(CameraConfigurationManager.TAG, "Initial camera parameters: " + parameters.flatten());

		if (safeMode) {
			Log.w(CameraConfigurationManager.TAG,
					"In camera config safe mode -- most settings will not be honored");
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this.context);

        this.initializeTorch(parameters, prefs, safeMode);

		String focusMode = null;
		if (true) {
			if (safeMode || false) {
				focusMode = CameraConfigurationManager.findSettableValue(
						parameters.getSupportedFocusModes(),
						Parameters.FOCUS_MODE_AUTO);
			} else {
				focusMode = CameraConfigurationManager.findSettableValue(
						parameters.getSupportedFocusModes(),
						"continuous-picture", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
												// in 4.0+
						"continuous-video", // Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
											// in 4.0+
						Parameters.FOCUS_MODE_AUTO);
			}
		}
		// Maybe selected auto-focus but not available, so fall through here:
		if (!safeMode && focusMode == null) {
			focusMode = CameraConfigurationManager.findSettableValue(parameters.getSupportedFocusModes(),
					Parameters.FOCUS_MODE_MACRO, "edof"); // Camera.Parameters.FOCUS_MODE_EDOF
																	// in 2.2+
		}
		if (focusMode != null) {
			parameters.setFocusMode(focusMode);
		}

		parameters.setPreviewSize(this.cameraResolution.x, this.cameraResolution.y);
		camera.setParameters(parameters);
	}

	Point getCameraResolution() {
		return this.cameraResolution;
	}

	Point getScreenResolution() {
		return this.screenResolution;
	}

	void setTorch(Camera camera, boolean newSetting) {
		Parameters parameters = camera.getParameters();
        this.doSetTorch(parameters, newSetting, false);
		camera.setParameters(parameters);
		boolean currentSetting = false;
		if (currentSetting != newSetting) {
			// SharedPreferences.Editor editor = prefs.edit();
			// editor.putBoolean(PreferencesActivity.KEY_FRONT_LIGHT,
			// newSetting);
			// editor.commit();
		}
	}

	private void initializeTorch(Parameters parameters,
			SharedPreferences prefs, boolean safeMode) {
		boolean currentSetting = false;
        this.doSetTorch(parameters, currentSetting, safeMode);
	}

	private void doSetTorch(Parameters parameters, boolean newSetting,
			boolean safeMode) {
		String flashMode;
		if (newSetting) {
			flashMode = CameraConfigurationManager.findSettableValue(parameters.getSupportedFlashModes(),
					Parameters.FLASH_MODE_TORCH,
					Parameters.FLASH_MODE_ON);
		} else {
			flashMode = CameraConfigurationManager.findSettableValue(parameters.getSupportedFlashModes(),
					Parameters.FLASH_MODE_OFF);
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}
	}

	private Point findBestPreviewSizeValue(Parameters parameters,
			Point screenResolution) {

		List<Size> rawSupportedSizes = parameters
				.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(CameraConfigurationManager.TAG,
					"Device returned no supported preview sizes; using default");
			Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		List<Size> supportedPreviewSizes = new ArrayList<Size>(
				rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Size>() {
			@Override
			public int compare(Size a, Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		if (Log.isLoggable(CameraConfigurationManager.TAG, Log.INFO)) {
			StringBuilder previewSizesString = new StringBuilder();
			for (Size supportedPreviewSize : supportedPreviewSizes) {
				previewSizesString.append(supportedPreviewSize.width)
						.append('x').append(supportedPreviewSize.height)
						.append(' ');
			}
			Log.i(CameraConfigurationManager.TAG, "Supported preview sizes: " + previewSizesString);
		}

		Point bestSize = null;
		float screenAspectRatio = (float) screenResolution.x
				/ (float) screenResolution.y;

		float diff = Float.POSITIVE_INFINITY;
		for (Size supportedPreviewSize : supportedPreviewSizes) {
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			if (pixels < CameraConfigurationManager.MIN_PREVIEW_PIXELS || pixels > CameraConfigurationManager.MAX_PREVIEW_PIXELS) {
				continue;
			}
			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;
			if (maybeFlippedWidth == screenResolution.x
					&& maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				Log.i(CameraConfigurationManager.TAG, "Found preview size exactly matching screen size: "
						+ exactPoint);
				return exactPoint;
			}
			float aspectRatio = (float) maybeFlippedWidth
					/ (float) maybeFlippedHeight;
			float newDiff = Math.abs(aspectRatio - screenAspectRatio);
			if (newDiff < diff) {
				bestSize = new Point(realWidth, realHeight);
				diff = newDiff;
			}
		}

		if (bestSize == null) {
			Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);
			Log.i(CameraConfigurationManager.TAG, "No suitable preview sizes, using default: " + bestSize);
		}

		Log.i(CameraConfigurationManager.TAG, "Found best approximate preview size: " + bestSize);
		return bestSize;
	}

	private static String findSettableValue(Collection<String> supportedValues,
			String... desiredValues) {
		Log.i(CameraConfigurationManager.TAG, "Supported values: " + supportedValues);
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(CameraConfigurationManager.TAG, "Settable value: " + result);
		return result;
	}

}
