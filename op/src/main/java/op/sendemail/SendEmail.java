package op.sendemail;

import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import op.services.UpdateEmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SendEmail {
	private static final Logger log = Logger.getLogger(UpdateEmailService.class.getName());
	public static enum RESPONSES { OK, ERROR };
	
	@Autowired
	private JavaMailSender mailSender;
	
	public RESPONSES sendEmail(String from, String to, String subject, String htmlContent) {
		
		// Prepare message using a Spring helper
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper message;
		try {
			message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);

			message.setText(htmlContent, true); // true = isHtml

			// Send mail
			mailSender.send(mimeMessage);
			
		} catch ( MailException | MessagingException e) {
			log.info("Update Email Errror: " + e.getMessage());
			return RESPONSES.ERROR;
		}
		return RESPONSES.OK;
	
	}
/*
		// Prepare message using a Spring helper
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper message;
		try {
			message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setFrom(from);
			message.setTo(to);
			message.setSubject(subject);

			message.setText(htmlContent, true); // true = isHtml

			// Send mail
			mailSender.send(mimeMessage);
		} catch ( MailException | MessagingException e) {
			log.info("Update Email Errror: " + e.getMessage());
			return RESPONSES.HARD_ERROR;
		}
		return RESPONSES.OK;
	
 */

}
