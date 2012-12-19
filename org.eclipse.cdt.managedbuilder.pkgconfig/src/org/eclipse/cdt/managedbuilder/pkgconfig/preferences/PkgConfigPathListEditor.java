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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * New implementation of PkgConfigListEditor.
 * Used to select PKG_CONFIG_PATH values from the dialog.
 * 
 */
public class PkgConfigPathListEditor extends PkgConfigListEditor {

	/**
	 * Constructor.
	 * 
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 */
	PkgConfigPathListEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}
	
	@Override
	/**
	 * Functionality for New button.
	 * Shows a browse dialog to select a directory and returns that directory path.
	 */
	protected String getNewInputObject() {
		DirectoryDialog dlg = new DirectoryDialog(getShell());
		final Text text = new Text(getShell(), SWT.BORDER);
		dlg.setFilterPath(text.getText());
		dlg.setText(Messages.PkgConfigPathListEditor_0);
		dlg.setMessage(Messages.PkgConfigPathListEditor_1);
		String dir = dlg.open();
		if(dir == null) {
			return null;
		}
		//remove white spaces
		dir = dir.trim();
		if (dir.length()!=0) {
			//get all existing items in the list
			String[] existingItems = getList().getItems();
			//check that the list doesn't already contain the added item
			if (existingItems.length>0) {
				//return null if duplicate item found
				for (String item : existingItems) {
					if (item.equalsIgnoreCase(dir)) {
						return null;
					}
				}					
			}
			//add a new PKG_CONFIG_PATH to the preference store
			PreferenceStore.appendPkgConfigPath(dir);
			return dir;
		}
		return null;
	}

	@Override
	/**
	 * Removes the path from the list as well as from the Tool's Option.
	 */
	protected void removePressed() {
		List incList = getList();
        setPresentsDefaultValue(false);
        String[] selected = incList.getSelection();
        for (String s : selected) {
            //remove PKG_CONFIG_PATH from the preference store
            PreferenceStore.removePkgConfigPath(s);
    		incList.remove(s);
    		selectionChanged();
        }
	}
	
}