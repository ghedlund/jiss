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
		final StringBuffer cmdBuffer = new StringBuffer(cmd);
		
		boolean keepProcessing = true;
		for(JissPreprocessor preprocessor:jissModel.getPreprocessors()) {
			keepProcessing &= !preprocessor.preprocessCommand(jissModel, cmd, cmdBuffer);
		}
		
		if(keepProcessing) {
			final ScriptEngine engine = jissModel.getScriptEngine();
			try {
				retVal = engine.eval(cmdBuffer.toString(), jissModel.getScriptContext());
			} catch (ScriptException se) {
				throw new JissError(se);
			} catch (Exception e) {
				throw new JissError(e);
			}
		}
		return retVal;
	}

}
