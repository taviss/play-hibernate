package forms;

import play.data.validation.Constraints;

public class ProductForm {

	@Constraints.Required()
	@Constraints.MinLength(4)
	@Constraints.MaxLength(15)
	public String productName;
}
