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
package org.eclipse.cdt.managedbuilder.pkgconfig;

import java.io.IOException;
//import java.util.Arrays;
//import java.util.LinkedHashSet;
import java.util.PropertyResourceBundle;
//import java.util.Set;

import org.eclipse.cdt.core.model.CoreModel;
//import org.eclipse.cdt.core.settings.model.CProjectDescriptionEvent;
//import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
//import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionListener;
//import org.eclipse.cdt.managedbuilder.core.IConfiguration;
//import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
//import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
//import org.eclipse.cdt.managedbuilder.pkgconfig.settings.PkgConfigExternalSettingProvider;
import org.eclipse.core.runtime.FileLocator;
//import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
//import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	//Plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.cdt.managedbuilder.pkgconfig"; //$NON-NLS-1$

	//Tool ID
	public static final String TOOL_ID = "org.eclipse.cdt.managedbuilder.pkgconfig.tool"; //$NON-NLS-1$

	//Shared instance
	private static Activator plugin;

	//Name for the properties file
	private final static String PROPERTIES = "plugin.properties"; //$NON-NLS-1$

	//Property Resource bundle
	private PropertyResourceBundle properties;

	private ICProjectDescriptionListener listener;

	/**
	 * The constructor
	 */
	public Activator() {
		super();
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		this.listener = new ICProjectDescriptionListener() {
//
//			// Handle the configuration updates in order to clear the pkg
//			// config settings (eg. if a new toolchain is selected, the pkg
//			// config settings will be updated after the user apply the
//			// changes even if he does not go through the pkg config
//			// property tab)
//			@Override
//			public void handleEvent(CProjectDescriptionEvent event) {
//				// Get the configuration description of the updated project
//				IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(event
//						.getProject());
//
//				ICProjectDescription projDesc = CoreModel.getDefault()
//						.getProjectDescription(event.getProject(), true);
//
//				if (projDesc != null) {
//					IConfiguration cfg = info.getDefaultConfiguration();
//					if (cfg != null) {
//						final ICConfigurationDescription confDesc = projDesc
//								.getConfigurationById(cfg.getId());
//
//						Job j = new Job("Update Pkg-config external settings provider") { //$NON-NLS-1$
//							@Override
//							protected IStatus run(IProgressMonitor monitor) {
//								// get all external setting providers
//								Set<String> externalSettingsProviders = new LinkedHashSet<String>(
//									Arrays.asList(confDesc.getExternalSettingsProviderIds()));
//								// add external setting provider
//								externalSettingsProviders.add(PkgConfigExternalSettingProvider.ID);
//								confDesc.setExternalSettingsProviderIds(externalSettingsProviders
//										.toArray(new String[externalSettingsProviders.size()]));
//						
//								// update external setting providers
//								confDesc.updateExternalSettingsProviders(new String[] { PkgConfigExternalSettingProvider.ID });
//								return Status.OK_STATUS;
//							}
//						};
//						j.setPriority(Job.INTERACTIVE);
//						j.schedule();
//					}
//				}
//			}
//		};
//		// Listen for the configuration updates (toolchain updates are important
//		// for us as the pkg-config plugin depends on the select toolchain)
//		CoreModel.getDefault().addCProjectDescriptionListener(this.listener,
//				CProjectDescriptionEvent.DATA_APPLIED);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		CoreModel.getDefault().removeCProjectDescriptionListener(this.listener);
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Get plugin.properties
	 * 
	 * @return PropertyResourceBundle
	 */
	public PropertyResourceBundle getProperties(){
		if (this.properties == null){
			try {
				this.properties = new PropertyResourceBundle(
						FileLocator.openStream(this.getBundle(),
								new Path(PROPERTIES),false));
			} catch (IOException e) {
				Activator.getDefault().log(e, "Creating a PropertyResourceBundle failed."); //$NON-NLS-1$
			}
		}
		return this.properties;
        }

	/**
	 * Log error.
	 * 
	 * @param e
	 */
	public void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Error", e)); //$NON-NLS-1$
	}

	/**
	 * Log status.
	 * 
	 * @param status
	 */
	public void log(IStatus status) {
		getLog().log(status);
	}

	/**
	 * Log Status, plug-in id, message and exception.
	 * 
	 * @param status
	 * @param e
	 * @param message
	 */
	public void log(int status, Exception e, String message) {
		getLog().log(new Status(status, PLUGIN_ID, message, e));
	}

	/**
	 * Log plug-in id, message and exception.
	 * 
	 * @param e
	 * @param message
	 */
	public void log(Exception e, String message) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}

}
