package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Product;
import models.User;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Security;
import play.mvc.Controller;
import models.Site;
import models.dao.SiteDAO;
import play.db.jpa.Transactional;
import play.mvc.Result;
import controllers.Secured;

import javax.inject.Inject;


/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class SiteController extends Controller {

	@Inject
	private SiteDAO siteDAO;

	@Inject
	private FormFactory formFactory;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addSite(){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return badRequest("Thou art not admin!");
		}
		JsonNode json = request().body().asJson();
		Form<Site> form = formFactory.form(Site.class).bind(json);

		if (form.hasErrors()) {
			return badRequest("Invalid form");
		}

		Site s = new Site();
		s.setSiteURL(form.get().getSiteURL());
		s.setSiteKeyword(form.get().getSiteURL().split("[.]")[0]);

		siteDAO.create(s);
		return ok("Site added: " + s.getSiteURL());
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	/* RollbackException if site has products assigned.
	 * To fix this, uncomment member in Site.java and see ProductController.addProduct,
	 * apply the same algorithm for product setting to site as I did for keyword setting to product.*/
	public Result deleteSite(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return badRequest("Thou art not admin!");
		}
		Site s = siteDAO.get(id);
		if(s == null){
			return notFound("Site doesn't exist");
		}
		siteDAO.delete(s);
		return ok("Site deleted: " + s.getSiteURL());
	}
}
