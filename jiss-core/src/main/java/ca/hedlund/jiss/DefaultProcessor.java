/*
 * Copyright (C) 2012-2018 Gregory Hedlund
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.hedlund.jiss;

import ca.hedlund.jiss.ui.FutureExtension;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.concurrent.*;

/**
 * Default jiss processor.
 *
 */
public class DefaultProcessor extends Processor {

	@Override
	public Object processCommand(JissModel jissModel, String cmd)
			throws JissError {
		fireProcessingStarted(jissModel, cmd);

		Object retVal = null;
		JissError error = null;

		final ScriptEngine engine = jissModel.getScriptEngine();
		try {
			retVal = execWithFuture(engine, cmd, jissModel);
		} catch (ExecutionException e) {
            error = new JissError(e);
            throw error;
        } catch (InterruptedException | CancellationException e) {
            error = new JissError(e);
		} finally {
			fireProcessingEnded(jissModel, cmd, retVal, error);
            jissModel.putExtension(FutureExtension.class, null);
		}
		
		return retVal;
	}

    private static Object execWithFuture(final ScriptEngine engine, final String script, final JissModel jissModel) throws ExecutionException, InterruptedException {
        final Callable<Object> val = () -> engine.eval(script, jissModel.getScriptContext());
        final Future<Object> f = Executors.newCachedThreadPool().submit(val);
        jissModel.putExtension(FutureExtension.class, new FutureExtension(f));
        return f.get();
    }

}
