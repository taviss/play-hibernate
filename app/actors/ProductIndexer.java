package actors;

import akka.actor.*;
import actors.IndexProductProtocol.*;
import play.Logger;
import play.db.jpa.JPAApi;
import services.ProductService;

import javax.inject.Inject;

/**
 * Created by octavian.salcianu on 8/9/2016.
 */
public class ProductIndexer extends UntypedActor {

    public static Props props = Props.create(ProductIndexer.class);

    @Inject
    private JPAApi jpaApi;

    /*
    @Inject
    public ProductIndexer(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
    }*/

    public void onReceive(Object msg) {
        try {
            if (msg instanceof IndexProduct) {
                ProductService productService = new ProductService();
                sender().tell(jpaApi.withTransaction("default", true, () -> productService.indexProduct(((IndexProduct) msg).product)), self());
                //sender().tell("Hello, " + ((IndexProduct) msg).name, self());
            }
        } catch (Exception e) {
            Logger.info("ERROR_____" + e.getMessage());
        }
    }
}
