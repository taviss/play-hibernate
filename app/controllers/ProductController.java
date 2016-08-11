package controllers;

import actors.ProductIndexer;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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
import play.db.jpa.JPAApi;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import play.Logger;
import scala.concurrent.ExecutionContext;
import services.ProductService;
import utils.PriceHistoryFilter;

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
	private JPAApi jpa;

	final ActorRef productIndexer;

	@Inject public ProductController(ActorSystem system) {
		productIndexer = system.actorOf(ProductIndexer.props);
	}

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
			try {
				Date startDate, endDate;
				String expectedPattern = "dd-MM-yyyy";
				SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
				startDate = formatter.parse(request().queryString().get("from")[0]);
				endDate = formatter.parse(request().queryString().get("to")[0]);
				prices = prices.stream().filter(p -> p.getInputDate().after(startDate)).collect(Collectors.toList());
				prices = prices.stream().filter(p -> p.getInputDate().before(endDate)).collect(Collectors.toList());
				if (prices.isEmpty()) {
					return notFound("This product doesn't have a price history");
				} else {
					return ok(Json.toJson(prices));
				}
			} catch (Exception e) {
				if (prices.isEmpty()) {
					return notFound("This product doesn't have a price history");
				} else {
					return ok(Json.toJson(prices));
				}
			}
		}
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result startIndexing(Long id) {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return badRequest("You are not authorized to use this");
		} else {
			List<Product> allProds = productDAO.getProductsBySiteId(id);
			int size = allProds.size();
			if(size > 0) Logger.info("Started indexing " + size + " product(s) for website " + allProds.get(0).getSite().getSiteURL());
			for(int i = 0; i < size; i++) {
				startIndexingProduct(allProds.get(i));
			}
			return ok("Indexing");
		}
	}

	public CompletionStage<Result> startIndexingProduct(Long product) {
		ExecutionContext ec = Akka.system().dispatchers().lookup("akka.actor.db-context");
		ProductService productService = new ProductService();
		return CompletableFuture.supplyAsync(() -> jpa.withTransaction("default", false, ()-> productService.indexProduct(productDAO.get(product))), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
	}

	public CompletionStage<Result> startIndexingProduct(Product product) {
		ExecutionContext ec = Akka.system().dispatchers().lookup("akka.actor.db-context");
		ProductService productService = new ProductService();

		return CompletableFuture.supplyAsync(() -> jpa.withTransaction("default", true, ()-> productService.indexProduct(product)), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
	}
}