package controllers;

import models.Product;
import models.Site;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import views.html.product;
import views.html.index;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class ProductController extends Controller {

	@Transactional
	public Result addProduct(){
		ProductDAO pd = new ProductDAO();
		Product p = new Product();
		SiteDAO s = new SiteDAO();
		p.setProdName("ASUS ROG stove test");
		p.setLinkAddress("emag.ro/asus-rog-something-test");
		p.setSite(s.getSiteIDByKeyword("emag"));
		p = pd.create(p);
		return ok(product.render(p.getId().toString(), p.getLinkAddress(), p.getProdName()));
	}
}