package op.controllers;

import static op.web.support.Message.MESSAGE_ATTRIBUTE;

import java.security.Principal;
import java.util.*;

import javax.annotation.PostConstruct;

import op.model.Account;
import op.repositories.AccountRepository;
import op.services.CodeInterfacesService;
import op.services.VerifyEmailService;
import op.web.support.Message;
import op.web.viewmodel.ViewModelBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import codesparser.CodeTitles;

@Controller
public class ProfileController {
	
	@Autowired
    private ViewModelBuilder viewModelBuilder;	
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CodeInterfacesService codeInterfaces;
	@Autowired
	private VerifyEmailService verifyEmailService;
    
//    private CodeTitles[] codeTitles;
    List<List<CodeTitles>> titleMatrix;
    
    @PostConstruct
    protected void initialize() {    	
		CodeTitles[] codeTitles = codeInterfaces.getCodeTitles();
		Arrays.sort(codeTitles, new Comparator<CodeTitles>() {
			@Override
			public int compare(CodeTitles o1, CodeTitles o2) {
				return o1.getShortTitle().compareTo(o2.getShortTitle());
			}});
		
		titleMatrix = new ArrayList<List<CodeTitles>>();
		List<CodeTitles> widths = new ArrayList<CodeTitles>();
		for (int i=0; i<codeTitles.length; i++) {
			widths.add(codeTitles[i]);
			if ( (i+1) % 3 == 0 ) { 
				titleMatrix.add( widths );
				widths = new ArrayList<CodeTitles>();
			}
		}
		if ( widths.size() > 0 ) titleMatrix.add( widths );
    	
    }
	
    @RequestMapping(value={"profile"}, method={RequestMethod.GET})
	public String doGet(
		@RequestParam(required=false) boolean resend, 
		@RequestParam(required=false) boolean delete, 
		Model model, 
		Principal principal
	) {
    	
		if ( delete == true ) {
			accountRepository.delete(principal.getName());
			SecurityContextHolder.clearContext();
	    	return "redirect:/";
		}
		model.addAttribute("viewModelBuilder", viewModelBuilder);
		Account account = accountRepository.findByEmail(principal.getName());    
		model.addAttribute("account", account);
		model.addAttribute("titleMatrix", titleMatrix);
		if ( resend == true ) {
			verifyEmailService.verify(account);
//			updateEmailService.sendUpdateEmails();
	        model.addAttribute(MESSAGE_ATTRIBUTE, new Message("verify.resent", Message.Type.INFO));
		}
		return "profile/profile";
	}
    
    @RequestMapping(value={"profile"}, method={RequestMethod.POST})
	public String doPost( 
		@ModelAttribute Account account,
		BindingResult result,
		@RequestParam String submit, 
		Principal principal 
//		final RedirectAttributes redirectAttributes 
	) {
    	// will need to do a database save before the redirect
		Account cAccount = accountRepository.findByEmail(principal.getName());
		cAccount.setCodes(account.getCodes());
    	accountRepository.merge(cAccount);
//    	redirectAttributes.addFlashAttribute("account", cAccount);
    	return "redirect:/";
    }
    
}
