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

import java.util.*;
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

			if((form.get().getProdName()) != null && (form.get().getLinkAddress() != null) && (smth != null)){
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
			/* I'm aware of this code duplication, will be fixed after release */
			String URL = product.getLinkAddress();
			String[] URLsite = URL.split("/");
			String[] URLkeywords = URLsite[1].split("-");
			Set<Keyword> kk = new HashSet<>();
			for(String s : URLkeywords){
				Keyword tibi = new Keyword();
				tibi.setId(null);
				tibi.setProduct(product);
				tibi.setKeyword(s);
				keywordDAO.create(tibi);
				kk.add(tibi);
			}
			product.setKeywords(kk);
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
				/* Hard delete */
				productDAO.delete(product);

				/* Soft delete */
//				productDAO.softDelete(product);
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
				return ok("Invalid form");
			}
			Product current = new Product();
			Product newP = new Product();
			if(id != null){
				current = productDAO.get(id);
			} else{
				ok("Pls provide ID!!!");
			}
			if (current == null) {
				return ok("Product doesn't exist");
			} else {
				newP = form.get();
				if(newP.getLinkAddress().equalsIgnoreCase(current.getLinkAddress())){
					productDAO.update(current);
				} else {
					productDAO.delete(current);
					/* I'm aware of this code duplication, will be fixed after release */
					String URL = newP.getLinkAddress();
					String[] URLsite = URL.split("/");
					String[] URLkeywords = URLsite[1].split("-");
					Set<Keyword> kk = new HashSet<>();
					for(String s : URLkeywords){
						Keyword tibi = new Keyword();
						tibi.setId(null);
						tibi.setProduct(newP);
						tibi.setKeyword(s);
						keywordDAO.create(tibi);
						kk.add(tibi);
					}
					newP.setKeywords(kk);
					productDAO.create(newP);
				}
				return ok("Success");
			}
		}
	}
}