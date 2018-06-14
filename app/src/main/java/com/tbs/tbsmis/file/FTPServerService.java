/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.ftp.Defaults;
import com.tbs.tbsmis.ftp.Globals;
import com.tbs.tbsmis.ftp.MyLog;
import com.tbs.tbsmis.ftp.ProxyConnector;
import com.tbs.tbsmis.ftp.SessionThread;
import com.tbs.tbsmis.ftp.TcpListener;
import com.tbs.tbsmis.ftp.UiUpdater;
import com.tbs.tbsmis.ftp.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FTPServerService extends Service implements Runnable {
    protected static Thread serverThread;

    protected boolean shouldExit;

    protected MyLog myLog = new MyLog(this.getClass().getName());

    protected static MyLog staticLog = new MyLog(FTPServerService.class.getName());

    public static final int BACKLOG = 21;

    public static final int MAX_SESSIONS = 5;

    public static final String WAKE_LOCK_TAG = "SwiFTP";

    // protected ServerSocketChannel wifiSocket;
    protected ServerSocket listenSocket;

    protected static WifiManager.WifiLock wifiLock;

    // protected static InetAddress serverAddress = null;

    protected static List<String> sessionMonitor = new ArrayList<String>();

    protected static List<String> serverLog = new ArrayList<String>();

    protected static int uiLogLevel = Defaults.getUiLogLevel();

    // The server thread will check this often to look for incoming
    // connections. We are forced to use non-blocking accept() and polling
    // because we cannot wait forever in accept() if we want to be able
    // to receive an exit signal and cleanly exit.
    public static final int WAKE_INTERVAL_MS = 1000; // milliseconds

    protected static int port;

    protected static boolean acceptWifi;

    protected static boolean acceptNet;

    protected static boolean fullWake;

    private TcpListener wifiListener;

    private ProxyConnector proxyConnector;

    private final List<SessionThread> sessionThreads = new ArrayList<SessionThread>();

    private static SharedPreferences settings;

    WakeLock wakeLock;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) && FTPServerService.isRunning()) {
                FTPServerService.this.stopSelf();
            }
        }
    };

    public FTPServerService() {
    }

    @Override
	public IBinder onBind(Intent intent) {
        // We don't implement this functionality, so ignore it
        return null;
    }

    @Override
	public void onCreate() {
        this.myLog.l(Log.DEBUG, "SwiFTP server created");
        // Set the application-wide context global, if not already set
        Context myContext = Globals.getContext();
        if (myContext == null) {
            myContext = this.getApplicationContext();
            if (myContext != null) {
                Globals.setContext(myContext);
            }
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        this.registerReceiver(this.mReceiver, intentFilter);
        return;
    }

    @Override
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        this.shouldExit = false;
        int attempts = 10;
        // The previous server thread may still be cleaning up, wait for it
        // to finish.
        while (FTPServerService.serverThread != null) {
            this.myLog.l(Log.WARN, "Won't start, server thread exists");
            if (attempts > 0) {
                attempts--;
                Util.sleepIgnoreInterupt(1000);
            } else {
                this.myLog.l(Log.ERROR, "Server thread already exists");
                return;
            }
        }
        this.myLog.l(Log.DEBUG, "Creating server thread");
        FTPServerService.serverThread = new Thread(this);
        FTPServerService.serverThread.start();

        // todo: we should broadcast an intent to inform anyone who cares
    }

    public static boolean isRunning() {
        // return true if and only if a server Thread is running
        if (FTPServerService.serverThread == null) {
            FTPServerService.staticLog.l(Log.DEBUG, "Server is not running (null serverThread)");
            return false;
        }
        if (!FTPServerService.serverThread.isAlive()) {
            FTPServerService.staticLog.l(Log.DEBUG, "serverThread non-null but !isAlive()");
        } else {
            FTPServerService.staticLog.l(Log.DEBUG, "Server is alive");
        }
        return true;
    }

    @Override
	public void onDestroy() {
        this.myLog.l(Log.INFO, "onDestroy() Stopping server");
        this.shouldExit = true;
        if (FTPServerService.serverThread == null) {
            this.myLog.l(Log.WARN, "Stopping with null serverThread");
            return;
        } else {
            FTPServerService.serverThread.interrupt();
            try {
                FTPServerService.serverThread.join(10000); // wait 10 sec for server thread to
                // finish
            } catch (InterruptedException e) {
            }
            if (FTPServerService.serverThread.isAlive()) {
                this.myLog.l(Log.WARN, "Server thread failed to exit");
                // it may still exit eventually if we just leave the
                // shouldExit flag set
            } else {
                this.myLog.d("serverThread join()ed ok");
                FTPServerService.serverThread = null;
            }
        }
        try {
            if (this.listenSocket != null) {
                this.myLog.l(Log.INFO, "Closing listenSocket");
                this.listenSocket.close();
            }
        } catch (IOException e) {
        }

        UiUpdater.updateClients();
        if (FTPServerService.wifiLock != null) {
            FTPServerService.wifiLock.release();
            FTPServerService.wifiLock = null;
        }
        this.clearNotification();
        this.unregisterReceiver(this.mReceiver);
        this.myLog.d("FTPServerService.onDestroy() finished");
    }

    private boolean loadSettings() {
        this.myLog.l(Log.DEBUG, "Loading settings");
        FTPServerService.settings = this.getSharedPreferences(Defaults.getSettingsName(), Defaults.getSettingsMode());
        FTPServerService.port = FTPServerService.settings.getInt("portNum", Defaults.portNumber);
        if (FTPServerService.port == 0) {
            // If port number from settings is invalid, use the default
            FTPServerService.port = Defaults.portNumber;
        }
        this.myLog.l(Log.DEBUG, "Using port " + FTPServerService.port);

        FTPServerService.acceptNet = false;
        FTPServerService.acceptWifi = true;
        FTPServerService.fullWake = false;

        return true;
    }

    // This opens a listening socket on all interfaces.
    void setupListener() throws IOException {
        this.listenSocket = new ServerSocket();
        this.listenSocket.setReuseAddress(true);
        this.listenSocket.bind(new InetSocketAddress(FTPServerService.port));
    }

    @SuppressWarnings("deprecation")
	private void setupNotification() {
        // http://developer.android.com/guide/topics/ui/notifiers/notifications.html

        // Instantiate a Notification
        //int icon = R.drawable.notification;
        CharSequence tickerText = this.getString(R.string.notif_server_starting);
        //Notification notification = new Notification(icon, tickerText, when);
        Notification notification;
        Notification.Builder builder1 = new Notification.Builder(this);
        builder1.setSmallIcon(R.drawable.notification); //设置图标
        builder1.setTicker(tickerText);
        //builder1.setContentTitle(title); //设置标题
       // builder1.setContentText(mContext.getString(R.string.downloading_msg)); //消息内容
        builder1.setWhen(System.currentTimeMillis()); //发送时间
        //builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder1.setAutoCancel(true);//打开程序后图标消失

        // Define Notification's message and Intent
        CharSequence contentTitle = this.getString(R.string.notif_title);
        CharSequence contentText = "";
        InetAddress address = getWifiIp();
        if (address != null) {
            String port = ":" + getPort();
            contentText = "ftp://" + address.getHostAddress() + (getPort() == 21 ? "" : port);
        }

        Intent notificationIntent = new Intent(this, FileExplorerTabActivity.class);
        notificationIntent.putExtra(GlobalConsts.INTENT_EXTRA_TAB, 2);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder1.setContentIntent(contentIntent);
        notification = builder1.build();
        //notification.setLatestEventInfo(getApplicationContext(), contentTitle, contentText, contentIntent);
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        this.startForeground(123453, notification);

        this.myLog.d("Notication setup done");
    }

    private void clearNotification() {
        this.stopForeground(true);
        this.myLog.d("Cleared notification");
    }

    private boolean safeSetupListener() {
        try {
            this.setupListener();
        } catch (IOException e) {
            this.myLog.l(Log.WARN, "Error opening port, check your network connection.");
            return false;
        }

        return true;
    }

    @Override
	public void run() {
        // The UI will want to check the server status to update its
        // start/stop server button
        int consecutiveProxyStartFailures = 0;
        long proxyStartMillis = 0;

        UiUpdater.updateClients();

        this.myLog.l(Log.DEBUG, "Server thread running");

        // set our members according to user preferences
        if (!this.loadSettings()) {
            // loadSettings returns false if settings are not sane
            this.cleanupAndStopService();
            return;
        }

        // Initialization of wifi
        if (FTPServerService.acceptWifi) {
            // If configured to accept connections via wifi, then set up the
            // socket
            int maxTry = 10;
            int atmp = 0;
            while (!this.safeSetupListener() && ++atmp < maxTry) {
                FTPServerService.port += 1;
            }

            if (atmp >= maxTry) {
                // serverAddress = null;
                this.cleanupAndStopService();
                return;
            }
            this.takeWifiLock();
        }
        this.takeWakeLock();

        this.myLog.l(Log.INFO, "SwiFTP server ready");
        this.setupNotification();

        // We should update the UI now that we have a socket open, so the UI
        // can present the URL
        UiUpdater.updateClients();

        while (!this.shouldExit) {
            if (FTPServerService.acceptWifi) {
                if (this.wifiListener != null) {
                    if (!this.wifiListener.isAlive()) {
                        this.myLog.l(Log.DEBUG, "Joining crashed wifiListener thread");
                        try {
                            this.wifiListener.join();
                        } catch (InterruptedException e) {
                        }
                        this.wifiListener = null;
                    }
                }
                if (this.wifiListener == null) {
                    // Either our wifi listener hasn't been created yet, or has
                    // crashed,
                    // so spawn it
                    this.wifiListener = new TcpListener(this.listenSocket, this);
                    this.wifiListener.start();
                }
            }
            if (FTPServerService.acceptNet) {
                if (this.proxyConnector != null) {
                    if (!this.proxyConnector.isAlive()) {
                        this.myLog.l(Log.DEBUG, "Joining crashed proxy connector");
                        try {
                            this.proxyConnector.join();
                        } catch (InterruptedException e) {
                        }
                        this.proxyConnector = null;
                        long nowMillis = new Date().getTime();
                        // myLog.l(Log.DEBUG,
                        // "Now:"+nowMillis+" start:"+proxyStartMillis);
                        if (nowMillis - proxyStartMillis < 3000) {
                            // We assume that if the proxy thread crashed within
                            // 3
                            // seconds of starting, it was a startup or
                            // connection
                            // failure.
                            this.myLog.l(Log.DEBUG, "Incrementing proxy start failures");
                            consecutiveProxyStartFailures++;
                        } else {
                            // Otherwise assume the proxy started successfully
                            // and
                            // crashed later.
                            this.myLog.l(Log.DEBUG, "Resetting proxy start failures");
                            consecutiveProxyStartFailures = 0;
                        }
                    }
                }
                if (this.proxyConnector == null) {
                    long nowMillis = new Date().getTime();
                    boolean shouldStartListener = false;
                    // We want to restart the proxy listener without much delay
                    // for the first few attempts, but add a much longer delay
                    // if we consistently fail to connect.
                    if (consecutiveProxyStartFailures < 3 && nowMillis - proxyStartMillis > 5000) {
                        // Retry every 5 seconds for the first 3 tries
                        shouldStartListener = true;
                    } else if (nowMillis - proxyStartMillis > 30000) {
                        // After the first 3 tries, only retry once per 30 sec
                        shouldStartListener = true;
                    }
                    if (shouldStartListener) {
                        this.myLog.l(Log.DEBUG, "Spawning ProxyConnector");
                        this.proxyConnector = new ProxyConnector(this);
                        this.proxyConnector.start();
                        proxyStartMillis = nowMillis;
                    }
                }
            }
            try {
                // todo: think about using ServerSocket, and just closing
                // the main socket to send an exit signal
                Thread.sleep(FTPServerService.WAKE_INTERVAL_MS);
            } catch (InterruptedException e) {
                this.myLog.l(Log.DEBUG, "Thread interrupted");
            }
        }

        this.terminateAllSessions();

        if (this.proxyConnector != null) {
            this.proxyConnector.quit();
            this.proxyConnector = null;
        }
        if (this.wifiListener != null) {
            this.wifiListener.quit();
            this.wifiListener = null;
        }
        this.shouldExit = false; // we handled the exit flag, so reset it to
        // acknowledge
        this.myLog.l(Log.DEBUG, "Exiting cleanly, returning from run()");
        this.clearNotification();
        this.releaseWakeLock();
        this.releaseWifiLock();
    }

    private void terminateAllSessions() {
        this.myLog.i("Terminating " + this.sessionThreads.size() + " session thread(s)");
        synchronized (this) {
            for (SessionThread sessionThread : this.sessionThreads) {
                if (sessionThread != null) {
                    sessionThread.closeDataSocket();
                    sessionThread.closeSocket();
                }
            }
        }
    }

    public void cleanupAndStopService() {
        // Call the Android Service shutdown function
        Context context = this.getApplicationContext();
        Intent intent = new Intent(context, FTPServerService.class);
        context.stopService(intent);
        this.releaseWifiLock();
        this.releaseWakeLock();
        this.clearNotification();
    }

    private void takeWakeLock() {
        if (this.wakeLock == null) {
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);

            // Many (all?) devices seem to not properly honor a
            // PARTIAL_WAKE_LOCK,
            // which should prevent CPU throttling. This has been
            // well-complained-about on android-developers.
            // For these devices, we have a config option to force the phone
            // into a
            // full wake lock.
            if (FTPServerService.fullWake) {
                this.wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, FTPServerService.WAKE_LOCK_TAG);
            } else {
                this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, FTPServerService.WAKE_LOCK_TAG);
            }
            this.wakeLock.setReferenceCounted(false);
        }
        this.myLog.d("Acquiring wake lock");
        this.wakeLock.acquire();
    }

    private void releaseWakeLock() {
        this.myLog.d("Releasing wake lock");
        if (this.wakeLock != null) {
            this.wakeLock.release();
            this.wakeLock = null;
            this.myLog.d("Finished releasing wake lock");
        } else {
            this.myLog.i("Couldn't release null wake lock");
        }
    }

    private void takeWifiLock() {
        this.myLog.d("Taking wifi lock");
        if (FTPServerService.wifiLock == null) {
            WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            FTPServerService.wifiLock = manager.createWifiLock("SwiFTP");
            FTPServerService.wifiLock.setReferenceCounted(false);
        }
        FTPServerService.wifiLock.acquire();
    }

    private void releaseWifiLock() {
        this.myLog.d("Releasing wifi lock");
        if (FTPServerService.wifiLock != null) {
            FTPServerService.wifiLock.release();
            FTPServerService.wifiLock = null;
        }
    }

    public void errorShutdown() {
        this.myLog.l(Log.ERROR, "Service errorShutdown() called");
        this.cleanupAndStopService();
    }

    /**
     * Gets the IP address of the wifi connection.
     *
     * @return The integer IP address if wifi enabled, or null if not.
     */
    public static InetAddress getWifiIp() {
        Context myContext = Globals.getContext();
        if (myContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (FTPServerService.isWifiEnabled()) {
            int ipAsInt = wifiMgr.getConnectionInfo().getIpAddress();
            if (ipAsInt == 0) {
                return null;
            } else {
                return Util.intToInet(ipAsInt);
            }
        } else {
            return null;
        }
    }

    public static boolean isWifiEnabled() {
        Context myContext = Globals.getContext();
        if (myContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }

    public static List<String> getSessionMonitorContents() {
        return new ArrayList<String>(FTPServerService.sessionMonitor);
    }

    public static List<String> getServerLogContents() {
        return new ArrayList<String>(FTPServerService.serverLog);
    }

    public static void log(int msgLevel, String s) {
        FTPServerService.serverLog.add(s);
        int maxSize = Defaults.getServerLogScrollBack();
        while (FTPServerService.serverLog.size() > maxSize) {
            FTPServerService.serverLog.remove(0);
        }
        // updateClients();
    }

    public static void updateClients() {
        UiUpdater.updateClients();
    }

    public static void writeMonitor(boolean incoming, String s) {
    }

    // public static void writeMonitor(boolean incoming, String s) {
    // if(incoming) {
    // s = "> " + s;
    // } else {
    // s = "< " + s;
    // }
    // sessionMonitor.add(s.trim());
    // int maxSize = Defaults.getSessionMonitorScrollBack();
    // while(sessionMonitor.size() > maxSize) {
    // sessionMonitor.remove(0);
    // }
    // updateClients();
    // }

    public static int getPort() {
        return FTPServerService.port;
    }

    public static void setPort(int port) {
        FTPServerService.port = port;
    }

    /**
     * The FTPServerService must know about all running session threads so they
     * can be terminated on exit. Called when a new session is created.
     */
    public void registerSessionThread(SessionThread newSession) {
        // Before adding the new session thread, clean up any finished session
        // threads that are present in the list.

        // Since we're not allowed to modify the list while iterating over
        // it, we construct a list in toBeRemoved of threads to remove
        // later from the sessionThreads list.
        synchronized (this) {
            List<SessionThread> toBeRemoved = new ArrayList<SessionThread>();
            for (SessionThread sessionThread : this.sessionThreads) {
                if (!sessionThread.isAlive()) {
                    this.myLog.l(Log.DEBUG, "Cleaning up finished session...");
                    try {
                        sessionThread.join();
                        this.myLog.l(Log.DEBUG, "Thread joined");
                        toBeRemoved.add(sessionThread);
                        sessionThread.closeSocket(); // make sure socket closed
                    } catch (InterruptedException e) {
                        this.myLog.l(Log.DEBUG, "Interrupted while joining");
                        // We will try again in the next loop iteration
                    }
                }
            }
            for (SessionThread removeThread : toBeRemoved) {
                this.sessionThreads.remove(removeThread);
            }

            // Cleanup is complete. Now actually add the new thread to the list.
            this.sessionThreads.add(newSession);
        }
        this.myLog.d("Registered session thread");
    }

    /** Get the ProxyConnector, may return null if proxying is disabled. */
    public ProxyConnector getProxyConnector() {
        return this.proxyConnector;
    }

    public static SharedPreferences getSettings() {
        return FTPServerService.settings;
    }
}
