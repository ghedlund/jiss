module ca.hedlund.jiss.history {
	requires java.logging;
	requires java.desktop;
	
	requires com.sun.xml.bind;
	
	requires transitive ca.hedlund.jiss.core;
	
	exports ca.hedlund.jiss.history;
	exports ca.hedlund.jiss.history.actions;
}
