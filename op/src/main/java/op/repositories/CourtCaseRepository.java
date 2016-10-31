package op.repositories;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import op.services.CodeInterfacesService;
import opinions.facade.*;
import opinions.model.courtcase.CourtCase;
import opinions.model.opinion.*;

@Repository
public class CourtCaseRepository extends Observable {
	private static final Logger logger = Logger.getLogger(CourtCaseRepository.class.getName());
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private CodeInterfacesService codeInterfaces;
	
	@Transactional
	public void updateJobUpdate() {
		try {
		// So, do the real work.
		OnlineFacade casesFacade = new OnlineFacade(
				em, 
				codeInterfaces.getCaseParserInterface(), 
				codeInterfaces.getCodesInterface()
			);
		casesFacade.updateDatabase();
		setChanged();
		notifyObservers();
		} catch ( Throwable t) {
			logger.severe("update database error: " + t.getMessage());
		}
	}
	
	@Transactional(readOnly=true)
	public List<CourtCase> listCourtCases(Date sd, Date ed) {
		DatabaseFacade databaseFacade = new DatabaseFacade(em);
		return databaseFacade.findByPublishDateRange(sd, ed);
	}
	
	@Transactional(readOnly=true)
	public List<OpinionCase> getOpinionCases(
		Date sd, 
		Date ed, 
		boolean compressCodeReferences, 
		int levelOfInterest
	) throws Exception {
		List<OpinionCase> opinionCases = new ArrayList<OpinionCase>();
		OpinionCaseBuilder opinionCaseBuilder = new OpinionCaseBuilder(codeInterfaces.getCodesInterface());
		List<CourtCase> cases = listCourtCases(sd, ed);
		for ( CourtCase ccase: cases ) {
			OpinionCase opinionCase = opinionCaseBuilder.buildParsedCase(ccase, compressCodeReferences);
			opinionCase.trimToLevelOfInterest(levelOfInterest);
			opinionCases.add(opinionCase); 
		}
		return opinionCases;
	}

	@Transactional(readOnly=true)
	public List<Date> listPublishDates() {
		DatabaseFacade databaseFacade = new DatabaseFacade(em);
		return databaseFacade.listPublishDates();
	}
	
	
	
}
