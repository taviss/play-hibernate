package controllers;

import forms.SetAdminForm;
import models.Product;
import models.User;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.UserDAO;
import play.data.Form;
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

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result setAdminLevel() {
        Form<SetAdminForm> form = Form.form(SetAdminForm.class).bindFromRequest();

        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }
        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }
        UserDAO ud = new UserDAO();
        User foundUser = ud.getUserByName(form.get().userName);

        if(foundUser == null) {
            return badRequest("No such user");
        } else {
            foundUser.setAdminLevel(form.get().adminLevel);
            ud.update(foundUser);
            return ok("Success");
        }
    }
    /**
     * Start indexing w/ threads?
     */
}