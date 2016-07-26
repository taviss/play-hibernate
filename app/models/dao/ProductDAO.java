package models.dao;

import models.*;
import models.Product;
import play.Logger;
import play.data.Form;
import play.db.jpa.JPA;

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
	private EntityManager em;
	private CriteriaBuilder criteriaBuilder;

	public ProductDAO() {
		this.em = JPA.em();
		this.criteriaBuilder = em.getCriteriaBuilder();
	}

	public void create(String productName, String linkAddress){
		Product product = new Product();
		SiteDAO siteDAO = new SiteDAO();
		product.setId(null);
		product.setProdName(productName);
		product.setLinkAddress(linkAddress);
		product.setSite(siteDAO.getSiteByURL(linkAddress.split("/")[0]));
		em.persist(product);

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
		EntityManager emRemove = JPA.em();
		emRemove.remove(product);
	}

	/* Current product name obtained from the endpoint;
	 * New values obtained from postman form */
	public void update(String name){
		SiteDAO siteDAO = new SiteDAO();
		Product product = new Product();
		Product newProduct =  new Product();
		ProductDAO productDAO = new ProductDAO();
		KeywordDAO keywordDAO =  new KeywordDAO();
		/* Store current fields in order to use them if form fields are null */
		String currentName = name;
		String currentLink = productDAO.getProduct(name).getLinkAddress();
		product = productDAO.getProduct(name);
		/* Get new data from form */
		Form<Product> form = Form.form(Product.class).bindFromRequest();
		newProduct = form.get();

		if(newProduct.getProdName() != null){
			product.setProdName(newProduct.getProdName());
		}
		if(newProduct.getProdName() != null){
			product.setLinkAddress(newProduct.getLinkAddress());
			product.setSite(siteDAO.getSiteByURL(product.getLinkAddress().split("/")[0]));
		}
//		keywordDAO.update(product);
	}

	public Product getProduct(String name){
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		criteriaQuery.where(this.criteriaBuilder.equal(root.get("prodName"), name));
		Query finalQuery = this.em.createQuery(criteriaQuery);
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
        Query query = this.em.createQuery(criteriaQuery);
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
