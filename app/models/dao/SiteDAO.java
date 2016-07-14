package models.dao;

import models.Site;
import models.User;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class SiteDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public SiteDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    /**
     * Adds the newly created site to the database(luckily) and returns the new site with the new "id" from db
     * @param site : Site
     * @return site : Site
     */
    public Site create(Site site) {
        site.setId(null);
        em.persist(site);
        return site;
    }
    /**
     * TBA: More stuff here
     */
}
