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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the path and name of a block.
 * 
 */
public class BlockPath {

	/**
	 * Path parts
	 */
	private final List<String> parts;
	
	/**
	 * Constructor
	 */
	public BlockPath() {
		parts = new ArrayList<String>();
	}
	
	public BlockPath(String path) {
		this(path.split("/"));
	}
	
	public BlockPath(String... part) {
		parts = Arrays.asList(part);
	}
	
	public void setPath(String... path) {
		parts.clear();
		for(String p:path) {
			parts.add(p);
		}
	}
	
	public void setPath(List<String> parts) {
		this.parts.clear();
		this.parts.addAll(parts);
	}
	
	public BlockPath pathByAddingPart(String part) {
		final BlockPath retVal = new BlockPath();
		retVal.parts.addAll(this.parts);
		retVal.parts.add(part);
		return retVal;
	}
	
	public BlockPath pathByRemovingPart(String part) {
		final BlockPath retVal = new BlockPath();
		retVal.parts.addAll(this.parts);
		retVal.parts.remove(part);
		return retVal;
	}
	
	public BlockPath pathByRemovingPart(int idx) {
		final BlockPath retVal = new BlockPath();
		retVal.parts.addAll(this.parts);
		retVal.parts.remove(idx);
		return retVal;
	}
	
	public String getPath() {
		return getPath(File.separator);
	}
	
	public String getPath(String separator) {
		final StringBuffer buffer = new StringBuffer();
		for(String part:this.parts) {
			if(buffer.length() > 0) buffer.append(separator);
			buffer.append(part);
		}
		return buffer.toString();
	}
}
