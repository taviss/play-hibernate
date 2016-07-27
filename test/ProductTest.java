import static org.junit.Assert.assertEquals;
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

import controllers.AuthorizationController;
import models.Product;
import models.dao.KeywordDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductTest extends WithApplication{

	/*
	private ProductDAO pd = mock(ProductDAO.class);
	private SiteDAO sd = mock(SiteDAO.class);
	private KeywordDAO kd = mock(KeywordDAO.class);

	@Before
	public void setUp() throws Exception {
		Http.Context context = mock(Http.Context.class);
		Http.Context.current.set(context);
	}

	@Override
	protected Application provideApplication() {
		return new GuiceApplicationBuilder()
				.configure("play.http.router", "router.Routes")
				.overrides(bind(ProductDAO.class).toInstance(pd))
				.overrides(bind(SiteDAO.class).toInstance(sd))
				.overrides(bind(KeywordDAO.class).toInstance(kd))
				.build();
	}

	@Test
	public void testCreateProduct(){
		Map form = new HashMap<String, String>();
		form.put("prodName", "Test");
		form.put("linkAddress", "test.com/te-st");

		Product p = new Product();
		p.setProdName("Test");
		p.setLinkAddress("test.test/test");
		when(pd.getProduct(anyString())).thenReturn(p);

		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.create()).bodyForm(form));
		assertEquals(OK, r.status());
	}*/
}
