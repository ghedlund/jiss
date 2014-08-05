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
