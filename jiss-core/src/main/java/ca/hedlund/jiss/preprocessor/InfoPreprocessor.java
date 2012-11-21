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
