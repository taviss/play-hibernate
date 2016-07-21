package controllers;

import models.Keyword;
import models.Product;
import models.dao.KeywordDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;
import views.html.productAdd;
import views.html.productRemove;
import views.html.productError;
import views.html.productUpdate;
import views.html.index;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductController extends Controller {

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct(){
		if(Secured.getAdminLevel() != 3){
			/* Could do return redirect(routes.Application.index()); */
			return ok("Not enough admin rights");
		} else {
			ProductDAO pd = new ProductDAO();
			Product p = new Product();
			p = pd.create(p);
			return ok("Added");
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteProduct(){
		if(Secured.getAdminLevel() != 3){
			return ok("Thou art not admin!");
		} else {
			Product p = new Product();
			ProductDAO pd = new ProductDAO();
			pd.delete(p);
			return ok("Deleted");
		}
	}

	/* name is the name of the product whose fields are to be updated*/
	@Security.Authenticated(Secured.class)
	@Transactional
	public Result updateProduct(String name){
		if(Secured.getAdminLevel() != 3){
			return ok("Thou art not admin!");
		} else {
			ProductDAO pd = new ProductDAO();
			pd.update(name);
			return ok("Product's fields updated");
		}
	}


}