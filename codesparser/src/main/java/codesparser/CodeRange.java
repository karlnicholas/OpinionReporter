package codesparser;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: brad
 * Date: 5/20/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CodeRange {
	private static final Logger logger = Logger.getLogger(CodeRange.class.getName());

//	private String sectionString;
    private SectionNumber sNumber;
    private SectionNumber eNumber;

/*
    public SectionRange(String sString, String eString) throws CodeException {
        sNumber = new SectionNumber(sString);
        if ( eString != null )
            eNumber = new SectionNumber(eString);
        else
            eNumber = null;
    }
*/
    public CodeRange() {
    	initEmpty();
    }
    
    public CodeRange(SectionNumber sNumber, SectionNumber eNumber) {
    	this.sNumber = sNumber;
    	this.eNumber = eNumber;
    }
    
    private void initEmpty() {
    	sNumber = null;
    	eNumber = null;
    }
    
    // can only assume that it is merged in order
    public void mergeRange(CodeRange codeRange) {
    	if ( sNumber == null && codeRange.sNumber != null && codeRange.sNumber.getSectionNumber() != null ) {
    		sNumber = codeRange.sNumber;
    	}
    	if ( codeRange.eNumber != null && codeRange.eNumber.getSectionNumber() != null ) {
        	eNumber = codeRange.eNumber;
    	} else if ( codeRange.sNumber != null && codeRange.sNumber.getSectionNumber() != null ) {
        	eNumber = codeRange.sNumber;
    	}
//    	sectionString = new String(sNumber + "-" + eNumber );
    }

	public SectionNumber getsNumber() {
		return sNumber;
	}

	public SectionNumber geteNumber() {
		return eNumber;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(sNumber.toString());
		if ( eNumber != null ) sb.append(" - " + eNumber);
		String ret = sb.toString();
		logger.finer(ret);
		return ret;
    }
    
}