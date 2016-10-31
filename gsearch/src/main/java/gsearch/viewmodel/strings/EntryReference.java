package gsearch.viewmodel.strings;

import java.util.*;

public interface EntryReference {
	public String getTitle();
	public void setTitle(String title);
	public String getDisplayTitle();
	public void setDisplayTitle(String displayTitle);
	public String getFullFacet();
	public void setFullFacet(String fullFacet);
	public int getCount();
	public void setCount(int count);
	public String getCodeRange();
	public void setCodeRange(String codeRange);
	public String getPart();
	public void setPart(String part);
	public String getPartNumber();
	public void setPartNumber(String partNumber);
	public int getPosBegin();
	public void setPosBegin(int posBegin);
	public int getPosEnd();
	public void setPosEnd(int posEnd);
	public boolean isPathPart();
	public void setPathPart(boolean pathPart);
	
	public boolean isTextEntry();
	public String getText();
	public List<EntryReference> getEntries();
}
