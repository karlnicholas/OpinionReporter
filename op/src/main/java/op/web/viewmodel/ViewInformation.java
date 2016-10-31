package op.web.viewmodel;

import java.util.Date;

import op.model.Account;

public class ViewInformation {
	public int totalCaseCount;
	public int accountCaseCount;
	public String navbarText;
	public Account account;
	public Date sd;
	public Date ed;
	public boolean compressCodeReferences;
	public int levelOfInterest;
	public ViewInformation(Date sd, Date ed, boolean compressCodeReferences, int levelOfInterest) {
		this.sd = sd;
		this.ed = ed;
		this.compressCodeReferences = compressCodeReferences;
		this.levelOfInterest = levelOfInterest;
	}
	public String getNavbarText() {
		String text = "Displaying " + Integer.toString(accountCaseCount) + " of " + Integer.toString(totalCaseCount);
		if ( account != null ) text = text + " selected cases";
		else text = text + " cases";
		if ( account != null ) text = text + " for " + account.getEmail();
		return text + ".";
	}
}

