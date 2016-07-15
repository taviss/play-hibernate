package controllers;

import forms.LoginForm;
import models.dao.UserDAO;
import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Messages;
import play.mvc.Result;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by octavian.salcianu on 7/15/2016.
 */
public class AuthorizationController {
    public static final int SALT_BYTES = 24;
    public static final int HASH_BYTES = 24;
    public static final int PBKDF2_ITERATIONS = 1000;

    /**
     * Attempts to login the user and returns ok if succes and badRequest if not
     * @return
     */
    public static Result tryLogin() {
        Form<LoginForm> form = Form.form(LoginForm.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest("Invalid form");
        }
        UserDAO ud = new UserDAO();
        LoginForm data = form.get();
        return ok("Success");
    }

    public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength ) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
}
