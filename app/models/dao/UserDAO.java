package models.dao;

import models.User;
import org.h2.api.Trigger;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by octavian.salcianu on 7/13/2016.
 * User DAO
 */
public class UserDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public UserDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public String testQuery() {
        String hql = "SELECT COUNT(*) FROM User";
        Query query = em.createQuery(hql);
        Object x = query.getSingleResult();
        return x.toString();
    }

    /**
     * Adds the newly created user to the database(luckily) and returns the new user with the new "id" from db
     * @param user : User
     * @return user : User
     */
    public User create(User user) {
        user.setId(null);
        user.setAdminLevel(0);
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        return user;
    }

    /**
     * Returns the user with "id"
     * @param id
     * @return User
     */
    public User get(Long id) {
        return this.em.find(User.class, id);
    }

    public User getUserName(String userName) {
        CriteriaQuery<User> criteriaQuery = this.criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.select(root);
        Predicate userNameP = this.criteriaBuilder.equal(root.get("userName"), userName);

        criteriaQuery.where(userNameP);
        Query query = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<User> foundUsers = (List<User>) query.getResultList();

        if (foundUsers.isEmpty()) return null;
        else if (foundUsers.size() == 1) return foundUsers.get(0);
        //TBA: throw new exception
        else return null;

        //TBA
    }
}