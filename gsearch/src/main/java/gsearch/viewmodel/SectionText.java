package gsearch.viewmodel;

import java.util.List;

import codesparser.CodeReference;

public class SectionText implements Comparable<SectionText>, EntryReference  {
	private String sectionNumber;
	private String text;
	private int position;
	private String fullFacet;
	public SectionText() {}
	public SectionText( String sectionNumber, int position, String text, String fullFacet ) {
		this.sectionNumber  = sectionNumber;
		this.position = position;
		this.text = text;
		this.fullFacet = fullFacet;
	}
	@Override
	public int compareTo(SectionText arg0) {
		return position - arg0.position;
	}

	public String getSectionNumber() { return sectionNumber; }
	public int getPosition() { return position; }
	public String getText() { return text; }
	public CodeReference getCodeReference() { return null; }
	@Override
	public int getCount() { throw new RuntimeException("no getCount() on SectionText"); }
	@Override
	public void setCount(int count) { throw new RuntimeException("no setCount() on SectionText"); }
	@Override
	public List<EntryReference> getEntries() { return null; }
	@Override
	public boolean isPathPart() { return false; }
	@Override
	public void setPathPart(boolean pathPart) { throw new RuntimeException("no setPathPart() on SectionText"); }
	@Override
	public boolean isSectionText() { return true; }
	@Override
	public String getFullFacet() { return fullFacet; }
}
