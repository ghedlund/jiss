package ca.hedlund.jiss.history;

import java.io.IOException;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * Adds commands to history.
 *
 */
public class JissHistoryPreprocessor implements JissPreprocessor {
	
	final JissHistoryManager historyManager = new JissHistoryManager();

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		final JissHistory history = jissModel.getExtension(JissHistory.class);
		if(history != null && orig.length() > 0) {
			history.addToHistory(orig);
			history.resetIterator();
			try {
				historyManager.saveHistory(history);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
