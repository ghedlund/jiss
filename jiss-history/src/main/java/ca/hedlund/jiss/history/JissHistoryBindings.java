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

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.history.actions.JissInsertNextEntry;
import ca.hedlund.jiss.history.actions.JissInsertPrevEntry;
import ca.hedlund.jiss.ui.JissConsole;

/**
 * Installs the history keyboard bindings
 * on the console.
 * 
 */
@Extension(JissConsole.class)
public class JissHistoryBindings implements ExtensionProvider {

	@Override
	public void installExtension(IExtendable obj) {
		final JissConsole console = JissConsole.class.cast(obj);
		
		final ActionMap actionMap = console.getActionMap();
		final InputMap inputMap = console.getInputMap(JissConsole.WHEN_FOCUSED);
		
		// add prev entry binding
		final String prevEntryId = "_prev_history_entry_";
		final KeyStroke prevKs = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
		final JissInsertPrevEntry prevEntry = new JissInsertPrevEntry(console, 
				actionMap.get(inputMap.get(prevKs)));
		actionMap.put(prevEntryId, prevEntry);
		inputMap.put(prevKs, prevEntryId);
		
		final String nextEntryId = "_next_history_entry_";
		final KeyStroke nextKs = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		final JissInsertNextEntry nextEntry = new JissInsertNextEntry(console,
				actionMap.get(inputMap.get(nextKs)));
		actionMap.put(nextEntryId, nextEntry);
		inputMap.put(nextKs, nextEntryId);
		
		console.setActionMap(actionMap);
		console.setInputMap(JissConsole.WHEN_FOCUSED, inputMap);
	}

}
