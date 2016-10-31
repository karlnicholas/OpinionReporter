package gsearch.viewmodel;

import java.util.*;

import codesparser.CodeReference;

public class SubcodeEntry extends EntryBase {

	private List<EntryReference> entries;
	
	public SubcodeEntry() {super(); init(); }
	public SubcodeEntry( CodeReference reference ) {
		super( reference, reference.getPart()==null?"": (reference.getPart() + " " + reference.getPartNumber()) ); 
		init(); 
	}

	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null;}
	@Override
	public boolean isSectionText() { return false; }
}