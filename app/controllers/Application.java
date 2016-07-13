package controllers;

import models.User;
import models.dao.UserDAO;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("MainTest", "Test"));
    }

    public Result createUser() {
        UserDAO ud = new UserDAO();
        User x = new User();
        x.setUserId(3L);
        ud.create(x);
        return ok(user.render(x));
    }

}
