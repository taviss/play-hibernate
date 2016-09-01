package models.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import models.Category;
import models.Keyword;
import models.Product;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryDAO {
	private EntityManager em;
	private CriteriaBuilder criteriaBuilder;

	public CategoryDAO() {
		this.em = JPA.em();
		this.criteriaBuilder = em.getCriteriaBuilder();
	}

	public void create(Category cat){
		cat.setDeleted(false);
		cat.setId(null);
		em.persist(cat);
	}

	public void delete(Category cat){
		em.remove(cat);
	}

	public void softDelete(Category cat){
		cat.setDeleted(true);
	}

	public Category get(Long id){
		return em.find(Category.class, id);
	}

	public List<Category> getAllCategories(){
		CriteriaQuery<Category> criteriaQuery = this.criteriaBuilder.createQuery(Category.class);
		Root<Category> root = criteriaQuery.from(Category.class);
		criteriaQuery.select(root);
		Query finalQuery = this.em.createQuery(criteriaQuery);
		@SuppressWarnings("unchecked")
		List<Category> categories = finalQuery.getResultList();
		if(categories.isEmpty()){
			return null;
		} else{
			return categories;
		}
	}

	public Category getCategoryByName(String name){
		CriteriaQuery<Category> criteriaQuery = this.criteriaBuilder.createQuery(Category.class);
		Root<Category> root = criteriaQuery.from(Category.class);
		criteriaQuery.select(root);
		criteriaQuery.where(this.criteriaBuilder.equal(root.get("catName"), name));
		Query finalQuery = this.em.createQuery(criteriaQuery);
		@SuppressWarnings("unchecked")
		List<Category> categories = finalQuery.getResultList();
		if(categories.isEmpty()){
			return null;
		} else{
			return categories.get(0);
		}
	}
	public  Category determineCategory(String[] keywords){
		List<Category> cats = getAllCategories();
		String category;

		for(String k : keywords){
			if(k.toLowerCase().contains("telefon") || k.toLowerCase().contains("mobil"))
				return getCategoryByName("Smartphone");
		}
		for(String k : keywords) {
			for (Category c : cats) {
				category = c.getCatName();
				if (category.toLowerCase().contains(k.toLowerCase())) {
					return c;
				}
			}
		}
		return getCategoryByName("Unknown");
	}


}
