package ca.hedlund.jiss.history;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.JissModel;

/**
 * {@link JissHistory} handles reading/writing console history.
 * Console history is available through {@link JissModel} as
 * an extension.
 * 
 * Multiple instances of {@link JissHistory} may be adding
 * entries to the same history file.
 *
 */
@Extension(JissModel.class)
public class JissHistory implements ExtensionProvider {
	
	private final static Logger LOGGER =
			Logger.getLogger(JissHistory.class.getName());
	
	/**
	 * History file location property
	 */
	public final static String HISTORY_PATH_PROP = JissHistory.class.getName() + ".jiss_history";
	
	/**
	 * Default history file path
	 */
	public final static String DEFAULT_HISTORY_PATH = System.getProperty("user.home") + File.separator + ".jiss_history";
	
	private static final String HISTORY_LINE_FORMAT = ": %d:%s";
	
	/**
	 * History file
	 */
	private File historyFile;
	
	/**
	 * Constructor
	 */
	public JissHistory() {
		super();
	}
	
	/**
	 * Return the history file.
	 * @return the history file
	 */
	public File getHistoryFile() {
		if(historyFile == null) {
			// TODO allow setting this variable using a pref/setting
			final String historyFilePath = DEFAULT_HISTORY_PATH;
			historyFile = new File(historyFilePath);
			
			if(!historyFile.exists()) {
				try {
					historyFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					LOGGER.severe(e.getMessage());
				}
			}
		}
		return historyFile;
	}
	
	/**
	 * Add a new entry to the history file.
	 * 
	 * @param cmd
	 */
	public void addToHistory(String cmd) {
		try {
			final FileWriter historyAppender = new FileWriter(getHistoryFile(), true);
			final PrintWriter out = new PrintWriter(historyAppender);
			
			final long timestamp = System.currentTimeMillis();
			final String output = String.format(HISTORY_LINE_FORMAT, timestamp, cmd);
			
			out.println(output);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}
	
	@Override
	public void installExtension(IExtendable obj) {
		if(obj instanceof JissModel) {
			final JissModel model = JissModel.class.cast(obj);
			model.putExtension(JissHistory.class, this);
		}
	}
}
