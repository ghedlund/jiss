package ca.hedlund.jiss.history;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.JissModel;

/**
 * {@link JissHistory} handles reading/writing console history.
 * Console history is available through {@link JissModel} as
 * an extension.
 * 
 * Multiple instances of {@link JissHistory} may be adding
 * entries to the same history file.
 *
 */
@XmlRootElement
@Extension(JissModel.class)
public class JissHistory {
	
	/**
	 * Keep a max of 100 entries in the history
	 * 
	 * TODO read this value from a property
	 */
	private final static int HISTORY_MAX_SIZE = 100;
	
	private List<JissHistoryEntry> entries;
	
	/**
	 * Current index of rev-iteration through the history.
	 * This is a zero-based index from the end of this list,
	 * meaning that 0 is actually the value of getSize()-1.
	 * If iteration has not been started, this value should be
	 * less than zero.
	 */
	private int itrIdx = -1;
	
	/**
	 * Constructor
	 */
	public JissHistory() {
		super();
		this.entries = new ArrayList<JissHistoryEntry>();
	}
	
	@XmlElement(name="entry")
	public List<JissHistoryEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<JissHistoryEntry> entries) {
		this.entries = entries;
	}

	/**
	 * Add a new entry to the history file.
	 * 
	 * @param cmd
	 */
	public void addToHistory(String cmd) {
		// add a new JissHistoryEntry to the list of entries
		// remove entries from beginning of list to ensure max size is maintained
		final JissHistoryEntry entry = new JissHistoryEntry();
		entry.setSource(cmd);
		entry.setTimestamp(System.currentTimeMillis());
		
		getEntries().add(entry);
		
		while(getEntries().size() > HISTORY_MAX_SIZE) {
			getEntries().remove(0);
		}
	}

	/**
	 * Returns the next value in the reverse iteration of the
	 * history.  Will begin iteration if it has not started.
	 * 
	 * @return the value of the next history entry, <code>null</code> if
	 *  the end of the iteration has been reached
	 */
	public String getNextHistoryEntry() {
		itrIdx++;
		final int realIdx = entries.size() - itrIdx - 1;
		String retVal = null;
		
		if(realIdx >= 0 && realIdx < entries.size()) {
			final JissHistoryEntry entry = entries.get(realIdx);
			retVal = entry.getSource();
		}
		return retVal;
	}
	
	/**
	 * Get current history value.
	 * 
	 * @return current history value
	 */
	public String getCurrentHistoryEntry() {
		final int realIdx = entries.size() - itrIdx - 1;
		String retVal = null;
		
		if(realIdx >= 0 && realIdx < entries.size()) {
			final JissHistoryEntry entry = entries.get(realIdx);
			retVal = entry.getSource();
		}
		
		return retVal;
	}
	
	/**
	 * Returns the previous value in the reverse iteration of the
	 * history.
	 * 
	 * @return the value of the prev history entry, <code>null</code> if
	 *  the end of the iteration has been reached
	 */
	public String getPrevHistoryEntry() {
		itrIdx--;
		final int realIdx = entries.size() - itrIdx - 1;
		String retVal = null;
		
		if(realIdx >= 0 && realIdx < entries.size()) {
			final JissHistoryEntry entry = entries.get(realIdx);
			retVal = entry.getSource();
		}
		return retVal;
	}
	
	/**
	 * Reset history iteration.
	 */
	public void resetIterator() {
		this.itrIdx = -1;
	}
	
}
