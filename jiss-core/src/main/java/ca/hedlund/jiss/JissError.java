package ca.hedlund.jiss;

/**
 * 
 */
public class JissError extends Error {
	
	private static final long serialVersionUID = 294019453399497415L;

	public JissError() {
		super();
	}

	public JissError(String message, Throwable cause) {
		super(message, cause);
	}

	public JissError(String message) {
		super(message);
	}

	public JissError(Throwable cause) {
		super(cause);
	}
	
}
