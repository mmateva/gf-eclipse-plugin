/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2011
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement n° FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.validation.Check;
import org.grammaticalframework.eclipse.gF.*;
import org.grammaticalframework.eclipse.scoping.GFGlobalScopeProvider;
import org.grammaticalframework.eclipse.scoping.GFLibraryAgent;

import com.google.inject.Inject;
 

// TODO: Auto-generated Javadoc
/**
 * The Class GFJavaValidator.
 */
public class GFJavaValidator extends AbstractGFJavaValidator {
	
	/**
	 * The scope provider.
	 */
	@Inject
	private IScopeProvider scopeProvider;
	
	/**
	 * Gets the scope provider.
	 *
	 * @return the scope provider
	 */
	protected IScopeProvider getScopeProvider() {
		return scopeProvider;
	}

	/**
	 * The provider.
	 */
	@Inject
	private GFGlobalScopeProvider provider;
	
	/**
	 * The converter.
	 */
	@Inject
	private IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();
	
	/**
	 * Gets the converter.
	 *
	 * @return the converter
	 */
	protected IQualifiedNameConverter getConverter() {
		return converter;
	}

	/**
	 * The lib agent.
	 */
	@Inject
	private GFLibraryAgent libAgent;

	// ==============================================

	/**
	 * It is a compiler error for a module's name not to match its filename.
	 *
	 * @param modtype the modtype
	 */
	@Check
	public void checkModuleNameMatchesFileName(ModType modtype) {
		String idealName = modtype.eResource().getURI().trimFileExtension().lastSegment();
		if (!modtype.getName().getS().equals(idealName)  ) {
			String msg = String.format("Module name \"%s\" differs from file name \"%s\"", modtype.getName().getS(), idealName);
			error(msg, GFPackage.Literals.MOD_TYPE__NAME);
		}
	}

	/**
	 * Warn when referencing a module which does not exist.
	 *
	 * @param modtype the modtype
	 */
	@Check
	public void checkAbstractModuleExists(ModType modtype) {
		// Concrete, Instance
		if (modtype.getAbstractName().getS() != null) {
			if (!libAgent.moduleExists(modtype.eResource(), modtype.getAbstractName().getS())) {
				String msg = String.format("Module \"%s\" not found", modtype.getAbstractName().getS());
				warning(msg, GFPackage.Literals.MOD_TYPE__ABSTRACT_NAME);
			}
		}
	}
	
	/**
	 * Check referenced module exists.
	 *
	 * @param open the open
	 */
// TODO These checks need to respect PATH directives in order to be useful!
//	@Check
//	public void checkReferencedModuleExists(Open open) {
//		// Opens, Instantiations
//		if (!libAgent.moduleExists(open.eResource(), open.getName().getS())) {
//			String msg = String.format("Module \"%s\" not found", open.getName().getS());
//			warning(msg, GFPackage.Literals.OPEN__NAME);
//		}
//	}
//	@Check
//	public void checkReferencedModuleExists(Included inc) {
//		// Extends, Functor instantiation
//		if (!libAgent.moduleExists(inc.eResource(), inc.getName().getS())) {
//			String msg = String.format("Module \"%s\" not found", inc.getName().getS());
//			warning(msg, GFPackage.Literals.INCLUDED__NAME);
//		}
//	}

	/**
	 * Some special flag checks.
	 *
	 * @param flagdef the flagdef
	 */
	@Check
	public void checkFlags(FlagDef flagdef) {
		if (flagdef.getName().getS().equals("startcat")) {
			String startCat = flagdef.getValue().getS();
			IScope scope = getScopeProvider().getScope(flagdef, GFPackage.Literals.FLAG_DEF__NAME);
			if (scope.getSingleElement( getConverter().toQualifiedName(startCat) ) == null) {
				String msg = String.format("Start category \"%1$s\" not found", startCat);
				warning(msg, GFPackage.Literals.FLAG_DEF__VALUE);
			}
		}
	}

