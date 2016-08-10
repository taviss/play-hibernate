package utils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by octavian.salcianu on 8/3/2016.
 */
public class CurrencyCalculator {

    public static float convert(Float value, String currencyFrom, String currencyTo) throws IOException {
        return value*conversionRate(currencyFrom, currencyTo);
    }

    public static float conversionRate(String currencyFrom, String currencyTo) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://download.finance.yahoo.com/d/quotes.csv?s=" + solveBadCurrencyYahooAPI(currencyFrom) + currencyTo + "=X&f=l1&e=.cs");
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpGet, responseHandler);
        httpclient.getConnectionManager().shutdown();
        return Float.parseFloat(responseBody);
    }

    public static String solveBadCurrency(String currency) {
        currency = currency.toUpperCase();
        switch(currency) {
            case "LEI": {
                return "ROL";
            }
            case "RON": {
                return "ROL";
            }
            default: {
                return currency;
            }
        }
    }

    private static String solveBadCurrencyYahooAPI(String currency) {
        currency = currency.toUpperCase();
        switch(currency) {
            case "LEI": {
                return "RON";
            }
            default: {
                return currency;
            }
        }
    }
}
