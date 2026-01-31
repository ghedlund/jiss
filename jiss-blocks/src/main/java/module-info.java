module ca.hedlund.jiss.blocks {
	requires jakarta.xml.bind;
	requires com.sun.xml.bind;

	requires transitive ca.hedlund.jiss.core;
	requires transitive ca.hedlund.jiss.history;

	exports ca.hedlund.jiss.blocks;
	exports ca.hedlund.jiss.blocks.preprocessor;

	opens ca.hedlund.jiss.blocks to com.sun.xml.bind;

	provides ca.hedlund.jiss.JissPreprocessor with
		ca.hedlund.jiss.blocks.preprocessor.SaveBlockPreprocessor,
		ca.hedlund.jiss.blocks.preprocessor.LoadBlockPreprocessor,
		ca.hedlund.jiss.blocks.preprocessor.ListBlocksPreprocessor;
}
