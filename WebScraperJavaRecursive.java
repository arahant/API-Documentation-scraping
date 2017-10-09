package com.scraper.java;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraperJavaRecursive {

	private static String baseurl;
	private static final String INDEX_DIR = "/home/arahant/Documents/Main/Career/API docs/";
	private static final int TOTAL_DEPTH = 2;
	
	public static void main(String[] args) {
		baseurl = "https://docs.oracle.com/javase/7/docs/api/";
		WebScraperJavaRecursive extract = new WebScraperJavaRecursive();
		if(extract.verifyURL(baseurl+"overview-summary.html"))
			extract.crawl(baseurl+"overview-summary.html",1,"Java7Sample");
	}
	
	private void crawl(String url,int depth,String domain) {
		System.err.println("Depth "+depth+" "+url+"---"+domain);
		Document doc = removeParts(url);
		Elements tables = doc.getElementsByTag("table");
		Elements links;
		LinkedHashMap<String,String> link_title_map = new LinkedHashMap<String, String>();
		downloadDocument(url, domain);
		for(Element table:tables) {
			links = table.getElementsByTag("a");
			for(Element a:links) {
				String attr = a.attr("href").replace("../", "/");
				if(verifyURL(baseurl+attr)) {
					if(++depth<=TOTAL_DEPTH)
						crawl(baseurl+attr,depth,domain+"."+a.text());
					depth--;
					downloadDocument(baseurl+attr,domain+"."+a.text());
					link_title_map.put(a.attr("href"), a.text());
				}
			}
		}
		replaceLinks(domain,link_title_map);
	}
	
	private boolean verifyURL(String url) {
		try {
			//if(url.contains("http"))
				if(Jsoup.connect(url).ignoreHttpErrors(true).execute().statusCode()<400)
					return true;
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	private Document removeParts(String url) {
		Document primaryDoc = null;
		try {
			primaryDoc = Jsoup.connect(url).get();

			Elements removable = primaryDoc.getElementsByAttributeValueContaining("class", "nav");
			for(Element child:removable)
				child.remove();
			removable.clear();

			removable.addAll(primaryDoc.getElementsByAttributeValueContaining("class", "footer"));
			for(Element child:removable)
				child.remove();
			removable.clear();

			removable.addAll(primaryDoc.getElementsByTag("footer"));
			for(Element child:removable)
				child.remove();
			removable.clear();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return primaryDoc;
	}
	
	private void downloadDocument(String url, String text) {
		
		Document doc = removeParts(url);
		FileOutputStream fOut;BufferedOutputStream bOut;
		
		String[] title = text.split("\\.");
		StringBuilder path = new StringBuilder(INDEX_DIR);
		for(String s:title)
			path.append(s).append("/");
		
		if(new File(path.toString()).mkdirs()) {
			try {
				File file = new File(path.toString()+"summary.html");
				fOut = new FileOutputStream(file);
				bOut = new BufferedOutputStream(fOut);
				bOut.write(doc.toString().getBytes());
				bOut.flush();bOut.close();
				System.out.println(path.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			System.err.println(path.toString());
	}
	
	private void replaceLinks(String domain, LinkedHashMap<String, String> link_title_map) {
		
		String[] title = domain.split("\\.");
		StringBuilder path = new StringBuilder();
		for(String s:title)
			path.append(s).append("/");
		
		try {
			File file = new File(INDEX_DIR+path.toString()+"summary.html");
			Document doc = Jsoup.parse(file, "UTF-8");
			String data = doc.toString();
			System.err.println("In "+INDEX_DIR+path.toString()+"summary.html replacing...");
			for(String link:link_title_map.keySet()) {
				
				String[] newfolders = link_title_map.get(link).split("\\.");
				StringBuilder newpath = new StringBuilder();
				for(String s:newfolders)
					newpath.append(s).append("/");
				
				String oldtag = "<a href=\""+link+"\"";
				String newtag = "<a href=\""+newpath.toString()+"summary.html\"";
				data = data.replace(oldtag, newtag);
				System.out.println(oldtag+" ---> "+newtag);
			}
			FileOutputStream fOut = new FileOutputStream(file);
			BufferedOutputStream bOut = new BufferedOutputStream(fOut);
			bOut.write(data.toString().getBytes());
			bOut.flush();bOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
