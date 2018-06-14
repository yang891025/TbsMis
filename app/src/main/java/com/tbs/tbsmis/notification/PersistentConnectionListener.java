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

import org.jivesoftware.smack.ConnectionListener;

import android.util.Log;

/** 
 * A listener class for monitoring connection closing and reconnection events.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class PersistentConnectionListener implements ConnectionListener {

    private static final String LOGTAG = LogUtil
            .makeLogTag(PersistentConnectionListener.class);

    private final XmppManager xmppManager;

    public PersistentConnectionListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void connectionClosed() {
        Log.d(PersistentConnectionListener.LOGTAG, "connectionClosed()...");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d(PersistentConnectionListener.LOGTAG, "connectionClosedOnError()...");
        if (this.xmppManager.getConnection() != null
                && this.xmppManager.getConnection().isConnected()) {
            this.xmppManager.getConnection().disconnect();
        }
        this.xmppManager.startReconnectionThread();
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(PersistentConnectionListener.LOGTAG, "reconnectingIn()...");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.d(PersistentConnectionListener.LOGTAG, "reconnectionFailed()...");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.d(PersistentConnectionListener.LOGTAG, "reconnectionSuccessful()...");
    }

}
