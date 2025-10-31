
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
 * Event fired during processing operations.
 */
public class ProcessorEvent extends EventObject {

    private final JissModel jissModel;
    private final String command;
    private final Object result;
    private final JissError error;

    public ProcessorEvent(Object source, JissModel jissModel, String command) {
        this(source, jissModel, command, null, null);
    }

    public ProcessorEvent(Object source, JissModel jissModel, String command, Object result) {
        this(source, jissModel, command, result, null);
    }

    public ProcessorEvent(Object source, JissModel jissModel, String command, Object result, JissError error) {
        super(source);
        this.jissModel = jissModel;
        this.command = command;
        this.result = result;
        this.error = error;
    }

    public JissModel getJissModel() {
        return jissModel;
    }

    public String getCommand() {
        return command;
    }

    public Object getResult() {
        return result;
    }

    public JissError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}

