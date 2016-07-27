package controllers;

/**
 * Created by octavian.salcianu on 7/18/2016.
 */

import models.dao.UserDAO;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

import java.util.Date;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {

        String lastActivity = ctx.session().get("lastActivity");

        if (lastActivity != null && !lastActivity.equals("")) {
            long previousT = Long.valueOf(lastActivity);
            long currentT = new Date().getTime();
            long timeout = Long.valueOf(Configuration.root().getString("sessionTimeout")) * 1000 * 60;
            if ((currentT - previousT) > timeout) {
                ctx.session().clear();
                return null;
            }
        }

        String tickString = Long.toString(new Date().getTime());
        ctx.session().replace("userTime", tickString);

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
