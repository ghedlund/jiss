module ca.hedlund.jiss.blocks {
	requires com.sun.xml.bind;
	
	requires transitive ca.hedlund.jiss.core;
	requires transitive ca.hedlund.jiss.history;
	
	exports ca.hedlund.jiss.blocks;
	exports ca.hedlund.jiss.blocks.preprocessor;
}
