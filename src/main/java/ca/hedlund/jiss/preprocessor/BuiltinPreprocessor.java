package ca.hedlund.jiss.preprocessor;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * Pre-processor for built-in commands.
 *
 */
public class BuiltinPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, StringBuffer cmd) {
		final String c = cmd.toString();
		if(c.equals("jiss::langs")) {
			cmd.setLength(0);
			printLangs(jissModel, cmd);
		}
		return false;
	}
	
	private void printLangs(JissModel model, StringBuffer cmd) {
		final ScriptEngineManager manager = new ScriptEngineManager(JissModel.class.getClassLoader());
		final List<String> cmds = new ArrayList<String>();
		cmds.add(createPrintCmd(model, "Lang:\\t\\tVersion:\\t\\tEngine:"));
		
		for(ScriptEngineFactory factory:manager.getEngineFactories()) {
			final String engineInfo = 
					factory.getLanguageName() + "\\t\\t" + factory.getLanguageVersion() + "\\t\\t" + factory.getEngineName();
			cmds.add(createPrintCmd(model, engineInfo));
		}
		final ScriptEngineFactory factory = model.getScriptEngine().getFactory();
		final String prog = factory.getProgram(cmds.toArray(new String[0]));
		cmd.append(prog);
	}
	
	private String createPrintCmd(JissModel model, String toPrint) {
		final ScriptEngineFactory factory = model.getScriptEngine().getFactory();
		final boolean inclnl = !factory.getOutputStatement("").startsWith("println");
		final String cmd = factory.getOutputStatement(toPrint + (inclnl ? "\\n" : ""));
		return cmd;
	}
}
