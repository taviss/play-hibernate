package models.dao;

import models.Keyword;
import models.Price;
import models.Product;
import play.Logger;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
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