	/**
	 * It is a compiler error to have a category declaration in a concrete module, and so on.
	 * Ref: http://www.grammaticalframework.org/doc/gf-reference.html
	 *
	 * @param topdef the topdef
	 * @return the structural feature
	 */
	private EAttribute getStructuralFeature(TopDef topdef) {
		if (topdef.isCat()) return GFPackage.Literals.TOP_DEF__CAT;
		if (topdef.isFun()) return GFPackage.Literals.TOP_DEF__FUN;
		if (topdef.isData()) return GFPackage.Literals.TOP_DEF__DATA;
		if (topdef.isDef()) return GFPackage.Literals.TOP_DEF__DEF;
		if (topdef.isParam()) return GFPackage.Literals.TOP_DEF__PARAM;
		if (topdef.isOper()) return GFPackage.Literals.TOP_DEF__OPER;
		if (topdef.isLincat()) return GFPackage.Literals.TOP_DEF__LINCAT;
		if (topdef.isLindef()) return GFPackage.Literals.TOP_DEF__LINDEF;
		if (topdef.isLin()) return GFPackage.Literals.TOP_DEF__LIN;
		if (topdef.isPrintname()) return GFPackage.Literals.TOP_DEF__PRINTNAME;
		if (topdef.isFlags()) return GFPackage.Literals.TOP_DEF__FLAGS;
		return null;
	}
	
	/**
	 * Check defs are in correct module types.
	 *
	 * @param topdef the topdef
	 */
	@Check
	public void checkDefsAreInCorrectModuleTypes(TopDef topdef) {
		// Ascend to module
		EObject temp = topdef;
		while (!(temp  instanceof ModDef) && temp.eContainer() != null) {
			temp = temp.eContainer();
		}
		ModType modtype = ((ModDef)temp).getType();
		
		// Flags are always ok
		if (topdef.isFlags()) return;

		// Abstract can contain [cat, fun, def, data]
		if (modtype.isAbstract() && !(topdef.isCat() || topdef.isFun() || topdef.isDef() || topdef.isData())) {
			String msg = String.format("Judgement type doesn't belong in an abstract module");
			error(msg, getStructuralFeature(topdef));
		}
	
		// Concrete can contain [param, oper, lincat, lin, lindef, printname]
		if (modtype.isConcrete() && !(topdef.isParam() || topdef.isOper() || topdef.isLincat() || topdef.isLin() || topdef.isLindef() || topdef.isPrintname())) {
			String msg = String.format("Judgement type doesn't belong in a concrete module");
			error(msg, getStructuralFeature(topdef));
		}
	
		// Resource can contain [param, oper]
		if (modtype.isResource() && !(topdef.isParam() || topdef.isOper())) {
			String msg = String.format("Judgement type doesn't belong in a resource module");
			error(msg, getStructuralFeature(topdef));
		}
		
		// interface, instance, functor, functor instantiation
		
	}
	
	/**
	 * Warn about lineariation rules not having any corresponding abstract declarations.
	 *
	 * @param name the name
	 */
	// TODO I disabled this because it was acting strangely
//	@Check
//	public void checkLinearisationsHaveAbstractEquivalents(Name name) {
//		if (name.eContainer().eContainer() instanceof TopDef) {
//			TopDef topDef = (TopDef) name.eContainer().eContainer();
//			IScope scope = getScopeProvider().getScope(name, GFPackage.Literals.NAME__NAME);
//			boolean found = (scope.getSingleElement(getConverter().toQualifiedName(name.getName().getS())) != null); 
//			if (topDef.isLincat() && !found) {
//				String msg = String.format("No declaration \"cat %1$s\" found for \"lincat %1$s\"",	name.getName().getS());
//				warning(msg, GFPackage.Literals.NAME__NAME);
//			}
//			else if (topDef.isLin() && !found) {
//				String msg = String.format("No declaration \"fun %1$s\" found for \"lin %1$s\"", name.getName().getS());
//				warning(msg, GFPackage.Literals.NAME__NAME);
//			}
//		}
//	}
	
	/**
	 * Warn about lineariation rules not having any corresponding abstract declarations
	 * @param name
	 */
	@Check
	public void checkOperatorOverloadsNamesMatch(Name name) {
		
		if (name.eContainer() instanceof Def && name.eContainer().eContainer() instanceof Def) {
			Def parent = (Def) name.eContainer().eContainer();
			if (parent.isOverload()) {
				// Convert to list of strings to be able to make comparison
				ArrayList<String> parentNames = new ArrayList<String>();
				for (Name n : parent.getName())
					parentNames.add(n.getName().getS());
				
				if (!parentNames.contains(name.getName().getS())) {
					StringBuilder parentNamesSB = new StringBuilder();
					Iterator<Name> i = parent.getName().iterator();
					while (i.hasNext()) {
					    parentNamesSB.append(i.next().getName().getS());
					    if (i.hasNext())
					    	parentNamesSB.append(", ");
					}
					String msg = String.format("Oper name \"%1$s\" does not occur in parent overload name \"%2$s\"", name.getName().getS(), parentNamesSB.toString());
					warning(msg, GFPackage.Literals.NAME__NAME);
				}
			}
			
		}
		
	}

