/*
* generated by Xtext
*/
package org.grammaticalframework.eclipse.parser.antlr;

import com.google.inject.Inject;

import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.grammaticalframework.eclipse.services.GFGrammarAccess;

public class GFParser extends org.eclipse.xtext.parser.antlr.AbstractAntlrParser {
	
	@Inject
	private GFGrammarAccess grammarAccess;
	
	@Override
	protected void setInitialHiddenTokens(XtextTokenStream tokenStream) {
		tokenStream.setInitialHiddenTokens("RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_COMPILER_PRAGMA", "RULE_GF_DOC");
	}
	
	@Override
	protected org.grammaticalframework.eclipse.parser.antlr.internal.InternalGFParser createParser(XtextTokenStream stream) {
		return new org.grammaticalframework.eclipse.parser.antlr.internal.InternalGFParser(stream, getGrammarAccess());
	}
	
	@Override 
	protected String getDefaultRuleName() {
		return "ModDef";
	}
	
	public GFGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(GFGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
	
}
