package controllers;

/**
 * Created by octavian.salcianu on 7/18/2016.
 */

import models.dao.UserDAO;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("user");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.Application.index());
    }

    public static int getAdminLevel() {
        UserDAO ud = new UserDAO();
        User u = ud.getUserByName(Context.current().request().username());
        return u.getAdminLevel();
    }
}
