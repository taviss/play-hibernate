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
		Map<Category, Integer> counter = new HashMap<>();
		List<Category> cats = getAllCategories();
		String keyword;
		String category;
		String[] scategory;
		for(Category c : cats){
			for(Keyword k : keywords){
				category = c.getCatName();
				keyword = k.getKeyword();
				if(category.contains(" ")){
					scategory = category.split(" ");
					for(String s : scategory){
						if(keyword.equalsIgnoreCase(s)){
							int count = counter.containsKey(c) ? counter.get(c) : 0;
							counter.put(c, count + 1);
						}
					}
				}
			}
		}

		List<Integer> max = Lists.newArrayList(counter.values());
		Collections.sort(max);
		Map<Integer, Category> inversed = new HashMap<Integer, Category>();
		Iterator it = counter.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Category, Integer> pair = (Map.Entry)it.next();
			if(!inversed.containsKey(pair.getValue()))
				inversed.put(pair.getValue(), pair.getKey());
		}
//		Map.Entry<Category, Integer> max = null;
//		for(Map.Entry<Category, Integer> entry : counter.entrySet()){
//			if(entry.getValue() > max.getValue())
//			{
//				test.add(entry.getKey().toString());
//				max = entry;
//			}
//		}
		return inversed.get(max);
	}
}
