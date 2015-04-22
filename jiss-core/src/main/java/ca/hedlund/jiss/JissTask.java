/*
 * jiss-core
 * Copyright (C) 2015, Gregory Hedlund <ghedlund@mun.ca>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.hedlund.jiss;

import java.io.PrintWriter;
import java.io.Writer;

import javax.script.Bindings;
import javax.script.ScriptContext;

/**
 * Execute the given command use the currently
 * selected scripting engine.  If provided,
 * the given writers will be used for the scripting
 * engine's stdout/stderr outputs.  Otherwise,
 * System.out/System.err are used.
 * 
 * 
 */
public class JissTask implements Runnable {

	/**
	 * The model
	 */
	private final JissModel model;
	
	/**
	 * The command to run
	 */
	private final String cmd;
	
	/**
	 * Stdout writer
	 */
	private Writer stdoutWriter;
	
	/**
	 * Stderr writer
	 */
	private Writer stderrWriter;
	
	public JissTask(JissModel model, String cmd) {
		this(model, cmd,
				new PrintWriter(System.out), new PrintWriter(System.err));
	}
	
	public JissTask(JissModel model, String cmd, 
			Writer stdout, Writer stderr) {
		super();
		this.model = model;
		this.cmd = cmd;
		this.stdoutWriter = stdout;
		this.stderrWriter = stderr;
	}

	public Writer getStdoutWriter() {
		return stdoutWriter;
	}

	public void setStdoutWriter(Writer stdoutWriter) {
		this.stdoutWriter = stdoutWriter;
	}

	public Writer getStderrWriter() {
		return stderrWriter;
	}

	public void setStderrWriter(Writer stderrWriter) {
		this.stderrWriter = stderrWriter;
	}

	public JissModel getModel() {
		return model;
	}
	
	@Override
	public void run() {
		final PrintWriter pwOut = new PrintWriter(stdoutWriter);
		final PrintWriter pwErr = new PrintWriter(stderrWriter);
		try {
			
			model.getScriptContext().setWriter(pwOut);
			model.getScriptContext().setErrorWriter(pwErr);
			
			final StringBuffer cmdBuffer = new StringBuffer(cmd);
			
			boolean keepProcessing = true;
			for(JissPreprocessor preprocessor:model.getPreprocessors()) {
				keepProcessing &= !preprocessor.preprocessCommand(model, cmd, cmdBuffer);
			}
			
			if(keepProcessing && cmdBuffer.toString().length() > 0) {			
				final JissProcessor processor = model.getProcessor();
				final Object val = processor.processCommand(model, cmdBuffer.toString());
				
				if(val != null) {
					pwOut.println(val.toString());
				}
				// insert last val into script context
				final Bindings bindings = model.getScriptContext().getBindings(ScriptContext.ENGINE_SCOPE);
				bindings.put(JissContext.LAST_VALUE, val);
			}
		} catch (JissError err) {
			pwErr.println(err.getLocalizedMessage());
		}
	}
}
