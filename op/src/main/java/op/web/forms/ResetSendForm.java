package op.web.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class ResetSendForm {
	
	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

    @NotBlank(message = ResetSendForm.NOT_BLANK_MESSAGE)
	@Email(message = ResetSendForm.EMAIL_MESSAGE)
	private String email;

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
