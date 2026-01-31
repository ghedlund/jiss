module ca.hedlund.jiss.history {
	requires java.logging;
	requires java.desktop;

	requires jakarta.xml.bind;
	requires com.sun.xml.bind;

	requires transitive ca.hedlund.jiss.core;

	exports ca.hedlund.jiss.history;
	exports ca.hedlund.jiss.history.actions;

	opens ca.hedlund.jiss.history to com.sun.xml.bind;

	provides ca.hedlund.dp.extensions.ExtensionProvider with
		ca.hedlund.jiss.history.JissHistoryManager,
		ca.hedlund.jiss.history.JissHistoryBindings;

	provides ca.hedlund.jiss.JissPreprocessor with
		ca.hedlund.jiss.history.JissHistoryPreprocessor;
}
