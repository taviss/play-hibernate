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
			/* First real product:
			 * prodName: Memorie Kingston 8GB 1333MHz DDR3 Non-ECC CL9 SODIMM
			 * linkAddress: http://www.emag.ro/memorie-kingston-8gb-1333mhz-ddr3-non-ecc-cl9-sodimm-kvr1333d3s9-8g/pd/EMQRDBBBM
			 *
			 * prodName: Telefon mobil Lenovo A6010, Dual SIM, 8GB, 4G, Black
			 * linkAddress: http://www.emag.ro/telefon-mobil-lenovo-a6010-dual-sim-8gb-4g-black-pa220081ro/pd/DS5KVYBBM/
			 * */

			Set<Keyword> kk = new HashSet<>();
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
			/* .split("[.]", 2)[1].split("/", 2)[0] */
			String URL = URLFixer.fixURL(product.getLinkAddress().split("[.]", 2)[1].split("/", 2)[0]);
			Site site = siteDAO.getSiteByURL(URL);
			if (site != null) {
				product.setSite(site);
			} else {
				return notFound("No such site: " + URL);
			}

			/* Keyword parsing from site */
			try{
				Connection connection = Jsoup.connect(product.getLinkAddress());
				/* Needed to get content from page */
				connection.userAgent("Mozilla/5.0");

				/* Get content of the page */
				Document document = connection.get();

				/* Get the meta tag with the name keywords */
				Elements elements = document.select("meta[name=keywords]");
				if(elements.isEmpty())
					return badRequest("No keywords meta tag in " + product.getLinkAddress());

				/* Get all keywords as one string */
				String s = elements.attr("content");

				/* Split keywords string in order ot get individual keywords */
				String[] split = s.split(", ");

				/* Remove , or . from individual keywords */
				for(int i = 0;i<split.length;i++){
					if(split[i].endsWith(",") || split[i].endsWith("."))
						split[i]=split[i].substring(0, split[i].length() - 1);
				}

				/* Create keyword objects */
				for(String kw : split){
					Keyword tibi = new Keyword();
					tibi.setId(null);
					tibi.setProduct(product);
					tibi.setKeyword(kw);
					kk.add(tibi);
				}
			} catch(IOException e){
				Logger.info("Could not connect to link: " + product.getLinkAddress());
			}
			product.setKeywords(kk);
			productDAO.create(product);
			return ok("Added product:" + product.getProdName());
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