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
		User u = User.find.byId(request().username());
		if(u.getAdminLevel() != 3){
			return ok("You're not admin enough for this!");
		}
		else{
			SiteDAO sd = new SiteDAO();
			Site s = new Site();
			s.setSiteURL("emag.ro/test");
			s.setSiteKeyword("keyword");
			s = sd.create(s);
			return ok(site.render(s.getSiteURL(), "Thou art admin!"));
		}
	}
}
