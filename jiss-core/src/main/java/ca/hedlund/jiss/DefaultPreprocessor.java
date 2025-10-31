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

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default preprocessor that loads preprocessors using
 * {@link ServiceLoader}.
 */
public class DefaultPreprocessor implements JissPreprocessor {

	private final List<PreprocessorListener> listeners = new CopyOnWriteArrayList<>();

	public void addPreprocessorListener(PreprocessorListener listener) {
		if (listener != null && !listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removePreprocessorListener(PreprocessorListener listener) {
		listeners.remove(listener);
	}

	protected void firePreprocessingStarted(JissModel jissModel, String orig, StringBuffer cmd) {
		PreprocessorEvent event = new PreprocessorEvent(this, jissModel, orig, cmd, false);
		for (PreprocessorListener listener : listeners) {
			listener.preprocessingStarted(event);
		}
	}

	protected void firePreprocessingEnded(JissModel jissModel, String orig, StringBuffer cmd, boolean handled) {
		PreprocessorEvent event = new PreprocessorEvent(this, jissModel, orig, cmd, handled);
		for (PreprocessorListener listener : listeners) {
			listener.preprocessingEnded(event);
		}
	}

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		firePreprocessingStarted(jissModel, orig, cmd);

		final ClassLoader cl = jissModel.getClass().getClassLoader();
		final ServiceLoader<JissPreprocessor> loader =
				ServiceLoader.load(JissPreprocessor.class, cl);
		
		boolean processed = false;
		final Iterator<JissPreprocessor> itr = loader.iterator();
		while(itr.hasNext()) {
			final JissPreprocessor preprocessor = itr.next();
			processed |= preprocessor.preprocessCommand(jissModel, orig, cmd);
		}
		
		firePreprocessingEnded(jissModel, orig, cmd, processed);
		return processed;
	}

}
