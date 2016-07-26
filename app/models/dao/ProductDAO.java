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

	public void create(String productName, String linkAddress){
		Product product = new Product();
		SiteDAO siteDAO = new SiteDAO();
		product.setId(null);
		product.setProdName(productName);
		product.setLinkAddress(linkAddress);
		product.setSite(siteDAO.getSiteByURL(linkAddress.split("/")[0]));
		emPD.persist(product);

		/* Adding keywords for the product that was created */
		String[] kw = keywordsFromProductURL(product);
		for(String s : kw){
			KeywordDAO kd = new KeywordDAO();
			Keyword k = new Keyword();
			kd.create(k, product, s);
		}
	}

	/* Delete product identified by its full name(which should be unique). */
	public void delete(Product product){
		emPD.remove(product);
	}

	public void updateLink(Product p, String link){
		SiteDAO sd = new SiteDAO();
		KeywordDAO kd = new KeywordDAO();
		p.setLinkAddress(link);
		p.setSite(sd.getSiteByURL(link.split("/")[0]));
	}

	public void updateName(Product p, String name){
		p.setProdName(name);
	}

	public void updateAll(Product p, String name, String link){
		SiteDAO sd = new SiteDAO();
		p.setProdName(name);
		p.setLinkAddress(link);
		p.setSite(sd.getSiteByURL(link.split("/")[0]));
	}

	public Product getProduct(String name){
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		criteriaQuery.where(this.criteriaBuilder.equal(root.get("prodName"), name));
		Query finalQuery = this.emPD.createQuery(criteriaQuery);
		List<Product> products = finalQuery.getResultList();
		return products.get(0);
	}

	public String[] keywordsFromProductURL(Product p){
		String URL = p.getLinkAddress();
		String[] URLsite = URL.split("/");
		String[] URLkeywords = URLsite[1].split("-");
		return URLkeywords;
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
