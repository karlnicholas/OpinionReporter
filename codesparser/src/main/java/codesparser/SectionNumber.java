package codesparser;

public class SectionNumber implements Comparable<SectionNumber> {
	private String sectionNumber;
	private int position;
	public SectionNumber(int position) {
		this.position = position;
	}

	public SectionNumber(int position, String sectionNumber) {
		this.position = position;
		this.sectionNumber = sectionNumber;
	}
	public String getSectionNumber() {
		return sectionNumber;
	}
	public void setSectionNumber(String sectionNumber) {
		this.sectionNumber = sectionNumber;
	}
	
	@Override
	public boolean equals(Object o) {
		if ( o instanceof SectionNumber ) {
			return sectionNumber.equals(((SectionNumber)o).sectionNumber);
		} else if ( o instanceof String ) {
			return sectionNumber.equals(((String)o));
		}
		else return false;
	}
	
	@Override
	public String toString() {
		return sectionNumber;
	}
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public int compareTo(SectionNumber arg0) {
		if ( position == -1 || arg0.position == -1 ) throw new RuntimeException("compareTo not enabled: " + position + ":" + arg0.position);
		return position - arg0.position;
	}
	
}
