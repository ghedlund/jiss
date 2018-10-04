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
