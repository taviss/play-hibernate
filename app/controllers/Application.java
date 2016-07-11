package controllers;

import models.entities.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;
import play.*;
import play.mvc.*;

import views.html.*;

import java.util.ArrayList;
import java.util.List;

import static play.data.Form.form;
import static utils.HibernateUtils.getSessionFactory;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("MainTest", "Test"));
    }

    public Result test() {
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Person p = new Person("Abc", 40);
        session.save(p);
        tx.commit();
        session.close();
        return ok("Test");
    }

    public Result testMethod(Integer id) {
        List<Person> personArray =  new ArrayList<Person>();
        personArray.add(new Person("P1", 30));
        personArray.add(new Person("P2", 20));
        personArray.add(new Person("P3", 23));
        personArray.add(new Person("P4", 40));
        String personName = "";

        if (form().bindFromRequest().get("name") != null) {
            try {
                personName = form().bindFromRequest().get("name");
            } catch (Exception e) {
                Logger.error("string not parsed...");
            }
        }
        Person found = null;
        for(Person p : personArray) {
            if(p.getName().equals(personName)) {
                found = p;
                break;
            }
        }
        //found = personArray.get(1);
        if (found == null) return badRequest(personName + " doesn't exist");
        return ok("Person:" + found.getName() + ", age:" + found.getAge());
        //return badRequest("Nothing to do");
    }

}
