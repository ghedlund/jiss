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
import ca.hedlund.jiss.DefaultProcessor;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissThread;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissDocument;

/**
 * 
 */
@Extension(JissConsole.class)
public class Cancel extends AbstractAction implements ExtensionProvider {

	public static final String CANCEL_COMMAND_KB = 
			Cancel.class.getName() + ".keystroke";
	
	private final static String CANCEL_COMMAND_ACT_ID =
			Cancel.class.getName() + ".cancel";
	
	/**
	 * console
	 */
	private WeakReference<JissConsole> consoleRef;
	
	public KeyStroke getKeystroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// check to see if  there is a current thread running
		final JissConsole console = consoleRef.get();
		final JissModel model = console.getModel();
		final JissThread currentThread = model.getExtension(JissThread.class);
		if(currentThread != null) {
			if(currentThread.isAlive()) {
				currentThread.stop();
				model.setProcessor(new DefaultProcessor());
				model.removeExtension(JissThread.class);
				console.prompt();
			}
		} else {
			// cancel current insertion
			final JissDocument doc = JissDocument.class.cast(console.getDocument());
			doc.insertSoftReturn(doc.getLength());
			console.prompt();
		}
	}

	@Override
	public void installExtension(IExtendable obj) {
		final JissConsole console = JissConsole.class.cast(obj);
		consoleRef = new WeakReference<JissConsole>(console);
		
		final ActionMap actionMap = console.getActionMap();
		final InputMap inputMap = console.getInputMap(JissConsole.WHEN_FOCUSED);
		
		final KeyStroke ks = getKeystroke();
		actionMap.put(CANCEL_COMMAND_ACT_ID, this);
		inputMap.put(ks, CANCEL_COMMAND_ACT_ID);
		
		console.setActionMap(actionMap);
		console.setInputMap(JissConsole.WHEN_FOCUSED, inputMap);
	}

}
