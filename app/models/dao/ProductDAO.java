package models.dao;

import models.Keyword;
import models.Product;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;

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

    public List<Product> findProductsByName(String productName, Set<Map.Entry<String, String[]>> queryString) {
        CriteriaQuery<Product> criteriaQuery = this.criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);

        criteriaQuery.select(root);
        //Join join = root.join("keywords");
        //SetJoin<Product, Keyword> keywords = root.join("keywords");
        List<Predicate> predicates=new ArrayList<>();
        /*
        for (Map.Entry<String,String[]> entry : queryString) {
            String key = entry.getKey();
            String value = Arrays.toString(entry.getValue());

            switch(key)
            {
                case "min-price": predicates.add(criteriaBuilder.greaterThan()
            }

            //TBA Price model + DAO + stuff
        }*/

        /*
        for (Keyword key : join) {
            predicates.add(criteriaBuilder.like(join.get("keyword"), productName));
        }*/
       //c.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));

        Predicate prodNameP = this.criteriaBuilder.like(root.get("prodName"), productName+"%");

        criteriaQuery.where(prodNameP);
        Query query = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<Product> foundProducts = (List<Product>) query.getResultList();
        return foundProducts;//empty check in controller
    }
}