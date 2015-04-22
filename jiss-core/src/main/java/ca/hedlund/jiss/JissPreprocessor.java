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
package ca.hedlund.jiss;

/**
 * Interface allowing custom pre-processing of
 * user inputted commands.
 *
 */
public interface JissPreprocessor {
	
	/**
	 * Pre-process the provided command. 
	 * The provided string buffer may be changed by the
	 * pre-processor.
	 * 
	 * @param jissModel
	 * @param orig the original command
	 * @param cmd the command as a mutable string
	 *  buffer.
	 * 
	 * @return <code>true</code> iff the command was handled
	 *  by the pre-processor and no further processing is
	 *  requried (i.e., the command should not be sent to the 
	 *  script engine.)
	 */
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd);

}
