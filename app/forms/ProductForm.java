package forms;

import play.data.validation.Constraints;

public class ProductForm {

	@Constraints.Required()
	public String productName;
}
