package op.controllers;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import op.model.*;
import op.repositories.*;
import op.web.viewmodel.*;
import opinions.model.opinion.OpinionCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
public class OpinionsController {
	
	@Autowired
    private ViewModelBuilder viewModelBuilder;	
	@Autowired
	private AccountRepository accountRepository;
	
	@RequestMapping(value = {"/", "opinions"}, method = RequestMethod.GET)
	public String index(
		@RequestParam(required=false) Date sd, 
		@RequestParam(required=false) Date ed, 
		Model model, 
		Principal principal
	) throws Exception {
//		List<Date> dates = courtCaseRepository.listDates();
//		model.addAttribute("dates", dates);
		model.addAttribute("viewModelBuilder", viewModelBuilder);
		ViewInformation viewInfo = new ViewInformation(sd,ed,true,2);
    	if ( sd != null && ed != null ) {
    		Account account = null;
	    	if ( principal != null ) {
		    	account = accountRepository.findByEmail(principal.getName());
	    	}
	    	viewInfo.account = account; 
	    	List<OpinionCase> opinionCases = viewModelBuilder.getOpinionCasesForAccount(viewInfo);
	    	model.addAttribute("opinionCases", opinionCases);
    	}
    	model.addAttribute("navbarText", viewInfo.getNavbarText());
		return "opinions/opinions";
//		return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
	}

	@InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
	
}
