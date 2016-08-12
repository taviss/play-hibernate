package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Product;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Security;
import play.mvc.Controller;
import models.Site;
import models.dao.SiteDAO;
import play.db.jpa.Transactional;
import play.mvc.Result;
import scala.concurrent.ExecutionContext;
import services.ProductService;
import utils.URLFixer;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
public class SiteController extends Controller {

	@Inject
	private SiteDAO siteDAO;

	@Inject
	private ProductDAO productDAO;

	@Inject
	private FormFactory formFactory;

	@Inject
	private JPAApi jpa;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addSite(){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return forbidden("Thou art not admin!");
		}
		JsonNode json = request().body().asJson();
		Form<Site> form = formFactory.form(Site.class).bind(json);

		if (form.hasErrors()) {
			return badRequest("Invalid form");
		}

		if(siteDAO.getSiteByURL(URLFixer.fixURL(form.get().getSiteURL())) != null) {
			return badRequest("Website already exists");
		}

		Site site = Json.fromJson(json, Site.class);
		site.setSiteURL(URLFixer.fixURL(site.getSiteURL()));
		if(site.getSiteKeyword() == null || site.getSiteKeyword().length() == 0) site.setSiteKeyword("none");
		siteDAO.create(site);
		return ok("Site added: " + site.getSiteURL());
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	/* RollbackException if site has products assigned. */
	public Result deleteSite(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return forbidden("Thou art not admin!");
		}
		Site s = siteDAO.get(id);
		if(s == null){
			return notFound("Site doesn't exist");
		}
		siteDAO.delete(s);
		return ok("Site deleted: " + s.getSiteURL());
	}

	@Security.Authenticated(Secured.class)
	@Transactional
	/* If someone ever needs to update a site, here you go...
	*  RollbackException if site has products assigned. */
	public Result updateSite(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return forbidden("Thou art not admin!");
		} else{
			Site s = siteDAO.get(id);
			if(s == null){
				return notFound("Site doesn't exist");
			} else{
				JsonNode json = request().body().asJson();
				Form<Site> form = formFactory.form(Site.class).bind(json);
				if(form.hasErrors()){
					return badRequest("Invalid form");
				} else{
					s.setSiteURL(URLFixer.fixURL(form.get().getSiteURL()));
					siteDAO.update(s);
					return ok("Updated");
				}
			}
		}
	}

	/**
	 * Gets all the products coresponding to a site id and starts fetching data from webpages asynchronously
	 * @param id
	 * @return Result
     */
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

	/**
	 * Starts the indexing process in another thread
	 * @param product
	 * @return
	 */
	public CompletionStage<Result> startIndexingProduct(Long product) {
		ExecutionContext ec = Akka.system().dispatchers().lookup("akka.actor.db-context");
		ProductService productService = new ProductService();
		return CompletableFuture.supplyAsync(() -> jpa.withTransaction("default", false, ()-> productService.indexProduct(productDAO.get(product))), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
	}

	/**
	 * Starts the indexing process in another thread
	 * @param product
	 * @return
     */
	public CompletionStage<Result> startIndexingProduct(Product product) {
		ExecutionContext ec = Akka.system().dispatchers().lookup("akka.actor.db-context");
		ProductService productService = new ProductService();

		return CompletableFuture.supplyAsync(() -> jpa.withTransaction("default", true, ()-> productService.indexProduct(product)), play.libs.concurrent.HttpExecution.fromThread(ec))
				.thenApply(i -> ok("Got result: " + i));
	}
}
