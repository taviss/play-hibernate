package controllers;

import forms.ProductForm;
import forms.ProductUpdateForm;
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
	public Result deleteProduct(Long id){
		if(false){
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
	public Result updateProduct(){
		if(false){
			return ok("Thou art not admin!");
		} else {
			try{
				Form<ProductUpdateForm> form = Form.form(ProductUpdateForm.class).bindFromRequest();
				/* Retrieves product to update */
				Product product = productDAO.getProduct(form.get().product);
				if((form.get().productName != null) && (form.get().linkAddress != null)){
					productDAO.updateAll(product, form.get().productName, form.get().linkAddress);
					return ok("Product's fields updated");
				}
				if(form.get().productName != null){
					productDAO.updateName(product, form.get().productName);
					return ok("Product's name updated");
				}
				if(form.get().linkAddress != null){
					productDAO.updateLink(product, form.get().linkAddress);
					return ok("Product's link updated");
				}
			} catch(IllegalStateException e){
				return ok("Must give actual product name! How you think old chinese man finds product to update???");
			}
			return ok("You give old chinese man no new data. Bye!");
		}
	}
}