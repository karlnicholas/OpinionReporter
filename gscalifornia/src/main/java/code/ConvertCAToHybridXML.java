package code;


import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import codesparser.Code;
import codesparser.CodeRange;
import codesparser.CodeReference;
import codesparser.IteratorXMLHandler;
import codesparser.Section;
import codesparser.SectionNumber;

/**
 * old, original files.
 *
 */
public class ConvertCAToHybridXML {
/*	
	private static final Logger logger = Logger.getLogger(ConvertCAToHybridXML.class.getName());
	private static final String DEBUGFILE = null;	//"fam";
	private static long globalsectioncount = 0;

	public static void main(String[] args) throws Exception {
		new ConvertCAToHybridXML().run(
				new File("c:/users/karl/code"), 
				new File("c:/users/karl/scsb/gscalifornia/src/main/resources/xmlcodes") 
			); 

		System.out.println("Section Count = " + globalsectioncount);
	}
	
	public void run(File codes, File xmlcodes) throws Exception {
//		logger.setLevel(Level.INFO);
		CACodes caCodes = new CACodes();
		File files[] = codes.listFiles(new FileFilter(){

			public boolean accept(File pathname) {
				if ( pathname.isDirectory()) return false;
				if (pathname.getName().toString().contains("constitution"))
					return false;
				if ( DEBUGFILE != null ) { 
					if (!pathname.getName().toString().contains(DEBUGFILE)) return false;
				}
				return true;
			}});
		for ( int i=0; i < files.length; ++i ) {
			logger.info( "Processing " + files[i]);
			processFile(caCodes, files[i], xmlcodes);
		}
		
	}

	private static void processFile(CACodes caCodes, File file, File xmlcodes) throws Exception {
		CodeParser parser = new CodeParser();
		Code c = parser.parse(caCodes, "ISO-8859-1", file);
		
		final String abvr = file.getName().toString().substring(0, file.getName().toString().indexOf('_'));
		final String inpath = file.getParent();
		logger.fine( "Path = " + inpath );

		OutputStream os;
		Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

*/
/*
// debug code
		Section section = c.findReference( new SectionNumber("469") ).returnSection();
		File codeDetail = new File("c:/users/karl/code/pen/02001-03000/2635-2643");
		SectionParser.parseSectionFile(codeDetail, section);
// end debug code
	*/
/*	
		xmlDoc.appendChild(c.iterateXML(xmlDoc, new IteratorXMLHandler() {
			public Element handleSection(Section section, Document document, Element eSection) throws Exception {
//				SectionRange range = section.getSectionRange();
//				if (range != null) {
				CodeRange range = section.getCodeRange();
				if (range != null) {
					String strRange = range.getsNumber();
					String firstInt = new String();
					for (int i = 0, il = strRange.length(); i < il; ++i) {
						char ch = strRange.charAt(i);
						if (Character.isDigit(ch)) {
							firstInt = firstInt.concat("" + ch);
						} else {
							break;
						}
					}
					int num = Integer.parseInt(firstInt);
					num = ((num - 1) / 1000) * 1000;
					String subdir = String.format("%05d-%05d", num + 1, num + 1000);
					if ( range.geteNumber().getSectionNumer() != null ) strRange = strRange + "-" + range.geteNumber();
					String strPath = new String(inpath + "/" + abvr + "/" + subdir + "/" + strRange);
					File codeDetail = new File(strPath);
					logger.finer(strPath);
					ArrayList<String> sections = SectionParser.parseSectionFile("ISO-8859-1", codeDetail, section);
					appendParagraphXML(sections, section, document, eSection);

				}
				return eSection;
			}
		}));

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		os = new FileOutputStream(xmlcodes.getPath() + "/" + c.getTitle() + ".xml");
		OutputStreamWriter writer = new OutputStreamWriter(os, Charset.forName("UTF-8"));

		// Write to a real file ... ;
		StreamResult result = new StreamResult(writer);

		DOMSource source = new DOMSource(xmlDoc);
		transformer.transform(source, result);

		result.getWriter().close();
		os.close();

		// System.out.println(c.getTitle());
	}

	private static Element appendParagraphXML(
		ArrayList<String> sections,
		Section section, 
		Document xmlDoc, 
		Element eSection
	) {
//		System.out.println(sections.size());

		for ( String sect: sections ) {
//			int slen = sect.length();
//			System.out.println(sect.substring(0, slen>20?20:slen ) + " ...");
			Element eParagraph = xmlDoc.createElement(CodeReference.SECTIONTEXT);
//			Really, the only difference is that we don't actually put any text into the XML 
//			eParagraph.appendChild(xmlDoc.createCDATASection(sect)); 

//			Element eParagraph = xmlDoc.createElement("sectiontext");
//			eParagraph.setTextContent(sect);
			
			SectionNumber PNumber = SectionParser.getSectionNumber(sect);
			eParagraph.setAttribute(CodeReference.SECTIONNUMBER, PNumber.toString());
			// System.out.println( sect.substring(0, 20) + " ...");
			eSection.appendChild(eParagraph);
			globalsectioncount++;
		}

		return eSection;

	}
*/	
}
