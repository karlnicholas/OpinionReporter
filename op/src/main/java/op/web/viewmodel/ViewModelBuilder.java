package op.web.viewmodel;

import java.util.*;

import javax.annotation.PostConstruct;

import op.repositories.CourtCaseRepository;
import opinions.model.opinion.*;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewModelBuilder implements Observer {
	private static final Logger log = Logger.getLogger(ViewModelBuilder.class.getName());
	private Object updateLock = new Object();
		
	@Autowired
	private CourtCaseRepository courtCaseRepository;
	// viewModel items for display
	// function as caches for database calls
	private List<Date[]> reportDates;
	private List<OpinionCase> allOpinionCases;
	private Date firstDate;
	private Date lastDate;
	private int totalCases;

	@PostConstruct
	protected void postConstruct() throws Exception {
		initialize();
		courtCaseRepository.addObserver(this);
	}
	
	private void initialize() throws Exception {
		reportDates = initReportDates();
		allOpinionCases = initOpinionCases();	// this depends on the initReportDates
		setTotalCases(allOpinionCases.size()); 
		log.info("ViewModelBuilder (re)initialized");
	}
	
	private List<OpinionCase> initOpinionCases() throws Exception {
		ViewInformation viewInfo = new ViewInformation(firstDate, lastDate, true, 2);
		return courtCaseRepository.getOpinionCases(viewInfo.sd, viewInfo.ed, viewInfo.compressCodeReferences, viewInfo.levelOfInterest);
	}
	
	private List<Date[]> initReportDates() {
		List<Date> dates = courtCaseRepository.listPublishDates();
		if ( dates.size() == 0 ) return null;
		// set these internal variables.
		lastDate = dates.get(0);
		firstDate = dates.get(dates.size()-1);
		// do the work.
		Calendar firstDay = Calendar.getInstance();
		firstDay.setTime(dates.get(0));
		Calendar lastDay = Calendar.getInstance();
		lastDay.setTime(dates.get(0));
		bracketWeek( firstDay, lastDay );
		List<Date[]> reportDates = new ArrayList<Date[]>();
		Date[] currentDates = new Date[2];
		for (Date date: dates) {
			if ( testBracket(date, firstDay, lastDay)) {
				addToCurrentDates(date, currentDates);
			} else {
				reportDates.add(currentDates);
				currentDates = new Date[2];
				firstDay.setTime(date);
				lastDay.setTime(date);
				bracketWeek(firstDay, lastDay);
			}
		}
		if ( currentDates[0] != null ) reportDates.add(currentDates);
		return reportDates;
	}
	
	private void addToCurrentDates(Date date, Date[] currentDates) {
		if (currentDates[0] == null ) {
			currentDates[0] = date;
			currentDates[1] = date;
			return;
		} else if ( currentDates[0].compareTo(date) > 0 ) {
			currentDates[0] = date;
			return;
		} else if ( currentDates[1].compareTo(date) < 0 ) {
			currentDates[1] = date;
			return;
		}
		return;
	}
	
	private boolean testBracket(Date date, Calendar firstDay, Calendar lastDay ) {
		boolean retVal = false;
		if ( firstDay.getTime().compareTo(date) < 0 && lastDay.getTime().compareTo(date) > 0 ) return true;
		return retVal;
	}
	
	public void bracketWeek(Calendar firstDay, Calendar lastDay ) {
		// get today and clear time of day
		firstDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		firstDay.clear(Calendar.MINUTE);
		firstDay.clear(Calendar.SECOND);
		firstDay.clear(Calendar.MILLISECOND);
		firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());
		firstDay.getTime();		// force recomputation. 

		// get today and clear time of day
		lastDay.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		lastDay.clear(Calendar.MINUTE);
		lastDay.clear(Calendar.SECOND);
		lastDay.clear(Calendar.MILLISECOND);
		lastDay.set(Calendar.DAY_OF_WEEK, lastDay.getFirstDayOfWeek());
		// start of the next week
		lastDay.add(Calendar.WEEK_OF_YEAR, 1);
	}
	
	/*
	 * Dynamic method (for now?)
	 */
	public List<OpinionCase> getOpinionCasesForAccount(
			ViewInformation viewInfo
	) throws Exception {
		List<OpinionCase> opinionCases = copyCasesForViewinfo(viewInfo);
		viewInfo.totalCaseCount = opinionCases.size();
    	if ( viewInfo.account != null ) {
	    	String[] codes = viewInfo.account.getCodes();
	    	if ( codes == null ) {
	    		opinionCases.clear();
	    		return opinionCases;	// Early termination
	    	}
	    	Iterator<OpinionCase> ocit = opinionCases.iterator();
	    	while ( ocit.hasNext() ) {
	    		OpinionCase opinionCase = ocit.next();
    			boolean found = false;
	    		for(OpinionCode opCode: opinionCase.getCodes() ) {
	    			String shortTitle = opCode.getCodeReference().getShortTitle();
			    	for ( String code: codes ) {
			    		if ( code.equals(shortTitle) ) {
			    			found = true;
			    			break;
			    		}
			    	}
			    	if ( found ) {
			    		break;
			    	}
	    		}
	    		if ( !found ) {
	    			ocit.remove();
	    		}
	    	}
    	}	
		viewInfo.accountCaseCount = opinionCases.size();
		return opinionCases;
	}
	
	private List<OpinionCase> copyCasesForViewinfo(ViewInformation viewInfo) {
		synchronized(updateLock) {
			List<OpinionCase> opinionCases = new ArrayList<OpinionCase>();
			for (OpinionCase opinionCase: allOpinionCases ) {
				if ( 
					opinionCase.getPublishDate().compareTo(viewInfo.sd) >= 0  
					&& opinionCase.getPublishDate().compareTo(viewInfo.ed) <= 0
				) {
					opinionCases.add(opinionCase);
				}
			}
			return opinionCases;
		}
	}
	
	public List<Date[]> getReportDates() {
		synchronized(updateLock) {
			return reportDates;
		}
	}
	
	public void setReportDates(List<Date[]> reportDates) {
		this.reportDates = reportDates;
	}

	@Override
	public void update(Observable o, Object arg) {
		// arg will be null, no object passed
		// re initialize
		synchronized(updateLock) {
			try {
				initialize();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public int getTotalCases() {
		return totalCases;
	}
	public void setTotalCases(int totalCases) {
		this.totalCases = totalCases;
	}
	
}
