package op.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


@SuppressWarnings("serial")
@Table(name="account")
@Entity
@NamedQueries({
	@NamedQuery(name = Account.FIND_BY_EMAIL, query = "select a from Account a where a.email = :email"), 
	@NamedQuery(name = Account.FIND_UNVERIFIED, query = "select a from Account a where a.verified = false and a.verifyErrors <= 3 and a.verifyCount <= 5"), 
})

public class Account implements java.io.Serializable {

	public static final String FIND_BY_EMAIL = "Account.findByEmail";
	public static final String FIND_UNVERIFIED = "Account.findUnverified";
//	public static final String FIND_EMAILUPDATES = "Account.findEmailUpdates";

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String email;
	private boolean emailUpdates;
	private boolean verified;
    private String verifyKey;
    private int verifyErrors;
    private int verifyCount;
    @Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
    @Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
    private Locale locale;
	
	@JsonIgnore
	private String password;

	private String[] codes;

	private String role = "ROLE_USER";

    protected Account() {}

    /**
     * Create account constructor 
     * 
     * @param email
     * @param emailUpdates
     * @param password
     * @param role
     */
	public Account(String email, boolean emailUpdates, String password, Locale locale, String role) {
		this.email = email;
		this.emailUpdates = emailUpdates;
		this.password = password;
		this.locale = locale;
		this.role = role;
		//
		this.verified = false;
		this.verifyKey = UUID.randomUUID().toString();
		this.createDate = new Date();
		Calendar firstDay = Calendar.getInstance();
		int year = firstDay.get(Calendar.YEAR);
		int dayOfYear = firstDay.get(Calendar.DAY_OF_YEAR);
		dayOfYear = dayOfYear - 4;
		if ( dayOfYear < 1 ) {
			year = year - 1;
			dayOfYear = 365 + dayOfYear;
		}
		firstDay.set(Calendar.YEAR, year);
		firstDay.set(Calendar.DAY_OF_YEAR, dayOfYear);
		this.updateDate = firstDay.getTime();
		this.verifyErrors = 0;
	}

	public Long getId() {
		return id;
	}

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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String[] getCodes() {
		return codes;
	}

	public void setCodes(String[] codes) {
		this.codes = codes;
	}

	public boolean isEmailUpdates() {
		return emailUpdates;
	}

	public void setEmailUpdates(boolean emailUpdates) {
		this.emailUpdates = emailUpdates;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getVerifyKey() {
		return verifyKey;
	}

	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public int getVerifyErrors() {
		return verifyErrors;
	}

	public void setVerifyErrors(int verifyErrors) {
		this.verifyErrors = verifyErrors;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getVerifyCount() {
		return verifyCount;
	}

	public void setVerifyCount(int verifyCount) {
		this.verifyCount = verifyCount;
	}
}
