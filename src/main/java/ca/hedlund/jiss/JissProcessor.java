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
	 * @throws {@link JissError} on error
	 */
	public void processCommand(JissModel jissModel, String cmd)
		throws JissError;

}
