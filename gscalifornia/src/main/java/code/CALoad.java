package code;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.*;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Element;

import codesparser.*;
import gsearch.load.*;

public class CALoad implements LoadInterface {
	private static final Logger logger = Logger.getLogger(CALoad.class.getName());
	private static final String DEBUGFILE = null;	//"fam";
//	private static long globalsectioncount = 0;
	private IndexWriter indexWriter;
	private TaxonomyWriter taxoWriter;
    private FacetsConfig facetsConfig;
	
	private int nDocsAdded;
    private int nFacetsAdded;
    
    private int position;
    
    /**
     * This file happens in two places. The first creates hybrid XML files, the second
     *  load up the lucene indexes.
     */

    /**
     * This part loads Lucene Indexes
     */
	@Override
	public void loadCode(File codesDir, File xmlcodes, File index, File indextaxo) throws Exception {
		Date start = new Date();
		CACodes caCodes = new CACodes();

		logger.info("Indexing to directory 'index'...");

		Directory indexDir = FSDirectory.open(index);
		Directory taxoDir = FSDirectory.open(indextaxo);

//		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9);		
		Analyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);
		// Create a new index in the directory, removing any
		// previously indexed documents:
		iwc.setOpenMode(OpenMode.CREATE);
	    // create and open an index writer
	    indexWriter = new IndexWriter(indexDir, iwc);
	    // create and open a taxonomy writer
	    taxoWriter = new DirectoryTaxonomyWriter(taxoDir, OpenMode.CREATE);
	    
	    facetsConfig = new FacetsConfig();
	    facetsConfig.setHierarchical(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
//	    facetsConfig.setRequireDimCount(FacetsConfig.DEFAULT_INDEX_FIELD_NAME, true);
/*	    
		FacetIndexingParams fip = new FacetIndexingParams(new CategoryListParams() {
			@Override
			public OrdinalPolicy getOrdinalPolicy(String dim) {
				// NO_PARENTS also works:
				return OrdinalPolicy.ALL_PARENTS;
			}
		});

		facetFields = new FacetFields(taxoWriter, fip);
*/
	    nDocsAdded = 0;
	    nFacetsAdded = 0;

		// dreams and aspirations
	    File[] files = codesDir.listFiles( new FileFilter() {

			public boolean accept(File pathname) {
				if ( pathname.isDirectory() ) return false;
				if (pathname.getName().toString().contains("constitution"))
					return false;
				if ( DEBUGFILE != null ) { 
					if (!pathname.getName().toString().contains(DEBUGFILE)) return false;
				}
				return true;
			}} );
	    
	    for ( int i=0;i<files.length; ++i ) {
			logger.info("Processing " + files[i]);
			processCodesFile(caCodes, codesDir, files[i]);
		}
		taxoWriter.commit();
		indexWriter.commit();

		taxoWriter.close();
		indexWriter.close();

		Date end = new Date();
		logger.info(end.getTime() - start.getTime() + " total milliseconds");
		logger.info("From " + "codes" + " " + nDocsAdded + ": Facets = " + nFacetsAdded);
	}

	private void processCodesFile(CACodes caCodes, File codesDir, File file) throws Exception {
		CodeParser parser = new CodeParser();
		Code c = parser.parse(caCodes, "ISO-8859-1", file);
		String abvr = file.getName().toString().substring(0, file.getName().toString().indexOf('_'));
		position = 1;
/*
		// debug code
		CodeReference reference = c.findReference(new SectionNumber("2150"));
		processReference( reference, codesdir.getPath() + "/" + abvr );
		// debug code
*/		
		iterateReferences( caCodes, c.getReferences(), codesDir.getPath() + "/" + abvr);		
	}
	
	private void iterateReferences( CACodes codes, ArrayList<CodeReference> references, String basepath) throws Exception {
		// Iterator over sections ..
		for ( CodeReference reference: references ) { 
        	// keep going until we get into a section
        	if ( reference.getReferences() != null ) iterateReferences(codes, reference.getReferences(), basepath);
        	processReference( codes, reference, basepath);
        }
	}
	
