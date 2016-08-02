package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import forms.ProductForm;
import forms.ProductUpdateForm;
import models.Keyword;
import models.Price;
import models.Product;
import models.Site;
import models.dao.KeywordDAO;
import models.admin.UserRoles;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Inject;

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
	private FormFactory formFactory;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addProduct() {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return badRequest("You are not authorized to use this");
		} else {
			JsonNode json = request().body().asJson();
			Form<Product> form = formFactory.form(Product.class).bind(json);
			if (form.hasErrors()) {
				return badRequest("Invalid form");
			}
			String smth = form.get().getLinkAddress().split("/")[0];
			Product product = new Product();

			if((form.get().getProdName()) != null && (form.get().getLinkAddress() != null) && (smth != null)){
				product = form.get();
			} else {
				return badRequest("Invalid form");
			}
			Site site = siteDAO.getSiteByURL(product.getLinkAddress().split("/")[0]);
			if (site != null) {
				product.setSite(site);
			} else {
				return badRequest("No such site");
			}
			/* I'm aware of this code duplication, will be fixed after release */
			String URL = product.getLinkAddress();
			String[] URLsite = URL.split("/");
			String[] URLkeywords = URLsite[1].split("-");
			Set<Keyword> kk = new HashSet<>();
			for(String s : URLkeywords){
				Keyword tibi = new Keyword();
				tibi.setId(null);
				tibi.setProduct(product);
				tibi.setKeyword(s);
				keywordDAO.create(tibi);
				kk.add(tibi);
			}
			product.setKeywords(kk);
			productDAO.create(product);
			return ok("Added");
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteProduct(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return badRequest("You are not authorized to use this");
		} else {
			Product product = productDAO.get(id);
			if(product ==  null){
				return notFound("No such product");
			} else{
				/* Hard delete */
				productDAO.delete(product);

				/* Soft delete */
//				productDAO.softDelete(product);
				return ok("Deleted");
			}
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	@BodyParser.Of(value = BodyParser.Json.class)
	public Result updateProduct(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return badRequest("You are not authorized to use this");
		} else {
			JsonNode json = request().body().asJson();
			Form<Product> form = formFactory.form(Product.class).bind(json);
			if (form.hasErrors()) {
				return badRequest("Invalid form");
			}
			Product current = new Product();
			Product newP = new Product();
			if(id != null){
				current = productDAO.get(id);
			} else{
				ok("Pls provide ID!!!");
			}
			if (current == null) {
				return notFound("Product doesn't exist");
			} else {
				newP = form.get();
				if(newP.getLinkAddress().equalsIgnoreCase(current.getLinkAddress())){
					productDAO.update(current);
				} else {
					productDAO.delete(current);
					/* I'm aware of this code duplication, will be fixed after release */
					String URL = newP.getLinkAddress();
					String[] URLsite = URL.split("/");
					String[] URLkeywords = URLsite[1].split("-");
					Set<Keyword> kk = new HashSet<>();
					for(String s : URLkeywords){
						Keyword tibi = new Keyword();
						tibi.setId(null);
						tibi.setProduct(newP);
						tibi.setKeyword(s);
						keywordDAO.create(tibi);
						kk.add(tibi);
					}
					newP.setKeywords(kk);
					productDAO.create(newP);
				}
				return ok("Success");
			}
		}
	}

	//@Security.Authenticated(Secured.class)
	@Transactional
	public Result startIndexing() {
		if (/*Secured.getAdminLevel() != UserRoles.LEAD_ADMIN*/false) {
			return badRequest("You are not authorized to use this");
		} else {
			List<Product> allProds = productDAO.getAll();
			allProds.forEach(i -> startIndexingProduct(i));
			return ok(Json.toJson(allProds));
		}
	}

	public CompletionStage<Result> startIndexingProduct(Product product) {
		return CompletableFuture.supplyAsync(() -> indexProduct(product))
				.thenApply(i -> ok("Got result: " + i));
	}

	public Result indexProduct(Product product) {
		Price p = new Price();
		p.setProduct(product);
		p.setValue((float)500);
		p.setInputDate(new Date());
		Set<Price> prices = product.getPrices();
		prices.add(p);
		product.setPrices(prices);
		return ok("Product " + product.getId() + " updated!");
	}
}