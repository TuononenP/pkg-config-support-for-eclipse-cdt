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

import java.util.ArrayList;
import java.util.List;

/**
 * Parses pkg-config utility output.
 *
 */
public class Parser {
	
	/**
	 * Parses options from "pkg-config --cflags" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseCflagOptions(String s) throws NullPointerException {
		if (s != null) {
			String str;
			//find the index where include list starts
			int end = s.indexOf("-I"); //$NON-NLS-1$
			if (end != -1) { //includes found
				if (end != 0) { //options found
					//truncate include paths
					str = s.substring(0, end-1);
					//insert options to an array
					String[] options = str.split(" "); //$NON-NLS-1$
					return options;
				} else if (end == 0) { //no options found
					return null;
				}
			} else { //if no includes found
				//check if any flags found
				int flagStart = s.indexOf("-"); //$NON-NLS-1$
				if (flagStart != -1) { //options found
					str = s.substring(flagStart, s.length()-1);
					//insert options into an array
					String[] options = str.split(" "); //$NON-NLS-1$
					return options;
				}
				return null;
			}
		}
		//should not reach here
		return null;
	}
	
	/**
	 * Parses include paths from "pkg-config --cflags" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseIncPaths(String s) throws NullPointerException {
		if (s != null) {
			String str;
			//find the index where include list starts
			int start = s.indexOf("-I"); //$NON-NLS-1$
			if (start != -1) { //if include paths found
				//truncate other than include paths
				str = s.substring(start, s.length()-1);
				//remove library search path flags
				str = str.replace("-I", ""); //$NON-NLS-1$ //$NON-NLS-2$
				//insert include paths into an array
				String[] incPaths = str.split(" "); //$NON-NLS-1$
				return incPaths;
			}
			return null;
		}
		return null;
	}
	
//	/**
//	 * Parses library search paths from "pkg-config --libs" input.
//	 * 
//	 * @param s Output from pkg-config.
//	 * @return Parsed String array.
//	 */
//	public static String[] parseLibPaths(String s) throws NullPointerException {
//		//find the index where library path list starts
//		int start = s.indexOf("-L"); //$NON-NLS-1$
//		String str;
//		if (start != -1) { //if library paths found
//			//find the index where library list starts
//			int end = s.indexOf(" -l"); //$NON-NLS-1$
//			//truncate other than library paths
//			str = s.substring(start, end);
//			//remove library search path flags
//			str = str.replace("-L", ""); //$NON-NLS-1$ //$NON-NLS-2$
//			//insert lib paths into an array
//			String[] libPaths = str.split(" "); //$NON-NLS-1$
//			return libPaths;
//		}
//		return null;
//	}
	
	/**
	 * Parses library search paths from "pkg-config --libs-only-L" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseLibPaths2(String s) throws NullPointerException{
		if (s != null) {
			//remove library search path flags
			String s2 = s.replace("-L", ""); //$NON-NLS-1$ //$NON-NLS-2$
			//insert lib paths into an array
			String[] libPaths = s2.split(" "); //$NON-NLS-1$
			return libPaths;
		}
		return null;
	}
	
//	/**
//	 * Parses libraries from "pkg-config --libs" input.
//	 * 
//	 * @param s Output from pkg-config.
//	 * @return Parsed String array.
//	 */
//	public static String[] parseLibs(String s) throws NullPointerException {
//		if (s != null) {
//			String str;
//			//special case if pkg-config --libs output starts with -l
//			int start = s.indexOf("-l"); //$NON-NLS-1$
//			if (start != 0) {
//				start = s.indexOf(" -l"); //$NON-NLS-1$
//			}
//			if (start != -1) { //if libraries found
//				//truncate library search paths
//				str = s.substring(start+1, s.length()-1);
//				//remove lib flags
//				str = str.replace(" -l", " "); //$NON-NLS-1$ //$NON-NLS-2$
//				//insert libs into an array
//				String[] libs = str.split(" "); //$NON-NLS-1$
//				return libs;
//			}
//			return null;			
//		}
//		return null;	
//	}
	
	/**
	 * Parses libraries from "pkg-config --libs-only-l" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseLibs2(String s) throws NullPointerException {
		if (s != null) {		
			String libStr = s;
			if(libStr.startsWith("-l")) { //$NON-NLS-1$
				libStr = libStr.replaceFirst("-l", ""); //$NON-NLS-1$ //$NON-NLS-2$
				//libStr = libStr.substring(2, libStr.length()-1);
			}
			//remove lib flags
			libStr = libStr.replace(" -l", " "); //$NON-NLS-1$ //$NON-NLS-2$
			//insert libs into an array
			String[] libs = libStr.split(" "); //$NON-NLS-1$
		    return libs;
		}
		return null;
	}
	
	/**
	 * Parse package list so that only package names are added to List.
	 * 
	 * @param packages
	 * @return
	 */
	public static List<String> parsePackageList(List<String> packages) {
		List<String> operated = new ArrayList<String>();
		for (String s : packages) {
			//cut the string after the first white space
			int end = s.indexOf(" "); //$NON-NLS-1$
			operated.add(s.substring(0, end));
		}
		return operated;
	}
	
	/**
	 * Parse package list that only package descriptions are added to List.
	 * 
	 * @param packages
	 * @return
	 */
	public static List<String> parseDescription(List<String> packages) {
		List<String> operated = new ArrayList<String>();
		int ws, start = 0;
		for (String s : packages) {
			ws = s.indexOf(" "); //$NON-NLS-1$
			//read as many characters forward that non white space is found
			find: for (int i=1; i+ws<s.length(); i++) {
				if (s.charAt(ws+i) != ' ') {
					start = ws+i;
					break find;
				}
			}
			operated.add(s.substring(start, s.length()));
		}
		return operated;
	}
	
}
