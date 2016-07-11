package controllers;

import org.hibernate.Session;
import org.hibernate.Transaction;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static utils.HibernateUtils.getSessionFactory;

import views.html.*;

/**
 * Created by octavian.salcianu on 7/11/2016.
 */
public class Person extends Controller {

    public Result updateUser(Person user) {
        Transaction trns = null;
        Session session = getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (RuntimeException e) {
            if (trns != null) {
                trns.rollback();
            }
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
    }

    public Result getAllUsers() {
        List<Person> users = new ArrayList<>();
        Transaction trns = null;
        Session session = getSessionFactory().openSession();
        try {
            trns = session.beginTransaction();
            users = session.createQuery("from persons").list();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            session.flush();
            session.close();
        }
        return ok(.render(users));
    }
}
