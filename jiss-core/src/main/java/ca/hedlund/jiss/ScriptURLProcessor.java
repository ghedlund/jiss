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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptURLProcessor implements JissProcessor {

	private static final Logger LOGGER = Logger
			.getLogger(ScriptURLProcessor.class.getName());
	
	private final URL url;
	
	public ScriptURLProcessor(URL url) {
		super();
		this.url = url;
	}

	@Override
	public Object processCommand(JissModel jissModel, String cmd)
			throws JissError {
		final String ext = getURLExtension(url);
		if(ext == null)
			err(jissModel, "Unable to determine script extension from path '" + url.getPath() + "'");
	
		final ScriptEngine engine = jissModel.engineForExtension(ext);
		if(engine == null)
			err(jissModel, "No script engine available for extension '" + ext + "'");
		
		Object retVal = null;
		
		try {
			final InputStream is = url.openStream();
			final InputStreamReader reader = new InputStreamReader(is, "UTF-8");
			retVal = engine.eval(reader, jissModel.getScriptContext());
		} catch (IOException e) {
			err(jissModel, e);
		} catch (ScriptException e) {
			err(jissModel, e);
		}
		
		jissModel.setProcessor(new DefaultProcessor());
		
		return retVal;
	}
	
	private void err(JissModel model, String msg) throws JissError {
		model.setProcessor(new DefaultProcessor());
		throw new JissError(msg);
	}
	
	private void err(JissModel model, Throwable e) throws JissError {
		model.setProcessor(new DefaultProcessor());
		throw new JissError(e);
	}
	
	private String getURLExtension(URL url) {
		String ext = null;
		final String name = url.getPath();
		final int lastDot = name.lastIndexOf('.');
		if(lastDot > 0) {
			ext = name.substring(lastDot+1);
		}
		return ext;
	}

}
