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
package ca.hedlund.jiss.blocks.preprocessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;

import ca.hedlund.jiss.JissContext;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.blocks.Block;
import ca.hedlund.jiss.blocks.BlockManager;
import ca.hedlund.jiss.blocks.BlockPath;

public class LoadBlockPreprocessor implements JissPreprocessor {

	private final static String LOAD_BLOCK_REGEX = 
			"\\:\\:block ([ a-zA-Z/_.]*)";
	
	private final Pattern LOAD_BLOCK_PATTERN =
			Pattern.compile(LOAD_BLOCK_REGEX);
	
	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig,
			StringBuffer cmd) {
		// check prefix first 
		if(!orig.startsWith("::block ")) return false;
		
		final ScriptContext context = jissModel.getScriptContext();
		final PrintWriter errWriter = new PrintWriter(context.getErrorWriter());
		
		final Matcher matcher = LOAD_BLOCK_PATTERN.matcher(orig);
		if(matcher.matches()) {
			final String blockPathString = matcher.group(1);
			final BlockPath bp = new BlockPath(blockPathString);
			final BlockManager blockManager = new BlockManager();
			try {
				final Block block = blockManager.loadBlock(bp);
				final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
				bindings.put(JissContext.PROMPT_VALUE, block.getSource());
			} catch (IOException e) {
				errWriter.println(e.getLocalizedMessage());
			}
		} else {
			errWriter.println("Syntax error.  String must match pattern: " + LOAD_BLOCK_REGEX);
		}
		
		// prevent from running in script engine
		return true;
	}

}
