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
 * User: karl
 * Date: 5/20/12
 * Time: 9:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class Code implements CodeReference, Comparable<Code> {
//	private static final Logger logger = Logger.getLogger(Code.class.getName());

    private String title;
    private String shortTitle;
	private String facetHead;
    // Always 0 for code, increments for each level of children
    private int depth;
    private CodeRange codeRange;
    private ArrayList<CodeReference> references;
    // wtf is this?
//    private ArrayList<CodeReference> comparableList;

    public Code(String title, String shortTitle, String facetHead) {
    	references = new ArrayList<CodeReference>();
    	codeRange = new CodeRange();
        this.title = title;
        this.shortTitle = shortTitle;
        this.facetHead = facetHead;
    	this.depth = 0;
    }

    public Code(Node node) {
    	references = new ArrayList<CodeReference>();
        
    	NamedNodeMap mapAttr = node.getAttributes();

    	Node nTemp = mapAttr.getNamedItem(PART);
		if ( nTemp != null ) facetHead = nTemp.getNodeValue();
		else facetHead = null;

    	nTemp = mapAttr.getNamedItem(DEPTH);
		if ( nTemp != null ) depth = Integer.parseInt(nTemp.getNodeValue() );
		else depth = 0;

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

		NodeList nodeList = node.getChildNodes();
	    for ( int ci=0, cl = nodeList.getLength(); ci<cl; ++ci ) {
			Node lnode = nodeList.item(ci);
			String nname = lnode.getNodeName();
			if ( nname.equals(TITLE) && title == null ) {
				title = lnode.getTextContent();
			} else if ( nname.equals(SHORTTITLE) && shortTitle == null ) {
				shortTitle = lnode.getTextContent();
			} else if ( nname.equals(SECTION) ) {
	        	references.add(new Section(lnode, this));
	    	} else if ( nname.equals(SUBCODE) ) {
	        	references.add(new Subcode(lnode, this));
	    	}
		}
        
    }

	public CodeReference findReference(SectionNumber sectionNumber) {
		Iterator<CodeReference> rit = references.iterator();
		while ( rit.hasNext() ) {
			CodeReference reference = rit.next().findReference(sectionNumber);
			if ( reference != null ) return reference;
		}
		return null;
	}

	@Override
	public CodeReference findReferenceByFacets(String... facet) {
		if ( facet.length == 0 ) return null;
		if ( !getReferenceFacet().equals(facet[0])) return null;
		if ( facet.length == 1 ) return this;
		for ( int i=0, j=references.size(); i<j; ++i ) {
			CodeReference reference = references.get(i).findReferenceByFacets(Arrays.copyOfRange(facet, 1, facet.length-1 ));
			if ( reference != null ) return reference;
		}
		return null;
/*		
		// Strip first facet ..
		int idx = facet.indexOf(CodeReference.PATHSEPARATOR);
		String remainingFacet = null;
		if ( idx != -1 ) {
			remainingFacet = facet.substring(idx+1);
		}

		if ( remainingFacet == null ) {
			// special case
			return this;
		}
		Iterator<CodeReference> rit = references.iterator();
		while ( rit.hasNext() ) {
			CodeReference reference = rit.next().findReferenceByFacet(remainingFacet);
			if ( reference != null ) return reference;
		}
		return null;
*/		
	}

	public boolean iterateSections(IteratorHandler handler) throws Exception {
		Iterator<CodeReference> rit = references.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false;
		}
		return true;
	}

    public CodeReference getParent() {
    	return null;
    }

    public void setParent( CodeReference parent ) {
    }

	public Element iterateXML(Document document, IteratorXMLHandler handler) throws Exception {
		Element eCode = document.createElement(CODE);
    	Iterator<CodeReference> rit = references.iterator();
    	while ( rit.hasNext() ) {
    		CodeReference reference = rit.next();
    		eCode.appendChild( reference.iterateXML(document, handler ) );
    	}
    	return createXML( document, eCode, false );
	}

	public Element createXML( Document document, boolean createChildren ) {
	    return createXML( document, document.createElement(CODE), createChildren );
	}

    public Element createXML( Document document, Element eCode, boolean createChildren ) {

        if ( facetHead != null ) {
            Attr attrPart = document.createAttribute(PART);
            attrPart.setValue(facetHead);
            eCode.setAttributeNode(attrPart);
        }

        Attr attrDepth = document.createAttribute(DEPTH);
        attrDepth.setValue(Integer.toString( depth ) );
        eCode.setAttributeNode(attrDepth);

	    if ( codeRange != null ) {
	    	if ( codeRange.getsNumber() != null ) {
	    		SectionNumber sSectionNumber = codeRange.getsNumber();
		        Attr attrRange = document.createAttribute(CODERANGEBEGIN);
		        attrRange.setValue(sSectionNumber.getSectionNumber());
		        eCode.setAttributeNode(attrRange);
		        Attr attrPosition = document.createAttribute(POSITIONBEGIN);
		        attrPosition.setValue(Integer.toString( sSectionNumber.getPosition()) );
		        eCode.setAttributeNode(attrPosition);
	    	}

	    	if ( codeRange.geteNumber() != null ) {
	    		SectionNumber eSectionNumber = codeRange.geteNumber();
	    		Attr attrRange = document.createAttribute(CODERANGEEND);
		        attrRange.setValue(eSectionNumber.getSectionNumber());
		        eCode.setAttributeNode(attrRange);
		        Attr attrPosition = document.createAttribute(POSITIONEND);
		        attrPosition.setValue(Integer.toString( eSectionNumber.getPosition()) );
		        eCode.setAttributeNode(attrPosition);
	    	}
	    }

	    Element eTitle = document.createElement(TITLE);
    	eTitle.appendChild( document.createTextNode(title));
    	eCode.appendChild( eTitle );

	    Element eShortTitle = document.createElement(SHORTTITLE);
	    eShortTitle.appendChild( document.createTextNode(shortTitle));
    	eCode.appendChild( eShortTitle );

    	if ( createChildren ) {
	    	Iterator<CodeReference> rit = references.iterator();
	    	while ( rit.hasNext() ) {
	    		CodeReference reference = rit.next();
	    		eCode.appendChild( reference.createXML(document, createChildren ) );
	    	}
    	}
        
        return eCode;
    }
	
	public void addReference(CodeReference reference) {
		references.add(reference);
    	mergeCodeRange(reference.getCodeRange());
	}

	public ArrayList<CodeReference> getReferences() {
		return references;
	}

	@Override
    public String toString() {
    	String cString = title + ": " + references.size() + " references";
        return cString;
    }
	
    public String getTitle() {
        return title;
    }
    
    public String getShortTitle() {
        return shortTitle;
    }
	
	public int getDepth() {
		return depth;	// always 0
	}
	
	public String getPart() {
		return facetHead;
	}
	
	public String getPartNumber() {
		return null;
	}

