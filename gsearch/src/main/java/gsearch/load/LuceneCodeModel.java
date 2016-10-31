package gsearch.load;


import java.util.ArrayList;

import codesparser.CodeReference;

/*
 * I need to save the title, the categorypath, the full path for reference, the text, the part and partnumber
 * and of course the section and sectionParagraph if it exists
 */
public class LuceneCodeModel {
	private CodeReference reference;
	private ArrayList<LuceneSectionModel> sections;
	
	public LuceneCodeModel( CodeReference reference ) {
		this.reference = reference;
		sections = new ArrayList<LuceneSectionModel>();
	}
	
	public CodeReference getReference() {
		return reference;
	}

	public ArrayList<LuceneSectionModel> getSections() {
		return sections;
	}

}
