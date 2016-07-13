package models.dao;

import models.User;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * Created by octavian.salcianu on 7/13/2016.
 */
public class UserDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public UserDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public User create(User user) {
        user.setId(null);
        user.setStatus("Test");
        em.persist(user);
        return user;
    }

    public User get(Long id) {
        return this.em.find(User.class, id);
    }
}
