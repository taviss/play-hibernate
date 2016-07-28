package models.dao;

import models.*;
import models.Product;
import play.Logger;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductDAO {
	private EntityManager emPD;
	private CriteriaBuilder criteriaBuilder;

	public ProductDAO() {
		this.emPD = JPA.em();
		this.criteriaBuilder = emPD.getCriteriaBuilder();
	}

	public void create(Product product){
		/* Adding keywords for the product that was created */
		KeywordDAO kd = new KeywordDAO();
		Keyword k = new Keyword();
		String[] kw = kd.keywordsFromProductURL(product);
		for(String s : kw){
			kd.create(k, product, s);
		}
		emPD.persist(product);
	}

	/* Delete product identified by its full name(which should be unique). */
	public void delete(Product product){
		emPD.remove(product);
	}

	public Product get(Long id) {
		return emPD.find(Product.class, id);
	}

	public void softDelete(Product product) {
		product.setDeleted(true);
		emPD.merge(product);
	}

	public void update(Product product){
		emPD.merge(product);
	}

	public Product getProductByName(String name){
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		criteriaQuery.where(this.criteriaBuilder.equal(root.get("prodName"), name));
		Query finalQuery = this.emPD.createQuery(criteriaQuery);
		List<Product> products = finalQuery.getResultList();
		if(products.isEmpty()){
			return null;
		} else{
			return products.get(0);
		}
	}


    public Set<Product> findProductsByName(String productName, Set<Map.Entry<String, String[]>> queryString) {
        CriteriaQuery<Keyword> criteriaQuery = this.criteriaBuilder.createQuery(Keyword.class);
        Root<Keyword> keywordRoot = criteriaQuery.from(Keyword.class);


        criteriaQuery.select(keywordRoot);
        Predicate prodNameP = this.criteriaBuilder.like(keywordRoot.get("keyword"), "%"+productName+"%");

        criteriaQuery.where(prodNameP);
        Query query = this.emPD.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<Keyword> foundKeywords = (List<Keyword>) query.getResultList();

        Set<Product> foundProducts = new HashSet<>();

        foundProducts.addAll(foundKeywords.stream().map(Keyword::getProduct).collect(Collectors.toSet()));
        for (Map.Entry<String,String[]> entry : queryString) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            Float val = Float.parseFloat(value[0]);
            switch (key) {
                case "min-price": {
                    //foundProducts.forEach(p -> Logger.info(p.getPrice().getValue().toString()));
                    foundProducts = foundProducts.stream().filter(p -> p.getPrice().getValue() > val).collect(Collectors.toSet());
                    //Logger.info("Filtered " + foundProducts);
                    break;
                }

                case "max-price": {
                    foundProducts = foundProducts.stream().filter(p -> p.getPrice().getValue() < val).collect(Collectors.toSet());
                    break;
                }

                default:
                    break;
            }
        }
        Logger.info(foundKeywords.toString());
        return foundProducts;//empty check in controller
    }
}
