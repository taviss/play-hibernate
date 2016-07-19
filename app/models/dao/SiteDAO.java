package models.dao;

import models.Site;
import models.User;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

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
	//No works!!!
    public Site getSiteIDByKeyword(String keyword){
	    CriteriaQuery<Site> criteriaQuery = this.criteriaBuilder.createQuery(Site.class);
	    Root<Site> root = criteriaQuery.from(Site.class);
	    criteriaQuery.select(root);
	    Predicate keywordp = this.criteriaBuilder.equal(root.get("siteKeyword"), keyword);
	    criteriaQuery.where(keywordp);
	    Query finalQuery = this.em.createQuery(criteriaQuery);
	    List<Site> sites = (List<Site>) finalQuery.getResultList();
	    if (sites.isEmpty()) return null;
	    else if (sites.size() == 1) return sites.get(0);
	    else return null;
    }

    /**
     * TBA: More stuff here
     */
}
