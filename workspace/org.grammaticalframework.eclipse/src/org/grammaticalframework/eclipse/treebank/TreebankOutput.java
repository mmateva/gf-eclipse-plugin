/**
 * GF Eclipse Plugin
 * http://www.grammaticalframework.org/eclipse/
 * John J. Camilleri, 2012
 * 
 * The research leading to these results has received funding from the
 * European Union's Seventh Framework Programme (FP7/2007-2013) under
 * grant agreement no. FP7-ICT-247914.
 */
package org.grammaticalframework.eclipse.treebank;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

public class TreebankOutput extends AbstractCollectionFile<List<String>> {
	
	public TreebankOutput(IFile outFile) {
		super(outFile);
	}
	
	/**
	 * Parse the output file into a 2D list of strings
	 */
	protected void parseFile() {
		BufferedReader outReader = null;
		try {
			outReader = new BufferedReader(new InputStreamReader(new DataInputStream(getFile().getContents(true))));
			
			String outLine;
			ArrayList<String> group = new ArrayList<String>();
			while ((outLine = outReader.readLine()) != null) {
				outLine = outLine.trim();
				
				// Empty line signifies a new "group"
				if (outLine.isEmpty())  {
					if (!group.isEmpty())
						list.add(group);
					group = new ArrayList<String>();
					continue;
				}
				
				// Create object and add to list
				group.add(outLine);
			}
			if (!group.isEmpty())
				list.add(group);
		} catch (Exception e) {
		} finally {
			try {
				outReader.close();
			} catch (Exception _) {	}
		}
	}
	
	public List<String> getGroup(int i) {
		return list.get(i);
	}
	

}
