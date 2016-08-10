package models.dao;

import models.*;
import models.Product;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import play.Logger;
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
	private EntityManager emPD;
	private CriteriaBuilder criteriaBuilder;

	public ProductDAO() {
		this.emPD = JPA.em();
		this.criteriaBuilder = emPD.getCriteriaBuilder();
	}

	public void create(Product product){
		product.setId(null);
		product.setDeleted(false);
		emPD.persist(product);
	}

	public void delete(Product product){
		emPD.remove(product);
	}

	public Product get(Long id) {
		return emPD.find(Product.class, id);
	}

	public List<Product> getAll() {
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);

		criteriaQuery.select(root);

		Predicate deletedP = this.criteriaBuilder.notEqual(root.get("deleted"), 1);

		criteriaQuery.where(deletedP);
		Query query = this.emPD.createQuery(criteriaQuery);
		@SuppressWarnings("unchecked")
		List<Product> resultList = (List<Product>) query.getResultList();
		return resultList;
	}

	public List<Product> getProductsBySiteId(Long id) {
		CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);

		criteriaQuery.select(root);

		Predicate deletedP = this.criteriaBuilder.notEqual(root.get("deleted"), 1);
		Predicate siteP = this.criteriaBuilder.equal(root.get("site").get("id"), id);

		Predicate[] predicates = {deletedP, siteP};

		criteriaQuery.where(predicates);
		Query query = this.emPD.createQuery(criteriaQuery);
		@SuppressWarnings("unchecked")
		List<Product> resultList = (List<Product>) query.getResultList();
		return resultList;
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
		@SuppressWarnings("unchecked")
		List<Product> products = finalQuery.getResultList();
		if(products.isEmpty()){
			return null;
		} else{
			return products.get(0);
		}
	}

	public Set<Product> findProductsByName(String productName, Set<Map.Entry<String, String[]>> queryString) {
		//Fetch the matching keywords
		FullTextEntityManager fullTextEntityManager =
				org.hibernate.search.jpa.Search.getFullTextEntityManager(emPD);
		QueryBuilder qb = fullTextEntityManager.getSearchFactory()
				.buildQueryBuilder().forEntity(Keyword.class).get();
		org.apache.lucene.search.Query luceneQuery = qb
				.keyword()
				.onFields("keyword")
				.matching(productName)
				.createQuery();

		javax.persistence.Query jpaQuery =
				fullTextEntityManager.createFullTextQuery(luceneQuery, Keyword.class);

		@SuppressWarnings("unchecked")
		List<Keyword> foundKeywords = (List<Keyword>) jpaQuery.getResultList();
		//Get the corresponding products
		List<Product> foundProducts = new ArrayList<>();
		foundProducts.addAll(foundKeywords.stream().map(Keyword::getProduct).collect(Collectors.toList()));

		//Get the count for every product
		Map<Product, Integer> counter = new HashMap<>();
		for (Product prod : foundProducts) {
			counter.put(prod, 1 + (counter.containsKey(prod) ? counter.get(prod) : 0));
		}

		//Sort the product list by frequency
		List<Product> list = new ArrayList<>(counter.keySet());
		Collections.sort(list, new Comparator<Product>() {
			@Override
			public int compare(Product x, Product y) {
				return counter.get(y) - counter.get(x);
			}
		});

		//Logger.info(foundProducts.toString());

		//Convert it to set and maintain the order(LinkedHashSet)
		//!!!Not necessary
		Set<Product> sortedProducts = new HashSet<>();

		sortedProducts.addAll(list.stream().collect(Collectors.toCollection(LinkedHashSet::new)));
		for (Map.Entry<String,String[]> entry : queryString) {
			String key = entry.getKey();
			String[] value = entry.getValue();

			Float val = Float.parseFloat(value[0]);
			switch (key) {
				case "min-price": {
					//foundProducts.forEach(p -> Logger.info(p.getPrice().getValue().toString()));
					sortedProducts = sortedProducts.stream().filter(p -> p.getPrice().getValue() > val).collect(Collectors.toSet());
					//Logger.info("Filtered " + foundProducts);
					break;
				}

				case "max-price": {
					sortedProducts = sortedProducts.stream().filter(p -> p.getPrice().getValue() < val).collect(Collectors.toSet());
					break;
				}

				default:
					break;
			}
		}
		return sortedProducts;//empty check in controller
	}

	/*
    public Set<Product> findProductsByName(String productName, Set<Map.Entry<String, String[]>> queryString) {
        CriteriaQuery<Keyword> criteriaQuery = this.criteriaBuilder.createQuery(Keyword.class);
        Root<Keyword> keywordRoot = criteriaQuery.from(Keyword.class);


        criteriaQuery.select(keywordRoot);
		String[] productKeywords = productName.split(" ");
		List<Predicate> predicates = new ArrayList<>();

		for(int i = 0; i < productKeywords.length; i++) {
			Predicate prodNameP = this.criteriaBuilder.like(keywordRoot.get("keyword"), "%"+productKeywords[i]+"%");
			predicates.add(prodNameP);
		}

        criteriaQuery.where(criteriaBuilder.or(predicates.toArray(new Predicate[]{})));
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
        return foundProducts;//empty check in controller
    }
    */
}
