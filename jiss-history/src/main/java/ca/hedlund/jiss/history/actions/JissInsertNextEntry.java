package ca.hedlund.jiss.history.actions;

import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;

import javax.swing.AbstractAction;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.history.JissHistory;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissDocument;

public class JissInsertNextEntry extends AbstractAction {

	/**
	 * Weak reference to console
	 */
	private WeakReference<JissConsole> consoleRef;
	
	public JissInsertNextEntry(JissConsole console) {
		super();
		this.consoleRef = new WeakReference<JissConsole>(console);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final JissConsole console = consoleRef.get();
		if(console != null) {
			final JissModel model = console.getModel();
			final JissHistory history = model.getExtension(JissHistory.class);
			if(history == null) return;
			
			final String prevEntry = history.getNextHistoryEntry();
			if(prevEntry != null) {
				final JissDocument consoleDoc = 
						JissDocument.class.cast(console.getDocument());
				consoleDoc.setPrompt(prevEntry);
			}
		}
	}

}
