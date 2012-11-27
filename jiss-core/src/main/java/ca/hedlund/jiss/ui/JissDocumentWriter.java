package ca.hedlund.jiss.ui;

import java.io.IOException;
import java.io.Writer;
import java.lang.ref.WeakReference;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * 
 */
public class JissDocumentWriter extends Writer {
	
	/**
	 * Weak reference to text document
	 */
	private WeakReference<Document> docRef;
	
	private final StringBuffer buffer = new StringBuffer();
	
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
		final String txt = buffer.toString();
		buffer.setLength(0);
		if(txt.length() > 0) {
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
		for(int i = off; i < off+len; i++) {
			char c = cbuf[i];
			buffer.append(c);
			if(c == '\n') {
				flush();
			}
		}
	}

}
