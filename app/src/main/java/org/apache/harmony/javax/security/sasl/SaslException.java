/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.harmony.javax.security.sasl;

import java.io.IOException;

public class SaslException extends IOException {

    private static final long serialVersionUID = 4579784287983423626L;

    /**
     * Serialized field for storing initial cause
     */
    private Throwable _exception;

    public SaslException() {
    }

    public SaslException(String detail) {
        super(detail);
    }

    public SaslException(String detail, Throwable ex) {
        super(detail);
        if (ex != null) {
            super.initCause(ex);
            this._exception = ex;
        }
    }

    @Override
    public Throwable getCause() {
        return this._exception;
    }

    @Override
    public Throwable initCause(Throwable cause) {
        super.initCause(cause);
        this._exception = cause;
        return this;
    }

    @Override
    public String toString() {
        if (this._exception == null) {
            return super.toString();
        }
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(", caused by: "); //$NON-NLS-1$
        sb.append(this._exception);
        return sb.toString();
    }
}
