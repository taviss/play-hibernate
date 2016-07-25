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

	public void create(){
		Product product = new Product();
		SiteDAO siteDAO = new SiteDAO();
		product.setId(null);
		Form<Product> fieldsForm = Form.form(Product.class).bindFromRequest();
		product = fieldsForm.get();
		product.setSite(siteDAO.getSiteByURL(product.getLinkAddress().split("/")[0]));
		em.persist(product);
		/* Adding keywords for the product that was created */
//		String[] kw = keywordsFromProductURL(prod);
//		for(String s : kw){
//			KeywordDAO kd = new KeywordDAO();
//			Keyword k = new Keyword();
//			kd.create(k, prod, s);
//		}
	}

	/* Delete product identified by its full name(which should be unique). */
	public void delete(){
		ProductDAO productDAO = new ProductDAO();
		Product lookFor = new Product();
		Product product = new Product();
		Form<Product> form = Form.form(Product.class).bindFromRequest();
		lookFor = form.get();
		product = productDAO.getProduct(lookFor.getProdName());
		em.remove(product);
	}

	/* Current product name obtained from the endpoint;
	 * New values obtained from postman form */
	public void update(String name){
		SiteDAO siteDAO = new SiteDAO();
		Product product = new Product();
		Product newProduct =  new Product();
		ProductDAO productDAO = new ProductDAO();
		/* Store current fields in order to use them if form fields are null */
		String currentName = name;
		String currentLink = productDAO.getProduct(name).getLinkAddress();
		product = productDAO.getProduct(name);
		/* Get new data from form */
		Form<Product> form = Form.form(Product.class).bindFromRequest();
		newProduct = form.get();

		boolean sameName = product.getProdName().equalsIgnoreCase(newProduct.getProdName());
		boolean sameLink = product.getLinkAddress().equalsIgnoreCase(newProduct.getLinkAddress());

		if(!sameName){
			product.setProdName(newProduct.getProdName());
		}
		if(!sameLink){
			product.setLinkAddress(newProduct.getLinkAddress());
			product.setSite(siteDAO.getSiteByURL(newProduct.getLinkAddress().split("/")[0]));
		}
//		CriteriaUpdate<Product> updateQuery = this.criteriaBuilder.createCriteriaUpdate(Product.class);
//		Root<Product> p = updateQuery.from(Product.class);
//		/* If product link changed, keywords need to be updated(old ones removed, add new ones) */
//		if((product.getLinkAddress() != null) && (linkUpdated(product.getLinkAddress(), currentLink))){
//			updateQuery.set("linkAddress", product.getLinkAddress());
//			updateQuery.set("site", siteDAO.getSiteByURL(product.siteFromURL()));
//		}
//		if(product.getProdName() != null){
//			updateQuery.set("prodName", product.getProdName());
//		}
//		updateQuery.where(this.criteriaBuilder.equal(p.get("prodName"), currentName));
//		Query finalQuery = this.em.createQuery(updateQuery);
//		finalQuery.executeUpdate();
//		keywordDAO.update(productDAO.getProductByName(name));
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
//		String URL = p.getLinkAddress();
//		String[] URLsite = URL.split("/");
//		String[] URLkeywords = URLsite[1].split("-");
//		return URLkeywords;
		return null;
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
