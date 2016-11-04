package op.controllers;

import javax.validation.Valid;

import op.services.AboutSendService;
import op.web.forms.AboutForm;
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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AboutController {

    private static final String ABOUT_VIEW_NAME = "about/about";

	@Autowired
    private ViewModelBuilder viewModelBuilder;	
	@Autowired
	private AboutSendService aboutSendService;
	
	@RequestMapping(value = "about")
	public String doGet(
			@RequestParam(required=false) boolean reset, 
			Model model
	) {
		model.addAttribute(new AboutForm());
		model.addAttribute("viewModelBuilder", viewModelBuilder);
        return ABOUT_VIEW_NAME;
	}
	
	@RequestMapping(value = "about", method = RequestMethod.POST)
	public String doPost(
		@Valid @ModelAttribute AboutForm aboutForm, 
		Errors errors, 
		RedirectAttributes ra, 
		Model model, 
		WebRequest webRequest
		
	) {
		model.addAttribute("viewModelBuilder", viewModelBuilder);
		if (errors.hasErrors()) {
			return ABOUT_VIEW_NAME;
		}
		// asynchronously 
		aboutSendService.send(aboutForm, webRequest.getLocale());

        // see /WEB-INF/i18n/messages.properties and /WEB-INF/views/homeSignedIn.html
        MessageHelper.addSuccessAttribute(ra, "about.sent");
		return "redirect:/";
	}
}
