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
