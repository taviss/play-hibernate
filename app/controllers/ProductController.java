package controllers;

import actors.IndexProductProtocol;
import actors.ProductIndexer;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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
import play.api.Play;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scala.concurrent.ExecutionContext;
import services.ProductService;
import utils.CurrencyCalculator;
import utils.URLFixer;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import static akka.pattern.Patterns.ask;

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

	@Inject
	private JPAApi jpa;

	final ActorRef productIndexer;

	@Inject public ProductController(ActorSystem system) {
		productIndexer = system.actorOf(ProductIndexer.props);
	}

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
			Logger.info(smth);
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
	public Result startIndexing(Long id) {
		if (/*Secured.getAdminLevel() != UserRoles.LEAD_ADMIN*/false) {
			return badRequest("You are not authorized to use this");
		} else {
			List<Product> allProds = productDAO.getProductsBySiteId(id);
			//allProds.forEach(i -> startIndexingProduct(i));
			int size = allProds.size();
			if(size > 0) Logger.info("Started indexing " + size + " product(s) for website " + allProds.get(0).getSite().getSiteURL());
			for(int i = 0; i < size; i++) {
				startIndexingProduct(allProds.get(i));
			}
			return ok("Indexing");
		}
	}

	@Transactional
	public Result testIndex(Long id) {
		startIndexingProduct(productDAO.get(id));
		return ok();
	}

	public CompletionStage<Result> startIndexingProduct(Long product) {
		ExecutionContext ec = Akka.system().dispatchers().lookup("akka.actor.db-context");
		ProductService productService = new ProductService();
		return CompletableFuture.supplyAsync(() -> jpa.withTransaction("default", false, ()-> productService.indexProduct(productDAO.get(product))), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
		/*
		return CompletableFuture.supplyAsync(() -> ask(productIndexer, new IndexProductProtocol.IndexProduct(productDAO.get(product)), 1000), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));

		*/
		/*
		return CompletableFuture.supplyAsync(() -> productService.indexProduct(productDAO.get(product)), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
		*/
	}

	public CompletionStage<Result> startIndexingProduct(Product product) {
		ExecutionContext ec = Akka.system().dispatchers().lookup("akka.actor.db-context");
		ProductService productService = new ProductService();

		return CompletableFuture.supplyAsync(() -> jpa.withTransaction("default", true, ()-> productService.indexProduct(product)), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
		/*
		return CompletableFuture.supplyAsync(() -> ask(productIndexer, new IndexProductProtocol.IndexProduct(product), 1000), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
		*/
		/*
		return CompletableFuture.supplyAsync(() -> productService.indexProduct(product), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
		*/
	}
}