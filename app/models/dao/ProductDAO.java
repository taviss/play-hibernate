package models.dao;

import models.*;
import models.Product;
import play.Logger;
import play.data.Form;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import play.db.jpa.JPA;

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

	public Product create(Product prod){
		SiteDAO sd = new SiteDAO();
		prod.setId(null);
		Form<Product> form = Form.form(Product.class).bindFromRequest();
		prod = form.get();

		/* setSite expects Site as argument, returns void(but does its magic)
		*  getSiteByURL expects String as argument, returns Site
		*  siteFromURL expects nothing as argument, returns String*/
		prod.setSite(sd.getSiteByURL(prod.siteFromURL()));
		em.persist(prod);

		/* Adding keywords for the product that was created */
		String[] kw = keywordsFromProductURL(prod);
		for(String s : kw){
			KeywordDAO kd = new KeywordDAO();
			Keyword k = new Keyword();
			kd.create(k, prod, s);
		}
		return prod;
	}

	public String[] keywordsFromProductURL(Product p){
		String URL = p.getLinkAddress();
		String[] URLsite = URL.split("/");
		String[] URLkeywords = URLsite[1].split("-");
		return URLkeywords;
	}

	public Product getProductByName(String name){
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate keywordp = this.criteriaBuilder.equal(root.get("prodName"), name);
		criteriaQuery.where(keywordp);
		Query finalQuery = this.em.createQuery(criteriaQuery);
		List<Product> prods = (List<Product>) finalQuery.getResultList();
		if (prods.isEmpty()) return null;
		else if (prods.size() == 1) return prods.get(0);
		else return null;
	}

	/* Delete product identified by its full name(which should be unique). */
	public void delete(Product p){
		Form<Product> form = Form.form(Product.class).bindFromRequest();
		p = form.get();
		CriteriaDelete<Product> deleteQuery = criteriaBuilder.createCriteriaDelete(Product.class);
		Root<Product> e = deleteQuery.from(Product.class);
		deleteQuery.where(this.criteriaBuilder.equal(e.get("prodName"), p.getProdName()));
		Query finalQuery = this.em.createQuery(deleteQuery);
		finalQuery.executeUpdate();
	}

	/* Updates product entries*/
	/* Current product name obtained from the endpoint;
	 * New values obtained from postman form */
	public void update(String name){
		Product product;
		ProductDAO productDAO = new ProductDAO();
		SiteDAO siteDAO = new SiteDAO();

		/* Store current fields in order to use them if form fields are null */
		String currentName = name;
		String currentLink = productDAO.getProductByName(name).getLinkAddress();

		/* Get new data from form */
		Form<Product> form = Form.form(Product.class).bindFromRequest();
		product = form.get();

		CriteriaUpdate<Product> updateQuery = this.criteriaBuilder.createCriteriaUpdate(Product.class);
		Root<Product> p = updateQuery.from(Product.class);
		/* If product link changed, keywords need to be updated(old ones removed, add new ones) */
		if((product.getLinkAddress() != null) && (linkUpdated(product.getLinkAddress(), currentLink))){
			updateQuery.set("linkAddress", product.getLinkAddress());
			updateQuery.set("site", siteDAO.getSiteByURL(product.siteFromURL()));
		}
		if(product.getProdName() != null){
			updateQuery.set("prodName", product.getProdName());
		}
		updateQuery.where(this.criteriaBuilder.equal(p.get("prodName"), currentName));
		Query finalQuery = this.em.createQuery(updateQuery);
		finalQuery.executeUpdate();
	}

	public boolean linkUpdated(String newLink, String oldLink){
		if(newLink.equalsIgnoreCase(oldLink))
			return false;
		else
			return true;
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
