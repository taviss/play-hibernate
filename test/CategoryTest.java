import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.route;
import static utils.LinkParser.*;
import static utils.PasswordHashing.hashPassword;

import controllers.AuthorizationController;
import controllers.ProductController;
import controllers.Secured;
import models.*;
import models.admin.UserRoles;
import models.dao.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import play.Application;

import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utils.LinkParser;

import java.net.MalformedURLException;
import java.util.*;

public class CategoryTest extends WithApplication {
	private CategoryDAO cd = mock(CategoryDAO.class);

	@Before
	public void setUp() throws Exception {
		Http.Context context = mock(Http.Context.class);
		Http.Context.current.set(context);

		/* Neutralize ProductDAO methods that interact with DB */
		doNothing().when(cd).create(any());
		doNothing().when(cd).delete(any());
	}

	@Override
	protected Application provideApplication() {
		return new GuiceApplicationBuilder()
				.configure("play.http.router", "router.Routes")
				.overrides(bind(CategoryDAO.class).toInstance(cd))
				.build();
	}

	@Test
	public void testCategoryAddSuccess(){
		Map form = new HashMap<String, String>();
		form.put("catName", "Test123");

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.addCategory()).session("user", "perkelex").bodyJson(Json.toJson(form)));
		assertEquals(OK, r.status());
	}

	@Test
	public void testCategoryAddUnauthorized(){
		Map form = new HashMap<String, String>();
		form.put("catName", "Test123");

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.addCategory()).session("user", "Test").bodyJson(Json.toJson(form)));
		assertEquals(FORBIDDEN, r.status());
	}

	@Test
	public void testCategoryAddNotLoggedIn(){
		Map form = new HashMap<String, String>();
		form.put("catName", "Test123");

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.addCategory()).bodyJson(Json.toJson(form)));
		assertEquals(SEE_OTHER, r.status());
	}

	@Test
	public void testCategoryAddNullName(){
		Map form = new HashMap<String, String>();
		form.put("catName", null);

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.addCategory()).session("user", "perkelex").bodyJson(Json.toJson(form)));
		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testCategoryDeleteSuccess(){
		Category cat = new Category();
		cat.setId(1L);

		when(cd.get(any())).thenReturn(cat);

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.deleteCategory(1L)).session("user", "perkelex"));
		assertEquals(OK, r.status());
	}

	@Test
	public void testCategoryDeleteUnauthorized(){
		Category cat = new Category();
		cat.setId(1L);

		when(cd.get(any())).thenReturn(cat);

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.deleteCategory(1L)).session("user", "Test"));
		assertEquals(FORBIDDEN, r.status());
	}

	@Test
	public void testCategoryDeleteNotLoggedIn(){
		Category cat = new Category();
		cat.setId(1L);

		when(cd.get(any())).thenReturn(cat);

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.deleteCategory(1L)));
		assertEquals(SEE_OTHER, r.status());
	}

	@Test
	public void testCategoryDeleteNotFound(){
		Category cat = new Category();
		cat.setId(1L);

		when(cd.get(any())).thenReturn(null);

		Result r = route(Helpers.fakeRequest(controllers.routes.CategoryController.deleteCategory(1L)).session("user", "perkelex"));
		assertEquals(NOT_FOUND, r.status());
	}


}
