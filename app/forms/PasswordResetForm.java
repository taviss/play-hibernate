package forms;

import play.data.validation.Constraints;

/**
 * Created by octavian.salcianu on 7/26/2016.
 */
public class PasswordResetForm {
    @Constraints.Required()
    @Constraints.MinLength(3)
    public String userName;

    @Constraints.Required()
    @Constraints.Email
    public String userMail;

}
