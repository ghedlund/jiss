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
package ca.hedlund.dp.visitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import ca.hedlund.dp.visitor.annotation.Visits;

/**
 * Multiple dispatch visitor adapter.  The generic {@link #visit(Object)}
 * method will look for other methods in this class with have the
 * {@link Visits} annotation declaring the given objects specific type.
 * Other wise, the {@link #fallbackVisit(Object)} method is called.
 * @param <T>
 */
public abstract class VisitorAdapter<T> implements Visitor<T> {
	
	private final static Logger LOGGER = Logger.getLogger(VisitorAdapter.class
			.getName());

	@Override
	public void visit(T obj) {
		if(obj == null) 
			throw new NullPointerException();
		// get the class type of the given object
		Class<?> objType = obj.getClass();
		Method visitMethod = findVisitMethod(objType);
		
		if(visitMethod != null) {
			try {
				visitMethod.invoke(this, obj);
			} catch (IllegalArgumentException e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		} else {
			fallbackVisit(obj);
		}
	}
	
	/**
	 * Look for a method having the {@link Visits} annotation
	 * of the given type.
	 * 
	 * @param type
	 */
	private Method findVisitMethod(Class<?> type) {
		Method retVal = null;
		Class<?> adapterClass = this.getClass();
		for(Method m:adapterClass.getMethods()) {
			// check for the visits annotation
			Visits visits = m.getAnnotation(Visits.class);
			if(visits != null) {
				// check type
				Class<?>[] paramTypes = m.getParameterTypes();
				
				if(paramTypes.length == 1 && paramTypes[0].isAssignableFrom(type)) {
					retVal = m;
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * Generic, i.e., fallback, processing method.
	 * 
	 * @param T the object to visit
	 */
	public abstract void fallbackVisit(T obj);
}
