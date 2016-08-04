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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.CurrencyCalculator;
import utils.URLFixer;

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
	public Result startIndexing() {
		if (/*Secured.getAdminLevel() != UserRoles.LEAD_ADMIN*/false) {
			return badRequest("You are not authorized to use this");
		} else {
			List<Product> allProds = productDAO.getAll();
			//allProds.forEach(i -> startIndexingProduct(i));
			int size = allProds.size();
			for(int i = 0; i < size; i++) {
				startIndexingProduct(allProds.get(i));
			}
			/*
			//Works fine, web client blocking
			indexProduct(productDAO.get(36L));
			indexProduct(productDAO.get(37L));
			indexProduct(productDAO.get(38L));
			indexProduct(productDAO.get(39L));
			indexProduct(productDAO.get(40L));
			*/
			return ok("Indexing");
		}
	}

	@Transactional
	//Doesn't matter if transactional or not
	public CompletionStage<Result> startIndexingProduct(Product id) {
		return CompletableFuture.supplyAsync(() -> indexProduct(id))
				.thenApply(i -> ok("Got result: " + i));
	}

	/**
	 * Returns the price by accesing the website of the product.
	 *
	 * Method works by taking the closest parent(class, siteKeyword) of the element containing the price on which it tests
	 * the pattern using the price keyword(priceElement) for finding the price value and currency keyword(currencyElement)
	 * for finding the currency.
	 * @param product
	 * @return
     */
	@Transactional
	public boolean indexProduct(Product product) {
		Logger.info("Indexing product " + product.getId() + "...");
		try {
			//Only update if the last update is 7 days old or older
			if (product.getPrice() == null || new Date().compareTo(product.getPrice().getInputDate()) >= 7) {
				URL testURL = new URL(URLFixer.fixURL(product.getLinkAddress()));
				Document doc = Jsoup.connect(URLFixer.fixURL(product.getLinkAddress())).userAgent("Mozilla/5.0").get();

				//Patterns for finding the price
				//<..."price"...>ACTUAL_PRICE
				//String patternTag = "(?is)(<.*?" + product.getSite().getPriceElement() + ".*?>)(([0-9]*[.])?[0-9]+)";
				//..."price"...ACTUAL_PRICE
				String pricePattern = "(?is)(.*?" + product.getSite().getPriceElement() + ".*?)(([0-9]*[.])?[0-9]+)";

				//Pattern for finding the currency
				String currencyPattern = "(?is)(.*?" + product.getSite().getCurrencyElement() + ".*?)(\\w+)";

				Element productElement = doc.getElementsByClass(product.getSite().getSiteKeyword()).first();

				Pattern pPattern = Pattern.compile(pricePattern);
				Matcher priceMatcher = pPattern.matcher(productElement.html());

				Pattern cPattern = Pattern.compile(currencyPattern);
				Matcher currencyMatcher = cPattern.matcher(productElement.html());

				Float productPrice = null;
				String productCurrency = null;

				//Logger.info(productElement.html());

				if (priceMatcher.find()) {
					productPrice = Float.parseFloat(priceMatcher.group(2));
					//Logger.info(productPrice.toString());
				}

				if (currencyMatcher.find()) {
					productCurrency = currencyMatcher.group(2);
					//Logger.info(productCurrency);
				}

				if (productPrice != null && productCurrency != null) {
					Price price = new Price();
					price.setInputDate(new Date());
					price.setProduct(product);
					price.setValue(CurrencyCalculator.convert(productPrice, productCurrency, "EUR"));
					product.setPrice(price);
					productDAO.update(product);
					Logger.info("Updated product " + product.getProdName());
					return true;
					//return ok("Product " + product.getId() + " updated!");
				} else {
					Logger.error("Error while updating product " + product.getProdName() + "(ID:" + product.getId() + ")");
					return false;
					// return badRequest();
				}
			} else {
				Logger.info("Product " + product.getId() + " was up to date");
				return false;
				//return ok("Product already up to date");
			}
		} catch (MalformedURLException e) {
			Logger.error("Bad URL while indexing product " + product.getId() + " " + e.getMessage());
			return false;
			//return badRequest();
		} catch (IOException|NullPointerException e) {
			Logger.error("Error while indexing product " + product.getId() + " " + e.getMessage());
			return false;
			//return badRequest();
		}
	}
}