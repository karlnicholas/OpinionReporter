package codesparser;

public class CodeTitles {
	private String facetHead;
	private String shortTitle;
	private String fullTitle;
	private String[] abvrTitles;
	
	public CodeTitles(CodeTitles codeTitles) {
		// shallow copy
		this.facetHead = codeTitles.facetHead;
		this.shortTitle = codeTitles.shortTitle;
		this.fullTitle = codeTitles.fullTitle;
		this.abvrTitles = codeTitles.abvrTitles;
	}
	
	public CodeTitles(String facetHead, String shortTitle, String fullTitle, String[] abvrTitles) {
		this.facetHead = facetHead;
		this.shortTitle = shortTitle;
		this.fullTitle = fullTitle;
		this.abvrTitles = abvrTitles;
	}
	public String getFacetHead() {
		return facetHead;
	}
	public void setFacetHead(String facetHead) {
		this.facetHead = facetHead;
	}
	public String getShortTitle() {
		return shortTitle;
	}
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	public String getFullTitle() {
		return fullTitle;
	}
	public void setFullTitle(String fullTitle) {
		this.fullTitle = fullTitle;
	}
	public String[] getAbvrTitles() {
		return abvrTitles;
	}
	public String getAbvrTitle(int idx) {
		return abvrTitles[idx];
	}
	public String getAbvrTitle() {
		return abvrTitles[0];
	}
	public void setAbvrTitles(String[] abvrTitles) {
		this.abvrTitles = abvrTitles;
	}
}
