package controllers;

import models.Product;
import models.User;
import models.dao.ProductDAO;
import models.dao.UserDAO;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.user;

import java.util.List;

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
        List<Product> products = pd.findProductsByName(productName);
        if(products.isEmpty()) return notFound();
        else return ok(Json.toJson(products));
    }
    /**
     * Start indexing w/ threads?
     */
}