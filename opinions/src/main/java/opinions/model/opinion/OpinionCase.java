package opinions.model.opinion;

import java.util.*;

import opinions.model.courtcase.CourtCase;

import org.w3c.dom.*;

public class OpinionCase extends CourtCase {
	private static final long serialVersionUID = 1L;

	private List<OpinionCode> codes;
	
	public OpinionCase(CourtCase ccase, ArrayList<OpinionCode> codes) {
		super(ccase);
		this.codes = codes;
	}

	public void trimToLevelOfInterest( int levelOfInterest ) {
		Iterator<OpinionCode> ci = codes.iterator();
		while ( ci.hasNext() ) {
			OpinionCode code = ci.next();
			code.trimToLevelOfInterest( levelOfInterest );
		}
	}
	
    public Element createXML(Document document ) {
    	
    	Element eOpinion = document.createElement("case");

        Iterator<OpinionCode> ci = codes.iterator();
        while ( ci.hasNext() ) {
            OpinionCode opCode = ci.next();
            eOpinion.appendChild( opCode.createXML( document ) );
        }

    	writeReportXML(document, eOpinion);

        return eOpinion;
    }

    public List<OpinionCode> getCodes() {
        return codes;
    }

}
