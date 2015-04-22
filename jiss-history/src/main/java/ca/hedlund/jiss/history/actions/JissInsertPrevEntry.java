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
package ca.hedlund.jiss.history.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.history.JissHistory;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissDocument;

/**
 * Set the console prompt to be the previous history
 * entry.
 */
public class JissInsertPrevEntry extends AbstractAction {
	
	/**
	 * Weak reference to console
	 */
	private WeakReference<JissConsole> consoleRef;
	
	private final Action prevAction;
	
	public JissInsertPrevEntry(JissConsole console, Action prevAction) {
		super();
		this.consoleRef = new WeakReference<JissConsole>(console);
		this.prevAction = prevAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final JissConsole console = consoleRef.get();
		final JissModel model = console.getModel();
		final JissHistory history = model.getExtension(JissHistory.class);
		final JissDocument doc = (JissDocument)console.getDocument();
		if(doc.getPrompt().length() > 0
				&& !doc.getPrompt().equals(history.getCurrentHistoryEntry())) {
			if(prevAction != null)
				prevAction.actionPerformed(e);
		} else {
			if(console != null) {
				if(history == null) return;
				
				final String prevEntry = history.getPrevHistoryEntry();
				if(prevEntry != null) {
					final JissDocument consoleDoc = 
							JissDocument.class.cast(console.getDocument());
					consoleDoc.setPrompt(prevEntry);
				}
			}
		}
	}

}
