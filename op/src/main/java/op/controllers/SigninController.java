package op.controllers;

import op.web.viewmodel.ViewModelBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SigninController {

	@Autowired
    private ViewModelBuilder viewModelBuilder;	

	@RequestMapping(value = "signin")
	public String signin(Model model) {
		model.addAttribute("viewModelBuilder", viewModelBuilder);
        return "signin/signin";
    }
}
