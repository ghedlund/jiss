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
package ca.hedlund.jiss.preprocessor;

import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;


import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * Pre-processor for built-in commands.
 *
 */
public class LangPreprocessor implements JissPreprocessor {

	@Override
	public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
		final String c = cmd.toString();
		if(c.equals("::langs")) {
			cmd.setLength(0);
			printLangs(jissModel, cmd);
		} else if (c.equals("::lang")) {
			cmd.setLength(0);
			printCurrentLang(jissModel, cmd);
		} else if(c.startsWith("::lang")) {
			cmd.setLength(0);
			
			final String parts[] = c.split("\\p{Space}");
			if(parts.length == 2) {
				final String lang = parts[1];
				
				final ScriptEngineManager manager = new ScriptEngineManager(JissModel.class.getClassLoader());
				ScriptEngine newEngine = null;
				for(ScriptEngineFactory factory:manager.getEngineFactories()) {
					if(factory.getLanguageName().equals(lang)
							|| factory.getExtensions().contains(lang)
							|| factory.getMimeTypes().contains(lang)) {
						newEngine = factory.getScriptEngine();
						break;
					}
				}
				
				if(newEngine != null) {
					jissModel.setScriptEngine(newEngine);
					printCurrentLang(jissModel, cmd);
				}
			}
		}
		return false;
	}
	
	private void printCurrentLang(JissModel model, StringBuffer cmd) {
		final ScriptEngineFactory factory = model.getScriptEngine().getFactory();
		final String engineInfo = 
				factory.getLanguageName() + " " + factory.getLanguageVersion() + ":" + factory.getEngineName() + " " + factory.getEngineVersion();
		try {
			model.getScriptContext().getWriter().write(engineInfo);
			model.getScriptContext().getWriter().write("\n");
			model.getScriptContext().getWriter().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private void printLangs(JissModel model, StringBuffer cmd) {
		final ScriptEngineManager manager = new ScriptEngineManager(JissModel.class.getClassLoader());
		for(ScriptEngineFactory factory:manager.getEngineFactories()) {
			final String engineInfo = 
					factory.getLanguageName() + " " + factory.getLanguageVersion() + ":" + factory.getEngineName() + " " + factory.getEngineVersion();
			try {
				model.getScriptContext().getWriter().write(engineInfo);
				model.getScriptContext().getWriter().write("\n");
				model.getScriptContext().getWriter().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
