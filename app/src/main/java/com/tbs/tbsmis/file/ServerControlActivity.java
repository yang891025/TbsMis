/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tbs.tbsmis.R;
import com.tbs.tbsmis.ftp.Defaults;
import com.tbs.tbsmis.ftp.Globals;
import com.tbs.tbsmis.ftp.MyLog;
import com.tbs.tbsmis.ftp.UiUpdater;

import java.io.File;
import java.net.InetAddress;

@SuppressLint("NewApi")
public class ServerControlActivity extends Fragment implements FileExplorerTabActivity.IBackPressedListener {

    private TextView ipText;

    protected MyLog myLog = new MyLog(getClass().getName());

    @SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // We are being told to do a UI update
                    // If more than one UI update is queued up, we only need to
                    // do one.
                    this.removeMessages(0);
                    ServerControlActivity.this.updateUi();
                    break;
                case 1: // We are being told to display an error message
                    this.removeMessages(1);
            }
        }
    };

    private TextView instructionText;

    private TextView instructionTextPre;

    private View startStopButton;

    private Activity mActivity;

    private View mRootView;

	private TextView title;

	private ImageView finishBtn;

	private ImageView downBtn;

    public ServerControlActivity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mActivity = this.getActivity();
        this.mRootView = inflater.inflate(R.layout.server_control_activity, container, false);

        // Set the application-wide context global, if not already set
        Context myContext = Globals.getContext();
        if (myContext == null) {
            myContext = this.mActivity.getApplicationContext();
            if (myContext == null) {
                throw new NullPointerException("Null context!?!?!?");
            }
            Globals.setContext(myContext);
        }

        this.ipText = (TextView) this.mRootView.findViewById(R.id.ip_address);
        this.instructionText = (TextView) this.mRootView.findViewById(R.id.instruction);
        this.instructionTextPre = (TextView) this.mRootView.findViewById(R.id.instruction_pre);
        this.startStopButton = this.mRootView.findViewById(R.id.start_stop_button);
//		finishBtn = (ImageView) mRootView.findViewById(R.id.more_btn);
//		downBtn = (ImageView) mRootView.findViewById(R.id.finish_btn);
//		title = (TextView) mRootView.findViewById(R.id.textView1);
//		title.setText(R.string.tab_remote);
        this.startStopButton.setOnClickListener(this.startStopListener);
