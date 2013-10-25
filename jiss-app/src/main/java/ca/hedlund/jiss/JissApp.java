package ca.hedlund.jiss;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ca.hedlund.jiss.ui.JissWindow;

/**
 * Displays a JissConsole in a window.
 *
 */
public class JissApp {
	
	private JissWindow window;
	
	private void createAndShow() {
		window = new JissWindow();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	
	public static void main(String[] args) {
		// create and display a JissWindow
		final JissApp app = new JissApp();
		final Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				app.createAndShow();
			}
		};
		SwingUtilities.invokeLater(runnable);
	}

}
