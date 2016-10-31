package opinions.model.opinion;

import java.util.*;

import opinions.model.courtcase.CodeCitation;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import codesparser.*;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpinionSection implements OpinionReference, Comparable<OpinionSection> { 
	//	private static final Logger logger = Logger.getLogger(OpinionSection.class.getName());
	// This stuff holds the reference .. 
	// Which "code" it is and which section within that code is referenced
	// Also a place for the number of reference counts
	// as well as "designated," a working variable that shows how "strong" the reference is
    private String code;
    private SectionNumber sectionNumber;
    private int refCount;
    private boolean designated;
    
    // This holds the CodeRefence to the 
    private CodeReference section;

    public OpinionSection(CodeCitation citation) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("code:" + code + ":section:" + section);
        this.code = citation.getCode();
        sectionNumber = new SectionNumber(-1, citation.getSectionNumber());
        refCount = citation.getRefCount();
        designated = citation.getDesignated();
    }

    public OpinionSection(Node node) throws DOMException {
    	NodeList nodeList = node.getChildNodes();
    	for ( int ni=0, nl=nodeList.getLength(); ni<nl; ++ni ) {
    		Node no = nodeList.item(ni);
    		if ( no.getNodeName().equals("title") ) {
    			NamedNodeMap mapAttr = no.getAttributes();

    			// need to fix this ..
    			Node nTemp = mapAttr.getNamedItem("refcount");
    			if ( nTemp != null ) refCount= Integer.parseInt( nTemp.getNodeValue() );
    			else refCount = -1;

    			int position = -1;
    			Node nTemp2 = mapAttr.getNamedItem("position");
    			if ( nTemp != null ) position = Integer.parseInt( nTemp2.getNodeValue() );

    			nTemp = mapAttr.getNamedItem("section");
    			if ( nTemp != null ) sectionNumber = new SectionNumber( position, nTemp.getNodeValue() );
    			else sectionNumber = null;

    			// There can be only one
    			break;
    		}
    	}
    }

    public boolean equals(OpinionSection dcs ) {
        if ( code == null && dcs.getCode() != null ) return false;
        if ( code != null && dcs.getCode() == null ) return false;

        if ( code.equals( dcs.getCode() ) && sectionNumber.equals( dcs.getSectionNumber() ) ) return true;

        return false;
    }

    public Element createXML( Document document ) {

    	// what do we really need from here ...
    	Element eSection = section.createXML(document, false);

        Attr attrRefCount = document.createAttribute("refcount");
        attrRefCount.setValue(Integer.toString(refCount));
        eSection.setAttributeNode(attrRefCount);
    
        Attr attrSecNumber = document.createAttribute("section");

        if ( sectionNumber != null ) {
            attrSecNumber.setValue("§ " + sectionNumber.toString());
            eSection.setAttributeNode(attrSecNumber);
        } else  {
            attrSecNumber.setValue("§§ " + ((Section)section).getCodeRange().toString());
            eSection.setAttributeNode(attrSecNumber);        	
        }

    	return eSection;
    }
    
    public int compareTo(OpinionSection dcs ) {
        if ( code == null && dcs.getCode() != null ) return -1;
        if ( code != null && dcs.getCode() == null ) return 1;

        if ( code != null && dcs.getCode() != null ) {
            int ret = code.compareTo( dcs.getCode() );
            if (  ret != 0 ) return ret;
        }

//        return sectionNumber.compareTo(dcs.getSectionNumber());
//        return section.getCodeRange().getsNumber().getSectionNumber().compareTo(dcs.getCodeReference().getCodeRange().getsNumber().getSectionNumber());
        // do a string compare for now .. really meant to compare codes 
        return sectionNumber.getSectionNumber().compareTo(dcs.getSectionNumber().getSectionNumber());
    }

    public SectionNumber getSectionNumber() {
       return sectionNumber;
    }

    // do this for the presentation layer
    public String getDisplaySectionNumber() {
    	String retString = new String();
        if ( sectionNumber != null ) {
        	retString += "§ " + sectionNumber.toString();
        } else  {
        	retString += "§§ " + ((Section)section).getCodeRange().toString();
        }
        return retString;
    }
    
	public String getCode() {
        return code;
    }
    public int getRefCount() {
        return refCount;
    }

    public int incRefCount(int amount) {
        refCount = refCount + amount;
        return refCount;
    }

    public void setRefCount(int count) {
        refCount = count;
    }

    public void setDesignated( boolean designated ) {
        this.designated = designated;
    }
    public boolean getDesignated() {
        return designated;
    }
    
    public void setCode( String code) {
    	this.code = code;
    }

    public void setCodeReference( CodeReference section ) {
    	this.section = section;
    }

    @Override
    public CodeReference getCodeReference( ) {
    	return section;
    }
	
	public void addReference(OpinionReference reference) {
		// do nothing
	}

	public ArrayList<OpinionReference> getChildReferences() {
		// nothing to return
		return null;
	}
	
	public void trimToLevelOfInterest(int levelOfInterest) {
		// nothing to do 
	}

	
	public boolean iterateSections(IterateSectionsHandler handler) throws Exception {
		return handler.handleOpinionSection(this);
	}
	
	public void incorporateOpinionReference(OpinionReference opReference, QueueUtility queue) {
		if ( queue.isCompressCodeReferences() ) {
	        incRefCount(opReference.getRefCount());
	        // basically, by ignoring the subcode it gets left out
	        // we should so incorporate the increment count into the section .. 
	        sectionNumber = null;
		}
	}
	
    public void addToChildren( QueueUtility queueUtility ) {
		// do nothing
	}

	
	public OpinionSection getOpinionSection() {
		return this;
	}

    public String toString() {
    	if ( sectionNumber == null ) {
    		return code + ":" + ((Section)section).getCodeRange().toString() + ":" + refCount;
    	}
        return code + ":" + sectionNumber + ":" + refCount;
    }

	@Override
    // nothing left to do here
	public List<OpinionSection> getSections() {
		return null;
	}

	@Override
	// nothing left to do here
	public List<OpinionReference> getSubcodes() {
		return null;
	}
    
}

