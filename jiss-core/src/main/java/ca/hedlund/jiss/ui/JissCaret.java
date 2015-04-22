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

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

/**
 * Block caret for the console.
 *
 */
public class JissCaret extends DefaultCaret {

	private static final long serialVersionUID = 1L;

    /**
     * @brief Class Constructor
     */
    public JissCaret() {
    }

    /* (non-Javadoc)
     * @see javax.swing.text.DefaultCaret#damage(java.awt.Rectangle)
     */
    @Override
    protected synchronized void damage(Rectangle r) {
        if (r == null)
            return;

        // give values to x,y,width,height (inherited from java.awt.Rectangle)
        x = r.x;
        y = r.y;
        height = r.height;
        
        if (width <= 0)
            width = getComponent().getWidth();
        
        repaint();
        repaint();
    }

    /* (non-Javadoc)
     * @see javax.swing.text.DefaultCaret#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        final JissConsole console = 
        		JissConsole.class.cast(getComponent());

        final int dot = getDot();
        Rectangle r = null;
        char dotChar;
        try {
            r = console.modelToView(dot);
            if (r == null)
                return;
            dotChar = console.getText(dot, 1).charAt(0);
        } catch (BadLocationException e) {
            return;
        }

        if(Character.isWhitespace(dotChar)) dotChar = '_';

        if ((x != r.x) || (y != r.y)) {
            damage(r);
            return;
        }

        g.setColor(console.getCaretColor());
        g.setXORMode(console.getBackground()); 

        width = g.getFontMetrics().charWidth(dotChar);
        if (isVisible())
            g.fillRect(r.x, r.y, width, r.height);
    }
    
}
