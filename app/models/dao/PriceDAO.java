package models.dao;

import models.Price;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by octavian.salcianu on 8/9/2016.
 */
public class PriceDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public PriceDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public List<Price> getPricesByProductId(Long id) {
        CriteriaQuery<Price> criteriaQuery = this.criteriaBuilder.createQuery(Price.class);
        Root<Price> root = criteriaQuery.from(Price.class);

        criteriaQuery.select(root);
        criteriaQuery.where(this.criteriaBuilder.equal(root.get("product").get("id"), id));

        Query finalQuery = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<Price> prices = finalQuery.getResultList();
        return prices;
    }
}
