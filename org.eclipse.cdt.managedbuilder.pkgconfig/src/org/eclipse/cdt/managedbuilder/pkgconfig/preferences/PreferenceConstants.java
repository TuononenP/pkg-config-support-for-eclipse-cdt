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
package org.eclipse.cdt.managedbuilder.pkgconfig.preferences;

public class PreferenceConstants {

	public static final String PKG_CONFIG_PATH = "A colon-separated" + //$NON-NLS-1$
			" (on Windows, semicolon-separated) list of directories" + //$NON-NLS-1$
			" to search for .pc files."; //$NON-NLS-1$
	
	public static final String PKG_CONFIG_LIBDIR = "Replaces the" + //$NON-NLS-1$
			" default pkg-config search directory."; //$NON-NLS-1$

	public static final String PKG_CONFIG_BIN = "Path to the pkg-config executable"; //$NON-NLS-1$
}