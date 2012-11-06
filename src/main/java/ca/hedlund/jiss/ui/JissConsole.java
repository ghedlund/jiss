package ca.hedlund.jiss.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import javax.lang.model.type.PrimitiveType;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import ca.hedlund.jiss.JissContext;
import ca.hedlund.jiss.JissError;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;
import ca.hedlund.jiss.JissProcessor;

/**
 * The console displays output from
 * the script engine as well as providing
 * an input prompt.
 * 
 */
public class JissConsole extends JPanel {
	
	private JTextArea outputArea;
	private JTextField inputField;
	
	/**
	 * Jiss model
	 */
	private JissModel jissModel;
	
	public JissConsole() {
		this(new JissModel());
	}
	
	public JissConsole(JissModel model) {
		this.jissModel = model;
		this.jissModel.addPreprocessor(clearPreprocessor);
		
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		
		final Font consoleFont = new Font("Menlo", Font.PLAIN, 13);
		outputArea = new JTextArea();
		outputArea.setFont(consoleFont);
		outputArea.setAutoscrolls(false);
		outputArea.setRows(30);
		
		final JScrollPane scroller = new JScrollPane(outputArea);
		add(scroller, BorderLayout.CENTER);
		
		inputField = new JTextField();
		inputField.setFont(consoleFont);
		inputField.setAction(new ExecuteAction());
		inputField.setColumns(100);
		add(inputField, BorderLayout.SOUTH);
	}
	
	public void execute(String cmd) {
		final ExecuteTask act = new ExecuteTask(cmd);
		final Thread th = new Thread(act);
		th.start();
	}
	
	private final class ExecuteAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			final String cmd = inputField.getText();
			inputField.setText("");
			
			execute(cmd);
		}
		
	}
	
	private final class ExecuteTask implements Runnable {
		
		private String cmd;
		
		public ExecuteTask(String cmd) {
			this.cmd = cmd;
		}
		
		@Override
		public void run() {
			try {
				final JissProcessor processor = jissModel.getProcessor();
				
				// setup output streams
				final StringWriter stdoutSw = new StringWriter();
				final PrintWriter stdoutPw = new PrintWriter(stdoutSw);
				
				jissModel.getScriptContext().setWriter(stdoutPw);
				
				SwingUtilities.invokeLater(new PrintOutputTask("> " + cmd + "\n"));
				
				processor.processCommand(jissModel, cmd);
				
				final String output = stdoutSw.getBuffer().toString();
				SwingUtilities.invokeLater(new PrintOutputTask(output));
			} catch (JissError err) {
				handleError(err);
				err.printStackTrace();
			}
		}
		
	}
	
	private void handleError(JissError err) {
		final Throwable cause = err.getCause();
		if(cause instanceof ScriptException) {
			final ScriptException se = ScriptException.class.cast(cause);
			SwingUtilities.invokeLater(new PrintOutputTask(se.getLocalizedMessage() + "\n"));
		}
	}
	
	private class PrintOutputTask implements Runnable {
		
		private String toPrint;
		
		public PrintOutputTask(String toPrint) {
			this.toPrint = toPrint;
		}

		@Override
		public void run() {
			
			outputArea.append(
					StringEscapeUtils.unescapeJava(toPrint) );
		}
		
	}
	
	private JissPreprocessor clearPreprocessor = new JissPreprocessor() {
		
		@Override
		public boolean preprocessCommand(JissModel jissModel, StringBuffer cmd) {
			if(cmd.toString().equals("clear")) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						outputArea.setText("");
					}
				});
				return true;
			}
			return false;
		}
	};

}
