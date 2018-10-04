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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import ca.hedlund.dp.extensions.ExtensionSupport;
import ca.hedlund.dp.extensions.IExtendable;

/**
 * Model for a jiss console.
 *
 */
public class JissModel implements IExtendable {
	
	/**
	 * Current script engine
	 */
	private ScriptEngine scriptEngine;
	public static final String SCRIPT_ENGINE_PROP = JissModel.class.getName() + ".scriptEngine";
	
	/**
	 * Current context
	 */
	private ScriptContext context;
	public static final String CONTEXT_PROP = JissModel.class.getName() + ".context";
	
	/**
	 * Processor
	 */
	private JissProcessor processor;
	public static final String PROCESSOR_PROP = JissModel.class.getName() + ".processor";
	
	/**
	 * Pre-processors
	 */
	private final List<JissPreprocessor> preProcessors = new ArrayList<JissPreprocessor>();
	public static final String PREPROCESSOR_PROP = JissModel.class.getName() + ".preProcessors";
	
	private final ClassLoader classLoader;
	
	/**
	 * Extension support
	 * 
	 */
	private final ExtensionSupport extensionSupport = new ExtensionSupport(JissModel.class, this);
	
	public Set<Class<?>> getExtensions() {
		return extensionSupport.getExtensions();
	}

	public <T> T getExtension(Class<T> cap) {
		return extensionSupport.getExtension(cap);
	}

	public <T> T putExtension(Class<T> cap, T impl) {
		return extensionSupport.putExtension(cap, impl);
	}

	public <T> T removeExtension(Class<T> cap) {
		return extensionSupport.removeExtension(cap);
	}

	/**
	 * Constructor
	 */
	public JissModel() {
		this(JissModel.class.getClassLoader());
	}
	
	public JissModel(ClassLoader classLoader) {
		super();
		extensionSupport.initExtensions();
		
		this.classLoader = classLoader;
		
		// setup a default script engine
		final ScriptEngineManager manager = new ScriptEngineManager(classLoader);
		final ScriptEngine scriptEngine = manager.getEngineByExtension("js");
		setScriptEngine(scriptEngine);
		
		setScriptContext(new JissContext());
		scriptEngine.setContext(getScriptContext());
		
		setProcessor(new DefaultProcessor());
		
		addPreprocessor(new DefaultPreprocessor());
	}
	
	public void setScriptEngine(ScriptEngine engine) {
		final ScriptEngine oldEngine = this.scriptEngine;
		this.scriptEngine = engine;
		firePropertyChange(SCRIPT_ENGINE_PROP, oldEngine, engine);
	}
	
	public ScriptEngine getScriptEngine() {
		return this.scriptEngine;
	}
	
	/**
	 * Determine the engine type (if any) that can execute a script file with
	 * the given extension.
	 * 
	 * @param ext
	 * 
	 * @return script engine
	 */
	public ScriptEngine engineForExtension(String ext) {
		final ScriptEngineManager manager = new ScriptEngineManager(
				JissModel.class.getClassLoader());
		ScriptEngine retVal = null;
		for (ScriptEngineFactory factory : manager.getEngineFactories()) {
			if (factory.getExtensions().contains(ext)) {
				retVal = factory.getScriptEngine();
			}
		}
		return retVal;
	}
	
	public void setScriptContext(ScriptContext context) {
		final ScriptContext oldContext = this.context;
		this.context = context;
		getScriptEngine().setContext(context);
		firePropertyChange(CONTEXT_PROP, oldContext, context);
	}
	
	public ScriptContext getScriptContext() {
		return this.context;
	}
	
	/**
	 * Set the processor.  The processor is responsible for
	 * executing provided commands as well as all preprocessors.
	 */
	public void setProcessor(JissProcessor processor) {
		final JissProcessor oldProcessor = this.processor;
		this.processor = processor;
		firePropertyChange(PROCESSOR_PROP, oldProcessor, processor);
	}
			
	public JissProcessor getProcessor() {
		return this.processor;
	}

	/**
	 * Add a preprocessor.
	 * 
	 * @param preprocessor
	 * @throws NullPointerException if preprocessor is <code>null</code>
	 */
	public void addPreprocessor(JissPreprocessor preprocessor) {
		if(preprocessor == null)
			throw new NullPointerException();
		
		synchronized(preProcessors) {
			// don't add twice
			if(!preProcessors.contains(preprocessor) && preProcessors.add(preprocessor)) {
				final int newIndex = preProcessors.indexOf(preprocessor);
				fireIndexedPropertyChange(PREPROCESSOR_PROP, newIndex, null, preprocessor);
			}
		}
	}
	
	public void removePreprocessor(JissPreprocessor preprocessor) {
		synchronized(preProcessors) {
		final int index = preProcessors.indexOf(preprocessor);
			if(preProcessors.contains(preprocessor) && preProcessors.remove(preprocessor)) {
				fireIndexedPropertyChange(PREPROCESSOR_PROP, index, preprocessor, null);
			}
		}
	}
	
	public JissPreprocessor[] getPreprocessors() {
		JissPreprocessor[] retVal = null;
		synchronized(preProcessors) {
			retVal = this.preProcessors.toArray(new JissPreprocessor[0]);
		}
		return retVal;
	}

	/**
	 * Property support
	 */
	private final PropertyChangeSupport propSupport =
			new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		propSupport.fireIndexedPropertyChange(propertyName, index, oldValue,
				newValue);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		propSupport.fireIndexedPropertyChange(propertyName, index, oldValue,
				newValue);
	}

	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		propSupport.fireIndexedPropertyChange(propertyName, index, oldValue,
				newValue);
	}

	public void firePropertyChange(PropertyChangeEvent evt) {
		propSupport.firePropertyChange(evt);
	}

	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
		propSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, int oldValue,
			int newValue) {
		propSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return propSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return propSupport.getPropertyChangeListeners(propertyName);
	}

	public boolean hasListeners(String propertyName) {
		return propSupport.hasListeners(propertyName);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propSupport.removePropertyChangeListener(propertyName, listener);
	}
}
