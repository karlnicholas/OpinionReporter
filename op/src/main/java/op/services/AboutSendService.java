package op.services;

import java.util.Locale;
import java.util.logging.Logger;

import op.sendemail.SendEmail;
import op.sendemail.SendEmail.RESPONSES;
import op.web.forms.AboutForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
public class AboutSendService {
	private static final Logger log = Logger.getLogger(AboutSendService.class.getName());
	
	@Autowired
	private SpringTemplateEngine mailTemplateEngine;
	@Autowired
	private SendEmail sendEmail;

	@Async
	public void send(AboutForm aboutForm, Locale locale) {
		// So, do the real work.
		// / accountRepository.findUnverified
		Context ctx = new Context(locale);
		// Prepare the evaluation context
		ctx.setVariable("about", aboutForm);
		String htmlContent = mailTemplateEngine.process("about.html", ctx);
		RESPONSES response = sendEmail.sendEmail(
			"no-reply@op-cacode.rhcloud.com",
			"karl.nicholas@outlook.com", 
			"Court Opinions - Feedback",
			htmlContent
		);

//		String htmlContent = mailTemplateEngine.process("verify.html", ctx);
		log.info("Feedback sent: " + response);
	}
		
}