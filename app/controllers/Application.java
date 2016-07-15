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

}