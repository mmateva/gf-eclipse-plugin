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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.grammaticalframework.eclipse.GFRuntimeModule;
import org.grammaticalframework.eclipse.GFStandaloneSetup;
import org.grammaticalframework.eclipse.builder.GFBuilder;
import org.grammaticalframework.eclipse.gF.GFFactory;
import org.grammaticalframework.eclipse.gF.Ident;
import org.grammaticalframework.eclipse.gF.impl.IdentImpl;

import com.google.inject.Inject;
import com.google.inject.Injector;

// TODO: Auto-generated Javadoc
/**
 * For resolving GF library modules and their exported definitions.
 *
 * @author John J. Camilleri
 */
public class GFLibraryAgent {
	
	/**
	 * Put together path to local gf source file.
	 *
	 * @param context the context
	 * @param moduleName the module name
	 * @return the local source path
	 */
	private String getLocalSourcePath(Resource context, String moduleName) {
		URI uri = context.getURI();
		return uri.trimSegments(1) + java.io.File.separator + moduleName + ".gf";
	}
	
	/**
	 * Put together path to compiled tags file in the build folder.
	 *
	 * @param context the context
	 * @param moduleName the module name
	 * @return the header path
	 */
	public URI getTagsFile(Resource context, String moduleName) {
		String sb = GFBuilder.getTagsFile(context.getURI().lastSegment());
		URI uri = URI.createURI(sb);
		return uri.resolve(context.getURI());
	}
	
	/**
	 * Put together path to compiled gfh header in the build folder.
	 *
	 * @param context the context
	 * @param moduleName the module name
	 * @return the header path
	 */
	private String getHeaderPath(Resource context, String moduleName) {
		StringBuilder sb = new StringBuilder();
		
		// Are we "outside" the gfbuild folder?
		if (!context.getURI().trimSegments(1).lastSegment().equals(GFBuilder.BUILD_FOLDER)) {
			sb.append(GFBuilder.BUILD_FOLDER + java.io.File.separator);
		}
		sb.append(moduleName + ".gfh");
		return sb.toString();
	}
	
	/**
	 * Check if a module exists.
	 *
	 * @param context the context
	 * @param moduleName the module name
	 * @return the module uri
	 */
	public URI getModuleURI(Resource context, String moduleName) {
		URI uri;
		
		// First try a local file
		uri = URI.createURI( getLocalSourcePath(context, moduleName) );
		if (!EcoreUtil2.isValidUri(context, uri)) {
			// Else try a compiled file
			uri = URI.createURI( getHeaderPath(context, moduleName) );
		}
		
		// Return as absolute URL
		if (EcoreUtil2.isValidUri(context, uri)) {
			return uri.resolve(context.getURI());
		} else {
			return null;
		}
	}
	
	
	/**
	 * Gets the module resource.
	 *
	 * @param context the context
	 * @param moduleName the module name
	 * @return the module resource
	 */
	public Resource getModuleResource(Resource context, String moduleName) {
		URI uri = getModuleURI(context, moduleName);
		try {
			return EcoreUtil2.getResource(context, uri.toString() );
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	/**
	 * Check if a module exists.
	 *
	 * @param context the context
	 * @param moduleName the module name
	 * @return true, if successful
	 */
	public boolean moduleExists(Resource context, String moduleName) {
		return (getModuleURI(context, moduleName) != null);
	}
	
	
//	@Inject
//	private ResourceDescriptionsProvider provider = new ResourceDescriptionsProvider();
	
	/**
	 * Given a path to source file (from a tags file), load it and return
	 * return the EObject matching by ident
	 *
	 */
	public EObject findEObjectInFile(Resource context, String filePath, String ident) throws RuntimeException {
/*		
		// Creating our own EObjects like this satisfies the validator, but will not allow you to "open declaration"
		IdentImpl eObject = (IdentImpl) GFFactory.eINSTANCE.createIdent();
		eObject.setS(ident);
//		URI uri = URI.createFileURI(filePath).appendFragment("///@body/@judgements.0/@definitions.0/@name");
		URI uri = URI.createURI("platform:/resource/Hello/ResEng.gf").appendFragment("///@body/@judgements.0/@definitions.0/@name");
		eObject.eSetProxyURI(uri);
		return eObject;
*/
		// Loads external file as EMF model, but tries to do linking which we don't want
//		Injector injector = new GFStandaloneSetup().createInjectorAndDoEMFRegistration();
//		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
//		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
//		Resource resource = resourceSet.getResource(uri, true);
		
		/*
		 * TODO
		 * This "works" except for for when there's a problem in parsing the actual source file, as in the mkN / mkA case.
		 * Additionally open declaration still doesn't work anyway.
		 * Essentially nothing I've tried in this method was able to make the OPEN DECLARATION function work, even if can satisfy the dangling
		 * dependancies. I think for now we'll jsut have to leave it broken, if we want to keep moving :(
		 */

		if (!openedResources.containsKey(filePath)) {
			if (resourceSet == null) {
				Injector injector = new GFStandaloneSetup().createInjectorAndDoEMFRegistration();
				resourceSet = injector.getInstance(XtextResourceSet.class);
			}
			resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.FALSE);
			Resource resource = resourceSet.getResource(URI.createFileURI(filePath), true);
			openedResources.put(filePath, resource);
			
//			Injector injector = new GFStandaloneSetup().createInjectorAndDoEMFRegistration();
//			IParser parser = injector.getInstance(IParser.class);
//			IParseResult parse = null;
//			try {
//				parse = parser.parse(new FileReader(filePath));
//				openedResources.put(filePath, parse);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
//		TreeIterator<EObject> iter = openedResources.get(filePath).getRootASTElement().eAllContents();
		TreeIterator<EObject> iter = openedResources.get(filePath).getAllContents();
		while (iter.hasNext()) {
			EObject e = iter.next();
			if (e instanceof Ident)
				if (((Ident)e).getS().equals(ident)) {
//					((MinimalEObjectImpl)e).eSetProxyURI(uri.appendFragment("///@body/@judgements.0/@definitions.0/@name"));
//					IdentImpl id = (IdentImpl) e;
//					id.eSetProxyURI(uri);
//					return id;
					return e;
				}
		}

//		((IdentImpl)eObject).getS();
		return null;
	}
	
	private XtextResourceSet resourceSet;
	
	private Map<String, Resource> openedResources = new HashMap<String, Resource>();
//	private static Map<String, IParseResult> openedResources = new HashMap<String, IParseResult>();

}
