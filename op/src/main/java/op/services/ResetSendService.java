package op.services;

import java.util.logging.Logger;

import op.model.Account;
import op.sendemail.SendEmail;
import op.sendemail.SendEmail.RESPONSES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class ResetSendService {
	private static final Logger log = Logger.getLogger(ResetSendService.class.getName());
	
	@Autowired
	private SpringTemplateEngine mailTemplateEngine;
	@Autowired
	private SendEmail sendEmail;

	@Async
	public void reset(Account account) {
		// So, do the real work.
		// / accountRepository.findUnverified
		Context ctx = new Context(account.getLocale());
		// Prepare the evaluation context
		ctx.setVariable("account", account);
		String htmlContent = mailTemplateEngine.process("resetsend.html", ctx);
		RESPONSES response = sendEmail.sendEmail(
			"no-reply@op-op.b9ad.pro-us-east-1.openshiftapps.com",
			account.getEmail(), 
			"Court Opinions - Reset Password",
			htmlContent
		);

//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		log.info("Password reset sent: " + response);
	}
		
}