//        finishBtn.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mActivity.finish();
//			}
//
//        });
//        downBtn.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mActivity.finish();
//			}
//
//        });
        this.updateUi();
        UiUpdater.registerClient(this.handler);
        
        // quickly turn on or off wifi.
        this.mRootView.findViewById(R.id.wifi_state_image).setOnClickListener(
                new View.OnClickListener() {
                    @Override
					public void onClick(View v) {
                        Intent intent = new Intent(
                                android.provider.Settings.ACTION_WIFI_SETTINGS);
                        ServerControlActivity.this.startActivity(intent);
                    }
                });

        return this.mRootView;
    }

    @Override
    public boolean onBack() {
        return false;
    }

    /**
     * Whenever we regain focus, we should update the button text depending on
     * the state of the server service.
     */
    @Override
	public void onStart() {
        super.onStart();
        UiUpdater.registerClient(this.handler);
        this.updateUi();
    }

    @Override
	public void onResume() {
        super.onResume();

        UiUpdater.registerClient(this.handler);
        this.updateUi();
        // Register to receive wifi status broadcasts
        this.myLog.l(Log.DEBUG, "Registered for wifi updates");
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.mActivity.registerReceiver(this.wifiReceiver, filter);
    }

    /*
     * Whenever we lose focus, we must unregister from UI update messages from
     * the FTPServerService, because we may be deallocated.
     */
    @Override
	public void onPause() {
        super.onPause();
        UiUpdater.unregisterClient(this.handler);
        this.myLog.l(Log.DEBUG, "Unregistered for wifi updates");
        this.mActivity.unregisterReceiver(this.wifiReceiver);
    }

    @Override
	public void onStop() {
        super.onStop();
        UiUpdater.unregisterClient(this.handler);
    }

    @Override
	public void onDestroy() {
        super.onDestroy();
        UiUpdater.unregisterClient(this.handler);
    }

    /**
     * This will be called by the static UiUpdater whenever the service has
     * changed state in a way that requires us to update our UI. We can't use
     * any myLog.l() calls in this function, because that will trigger an
     * endless loop of UI updates.
     */
    @SuppressWarnings("unused")
	public void updateUi() {
        this.myLog.l(Log.DEBUG, "Updating UI", true);

        WifiManager wifiMgr = (WifiManager) this.mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiMgr.getWifiState();
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID() : null;
        boolean isWifiReady = FTPServerService.isWifiEnabled();

        this.setText(R.id.wifi_state, isWifiReady ? wifiId : this.getString(R.string.no_wifi_hint));
        ImageView wifiImg = (ImageView) this.mRootView.findViewById(R.id.wifi_state_image);
        wifiImg.setImageResource(isWifiReady ? R.drawable.wifi_state4 : R.drawable.wifi_state0);

        boolean running = FTPServerService.isRunning();
        if (running) {
            this.myLog.l(Log.DEBUG, "updateUi: server is running", true);
            // Put correct text in start/stop button
            // Fill in wifi status and address
            InetAddress address = FTPServerService.getWifiIp();
            if (address != null) {
                String port = ":" + FTPServerService.getPort();
                this.ipText.setText("ftp://" + address.getHostAddress() + (FTPServerService.getPort() == 21 ? "" : port));

            } else {
                // could not get IP address, stop the service
                Context context = this.mActivity.getApplicationContext();
                Intent intent = new Intent(context, FTPServerService.class);
                context.stopService(intent);
                this.ipText.setText("");
            }
        }

        this.startStopButton.setEnabled(isWifiReady);
        TextView startStopButtonText = (TextView) this.mRootView.findViewById(R.id.start_stop_button_text);
        if (isWifiReady) {
            startStopButtonText.setText(running ? R.string.stop_server : R.string.start_server);
            startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(running ? R.drawable.disconnect
                    : R.drawable.connect, 0, 0, 0);
            startStopButtonText.setTextColor(running ? this.getResources().getColor(R.color.remote_disconnect_text)
                    : this.getResources().getColor(R.color.remote_connect_text));
        } else {
            if (FTPServerService.isRunning()) {
                Context context = this.mActivity.getApplicationContext();
                Intent intent = new Intent(context, FTPServerService.class);
                context.stopService(intent);
            }

            startStopButtonText.setText(R.string.no_wifi);
            startStopButtonText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            startStopButtonText.setTextColor(Color.GRAY);
        }

        this.ipText.setVisibility(running ? View.VISIBLE : View.INVISIBLE);
        this.instructionText.setVisibility(running ? View.VISIBLE : View.GONE);
        this.instructionTextPre.setVisibility(running ? View.GONE : View.VISIBLE);
    }

    private void setText(int id, String text) {
        TextView tv = (TextView) this.mRootView.findViewById(id);
        tv.setText(text);
    }

    View.OnClickListener startStopListener = new View.OnClickListener() {
        @Override
		public void onClick(View v) {
            Globals.setLastError(null);
            File chrootDir = new File(Defaults.chrootDir);
            if (!chrootDir.isDirectory())
                return;

            Context context = ServerControlActivity.this.mActivity.getApplicationContext();
            Intent intent = new Intent(context, FTPServerService.class);

            Globals.setChrootDir(chrootDir);
            if (!FTPServerService.isRunning()) {
                ServerControlActivity.this.warnIfNoExternalStorage();
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    context.startService(intent);
                }
            } else {
                context.stopService(intent);
            }
        }
    };

    private void warnIfNoExternalStorage() {
        String storageState = Environment.getExternalStorageState();
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            this.myLog.i("Warning due to storage state " + storageState);
            Toast toast = Toast.makeText(this.mActivity, R.string.storage_warning, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
		public void onReceive(Context ctx, Intent intent) {
            ServerControlActivity.this.myLog.l(Log.DEBUG, "Wifi status broadcast received");
            ServerControlActivity.this.updateUi();
        }
    };

    boolean requiredSettingsDefined() {
        SharedPreferences settings = this.mActivity.getSharedPreferences(Defaults.getSettingsName(), Defaults.getSettingsMode());
        String username = settings.getString("username", null);
        String password = settings.getString("password", null);
        return !(username == null || password == null);
    }

    /**
     * Get the settings from the FTPServerService if it's running, otherwise
     * load the settings directly from persistent storage.
     */
    SharedPreferences getSettings() {
        SharedPreferences settings = FTPServerService.getSettings();
        if (settings != null) {
            return settings;
        } else {
            return this.mActivity.getPreferences(Context.MODE_PRIVATE);
        }
    }
}
