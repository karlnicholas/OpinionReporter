package op.repositories;

import java.util.List;

import javax.persistence.*;
import javax.inject.Inject;

import op.model.Account;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Repository
public class AccountRepository {
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Transactional
	public Account encodeAndSave(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		em.persist(account);
		return account;
	}
	
	@Transactional
	public Account merge(Account account) {
		return em.merge(account);
	}
	
	@Transactional
	public Account updatePassword(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		return em.merge(account);
	}
	
	@Transactional
	public void delete(String email) {
		em.remove( 
			em.createNamedQuery(Account.FIND_BY_EMAIL, Account.class)
			.setParameter("email", email)
			.getSingleResult() 
		);
	}
	
	@Transactional(readOnly=true)
	public Account findByEmail(String email) {
		try {
			return em.createNamedQuery(Account.FIND_BY_EMAIL, Account.class)
				.setParameter("email", email)
				.getSingleResult();
		} catch (PersistenceException e) {
			return null;
		}
	}

	@Transactional(readOnly=true)
	public List<Account> findAllUnverified() {
		try {
			return em.createNamedQuery(Account.FIND_UNVERIFIED, Account.class).getResultList();
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<Account> findEmailUpdates() {
		try {
			return em.createNativeQuery("select * from account a where a.emailUpdates = true and a.verified = true and a.updateDate < NOW() - INTERVAL '4 days' order by a.updateDate asc limit 200", Account.class).getResultList();
		} catch (PersistenceException e) {
			return null;
		}
	}
}
