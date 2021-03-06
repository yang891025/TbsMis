/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smack.util;

import java.io.*;
import java.util.*;

/**
 * An ObservableReader is a wrapper on a Reader that notifies to its listeners when
 * reading character streams.
 * 
 * @author Gaston Dombiak
 */
public class ObservableReader extends Reader {

    Reader wrappedReader;
    List listeners = new ArrayList();

    public ObservableReader(Reader wrappedReader) {
        this.wrappedReader = wrappedReader;
    }
        
    @Override
	public int read(char[] cbuf, int off, int len) throws IOException {
        int count = this.wrappedReader.read(cbuf, off, len);
        if (count > 0) {
            String str = new String(cbuf, off, count);
            // Notify that a new string has been read
            ReaderListener[] readerListeners = null;
            synchronized (this.listeners) {
                readerListeners = new ReaderListener[this.listeners.size()];
                this.listeners.toArray(readerListeners);
            }
            for (int i = 0; i < readerListeners.length; i++) {
                readerListeners[i].read(str);
            }
        }
        return count;
    }

    @Override
	public void close() throws IOException {
        this.wrappedReader.close();
    }

    @Override
	public int read() throws IOException {
        return this.wrappedReader.read();
    }

    @Override
	public int read(char cbuf[]) throws IOException {
        return this.wrappedReader.read(cbuf);
    }

    @Override
	public long skip(long n) throws IOException {
        return this.wrappedReader.skip(n);
    }

    @Override
	public boolean ready() throws IOException {
        return this.wrappedReader.ready();
    }

    @Override
	public boolean markSupported() {
        return this.wrappedReader.markSupported();
    }

    @Override
	public void mark(int readAheadLimit) throws IOException {
        this.wrappedReader.mark(readAheadLimit);
    }

    @Override
	public void reset() throws IOException {
        this.wrappedReader.reset();
    }

    /**
     * Adds a reader listener to this reader that will be notified when
     * new strings are read.
     *
     * @param readerListener a reader listener.
     */
    public void addReaderListener(ReaderListener readerListener) {
        if (readerListener == null) {
            return;
        }
        synchronized (this.listeners) {
            if (!this.listeners.contains(readerListener)) {
                this.listeners.add(readerListener);
            }
        }
    }

    /**
     * Removes a reader listener from this reader.
     *
     * @param readerListener a reader listener.
     */
    public void removeReaderListener(ReaderListener readerListener) {
        synchronized (this.listeners) {
            this.listeners.remove(readerListener);
        }
    }

}
