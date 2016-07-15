package controllers;

import models.User;
import models.dao.UserDAO;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.user;

/**
 * Created by octavian.salcianu on 7/11/2016.
 */
public class UserController extends Controller {
    /**
     * Creates a new user and displays the newly generated id in a blank page
     * @return
     */
    @Transactional(readOnly = true)
    public Result createUser() {
        UserDAO ud = new UserDAO();
        User x = new User();
        x.setStatus("Testare");
        x.setUserId(5L);
        x = ud.create(x);
        return ok(user.render(x.getId()));
    }

    /**
     * Gets the user with specific id from the database
     * @return
     */
    @Transactional(readOnly = true)
    public Result getUser() {
        UserDAO ud = new UserDAO();
        User x = ud.get(1L);
        return ok(user.render(x.getUserId()));
    }

    /**
     * Start indexing w/ threads?
     */
}