/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2011
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement n° FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.scoping;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.*;
import org.grammaticalframework.eclipse.gF.ModDef;
import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Global scope provider is responsible for defining what is visible from
 * outside the current resource, for any given reference.
 * 
 * In our case, this means considering;
 * - Anything exended/inherited in this resource (remember inheritance is transitive)
 * - Anything opened in this resource
 * - If this is a concrete module, anything in its abstract
 * (where "this" means the resource in which the reference is defined)
 * 
 */

public class GFTagBasedGlobalScopeProvider extends AbstractGlobalScopeProvider {
	
	/**
	 * The logger
	 */
	private static final Logger log = Logger.getLogger(GFTagBasedGlobalScopeProvider.class);

	/**
	 * Instantiates a new gF global scope provider.
	 */
	public GFTagBasedGlobalScopeProvider() {
		super();
	}
	
	/**
	 * The library agent.
	 */
	@Inject
	private GFLibraryAgent libAgent;
	
	@Inject
	private ExtensibleURIConverterImpl uriConverter; 
	
	
// TODO Implement caching for Global Scope Provider
//	@Inject
//	private IResourceScopeCache cache;
//	
//	public void setCache(IResourceScopeCache cache) {
//		this.cache = cache;
//	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.xtext.scoping.impl.AbstractGlobalScopeProvider#getScope(org.eclipse.emf.ecore.resource.Resource, boolean, org.eclipse.emf.ecore.EClass, com.google.common.base.Predicate)
	 */
	@Override
	protected IScope getScope(Resource resource, boolean ignoreCase, EClass type, Predicate<IEObjectDescription> filter) {
		
		// Start a blank GFScope
		GFTagBasedScope scope = new GFTagBasedScope(IScope.NULLSCOPE, ignoreCase);
	
		// (try) get module definition
		ModDef moduleDef;
		String moduleName;
		try {
			moduleDef = (ModDef)resource.getContents().get(0);
			moduleName = moduleDef.getType().getName().getS();
		} catch (Exception _) {
			return scope;
		}
		
		ArrayList<TagEntry> tags = new ArrayList<TagEntry>(); 
		HashSet<URI> importURIs = new HashSet<URI>(); 
		
		// Find the corresponding tags file & parse it
		try {
			URI uri = libAgent.getTagsFile(resource, moduleName);
			InputStream is = uriConverter.createInputStream(uri);
			BufferedReader reader = new BufferedReader( new InputStreamReader(is) );
			String line;
			// Add everything into our arrays
			while ((line = reader.readLine()) != null) {
				TagEntry tag = new TagEntry(line);
				tags.add( tag );
				importURIs.add( URI.createFileURI(tag.file) );
			}
			reader.close();
			is.close();
		} catch (FileNotFoundException e) {
			log.warn("Couldn't find tags file for " + moduleName);
		} catch (IOException e) {
			log.error("Problem loading tags file for " + moduleName);
		}
		
		// Load all descriptions from all mentioned files/URIs
		IResourceDescriptions resourceDescriptions = getResourceDescriptions(resource, importURIs);
		
//		for (TagEntry tag : tags) {
//			scope.addTag(resource, tag);
//		}
		
		// Add everything from all the URIs mentioned in the tags file
		for (IResourceDescription resDesc : resourceDescriptions.getAllResourceDescriptions()) {
			scope = new GFTagBasedScope(scope, resDesc.getExportedObjects(), ignoreCase);
		}
		
		// Phew
		return scope;
	}
	
	
	/**
	 * The load on demand descriptions.
	 */
	@Inject
	private Provider<LoadOnDemandResourceDescriptions> loadOnDemandDescriptions;
	
	
	/**
	 * Gets the resource descriptions.
	 *
	 * @param resource the resource
	 * @param importUris the import uris
	 * @return the resource descriptions
	 */
	private IResourceDescriptions getResourceDescriptions(Resource resource, Collection<URI> importUris) {
		IResourceDescriptions result = getResourceDescriptions(resource);
		LoadOnDemandResourceDescriptions demandResourceDescriptions = loadOnDemandDescriptions.get();
		demandResourceDescriptions.initialize(result, importUris, resource);
		return demandResourceDescriptions;
	}
	
}