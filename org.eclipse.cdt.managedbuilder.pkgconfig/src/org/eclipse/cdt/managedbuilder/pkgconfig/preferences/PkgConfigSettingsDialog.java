/*******************************************************************************
 * Copyright (c) 2012 Melanie Bats and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Melanie Bats - Initial implementation
 *******************************************************************************/

package org.eclipse.cdt.managedbuilder.pkgconfig.preferences;

import org.eclipse.cdt.ui.newui.AbstractPropertyDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PkgConfigSettingsDialog extends AbstractPropertyDialog {
	private static final String UNSET_PKG_CONFIG = ""; //$NON-NLS-1$
	private static final String PKG_CONFIG_PATH = Messages.PkgConfigSettingsDialog_0;
	private static final String PKG_CONFIG_LIBDIR = Messages.PkgConfigSettingsDialog_1;

	private Combo pkgConfigPathKindCombo;
	private PkgConfigBinPathFieldEditor pkgconfigBinPathEditor;
	private PkgConfigPathListEditor configPathListEditor;

	private Button buttonOk;
	private Button buttonCancel;

	private String projectName;

	public PkgConfigSettingsDialog(Shell _parent, String title,
			String projectName) {
		super(_parent, title);
		this.projectName = projectName;
	}

	@Override
	public void buttonPressed(SelectionEvent e) {
		if (e.widget.equals(this.buttonOk)) {
			String pkgConfigBinEditorValue = this.pkgconfigBinPathEditor
					.getStringValue();
			PreferenceStore.setPkgConfigBinPath(pkgConfigBinEditorValue,
					this.projectName);

			String pkgConfigPathKind = this.pkgConfigPathKindCombo.getText();

			PreferenceStore.clearPkgConfigLibDir(this.projectName);
			PreferenceStore.clearPkgConfigPath(this.projectName);
			if (pkgConfigPathKind.length() != 0) {
				for (String pkgConfigPath : this.configPathListEditor
						.getPkgConfigPaths()) {
					if (PKG_CONFIG_PATH.equals(pkgConfigPathKind)) {
						PreferenceStore.setPkgConfigPath(pkgConfigPath,
								this.projectName);
					} else if (PKG_CONFIG_LIBDIR.equals(pkgConfigPathKind)) {
						PreferenceStore.setPkgConfigLibDir(pkgConfigPath,
								this.projectName);
					}
				}
			}
		}

		this.shell.dispose();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.shell.setSize(700, 250);
		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);
		Composite composite = new Composite(parent, SWT.None);
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(3, false));

		this.pkgconfigBinPathEditor = new PkgConfigBinPathFieldEditor(
				PreferenceConstants.PKG_CONFIG_BIN, Messages.PreferencePage_3,
				composite);

		this.pkgconfigBinPathEditor.setStringValue(PreferenceStore
				.getPkgConfigBinPath(this.projectName));

		Label comboLabel = new Label(composite, SWT.WRAP);
		GridData comboLabelLayout = new GridData();
		comboLabel.setLayoutData(comboLabelLayout);
		comboLabel.setText(Messages.PkgConfigSettingsDialog_2);

		this.pkgConfigPathKindCombo = new Combo(composite, SWT.BORDER
				| SWT.READ_ONLY);
		GridData comboLayout = new GridData();

		this.pkgConfigPathKindCombo.setItems(new String[] { UNSET_PKG_CONFIG,
				PKG_CONFIG_PATH, PKG_CONFIG_LIBDIR });
		this.pkgConfigPathKindCombo.setLayoutData(comboLayout);

		// Get pkg-config path and pkg-config libdir for the current project
		String[] pkgConfigPath = PreferenceStore
				.getPkgConfigPath(this.projectName);
		String[] pkgConfigLibDir = PreferenceStore
				.getPkgConfigLibDir(this.projectName);

		Composite composite2 = new Composite(composite, SWT.None);
		GridData layoutData2 = new GridData(GridData.FILL_BOTH);
		layoutData2.horizontalAlignment = SWT.FILL;
		layoutData2.horizontalSpan = 3;

		composite2.setLayoutData(layoutData2);
		composite2.setLayout(new GridLayout(2, false));
		this.configPathListEditor = new PkgConfigPathListEditor(
				PreferenceConstants.PKG_CONFIG_PATH, "", composite2); //$NON-NLS-1$

		// Initialize combo to current value
		if (pkgConfigLibDir != null) {
			// Select lib dir
			this.pkgConfigPathKindCombo.select(2);
			this.configPathListEditor.setData(pkgConfigLibDir);
		} else if (pkgConfigPath != null) {
			// Select config path
			this.pkgConfigPathKindCombo.select(1);
			this.configPathListEditor.setData(pkgConfigPath);
		} else {
			// None
			this.pkgConfigPathKindCombo.select(0);
			this.pkgConfigPathKindCombo.setData(UNSET_PKG_CONFIG);
		}

		// Buttons
		Composite compButtons = new Composite(composite, SWT.FILL);
		GridData gd = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false);
		gd.horizontalSpan = 4;
		// gd.grabExcessVerticalSpace = true;
		compButtons.setLayoutData(gd);
		compButtons.setLayout(new GridLayout(2, true));

		// Button Cancel
		this.buttonCancel = new Button(compButtons, SWT.PUSH);
		this.buttonCancel.setText(IDialogConstants.CANCEL_LABEL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		this.buttonCancel.setLayoutData(gd);
		this.buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				buttonPressed(event);
			}
		});

		// Button OK
		this.buttonOk = new Button(compButtons, SWT.PUSH);
		this.buttonOk.setText(IDialogConstants.OK_LABEL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		this.buttonOk.setLayoutData(gd);
		this.buttonOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				buttonPressed(event);
			}
		});

		parent.getShell().setDefaultButton(this.buttonOk);
		return parent;
	}
}
