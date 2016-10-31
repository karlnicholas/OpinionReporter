package codesparser;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public interface CodeReference {
	
    // strings for xml
	public static final String CODE = "code";
	public static final String SUBCODE = "subcode";
	public static final String SECTION = "section";
	public static final String TITLE = "title";
	public static final String SHORTTITLE = "shorttitle";
	public static final String PART = "part";
	public static final String PARTNUMBER = "partnumber";
	public static final String DEPTH = "depth";
	public static final String POSITION = "position";
	public static final String CODERANGEBEGIN = "codebegin";
	public static final String POSITIONBEGIN = "posbegin";
	public static final String CODERANGEEND = "codeend";
	public static final String POSITIONEND = "posend";
	public static final String SECTIONTEXT = "sectiontext";
	public static final String SECTIONNUMBER = "section";
//	public static final char PATHSEPARATOR = '/';
	
	public CodeReference findReference(SectionNumber sectionNumber);
//	public CodeReference findReferenceByFacet(String facet);
	public CodeReference findReferenceByFacets(String... facets);

	//	public String returnFullpath();
	public String[] getFullFacet();	
	public String getReferenceFacet();
	public void mergeCodeRange(CodeRange codeRange);
		
	public void setParent(CodeReference parent);
	public void addReference(CodeReference reference);

	public CodeReference getParent();
	public Code getParentCode();
	public void getParentReferences(ArrayList<CodeReference> returnPath);
	public ArrayList<CodeReference> getReferences();

	// for typecasting .. 
	public Section getSection();
	public Code getCode();
	
	public int getDepth();
	public String getTitle();
	public String getShortTitle();
	public String getPart();
	public String getPartNumber();
//	public SectionRange getSectionRange();
	public CodeRange getCodeRange();
	
	// return true to keep iterating, false to stop iteration
	public boolean iterateSections( IteratorHandler handler) throws Exception;
	public Element iterateXML( Document document, IteratorXMLHandler handler) throws Exception;
	public Element createXML(Document document, boolean createChildren);
	public Element createXML(Document document, Element element, boolean createChildren);
}
