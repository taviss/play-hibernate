package utils;

import models.Keyword;
import models.Site;
import models.dao.SiteDAO;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import play.Logger;

import java.io.IOException;
import java.util.Set;

public class LinkParser {

	private static SiteDAO sd =  new SiteDAO();

	public static String parseSite(String link){
		return link.split("[.]", 2)[1].split("/", 2)[0];
	}

	public static String[] parseKeywordsFromLink(String link){
		try{
			Connection connection = Jsoup.connect(link);
				/* Needed in order to to get content from page */
			connection.userAgent("Mozilla/5.0");

				/* Get content of the page */
			Document document = connection.get();

			String site = parseSite(link);

				/* Get the meta tag with the name keywords */
			Elements keywordsElements = document.select("meta[name=keywords]");
			if(keywordsElements.isEmpty()){
				/* Then user product name to get keywords, TY PCGARAGE */
				String gtfo[] = {"getFromName"};
				return gtfo;
			}

			/* Get all keywords as one string */
			String keywordsString = keywordsElements.attr("content");

			/* Split keywords string in order to get individual keywords */
			String[] individualKeywords = keywordsString.split(" ");

			/* Remove , or . from individual keywords */
			individualKeywords = removePunctuation(individualKeywords);
			return individualKeywords;

		} catch(IOException e){
			Logger.info("Could not connect to link: " + link);
		}
		return null;
	}

	public static String[] parseKeywordsFromName(String name){
		/* TY PCGARAGE */
		String[] tyPCGarage = name.split(" ");
		tyPCGarage = removePunctuation(tyPCGarage);
		return tyPCGarage;
	}

	/* Remove , or . from individual keywords */
	public static String[] removePunctuation(String[] str){
		for(int i = 0;i<str.length;i++){
			if(str[i].contains("."))
				str[i] = str[i].replace(".", "");
			if(str[i].contains(","))
				str[i] = str[i].replace(",", "");
		}
		return str;
	}
}
