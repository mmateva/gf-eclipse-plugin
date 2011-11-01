package org.grammaticalframework.eclipse;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * Identifiers for plugin-wide preferences
 * Use like so:
 *		IPreferencesService prefs = Platform.getPreferencesService();
 *		String _ = prefs.getString(GFPreferences.QUALIFIER, GFPreferences.GF_BIN_PATH, "default", null);
 * 
 * @author John J. Camilleri
 *
 */
public class GFPreferences {

	public static final String QUALIFIER = "org.grammaticalframework.eclipse.GF";
	
	public static final String GF_BIN_PATH = "runtimePath"; 
	public static final String GF_LIB_PATH = "libraryPath"; 
	public static final String SHOW_DEBUG = "showDebugMessages";
	
	public static String getString(String prefKey) {
		return getString(prefKey, (String)null);
	}
	public static String getString(String prefKey, String defaultValue) {
		IPreferencesService prefs = Platform.getPreferencesService();
//		IScopeContext[] contexts = new IScopeContext[] { ConfigurationScope.INSTANCE }; 
//		return prefs.getString(QUALIFIER, prefKey, defaultValue, contexts);
		return prefs.getString(QUALIFIER, prefKey, defaultValue, null);
	}
	
	public static Boolean getBoolean(String prefKey, Boolean defaultValue) {
		IPreferencesService prefs = Platform.getPreferencesService();
		IScopeContext[] contexts = new IScopeContext[] { ConfigurationScope.INSTANCE }; 
		return prefs.getBoolean(QUALIFIER, prefKey, defaultValue, contexts);
	}
	
}
