import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;
import static utils.PasswordHashing.hashPassword;

import controllers.AuthorizationController;
import models.User;
import models.dao.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import play.api.libs.mailer.MailerClient;

public class AuthorizationTest extends WithApplication {

    private UserDAO ud = mock(UserDAO.class);
    private MailerClient mailer = mock(MailerClient.class);
    @Before
    public void setUp() throws Exception {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("play.http.router", "router.Routes")
                .overrides(bind(UserDAO.class).toInstance(ud))
                .overrides(bind(MailerClient.class).toInstance(mailer))
                .build();
    }

    @Test
    public void testLoginSuccess() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "testtest");

        User u = new User();
        u.setUserName("Test");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyForm(form));
        assertEquals(OK, res.status());
        assertEquals("Test", res.session().get("user"));
    }

    @Test
    public void testLoginNoSuchUser() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abcd");
        form.put("userPass", "testtest");

        when(ud.getUserByName(anyString())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testLoginBadPassword() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "abcabc");

        User u = new User();
        u.setUserName("Test");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testLoginPasswordTooShort() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "abc");

        User u = new User();
        u.setUserName("Test");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testLoginUserTooShort() {
        Map form = new HashMap<String, String>();
        form.put("userName", "te");
        form.put("userPass", "testtest");

        User u = new User();
        u.setUserName("Test");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
        assertEquals(null, res.session().get("user"));
    }

    @Test
    public void testRegisterSuccess() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userPass", "abcabc");
        form.put("userMail", "abc@abc.com");

        when(ud.getUserByName(anyString())).thenReturn(null);
        when(ud.getUserByMail(anyString())).thenReturn(null);
        when(ud.create(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.registerUser()).bodyForm(form));
        assertEquals(OK, res.status());
    }

    @Test
    public void testRegisterInvalidMail() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userPass", "abcabc");
        form.put("userMail", "abc");

        when(ud.getUserByName(anyString())).thenReturn(null);
        when(ud.getUserByMail(anyString())).thenReturn(null);
        when(ud.create(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.registerUser()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testRegisterInvalidUser() {
        Map form = new HashMap<String, String>();
        form.put("userName", "a");
        form.put("userPass", "abcabc");
        form.put("userMail", "abc@abc.com");

        when(ud.getUserByName(anyString())).thenReturn(null);
        when(ud.getUserByMail(anyString())).thenReturn(null);
        when(ud.create(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.registerUser()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testRegisterUserExists() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userPass", "abcabc");
        form.put("userMail", "abc@abc.com");

        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");

        when(ud.getUserByName(anyString())).thenReturn(u);
        when(ud.getUserByMail(anyString())).thenReturn(null);
        when(ud.create(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.registerUser()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testRegisterMailExists() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userPass", "abcabc");
        form.put("userMail", "abc@abc.com");

        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("abc@abc.com");

        when(ud.getUserByName(anyString())).thenReturn(null);
        when(ud.getUserByMail(anyString())).thenReturn(u);
        when(ud.create(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.registerUser()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testConfirmUserSuccess() {
        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("abc@abc.com");
        u.setUserActive(false);

        when(ud.getUserByToken(anyString())).thenReturn(u);
        when(ud.update(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.confirmUser("test")));
        assertEquals(OK, res.status());
    }

    @Test
    public void testConfirmUserBadToken() {
        when(ud.getUserByToken(anyString())).thenReturn(null);
        when(ud.update(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.confirmUser("test")));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testConfirmUserAlreadyActive() {
        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("abc@abc.com");
        u.setUserActive(true);

        when(ud.getUserByToken(anyString())).thenReturn(u);
        when(ud.update(any())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.confirmUser("test")));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testResetUserPasswordSuccess() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userMail", "abc@abc.com");

        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("abc@abc.com");

        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.resetUserPassword()).bodyForm(form));
        assertEquals(OK, res.status());
    }

    @Test
    public void testResetUserPasswordNoSuchUser() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userMail", "abc@abc.com");

        when(ud.getUserByName(anyString())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.resetUserPassword()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testResetUserPasswordMailNotMatching() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Abc");
        form.put("userPass", "abcabc");
        form.put("userMail", "xyz@abc.com");

        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("abc@abc.com");

        when(ud.getUserByName(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.resetUserPassword()).bodyForm(form));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testConfirmPasswordResetSuccess() {
        User u = new User();
        u.setUserName("Abc");
        u.setUserPass(hashPassword("testtest".toCharArray()));
        u.setUserMail("abc@abc.com");

        when(ud.getUserByToken(anyString())).thenReturn(u);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.confirmPasswordReset("test")));
        assertEquals(OK, res.status());
    }

    @Test
    public void testConfirmPasswordResetNoSuchUser() {
        when(ud.getUserByToken(anyString())).thenReturn(null);

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.confirmPasswordReset("test")));
        assertEquals(BAD_REQUEST, res.status());
    }

    @Test
    public void testLogout() {
        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.logoutUser()));
        Optional<String> url = res.redirectLocation();
        assertTrue(url.isPresent());
    }

}