package ca.hedlund.jiss.history;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * Adds commands to history.
 *
 */
public class JissHistoryPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		final JissHistory history = jissModel.getExtension(JissHistory.class);
		if(history != null) {
			history.addToHistory(orig);
		}
		return false;
	}

}
