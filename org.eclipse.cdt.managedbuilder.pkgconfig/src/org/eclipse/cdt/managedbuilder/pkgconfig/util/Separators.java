/*******************************************************************************
 * Copyright (c) 2011 Petri Tuononen and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Petri Tuononen - Initial implementation
 *******************************************************************************/
package org.eclipse.cdt.managedbuilder.pkgconfig.util;

/**
 * Provides different separators such as file and path separators.
 * 
 */
public class Separators {

	private static final String pathSeparator = System.getProperty("path.separator"); //$NON-NLS-1$
	private static final String fileSeparator = System.getProperty("file.separator"); //$NON-NLS-1$
	
	/**
	 * Get path separator.
	 * 
	 * @return  path separator
	 */
	public static String getPathSeparator() {
		return pathSeparator;
	}
	
	/**
	 * Get file separator.
	 * 
	 * @return  file separator
	 */
	public static String getFileSeparator() {
		return fileSeparator;
	}
	
}
