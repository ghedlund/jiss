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

    uses ca.hedlund.dp.extensions.ExtensionProvider;
    uses ca.hedlund.jiss.JissPreprocessor;

    provides ca.hedlund.dp.extensions.ExtensionProvider with
        ca.hedlund.jiss.ui.bindings.SoftReturn,
        ca.hedlund.jiss.ui.bindings.RunCommand,
        ca.hedlund.jiss.ui.bindings.Cancel;

    provides ca.hedlund.jiss.JissPreprocessor with
        ca.hedlund.jiss.preprocessor.InfoPreprocessor,
        ca.hedlund.jiss.preprocessor.ExecPreprocessor,
        ca.hedlund.jiss.preprocessor.LangPreprocessor,
        ca.hedlund.jiss.preprocessor.ResetPreprocessor;
}
