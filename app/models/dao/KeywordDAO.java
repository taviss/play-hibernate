package models.dao;

import models.Keyword;
import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

public class KeywordDAO {
	private EntityManager emKW;
	private CriteriaBuilder criteriaBuilder;

	public KeywordDAO() {
		this.emKW = JPA.em();
		this.criteriaBuilder = emKW.getCriteriaBuilder();
	}

	public void create(Keyword k, Product p, String keyword){
		k.setId(null);
		k.setProduct(p);
		k.setKeyword(keyword);
		emKW.persist(k);
	}

	public void delete(Product product){
		CriteriaDelete<Keyword> deleteQ = this.emKW.getCriteriaBuilder().createCriteriaDelete(Keyword.class);
		Root<Keyword> root = deleteQ.from(Keyword.class);

		deleteQ.where(this.criteriaBuilder.equal(root.get("product"), product.getId()));
		this.emKW.createQuery(deleteQ).executeUpdate();
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
	}

	public List<Keyword> getProductExistingKeywords(String productName){
		ProductDAO productDAO = new ProductDAO();
		Product product = new Product();
		product = productDAO.getProduct(productName);
		CriteriaQuery<Keyword> query = this.criteriaBuilder.createQuery(Keyword.class);
		Root<Keyword> p = query.from(Keyword.class);
		query.select(p);
		query.where(this.criteriaBuilder.equal(p.get("product"), product.getId()));
		Query finalQuery = this.emKW.createQuery(query);
		List<Keyword> keywords =  finalQuery.getResultList();
		return keywords;
	}
}
