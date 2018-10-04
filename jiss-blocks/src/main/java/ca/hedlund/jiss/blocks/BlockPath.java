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
