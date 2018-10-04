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
