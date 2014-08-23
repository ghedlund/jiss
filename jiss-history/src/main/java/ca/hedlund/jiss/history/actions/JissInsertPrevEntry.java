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
