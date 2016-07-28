package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import forms.SetAdminForm;
import models.User;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.UserDAO;
import org.mockito.internal.matchers.Null;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import java.util.UUID;
import static utils.PasswordHashing.hashPassword;

/**
 * Created by octavian.salcianu on 7/11/2016.
 */
public class UserController extends Controller {
    @Inject
    private UserDAO ud;

    /**
     * Adds an user to the database and activates it. Not to be confused with the registration process
     * @return Result
     */
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
            createdUser.setUserPass(hashPassword(createdUser.getUserPass().toCharArray()));
            createdUser.setUserToken(UUID.randomUUID().toString());
            createdUser.setUserActive(true);
            ud.create(createdUser);
            return ok("Success");
        }
    }

    /**
     * Updates an existing user. The method assumes the sender already has the complete model of the user
     * @param id
     * @return Result
     */
    @Security.Authenticated(Secured.class)
    @Transactional
    public Result updateUser(Long id) {
        //Form<User> form = Form.form(User.class).bindFromRequest();

        if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }
        /*
        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }*/

        User foundUser = ud.get(id);

        if(foundUser == null) {
            return notFound("No such user");
        } else {
            try {
                JsonNode json = request().body().asJson();
                if(json != null) {
                    Form<User> user = Form.form(User.class);
                    Form<User> form = user.bind(json);

                    if(form.hasErrors()){
                        return badRequest("Invalid form");
                    } else {
                        User formUser = form.get();
                        //Logger.warn("User: " + formUser.getUserMail() + "  " + formUser.getAdminLevel());
                        if (!formUser.getId().equals(id)) {
                            return badRequest();
                        } else {
                            ud.update(formUser);
                            return ok("Success");
                        }
                    }
                } else {
                    return badRequest();
                }
            } catch (Exception e) {
                return badRequest("Invalid form");
            }
        }
    }

    /**
     * Returns the user with given id
     * @param id
     * @return Result
     */
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

    /**
     * Updates adminLevel of an user given the userName
     * @return Result
     */
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

    /**
     * Deletes an user given the id
     * @param id
     * @return Result
     */
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