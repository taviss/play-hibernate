package models.dao;

import models.Keyword;
import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
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
		k.setId(null);
		k.setProduct(p);
		k.setKeyword(keyword);
		em.persist(k);
		return k;
	}

	public void delete(Product product){
		CriteriaDelete<Keyword> deleteQuery = this.criteriaBuilder.createCriteriaDelete(Keyword.class);
		Root<Keyword> p = deleteQuery.from(Keyword.class);
		deleteQuery.where(this.criteriaBuilder.equal(p.get("product"), product.getId()));
		Query finalQuery = this.em.createQuery(deleteQuery);
		finalQuery.executeUpdate();
		em.close();
	}

	public void update(Product product){
		delete(product);
		KeywordDAO kd = new KeywordDAO();
		Keyword k = new Keyword();
		ProductDAO pd =  new ProductDAO();
		String[] kw =  pd.keywordsFromProductURL(product);
		for(String s : kw){
			kd.create(k, product, s);
		}
//		ProductDAO productDAO = new ProductDAO();
//		CriteriaUpdate<Keyword> updateQuery = this.criteriaBuilder.createCriteriaUpdate(Keyword.class);
//		Root<Keyword> k = updateQuery.from(Keyword.class);
//		updateQuery.where(this.criteriaBuilder.equal(k.get("product"), product.getId()));

	}

	public Keyword[] getKeywordsByProduct(Product product){

	}
}
