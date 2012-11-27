package ca.hedlund.jiss.history;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * History entry.
 */
@XmlType(propOrder={"timestamp", "source"})
public class JissHistoryEntry {

	private long timestamp;
	
	private String source;
	
	public JissHistoryEntry() {
		super();
	}

	@XmlAttribute
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@XmlValue
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
}
