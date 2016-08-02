package models.dao;

import models.Site;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
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
    public void create(Site site) {
        site.setId(null);
        em.persist(site);
    }
	/* Returns only the first site found with the keyword passed as argument. */
    public Site getSiteByKeyword(String keyword){
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

	public Site getSiteByURL(String url){
		CriteriaQuery<Site> criteriaQuery = this.criteriaBuilder.createQuery(Site.class);
		Root<Site> root = criteriaQuery.from(Site.class);
		criteriaQuery.select(root);
		Predicate keywordp = this.criteriaBuilder.equal(root.get("siteURL"), url);
		criteriaQuery.where(keywordp);
		Query finalQuery = this.em.createQuery(criteriaQuery);
		List<Site> sites = (List<Site>) finalQuery.getResultList();
		if (sites.isEmpty()) return null;
		else if (sites.size() == 1) return sites.get(0);
		else return null;
	}

	public Site get(Long id){ return em.find(Site.class, id);}

	/* Delete site identified by its keyword(which should be unique). */
	public void delete(Site site) {
		em.remove(site);
	}

	public void update(Site site){
		em.merge(site);
	}

    /**
     * TBA: More stuff here
     */
}
