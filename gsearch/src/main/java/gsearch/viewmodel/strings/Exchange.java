package gsearch.viewmodel.strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Exchange implements Serializable {
	private static final long serialVersionUID = 1L;
	public enum STATES {START, BROWSE, TERMINATE };
//	public String state;
	private STATES state;
	private String term;
	private String path;
	private boolean fragments;
	private int totalCount;

	// Codes Display: a list of all available codes
	// uses the count field to 
	public List<EntryReference> codeList;

	
	public Exchange() {super(); init();}
	public Exchange( String path, String term, boolean highlights ) {
		super(); init();
		this.path = path;
		this.term = term;
		this.fragments = highlights;
	}
	
	private void init() {
		codeList = new ArrayList<EntryReference>();
	}
	
	public STATES getState() { return state; }
	public void setState(STATES state) { this.state = state; }
	public String getTerm() { return term; }
	public void setTerm(String term) { this.term = term; }
	public String getPath() { return path; }
	public void setPath(String path) { this.path = path; }
	public int getTotalCount() { return totalCount; }
	public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
	public boolean isFragments() { return fragments; }
	public void setFragments(boolean highlights) { this.fragments = highlights; }
	public List<EntryReference> getCodeList() { return codeList; }
	public void setCodeList(List<EntryReference> codeList) { this.codeList = codeList; }
	
}
