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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.cdt.managedbuilder.pkgconfig.preferences.messages"; //$NON-NLS-1$
	public static String PkgConfigSettingsDialog_0;
	public static String PkgConfigSettingsDialog_1;
	public static String PkgConfigSettingsDialog_2;
	public static String PkgConfigPropertyTab_0;
	public static String PkgConfigPathListEditor_0;
	public static String PkgConfigPathListEditor_1;
	public static String PreferencePage_0;
	public static String PreferencePage_1;
	public static String PreferencePage_2;
	public static String PreferencePage_3;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
