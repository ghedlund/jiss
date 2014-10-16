package ca.hedlund.jiss.preprocessor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.ScriptURLProcessor;

public class ExecPreprocessor implements JissPreprocessor {
	
	private static final Logger LOGGER = Logger
			.getLogger(ExecPreprocessor.class.getName());

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig,
			StringBuffer cmd) {
		if(orig.startsWith("::exec ")) {
			String scriptLocation = orig.substring("::exec ".length()).trim();
			if(scriptLocation.startsWith("\"")) {
				scriptLocation = scriptLocation.substring(1, 
						scriptLocation.lastIndexOf('"'));
			}
			
			URL url = null;
			try {
				url = new URL(scriptLocation);
			} catch (MalformedURLException e) {
				try {
					url = (new File(scriptLocation)).toURI().toURL();
				} catch (MalformedURLException e1) {
					LOGGER
							.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
				}
			}
			
			if(url != null) {
				jissModel.setProcessor(new ScriptURLProcessor(url));
			}
		}
		return false;
	}

}
