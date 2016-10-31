package gsearch.viewmodel.strings;

import java.util.*;

import codesparser.CodeReference;

public class CodeEntry extends EntryBase implements EntryReference {
	
	private List<EntryReference> entries;
	
	public CodeEntry() {super(); init(); }
	public CodeEntry( CodeReference reference ) {
		super( reference.getShortTitle(), reference.getTitle(), reference );
		init();
	}
	
	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null;}	
}

