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
