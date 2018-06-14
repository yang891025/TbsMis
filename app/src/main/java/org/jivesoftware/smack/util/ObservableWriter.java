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
 * An ObservableWriter is a wrapper on a Writer that notifies to its listeners when
 * writing to character streams.
 * 
 * @author Gaston Dombiak
 */
public class ObservableWriter extends Writer {

    Writer wrappedWriter;
    List listeners = new ArrayList();

    public ObservableWriter(Writer wrappedWriter) {
        this.wrappedWriter = wrappedWriter;
    }

    @Override
	public void write(char cbuf[], int off, int len) throws IOException {
        this.wrappedWriter.write(cbuf, off, len);
        String str = new String(cbuf, off, len);
        this.notifyListeners(str);
    }

    @Override
	public void flush() throws IOException {
        this.wrappedWriter.flush();
    }

    @Override
	public void close() throws IOException {
        this.wrappedWriter.close();
    }

    @Override
	public void write(int c) throws IOException {
        this.wrappedWriter.write(c);
    }

    @Override
	public void write(char cbuf[]) throws IOException {
        this.wrappedWriter.write(cbuf);
        String str = new String(cbuf);
        this.notifyListeners(str);
    }

    @Override
	public void write(String str) throws IOException {
        this.wrappedWriter.write(str);
        this.notifyListeners(str);
    }

    @Override
	public void write(String str, int off, int len) throws IOException {
        this.wrappedWriter.write(str, off, len);
        str = str.substring(off, off + len);
        this.notifyListeners(str);
    }

    /**
     * Notify that a new string has been written.
     * 
     * @param str the written String to notify 
     */
    private void notifyListeners(String str) {
        WriterListener[] writerListeners = null;
        synchronized (this.listeners) {
            writerListeners = new WriterListener[this.listeners.size()];
            this.listeners.toArray(writerListeners);
        }
        for (int i = 0; i < writerListeners.length; i++) {
            writerListeners[i].write(str);
        }
    }

    /**
     * Adds a writer listener to this writer that will be notified when
     * new strings are sent.
     *
     * @param writerListener a writer listener.
     */
    public void addWriterListener(WriterListener writerListener) {
        if (writerListener == null) {
            return;
        }
        synchronized (this.listeners) {
            if (!this.listeners.contains(writerListener)) {
                this.listeners.add(writerListener);
            }
        }
    }

    /**
     * Removes a writer listener from this writer.
     *
     * @param writerListener a writer listener.
     */
    public void removeWriterListener(WriterListener writerListener) {
        synchronized (this.listeners) {
            this.listeners.remove(writerListener);
        }
    }

}
