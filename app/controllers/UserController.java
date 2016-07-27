package controllers;

import com.google.inject.Inject;
import forms.SetAdminForm;
import models.User;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.UserDAO;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by octavian.salcianu on 7/11/2016.
 */
public class UserController extends Controller {
    @Inject
    private UserDAO ud;

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result createUser() {
        Form<User> form = Form.form(User.class).bindFromRequest();
        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }
        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }

        User foundUser = ud.getUserByMail(form.get().getUserName());
        User foundMail = ud.getUserByName(form.get().getUserMail());

        if(foundUser != null || foundMail != null) {
            return badRequest("Username or email in use");
        } else {
            User createdUser = form.get();
            ud.create(createdUser);
            return ok("Success");
        }
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result updateUser(Long id) {
        Form<User> form = Form.form(User.class).bindFromRequest();
        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }
        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }

        User foundUser = ud.get(id);

        if(foundUser == null) {
            return notFound("No such user");
        } else {
            User formUser = form.get();
            formUser.setId(foundUser.getId());
            foundUser = formUser;
            ud.update(foundUser);
            return ok("Success");
        }
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result getUser(Long id) {
        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        User foundUser = ud.get(id);

        if(foundUser == null) {
            return notFound("No such user");
        } else {
            return ok(Json.toJson(foundUser));
        }
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result setAdminLevel() {
        Form<SetAdminForm> form = Form.form(SetAdminForm.class).bindFromRequest();

        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }
        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }
        User foundUser = ud.getUserByName(form.get().userName);

        if(foundUser == null) {
            return notFound("No such user");
        } else {
            foundUser.setAdminLevel(form.get().adminLevel);
            ud.update(foundUser);
            return ok("Success");
        }
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result deleteUser(Long id) {
        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        User foundUser = ud.get(id);

        if(foundUser == null) {
            return notFound("No such user");
        } else {
            ud.delete(foundUser);
            return ok("Deleted");
        }
    }
}