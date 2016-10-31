package gsearch.viewmodel.strings;

import codesparser.CodeReference;
import codesparser.FacetUtils;

public abstract class EntryBase {
	private String displayTitle;
	private String title;
	private String fullFacet;
	private int count;
	private String codeRange;
	private String part;
	private String partNumber;
	private int posBegin;
	private int posEnd;
	private boolean pathPart;
	
	public EntryBase() { init(); }
	public EntryBase( String displayTitle, String title, CodeReference reference ) {
		this.displayTitle = displayTitle;
		this.title= title;
		this.fullFacet = FacetUtils.toString(reference.getFullFacet()); 
		this.part = reference.getPart();
		this.partNumber = reference.getPartNumber();
		if ( reference.getCodeRange() != null ) {
			this.codeRange = reference.getCodeRange().toString();
			this.posBegin = reference.getCodeRange().getsNumber().getPosition();
			this.posEnd = reference.getCodeRange().geteNumber().getPosition();
		}
		init();
	}
	
	private void init() {
		count = 0;
		pathPart = true;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDisplayTitle() {
		return displayTitle;
	}
	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}
	public String getFullFacet() {
		return fullFacet;
	}
	public void setFullFacet(String fullFacet) {
		this.fullFacet = fullFacet;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCodeRange() {
		return codeRange;
	}
	public void setCodeRange(String codeRange) {
		this.codeRange = codeRange;
	}
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public int getPosBegin() {
		return posBegin;
	}
	public void setPosBegin(int posBegin) {
		this.posBegin = posBegin;
	}
	public int getPosEnd() {
		return posEnd;
	}
	public void setPosEnd(int posEnd) {
		this.posEnd = posEnd;
	}
	public boolean isPathPart() {
		return pathPart;
	}
	public void setPathPart(boolean pathPart) {
		this.pathPart = pathPart;
	}
	public boolean isTextEntry() { 
		return false; 
	}
}
