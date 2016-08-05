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

	public static String[] parseKeywords(String link){
		try{
			Connection connection = Jsoup.connect(link);
				/* Needed in order to to get content from page */
			connection.userAgent("Mozilla/5.0");

				/* Get content of the page */
			Document document = connection.get();

			String site = parseSite(link);

				/* Get the meta tag with the name keywords */
			Elements keywordsElements = document.select("meta[name=keywords]");
			Elements descriptionElements = document.select("meta[name=description]");
			if(keywordsElements.isEmpty() && descriptionElements.isEmpty())
				return null;

			if(!keywordsElements.isEmpty()){
				/* Get all keywords as one string */
				String s = keywordsElements.attr("content");

				/* Split keywords string in order ot get individual keywords */
				String[] split = s.split(", ");

				/* Remove , or . from individual keywords */
				for(int i = 0;i<split.length;i++){
					if(split[i].endsWith(",") || split[i].endsWith("."))
						split[i]=split[i].substring(0, split[i].length() - 1);
				}
				return split;
			}

			if(!descriptionElements.isEmpty()){
				/* Get all keywords as one string */
				String s = descriptionElements.attr("content");

				/* Split keywords string in order ot get individual keywords */
				String[] split = s.split(", ");

				/* Remove , or . from individual keywords */
				for(int i = 0;i<split.length;i++){
					if(split[i].endsWith(",") || split[i].endsWith("."))
						split[i]=split[i].substring(0, split[i].length() - 1);
				}
				return split;
			}

		} catch(IOException e){
			Logger.info("Could not connect to link: " + link);
		}
		return null;
	}
}
