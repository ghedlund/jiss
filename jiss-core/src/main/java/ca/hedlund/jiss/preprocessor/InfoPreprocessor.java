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
package ca.hedlund.jiss.preprocessor;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * Handles the command 'jiss::info'
 * Which prints basic information about jiss.
 *
 */
public class InfoPreprocessor implements JissPreprocessor {
	
	private final static String INFO_TXT = "Java Interactive Scripting Shell (jiss) 0.1";

	private final static String INFO_CMD = "jiss::info";
	
	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		final String c  = cmd.toString();
		if(c.equals(INFO_CMD)) {
			// clear string
			cmd.setLength(0);
			
			final ScriptEngine se = jissModel.getScriptEngine();
			final ScriptEngineFactory seFactory = se.getFactory();

			final boolean incnl = !seFactory.getOutputStatement("").startsWith("println");
			
			final String infoCmd = seFactory.getOutputStatement(INFO_TXT + (incnl ? "\\n" : ""));
			final String langCmd = seFactory.getOutputStatement(
					"Language:" + seFactory.getLanguageName() + " " + seFactory.getLanguageVersion() + (incnl ? "\\n" : ""));
			final String engineCmd = seFactory.getOutputStatement(
					"Engine:" + seFactory.getEngineName() + " " + seFactory.getEngineVersion() + (incnl ? "\\n" : ""));
			final String program = seFactory.getProgram(infoCmd, langCmd, engineCmd);
			cmd.append(
					StringEscapeUtils.unescapeJava(program)) ;
		}
		// we want the scripting engine to handle the replaced command
		return false;
	}
	
}
