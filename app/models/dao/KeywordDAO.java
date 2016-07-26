package models.dao;

import models.Keyword;
import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeywordDAO {
	private EntityManager em;
	private CriteriaBuilder criteriaBuilder;

	public KeywordDAO() {
		this.em = JPA.em();
		this.criteriaBuilder = em.getCriteriaBuilder();
	}

	public void create(Keyword k, Product p, String keyword){
		k.setId(null);
		k.setProduct(p);
		k.setKeyword(keyword);
		em.persist(k);
	}

	public void delete(String productName){
		KeywordDAO keywordDAO =  new KeywordDAO();
		List<Keyword> list = keywordDAO.getProductExistingKeywords(productName);
		for(Keyword k : list){
			em.remove(k);
		}
	}

	public void update(Product product){
		delete(product.getProdName());
		KeywordDAO kd = new KeywordDAO();
		Keyword k = new Keyword();
		ProductDAO pd =  new ProductDAO();
		String[] kw =  pd.keywordsFromProductURL(product);
		for(String s : kw){
			kd.create(k, product, s);
		}
	}

	public List<Keyword> getProductExistingKeywords(String productName){
		ProductDAO productDAO = new ProductDAO();
		Product product = new Product();
		product = productDAO.getProduct(productName);
		CriteriaQuery<Keyword> query = this.criteriaBuilder.createQuery(Keyword.class);
		Root<Keyword> p = query.from(Keyword.class);
		query.select(p);
		query.where(this.criteriaBuilder.equal(p.get("product"), product.getId()));
		Query finalQuery = this.em.createQuery(query);
//		finalQuery.executeUpdate();
		List<Keyword> keywords =  finalQuery.getResultList();
		return keywords;
	}
}
