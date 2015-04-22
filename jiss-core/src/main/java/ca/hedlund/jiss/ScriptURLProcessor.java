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
