package gsearch.load;


import codesparser.SectionNumber;

public class LuceneSectionModel {
	private SectionNumber sectionNumber;
	private String sectionParagraph;
	
	public LuceneSectionModel(SectionNumber sectionNumber, String sectionParagraph ) {
		this.sectionNumber = sectionNumber;
		this.sectionParagraph = sectionParagraph;
	}

	public SectionNumber getSectionNumber() {
		return sectionNumber;
	}

	public String getSectionParagraph() {
		return sectionParagraph;
	}

}
