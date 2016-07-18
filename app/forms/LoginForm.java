package forms;

/**
 * Created by octavian.salcianu on 7/15/2016.
 */
import play.data.validation.Constraints;

public class LoginForm {

    @Constraints.Required()
    @Constraints.MinLength(3)
    public String userName;

    @Constraints.Required()
    @Constraints.MinLength(6)
    public String password;
}