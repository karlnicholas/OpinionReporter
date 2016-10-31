package gsearch.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.tartarus.snowball.ext.PorterStemmer;

public class Highlighter {
	private static final PorterStemmer stemmer = new PorterStemmer();

	/*
	 * Definitely need to fix this up ... 
	 */
	public String highlightText( String text, String term, String preTag, String postTag ) {
		if ( term == null || term.isEmpty() ) return text;
		String lText = text.toLowerCase();
		String lTerm = StringEscapeUtils.unescapeHtml4( term.toLowerCase() );
		if ( lTerm.charAt(0) == '\"' && lTerm.charAt(lTerm.length()-1) == '\"') lTerm = lTerm.substring(1, lTerm.length()-1); 
	    stemmer.setCurrent(lTerm);
	    stemmer.stem();
	    lTerm = stemmer.getCurrent();

	    StringBuffer buffer = new StringBuffer();
		int cpos = 0;
		int idx = lText.indexOf(lTerm, cpos);
		
		while ( idx != -1 ) {
			buffer.append(text.substring(cpos, idx));
			buffer.append(preTag);
			buffer.append(text.substring(idx, idx + lTerm.length()));
			buffer.append(postTag);
			cpos = idx + lTerm.length();
			idx = lText.indexOf(lTerm, cpos);
		}
		buffer.append(text.substring(cpos));
		return buffer.toString();
	}
	
}
