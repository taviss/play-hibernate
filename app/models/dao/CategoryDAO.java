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
		cat.setId(null);
		em.persist(cat);
	}

	public void delete(Category cat){
		em.remove(cat);
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

//	public Category determineCategory(Set<Keyword> keywords){
//		List<Category> cats = getAllCategories();
//		Map<Integer, Category> catScore = new HashMap<>();
//		Map<Integer, Category> maxScore = new HashMap<>();
//		Set<Keyword> kw = new LinkedHashSet<>(keywords);
//		String keyword;
//		String category;
//		Object[] bestMatch = new Object[2];
//		int count;
//		int max = Integer.MIN_VALUE;
//		for(Category c : cats){
//			count = 0;
//			for(Keyword k : kw){
//				category = c.getCatName();
//				keyword = k.getKeyword();
//				if(category.toLowerCase().contains(keyword.toLowerCase())) {
//					count++;
//				}
//
//				/* Populate map */
//				catScore.put(count, c);
//			}
	public Category determineCategory(String[] keywords){
		List<Category> cats = getAllCategories();
		String category;
//		Object[] bestMatch = new Object[2];
//		int count;
//		int max = Integer.MIN_VALUE;
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

//			if(count > max){
//				max = count;
//				bestMatch[0] = c.getCatName();
//				bestMatch[1] = max;
//			}

//			Map.Entry<Integer, Category> maxEntry = null;
//
//			/* Get one entry with max key(which is number of keyword matches to the Category stored in value) */
//			for (Map.Entry<Integer, Category> entry : catScore.entrySet()){
//				if (maxEntry == null || entry.getKey().compareTo(maxEntry.getKey()) > 0){
//					maxEntry = entry;
//				}
//			}
//
//			/* Get all entries with max key */
//			for (Map.Entry<Integer, Category> entry : catScore.entrySet()) {
//				if (entry.getKey().compareTo(maxEntry.getKey()) == 0) {
//					maxScore.put(entry.getKey(), entry.getValue());
//				}
//			}
		return getCategoryByName("Unknown");
	}
}
