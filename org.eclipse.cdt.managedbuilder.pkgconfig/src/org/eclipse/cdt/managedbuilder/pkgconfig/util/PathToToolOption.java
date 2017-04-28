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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.pkgconfig.Activator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;

/**
 * Add other flags to compiler's miscellaneous option.

 */
public class PathToToolOption {

	//tool input extensions
	private static final String[] inputTypes = {"C", "c++", "cc", "cpp", "cxx"};  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
	
	private final static String OtherFlagsOptionName = "Other flags"; //$NON-NLS-1$
	
	private final static String DefinedSymbolsOptionName = "Defined symbols (-D)"; //$NON-NLS-1$

	/**
	 * Adds new other flag to Compiler's Other flags option.
	 * 
	 * @param otherFlag Include path to be added to Compiler's Include Option 
	 */
	public static void addOtherFlag(String otherFlag, IProject proj) {
		if (proj != null && (otherFlag.length()>0)) {
			IConfiguration cf = getActiveBuildConf(proj);
			if (cf != null) {
				//Add path to compiler's Other flags Option
				addOtherFlagToToolOption(cf, otherFlag);
			}
		}
	}
	
	/**
	 * Adds new defined symbol to Compiler's defined symbols option.
	 * 
	 * @param definedSymbol Defined Symbols to added 
	 */
	public static void addDefinedSymbol(String definedSymbol, IProject proj) {
		if (proj != null && (definedSymbol.length()>0)) {
			IConfiguration cf = getActiveBuildConf(proj);
			if (cf != null) {
				//Add defined symbol to compiler's settings
				addDefinedSymbolToToolOption(cf, definedSymbol);
			}
		}
	}

	/**
	 * Adds an other flag to compiler's Other flags Option.
	 * 
	 * @param cf IConfiguration Build configuration
	 * @param otherFlag Other flag
	 * @return boolean Returns true if Other flags Option was added successfully to the compiler.
	 */
	private static boolean addOtherFlagToToolOption(IConfiguration cf, String otherFlag) {
		ITool compiler = getCompiler(cf);
		//If the compiler is found from the given build configuration
		if (compiler != null) {
			//get compiler's Other flags option
			IOption otherFlagOption = getCompilerOtherFlagsOption(cf);
			if (otherFlagOption != null) {
				//add other flag to compiler's Other flags Option type
				return addOtherFlagToToolOption(cf, compiler, otherFlagOption, otherFlag);
			}
			return false;
		} 
		//adding the other flag failed
		return false;
	}
	
	
	/**
	 * Adds a Defined symbol to compiler's Settings.
	 * 
	 * @param cf IConfiguration Build configuration
	 * @param definedSymbol Defined symbol to be added 
	 * @return boolean Returns true if Defined symbols Option was added successfully to the compiler.
	 */
	private static boolean addDefinedSymbolToToolOption(IConfiguration cf, String definedSymbol) {
		ITool compiler = getCompiler(cf);
		//If the compiler is found from the given build configuration
		if (compiler != null) {
			//get compiler's Other flags option
			IOption definedSymbolsOption = getCompilerDefinedSymbolsOption(cf);
			if (definedSymbolsOption != null) {
				//add other flag to compiler's Other flags Option type
				return addDefinedSymbolToToolOption(cf, compiler, definedSymbolsOption, definedSymbol);
			}
			return false;
		} 
		//adding the other flag failed
		return false;
	}

	/**
	 * Adds new other flag to the Compiler's Other flags Option.
	 * 
	 * @param cf IConfiguration Build configuration
	 * @param cfTool ITool Tool
	 * @param option Tool Option type
	 * @param newOtherFlag
	 * @since 8.0
	 */
	private static boolean addOtherFlagToToolOption(IConfiguration cf, ITool cfTool, IOption option, String newOtherFlag) {
		String flags = null;
		try {
			flags = option.getStringValue();
		} catch (BuildException e1) {
			Activator
			.getDefault()
			.log(e1,
					String.format("Unable to retrieve option value for %1$s", option.getName())); //$NON-NLS-1$
		}
		if (flags == null) {
			flags = ""; //$NON-NLS-1$
		}

		if (!flags.contains(newOtherFlag)) {
			//append the new flag to existing flags
			flags = flags+" "+newOtherFlag; //$NON-NLS-1$

			//add a new other flag to compiler's other flags option.
			IOption opt = ManagedBuildManager.setOption(cf, cfTool, option, flags);
			try {
				return opt.getStringValue().contains(newOtherFlag);
			}catch(BuildException e) {
				Activator
				.getDefault()
				.log(e,
						String.format("Unable to update %1$s, flags=%2$s, value=%3$s not added", option.getName(),flags, newOtherFlag)); //$NON-NLS-1$
				return false;
			}
			
		} 
		return flags.contains(newOtherFlag);
	}
	
	
	/**
	 * Adds new defs flag to the Compiler's Other flags Option.
	 * 
	 * @param cf IConfiguration Build configuration
	 * @param cfTool ITool Tool
	 * @param option Tool Option type
	 * @param newDefinedSymbol
	 * @since 8.0
	 */
	private static boolean addDefinedSymbolToToolOption(IConfiguration cf, ITool cfTool, IOption option, String newDefinedSymbol) {
		String[] flags = null;
		try {
			flags = option.getDefinedSymbols();
		} catch (BuildException e1) {
			Activator
			.getDefault()
			.log(e1,
					String.format("Unable to retrieve option value for %1$s", option.getName())); //$NON-NLS-1$
		}
		if (flags == null) {
			flags = new String[0]; 
		}

		List<String> oldFlags = Arrays.asList(flags);
		if (!oldFlags.contains(newDefinedSymbol)) {//append the new flag to existing flags
			ArrayList<String> newFlags = new ArrayList<String>(flags.length + 1);
			Collections.addAll(newFlags, flags);
			if(newFlags.add(newDefinedSymbol)) {
				//add a new other flag to compiler's other flags option.
				IOption opt = ManagedBuildManager.setOption(cf, cfTool, option, newFlags.toArray(new String[newFlags.size()]));
				try {
					return Arrays.asList(opt.getDefinedSymbols()).contains(newDefinedSymbol);
				}catch(BuildException e) {
					Activator
					.getDefault()
					.log(e,
							String.format("Unable to update %1$s, flags=%2$s, value=%3$s not added", option.getName(),flags, newDefinedSymbol)); //$NON-NLS-1$
					return false;
				}
			}
		} 
		return oldFlags.contains(newDefinedSymbol);
	}

