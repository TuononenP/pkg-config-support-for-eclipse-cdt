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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.managedbuilder.pkgconfig.Activator;
import org.eclipse.cdt.managedbuilder.pkgconfig.preferences.PreferenceStore;

/**
 * Runs pkg-config utility in the command line and outputs necessary
 * options to build the given package.
 * 
 */
public class PkgConfigUtil {

	//Constant variables
	private static final String PKG_CONFIG = "pkg-config"; //$NON-NLS-1$
	private static final String LIST_PACKAGES = "--list-all"; //$NON-NLS-1$
	private static final String OUTPUT_LIBS = "--libs"; //$NON-NLS-1$
	private static final String OUTPUT_CFLAGS = "--cflags"; //$NON-NLS-1$
	private static final String OUTPUT_ALL = "--cflags --libs"; //$NON-NLS-1$
	private static final String OUTPUT_ONLY_LIB_PATHS = "--libs-only-L"; //$NON-NLS-1$
	private static final String OUTPUT_ONLY_LIB_FILES = "--libs-only-l"; //$NON-NLS-1$

	/**
	 * Get options needed to build the given package.
	 * Does not like spaces on paths except that getAllPackages seem to work.
	 * 
	 * @param pkgConfigOptions
	 *            Pkg-config options
	 * @param pkg
	 * @return
	 */
	private static String getPkgOutput(String pkgConfigOptions) {
		List<String> pkgOutputs = getPkgOutputs(pkgConfigOptions);
		if (pkgOutputs.isEmpty())
			return null;
		return pkgOutputs.get(0);
	}

	/**
	 * Get options needed to build the given package. Does not like spaces on
	 * paths except that getAllPackages seem to work.
	 * 
	 * @param pkgconfigOptions
	 *            Pkg-config options as --list-all, --libs...
	 * @param pkg
	 *            Pkg-config library name. In case of --list-all option, it must
	 *            be set to null
	 * @return pkg-config command results as a list of string
	 */
	private static List<String> getPkgOutputs(String pkgconfigOptions) {
		ProcessBuilder pb = null;
		String pkgConfigBinPath = PreferenceStore.getPkgConfigBinPath();

		if (pkgConfigBinPath.isEmpty()) {
			if (OSDetector.isWindows())
				pkgConfigBinPath = PKG_CONFIG + ".exe"; //$NON-NLS-1$
			else
				pkgConfigBinPath = PKG_CONFIG;
		}

		String pkgConfigPath = PreferenceStore.getPkgConfigPath();
		StringBuffer pkgConfigCmd = new StringBuffer();

		if (pkgConfigPath != null && !pkgConfigPath.isEmpty()) {
			pkgConfigPath = pkgConfigPath.replace(" ", "\\ "); //$NON-NLS-1$ //$NON-NLS-2$
			if (OSDetector.isWindows()) {
				pkgConfigCmd
						.append(" set PKG_CONFIG_PATH=" + pkgConfigPath + " ");//$NON-NLS-1$ //$NON-NLS-2$
			} else {
				pkgConfigCmd.append(" PKG_CONFIG_PATH=" + pkgConfigPath + " ");//$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		String pkgConfigLibDirPath = PreferenceStore.getPkgConfigLibDir();
		if (pkgConfigLibDirPath != null && !pkgConfigLibDirPath.isEmpty()) {
			pkgConfigLibDirPath = pkgConfigLibDirPath.replace(" ", "\\ "); //$NON-NLS-1$ //$NON-NLS-2$
			if (OSDetector.isWindows()) {
				if (pkgConfigPath != null && !pkgConfigPath.isEmpty())
					pkgConfigCmd.append("&&"); //$NON-NLS-1$
				pkgConfigCmd
						.append(" set PKG_CONFIG_LIBDIR=" + pkgConfigLibDirPath + " ");//$NON-NLS-1$ //$NON-NLS-2$
			} else {
				pkgConfigCmd
						.append(" PKG_CONFIG_LIBDIR=" + pkgConfigLibDirPath + " ");//$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		pkgConfigCmd.append(pkgConfigBinPath);
		if (pkgconfigOptions != null && !pkgconfigOptions.isEmpty())
			pkgConfigCmd.append(" " + pkgconfigOptions);//$NON-NLS-1$

		if (OSDetector.isWindows()) {
			// For Windows the command should look like :
			// cmd /c 'PKG_CONFIG_PATH=/path/to/something pkg-config
			// --list-all'
			pb = new ProcessBuilder("cmd", "/c", pkgConfigCmd.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			// For Unix/Mac the command should look like :
			// bash -c 'PKG_CONFIG_PATH=/path/to/something pkg-config
			// --list-all'
			pb = new ProcessBuilder("bash", "-c", pkgConfigCmd.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		return runCommand(pb);
	}

	/**
	 * Run the process and get the results as string array.
	 * 
	 * @param pb
	 *            Process builder
	 * @return Array of process results
	 */
	private static List<String> runCommand(ProcessBuilder pb) {
		List<String> results = new ArrayList<String>();

		try {
			Process p = pb.start();
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			do {
				line = input.readLine();
				if (line != null) {
					results.add(line);
				}
			} while (line != null);
			input.close();
		} catch (IOException e) {
			Activator
					.getDefault()
					.log(e,
							"Starting a process (executing a command line script) failed."); //$NON-NLS-1$
		}
		return results;
	}
	
	/**
	 * Get cflags and libraries needed to build the given package.
	 * 
	 * @param pkg
	 * @return
	 */
	public static String getAll(String pkg) {
		return getPkgOutput(OUTPUT_ALL + " " + pkg); //$NON-NLS-1$
	}
	
	/**
	 * Get libraries (files and paths) needed to build the given package.
	 * 
	 * @param pkg
	 * @return
	 */
	public static String getLibs(String pkg) {
		return getPkgOutput(OUTPUT_LIBS + " " + pkg); //$NON-NLS-1$
	}
	
	/**
	 * Get library paths needed to build the given package.
	 * 
	 * @param pkg
	 * @return
	 */
	public static String getLibPathsOnly(String pkg) {
		return getPkgOutput(OUTPUT_ONLY_LIB_PATHS + " " + pkg); //$NON-NLS-1$
	}

	/**
	 * Get library files needed to build the given package.
	 * 
	 * @param pkg
	 * @return
	 */
	public static String getLibFilesOnly(String pkg) {
		return getPkgOutput(OUTPUT_ONLY_LIB_FILES + " " + pkg); //$NON-NLS-1$
	}
	
	/**
	 * Get cflags needed to build the given package.
	 * 
	 * @param pkg
	 * @return
	 */
	public static String getCflags(String pkg) {
		return getPkgOutput(OUTPUT_CFLAGS + " " + pkg); //$NON-NLS-1$
	}
	
	/**
	 * Get all packages that pkg-config utility finds (package name with
	 * description).
	 * 
	 * @return
	 */
	public static List<String> getAllPackages() {
		return getPkgOutputs(LIST_PACKAGES);
	}
	
}
