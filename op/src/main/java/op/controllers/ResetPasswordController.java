package op.controllers;

import javax.validation.Valid;

import op.model.Account;
import op.repositories.AccountRepository;
import op.web.forms.ResetPasswordForm;
import op.web.support.MessageHelper;
import op.web.viewmodel.ViewModelBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ResetPasswordController {

    private static final String RESETPASSWORD_VIEW_NAME = "resetpassword/resetpassword";

	@Autowired
    private ViewModelBuilder viewModelBuilder;	
	@Autowired
	private AccountRepository accountRepository;		
	
	@RequestMapping(value = "resetpassword")
	public String doGet(
			@RequestParam String email, 
			@RequestParam String key, 
			RedirectAttributes ra, 
			Model model
	) {
		Account account = accountRepository.findByEmail(email);
		if ( account == null ) {
	        MessageHelper.addErrorAttribute(ra, "resetsend.noemail");
			return "redirect:/";
		} else if ( !account.getVerifyKey().equals(key)) {
			// tell a lie
	        MessageHelper.addErrorAttribute(ra, "resetsend.noemail");
			return "redirect:/";
		}
		ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
		resetPasswordForm.setEmail(email);
		model.addAttribute(resetPasswordForm);
		model.addAttribute("viewModelBuilder", viewModelBuilder);
        return RESETPASSWORD_VIEW_NAME;
	}
	
	@RequestMapping(value = "resetpassword", method = RequestMethod.POST)
	public String doPost(
		@Valid @ModelAttribute ResetPasswordForm passwordResetForm, 
		Errors errors, 
		RedirectAttributes ra, 
		Model model
	) {
		model.addAttribute("viewModelBuilder", viewModelBuilder);
		if (errors.hasErrors()) {
			return RESETPASSWORD_VIEW_NAME;
		}
		Account account = accountRepository.findByEmail(passwordResetForm.getEmail());
		if ( account == null ) {
	        errors.rejectValue("email", "resetsend.noemail");
			return RESETPASSWORD_VIEW_NAME;
		}
		account.setPassword(passwordResetForm.getPassword());
		// encode and update new password 
		accountRepository.updatePassword(account);

        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "resetpassword.updated");
		return "redirect:/";
	}
}
