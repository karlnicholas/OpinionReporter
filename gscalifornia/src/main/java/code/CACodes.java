package code;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import codesparser.*;

/**
 * Created with IntelliJ IDEA. User: karl Date: 6/7/12 Time: 5:37 AM To change
 * this template use File | Settings | File Templates.
 */
public class CACodes implements CodesInterface {
	private final static Logger logger = Logger.getLogger( CACodes.class.getName() );
    private static final String DEBUGFILE = null; // "bpc";	// "fam";
    
	private ArrayList<Code> codes;
	private CodeParser parser;
	private HashMap<String, CodeTitles> mapCodeToTitles;

    public static final String[] sectionTitles = {
        "title",
        "part",
        "division",
        "chapter",
        "article"
    };

/*
    public static String getShortTitle(String title) {
        if ( title == null ) return title;
        for (int i=0; i < patterns.length; ++i ) {
            if ( title.toLowerCase().contains(patterns[i]) )
                return patternsAbvr[i];
        }
        return title;
    }
*/    


	public CACodes() {
		mapCodeToTitles = new HashMap<String, CodeTitles> (); 
		mapCodeToTitles.put( "CALIFORNIA BUSINESS AND PROFESSIONS CODE".toLowerCase(), 
				new CodeTitles( "bpc", "Bus. & Professions", "business and professions code", new String[]{"bus. & prof. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA CODE OF CIVIL PROCEDURE".toLowerCase(), 
				new CodeTitles( "ccp", "Civ. Procedure", "code of civil procedure", new String[]{"code civ. proc.", "code of civ. pro."}) 
		);
		mapCodeToTitles.put( "CALIFORNIA CIVIL CODE".toLowerCase(), 
				new CodeTitles("civ", "Civil", "civil code", new String[]{"civ. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA COMMERCIAL CODE".toLowerCase(), 
				new CodeTitles( "com", "Commercial", "commercial code",new String[]{"com. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA CORPORATIONS CODE".toLowerCase(),
				new CodeTitles( "corp", "Corporations",  "corporations code", new String[]{"corp. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA EDUCATION CODE".toLowerCase(),
				new CodeTitles( "edc", "Education", "education code", new String[]{"ed. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA ELECTIONS CODE".toLowerCase(), 
				new CodeTitles( "elec", "Elections", "elections code", new String[]{"elec. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA EVIDENCE CODE".toLowerCase(), 
				new CodeTitles( "evid", "Evidence",  "evidence code", new String[]{"evid. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA FOOD AND AGRICULTURAL CODE".toLowerCase(),
				new CodeTitles( "fac", "Agriculture","food and agricultural code", new String[]{"food & agr. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA FAMILY CODE".toLowerCase(), 
				new CodeTitles( "fam", "Family", "family code", new String[]{"fam. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA FISH AND GAME CODE".toLowerCase(), 
				new CodeTitles( "fgc", "Fish & Game",  "fish and game code", new String[]{"fish & game code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA FINANCIAL CODE".toLowerCase(), 
				new CodeTitles( "fin", "Financial",  "financial code", new String[]{"fin. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA GOVERNMENT CODE".toLowerCase(), 
				new CodeTitles( "gov", "Government", "government code", new String[]{"gov. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA HARBORS AND NAVIGATION CODE".toLowerCase(), 
				new CodeTitles( "hnc", "Harbors & Nav.",  "harbors and navigation code", new String[]{"har. & nav. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA HEALTH AND SAFETY CODE".toLowerCase(), 
				new CodeTitles( "hsc", "Health", "health and safety code", new String[]{"health & saf. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA INSURANCE CODE".toLowerCase(),
				new CodeTitles( "ins", "Insurance", "insurance code", new String[]{"ins. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA LABOR CODE".toLowerCase(), 
				new CodeTitles( "lab", "Labor", "labor code", new String[]{"lab. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA MILITARY AND VETERANS CODE".toLowerCase(), 
				new CodeTitles( "mvc", "Military & Vets.","military and veterans code",new String[]{"mil. and vet. code"}) 
		);
		mapCodeToTitles.put( "CALIFORNIA PUBLIC CONTRACT CODE".toLowerCase(), 
				new CodeTitles( "pcc", "Public Contact", "public contract code", new String[]{"pub. con. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA PENAL CODE".toLowerCase(), 
				new CodeTitles( "pen", "Penal", "penal code", new String[]{"pen. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA PUBLIC RESOURCES CODE".toLowerCase(),
				new CodeTitles( "prc", "Public Resources", "public resources code", new String[]{"pub. res. code"} )
				);
		mapCodeToTitles.put( "CALIFORNIA PROBATE CODE".toLowerCase(), 
				new CodeTitles( "prob", "Probate", "probate code", new String[]{"prob. code"})
		);
		mapCodeToTitles.put( "CALIFORNIA PUBLIC UTILITIES CODE".toLowerCase(), 
				new CodeTitles( "puc", "Public Utilities", "public utilities code", new String[]{"pub. util. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA REVENUE AND TAXATION CODE".toLowerCase(), 
				new CodeTitles( "rtc", "Revenue & Tax.",  "revenue and taxation code", new String[]{"rev. & tax. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA STREETS AND HIGHWAYS CODE".toLowerCase(),
				new CodeTitles( "shc", "Highways",  "streets and highways code", new String[]{"st. & high. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA UNEMPLOYMENT INSURANCE CODE".toLowerCase(), 
				new CodeTitles( "uic", "Unemployment Ins.", "unemployment insurance code", new String[]{"unemp. ins. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA VEHICLE CODE".toLowerCase(),
				new CodeTitles( "veh", "Vehicle", "vehicle code", new String[]{"veh. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA WATER CODE".toLowerCase(),
				new CodeTitles( "wat", "Water", "water code", new String[]{"wat. code"} )
		);
		mapCodeToTitles.put( "CALIFORNIA WELFARE AND INSTITUTIONS CODE".toLowerCase(),
				new CodeTitles( "wic", "Welfare & Inst.", "welfare and institutions code", new String[]{"welf. & inst. code"} )
		);
	}

	/*
	 * There is a problem here. When using this method, the section numbers in  
	 * are not in consistent order. e.g.  Penal Code 273a is before 273.1
	 * but 270a is after 270.1 -- This makes is difficult, or impossible, to determine
	 * what file a specific section number belongs to. I'm coding it so that
	 * 270a is said to come before 270.1. This is needed because the files
	 * 270-273.5 includes 273a. The file 273.8-273.88 does not include 273a.
	 * I don't know if there are other situations where this is reversed ... 
	 * I should write a utility to check everything. See ConvertToHybridXML in the
	 * SCSB project.
	 * ...
	 * ok, there's more. The second numerical element of the section number is not ordered numberically, but lexically.
	 * so .. 422.865 comes before 422.88
	 */
	public void loadFromRawPath(File path) throws FileNotFoundException {
		// ArrayList<File> files = new ArrayList<File>();

		File[] files = path.listFiles( 
			new FileFilter() {
			public boolean accept(File file) {
				if (file.getName().toString().contains("constitution"))
					return false;
				if ( DEBUGFILE != null ) { 
					if (!file.getName().toString().contains(DEBUGFILE)) return false;
				}
				if ( file.isDirectory() ) return false;
				return true;
			}
			
		} );
		
		for ( int i=0; i < files.length; ++i ) {
			logger.info("Processing " + files[i]);
			loadRawFile( "ISO-8859-1", files[i] );
		}

		Collections.sort( codes );
	}

	public void loadXMLCodes(File xmlCodes) throws Exception {
		parser = new CodeParser();
		codes = new ArrayList<Code>();
	
		File[] files = xmlCodes.listFiles( 
				new FileFilter() {
				public boolean accept(File file) {
					// if ( directory.isDirectory() ) return false;
					if (file.getName().toString().contains("constitution"))
						return false;
					if ( DEBUGFILE != null ) { 
						if (!file.getName().toString().contains(DEBUGFILE)) return false;
					}
					return true;
				}
				
			} );
	
		for (int i = 0, j = files.length; i < j; ++i) {
			logger.info("Processing " + files[i] );
			loadXMLFile(files[i]);
		}
	
		Collections.sort( codes );
	}		

	private void loadXMLFile(File file) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return null; // Never resolve any IDs
            }
        });
        
        FileInputStream FIS = new FileInputStream( file );
        Document xmlDoc = db.parse( FIS );
        FIS.close();

        NodeList nodeList = xmlDoc.getChildNodes();
        
        for ( int di=0, dl = nodeList.getLength(); di<dl; ++di ) {
    		Node node = nodeList.item(di);
    		String nname = node.getNodeName();
	        if  ( nname.equals(CodeReference.CODE) ) {
	        	codes.add(new Code(node));
	        	return;
			}
        }
        return;
	}
	
	public void loadRawFile(String encoding, File file) throws FileNotFoundException {
		codes.add( parser.parse(this, encoding, file) );
	}

	public CodeReference findReference(String codeTitle, SectionNumber sectionNumber) {
		return findReference(codeTitle).findReference( sectionNumber );
	}

	public Code findReference(String codeTitle) {
		String tempTitle = codeTitle.toLowerCase();
		Iterator<Code> ci = codes.iterator();
		while (ci.hasNext()) {
			Code code = ci.next();
			if (code.getTitle().toLowerCase().contains(tempTitle)) {
				return code;
			}
			if ( tempTitle.contains(code.getTitle().toLowerCase())) {
				return code;
			}
		}
		throw new RuntimeException("Code not found:" + codeTitle);
	}

	private CodeReference findReferenceByShortTitle(String shortTitle) {
		Iterator<Code> ci = codes.iterator();
		while (ci.hasNext()) {
			Code code = ci.next();
			if (code.getShortTitle().equals(shortTitle)) {
				return code;
			}
		}
		throw new RuntimeException("Code not found:" + shortTitle);
	}

	public CodeReference findReferenceByFacet(String shortTitle, String fullFacet) {
		return findReferenceByShortTitle(shortTitle).findReferenceByFacets( fullFacet );
	}

// need something to build and manage "paths" .. 
// ie strings that indicate the "part" and "partNumber" of each reference
// leading up to the parent of the code section.. which needs its
	// own path entry as well.
	// for ex: family-1|division-3|chapter-3|article-5.6
	// we need to ..
	// generate a fullpath for any reference
	// find the code (any level) for any fullpath generated ..
	// ensure that the fullpath for just a code parent is unqiue
	// it's essentially a string-id into the code base.
	// also, the path needs to be parsable so as to point to a node
	// in the code .. or better a routine here to take a fullpath
	// and return the correct reference within with code heirarchy.
	// find the code and then it can be used to generate a return path
	// of references .. 
	// which it may well do already..
	// the only real issue is the top level .. and some of its first branches
	// which doesn't have any part-partNumber, so one has to be made up
	// question, do it on the fly, or, really, perhaps, when the code is 
	// first parsed from leginfo's raw codes ...
	// there MUST be a better way .. :)
	// 
/*	
	public Code findCode(String codeTitle) {
		String tempTitle = codeTitle.toLowerCase();
		Iterator<Code> ci = codes.iterator();
		while (ci.hasNext()) {
			Code code = ci.next();
			if (code.getTitle().toLowerCase().contains(tempTitle)) {
				return code;
			}
		}
		return null;
	}
	public static String generateFullPath(CodeReference reference ) {
		String header = new String();
		CodeReference codeReference = reference;
		while ( codeReference != null ) {
			String part = codeReference.getPart();
			String partNumber = codeReference.getPartNumber();
			if ( part == null ) {
				String tpart = codeReference.getTitle().trim().replace("CALIFORNIA", "").trim();
		    	part = tpart.substring(0, tpart.indexOf(" ", 0));	// CALIFORNIA
				partNumber = "1";
			}
			header = part + "-" + partNumber + header;
//			header = part + "-" + partNumber + header;
			codeReference = codeReference.getParent();
			if ( codeReference != null ) header = "|" + header;
		}
		return header;
	}

*/	
	public Code findCodeFromFacet( String fullPath ) {
		String mapValue = fullPath.substring(0, fullPath.indexOf('-')).toLowerCase();
		Iterator<Entry<String, CodeTitles>> sit = mapCodeToTitles.entrySet().iterator();
		while ( sit.hasNext() ) {
			Entry<String, CodeTitles> entry = sit.next();
			if ( entry.getValue().getFacetHead().equals( mapValue ) ) {
				return findReference(entry.getKey());
			}
		}
		throw new RuntimeException("Code Not Found:" + fullPath);
	}

	public String mapCodeToFacetHead(String title) {
		return mapCodeToTitles.get(title.toLowerCase()).getFacetHead(); 
	}
	
	public String getShortTitle(String title) {
		return mapCodeToTitles.get(title.toLowerCase()).getShortTitle(); 
	}

	public ArrayList<Code> getCodes() {
		return codes;
	}
	
	public static void main(String[] args) throws Exception {
//		logger.setLevel(Level.FINE);
		CACodes codes = new CACodes();
		codes.loadFromRawPath(new File("c:/users/karl/code"));
//		codes.loadFromXMLPath( new File("src/main/webapp/WEB-INF/xmlcodes"));
		// CodeParser parser = new CodeParser();
//		Path path = FileSystems.getDefault().getPath("codes/ccp_table_of_contents");
//		Path path = ;		// <--|
//		Code c = parser.parse(path);		// <--|
		CodeReference reference = codes.findReference("California Penal Code", new SectionNumber(0, "625") );
		System.out.println(reference );
		System.out.println( reference.getFullFacet());
	}

	@Override
	public CodeTitles[] getCodeTitles() {
		return mapCodeToTitles.values().toArray(new CodeTitles[0]);
	}

}
