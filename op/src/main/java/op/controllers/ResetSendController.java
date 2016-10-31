package op.controllers;

import javax.validation.Valid;

import op.model.Account;
import op.repositories.AccountRepository;
import op.services.ResetSendService;
import op.web.forms.ResetSendForm;
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
public class ResetSendController {

    private static final String RESETSEND_VIEW_NAME = "resetsend/resetsend";

	@Autowired
    private ViewModelBuilder viewModelBuilder;	
	@Autowired
	private AccountRepository accountRepository;		
	@Autowired
	private ResetSendService resetSendService;
	
	@RequestMapping(value = "resetsend")
	public String doGet(
			@RequestParam(required=false) boolean reset, 
			Model model
	) {
		model.addAttribute(new ResetSendForm());
		model.addAttribute("viewModelBuilder", viewModelBuilder);
        return RESETSEND_VIEW_NAME;
	}
	
	@RequestMapping(value = "resetsend", method = RequestMethod.POST)
	public String doPost(
		@Valid @ModelAttribute ResetSendForm passwordResetForm, 
		Errors errors, 
		RedirectAttributes ra, 
		Model model
	) {
		model.addAttribute("viewModelBuilder", viewModelBuilder);
		if (errors.hasErrors()) {
			return RESETSEND_VIEW_NAME;
		}
		Account account = accountRepository.findByEmail(passwordResetForm.getEmail());
		if ( account == null ) {
	        errors.rejectValue("email", "resetsend.noemail");
			return RESETSEND_VIEW_NAME;
		}
		// asynchronously 
		resetSendService.reset(account);

        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "resetsend.sent");
		return "redirect:/";
	}
}
