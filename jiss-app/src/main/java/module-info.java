module ca.hedlund.jiss.app {
	requires java.desktop;
	
	requires transitive ca.hedlund.jiss.core;
	requires transitive ca.hedlund.jiss.history;
	
	exports ca.hedlund.jiss.app;
}
