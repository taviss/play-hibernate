package controllers;

import forms.ProductForm;
import forms.ProductUpdateForm;
import models.Keyword;
import models.Product;
import models.dao.KeywordDAO;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;
import java.util.logging.Logger;

import javax.inject.Inject;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductController extends Controller {
	@Inject
	private ProductDAO productDAO;
	private KeywordDAO keywordDAO;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct(){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return ok("Not enough admin rights");
		} else {
			Form<Product> form = Form.form(Product.class).bindFromRequest();
			productDAO.create(form.get().getProdName(), form.get().getLinkAddress());
			return ok("Added");
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteProduct(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return ok("Thou art not admin!");
		} else {
			Product product = productDAO.get(id);
			productDAO.softDelete(product);
			return ok("Deleted");
		}
	}

	/* name is the name of the product whose fields are to be updated*/
	@Security.Authenticated(Secured.class)
	@Transactional
	public Result updateProduct(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return ok("Thou art not admin!");
		} else {
				productDAO.update(id);
			return ok("You give old chinese man no new data. Bye!");
		}
	}
}