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

public class ArrayUtil {

	/**
	 * Split paths to a String array.
	 * 
	 * @param str String of paths separated by a path separator.
	 * @return String array containing multiple paths.
	 */
	public static String[] stringToArray(String str) {
		return str.split(Separators.getPathSeparator()); 
	}

	/**
	 * Append an array of Strings to a String separated by a path separator.
	 * 
	 * @param array An array of Strings.
	 * @return string which contains all indexes of
	 * a String array separated by a path separator.
	 */
	public static String arrayToString(String[] array) {
		StringBuffer sB = new StringBuffer();
		//if array isn't empty and doesn't contain an empty String 
		if (array.length>0 /*&& !array[0].equals("")*/) {
			for (String i : array) {
				sB.append(i);
				sB.append(Separators.getPathSeparator()); 
			}			
		}
		return sB.toString();
	}
	
	/**
	 * Removes one path from the list of paths.
	 * 
	 * @param existingPaths Existing list of paths to remove from
	 * @param removePath Path to be removed.
	 * @return String[] List that includes existing paths without the path that was removed.
	 */
	public static String[] removePathFromExistingPathList(String[] existingPaths, String removePath) {
		List<String> newPathList = new ArrayList<String>();
		String path;
		//adds existing paths to new paths list
		for (int i = 0; i < existingPaths.length; i++) {
			path = existingPaths[i];
			newPathList.add(path);
		}
		newPathList.remove(removePath);
		//creates a new list that includes all existing paths except the removed path
		String[] newArray = newPathList.toArray(new String[0]);
		return newArray;
	}
	
}
