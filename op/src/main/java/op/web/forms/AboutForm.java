package op.web.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class AboutForm {
	
	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

    @NotBlank(message = AboutForm.NOT_BLANK_MESSAGE)
	@Email(message = AboutForm.EMAIL_MESSAGE)
	private String email;
    
    @NotBlank(message = AboutForm.NOT_BLANK_MESSAGE)
    private String comments;

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	

}
