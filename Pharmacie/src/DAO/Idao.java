package DAO;
 
import java.util.List;
 
public interface Idao<T> {
    boolean create(T o);
    boolean update(T o);
    boolean delete(String id);
    T findById(String id);
    List<T> findAll();
}
 