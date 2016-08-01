package forms;

/**
 * Created by octavian.salcianu on 7/15/2016.
 */
import play.data.validation.Constraints;

public class LoginForm {

    @Constraints.Required()
    @Constraints.MinLength(3)
    @Constraints.MaxLength(64)
    public String userName;

    @Constraints.Required()
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    public String userPass;
}