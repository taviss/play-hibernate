package controllers;

import models.Product;
import models.dao.ProductDAO;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.Map;
import java.util.Set;

/**
 * Created by octavian.salcianu on 7/11/2016.
 */
public class UserController extends Controller {

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result trySearch(String productName) {
        //DynamicForm requestData = Form.form().bindFromRequest();
        //String productName = requestData.get("productName");
        ProductDAO pd = new ProductDAO();
        Set<Map.Entry<String,String[]>> queryString = request().queryString().entrySet();
        Set<Product> products = pd.findProductsByName(productName, queryString);
        if(products.isEmpty()) return notFound();
        else return ok(Json.toJson(products));
    }
    /**
     * Start indexing w/ threads?
     */
}