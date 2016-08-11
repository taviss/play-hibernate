package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.admin.UserRoles;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Security;
import play.mvc.Controller;
import models.Site;
import models.dao.SiteDAO;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.URLFixer;

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
			return forbidden("Thou art not admin!");
		}
		JsonNode json = request().body().asJson();
		Form<Site> form = formFactory.form(Site.class).bind(json);

		if (form.hasErrors()) {
			return badRequest("Invalid form");
		}

		if(siteDAO.getSiteByURL(URLFixer.fixURL(form.get().getSiteURL())) != null) {
			return badRequest("Website already exists");
		}

		Site site = Json.fromJson(json, Site.class);
		site.setSiteURL(URLFixer.fixURL(site.getSiteURL()));
		siteDAO.create(site);
		return ok("Site added: " + site.getSiteURL());
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	/* RollbackException if site has products assigned. */
	public Result deleteSite(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return forbidden("Thou art not admin!");
		}
		Site s = siteDAO.get(id);
		if(s == null){
			return notFound("Site doesn't exist");
		}
		siteDAO.delete(s);
		return ok("Site deleted: " + s.getSiteURL());
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	/* If someone ever needs to update a site, here you go...
	*  RollbackException if site has products assigned. */
	public Result updateSite(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return forbidden("Thou art not admin!");
		} else{
			Site s = siteDAO.get(id);
			if(s == null){
				return notFound("Site doesn't exist");
			} else{
				JsonNode json = request().body().asJson();
				Form<Site> form = formFactory.form(Site.class).bind(json);
				if(form.hasErrors()){
					return badRequest("Invalid form");
				} else{
					s.setSiteURL(form.get().getSiteURL());
					siteDAO.update(s);
					return ok("Updated");
				}
			}
		}
	}
}
