package controllers;

import models.User;
import play.mvc.Security;
import play.mvc.Controller;
import models.Site;
import models.dao.SiteDAO;
import play.db.jpa.Transactional;
import play.mvc.Result;
import controllers.Secured;


/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class SiteController extends Controller {

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addSite(){
		/*adminLevel 3 means...for now...this user has enough privileges to add websites to the database.
		* Using random values for testing purpose.*/
//		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
//			return ok(siteAdd.render(null, "Thou art not admin!"));
//		}
		SiteDAO sd = new SiteDAO();
		Site s = new Site();
		s.setSiteURL("emag.ro/test");
		s.setSiteKeyword("emag");
		s = sd.create(s);
		return ok("Thou art admin!");
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result removeSite(){
		SiteDAO sd = new SiteDAO();
//		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
//			return ok(siteRemove.render(null, "Thou art not admin!"));
//		} else{
			sd.delete("keyword");
			return ok("Deleted");
	}
}
