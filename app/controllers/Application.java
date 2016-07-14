package controllers;

import models.User;
import models.dao.UserDAO;
import play.db.jpa.Transactional;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("MainTest", "Test"));
    }

    @Transactional(readOnly = true)
    public Result createUser() {
        UserDAO ud = new UserDAO();
        /*
        User x = new User();
        x.setStatus("Testare");
        x.setUserId(5L);
        x = ud.create(x);*/
        String s = ud.testQuery();
        return ok(index.render("Test", s));
        //return ok(user.render(x.getId()));
    }

    @Transactional(readOnly = true)
    public Result getUser() {
        UserDAO ud = new UserDAO();
        User x = ud.get(1L);
        return ok(user.render(x.getUserId()));
    }

}