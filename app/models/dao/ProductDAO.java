package models.dao;

import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import play.db.jpa.JPA;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductDAO {
	private EntityManager em;
	private CriteriaBuilder criteriaBuilder;

	public ProductDAO() {
		this.em = JPA.em();
		this.criteriaBuilder = em.getCriteriaBuilder();
	}

	public Product create(Product prod){
		prod.setId(null);
		em.persist(prod);
		return prod;
	}

	public void delete(/* Parameters to be added after schema revamp */){
		/* Delete product identified by something given as argument */
	}
}
