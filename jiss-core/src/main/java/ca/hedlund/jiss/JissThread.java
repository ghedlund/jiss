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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.hedlund.dp.extensions.Extension;

@Extension(JissModel.class)
public class JissThread extends Thread {

	/**
	 * List of tasks to execute.
	 */
	private final List<Runnable> tasks = 
			Collections.synchronizedList(new ArrayList<Runnable>());
	
	public JissThread() {
		super("jiss");
	}
	
	public JissThread(List<Runnable> tasks) {
		super("jiss");
		this.tasks.addAll(tasks);
	}
	
	@Override
	public void run() {
		for(Runnable task:tasks) {
			task.run();
		}
	}
}
