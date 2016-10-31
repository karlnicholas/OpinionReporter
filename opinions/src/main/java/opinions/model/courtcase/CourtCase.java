package opinions.model.courtcase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

import javax.persistence.*;

@NamedQueries({
	@NamedQuery(name="CourtCase.findByPublishDate", 
		query="select c from CourtCase c where c.publishDate=:publishDate"),
	@NamedQuery(name="CourtCase.findByPublishDateRange", 
		query="select c from CourtCase c where c.publishDate between :startDate and :endDate order by c.publishDate desc"),
	@NamedQuery(name="CourtCase.listPublishDates", 
		query="select distinct c.publishDate from CourtCase c order by c.publishDate desc"),
})


@Entity(name="CourtCase")
public class CourtCase implements Serializable { 
	private static final long serialVersionUID = 1L;

	// should be used as a natural key
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	private Long id;
	@Column(length=31,nullable=false,unique=true)
    private String name;
	@Column(length=31,nullable=false)
    private String extension;
	@Column(length=255,nullable=false)
    private String title;
    @Temporal(TemporalType.DATE)
    private Date opinionDate;
    @Temporal(TemporalType.DATE)
    private Date publishDate;
	@Column(length=31,nullable=false)
    private String court;
	@Column(length=31)
    private String disposition;
    @Column(length=4095)
    private String summary;
    // late init
	@Column(length=255)
    private String defaultCodeSection;
    @ElementCollection
    private List<CodeCitation> codeCitations;	// implements citation
    @ElementCollection
    private List<CaseCitation> caseCitations;	// implements citation

    public CourtCase() {}

	public CourtCase(CourtCase ccase) {
    	this.name = ccase.name;
    	this.extension = ccase.extension;
    	this.title = ccase.title;
    	this.opinionDate = ccase.opinionDate;
    	this.publishDate = ccase.publishDate;
    	this.court = ccase.court;
    	this.codeCitations = ccase.codeCitations;
    	this.caseCitations = ccase.caseCitations;
    	this.disposition = ccase.disposition;
    	this.summary = ccase.summary;
    }

    public CourtCase(String name, String extension, String title, Date opinionDate, Date publishDate, String court) throws ParseException {
        this.name = name;
        this.extension = extension;
        this.title = title;
    	this.opinionDate = opinionDate;
    	this.publishDate = publishDate;
        if ( court == null ) this.court = new String();
        else this.court = court;
    }

    public Element createXML(Document document ) {
    	
    	Element eOpinion = document.createElement("case");
    	writeReportXML(document, eOpinion);

        return eOpinion;
    }

    public void writeReportXML(Document doc, Element xmlElement) {
        Element ename = doc.createElement("name");
        ename.appendChild(doc.createTextNode(name.replace(".DOC", "")));
        xmlElement.appendChild(ename);

        Element eextension = doc.createElement("extension");
        eextension.appendChild(doc.createTextNode(extension));
        xmlElement.appendChild(eextension);

        Element etitle = doc.createElement("title");
        etitle.appendChild(doc.createTextNode(title));
        xmlElement.appendChild(etitle);

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Element odate = doc.createElement("opiondate");
        odate.appendChild(doc.createTextNode(df.format(opinionDate)));
        xmlElement.appendChild(odate);

        Element pdate = doc.createElement("publishdate");
        pdate.appendChild(doc.createTextNode(df.format(publishDate)));
        xmlElement.appendChild(pdate);

        Element ecourt = doc.createElement("court");
        ecourt.appendChild(doc.createTextNode(court));
        xmlElement.appendChild(ecourt);

        Element edisposition = doc.createElement("disposition");
        if ( disposition == null ) {
        	edisposition.appendChild(doc.createTextNode("UNKNOWN"));
        } else {
        	edisposition.appendChild(doc.createTextNode(disposition.toUpperCase()));
        }
        xmlElement.appendChild(edisposition);

/*        
        for (int i=0, j=summary.length(); i<j; ++i ) {
        	System.out.println( summary.charAt(i)+ ":" + summary.codePointAt(i) );
        }
*/
        
        Element esummary = doc.createElement("summary");
        if ( summary == null ) {
        	esummary.appendChild(doc.createTextNode("Unable to programmatically determine the summary for this opinion."));
        } else {
        	esummary.appendChild(doc.createTextNode(summary));
        }
        xmlElement.appendChild(esummary);
    }

	public String getName() {
        return name;
    }

    public void setName(String name) {
		this.name = name;
	}

    public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getOpinionDate() {
		return opinionDate;
	}

	public void setOpinionDate(Date opinionDate) {
		this.opinionDate = opinionDate;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public void setCourt(String court) {
		this.court = court;
	}

	public String getCourt() {
		return court;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		if ( disposition != null && disposition.length() > 250 ) disposition = disposition.substring(0, 250);
		this.disposition = disposition;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		if ( summary != null && summary.length() > 4090 ) summary = "..." + summary.substring(summary.length()-4087);
		this.summary = summary;
	}

	public List<CodeCitation> getCodeCitations() {
		return codeCitations;
	}

	public void setCodeCitations(List<CodeCitation> codeCitations) {
		this.codeCitations = codeCitations;
	}

	public List<CaseCitation> getCaseCitations() {
		return caseCitations;
	}

	public void setCaseCitations(List<CaseCitation> caseCitations) {
		this.caseCitations = caseCitations;
	}

	@Override
	public String toString() {
        return String.format("%1$S : %2$tm/%2$td/%2$ty : %3$S", name, publishDate, title );
    }

	public String getDefaultCodeSection() {
		return defaultCodeSection;
	}

	public void setDefaultCodeSection(String defaultCodeSection) {
		this.defaultCodeSection = defaultCodeSection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CourtCase other = (CourtCase) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		return true;
	}


}
