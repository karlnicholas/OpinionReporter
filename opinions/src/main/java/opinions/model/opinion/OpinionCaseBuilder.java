package opinions.model.opinion;

import java.util.*;

import opinions.model.courtcase.*;
import codesparser.*;

public class OpinionCaseBuilder {

    private CodesInterface codesInterface;
	
    public OpinionCaseBuilder(CodesInterface codesInterface) {
    	this.codesInterface = codesInterface;
    }

    public OpinionCase buildParsedCase(CourtCase ccase, boolean compressCodeReferences) throws Exception {

    	ArrayList<OpinionCode> codes = new ArrayList<OpinionCode>();
        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<CodeCitation> itc = ccase.getCodeCitations().iterator();
        
        while ( itc.hasNext() ) {
        	CodeCitation citation = itc.next();
            // This is a section
            if ( citation.getCode() != null ) {
                OpinionSection opSection = new OpinionSection(citation);
                // here we look for the Doc Section within the Code Section Hierachary
                // and place it within the sectionReference we previously parsed out of the opinion
                opSection.setCodeReference( codesInterface.findReference(opSection.getCode(), opSection.getSectionNumber() ) );
	//            Section codeSection = codeList.findCodeSection(sectionReference);
	            // We don't want to keep ones that we can't map .. so .. 
	            if ( opSection.getCodeReference() != null ) {
	            	// First .. let's get the OpinionCode for this sectionReference
	            	OpinionCode opCode = findOrMakeOpinionCode(codes, opSection, compressCodeReferences); 
	            	opCode.addNewSectionReference( opSection );
	            } 
            }
        }
        return new OpinionCase(ccase, codes);
    }


    
    // here we want to go up the parent tree and until we get to the top Section
    // because that is where the "Code" starts ...
    // What we know is that the sectionReference cannot be at the 
    // Top of the Code.
    private OpinionCode findOrMakeOpinionCode( ArrayList<OpinionCode> codes, OpinionSection sectionReference, boolean compressCodeReferences) {
    	// First, find the section's top code
    	CodeReference codeReference = sectionReference.getCodeReference();
    	while ( codeReference.getParent() != null ) {
    		codeReference = codeReference.getParent();
    	}
    	// ok, our codeSection is the top section
    	Iterator<OpinionCode> cit = codes.iterator();
    	while ( cit.hasNext() ) {
    		OpinionCode opCode = cit.next();
//    		if ( opCode.getCodeReference().equals( code.getTitle())) return opCode;
    		if ( opCode.getCodeReference() == codeReference ) return opCode;
    	}
    	// else, construct one ...
    	OpinionCode opCode = new OpinionCode( codeReference, compressCodeReferences);
        codes.add(opCode);
    	return opCode;
    }
    

//    private CodesInterface codesInterface;
/*    
    public static void main(String[] args) throws Exception {

//        File file = new File("Cases/B231123.DOC");

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File directory, String name) {
                if ( DEBUGFILE != null ) if ( !name.contains(DEBUGFILE)) return false;
                if (name.contains("~")) return false;
                if (name.contains(".DOC")) {
                    return true;
                }
                return false;
            }
        };

        OpinionParser po = new OpinionParser();

        File casesDirectory = new File("c:/users/karl/workspace/opinionsreport/cases");
        File[] files = casesDirectory.listFiles(filter);
        for (int i=0; i<files.length; ++i ) {
            
            if ( DEBUG ) System.out.print( files[i].getName() );

            po.parseOpinionFile( files[i]);
//            ArrayList<OpinionSection> sectionReferences = po.parseOpinionFile( files[i], sp );
//            if ( DEBUG ) System.out.println( sectionReferences );
            
        }
        
    }    
    public void parseCitations(Case ccase, InputStream inputStream, CodesInterface codesInterface) throws Exception {

    	codeTitles = codesInterface.getCodeTitles();

//        codes = new ArrayList<OpinionCode>();
        ArrayList<OpinionSection> sectionReferences = parseCase(inputStream);
        
//        logger.info(sectionReferences.toString());

        // Map to the Codes
        // and then compress the Section References into a Hierarchy
        // within the California Codes structure
        //

        ccase.setDisposition( getSectionParser().getDisposition() );
        ccase.setSummary( getSectionParser().getSummaryParagraph() );

        // copy results into the new list ..
        // Fill out the codeSections that these section are referencing ..
        // If possible ... 
        Iterator<OpinionSection>sri = sectionReferences.iterator();
        while ( sri.hasNext() ) {
            // This is a section
            OpinionSection opSection = sri.next();
            // here we look for the Doc Section within the Code Section Hierachary
            // and place it within the sectionReference we previously parsed out of the opinion
            String codeTitle = opSection.getCode();
            if ( codeTitle != null ) {
                opSection.setCodeReference( codesInterface.findReference(codeTitle, opSection.getSectionNumber() ) );
            }
//            Section codeSection = codeList.findCodeSection(sectionReference);
            // We don't want to keep ones that we can't map .. so .. 
            if ( opSection.getCodeReference() != null ) {
            	// First .. let's get the OpinionCode for this sectionReference
            	OpinionCode topCode = findOrMakeOpinionCode(opSection); 

                topCode.addNewSectionReference( opSection );
            } else {
//                    System.out.println("Cannot find codeSection for " + sectionReference);
            }
//            System.out.println(this);
        }

    }

*/
    


}
