package utils;
/**
 * Created by octavian.salcianu on 8/3/2016.
 */
public class URLFixer {

	public static String fixURL(String URL) {
		if (!URL.contains("http://www.")) {
			return "http://www." + URL;
		} else if (!URL.contains("http://")) {
			return "http://" + URL;
		} else return URL;
	}
}