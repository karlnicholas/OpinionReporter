package op.web.forms;

import java.util.Locale;

import op.model.Account;

import org.hibernate.validator.constraints.*;

public class ResetPasswordForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

    @NotBlank(message = ResetPasswordForm.NOT_BLANK_MESSAGE)
	@Email(message = ResetPasswordForm.EMAIL_MESSAGE)
	private String email;

    @NotBlank(message = ResetPasswordForm.NOT_BLANK_MESSAGE)
	private String password;
    
    private boolean emailUpdates = true;

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEmailUpdates() {
		return emailUpdates;
	}

	public void setEmailUpdates(boolean emailUpdates) {
		this.emailUpdates = emailUpdates;
	}

	public Account createAccount(Locale locale) {
        return new Account(getEmail(), isEmailUpdates(), getPassword(), locale, "ROLE_USER");
	}

}
