package op.services;

import javax.annotation.PostConstruct;

import op.repositories.CourtCaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateJobService {
	
    @Autowired
    private CourtCaseRepository courtCaseRepository;
    
    @PostConstruct
    protected void updateOnce() {
    	UpdateOnce updateOnce = new UpdateOnce();
    	updateOnce.start();
    }
    
    private class UpdateOnce extends Thread  {
        public void run() {
        	try {
				sleep(10000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
        	courtCaseRepository.updateJobUpdate();
        }
    }
    
    @Scheduled(cron="0 30 22 * * ?")	// 10:30 pm every day
	protected void updateNightly() {
    	// So, do the real work.
    	courtCaseRepository.updateJobUpdate();
	}
}
