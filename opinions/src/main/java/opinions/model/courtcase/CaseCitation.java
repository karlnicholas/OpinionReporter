package opinions.model.courtcase;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CaseCitation implements Comparable<CaseCitation>, Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String[] appellateSets = {
		"cal.2d", 
		"cal.3d", 
		"cal.4th", 
		"cal.5th", 
		"cal.app", 
		"cal.app.2d", 
		"cal.app.3d", 
		"cal.app.4th", 
		"cal.app.5th", 
	};
	
	private int volume;	
	private int vset;
	private int page;
	
	public CaseCitation() {}
	
	public CaseCitation(String volume, String set, String page) {
		setSvolume(volume);
		setSset(set);
		setSpage(page);
	}
	private void setSvolume(String volume) {
		this.volume = Integer.parseInt(volume);
	}
	private void setSset(String vset) {
		this.vset = findSetPosition(vset);
	}
	private void setSpage(String page) {
		this.page = Integer.parseInt(page);
	}
	
	@Override
	public String toString() {
		return "" + volume + " " + appellateSets[vset] + " " + page;
	}

	@Override
	public int compareTo(CaseCitation citation) {
		if ( vset != citation.vset) return vset - citation.vset;
		else if ( volume != citation.volume) return volume - citation.volume;
		else if ( page != citation.page) return page - citation.page;
		else return 0;
	}
	
	private int findSetPosition(String set) {
		for ( int i=0; i<appellateSets.length; ++i ) {
			if ( appellateSets[i].equals(set)) return i;
		}
		throw new RuntimeException("No set found: " + set);
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getVset() {
		return vset;
	}

	public void setVset(int vset) {
		this.vset = vset;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + vset;
		result = prime * result + page;
		result = prime * result + volume;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaseCitation other = (CaseCitation) obj;
		if (vset != other.vset)
			return false;
		if (page != other.page)
			return false;
		if (volume != other.volume)
			return false;
		return true;
	}

}
