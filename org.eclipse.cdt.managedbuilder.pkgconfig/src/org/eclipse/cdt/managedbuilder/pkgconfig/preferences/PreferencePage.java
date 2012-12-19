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

import org.eclipse.cdt.internal.core.envvar.EnvironmentVariableManager;
import org.eclipse.cdt.internal.core.envvar.UserDefinedEnvironmentSupplier;
import org.eclipse.cdt.managedbuilder.pkgconfig.Activator;
import org.eclipse.cdt.utils.envvar.StorableEnvironment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog.
 * 
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class.
 * 
 */
public class PreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

	private PkgConfigPathListEditor configPathListEditor;
	private LibDirFieldEditor libDirEditor;
	
	/**
	 * Constructor.
	 * Set preference page to use the pkg-config preference store.
	 */
	public PreferencePage() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.PreferencePage_0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		//nothing here
	}

	@Override
	/**
	 * Get Description name.
	 * 
	 * @param String Description
	 */
	public String getDescription() {
		return null;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
	@Override
    public boolean performOk() {
		//create PKG_CONFIG_LIBDIR environment variable
		String libEditorValue = this.libDirEditor.getStringValue();
		PreferenceStore.setPkgConfigLibDir(libEditorValue);
		UserDefinedEnvironmentSupplier fUserSupplier = EnvironmentVariableManager.fUserSupplier;
		StorableEnvironment vars = fUserSupplier.getWorkspaceEnvironmentCopy();
		vars.createVariable("PKG_CONFIG_LIBDIR", libEditorValue); //$NON-NLS-1$
		fUserSupplier.setWorkspaceEnvironment(vars);
		
		//create PKG_CONFIG_PATH environment variable
		vars.createVariable("PKG_CONFIG_PATH", PreferenceStore.getPkgConfigPath()); //$NON-NLS-1$
		fUserSupplier.setWorkspaceEnvironment(vars);
		
		restartWorkspaceDialog();
		
        return true;
    }
    
	/**
	 * Shows a dialog asking to restart workspace if pkg-config
	 * preferences have been changed.
	 */
	private static void restartWorkspaceDialog() {
		MessageDialog dialog = new MessageDialog(
				null, "Restart workspace?", null, "Changes made to pkg-config" + //$NON-NLS-1$ //$NON-NLS-2$
						" preferences need workspace restart in order to" + //$NON-NLS-1$
						" take effect.\n\n" + //$NON-NLS-1$
						"Would you like to restart the workspace now?", //$NON-NLS-1$
				MessageDialog.QUESTION,
				new String[] {"Yes", "No"}, //$NON-NLS-1$ //$NON-NLS-2$
				0);
		int result = dialog.open();
		if (result==0) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					PlatformUI.getWorkbench().restart();
				}
			});
		}
	}
	
    @Override
    protected void performApply() {
        performOk();
    }
    
	@Override
	/**
	 * Creates field editors for the preference page.
	 */
	protected void createFieldEditors() {
		//list editor for PKG_CONFIG_PATH values
		this.configPathListEditor = new PkgConfigPathListEditor(
				PreferenceConstants.PKG_CONFIG_PATH, Messages.PreferencePage_1, 
				getFieldEditorParent());
		addField(this.configPathListEditor);
		//field PKG_CONFIG_LIBDIR
		this.libDirEditor = new LibDirFieldEditor(PreferenceConstants.PKG_CONFIG_LIBDIR, 
				Messages.PreferencePage_2, getFieldEditorParent());
		addField(this.libDirEditor); 
	}

}