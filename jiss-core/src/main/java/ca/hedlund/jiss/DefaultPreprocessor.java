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

/**
 * Default preprocessor that loads preprocessors using
 * {@link ServiceLoader}.
 */
public class DefaultPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		final ClassLoader cl = jissModel.getClass().getClassLoader();
		final ServiceLoader<JissPreprocessor> loader =
				ServiceLoader.load(JissPreprocessor.class, cl);
		
		boolean processed = false;
		final Iterator<JissPreprocessor> itr = loader.iterator();
		while(itr.hasNext()) {
			final JissPreprocessor preprocessor = itr.next();
			processed |= preprocessor.preprocessCommand(jissModel, orig, cmd);
		}
		
		return processed;
	}

}
