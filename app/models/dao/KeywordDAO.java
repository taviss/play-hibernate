package models.dao;

import models.Keyword;
import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashSet;
import java.util.Set;

public class KeywordDAO {
	private EntityManager em;
	private CriteriaBuilder criteriaBuilder;

	public KeywordDAO() {
		this.em = JPA.em();
		this.criteriaBuilder = em.getCriteriaBuilder();
	}

	public Keyword create(Keyword k, Product p, String keyword){
		ProductDAO pd = new ProductDAO();
		k.setId(null);
		k.setProduct(p);
		k.setKeyword(keyword);
		em.persist(k);
		return k;
	}


}
