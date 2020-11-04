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
package ca.hedlund.jiss.app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

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
