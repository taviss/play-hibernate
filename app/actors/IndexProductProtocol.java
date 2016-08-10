package actors;

import models.Product;

/**
 * Created by octavian.salcianu on 8/9/2016.
 */
public class IndexProductProtocol {
    public static class IndexProduct {
        public final Product product;

        public IndexProduct(Product product) {
            this.product = product;
        }
    }
}