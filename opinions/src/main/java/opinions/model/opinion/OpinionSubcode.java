package opinions.model.opinion;

import codesparser.CodeReference;
import codesparser.SectionNumber;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 6/7/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpinionSubcode implements OpinionReference, Comparable<OpinionSubcode> {
    // This is holding the "chapter" section that is of interest ..
	private CodeReference subcode;
    private int cumulativeRefCount;
    // and pointers to under Chapters, Parts, Articles, etc
    private ArrayList<OpinionReference> childReferences;

    // Construct a branch .. with a 
    public OpinionSubcode(CodeReference subcode, int cumulativeRefCount ) {
    	childReferences = new ArrayList<OpinionReference>();
    	this.subcode = subcode;
    	this.cumulativeRefCount = cumulativeRefCount;
    }
    
    public void trimToLevelOfInterest( int levelOfInterest ) {
    	Iterator<OpinionReference> ori = childReferences.iterator();
    	while ( ori.hasNext() ) {
    		OpinionReference opReference = ori.next();
			opReference.trimToLevelOfInterest( levelOfInterest );	
    		if ( opReference.getRefCount() < levelOfInterest ) {
    			ori.remove();
    		}
    	}
    }

    public int compareTo( OpinionSubcode subcode ) {
        return cumulativeRefCount - subcode.getRefCount();
    }

    public CodeReference getCodeReference() {
        return subcode;
    }
    
    public int getRefCount() {
        return cumulativeRefCount;
    }

    public int incRefCount(int amount) {
    	cumulativeRefCount = cumulativeRefCount + amount;
/*
    	Iterator<OpinionReference> sit = opReferences.iterator();
    	while ( sit.hasNext() ) {
    		OpinionReference opReference = sit.next();
    		if ( opReference.returnOpSection() != null ) {
	    		opReference.incRefCount(amount);
//    		sectionRef.setSectionNumber(sectionRef.getCodeSection().getRange().toString());
	    		opReference.setSectionNumber(null);
    		}
    	}
*/    	
        return cumulativeRefCount;
    }

	
    public void incorporateOpinionReference( OpinionReference opReference, QueueUtility queue ) {
        incRefCount(opReference.getRefCount());
        // basically, by ignoring the subcode it gets left out
        // we should so incorporate the increment count into the section .. 
        addToChildren(queue);
    }

	
    public void addToChildren( QueueUtility queue ) {
        if ( queue.size() > 0 ) {
            queue.mergeSubcodes( childReferences);
        }
    }


    public Element createXML(Document document ) {

    	Element eOpSubcode = subcode.createXML(document, false);

    	Attr attrRefCount = document.createAttribute("refcount");
        attrRefCount.setValue(Integer.toString(cumulativeRefCount));
        eOpSubcode.setAttributeNode(attrRefCount);

    	Iterator<OpinionReference> rit = childReferences.iterator();
    	while ( rit.hasNext() ) {
    		OpinionReference opReference = rit.next();
            eOpSubcode.appendChild(opReference.createXML(document));
    	}
        
        return eOpSubcode;
        
    }
/*
    public String toString() {
        return "\n" + codeSection + ": " + cumulativeReferenceCount + subcodes;
    }
*/
	
	public void addReference(OpinionReference opReference) {
		childReferences.add(opReference);
	}

	public ArrayList<OpinionReference> getChildReferences() {
		return childReferences;
	}
	
	public void setSectionNumber(SectionNumber sectionNumber) {
		// do nothing here ..
	}
	
	public boolean iterateSections(IterateSectionsHandler handler) throws Exception {
		Iterator<OpinionReference> rit = childReferences.iterator();
		while ( rit.hasNext() ) {
			if ( !rit.next().iterateSections(handler) ) return false; 
		}
		return true;
	}

	
	public SectionNumber getSectionNumber() {
		// return nothing
		return null;
	}

	
	public OpinionSection getOpinionSection() {
		return null;
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
