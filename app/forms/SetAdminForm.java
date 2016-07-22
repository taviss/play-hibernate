package forms;

import play.data.validation.Constraints;

/**
 * Created by octavian.salcianu on 7/22/2016.
 */
public class SetAdminForm {

    @Constraints.Required
    public String userName;

    @Constraints.Required
    @Constraints.Max(3)
    @Constraints.Min(0)
    public int adminLevel;
}
