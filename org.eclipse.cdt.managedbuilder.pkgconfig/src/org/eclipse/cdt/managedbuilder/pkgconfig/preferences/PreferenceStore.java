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

import org.eclipse.cdt.managedbuilder.pkgconfig.Activator;
import org.eclipse.cdt.managedbuilder.pkgconfig.util.ArrayUtil;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Class used to access the Pkg-config preference store values.
 * 
 * This class is not intended to be subclassed by clients.
 */
public class PreferenceStore {

	private static final String PATH_SEPARATOR = ";"; //$NON-NLS-1$
	private static final String PKG_CONFIG_BIN_KIND = "PKG_CONFIG_BIN_KIND"; //$NON-NLS-1$
	private static final String PKG_CONFIG_BIN = "PKG_CONFIG_BIN"; //$NON-NLS-1$
	private static final String PKG_CONFIG_LIBDIR = "PKG_CONFIG_LIBDIR"; //$NON-NLS-1$
	private static final String PKG_CONFIG_PATH = "PKG_CONFIG_PATH"; //$NON-NLS-1$

	public enum PkgConfigExecutable {
		Default, Custom
	}

	/**
	 * Get the Pkg-config preference store.
	 * 
	 * @return Pkg-config preference store.
	 */
	public static IEclipsePreferences getPreferenceStore() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		return prefs;
	}

	/**
	 * Get a value from the preference store.
	 * 
	 * @param name the name of the preference
	 * @return the string-valued preference
	 */
	public static String getPreferenceStoreValue(String name) {
		return getPreferenceStore().get(name, ""); //$NON-NLS-1$
	}

	/**
	 * Set preference store value.
	 * 
	 * @param name the name of the preference
	 * @param value the string-valued preference
	 */
	public static void setPreferenceStoreValue(String name, String value) {
		getPreferenceStore().put(name, value);
	}

	/**
	 * Clear preference store value.
	 * 
	 * @param name
	 *            the name of the preference
	 */
	public static void clearPreferenceStoreValue(String name) {
		getPreferenceStore().put(name, ""); //$NON-NLS-1$
	}

	/**
	 * Set pkg-config executable type to the preference store.
	 * 
	 * @param pkgConfigExecutable
	 *            The pkg-config executable.
	 * @param project
	 */
	public static void setPkgConfigExecutable(
			PkgConfigExecutable pkgConfigExecutable, String project) {
		setPreferenceStoreValue(getPkgConfigExecutableKey(project),
				pkgConfigExecutable.toString());
	}

	/**
	 * Is pkg-config executable set to default in the preference store.
	 * 
	 * @return True if the executable to use is the deafult one else return false to use the custom.
	 */
	public static boolean isPkgConfigExecutableDefault(String project) {
		return !PkgConfigExecutable.Custom.name().equals(
				getPreferenceStoreValue(getPkgConfigExecutableKey(project)));
	}

	/**
	 * Set pkg-config bin to the preference store.
	 * 
	 * @param path
	 *            The pkg-config bin.
	 * @param project
	 */
	public static void setPkgConfigBinPath(String path, String project) {
		setPreferenceStoreValue(getPkgConfigBinKey(project), path);
	}

	/**
	 * Get pkg-config bin path from the preference store.
	 * 
	 * @return pkg-config bin path.
	 */
	public static String getPkgConfigBinPath(String project) {
		return getPreferenceStoreValue(getPkgConfigBinKey(project));
	}

	/**
	 * Get pkg-config path from the preference store.
	 * 
	 * @return pkg-config path.
	 */
	public static String[] getPkgConfigPath(String project) {
		String pkgConfigPathStringValue = getPreferenceStoreValue(getPkgConfigPathKey(project));
		if (pkgConfigPathStringValue.length() == 0)
			return null;
		return pkgConfigPathStringValue.split(PATH_SEPARATOR);
	}

	/**
	 * Set pkg-config path to the preference store.
	 * 
	 * @param path The pkg-config path
	 * @param project The project name
	 */
	public static void setPkgConfigPath(String path, String project) {
		String pkgConfigPath = getPreferenceStoreValue(getPkgConfigPathKey(project));
		if (pkgConfigPath.length() != 0) {
			pkgConfigPath += PATH_SEPARATOR;
		}
		setPreferenceStoreValue(getPkgConfigPathKey(project), pkgConfigPath
				+ path);
	}

	/**
	 * Get pkg-config libdir from the preference store.
	 * 
	 * @return pkg-config libdir.
	 */
	public static String[] getPkgConfigLibDir(String project) {
		String pkgConfigLibDirStringValue = getPreferenceStoreValue(getPkgConfigLibDirKey(project));
		if (pkgConfigLibDirStringValue.length() == 0)
			return null;
		return pkgConfigLibDirStringValue.split(PATH_SEPARATOR);
	}

	/**
	 * Set pkg-config libdir to the preference store.
	 * 
	 * @param path The pkg-config libdir
	 * @param project The project name
	 */
	public static void setPkgConfigLibDir(String path, String project) {
		String pkgConfigPath = getPreferenceStoreValue(getPkgConfigLibDirKey(project));
		if (pkgConfigPath.length() != 0) {
			pkgConfigPath += PATH_SEPARATOR;
		}
		setPreferenceStoreValue(getPkgConfigLibDirKey(project), pkgConfigPath
				+ path);
	}

	/**
	 * Clear pkg-config path to the preference store.
	 * 
	 * @param project
	 *            The project name
	 */
	public static void clearPkgConfigPath(String project) {
		clearPreferenceStoreValue(getPkgConfigPathKey(project));
	}

	/**
	 * Clear pkg-config libdir path to the preference store.
	 * 
	 * @param project
	 *            The project name
	 */
	public static void clearPkgConfigLibDir(String project) {
		clearPreferenceStoreValue(getPkgConfigLibDirKey(project));
	}

	/**
	 * Get values from the preference store as a String array. Used to get
	 * preference store values which consist of multiple paths separated by a
	 * path separator.
	 * 
	 * @param name
	 *            the name of the preference
	 * @return A String array containing all preference store values
	 */
	public static String[] getPreferenceStoreValueAsArray(String name) {
		return ArrayUtil.stringToArray(name);
	}

	/**
	 * Compute the key for the given pkg-config binary and the given project.
	 * 
	 * @param project
	 *            Project name
	 * @return Key
	 */
	private static String getPkgConfigBinKey(String project) {
		return PKG_CONFIG_BIN + " - " //$NON-NLS-1$
				+ project;
	}

	/**
	 * Compute the key for the given pkg-config binary kind and the given
	 * project.
	 * 
	 * @param project
	 *            Project name
	 * @return Key
	 */
	private static String getPkgConfigExecutableKey(String project) {
		return PKG_CONFIG_BIN_KIND + " - " //$NON-NLS-1$
				+ project;
	}

	/**
	 * Compute the key for the given pkg-config lib dir and the given project.
	 * 
	 * @param project
	 *            Project name
	 * @return Key
	 */
	private static String getPkgConfigLibDirKey(String project) {
		return PKG_CONFIG_LIBDIR + " - " //$NON-NLS-1$
				+ project;
	}
	
	/**
	 * Compute the key for the given pkg-config path and the given project.
	 * 
	 * @param project
	 *            Project name
	 * @return Key
	 */
	private static String getPkgConfigPathKey(String project) {
		return PKG_CONFIG_PATH + " - " //$NON-NLS-1$
				+ project;
	}
}