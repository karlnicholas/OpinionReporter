package opinions.model.opinion;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import codesparser.CodeReference;
import codesparser.SectionNumber;

public interface OpinionReference {

	public void trimToLevelOfInterest( int levelOfInterest );

	public void addReference(OpinionReference opReference);
    public void addToChildren( QueueUtility queueUtility );
	public void incorporateOpinionReference(OpinionReference opReference, QueueUtility queueUtility);	
	public int incRefCount(int amount);
	
	public SectionNumber getSectionNumber();
	public CodeReference getCodeReference();
	public List<OpinionReference> getChildReferences();
	public OpinionSection getOpinionSection();
	public List<OpinionSection> getSections();
	public List<OpinionReference> getSubcodes();
	public int getRefCount();

    // return true to keep iterating, false to stop iteration
	public boolean iterateSections( IterateSectionsHandler handler) throws Exception;
	public Element createXML(Document document);

}
