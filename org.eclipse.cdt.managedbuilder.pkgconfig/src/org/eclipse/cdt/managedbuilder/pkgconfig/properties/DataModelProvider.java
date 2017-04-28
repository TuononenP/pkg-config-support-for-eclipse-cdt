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
package org.eclipse.cdt.managedbuilder.pkgconfig.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.managedbuilder.pkgconfig.util.Parser;
import org.eclipse.cdt.managedbuilder.pkgconfig.util.PkgConfigUtil;

public class DataModelProvider {

	private List<DataModel> dms = new ArrayList<DataModel>();

	/**
	 * Initialize.
	 * @param project Project
	 */
	DataModelProvider(String project) {

		List<String> packages = PkgConfigUtil.getAllPackages(project);
		List<String> pkgList = Parser.parsePackageList(packages);
		List<String> nonSortedPkgList = Parser.parsePackageList(packages);
		HashMap<Integer, Integer> origSortedIdx = new HashMap<Integer, Integer>();
		Collections.sort(pkgList, String.CASE_INSENSITIVE_ORDER);
		int sortedIdx;
		for (int i=0; i<pkgList.size(); i++) {
			//get the index of sorted value
			sortedIdx = pkgList.indexOf(nonSortedPkgList.get(i));
			origSortedIdx.put(new Integer(i), new Integer(sortedIdx)); //map sorting
		}
		
		//get descriptions and sort according to package names
		List<String> descs = Parser.parseDescription(packages);
		int cellPlace;
		String[] sortedArray = new String[descs.size()];
		for (int i=0; i<descs.size(); i++) {
			cellPlace = origSortedIdx.get(new Integer(i)).intValue();
			sortedArray[cellPlace] = descs.get(i);
		}
		
		List<String> descList = Arrays.asList(sortedArray);  
		for (int i=0; i<descList.size(); i++) {
			this.dms.add(new DataModel(pkgList.get(i), descList.get(i)));
		}
	}

	/**
	 * Get the DataModel entries
	 * .
	 * @return
	 */
	public List<DataModel> getEntries() {
		return this.dms;
	}

}