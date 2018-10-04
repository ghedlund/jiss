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
