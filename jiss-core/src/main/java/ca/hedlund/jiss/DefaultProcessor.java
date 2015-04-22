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

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Default jiss processor.
 *
 */
public class DefaultProcessor implements JissProcessor {

	@Override
	public Object processCommand(JissModel jissModel, String cmd)
			throws JissError {
		Object retVal = null;
		
		final ScriptEngine engine = jissModel.getScriptEngine();
		try {
			retVal = engine.eval(cmd, jissModel.getScriptContext());
		} catch (ScriptException se) {
			throw new JissError(se);
		} catch (Exception e) {
			throw new JissError(e);
		}
		
		return retVal;
	}

}
