package ca.hedlund.jiss.ui.bindings;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissConsoleDocument;

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
	private JissConsole console;
	
	public KeyStroke getKeystroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_MASK);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JissConsoleDocument consoleDoc = 
				JissConsoleDocument.class.cast(console.getDocument());
		consoleDoc.insertSoftReturn(console.getCaretPosition());
	}

	@Override
	public void installExtension(IExtendable obj) {
		final JissConsole console = JissConsole.class.cast(obj);
		this.console = console;
		
		final ActionMap actionMap = console.getActionMap();
		final InputMap inputMap = console.getInputMap(JissConsole.WHEN_FOCUSED);
		
		final KeyStroke ks = getKeystroke();
		actionMap.put(SOFT_RETURN_ACT_ID, this);
		inputMap.put(ks, SOFT_RETURN_ACT_ID);
		
		console.setActionMap(actionMap);
		console.setInputMap(JissConsole.WHEN_FOCUSED, inputMap);
	}
	
}
