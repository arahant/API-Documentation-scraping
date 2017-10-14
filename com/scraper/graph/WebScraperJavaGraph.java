package com.scraper.graph;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraperJavaGraph {

	private static String baseurl;
	private static final String INDEX_DIR = "/home/arahant/Documents/Main/Career/API docs/";
	private static final int TOTAL_DEPTH = 2;
	private static int c = 0;
	
	private static LinkedHashMap<String,Vertex> link_node_map;
	private static LinkedHashSet<String> link_set;
	//private static LinkedHashMap<String,String> link_domain_map;
	
	static {
		link_node_map = new LinkedHashMap<String, Vertex>();
		link_set = new LinkedHashSet<String>();
	}

	public static void main(String[] args) {
		baseurl = "https://docs.oracle.com/javase/7/docs/api/";
		WebScraperJavaGraph extract = new WebScraperJavaGraph();
		if(extract.validateURL(baseurl+"overview-summary.html")) {
			System.err.println("Cawling and forming a web graph...");
			extract.crawl(baseurl+"overview-summary.html","Java7Graph",1);
			System.err.println("Traversing the web graph, replacing hyperlinks...");
			extract.traverseWebGraph(link_node_map.get(baseurl+"overview-summary.html"),0);
		}
	}
	
	private void crawl(String url,String domain,int depth) {
		System.out.println("Depth "+depth+" "+url+"---"+domain);
		if(link_set.add(url)) {
			Document doc = removeParts(url);
			Elements links = doc.getElementsByTag("a");
			if(links.size()==0)
				return;
			else {
				List<String> hyperlinks = new LinkedList<String>();
				for(Element a:links) {
					if(validateURL(baseurl+a.attr("href"))) {
						crawl(baseurl+a.attr("href"),domain+a.text(),++depth);
						hyperlinks.add(baseurl+a.attr("href"));
					}
				}
				Vertex node = new Vertex();
				node.setId(++c);node.setUrl(url);node.setDomain(domain);
				node.setHyperlinks(hyperlinks);
				link_node_map.put(url, node);
				downloadDocument(url, domain);
			}
		}
		else
			return;
	}

	private boolean validateURL(String url) {
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
	
	private void downloadDocument(String url, String domain) {

		Document doc = removeParts(url);
		FileOutputStream fOut;BufferedOutputStream bOut;
		
		String[] title = domain.split("\\.");
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
	
	private void traverseWebGraph(Vertex node, int idFrom) {
		System.out.println("From "+idFrom+" to "+node.getId());
		if(node.isVisited())
			return;
		else {
			node.setVisited(true);
			List<String> hyperlinks = node.getHyperlinks();
			try {
				File file = new File(INDEX_DIR+node.getDomain());
				Document doc = Jsoup.parse(file, "UTF-8");
				String data = new String(doc.toString());
				for(String link:hyperlinks) {
					Vertex target = link_node_map.get(link);
					String oldtag = "href=\""+link+"\"";
					String newtag = "href=\""+target.getDomain();
					data = data.replace(oldtag, newtag);
					traverseWebGraph(target, node.getId());
				}
				FileOutputStream fOut = new FileOutputStream(file);
				BufferedOutputStream bOut = new BufferedOutputStream(fOut);
				bOut.write(data.getBytes());
				bOut.flush();bOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
