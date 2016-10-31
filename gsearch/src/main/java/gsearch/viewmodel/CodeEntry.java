package gsearch.viewmodel;

import java.util.*;

import codesparser.CodeReference;

public class CodeEntry extends EntryBase {
	
	private List<EntryReference> entries;
	
	public CodeEntry() {super(); init(); }
	public CodeEntry( CodeReference reference ) {
		super( reference, reference.getShortTitle());
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