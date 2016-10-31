package gsearch.viewmodel;

import java.util.*;

import codesparser.CodeReference;
import codesparser.FacetUtils;

public abstract class EntryBase implements EntryReference {
	private CodeReference codeReference;
	private int count;
	private boolean pathPart;
	private String fullFacet;
	private String displayTitle;
	// only for presentation layer
	protected List<EntryReference> scratchList;
	
	public EntryBase() { init(); }
	public EntryBase( CodeReference codeReference, String displayTitle) {
		this.codeReference = codeReference;
		this.displayTitle = displayTitle;
		this.fullFacet = FacetUtils.toString(codeReference.getFullFacet()); 
		init();
	}
	
	private void init() {
		count = 0;
		this.pathPart = true;
		this.scratchList = new ArrayList<EntryReference>();
	}

	public CodeReference getCodeReference() {
		return codeReference;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isPathPart() {
		return pathPart;
	}
	public void setPathPart(boolean pathPart) {
		this.pathPart = pathPart;
	}
	public String getFullFacet() {
		return fullFacet;
	}
	public void setFullFacet(String[] fullFacet) {
		this.fullFacet = FacetUtils.toString(fullFacet);
	}
	public String getDisplayTitle() {
		return displayTitle;
	}
	public void setDisplayTitle(String displayTitle) {
		this.displayTitle = displayTitle;
	}
}
