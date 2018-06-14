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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;

import com.tbs.tbsmis.notification.PushMsgService.TaskSubmitter;
import com.tbs.tbsmis.notification.PushMsgService.TaskTracker;
import com.tbs.tbsmis.util.DES;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * This class is to manage the XMPP connection between client and server.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

	private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

	private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

	private final Context context;

	private final TaskSubmitter taskSubmitter;

	private final TaskTracker taskTracker;

	private final SharedPreferences sharedPrefs;

	private final String xmppHost;

	private final int xmppPort;

	private XMPPConnection connection;

	private String username;

	private String password;

	private final ConnectionListener connectionListener;

	private final PacketListener notificationPacketListener;

	private final Handler handler;

	private final List<Runnable> taskList;

	private boolean running;

	private Future<?> futureTask;

	private final Thread reconnection;

	private String ebsserver; // EBS ����

	private String ebsport; // EBS port

	private String deviceID;

	private int iAnonymityUser = 1;

	private String loginID;

	public XmppManager(PushMsgService notificationService) {
        this.context = notificationService;
        this.taskSubmitter = notificationService.getTaskSubmitter();
        this.taskTracker = notificationService.getTaskTracker();
        this.sharedPrefs = notificationService.getSharedPreferences();

        this.xmppHost = this.sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        this.xmppPort = this.sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
		// username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
		// password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");

        this.username = this.sharedPrefs.getString(Constants.XMPP_NEW_USERNAME, "");
        this.password = this.sharedPrefs.getString(Constants.XMPP_NEW_PASSWORD, "");

        this.loginID = this.sharedPrefs.getString(Constants.XMPP_EBS_LOGINID, "");

        this.iAnonymityUser = this.sharedPrefs.getInt(Constants.XMPP_IS_ANONYMITY, 1);

        this.ebsserver = this.sharedPrefs.getString(Constants.EBS_SERVER, "");
        this.ebsport = this.sharedPrefs.getString(Constants.EBS_PORT, "");

        this.deviceID = this.sharedPrefs.getString(Constants.DEVICE_ID, "");

        this.connectionListener = new PersistentConnectionListener(this);
        this.notificationPacketListener = new NotificationPacketListener(this);

        this.handler = new Handler();
        this.taskList = new ArrayList<Runnable>();
        this.reconnection = new ReconnectionThread(this);
	}

	public Context getContext() {
		return this.context;
	}

	public void connect() {
		Log.d(XmppManager.LOGTAG, "connect()...");
        this.submitLoginTask();
	}

	public void disconnect() {
		Log.d(XmppManager.LOGTAG, "disconnect()...");
        this.terminatePersistentConnection();
		if (this.connection != null) {
            this.connection.setConnected(false);
		}
        this.closeServerSess();
	}

	public void terminatePersistentConnection() {
		Log.d(XmppManager.LOGTAG, "terminatePersistentConnection()...");
		Runnable runnable = new Runnable() {
			final XmppManager xmppManager = XmppManager.this;

			@Override
			public void run() {
				if (this.xmppManager.isConnected()) {
					Log.d(XmppManager.LOGTAG, "terminatePersistentConnection()... run()");
                    this.xmppManager.getConnection().removePacketListener(
                            this.xmppManager.getNotificationPacketListener());
                    this.xmppManager.getConnection().disconnect();
				}
                this.xmppManager.runTask();
			}

		};
        this.addTask(runnable);
	}

	public void closeServerSess() {
		if (!this.isConnected()) {
			return;
		}

		Registration registration = new Registration();

		PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
				registration.getPacketID()), new PacketTypeFilter(IQ.class));

		PacketListener packetListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {

				if (packet instanceof IQ) {
					IQ response = (IQ) packet;
					if (response.getType() == Type.ERROR) {
						if (!response.getError().toString().contains("409")) {
							Log.e(XmppManager.LOGTAG,
									"Unknown error while registering XMPP account! "
											+ response.getError()
													.getCondition());
						}
					} else if (response.getType() == Type.RESULT) {

					}
				}
			}
		};
        this.connection.addPacketListener(packetListener, packetFilter);
		registration.setType(Type.SET);
		registration.addAttribute("isCloseSess", "true");
		registration.addAttribute("deviceid", this.getDeviceID());
        this.connection.sendPacket(registration);

	}

	public void EBSLogout() {
		if (!this.isConnected())
			return;
		Registration registration = new Registration();

		PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
				registration.getPacketID()), new PacketTypeFilter(IQ.class));

		PacketListener packetListener = new PacketListener() {

			@Override
			public void processPacket(Packet packet) {

				if (packet instanceof IQ) {
					IQ response = (IQ) packet;
					if (response.getType() == Type.ERROR) {
						if (!response.getError().toString().contains("409")) {
							Log.e(XmppManager.LOGTAG,
									"Unknown error while registering XMPP account! "
											+ response.getError()
													.getCondition());
						}
					} else if (response.getType() == Type.RESULT) {

					}
				}
			}
		};

        this.connection.addPacketListener(packetListener, packetFilter);

		registration.setType(Type.SET);

		registration.addAttribute("isEBSLogout", "true");
		registration.addAttribute("deviceid", this.getDeviceID());
        this.connection.sendPacket(registration);

	}

	public XMPPConnection getConnection() {
		return this.connection;
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public void setDeviceID(String DeviceID) {
        deviceID = DeviceID;
	}

	public String getDeviceID() {
		return this.deviceID;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginID() {
		return this.loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public int getIsAnonmityUser() {
		return this.iAnonymityUser;
	}

	public String getEBSServer() {
		return this.ebsserver;
	}

	public void setEBSServer(String ebsserver) {
		this.ebsserver = ebsserver;
	}

	public String getEBSPort() {
		return this.ebsport;
	}

	public void setEBSPort(String ebsport) {
		this.ebsport = ebsport;
	}

	public ConnectionListener getConnectionListener() {
		return this.connectionListener;
	}

	public PacketListener getNotificationPacketListener() {
		return this.notificationPacketListener;
	}

	public void startReconnectionThread() {
		synchronized (this.reconnection) {
			if (!this.reconnection.isAlive()) {
                this.reconnection.setName("Xmpp Reconnection Thread");
                this.reconnection.start();
			}
		}
	}

	public Handler getHandler() {
		return this.handler;
	}

	public void reregisterAccount() {
        this.removeAccount();
        this.submitLoginTask();
		// runTask();
	}

	public List<Runnable> getTaskList() {
		return this.taskList;
	}

	public Future<?> getFutureTask() {
		return this.futureTask;
	}

	public void runTask() {
		Log.d(XmppManager.LOGTAG, "runTask()...");
		synchronized (this.taskList) {
            this.running = false;
            this.futureTask = null;
			if (!this.taskList.isEmpty()) {
				Runnable runnable = this.taskList.get(0);
                this.taskList.remove(0);
                this.running = true;
                this.futureTask = this.taskSubmitter.submit(runnable);
				if (this.futureTask == null) {
                    this.taskTracker.decrease();
				}
			}
		}
        this.taskTracker.decrease();
		Log.d(XmppManager.LOGTAG, "runTask()...done");
	}

	private String newRandomUUID() {
		String uuidRaw = UUID.randomUUID().toString();
		return uuidRaw.replaceAll("-", "");
	}

	private boolean isConnected() {
		if (this.connection != null) {
			return this.connection.isConnected();
		} else {
			return false;
		}
	}

	private boolean isAuthenticated() {
		if (this.connection != null) {
			return this.connection.isConnected() && this.connection.isAuthenticated();
		} else {
			return false;
		}
	}

	private void submitConnectTask() {
		Log.d(XmppManager.LOGTAG, "submitConnectTask()...");
        this.addTask(new ConnectTask());
	}

	private void submitRegisterTask() {
		Log.d(XmppManager.LOGTAG, "submitRegisterTask()...");
        this.submitConnectTask();
        this.addTask(new RegisterTask());
	}

	private void submitLoginTask() {
		Log.d(XmppManager.LOGTAG, "submitLoginTask()...");
        this.submitRegisterTask();
        this.addTask(new LoginTask());
        this.runTask();
	}

	private void addTask(Runnable runnable) {
		Log.d(XmppManager.LOGTAG, "addTask(runnable)...");
        this.taskTracker.increase();
		synchronized (this.taskList) {
			if (this.taskList.isEmpty() && !this.running) {
                this.running = true;
                this.futureTask = this.taskSubmitter.submit(runnable);
				if (this.futureTask == null) {
                    this.taskTracker.decrease();
				}
			} else {
                this.taskList.add(runnable);
				// runTask()
			}
		}
		Log.d(XmppManager.LOGTAG, "addTask(runnable)... done");
	}

	private void removeAccount() {
		SharedPreferences.Editor editor = this.sharedPrefs.edit();
		editor.remove(Constants.XMPP_NEW_USERNAME);
		editor.remove(Constants.XMPP_NEW_PASSWORD);

		editor.commit();
	}

	/**
	 * A runnable task to connect the server.
	 */
	private class ConnectTask implements Runnable {

		final XmppManager xmppManager;

		private ConnectTask() {
            xmppManager = XmppManager.this;
		}

		@Override
		public void run() {
			Log.i(XmppManager.LOGTAG, "ConnectTask.run()...");

			if (!this.xmppManager.isConnected()) {
				// Create the configuration for this new connection

				ConnectionConfiguration connConfig = new ConnectionConfiguration(
                        XmppManager.this.xmppHost, XmppManager.this.xmppPort);
				// connConfig.setSecurityMode(SecurityMode.disabled);
				connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
				connConfig.setSASLAuthenticationEnabled(false);
				connConfig.setCompressionEnabled(false);

				XMPPConnection connection = new XMPPConnection(connConfig);
                this.xmppManager.setConnection(connection);

				try {
					// Connect to the server
					connection.connect();
					Log.i(XmppManager.LOGTAG, "XMPP connected successfully");

					// packet provider
					ProviderManager.getInstance().addIQProvider("notification",
							"androidpn:iq:notification",
							new NotificationIQProvider());

				} catch (XMPPException e) {
					Log.e(XmppManager.LOGTAG, "XMPP connection failed", e);
				}

                this.xmppManager.runTask();

			} else {
				Log.i(XmppManager.LOGTAG, "XMPP connected already");
                this.xmppManager.runTask();
			}
		}
	}

	/**
	 * A runnable task to register a new user onto the server.
	 */
	private class RegisterTask implements Runnable {

		final XmppManager xmppManager;

		private RegisterTask() {
            this.xmppManager = XmppManager.this;
		}

		@Override
		public void run() {
			Log.i(XmppManager.LOGTAG, "RegisterTask.run()...");

			// if (!xmppManager.isRegistered())
			// {

			String newUsername = null;
			String newPassword = null;

			if (this.xmppManager.getIsAnonmityUser() == 1) {
				// newUsername = newRandomUUID();
				// newPassword = newRandomUUID();
				newUsername = XmppManager.this.sharedPrefs.getString(
						Constants.XMPP_NEW_USERNAME, XmppManager.this.newRandomUUID());
				newPassword = XmppManager.this.sharedPrefs.getString(
						Constants.XMPP_NEW_PASSWORD, XmppManager.this.newRandomUUID());
				SharedPreferences.Editor editor = XmppManager.this.sharedPrefs.edit();
				editor.putInt(Constants.XMPP_IS_ANONYMITY, 0);
				editor.putString(Constants.XMPP_NEW_USERNAME, newUsername);
				editor.putString(Constants.XMPP_NEW_PASSWORD, newPassword);
				editor.commit();
			} else {
				newUsername = this.xmppManager.getUsername();
				newPassword = this.xmppManager.getPassword();
			}

            this.xmppManager.setUsername(newUsername);
            this.xmppManager.setPassword(newPassword);
			Registration registration = new Registration();

			PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
					registration.getPacketID()), new PacketTypeFilter(IQ.class));

			PacketListener packetListener = new PacketListener() {

				@Override
				public void processPacket(Packet packet) {
					Log.d("RegisterTask.PacketListener", "processPacket().....");
					Log.d("RegisterTask.PacketListener",
							"packet=" + packet.toXML());

					if (packet instanceof IQ) {
						IQ response = (IQ) packet;
						if (response.getType() == Type.ERROR) {
							if (!response.getError().toString().contains("409")) {
								Log.e(XmppManager.LOGTAG,
										"Unknown error while registering XMPP account! "
												+ response.getError()
														.getCondition());
							}
						} else if (response.getType() == Type.RESULT) {
							SharedPreferences.Editor editor = XmppManager.this.sharedPrefs.edit();
							editor.putString(Constants.XMPP_NEW_USERNAME,
                                    RegisterTask.this.xmppManager.getUsername());
							editor.putString(Constants.XMPP_NEW_PASSWORD,
                                    RegisterTask.this.xmppManager.getPassword());
							editor.commit();
							Log.i(XmppManager.LOGTAG, "Account registered successfully");
                            RegisterTask.this.xmppManager.runTask();
						}
					}
				}
			};
            XmppManager.this.connection.addPacketListener(packetListener, packetFilter);
			registration.setType(Type.SET);
			registration.addAttribute("username", newUsername);
			registration.addAttribute("password", DES.encrypt(newPassword));
			registration.addAttribute("loginid", XmppManager.this.getLoginID());
			registration.addAttribute("deviceid", this.xmppManager.getDeviceID());
            XmppManager.this.connection.sendPacket(registration);
		}
	}

	/**
	 * A runnable task to log into the server.
	 */
	private class LoginTask implements Runnable {

		final XmppManager xmppManager;

		private LoginTask() {
            xmppManager = XmppManager.this;
		}

		@Override
		public void run() {
			Log.i(XmppManager.LOGTAG, "LoginTask.run()...");

			if (!this.xmppManager.isAuthenticated()) {
				Log.d(XmppManager.LOGTAG, "username=" + this.xmppManager.getUsername());
				Log.d(XmppManager.LOGTAG, "password=" + this.xmppManager.getPassword());

				try {
                    this.xmppManager.getConnection().login(
                            this.xmppManager.getUsername(),
                            this.xmppManager.getPassword(),
                            this.xmppManager.getDeviceID() + ","
									+ this.xmppManager.getLoginID() + ","
									+ XmppManager.XMPP_RESOURCE_NAME);

					Log.d(XmppManager.LOGTAG, "Loggedn in successfully");

					// connection listener
					if (this.xmppManager.getConnectionListener() != null) {
                        this.xmppManager.getConnection().addConnectionListener(
                                this.xmppManager.getConnectionListener());
					}
					// packet filter
					PacketFilter packetFilter = new PacketTypeFilter(
							NotificationIQ.class);
					// packet listener
					PacketListener packetListener = this.xmppManager
							.getNotificationPacketListener();
                    XmppManager.this.connection.addPacketListener(packetListener, packetFilter);
                    this.xmppManager.runTask();
				} catch (XMPPException e) {
					// xmppManager.setConnection(null);
					Log.e(XmppManager.LOGTAG, "LoginTask.run()... xmpp error");
					Log.e(XmppManager.LOGTAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					String INVALID_CREDENTIALS_ERROR_CODE = "401";
					String errorMessage = e.getMessage();
					if (errorMessage != null
							&& errorMessage
									.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                        this.xmppManager.reregisterAccount();
						return;
					}
                    this.xmppManager.startReconnectionThread();

				} catch (Exception e) {
					Log.e(XmppManager.LOGTAG, "LoginTask.run()... other error");
					Log.e(XmppManager.LOGTAG, "Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
                    this.xmppManager.startReconnectionThread();
				}

			} else {
				Log.i(XmppManager.LOGTAG, "Logged in already");
                this.xmppManager.runTask();
			}

		}
	}

}
