package opinions.dao.base;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

/**
 * Provides generic common implementation of GenericDao interface persistence
 * methods. Extend this abstract class to implement DAO for your specific needs.
 * 
 * @author Arthur Vin
 */
public abstract class JpaDao<T, ID extends Serializable> implements GenericDao<T, ID> {

	protected EntityManager em;

	private Class<T> persistentClass;

	public JpaDao(Class<T> persistentClass, EntityManager em) {
		this.em = em;
		this.persistentClass = persistentClass;
	}

//	public void setEntityManager(EntityManager entityManager) {
//		this.entityManager = entityManager;
//	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public T find(ID id) {
		return em.find(persistentClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> list() {
		return em.createQuery(
				"select x from " + persistentClass.getSimpleName() + " x")
				.getResultList();
	}

	public void persist(T entity) {
		em.persist(entity);
	}

	public T merge(T entity) {
		return em.merge(entity);
	}

	public void remove(T entity) {
		em.remove(em.merge(entity));
	}
/*
	public void remove(T entity) {
		em.remove(entity);
	}
*/	
	public void flush() {
		em.flush();
	}

}
