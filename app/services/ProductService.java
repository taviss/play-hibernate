package services;

import models.Price;
import models.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import play.Configuration;
import play.Logger;
import play.api.Play;
import play.db.jpa.JPAApi;
import utils.CurrencyCalculator;
import utils.URLFixer;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by octavian.salcianu on 8/9/2016.
 */
public class ProductService {

    private static final JPAApi jpaApi = Play.current().injector().instanceOf(JPAApi.class);
    /**
     * Returns the price by accesing the website of the product.
     *
     * Method works by taking the closest parent(class, siteKeyword) of the element containing the price on which it tests
     * the pattern using the price keyword(priceElement) for finding the price value and currency keyword(currencyElement)
     * for finding the currency.
     * @param product
     * @return
     */
    public boolean indexProduct(Product product) {
        Logger.info("Indexing product " + product.getId() + "...");
        try {
            //Only update if the last update is 7 days old or older
            if (product.getPrice() == null || new Date().compareTo(product.getPrice().getInputDate()) >= Configuration.root().getInt("productMinUpdateTime")) {
                //Test that the URL is well formated and open the connection
                URL testURL = new URL(URLFixer.fixURL(product.getLinkAddress()));
                Document document = Jsoup.connect(URLFixer.fixURL(product.getLinkAddress())).userAgent("Mozilla/5.0").maxBodySize(0).get();
                Element doc;
                //If the site has a certain element containing the price & currency, use it to avoid duplicate price/currency elements
                if(product.getSite().getSiteKeyword().equals("none")) {
                    doc = document;
                } else {
                    //doc = document.getElementsContainingText(product.getSite().getSiteKeyword()).first();
                    doc = document.getElementsByClass(product.getSite().getSiteKeyword()).first();
                }
               //Element doc = document.select("div:contains(" + product.getSite().getSiteKeyword() + ")").first();
               //Element doc = document.getElementsByClass(product.getSite().getSiteKeyword()).first();

                //Patterns for finding the price
                //<..."price"...>ACTUAL_PRICE
                //String patternTag = "(?is)(<.*?" + product.getSite().getPriceElement() + ".*?>)(([0-9]*[.])?[0-9]+)";
                //..."price"...ACTUAL_PRICE
                String pricePattern = "(?is)(.*?" + product.getSite().getPriceElement() + ".*?)(([0-9]*[.])?[0-9]+)";
                //String pricePattern = "(?is)((\"" + product.getSite().getPriceElement() + "\"|" + product.getSite().getPriceElement() + "\\s*=|" + product.getSite().getPriceElement() + "\\s*>).*?)(([0-9]*[.])?[0-9]+)";

                //Patterns for finding the currency
                String currencyPatternTag = "(?is)(" + product.getSite().getCurrencyElement() + ".*?>)(\\w+)<";
                String currencyPatternProp = "(?is)(" + product.getSite().getCurrencyElement() + ".*?)\"(\\w+)\"";

                //Element productElement = doc.getElementsByClass(product.getSite().getSiteKeyword()).first();
                //Element priceElement = doc.select("div:containsOwn(" + product.getSite().getPriceElement() + "),meta:containsOwn(" + product.getSite().getPriceElement() + ")").first();
                //Element currencyElement = doc.select("div:containsOwn(" + product.getSite().getCurrencyElement() + "),meta:containsOwn(" + product.getSite().getCurrencyElement() + ")").first();
                Pattern pPattern = Pattern.compile(pricePattern);
                //Logger.info(pPattern.toString());
                Matcher priceMatcher = pPattern.matcher(doc.html());

                Pattern cPatternTag = Pattern.compile(currencyPatternTag);
                Pattern cPatternProp = Pattern.compile(currencyPatternProp);
                Matcher currencyMatcherTag = cPatternTag.matcher(doc.html());
                Matcher currencyMatcherProp = cPatternProp.matcher(doc.html());

                Float productPrice = null;
                String productCurrency = null;

                //Find the patterns and collect data
                if (priceMatcher.find()) {
                    productPrice = Float.parseFloat(priceMatcher.group(2));
                    //Logger.info(productPrice.toString());
                }

                if (currencyMatcherTag.find()) {
                    productCurrency = currencyMatcherTag.group(2);
                   // Logger.info(productCurrency);
                    //Check if the found string is a currency because of the false matching sometimes
                    try {
                        Set<Currency> currencies = Currency.getAvailableCurrencies();
                        Currency c = Currency.getInstance(CurrencyCalculator.solveBadCurrency(productCurrency));
                        if (!currencies.contains(c)) productCurrency = null;
                    } catch (Exception e) {
                        productCurrency = null;
                    }
                }
                //If there is no currency found, search using the second pattern and also check if the currency exists
                if (currencyMatcherProp.find() && productCurrency == null) {
                    productCurrency = currencyMatcherProp.group(2);
                    //Logger.info(productCurrency);
                    try {
                        Set<Currency> currencies = Currency.getAvailableCurrencies();
                        Currency c = Currency.getInstance(CurrencyCalculator.solveBadCurrency(productCurrency));
                        if (!currencies.contains(c)) productCurrency = null;
                    } catch (Exception e) {
                        productCurrency = null;
                    }
                }

                //Finally, check if price & currency were found
                if (productPrice != null && productCurrency != null) {
                    try {
                        Price price = new Price();
                        price.setInputDate(new Date());
                        price.setProduct(product);
                        price.setValue(CurrencyCalculator.convert(productPrice, productCurrency, "EUR"));
                        product.setPrice(price);

                        try {
                            jpaApi.withTransaction(() -> {
                                EntityManager em = jpaApi.em();
                                em.merge(product);
                            });
                        } catch (Exception e) {
                            Logger.error("There was an error while trying to merge product + " + product.getId() + ": " + e.getMessage());
                            return false;
                        }
                        Logger.info("Updated product " + product.getProdName());
                        return true;
                        //return ok("Product " + product.getId() + " updated!");
                    } catch (Exception e) {
                        Logger.info("Conversion exception: " + e.getMessage());
                        return false;
                    }
                } else {
                    Logger.error("Error while updating product " + product.getProdName() + "(ID:" + product.getId() + ")");
                    return false;
                    // return badRequest();
                }
            } else {
                Logger.info("Product " + product.getId() + " was up to date");
                return false;
                //return ok("Product already up to date");
            }
        } catch (MalformedURLException e) {
            Logger.error("Bad URL while indexing product " + product.getId() + " " + e.getMessage());
            return false;
            //return badRequest();
        } catch (IOException |NullPointerException e) {
            Logger.error("Error while indexing product " + product.getId() + " " + e.getMessage());
            return false;
            //return badRequest();
        }
    }
}
