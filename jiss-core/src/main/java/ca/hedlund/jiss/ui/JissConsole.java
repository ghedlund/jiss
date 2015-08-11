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
package ca.hedlund.jiss.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Set;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position.Bias;

import ca.hedlund.dp.extensions.ExtensionSupport;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.JissPreprocessor;

/**
 * The console displays output from the script engine as well as providing an
 * input prompt.
 * 
 */
public class JissConsole extends JTextPane implements IExtendable {

	private static final Logger LOGGER = Logger.getLogger(JissConsole.class
			.getName());

	private static final long serialVersionUID = -6892471980304012789L;

	/**
	 * Document
	 */
	public JissDocument doc;

	private final int DEFAULT_COLS = 100;
	private final int DEFAULT_ROWS = 25;

	/**
	 * Extension support
	 */
	private final ExtensionSupport extensionSupport = new ExtensionSupport(
			JissConsole.class, this);

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

		if (isInitialSizing) {
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
	 * Method to print a new prompt to the end of the console and setup prompt
	 * location for input.
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
		public boolean preprocessCommand(JissModel jissModel, String orig,
				StringBuffer cmd) {
			if (cmd.toString().equals("clear")) {
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
	 * Ensures that the caret does not move before the prompt location.
	 */
	private NavigationFilter navFilter = new NavigationFilter() {

		// keep track of where the dot is trying to go
		private int lastDot = -1;

		@Override
		public void setDot(FilterBypass fb, int dot, Bias bias) {
			if (dot >= doc.getPromptLocation()) {
				super.setDot(fb, dot, bias);
			}
			lastDot = dot;
		}

		@Override
		public void moveDot(FilterBypass fb, int dot, Bias bias) {
			// setup start location for selection based
			// on previous attempts to set dot location
			if (lastDot < doc.getPromptLocation()) {
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

//	private final AtomicReference<File> dndFile = 
//			new AtomicReference<File>();
//	@Override
//	public void dragEnter(DropTargetDragEvent dtde) {
//		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
//			try {
//				@SuppressWarnings("unchecked")
//				final List<File> fileList = (List<File>) dtde.getTransferable()
//						.getTransferData(DataFlavor.javaFileListFlavor);
//				// only accept single files
//				if (fileList.size() == 1) {
//					final File f = fileList.get(0);
//					final int lastDot = f.getName().lastIndexOf('.');
//					if (lastDot > 0) {
//						final String ext = f.getName().substring(lastDot + 1);
//						final ScriptEngine se = getModel().engineForExtension(ext);
//						if (se != null) {
//							dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
//							dndFile.set(f);
//						} else {
//							dtde.rejectDrag();
//							dndFile.set(null);
//						}
//					} else {
//						dtde.rejectDrag();
//						dndFile.set(null);
//					}
//				} else {
//					dtde.rejectDrag();
//					dndFile.set(null);
//				}
//			} catch (UnsupportedFlavorException e) {
//				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
//				dtde.rejectDrag();
//				dndFile.set(null);
//			} catch (IOException e) {
//				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
//				dtde.rejectDrag();
//				dndFile.set(null);
//			}
//		}
//	}
//
//	@Override
//	public void dragOver(DropTargetDragEvent dtde) {
//		
//	}
//
//	@Override
//	public void dropActionChanged(DropTargetDragEvent dtde) {
//	}
//
//	@Override
//	public void dragExit(DropTargetEvent dte) {
//	}
//
//	@Override
//	public void drop(DropTargetDropEvent dtde) {
//		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
//				&& dndFile.get() != null) {
//			final File f = dndFile.get();
//			try {
//				getDocument().insertString(getDocument().getLength(), 
//						"::exec \"" + f.getAbsolutePath() + "\"", null);
//			} catch (BadLocationException e) {
//				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
//			}
//		}
//	}

}
