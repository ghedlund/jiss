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
package ca.hedlund.jiss.preprocessor;

import java.util.ArrayList;
import java.util.List;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.PreprocessorEvent;
import ca.hedlund.jiss.PreprocessorListener;

/**
 * Abstract base class for preprocessors that provides basic listener handling.
 */
public abstract class AbstractPreprocessor implements JissPreprocessor {

	private final List<PreprocessorListener> listeners = new ArrayList<>();

	@Override
	public void addPreprocessorListener(PreprocessorListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removePreprocessorListener(PreprocessorListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Get all registered listeners.
	 *
	 * @return list of listeners
	 */
	protected List<PreprocessorListener> getListeners() {
		return new ArrayList<>(listeners);
	}

	/**
	 * Fire preprocessing started event to all listeners.
	 *
	 * @param jissModel the Jiss model
	 * @param originalCommand the original command being preprocessed
	 * @param command the command buffer
	 */
	protected void firePreprocessingStarted(JissModel jissModel, String originalCommand, StringBuffer command) {
		PreprocessorEvent event = new PreprocessorEvent(this, jissModel, originalCommand, command, false);
		for (PreprocessorListener listener : getListeners()) {
			listener.preprocessingStarted(event);
		}
	}

	/**
	 * Fire preprocessing completed event to all listeners.
	 *
	 * @param jissModel the Jiss model
	 * @param originalCommand the original command
	 * @param command the processed command buffer
	 * @param handled whether the command was handled
	 */
	protected void firePreprocessingEnded(JissModel jissModel, String originalCommand, StringBuffer command, boolean handled) {
		PreprocessorEvent event = new PreprocessorEvent(this, jissModel, originalCommand, command, handled);
		for (PreprocessorListener listener : getListeners()) {
			listener.preprocessingEnded(event);
		}
	}

}
