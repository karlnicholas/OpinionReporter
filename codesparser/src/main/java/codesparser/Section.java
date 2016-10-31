package codesparser;


import java.util.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Created with IntelliJ IDEA.
 * User: brad
 * Date: 5/20/12
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class Section implements CodeReference {
//	private static final Logger logger = Logger.getLogger(Section.class.getName());
    private CodeReference parent;
    private String part;
    private String partNumber;
    private String title;
//    private SectionRange sectionRange;
    private CodeRange codeRange;
    // keep track of how deep we are ..
    // always > 0 for sections
    private int depth;
    // optionally, keep track of the sectionNumbers within this CodeReference
    private ArrayList<SectionNumber> sectionNumbers;

//    public Section (String line, boolean codeRange, Section p, int level) throws CodeException {
    public Section (CodeReference parent, String part, String partNumber, String title, CodeRange range, int depth) {
    	sectionNumbers = new ArrayList<SectionNumber>();
    	this.parent = parent;
    	if ( part != null ) {
    		this.part = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
    	} else {
    		part = null;
    	}
    	this.partNumber = partNumber;
    	this.title = title;
    	this.codeRange = range;
    	this.depth = depth;
    	assert( depth>0 );
    }
    
    public Section(Node node, CodeReference parent) {
    	sectionNumbers = new ArrayList<SectionNumber>();
    	this.parent = parent;
    	NamedNodeMap mapAttr = node.getAttributes();

    	Node nTemp = mapAttr.getNamedItem(PART);
		if ( nTemp != null ) part = nTemp.getNodeValue();
		else part = null;

		nTemp = mapAttr.getNamedItem(PARTNUMBER);
		if ( nTemp != null ) partNumber = nTemp.getNodeValue();
		else partNumber = null;

		SectionNumber sSectionNumber = null;
		String sNumber = null;
		int sPosition = -1;
		SectionNumber eSectionNumber = null;
		String eNumber = null;
		int ePosition = -1;

		nTemp = mapAttr.getNamedItem(CODERANGEBEGIN);
		if ( nTemp != null ) sNumber = nTemp.getNodeValue();
		Node nTemp2 = mapAttr.getNamedItem(POSITIONBEGIN);
		if ( nTemp2 != null ) sPosition = Integer.parseInt( nTemp2.getNodeValue() );
		if ( nTemp != null ) sSectionNumber = new SectionNumber(sPosition, sNumber); 

		nTemp = mapAttr.getNamedItem(CODERANGEEND);
		if ( nTemp != null ) eNumber = nTemp.getNodeValue();
		nTemp2 = mapAttr.getNamedItem(POSITIONEND);
		if ( nTemp2 != null ) ePosition = Integer.parseInt( nTemp2.getNodeValue() );
		if ( nTemp != null ) eSectionNumber = new SectionNumber(ePosition, eNumber); 

		if ( sNumber != null || eNumber != null ) codeRange = new CodeRange( sSectionNumber, eSectionNumber );
		else codeRange = null;

		nTemp = mapAttr.getNamedItem(DEPTH);
		if ( nTemp != null ) depth = Integer.parseInt(nTemp.getNodeValue() );
		else depth = 0;

		NodeList nodeList = node.getChildNodes();
        for ( int ci=0, cl = nodeList.getLength(); ci<cl; ++ci ) {
    		Node lnode = nodeList.item(ci);
    		String nname = lnode.getNodeName();
    		if ( nname.equals(TITLE) ) {		
				title = lnode.getTextContent();
    		}
    		if ( nname.equals(SECTIONTEXT) ) {
    	    	NamedNodeMap stAttr = lnode.getAttributes();
    	    	nTemp2 = stAttr.getNamedItem(POSITION);
    	    	int position = -1;
    			if ( nTemp2 != null ) position = Integer.parseInt(nTemp2.getNodeValue() );
    	    	nTemp = stAttr.getNamedItem(SECTIONNUMBER);
    			if ( nTemp != null ) sectionNumbers.add(new SectionNumber( position, nTemp.getNodeValue() ) );
    		}
        }
    	
    }
    
	public CodeReference findReference(SectionNumber sectionNumber) {
		for ( SectionNumber sNumber: sectionNumbers ) {
			if ( sNumber.equals(sectionNumber)) return this;
		}
		return null;
	}

	@Override
	public CodeReference findReferenceByFacets(String... facet) {
		if ( facet.length == 0 ) return null;
		if ( !getReferenceFacet().equals(facet[0])) return null;
		if ( facet.length == 1 ) return this;
		return null;
	}
/*	
	public CodeReference findReferenceByFacet(String facet) {		
		// Strip first facet ..
		int idx = facet.indexOf(CodeReference.PATHSEPARATOR);
		String partFacet = facet;
		String remainingFacet = null;
		if ( idx != -1 ) {
			partFacet = facet.substring( 0, idx );
			remainingFacet = facet.substring(idx+1);
		}
		if ( remainingFacet != null ) {
			return null;
		}
		if ( this.returnPartPath().equals(partFacet)) {
			return this;
		}
		else return null;
	}
*/
	public boolean iterateSections(IteratorHandler handler) throws Exception {
		return handler.handleSection(this);
	}

    public CodeReference getParent() {
    	return parent;
    }
    
    public void setParent( CodeReference parent ) {
    	this.parent = parent;
    }
    
    public void addReference( CodeReference reference ) {
    }
    
	public ArrayList<CodeReference> getReferences() {
		return null;
	}

	public Element iterateXML(Document document, IteratorXMLHandler handler) throws Exception {
		// The reason that this is done in this fashion is that handle section modifies the 
		// the codeRange object of this section by filling out the position values.
		// So, the XML can't be written until the position values have been filled out.
		Element eSection = document.createElement(SECTION);
		handler.handleSection(this, document, eSection);
		return createXML(document, eSection, true); 
	}
	
	public Element createXML( Document document, boolean createChildren ) {
	    return createXML( document, document.createElement(SECTION), createChildren );
	}

    public Element createXML( Document document, Element eSection, boolean createChildren ) {

        if ( part != null ) {
            Attr attrPart = document.createAttribute(PART);
            attrPart.setValue(part);
            eSection.setAttributeNode(attrPart);
        }

        if ( partNumber != null ) {
            Attr attrPartNumber = document.createAttribute(PARTNUMBER);
            attrPartNumber.setValue(partNumber);
            eSection.setAttributeNode(attrPartNumber);
        }

	    if ( codeRange != null ) {
	    	if ( codeRange.getsNumber() != null ) {
	    		SectionNumber sSectionNumber = codeRange.getsNumber();
		        Attr attrRange = document.createAttribute(CODERANGEBEGIN);
		        attrRange.setValue(sSectionNumber.getSectionNumber());
		        eSection.setAttributeNode(attrRange);
		        Attr attrPosition = document.createAttribute(POSITIONBEGIN);
		        attrPosition.setValue(Integer.toString( sSectionNumber.getPosition()) );
		        eSection.setAttributeNode(attrPosition);
	    	}

	    	if ( codeRange.geteNumber() != null ) {
	    		SectionNumber eSectionNumber = codeRange.geteNumber();
	    		Attr attrRange = document.createAttribute(CODERANGEEND);
		        attrRange.setValue(eSectionNumber.getSectionNumber());
		        eSection.setAttributeNode(attrRange);
		        Attr attrPosition = document.createAttribute(POSITIONEND);
		        attrPosition.setValue(Integer.toString( eSectionNumber.getPosition()) );
		        eSection.setAttributeNode(attrPosition);
	    	}
	    }

        Attr attrDepth = document.createAttribute(DEPTH);
        attrDepth.setValue(Integer.toString( depth) );
        eSection.setAttributeNode(attrDepth);

        Element eTitle = document.createElement(TITLE);
        eTitle.appendChild( document.createTextNode(title));
        eSection.appendChild(eTitle);

        return eSection;
    }

	@Override
	public String getPart() {
        return part;
    }
	
	@Override
    public String getPartNumber() {
        return partNumber;
    }

	@Override
    public String getTitle() {
        return title;
    }

	@Override
	public String getShortTitle() {
		return null;
	}

	@Override
    public String toString() {
        String ret = new String( );
        if ( part != null ) ret = ret + part + " ";
        if ( partNumber != null ) ret = ret + partNumber + ":";
        if ( title != null ) ret = ret + title;
        if ( codeRange != null ) ret = ret + ":" + codeRange;
        return ret;
    }

	public int getDepth() {
		return depth;
	}

	public Section getSection() {
		return this;
	}

	public void mergeCodeRange(CodeRange codeRange) {}

	public CodeRange getCodeRange() {
		return codeRange;
	}

	public ArrayList<SectionNumber> getSectionNumbers() {
		return sectionNumbers;
	}

	public void setSectionNumbers(ArrayList<SectionNumber> sectionNumbers) {
		this.sectionNumbers = sectionNumbers;
	}

	public void getParentReferences(ArrayList<CodeReference> returnPath) {
		returnPath.add(parent);
		parent.getParentReferences(returnPath);
	}

	@Override
	public String[] getFullFacet() {
		String[] pFacet = parent.getFullFacet();
		String[] fFacet = new String[pFacet.length+1];
		System.arraycopy(pFacet, 0, fFacet, 0, pFacet.length);
		fFacet[fFacet.length-1] = getReferenceFacet(); 
		return fFacet; 		
	}

	public String getReferenceFacet() {
//		if ( part == null ) throw new RuntimeException("Section::part == null"); 
//		return part+'-'+partNumber;

//		if ( part != null ) return part+'-'+partNumber;
//		else
//			return codes.mapCodeToPart( returnParentCode() ) + '-' + Integer.toString(depth) + '-' + Integer.toString(parent.getReferences().indexOf(this));
		String partFacet = getParentCode().getPart() + '-' + Integer.toString(depth) + '-' + Integer.toString(parent.getReferences().indexOf(this));
		return partFacet;
	}

	public Code getParentCode() {
		return parent.getParentCode();
	}

	public Code getCode() {
		return null;
	}
	
	public void sortSectionNumbers(ArrayList<SectionNumber> sectionNumbers) { 
	}

}
