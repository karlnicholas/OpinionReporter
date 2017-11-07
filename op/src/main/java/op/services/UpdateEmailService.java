package op.services;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import op.model.Account;
import op.repositories.AccountRepository;
import op.sendemail.SendEmail;
import op.web.viewmodel.ViewInformation;
import op.web.viewmodel.ViewModelBuilder;
import opinions.model.opinion.OpinionCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class UpdateEmailService {
	private static final Logger log = Logger.getLogger(UpdateEmailService.class.getName());
	
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private SpringTemplateEngine mailTemplateEngine;
	@Autowired
	private ViewModelBuilder viewModelBuilder;
	@Autowired
	private SendEmail sendEmail;
		
	@Scheduled(cron="0 0 23 * * 0,5,6")		// 11 pm every Fri, Sat, and Sunday
	public void sendUpdateEmails() {
		// So, do the real work.
		// basically assuming we are on sunday.
		Calendar firstDay = Calendar.getInstance();
		int year = firstDay.get(Calendar.YEAR);
		int dayOfYear = firstDay.get(Calendar.DAY_OF_YEAR);
		dayOfYear = dayOfYear - 1;
		if ( dayOfYear < 1 ) {
			year = year - 1;
			dayOfYear = 365 + dayOfYear;
		}
		firstDay.set(Calendar.YEAR, year);
		firstDay.set(Calendar.DAY_OF_YEAR, dayOfYear);
		Calendar lastDay = Calendar.getInstance();
		lastDay.setTime(firstDay.getTime()); 
		viewModelBuilder.bracketWeek(firstDay, lastDay);
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
		ViewInformation viewInfo = new ViewInformation(
				firstDay.getTime(), 
				lastDay.getTime(), 
				true, 
				2);

		List<Account> accounts = accountRepository.findEmailUpdates();
		for ( Account account: accounts ) {
			viewInfo.account = account;
			List<OpinionCase> opinionCases;
			try {
				opinionCases = viewModelBuilder.getOpinionCasesForAccount(viewInfo);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			// Prepare the evaluation context
			sendEmail(viewInfo, opinionCases);
		}
			
//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		log.info("Update Emails Sent: Firstday = " + df.format(firstDay.getTime()) + " lastDay = " + df.format(lastDay.getTime()) );
	}
	
	private void sendEmail(ViewInformation viewInfo, List<OpinionCase> opinionCases) {
		Context ctx = new Context(viewInfo.account.getLocale());
		// Prepare the evaluation context
		ctx.setVariable("viewInfo", viewInfo);
		ctx.setVariable("cases", opinionCases);
		String htmlContent = mailTemplateEngine.process("updates.html", ctx);
		
		sendEmail.sendEmail(
			"no-reply@op-op.b9ad.pro-us-east-1.openshiftapps.com", 
			viewInfo.account.getEmail(), 
			"Court Opinions - Weekly Report", 
			htmlContent
		);

		viewInfo.account.setUpdateDate(new Date());
		accountRepository.merge(viewInfo.account);

		//		String htmlContent = mailTemplateEngine.process("updates.html", ctx);
//		log.info("Update email : " + htmlContent);
		
	}

}