	/*
	 * I need to save the title, the categorypath, the full path for reference, the text, the part and partnumber
	 * and of course the section and sectionParagraph if it exists
	 */
	private void processReference( CACodes codes, CodeReference reference, String basepath) throws Exception {
		LuceneCodeModel model = new LuceneCodeModel( reference );
		
//		SectionRange range = reference.getSectionRange();
//		if (range != null) {
		Section section = reference.getSection();
		if (section != null) {
			CodeRange range = reference.getCodeRange();
			String strRange = range.getsNumber().getSectionNumber();
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
			if ( range.geteNumber() != null && range.geteNumber().getSectionNumber() != null ) strRange = strRange + "-" + range.geteNumber();
			String strPath = new String(basepath + "\\" + subdir + "\\" + strRange);
			File codeDetail = new File(strPath);
			ArrayList<String>sections = SectionParser.parseSectionFile("ISO-8859-1", codeDetail, reference); //  parseParagraph( codeDetail, model );
			// not sure this is needed here .. but why not
			range.getsNumber().setPosition(position);
			parseSectionModels(sections, model);
			// not sure this is needed here .. but why not
			range.geteNumber().setPosition(position-1);
		}
		
		if ( model.getSections().size() == 0 ) {
			writeDocument(codes, reference, new LuceneSectionModel(new SectionNumber(-1, ""), "") );
		} else {
			for ( LuceneSectionModel sectionModel: model.getSections() ) {
				writeDocument(codes, reference, sectionModel);
			}
		}
	}
	
	private void writeDocument(CACodes codes, CodeReference reference, LuceneSectionModel sectionModel) throws Exception {

		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		
		String sectionNumber = sectionModel.getSectionNumber().toString();
		String sectionText = sectionModel.getSectionParagraph();
		String strPosition = Integer.toString( sectionModel.getSectionNumber().getPosition() );

		String part = reference.getPart();
		if ( part == null ) part = "";

		String partNumber = reference.getPartNumber();
		if ( partNumber == null ) partNumber = "";

/*		
		CodeRange range = reference.getCodeRange();
		String codeRange; 
		if ( range == null ) codeRange = "";
		else if ( range.geteNumber() != null && range.geteNumber().getSectionNumber() != null ) codeRange = range.getsNumber().getSectionNumber() + " - " + range.geteNumber().getSectionNumber();
		else if ( range.getsNumber() != null && range.getsNumber().getSectionNumber() != null ) codeRange = range.getsNumber().getSectionNumber();
		else codeRange = "";
*/		

		doc.add(new StringField("path", FacetUtils.toString(reference.getFullFacet()), Field.Store.YES));

//		doc.add(new StringField("part", part , Field.Store.YES));

//		doc.add(new StringField("partnumber", partNumber, Field.Store.YES));

//		doc.add(new TextField("title", reference.getTitle(), Field.Store.YES));

		doc.add(new StringField("sectionnumber", sectionNumber, Field.Store.YES));

//		doc.add(new StringField("coderange", codeRange, Field.Store.YES));

		doc.add(new StringField("position", strPosition, Field.Store.YES));

		//		doc.add(new VecTextField("sectiontext", sectionText, Field.Store.YES));
		doc.add(new TextField("sectiontext", sectionText, Field.Store.YES));

		// obtain the sample facets for current document
		String[] facetPath = reference.getFullFacet();
		// invoke the category document builder for adding categories to the document and,
		// as required, to the taxonomy index 
		FacetField facetField = new FacetField( 
				FacetsConfig.DEFAULT_INDEX_FIELD_NAME, 
				facetPath 
			);

		doc.add( facetField );
		
		// finally add the document to the index
		indexWriter.addDocument(facetsConfig.build(taxoWriter, doc));
		
		nDocsAdded++;
		nFacetsAdded += facetPath.length; 
		
	}

