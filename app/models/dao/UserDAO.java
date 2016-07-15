package models.dao;

import models.User;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
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
        user.setStatus("Abc");
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
}