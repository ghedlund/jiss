package ca.hedlund.jiss.ui.bindings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
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
	
	/**
	 * console
	 */
	private WeakReference<JissConsole> consoleRef;
	
	public KeyStroke getKeystroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JissConsole console = consoleRef.get();
		final JissDocument consoleDoc = 
				JissDocument.class.cast(console.getDocument());
		final String cmd = consoleDoc.getPrompt();
		
		final PrintWriter pwOut = 
				new PrintWriter(new JissDocumentWriter(consoleDoc));
		final PrintWriter pwErr =
				new PrintWriter(new JissDocumentWriter(consoleDoc));
		final List<Runnable> tasks = new ArrayList<Runnable>();
		
		final Runnable insertNL = new Runnable() {
			
			@Override
			public void run() {
				consoleDoc.addDocumentListener(caretMover);
				consoleDoc.insertSoftReturn(consoleDoc.getLength());
			}
		};
		tasks.add(insertNL);
		
		final JissTask exeTask = 
				new JissTask(console.getModel(), cmd, pwOut, pwErr);
		tasks.add(exeTask);
		
		final Runnable prompt = new Runnable() {
			
			@Override
			public void run() {
				console.prompt();
			}
		};
		tasks.add(prompt);
		
		final Runnable removeExt = new Runnable() {
			
			@Override
			public void run() {
				console.getModel().removeExtension(JissModel.class);
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
			console.setCaretPosition(console.getDocument().getLength());
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			
		}
	};
	
}
