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
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.JissContext;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissTask;
import ca.hedlund.jiss.JissThread;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissDocument;
import ca.hedlund.jiss.ui.JissDocumentWriter;

/**
 * Executes the current prompt
 */
@Extension(JissConsole.class)
public class RunCommand extends AbstractAction implements ExtensionProvider {

	public static final String RUN_COMMAND_KB = 
			RunCommand.class.getName() + ".keystroke";
	
	private final static String RUN_COMMAND_ACT_ID =
			RunCommand.class.getName() + ".runCommand";
	
	private String cmd;
	
	/**
	 * console
	 */
	private WeakReference<JissConsole> consoleRef;
	
	public RunCommand() {
		super();
	}
	
	public RunCommand(JissConsole console, String cmd) {
		super();
		consoleRef = new WeakReference<JissConsole>(console);
		this.cmd = cmd;
	}
	
	public KeyStroke getKeystroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	}

	
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		runCommand();
	}
	
	public void runCommand() {
		final JissConsole console = consoleRef.get();
		final JissModel model = console.getModel();
		final JissThread currentThread = model.getExtension(JissThread.class);
		// don't do anythying if we are already processing a command
		if(currentThread != null) return;
		
		final JissDocument consoleDoc = 
				JissDocument.class.cast(console.getDocument());
		final String cmd = 
				(this.cmd == null ? consoleDoc.getPrompt() : this.cmd);
		
		final PrintWriter pwOut = 
				new PrintWriter(new JissDocumentWriter(consoleDoc));
		final PrintWriter pwErr =
				new PrintWriter(new JissDocumentWriter(consoleDoc));
		final List<Runnable> tasks = new ArrayList<Runnable>();
		
		consoleDoc.insertSoftReturn(consoleDoc.getLength());
		
		final Runnable insertNL = new Runnable() {
			
			@Override
			public void run() {
				consoleDoc.addDocumentListener(caretMover);
			}
		};
		tasks.add(insertNL);
		
		final JissTask exeTask = 
				new JissTask(console.getModel(), cmd, pwOut, pwErr);
		tasks.add(exeTask);
		
		final Runnable prompt = new Runnable() {
			
			@Override
			public void run() {
				// check for JissContext.__prompt_val and
				// setup prompt text accordingly
				final ScriptContext context = console.getModel().getScriptContext();
				final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
				final Object promptVal = bindings.get(JissContext.PROMPT_VALUE);
				console.prompt(promptVal != null ? promptVal.toString() : "");
				bindings.put(JissContext.PROMPT_VALUE, null);
			}
		};
		tasks.add(prompt);
		
		final Runnable removeExt = new Runnable() {
			
			@Override
			public void run() {
				console.getModel().removeExtension(JissThread.class);
				consoleDoc.removeDocumentListener(caretMover);
			}
		};
		tasks.add(removeExt);
		
		final JissThread th = new JissThread(tasks);
		console.getModel().putExtension(JissThread.class, th);
		th.start();
	}

	@Override
	public void installExtension(IExtendable obj) {
		final JissConsole console = JissConsole.class.cast(obj);
		this.consoleRef = new WeakReference<JissConsole>(console);
		
		final ActionMap actionMap = console.getActionMap();
		final InputMap inputMap = console.getInputMap(JissConsole.WHEN_FOCUSED);
		
		final KeyStroke ks = getKeystroke();
		actionMap.put(RUN_COMMAND_ACT_ID, this);
		inputMap.put(ks, RUN_COMMAND_ACT_ID);
		
		console.setActionMap(actionMap);
		console.setInputMap(JissConsole.WHEN_FOCUSED, inputMap);
	}
	
	private DocumentListener caretMover = new DocumentListener() {
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			
		}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			final JissConsole console = consoleRef.get();
//			final Runnable onEDT = new Runnable() { 
//				public void run() {
					console.setCaretPosition(console.getDocument().getLength());
//				}
//			};
//			SwingUtilities.invokeLater(onEDT);
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			
		}
	};
	
}
