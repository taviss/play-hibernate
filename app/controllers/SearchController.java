package controllers;

import com.google.inject.Inject;
import models.Product;
import models.SearchHistory;
import models.User;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.SearchHistoryDAO;
import models.dao.UserDAO;
import play.Logger;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.*;

import static play.mvc.Controller.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

/**
 * Created by octavian.salcianu on 7/27/2016.
 */
public class SearchController {
    @Inject
    private ProductDAO productDAO;

    @Inject
    private UserDAO userDAO;

    @Inject
    private SearchHistoryDAO searchHistoryDAO;

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result trySearch(String productName) {
        //DynamicForm requestData = Form.form().bindFromRequest();
        //String productName = requestData.get("productName");
        Set<Map.Entry<String,String[]>> queryString = request().queryString().entrySet();
        Set<Product> products = productDAO.findProductsByName(productName, queryString);

        try {
            User user = userDAO.getUserByName(Http.Context.current().request().username());
            List<SearchHistory> searchHistoryList = searchHistoryDAO.getUserSearchHistory(user.getId());
            //Replace the oldest history row if there are 10 rows already or insert a new one otherwise
            if(searchHistoryList.size() == 10) {
                Collections.sort(searchHistoryList, new Comparator<SearchHistory>() {
                    @Override
                    public int compare(SearchHistory x, SearchHistory y) {
                        return x.getInputDate().compareTo(y.getInputDate());
                    }
                });
                SearchHistory searchHistory = searchHistoryList.get(0);
                searchHistory.setQueryString(productName);
                searchHistory.setInputDate(new Date());
                searchHistoryDAO.update(searchHistory);
            } else {
                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setQueryString(productName);
                searchHistory.setInputDate(new Date());
                searchHistory.setUser(user);
                searchHistoryDAO.create(searchHistory);
            }
        } catch (NullPointerException e) {
            Logger.error("Error while inserting search history(" + request().username() + "[" + request().remoteAddress() + "]) " + e.getMessage());
        }

        if (products.isEmpty()) return notFound();
        else return ok(Json.toJson(products));
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result searchHistory() {
        List<SearchHistory> searchHistory = null;
        try {
            User user = userDAO.getUserByName(Http.Context.current().request().username());
            searchHistory = searchHistoryDAO.getUserSearchHistory(user.getId());
        } catch (NullPointerException e) {
            Logger.error("Error while fetching search history(" + request().username() + "[" + request().remoteAddress() + "]) " + e.getMessage());
        }

        if(searchHistory != null) {
            if (searchHistory.isEmpty()) return notFound("No history yet");
            else return ok(Json.toJson(searchHistory));
        } else return badRequest("Session error");
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result getUserSearchHistory(Long id) {
        if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        User user = userDAO.get(id);
        try {
            List<SearchHistory> searchHistory = searchHistoryDAO.getUserSearchHistory(user.getId());

            if (searchHistory.isEmpty()) return notFound("No history yet");
            else return ok(Json.toJson(searchHistory));
        } catch (NullPointerException e) {
            return notFound("No such user");
        }
    }
}
