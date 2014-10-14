package ca.hedlund.jiss.textarea;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.lang.ref.WeakReference;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.ui.JissConsole;
import ca.hedlund.jiss.ui.JissDocument;

@Extension(JissConsole.class)
public class OpenTextAreaAction extends AbstractAction implements
		ExtensionProvider {
	
	public static final String OPEN_TEXTAREA_KB =
			OpenTextAreaAction.class.getName() + ".keystroke";
	
	public static final String OPEN_TEXTAREA_ACT_ID =
			OpenTextAreaAction.class.getName() + ".opentextarea";
	
	private WeakReference<JissConsole> consoleRef;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final JissConsole console = consoleRef.get();
		if(console == null) return;
		
		final JissDocument doc = 
				JissDocument.class.cast(console.getDocument());
		
		final ScriptEngine currentEngine = console.getModel().getScriptEngine();
		final ScriptEngineFactory factory = currentEngine.getFactory();
		
		final RSyntaxTextArea textArea = new RSyntaxTextArea() {
			@Override
			public void paintComponent(Graphics g) {
				final Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				super.paintComponent(g2);
			}
		};
		textArea.setText(doc.getPrompt());
		textArea.setFont(console.getFont());
		textArea.setColumns(80);
		textArea.setRows(20);
		textArea.setEditable(true);
		final RTextScrollPane scroller = new RTextScrollPane(textArea, true);
		textArea.setSyntaxEditingStyle("text/" + factory.getExtensions().get(0));
		
		final AbstractDocument abDoc = (AbstractDocument)textArea.getDocument();
		abDoc.setDocumentFilter(new DocumentFilter() {

			@Override
			public void insertString(FilterBypass fb, int offset,
					String string, AttributeSet attr)
					throws BadLocationException {
				super.insertString(fb, offset, string, attr);
				
				doc.insertString(offset+doc.getPromptLocation(), string, attr);
			}

			@Override
			public void remove(FilterBypass fb, int offset, int length)
					throws BadLocationException {
				super.remove(fb, offset, length);
				
				doc.remove(offset+doc.getPromptLocation(), length);
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length,
					String text, AttributeSet attrs)
					throws BadLocationException {
				super.replace(fb, offset, length, text, attrs);
				
				doc.replace(offset+doc.getPromptLocation(), length, text, attrs);
			}
			
		});
		
		Rectangle promptRect = new Rectangle();
		try {
			promptRect = console.modelToView(doc.getPromptLocation());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		final Point p = new Point(promptRect.x, promptRect.y);
		SwingUtilities.convertPointToScreen(p, console);
		
		final Window parentWindow = 
				Window.class.cast(SwingUtilities.getAncestorOfClass(Window.class, console));
		
		final JWindow window = new JWindow(parentWindow);
		window.setLayout(new BorderLayout());
		window.add(scroller, BorderLayout.CENTER);
		
		window.setLocation(p.x, p.y);
		window.pack();
		
		window.setVisible(true);
		window.setFocusable(true);
		window.setFocusableWindowState(true);
		window.requestFocus();
		window.addWindowFocusListener(new WindowFocusListener() {
			
			@Override
			public void windowLostFocus(WindowEvent arg0) {
				window.dispose();
			}
			
			@Override
			public void windowGainedFocus(WindowEvent arg0) {
			}
		});
		textArea.requestFocusInWindow();
	}
	
	public KeyStroke getKeystroke() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK);
	}

	@Override
	public void installExtension(IExtendable obj) {
		final JissConsole console = JissConsole.class.cast(obj);
		this.consoleRef = new WeakReference<JissConsole>(console);
		
		final ActionMap actionMap = console.getActionMap();
		final InputMap inputMap = console.getInputMap(JissConsole.WHEN_FOCUSED);
		
		final KeyStroke ks = getKeystroke();
		actionMap.put(OPEN_TEXTAREA_ACT_ID, this);
		inputMap.put(ks, OPEN_TEXTAREA_ACT_ID);
		
		console.setActionMap(actionMap);
		console.setInputMap(JissConsole.WHEN_FOCUSED, inputMap);
	}

}
