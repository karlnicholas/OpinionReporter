package opinions.model.opinion;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import codesparser.CodeReference;
import codesparser.SectionNumber;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpinionCode implements OpinionReference, Comparable<OpinionCode> {
    // This is holding the "chapter" section that is of interest ..
    private int cumulativeRefCount;
    private ArrayList<OpinionReference> childReferences;
    
    // Well, this should really be the toplevel object in the codes hierarchy ..
    // but, lets set it to this for now, come back to it later ..
    private CodeReference code;
    
    // this thing again
    private QueueUtility queue;

    // the old one was .. Section section, OpinionSection opSectionReference
    // Section section, OpinionSection opSectionReference
    public OpinionCode(CodeReference code, boolean compressCodeReferences) {
        this.code = code;
        childReferences = new ArrayList<OpinionReference>();
        queue = new QueueUtility(compressCodeReferences); 
        // even so .. we'll have think about this .. 
        // no references initially .. 
        cumulativeRefCount = 0;
    }
    
    public void trimToLevelOfInterest( int levelOfInterest ) {
    	Iterator<OpinionReference> ori = childReferences.iterator();
    	while ( ori.hasNext() ) {
    		OpinionReference opReference= ori.next();
    		opReference.trimToLevelOfInterest( levelOfInterest );	
    		if ( opReference.getRefCount() < levelOfInterest ) {
    			ori.remove();
    		}
    	}
    }

    // push the new SectionReference and all its parents onto a stack, 
    // then call the mergeSubcodes so the stack can combine parents that
    // are the same
    public void addNewSectionReference( OpinionReference opReference ) {
		queue.push(opReference);
		//
	    while ( opReference.getCodeReference().getParent() != null ) {
	    	// First, check to see if are at the top ...
	    	CodeReference codeReference = opReference.getCodeReference().getParent();
	    	if ( codeReference.getParent() != null  ) {
//		    	subcode = new OpinionSubcode( codeSection, subcode );
		    	opReference = new OpinionSubcode( codeReference, opReference.getRefCount() );
		        queue.push(opReference);
	    	} else {
	    		break;
	    	}
	    }
	    // Merge the queue into the grand hierarchy 
	    incRefCount( queue.mergeSubcodes(childReferences).getRefCount() );
    }    

    public int compareTo( OpinionCode opCode) {
        return cumulativeRefCount - opCode.getRefCount();
    }

    public CodeReference getCodeReference() {
    	return code;
    }

    public ArrayList<OpinionReference> getChildReferences() {
    	return this.childReferences;
    }

    public int getRefCount() {
        return cumulativeRefCount;
    }

    public int incRefCount(int amount) {
    	cumulativeRefCount = cumulativeRefCount + amount;
        return cumulativeRefCount;
    }
            
	public void incorporateOpinionReference(OpinionReference opReference, QueueUtility queue) {
        incRefCount(opReference.getRefCount());
	}

    public Element createXML(Document document) {

        Element eCode;
        eCode = code.createXML(document, false);

    	Attr attrRefCount = document.createAttribute("refcount");
        attrRefCount.setValue(Integer.toString(cumulativeRefCount));
        eCode.setAttributeNode(attrRefCount);

    	Iterator<OpinionReference> rit = childReferences.iterator();
    	while ( rit.hasNext() ) {
    		OpinionReference opReference= rit.next();
    		eCode.appendChild( opReference.createXML( document ) );
    	}
        return eCode;
    }

	public void addReference(OpinionReference opReference) {
		childReferences.add(opReference);
	}


	public SectionNumber getSectionNumber() {
		// return nothing
		return null;
	}

	public void addToChildren(QueueUtility queueUtility) {
        if ( queue.size() > 0 ) {
            queue.mergeSubcodes( childReferences);
        }
	}

	public boolean iterateSections(IterateSectionsHandler handler) throws Exception {
		Iterator<OpinionReference> rit = childReferences.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false; 
		}
		return true;
	}

	public String toString() {
        return "\n" + childReferences;
    }

	public OpinionSection getOpinionSection() {
		return null;
	}

	public void setSectionNumber(SectionNumber sectionNumber) {
		// do nothing
	}

	// Do the test here so that it doesn't need to be done in the presentation layer.
	@Override
	public List<OpinionSection> getSections() {
		List<OpinionSection> sectionList = new ArrayList<OpinionSection>();
		for ( OpinionReference opReference: childReferences ) {
			if ( opReference instanceof OpinionSection ) sectionList.add((OpinionSection)opReference);
		};
		return sectionList;
	}

	// Do the test here so that it doesn't need to be done in the presentation layer.
	@Override
	public List<OpinionReference> getSubcodes() {
		List<OpinionReference> referenceList = new ArrayList<OpinionReference>();
		for ( OpinionReference opReference: childReferences ) {
			if ( opReference instanceof OpinionSubcode ) referenceList.add((OpinionSubcode)opReference);
		};
		return referenceList;
	}
}
