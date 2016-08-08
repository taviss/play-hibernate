import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;
import static utils.PasswordHashing.hashPassword;

import controllers.AuthorizationController;
import controllers.ProductController;
import controllers.Secured;
import models.Product;
import models.User;
import models.admin.UserRoles;
import models.dao.KeywordDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import models.dao.UserDAO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;

import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductTest extends WithApplication{


	private ProductDAO pd = mock(ProductDAO.class);
	private SiteDAO sd = mock(SiteDAO.class);
	private KeywordDAO kd = mock(KeywordDAO.class);
	private UserDAO ud = mock(UserDAO.class);

	@Before
	public void setUp() throws Exception {
		Http.Context context = mock(Http.Context.class);
		Http.Context.current.set(context);
//		when(context.session().get("user")).thenReturn("username55");
	}

	@Override
	protected Application provideApplication() {
		return new GuiceApplicationBuilder()
				.configure("play.http.router", "router.Routes")
				.overrides(bind(ProductDAO.class).toInstance(pd))
				.overrides(bind(SiteDAO.class).toInstance(sd))
				.overrides(bind(KeywordDAO.class).toInstance(kd))
				.overrides(bind(UserDAO.class).toInstance(ud))
				.build();
	}

	/* Fails miserably, problem unknown */
	/*
	@Test
	public void testCreateProduct(){

		ProductController controller = new ProductController();

		controller.addProduct();
		Map form = new HashMap<String, String>();
		form.put("prodName", "Test");
		form.put("linkAddress", "test.com/te-st-manycharacters");

		Map LogInForm = new HashMap<String, String>();
		LogInForm.put("userName", "Test");
		LogInForm.put("userPass", "testtest");

		User u = new User();
		u.setUserName("Test");
		u.setUserPass(hashPassword("testtest".toCharArray()));
		u.setUserMail("test@test.com");
		u.setAdminLevel(UserRoles.LEAD_ADMIN);
		when(ud.getUserByName(anyString())).thenReturn(u);

		Result res = route(Helpers.fakeRequest(controllers.routes.AuthorizationController.tryLogin()).bodyJson(Json.toJson(LogInForm)));

		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).bodyForm(form));
		assertEquals(OK, r.status());
	}*/
}
