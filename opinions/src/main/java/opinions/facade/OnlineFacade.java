package opinions.facade;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import javax.persistence.*;

import opinions.model.courtcase.CourtCase;
import opinions.parsers.*;
import codesparser.*;

public class OnlineFacade {
	
	private static final Logger log = Logger.getLogger(OnlineFacade.class.getName()); 
	
	private EntityManager em;
	private CaseParserInterface caseParserInterface;
	private CodesInterface codesInterface;
	
	public OnlineFacade(
			EntityManager em, 
			CaseParserInterface caseParserInterface, 
			CodesInterface codesInterface
	) {
		this.em = em;
		this.caseParserInterface = caseParserInterface;
		this.codesInterface = codesInterface;
	}
	
	public synchronized void updateDatabase() {
		DatabaseFacade dbFacade = new DatabaseFacade(em);
		
		List<CourtCase> onlineCases = listOnlineCases();
		if ( onlineCases == null || onlineCases.size() == 0 ) {
			log.info("No cases found online: returning.");
			return;
		}
		List<CourtCase> dbCases = dbFacade.listCases();
		List<CourtCase> dbCopy = new ArrayList<CourtCase>(dbCases);
		log.info("Found " + dbCases.size() + " in the database.");
		
		// Determine old cases
		// remove online cases from dbCopy
		// what's left is no longer in online List
		Iterator<CourtCase> dbit = dbCopy.iterator();
		while ( dbit.hasNext() ) {
			CourtCase dbCase = dbit.next();
			if ( onlineCases.contains(dbCase) ) {
				dbit.remove();
			}
		}
		if ( dbCopy.size() > 0 ) {
			log.info("Deleting " + dbCopy.size() + " cases." );
			dbFacade.removeCases(dbCopy);
		} else {
			log.info("No cases deleted.");
		}
		
		// Determine new cases
		// remove already persisted cases from onlineList
		for ( CourtCase dbCase: dbCases ) {
			int idx = onlineCases.indexOf(dbCase);
			if ( idx >= 0 ) {
				onlineCases.remove(idx);
			}
		}
		if ( onlineCases.size() > 0 ) {
			int errMax = 3;
			while ( onlineCases.size() > 0 ) {
				List<CourtCase> tenCases = new ArrayList<CourtCase>();
				Iterator<CourtCase> onit = onlineCases.iterator();
				int count = 10;
				while ( onit.hasNext() ) {
					tenCases.add(onit.next());
					onit.remove();
					if ( --count <= 0 ) break;
				}
				try{
					downloadCases(tenCases);
					log.info("Persisting " + tenCases.size() + " cases." );
					dbFacade.mergeAndPersistCases(tenCases);
				} catch ( Throwable t ) {
					log.warning(t.getMessage());
					if ( --errMax <= 0 ) break;
				}
			}
		} else {
			log.info("No new cases.");
		}
	}
	
	private List<CourtCase> listOnlineCases() {
		Reader reader = null;
		try {
	    	reader = caseParserInterface.getCaseList();
	    	List<CourtCase> courtCases = caseParserInterface.parseCaseList(reader);
	    	reader.close();
	    	return courtCases;
		} catch ( Exception e) {
			throw new RuntimeException(e);
		} finally {
			if ( reader != null ) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	/**
	 * Not thread safe, so synchronized. I don't think anyone would 
	 * be calling it expect internally, but making the point anyway.
	 */
	private void downloadCases(List<CourtCase> cases) {
		try {
	    	// Create the CACodes list
			CodeTitles[] codeTitles = codesInterface.getCodeTitles();
			CodeCitationParser parser = new CodeCitationParser(codeTitles);
			// loop and download each case
			for( CourtCase courtCase: cases ) {
				log.info("Downloading Case: " + courtCase.getName());
				parser.parseCase(caseParserInterface.getCaseFile(courtCase, false), courtCase);
			}
		} catch (Exception e) {
			throw new RuntimeException( e );
		} 
	}

}
