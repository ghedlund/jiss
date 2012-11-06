package ca.hedlund.jiss;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Default preprocessor that loads preprocessors using
 * {@link ServiceLoader}.
 */
public class DefaultPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, StringBuffer cmd) {
		final ClassLoader cl = jissModel.getClass().getClassLoader();
		final ServiceLoader<JissPreprocessor> loader =
				ServiceLoader.load(JissPreprocessor.class, cl);
		
		boolean processed = false;
		final Iterator<JissPreprocessor> itr = loader.iterator();
		while(itr.hasNext()) {
			final JissPreprocessor preprocessor = itr.next();
			processed |= preprocessor.preprocessCommand(jissModel, cmd);
			if(processed) break;
		}
		
		return processed;
	}

}
