package op.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This class exists so that an email can be sent with url parameters which will be
 * passed along after the user is forced to signin.
 * 
 */

@Controller
public class UserOpinionsController {
	@RequestMapping(value = {"useropinions"}, method = RequestMethod.GET)
	public String redirect() {
		return "forward:opinions";
	}
}
