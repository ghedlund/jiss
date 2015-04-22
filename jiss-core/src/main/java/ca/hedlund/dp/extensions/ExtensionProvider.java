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
package ca.hedlund.dp.extensions;

/**
 * Handles installation of extensions on {@link IExtendable}
 * object.  Extensions which require automatic loading
 * should provide an implementation of {@link ExtensionProvider}.
 */
public interface ExtensionProvider {

	/**
	 * Install extension on an extendable object.
	 */
	public void installExtension(IExtendable obj);
	
}
