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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses pkg-config utility output.
 *
 */
public class Parser {
	
	// Regex for matching include path from "pkg-config --cflags" command
	private static Pattern incPathRegex = Pattern.compile("(-I)(/{1}[^/\\s]*)+");//$NON-NLS-1$
	
	// Regex for matching include path from "pkg-config --libs-only-L" command
	private static Pattern libPathRegex = Pattern.compile("(-L)(/{1}[^/\\s]*)+");//$NON-NLS-1$
	
	// Regex for matching include path from "pkg-config --libs-only-l" command
	private static Pattern libsRegex = Pattern.compile("(-l)([^/\\s]*)+");//$NON-NLS-1$
	/**
	 * Parses options from "pkg-config --cflags" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseCflagOptions(String s) throws NullPointerException {
		if (s != null && !s.isEmpty()) {
			String str = null;
			//find the first index where include list starts, look for -I<unix-path>
			//Using regex (-I)(/[^/\s]*)+
			Matcher m = incPathRegex.matcher(s);
			// removing matches leaving only the flags
			str = m.replaceAll("").trim();//$NON-NLS-1$
			// splitting on the spaces
			return str.split(" ");//$NON-NLS-1$
		}
		return null;
			
	}
	
	/**
	 * Parses include paths from "pkg-config --cflags" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseIncPaths(String s) throws NullPointerException {
		if (s != null && !s.isEmpty()) {
			String str = null;
			//Using regex (-I)(/{1}[^/\s]*)+")
			Matcher m = incPathRegex.matcher(s);
			ArrayList<String> incPaths = new ArrayList<String>();
			while(m.find() && (str = m.group())!= null && !str.isEmpty()) {
				// removing '-I' then inserting path into array
				incPaths.add(str.substring(2,str.length()));
			}
			return incPaths.size()>0?incPaths.toArray(new String[incPaths.size()]):null;
		}
		return null;
	}
	

	/**
	 * Parses library search paths from "pkg-config --libs-only-L" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseLibPaths2(String s) throws NullPointerException{
		if (s != null && !s.isEmpty()) {
			String str = null;
			//Using regex (-L)(/{1}[^/\s]*)+
			Matcher m = libPathRegex.matcher(s);
			ArrayList<String> libPaths = new ArrayList<String>();
			while(m.find() && (str = m.group())!= null && !str.isEmpty()) {
				// removing '-L' then inserting path into array
				libPaths.add(str.substring(2,str.length()));
			}
			return libPaths.size()>0?libPaths.toArray(new String[libPaths.size()]):null;
		}
		return null;
	}
	
	/**
	 * Parses libraries from "pkg-config --libs-only-l" input.
	 * 
	 * @param s Output from pkg-config.
	 * @return Parsed String array.
	 */
	public static String[] parseLibs2(String s) throws NullPointerException {
		if (s != null && !s.isEmpty()) {
			String str = null;
			//Using regex (-l)([^/\s]*)+
			Matcher m = libsRegex.matcher(s);
			ArrayList<String> libs = new ArrayList<String>();
			while(m.find() && (str = m.group())!= null && !str.isEmpty()) {
				// removing '-l' then inserting path into array
				libs.add(str.substring(2,str.length()));
			}
			return libs.size()>0?libs.toArray(new String[libs.size()]):null;
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
