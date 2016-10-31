package op.controllers;

import op.model.Account;
import op.repositories.AccountRepository;
import op.web.support.MessageHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class VerifyEmailController {
	
	@Autowired
	private AccountRepository accountRepo;
	
	@RequestMapping(value="verify")
	public String doGet(
		@RequestParam(required = false) String email, 
		@RequestParam(required = false) String key, 
		RedirectAttributes ra
	) {
		if ( email == null || key == null ) {
	        MessageHelper.addErrorAttribute(ra, "verify.notprovided");
			return "redirect:/";
		}
		Account account = accountRepo.findByEmail(email);
		if ( account == null ) {
			MessageHelper.addErrorAttribute(ra, "verify.noemail");
		} else if ( account.isVerified() ) {
			MessageHelper.addInfoAttribute(ra, "verify.alreadyverified");
		} else if ( !account.getVerifyKey().equals(key)) {
			MessageHelper.addErrorAttribute(ra, "verify.nokeymatch");
		} else {
			account.setVerified(true);
			accountRepo.merge(account);
			// default to verified
			MessageHelper.addSuccessAttribute(ra, "verify.verified");
		}
		return "redirect:/";
	}

}
