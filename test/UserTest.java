import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

import controllers.AuthorizationController;
import models.User;
import models.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.Application;
import play.Logger;
import play.api.mvc.RequestHeader;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserTest extends WithApplication {

    @Before
    public void setUp() throws Exception {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);
    }

    @Override
    protected Application provideApplication() {
        UserDAO ud = mock(UserDAO.class);
        User u = new User();
        u.setUserName("Test");
        u.setUserPass(AuthorizationController.hashPassword("testtest".toCharArray()));
        u.setUserMail("test@test.com");
        when(ud.getUserByName(anyString())).thenReturn(u);
        return new GuiceApplicationBuilder()
                .configure("play.http.router", "router.Routes")
                .overrides(bind(UserDAO.class).toInstance(ud))
                .build();
    }

    @Test
    public void testLogin() {
        Map form = new HashMap<String, String>();
        form.put("userName", "Test");
        form.put("userPass", "testtest");

        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyForm(form));
        assertEquals(OK, res.status());
        assertEquals("Test", res.session().get("user"));
    }

    @Test
    public void testLogout() {
        Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.logoutUser()));
        Optional<String> url = res.redirectLocation();
        assertTrue(url.isPresent());
    }

}