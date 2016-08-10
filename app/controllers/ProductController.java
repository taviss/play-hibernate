package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Keyword;
import models.Price;
import models.Product;
import models.Site;
import models.dao.CategoryDAO;
import models.dao.KeywordDAO;
import models.admin.UserRoles;
import models.dao.PriceDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;

import java.util.*;

import javax.inject.Inject;

import static utils.LinkParser.*;


/**
 * Created by octavian.salcianu on 7/14/2016.
 */


public class ProductController extends Controller {
	@Inject
	private ProductDAO productDAO;

	@Inject
	private KeywordDAO keywordDAO;

	@Inject
	private SiteDAO siteDAO;

	@Inject
	private PriceDAO priceDAO;

	@Inject
	private FormFactory formFactory;

	@Inject
	private CategoryDAO catDAO;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct() {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return forbidden("Not enough admin rights");
		} else {
			JsonNode json = request().body().asJson();
			Form<Product> form = formFactory.form(Product.class).bind(json);
			if (form.hasErrors())
				return badRequest("Invalid form");
			Product product = new Product();

			product = form.get();

			Site site = siteDAO.getSiteByURL(parseSite(product.getLinkAddress()));

			if(site != null)
				product.setSite(site);
			else
				return badRequest("No such site");

			String[] keywords = parseKeywordsFromLink(product.getLinkAddress());
			if(keywords == null)
				return badRequest("Invalid site or missing meta tag");

			if(keywords.length == 1 && keywords[0].equals("getFromName"))
				keywords = parseKeywordsFromName(product.getProdName());

			Set<Keyword> kk = new HashSet<>();

			/* Create keyword objects */
			for(String kw : keywords){
				Keyword tibi = new Keyword();
				tibi.setId(null);
				tibi.setProduct(product);
				tibi.setKeyword(kw);
				kk.add(tibi);
			}
			product.setKeywords(kk);
			product.setCategory(catDAO.determineCategory(kk));
			productDAO.create(product);
			return ok("Added product: " + product.getProdName() + " " + catDAO.determineCategory(kk));
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteProduct(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return forbidden("Thou art not admin!");
		} else {
			Product product = productDAO.get(id);
			if(product ==  null){
				return notFound("No such product");
			} else{
				/* Hard delete */
				productDAO.delete(product);

				/* Soft delete */
//				productDAO.softDelete(product);
				return ok("Product deleted: " + product.getProdName());

			}
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	@BodyParser.Of(value = BodyParser.Json.class)
	public Result updateProduct(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return forbidden("Thou art not admin!");
		} else {
			JsonNode json = request().body().asJson();
			Form<Product> form = formFactory.form(Product.class).bind(json);
			if (form.hasErrors()) {
				return badRequest("Invalid form");
			}
			Product current = new Product();
			if(id != null){
				current = productDAO.get(id);
			} else{
				return notFound("Pls provide ID!!!");
			}
			if (current == null) {
				return notFound("Product doesn't exist");
			} else {

				if(form.get().getLinkAddress().equalsIgnoreCase(current.getLinkAddress())){
					current.setProdName(form.get().getProdName());
					current.setLinkAddress(form.get().getLinkAddress());
					productDAO.update(current);
				} else {
					current.setProdName(form.get().getProdName());
					current.setLinkAddress(form.get().getLinkAddress());

					Site site = siteDAO.getSiteByURL(parseSite(current.getLinkAddress()));

					if(site != null)
						current.setSite(site);
					else
						return badRequest("No such site");

					String[] keywords = parseKeywordsFromLink(current.getLinkAddress());
					if(keywords == null)
						return badRequest("Invalid site or missing meta tag");

					if(keywords.length == 1 && keywords[0].equals("getFromName"))
						keywords = parseKeywordsFromName(current.getProdName());

					keywordDAO.delete(current);
					Set<Keyword> kk = new HashSet<>();
					for(String s : keywords){
						Keyword tibi = new Keyword();
						tibi.setId(null);
						tibi.setProduct(current);
						tibi.setKeyword(s);
						kk.add(tibi);
					}
					current.setKeywords(kk);
					productDAO.update(current);
				}
				return ok("Product updated: " + current.getProdName());
			}
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result getProductPriceHistory(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return forbidden("You are not authorized to do this!");
		} else {
			List<Price> prices = priceDAO.getPricesByProductId(id);
			if(prices.isEmpty()) {
				return notFound("This product doesn't have a price history");
			} else {
				return ok(Json.toJson(prices));
			}
		}
	}
}