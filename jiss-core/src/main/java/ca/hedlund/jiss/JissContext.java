package ca.hedlund.jiss;

import javax.script.SimpleScriptContext;

public class JissContext extends SimpleScriptContext {
	
	/**
	 * Constructor
	 */
	public JissContext() {
		super();
		
		super.getBindings(ENGINE_SCOPE).put("context", this);
	}
	
}
