/*
 * Copyright (C) 2010 Moduad Co., Ltd.
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
package com.tbs.tbsmis.notification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.util.UIHelper;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Service that continues to run in background and respond to the push
 * notification events from the server. This should be registered as service in
 * AndroidManifest.xml.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class PushMsgService extends Service
{

    private static final String LOGTAG = LogUtil
            .makeLogTag(PushMsgService.class);

    private TelephonyManager telephonyManager;

    private final BroadcastReceiver notificationReceiver;

    private final BroadcastReceiver connectivityReceiver;

    private final PhoneStateListener phoneStateListener;

    private final ExecutorService executorService;

    private final PushMsgService.TaskSubmitter taskSubmitter;

    private final PushMsgService.TaskTracker taskTracker;

    private static XmppManager xmppManager;

    private SharedPreferences sharedPrefs;

    private String deviceId;

    public PushMsgService() {
        this.notificationReceiver = new NotificationReceiver();
        this.connectivityReceiver = new ConnectivityReceiver(this);
        this.phoneStateListener = new PhoneStateChangeListener(this);
        this.executorService = Executors.newSingleThreadExecutor();
        this.taskSubmitter = new PushMsgService.TaskSubmitter(this);
        this.taskTracker = new PushMsgService.TaskTracker(this);
    }

    @Override
    public void onCreate() {
        Log.d(PushMsgService.LOGTAG, "onCreate()...");
        this.telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        this.sharedPrefs = this.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        // Get deviceId
        this.deviceId = UIHelper.DeviceMD5ID(this);
        // Log.d(LOGTAG, "deviceId=" + deviceId);
        SharedPreferences.Editor editor = this.sharedPrefs.edit();
        editor.putString(Constants.DEVICE_ID, this.deviceId);
        // editor.putString(Constants.XMPP_HOST, "168.160.111.135");
        editor.commit();
        // If running on an emulator
        if (this.deviceId == null || this.deviceId.trim().length() == 0
                || this.deviceId.matches("0+")) {
            if (this.sharedPrefs.contains("EMULATOR_DEVICE_ID")) {
                this.deviceId = this.sharedPrefs.getString(Constants.EMULATOR_DEVICE_ID,
                        "");
            } else {
                this.deviceId = new StringBuilder("EMU").append(
                        new Random(System.currentTimeMillis()).nextLong())
                        .toString();
                editor.putString(Constants.EMULATOR_DEVICE_ID, this.deviceId);
                editor.commit();
            }
        }
        Log.d(PushMsgService.LOGTAG, "deviceId=" + this.deviceId);
        PushMsgService.xmppManager = new XmppManager(this);
        this.taskSubmitter.submit(new Runnable()
        {
            @Override
            public void run() {
                start();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = Service.START_STICKY;
        return super.onStartCommand(intent, flags, startId);
        // return START_REDELIVER_INTENT;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(PushMsgService.LOGTAG, "onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(PushMsgService.LOGTAG, "onDestroy()...");
        this.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(PushMsgService.LOGTAG, "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(PushMsgService.LOGTAG, "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(PushMsgService.LOGTAG, "onUnbind()...");
        return true;
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(context.getString(R.string.SERVICE_NAME));//你定义的service的action
        intent.setPackage(context.getPackageName());//这里你需要设置你应用的包名
        //this.stopService(intent);
        return intent;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public PushMsgService.TaskSubmitter getTaskSubmitter() {
        return this.taskSubmitter;
    }

    public PushMsgService.TaskTracker getTaskTracker() {
        return this.taskTracker;
    }

    public static XmppManager getXmppManager() {
        return PushMsgService.xmppManager;
    }

    public SharedPreferences getSharedPreferences() {
        return this.sharedPrefs;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void connect() {
        Log.d(PushMsgService.LOGTAG, "connect()...");
        this.taskSubmitter.submit(new Runnable()
        {
            @Override
            public void run() {
                PushMsgService.getXmppManager().connect();
            }
        });
    }

    public void disconnect() {
        Log.d(PushMsgService.LOGTAG, "disconnect()...");
        this.taskSubmitter.submit(new Runnable()
        {
            @Override
            public void run() {
                PushMsgService.getXmppManager().disconnect();
            }
        });
    }

    private void registerNotificationReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_SHOW_NOTIFICATION);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLICKED);
        filter.addAction(Constants.ACTION_NOTIFICATION_CLEARED);
        this.registerReceiver(this.notificationReceiver, filter);
    }

    private void unregisterNotificationReceiver() {
        try {
            this.unregisterReceiver(this.notificationReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
            } else {
                // unexpected, re-throw
                throw e;
            }
        }

    }

    private void registerConnectivityReceiver() {
        Log.d(PushMsgService.LOGTAG, "registerConnectivityReceiver()...");
        this.telephonyManager.listen(this.phoneStateListener,
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(this.connectivityReceiver, filter);
    }

    private void unregisterConnectivityReceiver() {
        Log.d(PushMsgService.LOGTAG, "unregisterConnectivityReceiver()...");
        this.telephonyManager.listen(this.phoneStateListener,
                PhoneStateListener.LISTEN_NONE);
        try {
            this.unregisterReceiver(this.connectivityReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. This is exactly what is desired
            } else {
                // unexpected, re-throw
                throw e;
            }
        }

    }

    private void start() {
        Log.d(PushMsgService.LOGTAG, "start()...");
        this.registerNotificationReceiver();
        this.registerConnectivityReceiver();
        // Intent intent = getIntent();
        // startService(intent);
        PushMsgService.xmppManager.connect();
    }

    private void stop() {
        Log.d(PushMsgService.LOGTAG, "stop()...");
        this.unregisterNotificationReceiver();
        this.unregisterConnectivityReceiver();
        PushMsgService.xmppManager.disconnect();
        this.executorService.shutdown();
    }

    /**
     * Class for summiting a new runnable task.
     */
    public class TaskSubmitter
    {

        final PushMsgService notificationService;

        public TaskSubmitter(PushMsgService notificationService) {
            this.notificationService = notificationService;
        }

        @SuppressWarnings("rawtypes")
        public Future submit(Runnable task) {
            Future result = null;
            if (!this.notificationService.getExecutorService().isTerminated()
                    && !this.notificationService.getExecutorService().isShutdown()
                    && task != null) {
                result = this.notificationService.getExecutorService().submit(task);
            }
            return result;
        }

    }

    /**
     * Class for monitoring the running task count.
     */
    public class TaskTracker
    {

        final PushMsgService notificationService;

        public int count;

        public TaskTracker(PushMsgService notificationService) {
            this.notificationService = notificationService;
            count = 0;
        }

        public void increase() {
            synchronized (this.notificationService.getTaskTracker()) {
                this.notificationService.getTaskTracker().count++;
                Log.d(PushMsgService.LOGTAG, "Incremented task count to " + this.count);
            }
        }

        public void decrease() {
            synchronized (this.notificationService.getTaskTracker()) {
                this.notificationService.getTaskTracker().count--;
                Log.d(PushMsgService.LOGTAG, "Decremented task count to " + this.count);
            }
        }

    }

}
