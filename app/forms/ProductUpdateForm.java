package forms;

import play.data.validation.Constraints;

public class ProductUpdateForm {
	/* Current product name */
	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(15)
	public String product;

	/* New product name */
	@Constraints.Required
	@Constraints.MinLength(4)
	@Constraints.MaxLength(15)
	public String productName;


	/* New product link address */
	@Constraints.Required
	@Constraints.MinLength(20)
	@Constraints.MaxLength(150)
	public String linkAddress;
}
