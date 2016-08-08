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
import models.Keyword;
import models.Product;
import models.Site;
import models.User;
import models.admin.UserRoles;
import models.dao.KeywordDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import models.dao.UserDAO;
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

public class ProductTest extends WithApplication {


	private ProductDAO pd = mock(ProductDAO.class);
	private SiteDAO sd = mock(SiteDAO.class);
	private KeywordDAO kd = mock(KeywordDAO.class);
	private UserDAO ud = mock(UserDAO.class);

	@Before
	public void setUp() throws Exception {
		Http.Context context = mock(Http.Context.class);
		Http.Context.current.set(context);

		/* Neutralize ProductDAO methods that interact with DB */
		doNothing().when(pd).create(any(Product.class));
		doNothing().when(pd).delete(any());
		doNothing().when(pd).softDelete(any());
		doNothing().when(pd).update(any());
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


	/* Add product tests */

	@Test
	public void testAddProductSuccess() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");


		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(OK, r.status());
	}

	@Test
	public void testAddProductNullName() {

		Map form = new HashMap<String, String>();
		form.put("prodName", null);
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		/* Always return preset site */
		when(sd.getSiteByURL(anyString())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testAddProductNameTooShort() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "3ch");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		/* Always return preset site */
		when(sd.getSiteByURL(anyString())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testAddProductNullLink() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "3ch11");
		form.put("linkAddress", null);

		Site site = new Site();
		site.setSiteURL("emag.ro");

		/* Always return preset site */
		when(sd.getSiteByURL(anyString())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testAddProductLinkTooShort() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "3ch11");
		form.put("linkAddress", "link2short");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		/* Always return preset site */
		when(sd.getSiteByURL(anyString())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testAddProductNullNameAndLink() {

		Map form = new HashMap<String, String>();
		form.put("prodName", null);
		form.put("linkAddress", null);

		Site site = new Site();
		site.setSiteURL("emag.ro");

		/* Always return preset site */
		when(sd.getSiteByURL(anyString())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testAddProductNameAndLinkTooShort() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "n2s");
		form.put("linkAddress", "link2short");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		/* Always return preset site */
		when(sd.getSiteByURL(anyString())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testAddProductUnauthorized() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");


		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).session("user", "Test").bodyJson(Json.toJson(form)));

		assertEquals(FORBIDDEN, r.status());
	}

	@Test
	public void testAddProductNotLoggedIn() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");


		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.addProduct()).bodyJson(Json.toJson(form)));

		assertEquals(SEE_OTHER, r.status());
	}

	/*Update product tests*/

	@Test
	public void testUpdateProductSuccess() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(OK, r.status());
		assertEquals(form.get("prodName"), p.getProdName());
		assertEquals(form.get("linkAddress"), p.getLinkAddress());
	}

	@Test
	public void testUpdateProductNotLoggedIn() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).bodyJson(Json.toJson(form)));

		assertEquals(SEE_OTHER, r.status());
	}

	@Test
	public void testUpdateProductNotAdmin() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "Test").bodyJson(Json.toJson(form)));

		assertEquals(FORBIDDEN, r.status());
	}

	@Test
	public void testUpdateProductNullProduct() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "Test123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(null);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(NOT_FOUND, r.status());
	}

	@Test
	public void testUpdateProductNullFormName() {

		Map form = new HashMap<String, String>();
		form.put("prodName", null);
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testUpdateProductFormNameTooShort() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "123");
		form.put("linkAddress", "http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testUpdateProductFormSiteTooShort() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "1234");
		form.put("linkAddress", "http://www.emag.ro");

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	@Test
	public void testUpdateProductNullFormSite() {

		Map form = new HashMap<String, String>();
		form.put("prodName", "1234");
		form.put("linkAddress", null);

		Site site = new Site();
		site.setSiteURL("emag.ro");

		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);

		/* Always return preset site */
		when(sd.getSiteByURL(any())).thenReturn(site);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.updateProduct(1)).session("user", "username55").bodyJson(Json.toJson(form)));

		assertEquals(BAD_REQUEST, r.status());
	}

	/* Delete product tests */

	@Test
	public void testDeleteProductSuccess() {
		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.deleteProduct(1)).session("user", "username55"));

		assertEquals(OK, r.status());
	}

	@Test
	public void testDeleteProductProductNotFound() {
		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(null);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.deleteProduct(1)).session("user", "username55"));

		assertEquals(NOT_FOUND, r.status());
	}

	@Test
	public void testDeleteProductUnauthorized() {
		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.deleteProduct(1)).session("user", "Test"));

		assertEquals(FORBIDDEN, r.status());
	}

	@Test
	public void testDeleteProductNotLoggedIn() {
		Product p = new Product();
		p.setProdName("produs");
		p.setLinkAddress("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM");

		/* Always get same product*/
		when(pd.get(any())).thenReturn(p);


		Result r = route(Helpers.fakeRequest(controllers.routes.ProductController.deleteProduct(1)));

		assertEquals(SEE_OTHER, r.status());
	}
}