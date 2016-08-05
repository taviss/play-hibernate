package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.mysql.jdbc.Constants;
import forms.ProductForm;
import forms.ProductUpdateForm;
import models.Keyword;
import models.Product;
import models.Site;
import models.dao.KeywordDAO;
import models.admin.UserRoles;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.db.jpa.Transactional;
import play.mvc.Security;

import java.io.IOException;
import java.util.*;

import javax.inject.Inject;
import play.Logger;
import utils.URLFixer;

import static utils.LinkParser.parseKeywords;
import static utils.LinkParser.parseSite;


/**
 * Created by octavian.salcianu on 7/14/2016.
 */

/* TODO: method for parsing keywords out of product's site(so the code looks nice) */

public class ProductController extends Controller {
	@Inject
	private ProductDAO productDAO;

	@Inject
	private KeywordDAO keywordDAO;

	@Inject
	private SiteDAO siteDAO;

	@Inject
	private FormFactory formFactory;

	@Transactional
	public Result testJsoup(){
		try{
			Connection connection = Jsoup.connect("http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM/");
			connection.userAgent("Mozilla/5.0");
			Document document = connection.get();
			Elements elements = document.select("meta[name=keywords]");
//			for(Element e : elements){
			String s = elements.attr("content");
			String[] split = s.split(", ");

//			}
			return ok(Json.toJson(split));
		} catch(IOException e){
			Logger.info("Could not connect to link: http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM/");
		}
		return ok("1");
	}

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

			String[] keywords = parseKeywords(product.getLinkAddress());
			if(keywords == null)
				return badRequest("Invalid site or missing keywords tag");

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
			productDAO.create(product);
			return ok("Added product: " + product.getProdName());
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
				return ok("Deleted");
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
					keywordDAO.delete(current);
					productDAO.update(current);
				} else {
					current.setProdName(form.get().getProdName());
					current.setLinkAddress(form.get().getLinkAddress());
					/* I'm aware of this code duplication, will be fixed after release */
					String URL = current.getLinkAddress();
					String[] URLsite = URL.split("/");
					String[] URLkeywords = URLsite[1].split("-");
					Set<Keyword> kk = new HashSet<>();
					for(String s : URLkeywords){
						Keyword tibi = new Keyword();
						tibi.setId(null);
						tibi.setProduct(current);
						tibi.setKeyword(s);
						kk.add(tibi);
					}
					current.setKeywords(kk);
					productDAO.update(current);
				}
				return ok("Success");
			}
		}
	}
}