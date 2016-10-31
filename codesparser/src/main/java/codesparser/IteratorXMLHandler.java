package codesparser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IteratorXMLHandler {
	public Element handleSection(Section section, Document document, Element eSection ) throws Exception;
}
