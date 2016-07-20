package controllers;

import models.Product;
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
	public Result masterProduct(String action){
		switch(action){
			case "add": return addProduct();
			default: return ok(productError.render("Unknown action"));
		}

	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct(){
		if(Secured.getAdminLevel() != 3){
			/* Could do return redirect(routes.Application.index()); */
			return ok(productError.render("Not enough admin rights"));
		} else {
			ProductDAO pd = new ProductDAO();
			Product p = new Product();
			SiteDAO s = new SiteDAO();
			p.setProdName("ASUS ROG stove test");
			p.setLinkAddress("emag.ro/asus-rog-something-test");
			p.setSite(s.getSiteByKeyword("emag"));
			p = pd.create(p);
			return ok(productAdd.render(p.getId().toString(), p.getLinkAddress(), p.getProdName()));
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result removeProduct(){
		if(Secured.getAdminLevel() != 3){
			return ok(productError.render("Thou art not admin!"));
		} else {
			ProductDAO pd = new ProductDAO();
			pd.delete("ASUS ROG stove test");
			return ok(productRemove.render("Deleted"));
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result updateProductLink(){
		if(Secured.getAdminLevel() != 3){
			return ok(productError.render("Thou art not admin!"));
		} else {
			ProductDAO pd = new ProductDAO();
			pd.updateLink("testlinklel","ASUS ROG stove test");
			return ok(productUpdate.render("Link updated for product with name ASUS ROG stove test"));
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result updateProductName(){
		if(Secured.getAdminLevel() != 3){
			return ok(productError.render("Thou art not admin!"));
		} else {
			ProductDAO pd = new ProductDAO();
			pd.updateName("testlinklel","ASUS ROG stove test");
			return ok(productUpdate.render("Name updated for product with name ASUS ROG stove test"));
		}
	}


}