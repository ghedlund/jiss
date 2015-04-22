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
package ca.hedlund.jiss.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;

public class JissDocument extends DefaultStyledDocument {
	
	/**
	 * Prompt location
	 */
	private int promptLocation = -1;

	public JissDocument() {
		super();
	}

	public JissDocument(Content arg0, StyleContext arg1) {
		super(arg0, arg1);
	}

	public JissDocument(StyleContext arg0) {
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

	/**
	 * Set the prompt to the given value.
	 * 
	 * @param prompt
	 */
	public void setPrompt(String prompt) {
		try {
			final int remLen = getLength() - getPromptLocation();
			remove(getPromptLocation(), remLen);
			insertString(getPromptLocation(), prompt, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
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
