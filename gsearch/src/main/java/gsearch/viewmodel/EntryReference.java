package gsearch.viewmodel;

import java.util.*;

import codesparser.CodeReference;

public interface EntryReference {
	String getFullFacet();
	CodeReference getCodeReference();
	List<EntryReference> getEntries();
	String getText();
	int getCount();
	void setCount(int count);
	boolean isPathPart();
	void setPathPart(boolean pathPart);
	boolean isSectionText();
}
