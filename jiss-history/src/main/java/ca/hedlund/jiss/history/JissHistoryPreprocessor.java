/*
 * jiss-history
 * Copyright (C) 2015, Gregory Hedlund <ghedlund@mun.ca>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
