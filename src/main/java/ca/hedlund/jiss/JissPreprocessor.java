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
	 * @param cmd the command as a mutable string
	 *  buffer.
	 * 
	 * @return <code>true</code> iff the command was handled
	 *  by the pre-processor and no further processing is
	 *  requried (i.e., the command should not be sent to the 
	 *  script engine.)
	 */
	public boolean preprocessCommand(JissModel jissModel, StringBuffer cmd);

}
