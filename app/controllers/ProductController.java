package controllers;

import models.Keyword;
import models.Product;
import models.dao.KeywordDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;

import javax.inject.Inject;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductController extends Controller {
	@Inject
	private ProductDAO productDAO;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct(){
		if(false){
			return ok("Not enough admin rights");
		} else {
			Form<Product> form = Form.form(Product.class).bindFromRequest();
			productDAO.create(form.get().getProdName(), form.get().getLinkAddress());
			return ok("Added");
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteProduct(){
		if(false){
			return ok("Thou art not admin!");
		} else {
			Form<Product> form = Form.form(Product.class).bindFromRequest();
			productDAO.delete(productDAO.getProduct(form.get().getProdName()));
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
			productDAO.update(name);
			return ok("Product's fields updated");
		}
	}
}