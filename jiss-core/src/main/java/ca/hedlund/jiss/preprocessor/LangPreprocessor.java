package ca.hedlund.jiss.preprocessor;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringEscapeUtils;

import ca.hedlund.jiss.JissContext;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * Pre-processor for built-in commands.
 *
 */
public class LangPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		final String c = cmd.toString();
		if(c.equals("::langs")) {
			cmd.setLength(0);
			printLangs(jissModel, cmd);
		} else if (c.equals("::lang")) {
			cmd.setLength(0);
			printCurrentLang(jissModel, cmd);
		} else if(c.startsWith("::lang")) {
			cmd.setLength(0);
			
			final String parts[] = c.split("\\p{Space}");
			if(parts.length == 2) {
				final String lang = parts[1];
				
				final ScriptEngineManager manager = new ScriptEngineManager(JissModel.class.getClassLoader());
				ScriptEngine newEngine = null;
				for(ScriptEngineFactory factory:manager.getEngineFactories()) {
					if(factory.getLanguageName().equals(lang)
							|| factory.getExtensions().contains(lang)
							|| factory.getMimeTypes().contains(lang)) {
						newEngine = factory.getScriptEngine();
						break;
					}
				}
				
				if(newEngine != null) {
					jissModel.setScriptEngine(newEngine);
					printCurrentLang(jissModel, cmd);
				}
			}
		}
		return false;
	}
	
	private void printCurrentLang(JissModel model, StringBuffer cmd) {
		final List<String> cmds = new ArrayList<String>();
		
		final ScriptEngineFactory factory = model.getScriptEngine().getFactory();
		final String engineInfo = 
				factory.getLanguageName() + " " + factory.getLanguageVersion() + ":" + factory.getEngineName() + " " + factory.getEngineVersion();
		cmds.add(createPrintCmd(model, engineInfo));
		
		final String prog = StringEscapeUtils.unescapeJava(	factory.getProgram(cmds.toArray(new String[0])) );
		cmd.append(prog);
	}
	
	private void printLangs(JissModel model, StringBuffer cmd) {
		final ScriptEngineManager manager = new ScriptEngineManager(JissModel.class.getClassLoader());
		final List<String> cmds = new ArrayList<String>();
		
		for(ScriptEngineFactory factory:manager.getEngineFactories()) {
			final String engineInfo = 
					factory.getLanguageName() + " " + factory.getLanguageVersion() + ":" + factory.getEngineName() + " " + factory.getEngineVersion();
			cmds.add(createPrintCmd(model, engineInfo));
		}
		final ScriptEngineFactory factory = model.getScriptEngine().getFactory();
		final String prog = StringEscapeUtils.unescapeJava( factory.getProgram(cmds.toArray(new String[0])) );
		cmd.append(prog);
	}
	
	private String createPrintCmd(JissModel model, String toPrint) {
		final ScriptEngineFactory factory = model.getScriptEngine().getFactory();
		boolean includeQuotes = factory.getOutputStatement(new String()).indexOf('\"') < 0;
		final String cmd = factory.getOutputStatement((includeQuotes ? "\"" : "") + toPrint + (includeQuotes ? "\"" : ""));
		return cmd;
	}
}
