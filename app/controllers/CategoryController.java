package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Category;
import models.Product;
import models.admin.UserRoles;
import models.dao.CategoryDAO;
import models.dao.KeywordDAO;
import models.dao.ProductDAO;
import models.dao.SiteDAO;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

public class CategoryController extends Controller {

	@Inject
	private FormFactory formFactory;

	@Inject
	private CategoryDAO catDAO;

	@Security.Authenticated(Secured.class)
	@Transactional
	public Result addCategory() {
		if (Secured.getAdminLevel() != UserRoles.LEAD_ADMIN) {
			return forbidden("Not enough admin rights");
		} else {
			JsonNode json = request().body().asJson();
			Form<Category> form = formFactory.form(Category.class).bind(json);
			if (form.hasErrors())
				return badRequest("Invalid form");
			Category cat = form.get();

			if(catDAO.getCategoryByName(cat.getCatName()) != null)
				return badRequest("Category already exists!");
			catDAO.create(cat);
			return ok("Category added: " + cat.getCatName());
		}
	}


	@Security.Authenticated(Secured.class)
	@Transactional
	public Result deleteCategory(Long id){
		if(Secured.getAdminLevel() != UserRoles.LEAD_ADMIN){
			return forbidden("Not enough admin rights");
		} else{
			Category cat = catDAO.get(id);
			if(cat == null){
				return notFound("Category with id " + id + " could not be found!");
			}
//			catDAO.delete(cat);

			catDAO.softDelete(cat);
			return ok("Category with id " + id + " deleted");
		}
	}
}
