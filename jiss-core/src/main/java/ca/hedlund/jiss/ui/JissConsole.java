package ca.hedlund.jiss.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.NavigationFilter;
import javax.swing.text.NavigationFilter.FilterBypass;
import javax.swing.text.Position.Bias;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import ca.hedlund.dp.extensions.ExtensionSupport;
import ca.hedlund.dp.extensions.IExtendable;
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
public class JissConsole extends JTextPane implements IExtendable {
	
	/**
	 * Document
	 */
	public JissDocument doc;
	
	private final int DEFAULT_COLS = 100;
	private final int DEFAULT_ROWS = 25;
	
	/**
	 * Extension support
	 */
	private final ExtensionSupport extensionSupport = 
			new ExtensionSupport(JissConsole.class, this);
	
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
		this.doc = new JissDocument();
		setDocument(doc);
		setCaret(new JissCaret());
		
		init();
		extensionSupport.initExtensions();
	}
	
	private void init() {
		// setup a default monospaced font
		final Font monospaced = new Font("Monospaced", Font.PLAIN, 12);
		setFont(monospaced);
		prompt();
		
		super.setNavigationFilter(navFilter);
	}
	
	public JissModel getModel() {
		return this.jissModel;
	}
	
	public String getPromptString() {
		final ScriptEngine engine = jissModel.getScriptEngine();
		final String promptTxt = engine.getFactory().getExtensions().get(0);
		
		return promptTxt + " " + "$ ";
	}
	
	public void prompt() {
		prompt("");
	}
	
	private boolean isInitialSizing = true;
	@Override
	public Dimension getPreferredSize() {
		final Dimension retVal = super.getPreferredSize();
		
		if(isInitialSizing) {
			final Font f = getFont();
			final Graphics2D g2d = Graphics2D.class.cast(getGraphics());
			final FontMetrics fm = g2d.getFontMetrics(f);
			
			final int fontHeight = fm.getHeight();
			final int prefHeight = fontHeight * DEFAULT_ROWS;
			final int charWidth = fm.getWidths()['w'];
			final int prefWidth = charWidth * DEFAULT_COLS;
			
			retVal.height = prefHeight;
			retVal.width = prefWidth;
			
			isInitialSizing = false;
		}
		
		return retVal;
	}
	
	/**
	 * Method to print a new prompt to the end of the
	 * console and setup prompt location for input.
	 */
	public void prompt(final String txt) {
		final Runnable onEDT = new Runnable() {
			@Override
			public void run() {
				final String promptString = getPromptString();
				try {
					doc.insertString(doc.getLength(), promptString, null);
					doc.markPromptLocation();
					int promptLocation = doc.getLength();
					setCaretPosition(promptLocation);
					doc.insertString(promptLocation, txt, null);
				} catch (BadLocationException be) {
					be.printStackTrace();
				}
			}
		};
		SwingUtilities.invokeLater(onEDT);
	}
	
	/**
	 * Handles clering the console
	 */
	private JissPreprocessor clearPreprocessor = new JissPreprocessor() {
		@Override
		public boolean preprocessCommand(JissModel jissModel, String orig, StringBuffer cmd) {
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
	
	
}
