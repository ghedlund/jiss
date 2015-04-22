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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * 
 */
public class JissDocumentWriter extends Writer implements Runnable {
	
	/**
	 * Weak reference to text document
	 */
	private WeakReference<Document> docRef;
	
	private final StringBuffer buffer = new StringBuffer();
	
	private volatile boolean alreadyWaiting = false;
	
	/**
	 * Constructor
	 * 
	 * @param the document
	 */
	public JissDocumentWriter(Document doc) {
		super();
		this.docRef = new WeakReference<Document>(doc);
		buffer.setLength(0);
	}
	
	public Document getDocument() {
		return docRef.get();
	}

	@Override
	public void close() throws IOException {
		// flush in case there's anything in the buffer
		flush();
	}

	@Override
	public void flush() throws IOException {
//		synchronized (buffer) {
			if(!alreadyWaiting) {
				alreadyWaiting = true;
				SwingUtilities.invokeLater(this);
			}
//		}
	}
	
	@Override
	public void run() {
		synchronized(buffer) {
			final String txt = buffer.toString();
			buffer.setLength(0);
			alreadyWaiting = false;
			
			try {
				final Document doc = getDocument();
				doc.insertString(doc.getLength(), txt, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		synchronized(buffer) {
			for(int i = off; i < off+len; i++) {
				char c = cbuf[i];
				buffer.append(c);
				if(c == '\n') {
					flush();
				}
			}
		}
	}

}
