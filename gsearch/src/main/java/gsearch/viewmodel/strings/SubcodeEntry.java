package gsearch.viewmodel.strings;

import java.util.*;

import codesparser.CodeReference;

public class SubcodeEntry extends EntryBase implements EntryReference {

	private List<EntryReference> entries;
	
	public SubcodeEntry() {super(); init(); }
	public SubcodeEntry( CodeReference reference ) {		
		super(reference.getPart() + " " + reference.getPartNumber(), reference.getTitle(), reference ); 
		init(); 
	}

	private void init() { entries = new ArrayList<EntryReference>(); }
	@Override
	public List<EntryReference> getEntries() { return entries; }
	@Override
	public String getText() { return null;}
}

