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
