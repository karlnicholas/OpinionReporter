package gsearch.viewmodel.strings;

import java.util.List;

public class TextEntry implements Comparable<TextEntry>, EntryReference {
	private String sectionNumber;
	private String text;
	private int position;
	public TextEntry() {}
	public TextEntry( String sectionNumber, int position, String text ) {
		this.sectionNumber  = sectionNumber;
		this.position = position;
		this.text = text;
	}
	@Override
	public int compareTo(TextEntry arg0) {
		return position - arg0.position;
	}
	@Override
	public String getText() { return text; }
	public String getSectionNumber() { return sectionNumber; }
	public int getPosition() { return position; }
	
	@Override
	public List<EntryReference> getEntries() { return null; }
	@Override
	public void setPathPart(boolean pathEnd) {}
	@Override
	public boolean isPathPart() { return false; }
	@Override
	public String getFullFacet() { return null; }
	@Override
	public void setCount(int count) {}
	@Override
	public int getCount() {return -1;}
	@Override
	public String getTitle() { return null;	}
	@Override
	public String getCodeRange() { return null; }
	@Override
	public int getPosBegin() { return position; }
	@Override
	public int getPosEnd() { return position; }
	@Override
	public String getPart() { return null; }
	@Override
	public String getPartNumber() { return null; }
	@Override
	public void setTitle(String title) {}
	@Override
	public String getDisplayTitle() {return null;}
	@Override
	public void setDisplayTitle(String displayTitle) {}
	@Override
	public void setFullFacet(String fullFacet) {}
	@Override
	public void setCodeRange(String codeRange) {}
	@Override
	public void setPart(String part) {}
	@Override
	public void setPartNumber(String partNumber) {}
	@Override
	public void setPosBegin(int posBegin) {}
	@Override
	public void setPosEnd(int posEnd) {}
	@Override
	public boolean isTextEntry() {  return true; }
}
