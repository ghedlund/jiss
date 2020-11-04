module ca.hedlund.jiss.core {
	requires java.desktop;
	requires java.logging;
	requires java.compiler;
	
	requires transitive org.apache.commons.lang3;
	requires transitive java.scripting;
	
	exports ca.hedlund.dp.extensions;
	exports ca.hedlund.dp.visitor;
	exports ca.hedlund.jiss;
	exports ca.hedlund.jiss.preprocessor;
	exports ca.hedlund.jiss.ui;
	exports ca.hedlund.jiss.ui.bindings;
	exports ca.hedlund.jiss.ui.menu;
}
