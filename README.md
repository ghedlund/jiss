# Java Interactive Scripting Shell (jiss)

A swing component for embedding a scripting shell in Java
applications.  Any Java compatible scripting language may
be used within the shell.

## Example

```
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.ui.JissConsole;

import ...

public class MyScriptPanel extends JPanel {

	private JissConsole console;
	
	public MyScriptPanel() {
		super();
		
		setLayout(new BorderLayout());
		console = setupConsole();
		add(new JScrollPane(console), BorderLayout.CENTER);
	}
	
	private JissConsole setupConsole() {
		// create a new model
		final JissModel model = new JissModel();
		
		// get the global context for the console
		final ScriptContext context = model.getScriptContext();
		final Bindings bindings = context.getBindings(ScriptContext.GLOBAL_SCOPE);
		
		// add some useful objects
		bindings.put(...);
		...
		
		return new JissConsole(model);
	}

}
```
