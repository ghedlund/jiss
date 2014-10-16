package ca.hedlund.jiss.preprocessor;

import ca.hedlund.jiss.JissContext;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

public class ResetPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig,
			StringBuffer cmd) {
		if(orig.equals("::reset")) {
			// reset context
			final JissContext newContext = new JissContext();
			jissModel.setScriptContext(newContext);
			return true;
		}
		return false;
	}

}
