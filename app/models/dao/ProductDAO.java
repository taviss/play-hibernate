package models.dao;

import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;

import play.db.jpa.JPA;

import java.util.List;

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

	public Product getProductByName(String keyword){
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate keywordp = this.criteriaBuilder.equal(root.get("prodName"), keyword);
		criteriaQuery.where(keywordp);
		Query finalQuery = this.em.createQuery(criteriaQuery);
		List<Product> sites = (List<Product>) finalQuery.getResultList();
		if (sites.isEmpty()) return null;
		else if (sites.size() == 1) return sites.get(0);
		else return null;
	}

	/*Delete product identified by its full name(which should be unique).*/
	public void delete(String name){
//		Product p = getProductByName(name);
		CriteriaDelete<Product> deleteQuery = criteriaBuilder.createCriteriaDelete(Product.class);
		Root<Product> e = deleteQuery.from(Product.class);
		deleteQuery.where(this.criteriaBuilder.equal(e.get("prodName"), name));
		Query finalQuery = this.em.createQuery(deleteQuery);
		finalQuery.executeUpdate();
	}

	/* Updates product entries*/

	public void updateLink(String newLink, String productName){
		CriteriaUpdate<Product> updateQuery = this.criteriaBuilder.createCriteriaUpdate(Product.class);
		Root<Product> p = updateQuery.from(Product.class);
		updateQuery.set("linkAddress", newLink);
		updateQuery.where(this.criteriaBuilder.equal(p.get("prodName"), productName));
		Query finalQuery = this.em.createQuery(updateQuery);
		finalQuery.executeUpdate();
	}

	public void updateName(String newName, String productName){
		CriteriaUpdate<Product> updateQuery = this.criteriaBuilder.createCriteriaUpdate(Product.class);
		Root<Product> p = updateQuery.from(Product.class);
		updateQuery.set("prodName", newName);
		updateQuery.where(this.criteriaBuilder.equal(p.get("prodName"), productName));
		Query finalQuery = this.em.createQuery(updateQuery);
		finalQuery.executeUpdate();
	}

	public String getProductName(String nameToSearch){
		return getProductByName(nameToSearch).getProdName();
	}
}
