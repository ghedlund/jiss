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

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default jiss processor.
 *
 */
public class DefaultProcessor implements JissProcessor {

	private final List<ProcessorListener> listeners = new CopyOnWriteArrayList<>();

	public void addProcessorListener(ProcessorListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeProcessorListener(ProcessorListener listener) {
		listeners.remove(listener);
	}

	protected void fireProcessingStarted(JissModel jissModel, String cmd) {
		ProcessorEvent event = new ProcessorEvent(this, jissModel, cmd);
		for (ProcessorListener listener : listeners) {
			listener.processingStarted(event);
		}
	}

	protected void fireProcessingEnded(JissModel jissModel, String cmd, Object result, JissError error) {
		ProcessorEvent event = new ProcessorEvent(this, jissModel, cmd, result, error);
		for (ProcessorListener listener : listeners) {
			listener.processingEnded(event);
		}
	}

	@Override
	public Object processCommand(JissModel jissModel, String cmd)
			throws JissError {
		fireProcessingStarted(jissModel, cmd);

		Object retVal = null;
		JissError error = null;

		final ScriptEngine engine = jissModel.getScriptEngine();
		try {
			retVal = engine.eval(cmd, jissModel.getScriptContext());
		} catch (ScriptException se) {
			error = new JissError(se);
			throw error;
		} catch (Exception e) {
			error = new JissError(e);
			throw error;
		} finally {
			fireProcessingEnded(jissModel, cmd, retVal, error);
		}
		
		return retVal;
	}

}
