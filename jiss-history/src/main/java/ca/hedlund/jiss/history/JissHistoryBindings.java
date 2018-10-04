/*
 * Copyright (C) 2012-2018 Gregory Hedlund
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
