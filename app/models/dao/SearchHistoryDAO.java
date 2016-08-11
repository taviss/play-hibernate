package models.dao;

import models.SearchHistory;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by octavian.salcianu on 8/11/2016.
 */
public class SearchHistoryDAO {
    private EntityManager em;
    private CriteriaBuilder criteriaBuilder;

    public SearchHistoryDAO() {
        this.em = JPA.em();
        this.criteriaBuilder = em.getCriteriaBuilder();
    }

    public void create(SearchHistory searchHistory){
        searchHistory.setId(null);
        em.persist(searchHistory);
    }

    public void delete(SearchHistory searchHistory){
        em.remove(searchHistory);
    }

    public SearchHistory get(Long id){
        return em.find(SearchHistory.class, id);
    }

    public List<SearchHistory> getUserSearchHistory(Long id) {
        CriteriaQuery<SearchHistory> criteriaQuery = this.criteriaBuilder.createQuery(SearchHistory.class);
        Root<SearchHistory> root = criteriaQuery.from(SearchHistory.class);

        criteriaQuery.select(root);
        criteriaQuery.where(this.criteriaBuilder.equal(root.get("user").get("id"), id));

        Query finalQuery = this.em.createQuery(criteriaQuery);
        @SuppressWarnings("unchecked")
        List<SearchHistory> history = finalQuery.getResultList();
        return history;
    }
}
