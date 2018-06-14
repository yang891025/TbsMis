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



public class ChoiceCallback implements Callback, Serializable {

    private static final long serialVersionUID = -3975664071579892167L;

    private int defaultChoice;

    private String prompt;

    private final boolean multipleSelectionsAllowed;

    private String[] choices;

    private int[] selections;

    private void setChoices(String[] choices) {
        if (choices == null || choices.length == 0) {
            throw new IllegalArgumentException("auth.1C"); //$NON-NLS-1$
        }
        for (int i = 0; i < choices.length; i++) {
            if (choices[i] == null || choices[i].length() == 0) {
                throw new IllegalArgumentException("auth.1C"); //$NON-NLS-1$
            }
        }
        //FIXME: System.arraycopy(choices, 0 , new String[choices.length], 0, choices.length);
        this.choices = choices;

    }

    private void setPrompt(String prompt) {
        if (prompt == null || prompt.length() == 0) {
            throw new IllegalArgumentException("auth.14"); //$NON-NLS-1$
        }
        this.prompt = prompt;
    }

    private void setDefaultChoice(int defaultChoice) {
        if (0 > defaultChoice || defaultChoice >= this.choices.length) {
            throw new IllegalArgumentException("auth.1D"); //$NON-NLS-1$
        }
        this.defaultChoice = defaultChoice;
    }

    public ChoiceCallback(String prompt, String[] choices, int defaultChoice,
            boolean multipleSelectionsAllowed) {
        this.setPrompt(prompt);
        this.setChoices(choices);
        this.setDefaultChoice(defaultChoice);
        this.multipleSelectionsAllowed = multipleSelectionsAllowed;
    }

    public boolean allowMultipleSelections() {
        return this.multipleSelectionsAllowed;
    }

    public String[] getChoices() {
        return this.choices;
    }

    public int getDefaultChoice() {
        return this.defaultChoice;
    }

    public String getPrompt() {
        return this.prompt;
    }

    public int[] getSelectedIndexes() {
        return this.selections;
    }

    public void setSelectedIndex(int selection) {
        selections = new int[1];
        selections[0] = selection;
    }

    public void setSelectedIndexes(int[] selections) {
        if (!this.multipleSelectionsAllowed) {
            throw new UnsupportedOperationException();
        }
        this.selections = selections;
        //FIXME: 
        // this.selections = new int[selections.length]
        //System.arraycopy(selections, 0, this.selections, 0, this.selections.length);
    }
}
