package forms;

import play.data.validation.Constraints;

public class ProductUpdateForm {
	/* Current product name */
	@Constraints.Required
	public String product;

	/* New product name */
	public String productName;

	/* New product link address */
	public String linkAddress;
}
