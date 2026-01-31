module ca.hedlund.jiss.rsyntaxarea.input {
	requires java.desktop;

	requires org.fife.RSyntaxTextArea;

	requires transitive ca.hedlund.jiss.core;

	exports ca.hedlund.jiss.textarea;

	provides ca.hedlund.dp.extensions.ExtensionProvider with
		ca.hedlund.jiss.textarea.OpenTextAreaAction;
}
