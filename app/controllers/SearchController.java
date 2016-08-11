package controllers;

import com.google.inject.Inject;
import models.Product;
import models.SearchHistory;
import models.User;
import models.dao.ProductDAO;
import models.dao.SearchHistoryDAO;
import models.dao.UserDAO;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import static play.mvc.Controller.request;
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
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setQueryString(productName);
        searchHistory.setInputDate(new Date());
        User user = userDAO.getUserByName(Http.Context.current().request().username());
        searchHistory.setUser(user);
        searchHistoryDAO.create(searchHistory);

        if (products.isEmpty()) return notFound();
        else return ok(Json.toJson(products));
    }
}
