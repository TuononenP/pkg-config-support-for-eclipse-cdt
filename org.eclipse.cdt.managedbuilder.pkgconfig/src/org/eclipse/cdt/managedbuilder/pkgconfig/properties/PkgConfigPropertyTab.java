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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.core.settings.model.ICStorageElement;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.pkgconfig.Activator;
import org.eclipse.cdt.managedbuilder.pkgconfig.preferences.LibDirFieldEditor;
import org.eclipse.cdt.managedbuilder.pkgconfig.preferences.Messages;
import org.eclipse.cdt.managedbuilder.pkgconfig.preferences.PkgConfigBinPathFieldEditor;
import org.eclipse.cdt.managedbuilder.pkgconfig.preferences.PkgConfigPathListEditor;
import org.eclipse.cdt.managedbuilder.pkgconfig.preferences.PkgConfigSettingsDialog;
import org.eclipse.cdt.managedbuilder.pkgconfig.settings.PkgConfigExternalSettingProvider;
import org.eclipse.cdt.managedbuilder.pkgconfig.util.Parser;
import org.eclipse.cdt.managedbuilder.pkgconfig.util.PathToToolOption;
import org.eclipse.cdt.managedbuilder.pkgconfig.util.PkgConfigUtil;
import org.eclipse.cdt.ui.newui.AbstractCPropertyTab;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Property tab to select packages and add pkg-config output of checked packages
 * to compiler and linker (MBS).
 * 
 */
public class PkgConfigPropertyTab extends AbstractCPropertyTab {

	PkgConfigBinPathFieldEditor pkgConfigBinPathFieldEditor;
	PkgConfigPathListEditor configPathListEditor;
	LibDirFieldEditor libDirEditor;
	CheckboxTableViewer pkgCfgViewer;
	private Set<Object> previouslyChecked;
	private ArrayList<Object> newItems = new ArrayList<Object>();
	private static final int BUTTON_SELECT = 0;
	private static final int BUTTON_DESELECT = 1;
	private static final int BUTTON_ADVANCED = 2;
	private final String PACKAGES = "packages"; //$NON-NLS-1$
	private boolean reindexToggle = false;

	private SashForm sashForm;

