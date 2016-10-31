package opinions.facade;

import java.util.*;

import javax.persistence.*;

import opinions.dao.*;
import opinions.model.courtcase.CourtCase;

public class DatabaseFacade {
	
	private CaseDao caseDao;

	public DatabaseFacade(EntityManager em) {
		caseDao = new CaseJpaDao(em);
	}

	public void removeCases(List<CourtCase> oldCases) {
		for( CourtCase courtCase: oldCases ) {
			caseDao.remove(courtCase);
		}
	}

	public void persistCases(List<CourtCase> cases) {
		for(CourtCase ccase: cases) {
			caseDao.persist(ccase);
		}
	}
	
	public void mergeAndPersistCases(List<CourtCase> newCases) {
		Iterator<CourtCase> cit = newCases.iterator();
		while ( cit.hasNext() ) {
			CourtCase newCase = cit.next();
			caseDao.persist( caseDao.merge(newCase) );			
		}
	}
	
	public List<CourtCase> findByPublishDate(Date publishDate) {
		return caseDao.findByPublishDate(publishDate);
	}
	
	public List<CourtCase> findByPublishDateRange(Date startDate, Date endDate) {
		return caseDao.findByPublishDateRange(startDate, endDate);
	}

	public List<Date> listPublishDates() {
		return caseDao.listPublishDates();
	}

	public List<CourtCase> listCases() {
/*		
		List<CourtCase> ccases = caseDao.list();
		// handle lazy fetch
		for(CourtCase ccase: ccases ) {
			ccase.getCaseCitations();
			ccase.getCodeCitations();
		}
*/		
		return caseDao.list();
	}

}
