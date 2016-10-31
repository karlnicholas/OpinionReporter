package codesparser;

import java.io.File;
import java.util.ArrayList;

public interface CodesInterface {
	public ArrayList<Code> getCodes();
	
	public Code findCodeFromFacet(String fullFacet);
    public CodeReference findReferenceByFacet(String shortTitle, String fullFacet);
    public String mapCodeToFacetHead(String title);

    public CodeReference findReference(String title, SectionNumber sectionNumber);

    public CodeTitles[] getCodeTitles();
    public String getShortTitle(String title);

    public void loadXMLCodes(File xmlCodes) throws Exception;
}