	/**
	 * Return compiler according to the input type.
	 * @param cf IConfiguration Build configuration
	 * @return ITool Compiler
	 */
	private static ITool getCompiler(IConfiguration cf) {
		//get compiler according to the input type
		for(int i=0; i<inputTypes.length; i++) {
			ITool tool = getIToolByInputType(cf, inputTypes[i]);
			if (tool != null) {
				return tool;
			}
		}
		return null;
	}

	/**
	 * Returns ITool associated based on the input extension.
	 * 
	 * @param cf IConfiguration Build configuration
	 * @param ext input extension associated with ITool
	 * @return ITool Tool that matches input extension
	 */
	private static ITool getIToolByInputType(IConfiguration cf, String ext) {
		//get ITool associated with the input extension
		return cf.getToolFromInputExtension(ext);
	}

	/**
	 * Returns compiler's Other flags Option type.
	 * 
	 * @param cf IConfiguration Project build configuration
	 * @return IOption Tool option type
	 */
	private static IOption getCompilerOtherFlagsOption(IConfiguration cf) {
		//get ITool associated with the input extension
		ITool cfTool = getCompiler(cf);
		//get option id for other flags
		String otherFlagsOptionId = getOptionIdByName(cfTool, OtherFlagsOptionName);
		return getToolOptionType(cfTool, otherFlagsOptionId);
	}
	
	
	/**
	 * Returns compiler's Defined symbols Option type.
	 * 
	 * @param cf IConfiguration Project build configuration
	 * @return IOption Tool option type
	 */
	private static IOption getCompilerDefinedSymbolsOption(IConfiguration cf) {
		//get ITool associated with the input extension
		ITool cfTool = getCompiler(cf);
		//get option id for other flags
		String definedSymbolsOptionId = getOptionIdByName(cfTool, DefinedSymbolsOptionName);
		return getToolOptionType(cfTool,definedSymbolsOptionId);
	}

	/**
	 * Returns Tool's option id.
	 * 
	 * @param cfTool ITool Tool
	 * @param name Option's name
	 * @return optionId Tool's option id.
	 */
	private static String getOptionIdByName(ITool cfTool, String name) {
		String optionId = null;
		//get all Tool options.
		IOption[] options = cfTool.getOptions();
		for (IOption opt : options) {
			if(opt != null) {
				//try to match option name
				if(opt.getName().equalsIgnoreCase(name)) {
					//get option id
					optionId = opt.getId();
					break;
				}
			}
		}	
		return optionId;
	}
	
	/**
	 * Returns Tool's Option type by Id.
	 * 
	 * @param cfTool ITool Tool
	 * @param optionId String Tool option type id
	 * @return IOption Tool option type
	 */
	private static IOption getToolOptionType(ITool cfTool, String optionId) {
		//get option type with specific id for the ITool
		return cfTool.getOptionById(optionId);
	}

	/**
	 * Adds one or more paths to the list of paths.
	 * 
	 * @param existingPaths Existing list of paths to add to
	 * @param newPath New path to add. May include multiple directories with a path delimiter.
	 * @return String[] List that includes existing paths as well as new paths.
	 */
	public static String[] addNewPathToExistingPathList(String[] existingPaths, String newPath) {
		List<String> newPathList = new ArrayList<String>();
		String path;
		//adds existing paths to new paths list
		for (int i = 0; i < existingPaths.length; i++) {
			path = existingPaths[i];
			newPathList.add(path);
		}
		//separates new path if it has multiple paths separated by a path separator
		String[] newPathArray = newPath.split(Separators.getPathSeparator());
		for (int i = 0; i < newPathArray.length; i++) {
			path = newPathArray[i];
			newPathList.add(path);
		}
		//creates a new list that includes all existing paths as well as new paths
		String[] newArray = newPathList.toArray(new String[0]);
		return newArray;
	}

	/**
	 * Get the active build configuration.
	 * 
	 * @param proj IProject
	 * @return IConfiguration
	 */
	public static IConfiguration getActiveBuildConf(IProject proj) {
		IConfiguration conf = null;
		IManagedBuildInfo info = null;
		//try to get Managed build info
		try {
			info = ManagedBuildManager.getBuildInfo(proj); //null if doesn't exists
		} catch (Exception e) { //if not a managed build project
			Activator.getDefault().log(IStatus.INFO, e, "Not a managed build project."); //$NON-NLS-1$
			return conf;
		}
		//info can be null for projects without build info. For example, when creating a project
		//from Import -> C/C++ Executable
		if(info == null) {
			return conf;
		}
		conf = info.getDefaultConfiguration();
		return conf;
	}
	
}
