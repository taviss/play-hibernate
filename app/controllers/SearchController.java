package controllers;

import com.google.inject.Inject;
import models.Product;
import models.dao.ProductDAO;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

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
    private ProductDAO pd;

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result trySearch(String productName) {
        //DynamicForm requestData = Form.form().bindFromRequest();
        //String productName = requestData.get("productName");
        Set<Map.Entry<String,String[]>> queryString = request().queryString().entrySet();
        Set<Product> products = pd.findProductsByName(productName, queryString);
        if(products.isEmpty()) return notFound();
        else return ok(Json.toJson(products));
    }
}
