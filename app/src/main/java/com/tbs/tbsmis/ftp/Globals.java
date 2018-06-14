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

import java.io.File;

import android.content.Context;

public class Globals {
    private static Context context;
    private static String lastError;
    private static File chrootDir = new File(Defaults.chrootDir);
    private static ProxyConnector proxyConnector;
    private static String username;

    public static ProxyConnector getProxyConnector() {
        if(Globals.proxyConnector != null) {
            if(!Globals.proxyConnector.isAlive()) {
                return null;
            }
        }
        return Globals.proxyConnector;
    }

    public static void setProxyConnector(ProxyConnector proxyConnector) {
        Globals.proxyConnector = proxyConnector;
    }

    public static File getChrootDir() {
        return Globals.chrootDir;
    }

    public static void setChrootDir(File chrootDir) {
        if(chrootDir.isDirectory()) {
            Globals.chrootDir = chrootDir;
        }
    }

    public static String getLastError() {
        return Globals.lastError;
    }

    public static void setLastError(String lastError) {
        Globals.lastError = lastError;
    }

    public static Context getContext() {
        return Globals.context;
    }

    public static void setContext(Context context) {
        if(context != null) {
            Globals.context = context;
        }
    }

    public static String getUsername() {
        return Globals.username;
    }

    public static void setUsername(String username) {
        Globals.username = username;
    }

}
