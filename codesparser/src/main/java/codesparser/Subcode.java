package codesparser;

import java.util.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Subcode implements CodeReference {
//	private static final Logger logger = Logger.getLogger(Subcode.class.getName());
	private CodeReference parent;
	private String part;
    private String partNumber;
    private String title;
    private CodeRange codeRange;
    // keep track of how deep we are ..
    // Always > 0 for Subcode
    private int depth;
	// and pointers to under Chapters, Parts, Articles, etc
    private ArrayList<CodeReference> references;
        
    public Subcode(CodeReference parent, String part, String partNumber, String title, int depth) {
    	references = new ArrayList<CodeReference>();
    	codeRange = new CodeRange();
    	
    	this.parent = parent;
    	if ( part != null ) {
    		this.part = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
    	} else {
    		part = null;
    	}
    	this.partNumber = partNumber;
    	this.title = title;
    	this.depth = depth;
    	assert( depth>0 );
    }
    
    public Subcode( Node node, CodeReference parent )  {
    	references = new ArrayList<CodeReference>();
    	this.parent = parent;

    	NamedNodeMap mapAttr = node.getAttributes();
		
		Node nTemp = mapAttr.getNamedItem(PART);
		if ( nTemp != null ) part = nTemp.getNodeValue();
		else part = null;

		nTemp = mapAttr.getNamedItem(PARTNUMBER);
		if ( nTemp != null ) partNumber = nTemp.getNodeValue();
		else partNumber = null;
		
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
    		if ( nname.equals(TITLE) ) {
    			title = lnode.getTextContent();
    		} else if  ( nname.equals(SECTION) ) {
	        	references.add(new Section(lnode, this));
    		} else if  ( nname.equals(SUBCODE) ) {
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
		if ( !this.returnPartPath().equals(partFacet)) return null;
		if ( remainingFacet == null ) return this;
		Iterator<CodeReference> rit = references.iterator();
		while ( rit.hasNext() ) {
			CodeReference reference = rit.next().findReferenceByFacet(remainingFacet);
			if ( reference != null ) return reference;
		}
		return null;
	}
*/
	public boolean iterateSections(IteratorHandler handler) throws Exception {
		Iterator<CodeReference> rit = references.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false;
		}
		return true;
	}

    public void addReference( CodeReference reference ) {
    	references.add(reference);
    	mergeCodeRange(reference.getCodeRange());
    }

    public ArrayList<CodeReference> getReferences() {
    	return references;
    }
    
    public CodeReference getParent() {
    	return parent;
    }

    public void setParent( CodeReference parent ) {
    	this.parent = parent;
    }

	public Element iterateXML(Document document, IteratorXMLHandler handler) throws Exception {
		Element eSubcode = document.createElement(SUBCODE);
    	Iterator<CodeReference> rit = references.iterator();
    	while ( rit.hasNext() ) {
    		CodeReference reference = rit.next();
    		eSubcode.appendChild( reference.iterateXML(document, handler ) );
    	}
    	return createXML( document, eSubcode, false );		
	}

	public Element createXML( Document document, boolean createChildren ) {
	    return createXML( document, document.createElement(SUBCODE), createChildren );
	}

    public Element createXML( Document document, Element eSubcode, boolean createChildren ) {

    	if ( part != null ) {
            Attr attrPart = document.createAttribute(PART);
            attrPart.setValue(part);
            eSubcode.setAttributeNode(attrPart);
        }

        if ( partNumber != null ) {
            Attr attrPartNumber = document.createAttribute(PARTNUMBER);
            attrPartNumber.setValue(partNumber);
            eSubcode.setAttributeNode(attrPartNumber);
        }

        Attr attrDepth = document.createAttribute(DEPTH);
        attrDepth.setValue(Integer.toString( depth) );
        eSubcode.setAttributeNode(attrDepth);

	    if ( codeRange != null ) {
	    	if ( codeRange.getsNumber() != null ) {
	    		SectionNumber sSectionNumber = codeRange.getsNumber();
		        Attr attrRange = document.createAttribute(CODERANGEBEGIN);
		        attrRange.setValue(sSectionNumber.getSectionNumber());
		        eSubcode.setAttributeNode(attrRange);
		        Attr attrPosition = document.createAttribute(POSITIONBEGIN);
		        attrPosition.setValue(Integer.toString( sSectionNumber.getPosition()) );
		        eSubcode.setAttributeNode(attrPosition);
	    	}

	    	if ( codeRange.geteNumber() != null ) {
	    		SectionNumber eSectionNumber = codeRange.geteNumber();
	    		Attr attrRange = document.createAttribute(CODERANGEEND);
		        attrRange.setValue(eSectionNumber.getSectionNumber());
		        eSubcode.setAttributeNode(attrRange);
		        Attr attrPosition = document.createAttribute(POSITIONEND);
		        attrPosition.setValue(Integer.toString( eSectionNumber.getPosition()) );
		        eSubcode.setAttributeNode(attrPosition);
	    	}
	    }

	    Element eTitle = document.createElement(TITLE);
        eTitle.appendChild( document.createTextNode(title));
        eSubcode.appendChild( eTitle);

        if ( createChildren ) {
	    	Iterator<CodeReference> rit = references.iterator();
	    	while ( rit.hasNext() ) {
	    		CodeReference reference= rit.next();
	    		eSubcode.appendChild( reference.createXML(document, createChildren ) );
	    	}
        }
        
        return eSubcode;

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
		return title;
	}

	public String toString() {
        String indent = new String();
        String ret = indent + part + " " + partNumber + ":" + title;
        return ret;
    }

	public int getDepth() {
		return depth;
	}

	public Section getSection() {
		return null;
	}

	// can only assume that they are read in order
	// and therefore 
	public void mergeCodeRange(CodeRange codeRange) {
    	this.codeRange.mergeRange(codeRange);
    	parent.mergeCodeRange(this.codeRange);		
	}

	public CodeRange getCodeRange() {
		return codeRange;
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
//		if ( part == null ) throw new RuntimeException("Subcode::part == null");
//		return part+'-'+partNumber;
//		if ( part != null ) return part+'-'+partNumber;
//		else
		String partFacet = getParentCode().getPart() + '-' + Integer.toString(depth) + '-' + Integer.toString(parent.getReferences().indexOf(this));
		return partFacet;
	}

	public Code getParentCode() {
		return parent.getParentCode();
	}

	public Code getCode() {
		return null;
	}


}
