package ca.hedlund.jiss.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import ca.hedlund.jiss.JissModel;

/**
 * Jiss window
 */
public class JissWindow extends JFrame {

	private final JissModel model;
	
	private final JissConsole console;
	
	public JissWindow() {
		super("Jiss");
		
		model = new JissModel();
		console = new JissConsole(model);
		setupWindow();
	}
	
	private void setupWindow() {
		setLayout(new BorderLayout());
		add(console, BorderLayout.CENTER);
	}
	
}
