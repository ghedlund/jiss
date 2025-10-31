/*
 * Copyright (C) 2012-2018 Gregory Hedlund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.hedlund.jiss;

import java.util.EventObject;

/**
 * Event fired during preprocessing operations.
 */
public class PreprocessorEvent extends EventObject {

    private final JissModel jissModel;
    private final String originalCommand;
    private final StringBuffer command;
    private final boolean handled;

    public PreprocessorEvent(Object source, JissModel jissModel, String originalCommand, StringBuffer command, boolean handled) {
        super(source);
        this.jissModel = jissModel;
        this.originalCommand = originalCommand;
        this.command = command;
        this.handled = handled;
    }

    public JissModel getJissModel() {
        return jissModel;
    }

    public String getOriginalCommand() {
        return originalCommand;
    }

    public StringBuffer getCommand() {
        return command;
    }

    public boolean isHandled() {
        return handled;
    }
}
