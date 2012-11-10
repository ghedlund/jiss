package ca.hedlund.jiss.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import javax.lang.model.type.PrimitiveType;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.NavigationFilter;
import javax.swing.text.NavigationFilter.FilterBypass;
import javax.swing.text.Position.Bias;

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
public class JissConsole extends JTextPane {
	
	/**
	 * Document
	 */
	public JissConsoleDocument doc;
	
	/**
	 * Document styles
	 */
	
	/**
	 * Jiss model
	 */
	private JissModel jissModel;
	
	public JissConsole() {
		this(new JissModel());
	}
	
	public JissConsole(JissModel model) {
		super();
		this.jissModel = model;
		this.jissModel.addPreprocessor(clearPreprocessor);
		this.doc = new JissConsoleDocument();
		setDocument(doc);
		
		init();
	}
	
	private void init() {
		prompt();
		
		final ActionMap am = super.getActionMap();
		final InputMap im = super.getInputMap(WHEN_FOCUSED);
		
		final Action exeAct = new ExecuteAction();
		final KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		final String exeId = "_execute_";
		im.put(ks, exeId);
		am.put(exeId, exeAct);
		
		
		
		super.setNavigationFilter(navFilter);
		
		super.setActionMap(am);
		super.setInputMap(WHEN_FOCUSED, im);
	}
	
	public void execute(String cmd) {
		final ExecuteTask act = new ExecuteTask(cmd);
		final Thread th = new Thread(act);
		th.start();
	}
	
	public String getPromptString() {
		final ScriptEngine engine = jissModel.getScriptEngine();
		final String promptTxt = engine.getFactory().getExtensions().get(0);
		
		return "-(" + promptTxt + ")-" + "$ ";
	}
	
	/**
	 * Method to print a new prompt to the end of the
	 * console and setup prompt location for input.
	 */
	private void prompt() {
		final Runnable onEDT = new Runnable() {
			@Override
			public void run() {
				final String promptString = getPromptString();
				try {
					doc.insertString(doc.getLength(), promptString, null);
					doc.markPromptLocation();
					int promptLocation = doc.getLength();
					setCaretPosition(promptLocation);
				} catch (BadLocationException be) {
					be.printStackTrace();
				}
			}
		};
		SwingUtilities.invokeLater(onEDT);
	}
	
	private final class ExecuteAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				final String cmd = doc.getPrompt();
				
				if(!cmd.endsWith("\\")) { 
					// escaped string, continue with input
					execute(cmd.replaceAll("\\\\", ""));
				} else {
					doc.insertString(doc.getLength(), "\n", null);
				}
			} catch (BadLocationException be) {
				be.printStackTrace();
			}
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
				
				final Object val = processor.processCommand(jissModel, cmd);
				
				final String output = 
						StringEscapeUtils.unescapeJava( stdoutSw.getBuffer().toString() );
				
				SwingUtilities.invokeLater(new PrintOutputTask("\n"));
				if(output.length() > 0) {
					boolean addnl = !output.endsWith("\n");
					SwingUtilities.invokeLater(new PrintOutputTask(output 
							+ (addnl ? "" : "\n")) );
				}
				
				if(val != null)
					SwingUtilities.invokeLater(new PrintOutputTask(val.toString() + "\n"));
				
				prompt();
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
			SwingUtilities.invokeLater(new PrintOutputTask("\n" + se.getLocalizedMessage() + "\n"));
			prompt();
		}
	}
	
	/**
	 * 
	 */
	private class PrintOutputTask implements Runnable {
		
		private String toPrint;
		
		private boolean escape = true;
		
		public PrintOutputTask(String toPrint) {
			this.toPrint = toPrint;
			this.escape = escape;
		}

		@Override
		public void run() {
			final String txt = (escape ? StringEscapeUtils.unescapeJava(toPrint) : toPrint);
			
			final Document doc = getDocument();
			try {
				doc.insertString(doc.getLength(), txt, null);
			} catch (BadLocationException be) {
				be.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Handles clering the console
	 */
	private JissPreprocessor clearPreprocessor = new JissPreprocessor() {
		@Override
		public boolean preprocessCommand(JissModel jissModel, StringBuffer cmd) {
			if(cmd.toString().equals("clear")) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						doc.clear();
						prompt();
					}
				});
				return true;
			}
			return false;
		}
	};
	
	/**
	 * Ensures that the caret does not move before the
	 * prompt location.
	 */
	private NavigationFilter navFilter = new NavigationFilter() {
		
		// keep track of where the dot is trying to go
		private int lastDot = -1;
		
		@Override
		public void setDot(FilterBypass fb, int dot, Bias bias) {
			if(dot >= doc.getPromptLocation()) {
				super.setDot(fb, dot, bias);
			}
			lastDot = dot;
		}

		@Override
		public void moveDot(FilterBypass fb, int dot, Bias bias) {
			// setup start location for selection based
			// on previous attempts to set dot location
			if(lastDot < doc.getPromptLocation()) {
				super.setDot(fb, lastDot, bias);
			}
			super.moveDot(fb, dot, bias);
		}
		
	};

	public static void main(String[] args) {
		final Font fixedWidth = new Font("Menlo", Font.PLAIN, 13);
		
		final JissConsole console = new JissConsole();
		console.setForeground(Color.white);
		console.setBackground(Color.black);
		console.setCaretColor(Color.white);
		
		console.setFont(fixedWidth);
		
		final JScrollPane sp = new JScrollPane(console);
		final JFrame f = new JFrame("JissConsole");
		f.add(sp);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setLocationByPlatform(true);
		f.setVisible(true);
	}
}
