package gsearch;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.search.highlight.Fragmenter;

public class GSearchFragmenter implements Fragmenter {
//	public final static Logger logger = Logger.getLogger(GSearchFragmenter.class.getName());
	private OffsetAttribute offset;
	private int[] lengths;
	private int lengthLoc;
	private static final int FRAGLENGTH = 200;

	/*
	Test to see if this token from the stream should be held in a new TextFragment. 
	Every time this is called, the TokenStream passed to 
	    start(String, TokenStream) will have been incremented. 
	 */
	public boolean isNewFragment() {
		boolean ret = false;

		if ( lengthLoc < lengths.length && offset.startOffset() > lengths[lengthLoc] ) {
			lengthLoc++;
			ret = true;
		}
		return ret;
	}

	/*
    Initializes the Fragmenter. You can grab references to the Attributes 
    you are interested in from tokenStream and then access the values in isNewFragment().

    Parameters:
        originalText - the original source text
        tokenStream - the TokenStream to be fragmented
	 */
	public void start(String originalText, TokenStream tokenStream) {
		offset = tokenStream.getAttribute(OffsetAttribute.class);
		int total = originalText.length() / FRAGLENGTH +1;
		lengths = new int[total];
		lengthLoc = 0;
		int loc=0;
		for ( int i=FRAGLENGTH, l = originalText.length(); i < l; i+=FRAGLENGTH ) {
			lengths[loc] = i;
			loc++;
		}
	}

}
