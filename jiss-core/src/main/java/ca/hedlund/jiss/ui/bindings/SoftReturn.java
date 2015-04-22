/*
 * jiss-core
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
package ca.hedlund.jiss.ui.bindings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.ref.WeakReference;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissDocument;

/**
 * Insert a soft-return into the console
 * using the keybinding from the property: SOFT_RETURN_KB
 * 
 * 
 */
@Extension(JissConsole.class)
public class SoftReturn extends AbstractAction implements ExtensionProvider {

	public static final String SOFT_RETURN_KB = 
			SoftReturn.class.getName() + ".keystroke";
	
	private final static String SOFT_RETURN_ACT_ID =
			SoftReturn.class.getName() + ".softReturn";
	
	/**
	 * console
	 */
	private WeakReference<JissConsole> consoleRef;
	
	public KeyStroke getKeystroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JissDocument consoleDoc = 
				JissDocument.class.cast(consoleRef.get().getDocument());
		consoleDoc.insertSoftReturn(consoleRef.get().getCaretPosition());
	}

	@Override
	public void installExtension(IExtendable obj) {
		final JissConsole console = JissConsole.class.cast(obj);
		this.consoleRef = new WeakReference<JissConsole>(console);
		
		final ActionMap actionMap = console.getActionMap();
		final InputMap inputMap = console.getInputMap(JissConsole.WHEN_FOCUSED);
		
		final KeyStroke ks = getKeystroke();
		actionMap.put(SOFT_RETURN_ACT_ID, this);
		inputMap.put(ks, SOFT_RETURN_ACT_ID);
		
		console.setActionMap(actionMap);
		console.setInputMap(JissConsole.WHEN_FOCUSED, inputMap);
	}
	
}
