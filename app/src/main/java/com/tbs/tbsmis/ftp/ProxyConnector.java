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

package com.tbs.tbsmis.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import org.json.JSONException;
import org.json.JSONObject;

import com.tbs.tbsmis.file.FTPServerService;
import com.tbs.tbsmis.ftp.SessionThread.Source;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class ProxyConnector extends Thread {
	public static final int IN_BUF_SIZE = 2048;
	public static final String ENCODING = "UTF-8";
	public static final int RESPONSE_WAIT_MS = 10000;
	public static final int QUEUE_WAIT_MS = 20000;
	public static final long UPDATE_USAGE_BYTES = 5000000; 
	public static final String PREFERRED_SERVER = "preferred_server"; //preferences
	public static final int CONNECT_TIMEOUT = 5000;
	
	private final FTPServerService ftpServerService;
	private final MyLog myLog = new MyLog(this.getClass().getName());
	private JSONObject response;
	private Thread responseWaiter;
	private final Queue<Thread> queuedRequestThreads = new LinkedList<Thread>();
	private Socket commandSocket;
	private OutputStream out;
	private String hostname;
	private InputStream inputStream;
	private long proxyUsage;
	private ProxyConnector.State proxyState = ProxyConnector.State.DISCONNECTED;
	private String prefix;
	private String proxyMessage;

	public enum State {CONNECTING, CONNECTED, FAILED, UNREACHABLE, DISCONNECTED}

    //QuotaStats cachedQuotaStats = null; // quotas have been canceled for now

	static final String USAGE_PREFS_NAME = "proxy_usage_data";

	/* We establish a so-called "command session" to the proxy. New connections
	 * will be handled by creating addition control and data connections to the
	 * proxy. See proxy_protocol.txt and proxy_architecture.pdf for an
	 * explanation of how proxying works. Hint: it's complicated.
	 */

	public ProxyConnector(FTPServerService ftpServerService) {
		this.ftpServerService = ftpServerService;
        proxyUsage = this.getPersistedProxyUsage();
        this.setProxyState(ProxyConnector.State.DISCONNECTED);
		Globals.setProxyConnector(this);
	}

	@Override
	public void run() {
        this.myLog.i("In ProxyConnector.run()");
        this.setProxyState(ProxyConnector.State.CONNECTING);
		try {
			String candidateProxies[] = this.getProxyList();
			for(String candidateHostname : candidateProxies) {
                this.hostname = candidateHostname;
                this.commandSocket = this.newAuthedSocket(this.hostname, Defaults.REMOTE_PROXY_PORT);
				if(this.commandSocket == null) {
					continue;
				}
                this.commandSocket.setSoTimeout(0); // 0 == forever
				//commandSocket.setKeepAlive(true);
				// Now that we have authenticated, we want to start the command session so we can
				// be notified of pending control sessions.
				JSONObject request = this.makeJsonRequest("start_command_session");
                this.response = this.sendRequest(this.commandSocket, request);
				if(this.response == null) {
                    this.myLog.i("Couldn't create proxy command session");
					continue; // try next server
				}
				if(!this.response.has("prefix")) {
                    this.myLog.l(Log.INFO, "start_command_session didn't receive a prefix in response");
					continue; // try next server
				}
                this.prefix = this.response.getString("prefix");
                this.response = null;  // Indicate that response is free for other use
                this.myLog.l(Log.INFO, "Got prefix of: " + this.prefix);
				break; // breaking with commandSocket != null indicates success
			}
			if(this.commandSocket == null) {
                this.myLog.l(Log.INFO, "No proxies accepted connection, failing.");
                this.setProxyState(ProxyConnector.State.UNREACHABLE);
				return;
			}
            this.setProxyState(ProxyConnector.State.CONNECTED);
            this.preferServer(this.hostname);
            this.inputStream = this.commandSocket.getInputStream();
            this.out = this.commandSocket.getOutputStream();
			int numBytes;
			byte[] bytes = new byte[ProxyConnector.IN_BUF_SIZE];
			//spawnQuotaRequester().start();
			while(true) {
                this.myLog.d("to proxy read()");
				numBytes = this.inputStream.read(bytes);
                this.incrementProxyUsage(numBytes);
                this.myLog.d("from proxy read()");
				JSONObject incomingJson = null;
				if(numBytes > 0) {
					String responseString = new String(bytes, ProxyConnector.ENCODING);
					incomingJson = new JSONObject(responseString);
					if(incomingJson.has("action")) {
						// If the incoming JSON object has an "action" field, then it is a
						// request, and not a response
                        this.incomingCommand(incomingJson);
					} else {
						// If the incoming JSON object does not have an "action" field, then
						// it is a response to a request we sent earlier.
						// If there's an object waiting for a response, then that object
						// will be referenced by responseWaiter.
						if(this.responseWaiter != null) {
							if(this.response != null) {
                                this.myLog.l(Log.INFO, "Overwriting existing cmd session response");
							}
                            this.response = incomingJson;
                            this.responseWaiter.interrupt();
						} else {
                            this.myLog.l(Log.INFO, "Response received but no responseWaiter");
						}
					}
				} else if(numBytes  == 0) {
                    this.myLog.d("Command socket read 0 bytes, looping");
				} else { // numBytes < 0
                    this.myLog.l(Log.DEBUG, "Command socket end of stream, exiting");
					if(this.proxyState != ProxyConnector.State.DISCONNECTED) {
						// Set state to FAILED unless this was an intentional
						// socket closure.
                        this.setProxyState(ProxyConnector.State.FAILED);
					}
					break;
				}
			}
            this.myLog.l(Log.INFO, "ProxyConnector thread quitting cleanly");
		} catch (IOException e) {
            this.myLog.l(Log.INFO, "IOException in command session: " + e);
            this.setProxyState(ProxyConnector.State.FAILED);
		} catch (JSONException e) {
            this.myLog.l(Log.INFO, "Commmand socket JSONException: " + e);
            this.setProxyState(ProxyConnector.State.FAILED);
		} catch (Exception e) {
            this.myLog.l(Log.INFO, "Other exception in ProxyConnector: " + e);
            this.setProxyState(ProxyConnector.State.FAILED);
		} finally {
			Globals.setProxyConnector(null);
            this.hostname = null;
            this.myLog.d("ProxyConnector.run() returning");
            this.persistProxyUsage();
		}
	}

	// This function is used to spawn a new Thread that will make a request over the
	// command thread. Since the main ProxyConnector thread handles the input
	// request/response de-multiplexing, it cannot also make a request using the
	// sendCmdSocketRequest, since sendCmdSocketRequest will block waiting for
	// a response, but the same thread is expected to deliver the response.
	// The short story is, if the main ProxyConnector command session thread wants to
	// make a request, the easiest way is to spawn a new thread and have it call
	// sendCmdSocketRequest in the same way as any other thread.
	//private Thread spawnQuotaRequester() {
	//	return new Thread() {
	//		public void run() {
	//			getQuotaStats(false);
	//		}
	//	};
	//}

	/**
	 * Since we want devices to generally stick with the same proxy server,
	 * and we may want to explicitly redirect some devices to other servers,
	 * we have this mechanism to store a "preferred server" on the device.
	 */
	private void preferServer(String hostname) {
		SharedPreferences prefs = Globals.getContext()
			.getSharedPreferences(ProxyConnector.PREFERRED_SERVER, 0);
		Editor editor = prefs.edit();
		editor.putString(ProxyConnector.PREFERRED_SERVER, hostname);
		editor.commit();
	}

	private String[] getProxyList() {
		SharedPreferences prefs = Globals.getContext()
			.getSharedPreferences(ProxyConnector.PREFERRED_SERVER, 0);
		String preferred = prefs.getString(ProxyConnector.PREFERRED_SERVER, null);

		String[] allProxies;

		if(Defaults.release) {
			allProxies = new String[] {
				"c1.swiftp.org",
				"c2.swiftp.org",
				"c3.swiftp.org",
				"c4.swiftp.org",
				"c5.swiftp.org",
				"c6.swiftp.org",
				"c7.swiftp.org",
				"c8.swiftp.org",
				"c9.swiftp.org"};
		} else {
			//allProxies = new String[] {
			//	"cdev.swiftp.org"
			//};
			allProxies = new String[] {
					"c1.swiftp.org",
					"c2.swiftp.org",
					"c3.swiftp.org",
					"c4.swiftp.org",
					"c5.swiftp.org",
					"c6.swiftp.org",
					"c7.swiftp.org",
					"c8.swiftp.org",
					"c9.swiftp.org"};
		}

		// We should randomly permute the server list in order to spread
		// load between servers. Collections offers a shuffle() function
		// that does this, so we'll convert to List and back to String[].
		List<String> proxyList = Arrays.asList(allProxies);
		Collections.shuffle(proxyList);
		allProxies = proxyList.toArray(new String[] {}); // arg used for type

		// Return preferred server first, followed by all others
		if(preferred == null) {
			return allProxies;
		} else {
			return Util.concatStrArrays(
					new String[] {preferred}, allProxies);
		}
	}



	private boolean checkAndPrintJsonError(JSONObject json) throws JSONException {
		if(json.has("error_code")) {
			// The returned JSON object will have a field called "errorCode"
			// if there was a problem executing our request.
			StringBuilder s = new StringBuilder(
					"Error in JSON response, code: ");
			s.append(json.getString("error_code"));
			if(json.has("error_string")) {
				s.append(", string: ");
				s.append(json.getString("error_string"));
			}
            this.myLog.l(Log.INFO, s.toString());

			// Obsolete: there's no authentication anymore
			// Dev code to enable frequent database wipes. If we fail to login,
			// remove our stored account info, causing a create_account action
			// next time.
			//if(!Defaults.release) {
			//	if(json.getInt("error_code") == 11) {
			//		myLog.l(Log.DEBUG, "Dev: removing secret due to login failure");
			//		removeSecret();
			//	}
			//}
			return true;
		}
		return false;
	}

	/**
	 * Reads our persistent storage, looking for a stored proxy authentication secret.
	 * @return The secret, if present, or null.
	 */
	//Obsolete, there's no authentication anymore
	/*private String retrieveSecret() {
		SharedPreferences settings = Globals.getContext().
			getSharedPreferences(Defaults.getSettingsName(),
			Defaults.getSettingsMode());
		return settings.getString("proxySecret", null);
	}*/

	//Obsolete, there's no authentication anymore
	/*private void storeSecret(String secret) {
		SharedPreferences settings = Globals.getContext().
			getSharedPreferences(Defaults.getSettingsName(),
			Defaults.getSettingsMode());
		Editor editor = settings.edit();
		editor.putString("proxySecret", secret);
		editor.commit();
	}*/

	//Obsolete, there's no authentication anymore
	/*private void removeSecret() {
		SharedPreferences settings = Globals.getContext().
			getSharedPreferences(Defaults.getSettingsName(),
					Defaults.getSettingsMode());
		Editor editor = settings.edit();
		editor.remove("proxySecret");
		editor.commit();
	}*/

	private void incomingCommand(JSONObject json) {
		try {
			String action = json.getString("action");
			if(action.equals("control_connection_waiting")) {
                this.startControlSession(json.getInt("port"));
			} else if(action.equals("prefer_server")) {
				String host = json.getString("host"); // throws JSONException, fine
                this.preferServer(host);
                this.myLog.i("New preferred server: " + host);
			} else if(action.equals("message")) {
                this.proxyMessage = json.getString("text");
                this.myLog.i("Got news from proxy server: \"" + this.proxyMessage + "\"");
				FTPServerService.updateClients(); // UI update to show message
			} else if(action.equals("noop")) {
                this.myLog.d("Proxy noop");
			} else {
                this.myLog.l(Log.INFO, "Unsupported incoming action: " + action);
			}
			// If we're starting a control session register with ftpServerService
		} catch (JSONException e){
            this.myLog.l(Log.INFO, "JSONException in proxy incomingCommand");
		}
	}

	private void startControlSession(int port) {
		Socket socket;
        this.myLog.d("Starting new proxy FTP control session");
		socket = this.newAuthedSocket(this.hostname, port);
		if(socket == null) {
            this.myLog.i("startControlSession got null authed socket");
			return;
		}
		ProxyDataSocketFactory dataSocketFactory = new ProxyDataSocketFactory();
		SessionThread thread = new SessionThread(socket, dataSocketFactory,
				Source.PROXY);
		thread.start();
        this.ftpServerService.registerSessionThread(thread);
	}

	/**
	 * Connects an outgoing socket to the proxy and authenticates, creating an account
	 * if necessary.
	 */
	private Socket newAuthedSocket(String hostname, int port) {
		if(hostname == null) {
            this.myLog.i("newAuthedSocket can't connect to null host");
			return null;
		}
		JSONObject json = new JSONObject();
		//String secret = retrieveSecret();
		Socket socket;
		OutputStream out = null;
		InputStream in = null;

		try {
            this.myLog.d("Opening proxy connection to " + hostname + ":" + port);
			socket = new Socket();
			socket.connect(new InetSocketAddress(hostname, port), ProxyConnector.CONNECT_TIMEOUT);
			json.put("android_id", Util.getAndroidId());
			json.put("swiftp_version", Util.getVersion());
			json.put("action", "login");
			out = socket.getOutputStream();
			in = socket.getInputStream();
			int numBytes;

			out.write(json.toString().getBytes(ProxyConnector.ENCODING));
            this.myLog.l(Log.DEBUG, "Sent login request");
			// Read and parse the server's response
			byte[] bytes = new byte[ProxyConnector.IN_BUF_SIZE];
			// Here we assume that the server's response will all be contained in
			// a single read, which may be unsafe for large responses
			numBytes = in.read(bytes);
			if(numBytes == -1) {
                this.myLog.l(Log.INFO, "Proxy socket closed while waiting for auth response");
				return null;
			} else if (numBytes == 0) {
                this.myLog.l(Log.INFO, "Short network read waiting for auth, quitting");
				return null;
			}
			json = new JSONObject(new String(bytes, 0, numBytes, ProxyConnector.ENCODING));
			if(this.checkAndPrintJsonError(json)) {
				return null;
			}
            this.myLog.d("newAuthedSocket successful");
			return socket;
		} catch(Exception e) {
            this.myLog.i("Exception during proxy connection or authentication: " + e);
			return null;
		}
	}

	public void quit() {
        this.setProxyState(ProxyConnector.State.DISCONNECTED);
		try {
            this.sendRequest(this.commandSocket, this.makeJsonRequest("finished")); // ignore reply

			if(this.inputStream != null) {
                this.myLog.d("quit() closing proxy inputStream");
                this.inputStream.close();
			} else {
                this.myLog.d("quit() won't close null inputStream");
			}
			if(this.commandSocket != null) {
                this.myLog.d("quit() closing proxy socket");
                this.commandSocket.close();
			} else {
                this.myLog.d("quit() won't close null socket");
			}
		}
		catch (IOException e) {}
		catch(JSONException e) {}
        this.persistProxyUsage();
		Globals.setProxyConnector(null);
	}

	@SuppressWarnings("unused")
	private JSONObject sendCmdSocketRequest(JSONObject json) {
		try {
			boolean queued;
			synchronized(this) {
				if(this.responseWaiter == null) {
                    this.responseWaiter = Thread.currentThread();
					queued = false;
                    this.myLog.d("sendCmdSocketRequest proceeding without queue");
				} else if (!this.responseWaiter.isAlive()) {
					// This code should never run. It is meant to recover from a situation
					// where there is a thread that sent a proxy request but died before
					// starting the subsequent request. If this is the case, the correct
					// behavior is to run the next queued thread in the queue, or if the
					// queue is empty, to perform our own request.
                    this.myLog.l(Log.INFO, "Won't wait on dead responseWaiter");
					if(this.queuedRequestThreads.size() == 0) {
                        this.responseWaiter = Thread.currentThread();
						queued = false;
					} else {
                        this.queuedRequestThreads.add(Thread.currentThread());
                        this.queuedRequestThreads.remove().interrupt(); // start queued thread
						queued = true;
					}
				} else {
                    this.myLog.d("sendCmdSocketRequest queueing thread");
                    this.queuedRequestThreads.add(Thread.currentThread());
					queued = true;
				}
			}
			// If a different thread has sent a request and is waiting for a response,
			// then the current thread will be in a queue waiting for an interrupt
			if(queued) {
				// The current thread must wait until we are popped off the waiting queue
				// and receive an interrupt()
				boolean interrupted = false;
				try {
                    this.myLog.d("Queued cmd session request thread sleeping...");
					Thread.sleep(ProxyConnector.QUEUE_WAIT_MS);
				} catch (InterruptedException e) {
                    this.myLog.l(Log.DEBUG, "Proxy request popped and ready");
					interrupted = true;
				}
				if(!interrupted) {
                    this.myLog.l(Log.INFO, "Timed out waiting on proxy queue");
					return null;
				}
			}
			// We have been popped from the wait queue if necessary, and now it's time
			// to send the request.
			try {
                this.responseWaiter = Thread.currentThread();
				byte[] outboundData = Util.jsonToByteArray(json);
				try {
                    this.out.write(outboundData);
				} catch(IOException e) {
                    this.myLog.l(Log.INFO, "IOException sending proxy request");
					return null;
				}
				// Wait RESPONSE_WAIT_MS for a response from the proxy
				boolean interrupted = false;
				try {
					// Wait for the main ProxyConnector thread to interrupt us, meaning
					// that a response has been received.
                    this.myLog.d("Cmd session request sleeping until response");
					Thread.sleep(ProxyConnector.RESPONSE_WAIT_MS);
				} catch (InterruptedException e) {
                    this.myLog.d("Cmd session response received");
					interrupted = true;
				}
				if(!interrupted) {
                    this.myLog.l(Log.INFO, "Proxy request timed out");
					return null;
				}
				// At this point, the main ProxyConnector thread will have stored
				// our response in "JSONObject response".
                this.myLog.d("Cmd session response was: " + this.response);
				return this.response;
			}
			finally {
				// Make sure that when this request finishes, the next thread on the
				// queue gets started.
				synchronized(this) {
					if(this.queuedRequestThreads.size() != 0) {
                        this.queuedRequestThreads.remove().interrupt();
					}
				}
			}
		} catch (JSONException e) {
            this.myLog.l(Log.INFO, "JSONException in sendRequest: " + e);
			return null;
		}
	}

	public JSONObject sendRequest(InputStream in, OutputStream out, JSONObject request)
	throws JSONException {
		try {
			out.write(Util.jsonToByteArray(request));
			byte[] bytes = new byte[ProxyConnector.IN_BUF_SIZE];
			int numBytes = in.read(bytes);
			if(numBytes < 1) {
                this.myLog.i("Proxy sendRequest short read on response");
				return null;
			}
			JSONObject response = Util.byteArrayToJson(bytes);
			if(response == null) {
                this.myLog.i("Null response to sendRequest");
			}
			if(this.checkAndPrintJsonError(response)) {
                this.myLog.i("Error response to sendRequest");
				return null;
			}
			return response;
		} catch (IOException e) {
            this.myLog.i("IOException in proxy sendRequest: " + e);
			return null;
		}
	}

	public JSONObject sendRequest(Socket socket, JSONObject request)
	throws JSONException {
		 try {
			 if(socket == null) {
				 // The server is probably shutting down
                 this.myLog.i("null socket in ProxyConnector.sendRequest()");
				 return null;
			 } else {
				 return this.sendRequest(socket.getInputStream(),
						 socket.getOutputStream(),
						 request);
			 }
		 } catch (IOException e) {
             this.myLog.i("IOException in proxy sendRequest wrapper: " + e);
			 return null;
		 }
	}

	public ProxyDataSocketInfo pasvListen() {
		try {
			// connect to proxy and authenticate
            this.myLog.d("Sending data_pasv_listen to proxy");
			Socket socket = this.newAuthedSocket(hostname, Defaults.REMOTE_PROXY_PORT);
			if(socket == null) {
                this.myLog.i("pasvListen got null socket");
				return null;
			}
			JSONObject request = this.makeJsonRequest("data_pasv_listen");

			JSONObject response = this.sendRequest(socket, request);
			if(response == null) {
				return null;
			}
			int port = response.getInt("port");
			return new ProxyDataSocketInfo(socket, port);
		} catch(JSONException e) {
            this.myLog.l(Log.INFO, "JSONException in pasvListen");
			return null;
		}
	}

	public Socket dataPortConnect(InetAddress clientAddr, int clientPort) {
		/**
		 * This function is called by a ProxyDataSocketFactory when it's time to
		 * transfer some data in PORT mode (not PASV mode). We send a
		 * data_port_connect request to the proxy, containing the IP and port
		 * of the FTP client to which a connection should be made.
		 */
		try {
            this.myLog.d("Sending data_port_connect to proxy");
			Socket socket = this.newAuthedSocket(hostname, Defaults.REMOTE_PROXY_PORT);
			if(socket == null) {
                this.myLog.i("dataPortConnect got null socket");
				return null;
			}
			JSONObject request = this.makeJsonRequest("data_port_connect");
			request.put("address", clientAddr.getHostAddress());
			request.put("port", clientPort);
			JSONObject response = this.sendRequest(socket, request);
			if(response == null) {
				return null; // logged elsewhere
			}
			return socket;
		} catch (JSONException e) {
            this.myLog.i("JSONException in dataPortConnect");
			return null;
		}
	}

	/**
	 * Given a socket returned from pasvListen(), send a data_pasv_accept request
	 * over the socket to the proxy, which should result in a socket that is ready
	 * for data transfer with the FTP client. Of course, this will only work if the
	 * FTP client connects to the proxy like it's supposed to. The client will have
	 * already been told to connect by the response to its PASV command.
	 *
	 * This should only be called from the onTransfer method of ProxyDataSocketFactory.
	 *
	 * @param socket A socket previously returned from ProxyConnector.pasvListen()
	 * @return true if the accept operation completed OK, otherwise false
	 */

	public boolean pasvAccept(Socket socket) {
		try {
			JSONObject request = this.makeJsonRequest("data_pasv_accept");
			JSONObject response = this.sendRequest(socket, request);
			if(response == null) {
				return false;  // error is logged elsewhere
			}
			if(this.checkAndPrintJsonError(response)) {
                this.myLog.i("Error response to data_pasv_accept");
				return false;
			}
			// The proxy's response will be an empty JSON object on success
            this.myLog.d("Proxy data_pasv_accept successful");
			return true;
		} catch (JSONException e) {
            this.myLog.i("JSONException in pasvAccept: " + e);
			return false;
		}
	}

	public InetAddress getProxyIp() {
		if(isAlive()) {
			if(this.commandSocket.isConnected()) {
				return this.commandSocket.getInetAddress();
			}
		}
		return null;
	}

	private JSONObject makeJsonRequest(String action) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("action", action);
		return json;
	}

	/* Quotas have been canceled for now
	  public QuotaStats getQuotaStats(boolean canUseCached) {
		if(canUseCached) {
			if(cachedQuotaStats != null) {
				myLog.d("Returning cachedQuotaStats");
				return cachedQuotaStats;
			} else {
				myLog.d("Would return cached quota stats but none retrieved");
			}
		}
		// If there's no cached quota stats, or if the called wants fresh stats,
		// make a JSON request to the proxy, assuming the command session is open.
		try {
			JSONObject response = sendCmdSocketRequest(makeJsonRequest("check_quota"));
			int used, quota;
			if(response == null) {
				myLog.w("check_quota got null response");
				return null;
			}
			used = response.getInt("used");
			quota = response.getInt("quota");
			myLog.d("Got quota response of " + used + "/" + quota);
			cachedQuotaStats = new QuotaStats(used, quota) ;
			return cachedQuotaStats;
		} catch (JSONException e) {
			myLog.w("JSONException in getQuota: " + e);
			return null;
		}
	}*/

	// We want to track the total amount of data sent via the proxy server, to
	// show it to the user and encourage them to donate.
	void persistProxyUsage() {
		if(this.proxyUsage == 0) {
			return;  // This shouldn't happen, but just for safety
		}
		SharedPreferences prefs = Globals.getContext().
			getSharedPreferences(ProxyConnector.USAGE_PREFS_NAME, 0); // 0 == private
		Editor editor = prefs.edit();
		editor.putLong(ProxyConnector.USAGE_PREFS_NAME, this.proxyUsage);
		editor.commit();
        this.myLog.d("Persisted proxy usage to preferences");
	}

	long getPersistedProxyUsage() {
		// This gets the last persisted value for bytes transferred through
		// the proxy. It can be out of date since it doesn't include data
		// transferred during the current session.
		SharedPreferences prefs = Globals.getContext().
			getSharedPreferences(ProxyConnector.USAGE_PREFS_NAME, 0); // 0 == private
		return prefs.getLong(ProxyConnector.USAGE_PREFS_NAME, 0); // Default count of 0
	}

	public long getProxyUsage() {
		// This gets the running total of all proxy usage, which may not have
		// been persisted yet.
		return this.proxyUsage;
	}

	void incrementProxyUsage(long num) {
		long oldProxyUsage = this.proxyUsage;
        this.proxyUsage += num;
		if(this.proxyUsage % ProxyConnector.UPDATE_USAGE_BYTES < oldProxyUsage % ProxyConnector.UPDATE_USAGE_BYTES) {
			FTPServerService.updateClients();
            this.persistProxyUsage();
		}
	}

	public ProxyConnector.State getProxyState() {
		return this.proxyState;
	}

	private void setProxyState(ProxyConnector.State state) {
        this.proxyState = state;
        this.myLog.l(Log.DEBUG, "Proxy state changed to " + state, true);
		FTPServerService.updateClients(); // UI update
	}

	public static String stateToString(ProxyConnector.State s) {
//		Context ctx = Globals.getContext();
//		switch(s) {
//		case DISCONNECTED:
//			return ctx.getString(R.string.pst_disconnected);
//		case CONNECTING:
//			return ctx.getString(R.string.pst_connecting);
//		case CONNECTED:
//			return ctx.getString(R.string.pst_connected);
//		case FAILED:
//			return ctx.getString(R.string.pst_failed);
//		case UNREACHABLE:
//			return ctx.getString(R.string.pst_unreachable);
//		default:
//			return ctx.getString(R.string.unknown);
//		}
	    return "";
	}

	/**
	 * The URL to which users should point their FTP client.
	 */
	public String getURL() {
		if(this.proxyState == ProxyConnector.State.CONNECTED) {
			String username = Globals.getUsername();
			if(username != null) {
				return "ftp://" + this.prefix + "_" + username + "@" + this.hostname;
			}
		}
		return "";
	}
	
	/** If the proxy sends a human-readable message, it can be retrieved by
	 * calling this function. Returns null if no message has been received.
	 */
	public String getProxyMessage() {
		return this.proxyMessage;
	}

}

