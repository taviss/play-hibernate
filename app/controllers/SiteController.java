package controllers;

import models.User;
import play.mvc.Security;
import play.mvc.Controller;
import models.Site;
import models.dao.SiteDAO;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.site;


/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class SiteController extends Controller {

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addSite(){
		/*adminLevel 3 means...for now...this user has enough privileges to add websites to the database.
		* Using random values for testing purpose.*/
		if(Secured.getAdminLevel() != 3){
			return ok(site.render(null, "Thou art not admin!"));
		}
		SiteDAO sd = new SiteDAO();
		Site s = new Site();
		s.setSiteURL("emag.ro/test");
		s.setSiteKeyword("emag");
		s = sd.create(s);
		return ok(site.render(s.getSiteURL(), "Thou art admin!"));
	}

	public Result removeSite(){
		if(Secured.getAdminLevel() != 3){
			return ok(site.render(null, "Thou art not admin!"));
		} else{
			return ok(site.render(null, "lalalalllla"));
		}
	}
}