	/**
	 * The lexer will treat ResEng.Gender as Ident.Label, rather than as single Ident. Thus cross-referencing is only
	 * checked on the module name ResEng, but not on the member Gender.
	 * This method exists to perform this exact checking as a post-process to the generated parser.
	 *
	 * @param label the label
	 */
	// FIXME Re-visit this, make sure it handles aliases proeprly
//	@Check
//	public void checkQualifiedNames(Label label) {
//		
//		// Try get first bit of qualified name, i.e. "ResEng". Labels do no necessarily follow Idents, but ANY type of Exp6.
//		try {
//			Ident qualifier = ((Exp5)label.eContainer()).getV().getName();
//			QualifiedName fullyQualifiedName = getConverter().toQualifiedName(qualifier.getS() + "." + label.getName().getS());
//			
//			// See if the qualifier is a valid MODULE name
//			EObject temp = label;
//			while (!(temp  instanceof TopDef) && temp.eContainer() != null) {
//				temp = temp.eContainer();
//			}
//			TopDef topDef = (TopDef)temp;
//			IScope scope = getScopeProvider().getScope(topDef, GFPackage.Literals.TOP_DEF__DEFINITIONS);
//			if (scope.getSingleElement(qualifier) != null) {
//				
//				// We now we are dealing with a Qualified name, now see if the full thing is valid:
//				if (scope.getSingleElement(fullyQualifiedName) == null) {
//					String msg = String.format("No declaration \"%1$s\" found in module \"%2$s\"", label.getName().getS(), qualifier.getS());
//					error(msg, GFPackage.Literals.LABEL__NAME);
//				}
//			}
//			
//		} catch (Exception _) {
//			// just means the first part wasn't an Ident
//		}
//	}
	
	
	/**
	 * Warn when functor instantiations don't fully instantiate their functor.
	 *
	 * @param modBody the mod body
	 */
	@Check
	public void checkFunctorInstantiations(ModBody modBody) {
		if (modBody.isFunctorInstantiation()) {
			// Get list of what the functor itself OPENs
			Ident functorName = modBody.getFunctor().getName();
			ArrayList<String> functorOpens = new ArrayList<String>();
			URI uri = libAgent.getModuleURI(modBody.eResource(), functorName.getS() );
			if (!libAgent.moduleExists(modBody.eResource(), functorName.getS())) {
				// This should have already been checked
//				String msg = String.format("Cannot find module \"%1$s\"", functorName.getS());
//				error(msg, GFPackage.Literals.OPEN__NAME);
				return;
			}
			final LinkedHashSet<URI> uriAsCollection = new LinkedHashSet<URI>(1);
			uriAsCollection.add(uri);
			IResourceDescriptions descriptions = provider.getResourceDescriptions(modBody.eResource(), uriAsCollection);
			IResourceDescription desc = descriptions.getResourceDescription(uri);
			// TODO Checking via regexp is very bad! But it works >:(
			for (IEObjectDescription qn : desc.getExportedObjectsByType(GFPackage.Literals.IDENT)) {
				if(qn.getEObjectURI().toString().matches("^.*?//@body/@opens.[0-9]+/@name$")) {
					functorOpens.add(qn.getName().getLastSegment());
				}
			}
			
			ArrayList<String> thisOpens = new ArrayList<String>();
			for (Open o : modBody.getInstantiations())
				thisOpens.add(o.getAlias().getS());
			
			// Check that we are instantiating one of them
			if (!thisOpens.containsAll(functorOpens)) {
				StringBuilder msg = new StringBuilder();
				msg.append( String.format("Instantiation of functor \"%1$s\" must instantiate: ", functorName.getS()) );
				Iterator<String> i = functorOpens.iterator();
				while (i.hasNext()) {
					msg.append(i.next());
					if (i.hasNext())
						msg.append(", ");
				}
				error(msg.toString(), GFPackage.Literals.MOD_BODY__FUNCTOR_INSTANTIATION);
			}
		}
	}

}
