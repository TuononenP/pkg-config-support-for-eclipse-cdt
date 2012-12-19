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
import org.eclipse.cdt.managedbuilder.pkgconfig.util.Separators;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * Class used to access the Pkg-config preference store values.
 * 
 * This class is not intended to be subclassed by clients.
 */
public class PreferenceStore {

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
	 * Get pkg-config path from the preference store.
	 * 
	 * @return pkg-config path.
	 */
	public static String getPkgConfigPath() {
		return getPreferenceStoreValue(PreferenceConstants.PKG_CONFIG_PATH);
	}

	/**
	 * Set pkg-config path to the preference store.
	 * 
	 * @param path The pkg-config path.
	 */
	public static void setPkgConfigPath(String path) {
		setPreferenceStoreValue(PreferenceConstants.PKG_CONFIG_PATH, path);
	}
	
	/**
	 * Get pkg-config libdir from the preference store.
	 * 
	 * @return pkg-config libdir.
	 */
	public static String getPkgConfigLibDir() {
		return getPreferenceStoreValue(PreferenceConstants.PKG_CONFIG_LIBDIR);
	}

	/**
	 * Set pkg-config libdir to the preference store.
	 * 
	 * @param path The pkg-config libdir.
	 */
	public static void setPkgConfigLibDir(String path) {
		setPreferenceStoreValue(PreferenceConstants.PKG_CONFIG_LIBDIR, path);
	}
	
	/**
	 * Get values from the preference store as a String array.
	 * Used to get preference store values which consist of multiple paths
	 * separated by a path separator.
	 * 
	 * @param name the name of the preference
	 * @return A String array containing all preference store values
	 */
	public static String[] getPreferenceStoreValueAsArray(String name) {
		return ArrayUtil.stringToArray(name);
	}

	/**
	 * Get existing paths from the Preference store.
	 * 
	 * @param name the name of the preference
	 * @return paths 
	 */
	private static String getExistingValues(String name) {
		String paths = ""; //$NON-NLS-1$
		if (name.equals(PreferenceConstants.PKG_CONFIG_PATH)) {
			paths = getPkgConfigPath();
		} else if (name.equals(PreferenceConstants.PKG_CONFIG_LIBDIR)) {
			paths = getPkgConfigLibDir();
		}	
		return paths;
	}

	/**
	 * Append a new value to the Preference store if it doesn't already exists.
	 * 
	 * @param name the name of the preference
	 * @param value the string-valued preference
	 */
	public static void appendValue(String name, String value) {
		StringBuffer sB = new StringBuffer();
		String paths = null;
		//get existing paths
		paths = getExistingValues(name);
		//if values exist
		if (paths.length()!=0) {
			//if the value is reasonable
			if (!value.equalsIgnoreCase("") && value.length()!=0) { //$NON-NLS-1$
				//if the paths doesn't contain the new value
				if (!paths.contains(value)) {
					//append existing paths to the string buffer
					sB.append(paths);
					//add a path separator in the end if it doesn't exists
					if (paths.charAt(paths.length()-1) != Separators.getPathSeparator().charAt(0)) {
						sB.append(Separators.getPathSeparator());
					}
					//append the new value to end of the list
					sB.append(value);
				}				
			}
		} else { //no existing values
			//if the value is reasonable
			if (!value.equalsIgnoreCase("") && value.length()!=0) { //$NON-NLS-1$
				//append a new path to the string buffer
				sB.append(value);
			}
		}
		String newValues = sB.toString();
		if (newValues.length()!=0) {
			//set the new preference store value
			setPreferenceStoreValue(name, newValues);			
		}
	}

	/**
	 * Append pkg-config path to the preference store.
	 * 
	 * @param path The pkg-config path.
	 */
	public static void appendPkgConfigPath(String path) {
		appendValue(PreferenceConstants.PKG_CONFIG_PATH, path);
	}

	/**
	 * Remove a value from the preference store.
	 * 
	 * @param name Name of the preference
	 * @param value Value to be removed from the preference store
	 */
	public static void removeValue(String name, String value) {
		StringBuffer sB = new StringBuffer();
		String existingValues = null;
		String newValue = null;
		//get existing values
		existingValues = getExistingValues(name);
		//if the String contains the value
		if (existingValues.contains(value)) {
			//if many values i.e. contains path separator
			if (existingValues.contains(Separators.getPathSeparator())) {
				//separate String of values to an array
				String[] exValArray = existingValues.split(Separators.getPathSeparator());
				//if more than one value
				if (exValArray.length > 1) {
					//remove the value from the array
					exValArray = ArrayUtil.removePathFromExistingPathList(exValArray, value);
					//if the array isn't empty
					if (exValArray.length > 0) {
						//append all values to the StringBuffer excluding the removed one
						for (String val : exValArray) {
							//append a value
							sB.append(val);
							//append a path separator
							sB.append(Separators.getPathSeparator());
						}
						//form a String
						newValue = sB.toString();
					}
				} else { //only one value with a path separator at the end
					newValue = ""; //$NON-NLS-1$
				}

			} else { //only value without a path separator at the end
				newValue = ""; //$NON-NLS-1$
			}
			//set the new preference store value
			setPreferenceStoreValue(name, newValue);
		}
	}

	/**
	 * Remove pkg-config path from the preference store.
	 * 
	 * @param path The include path to be removed from the preference store.
	 */
	public static void removePkgConfigPath(String path) {
		removeValue(PreferenceConstants.PKG_CONFIG_PATH, path);
	}

	/**
	 * Remove pkg-config libdir from the preference store.
	 * 
	 * @param path The pkg-config libdir to be removed from the preference store.
	 */
	public static void removeLibraryPath(String path) {
		removeValue(PreferenceConstants.PKG_CONFIG_LIBDIR, path);
	}

}