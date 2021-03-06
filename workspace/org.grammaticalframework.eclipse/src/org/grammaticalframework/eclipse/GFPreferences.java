/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2012
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement no. FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * Identifiers for plugin-wide preferences
 * Use like so:
 * <pre>
 * {@code
 * IPreferencesService prefs = Platform.getPreferencesService();
 * String _ = prefs.getString(GFPreferences.QUALIFIER, GFPreferences.GF_BIN_PATH, "default", null);
 * }
 * </pre>
 * 
 * @author John J. Camilleri
 *
 */
public class GFPreferences {

	/**
	 * Qualifier for retrieving the plugin preferences
	 */
//	public static final String QUALIFIER = "org.grammaticalframework.eclipse.GF";
	public static final String QUALIFIER = "org.grammaticalframework.eclipse.ui";
	
	/**
	 * Preference to hold path of GF runtime
	 */
	public static final String GF_BIN_PATH = "runtimePath";
	public static String getRuntimePath() {
		return getString(GF_BIN_PATH);
	}
	
	/**
	 * Preference to hold an additional --path directive
	 */
	public static final String GF_LIB_PATH = "libraryPath"; 
	public static String getLibraryPath() {
		return getString(GF_LIB_PATH);
	}
	
	/**
	 * Preference to hold source path of GF libraries
	 */
	public static final String GF_LIB_SRC_PATH = "librarySourcePath"; 
	public static String getLibrarySourcePath() {
		String s = getString(GF_LIB_SRC_PATH);
		return (s != null && !s.isEmpty() && !s.matches(".*[/\\\\]$")) ? s + File.separator : s; // ensure always ends with slash
	}
	
	/**
	 * Preference to control auto-building dependencies
	 */
	public static final String BUILD_DEPENDENTS = "buildDependents"; 
	public static Boolean getBuildDependents() {
		return getBoolean(BUILD_DEPENDENTS);
	}
	
	/**
	 * Preference to specify debug level
	 */
	public static final String LOG_LEVEL = "logLevel";
	public static String getLogLevel() {
		return getString(LOG_LEVEL);
	}
	
	/**
	 * Gets a string preference with no default.
	 *
	 * @param prefKey the pref key
	 * @return the string
	 */
	public static String getString(String prefKey) {
		return getString(prefKey, (String)null);
	}
	
	/**
	 * Gets a string preference, with a default value.
	 *
	 * @param prefKey the pref key
	 * @param defaultValue the default value
	 * @return the string
	 */
	public static String getString(String prefKey, String defaultValue) {
		IPreferencesService prefs = Platform.getPreferencesService();
//		IScopeContext[] contexts = new IScopeContext[] { ConfigurationScope.INSTANCE }; 
		IScopeContext[] contexts = null; 
		return prefs.getString(QUALIFIER, prefKey, defaultValue, contexts);
	}
	
	/**
	 * Gets a boolean preference.
	 *
	 * @param prefKey the pref key
	 * @param defaultValue the default value
	 * @return the boolean
	 */
//	public static Boolean getBoolean(String prefKey, Boolean defaultValue) {
	public static Boolean getBoolean(String prefKey) {
		IPreferencesService prefs = Platform.getPreferencesService();
//		IScopeContext[] contexts = new IScopeContext[] { ConfigurationScope.INSTANCE }; 
		IScopeContext[] contexts = null; 
//		return prefs.getBoolean(QUALIFIER, prefKey, defaultValue, contexts);
		return prefs.getBoolean(QUALIFIER, prefKey, false, contexts);
	}
	
}
