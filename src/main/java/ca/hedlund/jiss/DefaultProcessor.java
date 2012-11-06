package ca.hedlund.jiss;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Default jiss processor.
 *
 */
public class DefaultProcessor implements JissProcessor {

	@Override
	public void processCommand(JissModel jissModel, String cmd)
			throws JissError {
		final StringBuffer cmdBuffer = new StringBuffer(cmd);
		
		boolean keepProcessing = true;
		for(JissPreprocessor preprocessor:jissModel.getPreprocessors()) {
			keepProcessing &= !preprocessor.preprocessCommand(jissModel, cmdBuffer);
		}
		
		if(keepProcessing) {
			final ScriptEngine engine = jissModel.getScriptEngine();
			try {
				engine.eval(cmdBuffer.toString());
			} catch (ScriptException se) {
				throw new JissError(se);
			} catch (Exception e) {
				throw new JissError(e);
			}
		}
	}

}
