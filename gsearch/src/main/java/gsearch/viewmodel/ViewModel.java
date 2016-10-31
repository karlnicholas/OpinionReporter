package gsearch.viewmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewModel extends EntryBase implements Serializable, EntryReference {
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
	private List<EntryReference> entries;

	
	public ViewModel() {super(); init();}
	public ViewModel( String path, String term, boolean frag ) {
		super(); init();
		this.path = path==null?"":path;
		this.term = term==null?"":term;
		this.fragments = frag;
	}
	
	private void init() {
		entries = new ArrayList<EntryReference>();
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
	public void setEntries(List<EntryReference> entries) { this.entries = entries; }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null; }
	@Override
	public boolean isSectionText() { return false; }
}
