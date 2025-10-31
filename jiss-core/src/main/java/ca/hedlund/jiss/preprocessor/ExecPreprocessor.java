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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.ScriptURLProcessor;

public class ExecPreprocessor extends AbstractPreprocessor {

	private static final Logger LOGGER = Logger
			.getLogger(ExecPreprocessor.class.getName());

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig,
			StringBuffer cmd) {
		if(orig.startsWith("::exec ")) {
			String scriptLocation = orig.substring("::exec ".length()).trim();
			if(scriptLocation.startsWith("\"")) {
				scriptLocation = scriptLocation.substring(1, 
						scriptLocation.lastIndexOf('"'));
			}
			
			URL url = null;
			try {
				url = new URL(scriptLocation);
			} catch (MalformedURLException e) {
				try {
					url = (new File(scriptLocation)).toURI().toURL();
				} catch (MalformedURLException e1) {
					LOGGER
							.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
				}
			}
			
			if(url != null) {
				jissModel.setProcessor(new ScriptURLProcessor(url));
			}
		}
		return false;
	}

}
