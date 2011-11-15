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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single "tag" entry in the GF-produced tags file, e.g.:
 * <pre>
 * {@code
 * AAdj	param-value	/.../ResEng.gf:75	Degree -> Case -> AForm
 * }
 * </pre>
 */
public class TagEntry {
	
	/**
	 * Record fields
	 */
	public String ident, type, file, args;
	private Integer lineFrom, lineTo;
	private Boolean isIndirect;
	
	/**
	 * Instantiates a new tag entry.
	 *
	 * @param elements The elements as extracted from the tags file, by String.split()
	 */
	public TagEntry(String line) {
		String[] elements = line.split("\t", -1);
		this.ident = elements[0];
		this.type = elements[1];
		this.isIndirect = this.type.equals("indir");
		if (this.isIndirect) {
			parseFileAndLineNumbers(elements[4]);
			
			// TODO Is this manual manipulation really good enough? Ideally poke into ref'd tags file and get URI from there
			this.file = this.file.replaceFirst("^(.+)(.gfbuild[/\\\\])(.+\\.gf)-tags$", "$1$3");
		} else {
			parseFileAndLineNumbers(elements[2]);
			this.args = elements[3];
		}
	}
	
	/**
	 * Parse the file path and line numbers (could be single (9) or range (9-18))
	 * @param s
	 */
	private void parseFileAndLineNumbers(String s) {
		int ix = s.lastIndexOf(':');
		this.file = (ix > 0) ? s.substring(0, ix) : s;
		String lineStr = s.substring(ix+1);
		int rangeIx = lineStr.indexOf('-');
		try {
			if (rangeIx > 0) {
				this.lineFrom = Integer.valueOf(lineStr.substring(0, rangeIx));
				this.lineTo = Integer.valueOf(lineStr.substring(rangeIx+1));
			} else {
				this.lineFrom = this.lineTo = Integer.valueOf(lineStr);
			}
		} catch (NumberFormatException e) {
			this.lineFrom = this.lineTo = null;
		}
	}
	
	/**
	 * Gets the fields as a Map<String, String> for use in an IEObjectDescription
	 *
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		HashMap<String, String> userData = new HashMap<String, String>();
		userData.put("ident", ident);
		userData.put("type", type);
		userData.put("file", file);
		userData.put("lineNo", lineFrom != null ? lineFrom.toString() : ""); // TODO Iclude range?
		userData.put("args", args);
		return userData;
	}
	
	public Integer getLineNumber() {
		return lineFrom;
	}
	public Integer getLineFrom() {
		return lineFrom;
	}
	public Integer getLineTo() {
		return lineTo;
	}
	public Boolean getIsInDirect() {
		return isIndirect;
	}

	/**
	 * Returns tag in tab-delimited format (suitable for writing back to a tags file)
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(ident + "\t" + type + "\t" + file);
		if (lineFrom != null) {
			sb.append(":" + lineFrom);
			if (lineTo != null) {
				sb.append("-" + lineTo);
			}
		}
		sb.append("\t" + args);
		return sb.toString();
	}
	
	
}
