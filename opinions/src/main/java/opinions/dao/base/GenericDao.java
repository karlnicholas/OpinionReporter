package opinions.dao.base;

import java.io.Serializable;

import java.util.List;
 
/**
 * Generic interface for Data Access Objects. To be extended or implemented.
 * Contains common persistence methods.
 * 
 */
public interface GenericDao<T, ID extends Serializable> {
    void persist(T entity);
    T merge(T entity);
    void remove(T entity);
    T find(ID id);
    List<T> list();
    void flush();
} 
