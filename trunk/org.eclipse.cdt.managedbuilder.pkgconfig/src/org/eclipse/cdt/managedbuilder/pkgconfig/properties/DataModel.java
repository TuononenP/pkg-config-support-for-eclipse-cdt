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

public class DataModel {
	
	private String pkg;
	private String desc;

	public DataModel(String pkg, String desc) {
		super();
		this.pkg = pkg;
		this.desc = desc;
	}

	public String getPackage() {
		return this.pkg;
	}

	public String getDescription() {
		return this.desc;
	}
	
	@Override
	public String toString() {
		return this.pkg;
	}

}