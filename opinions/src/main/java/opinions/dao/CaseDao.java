package opinions.dao;

import java.util.*;

import opinions.dao.base.GenericDao;
import opinions.model.courtcase.CourtCase;

public interface CaseDao extends GenericDao<CourtCase, String> {

	public List<CourtCase> findByPublishDate(Date publishDate);
	public List<CourtCase> findByPublishDateRange(Date startDate, Date endDate);
	public List<Date> listPublishDates();
}
