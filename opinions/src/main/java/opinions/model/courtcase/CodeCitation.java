package opinions.model.courtcase;

import java.io.Serializable;

import javax.persistence.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: karl
 * Date: 5/27/12
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */

@Embeddable
public class CodeCitation implements Comparable<CodeCitation>, Serializable { 
	private static final long serialVersionUID = 1L;
	//	private static final Logger logger = Logger.getLogger(OpinionSection.class.getName());
	// This stuff holds the reference .. 
	// Which "code" it is and which section within that code is referenced
	// Also a place for the number of reference counts
	// as well as "designated," a working variable that shows how "strong" the reference is

	@Column(length=255)
	private String code;
	@Column(length=127,nullable=false)
    private String sectionNumber;
    private int refCount;
    private boolean designated;
    
    public CodeCitation() {}
    
    public CodeCitation(String code, String sectionNumber) {
    	// this is constructed without a parent and that's added later
    	// when we build the hierarchy
//    	logger.fine("code:" + code + ":section:" + section);
        this.code = code;
        this.sectionNumber = sectionNumber;
        refCount = 1;
        if ( code == null ) {
            designated = false;
        } else {
            designated = true;
        }
    }

    public Element createXML( Document document ) {

    	// what do we really need from here ...
    	Element eSection = document.createElement("citation");

        Attr attrRefCount = document.createAttribute("refcount");
        attrRefCount.setValue(Integer.toString(refCount));
        eSection.setAttributeNode(attrRefCount);
    
        Attr attrSecNumber = document.createAttribute("section");

        if ( sectionNumber != null ) {
            attrSecNumber.setValue("§ " + sectionNumber.toString());
            eSection.setAttributeNode(attrSecNumber);
        }

    	return eSection;
    }
    
    public String getSectionNumber() {
       return sectionNumber;
    }
    
	public void setSectionNumber(String sectionNumber) {
		this.sectionNumber = sectionNumber;
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
	
    public String toString() {
        return code + (designated==true?"*":"") + ":" + sectionNumber + ":" + refCount;
    }

	@Override
	public int compareTo(CodeCitation o) {
		if ( code == null && o.code != null ) return -1;
		if ( code != null && o.code == null ) return 1;
		if ( code != null && o.code != null ) {
			int r = code.compareTo(o.code); 
			if (  r != 0 ) return r; 
		}  
		if ( sectionNumber == null && o.sectionNumber != null ) return -1;
		if ( sectionNumber != null && o.sectionNumber == null ) return 1;
		if ( sectionNumber != null && o.sectionNumber != null ) {
			int r = sectionNumber.compareTo(o.sectionNumber); 
			if (  r != 0 ) return r; 
		}  
		return sectionNumber.compareTo(o.sectionNumber);
	}

	/*
	 * (non-Javadoc)
	 * In place for CodeCitationParser
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result
				+ ((sectionNumber == null) ? 0 : sectionNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeCitation other = (CodeCitation) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (sectionNumber == null) {
			if (other.sectionNumber != null)
				return false;
		} else if (!sectionNumber.equals(other.sectionNumber))
			return false;
		return true;
	}

}