	private static final String[] BUTTONS = new String[] { "Select", //$NON-NLS-1$
			"Deselect", //$NON-NLS-1$
			"Advanced..." //$NON-NLS-1$
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.ui.newui.AbstractCPropertyTab#createControls(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		super.createControls(parent);
		this.usercomp.setLayout(new GridLayout(1, false));

		this.sashForm = new SashForm(this.usercomp, SWT.NONE);
		this.sashForm.setBackground(this.sashForm.getDisplay().getSystemColor(
				SWT.COLOR_GRAY));
		this.sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 5;
		this.sashForm.setLayout(layout);

		final Composite c1 = new Composite(this.sashForm, SWT.NONE);
		GridLayout layout2 = new GridLayout(3, false);
		c1.setLayout(layout2);

		this.pkgCfgViewer = CheckboxTableViewer
				.newCheckList(c1, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
						| SWT.FULL_SELECTION | SWT.BORDER);
		final Table tbl = this.pkgCfgViewer.getTable();
		tbl.setHeaderVisible(true);
		tbl.setLinesVisible(true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		tbl.setLayoutData(gd);

		createColumns(c1, this.pkgCfgViewer);
		this.pkgCfgViewer.setContentProvider(new ArrayContentProvider());
		this.pkgCfgViewer.setInput(new DataModelProvider(this.page.getProject()
				.getName()).getEntries());

		this.pkgCfgViewer.addCheckStateListener(new PkgListener());

		this.pkgCfgViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				TableItem itm = tbl.getSelection()[0];
				if (itm.getChecked()) {
					itm.setChecked(false);
				} else {
					itm.setChecked(true);
				}
				handleCheckStateChange();
			}
		});

		// buttons
		Composite compositeButtons = new Composite(c1, SWT.NONE);
		initButtons(compositeButtons, BUTTONS);

		initializePackageStates();
		this.previouslyChecked = new HashSet<Object>(
				Arrays.asList(getCheckedItems()));
	}

	/**
	 * Get checked items.
	 * 
	 * @return
	 */
	private Object[] getCheckedItems() {
		return this.pkgCfgViewer.getCheckedElements();
	}

	/**
	 * Action for the check state change.
	 */
	void handleCheckStateChange() {
		Object[] checkedItems = getCheckedItems();

		// check if new items checked
		if (checkedItems.length > this.previouslyChecked.size()) {
			// add checked items to an array list
			for (Object o : checkedItems) {
				// if new item
				if (!this.previouslyChecked.contains(o)) {
					this.newItems.add(o);
				}
			}
			addPackageValues(this.newItems.toArray(), this.page.getProject());
			this.reindexToggle = true;
		}

		saveChecked();
		updateData(getResDesc());
		this.previouslyChecked = new HashSet<Object>(
				Arrays.asList(checkedItems));
		this.newItems.clear();
	}

	/**
	 * Add new flags that the packages need to Tools' Options. Only for other
	 * flags.
	 * 
	 * @param addedItems
	 *            Object[]
	 * @param proj
	 *            IProject
	 */
	private static void addPackageValues(Object[] addedItems, IProject proj) {
		for (Object item : addedItems) {
			// handle options
			String cflags = PkgConfigUtil.getCflags(
					item.toString(), proj.getName());
			String[] optionsArray = Parser.parseCflagOptions(cflags);
			if (optionsArray != null) {
				for (String option : optionsArray) {
					PathToToolOption.addOtherFlag(option, proj);
				}
			}
		}
		ManagedBuildManager.saveBuildInfo(proj, true);
	}

	/**
	 * Initializes the check state of the packages from the storage.
	 */
	private void initializePackageStates() {
		ICConfigurationDescription desc = getResDesc().getConfiguration();
		ICStorageElement strgElem = null;
		try {
			strgElem = desc.getStorage(this.PACKAGES, true);
			TableItem[] items = this.pkgCfgViewer.getTable().getItems();
			String value = null;
			for (TableItem item : items) {
				/*
				 * The package names with + symbols were converted so that + ->
				 * plus in order to prevent an error when saving to
				 * ICStorageElement.
				 */
				if (item.getText().contains("+")) { //$NON-NLS-1$
					String newItemName = item.getText().replace("+", "plus"); //$NON-NLS-1$ //$NON-NLS-2$
					value = strgElem.getAttribute(newItemName);
				} else {
					value = strgElem.getAttribute(item.getText());
				}
				if (value != null) {
					if (value.equals("true")) { //$NON-NLS-1$
						item.setChecked(true);
					}
				}
			}
		} catch (CoreException e) {
			Activator.getDefault().log(e, "Initialization of packages failed."); //$NON-NLS-1$
		}
	}

	/**
	 * Saves checked state of the packages.
	 */
	private void saveChecked() {
		ICConfigurationDescription desc = getResDesc().getConfiguration();
		ICStorageElement strgElem = null;
		// get storage or create one if it doesn't exist
		try {
			strgElem = desc.getStorage(this.PACKAGES, true);
		} catch (CoreException e) {
			Activator.getDefault().log(e,
					"Getting packages from the storage failed."); //$NON-NLS-1$
		}
		TableItem[] items = this.pkgCfgViewer.getTable().getItems();
		for (TableItem item : items) {
			if (item != null) {
				String chkd;
				// form literal form of boolean state
				if (item.getChecked()) {
					chkd = "true"; //$NON-NLS-1$
				} else {
					chkd = "false"; //$NON-NLS-1$
				}
				/*
				 * add package name and the checkbox state to the storage
				 */
				try {
					String pkgName = item.getText();
					// need to convert + symbols to "plus"
					if (pkgName.contains("+")) { //$NON-NLS-1$
						String newPkgName = pkgName.replace("+", "plus"); //$NON-NLS-1$ //$NON-NLS-2$
						if (strgElem != null) {
							strgElem.setAttribute(newPkgName, chkd);
						}
					} else {
						if (strgElem != null) {
							strgElem.setAttribute(pkgName, chkd);
						}
					}
				} catch (Exception e) {
					Activator.getDefault().log(e,
							"Setting attribute to ICStorageElement failed."); //$NON-NLS-1$
					// Seems like ICStorageElement cannot store Strings with +
					/*
					 * INVALID_CHARACTER_ERR: An invalid or illegal XML
					 * character is specified.
					 */
				}
			}
		}
	}

	@Override
	protected void performApply(ICResourceDescription src,
			ICResourceDescription dst) {
		updateData(getResDesc());
	}

	@Override
	protected void performDefaults() {
		// uncheck every checkbox
		this.pkgCfgViewer.setCheckedElements(new Object[] {});

		// remove values from Tools Options
		handleCheckStateChange();
	}

	@Override
	protected void performOK() {
		// freshen index if new packages have been selected
		if (this.reindexToggle) {
			rebuiltIndex();
		}
		this.reindexToggle = false;
	}

	@Override
	protected void updateButtons() {
		// nothing here
	}

	@Override
	protected void updateData(ICResourceDescription cfg) {
		final ICConfigurationDescription confDesc = cfg.getConfiguration();
		ICProjectDescription projDesc = confDesc.getProjectDescription();

		Job j = new Job("Update Pkg-config exernal settings provider") { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// a set holding external setting providers
				Set<String> externalSettingsProviders = new LinkedHashSet<String>(
						Arrays.asList(confDesc.getExternalSettingsProviderIds()));

				// remove pkg-config external setting provider
				externalSettingsProviders
						.remove(PkgConfigExternalSettingProvider.ID);
				confDesc.setExternalSettingsProviderIds(externalSettingsProviders
						.toArray(new String[externalSettingsProviders.size()]));

				// add pkg-config external setting provider
				externalSettingsProviders
						.add(PkgConfigExternalSettingProvider.ID);
				confDesc.setExternalSettingsProviderIds(externalSettingsProviders
						.toArray(new String[externalSettingsProviders.size()]));

				// update external setting providers
				confDesc.updateExternalSettingsProviders(new String[] { PkgConfigExternalSettingProvider.ID });
				return Status.OK_STATUS;
			}
		};
		j.setPriority(Job.INTERACTIVE);
		j.schedule();

		try {
			CoreModel.getDefault().setProjectDescription(
					this.page.getProject(), projDesc);
		} catch (CoreException e) {
			Activator.getDefault().log(e,
					"Setting/updating the project description failed."); //$NON-NLS-1$
		}
	}

	/**
	 * Check state listener for the table viewer.
	 * 
	 */
	public class PkgListener implements ICheckStateListener {

		@Override
		public void checkStateChanged(CheckStateChangedEvent e) {
			handleCheckStateChange();
		}
	}

	/**
	 * Creates table columns, headers and sets the size of the columns.
	 * 
	 * @param parent
	 * @param viewer
	 */
	private void createColumns(
			@SuppressWarnings("unused") final Composite parent,
			@SuppressWarnings("unused") final TableViewer viewer) {
		String[] titles = { "Packages", "Description" }; //$NON-NLS-1$ //$NON-NLS-2$
		int[] bounds = { 200, 450 };

		// first column is for the package
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				DataModel dm = (DataModel) element;
				return dm.getPackage();
			}
		});

		// second column is for the description
		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				DataModel dm = (DataModel) element;
				return dm.getDescription();
			}
		});
	}

	/**
	 * Creates a column for the table viewer.
	 * 
	 * @param title
	 * @param bound
	 * @return
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(
				this.pkgCfgViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();

		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);

		return viewerColumn;
	}

	/**
	 * Get selected item(s).
	 * 
	 * @return
	 */
	private TableItem[] getSelected() {
		TableItem[] selected = this.pkgCfgViewer.getTable().getSelection();
		return selected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.ui.newui.AbstractCPropertyTab#buttonPressed(int)
	 */
	@Override
	public void buttonPressed(int n) {
		switch (n) {
		case BUTTON_SELECT:
			selectedButtonPressed();
			break;
		case BUTTON_DESELECT:
			deselectedButtonPressed();
			break;
		case BUTTON_ADVANCED:
			advancedButtonPressed();
			break;
		default:
			break;
		}
		updateButtons();
	}

	/**
	 * Action for the Select button.
	 */
	private void selectedButtonPressed() {
		TableItem[] selected = getSelected();
		for (TableItem itm : selected) {
			itm.setChecked(true);
		}
		handleCheckStateChange();
	}

	/**
	 * Action for the Deselect button.
	 */
	private void deselectedButtonPressed() {
		TableItem[] selected = getSelected();
		for (TableItem itm : selected) {
			itm.setChecked(false);
		}
		handleCheckStateChange();
	}

	/**
	 * Action for the Select button.
	 */
	private void advancedButtonPressed() {
		// Create new dialog
		PkgConfigSettingsDialog dialog = new PkgConfigSettingsDialog(
				this.usercomp.getShell(), Messages.PkgConfigPropertyTab_0,
				this.page.getProject().getName());
		dialog.open();
		if (PkgConfigPropertyTab.this.pkgCfgViewer != null) {
			// Update pkg-config libraries for the project
			PkgConfigPropertyTab.this.pkgCfgViewer
					.setInput(new DataModelProvider(
							PkgConfigPropertyTab.this.page.getProject()
									.getName()).getEntries());
		}
	}

	/**
	 * Rebuilts the index of the selected project in the workspace.
	 */
	private void rebuiltIndex() {
		ICProject cproject = CoreModel.getDefault().getCModel()
				.getCProject(this.page.getProject().getName());
		CCorePlugin.getIndexManager().reindex(cproject);
	}

}