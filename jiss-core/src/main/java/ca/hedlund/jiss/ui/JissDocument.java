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
