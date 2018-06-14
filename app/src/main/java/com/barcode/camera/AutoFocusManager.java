package com.barcode.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

import com.barcode.executor.AsyncTaskExecInterface;
import com.barcode.executor.AsyncTaskExecManager;

/**
 * 自动对焦管理
 * @author 火蚁（http://my.oschina/LittleDY）
 * @created 2014-03-14
 */
final class AutoFocusManager implements AutoFocusCallback {

	private static final String TAG = AutoFocusManager.class.getSimpleName();

	private static final long AUTO_FOCUS_INTERVAL_MS = 2000L;
	private static final Collection<String> FOCUS_MODES_CALLING_AF;
	static {
		FOCUS_MODES_CALLING_AF = new ArrayList<String>(2);
        AutoFocusManager.FOCUS_MODES_CALLING_AF.add(Parameters.FOCUS_MODE_AUTO);
        AutoFocusManager.FOCUS_MODES_CALLING_AF.add(Parameters.FOCUS_MODE_MACRO);
	}

	private boolean active;
	private final boolean useAutoFocus;
	private final Camera camera;
	private AutoFocusManager.AutoFocusTask outstandingTask;
	private final AsyncTaskExecInterface taskExec;

	AutoFocusManager(Context context, Camera camera) {
		this.camera = camera;
        this.taskExec = new AsyncTaskExecManager().build();
		String currentFocusMode = camera.getParameters().getFocusMode();
        this.useAutoFocus = true && AutoFocusManager.FOCUS_MODES_CALLING_AF.contains(currentFocusMode);
		Log.i(AutoFocusManager.TAG, "Current focus mode '" + currentFocusMode+ "'; use auto focus? " + this.useAutoFocus);
        this.start();
	}

	@Override
	public synchronized void onAutoFocus(boolean success, Camera theCamera) {
		if (this.active) {
            this.outstandingTask = new AutoFocusManager.AutoFocusTask();
            this.taskExec.execute(this.outstandingTask);
		}
	}

	synchronized void start() {
		if (this.useAutoFocus) {
            this.active = true;
			try {
                this.camera.autoFocus(this);
			} catch (RuntimeException re) {
				// Have heard RuntimeException reported in Android 4.0.x+;
				// continue?
				Log.w(AutoFocusManager.TAG, "Unexpected exception while focusing", re);
			}
		}
	}

	synchronized void stop() {
		if (this.useAutoFocus) {
			try {
                this.camera.cancelAutoFocus();
			} catch (RuntimeException re) {
				Log.w(AutoFocusManager.TAG, "Unexpected exception while cancelling focusing", re);
			}
		}
		if (this.outstandingTask != null) {
            this.outstandingTask.cancel(true);
            this.outstandingTask = null;
		}
        this.active = false;
	}

	private final class AutoFocusTask extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... voids) {
			try {
				Thread.sleep(AutoFocusManager.AUTO_FOCUS_INTERVAL_MS);
			} catch (InterruptedException e) {
				// continue
			}
			synchronized (AutoFocusManager.this) {
				if (AutoFocusManager.this.active) {
                    AutoFocusManager.this.start();
				}
			}
			return null;
		}
	}

}