	private void parseSectionModels(ArrayList<String> sections, LuceneCodeModel model) throws Exception {
		for ( String sect: sections ) {
			SectionNumber PNumber = SectionParser.getSectionNumber(position, sect);
			position++;
			LuceneSectionModel sectionModel = new LuceneSectionModel( PNumber, sect );
			model.getSections().add(sectionModel);
		}
	}

	/**
	 * 
	 * THIS PART CREATES HYBRID XML FILES
	 * 
	 */

	@Override
	public void createXMLCodes(File codesDir, File xmlcodes) throws Exception {
//		logger.setLevel(Level.INFO);
		CodesInterface codesInterface = new CACodes();
		File files[] = codesDir.listFiles(new FileFilter(){

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
			processFile(codesInterface, files[i], xmlcodes);
		}
		
	}
	
	public class MyIteratorXMLHandler implements IteratorXMLHandler {
		private String abvr;
		private String inpath;
		
		public MyIteratorXMLHandler(File file) {
			position = 1;
			 abvr = file.getName().toString().substring(0, file.getName().toString().indexOf('_'));
			 inpath = file.getParent();
		}
		
		public Element handleSection(Section section, org.w3c.dom.Document document, Element eSection) throws Exception {
//			SectionRange range = section.getSectionRange();
//			if (range != null) {
			CodeRange range = section.getCodeRange();
			if (range != null) {
				String strRange = range.getsNumber().getSectionNumber();
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
				if ( range.geteNumber() != null && range.geteNumber().getSectionNumber() != null ) strRange = strRange + "-" + range.geteNumber();
				String strPath = new String(inpath + "/" + abvr + "/" + subdir + "/" + strRange);
				File codeDetail = new File(strPath);
				ArrayList<String> sections = SectionParser.parseSectionFile("ISO-8859-1", codeDetail, section);
				// update CodeRange positions
				range.getsNumber().setPosition(position);
				appendParagraphXML(sections, section, document, eSection);
				// update CodeRange positions
				range.geteNumber().setPosition(position-1);			
			}
			return eSection;
		}
	}
	
	private void processFile(CodesInterface codesInterface, File file, File xmlcodes) throws Exception {
		CodeParser parser = new CodeParser();
		Code c = parser.parse(codesInterface, "ISO-8859-1", file);

		OutputStream os;
		org.w3c.dom.Document xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

/*
// debug code
		Section section = c.findReference( new SectionNumber("469") ).returnSection();
		File codeDetail = new File("c:/users/karl/code/pen/02001-03000/2635-2643");
		SectionParser.parseSectionFile(codeDetail, section);
// end debug code
	*/
		IteratorXMLHandler myHandler = new MyIteratorXMLHandler(file);
		xmlDoc.appendChild(c.iterateXML(xmlDoc,myHandler));

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();

		os = new FileOutputStream(xmlcodes.getPath() + "/" + c.getTitle() + ".xml");
		OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");

		// Write to a real file ... ;
		StreamResult result = new StreamResult(writer);

		DOMSource source = new DOMSource(xmlDoc);
		transformer.transform(source, result);

		result.getWriter().close();
		os.close();

		// System.out.println(c.getTitle());
	}

	private Element appendParagraphXML(
		ArrayList<String> sections,
		Section section, 
		org.w3c.dom.Document xmlDoc, 
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
			
			SectionNumber PNumber = SectionParser.getSectionNumber(position, sect);
			eParagraph.setAttribute(CodeReference.SECTIONNUMBER, PNumber.toString());
			eParagraph.setAttribute(CodeReference.POSITION, Integer.toString(position));
			position++;
			// System.out.println( sect.substring(0, 20) + " ...");
			eSection.appendChild(eParagraph);
//			globalsectioncount++;
		}

		return eSection;

	}

}
