package controllers;

import forms.ProductForm;
import forms.ProductUpdateForm;
import models.Keyword;
import models.Product;
import models.Site;
import models.dao.KeywordDAO;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductController extends Controller {
	@Inject
	private ProductDAO productDAO;

	@Inject
	private KeywordDAO keywordDAO;

	@Inject
	private SiteDAO siteDAO;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct() {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return ok("Not enough admin rights");
		} else {
			Form<Product> form = Form.form(Product.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest("Invalid form");
			}
			String smth = form.get().getLinkAddress().split("/")[0];
			Product product = new Product();
			if((form.get().getProdName()) != null &&
					(form.get().getLinkAddress() != null) &&
					(smth != null)){
				product = form.get();
			} else {
				return badRequest("Invalid form");
			}
			Site site = siteDAO.getSiteByURL(product.getLinkAddress().split("/")[0]);
			if (site != null) {
				product.setSite(site);
			} else {
				return badRequest("No such site");
			}
			productDAO.create(product);
			return ok("Added");
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteProduct(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return ok("Thou art not admin!");
		} else {
			Product product = productDAO.get(id);
			if(product ==  null){
				return notFound("No such product");
			} else{
				productDAO.softDelete(product);
				return ok("Deleted");
			}
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result updateProduct(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return ok("Thou art not admin!");
		} else {
			Form<Product> form = Form.form(Product.class).bindFromRequest();
			if (form.hasErrors()) {
				return badRequest("Invalid form");
			}
			Product product = new Product();
			if(id != null){
				product = productDAO.get(id);
			} else{
				badRequest("Pls provide ID!!!");
			}

			if (product == null) {
				return notFound("User doesn't exist");
			} else {
				Product formProduct = form.get();
				if(formProduct != null){
					productDAO.update(formProduct);
					return ok("Success");
				} else{
					return badRequest("I was given null object");
				}
			}
		}
	}
}