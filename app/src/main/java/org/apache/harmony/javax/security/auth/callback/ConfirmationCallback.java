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

package org.apache.harmony.javax.security.auth.callback;

import java.io.Serializable;



public class ConfirmationCallback implements Callback, Serializable {

    private static final long serialVersionUID = -9095656433782481624L;

    public static final int YES = 0; // default options

    public static final int NO = 1;

    public static final int CANCEL = 2;

    public static final int OK = 3;

    public static final int YES_NO_OPTION = 0; // options type

    public static final int YES_NO_CANCEL_OPTION = 1;

    public static final int OK_CANCEL_OPTION = 2;

    public static final int UNSPECIFIED_OPTION = -1;

    public static final int INFORMATION = 0; // messages type

    public static final int WARNING = 1;

    public static final int ERROR = 2;

    private String prompt;

    private final int messageType;

    private int optionType = ConfirmationCallback.UNSPECIFIED_OPTION;

    private final int defaultOption;

    private String[] options;

    private int selection;

    public ConfirmationCallback(int messageType, int optionType, int defaultOption) {
        if (messageType > ConfirmationCallback.ERROR || messageType < ConfirmationCallback.INFORMATION) {
            throw new IllegalArgumentException("auth.16"); //$NON-NLS-1$
        }

        switch (optionType) {
            case ConfirmationCallback.YES_NO_OPTION:
                if (defaultOption != ConfirmationCallback.YES && defaultOption != ConfirmationCallback.NO) {
                    throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
                }
                break;
            case ConfirmationCallback.YES_NO_CANCEL_OPTION:
                if (defaultOption != ConfirmationCallback.YES && defaultOption != ConfirmationCallback.NO && defaultOption != ConfirmationCallback.CANCEL) {
                    throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
                }
                break;
            case ConfirmationCallback.OK_CANCEL_OPTION:
                if (defaultOption != ConfirmationCallback.OK && defaultOption != ConfirmationCallback.CANCEL) {
                    throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
                }
                break;
            default:
                throw new IllegalArgumentException("auth.18"); //$NON-NLS-1$
        }
        this.messageType = messageType;
        this.optionType = optionType;
        this.defaultOption = defaultOption;
    }

    public ConfirmationCallback(int messageType, String[] options, int defaultOption) {
        if (messageType > ConfirmationCallback.ERROR || messageType < ConfirmationCallback.INFORMATION) {
            throw new IllegalArgumentException("auth.16"); //$NON-NLS-1$
        }

        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("auth.1A"); //$NON-NLS-1$
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i] == null || options[i].length() == 0) {
                throw new IllegalArgumentException("auth.1A"); //$NON-NLS-1$
            }
        }
        if (0 > defaultOption || defaultOption >= options.length) {
            throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
        }
        // FIXME:System.arraycopy(options, 0 , new String[this.options.length],
        // 0, this.options.length);
        this.options = options;
        this.defaultOption = defaultOption;
        this.messageType = messageType;
    }

    public ConfirmationCallback(String prompt, int messageType, int optionType,
            int defaultOption) {
        if (prompt == null || prompt.length() == 0) {
            throw new IllegalArgumentException("auth.14"); //$NON-NLS-1$
        }

        if (messageType > ConfirmationCallback.ERROR || messageType < ConfirmationCallback.INFORMATION) {
            throw new IllegalArgumentException("auth.16"); //$NON-NLS-1$
        }

        switch (optionType) {
            case ConfirmationCallback.YES_NO_OPTION:
                if (defaultOption != ConfirmationCallback.YES && defaultOption != ConfirmationCallback.NO) {
                    throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
                }
                break;
            case ConfirmationCallback.YES_NO_CANCEL_OPTION:
                if (defaultOption != ConfirmationCallback.YES && defaultOption != ConfirmationCallback.NO && defaultOption != ConfirmationCallback.CANCEL) {
                    throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
                }
                break;
            case ConfirmationCallback.OK_CANCEL_OPTION:
                if (defaultOption != ConfirmationCallback.OK && defaultOption != ConfirmationCallback.CANCEL) {
                    throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
                }
                break;
            default:
                throw new IllegalArgumentException("auth.18"); //$NON-NLS-1$
        }
        this.prompt = prompt;
        this.messageType = messageType;
        this.optionType = optionType;
        this.defaultOption = defaultOption;
    }

    public ConfirmationCallback(String prompt, int messageType, String[] options,
            int defaultOption) {
        if (prompt == null || prompt.length() == 0) {
            throw new IllegalArgumentException("auth.14"); //$NON-NLS-1$
        }

        if (messageType > ConfirmationCallback.ERROR || messageType < ConfirmationCallback.INFORMATION) {
            throw new IllegalArgumentException("auth.16"); //$NON-NLS-1$
        }

        if (options == null || options.length == 0) {
            throw new IllegalArgumentException("auth.1A"); //$NON-NLS-1$
        }
        for (int i = 0; i < options.length; i++) {
            if (options[i] == null || options[i].length() == 0) {
                throw new IllegalArgumentException("auth.1A"); //$NON-NLS-1$
            }
        }
        if (0 > defaultOption || defaultOption >= options.length) {
            throw new IllegalArgumentException("auth.17"); //$NON-NLS-1$
        }
        // FIXME:System.arraycopy(options, 0 , new String[this.options.length],
        // 0, this.options.length);
        this.options = options;
        this.defaultOption = defaultOption;
        this.messageType = messageType;
        this.prompt = prompt;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public int getDefaultOption() {
        return this.defaultOption;
    }

    public String[] getOptions() {
        return this.options;
    }

    public int getOptionType() {
        return this.optionType;
    }

    public int getSelectedIndex() {
        return this.selection;
    }

    public void setSelectedIndex(int selection) {
        if (this.options != null) {
            if (0 <= selection && selection <= this.options.length) {
                this.selection = selection;
            } else {
                throw new ArrayIndexOutOfBoundsException("auth.1B"); //$NON-NLS-1$
            }
        } else {
            switch (this.optionType) {
                case ConfirmationCallback.YES_NO_OPTION:
                    if (selection != ConfirmationCallback.YES && selection != ConfirmationCallback.NO) {
                        throw new IllegalArgumentException("auth.19"); //$NON-NLS-1$
                    }
                    break;
                case ConfirmationCallback.YES_NO_CANCEL_OPTION:
                    if (selection != ConfirmationCallback.YES && selection != ConfirmationCallback.NO && selection != ConfirmationCallback.CANCEL) {
                        throw new IllegalArgumentException("auth.19"); //$NON-NLS-1$
                    }
                    break;
                case ConfirmationCallback.OK_CANCEL_OPTION:
                    if (selection != ConfirmationCallback.OK && selection != ConfirmationCallback.CANCEL) {
                        throw new IllegalArgumentException("auth.19"); //$NON-NLS-1$
                    }
                    break;
            }
            this.selection = selection;
        }
    }
}
