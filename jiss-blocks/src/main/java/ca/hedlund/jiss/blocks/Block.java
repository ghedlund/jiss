package ca.hedlund.jiss.blocks;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class representing a saved block of code.
 *
 *
 */
@XmlRootElement
public class Block {

	/**
	 * Mimetype
	 */
	private String mimetype;
	
	/**
	 * source
	 */
	private String source;
	
	/**
	 * date created
	 */
	private Date created;
	
	public Block() {
		super();
	}

	public Block(String mimetype, String source) {
		super();
		this.mimetype = mimetype;
		this.source = source;
	}
	
	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public Date getCreated() {
		return this.created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
}
