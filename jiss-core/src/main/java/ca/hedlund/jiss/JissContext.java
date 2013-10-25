package ca.hedlund.jiss;

import javax.script.SimpleScriptContext;

public class JissContext extends SimpleScriptContext {
	
	/**
	 * name of last value param
	 */
	public final static String LAST_VALUE = 
			"__last";
	
	/**
	 * name of variable used to setup prompt string
	 * when command is finished executing.
	 */
	public final static String PROMPT_VALUE = 
			"__prompt_val";
	
	/**
	 * Constructor
	 */
	public JissContext() {
		super();
		
		super.getBindings(ENGINE_SCOPE).put("context", this);
	}
	
}
