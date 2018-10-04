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
package ca.hedlund.jiss.blocks.preprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.apache.commons.lang3.StringEscapeUtils;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.blocks.BlockManager;

public class ListBlocksPreprocessor implements JissPreprocessor {

	private final static String LIST_BLOCKS_REGEX = 
			"\\:\\:block list( .*)?";
	
	private final Pattern LIST_BLOCKS_PATTERN =
			Pattern.compile(LIST_BLOCKS_REGEX);

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig,
			StringBuffer cmd) {
		if(!orig.startsWith("::block list")) return false;
		
		final ScriptEngine se = jissModel.getScriptEngine();
		final ScriptEngineFactory seFactory = se.getFactory();

		final boolean incnl = !seFactory.getOutputStatement("").startsWith("println");
		
		final List<String> blockInfoCmds = new ArrayList<String>();
		blockInfoCmds.add(seFactory.getOutputStatement("Available blocks:" + (incnl ? "\\n" : "")));
		
		final Matcher matcher = LIST_BLOCKS_PATTERN.matcher(orig);
		if(matcher.matches()) {
			final String blockNameRegex = matcher.group(1);
			final BlockManager bm = new BlockManager();
			
			for(String blockName:bm.getBlocks()) {
				boolean printBlock = true;
				if(blockNameRegex != null) {
					printBlock = blockName.matches(blockNameRegex);
				}
				if(printBlock) {
					blockInfoCmds.add(seFactory.getOutputStatement(blockName) + (incnl ? "\\n" : ""));
				}
			}
		}
		
		final String program = seFactory.getProgram(blockInfoCmds.toArray(new String[0]));
		cmd.setLength(0);
		cmd.append(
				StringEscapeUtils.unescapeJava(program)) ;
		
		return false;
	}
	
}
