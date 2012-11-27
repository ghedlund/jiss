package ca.hedlund.jiss;

import javax.script.SimpleScriptContext;

public class JissContext extends SimpleScriptContext {
	
	public final static String LAST_VALUE = 
			"__last";
	
	/**
	 * Constructor
	 */
	public JissContext() {
		super();
		
		super.getBindings(ENGINE_SCOPE).put("context", this);
	}
	
}
