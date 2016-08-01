package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import forms.SetAdminForm;
import models.User;
import models.admin.UserRoles;
import models.dao.UserDAO;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
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
    private UserDAO userDAO;

    @Inject
    private FormFactory formFactory;

    /**
     * Adds an user to the database and activates it. Not to be confused with the registration process
     * @return Result
     */
    @Security.Authenticated(Secured.class)
    @Transactional
    @BodyParser.Of(value = BodyParser.Json.class)
    public Result createUser() {
        if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        JsonNode json = request().body().asJson();
        Form<User> form = formFactory.form(User.class).bind(json);

        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }

        User foundUser = userDAO.getUserByMail(form.get().getUserName());
        User foundMail = userDAO.getUserByName(form.get().getUserMail());

        if (foundUser != null || foundMail != null) {
            return badRequest("Username or email in use");
        } else {
            User createdUser = Json.fromJson(json, User.class);
            createdUser.setUserPass(hashPassword(createdUser.getUserPass().toCharArray()));
            createdUser.setUserToken(UUID.randomUUID().toString());
            createdUser.setUserActive(true);
            userDAO.create(createdUser);
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
    @BodyParser.Of(value = BodyParser.Json.class)
    public Result updateUser(Long id) {
        if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }
        User foundUser = userDAO.get(id);

        if (foundUser == null) {
            return notFound("No such user");
        } else {
            JsonNode json = request().body().asJson();
            Form<User> form = formFactory.form(User.class).bind(json);

            if (form.hasErrors()) {
                return badRequest("Invalid form");
            } else {
                User user = Json.fromJson(json, User.class);
                if (!user.getId().equals(id)) {
                    return badRequest();
                } else {
                    userDAO.update(user);
                    return ok("Success");
                }
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
        if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        User foundUser = userDAO.get(id);
        if (foundUser == null) {
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
    @BodyParser.Of(value = BodyParser.Json.class)
    public Result setAdminLevel() {
        if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        JsonNode json = request().body().asJson();
        Form<SetAdminForm> form = formFactory.form(SetAdminForm.class).bind(json);

        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }

        User foundUser = userDAO.getUserByName(form.get().userName);
        if (foundUser == null) {
            return notFound("No such user");
        } else {
            foundUser.setAdminLevel(form.get().adminLevel);
            userDAO.update(foundUser);
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
        if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
            return badRequest("Not enough privileges");
        }

        User foundUser = userDAO.get(id);

        if (foundUser == null) {
            return notFound("No such user");
        } else {
            userDAO.delete(foundUser);
            return ok("Deleted");
        }
    }
}