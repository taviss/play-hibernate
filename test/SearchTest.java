import controllers.Mailer;
import models.Price;
import models.Product;
import models.dao.ProductDAO;
import models.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;

/**
 * Created by octavian.salcianu on 8/5/2016.
 */
public class SearchTest extends WithApplication {

    private ProductDAO productDAO = mock(ProductDAO.class);

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("play.http.router", "router.Routes")
                .overrides(bind(ProductDAO.class).toInstance(productDAO))
                .build();
    }

    @Before
    public void setUp() throws Exception {
        Http.Context context = mock(Http.Context.class);
        Http.Context.current.set(context);

        //when(context.session().get("user")).thenReturn("TaviAdmin");
    }

    @Test
    public void testSearchSuccessful() {
        Product product = new Product();
        product.setDeleted(false);
        product.setLinkAddress("test.com/test");
        product.setProdName("Test prod");
        Price p = new Price();
        p.setValue((float)2500);
        p.setProduct(product);
        p.setInputDate(new Date());
        Set<Price> prices = new HashSet<>();
        product.setPrices(prices);

        Set<Product> foundProducts = new HashSet<>();
        foundProducts.add(product);

        when(productDAO.findProductsByName(anyString(), any())).thenReturn(foundProducts);
        Result res = route(Helpers.fakeRequest(controllers.routes.SearchController.trySearch("test")).session("user", "TaviAdmin"));
        assertEquals(OK, res.status());
    }

    @Test
    public void testSearchNoSuchProduct() {
        when(productDAO.findProductsByName(anyString(), any())).thenReturn(new HashSet<Product>());

        Result res = route(Helpers.fakeRequest(controllers.routes.SearchController.trySearch("test")).session("user", "TaviAdmin"));
        assertEquals(NOT_FOUND, res.status());
    }
}
