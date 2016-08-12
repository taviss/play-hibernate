package utils;
/**
 * Created by octavian.salcianu on 8/3/2016.
 */
public class URLFixer {

	/**
	 * Takes an URL in form of a string and makes it valid
	 * @param URL
	 * @return String
     */
	public static String fixURL(String URL) {
		if(URL.contains("https://")) {
			URL = URL.replace("https", "http");
		}
		if (!URL.contains("www.")) {
			URL = "www." + URL;
		}
		if (!URL.contains("http://")) {
			URL = "http://" + URL;
		}
		return URL;
	}
}