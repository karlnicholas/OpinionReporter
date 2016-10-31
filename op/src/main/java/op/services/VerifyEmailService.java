package op.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import op.model.Account;
import op.repositories.AccountRepository;
import op.sendemail.SendEmail;
import op.sendemail.SendEmail.RESPONSES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class VerifyEmailService {
	private static final Logger log = Logger.getLogger(VerifyEmailService.class.getName());
	
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private SpringTemplateEngine mailTemplateEngine;
	@Autowired
	private SendEmail sendEmail;

	@Async
	public void verify(Account account) {
		// So, do the real work.
		// / accountRepository.findUnverified

		sendEmail(account);
		
//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		log.info("VerifyEmail sent: " );
	}
		
	@Scheduled(cron="0 30 0 * * ?")		// 12:30 am every day
	public void verifyHousekeeping() {
		// So, do the real work.
		// / accountRepository.findUnverified
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		dayOfYear = dayOfYear - 4;
		if ( dayOfYear < 1 ) {
			year = year - 1;
			dayOfYear = 365 + dayOfYear;
		}
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
		Date threeDaysAgo = cal.getTime();

		List<Account> accounts = accountRepository.findAllUnverified();
		for ( Account account: accounts ) {
			if ( account.getCreateDate().compareTo(threeDaysAgo) < 0 ) {
				accountRepository.delete(account.getEmail());
				continue;
			}
	
			// Prepare the evaluation context
			sendEmail(account);
//			System.out.println("Resend = " + account.getEmail());
		}
			
//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		log.info("VerifyEmail sent: " );
	}
	
	private void sendEmail(Account account) {
		// too many errors?
		if ( account.getVerifyErrors() > 3 ) return;
		
		Context ctx = new Context(account.getLocale());
		// Prepare the evaluation context
		ctx.setVariable("account", account);
		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		RESPONSES response = sendEmail.sendEmail(
			"no-reply@op-cacode.rhcloud.com",
			account.getEmail(), 
			"Court Opinions - Please Verify Your Account",
			htmlContent
		);

		if ( response != RESPONSES.OK ) {
			account.setVerifyErrors(account.getVerifyErrors()+1);
			accountRepository.merge(account);
		} else {
			account.setVerifyCount(account.getVerifyCount()+1);
			accountRepository.merge(account);
		}
		
	}

}