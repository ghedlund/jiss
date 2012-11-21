package ca.hedlund.jiss.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

public class JissConsoleDocument extends DefaultStyledDocument {
	
	/**
	 * Prompt location
	 */
	private int promptLocation = -1;

	public JissConsoleDocument() {
		super();
	}

	public JissConsoleDocument(Content arg0, StyleContext arg1) {
		super(arg0, arg1);
	}

	public JissConsoleDocument(StyleContext arg0) {
		super(arg0);
	}
	
	/**
	 * Sets the current prompt location to the end of the 
	 * document.
	 */
	public void markPromptLocation() {
		promptLocation = getLength();
	}
	
	public int getPromptLocation() {
		return promptLocation;
	}
	
	/**
	 * Clear text
	 */
	public void clear() {
		try {
			super.remove(0, getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the string fragment for the current prompt.
	 */
	public String getPrompt() {
		String retVal = "";
		try {
			retVal = getText(promptLocation, getLength()-promptLocation);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	@Override
	public void insertString(int arg0, String arg1, AttributeSet arg2)
			throws BadLocationException {
		if(arg0 >= promptLocation) {
			super.insertString(arg0, arg1, arg2);
		}
	}

	@Override
	public void remove(int arg0, int arg1) throws BadLocationException {
		if(arg0 >= promptLocation) {
			super.remove(arg0, arg1);
		}
	}
	
	/**
	 * Insert a soft return into the console
	 */
	public void insertSoftReturn(int pos) {
		try {
			super.insertString(pos, System.getProperty("line.separator"), null);
		} catch (BadLocationException be) {
			be.printStackTrace();
		}
	}
	
}
