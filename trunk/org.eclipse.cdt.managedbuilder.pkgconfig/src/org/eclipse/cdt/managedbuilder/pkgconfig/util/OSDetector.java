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
 *	Detects the operating system of the machine. 
 *
 */
public class OSDetector {

	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		return (os.indexOf("nix") >=0 || os.indexOf("nux") >=0); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		return (os.indexOf("win") >= 0);  //$NON-NLS-1$
	}

	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		return (os.indexOf("mac") >= 0);  //$NON-NLS-1$
	}

}