//	@Override
//	public SectionRange getSectionRange() {
//		return null;
//	}

	public Section getSection() {
		return null;
	}

	public CodeRange getCodeRange() {
		return codeRange;
	}

	public void mergeCodeRange(CodeRange codeRange) {
		this.codeRange.mergeRange(codeRange);
	}

	public void getParentReferences(ArrayList<CodeReference> returnPath) {}

	@Override
	public String[] getFullFacet() {
		String pFacet = getReferenceFacet();
		return new String[] {pFacet};
	}

	public String getReferenceFacet() {
//		if ( part == null ) throw new RuntimeException("Subcode::part == null");
//		return part+'-'+Integer.toString(depth);
		String facet = facetHead+'-'+Integer.toString(depth);
		return facet;
	}

	public Code getParentCode() {
		return this;
	}

	public Code getCode() {
		return this;
	}

	public int compareTo(Code o) {
		return this.title.compareTo(o.getTitle());
	}
/*	
	private class MyIteratorHandler implements IteratorHandler {
		ArrayList<CodeReference> codeReferences;
		public MyIteratorHandler( ArrayList<CodeReference> codeReferences ) {
			this.codeReferences = codeReferences;
		}
		public boolean handleSection(Section section) {
			codeReferences.add(section);
			return true;
		}
	}

	public void setupComparable() throws Exception {
		if (comparableList != null ) {
			comparableList = new ArrayList<CodeReference>();
			MyIteratorHandler iteratorHandler = new MyIteratorHandler(comparableList);
			iterateSections(iteratorHandler);
		}
	}
*/
/*
	public int compareReferences(CodeReference cr1, CodeReference cr2) {
		int i1 = comparableList.indexOf( cr1 );
		int i2 = comparableList.indexOf( cr2 );
		return i1-i2;
	}
*/
}