package opinions.dao;

import java.util.*;

import javax.persistence.*;

import opinions.dao.base.JpaDao;
import opinions.model.courtcase.CourtCase;

public class CaseJpaDao extends JpaDao<CourtCase, String> implements CaseDao {

	private TypedQuery<CourtCase> findByPublishDate;
	private TypedQuery<CourtCase> findByPublishDateRange;
	private TypedQuery<Date> listPublishDates;

	public CaseJpaDao(EntityManager em) {
		super(CourtCase.class, em);
	    findByPublishDate = em.createNamedQuery("CourtCase.findByPublishDate", CourtCase.class);
	    findByPublishDateRange = em.createNamedQuery("CourtCase.findByPublishDateRange", CourtCase.class);
	    listPublishDates = em.createNamedQuery("CourtCase.listPublishDates", Date.class);
	}

	@Override
	public List<CourtCase> findByPublishDate(Date publishDate) {
		return findByPublishDate.setParameter("publishDate", publishDate).getResultList();
	}

	@Override
	public List<CourtCase> findByPublishDateRange(Date startDate, Date endDate) {
		return findByPublishDateRange.setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
	}
	
	@Override
	public List<Date> listPublishDates() {
		return listPublishDates.getResultList();
	}
}
