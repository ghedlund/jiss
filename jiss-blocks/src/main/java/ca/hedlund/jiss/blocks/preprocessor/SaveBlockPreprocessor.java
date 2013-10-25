package ca.hedlund.jiss.blocks.preprocessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.blocks.Block;
import ca.hedlund.jiss.blocks.BlockManager;
import ca.hedlund.jiss.blocks.BlockPath;
import ca.hedlund.jiss.history.JissHistory;
import ca.hedlund.jiss.history.JissHistoryEntry;

public class SaveBlockPreprocessor implements JissPreprocessor {

	private final static String SAVE_BLOCK_REGEX = 
			"jiss\\:\\:block save ([ a-zA-Z/_.]*)";
	
	private final Pattern SAVE_BLOCK_PATTERN =
			Pattern.compile(SAVE_BLOCK_REGEX);
	
	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig,
			StringBuffer cmd) {
		// check prefix first 
		if(!orig.startsWith("jiss::block save")) return false;
		
		final ScriptContext context = jissModel.getScriptContext();
		final PrintWriter errWriter = new PrintWriter(context.getErrorWriter());
		final PrintWriter outWriter = new PrintWriter(context.getWriter());
		
		final Matcher matcher = SAVE_BLOCK_PATTERN.matcher(orig);
		if(matcher.matches()) {
			// get history from model
			final JissHistory history = jissModel.getExtension(JissHistory.class);
			if(history != null) {
				final List<JissHistoryEntry> entries = history.getEntries();
				if(entries.size() > 0) {
					final JissHistoryEntry lastEntry = entries.get(entries.size()-1);
					
					final ScriptEngine scriptEngine = jissModel.getScriptEngine();
					final ScriptEngineFactory scriptEngineFactory = scriptEngine.getFactory();
					final String mimetype = scriptEngineFactory.getMimeTypes().get(0);
					
					final Block block = new Block(mimetype, lastEntry.getSource());
					final String blockPathString = matcher.group(1);
					final BlockPath bp = new BlockPath(blockPathString);
					final BlockManager blockManager = new BlockManager();
					try {
						blockManager.saveBlock(bp, block);
						outWriter.println("Most recent history entry saved as block '" + blockPathString + "'");
					} catch (IOException e) {
						errWriter.println(e.getLocalizedMessage());
					}
				} else {
					errWriter.println("History is empty.");
				}
			} else {
				errWriter.println("History not available.");
			}
		} else {
			errWriter.println("Syntax error.  String must match pattern: " + SAVE_BLOCK_REGEX);
		}
		
		// prevent from running in script engine
		return true;
	}

}
