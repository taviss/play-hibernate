package controllers;

import models.User;
import play.Configuration;
import play.i18n.Messages;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import org.apache.commons.mail.EmailException;
import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;


public class Mailer {
    @Inject
    private MailerClient mailerClient;

    /**
     * Sends the newly registered user a confirmation mail containing a token that can be used to activate the account
     * @param user
     * @throws EmailException
     * @throws MalformedURLException
     */
    public void sendConfirmationMail(User user) throws EmailException, MalformedURLException {
        //Create the strings
        String subject = Messages.get("mail.confirmation.subject");
        String urlString = "http://" + Configuration.root().getString("server.hostname") + "/confirm/" + user.getUserToken();
        URL url = new URL(urlString);
        String message = Messages.get("mail.confirmation.body") + ", " + url.toString();

        //Compose the email
        Email email = new Email()
                .setSubject(subject)
                .setFrom("test@gmail.com")
                .addTo(user.getUserMail())
                .setBodyText(message);

        //Send the email
        mailerClient.send(email);
    }

    /**
     * Sends the user an email containing a token that can be used to reset their password
     * @param user
     * @throws EmailException
     * @throws MalformedURLException
     */
    public void sendPasswordResetMail(User user) throws EmailException, MalformedURLException {
        //Create the strings
        String subject = Messages.get("mail.password.reset.subject");
        String urlString = "http://" + Configuration.root().getString("server.hostname") + "/confirmreset/" + user.getUserToken();
        URL url = new URL(urlString);
        String message = Messages.get("mail.password.reset.body") + ", " + url.toString();

        //Compose the email
        Email email = new Email()
                .setSubject(subject)
                .setFrom("test@gmail.com")
                .addTo(user.getUserMail())
                .setBodyText(message);

        //Send the email
        mailerClient.send(email);
    }

    /**
     * Sends the user an email containing the random password generated upon validating the token
     * @param user
     * @throws EmailException
     * @throws MalformedURLException
     */
    public void sendRandomPasswordMail(User user) throws EmailException, MalformedURLException {
        //Create the strings
        String subject = Messages.get("mail.password.random.subject");
        String message = Messages.get("mail.password.random.body") + user.getUserPass();

        //Compose the email
        Email email = new Email()
                .setSubject(subject)
                .setFrom("test@gmail.com")
                .addTo(user.getUserMail())
                .setBodyText(message);

        //Send the email
        mailerClient.send(email);
    }
}