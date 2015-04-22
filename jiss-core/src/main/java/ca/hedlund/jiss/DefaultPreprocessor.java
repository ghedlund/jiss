/*
 * jiss-core
 * Copyright (C) 2015, Gregory Hedlund <ghedlund@mun.ca>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
