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

	public Category determineCategory(Set<Keyword> keywords){
		List<Category> cats = getAllCategories();
		String keyword;
		String category;
		Object[] bestMatch = new Object[2];
		int count;
		int max = Integer.MIN_VALUE;
		for(Category c : cats){
			count = 0;
			for(Keyword k : keywords){
				category = c.getCatName();
				keyword = k.getKeyword();
				if(category.toLowerCase().contains(keyword.toLowerCase())) {
					count++;
				}
			}
			/* Store the category with most keyword matches and the number of matches */
			if(count > max){
				max = count;
				bestMatch[0] = c;
				bestMatch[1] = max;
			}
		}
		return (Category)bestMatch[0];
	}
}
