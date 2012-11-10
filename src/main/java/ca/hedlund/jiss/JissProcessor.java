package ca.hedlund.jiss;

/**
 * Interface for the Jiss processor.
 */
public interface JissProcessor {
	
	/**
	 * Process the command.
	 * 
	 * @param jissModel
	 * @param cmd
	 * 
	 * @return the object returned by the script engine.  May
	 *  be {@link NullPointerException}
	 * @throws {@link JissError} on error
	 */
	public Object processCommand(JissModel jissModel, String cmd)
		throws JissError;

}
