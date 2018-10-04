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
