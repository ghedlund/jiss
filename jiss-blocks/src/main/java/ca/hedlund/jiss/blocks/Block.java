/*
 * jiss-blocks
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
