package forms;

import play.data.validation.Constraints;

/**
 * Created by octavian.salcianu on 7/22/2016.
 */
public class PasswordChangeForm {

    @Constraints.Required()
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    public String oldPassword;

    @Constraints.Required()
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    public String newPassword;

    @Constraints.Required()
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    public String newPasswordRepeat;
}
