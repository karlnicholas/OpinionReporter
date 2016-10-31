package op.controllers;

import javax.validation.Valid;

import op.model.*;
import op.repositories.AccountRepository;
import op.services.*;
import op.web.forms.SignupForm;
import op.web.support.*;
import op.web.viewmodel.ViewModelBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignupController {

    private static final String SIGNUP_VIEW_NAME = "signup/signup";

	@Autowired
    private ViewModelBuilder viewModelBuilder;	
	@Autowired
	private AccountRepository accountRepository;		
	@Autowired
	private UserService userService;
	@Autowired
	private VerifyEmailService verifyEmailService;
	
	@RequestMapping(value = "signup")
	public String signup(Model model) {
		model.addAttribute(new SignupForm());
		model.addAttribute("viewModelBuilder", viewModelBuilder);
        return SIGNUP_VIEW_NAME;
	}
	
	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(
		@Valid @ModelAttribute SignupForm signupForm, 
		Errors errors, 
		RedirectAttributes ra, 
		Model model, 
		WebRequest webRequest
	) {
		model.addAttribute("viewModelBuilder", viewModelBuilder);
		if (errors.hasErrors()) {
			return SIGNUP_VIEW_NAME;
		}
		Account account = accountRepository.encodeAndSave(signupForm.createAccount(webRequest.getLocale()));
		// asynchronously 
		verifyEmailService.verify(account);

		userService.signin(account);
        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:/";
	}
}
